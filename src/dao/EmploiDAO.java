package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmploiDAO {

    private static final String SQL_ALL = """
        SELECT e.id, e.jour, e.heure, e.salle,
               g.nom AS groupe, m.nom AS matiere, en.nom AS enseignant,
               e.groupe_id, e.matiere_id, e.enseignant_id
        FROM emploi e
        JOIN groupe g     ON e.groupe_id     = g.id
        JOIN matiere m    ON e.matiere_id    = m.id
        JOIN enseignant en ON e.enseignant_id = en.id
        """;

    /** All rows: {ID, Jour, Heure, Salle, Groupe, Matière, Enseignant} */
    public List<Object[]> getAll() {
        List<Object[]> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            ResultSet rs = c.createStatement().executeQuery(SQL_ALL);
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Filter by enseignant id — planning personnel */
    public List<Object[]> getByEnseignant(int enseignantId) {
        List<Object[]> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(SQL_ALL + " WHERE e.enseignant_id = ? ORDER BY e.jour, e.heure");
            ps.setInt(1, enseignantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Filter by enseignant id AND groupe */
    public List<Object[]> getByEnseignantAndGroupe(int enseignantId, int groupeId) {
        List<Object[]> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(SQL_ALL + " WHERE e.enseignant_id = ? AND e.groupe_id = ? ORDER BY e.jour, e.heure");
            ps.setInt(1, enseignantId);
            ps.setInt(2, groupeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }


    public List<Object[]> getEmploiParGroupe(String groupeNom) {
        List<Object[]> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(SQL_ALL + " WHERE g.nom = ?");
            ps.setString(1, groupeNom);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** For EtudiantDashboard planning */
    public List<Object[]> getEmploiParEtudiant(int etudiantId) {
        List<Object[]> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            String sql = """
                SELECT e.jour, e.heure, m.nom AS matiere, e.salle
                FROM emploi e
                JOIN etudiant et ON e.groupe_id = et.groupe_id
                JOIN matiere m   ON e.matiere_id = m.id
                WHERE et.id = ?
                """;
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setInt(1, etudiantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("jour"), rs.getString("heure"),
                    rs.getString("matiere"), rs.getInt("salle")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean ajouter(String jour, String heure, int salle,
                            int groupeId, int matiereId, int enseignantId) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO emploi(jour,heure,salle,groupe_id,matiere_id,enseignant_id) VALUES(?,?,?,?,?,?)"
            );
            ps.setString(1, jour);
            ps.setString(2, heure);
            ps.setInt(3, salle);
            ps.setInt(4, groupeId);
            ps.setInt(5, matiereId);
            ps.setInt(6, enseignantId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean modifier(int id, String jour, String heure, int salle,
                             int groupeId, int matiereId, int enseignantId) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "UPDATE emploi SET jour=?,heure=?,salle=?,groupe_id=?,matiere_id=?,enseignant_id=? WHERE id=?"
            );
            ps.setString(1, jour);
            ps.setString(2, heure);
            ps.setInt(3, salle);
            ps.setInt(4, groupeId);
            ps.setInt(5, matiereId);
            ps.setInt(6, enseignantId);
            ps.setInt(7, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean supprimer(int id) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM emploi WHERE id=?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private Object[] mapRow(ResultSet rs) throws SQLException {
        return new Object[]{
            rs.getInt("id"),
            rs.getString("jour"),
            rs.getString("heure"),
            rs.getInt("salle"),
            rs.getString("groupe"),
            rs.getString("matiere"),
            rs.getString("enseignant")
        };
    }
}

package dao;

import model.Matiere;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatiereDAO {

    private static Boolean hasCode    = null;
    private static Boolean hasVolHor  = null;

    private static boolean columnExists(Connection c, String col) {
        try {
            ResultSet rs = c.getMetaData().getColumns(null, null, "matiere", col);
            return rs.next();
        } catch (Exception e) { return false; }
    }

    private static void detectColumns(Connection c) {
        if (hasCode == null) {
            hasCode   = columnExists(c, "code");
            hasVolHor = columnExists(c, "volume_horaire");
        }
    }

    public List<Matiere> getAll() {
        List<Matiere> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            detectColumns(c);
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM matiere");
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Matières enseignées par cet enseignant (via table emploi) */
    public List<Matiere> getByEnseignant(int enseignantId) {
        List<Matiere> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            detectColumns(c);
            PreparedStatement ps = c.prepareStatement(
                "SELECT DISTINCT m.* FROM matiere m " +
                "JOIN emploi e ON e.matiere_id = m.id " +
                "WHERE e.enseignant_id = ? ORDER BY m.nom"
            );
            ps.setInt(1, enseignantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Matières enseignées par cet enseignant pour un groupe précis */
    public List<Matiere> getByEnseignantAndGroupe(int enseignantId, int groupeId) {
        List<Matiere> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            detectColumns(c);
            PreparedStatement ps = c.prepareStatement(
                "SELECT DISTINCT m.* FROM matiere m " +
                "JOIN emploi e ON e.matiere_id = m.id " +
                "WHERE e.enseignant_id = ? AND e.groupe_id = ? ORDER BY m.nom"
            );
            ps.setInt(1, enseignantId);
            ps.setInt(2, groupeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean ajouter(Matiere m) {
        try (Connection c = DBConnection.getConnection()) {
            detectColumns(c);
            PreparedStatement ps;
            if (Boolean.TRUE.equals(hasCode) && Boolean.TRUE.equals(hasVolHor)) {
                ps = c.prepareStatement(
                    "INSERT INTO matiere(nom,code,coefficient,volume_horaire) VALUES(?,?,?,?)");
                ps.setString(1, m.getNom());
                ps.setString(2, m.getCode() != null ? m.getCode() : "");
                ps.setDouble(3, m.getCoefficient());
                ps.setInt(4, m.getVolumeHoraire());
            } else if (Boolean.TRUE.equals(hasCode)) {
                ps = c.prepareStatement(
                    "INSERT INTO matiere(nom,code,coefficient) VALUES(?,?,?)");
                ps.setString(1, m.getNom());
                ps.setString(2, m.getCode() != null ? m.getCode() : "");
                ps.setDouble(3, m.getCoefficient());
            } else {
                ps = c.prepareStatement(
                    "INSERT INTO matiere(nom,coefficient) VALUES(?,?)");
                ps.setString(1, m.getNom());
                ps.setDouble(2, m.getCoefficient());
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean modifier(int id, String nom, String code, double coefficient, int volumeHoraire) {
        try (Connection c = DBConnection.getConnection()) {
            detectColumns(c);
            PreparedStatement ps;
            if (Boolean.TRUE.equals(hasCode) && Boolean.TRUE.equals(hasVolHor)) {
                ps = c.prepareStatement(
                    "UPDATE matiere SET nom=?,code=?,coefficient=?,volume_horaire=? WHERE id=?");
                ps.setString(1, nom);
                ps.setString(2, code != null ? code : "");
                ps.setDouble(3, coefficient);
                ps.setInt(4, volumeHoraire);
                ps.setInt(5, id);
            } else if (Boolean.TRUE.equals(hasCode)) {
                ps = c.prepareStatement(
                    "UPDATE matiere SET nom=?,code=?,coefficient=? WHERE id=?");
                ps.setString(1, nom);
                ps.setString(2, code != null ? code : "");
                ps.setDouble(3, coefficient);
                ps.setInt(4, id);
            } else {
                ps = c.prepareStatement(
                    "UPDATE matiere SET nom=?,coefficient=? WHERE id=?");
                ps.setString(1, nom);
                ps.setDouble(2, coefficient);
                ps.setInt(3, id);
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean supprimerParNom(String nom) {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            c.setAutoCommit(false);

            // Récupérer l'id d'abord
            PreparedStatement psId = c.prepareStatement("SELECT id FROM matiere WHERE nom=?");
            psId.setString(1, nom);
            java.sql.ResultSet rs = psId.executeQuery();
            if (!rs.next()) { c.rollback(); return false; }
            int id = rs.getInt("id");

            // Supprimer les absences liées
            PreparedStatement psAbs = c.prepareStatement("DELETE FROM absence WHERE matiere_id=?");
            psAbs.setInt(1, id);
            psAbs.executeUpdate();

            // Supprimer la matière
            PreparedStatement psMat = c.prepareStatement("DELETE FROM matiere WHERE id=?");
            psMat.setInt(1, id);
            int rows = psMat.executeUpdate();

            c.commit();
            return rows > 0;

        } catch (Exception e) {
            if (c != null) try { c.rollback(); } catch (Exception ignored) {}
            e.printStackTrace();
        } finally {
            if (c != null) try { c.setAutoCommit(true); c.close(); } catch (Exception ignored) {}
        }
        return false;
    }

    public boolean supprimer(int id) {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            c.setAutoCommit(false);

            // 1. Supprimer les absences liées à cette matière
            PreparedStatement psAbs = c.prepareStatement(
                "DELETE FROM absence WHERE matiere_id = ?"
            );
            psAbs.setInt(1, id);
            psAbs.executeUpdate();

            // 2. Supprimer la matière
            PreparedStatement psMat = c.prepareStatement(
                "DELETE FROM matiere WHERE id = ?"
            );
            psMat.setInt(1, id);
            int rows = psMat.executeUpdate();

            c.commit();
            return rows > 0;

        } catch (Exception e) {
            if (c != null) try { c.rollback(); } catch (Exception ignored) {}
            e.printStackTrace();
        } finally {
            if (c != null) try { c.setAutoCommit(true); c.close(); } catch (Exception ignored) {}
        }
        return false;
    }

    private Matiere map(ResultSet rs) throws SQLException {
        String code = "";
        int vh = 0;
        try { if (Boolean.TRUE.equals(hasCode))   code = rs.getString("code");        } catch (SQLException ignored) {}
        try { if (Boolean.TRUE.equals(hasVolHor)) vh   = rs.getInt("volume_horaire"); } catch (SQLException ignored) {}
        return new Matiere(
            rs.getInt("id"),
            rs.getString("nom"),
            code,
            rs.getDouble("coefficient"),
            vh
        );
    }
}

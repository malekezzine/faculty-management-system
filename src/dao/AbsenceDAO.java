package dao;

import model.Absence;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbsenceDAO {

    /** {date_absence, matiere_nom} pour l'espace étudiant */
    public List<Object[]> getByEtudiantWithMatiere(int etudiantId) {
        List<Object[]> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) { //cnx
            PreparedStatement ps = c.prepareStatement( //preparer requette sql
                "SELECT a.date_absence, m.nom AS matiere " +
                "FROM absence a " +
                "JOIN matiere m ON a.matiere_id = m.id " +
                "WHERE a.etudiant_id = ? " +
                "ORDER BY a.date_absence DESC"
            );
            ps.setInt(1, etudiantId); // remplacer ? par id etudiant
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getTimestamp("date_absence").toString(),
                    rs.getString("matiere")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Absence> getByEtudiant(int etudiantId) {
        List<Absence> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM absence WHERE etudiant_id=? ORDER BY date_absence DESC"
            );
            ps.setInt(1, etudiantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Absence(
                    rs.getInt("id"),
                    rs.getInt("etudiant_id"),
                    rs.getInt("matiere_id"),
                    rs.getTimestamp("date_absence")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Ajouter une absence avec NOW() */
    public boolean ajouter(int etudiantId, int matiereId) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO absence(etudiant_id, matiere_id, date_absence) VALUES(?, ?, NOW())"
            );
            ps.setInt(1, etudiantId);
            ps.setInt(2, matiereId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Ajouter une absence avec une date précise (choisie par l'enseignant) */
    public boolean ajouter(int etudiantId, int matiereId, java.sql.Date date) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO absence(etudiant_id, matiere_id, date_absence) VALUES(?, ?, ?)"
            );
            ps.setInt(1, etudiantId);
            ps.setInt(2, matiereId);
            ps.setDate(3, date);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Supprimer une absence par id */
    public boolean supprimer(int absenceId) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM absence WHERE id=?");
            ps.setInt(1, absenceId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Tous les étudiants d'un groupe avec leur statut d'absence pour une matière.
     * Retourne : {absence_id (null si présent), etudiant_id, nom, prenom, date_absence (null si présent)}
     */
    public List<Object[]> getByGroupeAndMatiere(int groupeId, int matiereId) {
        List<Object[]> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "SELECT a.id AS abs_id, et.id AS etudiant_id, et.nom, et.prenom, a.date_absence " +
                "FROM etudiant et " +
                "LEFT JOIN absence a ON a.etudiant_id = et.id AND a.matiere_id = ? " +
                "WHERE et.groupe_id = ? " +
                "ORDER BY et.nom, et.prenom"
            );
            ps.setInt(1, matiereId);
            ps.setInt(2, groupeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getObject("abs_id"),
                    rs.getInt("etudiant_id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getObject("date_absence")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Vérifier si une absence existe déjà pour cette date */
    public boolean existeAbsence(int etudiantId, int matiereId, java.sql.Date date) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "SELECT COUNT(*) FROM absence WHERE etudiant_id=? AND matiere_id=? AND DATE(date_absence)=?"
            );
            ps.setInt(1, etudiantId);
            ps.setInt(2, matiereId);
            ps.setDate(3, date);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Stats absences par groupe : {nom, prenom, total} */
    public List<Object[]> getStatsByGroupe(int groupeId) {
        List<Object[]> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "SELECT et.nom, et.prenom, COUNT(a.id) AS total " +
                "FROM etudiant et " +
                "LEFT JOIN absence a ON a.etudiant_id = et.id " +
                "WHERE et.groupe_id = ? " +
                "GROUP BY et.id ORDER BY total DESC"
            );
            ps.setInt(1, groupeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getInt("total")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}

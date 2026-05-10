package dao;

import model.Seance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeanceDAO {

    public List<Seance> getAll() {
        List<Seance> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM seance ORDER BY date, debut");
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Seance> getByGroupe(int groupeId) {
        List<Seance> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM seance WHERE groupe_id=? ORDER BY date, debut"
            );
            ps.setInt(1, groupeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Récupère {date, heure_debut, heure_fin, matiere_nom, salle_id} pour affichage */
    public List<Object[]> getDisplayByGroupe(int groupeId) {
        List<Object[]> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "SELECT s.date, s.debut, s.h_fin, m.nom AS matiere, s.salle_id " +
                "FROM seance s JOIN matiere m ON s.matiere_id = m.id " +
                "WHERE s.groupe_id = ? ORDER BY s.date, s.debut"
            );
            ps.setInt(1, groupeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getDate("date").toString(),
                    rs.getTime("debut").toString(),
                    rs.getTime("h_fin").toString(),
                    rs.getString("matiere"),
                    rs.getInt("salle_id")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean ajouter(Seance s) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO seance(date, debut, h_fin, matiere_id, salle_id, groupe_id) VALUES(?,?,?,?,?,?)"
            );
            ps.setDate(1, s.getDate());
            ps.setTime(2, s.getDebut());
            ps.setTime(3, s.getHFin());
            ps.setInt(4, s.getMatiereId());
            ps.setInt(5, s.getSalleId());
            ps.setInt(6, s.getGroupeId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean modifier(Seance s) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "UPDATE seance SET date=?,debut=?,h_fin=?,matiere_id=?,salle_id=?,groupe_id=? WHERE id_seance=?"
            );
            ps.setDate(1, s.getDate());
            ps.setTime(2, s.getDebut());
            ps.setTime(3, s.getHFin());
            ps.setInt(4, s.getMatiereId());
            ps.setInt(5, s.getSalleId());
            ps.setInt(6, s.getGroupeId());
            ps.setInt(7, s.getIdSeance());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean supprimer(int id) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM seance WHERE id_seance=?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private Seance map(ResultSet rs) throws SQLException {
        return new Seance(
            rs.getInt("id_seance"),
            rs.getDate("date"),
            rs.getTime("debut"),
            rs.getTime("h_fin"),
            rs.getInt("matiere_id"),
            rs.getInt("salle_id"),
            rs.getInt("groupe_id")
        );
    }
}

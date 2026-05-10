package dao;

import model.Salle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalleDAO {

    public List<Salle> getAll() {
        List<Salle> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM salle");
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public Salle getById(int id) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM salle WHERE id_salle=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean ajouter(Salle s) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO salle(capacite) VALUES(?)"
            );
            ps.setInt(1, s.getCapacite());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean modifier(int id, int capacite) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "UPDATE salle SET capacite=? WHERE id_salle=?"
            );
            ps.setInt(1, capacite);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean supprimer(int id) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM salle WHERE id_salle=?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private Salle map(ResultSet rs) throws SQLException {
        return new Salle(rs.getInt("id_salle"), rs.getInt("capacite"));
    }
}

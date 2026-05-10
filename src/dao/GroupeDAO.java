package dao;

import model.Groupe;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupeDAO {

    public List<Groupe> getAll() {
        List<Groupe> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM groupe");
            while (rs.next()) {
                list.add(new Groupe(rs.getInt("id"), rs.getString("nom")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Groupes dans lesquels enseigne cet enseignant */
    public List<Groupe> getByEnseignant(int enseignantId) {
        List<Groupe> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "SELECT DISTINCT g.* FROM groupe g " +
                "JOIN emploi e ON e.groupe_id = g.id " +
                "WHERE e.enseignant_id = ? ORDER BY g.nom"
            );
            ps.setInt(1, enseignantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new Groupe(rs.getInt("id"), rs.getString("nom")));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int getIdParNom(String nom) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("SELECT id FROM groupe WHERE nom=?");
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }
}

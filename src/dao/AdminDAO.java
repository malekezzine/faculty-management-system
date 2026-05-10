package dao;

import model.Admin;
import java.sql.*;

public class AdminDAO {

    /** Authentifie l'admin par login + mot de passe */
    public Admin authenticate(String login, String mdp) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement( //Requête SQL sécurisée
                "SELECT * FROM admin WHERE login=? AND mot_de_passe=?"
            );
            ps.setString(1, login);
            ps.setString(2, mdp);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs); //on le transforme en objet Admin//grâce à la méthode map()
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public Admin getById(int id) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM admin WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean ajouter(Admin a) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO admin(nom,prenom,login,mot_de_passe,matrice,poste,service) VALUES(?,?,?,?,?,?,?)"
            );
            ps.setString(1, a.getNom());
            ps.setString(2, a.getPrenom());
            ps.setString(3, a.getLogin());
            ps.setString(4, a.getMdp());
            ps.setString(5, a.getMatrice());
            ps.setString(6, a.getPoste());
            ps.setString(7, a.getService());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean modifier(Admin a) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "UPDATE admin SET nom=?,prenom=?,matrice=?,poste=?,service=? WHERE id=?"
            );
            ps.setString(1, a.getNom());
            ps.setString(2, a.getPrenom());
            ps.setString(3, a.getMatrice());
            ps.setString(4, a.getPoste());
            ps.setString(5, a.getService());
            ps.setInt(6, a.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private Admin map(ResultSet rs) throws SQLException {
        String matrice = "", poste = "", service = "";
        try { matrice = rs.getString("matrice"); } catch (SQLException ignored) {}
        try { poste   = rs.getString("poste");   } catch (SQLException ignored) {}
        try { service = rs.getString("service"); } catch (SQLException ignored) {}
        return new Admin(
            rs.getInt("id"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getString("login"),
            rs.getString("mot_de_passe"),
            matrice, poste, service
        );
    }
}

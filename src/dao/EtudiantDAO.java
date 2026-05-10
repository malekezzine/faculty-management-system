package dao;

import model.Etudiant;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO {

    public List<Etudiant> getAll() {
        List<Etudiant> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM etudiant");
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Etudiant> getByGroupe(int groupeId) {
        List<Etudiant> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM etudiant WHERE groupe_id=?");
            ps.setInt(1, groupeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public Etudiant authenticate(String login, String motDePasse) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM etudiant WHERE login=? AND mot_de_passe=?"
            );
            ps.setString(1, login);
            ps.setString(2, motDePasse);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public Etudiant getById(int id) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM etudiant WHERE id=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean ajouter(Etudiant e) {
        try (Connection c = DBConnection.getConnection()) {
            String login = e.getPrenom().toLowerCase() + "." + e.getNom().toLowerCase();
            String mdp   = String.valueOf(e.getCin());
            PreparedStatement ps = c.prepareStatement(
                "INSERT INTO etudiant(nom,prenom,cin,email,telephone,date_naissance,adresse," +
                "groupe_id,login,mot_de_passe,niveau,filiere) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)"
            );
            ps.setString(1, e.getNom());
            ps.setString(2, e.getPrenom());
            ps.setInt(3, e.getCin());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getTelephone());
            ps.setDate(6, e.getDateNaissance());
            ps.setString(7, e.getAdresse());
            ps.setInt(8, e.getGroupeId());
            ps.setString(9, login);
            ps.setString(10, mdp);
            ps.setString(11, e.getNiveau()  != null ? e.getNiveau()  : "");
            ps.setString(12, e.getFiliere() != null ? e.getFiliere() : "");
            return ps.executeUpdate() > 0;
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean modifier(Etudiant e) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                "UPDATE etudiant SET nom=?,prenom=?,cin=?,email=?,telephone=?," +
                "date_naissance=?,adresse=?,groupe_id=?,niveau=?,filiere=? WHERE id=?"
            );
            ps.setString(1, e.getNom());
            ps.setString(2, e.getPrenom());
            ps.setInt(3, e.getCin());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getTelephone());
            ps.setDate(6, e.getDateNaissance());
            ps.setString(7, e.getAdresse());
            ps.setInt(8, e.getGroupeId());
            ps.setString(9, e.getNiveau()  != null ? e.getNiveau()  : "");
            ps.setString(10, e.getFiliere() != null ? e.getFiliere() : "");
            ps.setInt(11, e.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean supprimerParCin(int cin) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM etudiant WHERE cin=?");
            ps.setInt(1, cin);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean supprimer(int id) {
        Connection c = null;
        try {
            c = DBConnection.getConnection();
            c.setAutoCommit(false);   // début de transaction

            // 1. Supprimer d'abord les absences liées à cet étudiant
            PreparedStatement psAbs = c.prepareStatement(
                "DELETE FROM absence WHERE etudiant_id = ?"
            );
            psAbs.setInt(1, id);
            psAbs.executeUpdate();

            // 2. Supprimer l'étudiant
            PreparedStatement psEtu = c.prepareStatement(
                "DELETE FROM etudiant WHERE id = ?"
            );
            psEtu.setInt(1, id);
            int rows = psEtu.executeUpdate();

            c.commit();   // valider la transaction
            return rows > 0;

        } catch (Exception e) {
            if (c != null) try { c.rollback(); } catch (Exception ignored) {}
            e.printStackTrace();
        } finally {
            if (c != null) try { c.setAutoCommit(true); c.close(); } catch (Exception ignored) {}
        }
        return false;
    }

    private Etudiant map(ResultSet rs) throws SQLException {
        String login = "", mdp = "", niveau = "", filiere = "";
        try { login   = rs.getString("login");        } catch (SQLException ignored) {}
        try { mdp     = rs.getString("mot_de_passe"); } catch (SQLException ignored) {}
        try { niveau  = rs.getString("niveau");       } catch (SQLException ignored) {}
        try { filiere = rs.getString("filiere");      } catch (SQLException ignored) {}
        return new Etudiant(
            rs.getInt("id"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getInt("cin"),
            rs.getInt("groupe_id"),
            rs.getString("email"),
            rs.getString("telephone"),
            rs.getDate("date_naissance"),
            rs.getString("adresse"),
            login, mdp, niveau, filiere
        );
    }
}

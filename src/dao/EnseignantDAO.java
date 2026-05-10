package dao;

import model.Enseignant;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnseignantDAO {

    /** Colonnes optionnelles détectées à la première connexion */
    private static Boolean hasMatrice   = null;
    private static Boolean hasLogin     = null;
    private static Boolean hasMdp       = null;

    /** Vérifie une seule fois si une colonne existe dans la table enseignant */
    private static boolean columnExists(Connection c, String col) {
        try {
            ResultSet rs = c.getMetaData().getColumns(null, null, "enseignant", col);
            return rs.next();
        } catch (Exception e) { return false; }
    }

    private static void detectColumns(Connection c) {
        if (hasMatrice == null) {
            hasMatrice = columnExists(c, "matrice");
            hasLogin   = columnExists(c, "login");
            hasMdp     = columnExists(c, "mot_de_passe");
        }
    }

    public List<Enseignant> getAll() {
        List<Enseignant> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection()) {
            detectColumns(c);
            // GROUP BY nom,prenom pour éviter les doublons si un enseignant
            // est inséré plusieurs fois avec des IDs différents
            ResultSet rs = c.createStatement().executeQuery(
                "SELECT MIN(id) as id, nom, prenom, specialite" +
                (Boolean.TRUE.equals(hasLogin)   ? ", MIN(login) as login"           : "") +
                (Boolean.TRUE.equals(hasMdp)     ? ", MIN(mot_de_passe) as mot_de_passe" : "") +
                (Boolean.TRUE.equals(hasMatrice) ? ", MIN(matrice) as matrice"       : "") +
                " FROM enseignant GROUP BY nom, prenom ORDER BY MIN(id)");
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public Enseignant authenticate(String login, String mdp) {
        try (Connection c = DBConnection.getConnection()) {
            detectColumns(c);
            if (!Boolean.TRUE.equals(hasLogin) || !Boolean.TRUE.equals(hasMdp)) {
                return null; // colonnes absentes, login impossible
            }
            PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM enseignant WHERE login=? AND mot_de_passe=?"
            );
            ps.setString(1, login);
            ps.setString(2, mdp);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean ajouter(Enseignant e) {
        try (Connection c = DBConnection.getConnection()) {
            detectColumns(c);
            String login = e.getPrenom().toLowerCase() + "." + e.getNom().toLowerCase();

            String sql;
            PreparedStatement ps;

            if (Boolean.TRUE.equals(hasMatrice) && Boolean.TRUE.equals(hasLogin) && Boolean.TRUE.equals(hasMdp)) {
                sql = "INSERT INTO enseignant(nom,prenom,specialite,matrice,login,mot_de_passe) VALUES(?,?,?,?,?,?)";
                ps = c.prepareStatement(sql);
                ps.setString(1, e.getNom());
                ps.setString(2, e.getPrenom());
                ps.setString(3, e.getSpecialite());
                ps.setString(4, e.getMatrice() != null ? e.getMatrice() : "");
                ps.setString(5, login);
                ps.setString(6, "prof123");
            } else if (Boolean.TRUE.equals(hasLogin) && Boolean.TRUE.equals(hasMdp)) {
                sql = "INSERT INTO enseignant(nom,prenom,specialite,login,mot_de_passe) VALUES(?,?,?,?,?)";
                ps = c.prepareStatement(sql);
                ps.setString(1, e.getNom());
                ps.setString(2, e.getPrenom());
                ps.setString(3, e.getSpecialite());
                ps.setString(4, login);
                ps.setString(5, "prof123");
            } else {
                sql = "INSERT INTO enseignant(nom,prenom,specialite) VALUES(?,?,?)";
                ps = c.prepareStatement(sql);
                ps.setString(1, e.getNom());
                ps.setString(2, e.getPrenom());
                ps.setString(3, e.getSpecialite());
            }
            return ps.executeUpdate() > 0;
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }

    public boolean modifier(int id, String nom, String prenom, String specialite, String matrice) {
        try (Connection c = DBConnection.getConnection()) {
            detectColumns(c);
            String sql;
            PreparedStatement ps;
            if (Boolean.TRUE.equals(hasMatrice)) {
                sql = "UPDATE enseignant SET nom=?,prenom=?,specialite=?,matrice=? WHERE id=?";
                ps = c.prepareStatement(sql);
                ps.setString(1, nom);
                ps.setString(2, prenom);
                ps.setString(3, specialite);
                ps.setString(4, matrice != null ? matrice : "");
                ps.setInt(5, id);
            } else {
                sql = "UPDATE enseignant SET nom=?,prenom=?,specialite=? WHERE id=?";
                ps = c.prepareStatement(sql);
                ps.setString(1, nom);
                ps.setString(2, prenom);
                ps.setString(3, specialite);
                ps.setInt(4, id);
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean supprimerParNom(String nom) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM enseignant WHERE nom=?");
            ps.setString(1, nom);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean supprimer(int id) {
        try (Connection c = DBConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement("DELETE FROM enseignant WHERE id=?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private Enseignant map(ResultSet rs) throws SQLException {
        String matrice = "", login = "", mdp = "";
        try { if (Boolean.TRUE.equals(hasMatrice)) matrice = rs.getString("matrice"); } catch (SQLException ignored) {}
        try { if (Boolean.TRUE.equals(hasLogin))   login   = rs.getString("login");   } catch (SQLException ignored) {}
        try { if (Boolean.TRUE.equals(hasMdp))     mdp     = rs.getString("mot_de_passe"); } catch (SQLException ignored) {}
        return new Enseignant(
            rs.getInt("id"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getString("specialite"),
            matrice, login, mdp
        );
    }
}

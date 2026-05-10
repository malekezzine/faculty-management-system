package model;

/**
 * Diagramme : enseignant  extends Utilisateur
 * Champs ajoutés : matrice
 */
public class Enseignant extends Utilisateur {

    private String matrice;      // nouveau — diagramme
    private String specialite;

    public Enseignant() {}

    public Enseignant(int id, String nom, String prenom, String specialite,
                      String matrice, String login, String mdp) {
        super(id, nom, prenom, login, mdp);
        this.specialite = specialite;
        this.matrice    = matrice != null ? matrice : "";
    }

    /** Constructeur rétro-compatible */
    public Enseignant(int id, String nom, String prenom, String specialite) {
        this(id, nom, prenom, specialite, "",
             prenom.toLowerCase() + "." + nom.toLowerCase(), "prof123");
    }

    // ── Méthode diagramme ──────────────────────────────────
    public void gererEnseignant() { /* logique métier */ }

    // ── Getters / Setters ──────────────────────────────────
    public String getMatrice()                  { return matrice; }
    public void   setMatrice(String matrice)    { this.matrice = matrice; }

    public String getSpecialite()                     { return specialite; }
    public void   setSpecialite(String specialite)    { this.specialite = specialite; }

    @Override
    public String toString() {
        return "Enseignant{id=" + id + ", nom='" + nom + "', prenom='" + prenom
               + "', specialite='" + specialite + "', matrice='" + matrice + "'}";
    }
}

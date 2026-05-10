package model;

/**
 * Diagramme : matière
 * Champs ajoutés : code, volumeHoraire
 */
public class Matiere {

    private int    id;
    private String nom;
    private String code;          // nouveau — diagramme
    private double coefficient;
    private int    volumeHoraire; // nouveau — diagramme

    public Matiere() {}

    public Matiere(int id, String nom, String code, double coefficient, int volumeHoraire) {
        this.id            = id;
        this.nom           = nom;
        this.code          = code          != null ? code : "";
        this.coefficient   = coefficient;
        this.volumeHoraire = volumeHoraire;
    }

    /** Constructeur rétro-compatible */
    public Matiere(int id, String nom, double coefficient) {
        this(id, nom, "", coefficient, 0);
    }

    // ── Méthodes diagramme ─────────────────────────────────
    public void ajouterMatiere()    { /* délégué au DAO */ }
    public void modifierMatiere()   { /* délégué au DAO */ }
    public void supprimerMatiere()  { /* délégué au DAO */ }

    // ── Getters / Setters ──────────────────────────────────
    public int    getId()                { return id; }
    public void   setId(int id)          { this.id = id; }

    public String getNom()               { return nom; }
    public void   setNom(String nom)     { this.nom = nom; }

    public String getCode()                  { return code; }
    public void   setCode(String code)       { this.code = code; }

    public double getCoefficient()                        { return coefficient; }
    public void   setCoefficient(double coefficient)      { this.coefficient = coefficient; }

    public int    getVolumeHoraire()                      { return volumeHoraire; }
    public void   setVolumeHoraire(int volumeHoraire)     { this.volumeHoraire = volumeHoraire; }

    @Override
    public String toString() {
        return nom;
    }
}

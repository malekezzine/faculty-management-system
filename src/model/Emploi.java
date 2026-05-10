package model;

/**
 * Diagramme : EmploiDuTemps
 * Champ ajouté : semaine
 */
public class Emploi {

    private int    id;
    private String jour;
    private String heure;
    private int    salle;
    private int    groupeId;
    private int    matiereId;
    private int    enseignantId;
    private String semaine;   // nouveau — diagramme ex: "S1", "2024-W10"

    public Emploi() {}

    public Emploi(int id, String jour, String heure, int salle,
                  int groupeId, int matiereId, int enseignantId, String semaine) {
        this.id           = id;
        this.jour         = jour;
        this.heure        = heure;
        this.salle        = salle;
        this.groupeId     = groupeId;
        this.matiereId    = matiereId;
        this.enseignantId = enseignantId;
        this.semaine      = semaine != null ? semaine : "";
    }

    /** Constructeur rétro-compatible */
    public Emploi(int id, String jour, String heure, int salle,
                  int groupeId, int matiereId, int enseignantId) {
        this(id, jour, heure, salle, groupeId, matiereId, enseignantId, "");
    }

    // ── Méthodes diagramme ─────────────────────────────────
    public void creer()          { /* délégué au DAO */ }
    public void modifier()       { /* délégué au DAO */ }
    public void afficher()       { /* délégué au DAO */ }
    public void consulterEmploi(){ /* délégué au DAO */ }

    // ── Getters / Setters ──────────────────────────────────
    public int    getId()                { return id; }
    public void   setId(int id)          { this.id = id; }

    public String getJour()              { return jour; }
    public void   setJour(String jour)   { this.jour = jour; }

    public String getHeure()               { return heure; }
    public void   setHeure(String heure)   { this.heure = heure; }

    public int    getSalle()             { return salle; }
    public void   setSalle(int salle)    { this.salle = salle; }

    public int    getGroupeId()                    { return groupeId; }
    public void   setGroupeId(int groupeId)        { this.groupeId = groupeId; }

    public int    getMatiereId()                     { return matiereId; }
    public void   setMatiereId(int matiereId)        { this.matiereId = matiereId; }

    public int    getEnseignantId()                        { return enseignantId; }
    public void   setEnseignantId(int enseignantId)        { this.enseignantId = enseignantId; }

    public String getSemaine()                   { return semaine; }
    public void   setSemaine(String semaine)     { this.semaine = semaine; }

    @Override
    public String toString() {
        return "Emploi{id=" + id + ", jour='" + jour + "', heure='" + heure
               + "', salle=" + salle + ", semaine='" + semaine + "'}";
    }
}

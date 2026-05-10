package model;

import java.sql.Date;

/**
 * Diagramme : étudiant  extends Utilisateur
 * Champs ajoutés : niveau, filiere
 */
public class Etudiant extends Utilisateur {

    private int    cin;
    private int    groupeId;
    private String email;
    private String telephone;
    private Date   dateNaissance;
    private String adresse;
    private String niveau;      // nouveau — diagramme
    private String filiere;     // nouveau — diagramme

    public Etudiant() {}

    /** Constructeur complet */
    public Etudiant(int id, String nom, String prenom, int cin, int groupeId,
                    String email, String telephone, Date dateNaissance, String adresse,
                    String login, String motDePasse, String niveau, String filiere) {
        super(id, nom, prenom, login, motDePasse);
        this.cin           = cin;
        this.groupeId      = groupeId;
        this.email         = email;
        this.telephone     = telephone;
        this.dateNaissance = dateNaissance;
        this.adresse       = adresse;
        this.niveau        = niveau  != null ? niveau  : "";
        this.filiere       = filiere != null ? filiere : "";
    }

    /** Constructeur rétro-compatible (sans niveau/filiere) */
    public Etudiant(int id, String nom, String prenom, int cin, int groupeId,
                    String email, String telephone, Date dateNaissance, String adresse,
                    String login, String motDePasse) {
        this(id, nom, prenom, cin, groupeId, email, telephone,
             dateNaissance, adresse, login, motDePasse, "", "");
    }

    /** Constructeur minimal (login/mdp dérivés du CIN) */
    public Etudiant(int id, String nom, String prenom, int cin, int groupeId,
                    String email, String telephone, Date dateNaissance, String adresse) {
        this(id, nom, prenom, cin, groupeId, email, telephone, dateNaissance, adresse,
             prenom.toLowerCase() + "." + nom.toLowerCase(), String.valueOf(cin));
    }

    // ── Méthodes diagramme ─────────────────────────────────
    public void sInscrire() { /* logique métier */ }
    public void gererEtudiant() { /* logique métier */ }

    // ── Getters / Setters ──────────────────────────────────
    public int    getCin()                { return cin; }
    public void   setCin(int cin)         { this.cin = cin; }

    public int    getGroupeId()                    { return groupeId; }
    public void   setGroupeId(int groupeId)        { this.groupeId = groupeId; }

    public String getEmail()                  { return email; }
    public void   setEmail(String email)      { this.email = email; }

    public String getTelephone()                      { return telephone; }
    public void   setTelephone(String telephone)      { this.telephone = telephone; }

    public Date   getDateNaissance()                          { return dateNaissance; }
    public void   setDateNaissance(Date dateNaissance)        { this.dateNaissance = dateNaissance; }

    public String getAdresse()                    { return adresse; }
    public void   setAdresse(String adresse)      { this.adresse = adresse; }

    public String getNiveau()                   { return niveau; }
    public void   setNiveau(String niveau)      { this.niveau = niveau; }

    public String getFiliere()                    { return filiere; }
    public void   setFiliere(String filiere)      { this.filiere = filiere; }

    /** Alias pour compatibilité avec le code existant */
    public String getMotDePasse()              { return getMdp(); }
    public void   setMotDePasse(String mdp)    { setMdp(mdp); }

    @Override
    public String toString() {
        return "Etudiant{id=" + id + ", nom='" + nom + "', prenom='" + prenom
               + "', cin=" + cin + ", login='" + login + "'}";
    }
}

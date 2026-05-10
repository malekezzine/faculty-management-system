package model;

/**
 * Classe abstraite mère — diagramme de classe : utilisateur
 * Héritée par Etudiant, Enseignant, Admin
 */
public abstract class Utilisateur {

    protected int    id;
    protected String nom;
    protected String prenom;
    protected String mdp;       // mot de passe
    protected String login;

    public Utilisateur() {}

    public Utilisateur(int id, String nom, String prenom, String login, String mdp) {
        this.id     = id;
        this.nom    = nom;
        this.prenom = prenom;
        this.login  = login;
        this.mdp    = mdp;
    }

    /** Authentification — redéfinie si besoin dans les sous-classes */
    public boolean seConnecter(String login, String mdp) {
        return this.login != null && this.login.equals(login)
            && this.mdp   != null && this.mdp.equals(mdp);
    }

    // ── Getters / Setters ──────────────────────────────────
    public int    getId()              { return id; }
    public void   setId(int id)        { this.id = id; }

    public String getNom()             { return nom; }
    public void   setNom(String nom)   { this.nom = nom; }

    public String getPrenom()                  { return prenom; }
    public void   setPrenom(String prenom)     { this.prenom = prenom; }

    public String getMdp()             { return mdp; }
    public void   setMdp(String mdp)   { this.mdp = mdp; }

    public String getLogin()               { return login; }
    public void   setLogin(String login)   { this.login = login; }
}

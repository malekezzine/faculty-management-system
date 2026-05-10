package model;

/**
 * Diagramme : administrateurDuPersonnel  extends Utilisateur
 * Champs : matrice, poste, service
 */
public class Admin extends Utilisateur {

    private String matrice;
    private String poste;
    private String service;

    public Admin() {}

    public Admin(int id, String nom, String prenom,
                 String login, String mdp,
                 String matrice, String poste, String service) {
        super(id, nom, prenom, login, mdp);
        this.matrice = matrice != null ? matrice : "";
        this.poste   = poste   != null ? poste   : "";
        this.service = service != null ? service : "";
    }

    // ── Getters / Setters ──────────────────────────────────
    public String getMatrice()                  { return matrice; }
    public void   setMatrice(String matrice)    { this.matrice = matrice; }

    public String getPoste()                { return poste; }
    public void   setPoste(String poste)    { this.poste = poste; }

    public String getService()                  { return service; }
    public void   setService(String service)    { this.service = service; }

    @Override
    public String toString() {
        return "Admin{id=" + id + ", nom='" + nom + "', login='" + login
               + "', poste='" + poste + "', service='" + service + "'}";
    }
}

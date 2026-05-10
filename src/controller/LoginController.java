package controller;

import dao.EtudiantDAO;
import dao.EnseignantDAO;
import model.Etudiant;
import model.Enseignant;
import view.*; //toutes les interfaces graphiques (LoginView, AdminView, etc.)


//C’est le contrôleur dans l’architecture MVC (Model - View - Controller)
//Il gère la logique du login.
public class LoginController {

    private final LoginView view;
    private final EtudiantDAO  etudiantDAO  = new EtudiantDAO();
    private final EnseignantDAO enseignantDAO = new EnseignantDAO();

    //Constructeur
    public LoginController(LoginView view) {
        this.view = view;
        view.getBtnLogin().addActionListener(e -> handleLogin()); //si on clique sur button appeler fonction
    }

    private void handleLogin() {
        String login = view.getLogin();
        String pass  = view.getPassword();
        String role  = view.getRole();

        if (login.isEmpty() || pass.isEmpty()) {
            view.showError("Veuillez renseigner le login et le mot de passe.");
            return;
        }

        switch (role) {
            case "Admin" -> loginAdmin(login, pass);
            case "Enseignant" -> loginEnseignant(login, pass);
            case "Étudiant" -> loginEtudiant(login, pass);
        }
    }

    // ── Admin : identifiants fixes ─────────────────────────
    private void loginAdmin(String login, String pass) {
        if (login.equals("admin") && pass.equals("admin123")) {
            view.dispose(); //fermer interface login
            AdminView adminView = new AdminView(); //ouvrir interface admin
            new AdminController(adminView);
            adminView.setVisible(true);
        } else {
            view.showError("Identifiants admin incorrects.");
        }
    }

    // ── Enseignant : authentification via base de données ─────────────────
    //    login        = prenom.nom  (ex: hassan.ben)
    //    mot_de_passe = prof123 par défaut (modifiable en base)
    private void loginEnseignant(String login, String pass) {
        Enseignant enseignant = enseignantDAO.authenticate(login, pass); //verifier si existe dans bd
        if (enseignant != null) {
            view.dispose();
            EnseignantView ensView = new EnseignantView(
                enseignant.getPrenom() + " " + enseignant.getNom(),
                enseignant.getId()
            );
            new EnseignantController(ensView); //ouvrir l'interface
            ensView.setVisible(true);
        } else {
            view.showError( //messagebox erreur
                "Identifiants enseignant incorrects.\n\n" +
                "Login    : prenom.nom  (ex: hassan.ben)\n" +
                "Mot de passe : prof123 (par défaut)"
            );
        }
    }

    // ── Étudiant : authentification via base de données ───
    //    login        = prenom.nom  (ex: yasmine.bk)
    //    mot_de_passe = numéro CIN  (ex: 14522360)
    private void loginEtudiant(String login, String pass) {
        Etudiant etudiant = etudiantDAO.authenticate(login, pass);
        if (etudiant != null) {
            view.dispose();
            EtudiantView etView = new EtudiantView(etudiant.getNom(), etudiant.getPrenom());
            new EtudiantController(etView, etudiant.getId());
            etView.setVisible(true);
        } else {
            view.showError(
                "Identifiants incorrects.\n\n" +
                "Login    : prenom.nom  (ex: yasmine.bk)\n" +
                "Mot de passe : votre numéro CIN"
            );
        }
    }
}

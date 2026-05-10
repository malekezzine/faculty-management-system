package controller;

import dao.*;
import model.*;
import view.EnseignantView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class EnseignantController {
//attributs
    private final EnseignantView view; //reference interface
    private final int            enseignantId; //id enseigant
//DAO
    private final GroupeDAO   groupeDAO   = new GroupeDAO(); //acces aux chaque champs
    private final EtudiantDAO etudiantDAO = new EtudiantDAO();
    private final AbsenceDAO  absenceDAO  = new AbsenceDAO();
    private final EmploiDAO   emploiDAO   = new EmploiDAO();
    private final MatiereDAO  matiereDAO  = new MatiereDAO();
//constructeur
    public EnseignantController(EnseignantView view) {
        this.view         = view;
        this.enseignantId = view.getEnseignantId();
        init();
    }

    private void init() {
        loadGroupes();
        loadMatieres();
        loadHistoMatieres();
        loadPlanning(0);
        bindActions();
    }

    // ── Chargement initial ────────────────────────────────────────────────────
    private void loadGroupes() {
        List<Groupe> groupes = enseignantId > 0
            ? groupeDAO.getByEnseignant(enseignantId) //récupère groupes de cet enseignant
            : groupeDAO.getAll(); //sinon tous grps
        view.setGroupes(groupes); //afficher dans ComboBox
        if (!groupes.isEmpty()) loadEtudiants(groupes.get(0).getId()); //charger étudiants du premier groupe
    }

    private void loadMatieres() {
        // Matières selon le groupe sélectionné et l'enseignant
        Groupe g = (Groupe) view.getComboGroupes().getSelectedItem(); //récupérer groupe sélectionné
        List<Matiere> matieres;
        if (enseignantId > 0 && g != null && g.getId() > 0) {
            matieres = matiereDAO.getByEnseignantAndGroupe(enseignantId, g.getId());
        } else if (enseignantId > 0) {
            matieres = matiereDAO.getByEnseignant(enseignantId);
        } else {
            matieres = matiereDAO.getAll();
        }
        view.setMatieres(matieres);
    }

    private void loadHistoMatieres() {
        List<Matiere> matieres = enseignantId > 0
            ? matiereDAO.getByEnseignant(enseignantId)
            : matiereDAO.getAll();
        view.setHistoMatieres(matieres);
    }

    private void loadEtudiants(int groupeId) {
        List<Etudiant> etudiants = etudiantDAO.getByGroupe(groupeId);
        List<Object[]> rows = new ArrayList<>(); // jtable list
        for (Etudiant e : etudiants) {
            rows.add(new Object[]{e.getId(), e.getNom(), e.getPrenom(), false});
        }
        view.setEtudiants(rows);
    }

    /** Charge l'historique des absences pour un groupe + matière */
    private void loadHistorique(int groupeId, int matiereId) {
        List<Object[]> raw = absenceDAO.getByGroupeAndMatiere(groupeId, matiereId);
        // raw: {abs_id, etudiant_id, nom, prenom, date_absence}
        // On construit: {abs_id, etudiant_id, nom, prenom, date_str, isAbsent}
        List<Object[]> rows = new ArrayList<>(); //les données de la base
        for (Object[] r : raw) {
            boolean absent = r[0] != null; // abs_id non nul => absent
            String dateStr = r[4] != null ? r[4].toString() : ""; //convertir date
            rows.add(new Object[]{r[0], r[1], r[2], r[3], dateStr, absent});
        }
        view.setHistoData(rows);
    }
 //affiche emploi du temps
 //si groupe sélectionné → filtre
    private void loadPlanning(int groupeId) {
        List<Object[]> raw;
        if (enseignantId > 0) {
            raw = groupeId == 0
                ? emploiDAO.getByEnseignant(enseignantId)
                : emploiDAO.getByEnseignantAndGroupe(enseignantId, groupeId);
        } else {
            raw = groupeId == 0 ? emploiDAO.getAll() : emploiDAO.getEmploiParGroupe(
                groupeDAO.getAll().stream()
                    .filter(g -> g.getId() == groupeId)
                    .map(Groupe::getNom)
                    .findFirst().orElse("")
            );
        }
        view.setPlanningData(toDisplayRows(raw));
    }

    private List<Object[]> toDisplayRows(List<Object[]> full) {
        List<Object[]> result = new ArrayList<>();
        for (Object[] r : full)
            result.add(new Object[]{r[1], r[2], r[5], r[3], r[4]});
        return result;
    }

    // ── Liaison des actions ───────────────────────────────────────────────────
    private void bindActions() {
        // Tab 1 — Changement de groupe => recharger étudiants + matières
        view.getComboGroupes().addActionListener(e -> {
            Groupe g = (Groupe) view.getComboGroupes().getSelectedItem();
            if (g != null) {
                loadEtudiants(g.getId());
                loadMatieres();
            }
        });

        // Tab 1 — Enregistrer absences
        view.getBtnSaveAbsences().addActionListener(e -> saveAbsences());

        // Tab 2 — Afficher historique
        view.getBtnLoadHisto().addActionListener(e -> {
            Groupe  g = view.getHistoSelectedGroupe();
            Matiere m = view.getHistoSelectedMatiere();
            if (g == null || m == null) { view.showError("Sélectionnez un groupe et une matière."); return; }
            loadHistorique(g.getId(), m.getId());
        });

        // Tab 2 — Supprimer l'absence sélectionnée
        view.getBtnSupprimerAbsence().addActionListener(e -> supprimerAbsenceSelectionnee());

        // Tab 2 — Enregistrer les modifications de coches
        view.getBtnSaveHisto().addActionListener(e -> saveHistoChanges());

        // Tab 3 — Filtrer planning
        view.getBtnFilterPlanning().addActionListener(e -> {
            Groupe g = (Groupe) view.getComboPlanningGroupe().getSelectedItem();
            loadPlanning(g == null ? 0 : g.getId());
        });
    }

    // ── Actions métier ────────────────────────────────────────────────────────
    private void saveAbsences() {
        List<Integer> absentIds = view.getAbsentIds(); //récupérer étudiants cochés
        if (absentIds.isEmpty()) { view.showError("Aucun étudiant marqué absent."); return; }

        Matiere matiere = view.getSelectedMatiere();
        if (matiere == null) { view.showError("Veuillez sélectionner une matière."); return; }

        java.sql.Date date = view.getSelectedDate();
        String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(date);

        if (!view.confirmerEnregistrement(absentIds.size(), matiere.getNom(), dateStr)) return;

        int saved = 0, skipped = 0;
        for (int etudiantId : absentIds) {
            if (absenceDAO.existeAbsence(etudiantId, matiere.getId(), date)) {
                skipped++;
            } else if (absenceDAO.ajouter(etudiantId, matiere.getId(), date)) {
                saved++;
            }
        }

        String msg = saved + " absence(s) enregistrée(s) avec succès !\nDate : " + dateStr;
        if (skipped > 0) msg += "\n(" + skipped + " absence(s) déjà existante(s) ignorée(s))";
        view.showMessage(msg);

        Groupe g = (Groupe) view.getComboGroupes().getSelectedItem();
        if (g != null) loadEtudiants(g.getId());
    }

    private void supprimerAbsenceSelectionnee() {
        int row = view.getHistoSelectedRow();
        if (row < 0) { view.showError("Sélectionnez une ligne dans le tableau."); return; }

        Object absId = view.getHistoValue(row, 0);
        if (absId == null) { view.showError("Cet étudiant n'a pas d'absence enregistrée."); return; }

        int rep = javax.swing.JOptionPane.showConfirmDialog(null,
            "Confirmer la suppression de cette absence ?",
            "Confirmation", javax.swing.JOptionPane.YES_NO_OPTION);
        if (rep != javax.swing.JOptionPane.YES_OPTION) return;

        if (absenceDAO.supprimer(Integer.parseInt(absId.toString()))) {
            view.showMessage("Absence supprimée avec succès.");
            // Recharger
            Groupe  g = view.getHistoSelectedGroupe();
            Matiere m = view.getHistoSelectedMatiere();
            if (g != null && m != null) loadHistorique(g.getId(), m.getId());
        } else {
            view.showError("Erreur lors de la suppression.");
        }
    }

    private void saveHistoChanges() {
        Matiere m = view.getHistoSelectedMatiere();
        if (m == null) { view.showError("Sélectionnez une matière."); return; }

        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        int added = 0, removed = 0;

        for (Object[] change : view.getHistoChanges()) {
            Object  absId     = change[0];   // null si pas d'absence
            int     etudId    = Integer.parseInt(change[1].toString());
            boolean isAbsent  = Boolean.TRUE.equals(change[2]);

            if (isAbsent && absId == null) {
                // Nouvelle absence à ajouter
                if (!absenceDAO.existeAbsence(etudId, m.getId(), today) &&
                     absenceDAO.ajouter(etudId, m.getId(), today)) added++;
            } else if (!isAbsent && absId != null) {
                // Supprimer l'absence existante
                if (absenceDAO.supprimer(Integer.parseInt(absId.toString()))) removed++;
            }
        }

        view.showMessage(added + " absence(s) ajoutée(s), " + removed + " supprimée(s).");
        Groupe g = view.getHistoSelectedGroupe();
        if (g != null && m != null) loadHistorique(g.getId(), m.getId());
    }
}

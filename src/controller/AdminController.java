package controller;

import dao.*;
import model.*;
import view.AdminView;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AdminController {

    private final AdminView view;
    private final EtudiantDAO   etudiantDAO   = new EtudiantDAO();
    private final EnseignantDAO enseignantDAO = new EnseignantDAO();
    private final MatiereDAO    matiereDAO    = new MatiereDAO();
    private final GroupeDAO     groupeDAO     = new GroupeDAO();
    private final EmploiDAO     emploiDAO     = new EmploiDAO();

    // Track selected IDs for modify/delete
    private int selectedEtudiantId   = -1;
    private int selectedEnseignantId = -1;
    private int selectedMatiereId    = -1;
    private int selectedEmploiId     = -1;

    public AdminController(AdminView view) {
        this.view = view;
        init();
    }

    private void init() {
        loadCombos(); //chargement
        loadAllTables();
        bindTableSelection();
        bindButtons();
    }

    // ══════════════════════════════════════════════════════════
    //  LOAD DATA
    // ══════════════════════════════════════════════════════════
    private void loadCombos() {
        List<Groupe> groupes = groupeDAO.getAll(); //aficher groupes
        view.setGroupes(groupes);

        // Matière names for emploi combo
        List<String> matiereNoms = new ArrayList<>();
        for (Matiere m : matiereDAO.getAll()) matiereNoms.add(m.getNom());
        view.setMatiereItems(matiereNoms);

        // Enseignant names for emploi combo — dédupliqués par id, prenom+nom
        java.util.LinkedHashMap<Integer,String> ensMap = new java.util.LinkedHashMap<>();
        for (Enseignant e : enseignantDAO.getAll())
            ensMap.putIfAbsent(e.getId(), e.getPrenom() + " " + e.getNom());
        view.setEnseignantItems(new ArrayList<>(ensMap.values()));
    }

    private void loadAllTables() {
        loadEtudiants();
        loadEnseignants();
        loadMatieres();
        loadEmplois(null);
    }

    private void loadEtudiants() {
        List<Object[]> rows = new ArrayList<>();
        for (Etudiant e : etudiantDAO.getAll()) {
            // Resolve groupe name
            String groupeNom = "";
            for (Groupe g : groupeDAO.getAll())
                if (g.getId() == e.getGroupeId()) { groupeNom = g.getNom(); break; }
            rows.add(new Object[]{
                e.getId(), e.getNom(), e.getPrenom(), e.getCin(),
                e.getEmail(), e.getTelephone(), e.getDateNaissance(), e.getAdresse(), groupeNom
            });
        }
        view.setEtudiantData(rows);
    }

    private void loadEnseignants() {
        List<Object[]> rows = new ArrayList<>();
        java.util.LinkedHashMap<Integer, Object[]> seen = new java.util.LinkedHashMap<>();
        for (Enseignant e : enseignantDAO.getAll())
            seen.putIfAbsent(e.getId(), new Object[]{e.getId(), e.getNom(), e.getPrenom(), e.getSpecialite()});
        rows.addAll(seen.values());
        view.setEnseignantData(rows);
    }

    private void loadMatieres() {
        List<Object[]> rows = new ArrayList<>();
        for (Matiere m : matiereDAO.getAll())
            rows.add(new Object[]{m.getId(), m.getNom(), m.getCoefficient()});
        view.setMatiereData(rows);
    }

    private void loadEmplois(String groupeFilter) {
        List<Object[]> rows = (groupeFilter == null)
            ? emploiDAO.getAll()
            : emploiDAO.getEmploiParGroupe(groupeFilter);
        view.setEmploiData(rows);
    }

    // ══════════════════════════════════════════════════════════
    //  TABLE SELECTION → fill form automatiquement
    // ══════════════════════════════════════════════════════════
    private void bindTableSelection() {
        // Étudiant row click → fill form
        view.getTableEtudiant().getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = view.getTableEtudiant().getSelectedRow();
            if (row < 0) return;
            selectedEtudiantId = (int) view.getModelEtudiant().getValueAt(row, 0);
            Object[] data = new Object[9];
            for (int i = 0; i < 9; i++) data[i] = view.getModelEtudiant().getValueAt(row, i);
            view.fillEtudiantForm(data);
        });

        // Enseignant row click
        view.getTableEnseignant().getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = view.getTableEnseignant().getSelectedRow();
            if (row < 0) return;
            selectedEnseignantId = (int) view.getModelEnseignant().getValueAt(row, 0);
            Object[] data = new Object[4];
            for (int i = 0; i < 4; i++) data[i] = view.getModelEnseignant().getValueAt(row, i);
            view.fillEnseignantForm(data);
        });

        // Matière row click
        view.getTableMatiere().getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = view.getTableMatiere().getSelectedRow();
            if (row < 0) return;
            selectedMatiereId = (int) view.getModelMatiere().getValueAt(row, 0);
            Object[] data = new Object[3];
            for (int i = 0; i < 3; i++) data[i] = view.getModelMatiere().getValueAt(row, i);
            view.fillMatiereForm(data);
        });

        // Emploi row click
        view.getTableEDT().getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = view.getTableEDT().getSelectedRow();
            if (row < 0) return;
            selectedEmploiId = (int) view.getModelEDT().getValueAt(row, 0);
            Object[] data = new Object[7];
            for (int i = 0; i < 7; i++) data[i] = view.getModelEDT().getValueAt(row, i);
            view.fillEmploiForm(data);
        });
    }

    // ══════════════════════════════════════════════════════════
    //  BUTTON BINDINGS
    // ══════════════════════════════════════════════════════════
    private void bindButtons() {
        // ── Étudiant ──
        view.getBtnAddEtudiant()   .addActionListener(e -> ajouterEtudiant());
        view.getBtnModifyEtudiant().addActionListener(e -> modifierEtudiant());
        view.getBtnDelEtudiant()   .addActionListener(e -> supprimerEtudiant());

        // ── Enseignant ──
        view.getBtnAddEnseignant()   .addActionListener(e -> ajouterEnseignant());
        view.getBtnModifyEnseignant().addActionListener(e -> modifierEnseignant());
        view.getBtnDelEnseignant()   .addActionListener(e -> supprimerEnseignant());

        // ── Matière ──
        view.getBtnAddMatiere()   .addActionListener(e -> ajouterMatiere());
        view.getBtnModifyMatiere().addActionListener(e -> modifierMatiere());
        view.getBtnDelMatiere()   .addActionListener(e -> supprimerMatiere());

        // ── Emploi ──
        view.getBtnAddEmploi()   .addActionListener(e -> ajouterEmploi());
        view.getBtnModifyEmploi().addActionListener(e -> modifierEmploi());
        view.getBtnDelEmploi()   .addActionListener(e -> supprimerEmploi());
        view.getBtnFiltrerEmploi().addActionListener(e -> filtrerEmploi());
    }

    // ══════════════════════════════════════════════════════════
    //  ÉTUDIANT CRUD
    // ══════════════════════════════════════════════════════════
    private void ajouterEtudiant() {
        try {
            Etudiant e = buildEtudiantFromForm();
            if (e == null) return;
            if (etudiantDAO.ajouter(e)) {
                view.showMessage("Étudiant ajouté avec succès !");
                view.clearEtudiantForm();
                loadEtudiants();
            } else view.showError("Échec de l'ajout.");
        } catch (Exception ex) { view.showError("Données invalides : " + ex.getMessage()); }
    }

    private void modifierEtudiant() {
        if (selectedEtudiantId < 0) { view.showError("Sélectionnez un étudiant dans le tableau."); return; }
        try {
            Etudiant e = buildEtudiantFromForm();
            if (e == null) return;
            e.setId(selectedEtudiantId);
            if (etudiantDAO.modifier(e)) {
                view.showMessage("Étudiant modifié !");
                view.clearEtudiantForm();
                selectedEtudiantId = -1;
                loadEtudiants();
            } else view.showError("Échec de la modification.");
        } catch (Exception ex) { view.showError("Données invalides : " + ex.getMessage()); }
    }

    private void supprimerEtudiant() {
        if (selectedEtudiantId < 0) { view.showError("Sélectionnez un étudiant dans le tableau."); return; }
        if (!view.confirm("Supprimer cet étudiant ?\n\nATTENTION : toutes ses absences seront également supprimées.")) return;
        if (etudiantDAO.supprimer(selectedEtudiantId)) {
            view.showMessage("Étudiant et ses absences supprimés avec succès !");
            view.clearEtudiantForm();
            selectedEtudiantId = -1;
            loadEtudiants();
        } else view.showError("Échec de la suppression.");
    }
//récupère les données que l’utilisateur a saisies
    private Etudiant buildEtudiantFromForm() {
        Groupe g = view.getSelectedGroupe();
        if (g == null) { view.showError("Veuillez sélectionner un groupe."); return null; }
        Etudiant e = new Etudiant();
        e.setNom(view.getTxtNom());
        e.setPrenom(view.getTxtPrenom());
        e.setCin(Integer.parseInt(view.getTxtCin()));
        e.setEmail(view.getTxtEmail());
        e.setTelephone(view.getTxtTelephone());
        e.setDateNaissance(Date.valueOf(view.getTxtDateNaissance()));
        e.setAdresse(view.getTxtAdresse());
        e.setGroupeId(g.getId());
        return e;
    }

    // ══════════════════════════════════════════════════════════
    //  ENSEIGNANT CRUD
    // ══════════════════════════════════════════════════════════
    private void ajouterEnseignant() {
        Enseignant e = new Enseignant(0,
            view.getTxtEnsNom(), view.getTxtEnsPrenom(), view.getTxtEnsSpecialite());
        if (enseignantDAO.ajouter(e)) {
            view.showMessage("Enseignant ajouté !");
            loadEnseignants();
            loadCombos(); // refresh enseignant combo in emploi
        } else view.showError("Échec de l'ajout.");
    }

    private void modifierEnseignant() {
        if (selectedEnseignantId < 0) { view.showError("Sélectionnez un enseignant dans le tableau."); return; }
        if (enseignantDAO.modifier(selectedEnseignantId,
                view.getTxtEnsNom(), view.getTxtEnsPrenom(), view.getTxtEnsSpecialite(), "")) {
            view.showMessage("Enseignant modifié !");
            selectedEnseignantId = -1;
            loadEnseignants();
            loadCombos();
        } else view.showError("Échec de la modification.");
    }

    private void supprimerEnseignant() {
        if (selectedEnseignantId < 0) { view.showError("Sélectionnez un enseignant dans le tableau."); return; }
        if (!view.confirm("Supprimer cet enseignant ?")) return;
        if (enseignantDAO.supprimer(selectedEnseignantId)) {
            view.showMessage("Enseignant supprimé !");
            selectedEnseignantId = -1;
            loadEnseignants();
            loadCombos();
        } else view.showError("Échec de la suppression.");
    }

    // ══════════════════════════════════════════════════════════
    //  MATIÈRE CRUD
    // ══════════════════════════════════════════════════════════
    private void ajouterMatiere() {
        try {
            Matiere m = new Matiere(0, view.getTxtMatNom(), "",
                Double.parseDouble(view.getTxtMatCoef()), 0);
            if (matiereDAO.ajouter(m)) {
                view.showMessage("Matière ajoutée !");
                loadMatieres();
                loadCombos();
            } else view.showError("Échec de l'ajout.");
        } catch (Exception ex) { view.showError("Coefficient invalide : " + ex.getMessage()); }
    }

    private void modifierMatiere() {
        if (selectedMatiereId < 0) { view.showError("Sélectionnez une matière dans le tableau."); return; }
        try {
            if (matiereDAO.modifier(selectedMatiereId, view.getTxtMatNom(), "",
                    Double.parseDouble(view.getTxtMatCoef()), 0)) {
                view.showMessage("Matière modifiée !");
                selectedMatiereId = -1;
                loadMatieres();
                loadCombos();
            } else view.showError("Échec de la modification.");
        } catch (Exception ex) { view.showError("Coefficient invalide : " + ex.getMessage()); }
    }

    private void supprimerMatiere() {
        if (selectedMatiereId < 0) { view.showError("Sélectionnez une matière dans le tableau."); return; }
        if (!view.confirm("Supprimer cette matière ?\n\nATTENTION : toutes les absences liées à cette matière seront également supprimées.")) return;
        if (matiereDAO.supprimer(selectedMatiereId)) {
            view.showMessage("Matière supprimée !");
            selectedMatiereId = -1;
            loadMatieres();
            loadCombos();
        } else view.showError("Échec de la suppression.");
    }

    // ══════════════════════════════════════════════════════════
    //  EMPLOI CRUD
    // ══════════════════════════════════════════════════════════
    private void ajouterEmploi() {
        try {
            int[] ids = resolveEmploiIds();
            if (ids == null) return;
            int salle = Integer.parseInt(view.getTxtEmploiSalle());
            if (emploiDAO.ajouter(view.getTxtEmploiJour(), view.getTxtEmploiHeure(),
                    salle, ids[0], ids[1], ids[2])) {
                view.showMessage("Créneau ajouté !");
                loadEmplois(null);
            } else view.showError("Échec de l'ajout.");
        } catch (Exception ex) { view.showError("Données invalides : " + ex.getMessage()); }
    }

    private void modifierEmploi() {
        if (selectedEmploiId < 0) { view.showError("Sélectionnez un créneau dans le tableau."); return; }
        try {
            int[] ids = resolveEmploiIds();
            if (ids == null) return;
            int salle = Integer.parseInt(view.getTxtEmploiSalle());
            if (emploiDAO.modifier(selectedEmploiId, view.getTxtEmploiJour(),
                    view.getTxtEmploiHeure(), salle, ids[0], ids[1], ids[2])) {
                view.showMessage("Créneau modifié !");
                selectedEmploiId = -1;
                loadEmplois(null);
            } else view.showError("Échec de la modification.");
        } catch (Exception ex) { view.showError("Données invalides : " + ex.getMessage()); }
    }

    private void supprimerEmploi() {
        if (selectedEmploiId < 0) { view.showError("Sélectionnez un créneau dans le tableau."); return; }
        if (!view.confirm("Supprimer ce créneau ?")) return;
        if (emploiDAO.supprimer(selectedEmploiId)) {
            view.showMessage("Créneau supprimé !");
            selectedEmploiId = -1;
            loadEmplois(null);
        } else view.showError("Échec de la suppression.");
    }

    private void filtrerEmploi() {
        Groupe g = view.getSelectedFiltreGroupe();
        if (g == null) loadEmplois(null);
        else loadEmplois(g.getNom());
    }

    /**
     * Resolves groupe_id, matiere_id, enseignant_id from the form combos.
     * Returns int[]{groupeId, matiereId, enseignantId} or null on error.
     */
    private int[] resolveEmploiIds() {
        Groupe g = view.getSelectedEmploiGroupe();
        String matiereNom = view.getSelectedEmploiMatiere();
        String ensNom     = view.getSelectedEmploiEnseignant();

        if (g == null || matiereNom == null || ensNom == null) {
            view.showError("Renseignez groupe, matière et enseignant.");
            return null;
        }

        int matiereId = -1;
        for (Matiere m : matiereDAO.getAll())
            if (m.getNom().equals(matiereNom)) { matiereId = m.getId(); break; }

        int ensId = -1;
        for (Enseignant e : enseignantDAO.getAll()) {
            String label = e.getPrenom() + " " + e.getNom();
            if (label.equals(ensNom) || e.getNom().equals(ensNom)) { ensId = e.getId(); break; }
        }

        if (matiereId < 0 || ensId < 0) {
            view.showError("Matière ou enseignant introuvable.");
            return null;
        }

        return new int[]{g.getId(), matiereId, ensId};
    }
}

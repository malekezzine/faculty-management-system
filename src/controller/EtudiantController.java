package controller;

import dao.*; //acces a la bd
import model.*; //class métier (etudiant / absence)
import view.EtudiantView;

import java.util.List;

public class EtudiantController {

    private final EtudiantView view;
    private final EtudiantDAO  etudiantDAO = new EtudiantDAO(); //récupérer infos étudiant
    private final AbsenceDAO   absenceDAO  = new AbsenceDAO();
    private final EmploiDAO    emploiDAO   = new EmploiDAO();
    private final int          etudiantId;

    public EtudiantController(EtudiantView view, int etudiantId) {
        this.view       = view;
        this.etudiantId = etudiantId;
        loadInfo();
        loadAbsences();
        loadPlanning();
    }

    private void loadInfo() {
        Etudiant e = etudiantDAO.getById(etudiantId);
        if (e != null) {
            view.setEtudiantInfo(e); // si existe on envoie les infos a la vue
        } else {
            view.showError("Étudiant introuvable (id=" + etudiantId + ")");
        }
    }

    private void loadAbsences() {
        List<Object[]> rows = absenceDAO.getByEtudiantWithMatiere(etudiantId); //liste de lignes / chaque ligne = tab de colonne
        view.setAbsences(rows);
    }

    private void loadPlanning() {
        List<Object[]> rows = emploiDAO.getEmploiParEtudiant(etudiantId);
        view.setPlanningData(rows);
    }
}

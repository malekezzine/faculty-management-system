package model;

import java.sql.Timestamp;

public class Absence {
    private int id;
    private int etudiantId;
    private int matiereId;
    private Timestamp dateAbsence;

    public Absence() {} //vide
//constructeur complet
    public Absence(int id, int etudiantId, int matiereId, Timestamp dateAbsence) {
        this.id = id;
        this.etudiantId = etudiantId;
        this.matiereId = matiereId;
        this.dateAbsence = dateAbsence;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEtudiantId() { return etudiantId; }
    public void setEtudiantId(int etudiantId) { this.etudiantId = etudiantId; }

    public int getMatiereId() { return matiereId; }
    public void setMatiereId(int matiereId) { this.matiereId = matiereId; }

    public Timestamp getDateAbsence() { return dateAbsence; }
    public void setDateAbsence(Timestamp dateAbsence) { this.dateAbsence = dateAbsence; }

    @Override
    public String toString() {
        return "Absence{id=" + id + ", etudiantId=" + etudiantId +
               ", matiereId=" + matiereId + ", dateAbsence=" + dateAbsence + "}";
    }
}


//La classe Absence est un modèle représentant une absence d’un étudiant.
// Elle contient les attributs correspondant
// à la table de la base de données ainsi que
// les getters et setters nécessaires pour manipuler les données.
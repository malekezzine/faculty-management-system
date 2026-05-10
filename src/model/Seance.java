package model;

import java.sql.Date;
import java.sql.Time;

/**
 * Diagramme : séance
 * Liée à une matière et une salle (1 salle pour 1 séance)
 */
public class Seance {

    private int     idSeance;
    private Date    date;
    private Time    debut;
    private Time    hFin;
    private int     matiereId;
    private int     salleId;
    private int     groupeId;

    public Seance() {}

    public Seance(int idSeance, Date date, Time debut, Time hFin,
                  int matiereId, int salleId, int groupeId) {
        this.idSeance  = idSeance;
        this.date      = date;
        this.debut     = debut;
        this.hFin      = hFin;
        this.matiereId = matiereId;
        this.salleId   = salleId;
        this.groupeId  = groupeId;
    }

    // ── Méthode diagramme ──────────────────────────────────
    public void planifier() { /* délégué au DAO */ }

    // ── Getters / Setters ──────────────────────────────────
    public int  getIdSeance()               { return idSeance; }
    public void setIdSeance(int idSeance)   { this.idSeance = idSeance; }

    public Date getDate()            { return date; }
    public void setDate(Date date)   { this.date = date; }

    public Time getDebut()             { return debut; }
    public void setDebut(Time debut)   { this.debut = debut; }

    public Time getHFin()            { return hFin; }
    public void setHFin(Time hFin)   { this.hFin = hFin; }

    public int  getMatiereId()                  { return matiereId; }
    public void setMatiereId(int matiereId)     { this.matiereId = matiereId; }

    public int  getSalleId()                { return salleId; }
    public void setSalleId(int salleId)     { this.salleId = salleId; }

    public int  getGroupeId()                 { return groupeId; }
    public void setGroupeId(int groupeId)     { this.groupeId = groupeId; }

    @Override
    public String toString() {
        return "Seance{id=" + idSeance + ", date=" + date
               + ", " + debut + "-" + hFin + ", salle=" + salleId + "}";
    }
}

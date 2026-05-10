package model;

/**
 * Diagramme : salle
 */
public class Salle {

    private int idSalle;
    private int capacite;

    public Salle() {}

    public Salle(int idSalle, int capacite) {
        this.idSalle  = idSalle;
        this.capacite = capacite;
    }

    public int  getIdSalle()              { return idSalle; }
    public void setIdSalle(int idSalle)   { this.idSalle = idSalle; }

    public int  getCapacite()               { return capacite; }
    public void setCapacite(int capacite)   { this.capacite = capacite; }

    @Override
    public String toString() {
        return "Salle{id=" + idSalle + ", capacite=" + capacite + "}";
    }
}

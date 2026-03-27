package models.dto;

public class PointStatus {
    private int id;
    private String adresse;
    private double remplissage;
    private boolean full;

    public PointStatus() {}

    public PointStatus(int id, String adresse, double remplissage, boolean full) {
        this.id = id;
        this.adresse = adresse;
        this.remplissage = remplissage;
        this.full = full;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public double getRemplissage() { return remplissage; }
    public void setRemplissage(double remplissage) { this.remplissage = remplissage; }

    public boolean isFull() { return full; }
    public void setFull(boolean full) { this.full = full; }
}
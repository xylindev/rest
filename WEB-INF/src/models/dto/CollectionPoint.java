package models.dto;

import java.util.List;

public class CollectionPoint {
    private int id;
    private String adresse;
    private int capaciteMax;
    private List<WasteType> acceptedWastes;

    public CollectionPoint() {}

    public CollectionPoint(int id, String adresse, int capaciteMax) {
        this.id = id;
        this.adresse = adresse;
        this.capaciteMax = capaciteMax;
    }

    public int getId() { return id; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public int getCapaciteMax() { return capaciteMax; }
    public void setCapaciteMax(int capaciteMax) { this.capaciteMax = capaciteMax; }
    public List<WasteType> getAcceptedWastes() { return acceptedWastes; }
    public void setAcceptedWastes(List<WasteType> acceptedWastes) { this.acceptedWastes = acceptedWastes; }
}
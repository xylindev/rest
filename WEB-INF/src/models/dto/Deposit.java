package models.dto;

import java.sql.Timestamp;

public class Deposit {
    private int id;
    private int userId;
    private int pointId;
    private int wasteTypeId;
    private double poids;
    private Timestamp dateDepot;
    private boolean collecte;

    public Deposit() {}

    public Deposit(int id, int userId, int pointId, int wasteTypeId, double poids, Timestamp dateDepot, boolean collecte) {
        this.id = id;
        this.userId = userId;
        this.pointId = pointId;
        this.wasteTypeId = wasteTypeId;
        this.poids = poids;
        this.dateDepot = dateDepot;
        this.collecte = collecte;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getPointId() { return pointId; }
    public void setPointId(int pointId) { this.pointId = pointId; }

    public int getWasteTypeId() { return wasteTypeId; }
    public void setWasteTypeId(int wasteTypeId) { this.wasteTypeId = wasteTypeId; }

    public double getPoids() { return poids; }
    public void setPoids(double poids) { this.poids = poids; }

    public Timestamp getDateDepot() { return dateDepot; }
    public void setDateDepot(Timestamp dateDepot) { this.dateDepot = dateDepot; }

    public boolean isCollecte() { return collecte; }
    public void setCollecte(boolean collecte) { this.collecte = collecte; }
}
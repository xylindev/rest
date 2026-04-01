package models.dto;

public class DepositDetail {
    private int id;
    private int userId;
    private String wasteTypeName;
    private String pointAdresse;
    private double poids;
    private java.sql.Timestamp dateDepot;
    private boolean collecte;

    public DepositDetail() {}

    public DepositDetail(int id, int userId, String wasteTypeName, String pointAdresse,
                         double poids, java.sql.Timestamp dateDepot, boolean collecte) {
        this.id = id;
        this.userId = userId;
        this.wasteTypeName = wasteTypeName;
        this.pointAdresse = pointAdresse;
        this.poids = poids;
        this.dateDepot = dateDepot;
        this.collecte = collecte;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getWasteTypeName() { return wasteTypeName; }
    public void setWasteTypeName(String wasteTypeName) { this.wasteTypeName = wasteTypeName; }

    public String getPointAdresse() { return pointAdresse; }
    public void setPointAdresse(String pointAdresse) { this.pointAdresse = pointAdresse; }

    public double getPoids() { return poids; }
    public void setPoids(double poids) { this.poids = poids; }

    public java.sql.Timestamp getDateDepot() { return dateDepot; }
    public void setDateDepot(java.sql.Timestamp dateDepot) { this.dateDepot = dateDepot; }

    public boolean isCollecte() { return collecte; }
    public void setCollecte(boolean collecte) { this.collecte = collecte; }
}

package models.dto;

public class WasteType {
    private int id;
    private String name;
    private int pointsPerKilo;

    public WasteType(int id, String name, int pointsPerKilo){
        this.id = id;
        this.name = name;
        this.pointsPerKilo = pointsPerKilo;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPointsPerKilo() {
        return pointsPerKilo;
    }

    public void setPointsPerKilo(int pointsPerKilo) {
        this.pointsPerKilo = pointsPerKilo;
    }
}

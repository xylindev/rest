package models.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import models.db.PSQLConnection;
import models.dto.Deposit;

public class DepositDAO {
    private final Connection CONNECTION;

    public DepositDAO() throws SQLException, ClassNotFoundException, IOException {
        this.CONNECTION = PSQLConnection.getConnection();
    }

    public Connection getCONNECTION() {
        return CONNECTION;
    }

    public boolean isSaturated(int pointId, double newPoids) throws SQLException {
        String sqlCapacite = "SELECT capaciteMax FROM CollectionPoint WHERE id = ?";
        PreparedStatement psCapacite = CONNECTION.prepareStatement(sqlCapacite);
        psCapacite.setInt(1, pointId);
        ResultSet rsCapacite = psCapacite.executeQuery();
        
        if (!rsCapacite.next()) {
            throw new SQLException("Point de collecte introuvable pour l'ID " + pointId);
        }
        int capaciteMax = rsCapacite.getInt("capaciteMax");

        String sqlCharge = "SELECT SUM(poids) AS total FROM Deposit WHERE pointId = ? AND collecte = false";
        PreparedStatement psCharge = CONNECTION.prepareStatement(sqlCharge);
        psCharge.setInt(1, pointId);
        ResultSet rsCharge = psCharge.executeQuery();
        
        double chargeActuelle = 0;
        if (rsCharge.next()) {
            chargeActuelle = rsCharge.getDouble("total");
        }

        return (chargeActuelle + newPoids) > capaciteMax;
    }

    public boolean insert(Deposit deposit) throws SQLException {
        String sql = "INSERT INTO Deposit (userid, pointid, wastetypeid, poids, datedepot, collecte) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, false)";
        
        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ps.setInt(1, deposit.getUserId());
        ps.setInt(2, deposit.getPointId());
        ps.setInt(3, deposit.getWasteTypeId());
        ps.setDouble(4, deposit.getPoids());

        return ps.executeUpdate() > 0;
    }

    public java.util.List<Deposit> findAll() throws SQLException {
        java.util.List<Deposit> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM Deposit";
        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Deposit(rs.getInt("id"), rs.getInt("userid"), rs.getInt("pointid"), rs.getInt("wastetypeid"), rs.getDouble("poids"), rs.getTimestamp("datedepot"), rs.getBoolean("collecte")));
        }
        return list;
    }

    public Deposit find(int id) throws SQLException {
        String sql = "SELECT * FROM Deposit WHERE id = ?";
        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Deposit(rs.getInt("id"), rs.getInt("userid"), rs.getInt("pointid"), rs.getInt("wastetypeid"), rs.getDouble("poids"), rs.getTimestamp("datedepot"), rs.getBoolean("collecte"));
        }
        return null;
    }
}
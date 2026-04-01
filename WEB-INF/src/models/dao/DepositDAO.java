package models.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.db.PSQLConnection;
import models.dto.Deposit;
import models.dto.DepositDetail;

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
        String sql = "INSERT INTO Deposit (userid, pointid, wastetypeid, poids, datedepot, collecte) " +
                     "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, false)";

        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ps.setInt(1, deposit.getUserId());
        ps.setInt(2, deposit.getPointId());
        ps.setInt(3, deposit.getWasteTypeId());
        ps.setDouble(4, deposit.getPoids());

        return ps.executeUpdate() > 0;
    }

    public boolean update(Deposit deposit) throws SQLException {
        String sql = "UPDATE Deposit SET userid=?, pointid=?, wastetypeid=?, poids=?, collecte=? WHERE id=?";

        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ps.setInt(1, deposit.getUserId());
        ps.setInt(2, deposit.getPointId());
        ps.setInt(3, deposit.getWasteTypeId());
        ps.setDouble(4, deposit.getPoids());
        ps.setBoolean(5, deposit.isCollecte());
        ps.setInt(6, deposit.getId());

        return ps.executeUpdate() > 0;
    }

    public List<DepositDetail> findAllDetails() throws SQLException {
        List<DepositDetail> list = new ArrayList<>();
        String sql = "SELECT d.id, d.userid, w.name AS wasteTypeName, c.adresse AS pointAdresse, " +
                     "d.poids, d.datedepot, d.collecte " +
                     "FROM Deposit d " +
                     "JOIN WasteType w ON d.wastetypeid = w.id " +
                     "JOIN CollectionPoint c ON d.pointid = c.id";

        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new DepositDetail(
                rs.getInt("id"),
                rs.getInt("userid"),
                rs.getString("wasteTypeName"),
                rs.getString("pointAdresse"),
                rs.getDouble("poids"),
                rs.getTimestamp("datedepot"),
                rs.getBoolean("collecte")
            ));
        }
        return list;
    }

    public List<Deposit> findAll() throws SQLException {
        List<Deposit> list = new ArrayList<>();
        String sql = "SELECT * FROM Deposit";
        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new Deposit(
                rs.getInt("id"),
                rs.getInt("userid"),
                rs.getInt("pointid"),
                rs.getInt("wastetypeid"),
                rs.getDouble("poids"),
                rs.getTimestamp("datedepot"),
                rs.getBoolean("collecte")
            ));
        }
        return list;
    }

    public Deposit find(int id) throws SQLException {
        String sql = "SELECT * FROM Deposit WHERE id = ?";
        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Deposit(
                rs.getInt("id"),
                rs.getInt("userid"),
                rs.getInt("pointid"),
                rs.getInt("wastetypeid"),
                rs.getDouble("poids"),
                rs.getTimestamp("datedepot"),
                rs.getBoolean("collecte")
            );
        }
        return null;
    }
}

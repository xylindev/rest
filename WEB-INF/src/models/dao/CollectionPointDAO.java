package models.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.db.PSQLConnection;
import models.dto.CollectionPoint;
import models.dto.WasteType;

public class CollectionPointDAO {
    private final Connection CONNECTION;

    public CollectionPointDAO() throws SQLException, ClassNotFoundException, IOException {
        this.CONNECTION = PSQLConnection.getConnection();
    }

    public Connection getCONNECTION() {
        return CONNECTION;
    }

    public List<CollectionPoint> findAll() throws SQLException {
        List<CollectionPoint> list = new ArrayList<>();
        String sql = "SELECT * FROM CollectionPoint";

        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new CollectionPoint(rs.getInt(1), rs.getString(2), rs.getInt(3)));
        }

        return list;
    }

    public CollectionPoint find(int id) throws SQLException {
        CollectionPoint point = null;
        String sql = "SELECT * FROM CollectionPoint WHERE id=?";

        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            point = new CollectionPoint(rs.getInt(1), rs.getString(2), rs.getInt(3));
            point.setAcceptedWastes(findAcceptedWastes(id));
        }

        return point;
    }

    public List<WasteType> findAcceptedWastes(int pointId) throws SQLException {
        List<WasteType> wastes = new ArrayList<>();
        String sql = "SELECT w.id, w.name, w.pointsperkilo FROM wastetype w JOIN accepts a ON w.id = a.wastetypeid WHERE a.pointid=?";

        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ps.setInt(1, pointId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            wastes.add(new WasteType(rs.getInt(1), rs.getString(2), rs.getInt(3)));
        }

        return wastes;
    }

    public boolean insert(CollectionPoint point) throws SQLException {
        String sql = "INSERT INTO CollectionPoint VALUES (?, ?, ?)";

        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ps.setInt(1, point.getId());
        ps.setString(2, point.getAdresse());
        ps.setInt(3, point.getCapaciteMax());

        return ps.executeUpdate() > 0;
    }

    public boolean update(CollectionPoint point) throws SQLException {
        String sql = "UPDATE CollectionPoint SET adresse=?, capacitemax=? WHERE id=?";

        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ps.setString(1, point.getAdresse());
        ps.setInt(2, point.getCapaciteMax());
        ps.setInt(3, point.getId());

        return ps.executeUpdate() > 0;
    }

    public boolean patchAdresse(int id, String adresse) throws SQLException {
        String sql = "UPDATE CollectionPoint SET adresse=? WHERE id=?";

        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ps.setString(1, adresse);
        ps.setInt(2, id);

        return ps.executeUpdate() > 0;
    }

    public boolean clearDeposits(int pointId) throws SQLException {
        String sql = "DELETE FROM deposit WHERE pointid=?";

        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ps.setInt(1, pointId);
        ps.executeUpdate();

        return true;
    }
}

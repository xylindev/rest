package models.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.db.PSQLConnection;
import models.dto.WasteType;

public class WasteTypeDAO {
    private final Connection CONNECTION;
    
    public WasteTypeDAO() throws SQLException, ClassNotFoundException, IOException{
        this.CONNECTION = PSQLConnection.getConnection();
    }

    public Connection getCONNECTION() {
        return CONNECTION;
    }

    public boolean insert(WasteType wasteType) throws SQLException {
        String insert = "insert into wastetype values (?,?,?)";

        PreparedStatement preparedStatement = CONNECTION.prepareStatement(insert);
        preparedStatement.setInt(1, wasteType.getId());
        preparedStatement.setString(2, wasteType.getName());
        preparedStatement.setInt(3, wasteType.getPointsPerKilo());

        return preparedStatement.executeUpdate() > 0;
    }

    public boolean update(WasteType wasteType) throws SQLException {
        String update = "update wastetype set name=?, pointsperkilo=? where id=?";

        PreparedStatement preparedStatement = CONNECTION.prepareStatement(update);
        preparedStatement.setString(1, wasteType.getName());
        preparedStatement.setInt(2, wasteType.getPointsPerKilo());
        preparedStatement.setInt(3, wasteType.getId());

        return preparedStatement.executeUpdate() > 0;
    }

    public boolean delete(int id) throws SQLException {
        String delete = "delete from wastetype where id=?";

        PreparedStatement preparedStatement = CONNECTION.prepareStatement(delete);
        preparedStatement.setInt(1, id);

        return preparedStatement.executeUpdate() > 0;
    }

    public WasteType find(int id) throws SQLException {
        WasteType wasteType = null;

        String query = "select * from wastetype where id=?";
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(query);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if(resultSet.next()){
            wasteType = new WasteType(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getInt(3)
            );
        }

        return wasteType;
    }

    public List<WasteType> findAll() throws SQLException {
        List<WasteType> results = new ArrayList<>();

        String query = "select * from wastetype";
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            results.add(
                new WasteType(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getInt(3)
                )
            );
        }

        return results;
    }
}
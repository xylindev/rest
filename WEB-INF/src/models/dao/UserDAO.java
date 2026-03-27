package models.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import models.db.PSQLConnection;
import models.dto.User;

public class UserDAO {
    private final Connection CONNECTION;

    public UserDAO() throws SQLException, ClassNotFoundException, IOException {
        this.CONNECTION = PSQLConnection.getConnection();
    }

    public Connection getCONNECTION() {
        return CONNECTION;
    }

    public User findByLogin(String login) throws SQLException {
        String sql = "SELECT id, login, password, role FROM users WHERE login=?";
        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ps.setString(1, login);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
        }
        return null;
    }
}

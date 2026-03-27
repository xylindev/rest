package models.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            return new User(rs.getInt("id"), rs.getString("login"), rs.getString("password"), rs.getString("role"));
        }
        return null;
    }

    public User find(int id) throws SQLException {
        String sql = "SELECT id, login, password, role FROM users WHERE id=?";
        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new User(rs.getInt("id"), rs.getString("login"), rs.getString("password"), rs.getString("role"));
        }
        return null;
    }

    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET login=?, password=?, role=? WHERE id=?";
        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ps.setString(1, user.getLogin());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getRole());
        ps.setInt(4, user.getId());

        return ps.executeUpdate() > 0;
    }

    public List<User> getLeaderboard() throws SQLException {
        List<User> leaderboard = new ArrayList<>();

        String sql = "SELECT u.id, u.login, u.password, u.role, SUM(d.poids * w.pointsperkilo) AS score " +
                     "FROM users u " +
                     "JOIN deposit d ON u.id = d.userid " +
                     "JOIN wastetype w ON d.wastetypeid = w.id " +
                     "GROUP BY u.id, u.login, u.password, u.role " +
                     "ORDER BY score DESC " +
                     "LIMIT 10";

        PreparedStatement ps = CONNECTION.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            User user = new User(rs.getInt("id"), rs.getString("login"), rs.getString("password"), rs.getString("role"));
            user.setScore(rs.getDouble("score"));
            leaderboard.add(user);
        }

        return leaderboard;
    }
}
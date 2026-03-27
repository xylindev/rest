package models.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PSQLConnection {
    static String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
    public static Connection getConnection() throws SQLException, ClassNotFoundException, IOException {
        final String DRIVER;
        final String URL;
        final String USER;
        final String PASSWORD;

        InputStream psqlConfig = new FileInputStream(rootPath + "config.psql.prop");

        Properties properties = new Properties();
        properties.load(psqlConfig);

        DRIVER = properties.getProperty("driver");
        Class.forName(DRIVER);

        URL = properties.getProperty("url");
        USER = properties.getProperty("user");
        PASSWORD = properties.getProperty("password");

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
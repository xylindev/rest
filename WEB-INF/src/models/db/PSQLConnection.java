package models.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PSQLConnection {
    public static Connection getConnection() throws SQLException, ClassNotFoundException, IOException {
        final String DRIVER;
        final String URL;
        final String USER;
        final String PASSWORD;

        InputStream psqlConfig = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.psql.prop");

        if (psqlConfig == null) {
            throw new IOException("Le fichier config.psql.prop est introuvable dans le classpath.");
        }

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
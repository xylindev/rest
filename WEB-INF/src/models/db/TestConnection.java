package models.db;

import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        System.out.print(PSQLConnection.rootPath);
        try {
            Connection connection = PSQLConnection.getConnection();

            if(connection != null)
                System.out.println("All is ok!");
            
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

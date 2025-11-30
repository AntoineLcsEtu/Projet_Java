package be.lucas.util;

import java.io.File;
import java.sql.*;

public class DBConnection {

    private static final String DB_PATH 
    	= "database\\Club_Velo.accdb";

    public static Connection getConnection() throws SQLException {
        File dbFile = new File(DB_PATH);
        String absolutePath = dbFile.getAbsolutePath();
        String url = "jdbc:ucanaccess://" + absolutePath;

        url += ";memory=false";

        return DriverManager.getConnection(url);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Connexion réussie à la base !");
        } catch (SQLException e) {
            System.err.println("Échec de connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
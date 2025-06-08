package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static DatabaseManager dbManager = DatabaseManager.getInstance();
    private static final String URL = "jdbc:sqlite:"+dbManager.getDatabasePath();
    private static Connection con;


    private DBConnection() {
        try{
            con=DriverManager.getConnection(URL);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        public static Connection getInstance() {
            if(con==null) new DBConnection();
            return con;
        }


}

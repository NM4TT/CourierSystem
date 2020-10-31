package core.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database class with some useful methods to manage a database.
 * @author NM4TT - https://github.com/NM4TT
 */
public final class DataBase {
    
    /**
     * Server url where the database is.
     */
    static final String SERVER_URL = "localhost:3360";
    
    /**
     * Database name.
     */
    static final String DB_NAME = "datasystem";         
    
    /**
     * This constant contains the remote database host.
     */
    static final String HOST = "jdbc:mysql://" + SERVER_URL + "/" + DB_NAME;
    
    /**
     * Admin username of database.
     */
    static final String USER = "root";
    
    /**
     * Admin password.
     */
    static final String PASSWORD = "28032001";
    
    /**
     * This method is used to create the main connection to the remote database.
     * @return Connection object.
     * @throws SQLException 
     */
    public static Connection connect(){
        Connection cn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            cn = DriverManager.getConnection(HOST,USER,PASSWORD);
            
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error in DB class: " + e.getMessage());
        }
        
        return cn;        
    }
    
    /**
     * This method is used to close the statement and connection objects.
     * @param st the statement obj.
     * @param cn the connection obj.
     * @throws SQLException 
     */    
    public static void close(Statement st, Connection cn) throws SQLException, NullPointerException{
        st.close();
        cn.close();        
    }
    
    /**
     * This method is used to close the resultset, statement and the connection objects.
     * @param cn the connection obj.
     * @param st the statement obj.
     * @param rs the resultset obj.
     * @throws SQLException
     */    
    public static void close(ResultSet rs, Statement st, Connection cn) throws SQLException, NullPointerException{
        rs.close();
        st.close();
        cn.close();        
    }
    
    
    /**
     * This method is used to clean the database.
     * @param table, this is the table you want to truncate.
     */    
    public static void truncate(String table){
   
        try{ 
            Connection con = connect();
            Statement sta = con.createStatement();
            sta.executeUpdate("TRUNCATE TABLE " + table);
            
            close(sta, con);
            
        } catch (SQLException e){
            System.err.println("Error in truncate: " + e.getMessage());
        }        
    }
       
}

package core.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

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
     * @return the main connection of the <b>database</b>
     * @throws NullPointerException if connection not established.
     */
    public static Connection connect() throws NullPointerException{
        Connection cn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            cn = DriverManager.getConnection(HOST,USER,PASSWORD);
            
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR CREATING CONNECTION TO DATABASE", JOptionPane.ERROR_MESSAGE);            
        }
        
        return cn;  
    }
    
    /**
     * This method shall only be used for static SQL operations.
     * <p> It can only be used by a programmer and its use is going to be
     * locked with admin password.</p>
     * @param operation sql operation to be executed.
     * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
     */
    public static boolean customOperation(String operation){
        boolean taskDone = false;
        
        try {
            Connection cn = DataBase.connect();
            Statement st = cn.createStatement();
            st.executeUpdate(operation);
            
            DataBase.close(st, cn);
            
            taskDone = true;
        } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR EXECUTING CUSTOM OPERATION", JOptionPane.ERROR_MESSAGE);            
        }
        
        return taskDone;
    }
    
    /**
     * This method is used to close the statement and connection objects.
     * @param cn the <tt>connection</tt> object
     * @param st the <tt>statement</tt> object
     * @throws SQLException 
     */    
    public static void close(Statement st, Connection cn) throws SQLException{
        st.close();
        cn.close();        
    }
    
    /**
     * This method is used to close the statement and connection objects.
     * @param cn the <tt>connection</tt> object
     * @param pst the <tt>statement</tt> object
     * @throws SQLException 
     */    
    public static void close(PreparedStatement pst, Connection cn) throws SQLException{
        pst.close();
        cn.close();        
    }    

    /**
     * This method is used to close the resultset, statement and the connection objects currently in use.
     * @param cn the <tt>connection</tt> object
     * @param pst the <tt>prepared statement</tt> object
     * @param rs the <tt>resultset</tt> object
     * @throws SQLException
     */    
    public static void close(ResultSet rs, PreparedStatement pst, Connection cn) throws SQLException{
        rs.close();
        pst.close();
        cn.close();        
    }
       
}

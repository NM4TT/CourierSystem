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
    static final String SERVER_URL = "localhost:3306";
    
    /**
     * Database name.
     */
    static final String DB_NAME = "datasystem";         
    
    /**
     * SSL boolean for host. Does it include SSL certificate?
     */
    static final boolean INCLUDE_SSL = false;
    
    /**
     * This constant contains the remote database host.
     */
    static final String HOST = "jdbc:mysql://" + SERVER_URL + "/" + DB_NAME + "?useSSL="+ INCLUDE_SSL;
    
    /**
     * Admin username of database.
     */
    static final String USER = "root";
    
    /**
     * Admin password.
     */
    static final String PASSWORD = "28032001";
    
    /**
     * This method is used to create a connection of the remote database.
     * @return the <tt>connection</tt> to the database.
     */
    public static Connection connect(){
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            return DriverManager.getConnection(HOST,USER,PASSWORD);
            
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR CREATING CONNECTION TO DATABASE", JOptionPane.ERROR_MESSAGE);                        
            return null;
        }
      
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
        Connection cn = DataBase.connect(); 
        Statement st = null;
        
        try {
            
            if (cn != null) {
                st = cn.createStatement();
                st.executeUpdate(operation);
                taskDone = true;
            }       
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR EXECUTING CUSTOM OPERATION", JOptionPane.ERROR_MESSAGE);            
        } catch (NullPointerException e) {
            
        } finally {
          DataBase.close(st, cn);
        }
        
        return taskDone;
    }
    
    /**
     * This method is used to close the statement and connection objects.
     * @param cn the <tt>connection</tt> object
     * @param st the <tt>statement</tt> object
     */    
    public static void close(Statement st, Connection cn){
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR CLOSING STATEMENT", JOptionPane.ERROR_MESSAGE);                
            }
        }
        if (cn != null) {
            try {
                cn.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR CLOSING CONNECTION", JOptionPane.ERROR_MESSAGE);                
            }
        }      
    }
    
    /**
     * This method is used to close the statement and connection objects.
     * @param cn the <tt>connection</tt> object
     * @param pst the <tt>statement</tt> object
     */    
    public static void close(PreparedStatement pst, Connection cn){
        if (pst != null) {
            try {
                pst.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR CLOSING PREPARED STATEMENT", JOptionPane.ERROR_MESSAGE);                
            }
        }
        if (cn != null) {
            try {
                cn.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR CLOSING CONNECTION", JOptionPane.ERROR_MESSAGE);                
            }
        }       
    }    

    /**
     * This method is used to close the resultset, statement and the connection objects currently in use.
     * @param cn the <tt>connection</tt> object
     * @param pst the <tt>prepared statement</tt> object
     * @param rs the <tt>resultset</tt> object
     */    
    public static void close(ResultSet rs, PreparedStatement pst, Connection cn){
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR CLOSING RESULTSET", JOptionPane.ERROR_MESSAGE);                
            }
        }
        if (pst != null) {
            try {
                pst.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR CLOSING PREPARED STATEMENT", JOptionPane.ERROR_MESSAGE);                
            }
        }
        if (cn != null) {
            try {
                cn.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR CLOSING CONNECTION", JOptionPane.ERROR_MESSAGE);                
            }
        }        
    }
       
}

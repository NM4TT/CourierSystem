/*
 * Copyright 2020 NM4TT - https://github.com/NM4TT.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package core.logic;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import core.db.*;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;

/**
 * Customer template.
 * @author NM4TT - https://github.com/NM4TT
 */
public class Customer extends Person{
    
    /**
     * Database table name of customers.
     */
    static final String TABLE = "customers";
  
    /**
     * ID refers to the specific code given to a customer for recognizing his/her orders.
     */
    private String ID;
    
    /**
     * Default constructor that sets all attributes to null.
     */
    public Customer(){
        this.ID = null;
        this.name = null;
        this.lastname = null;
        this.email =  null;
        this.celphone = null;
    }
      
    /**
     * Constructor with all the class attributes.
     * @param id
     * @param name
     * @param lastname
     * @param email
     * @param celphone 
     */
    public Customer(String id, String name, String lastname, String email, String celphone){
       this.ID = id;
       this.name = name;
       this.lastname = lastname;
       this.email = email;
       this.celphone = celphone;
    }
    
    @Override
    public boolean addToDatabase(){
        boolean taskDone = false;
        Connection cn = DataBase.connect(); 
        PreparedStatement pst = null;
            
            try {


                if (cn != null) {
                    pst = cn.prepareStatement("INSERT INTO " + TABLE + " VALUES (?,?,?,?,?)");

                    pst.setString(1, this.getID());
                    pst.setString(2, this.getName());
                    pst.setString(3, this.getLastname());
                    pst.setString(4, this.getEmail());
                    pst.setString(5, this.getCelphone());

                    pst.executeUpdate();
                    taskDone = true;
                }
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR ADDING CUSTOMER", JOptionPane.ERROR_MESSAGE);
            } finally {
                DataBase.close(pst, cn);
            }
        return taskDone;    
    }
    
    /**
     * Method to update a person in database.
     * <p>Once the customer is updated, the newClient instance is cleaned.
     * @param newClient
     * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
     */
    public boolean update(Customer newClient){
        boolean taskDone = false;
        Connection cn = DataBase.connect(); 
        PreparedStatement pst = null;      
               
            try {
                    if (cn != null) {
                        pst = cn.prepareStatement("UPDATE " + TABLE + " SET Client_ID = ?, Client_Name = ?, Client_LastName = ?, Client_Email = ?, Client_Celphone = ? WHERE Client_ID = ?");             
                        pst.setString(1, newClient.getID());
                        pst.setString(2, newClient.getName());
                        pst.setString(3, newClient.getLastname());
                        pst.setString(4, newClient.getEmail());
                        pst.setString(5, newClient.getCelphone());
                        pst.setString(6, this.getID());

                        pst.executeUpdate();

                        taskDone = true;
                        newClient.cleanData();
                    }
                    
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR UPDATING CUSTOMER", JOptionPane.ERROR_MESSAGE);
            } finally {
               DataBase.close(pst, cn); 
            }
        return taskDone;           
    }
    
    @Override
    public boolean deleteFromDatabase(){
        boolean taskDone = false;
        Connection cn = DataBase.connect(); 
        PreparedStatement pst = null;
        
            try {
                if (cn != null) {
                    pst = cn.prepareStatement("DELETE FROM " + TABLE + " WHERE Client_ID = ?");
                
                    pst.setString(1, this.getID());

                    pst.executeUpdate();
                    taskDone = true;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR DELETING CUSTOMER", JOptionPane.ERROR_MESSAGE);
            } finally {
                DataBase.close(pst, cn);
            }
        return taskDone;           
    }
    
    /**
     * Method to search a customer in database.
     * @param customerID
     * @return Customer
     * @throws NullPointerException
     */
    public static Customer searchOnDatabase(String customerID) throws NullPointerException{
        Customer customer = new Customer();
        Connection cn = DataBase.connect(); 
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
             if (cn != null) {
                pst = cn.prepareStatement("SELECT * FROM " + TABLE + " WHERE Client_ID = ?");
                pst.setString(1, customerID);
                rs = pst.executeQuery();

                if(rs.next()) {                
                    customer.setID(customerID);
                    customer.setName(rs.getString("Client_Name"));
                    customer.setLastname(rs.getString("Client_LastName"));
                    customer.setEmail(rs.getString("Client_Email"));
                    customer.setCelphone(rs.getString("Client_Celphone"));
                }                
            }
 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR SEARCHING CUSTOMER", JOptionPane.ERROR_MESSAGE);
        } finally {
            DataBase.close(rs, pst, cn);
        }
        
        if (customer.getID() != null) {
            return customer;
        } else {
            return null;
        }
    }
    
    
    
    
    @Override
    public boolean sendEmail(String title, String message) {
        return false;
    }    
    
    @Override
    public void cleanData() {
        this.setCelphone(null);
        this.setEmail(null);
        this.setLastname(null);
        this.setName(null);
        this.setID(null);
    }    

    /**
     * @return the ID
     */
    public String getID() {
        return ID;
    }

    /**
     * @param ID the ID to set
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * @param lastname the lastname to set
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the celphone
     */
    public String getCelphone() {
        return celphone;
    }

    /**
     * @param celphone the celphone to set
     */
    public void setCelphone(String celphone) {
        this.celphone = celphone;
    }    
    
}

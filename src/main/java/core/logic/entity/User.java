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
package core.logic.entity;

import core.db.DataBase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * User (Employee) template.
 * @author NM4TT - https://github.com/NM4TT
 */
public class User extends Person{
    
    private String ID;
    private String username;
    private String password;
    /**
     * This is the employee rol, an int value of 0 means <tt>none rol</tt> status.
     */
    private int rol;

    /**
     * Default constructor that set attributes to null, but rol sets to <tt>none rol</tt>.
     */
    public User(){
        this.ID = null;
        this.name = null;
        this.lastname = null;
        this.username = null;
        this.email = null;
        this.celphone = null;
        this.password = null;
        this.rol = 0; //Zero is a none rol status.
    }
    
    /**
     * Database table name of customers.
     */
    static final String TABLE = "employees";
    
    /**
     * Constructor with all the class attributes.
     * @param id
     * @param name
     * @param lastname
     * @param email
     * @param celphone
     * @param username
     * @param password
     * @param rol
     */
    public User(String id, String name, String lastname, String email, String celphone, String username, String password, int rol){
        this.ID = id;
        this.name = name;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.rol = rol;
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
                    pst = cn.prepareStatement("INSERT INTO " + TABLE + " VALUES (?,?,?,?,?,?,?,?)");

                    pst.setString(1, this.getID());
                    pst.setString(2, this.getName());
                    pst.setString(3, this.getLastname());
                    pst.setString(4, this.getEmail());
                    pst.setString(5, this.getCelphone());
                    pst.setString(6, this.getUsername());
                    pst.setString(7, this.getPassword());
                    pst.setInt(8, this.getRol());

                    pst.executeUpdate();
                    taskDone = true;
                }
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR ADDING USER", JOptionPane.ERROR_MESSAGE);
            } finally {
                DataBase.close(pst, cn);
            }
        return taskDone; 
    }
    
    /**
     * Method to update a user in database.
     * <p>Once the user is updated, the newUser instance is cleaned.
     * @param newUser is the User instance with new data.
     * @return taskDone true or false.
     */    
    public boolean update(User newUser){
        boolean taskDone = false;
        Connection cn = DataBase.connect(); 
        PreparedStatement pst = null;      
               
            try {
                    if (cn != null) {
                        pst = cn.prepareStatement("UPDATE " + TABLE + " SET Employee_ID = ?, Employee_Name = ?, Employee_LastName = ?, Employee_Email = ?, Employee_Celphone = ?, Employee_User = ?, Employee_Password = ?, Employee_Position = ? WHERE Employee_ID = ?");             
                        pst.setString(1, newUser.getID());
                        pst.setString(2, newUser.getName());
                        pst.setString(3, newUser.getLastname());
                        pst.setString(4, newUser.getEmail());
                        pst.setString(5, newUser.getCelphone());
                        pst.setString(6, newUser.getUsername());
                        pst.setString(7, newUser.getPassword());
                        pst.setInt(8, newUser.getRol());
                        pst.setString(9, this.getID());

                        pst.executeUpdate();

                        taskDone = true;
                        newUser.cleanData();
                    }
                    
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR UPDATING USER", JOptionPane.ERROR_MESSAGE);
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
                    pst = cn.prepareStatement("DELETE FROM " + TABLE + " WHERE Employee_ID = ?");
                
                    pst.setString(1, this.getID());

                    pst.executeUpdate();
                    taskDone = true;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR DELETING USER", JOptionPane.ERROR_MESSAGE);
            } finally {
                DataBase.close(pst, cn);
            }
        return taskDone;   
    }
    
    /**
     * Method to search an user in database.
     * @param userID
     * @return User
     * @throws NullPointerException
     */
    public static User searchOnDatabase(String userID) throws NullPointerException{
        User user = new User();
        Connection cn = DataBase.connect(); 
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
             if (cn != null) {
                pst = cn.prepareStatement("SELECT * FROM " + TABLE + " WHERE Employee_ID = ?");
                pst.setString(1, userID);
                rs = pst.executeQuery();

                if(rs.next()) {                
                    user.setID(userID);
                    user.setName(rs.getString("Employee_Name"));
                    user.setLastname(rs.getString("Employee_LastName"));
                    user.setEmail(rs.getString("Employee_Email"));
                    user.setCelphone(rs.getString("Employee_Celphone"));
                    user.setUsername(rs.getString("Employee_User"));
                    user.setPassword(rs.getString("Employee_Password"));
                    user.setRol(rs.getInt("Employee_Position"));
                }                
            }
 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR SEARCHING USER", JOptionPane.ERROR_MESSAGE);
        } finally {
            DataBase.close(rs, pst, cn);
        }
        
        if (user.getID() != null) {
            return user;
        } else {
            return null;
        }
    }

    /**
     * This method adds custom rols to the DataBase in order to classify users
     * <p>This method <b>can only be executed once</b> to set the rols.
     * <p>Once the rols are created, the rols <tt>array</tt> is cleaned.
     * @param rols is the rol list as an array structure.
     * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
     */
    public static boolean createUserRols(String rols[]){
        boolean taskDone = false;
        Connection cn = DataBase.connect(); 
        PreparedStatement pst = null;
        
        //This is done in order to include a NONE ROL status know as ID 0.
        List<String> ListOfRols = new ArrayList<>();
        ListOfRols.add("NONE");
        ListOfRols.addAll(Arrays.asList(rols));
        
        
        try {
             if (cn != null) {
                pst = cn.prepareStatement("INSERT INTO positions VALUES(?,?)");
                
                 for (int i = 0; i < ListOfRols.size(); i++) {
                     
                     //Clean the rols array.
                     if(i < rols.length){
                         rols[i] = null;
                     }
                     
                     pst.setInt(1, i); //from 1 and so on.
                     pst.setString(2, ListOfRols.get(i));
                     pst.execute();
                 }
                taskDone = true;
                ListOfRols.clear();
            }
 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR CREATING USER ROLS", JOptionPane.ERROR_MESSAGE);
        } finally {
            DataBase.close(pst,cn);
        }
        
        return taskDone;
    }
    
    /**
     * Method to know which ID rol an user has in the database. 
     * <p>This method returns <tt>0</tt> if there is no ID with that name.
     * @param rolName is the rol name to find the specific ID.
     * @return ID of user's rol.
     */
    public static int getRol(String rolName){
        int rol = 0;
        Connection cn = DataBase.connect(); 
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
             if (cn != null) {
                pst = cn.prepareStatement("SELECT ID FROM positions WHERE Position_Name = ?");
                pst.setString(1,rolName);
                rs = pst.executeQuery();
                
                if(rs.next()){
                    rol = rs.getInt("ID");
                }
                
            }
 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR GETTING ROL ID", JOptionPane.ERROR_MESSAGE);
        } finally {
            DataBase.close(rs,pst,cn);
        }        
        return rol;
    }

    /**
     * Method to know which rol name an user has in the database. 
     * <p>This method returns <tt>null</tt> if there is no rol with that ID.
     * @param rolID is the rol ID to find the specific name.
     * @return User rol's name.
     */
    public static String getRol(int rolID){
        String rol = null;
        Connection cn = DataBase.connect(); 
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
             if (cn != null) {
                pst = cn.prepareStatement("SELECT Position_Name FROM positions WHERE ID = ?");
                pst.setInt(1,rolID);
                rs = pst.executeQuery();
                
                if(rs.next()){
                    rol = rs.getString("Position_Name");
                }
                
            }
 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR GETTING ROL NAME", JOptionPane.ERROR_MESSAGE);
        } finally {
            DataBase.close(rs,pst,cn);
        }        
        return rol;
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
        this.setUsername(null);
        this.setPassword(null);
        this.setRol(0);
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the rol
     */
    public int getRol() {
        return rol;
    }

    /**
     * @param rol the rol to set
     */
    public void setRol(int rol) {
        this.rol = rol;
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
    
}

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
package core.logic.segurity;

import core.logic.entity.User;
import java.sql.Connection;
import core.db.DataBase;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;


/**
 *
 * @author NM4TT - https://github.com/NM4TT
 */
public class SystemAssistant {
    
    /**
     * This method is used to sign into the system.
     * <p>If username & password are incorrect or are not registered in the database the program will <b>close</b>.
     * @param username
     * @param password
     * @return User already signed in.
     */
    public static User signIn(String username, String password){
        User user = new User();
        Connection cn = DataBase.connect();
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            if(cn != null){
                pst = cn.prepareStatement("SELECT * FROM users WHERE Employee_User = ? AND Employee_Password = ?");
                pst.setString(1,username);
                pst.setString(2,password);
                rs = pst.executeQuery();
                
                if (rs.next()) {
                    user.setID("Employee_ID");
                    user.setName(rs.getString("Employee_Name"));
                    user.setLastname(rs.getString("Employee_LastName"));
                    user.setEmail(rs.getString("Employee_Email"));
                    user.setCelphone(rs.getString("Employee_Celphone"));
                    user.setUsername(rs.getString(username));
                    user.setPassword(rs.getString(password));
                    user.setRol(rs.getInt("Employee_Position"));                    
                } else {
                    JOptionPane.showMessageDialog(null, "You are not registered in this system.", "Invalid data to sign in", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
                
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR SIGNING IN", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } finally {
            DataBase.close(rs, pst, cn);
        }
        return user;
    }
    
    /**
     * This method checks if an user has admin permissions to do important stuff.
     * @param user
     * @return <tt>true</tt> or <tt>false</tt>
     */
    public static boolean hasPermissions(User user){
        return user.getRol() == 1;
    }
    
    /**
     * This method cleans the instance and closes the program.
     * @param user 
     */
    public static void logOut(User user){
        user.cleanData();
        System.exit(0);
    }
    
}


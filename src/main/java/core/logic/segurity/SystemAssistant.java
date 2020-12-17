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
import core.logic.entity.Person;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
    public static boolean signIn(String username, String password){
        boolean isRegistered = false;
        Connection cn = DataBase.connect();
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        try {
            if(cn != null){
                pst = cn.prepareStatement(
                "SELECT * FROM employees WHERE "
                + "(Employee_Username = ? AND Employee_Password = ?)");
                pst.setString(1,username);
                pst.setString(2,password);
                rs = pst.executeQuery();
                
                if (rs.next()) {
                    isRegistered = true;
                }
                
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR SIGNING IN", JOptionPane.ERROR_MESSAGE);
        } finally {
            DataBase.close(rs, pst, cn);
        }
        return isRegistered;
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
     * This method cleans the user instance.
     * @param user 
     */
    public static void logOut(User user){
        user.cleanData();
    }
    
    /**
     * Sends a gmail message to a list of receivers.
     * <br><br>
     * <b>IMPORTANT:</b> in order to use this method, the transmitter <i>MUST</i> allow
     * non-google third-party apps access to his/her email. To do so, it's needed to:
     * <br>
     * <ol>
     *  <li>Sign in your Gmail account.</li>
     *  <li>Go to <a href = "https://www.google.com/settings/security/lesssecureapps">this page</a></li>
     *  <li>Turn ON the option below</li>
     * </ol>
     * @param transmitter who sent the message
     * @param gmail_password the gmail password of the transmitter
     * @param message the message to be sent
     */
    public static void sendEmail(Person transmitter, String gmail_password, Message message){
        boolean messageSent = false;
        
            Properties property = new Properties(); // property object for gmail session
            property.setProperty("mail.smtp.host", "smtp.gmail.com");
            property.setProperty("mail smtp starttls enable", "true");
            property.put("mail.smtp.starttls.enable", "true");
            property.setProperty("mail.smtp.port", "587");
            property.setProperty("mail smtp auth", "true");
            
            Session session = Session.getDefaultInstance(property);
            
            MimeMessage mail = new MimeMessage (session);
            
        try {
  
            if(!message.getReceivers().isEmpty()){
                
                mail.setFrom(new InternetAddress(transmitter.getEmail()));
                
                java.util.Iterator<Person> receiverList = message.getReceivers().listIterator();
                
                while(receiverList.hasNext()){
                    mail.addRecipient(javax.mail.Message.RecipientType.TO, 
                    new InternetAddress(receiverList.next().getEmail()));
                }
                
                mail.setSubject(message.getSubject());
                
                mail.setText(message.getBody());
                
                try (Transport transportation = session.getTransport("smtp")) {
                    transportation.connect(transmitter.getEmail(), gmail_password);
                    
                    transportation.sendMessage(mail, mail.getRecipients(javax.mail.Message.RecipientType.TO));
                    
                    messageSent = true;
                }
                
            } else {
                JOptionPane.showMessageDialog(null,"There is no destiny to send this message."); 
            }
        } catch (AddressException ex) {
            JOptionPane.showMessageDialog(null, "Wrong email format, please check \n your email and try again.", "Error using your email.", JOptionPane.ERROR_MESSAGE);
        } catch (MessagingException ex) {
            JOptionPane.showMessageDialog(null,"An error ocurred: " + ex.getMessage() + "\n Pleas try again. ");
        }
        
        if (messageSent) {
            JOptionPane.showMessageDialog(null,"Message sent succesfully");
        }
    }
    
    /**
     * This class is a Message template for e-mails messages.
     */
    public static class Message {

        /**
         * Subject of the message.
         */
        private String subject = null;
        /**
         * Whole body of the message.
         */
        private String body = null;
        /**
         * List of receivers.
         */
        private java.util.LinkedList<core.logic.entity.Person> receivers = new java.util.LinkedList<>();
        
        /**
         * Constructor for initializing messages.
         * @param subject
         * @param body
         * @param receivers 
         */
        public Message(String subject, String body, java.util.LinkedList<core.logic.entity.Person> receivers) {
            this.subject = subject;
            this.body = body;
            this.receivers = receivers;
        }
        
        /**
         * Returns the body of the message as String.
         * @return body
         */
        public String getBody(){
            return this.body;
        }
        
        /**
         * Returns the subject of the message as String.
         * @return subject
         */
        public String getSubject(){
            return this.subject;
        }
        
        /**
         * Returns the list of receivers of this message.
         * @return 
         */
        public java.util.LinkedList<Person> getReceivers(){
            return this.receivers;
        }
        
        /**
         * Cleans a message instance.
         */
        public void cleanData(){
            this.body = null;
            this.subject = null;
            this.receivers = null;
        }
    }
    
}


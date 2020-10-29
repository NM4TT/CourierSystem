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

/**
 * User (Employee) template.
 * @author NM4TT - https://github.com/NM4TT
 */
public class User extends Person{
    
    private String username;
    private String password;
    /**
     * This is the employee rol, for instance: Manager.
     */
    private String rol;

    /**
     * Default constructor that set attributes to null, but rol sets to "Regular".
     */
    public User(){
        this.name = null;
        this.lastname = null;
        this.username = null;
        this.email = null;
        this.celphone = null;
        this.password = null;
        this.rol = "Regular";
    }
    
    /**
     * Constructor with all the class attributes.
     * @param name
     * @param lastname
     * @param email
     * @param celphone
     * @param username
     * @param password
     * @param rol
     */
    public User(String name, String lastname, String email, String celphone, String username, String password, String rol){
        this.name = name;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.email = email;
        this.celphone = celphone;
    }
    
    @Override
    public void addToDatabase(){
        
    }
    
    @Override
    public void update(Person newUser){
        
    }
    
    @Override
    public void deleteFromDatabase(){
        
    }
    
    /**
     * Method to search an user in database.
     * @param username
     * @return User
     */
    public static User searchOnDatabase(String username){
        User user = new User();
        
        return user;
    }    
    
    
    
    
    
    
    
    @Override
    public void sendEmail(Person entity, String message) {
        
    }    
    
    @Override
    public void clean_Stored_Data(Person entity) {
        this.setCelphone(null);
        this.setEmail(null);
        this.setLastname(null);
        this.setName(null);
        this.setUsername(null);
        this.setPassword(null);
        this.setRol(null);
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
    public String getRol() {
        return rol;
    }

    /**
     * @param rol the rol to set
     */
    public void setRol(String rol) {
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
    
}

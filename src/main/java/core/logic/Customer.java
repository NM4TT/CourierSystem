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
 * Customer template.
 * @author NM4TT - https://github.com/NM4TT
 */
public class Customer extends Person{
  
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
    public void addToDatabase(){
        
    }
    
    @Override
    public void update(Person newClient){
        
    }
    
    @Override
    public void deleteFromDatabase(){
        
    }
    
    /**
     * Method to search a customer in database.
     * @param customerID
     * @return Customer
     */
    public static Customer searchOnDatabase(String customerID){
        Customer customer = new Customer();
        
        return customer;
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

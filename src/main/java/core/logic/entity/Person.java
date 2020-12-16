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

/**
 * Abstract class for a Person concept.
 * @author NM4TT - https://github.com/NM4TT
 */
public abstract class Person {
    
    private String name = null;
    private String lastname = null;
    private String email = null;
    private String celphone = null;
    
    
    /**
     * This method clears data stored in a Person instance. 
     */
    public abstract void cleanData();
     
    /**
     * Method to add a person to database.
     */
    public abstract void addToDatabase();
    
    /**
     * Method to delete a person from database.
     */
    public abstract void deleteFromDatabase();    

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

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
 * Abstract class for a Person concept.
 * @author NM4TT - https://github.com/NM4TT
 */
public abstract class Person {
    
    String name;
    String lastname;
    String email;
    String celphone;
    
    
    /**
     * This method clears data stored in a Person instance. 
     */
    public abstract void cleanData();
    
    /**
     * This method sends an email to a person.
     * @param title
     * @param message 
     * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
     */
    public abstract boolean sendEmail(String title, String message);
     
    /**
     * Method to add a person to database.
     * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
     */
    public abstract boolean addToDatabase();
    
    /**
     * Method to delete a person from database.
     * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
     */
    public abstract boolean deleteFromDatabase();    
    
    
}

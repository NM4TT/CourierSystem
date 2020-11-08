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
import java.util.Stack;
import javax.swing.JOptionPane;

/**
 * Class for dealing with client orders.
 * @author NM4TT - https://github.com/NM4TT
 */
public class OrderList {
    
    /**
     * This String refers to the table name in the database for these instances.
     */
    static final String TABLE = "orders";
    
    /**
     * Order ID.
     */
    private String ID;

    /**
     * Date of order. Format <tt>YYYY-MM-DD</tt>
     */
    private String date;
    
    /**
     * Enum for order status information.
     */
    public static enum Status {

        /**
         * <b>Undefined</b> status.
         */
        UNDEFINED(0),
        /**
         * Order <b>shipped</b> status.
         */
        SHIPPED(1),
        /**
         * Order <b>in transit</b> status.
         */
        IN_TRANSIT(2),
        /**
         * Order <b>arrived</b> status.
         */
        ARRIVED(3);
        
        private int value;
        
        /**
         * This String refers to the table name in the database for order status.
         */
        static final String TABLE = "delivery_status";        
        
        /**
         * Constructor for status enum.
         * @param value 
         */
        Status(int value){
            this.value = value;
        }
        
        /**
         * This returns the constant value of the enum.
         * @return value as <tt>int</tt> 
         */
        int getValue(){
            return value;
        }
        /**
         * This returns the constant name as String.
         * @param value its the constant value as int.
         * @return 
         */
        String getValue(int value){
            return Status.values()[value].toString();
        }
    
    }
    
    /**
     * Status of orders. A <tt>zero</tt> value means a <b>UNDEFINED</b> status.
     */
    private int status;
    
    /**
     * Customer object with client information.
     */
    private Customer client;
    
    /**
     * Constructor that initializes all attributes to default value.
     */
    public OrderList(){
        this.ID = null;
        this.date = null;
        this.status = 0;
        this.client = new Customer();
    }

    /**
     * Constructor to initialize the instance with data except the packetList.
     * @param id
     * @param date
     * @param status
     * @param client 
     */
    public OrderList(String id, String date, String status, Customer client ){
        this.ID = id;
        this.date = date;
        this.status = Status.valueOf(status.toUpperCase()).getValue();
        this.client = client;
    }    
    
    /**
     * This method adds a new order to the database.
     * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
     */
    public boolean addToDataBase(){
        boolean taskDone = false;
        Connection cn = DataBase.connect();
        PreparedStatement pst = null;
        
            try {
                if(cn != null){
                    pst = cn.prepareStatement("INSERT INTO " + TABLE + " VALUES (?,?,?,?)");
                    pst.setString(1, this.getID());
                    pst.setString(2, this.getDate());
                    pst.setInt(3, this.getStatus());
                    pst.setString(4, this.getClient().getID());
                    
                    pst.execute();
                    taskDone = true;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR ADDING ORDER", JOptionPane.ERROR_MESSAGE);
            } finally {
                DataBase.close(pst, cn);
            }
        
        return taskDone;
    }
    
    /**
     * This method is used to assign a packet to this order.
     * <p>The packet once it's added to the order, its instance is cleaned.
     * @param packet
     * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
     */
    public boolean addPacket(Packet packet){
        boolean taskDone = false;
        
        if (packet.getID() != null) {
            Connection cn = DataBase.connect();
            PreparedStatement pst = null;
        
            try {
                if(cn != null){
                    pst = cn.prepareStatement("UPDATE " + Packet.TABLE + " SET Order_ID = ? WHERE Packet_ID = ?");
                    pst.setString(1,this.getID());
                    pst.setString(2,packet.getID());
                    
                    pst.execute();
                    taskDone = true;
                    packet.cleanData();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR ADDING PACKET TO ORDER", JOptionPane.ERROR_MESSAGE);
            } finally {
                DataBase.close(pst, cn);
            }
        }
        
        return taskDone;
    }
    
    /**
     * This method is used to update an order.
     * <p>Once the order is updated, the order instance to update is cleaned.
     * @param newOrder is the updated order.
     * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
     */
    public boolean updateOrder(OrderList newOrder){
        boolean taskDone = false;
        Connection cn = DataBase.connect();
        PreparedStatement pst = null;
        
            try {
                if(cn != null){
                    pst = cn.prepareStatement("UPDATE " + TABLE + " SET Order_ID = ?, Order_Date = ?, Order_Status = ?, Client_ID = ? WHERE Order_ID = ?");
                    pst.setString(1,newOrder.getID());
                    pst.setString(2,newOrder.getDate());
                    pst.setInt(3,newOrder.getStatus());
                    pst.setString(4,newOrder.getClient().getID());
                    pst.setString(5,this.getID());
                    
                    pst.execute();
                    taskDone = true;
                    newOrder.cleanData();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR UPDATING ORDER", JOptionPane.ERROR_MESSAGE);
            } finally {
                DataBase.close(pst, cn);
            }
        
        return taskDone;
    }
    
    /**
     * This method is used only to update the order status.
     * @param newStatus status name.
     * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
     */
    public boolean updateOrderStatus(String newStatus){
        boolean taskDone = false;
        Connection cn = DataBase.connect();
        PreparedStatement pst = null;
        
            try{
                if (cn != null) {
                    pst = cn.prepareStatement("UPDATE " + TABLE + " SET Order_Status = ? WHERE Order_ID = ?");
                    pst.setInt(1,Status.valueOf(newStatus.toUpperCase()).getValue());
                    pst.setString(2,this.getID());
                    
                    pst.execute();
                    taskDone = true;
                    
                }
            } catch(SQLException e){
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR UPDATING ORDER STATUS", JOptionPane.ERROR_MESSAGE);
            } finally {
                DataBase.close(pst, cn);
            }
        
        return taskDone;
    }
    
    /**
     * This method is used to delete from the database an order.
     * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
     */
    public boolean deleteFromDataBase(){
        boolean taskDone = false;
        Connection cn = DataBase.connect();
        PreparedStatement pst = null;
        
            try {
                if(cn != null){
                    pst = cn.prepareStatement("DELETE FROM " + TABLE + " WHERE Order_ID = ?");
                    pst.setString(1, this.getID());
                    
                    pst.execute();
                    taskDone = true;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR DELETING ORDER", JOptionPane.ERROR_MESSAGE);
            } finally {
                DataBase.close(pst, cn);
            }
            
        return taskDone;
    }
    
    /**
     * This method is used to delete all the packets this order has.
     * <p>It is useful only if the actual orderList is going to be deleted too.
     * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
     */
    public boolean deletePackets(){
        boolean taskDone = false;
        Connection cn = DataBase.connect();
        PreparedStatement pst = null;
        
            try {
                if(cn != null){
                    pst = cn.prepareStatement("DELETE FROM " + Packet.TABLE + " WHERE Order_ID = ?");
                    pst.setString(1, this.getID());
                    
                    pst.execute();
                    taskDone = true;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR DELETING PACKETS", JOptionPane.ERROR_MESSAGE);
            } finally {
                DataBase.close(pst, cn);
            }
        
        return taskDone;
    }
    
    /**
     * This method is used to search an order within the database.
     * @param orderID the ID of the order to search.
     * @return OrderList
     * @throws NullPointerException
     */
    public static OrderList searchOnDatabase(String orderID) throws NullPointerException{
        OrderList list = new OrderList();
        Connection cn = DataBase.connect();
        PreparedStatement pst = null;
        ResultSet rs = null;
        
            try {
                if(cn != null){
                    pst = cn.prepareStatement("SELECT * FROM " + TABLE + " WHERE Order_ID = ?");
                    pst.setString(1,orderID);
                    rs = pst.executeQuery();
                    
                    if(rs.next()){
                        list.setID(orderID);
                        list.setDate(rs.getString("Order_Date"));
                        list.setStatus(rs.getInt("Order_Status"));
                        list.setClient(Customer.searchOnDatabase(rs.getString("Client_ID")));
                    }
                    
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR SEARCHING ORDER", JOptionPane.ERROR_MESSAGE);
            } finally {
                DataBase.close(rs, pst, cn);
            }
        
        if (list.getID() != null) {
            return list;
        } else {
            return null;
        }
    }
    
    /**
     * This method is used to get all the packets of this order as a Stack.
     * @return Stack of packets
     */
    public Stack<Packet> getPacketStack(){
        Stack<Packet> stack = new Stack();
        Connection cn = DataBase.connect();
        PreparedStatement pst = null;
        ResultSet rs = null;
        
            try{
                if (cn != null) {
                    pst = cn.prepareStatement("SELECT * FROM " + Packet.TABLE + " WHERE Order_ID = ?");
                    pst.setString(1, this.getID());
                    rs = pst.executeQuery();
                    
                    
                    while(rs.next()){
                        Packet packet = new Packet(rs.getString("Packet_ID"), rs.getString("Order_ID"), rs.getString("Packet_Date"), rs.getString("Concept"), rs.getDouble("Weight_Lb"), rs.getDouble("Volumetric_Weight"));
                        stack.add(packet);
                    }
                    
                }
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR GETTING ORDER PACKETS", JOptionPane.ERROR_MESSAGE);
            }finally{
                DataBase.close(rs, pst, cn);
            }
        
        if (!stack.empty()) {
            return stack;
        } else {
            return null;
        }
    }
        
    /**
     * This method is used to clean the instance data.
     */
    public void cleanData(){
        this.setID(null);
        this.setDate(null);
        this.setClient(null);
        this.setStatus(0);
    }
    
    /**
     * This class is used to deal with packets stuff.
     */
    public static class Packet {
        
        /**
         * DataBase table of Packets.
         */
        static final String TABLE = "packets";
        /**
         * Packet ID code.
         */
        private String ID;
        /**
         * ID of the order the packet belongs to.
         */
        private String Order_ID;
        /**
         * Date of ordered. format <tt>YYYY-MM-DD</tt>.
         */
        private String date;
        /**
         * Small summary of packet.
         */
        private String concept;
        /**
         * Weight of the packet <b>as Lb</b>
         */
        private double weight;
        /**
         * Volumetric weight of the packet.
         */
        private double volumetricWeight;
        
        /**
         * Constructor to set all attributes to null.
         */
        public Packet(){
            this.ID = null;
            this.Order_ID = null;
            this.date = null;
            this.concept = null;
            this.weight = 0.0;
            this.volumetricWeight = 0.0;
        }

        /**
         * Constructor with all class attributes.
         * @param ID
         * @param orderID
         * @param date
         * @param concept
         * @param weight
         * @param volumetricWeight 
         */
        public Packet(String ID, String orderID, String date, String concept, double weight, double volumetricWeight){
            this.ID = ID;
            this.Order_ID = orderID;
            this.date = date;
            this.concept = concept;
            this.weight = weight;
            this.volumetricWeight = volumetricWeight;
        }
        
        /**
         * Constructor with all class attributes except orderID.
         * @param ID
         * @param date
         * @param concept
         * @param weight
         * @param volumetricWeight 
         */
        public Packet(String ID, String date, String concept, double weight, double volumetricWeight){
            this.ID = ID;
            this.date = date;
            this.concept = concept;
            this.weight = weight;
            this.volumetricWeight = volumetricWeight;
        }        
        
        /**
         * This method is used to register a packet in the database.
         * <b>IMPORTANT:</b>these packets need to be part of an active order.
         * <p>Before adding a packet, be sure there is an order to store it.
         * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
         */
        public boolean registerPacket(){
            boolean taskDone = false;
            Connection cn = DataBase.connect();
            PreparedStatement pst = null;
            
            try {
                if(cn != null){
                    pst = cn.prepareStatement("INSERT INTO " + TABLE + " VALUES (?,?,?,?,?,?)");
                    pst.setString(1, this.getID());
                    pst.setString(2, null);
                    pst.setString(3, this.getDate());
                    pst.setString(4, this.getConcept());
                    pst.setDouble(5, this.getWeight());
                    pst.setDouble(6,this.getVolumetricWeight());
                    
                    pst.execute();
                    taskDone = true;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR ADDING PACKET", JOptionPane.ERROR_MESSAGE);
            } finally {
                DataBase.close(pst, cn);
            }
            
            return taskDone;
        }

        /**
         * This method is used to update a packet within the database.
         * <p>Once the packet is updated, the newPacket instance is cleaned.
         * <b>IMPORTANT:</b>do not modify the orderID unless the order changes its ID too.
         * @param newPacket is the packetUpdate as a <tt>Packet</tt> object
         * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
         */
        public boolean updatePacket(Packet newPacket){
            boolean taskDone = false;
            Connection cn = DataBase.connect();
            PreparedStatement pst = null;
            
                try {
                    if(cn != null){
                        pst = cn.prepareStatement("UPDATE " + TABLE + " SET Packet_ID = ?, Order_ID = ?, Packet_Date = ?, Concept = ?, Weight_Lb = ?, Volumetric_Weight = ? WHERE Packet_ID = ?");
                        pst.setString(1, newPacket.getID());
                        pst.setString(2, newPacket.getOrder_ID());
                        pst.setString(3, newPacket.getDate());
                        pst.setString(4, newPacket.getConcept());
                        pst.setDouble(5, newPacket.getWeight());
                        pst.setDouble(6,newPacket.getVolumetricWeight());
                        pst.setString(7, this.getID());

                        pst.execute();
                        taskDone = true;
                        newPacket.cleanData();
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR UPDATING PACKET TO DB", JOptionPane.ERROR_MESSAGE);
                } finally {
                    DataBase.close(pst, cn);
                }            
            return taskDone;
        }
        
        /**
         * This method is used to delete a packet within the database.
         * <b>IMPORTANT:</b>This action does not deletes the actual order itself...
         * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
         */        
        public boolean deletePacket(){
            boolean taskDone = false;
            Connection cn = DataBase.connect();
            PreparedStatement pst = null;
            
                try {
                    if(cn != null){
                        pst = cn.prepareStatement("DELETE FROM " + TABLE + " WHERE Packet_ID = ?");
                        pst.setString(1, this.getID());

                        pst.execute();
                        taskDone = true;
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR DELETING PACKET", JOptionPane.ERROR_MESSAGE);
                } finally {
                    DataBase.close(pst, cn);
                }            
            return taskDone;
        }
        
        /**
         * This method is used to search a packet stored in the dataBase.
         * @param packetID is needed to search an specific packet.
         * @return <tt>Packet</tt> object with data.
         * @throws NullPointerException
         */
        public static Packet searchOnDataBase(String packetID) throws NullPointerException{
            Packet packet = new Packet();
            Connection cn = DataBase.connect();
            PreparedStatement pst = null;
            ResultSet rs = null;
            
                try {
                    if(cn != null){
                        pst = cn.prepareStatement("SELECT * FROM " + TABLE + " WHERE Packet_ID = ?");
                        pst.setString(1,packetID);
                        rs = pst.executeQuery();
                        
                        if(rs.next()){
                            packet.setID(packetID);
                            packet.setOrder_ID(rs.getString("Order_ID"));
                            packet.setDate(rs.getString("Packet_Date"));
                            packet.setConcept(rs.getString("Concept"));
                            packet.setWeight(rs.getDouble("Weight_Lb"));
                            packet.setVolumetricWeight(rs.getDouble("Volumetric_Weight"));
                        }
                        
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR SEARCHING PACKET", JOptionPane.ERROR_MESSAGE);                    
                } finally {
                    DataBase.close(rs, pst, cn);
                }
            
            if (packet.getID() != null) {
                return packet;
            } else {
                return null;
            }
            
        }
        
        /**
         * This method clean Packet instances data.
         */
        public void cleanData(){
            this.setID(null);
            this.setOrder_ID(null);
            this.setDate(null);
            this.setConcept(null);
            this.setWeight(0.0);
            this.setVolumetricWeight(0.0);
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
         * @return the Order_ID
         */
        public String getOrder_ID() {
            return Order_ID;
        }

        /**
         * @param Order_ID the Order_ID to set
         */
        public void setOrder_ID(String Order_ID) {
            this.Order_ID = Order_ID;
        }

        /**
         * @return the date
         */
        public String getDate() {
            return date;
        }

        /**
         * @param date the date to set
         */
        public void setDate(String date) {
            this.date = date;
        }

        /**
         * @return the concept
         */
        public String getConcept() {
            return concept;
        }

        /**
         * @param concept the concept to set
         */
        public void setConcept(String concept) {
            this.concept = concept;
        }

        /**
         * @return the weight
         */
        public double getWeight() {
            return weight;
        }

        /**
         * @param weight the weight to set
         */
        public void setWeight(double weight) {
            this.weight = weight;
        }

        /**
         * @return the volumetricWeight
         */
        public double getVolumetricWeight() {
            return volumetricWeight;
        }

        /**
         * @param volumetricWeight the volumetricWeight to set
         */
        public void setVolumetricWeight(double volumetricWeight) {
            this.volumetricWeight = volumetricWeight;
        }
        
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
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the client
     */
    public Customer getClient() {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(Customer client) {
        this.client = client;
    }
}

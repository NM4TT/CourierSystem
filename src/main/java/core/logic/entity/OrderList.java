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
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.swing.JOptionPane;

/**
 * Class for dealing with client orders.
 * @author NM4TT - https://github.com/NM4TT
 */
public class OrderList {
    
    /**
     * Order ID.
     */
    private String ID;

    /**
     * Date of order. Format <tt>YYYY-MM-DD</tt>
     */
    private String date;
    
    /**
     * Enum for order delivery_status information.
     */
    public static enum Delivery_Status {

        /**
         * <b>Undefined</b> delivery_status.
         */
        UNDEFINED(0),
        /**
         * Order <b>shipped</b> delivery_status.
         */
        SHIPPED(1),
        /**
         * Order <b>in transit</b> delivery_status.
         */
        IN_TRANSIT(2),
        /**
         * Order <b>arrived</b> delivery_status.
         */
        ARRIVED(3);
        
        private int value;     
        
        /**
         * Constructor for delivery_status enum.
         * @param value 
         */
        Delivery_Status(int value){
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
            return Delivery_Status.values()[value].toString();
        }
    }
    
    public static enum Payment_Status {

        /**
         * <b>Not Paid</b> delivery_status.
         */
        NOT_PAID(0),
        /**
         * <b>Paid</b> delivery_status.
         */
        PAID(1);
        
        private int value;  
        
        /**
         * Constructor for delivery_status enum.
         * @param value 
         */
        Payment_Status(int value){
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
            return Payment_Status.values()[value].toString();
        }
    
    }    
    
    /**
     * Delivery_Status of orders. A <tt>zero</tt> value means a <b>UNDEFINED</b> delivery_status.
     */
    private int delivery_status;
    
    /**
     * Payment_Status of orders. A <tt>zero</tt> value means ORDER NOT PAID.
     */
    private int payment_status;
    
    /**
     * Customer object with client information.
     */
    private Customer client;
    
    /**
     * This constant has the tax % for price calculation.
     * <p>The actual TAX is panamanian (%7). The product of this tax times the subtotal
     * will be the <b>TOTAL TO PAY</b>.
     */
    public static final double TAX = 1.07;
    
    /**
     * Constructor that initializes all attributes to default value.
     */
    public OrderList(){
        this.ID = null;
        this.date = null;
        this.delivery_status = 0;
        this.payment_status = 0;
        this.client = new Customer();
    }

    /**
     * Constructor to initialize the instance with data except the packetList.
     * @param id
     * @param date
     * @param delivery_status
     * @param payment_status
     * @param client 
     */
    public OrderList(String id, String date, String delivery_status,String payment_status, Customer client ){
        this.ID = id;
        this.date = date;
        this.delivery_status = Delivery_Status.valueOf(delivery_status.toUpperCase()).getValue();
        this.payment_status = Payment_Status.valueOf(payment_status.toUpperCase()).getValue();
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
                    pst = cn.prepareStatement("INSERT INTO orders VALUES (?,?,?,?,?)");
                    pst.setString(1, this.getID());
                    pst.setString(2, this.getDate());
                    pst.setInt(3, this.getDeliveryStatus());
                    pst.setString(4, this.getClient().getID());
                    pst.setInt(5,this.getPaymentStatus());
                    
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
                    pst = cn.prepareStatement("UPDATE packets SET Order_ID = ? WHERE Packet_ID = ?");
                    pst.setString(1,this.getID());
                    pst.setString(2,packet.getID());
                    
                    pst.execute();
                    taskDone = true;
                    
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
                    pst = cn.prepareStatement("UPDATE orders SET Order_ID = ?, Order_Date = ?, Delivery_Status = ?, Client_ID = ?, Payment_Status = ? WHERE Order_ID = ?");
                    pst.setString(1,newOrder.getID());
                    pst.setString(2,newOrder.getDate());
                    pst.setInt(3,newOrder.getDeliveryStatus());
                    pst.setString(4,newOrder.getClient().getID());
                    pst.setInt(5,newOrder.getPaymentStatus());
                    pst.setString(6,this.getID());
                    
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
     * This method is used only to update the order delivery_status.
     * @param newStatus delivery_status name.
     * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
     */
    public boolean updateDeliveryStatus(String newStatus){
        boolean taskDone = false;
        Connection cn = DataBase.connect();
        PreparedStatement pst = null;
        
            try{
                if (cn != null) {
                    pst = cn.prepareStatement("UPDATE orders SET Delivery_Status = ? WHERE Order_ID = ?");
                    pst.setInt(1,Delivery_Status.valueOf(newStatus.toUpperCase()).getValue());
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
     * This method is used only to update the order payment_status.
     * @param newStatus payment_status name.
     * @return <b>taskDone</b> as <tt>true</tt> or <tt>false</tt>
     */
    public boolean updatePaymentStatus(String newStatus){
        boolean taskDone = false;
        Connection cn = DataBase.connect();
        PreparedStatement pst = null;
        
            try{
                if (cn != null) {
                    pst = cn.prepareStatement("UPDATE orders SET Payment_Status = ? WHERE Order_ID = ?");
                    pst.setInt(1,Payment_Status.valueOf(newStatus.toUpperCase()).getValue());
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
                    pst = cn.prepareStatement("DELETE FROM orders WHERE Order_ID = ?");
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
                    pst = cn.prepareStatement("DELETE FROM packets WHERE Order_ID = ?");
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
                    pst = cn.prepareStatement("SELECT * FROM orders WHERE Order_ID = ?");
                    pst.setString(1,orderID);
                    rs = pst.executeQuery();
                    
                    if(rs.next()){
                        list.setID(orderID);
                        list.setDate(rs.getString("Order_Date"));
                        list.setDeliveryStatus(rs.getInt("Delivery_Status"));
                        list.setClient(Customer.searchOnDatabase(rs.getString("Client_ID")));
                        list.setPaymentStatus(rs.getInt("Payment_Status"));
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
                    pst = cn.prepareStatement("SELECT * FROM packets WHERE Order_ID = ?");
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
     * This method will calculate the cost of an order based on its <b>weight</b>.
     * <p>The function consults to the database the actual lb_cost stored in the system to calculate.
     * @return <tt>double</tt> cost.
     */
    public double getWeightCost(){
        double cost = 0.0;
        double weight = 0.0;
        Connection cn = DataBase.connect();
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        Stack<Packet> packetStack = this.getPacketStack();
        
        while(!packetStack.isEmpty()){
            
            weight = packetStack.pop().getWeight();         
        }
        
        try {
            if(cn != null){
                pst = cn.prepareStatement("SELECT Info_Data FROM system_information WHERE Info_Name = 'lb_cost' ");
                rs = pst.executeQuery();
                
                if(rs.next()){
                    cost = (rs.getString("Info_Data") != null) ? rs.getDouble("Info_Data") : 0;
                }
                
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR GETTING ORDER COST (Weight)", JOptionPane.ERROR_MESSAGE);
        } finally {
            DataBase.close(rs, pst, cn);
        }
        
        //less than 1lb cost as 1lb.
        if (weight <= 1) {
            return Math.round(cost * 100.0) / 100.0;
        } else {
            return Math.round((cost * weight) * 100.0) / 100.0;
        }
    }
    
    /**
     * This method calculates the cost of an order based on its volumetric weight.
     * <p>It iterates the order packets to get the amount of volumetric weight before calculate.
     * <p>If packet's volume is less than the volumetric_limit it returns 0.
     * @return <tt>double</tt> cost
     */
    public double getVolumetricCost(){
        
        Map<String,String> system_Data = new HashMap<>(2); //This map will save the whole system_information table data.
        
        Connection cn = DataBase.connect();
        PreparedStatement pst = null;
        ResultSet rs = null;
        double volumetric_limit, volumetric_cost;
        
        try {
            if(cn != null){
                pst = cn.prepareStatement("SELECT * FROM system_information");
                rs = pst.executeQuery();
                
                while(rs.next()){
                   system_Data.put(rs.getString("Info_Name"), rs.getString("Info_Data"));
                }
                
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR GETTING ORDER COST (Volumetric)", JOptionPane.ERROR_MESSAGE);
        } finally {
            DataBase.close(rs, pst, cn);
        }
        
        volumetric_limit = (system_Data.get("volumetric_limit") != null) ? Double.parseDouble(system_Data.get("volumetric_limit")) : 0;
        volumetric_cost = (system_Data.get("volumetric_cost") != null) ? Double.parseDouble(system_Data.get("volumetric_cost")) : 0;
        system_Data.clear();
        
        Stack<Packet> packetStack = this.getPacketStack();
        double total_volume = 0;
        while (!packetStack.isEmpty()) {            
            total_volume += packetStack.pop().getVolumetricWeight();
        }
        
        //if limited exceeded return 0.
        if (total_volume <= volumetric_limit) {
            return 0.0;
        } else {
            double surplus = total_volume - volumetric_limit;
            return Math.round((surplus * volumetric_cost) * 100.0) / 100.0;
        }
    } 
        
    /**
     * This method is used to clean the instance data.
     */
    public void cleanData(){
        this.setID(null);
        this.setDate(null);
        this.setClient(null);
        this.setDeliveryStatus(0);
    }
    
    /**
     * This class is used to deal with packets stuff.
     */
    public static class Packet {
        
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
                    pst = cn.prepareStatement("INSERT INTO packets VALUES (?,?,?,?,?,?)");
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
                        pst = cn.prepareStatement("UPDATE packets SET Packet_ID = ?, Order_ID = ?, Packet_Date = ?, Concept = ?, Weight_Lb = ?, Volumetric_Weight = ? WHERE Packet_ID = ?");
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
                        pst = cn.prepareStatement("DELETE FROM packets WHERE Packet_ID = ?");
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
                        pst = cn.prepareStatement("SELECT * FROM packets WHERE Packet_ID = ?");
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
         * This method is used to calculate a packet cost based on its <b>weight</b>.
         * <p>The function consults to the database the actual lb_cost stored in the system.
         * @return <tt>double</tt> cost
         */
        public double getWeightCost(){
            double cost = 0.0;
            Connection cn = DataBase.connect();
            PreparedStatement pst = null;
            ResultSet rs = null;
            
            try {
                if(cn != null){
                    pst = cn.prepareStatement("SELECT Info_Data FROM system_information WHERE Info_Name = 'lb_cost' ");
                    rs = pst.executeQuery();

                    if(rs.next()){
                        cost = rs.getDouble("Info_Data");
                    }

                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,e.getMessage(),"ERROR GETTING PACKET COST", JOptionPane.ERROR_MESSAGE);
            } finally {
                DataBase.close(rs, pst, cn);
            }            
            
            return cost * this.getWeight();
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

    /**
     * @return the delivery_status
     */
    public int getDeliveryStatus() {
        return delivery_status;
    }

    /**
     * @param delivery_status the delivery_status to set
     */
    public void setDeliveryStatus(int delivery_status) {
        this.delivery_status = delivery_status;
    }

    /**
     * @return the payment_status
     */
    public int getPaymentStatus() {
        return payment_status;
    }

    /**
     * @param payment_status the payment_status to set
     */
    public void setPaymentStatus(int payment_status) {
        this.payment_status = payment_status;
    }
}

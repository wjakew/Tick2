/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import com.jakubwawak.database.Database;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *Object for storing addresses
 * @author jakub
 */
public class Tick_Address extends Tick_Element{
    /**
     * CREATE TABLE ADDRESS
        (
        address_id INT AUTO_INCREMENT PRIMARY KEY,
        address_city VARCHAR(30),
        address_street VARCHAR (30),
        address_house_number INT,
        address_flat_number INT,
        address_postal VARCHAR(15),
        address_country VARCHAR(30)
        );
     */
    public int address_id;
    public String address_city;
    public String address_street;
    public int address_house_number;
    public int address_flat_number;
    public String address_postal;
    public String address_country;
 
    // main constructor
    public Tick_Address(){
        super("Tick_Address");
        address_id = -1;
        address_city = "";
        address_street = "";
        address_house_number = -1;
        address_flat_number = -1;
        address_postal = "";
        address_country = "";
        super.put_elements(wall_updater());
    }
    // constructor with one given argument
    public Tick_Address(ArrayList<Tick_Brick> to_add){
        super("Tick_Address");
        address_id = to_add.get(0).i_get();
        address_city = to_add.get(1).s_get();
        address_street = to_add.get(2).s_get();
        address_house_number = to_add.get(3).i_get();
        address_flat_number = to_add.get(4).i_get();
        address_postal = to_add.get(5).s_get();
        address_country = to_add.get(6).s_get();
        super.put_elements(wall_updater());
    }
    
    // constructor with ResultSet implementation
    public Tick_Address(ResultSet to_add) throws SQLException{
        super("Tick_Address");
        address_id = to_add.getInt("address_id");
        address_city = to_add.getString("address_city");
        address_street = to_add.getString("address_street");
        address_house_number = to_add.getInt("address_house_number");
        address_flat_number = to_add.getInt("address_flat_number");
        address_postal = to_add.getString("address_postal");
        address_country = to_add.getString("address_country");
        super.put_elements(wall_updater());
    }
    
    /**
     * Tick_Address.wall_updater()
     * @return ArrayList
     * Returns collection of Tick_Brick object
     */
    public ArrayList<Tick_Brick> wall_updater(){
        ArrayList<Tick_Brick> to_ret = new ArrayList<>();
        to_ret.add(new Tick_Brick(address_id));
        to_ret.add(new Tick_Brick(address_city));
        to_ret.add(new Tick_Brick(address_street));
        to_ret.add(new Tick_Brick(address_house_number));
        to_ret.add(new Tick_Brick(address_flat_number));
        to_ret.add(new Tick_Brick(address_postal));
        to_ret.add(new Tick_Brick(address_country));
        super.put_elements(to_ret);
        return to_ret;
    }
    /**
     * Tick_Address.init_CUI()
     * User input interface for CUI
     */
    public void init_CUI(){
        super.inter.interface_print("Adding new address:");
        super.inter.interface_print("City name:");
        address_city = super.inter.interface_get();
        super.inter.interface_print("Street name:");
        address_street = super.inter.interface_get();
        super.inter.interface_print("House number:");
        address_house_number = Integer.parseInt(super.inter.interface_get());
        super.inter.interface_print("Flat number:");
        address_flat_number = Integer.parseInt(super.inter.interface_get());
        super.inter.interface_print("Postal code:");
        address_postal = super.inter.interface_get();
        super.inter.interface_print("Country:");
        address_country = super.inter.interface_get();
        super.put_elements(wall_updater());
    }
    /**
     * Tick_Address.get_lines_to_show()
     * @return ArrayList
     * Returns lines of object content
     */
    public ArrayList<String> get_lines_to_show(){
        /**
         * id: /id number/
         * /address_street/ /address_house_number///address_flat_number/
         * /address_postal/ /address_city/
         * /address_country/
         */
        ArrayList<String> to_ret = new ArrayList<>();
        to_ret.add("id: "+Integer.toString(address_id));
        to_ret.add(address_street+" "+
                Integer.toString(address_house_number)+"/"+Integer.toString(address_flat_number));
        to_ret.add(address_postal+" "+address_city);
        to_ret.add(address_country);
        return to_ret;
    }
    
    /**
     * Tick_Address.simple_show()
     * @return String
     * Returns simple show of major data
     */
    public String simple_show(){
        return Integer.toString(this.address_id)+ ":"+address_street+" "+address_city;
    }
    
    /**
     * Tick_Address.get_query(Database database)
     * @param database
     * @return PreparedStatment
     * @throws SQLException 
     */
    public PreparedStatement get_query(Database database) throws SQLException{
        String query = "INSERT INTO ADDRESS\n" +
                       "(address_city,address_street,address_house_number,address_flat_number,address_postal,address_country)\n" +
                       "VALUES\n" +
                       "(?,?,?,?,?,?);";
        
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setString(1,address_city);
        ppst.setString(2,address_street);
        ppst.setInt(3, address_house_number);
        ppst.setInt(4,address_flat_number);
        ppst.setString(6,address_postal);
        ppst.setString(7,address_country);
    
        return ppst;
    }
}

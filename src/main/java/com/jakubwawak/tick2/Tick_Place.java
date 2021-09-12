/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *Object for storing Place info
 * @author jakub
 */
public class Tick_Place extends Tick_Element{
    /**
     * CREATE TABLE PLACE
        (
        place_id INT AUTO_INCREMENT PRIMARY KEY,
        owner_id INT,
        place_name VARCHAR(30),
        address_id INT,
        CONSTRAINT fk_place FOREIGN KEY (address_id) REFERENCES ADDRESS(address_id),
        CONSTRAINT fk_place2 FOREIGN KEY (owner_id) REFERENCES OWN(owner_id)
        );
     */
    public int place_id;
    public int owner_id;
    public String place_name;
    public int address_id;
    
    // main constructor
    public Tick_Place(){
        super("Tick_Place");
        place_id = -1;
        owner_id = -1;
        place_name = "";
        address_id = 1;
        
        super.put_elements(wall_updater());
    }
    // constructor with data from database
    // optional
    public Tick_Place(ResultSet rs) throws SQLException{
        super("Tick_Place");
        place_id = rs.getInt("place_id");
        owner_id = rs.getInt("owner_id");
        place_name = rs.getString("place_name");
        address_id = rs.getInt("address_id");
        
        super.put_elements(wall_updater());
    }
    
    // constructor with 1 parameter
    public Tick_Place(ArrayList<Tick_Brick> to_add){
        super("Tick_Place");
        place_id = to_add.get(0).i_get();
        owner_id = to_add.get(1).i_get();
        place_name = to_add.get(2).s_get();
        address_id = to_add.get(3).i_get();
        super.put_elements(wall_updater());
    }
    
    
    /**
     * Tick_Place.wall_updater()
     * @return ArrayList
     * Updates Tick_Brick collection
     */
    public ArrayList<Tick_Brick> wall_updater(){
        ArrayList<Tick_Brick> to_ret = new ArrayList<>();
        
        to_ret.add(new Tick_Brick(place_id));
        to_ret.add(new Tick_Brick(owner_id));
        to_ret.add(new Tick_Brick(place_name));
        to_ret.add(new Tick_Brick(address_id));
        return to_ret;
    }
    /**
     * Tick_Place.init_CUI()
     * Prints prompts to enter the place
     */
    public void init_CUI(){
        super.inter.interface_print("Enter the place name: ");
        place_name = super.inter.interface_get();
    }
    /**
     * Tick_Place.get_lines_to_show()
     * @return ArrayList
     * Returns lines of object content
     */
    public ArrayList<String> get_lines_to_show(){
        /**
         * id: /place_id/
         * Place name: /place_name/
         */
        ArrayList<String> to_ret = new ArrayList<>();
        to_ret.add("id: "+Integer.toString(place_id));
        to_ret.add("Place name: "+place_name);
        return to_ret;
    }
    
    /**
     * Tick_Place.simple_view()
     * @return String
     * Function for returing simple data about object
     */
    public String simple_view(){
        return Integer.toString(place_id)+ ": "+place_name;
    }

    
}

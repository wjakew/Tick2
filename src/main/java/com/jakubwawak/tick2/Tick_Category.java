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
 *Object for storing category data.
 * @author jakub
 */
public class Tick_Category extends Tick_Element{
    /**
     * CREATE TABLE CATEGORY
        (
        category_id INT AUTO_INCREMENT PRIMARY KEY,
        owner_id INT,
        category_name VARCHAR(45),
        category_note VARCHAR(100),
        CONSTRAINT fk_category FOREIGN KEY (owner_id) REFERENCES OWN(owner_id)
        );
     */
    public int category_id;
    public int owner_id;
    public String category_name;
    public String category_note;
    
    // main constructor
    public Tick_Category(){
        super("Tick_Category");
        category_id = -1;
        owner_id = -1;
        category_name = "";
        category_note = "";
        super.put_elements(wall_updater());
    }
    
    // costructor with using data from database
    // optional
    public Tick_Category(ResultSet rs) throws SQLException{
        super("Tick_Cateory");
        category_id = rs.getInt("category_id");
        owner_id = rs.getInt("owner_id");
        category_name = rs.getString("category_name");
        category_note = rs.getString("category_note");
        super.put_elements(wall_updater());
    }
    
    // one argument constructor
    public Tick_Category(ArrayList<Tick_Brick> to_add){
        super("Tick_Category");
        category_id = to_add.get(0).i_get();
        owner_id = to_add.get(1).i_get();
        category_name = to_add.get(2).s_get();
        category_note = to_add.get(3).s_get();
        super.put_elements(wall_updater());
    }
    
    /**
     * Tick_Category.wall_updater()
     * @return ArrayList
     * Returns 'brick wall'
     */
    public ArrayList<Tick_Brick> wall_updater(){
        ArrayList<Tick_Brick> to_ret = new ArrayList<>();
        
        to_ret.add(new Tick_Brick(category_id));
        to_ret.add(new Tick_Brick(owner_id));
        to_ret.add(new Tick_Brick(category_name));
        to_ret.add(new Tick_Brick(category_note));
        super.put_elements(to_ret);
        return to_ret;
    }
    /**
     * Tick_Category.init_CUI()
     * Interface for CUI
     */
    public void init_CUI(){
        super.inter.interface_print("Category name:");
        category_name = super.inter.interface_get();
        super.inter.interface_print("Category note:");
        category_note = super.inter.interface_get();
        super.put_elements(wall_updater());
    }
    /**
     * Tick_Category.get_lines_to_show()
     * @return ArrayList
     * Returns lines of object content
     */
    public ArrayList<String> get_lines_to_show(){
        /**
         * id: /category_id/
         * Category name: /category_name/
         * Note:
         * /category_note/
         */
        ArrayList<String> to_ret = new ArrayList<>();
        to_ret.add("id: "+Integer.toString(category_id));
        to_ret.add("Category name: "+category_name);
        to_ret.add("Note:\n"+category_note);
        return to_ret;
    }
    /**
     * Tick_Category.simple_view()
     * @return String
     * Function for returning simple view information about objects
     */
    public String simple_view(){
        return Integer.toString(category_id)+": "+category_name;
    }
    
}

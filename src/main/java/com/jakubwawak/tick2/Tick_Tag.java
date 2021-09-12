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
 *Object for storing tags from database
 * @author jakub
 */
public class Tick_Tag extends Tick_Element{
    
    /**
     * CREATE TABLE TAG
        (
        tag_id INT AUTO_INCREMENT PRIMARY KEY,
        owner_id INT,
        hashtag_table_id INT,
        tag_name VARCHAR(45),
        tag_note VARCHAR(100),
        CONSTRAINT fk_tag FOREIGN KEY (owner_id) REFERENCES OWN(owner_id),
        CONSTRAINT fk_tag2 FOREIGN KEY (hashtag_table_id) REFERENCES HASHTAG_TABLE(hashtag_table_id)
        );
     */
    
    public int tag_id;
    public int owner_id;
    public int hashtag_table_id;
    public String tag_name;
    public String tag_note;
    
    // main constructor
    public Tick_Tag(){
        super("Tick_Tag");
        tag_id = -1;
        owner_id = -1;
        hashtag_table_id = 1;
        tag_name = "";
        tag_note = "";
        super.put_elements(wall_updater());
        
    }
    
    public Tick_Tag(ResultSet to_add) throws SQLException{
        super("Tick_Tag");
        tag_id = to_add.getInt("tag_id");
        owner_id = to_add.getInt("owner_id");
        hashtag_table_id = to_add.getInt("hashtag_table_id");
        tag_name = to_add.getString("tag_name");
        tag_note = to_add.getString("tag_note");
        super.put_elements(wall_updater());
    }
    
    // one argument constructor
    public Tick_Tag(ArrayList<Tick_Brick> to_add){
        super("Tick_Tag");
        tag_id = to_add.get(0).i_get();
        owner_id = to_add.get(1).i_get();
        hashtag_table_id = to_add.get(2).i_get();
        tag_name = to_add.get(3).s_get();
        tag_note = to_add.get(4).s_get();
        super.put_elements(wall_updater());
    }
    
    /**
     * Tick_Tag.wall_updater()
     * @return ArrayList
     * Function for 'making wall'
     */
    public ArrayList<Tick_Brick> wall_updater(){
        ArrayList<Tick_Brick> to_ret = new ArrayList<>();
        
        to_ret.add(new Tick_Brick(tag_id));
        to_ret.add(new Tick_Brick(owner_id));
        to_ret.add(new Tick_Brick(hashtag_table_id));
        to_ret.add(new Tick_Brick(tag_name));
        to_ret.add(new Tick_Brick(tag_note));
        
        super.put_elements(to_ret);
         
        return to_ret;
    }
    
    /**
     * Function for getting glance
     * @return String
     */
    public String get_glance(){
        return Integer.toString(tag_id)+ " : "+tag_name;
    }
    
    /**
     * Tick_Tag.init_CUI()
     * Interface CUI input
     */
    public void init_CUI(){
        super.inter.interface_print("Tag name:");
        tag_name = super.inter.interface_get();
        super.inter.interface_print("Tag note:");
        tag_note = super.inter.interface_get();
        super.put_elements(wall_updater());
    }
    /**
     * Tick_Tag.get_lines_to_show()
     * @return ArrayList
     * Returns lines of object content
     */
    public ArrayList<String> get_lines_to_show(){
        /**
         * id: /tag_id/
         * Tag name: /tag_name/
         * Note:
         * /tag_note/
         */
        ArrayList<String> to_ret = new ArrayList<>();
        to_ret.add("id: "+Integer.toString(tag_id));
        to_ret.add("Tag name: " + tag_name);
        to_ret.add("Note:");
        to_ret.add(tag_note);
        return to_ret;
    }
}

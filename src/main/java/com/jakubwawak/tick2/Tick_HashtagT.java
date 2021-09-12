/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import com.jakubwawak.database.Database;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;


/**
 *Object for storing tables of tags
 * @author jakub
 */
public class Tick_HashtagT extends Tick_Element{
    
    /**
     * CREATE TABLE HASHTAG_TABLE
        (
        hashtag_table_id INT AUTO_INCREMENT PRIMARY KEY,
        owner_id INT,
        hashtag_table_name VARCHAR(45),
        hashtag_table_note VARCHAR(100),
        CONSTRAINT fk_hshtable2 FOREIGN KEY (owner_id) REFERENCES OWN(owner_id)
        );
     */
    public int hashtag_table_id;
    public int owner_id;
    public String hashtag_table_name;
    public String hashtag_table_note;
    
    // main constructor
    public Tick_HashtagT(){
        super("Tick_HashtagT");
        hashtag_table_id = -1;
        owner_id = -1;
        hashtag_table_name = "";
        hashtag_table_note = "";
        super.put_elements(wall_updater());
    }
    // constructor with data from database
    // optional
    public Tick_HashtagT(ResultSet rs) throws SQLException{
        super("Tick_HashtagT");
        hashtag_table_id = rs.getInt("hashtag_table_id");
        owner_id = rs.getInt("owner_id");
        hashtag_table_name = rs.getString("hashtag_table_name");
        hashtag_table_note = rs.getString("hashtag_table_note");
        super.put_elements(wall_updater());
    }
    
    // constructor with one argument
    public Tick_HashtagT(ArrayList<Tick_Brick> to_add){
        super("Tick_HashtagT");
        hashtag_table_id = to_add.get(0).i_get();
        owner_id = to_add.get(1).i_get();
        hashtag_table_name = to_add.get(2).s_get();
        hashtag_table_note = to_add.get(3).s_get();
        super.put_elements(wall_updater());
    }
    
    /**
     * Tick_HashtagT.wall_updater()
     * @return ArrayList
     * Returns 'brick wall'
     */
    public ArrayList<Tick_Brick> wall_updater(){
        ArrayList<Tick_Brick> to_ret = new ArrayList<>();
        to_ret.add(new Tick_Brick(hashtag_table_id));
        to_ret.add(new Tick_Brick(owner_id));
        to_ret.add(new Tick_Brick(hashtag_table_name));
        to_ret.add(new Tick_Brick(hashtag_table_note));
        super.put_elements(to_ret);
        return to_ret;
    }
    /**
     * Tick_HashtagT.init_CUI()
     * Input for CUI
     */
    public void init_CUI(){
        super.inter.interface_print("Hashtag Table Name:");
        hashtag_table_name = super.inter.interface_get();
        super.inter.interface_print("Hashtag Table Note:");
        hashtag_table_note = super.inter.interface_get();
        super.put_elements(wall_updater());
    }
    /**
     * Tick_Hashtag.get_lines_to_show()
     * @return ArrayList
     * Returns lines of object content
     */
    public ArrayList<String> get_lines_to_show(){
        /**
         * id: /hashtag_table_id/
         * Hashtag Table Name: /hashtag_table_name/
         * Hashtag Table Note:
         * /hashtag_table_note/
         */
        ArrayList<String> to_ret = new ArrayList<>();
        to_ret.add("id: "+Integer.toString(hashtag_table_id));
        to_ret.add("Hashtag Table Name: "+hashtag_table_name);
        to_ret.add("Hashtag Table Note:\n"+hashtag_table_note);
        return to_ret;
    }
    
    /**
     * Tick_Hashtag.get_all_tags(Database database)
     * @param database
     * @return ArrayList
     * @throws SQLException
     * Function for getting all tags from current hashtag table
     */
    public ArrayList<Tick_Tag> get_all_tags(Database database) throws SQLException{
        String query = "SELECT * FROM TAG where hashtag_table_id = ?";
        ArrayList<Tick_Tag> data_toRet = new ArrayList<>();
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1,hashtag_table_id);
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            while(rs.next()){
                data_toRet.add(new Tick_Tag(rs));
            }
        }catch(SQLException e){
            return null;
        }
        return data_toRet;
    }
    
    /**
     * Tick_HashtagT.simple_view()
     * @return String
     * Function for returning simple view of the object
     */
    public String simple_view(){
        return Integer.toString(hashtag_table_id)+": "+hashtag_table_name;
    }
    
}

/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import com.jakubwawak.database.Database;
import com.jakubwawak.database.Database_Scene;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *Object for storing and making scenes
 * @author jakub
 */
public class Tick_Scene extends Tick_Element{
    /**
     * CREATE TABLE SCENE
        (
        scene_id INT AUTO_INCREMENT PRIMARY KEY,
        hashtag_table_id INT,
        place_id INT,
        owner_id INT,
        category_id INT,
        scene_name VARCHAR(30),
        scene_note VARCHAR(100),
        CONSTRAINT fk_scene FOREIGN KEY (hashtag_table_id) REFERENCES HASHTAG_TABLE(hashtag_table_id),
        CONSTRAINT fk_scene2 FOREIGN KEY (place_id) REFERENCES PLACE(place_id),
        CONSTRAINT fk_scene3 FOREIGN KEY (owner_id) REFERENCES OWN(owner_id),
        CONSTRAINT fk_scene4 FOREIGN KEY (category_id) REFERENCES CATEGORY(category_id)
        );
    */
    public int scene_id;
    public int hashtag_table_id;
    public int place_id;
    public int owner_id;
    public int category_id;
    public String scene_name;
    public String scene_note;
    
    // main constructor
    public Tick_Scene(){
        super("Tick_Scene");
        scene_id = -1;
        hashtag_table_id = -1;
        place_id = -1;
        owner_id = -1;
        category_id = -1;
        scene_name = "";
        scene_note = "";
        super.put_elements(wall_updater());
    }
    // constructor with one argument
    public Tick_Scene(ArrayList<Tick_Brick> to_add){
        super("Tick_Scene");
        scene_id = to_add.get(0).i_get();
        hashtag_table_id = to_add.get(1).i_get();
        place_id = to_add.get(2).i_get();
        owner_id = to_add.get(3).i_get();
        category_id = to_add.get(4).i_get();
        scene_name = to_add.get(5).s_get();
        scene_note = to_add.get(6).s_get();
        super.put_elements(wall_updater());
    }
    
    // constructor with ResultSet object
    public Tick_Scene(ResultSet to_add) throws SQLException{
        super("Tick_Scene");
        scene_id = to_add.getInt("scene_id");
        hashtag_table_id = to_add.getInt("hashtag_table_id");
        place_id = to_add.getInt("place_id");
        owner_id = to_add.getInt("owner_id");
        category_id = to_add.getInt("category_id");
        scene_name = to_add.getString("scene_name");
        scene_note = to_add.getString("scene_note");
        super.put_elements(wall_updater());
    }
    /**
     * Tick_Scene.wall_updater()
     * @return ArrayList
     * Returns 'brick wall'
     */
    public ArrayList<Tick_Brick> wall_updater(){
        ArrayList<Tick_Brick> to_ret = new ArrayList<>();
        to_ret.add(new Tick_Brick(scene_id));
        to_ret.add(new Tick_Brick(hashtag_table_id));
        to_ret.add(new Tick_Brick(place_id));
        to_ret.add(new Tick_Brick(owner_id));
        to_ret.add(new Tick_Brick(category_id));
        to_ret.add(new Tick_Brick(scene_name));
        to_ret.add(new Tick_Brick(scene_note));
        super.put_elements(to_ret);
        return to_ret;
    }
    /**
     * Tick_Scene.init_CUI()
     * Input for console interface
     */
    public void init_CUI(){
        super.inter.interface_print("Place id: ");
        super.inter.interface_get();
        place_id = super.inter.last_input;
        if (place_id !=-1){
            super.inter.interface_print("Category id: ");
            super.inter.interface_get();
            category_id = super.inter.last_input;
            if ( category_id != -1){
                super.inter.interface_print("Hashtag Table id: ");
                super.inter.interface_get();
                hashtag_table_id = super.inter.last_input;
                if ( hashtag_table_id != -1){
                    super.inter.interface_print("Name of the scene: ");
                    scene_name = super.inter.interface_get();
                    super.inter.interface_print("Note: ");
                    scene_note = super.inter.interface_get();
                    super.put_elements(wall_updater());
                }
            }
        }
    }
    
    /**
     * Tick_Scene.return_tick_from_scene(Database database)
     * @param database
     * @return ArrayList
     * @throws SQLException
     * Function for returning Tick objects with given parameters 
     */
    public ArrayList<Tick_Tick> return_tick_from_scene(Database database) throws SQLException{
        ArrayList<Tick_Tick> tick_data = new ArrayList<>();
        Database_Scene ds = new Database_Scene(database);
        Tick_Scene ts = ds.get_scene_object(scene_id);
        ResultSet rs = database.return_resultset(ts.query_creator());
        
        while( rs.next() ){
            tick_data.add(new Tick_Tick(rs));
        }
        
        return tick_data;
    }
    /**
     * Tick_Scene.stop_CUI()
     * @return boolean
     * Checks if data is okey
     */
    public boolean stop_CUI(){
        return place_id == -1 || category_id == -1 || hashtag_table_id == -1;
    }
    
    /**
     * Tick_Scene.check_integrity()
     * @param database
     * @return boolean
     * @throws SQLException 
     * Check if user input is correct
     */
    public boolean check_integrity(Database database) throws SQLException{
        return ( database.check_if_record_exists(place_id, "place") 
                && database.check_if_record_exists(category_id, "category")
                    && database.check_if_record_exists(hashtag_table_id, "hashtag table"));
    }
    
    /**
     * Tick_Scene.repair()
     * @param database
     * @return boolean
     * @throws SQLException 
     */
    public String repair(Database database) throws SQLException{
        String to_ret = "";
        if (!database.check_if_record_exists(place_id, "place")){
            place_id = 1;
            to_ret = to_ret + "place ";
        }
        if (!database.check_if_record_exists(category_id, "category")){
            category_id = 1;
            to_ret = to_ret + " category ";
        }
        if(!database.check_if_record_exists(hashtag_table_id, "hashtag table")){
            hashtag_table_id = 1;
            to_ret = to_ret + " hashtag_table";
        }
        super.put_elements(wall_updater());
        return to_ret;
    }
    /**
     * Tick_Scene.get_lines_to_show()
     * @return ArrayList
     * Function returns lines to show in Database Viewer
     */
    public ArrayList<String> get_lines_to_show(){
        ArrayList<String> to_ret = new ArrayList<>();
        /**
         * Scene name: /scene name/ ------------------------->/scene_id/
         * Category: /category_id/
         * Hashtag Table: /hashtag_table_id/
         * Place id: /place_id/
         */
        to_ret.add("Scene name: "+scene_name+" ------------------------->"+ Integer.toString(scene_id));
        to_ret.add("Category: "+Integer.toString(category_id));
        to_ret.add("Hashtag Table: "+Integer.toString(hashtag_table_id));
        to_ret.add("Place id: "+Integer.toString(place_id));
        return to_ret;
    }
    
    /**
     * Tick_Scene.query_creator()
     * @return String
     * Returns query to select ticks
     */
    public String query_creator(){
        
        /**
         *      scene_id INT AUTO_INCREMENT PRIMARY KEY,
                hashtag_table_id INT, --> 1 - default
                place_id INT,   --> 1 - default
                owner_id INT,   
                category_id INT,--> 1 - default
                scene_name VARCHAR(30),  
                scene_note VARCHAR(100),
                 
         */
        
        String query = "";
        
        query = " SELECT * from TICK where ";
        
        String copy = query;
        
        // checking hashtags
        if ( hashtag_table_id != 1){
            query = query + "hashtag_table_id = "+Integer.toString(hashtag_table_id) + " ";
        }
        
        // checking places
        
        if ( place_id != 1 ){
            if ( !query.equals(copy) ){
                query = query +"and";
            }
            query = query +" place_id = "+Integer.toString(place_id)+" ";
        }
        
        // checking categories
        if ( category_id != 1){
            if ( !query.equals(copy) ){
                query = query + "and";
            }
            query = query + " category_id = "+Integer.toString(category_id)+" ";
        }
        
        query = query + ";";
        
        return query;
    }
    
    /**
     * Function for showing 
     * @return 
     */
    public String show_glance(){
        return Integer.toString(scene_id)+": "+scene_name;
    }
}

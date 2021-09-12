/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import java.sql.SQLException;
import java.util.ArrayList;
import com.jakubwawak.database.Database;

/**
 *Container for storing serialized data from database
 * @author jakubwawak
 */
public class Container {
    
    ArrayList<Tick_Brick> wall;
    String mode;
    Database database;
    Tick_User logged;
    
    String move = "     ";
    
    /**
     * Main constructor
     * @param list_of_objects
     * @param mode
     * @param database
     * @param logged 
     */
    public Container(ArrayList<Tick_Brick> list_of_objects,String mode,Database database,Tick_User logged){
        wall = list_of_objects;
        this.mode = mode;
        this.database = database;
        this.logged = logged;
    }
    /**
     * Container.copy_list(ArrayList<String> src,ArrayList<String> to_copy)
     * @param src
     * @param to_copy 
     * Function for coping arraylist content
     */
    void copy_list(ArrayList<String> src,ArrayList<String> to_copy){
        for( String line: to_copy){
            src.add(line);
        }
    }
    /**
     * Container.kick_list(ArrayList<String> to_kick)
     * @param to_kick
     * @return ArrayList
     * Returns moved text by 'move' variable
     */
    ArrayList<String> kick_list(ArrayList<String> to_kick){
        ArrayList<String> to_ret = new ArrayList<>();
        for( String line : to_kick){
            to_ret.add(move + line);
        }
        return to_ret;
    }
    /**
     * Container.make_lines()
     * @return ArrayList<String>
     * Return lines
     */
    public ArrayList<String> make_lines() throws SQLException{
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<Tick_Brick> part = new ArrayList<>();
        lines.add("Database_Viewer (powered by Container)");
        if (this.mode.equals("address")){
            /**
             *  address_id INT AUTO_INCREMENT PRIMARY KEY,
                address_city VARCHAR(30),
                address_street VARCHAR (30),
                address_house_number INT,
                address_flat_number INT,
                address_postal VARCHAR(15),
                address_country VARCHAR(30)
             */
            for (Tick_Brick element : wall){    // looping on Tick_Brick objects
                if ( !element.STOP ){
                    part.add(element);
                }
                else{
                    Tick_Address to_add = new Tick_Address(part);
                    copy_list(lines,to_add.get_lines_to_show());
                    part.clear();
                }
            }
        }
        else if (this.mode.equals("category")){
            /**
             *  category_id INT AUTO_INCREMENT PRIMARY KEY,
                owner_id INT,
                category_name VARCHAR(45),
                category_note VARCHAR(100),
             */
            for (Tick_Brick element : wall){    // looping on Tick_Brick objects
                if ( !element.STOP ){
                    part.add(element);
                }
                else{
                    Tick_Category to_add = new Tick_Category(part);
                    copy_list(lines,to_add.get_lines_to_show());
                    part.clear();
                }
            }  
        }
        else if (this.mode.equals("hashtag table")){
            /**
             *  hashtag_table_id INT AUTO_INCREMENT PRIMARY KEY,
                owner_id INT,
                hashtag_table_name VARCHAR(45),
                hashtag_table_note VARCHAR(100),
                * 
             */
            for (Tick_Brick element : wall){    // looping on Tick_Brick objects
                if ( !element.STOP ){
                    part.add(element);
                }
                else{
                    Tick_HashtagT to_add = new Tick_HashtagT (part);
                    copy_list(lines,to_add.get_lines_to_show());
                    part.clear();
                }
            }
        }
        else if (this.mode.equals("note")){
            /**
             *  note_id INT AUTO_INCREMENT PRIMARY KEY,
                owner_id INT,
                note_content VARCHAR(100),
                setting1 VARCHAR(40),
                setting2 VARCHAR(40),
                setting3 VARCHAR(40),
             */
            for (Tick_Brick element : wall){    // looping on Tick_Brick objects
                if ( !element.STOP ){
                    part.add(element);
                }
                else{
                    Tick_Note to_add = new Tick_Note(part);
                    copy_list(lines,to_add.get_lines_to_show());
                    part.clear();
                }
            }
        }
        else if (this.mode.equals("place")){
            /**
             *  place_id INT AUTO_INCREMENT PRIMARY KEY,
                owner_id INT,
                place_name VARCHAR(30),
                address_id INT,
             */
            for (Tick_Brick element : wall){    // looping on Tick_Brick objects
                if ( !element.STOP ){
                    part.add(element);
                }
                else{
                    Tick_Place to_add = new Tick_Place(part);
                    copy_list(lines,to_add.get_lines_to_show());
                    // check if linked
                    if ( check_link_place(to_add) ){
                        String query = "SELECT * FROM ADDRESS WHERE address_id = "+Integer.toString(to_add.address_id)+";";
                        Tick_Address linked = new Tick_Address(database.return_TB_collection(logged, "address",query));
                        lines.add("Place linked to: ");
                        copy_list(lines,kick_list(linked.get_lines_to_show()));
                    }
                    else{
                        lines.add(move+"Place is not linked to address");
                    }
                    part.clear();
                }
            }
        }
        else if (mode.equals("tag")){
            /**
             *  tag_id INT AUTO_INCREMENT PRIMARY KEY,
                owner_id INT,
                hashtag_table_id INT,
                tag_name VARCHAR(45),
                tag_note VARCHAR(100),
             */
            for (Tick_Brick element : wall){    // looping on Tick_Brick objects
                if ( !element.STOP ){
                    part.add(element);
                }
                else{
                    Tick_Tag to_add = new Tick_Tag(part);
                    copy_list(lines,to_add.get_lines_to_show());
                    // every tag is linked to the hashtable, default to standard
                    String query = "SELECT * FROM HASHTAG_TABLE WHERE hashtag_table_id = "+Integer.toString(to_add.hashtag_table_id)+";";
                    Tick_HashtagT linked = new Tick_HashtagT(database.return_TB_collection(logged, "hashtag table", query));
                    lines.add("Linked to: ");
                    copy_list(lines,kick_list(linked.get_lines_to_show()));
                    part.clear();
                }
            }
        }
        else if (mode.equals("scene")){
            /**
             *  scene_id INT AUTO_INCREMENT PRIMARY KEY,
                hashtag_table_id INT,
                place_id INT,
                owner_id INT,
                category_id INT,
                scene_name VARCHAR(30),
                scene_note VARCHAR(100),
             */
            for (Tick_Brick element : wall){    // looping on Tick_Brick objects
                if ( !element.STOP ){
                    part.add(element);
                }
                else{
                    Tick_Scene to_add = new Tick_Scene(part);
                    copy_list(lines,kick_list(to_add.get_lines_to_show()));
                    part.clear();
                }
            }
        }
        else if (mode.equals("tick")){
            /**
             *  tick_id INT AUTO_INCREMENT PRIMARY KEY,
                owner_id INT,
                place_id INT,
                category_id INT,
                note_id INT,
                hashtag_table_id INT,
                tick_done_id INT,
                tick_done_start VARCHAR(60),
                tick_date_end VARCHAR(60),
                tick_name VARCHAR(60),
             */
            for (Tick_Brick element : wall){    // looping on Tick_Brick objects
                if ( !element.STOP ){
                    part.add(element);
                }
                else{
                    Tick_Tick to_add = new Tick_Tick(part);
                    copy_list(lines,kick_list(to_add.get_lines_to_show()));
                    part.clear();
                }
            }
        }
        else if (mode.equals("lists")){
                /**
                *  list_id INT AUTO_INCREMENT PRIMARY KEY,
                   owner_id INT,
                   tick_list_id VARCHAR(100),
                   list_name VARCHAR(50),
                   list_date VARCHAR(50),
                */
            for (Tick_Brick element : wall){    // looping on Tick_Brick objects
                if ( !element.STOP ){
                    part.add(element);
                }
                else{
                    Tick_List to_add = new Tick_List(part);
                    copy_list(lines,kick_list(to_add.get_lines_to_show()));
                    part.clear();
                }
            }
        }
        return lines;
    }
    
    /**
            * Tick_Place - > Tick_Address + 
            * Tick_Tag - > Tick_HashtagT +
    */
    
    boolean check_link_place(Tick_Place to_check){
        return (to_check.address_id != 1);
    }
}

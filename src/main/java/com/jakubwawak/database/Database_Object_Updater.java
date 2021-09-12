/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.tick2.Tick_Brick;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *Object for updating object in java
 * @author jakubwawak
 */
public class Database_Object_Updater {
    
    final String HEADER = "DATABASE OBJECT UPDATER";
    
    public Database database;
    public String mode;
    public ArrayList<Tick_Brick> updated_data;
    
    
    /**
     * Constructor of the object
     * @param mode
     * @param object_id
     * @param database 
     * modes:
     * category
     * tag
     * hashtag table
     * place
     * scene
     */
    public Database_Object_Updater(Database database){
        this.database = database;
    }
    
    /**
     * Function for getting custom query for each object
     * @param mode
     * @return String
     */
    public String update_query(String mode){
        
        String query = "";

        switch(mode){
            
            case "place" :
                /**
                 *  place_id INT AUTO_INCREMENT PRIMARY KEY,
                    owner_id INT,
                    place_name VARCHAR(30),
                    address_id INT,
                 */
                query = "UPDATE PLACE SET owner_id = ?, place_name = ?, address_id = ? where place_id = ?;";
                break;
            
            case "category" :
                /**
                 * category_id INT AUTO_INCREMENT PRIMARY KEY,
                   owner_id INT,
                   category_name VARCHAR(45),
                   category_note VARCHAR(100),
                 */
                query = "UPDATE CATEGORY SET owner_id = ?, category_name = ?,category_note = ? where category_id = ?;";
                break;
                
            case "hashtag table":
                /**
                 *  hashtag_table_id INT AUTO_INCREMENT PRIMARY KEY,
                    owner_id INT,
                    hashtag_table_name VARCHAR(45),
                    hashtag_table_note VARCHAR(100),
                 */
                query = "UPDATE HASHTAG_TABLE SET owner_id = ?, hashtag_table_name = ?, hashtag_table_note = ? where hashtag_table_id = ?;";
                break;
            case "tag":
                /**
                 *  tag_id INT AUTO_INCREMENT PRIMARY KEY,
                    owner_id INT,
                    hashtag_table_id INT,
                    tag_name VARCHAR(45),
                    tag_note VARCHAR(100),
                 */
                query = "UPDATE TAG SET owner_id = ?, hashtag_table_id = ?,tag_name = ?, tag_note = ? where tag_id = ?;";
                break;
            case "scene":
                /**
                 *  scene_id INT AUTO_INCREMENT PRIMARY KEY,
                    hashtag_table_id INT,
                    place_id INT,
                    owner_id INT,
                    category_id INT,
                    scene_name VARCHAR(30),
                    scene_note VARCHAR(100),
                 */
                query = "UPDATE SCENE SET hashtag_table_id = ?, place_id = ?, owner_id = ?, category_id = ?, scene_name = ?,scene_note = ? where scene_id = ?;";
                break;
            default:
                break;
                
        }
        database.log.add("Update query set to: "+query,HEADER);
        return query;
    }
    
    /**
     * Function for counting question marks
     * @param data_to_count
     * @return 
     */
    public int count_variables(String data_to_count){
        int len = data_to_count.length();
        int counter = 0;
        
        for(int i = 0; i < len; i++){
            if ( data_to_count.charAt(i) == '?'){
                counter++;
            }
        }
        database.log.add("Counted parameters : "+Integer.toString(counter),HEADER);
        return counter;
    }
    
    
    /**
     * Function for updating object
     * @param data
     * @param mode
     * @return boolean
     */
    public boolean update_record(ArrayList<Tick_Brick> data,String mode) throws SQLException{
        
        String query = update_query(mode);          // getting query from method
        int counter = count_variables(query);       // getting number of variables
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        // setting index of the object in query
        ppst.setInt(counter,data.get(0).i_get());
        System.out.println(data.toString());
        
        data.remove(0);                             // clearing id object
        counter--;
        for(Tick_Brick tb : data){
            tb.show();
        }
        System.out.println(data.toString());
        database.log.add("Updating record query: "+ppst.toString(),HEADER);
        // setting other objects                               
        for( int i = 0; i < counter;i++){         // looping on '?' indexes
            if ( data.get(i).category == 1){
                ppst.setInt(i+1,data.get(i).i_get());
            }
            else if ( data.get(i).category == 2){
                ppst.setString(i+1,data.get(i).s_get());
            }
            else if ( data.get(i).category == 3){
                ppst.setString(i+1,data.get(i).a_get().toString());
            }
        }
        
        try{
            database.log.add("Updating: "+mode,HEADER);
            database.log.add("Updating query = "+ppst.toString(),HEADER);
            ppst.execute();
            return true;
        }catch(SQLException e){
            database.log.add("Failed to update ("+e.toString()+")",HEADER+" E!!!");
            return false;
        }
    }
    
    
}

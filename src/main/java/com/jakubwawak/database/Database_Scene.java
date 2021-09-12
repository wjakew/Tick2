/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.tick2.Tick_Brick;
import com.jakubwawak.tick2.Tick_Scene;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *Object for maintain scene objects
 * @author jakubwawak
 */
public class Database_Scene {
    final String HEADER = "DATABASE SCENE";
    Database database;
    
    
    /**
     * Main constructor
     * @param database 
     */
    public Database_Scene(Database database){
        this.database = database;
    }
    
    /**
     * Database_Scene.to_add(Tick_Scene to_add)
     * @param to_add
     * @return Boolean
     * @throws SQLException
     * Function for adding scene object 
     */
    public boolean add_scene(Tick_Scene to_add) throws SQLException{
    database.log.add("Adding new scene",HEADER);
    String query = "INSERT INTO SCENE\n"
            + "(hashtag_table_id,place_id,owner_id,category_id,scene_name,scene_note)\n"
            + "VALUES\n"
            + "(?,?,?,?,?,?);";
    PreparedStatement ppst = database.con.prepareStatement(query);

    for ( int i = 1,j=1 ; i < to_add.tick_Element_size ; i++,j++){
        Tick_Brick act = to_add.tick_Element_Elements.get(j);

        if (act.category == 1){
            ppst.setInt(i,act.i_get());
        }
        else if (act.category == 2){
            ppst.setString(i, act.s_get());
        }
    }
    try{
        database.log.add("QUERY: "+ppst.toString(),HEADER);
        ppst.execute();
        return true;
    }catch (Exception e){
        database.log.add("Failed to add scene",HEADER);
        database.log.add("QUERY FAILED: "+e.getMessage(),HEADER+ " E!!!");
        return false;
        }
    }
        
    /**
     * Database_Scene.get_glances()
     * @return ArrayList
     * @throws SQLException
     * Function for getting information
     */
    public ArrayList<String> get_glances() throws SQLException{
        ArrayList<String> data_toRet = new ArrayList<>();
        String query = "SELECT * from SCENE where owner_id = ?";
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1,database.logged.owner_id);
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            while ( rs.next() ){
                Tick_Scene act = get_scene_object(rs.getInt("scene_id"));
                
                if ( act != null ){
                    data_toRet.add(act.show_glance());
                }                    
            }
        
            return data_toRet;
        }catch(SQLException e){
            database.log.add("Failed to get glances ("+e.toString()+")",HEADER+" E!!!");
            return null;
        }
    }
    
    /**
     * Database_Scene.get_scene_object(int scene_id)
     * @param scene_id
     * @return Tick_Scene
     * Function for loading scene object
     */
    public Tick_Scene get_scene_object(int scene_id) throws SQLException{
        String query = "SELECT * FROM SCENE where scene_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1,scene_id);
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            if ( rs.next() ){
                return new Tick_Scene(rs);
            }
            return null;
            
        }catch(SQLException e){
            database.log.add("Failed to get scene object: ("+e.toString()+")",HEADER +" E!!!");
            return null;
        }
    }

}

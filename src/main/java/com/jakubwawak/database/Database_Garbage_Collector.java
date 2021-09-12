/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *Object for deleting data from the database
 * @author jakubwawak
 */
public class Database_Garbage_Collector {
    
    final String version = "v1.0.0";
    final String HEADER = "DATABASE GARBAGE COLLECTOR";
    Database database;
    
    
    /**
     * Main constructor
     * @param database 
     */
    public Database_Garbage_Collector(Database database){
        this.database = database;
        System.out.println("Database Garbage Collector "+version+" is ready.");
    }
    
    /**
     * Database_Garbage_Collector.sec_check(int data_id, String data_name,String table)
     * @param data_id
     * @param data_name
     * @param table
     * @return int
     * @throws SQLException
     * Checks if table has data with given id
     * return codes:
     * 1 - has data
     * 0 - no data
     * -1 - fail
     */
    public int sec_check(int data_id, String data_name, String table) throws SQLException{
        String query = "SELECT * from "+table+" where "+data_name+"= ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1,data_id);
        try{
            ResultSet rs = ppst.executeQuery();
            if (rs.next()){
                System.out.println("sec_check : found data in "+table);
                return 1;
            }
            return 0;
        }catch(SQLException e ){
            database.log.add("Failed to check given id. ("+e.toString()+")",HEADER+" E!!!");
            return -1;
        }
    }
    //--------------------------functions for garbage collection
    /**
     * Function for clearing notes
     * @throws SQLException
     * @return Integer
     * Returns number of deleted items
     */
    public int garbage_notes() throws SQLException{
        int iterator = 0;
        database.log.add("Started garbage_notes..",HEADER);
        int note_index;
        boolean has_it;
        String query = "SELECT * FROM NOTE where owner_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1,database.logged.owner_id);
        try{
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                has_it = false;
                note_index = rs.getInt("note_id");
                // loading tick data from notes
                query = "SELECT note_id FROM TICK where owner_id = ?";
                ppst = database.con.prepareStatement(query);
                ppst.setInt(1,database.logged.owner_id);
                
                ResultSet rs2 = ppst.executeQuery();
                // looping on data from TICK table
                while(rs2.next()){
                    if ( note_index == rs2.getInt("note_id")){
                        has_it = true;
                        break;
                    }
                }
                if ( !has_it ){
                    delete_note(note_index);
                    iterator++;
                    database.log.add("Found note, note_id: "+Integer.toString(note_index)+" DELETED",HEADER);
                }
            }
            
        }catch(SQLException e){
            database.log.add("Failed to garbage notes ( "+e.toString()+")",HEADER+" E!!!");
        }
        return iterator;
    }
    /**
     * Function for clearing archived ticks
     * @return Integer
     * Returns number of deleted elements
     */
    public int garbage_archived_ticks() throws SQLException{
        int iterator = 0;
        String query = "SELECT tick_id from TICK where owner_id = ? and tick_done_id != 1;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1,database.logged.owner_id);
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            // looping on archived ticks
            while ( rs.next() ){
                if ( delete_tick(rs.getInt("tick_id")) ) {
                    iterator++;
                    database.log.add("Found archived tick: "+Integer.toString(rs.getInt("tick_id")),HEADER);
                }
            } 
        }catch(SQLException e){
            database.log.add("Failed to delete archived ticks ( "+e.toString()+" )",HEADER+" E!!!");
        }
        return iterator;
    }
    /**
     * Function for collecting user garbage (like not used notes etc)
     * @parm mode
     * modes:
     * 0 - only notes
     * 1 - all elements
     * 2 - archived ticks
     */
    public void collect_garbage(int mode) throws SQLException{
        System.out.println("Collecting garbage inicialized..");
        if ( mode == 1){
            System.out.println("Searching for: notes, archived ticks");
            int notes_deleted = garbage_notes();
            int ticks_deleted = garbage_archived_ticks();
            System.out.println("Status: ");
            System.out.println("Number of notes deleted: "+Integer.toString(notes_deleted));
            System.out.println("Number of archived ticks deleted: "+Integer.toString(ticks_deleted));
        }
        else if ( mode == 0){
            System.out.println("Searching for: notes");
            int notes_deleted = garbage_notes();
            System.out.println("Status: ");
            System.out.println("Number of notes deleted: "+Integer.toString(notes_deleted));
        }
        else if ( mode == 2 ){
            System.out.println("Searching for: archived ticks");
            int ticks_deleted = garbage_archived_ticks();
            System.out.println("Status: ");
            System.out.println("Number of archived ticks deleted: "+Integer.toString(ticks_deleted));
        }
    }
    //---------------------------end of functions
    /**
     * Database_Garbage_Collector.update_data(int new_id,int lookup_id,String data_name,String table_name)
     * @param new_id
     * @param lookup_id
     * @param data_name
     * @param table_name
     * @return boolean
     * @throws SQLException 
     * Function updates tables
     */
    public boolean update_data(int new_id,int lookup_id,String data_name,String table_name) throws SQLException{
        String query = "UPDATE "+table_name+" SET "+data_name+ " = ? where "+data_name+"=?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1,new_id);
        ppst.setInt(2,lookup_id);
        
        try{
            ppst.execute();
            System.out.println("Updated data in "+table_name);
            return true;
        }catch(SQLException e){
            System.out.println("Update failed: "+e.toString());
            database.log.add("Failed to update data. ("+e.toString()+")",HEADER+" E!!!");
            return false;
        }
    }
    
    /**
     * Database_Garbage_Collector.delete(int object_id,String field_name,String table_name)
     * @param object_id
     * @param field_name
     * @param table_name
     * @return boolean
     * @throws SQLException 
     * Delete object by given id in table name
     * WARNING:
     * Not save to use alone.
     */
    public boolean delete(int object_id,String field_name,String table_name) throws SQLException{
        String query = "DELETE FROM "+table_name+" where "+field_name+" = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1,object_id);
        
        if( table_name.equals("CATEGORY") && object_id == 1){
            database.log.add("Cannot delete default data",HEADER+" W!!!");
            return false;
        }
        else if ( table_name.equals("PLACE") && object_id == 1){
           database.log.add("Cannot delete default data",HEADER+" W!!!");
            return false;
        }
        
        try{
            ppst.execute();
            return true;
        }catch(SQLException e){
            System.out.println("Delete failed ("+e.toString()+")");
            database.log.add("Failed to delete object. ("+e.toString()+")",HEADER+" E!!!");
            database.log.add("Failed query : "+ppst.toString(),HEADER+" E!!!");
            return false;
        }
    }
    
    /**
     * Database_Garbage_Collector.delete_address(int address_id)
     * @param address_id
     * @return boolean
     * @throws SQLException
     * Safely delete addreses
     */
    public boolean delete_address(int address_id) throws SQLException{
        System.out.println("Prepare to delete address object with id: "+Integer.toString(address_id));
        String query_upd = "UPDATE OWN SET address_id = 1 where address_id = ?;";
        
        PreparedStatement ppst = database.con.prepareStatement(query_upd);
        ppst.setInt(1,address_id);
        ppst.execute();
        
        query_upd = "UPDATE PLACE SET address_id = 1 where address_id = ?;";
        ppst = database.con.prepareStatement(query_upd);
        ppst.setInt(1,address_id);
        ppst.execute();
        System.out.println("Safely deleting address...");
        query_upd = "DELETE FROM ADDRESS where address_id = ?;";
        
        ppst = database.con.prepareStatement(query_upd);
        
        ppst.setInt(1,address_id);
        
        try{
            ppst.execute();
            return true;
        }catch(SQLException e){
            System.out.println("Failed: "+e.toString());
            database.log.add("Failed to delete address. ("+e.toString()+")",HEADER+" E!!!");
            return false;
        }
    }
    
    /**
     * Database_Garbage_Collector.delete_place(int place_id)
     * @param place_id
     * @return boolean
     * @throws SQLException 
     * Safely deletes place
     */
    public boolean delete_place(int place_id) throws SQLException{
        if ( sec_check(place_id,"place_id","TICK") == 1){
            // we have place in tick
            update_data(1,place_id,"place_id","TICK");
        }
        else if ( sec_check(place_id,"place_id","SCENE") == 1){
            // we have place in scene
            update_data(1,place_id,"place_id","SCENE");
        }
        return delete(place_id,"place_id","PLACE");
    }
    
    /**
     * Database_Garbage_Collector.delete_category(int category_id)
     * @param category_id
     * @return boolean
     * @throws SQLException 
     * Function delete category by given id
     */
    public boolean delete_category(int category_id) throws SQLException{
        if (sec_check(category_id,"category_id","SCENE") == 1){
            // we have category in scene
            update_data(1,category_id,"category_id","SCENE");
        }
        else if (sec_check(category_id,"category_id","TICK") == 1){
            // we have category in tick
            update_data(1,category_id,"category_id","TICK");
        }
        return delete(category_id,"category_id","CATEGORY");
    }
    
    /**
     * Database_Garbage_Collector.delete_tag(int tag_id)
     * @param tag_id
     * @return boolean
     * @throws SQLException
     * Function delete tag
     */
    public boolean delete_tag(int tag_id) throws SQLException{
        return delete(tag_id,"tag_id","TAG");
    }
    
    /**
     * Database_Garbage_Collector.delete_hashtag_table(int hashtag_table_id)
     * @param hashtag_table_id
     * @return boolean
     * @throws SQLException 
     * Function delete hashtag table
     */
    public boolean delete_hashtag_table(int hashtag_table_id) throws SQLException{
        if ( sec_check(hashtag_table_id,"hashtag_table_id","TAG") == 1){
            // we have hashtag table in tag
            update_data(1,hashtag_table_id,"hashtag_table_id","HASHTAG_TABLE");
        }
        else if( sec_check(hashtag_table_id,"hashtag_table_id","TICK") == 1){
            // we have hashtag table in tick
            update_data(1,hashtag_table_id,"hashtag_table_id","TICK");
        }
        else if( sec_check(hashtag_table_id,"hashtag_table_id","SCENE") == 1){
            // we have hashtag table in scene
            update_data(1,hashtag_table_id,"hashtag_table_id","SCENE");
        }
        
        return delete(hashtag_table_id,"hashtag_table_id","HASHTAG_TABLE");
    }
    
    /**
     * Database_Garbage_Collector.delete_note(int note_id)
     * @param note_id
     * @return boolean
     * @throws SQLException 
     * Function for deleting note
     */
    public boolean delete_note(int note_id) throws SQLException{
        if ( sec_check(note_id,"note_id","TICK") == 1){
            // we have note in tick
            update_data(1,note_id,"note_id","TICK");
        }
        return delete(note_id,"note_id","NOTE");
    }
    
    /**
     * Database_Garbage_Collector.delete_scene(int scene_id)
     * @param scene_id
     * @return int
     * @throws SQLException
     * Function for deleting scene
     */
    public boolean delete_scene(int scene_id) throws SQLException{
        return delete(scene_id,"scene_id","SCENE");
    }
    
    /**
     * Database_Garbage_Collector.delete_tick(int tick_id)
     * @param tick_id
     * @return boolean
     * @throws SQLException 
     * Deleting tick
     */
    public boolean delete_tick(int tick_id) throws SQLException{
        return delete(tick_id,"tick_id","TICK");
    }
}

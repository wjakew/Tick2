/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.tick2.Tick_Address;
import com.jakubwawak.tick2.Tick_HashtagT;
import com.jakubwawak.tick2.Tick_Place;
import com.jakubwawak.tick2.Tick_Tag;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;

/**
 *Function for getting data from database
 * @author jakubwawak
 */
public class Database_DataGetter {
    String version = "v0.0.1";
    final String HEADER = "DATABASE_DATAGETTER "+version;
    Database database;
    
    // main database
    public Database_DataGetter(Database database){
        this.database = database;
    }
    
    /**
     * Function for loading result
     * @param to_load
     * @return ResultSet
     * @throws SQLException 
     */
    public ResultSet load_result(PreparedStatement to_load) throws SQLException{
        try{
            database.log.add("Trying to execute: "+to_load.toString(),HEADER);
            ResultSet rs = to_load.executeQuery();
            
            if ( rs.next() ){
                return rs;
            }
            return null;
        
        }catch(SQLException e){
            database.log.add("Failed to load result! ("+e.toString()+")",HEADER);
            return null;
        }
    }
    
    /**
     * Function fro getting all places
     * @return ArrayList
     */
    public ArrayList<Tick_Place> get_all_places() throws SQLException{
        ArrayList<Tick_Place> data_toRet = new ArrayList<>();
        
        String query = "SELECT * FROM PLACE WHERE owner_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1,database.logged.owner_id);
        database.log.add("Query: "+ppst.toString(),HEADER);
        try{
            
            ResultSet rs = load_result(ppst);
            if ( rs == null ){
                return null;
            }
            else{
                
                while ( rs.next() ){
                    data_toRet.add(new Tick_Place(rs));
                }
                return data_toRet;
            }

        
        }catch(SQLException e){
            database.log.add("Failed to get places ("+e.toString()+")",HEADER);
            return null;
        }
    }
    
    /**
     * Function for getting all addresses
     * @return ArrayList
     */
    public ArrayList<Tick_Address> get_all_address() throws SQLException{
        ArrayList<Tick_Address> data = new ArrayList<>();
        
        String query = "SELECT * FROM ADDRESS;";
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ResultSet rs = load_result(ppst);
        if ( rs == null){
            return null;
        }
        else{
            
            while( rs.next() ){
                data.add(new Tick_Address(rs));
            }
            return data;
        }
    }
    
    /**
     * Function for getting all tags
     * @return ArrayList
     */
    public ArrayList<Tick_Tag> get_all_tags() throws SQLException{
        ArrayList<Tick_Tag> data = new ArrayList<>();
        
        String query = "SELECT * FROM TAG WHERE owner_id = ?;";
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1,database.logged.owner_id);
        
        ResultSet rs = load_result(ppst);
        
        if ( rs == null){
            return null;
        }
        else{
            while(rs.next()){
                data.add(new Tick_Tag(rs));
            }
            return data;
        }
    }
    
    /**
     * Function for getting all tags
     * @return ArrayList
     */
    public ArrayList<Tick_HashtagT> get_all_htags() throws SQLException{
        ArrayList<Tick_HashtagT> data = new ArrayList<>();
        
        String query = "SELECT * FROM HASHTAG_TABLE WHERE owner_id = ?;";
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1,database.logged.owner_id);
        
        ResultSet rs = load_result(ppst);
        
        if ( rs == null){
            return null;
        }
        else{
            while(rs.next()){
                data.add(new Tick_HashtagT(rs));
            }
            return data;
        }
    }

}

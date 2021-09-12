/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.tick2.MDate_Object;
import com.jakubwawak.tick2.MDate_Parser;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

/**
 *Object for watching database data
 * @author jakubwawak
 */
public class Database_Watcher {
    final String version = "v0.0.1";
    final String HEADER = "DATABASE WATCHER "+version;
    
    /**
     * Constructor
     * @param mode
     * modes:
     * "lists" - checks lists activity
     */
    Database database;
    
    /**
     * Main constructor
     * @param database 
     */
    public Database_Watcher(Database database){
        
        this.database = database;
        
        database.log.add("Module is running..",HEADER);
        
    }
    
    /**
     * Database_Watcher.check_tick_expiration_date()
     * @return ArrayList
     * @throws SQLException
     * Function checks and returns which tick are after the due date
     */
    public List check_tick_expiration_date() throws SQLException{
        List<Integer> tick_ids = new ArrayList<Integer>();
        String query = "SELECT * from TICK WHERE owner_id = ? and tick_done_id = 1;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        database.log.add("Checking tick expiration date...",HEADER);
        
        ppst.setInt(1,database.logged.owner_id);
        
        database.log.add("Query: "+ppst.toString(),HEADER);
        
        try{

            ResultSet rs = ppst.executeQuery();
            int index = 0;
            while(rs.next()){
                // loping on ticks
                String loop_date = rs.getString("tick_date_end");
                if ( !loop_date.equals("")){    // checking if field isn't empty
                    // parsing date
                    MDate_Parser date_parser = new MDate_Parser(loop_date,1);
                    // making MDate_Object
                    MDate_Object date = date_parser.ret_date_object();

                    // now we have date of tick done 

                    Date current_time = new Date();

                    // the date is already gone
                    if ( date.compare(current_time) == 2){
                        tick_ids.add(rs.getInt("tick_id"));
                    }
                    index++;
                }
            }
        }catch(SQLException e){
            database.log.add("Failed to check tick expiration date ("+e.toString()+")",HEADER + " E!!!");
        }
        database.log.add("Found "+ Integer.toString(tick_ids.size())+" expired ticks",HEADER);
        return tick_ids;
    }

    
}

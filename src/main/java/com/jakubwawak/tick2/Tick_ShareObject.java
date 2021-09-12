/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import com.jakubwawak.database.Database;
import java.sql.SQLException;

/**
 *Object for storing data of share
 * @author jakubwawak
 */
public class Tick_ShareObject {
    
    int share_id;
    int owner_id;
    int owner_to;
    int tick_id;
    boolean done;
    
    
    public Tick_ShareObject(int share_id,int owner_id,int owner_to,int tick_id){
        this.share_id = share_id;
        this.owner_id = owner_id;
        this.owner_to = owner_to;
        this.tick_id = tick_id;
   
    }
    
    /**
     * Tick_ShareObject.show_details(Database database)
     * @param database
     * @return String
     * @throws SQLException
     * Function returns lines with details about share
     */
    public String show_details(Database database) throws SQLException{
        String to_ret = "";
        
        to_ret = "raw data:  share_id/" +Integer.toString(share_id) + " owner_id/"+Integer.toString(owner_id) 
                + "owner_to/"+Integer.toString(owner_to)+ " tick_id/"+Integer.toString(tick_id);
        
        to_ret = to_ret +"\n";
        
        to_ret = to_ret + "From: "+database.ret_owner_name(owner_id) +"\n"
                +"To: "+database.ret_owner_name(owner_to) + "\n"
                +"Tick det: "+database.ret_tick_name(tick_id);

        return to_ret;
    }
    
    
    
    
}

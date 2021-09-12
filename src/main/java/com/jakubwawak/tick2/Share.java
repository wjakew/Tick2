/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import com.jakubwawak.database.Database;
import com.jakubwawak.database.Database_Tick;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 *Object for sharing ticks
 * @author jakubwawak
 */
public class Share {
    final String version = "v1.0.0";
    final String HEADER = "SHARE "+version;
    
    /**
     *  CREATE TABLE SHARE_QUEUE
        (
        share_id INT AUTO_INCREMENT PRIMARY KEY,
        owner_id INT,
        owner_to INT,
        tick_id INT,
        share_date VARCHAR(50),
        share_done VARCHAR(10),
        CONSTRAINT fk_share1 FOREIGN KEY (owner_id) REFERENCES OWN(owner_id),
        CONSTRAINT fk_share2 FOREIGN KEY (owner_to) REFERENCES OWN(owner_id),
        CONSTRAINT fk_share3 FOREIGN KEY (tick_id) REFERENCES TICK(tick_id)
        );
     */
    
    
    public  int owner_id;
    public ArrayList<Tick_ShareObject> to_do;
    public boolean filled;
  
    public Database database;
    public int status = -2;
    
    public Share( Database database ) throws SQLException{
        this.database = database;
        owner_id = database.logged.owner_id;
        to_do = new ArrayList<>();
    }
    /**
     * Share.share_tick(int tick_id,int owner_to)
     * @param tick_id
     * @param owner_to
     * @return int
     * @throws SQLException
     * Function for sharing ticks
     * ret codes:
     * 
     *  1 - added succesfully data to the database
     * -1 - internal fail of tick object
     * -2 - tick with given id not found
     * -3 - user owner_to not found
     * -4 - database fail
     */
    public int share_tick(int tick_id,int owner_to) throws SQLException{
        
        if ( database.check_if_record_exists(tick_id, "tick")){
            // tick exists
            
            Tick_Tick tt = new Tick_Tick(database.return_TB_collection(database.logged, "tick", tick_id));
            
            database.log.add("Share det: tick_id/"+Integer.toString(tick_id)+ " owner_to/"+Integer.toString(owner_to),HEADER);
            
            if ( database.ret_owner_name(owner_to) != null){
                if ( !tt.internal_fail ){
                    Date d = new Date();
                    database.log.add("Found tick to share in database",HEADER);
                    // we found tick and user to send
                    String query =  "INSERT INTO SHARE_QUEUE\n" +
                                    "(owner_id,owner_to,tick_id,share_date,share_done)\n" +
                                    "VALUES\n" +
                                    "(?,?,?,?,?);";
                    PreparedStatement ppst = database.con.prepareStatement(query);

                    ppst.setInt(1, owner_id);   
                    ppst.setInt(2, owner_to);
                    ppst.setInt(3, tick_id);
                    ppst.setString(4, d.toString());
                    ppst.setInt(5,0);
                    
                    try{
                        
                        ppst.execute();
                        
                        database.log.add("Added share data to the database",HEADER);
                        return 1;
                        
                    }catch(SQLException e){
                        database.log.add("Failed to add share data in database ( "+e.toString()+")",HEADER+" E!!!");
                        return -4;
                    }
                }
                else{
                    database.log.add("Internal fail of Tick_Tick object",HEADER+" E!!!");
                    return -1;
                }
            }
            else{
                database.log.add("Failed to find user",HEADER);
                return -3;
            }
        }
        return -2;
    }
    
    /**
     * Share.share_done(int share_id)
     * @param share_id
     * @return boolean
     * @throws SQLException
     * Making share done
     */
    public boolean share_done(int share_id) throws SQLException{
        String query = "UPDATE SHARE_QUEUE SET share_done = ? where share_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1,1);
        ppst.setInt(2,share_id);
        
        try{
            ppst.execute();
            return true;
        }catch( SQLException e ){
            database.log.add("Failed making done share ("+e.toString()+")",HEADER+" E!!!");
            return false;
        }
    }
    
    /**
     * Share.share_load()
     * @return int
     * Function for loads ticks to user records in database
     * ret codes:
     * 0 - nothing to share
     * 1 - at least 1 to share
     * -1 - no share check first
     */
    public int share_load() throws SQLException{
        Database_Tick dt = new Database_Tick(database);
        if ( status != -2){
            if ( !to_do.isEmpty() ){
                // to_do list is not empty
                for (Tick_ShareObject tick_ids : to_do){
                    Tick_Tick tt = new Tick_Tick(database.return_TB_collection("tick", tick_ids.tick_id));
                    tt.owner_id = database.logged.owner_id;
                    tt.wall_updater();
                    database.log.add("Adding shared tick : "+Integer.toString(tt.tick_id),HEADER);
                    dt.add_tick(tt);
                    share_done(tick_ids.share_id);
                }
                return 1;
            }
            else{
                return 0;
            }
        }
        else{
            return -1;
        }
        
    }
    
    /**
     * Share.check_database()
     * @return Integer
     * @throws SQLException
     * Function checks database for things to share
     * ret codes:
     * 1 - found at least one object to share
     * 0 - no objects to share
     * -1 - internal fail
     */
    public int share_check_database() throws SQLException{
       owner_id = database.logged.owner_id;
       
       String query = "SELECT * FROM SHARE_QUEUE where owner_to = ? and share_done = 0";
       
       PreparedStatement ppst = database.con.prepareStatement(query);
       
       ppst.setInt(1,owner_id);
       
       try{
           ResultSet rs = ppst.executeQuery();
           
           while(rs.next()){
               to_do.add(new Tick_ShareObject(rs.getInt("share_id"),rs.getInt("owner_id"),
                       rs.getInt("owner_to"),rs.getInt("tick_id")));
           }
           
           if ( to_do.size() != 0 ){
               status = 1;
               return 1;
           }
           status = 0;
           return 0;
       }catch(SQLException e){
           database.log.add("Failed to reach SHARE_QUEUE ("+e.toString()+")",HEADER);
           status = -1;
           return -1;
       }
   }
   
   /**
    * Share.return_data()
    * @return ArrayList<String>
    * @throws SQLException 
    * Simple function returns lines to show of ticks shared for user
    */
    public ArrayList<String> return_data() throws SQLException{
       ArrayList<String> to_ret = new ArrayList<>();
       
       to_ret.add("Shares for your account:");
       
       String query = "SELECT * FROM SHARE_QUEUE where owner_to = ? and share_done = 0;";
       PreparedStatement ppst = database.con.prepareStatement(query);
       
       ppst.setInt(1,owner_id);
       
       
       try{
           ResultSet rs = ppst.executeQuery();
           
           while( rs.next() ){
               // looping on share objects
               String login = database.ret_owner_name(rs.getInt("owner_id"));
               if ( login == null){
                   login = "not found";
               }
               String tick_name = database.ret_tick_name(rs.getInt("tick_id"));
               if ( tick_name == null){
                   tick_name = "not found";
               }
               to_ret.add("Share id : "+Integer.toString(rs.getInt("share_id"))+" from: "+login+"/ "+tick_name);
           }
       }catch(SQLException e){
           database.log.add("Failed to load share data from database ("+e.toString()+")",HEADER);
           return null;
       }
       
       if ( to_ret.size() == 1 ){
           to_ret.add("Empty");
       }

       return to_ret;
   }
   /**
    * Share.return_data_history()
    * @return ArrayList
    * Simple function returns lines of shares shared by user
    */
    public ArrayList<String> return_data_history() throws SQLException{
       ArrayList<String> to_ret = new ArrayList<>();
       
       to_ret.add("Your shares to other user:");
       
       String query = "SELECT * FROM SHARE_QUEUE where owner_id = ?;";
       PreparedStatement ppst = database.con.prepareStatement(query);
       
       ppst.setInt(1,owner_id);
       
       
       try{
           ResultSet rs = ppst.executeQuery();
           
           while( rs.next() ){
               // looping on share objects
               String login = database.ret_owner_name(rs.getInt("owner_to"));
               if ( login == null){
                   login = "not found";
               }
               String tick_name = database.ret_tick_name(rs.getInt("tick_id"));
               if ( tick_name == null){
                   tick_name = "not found";
               }
               to_ret.add("Share id : "+Integer.toString(rs.getInt("share_id"))+" shared to: "+login+"/ "+tick_name);
           }
       }catch(SQLException e){
           database.log.add("Failed to load share data from database ("+e.toString()+")",HEADER);
           return null;
       }
       
       if ( to_ret.size() == 1 ){
           to_ret.add("Empty");
       }

       return to_ret;
   }
   /**
    * Share.return_my_data_history()
    * @return ArrayList
    * @throws SQLException
    * Function shows details about shares
    */
    public ArrayList<String> return_my_data_history() throws SQLException{
       ArrayList<String> to_ret = new ArrayList<>();
       
       to_ret.add("Done shares for your account:");
       
       String query = "SELECT * FROM SHARE_QUEUE where owner_to = ? and share_done = 1;";
       PreparedStatement ppst = database.con.prepareStatement(query);
       
       ppst.setInt(1,owner_id);
       
       
       try{
           ResultSet rs = ppst.executeQuery();
           
           while( rs.next() ){
               // looping on share objects
               String login = database.ret_owner_name(rs.getInt("owner_id"));
               if ( login == null){
                   login = "not found";
               }
               String tick_name = database.ret_tick_name(rs.getInt("tick_id"));
               if ( tick_name == null){
                   tick_name = "not found";
               }
               to_ret.add("Share id : "+Integer.toString(rs.getInt("share_id"))+" from: "+login+"/ "+tick_name);
           }
       }catch(SQLException e){
           database.log.add("Failed to load share data from database ("+e.toString()+")",HEADER);
           return null;
       }
       
       if ( to_ret.size() == 1 ){
           to_ret.add("Empty");
       }

       return to_ret;
   }
}

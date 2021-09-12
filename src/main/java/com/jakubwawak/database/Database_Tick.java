/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.tick2.Tick_Category;
import com.jakubwawak.tick2.Tick_HashtagT;
import com.jakubwawak.tick2.Tick_Note;
import com.jakubwawak.tick2.Tick_Place;
import com.jakubwawak.tick2.Tick_Tick;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 *Object for maintaining tick objects on the database
 * @author jakubwawak
 */
public class Database_Tick {
    final String version = "v2.0.1";
    final String HEADER = "DATABASE_TICK";
    Database database;
    
    /**
     * Main constructor
     * @param database 
     */
    public Database_Tick(Database database){
        this.database = database;
    }
    
    
    /**
     * Database_Tick.add_tick(Tick_Tick to_add)
     * @param to_add
     * @return boolean
     * @throws SQLException
     * Function for adding tick to the database
     */
    public boolean add_tick(Tick_Tick to_add) throws SQLException{
        String query = "INSERT INTO TICK\n" +
                       "(owner_id,place_id,category_id,note_id,hashtag_table_id,tick_done_id,tick_done_start,tick_date_end,tick_name,tick_priority)\n" +
                       "VALUES\n" +
                       "(?,?,?,?,?,?,?,?,?,?);";
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1,database.logged.owner_id);
        ppst.setInt(2, to_add.place_id);
        ppst.setInt(3, to_add.category_id);
        ppst.setInt(4, to_add.note_id);
        ppst.setInt(5, to_add.hashtag_table_id);
        ppst.setInt(6, to_add.tick_done_id);
        ppst.setString(7, to_add.tick_done_start);
        ppst.setString(8, to_add.tick_done_end);
        ppst.setString(9, to_add.tick_name);
        ppst.setInt(10,to_add.tick_priority);
        try{
            ppst.execute();
            return true;
        }catch(SQLException e){
            database.log.add("Failed to add tick ( "+e.toString()+")", HEADER);
            return false;
        }
    }
    
    /**
     * Database_Tick.filter_by(String mode, int object_id)
     * @param mode
     * @param object_id
     * @return ArrayList
     * Function for sorting ticks by different objects
     */
    public ArrayList<Tick_Tick> filter_by(String mode,int object_id) throws SQLException{
        
        database.log.add("Filtering tick. mode: "+mode+" object_id ="+Integer.toString(object_id),HEADER);
        
        ArrayList<Tick_Tick> data_toRet = new ArrayList<>();
        
        String query = "SELECT * FROM ";
        
        switch(mode){
            case "category":
                query = query + " TICK where category_id = ? and owner_id  = ?;";
                break;
            case "place":
                query = query + " TICK where place_id = ? and  owner_id = ?;";
                break;
            case "hashtag table":
                query = "query" + "TICK where hashtag_table_id = ? and owner_id = ?;";
                break;
            default:
                break;            
        }
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1, object_id);
        ppst.setInt(2,database.logged.owner_id);
        
        database.log.add("Query for filter: "+ppst.toString(),HEADER);
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            while (rs.next()){
                
                data_toRet.add(new Tick_Tick(rs));                
            }
        }catch(SQLException e){
            
            database.log.add("Failed to sort object (mode: "+mode+" ,object_id: "+Integer.toString(object_id)+" | "+e.toString()+")",HEADER+" E!!!");
            return null;
        }
        
        return data_toRet;
        
        
    }
    
    /**
     * Database_Tick.check_if_exists(int tick_id)
     * @param tick_id
     * @return boolean
     * @throws SQLException
     * Function returns true 
     */
    public boolean check_if_exists(int tick_id) throws SQLException{
        String query = " SELECT * FROM TICK WHERE owner_id = ? and tick_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1,database.logged.owner_id);
        ppst.setInt(2,tick_id);
        
        ResultSet rs = ppst.executeQuery();
        
        return rs.next();
    }
    /**
     * Database_Tick.update_data(int data_id,String mode)
     * @param data_id
     * @param mode
     * @return boolean
     * Modes:
     * place
     * address
     * hashtag_table
     * category
     * note
     * tick_done
     */
    public boolean update_data(int data_id,int tick_id,String mode) throws SQLException{
        String query = "UPDATE TICK SET "+mode+"_id = ? where tick_id = ? and owner_id = ?;";
        if ( mode.equals("tick_priority") ){
            query = "UPDATE TICK SET "+mode+"= ? where tick_id = ? and owner_id = ?;";
        }
        
        PreparedStatement ppst = database.con.prepareStatement(query);
      
        ppst.setInt(1,data_id);
        ppst.setInt(2,tick_id);
        ppst.setInt(3,database.logged.owner_id);
        try{
            ppst.execute();
            return true;
        }catch(SQLException e){
            database.log.add("FAILED TO UPDATE TICK ( "+e.toString()+")", HEADER);
            return false;
        }
    }
    /**
     * Database_Tick.set_end_date(String data,int tick_id)
     * @param data
     * @param tick_id
     * @return boolean
     * @throws SQLException 
     */
    public boolean set_end_date(String data,int tick_id) throws SQLException{
        String query = "UPDATE TICK SET tick_date_end = ? where tick_id = ? and owner_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setString(1,data);
        ppst.setInt(2, tick_id);
        ppst.setInt(3,database.logged.owner_id);
        
        try{
            ppst.execute();
            return true;
        }catch(SQLException e){
            database.log.add("FAILED TO SET END DATE ("+e.toString()+")",HEADER+" E!!!");
            return false;
        }
    }
    
    /**
     * Database_Tick.make_default(int tick_id)
     * @param tick_id
     * @return boolean
     * @throws SQLException
     * Returns boolean when makes default data to given tick by tick_id
     */
    public boolean make_default(int tick_id) throws SQLException{
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
        String query = "UPDATE TICK set "
                + "place_id = 1, category_id = 1,note_id = 1,hashtag_table_id = 1"
                + ",tick_done_id = 1 where tick_id = ? and owner_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1,tick_id);
        ppst.setInt(2,database.logged.owner_id);
        
        try{
            ppst.execute();
            return true;
        }catch(SQLException e){
            database.log.add("FAILED TO SET TICK DEFAULT ("+e.toString()+")", HEADER);
            return false;
        }
    }
    
    /**
     * Database_Tick.last_mark_done_id()
     * @return int
     * @throws SQLException
     * Function return index of the last tick_done_id
     */
    public int last_mark_done_id() throws SQLException{
        String query = "SELECT tick_done_id FROM TICK_DONE where owner_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        int index = 0;
        ppst.setInt(1,database.logged.owner_id);
        
        ResultSet rs = ppst.executeQuery();
        
        while(rs.next()){
            index = rs.getInt("tick_done_id");
        }
        
        return index;
        
    }
    /**
     * Database_Tick.mark_done(String note)
     * @param note
     * @return boolean
     * @throws SQLException 
     * Function for marking tick done
     */
    public boolean mark_done(String note, int tick_id) throws SQLException{
        Date actual = new Date();
        String query = "INSERT INTO TICK_DONE\n" +
                       "(owner_id,tick_done_date,tick_done_duration,tick_done_note)\n"+
                       "VALUES\n" +
                       "(?,?,?,?);";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1,database.logged.owner_id);
        ppst.setString(2, actual.toString());
        ppst.setString(3,"no data");
        ppst.setString(4, note);
        
        try{
            ppst.execute();
            update_data(last_mark_done_id(),tick_id,"tick_done");
            return true;
        }catch(SQLException e ){
            database.log.add("FAILED TO MARK DONE ( "+e.toString()+")",HEADER);
            return false;
        }        
    }
    
    /**
     * Database_Tick.unmark_done(int tick_id)
     * @param tick_id
     * @return boolean
     * @throws SQLException 
     * Function for unmarking tick
     */
    public boolean unmark_done(int tick_id) throws SQLException{
        String query = " SELECT tick_done_id FROM TICK where tick_id = ?";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1,database.logged.owner_id);
        try{
            ResultSet rs = ppst.executeQuery();
            
            if (rs.next()){
                
                query = "UPDATE TICK SET tick_done_id = 1 where tick_id = ? and owner_id = ?";
                ppst = database.con.prepareStatement(query);
                ppst.setInt(1,tick_id);
                ppst.setInt(2,database.logged.owner_id);
                ppst.execute();
                return true;
            }
            else{
                database.log.add("Can't find tick with id : "+Integer.toString(tick_id),HEADER+" E!!!");
                return false;
            }
        }catch(SQLException e){
                database.log.add("Failed to unmark done tick ("+e.toString()+")",HEADER+" E!!!");
                return false;
        }

    }
    /**
     * Database_Tick.get_serialization(int tick_id)
     * @param tick_id
     * @return String 
     * @throws SQLException 
     * Returns serialized data of tick
     */
    public String get_serialization(int tick_id) throws SQLException{
        String query = "SELECT * FROM TICK where tick_id = ? and owner_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1,tick_id);
        ppst.setInt(2,database.logged.owner_id);
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            if ( rs.next() ){
                Tick_Tick to_show = new Tick_Tick(database.return_one_tick_brick(rs, "tick"));
                return to_show.serialise();
            }
            return null;
        }catch(SQLException e){
            database.log.add("Failed to gather serialised data for tick ("+e.toString()+")",HEADER+" E!!!");
            return null;
        }
    }
    /**
     * Database_Tick.get_note(int tick_id)
     * @param tick_id
     * @return String
     * @throws SQLException
     * Function for returning note content by given tick id
     */
    public String get_note(int tick_id) throws SQLException{
        if(tick_id == -1){  // for GUI and clear and new Tick object
            return "";
        }
        String query = "SELECT note_id from TICK where tick_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1,tick_id);
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            if ( rs.next() ){
                Tick_Note tn = new Tick_Note(database.return_TB_collection(database.logged, "note", rs.getInt("note_id"))); 
                return tn.note_content;
            }
            else{
                return null;
            }
            
        
        }catch(SQLException e){
            database.log.add("Failed to load note data ("+e.toString()+")",HEADER+" E!!!");
            return null;
        }
    }
    
    /**
     * Database_Tick.view_simpleviews()
     * @return ArrayList
     * @throws SQLException
     * Function for loading simple views from database
     * modes:
     * 0 - not done ticks
     * 1 - done ticks
     */
    public ArrayList<String> view_simpleviews(int mode) throws SQLException{
        ArrayList<String> headers = new ArrayList<>();
        String query = "SELECT * FROM TICK WHERE owner_id = ? and tick_done_id = 1;";
        if ( mode == 1){
            query = "SELECT * FROM TICK WHERE owner_id = ? and tick_done_id != 1;";
        }
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1,database.logged.owner_id);
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            while ( rs.next() ){
                
                Tick_Tick loop_act = new Tick_Tick(database.return_one_tick_brick(rs, "tick"));
                headers.add(loop_act.simple_show());
                
            }
            return headers;
        
        }catch(SQLException e){
            database.log.add("Failed to load simple views from database ("+e.toString()+")",HEADER+" E!!!");
            return null;
        }
    }
    
    /**
     * Database_Tick.view_tick(int tick_id)
     * @param tick_id
     * @return ArrayList<String>
     * @throws SQLException 
     * Returns lines to show 
     */
    public ArrayList<String> view_tick(int tick_id) throws SQLException{
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
        String tab = "  ";
        ArrayList<String> to_ret_lines = new ArrayList<>();
        String query = "SELECT * FROM TICK where tick_id = ? and owner_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        
        ppst.setInt(1,tick_id);
        ppst.setInt(2,database.logged.owner_id);
        database.log.add("TICK QUERY: " +ppst.toString(),"DATABASE TICK");
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            Tick_Tick to_show = new Tick_Tick(database.return_tick_brick(rs, "tick"));
            
            to_ret_lines.add(to_show.simple_show());
            // setting priority
            to_ret_lines.add(tab + "Priority: "+Integer.toString(to_show.tick_priority));
            if ( to_show.tick_done_id == 1){
                to_ret_lines.add(tab + "TICK NOT DONE");
                if ( to_show.tick_done_end.equals("")){
                    to_ret_lines.add(tab+tab+"End date not set");
                }
                else{
                    to_ret_lines.add(tab+tab+"End date is set to: "+to_show.tick_done_end);
                }
                
                to_ret_lines.add("---");
            }
            else{
                to_ret_lines.add(tab + "TICK DONE");
                to_ret_lines.add("---");
            }
            
            // setting categories
            if ( to_show.category_id != 1){
                to_ret_lines.add("Category linked:");
                String clear_query = "SELECT * FROM CATEGORY WHERE category_id = ?;";
                ppst = database.con.prepareStatement(clear_query);
                
                ppst.setInt(1, to_show.category_id );
                
                rs = ppst.executeQuery();
                
                Tick_Category tc = new Tick_Category ( database.return_tick_brick(rs, "category"));
                to_ret_lines.addAll(tc.get_lines_to_show());
                to_ret_lines.add("---");
            }
            else {
                to_ret_lines.add(tab + "NO CATEGORIES");
                to_ret_lines.add("---");
            }
            // setting hashtag table
            if ( to_show.hashtag_table_id != 1){
                to_ret_lines.add("Hashtag table linked:");
                String clear_query = "SELECT * FROM HASHTAG_TABLE WHERE hashtag_table_id = ?;";
                ppst = database.con.prepareStatement(clear_query);
                
                ppst.setInt(1, to_show.hashtag_table_id);
                
                rs = ppst.executeQuery();
                
                Tick_HashtagT tht = new Tick_HashtagT(database.return_tick_brick(rs,"hashtag table"));
                
                to_ret_lines.addAll(tht.get_lines_to_show());
                to_ret_lines.add("---");
            }
            else{
                to_ret_lines.add(tab + "NO HASHTAG TABLES");
                to_ret_lines.add("---");
            }
            // setting places
            if ( to_show.place_id != 1){
                to_ret_lines.add("Place linked:");
                String clear_query = "SELECT * FROM PLACE where place_id = ?;";
                
                ppst = database.con.prepareStatement(clear_query);
                ppst.setInt(1,to_show.place_id);
                rs = ppst.executeQuery();
                
                Tick_Place tp = new Tick_Place(database.return_tick_brick(rs, "place"));
                
                to_ret_lines.addAll(tp.get_lines_to_show());
                to_ret_lines.add("---");
            }
            else{
                to_ret_lines.add(tab + "NO PLACES");
                to_ret_lines.add("---");
            }
            // setting note
            if ( to_show.note_id != 1){
                to_ret_lines.add("Note linked:");
                String clear_query = "SELECT * FROM NOTE where note_id = ?;";
                
                ppst = database.con.prepareStatement(clear_query);
                
                ppst.setInt(1, to_show.note_id);
                rs = ppst.executeQuery();
                
                Tick_Note tn = new Tick_Note(database.return_tick_brick(rs,"note"));
                
                to_ret_lines.addAll(tn.get_lines_to_show());
                to_ret_lines.add("---");
            }
            else{
                to_ret_lines.add(tab + "NO NOTES");
                to_ret_lines.add("---");
            }
            return to_ret_lines;
            
        }catch (SQLException e){
            database.log.add("TICK ERROR: "+e.toString(),"DATABASE TICK E!!!");
            return null;
        }
    }
    
}

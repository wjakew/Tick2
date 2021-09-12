/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.tick2.Configuration;
import com.jakubwawak.tick2.Tick_Address;
import com.jakubwawak.tick2.Tick_Brick;
import com.jakubwawak.tick2.Tick_Category;
import com.jakubwawak.tick2.Tick_HashtagT;
import java.sql.ResultSetMetaData;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import com.jakubwawak.tick2.Tick_Log;
import com.jakubwawak.tick2.Tick_Note;
import com.jakubwawak.tick2.Tick_Place;
import com.jakubwawak.tick2.Tick_Scene;
import com.jakubwawak.tick2.Tick_Tag;
import com.jakubwawak.tick2.Tick_User;

/**
 *Object for maintaining connection to the database.
 * @author jakubwawak
 */
public class Database {
    
    final String version = "v1.0.8";
    
    final String HEADER = "DATABASE ("+version+")";
    
    // database connection data
    String ip = "localhost";
    String database_name = "tick_database";
    String database_user = "root";
    String database_password = "password";
    // end of database connection data
    
    public Connection con = null;          // connection to the database
    ResultSet rs = null;            // result set of the query
    public boolean connected = false;
    public Tick_User logged;               // actual logged user on the database
    public Tick_Log log;                   // program log saver
    Configuration config;
    String database_data;           // for storing data
    Date date;
    
    /**
     * Main Database maintaining connection object
     * @param log 
     */
    public Database(Tick_Log log,Configuration config){
        this.config = config;
        this.log = log;
        database_data = HEADER +"\n";
        date = new Date();
        if ( this.config != null ){
            log.add("Config found. Setting data from it.",HEADER);
            ip = config.database_ip;
            database_name = config.database_name;
            database_user = config.database_user;
            database_password = config.database_pass;
        }
        else{
            log.add("Config not found. Setting standard connection",HEADER);
        }
    }
    //----------------------------maintanance and optional methods
    public void connect(){
        try{
            log.add("Set connection data: "+ip+"/"+database_name+"/"+database_user+"/|"+database_password+"|",HEADER);
            con = DriverManager.getConnection("jdbc:mysql://"+this.ip+"/"+database_name+"?"
                + "useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&" +
                                   "user="+database_user+"&password="+database_password);
            this.log.add("Connected: "+con.toString(),"DATABASE");
            connected = true;
            
            database_data = database_data + "Connected to "+con.toString()+"\n";
            database_data = database_data + "Connected at "+date.toString()+"\n";
            database_data = database_data + "Build number: "+get_buildnumber()+"\n";
            database_data = database_data + "Database version: "+check_database_version();
            
        }catch(SQLException ex){
            this.log.add("SQLException: " + ex.getMessage(),HEADER);
            this.log.add("SQLState: " + ex.getSQLState(),HEADER);
            this.log.add("VendorError: " + ex.getErrorCode(),HEADER); 
            connected = false;
        }
    }
    /**
     * Database.check_database_version()
     * @return String
     * @throws SQLException
     * Function for showing database version
     */
    public String check_database_version() throws SQLException{
        String query = "SELECT * FROM GENERAL_INFO";
        PreparedStatement ppst = con.prepareStatement(query);
        
        try{
            ResultSet rs_dv = ppst.executeQuery();
            if ( rs_dv.next() ){
                return rs_dv.getString("gi_version");
            }
            return null;
        
        }catch(SQLException e){
            log.add("Failed to gather database information ("+e.toString()+")",HEADER+"E!!!");
            return null;
        }
    }
    /**
     * Database.get_buildnumber()
     * @return String
     * @throws SQLException
     * Function for getting build number from database
     */
    public String get_buildnumber() throws SQLException{
        String query = "SELECT * FROM GENERAL_INFO";
        PreparedStatement ppst = con.prepareStatement(query);
        
        try{
            ResultSet rs_h = ppst.executeQuery();
            if ( rs_h.next() ){
                return rs_h.getString("gi_build_id");
            }
            return null;
        
        }catch(SQLException e){
            log.add("Failed to gather database information ("+e.toString()+")",HEADER+"E!!!");
            return null;
        }
    }
    
    /**
     * Database.check_if_record_exists(int id,String mode)
     * @param id
     * @param mode
     * @return
     * @throws SQLException 
     * Function checks if record exists
     */
    public boolean check_if_record_exists(int id,String mode) throws SQLException{
        String query = "";
        switch (mode) {
            case "address":
                query = "SELECT * FROM ADDRESS where address_id = ?;";
                break;
            case "category":
                query = "SELECT * FROM CATEGORY where category_id = ?;";
                break;
            case "hashtag table":
                query = "SELECT * FROM HASHTAG_TABLE where hashtag_table_id = ?;";
                break;
            case "note":
                query = "SELECT * FROM NOTE where note_id = ?;";
                break;
            case "place":
                query = "SELECT * FROM PLACE where place_id = ?;";
                break;
            case "tag":
                query = "SELECT * FROM TAG where tag_id = ?;";
                break;
            case "tick":
                query = "SELECT * FROM TICK where tick_id = ?;";
                break;
            case "tick_done":
                query = "SELECT * FROM TICK_DONE where tick_done_id = ?;";
                break;
            case "scene":
                query = "SELECT * FROM SCENE where scene_id =?;";
                break;
            case "list":
                query = "SELECT * FROM LISTS where list_id =?;";
                break;
            default:
                break;
        }
        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setInt(1, id);
        ResultSet rs_re = ppst.executeQuery();
        return rs_re.next();
    }
    /**
     * Database.get_debug_info(int owner_id)
     * @param user
     * @return int
     * @throws SQLException
     * Returns debug info number from database
     */
    public int get_debug_info(Tick_User user) throws SQLException{
        String query = "SELECT debug FROM CONFIGURATION where owner_id = ?";
        this.log.add("Getting debug info from database", HEADER);
        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setInt(1, user.owner_id);
        ResultSet rs_di = ppst.executeQuery();
        
        if ( rs_di.next() ){
            return rs_di.getInt("debug");
        }
        return -1;
    }
    
    /**
     * Database.get_all_elements()
     * @return ArrayList
     * Function for getting all elements like place category hashtagtable or tag from table
     */
    public ArrayList<String> get_all_elements() throws SQLException{
        ArrayList<String> data_toRet = new ArrayList<>();
        this.log.add("Getting elements from database...",HEADER);
        String data;
        String [] names = {"PLACE","CATEGORY","HASHTAG_TABLE"};
        
        for(String name : names){
            data = name;
            
            this.log.add("name: "+data,HEADER);
            
            
            String query = "SELECT * from "+ data + " where owner_id = ?;";
            
            PreparedStatement ppst = con.prepareStatement(query);
            
            ppst.setInt(1,logged.owner_id);
            
            this.log.add("QUERY: "+ppst.toString(),HEADER);
            
            try{
                
                ResultSet rs_gaea = ppst.executeQuery();
                
                while ( rs_gaea.next() ){
                    
                    switch (name) {
                        case "PLACE":
                            data_toRet.add("P "+rs_gaea.getString("place_name"));
                            break;
                        case "CATEGORY":
                            data_toRet.add("C "+rs_gaea.getString("category_name"));
                            break;
                        case "HASHTAG_TABLE":
                            data_toRet.add("H "+rs_gaea.getString("hashtag_table_name"));
                            break;
                        default:
                            break;
                    }
                }
                
            }catch(SQLException e){
                this.log.add("Failed to get all elements ("+e.toString()+")",HEADER);
                return null;
            }
        }
        
        return data_toRet;
        
    }
    
    /**
     * Database.array_has_it(int array[],int a)
     * @param array
     * @param a
     * @return boolean
     * Function for checking if array has a number
     */
    public boolean array_has_it(int array[],int a){
        for (int number : array){
            if (number == a){
                return true;
            }
        }
        return false;
    }
    public void close() throws SQLException{
        con.close();
        connected = false;
        logged = null;
        this.log.add("Database connection ended", HEADER);
    }
    /**
     * Database.dump_log()
     * Function for dumping log
     */
    public void dump_log() throws SQLException{
        String query = "INSERT INTO LOG\n" +
                        "(owner_id,log_string,log_date,error_code)\n" +
                        "VALUES\n" +
                        "(?,?,?,?);";
        
        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setInt(1,logged.owner_id);
        ppst.setString(3,log.actual_date.toString());
        
        for(String line : log.log_lines){
            ppst.setString(2,line);
            
            if ( line.contains("E!!!")){
                ppst.setInt(4,1);
            }
            else{
                ppst.setInt(4, 0);
            }
            
            try{
                ppst.execute();
            }catch(SQLException e){
                log.add("Failed to dump log to database (" + e.toString() + ")",HEADER);
            }
        }
    }
    /**
     * Database.make_first_configuration(Tick_User user)
     * @param user
     * @throws SQLException 
     * Setting the first configuration
     */
    public void make_first_configuration(Tick_User user) throws SQLException{
        log.add("Making first configuration for user: "+user.owner_login,HEADER);
        String query = "INSERT INTO CONFIGURATION\n" +
                       "(owner_id,sum_entries,debug,conf2,conf3,conf4,conf5,conf6,conf7)\n" +
                       "VALUES\n" +
                       "(?,1,1,'',0,'','','','');";
        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setInt(1, user.owner_id);
        log.add("QUERY FOR CONFIGURATION: "+ppst.toString(),HEADER);
        ppst.execute();
        log.add("First configuration added",HEADER);
    }
    /**
     * Database.ret_owner_id(String owner_login)
     * @param owner_login
     * @return int
     * @throws SQLException
     * Returns id of user with that login
     */
    public int ret_owner_id (String owner_login) throws SQLException{
        log.add("Returning id for user : " + owner_login, HEADER);
        String query = " SELECT owner_id from OWN WHERE owner_login = ?;";
        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setString(1, owner_login);
        ResultSet rs_roi = ppst.executeQuery();
        
        if ( rs_roi.next() ){
            return rs_roi.getInt("owner_id");
        }
        
        return -1;
    }
    
    /**
     * Database.ret_owner_name(int owner_id)
     * @param owner_id
     * @return String
     * @throws SQLException 
     * Returns owner login by owner_id
     */
    public String ret_owner_name(int owner_id) throws SQLException{
        String query = "SELECT owner_login FROM OWN where owner_id = ?;";
        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setInt(1, owner_id);
        
        try{
            ResultSet rs_ron = ppst.executeQuery();
            log.add("RET NAME: QUERY "+ ppst.toString(),HEADER);
            if ( rs_ron.next() ){
            return rs_ron.getString("owner_login");
            }
            else{
                return null;
            }
        }catch(SQLException e){
            log.add("Failed to get owner name by id ("+e.toString()+")",HEADER+" E!!!");
            return null;
        }
        
        
    }
    /**
     * Database.tick_name(int tick_id)
     * @param tick_id
     * @return String
     * @throws SQLException
     * Returns name of the tick by given tick id
     */
    public String ret_tick_name(int tick_id) throws SQLException{
        String query = "SELECT * from TICK where tick_id = ?;";
        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setInt(1, tick_id);
        
        try{
            ResultSet rs_rtn = ppst.executeQuery();
            if ( rs_rtn.next()){
                return rs_rtn.getString("tick_name");
            }
            return null;
            
        }catch(SQLException e){
            log.add("Failed to find tick name ("+e.toString()+")",HEADER+" E!!!");
            return null;
        }
    }
    /**
     * Database.get_last_address_id()
     * @return int
     * @throws SQLException 
     * Returning last address id
     */
    public int get_last_address_id() throws SQLException{
        String query = "select address_id from ADDRESS ORDER BY id DESC LIMIT 1;";
        PreparedStatement ppst = con.prepareStatement(query);
        try{
            ResultSet rs_lai = ppst.executeQuery();
            if ( rs_lai.next() ){
                return rs_lai.getInt("address_id");
            }
            return -1;
        }catch(SQLException e){
            log.add("Failed to get last address id ( "+e.toString()+")",HEADER);
            return -2;
        }
        
    }
    
    /**
     * Database.get_category_id_byname(String category_name)
     * @param category_name
     * @return
     * @throws SQLException 
     * Function gets category id by given name
     */
    public int get_category_id_byname(String category_name) throws SQLException{
        String query = "SELECT * FROM CATEGORY WHERE category_name = ?";
        PreparedStatement ppst = con.prepareStatement(query);
        
        ppst.setString(1,category_name);
        
        try{
            ResultSet rs_gcib  = ppst.executeQuery();
            
            if ( rs_gcib.next() ){
                return rs_gcib.getInt("category_id");
            }
            return -1;
        }catch(SQLException e){
            log.add("Failed to get category id by name ( "+e.toString()+")",HEADER);
            return -2;
        }  
    }
    /**
     * Database.get_place_id_byname(String place_name)
     * @param place_name
     * @return Integer
     * @throws SQLException 
     * Function gets place id by name
     */
    public int get_place_id_byname(String place_name) throws SQLException{
        String query = "SELECT * FROM PLACE where place_name = ?;";
        PreparedStatement ppst = con.prepareStatement(query);
        
        ppst.setString(1,place_name);
        
        try{
            ResultSet rs_gpib = ppst.executeQuery();
            
            if ( rs_gpib.next() ){
                return rs_gpib.getInt("place_id");
            }
            return -1;
        }catch(SQLException e){
            log.add("Failed to get place id by name ("+e.toString()+")",HEADER);
            return -2;
        }
    }
    /**
     * Database.get_hashtagtable_id_byname(String hashtagtable_name)
     * @param hashtagtable_name
     * @return Integer
     * @throws SQLException
     * Function for getting hashtagtable by name
     */
    public int get_hashtagtable_id_byname(String hashtagtable_name) throws SQLException{
        String query = "SELECT * from HASHTAG_TABLE where hashtag_table_name = ?;";
        PreparedStatement ppst = con.prepareStatement(query);
        
        ppst.setString(1,hashtagtable_name);
        
        try{
            ResultSet rs_h = ppst.executeQuery();
            
            if ( rs_h.next() ){
                return rs_h.getInt("hashtag_table_id");
            }
            return -1;
        }catch(SQLException e){
            log.add("Failed to get hashtag table id by name ("+e.toString()+")",HEADER+" E!!!");
            return -2;
        }
    }
    
    /**
     * Database.get_hashtagtable_obj_byid(int hashtag_table_id)
     * @param hashtag_table_id
     * @return Tick_HashtagT
     * Function gets hashtag table object by given id
     */
    public Tick_HashtagT get_hashtagtable_obj_byid(int hashtag_table_id) throws SQLException{
        String query = "SELECT * FROM HASHTAG_TABLE where hashtag_table_id = ?;";
        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setInt(1, hashtag_table_id);
        try{
            ResultSet rs_hob = ppst.executeQuery();
            if ( rs_hob.next() ){
                return new Tick_HashtagT(rs_hob);
            }
            log.add("Failed to get hashtag table by id ( resultset empty ) ",HEADER+" E!!!");
            return null;
            
        }catch(SQLException e){
            log.add("Failed to get hashtag table by id ("+e.toString()+")",HEADER+" E!!!");
            return null;
        }
    }
    
    /**
     * Database.get_tag_obj_byid(int tag_id)
     * @param tag_id
     * @return
     * @throws SQLException 
     */
    public Tick_Tag get_tag_obj_byid(int tag_id) throws SQLException{
        String query = "SELECT * FROM TAG WHERE tag_id = ?;";
        
        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setInt(1,tag_id);
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            if ( rs.next() ){
                return new Tick_Tag(rs);
            }
            log.add("Tick_Tag with tag_id: "+Integer.toString(tag_id)+" not found",HEADER);
            return null;
        }catch(SQLException e){
            log.add("Failed to get tag by id ("+e.toString()+")",HEADER+" E!!!");
            return null;
        }
    }
    /**
     * Database.get_last_id(String table_name)
     * @param table_name
     * @return int
     * @throws SQLException 
     * Returns last id of given table
     */
    public int get_last_id(String table_name) throws SQLException{
        int index = 0;
        String field = table_name.toLowerCase();
        field = field + "_id";
        String query = "SELECT * from "+table_name+" where owner_id = ?;";
        PreparedStatement ppst = con.prepareStatement(query);
        
        ppst.setInt(1,logged.owner_id);
        
        try{
            ResultSet rs_gli = ppst.executeQuery();
            
            while ( rs_gli.next() ){
                if ( table_name.equals("LISTS")){
                    field = "list_id";
                }
                index = rs_gli.getInt(field);
            }
        }catch(SQLException e){
            log.add("Failed to get last_id ("+e.toString()+")",HEADER+"E!!!");
        }
        return index;
    }
    /**
     * Database.get_element_name(String mode)
     * @param mode
     * @return ArrayList
     * @throws SQLException
     * modes:
     * category - returns name of elements in CATEGORY table
     * place - returns name of elements in PLACE table
     * hashtag table - returns name of elements in HASHTAG_TABLE table
     * options:
     * 1 - adding "None" and "Manage..." to list ( for objects used in GUI )
     * 0 - not adding anything more than names of elements
     */
    public ArrayList<String> get_element_name(String mode,int option) throws SQLException{
        ArrayList<String> data_toRet = new ArrayList<>();
        String query = "",element_name = "";
        String addon = "Manage...";
        switch (mode) {
            case "category":
                query = "SELECT * from CATEGORY;";
                element_name = "category_name";
                break;
            case "place":
                query = "SELECT * from PLACE;";
                element_name = "place_name";
                break;
            case "hashtag table":
                query = "SELECT * from HASHTAG_TABLE";
                element_name = "hashtag_table_name";
                break;
            default:
                break;
        }
        PreparedStatement ppst = con.prepareStatement(query);
        //ppst.setInt(1,logged.owner_id);
        try{
            ResultSet rs_gen  = ppst.executeQuery();
            
            while ( rs_gen.next() ){
                data_toRet.add(rs_gen.getString(element_name));
            }
            
            if ( data_toRet.isEmpty() ){
                data_toRet.add("-");
            }
            
            // adding options to not adding element
            if ( option == 1){
                data_toRet.add("None");
                data_toRet.add(addon);
            }
            
        }catch(SQLException e){
            log.add("Failed to load element names...",HEADER + " E!!!");
            return null;
        }
        return data_toRet;
    }
    
    /**
     * Database.get_all_tags()
     * @return ArrayList
     * @throws SQLException
     * Function for getting all tags from database
     */
    public ArrayList<Tick_Tag> get_all_tags() throws SQLException{
        ArrayList<Tick_Tag> data_toRet = new ArrayList<>();
        ArrayList<Integer> ids = get_all_id("tag");
        String query = "SELECT * from tag where tag_id = ?";
        PreparedStatement ppst = con.prepareStatement(query);
        for(int id : ids){
            ppst.setInt(1, id);
            ResultSet rs_gat = ppst.executeQuery();
            
            if ( rs_gat.next() ){
                data_toRet.add(new Tick_Tag(rs_gat));
            }
        }
        return data_toRet;
    }
    
    /**
     * Database.get_all_id(String mode)
     * @param mode
     * @return int[]
     * @throws SQLException
     * Function for getting all ids from table
     */
    public ArrayList<Integer> get_all_id(String mode) throws SQLException{
        ArrayList<Integer> ids = new ArrayList<>();
        String query = "";
        switch (mode) {
            case "address":
                query = "SELECT * FROM ADDRESS;";
                break;
            case "category":
                query = "SELECT * FROM CATEGORY where owner_id = ?;";
                break;
            case "hashtag table":
                query = "SELECT * FROM HASHTAG_TABLE where owner_id = ?;";
                break;
            case "note":
                query = "SELECT * FROM NOTE where owner_id = ?;";
                break;
            case "place":
                query = "SELECT * FROM PLACE where owner_id = ?;";
                break;
            case "tag":
                query = "SELECT * FROM TAG where owner_id = ?;";
                break;
            case "list":
                query = "SELECT * FROM LISTS where owner_id = ?";
                break;
            default:
                break;
        }
        PreparedStatement ppst = con.prepareStatement(query);
        if ( !mode.equals("address") ){
            ppst.setInt(1,logged.owner_id);
        }
        try{
            ResultSet rs_gai = ppst.executeQuery();
            
            while(rs_gai.next()){

                ids.add(rs_gai.getInt(mode+"_id"));
            }
        }catch(SQLException e){
            log.add("Failed to get all ids from table ("+e.toString()+")",HEADER);
            return null;
        }
        return ids;
    }
    
    /**
     * Database.return_resultset(String query)
     * @param query
     * @return ResultSet
     * @throws SQLException
     * Returns resultset of given string
     */
    public ResultSet return_resultset(String query) throws SQLException{
         PreparedStatement ppst = con.prepareStatement(query);
         return ppst.executeQuery();
    }
    
    /**
     * Database.make_query(String query_head, int [] numbers)
     * @param query_head
     * @param numbers
     * @return String
     * Function returns string for query
     */
    public String make_query(String query_head, int [] numbers){
        String [] parts = query_head.split("?");
        String result = "";
        int i = 0;
        for( String part : parts ){
            result = result + part.substring(0, part.length()-2) + Integer.toString(numbers[i]);
            i++;
        }
        return result;
    }
    /**
     * Database.return_resultset(String mode,Tick_User logged_user)
     * @param mode
     * @param logged_user
     * @return
     * @throws SQLException 
     * Returns ResultSet for choosen object
     */
    public ResultSet return_resultset(String mode,Tick_User logged_user) throws SQLException{
        String query = "";
        switch (mode) {
            case "address":
                query = "SELECT * FROM ADDRESS;";
                break;
            case "category":
                query = "SELECT * FROM CATEGORY where owner_id = ?;";
                break;
            case "hashtag table":
                query = "SELECT * FROM HASHTAG_TABLE where owner_id = ?;";
                break;
            case "note":
                query = "SELECT * FROM NOTE where owner_id = ?;";
                break;
            case "place":
                query = "SELECT * FROM PLACE where owner_id = ?;";
                break;
            case "tag":
                query = "SELECT * FROM TAG where owner_id = ?;";
                break;
            default:
                break;
        }
        PreparedStatement ppst = con.prepareStatement(query);
        if ( !mode.equals("address") ){
            ppst.setInt(1,logged_user.owner_id);
        }
        try{
            return ppst.executeQuery();
        }catch(SQLException e){
            log.add("Cant return ResultSet",HEADER);
            return null;
        }
    }
    /**
     * Database.prepare_tick_brick(ResultSet rs_ul,String mode)
     * @param rs
     * @param mode
     * @return ArrayList
     * @throws SQLException
     * Returns collection of Tick_Brick to make object
     */
    public ArrayList<Tick_Brick> return_tick_brick(ResultSet rs,String mode) throws SQLException{
        // metadata for number of columns
        ResultSetMetaData meta   = (ResultSetMetaData) rs.getMetaData();
        ArrayList<Tick_Brick> to_ret = new ArrayList<>();  // tick_brick to ret
        
        int index[] = {};                // array of int indexes
        switch (mode) {
            case "address":
                /**
                 *  address_id INT AUTO_INCREMENT PRIMARY KEY,
                 * address_city VARCHAR(30),
                 * address_street VARCHAR (30),
                 * address_house_number INT,
                 * address_flat_number INT,
                 * address_postal VARCHAR(15),
                 * address_country VARCHAR(30)
                 */
                index = new int[] {1,4,5};
                break;
            case "category":
                /**
                 *  category_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * category_name VARCHAR(45),
                 * category_note VARCHAR(100),
                 */
                index = new int[] {1,2};
                break;
            case "hashtag table":
                /**
                 *  hashtag_table_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * hashtag_table_name VARCHAR(45),
                 * hashtag_table_note VARCHAR(100),
                 *
                 */
                index = new int[] {1,2};
                break;
            case "note":
                /**
                 *  note_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * note_content VARCHAR(100),
                 * setting1 VARCHAR(40),
                 * setting2 VARCHAR(40),
                 * setting3 VARCHAR(40),
                 */
                index = new int[] {1,2};
                break;
            case "place":
                /**
                 *  place_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * place_name VARCHAR(30),
                 * address_id INT,
                 */
                index = new int[] {1,2,4};
                break;
            case "tag":
                /**
                 *  tag_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * hashtag_table_id INT,
                 * tag_name VARCHAR(45),
                 * tag_note VARCHAR(100),
                 */
                index = new int[] {1,2,3};
                break;
            case "tick":
                /**
                 *  tick_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * place_id INT,
                 * category_id INT,
                 * note_id INT,
                 * hashtag_table_id INT,
                 * tick_done_id INT,
                 * tick_done_start VARCHAR(60),
                 * tick_date_end VARCHAR(60),
                 * tick_name VARCHAR(60),
                 * tick_priority INT
                 */
                index = new int[] {1,2,3,4,5,6,7,11};
                break;
            case "scene":
                /**
                 *  scene_id INT AUTO_INCREMENT PRIMARY KEY,
                 * hashtag_table_id INT,
                 * place_id INT,
                 * owner_id INT,
                 * category_id INT,
                 * scene_name VARCHAR(30),
                 * scene_note VARCHAR(100),
                 */
                index = new int[] {1,2,3,4,5};
                break;
            default:
                break;
        }
        // coping array to collection
        List<Integer> int_index = Arrays.stream(index).boxed().collect(Collectors.toList());
        int colmax = meta.getColumnCount();
        while(rs.next()){
                    // looping on all database records returned by ResultSet
            // looping on one record
            for ( int i = 1 ; i <= colmax; i++){
                if ( int_index.contains(i)){
                    to_ret.add(new Tick_Brick(rs.getInt(meta.getColumnName(i))));
                }
                else{
                    to_ret.add(new Tick_Brick(rs.getString(meta.getColumnName(i))));
                }
            }
            // flagging end of the object
            Tick_Brick brake = new Tick_Brick();
            brake.STOP = true;
            to_ret.add(brake);
        }
        if (to_ret.size()>0){
            log.add("Tick_Brick Collection returns succesfully", HEADER); 
            log.add("RS DATA: "+rs.toString(), HEADER);
        }
        else{
            log.add("RS size is probably 0.("+rs.toString()+")",HEADER+"E!!!");
            log.add("Failed to reach RS",HEADER+"E!!!");
        }

        return to_ret;
    }
    
    public ArrayList<Tick_Brick> return_one_tick_brick(ResultSet rs,String mode) throws SQLException{
        // metadata for number of columns
        ResultSetMetaData meta   = (ResultSetMetaData) rs.getMetaData();
        ArrayList<Tick_Brick> to_ret = new ArrayList<>();  // tick_brick to ret
        
        int index[] = {};                // array of int indexes
        switch (mode) {
            case "address":
                /**
                 *  address_id INT AUTO_INCREMENT PRIMARY KEY,
                 * address_city VARCHAR(30),
                 * address_street VARCHAR (30),
                 * address_house_number INT,
                 * address_flat_number INT,
                 * address_postal VARCHAR(15),
                 * address_country VARCHAR(30)
                 */
                index = new int[] {1,4,5};
                break;
            case "category":
                /**
                 *  category_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * category_name VARCHAR(45),
                 * category_note VARCHAR(100),
                 */
                index = new int[] {1,2};
                break;
            case "hashtag table":
                /**
                 *  hashtag_table_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * hashtag_table_name VARCHAR(45),
                 * hashtag_table_note VARCHAR(100),
                 *
                 */
                index = new int[] {1,2};
                break;
            case "note":
                /**
                 *  note_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * note_content VARCHAR(100),
                 * setting1 VARCHAR(40),
                 * setting2 VARCHAR(40),
                 * setting3 VARCHAR(40),
                 */
                index = new int[] {1,2};
                break;
            case "place":
                /**
                 *  place_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * place_name VARCHAR(30),
                 * address_id INT,
                 */
                index = new int[] {1,2,4};
                break;
            case "tag":
                /**
                 *  tag_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * hashtag_table_id INT,
                 * tag_name VARCHAR(45),
                 * tag_note VARCHAR(100),
                 */
                index = new int[] {1,2,3};
                break;
            case "tick":
                /**
                 *  tick_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * place_id INT,
                 * category_id INT,
                 * note_id INT,
                 * hashtag_table_id INT,
                 * tick_done_id INT,
                 * tick_done_start VARCHAR(60),
                 * tick_date_end VARCHAR(60),
                 * tick_name VARCHAR(60),
                 */
                index = new int[] {1,2,3,4,5,6,7};
                break;
            case "scene":
                /**
                 *  scene_id INT AUTO_INCREMENT PRIMARY KEY,
                 * hashtag_table_id INT,
                 * place_id INT,
                 * owner_id INT,
                 * category_id INT,
                 * scene_name VARCHAR(30),
                 * scene_note VARCHAR(100),
                 */
                index = new int[] {1,2,3,4,5};
                break;
            case "list":
                /**
                 *  list_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * tick_list_id VARCHAR(100),
                 * list_name VARCHAR(50),
                 * list_date VARCHAR(50),
                 */
                index = new int[] {1,2};
                break;
            default:
                break;
        }
        // coping array to collection
        List<Integer> int_index = Arrays.stream(index).boxed().collect(Collectors.toList());
        int colmax = meta.getColumnCount();
        
        // looping on all database records returned by ResultSet
        // looping on one record
        for ( int i = 1 ; i <= colmax; i++){
            if ( int_index.contains(i)){
                to_ret.add(new Tick_Brick(rs.getInt(meta.getColumnName(i))));
            }
            else{
                to_ret.add(new Tick_Brick(rs.getString(meta.getColumnName(i))));
            }
        }
        // flagging end of the object
        Tick_Brick brake = new Tick_Brick();
        brake.STOP = true;
        to_ret.add(brake);
        
        if (to_ret.size()>0){
            log.add("Tick_Brick Collection returns succesfully", HEADER); 
            log.add("RS DATA: "+rs.toString(), HEADER);
        }
        else{
            log.add("RS size is probably 0. Mode ("+mode+")",HEADER+"E!!!");
            log.add("Failed to reach RS",HEADER+"E!!!");
        }

        return to_ret;
    }
    //----------------------------tick brick functions
    /**
     * Database.prepare_tick_brick(ResultSet rs_ul)
     * @param rs
     * @return ArrayList
     * Function returns collection of Tick_Brick
     */
    public ArrayList<Tick_Brick> prepare_tick_brick(ResultSet rs,String mode) throws SQLException{
        // metadata for number of columns
        ResultSetMetaData meta   = (ResultSetMetaData) rs.getMetaData();
        
        ArrayList<Tick_Brick> to_ret = new ArrayList<>();  // tick_brick to ret
        
        int index[] = {};                // array of int indexes
        switch (mode) {
            case "address":
                /**
                 *  address_id INT AUTO_INCREMENT PRIMARY KEY,
                 * address_city VARCHAR(30),
                 * address_street VARCHAR (30),
                 * address_house_number INT,
                 * address_flat_number INT,
                 * address_postal VARCHAR(15),
                 * address_country VARCHAR(30)
                 */
                index = new int[] {1,4,5};
                break;
            case "category":
                /**
                 *  category_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * category_name VARCHAR(45),
                 * category_note VARCHAR(100),
                 */
                index = new int[] {1,2};
                break;
            case "hashtag table":
                /**
                 *  hashtag_table_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * hashtag_table_name VARCHAR(45),
                 * hashtag_table_note VARCHAR(100),
                 *
                 */
                index = new int[] {1,2};
                break;
            case "note":
                /**
                 *  note_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * note_content VARCHAR(100),
                 * setting1 VARCHAR(40),
                 * setting2 VARCHAR(40),
                 * setting3 VARCHAR(40),
                 */
                index = new int[] {1,2};
                break;
            case "place":
                /**
                 *  place_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * place_name VARCHAR(30),
                 * address_id INT,
                 */
                index = new int[] {1,2,4};
                break;
            case "tag":
                /**
                 *  tag_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * hashtag_table_id INT,
                 * tag_name VARCHAR(45),
                 * tag_note VARCHAR(100),
                 */
                index = new int[] {1,2,3};
                break;
            case "scene":
                /**
                 *  scene_id INT AUTO_INCREMENT PRIMARY KEY,
                 * hashtag_table_id INT,
                 * place_id INT,
                 * owner_id INT,
                 * category_id INT,
                 * scene_name VARCHAR(30),
                 * scene_note VARCHAR(100),
                 */
                index = new int[] {1,2,3,4,5};
                break;
            case "tick":
            case "tick_done":
                /**
                 *  tick_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * place_id INT,
                 * category_id INT,
                 * note_id INT,
                 * hashtag_table_id INT,
                 * tick_done_id INT,
                 * tick_done_start VARCHAR(60),
                 * tick_date_end VARCHAR(60),
                 * tick_name VARCHAR(60),
                 * tick_priority INT,
                 */
                index = new int[] {1,2,3,4,5,6,7,11};
                break;
            case "lists":
                /**
                 *  list_id INT AUTO_INCREMENT PRIMARY KEY,
                 * owner_id INT,
                 * tick_list_id VARCHAR(100),
                 * list_name VARCHAR(50),
                 * list_date VARCHAR(50),
                 */
                index = new int[] {1,2};
                break;
            default:
                break;
        }
        // coping array to collection
        List<Integer> int_index = Arrays.stream(index).boxed().collect(Collectors.toList());
        int colmax = meta.getColumnCount();
        
        if ( rs != null ){
            // looping on all database records returned by ResultSet
            while( rs.next() ){
                // looping on one record
                
                for ( int i = 1 ; i <= colmax; i++){
                    if ( int_index.contains(i)){
                        to_ret.add(new Tick_Brick(rs.getInt(meta.getColumnName(i))));
                    }
                    else{
                        to_ret.add(new Tick_Brick(rs.getString(meta.getColumnName(i))));
                    }
                }
                // flagging end of the object
                Tick_Brick brake = new Tick_Brick();
                brake.STOP = true;
                to_ret.add(brake);
            } 
            log.add("Tick_Brick Collection returns succesfully", HEADER); 
        }
        else{
            log.add("Tick_Brick Collection failed", HEADER);
        }
        return to_ret;
    }
    /**
     * Database.return_collection(Tick_User logged_user, String mode)
     * @param logged_user
     * @param mode
     * @return ArrayList
     * @throws SQLException
     * Returns collection of Tick_Brick objects to make Tick_Element object
     */
    public ArrayList<Tick_Brick> return_TB_collection(Tick_User logged_user, String mode) throws SQLException{
        ResultSet actual = return_resultset(mode,logged_user);
        return return_tick_brick(actual,mode);
    }
    public ArrayList<Tick_Brick> return_TB_collection(Tick_User logged_user, String mode,String query) throws SQLException{
        ResultSet actual = return_resultset(query);
        return return_tick_brick(actual,mode);
    }
    public ArrayList<Tick_Brick> return_TB_collection(Tick_User logged_user, String mode, int object_id) throws SQLException{
        log.add("Started return_TB_collection function : tick_user, mode and object",HEADER);
        String query = "";
        switch (mode) {
            case "address":
                query = "SELECT * FROM ADDRESS where address_id = ?;";
                break;
            case "category":
                query = "SELECT * FROM CATEGORY where category_id = ? ;";
                break;
            case "hashtag table":
                query = "SELECT * FROM HASHTAG_TABLE where hashtag_table_id = ?;";
                break;
            case "note":
                query = "SELECT * FROM NOTE where note_id = ?;";
                break;
            case "place":
                query = "SELECT * FROM PLACE where place_id = ?;";
                break;
            case "tag":
                query = "SELECT * FROM TAG where tag_id = ?;";
                break;
            case "scene":
                query = "SELECT * FROM SCENE where scene_id = ?;";
                break;
            case "tick":
                query = "SELECT * FROM TICK where tick_id = ?;";
                break;
            case "list":
                query = "SELECT * FROM LISTS where list_id =?;";
                break;
            default:
                break;
        }
        
        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setInt(1,object_id);
        
        ResultSet result = ppst.executeQuery();
        
        log.add("RS QUERY : "+ppst.toString(),HEADER);
        
        if (result.next()){
            log.add("RS OWNER ID: "+Integer.toString(result.getInt("owner_id")),HEADER);
            return return_one_tick_brick(result,mode);
        }
        return null; 
    }
    public ArrayList<Tick_Brick> return_TB_collection(String mode,int object_id) throws SQLException{
        log.add("Started return_TB_collection function : object and mode",HEADER);
        String query = "";
        switch (mode) {
            case "address":
                query = "SELECT * FROM ADDRESS where address_id = ?;";
                break;
            case "category":
                query = "SELECT * FROM CATEGORY where category_id = ?;";
                break;
            case "hashtag table":
                query = "SELECT * FROM HASHTAG_TABLE where and hashtag_table_id = ?;";
                break;
            case "note":
                query = "SELECT * FROM NOTE where note_id = ?;";
                break;
            case "place":
                query = "SELECT * FROM PLACE where place_id = ?;";
                break;
            case "tag":
                query = "SELECT * FROM TAG where tag_id = ?;";
                break;
            case "scene":
                query = "SELECT * FROM SCENE where scene_id = ?;";
                break;
            case "tick":
                query = "SELECT * FROM TICK where tick_id = ?;";
                break;
            case "list":
                query = "SELECT * FROM LISTS where list_id =?;";
                break;
            default:
                break;
        }
        
        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setInt(1,object_id);
        
        ResultSet result = ppst.executeQuery();
        
        log.add("RS QUERY : "+ppst.toString(),HEADER);
        
        if (result.next()){
            log.add("RS OWNER ID: "+Integer.toString(result.getInt("owner_id")),HEADER);
            return return_one_tick_brick(result,mode);
        }
        return null; 
    }
    //----------------------------USER LOGIN TO THE DATABASE
    /**
     * Database.user_login(String user_login, String user_password)
     * @param user_login
     * @param user_password
     * @return
     * @throws SQLException 
     * Logging into database
     */
    public Tick_User user_login(String user_login, String user_password) throws SQLException{
        String query = "SELECT * FROM OWN WHERE owner_login = ? and owner_password = ?;";
        this.log.add("Trying to log, user credentials: "+user_login+"/"+user_password, HEADER);
        logged = null;
        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setString(1, user_login);
        ppst.setString(2, user_password);
        
        ResultSet rs_ul = ppst.executeQuery();
        
        ResultSetMetaData meta   = (ResultSetMetaData) rs_ul.getMetaData();
        int colmax = meta.getColumnCount();
        this.log.add("COLMAX "+Integer.toString(colmax), HEADER);
        int a[] = {1,3,8,9};
        
        ArrayList<Tick_Brick> us_part = new ArrayList<>();
        if ( rs_ul.next() ){
            for ( int i = 1 ; i <= colmax; i++){
                if ( array_has_it(a,i)){
                    us_part.add(new Tick_Brick(rs_ul.getInt(meta.getColumnName(i))));
                }
                else{
                    us_part.add(new Tick_Brick(rs_ul.getString(meta.getColumnName(i))));
                }
            }
            this.log.add("Logged user correctly", HEADER);
            logged = new Tick_User(us_part);
        }
        return logged;
    }
    //----------------------------FUNCTIONS FOR USER
    /**
     * Database.check_debug()
     * @return data about checking debug
     * @throws SQLException 
     */
    public boolean check_debug() throws SQLException{
        String query = " SELECT debug from CONFIGURATION where owner_id = ?;";
        
        PreparedStatement ppst = con.prepareStatement(query);
        
        ppst.setInt(1,logged.owner_id);
        
        try{
            ResultSet act_rs = ppst.executeQuery();
            
           if (act_rs.next()){
                return act_rs.getInt("debug") == 1;
           }
        
        }catch(SQLException e){
            this.log.add("Failed to check debug info ("+e.toString()+")",HEADER+"E!!!");
            return false;
        }
        return false;
    }
    /**
     * Database.register_user(Tick_User to_add)
     * @param to_add
     * @throws SQLException 
     * Function adds user records to database
     */
    public void register_user(Tick_User to_add) throws SQLException{
        log.add("Registering new user...",HEADER);
        String query = "INSERT INTO OWN\n" +
                       "(owner_login,address_id,owner_password,owner_name,owner_surname,owner_email_address,\n" +
                       "owner_age,owner_status)\n" +
                       "VALUES\n" +
                       "(?,?,?,?,?,?,?,?);";
        log.add("QUERY: "+query, HEADER);
        PreparedStatement ppst = con.prepareStatement(query);
        
        for ( int i = 1,j=1 ; i < to_add.tick_Element_size ; i++,j++){
            Tick_Brick act = to_add.tick_Element_Elements.get(j);
            
            if ( act.category == 1){
                ppst.setInt(i, act.data_int);
            }
            else if (act.category == 2){
                ppst.setString(i, act.data_string);
            }
        }
        log.add("QUERY: "+ppst.toString(), HEADER);
        ppst.execute();
        
        to_add.owner_id = ret_owner_id(to_add.owner_login);
        make_first_configuration(to_add);
    }
    
    /**
     * Database.change_password(Tick_User logged, String new_password)
     * @param logged
     * @param new_password
     * @return
     * @throws SQLException 
     * Function changes user password
     */
    public boolean change_password(Tick_User logged, String new_password) throws SQLException{
        log.add("Changing user password",HEADER);
        String query = "UPDATE OWN SET owner_password = ? WHERE owner_id = ?;";
        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setString(1,new_password);
        ppst.setInt(2,logged.owner_id);
        log.add("QUERY: "+ppst.toString(), HEADER);
        try{
            ppst.execute();
            return true;
        }catch(SQLException e){
            log.add("Failed to change user password",HEADER);
            return false;
        }
    }
    /**
     * Database.check_password(Tick_User logged,String password)
     * @param logged
     * @param password
     * @return boolean
     * @throws SQLException
     * Function returns true if password is correct.
     */
    public boolean check_password(Tick_User logged,String password) throws SQLException{
        String query = "SELECT owner_id from OWN where owner_id = ? and owner_password = ?; ";
        PreparedStatement ppst = con.prepareStatement(query);
        
        ppst.setInt(1,logged.owner_id);
        ppst.setString(2,password);
        try{
            return ppst.executeQuery().next();
        }catch(SQLException e){
            log.add("Failed to check password ("+e.toString()+")",HEADER);
            return false;
        }
    }
    
    /**
     * Function for checking if given login is taken
     * @param login
     * @return boolean
     */
    public boolean check_login_availability(String login){
        String query = "SELECT owner_login from OWN where owner_login = ?;";
        
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            
            ppst.setString(1,login);
            
            return ppst.executeQuery().next();
        }catch(SQLException e){
            log.add("Failed to check login availability! ("+e.toString()+")",HEADER);
            return false;
        }
    }
    //----------------------------FUNCTIONS FOR PLACE
    /**
     * Database.add_place(Tick_Place to_add)
     * @param to_add
     * @return boolean
     * @throws SQLException
     * 
     */
    public boolean add_place(Tick_Place to_add) throws SQLException{
        String query = "INSERT INTO PLACE\n" + 
                       "(owner_id,place_name,address_id)\n" +
                       "VALUES\n" +
                       "(?,?,?);";
        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setInt(1, to_add.owner_id);
        ppst.setString(2, to_add.place_name);
        ppst.setInt(3, to_add.address_id);
        try{
            ppst.execute();
            log.add("Added place",HEADER);
            return true;
        }catch(SQLException e){
            log.add("Failed to add place",HEADER);
            log.add("QUERY FAILED: "+e.getMessage(),HEADER+ "E!!");
            return false;
        }      
    }
    //----------------------------FUNCTIONS FOR ADDRESS
    /**
     * Database add_address(Tick_Address to_add)
     * @param to_add
     * @throws SQLException 
     * Adding address to the database
     */
    public boolean add_address(Tick_Address to_add) throws SQLException{
        log.add("Adding new address",HEADER);
        String query = "INSERT INTO ADDRESS\n" +
                       "(address_city,address_street,address_house_number,address_flat_number,address_postal,\n" +
                       "address_country)\n" +
                       "VALUES\n" +
                       "(?,?,?,?,?,?);";
        PreparedStatement ppst = con.prepareStatement(query);

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
            log.add("QUERY: "+ppst.toString(),HEADER);
            ppst.execute();
            return true;
        }catch (SQLException e){
            log.add("Failed to add address",HEADER);
            log.add("QUERY FAILED: "+e.getMessage(),HEADER+ "E!!");
            return false;
        }
    }
    //---------------------------------FUNCTIONS FOR HASHTAG TABLE
    /**
     * Database.add_hashtagT(Tick_HashtagT to_add)
     * @param to_add
     * @return boolean
     * @throws SQLException
     * Function adds hashtag table
     */
    public boolean add_hashtagT(Tick_HashtagT to_add) throws SQLException{
        log.add("Adding new Hashtag Table",HEADER);
        String query = "INSERT INTO HASHTAG_TABLE\n" +
                       "(owner_id,hashtag_table_name,hashtag_table_note)\n" +
                       "VALUES\n" +
                       "(?,?,?);";
        PreparedStatement ppst = con.prepareStatement(query);
        
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
            log.add("QUERY: "+ppst.toString(),HEADER);
            ppst.execute();
            return true;
        }catch (SQLException e){
            log.add("Failed to add Hashtag Table",HEADER);
            log.add("QUERY FAILED: "+e.getMessage(),HEADER+ "E!!");
            return false;
        }
    }
    //---------------------------------FUNCTIONS FOR TAGS
    /**
     * Database.add_tag(Tick_Tag to_add)
     * @param to_add
     * @return
     * @throws SQLException 
     * Function thats add tag
     */
    public boolean add_tag(Tick_Tag to_add) throws SQLException{
        log.add("Adding new tag",HEADER);
        String query = "INSERT INTO TAG\n" +
                       "(owner_id,hashtag_table_id,tag_name,tag_note)\n" +
                       "VALUES\n" +
                       "(?,?,?,?);";
        PreparedStatement ppst = con.prepareStatement(query);
        
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
            log.add("QUERY: "+ppst.toString(),HEADER);
            ppst.execute();
            return true;
        }catch (SQLException e){
            log.add("Failed to add tag",HEADER);
            log.add("QUERY FAILED: "+e.getMessage(),HEADER+ "E!!");
            return false;
        }
    }
    //----------------------------------FUNCTIONS FOR CATEGORY
    /**
     * Database.add_category(Tick_Category to_add)
     * @param to_add
     * @return
     * @throws SQLException 
     */
    public boolean add_category(Tick_Category to_add) throws SQLException{
        log.add("Adding new category",HEADER);
        String query = "INSERT INTO CATEGORY\n" +
                       "(owner_id,category_name,category_note)\n" +
                       "VALUES\n" +
                       "(?,?,?);";
        PreparedStatement ppst = con.prepareStatement(query);
        
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
            log.add("QUERY: "+ppst.toString(),HEADER);
            ppst.execute();
            return true;
        }catch (SQLException e){
            log.add("Failed to add category",HEADER);
            log.add("QUERY FAILED: "+e.getMessage(),HEADER+ "E!!");
            return false;
        }
    }
    //------------------------------FUNCTIONS FOR NOTE
    /**
     * Database.add_note(Tick_Note to_add)
     * @param to_add
     * @return
     * @throws SQLException 
     * Functions for adding data to database
     */
    public boolean add_note(Tick_Note to_add) throws SQLException{
        log.add("Adding new note",HEADER);
        String query = "INSERT INTO NOTE\n" +
                       "(owner_id,note_content,setting1,setting2,setting3)\n" +
                       "VALUES\n" +
                       "(?,?,?,?,?);";
        PreparedStatement ppst = con.prepareStatement(query);
        
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
            log.add("QUERY: "+ppst.toString(),HEADER);
            ppst.execute();
            return true;
        }catch (SQLException e){
            log.add("Failed to add note",HEADER);
            log.add("QUERY FAILED: "+e.getMessage(),HEADER+ "E!!");
            return false;
        }
    }
    //--------------------------------------FUNCTIONS FOR SCENE
    /**
     * Database.add_scene(Tick_Scene to_add)
     * @param to_add
     * @return boolean
     * @throws SQLException
     * Function for adding scene
     */
    public boolean add_scene(Tick_Scene to_add) throws SQLException{
        log.add("Adding new scene",HEADER);
        String query = "INSERT INTO SCENE\n"
                + "(hashtag_table_id,place_id,owner_id,category_id,scene_name,scene_note)\n"
                + "VALUES\n"
                + "(?,?,?,?,?,?);";
        PreparedStatement ppst = con.prepareStatement(query);
        
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
            log.add("QUERY: "+ppst.toString(),HEADER);
            ppst.execute();
            return true;
        }catch (SQLException e){
            log.add("Failed to add scene",HEADER);
            log.add("QUERY FAILED: "+e.getMessage(),HEADER+ "E!!");
            return false;
        }
    }
    
    /**
     * Database.stats()
     * @return ArrayList
     * @throws SQLException
     * Function returns lines to show of stats from database
     * like number of items etc.
     */
    public ArrayList<String> stats() throws SQLException{
        ArrayList<String> to_ret = new ArrayList<>();
        
        to_ret.add("Logged user: "+ logged.owner_login);
        to_ret.add("User id: "+ Integer.toString(logged.owner_id));
        
        String[] names = new String[] {"TICK","PLACE","ADDRESS","CATEGORY","HASHTAG_TABLE","TAG","SCENE"};
        
        for(String name : names){
            String query = "SELECT COUNT(*) FROM ";
            query = query + name + ";";
            PreparedStatement ppst = con.prepareStatement(query);
            
            try{
                ResultSet act_rs = ppst.executeQuery();
                
                if ( act_rs.next() ){
                    to_ret.add(name + " -> "+Integer.toString(act_rs.getInt("COUNT(*)")));
                }

            }catch(SQLException e){
                log.add("Failed to get stats ("+e.toString(),HEADER+"E!!!");
                System.out.println(ppst.toString());
                System.out.println(e.toString());
                return null;
            }
        }
        return to_ret;
    }
}

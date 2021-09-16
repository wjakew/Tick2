/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import com.jakubwawak.database.Database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *Function for storing options
 * @author jakubwawak
 */
public class Options {
    /**
     *  CREATE TABLE CONFIGURATION
        (
        configuration_id INT AUTO_INCREMENT PRIMARY KEY,
        owner_id INT,
        sum_entries INT,
        *       ret codes: ( number of logins )
        debug INT,          -- setting for log saving
        *       ret codes: ( 1 - debug is on / 0 - debug is off ) 
        conf2 VARCHAR(40),  -- welcome screen ( set what user see when logged )
        *       ret codes: ( scenes/scene_id | ticks | lists/list_id | blank)
        conf3 INT,          -- auto check of new shares ( 0 / 1)
        *       ret codes: ( 1 - auto load shares | 0 - opposite )
        conf4 VARCHAR(40),  -- fast login
        *       ret codes: ( x - user login | 'blank' - fast login not configured )
        conf5 VARCHAR(40),  -- free
        conf6 VARCHAR(40),  -- free
        conf7 VARCHAR(40),  -- free
        CONSTRAINT fk_configuration FOREIGN KEY (owner_id) REFERENCES OWN(owner_id)
        );
     */
    
    final String version = "v1.0.3";
    final String HEADER = "OPTIONS "+version;
    public Database database;
    
    boolean internal_fail;
    
    // data fields
    public int debug;
    public String welcome_screen;
    int sum_entries;
    public int auto_shares;
    public String auto_login;
    public String debug_log;
    
    public Options(Database database) throws SQLException{
        
        this.database = database;
        internal_fail = false;
        
        debug = -1;
        welcome_screen = null;
        auto_login = "";
        sum_entries = -1;
        auto_shares = -1;
    }
    
    /**
     * Options.run()
     * @return int
     * @throws SQLException 
     * Main function - prepares object to run
     * ret codes:
     * 1 - configuration made and loaded
     * 0 - configuration loaded
     */
    public int run() throws SQLException{
        if ( check_configuration() ){
            update_fast_login();
            load();
            database.log.add("Configuration found. Set.",HEADER);
            return 0;
        }
        else{
            database.log.add("Configuration for user not found. Making new.",HEADER);
            make_configuration();
            load();
            return 1;

        }
    }
    
    /**
     * Options.check_configuration()
     * @return boolean
     * @throws SQLException
     * Function checks if user has configuration
     */
    boolean check_configuration() throws SQLException{
        String query = "SELECT * from CONFIGURATION where owner_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1, database.logged.owner_id);
        
        try{
            ResultSet rs = ppst.executeQuery();
            return rs.next();
        }catch(SQLException e){
            database.log.add("Failed to check configuration ("+e.toString()+")",HEADER+"E!!!");
            return false;
        }
    }
    
    /**
     * Options.make_configuration()
     * @return boolean
     * @throws SQLException
     * Functions loads into database main configruation of the program
     */
    boolean make_configuration() throws SQLException{
        String query = "INSERT INTO CONFIGURATION\n" +
                       "(owner_id,sum_entries,debug,conf2,conf3,conf4,conf5,conf6,conf7)\n" +
                       "VALUES\n" +
                       "(?,?,?,?,?,?,?,?,?);";
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        
        /**
        sum_entries INT,
        *       ret codes: ( number of logins )
        debug INT,          -- setting for log saving
        *       ret codes: ( 1 - debug is on / 0 - debug is off ) 
        conf2 VARCHAR(40),  -- welcome screen ( set what user see when logged )
        *       ret codes: ( scenes/scene_id | ticks | lists/list_id | blank)
        conf3 INT,          -- auto check of new shares ( 0 / 1)
        *       ret codes: ( 1 - auto load shares | 0 - opposite )
        conf4 VARCHAR(40),  -- fast login
        *       ret codes: ( x - user login | 'blank' - fast login not configured )
        conf5 VARCHAR(40),  -- debug log
        *       ret codes: ( '1' - print login to screen | '0' - no printing 
        conf6 VARCHAR(40),  -- free
        conf7 VARCHAR(40),  -- free
         */
        ppst.setInt(1,database.logged.owner_id);
        ppst.setInt(2, 1);
        ppst.setInt(3, 1);
        ppst.setString(4,"blank");
        ppst.setInt(5,0);
        ppst.setString(6,"");
        ppst.setString(7,"1");
        ppst.setString(8,"");
        ppst.setString(9,"");
        database.log.add("Query : ("+ppst.toString()+")",HEADER);
        
        try{
            ppst.execute();
            return true;
        }catch(SQLException e){
            database.log.add("Failed to make configuration ("+e.toString()+")",HEADER+"E!!!");
            return false;
        }
    }
    
    /**
     * Options.update_password(String old_password, String new_password)
     * @param old_password
     * @param new_password
     * @return int
     * @throws SQLException 
     * Function for updating user password
     * ret codes:
     * 1 - password updated
     * 0 - old password failed to check
     * -1 - database problem
     */
    int update_password(String old_password, String new_password) throws SQLException{
        
        if ( database.check_password(database.logged, old_password)){
            if(database.change_password(database.logged, new_password)){
                return 1;
            }
            return -1;
        }
        return 0;
    }
    
    
    /**
     * Options.load_on_startup()
     * @return boolean
     * @throws SQLException
     * Loades on statup data from the database to the object
     * Function used at initzialization of the object
     */
    boolean load() throws SQLException{
        /**
        sum_entries INT,
        *       ret codes: ( number of logins )
        debug INT,          -- setting for log saving
        *       ret codes: ( 1 - debug is on / 0 - debug is off ) 
        conf2 VARCHAR(40),  -- welcome screen ( set what user see when logged )
        *       ret codes: ( scenes/scene_id | ticks | lists/list_id | blank)
        conf3 INT,          -- auto check of new shares ( 0 / 1)
        *       ret codes: ( 1 - auto load shares | 0 - opposite )
        conf4 VARCHAR(40),  -- fast login
        *       ret codes: ( x - user login | 'blank' - fast login not configured )
        conf5 VARCHAR(40),  -- debug log
        *       ret codes: ( '1' - print login to screen | '0' - no printing 
        conf6 VARCHAR(40),  -- free
        conf7 VARCHAR(40),  -- free
         */
        String query = " SELECT * FROM CONFIGURATION where owner_id = ?";
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1, database.logged.owner_id);
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            if ( rs.next() ){
                // looping on options in CONFIGURATION table on database
                
                debug = rs.getInt("debug");
                welcome_screen = rs.getString("conf2");
                sum_entries = rs.getInt("sum_entries");
                auto_shares = rs.getInt("conf3");
                auto_login = rs.getString("conf4");
                debug_log = rs.getString("conf5");
                // here add more of functionalities stored in the database
                database.log.add("Options data loaded successfully",HEADER);
                return true;
            }
            else{
                database.log.add("Failed to load Options data from database",HEADER);
                internal_fail = true;
                return false;
            }
            
            
        }catch(SQLException e){
            internal_fail = true;
            database.log.add("Failed load on startup ("+e.toString()+")",HEADER);
            return false;
        } 
    }
    
    /**
     * Options.show_data()
     * @return ArrayList
     * Function returns data stored in the object
     */
    ArrayList<String> show_data() throws SQLException{
        load();
        ArrayList<String> to_ret = new ArrayList<>();
        
        to_ret.add(HEADER);
        to_ret.add("Actual state of options:");
        
        to_ret.add("Debug : "+Integer.toString(debug));
        if ( debug == 1){
            to_ret.add("         - logs going to be stored at program src");   
        }
        else{
            to_ret.add("         - logs are not stored");
        }
        to_ret.add("Debug log: "+debug_log);
        to_ret.add("Welcome screen: "+welcome_screen);
        to_ret.add("Number of entries: "+Integer.toString(sum_entries));
        to_ret.add("Auto share: "+Integer.toString(auto_shares));
        
        if ( auto_shares == 1){
            to_ret.add("        - program is going to auto add shared ticks to your data");
        }
        else{
            to_ret.add("        - program is not gonna add tick to your data without your permission");
        }
        if ( auto_login.equals("blank") ){
            to_ret.add("Fast login not configured");
        }
        else{
            to_ret.add("Fast login set ( "+auto_login+")");
        }
            
        return to_ret;
    }
//---------------------------------------MANAGING DATA ON DATABASE
     /**
     * Options.update_user_logins(Tick_User user)
     * @param user
     * @throws SQLException 
     */
    public void update_user_logins(Tick_User user) throws SQLException{
        String query = "UPDATE CONFIGURATION SET sum_entries = sum_entries + 1 WHERE owner_id = ?;";
        
        database.log.add("Updating sum_entries value on database...", HEADER);
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1, user.owner_id);
        ppst.execute();
    }
    
    
    /**
     * Options.get_view_option()
     * @return String
     * @throws SQLException
     * Returns value of conf2 row in database
     */
    String get_view_option() throws SQLException{
        String query = " SELECT conf2 from CONFIGURATION where owner_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1,database.logged.owner_id);
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            if ( rs.next() ){
                database.log.add("conf2 returning",HEADER);
                return rs.getString("conf2");
            }
            database.log.add("conf2 data not found",HEADER+"E!!!");
            return null;
        }catch(SQLException e){
            database.log.add("Failed to reach data ("+e.toString()+")",HEADER+"E!!!");
            return null;
        }   
    }
    
    /**
     * Options.update_view_option(String option)
     * @param option
     * @return boolean
     * @throws SQLException
     * Function updates view data in database
     */
    boolean update_view_option(String option) throws SQLException{
        
        String query = "UPDATE CONFIGURATION SET conf2 = ? where owner_id = ?;";
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setString(1, option);
        ppst.setInt(2,database.logged.owner_id); 
        
        try{
            ppst.execute();
            database.log.add("Updated view option = "+option,HEADER);
            return true;
        }catch(SQLException e){
            database.log.add("Failed to update view option ("+e.toString()+")",HEADER+"E!!!");
            return false;
        }
    }
    
    /**
     * Options.get_debug()
     * @return int
     * @throws SQLException
     * Function returns debug info from database
     */
    int get_debug() throws SQLException{
        
        String query = "SELECT debug from CONFIGURATION where owner_id = ?;";
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1,database.logged.owner_id);
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            if ( rs.next() ){
                return rs.getInt("debug");
            }
            database.log.add("Failed to get debug info from user configuration ",HEADER+"E!!!");
            return -1;
        }catch(SQLException e){
            database.log.add("Failed to read debug info from database",HEADER+"E!!!");
            return -2;
        }
    }
    
    /**
     * Function for loading debug log
     * @return Integer
     * @throws SQLException 
     */
    int get_debug_log() throws SQLException{
        String query = "SELECT conf5 from CONFIGURATION where owner_id = ?;";
        
        try{
            PreparedStatement ppst = database.con.prepareCall(query);
            
            ppst.setInt(1,database.logged.owner_id);
            
            ResultSet rs = ppst.executeQuery();
            
            if ( rs.next() ){
                return Integer.parseInt(rs.getString("conf5"));
            }
            return -1;
        }catch(SQLException e){
            database.log.add("Failed to get database log data ("+e.toString(),HEADER+"E!!!");
            return -2;
        }
    }
    
    /**
     * Function for updating debug log info
     * @param debug_log
     * @return Boolean
     * @throws SQLException 
     */
    boolean update_debug_log(int debug_log) throws SQLException{
        String query = "UPDATE CONFIGURATION SET conf5 = ? where owner_id = ?";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1,debug_log);
        ppst.setInt(2,database.logged.owner_id);
        
        try{
            ppst.execute();
            return true;
        }catch(SQLException e){
            database.log.add("Failed to update debug log info ("+e.toString()+")",HEADER+"E!!!");
            return false;
        } 
    }
    
    /**
     * Options.update_debug(int debug)
     * @param debug
     * @return boolean
     * @throws SQLException 
     * Function for updating debug info
     */
    boolean update_debug(int debug) throws SQLException{
        String query = "UPDATE CONFIGURATION SET debug = ? where owner_id = ?";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1,debug);
        ppst.setInt(2,database.logged.owner_id);
        
        try{
            return ppst.execute();
        }catch(SQLException e){
            database.log.add("Failed to update debug info ("+e.toString()+")",HEADER+"E!!!");
            return false;
        } 
    }
    
    /**
     * Options.get_fast_login()
     * @return String
     * @throws SQLException
     * Function returns fast login data
     */
    String get_fast_login() throws SQLException{
        String query = "SELECT conf4 from CONFIGURATION where owner_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1,database.logged.owner_id);
        
        try{
            ResultSet act_rs = ppst.executeQuery();
            
            if ( act_rs.next() ){
                return act_rs.getString("conf4");
            }
            return null;
            
        }catch(SQLException e){
            database.log.add("Failed to get conf4 ( fast login data ) ("+e.toString()+")",HEADER+"E!!!");
            return null;
        }
    }
    
    /**
     * Options.update_fast_login(String data)
     * @param data
     * @return boolean
     * @throws SQLException
     * Function for updating fast login data
     */
    boolean update_fast_login() throws SQLException{
        String query = "UPDATE CONFIGURATION SET conf4 = ? where owner_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setString(1,database.logged.owner_login);
        ppst.setInt(2,database.logged.owner_id);
        
        try{
            ppst.execute();
            return true;
        }catch(SQLException e){
            database.log.add("Failed to update fast login data ("+e.toString()+")",HEADER+"E!!!");
            return false;
        }
    }
    boolean clear_fast_login() throws SQLException{
        String query = "UPDATE CONFIGURATION SET conf4 = 'blank' where owner_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);

        ppst.setInt(1,database.logged.owner_id);
        
        try{
            return ppst.execute();
        }catch(SQLException e){
            database.log.add("Failed to update fast login data ("+e.toString()+")",HEADER+"E!!!");
            return false;
        }
    }
}

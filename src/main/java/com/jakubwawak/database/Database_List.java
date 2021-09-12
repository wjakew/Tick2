/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.tick2.Tick_List;
import com.jakubwawak.tick2.Tick_Tick;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *Object for list database link implementation
 * @author jakubwawak
 */
public class Database_List {
    
    Database database;
    
    
    /**
     * Main constructor
     * @param database 
     */
    public Database_List(Database database){
        this.database = database;
    }
    
    /**
     * Database_List.get_list_names()
     * @return ArrayList
     * @throws SQLException
     * Function for gathering small data of list
     */
    public ArrayList<String> get_list_names() throws SQLException{
        ArrayList<String> data_toRet = new ArrayList<>();
        String query = "SELECT * from LISTS where owner_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1, database.logged.owner_id);
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            while ( rs.next() ){
                Tick_List tl = new Tick_List(rs);
                data_toRet.add(tl.show_glance());
            }
            return data_toRet;
        }catch(SQLException e){
            database.log.add("Failed to load list names ( "+e.toString()+")","TICK LIST E!!!");
            return null;
        }
        
    }
    
    /**
     * Database_List.get_list(int list_id)
     * @param list_id
     * @return Tick_List
     * @throws SQLException
     * Function for getting data from list
     */
    public Tick_List get_list(int list_id) throws SQLException{
        String query = "SELECT * from LISTS where list_id = ? and owner_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1,list_id);
        ppst.setInt(2, database.logged.owner_id);
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            if ( rs.next () ){
                return new Tick_List(rs);
            }
            return null;
            
        }catch(SQLException e){
            database.log.add("Failed to get List object ("+e.toString()+")","TICK LIST E!!!");
            return null;
        }
    }
   
    /**
     * Database_List.load_tick_data(ArrayList<Integer>)
     * @param ids
     * @return ArrayList
     * @throws SQLException
     * Function for loading tick data from Arraylist
     */
    public ArrayList<String> load_tick_data(ArrayList<Integer> ids) throws SQLException{
        ArrayList<String> data = new ArrayList<>();
        String query = "SELECT * from TICK where tick_id = ?;";
        
        for(Integer tick_id : ids){
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ppst.setInt(1,tick_id);
            
            try{
                ResultSet rs = ppst.executeQuery();
                
                if ( rs.next() ){
                    Tick_Tick tt = new Tick_Tick(rs);
                    
                    tt.simple_show();
                    
                    data.add(tt.simple_show());
                    
                }
            }catch(SQLException e){
                database.log.add("Failed to load tick data from id ("+e.toString()+")","TICK LIST E!!!");
            }
        }
        return data; 
    }

    /**
     * Database_List.add_list(Tick_List to_add)
     * @param to_add
     * @return boolean
     * @throws SQLException
     * Function for adding list 
     */
    public boolean add_list(Tick_List to_add) throws SQLException{
        
        String query =  "INSERT INTO LISTS\n" +
                        "(owner_id,list_name,tick_list_id,list_date)\n" +
                        "VALUES\n" +
                        "(?,?,?,?);";
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1, database.logged.owner_id);
        ppst.setString(2,to_add.list_name);
        ppst.setString(3,to_add.tick_list_id);
        ppst.setString(4,to_add.list_date);
        
        try{
            ppst.execute();
            return true;
        }catch(SQLException e){
            database.log.add("Failed to add list ( "+e.toString(),"DATABASE LIST E!!!");
            return false;
        }
    }
    
    /**
     * Database_List.get_tick_list(int list_id)
     * @param list_id
     * @return String
     * @throws SQLException 
     * Function for getting list of ticks
     */
    public String get_tick_list(int list_id) throws SQLException{
        String query = "SELECT tick_list_id FROM LISTS where list_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1,list_id);
        System.out.println(ppst.toString());
        try{
            ResultSet rs = ppst.executeQuery();
            
            if(rs.next()){
                return rs.getString("tick_list_id");
            }
            else{
                return null;
            }  
        }catch(SQLException e ){
            database.log.add("Getting tick list occured problem ("+e.toString()+")","DATABASE LIST E!!!");
            return null;
        }
    }

    /**
     * Database_List.add_tick_to_list(int tick_id,int list_id)
     * @param tick_id
     * @param list_id
     * @return boolean
     * @throws SQLException 
     * Function for adding tick_id to list of ticks in list object
     */
    public boolean add_tick_to_list(int tick_id,int list_id) throws SQLException{
        
        if ( database.check_if_record_exists(tick_id, "tick")){
            String ticks = get_tick_list(list_id);
            
            if ( ticks != null && !check_tick_in_list(tick_id,list_id)){
                ticks = ticks + "," + Integer.toString(tick_id);
                String query = "UPDATE LISTS SET tick_list_id = ? where list_id = ?;";
                
                PreparedStatement ppst = database.con.prepareStatement(query);
                ppst.setString(1,ticks);
                ppst.setInt(2,list_id);
                
                try{
                    ppst.execute();
                    return true;
                }catch(SQLException e){
                    database.log.add("Failed adding tick to tick_list_id ("+e.toString()+")","DATABASE LIST E!!!");
                    return false;
                }
                
            }
            return false;
        }
        else{
            return false;
        }
    }
    
    /**
     * Database_List.delete_tick_from_list(int tick_id,int list_id)
     * @param tick_id
     * @param list_id
     * @return boolean
     * @throws SQLException
     * Function for deleting tick from list
     */
    public boolean delete_tick_from_list(int tick_id,int list_id) throws SQLException{
        if ( database.check_if_record_exists(list_id, "list")){
            
            Tick_List tl = new Tick_List(database.return_TB_collection(database.logged, "list", list_id));
            
            ArrayList<Integer> numbers = tl.understand_id();
            
            if ( numbers.contains(tick_id)){
                numbers.remove(numbers.indexOf(tick_id));
                String ret = ",";
                for(Integer n : numbers){
                    ret = ret + Integer.toString(n)+",";
                }
                
                String query = "UPDATE LISTS SET tick_list_id = ? where list_id = ?;";
                PreparedStatement ppst = database.con.prepareStatement(query);
                ppst.setString(1,ret);
                ppst.setInt(2,list_id);
                
                try{
                    ppst.execute();
                    return true;
                }catch(SQLException e){
                    database.log.add("Failed to delete tick id from list ("+e.toString()+")","DATABASE LIST E!!!");
                    return false;
                }
                
            }
        }
        database.log.add("User asked to find non existing list!","DATABASE LIST W!!!");
        return false;
    }
    
    /**
     * Database_List.check_tick_in_list(int tick_id,int list_id)
     * @param tick_id
     * @param list_id
     * @return boolean
     * @throws SQLException 
     * Checks if number is in list
     */
    public boolean check_tick_in_list(int tick_id,int list_id) throws SQLException{
        String tick_list = get_tick_list(list_id);
        String[] numbers = tick_list.split(",");
        
        for(String n : numbers){
            try{
                int nn = Integer.parseInt(n);

                if ( nn == tick_id){
                    return true;
                }
            }catch(NumberFormatException e){
                database.log.add("String is not a number ("+e.toString()+")","DATABASE LIST W!!!");
            }
            
        }
        return false;
    }
    
    /**
     * Database_List.delete_list(int list_id)
     * @param list_id
     * @return boolean
     * @throws SQLException 
     * Function for deleting list
     */
    public boolean delete_list(int list_id) throws SQLException{
        if ( database.check_if_record_exists(list_id, "list") ){
            Database_Garbage_Collector dgc = new Database_Garbage_Collector(database);
            return dgc.delete(list_id,"list_id","LISTS");
        }
        return false;
    }
}

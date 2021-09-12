/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import com.jakubwawak.database.Database;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *Object for storing List
 * @author jakubwawak
 */
public class Tick_List extends Tick_Element{
    
    /**
     *  list_id INT AUTO_INCREMENT PRIMARY KEY,
        owner_id INT,
        tick_list_id VARCHAR(100),
        list_name VARCHAR(50),
        list_date VARCHAR(50),
     */
    
    public int list_id;
    public int owner_id;
    public String tick_list_id;
    public String list_name;
    public String  list_date;
    
    // main constructor
    public Tick_List(){
        super("Tick_List");
        list_id = -1;
        owner_id = -1;
        tick_list_id = "";
        list_name = "";
        list_date = "";
        super.put_elements(wall_updater());
    }
    
    // constructor implementing Tick_Brick
    public Tick_List(ArrayList<Tick_Brick> to_add){
        super("Tick_List");
        list_id = to_add.get(0).i_get();
        owner_id = to_add.get(1).i_get();
        tick_list_id = to_add.get(2).s_get();
        list_name = to_add.get(3).s_get();
        list_date = to_add.get(4).s_get();
        super.put_elements(wall_updater());
    }
    
    // optional constructor with use of ResultSet
    public Tick_List(ResultSet to_add) throws SQLException{
        super("Tick_List");
        list_id = to_add.getInt("list_id");
        owner_id = to_add.getInt("owner_id");
        tick_list_id = to_add.getString("tick_list_id");
        list_name = to_add.getString("list_name");
        list_date = to_add.getString("list_date");
        super.put_elements(wall_updater());
    }
    
    /**
     * Tick_List.wall_updater()
     * @return ArrayList
     * Returns 'brick wall'
     */
    public ArrayList<Tick_Brick> wall_updater(){
        ArrayList<Tick_Brick> to_ret = new ArrayList<>();
        to_ret.add(new Tick_Brick(list_id));
        to_ret.add(new Tick_Brick(owner_id));
        to_ret.add(new Tick_Brick(tick_list_id));
        to_ret.add(new Tick_Brick(list_name));
        to_ret.add(new Tick_Brick(list_date));
        return to_ret;
    }
    /**
     * Tick_List.init_CUI()
     * Input for CUI
     */
    public void init_CUI(){
        Date ac = new Date();
        super.inter.interface_print("List Name:");
        list_name = super.inter.interface_get();
        list_date = ac.toString();
    }
    
    /**
     * Tick_List.understand_id()
     * @return ArrayList
     * Returns arraylist of ids from tick_list_id field
     */
    public ArrayList<Integer> understand_id(){
        ArrayList<Integer> to_ret = new ArrayList<>();
        
        String [] ids = tick_list_id.split(",");
        
        for(String number : ids){
            try{
                int num = Integer.parseInt(number);
                to_ret.add(num);
            }catch(Exception e){}
        }
        return to_ret;
    }
    
    /**
     * Tick_List.show_glance()
     * @return String
     * Showing simple information about list
     */
    public String show_glance(){
        return Integer.toString(list_id)+ ":" + list_name + " " + list_date + ":" + understand_id().toString();
    }
    
    /**
     * Tick_List.get_lines_to_show()
     * @return String
     * Show lines to show in UI
     */
    public ArrayList<String> get_lines_to_show(){
        ArrayList<String> to_ret = new ArrayList<>();
        to_ret.add(show_glance());
        return to_ret;
    }
    
    /**
     * Tick_List.get_all_info(Database database)
     * @param database
     * @return ArrayList
     * Function for showing all data of list
     */
    public ArrayList<String> get_all_info(Database database) throws SQLException{
        ArrayList<String> data_to_ret = new ArrayList<>();
        
        data_to_ret.add(this.list_name+"\n");
        data_to_ret.add(this.list_date+"\n");

        ArrayList<Integer> tick_ids = understand_id();
        
        String query = "SELECT * from TICK where tick_id = ?;";
        
        for(int id : tick_ids){
        
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ppst.setInt(1,id);
            
            try{
                ResultSet rs = ppst.executeQuery();
                
                if ( rs.next() ){
                    Tick_Tick tt = new Tick_Tick(rs);
                    
                    data_to_ret.add(tt.simple_show());
                    
                }
            }catch(SQLException e){
                database.log.add("Failed to get all info from List ( "+e.toString()+")","TICK LIST E!!!");
                return null;
            }
            
        }
        return data_to_ret;
    }
}

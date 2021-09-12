/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.tick2.Tick_Address;
import com.jakubwawak.tick2.Tick_Brick;
import com.jakubwawak.tick2.Tick_HashtagT;
import com.jakubwawak.tick2.Tick_Note;
import com.jakubwawak.tick2.Tick_Place;
import com.jakubwawak.tick2.Tick_Tag;
import com.jakubwawak.tick2.Tick_Tick;
import com.jakubwawak.tick2.Tick_User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *Object for linking data
 * @author jakubwawak
 */
public class Database_Link {
    final String version = "v1.0.1";
    final String HEADER = "DATABASE_LINK ("+version+")";
    
    /**
     * Tick_Own - > Tick_Address + 
     * Tick_Place - > Tick_Address + 
     * Tick_Tag - > Tick_HashtagT +
     */
    
    Database database;
    
    /**
     * Constructor
     * @param database 
     */
    public Database_Link(Database database){
        this.database = database;
        this.database.log.add("Linker is now ready",HEADER);
    }
    
    /**
     * Database_Link.link_user_address(Tick_User user ,Tick_Address address)
     * @param user
     * @param address
     * @return boolean
     * @throws SQLException 
     * Links user to address
     */
    public boolean link_user_address(Tick_User user ,Tick_Address address) throws SQLException{
        database.log.add("Trying to link address to user",HEADER);
        String query = "update OWN SET address_id = ? WHERE owner_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1, address.address_id);
        ppst.setInt(2, user.owner_id);
        try{
            ppst.execute();
            database.log.add("QUERY: "+ppst.toString(),HEADER);
            return true;
        }catch(SQLException e){
            database.log.add("Cannot link user to address ("+e.toString()+")", HEADER+" E!!!");
            return false;
        }
    }
    /**
     * Database_Link.link_place_address(Tick_Place place, Tick_Address address)
     * @param place
     * @param address
     * @return boolean
     * @throws SQLException 
     * Links place to address
     */
    public boolean link_place_address(Tick_Place place, Tick_Address address) throws SQLException{
        database.log.add("Trying to link address to place",HEADER);
        String query = "update PLACE SET address_id = ? WHERE place_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1, address.address_id);
        ppst.setInt(2, place.place_id);
        try{
            ppst.execute();
            database.log.add("QUERY: "+ppst.toString(),HEADER);
            return true;
        }catch(SQLException e){
            database.log.add("Cannot link place to address ( "+e.toString()+")", HEADER+" E!!!");
            return false;
        }
    }
    /**
     * Database_Link.link_tag_hashtagT(Tick_Tag tag, Tick_HashtagT table)
     * @param tag
     * @param table
     * @return boolean
     * @throws SQLException 
     * Links tag to hashtag table
     */
    public boolean link_tag_hashtagT(Tick_Tag tag, Tick_HashtagT table) throws SQLException{
        database.log.add("Trying to link hashtag table to tag",HEADER);
        String query = "UPDATE TAG set hashtag_table_id = ? WHERE tag_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1,table.hashtag_table_id);
        ppst.setInt(2, tag.tag_id);
        try{
            ppst.execute();
            database.log.add("QUERY: "+ppst.toString(),HEADER);
            return true;
        }catch(SQLException e){
            database.log.add("Cannot link place to address ("+e.toString()+")", HEADER+ " E!!!");
            return false;
        }
    }
    /**
     * Database_Link.link_tick_note(Tick_Tick tick_obj,Tick_Note,note_obj)
     * @param tick_obj
     * @param note_obj
     * @return boolean
     * @throws SQLException
     * Function for linking note to tick
     */
    public boolean link_tick_note(Tick_Tick tick_obj, Tick_Note note_obj) throws SQLException{
        database.log.add("Trying to link note to tick",HEADER);
        String query = "UPDATE TICK set note_id = ? where tick_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1,note_obj.note_id);
        ppst.setInt(2, tick_obj.tick_id);
        try{
            ppst.execute();
            database.log.add("QUERY : "+ppst.toString(),HEADER);
            return true;
        }catch(SQLException e){
            database.log.add("Failed to link note to tick ("+e.toString()+")",HEADER + " E!!!");
            return false;
        }
    }
    //----------------------------------functions for getting objects
    public Tick_Address get_object_address(int address_id) throws SQLException{
        String query = "SELECT * FROM ADDRESS WHERE address_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1, address_id);
        ResultSet rs = ppst.executeQuery();
        ArrayList<Tick_Brick> lists = database.return_tick_brick(rs,"address");
        return new Tick_Address(lists);
    }
    
    public Tick_Place get_object_place(int place_id) throws SQLException{
        String query = "SELECT * FROM PLACE WHERE place_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1, place_id);
        
        ResultSet rs = ppst.executeQuery();
        ArrayList<Tick_Brick> lists = database.return_tick_brick(rs,"place");
        return new Tick_Place(lists);
    }
    public Tick_Tag get_object_tag(int tag_id) throws SQLException{
        String query = "SELECT * FROM TAG WHERE tag_id = ?";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1, tag_id);
        
        ResultSet rs = ppst.executeQuery();
        ArrayList<Tick_Brick> lists = database.return_tick_brick(rs,"tag");
        return new Tick_Tag(lists);
    }
    public Tick_HashtagT get_object_hashtagT(int hashtag_table_id) throws SQLException{
        String query = "SELECT * FROM HASHTAG_TABLE WHERE hashtag_table_id = ?";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1, hashtag_table_id);
        ResultSet rs = ppst.executeQuery();
        ArrayList<Tick_Brick> lists = database.return_tick_brick(rs,"hashtag table");
        return new Tick_HashtagT(lists);
    }
    // functions for checking if data is linked
    /**
     * Tick_Own - > Tick_Address + 
     * Tick_Place - > Tick_Address + 
     * Tick_Tag - > Tick_HashtagT +
     */
    /**
     * Database_Link.check_link_place(int place_id)
     * @param place_id
     * @return boolean
     * @throws SQLException
     * Returns if place by given id is linked to the address
     */
    public boolean check_link_place(int place_id) throws SQLException{
        String query = "SELECT address_id from PLACE where place_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1, place_id);
        ResultSet rs = ppst.executeQuery();
        while (rs.next()){
            return rs.getInt("address_id") != 1;
        }
        return false;
    }
    /**
     * Database_Link.check_link_tag(int tag_id)
     * @param tag_id
     * @return boolean
     * @throws SQLException 
     * Returns if tag by given id is linked to the address
     */
    public boolean check_link_tag(int tag_id) throws SQLException{
        String query = "SELECT hashtag_table_id from TAG where tag_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setInt(1, tag_id);
        ResultSet rs = ppst.executeQuery();
        while (rs.next()){
            return rs.getInt("hashtag_table_id") != 1;
        }
        return false;
    }
}

/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.tick2.Tick_Category;
import com.jakubwawak.tick2.Tick_HashtagT;
import com.jakubwawak.tick2.Tick_Place;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;
import javax.swing.DefaultListModel;

/**
 *Object for storing more demanding methods to fill Swing components
 * @author jakubwawak
 */
public class Database_ComponentFiller {
    final String version = "v1.0.0";
    final String HEADER = "Database_ComponentFiller" + " " + version;
    Database database;
    
    /**
     * Constructor
     * @param database 
     */
    public Database_ComponentFiller(Database database){
        this.database = database;
    }
    
    /**
     * Function for translating arraylists to list models
     * @param data
     * @return DefaultListModel
     */
    public DefaultListModel translator(ArrayList<String> data){
        DefaultListModel dlm = new DefaultListModel();
        dlm.addAll(data);
        return dlm;
    }
    
    /**
     * Function for gathering data about object on the database
     * @param mode
     * @return ArrayList
     * @throws SQLException
     * modes:
     * category,place,hashtag table
     */
    public ArrayList<String> datagather_simpleview(String mode) throws SQLException{
        ArrayList<String> data_toRet = new ArrayList<>();
        String query = "";
        if (mode.equals("category")){
            query = "SELECT * from CATEGORY where owner_id = 1 or owner_id = ?;";
        }
        else if ( mode.equals("place")){
            query = "SELECT * from PLACE where owner_id = 1 or owner_id  = ?;";
        }
        else if ( mode.equals("hashtag table")){
            query = "SELECT * from HASHTAG_TABLE where owner_id = 1 or owner_id = ?;";
        }
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1,database.logged.owner_id);
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            while ( rs.next() ){
                
                if ( mode.equals("category")){
                    Tick_Category tc = new Tick_Category(rs);
                    data_toRet.add(tc.simple_view());
                }
                else if ( mode.equals("place")){
                    Tick_Place tp = new Tick_Place(rs);
                    data_toRet.add(tp.simple_view());
                }
                else if (mode.equals("hashtag table")){
                    Tick_HashtagT th = new Tick_HashtagT(rs);
                    data_toRet.add(th.simple_view());
                }
            }
        }catch(SQLException e){
            System.out.println(e.toString());
            database.log.add("Failed to gather simpleviews ( "+e.toString()+")",HEADER+" E!!!");
            return null;
        }
        
        
        if ( data_toRet.isEmpty()){
            data_toRet.add("Empty");
        }

        return data_toRet;
    }
    
}

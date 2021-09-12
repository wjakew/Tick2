/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;
import com.jakubwawak.gui.UI_Interface;
import com.jakubwawak.tick2.Tick_Brick;
import com.jakubwawak.tick2.Tick_User;
import com.jakubwawak.tick2.Container;
import com.jakubwawak.tick2.Tick_List;
import com.jakubwawak.tick2.Tick_Tick;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/**
 *Database viewer for main components
 * @author jakubwawak
 */
public class Database_Viewer {
    
    final String version = "v1.0.7";
    final String HEADER = "DATABASE_VIEWER ("+version+")"; 
    public String custom_query;
    /**
     * modes:
     *      1 - address
     *      2 - category
     *      3 - hashtag table
     *      4 - note
     *      5 - place
     *      6 - tag
     */
    
    // storage for main object info
    Tick_User logged;
    Database database;
    UI_Interface i;
    
    //
    String mode;
    
    /**
     * Main constructor
     * @param database
     * @param logged
     * @param mode 
     */
    public Database_Viewer(Database database,Tick_User logged,String mode){
        this.database = database;
        this.logged = logged;
        this.mode = mode;
        i = new UI_Interface();
        custom_query = null;
    }
    /**
     * Database_Viewer.make_view()
     * @return ArrayList
     * @throws SQLException 
     * Returns lines to show
     */
    public ArrayList<String> make_view() throws SQLException{
        if ( mode.equals("scene view") ){
            return view_scene_creator();
        }
        else if (mode.equals("list view")){
            return view_list_creator();
        }
        else{
            ResultSet actual= prepare_query();      // getting data from the base
            // based on the mode variable
            ArrayList<Tick_Brick> wall = prepare_tick_brick(actual);    // prepared universal storage object
            return get_lines(wall); // getting object from Tick_Brick collection
        }
    }
    /**
     * Database_Viewer.get_lines(ArrayList<Tick_Brick> to_get)
     * @param to_get
     * @return ArrayList
     * Returns lines to show
     */
    ArrayList<String> get_lines(ArrayList<Tick_Brick> to_get) throws SQLException{
        if ( mode.equals("tick_done")){
            mode = "tick";
        }
        Container obj = new Container (to_get,mode,database,logged);
        ArrayList<String> to_ret = obj.make_lines();
        if ( to_ret.isEmpty() ){
            to_ret.add("Empty");
        }
        return to_ret;
    }
    /**
     * Database_Viewer.prepare_query(String mode)
     * @return ResultSet
     * @throws SQLException 
     * Function prepares query for view
     */
    ResultSet prepare_query() throws SQLException{
        String query = "";
        if (this.mode.equals("address")){
            query = "SELECT * FROM ADDRESS;";
        }
        else if (this.mode.equals("category")){
            query = "SELECT * FROM CATEGORY where owner_id = ?;";
        }
        else if (this.mode.equals("hashtag table")){
            query = "SELECT * FROM HASHTAG_TABLE where owner_id = ?;";
        }
        else if (this.mode.equals("note")){
            query = "SELECT * FROM NOTE where owner_id = ?;";
        }
        else if (this.mode.equals("place")){
            query = "SELECT * FROM PLACE where owner_id = ?;";
        }
        else if (mode.equals("tag")){
            query = "SELECT * FROM TAG where owner_id = ?;";
        }
        else if (mode.equals("scene")){
            query = "SELECT * FROM SCENE where owner_id = ?;";
        }
        else if (mode.equals("tick")){
            query = "SELECT * FROM TICK where owner_id = ? and tick_done_id = 1;";
        }
        else if (mode.equals("tick_done")){
            query = "SELECT * FROM TICK where owner_id = ? and tick_done_id != 1;";
        }
        else if (mode.equals("lists")){
            query = "SELECT * FROM LISTS where owner_id = ?;";
        }
        
        // checking if custom query is not flagged
        if ( custom_query != null){
            query = custom_query;
        }

        PreparedStatement ppst = database.con.prepareStatement(query);
        if ( !mode.equals("address") && custom_query == null){
            ppst.setInt(1,logged.owner_id);
        }
        try{
            return ppst.executeQuery();
        }catch(SQLException e){
            database.log.add("Cant return ResultSet ("+e.toString()+")",HEADER);
            return null;
        }
    }
    /**
     * Database_Viewer.prepare_tick_brick(ResultSet rs)
     * @param rs
     * @return ArrayList
     * Function returns collection of Tick_Brick
     */
    ArrayList<Tick_Brick> prepare_tick_brick(ResultSet rs) throws SQLException{
        // metadata for number of columns
        ResultSetMetaData meta   = (ResultSetMetaData) rs.getMetaData();
        
        ArrayList<Tick_Brick> to_ret = new ArrayList<>();  // tick_brick to ret
        
        int index[] = {};                // array of int indexes
        
        if (this.mode.equals("address")){
            /**
             *  address_id INT AUTO_INCREMENT PRIMARY KEY,
                address_city VARCHAR(30),
                address_street VARCHAR (30),
                address_house_number INT,
                address_flat_number INT,
                address_postal VARCHAR(15),
                address_country VARCHAR(30)
             */
            index = new int[] {1,4,5};
            
        }
        else if (this.mode.equals("category")){
            /**
             *  category_id INT AUTO_INCREMENT PRIMARY KEY,
                owner_id INT,
                category_name VARCHAR(45),
                category_note VARCHAR(100),
             */
            index = new int[] {1,2};
            
        }
        else if (this.mode.equals("hashtag table")){
            /**
             *  hashtag_table_id INT AUTO_INCREMENT PRIMARY KEY,
                owner_id INT,
                hashtag_table_name VARCHAR(45),
                hashtag_table_note VARCHAR(100),
                * 
             */
            index = new int[] {1,2};
        }
        else if (this.mode.equals("note")){
            /**
             *  note_id INT AUTO_INCREMENT PRIMARY KEY,
                owner_id INT,
                note_content VARCHAR(100),
                setting1 VARCHAR(40),
                setting2 VARCHAR(40),
                setting3 VARCHAR(40),
             */
            index = new int[] {1,2};
        }
        else if (this.mode.equals("place")){
            /**
             *  place_id INT AUTO_INCREMENT PRIMARY KEY,
                owner_id INT,
                place_name VARCHAR(30),
                address_id INT,
             */
            index = new int[] {1,2,4};
            
        }
        else if (mode.equals("tag")){
            /**
             *  tag_id INT AUTO_INCREMENT PRIMARY KEY,
                owner_id INT,
                hashtag_table_id INT,
                tag_name VARCHAR(45),
                tag_note VARCHAR(100),
             */
            index = new int[] {1,2,3};
        }
        else if (mode.equals("scene")){
            /**
             *  scene_id INT AUTO_INCREMENT PRIMARY KEY,
                hashtag_table_id INT,
                place_id INT,
                owner_id INT,
                category_id INT,
                scene_name VARCHAR(30),
                scene_note VARCHAR(100),
             */
            index = new int[] {1,2,3,4,5};
        }
        else if (mode.equals("tick") || mode.equals("tick_done")){
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
                tick_priority INT,
             */
            index = new int[] {1,2,3,4,5,6,7,11};
        }
        else if (mode.equals("lists")){
                /**
                *  list_id INT AUTO_INCREMENT PRIMARY KEY,
                   owner_id INT,
                   tick_list_id VARCHAR(100),
                   list_name VARCHAR(50),
                   list_date VARCHAR(50),
                */
            index = new int[] {1,2};
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
            database.log.add("Tick_Brick Collection returns succesfully", HEADER); 
        }
        else{
            database.log.add("Tick_Brick Collection failed", HEADER);
        }
        return to_ret;
    }
    /**
     * Database_Viewer.slicer(String text)
     * @param text
     * @return String
     * Slices text 
     */
    String slicer(String text){
        if ( text.contains(":") ){
            return text.split(":")[1];
        }
        return text;
    }
    /**
     * Database_Viewer.view_scene_creator()
     * @return ArrayList
     * @throws SQLException 
     * Shows view of objects for making scenes
     */
    ArrayList<String> view_scene_creator() throws SQLException{
        mode = "hashtag table";
        ArrayList<String> hsh_array = get_lines(prepare_tick_brick(prepare_query()));
        mode = "place";
        ArrayList<String> plp_array = get_lines(prepare_tick_brick(prepare_query()));
        mode = "category";
        ArrayList<String> ctg_array = get_lines(prepare_tick_brick(prepare_query()));

        ArrayList<String> result = new ArrayList<>();
        result.add("                      HASHTAGS TABLES:");
        result.addAll(hsh_array);
        if (hsh_array.size() == 1){
            result.add("Empty");
        }
        result.add("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        result.add("                      PLACES:");
        result.addAll(plp_array);
        if (plp_array.size() == 1){
            result.add("Empty");
        }
        result.add("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        result.add("                      CATEGORIES:");
        result.addAll(ctg_array);
        if (ctg_array.size() == 1){
            result.add("Empty");
        }
        result.add("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        
        return result;
    }
    
    /**
     * Database_Viewer.view_list_creator()
     * @return ArrayList
     * @throws SQLException
     * Returns view for list creator
     */
    ArrayList<String> view_list_creator() throws SQLException{
        ArrayList<String> to_ret = new ArrayList<>();
        
        String query = "SELECT * FROM LISTS where owner_id = ?;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setInt(1,database.logged.owner_id);
        
        try{
            ResultSet rs = ppst.executeQuery();
            
            while (rs.next()){
                
                // major list info
                to_ret.add("List name: "+rs.getString("list_name"));
                to_ret.add("List id: "+Integer.toString(rs.getInt("list_id")));
                to_ret.add("List content:");

                Tick_List tl = new Tick_List(database.return_TB_collection(database.logged, "list", rs.getInt("list_id")));
                
                for( Integer n : tl.understand_id() ){
                    Tick_Tick tt = new Tick_Tick(database.return_TB_collection(database.logged, "tick", n));
                    to_ret.add("    "+tt.simple_show() + tt.done_label);
                }
                to_ret.add("----------");
            }
        }catch(SQLException e){
            database.log.add("Failed to load lists... ("+e.toString()+")","DATABASE VIEWER E!!!");
            return null;
        }
        return to_ret;
    }
}

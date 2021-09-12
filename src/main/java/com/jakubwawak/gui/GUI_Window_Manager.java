/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.gui;

import com.jakubwawak.database.Database_Garbage_Collector;
import com.jakubwawak.database.Database_List;
import com.jakubwawak.database.Database_Scene;
import com.jakubwawak.database.Database_Tick;
import com.jakubwawak.tick2.Tick_Tick;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextArea;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *Object for managing windows in the program
 * @author jakubwawak
 */
public class GUI_Window_Manager {
    final String version = "v.0.0.4";
    
    GUI_main_window window;
    DefaultListModel model_obj; // jList model object
    
    
    /**
     * Main constructor
     * @param window 
     */
    GUI_Window_Manager(GUI_main_window window){
        this.window = window;   
    }
    
    /**
     * Function for reloading filters in Tick card
     */
    void reload_filters_combobox() throws SQLException{
        DefaultComboBoxModel dcm = new DefaultComboBoxModel();
        
        dcm.addAll(window.database.get_all_elements());
        
        window.TICK_combobox_filter.setModel(dcm);
        log("Model for ComboBox filters updated");
        
    }
    //----------------------- functions for reloading components
    /**
     * Function for reloading list
     * @throws SQLException 
     */
    void reload_listlist() throws SQLException{
        Database_List dl = new Database_List(window.database);
        ArrayList<String> data_to_show = dl.get_list_names();
        
        model_obj = new DefaultListModel();
        for(String element : data_to_show ) {
            model_obj.addElement(element);
        }
        
        window.LIST_listlist.setModel(model_obj);
        log("Model for List list updated");
    }
    
    void reload_SCENElisttick(int scene_id) throws SQLException{
        //todo
        
    }
    
    /**
     * Function for reloading scenes
     * @throws SQLException 
     */
    void reload_listscenes() throws SQLException{
        Database_Scene ds = new Database_Scene(window.database);
        ArrayList<String> data_to_show = ds.get_glances();
        model_obj = new DefaultListModel();
        
        for(String element : data_to_show){
            model_obj.addElement(element);
        }
        window.SCENES_listscenes.setModel(model_obj);
        
    }
    
    /**
     * Function for loading data to JList object
     * @param object_to_load
     * @param data 
     */
    void load_list(JList object_to_load, ArrayList<String> data){
        DefaultListModel dlm = new DefaultListModel();
        
        for(String element: data){
            dlm.addElement(element);
        }
        
        object_to_load.setModel(dlm);
    }
    
    /**
     * Function for loading data to JTextArea
     * @param object_to_load
     * @param data 
     */
    void load_textarea(JTextArea object_to_load, ArrayList<String> data){
        String content = "";
        
        for(String line : data){
            content = content + line + "\n";
        }
        object_to_load.setText(content);
    }
    /**
     * Function for reloading data in jList ( TICK_list_ticklist )
     * @throws SQLException 
     * modes:
     * 0 - not done ticks
     * 1 - done ticks
     */
    void reload_ticklist(int mode,JList to_update) throws SQLException{
        // setting view handler
        Database_Tick dt = new Database_Tick(window.database);
        
        model_obj = new DefaultListModel(); // setting model
        
        for(String element : dt.view_simpleviews(mode)){
            model_obj.addElement(element);
        }
        
        to_update.setModel(model_obj);
        log("Model for Tick list updated");
    }
    /**
     * Function for reloading data in jList ( any jList )
     * @param data
     * @param object 
     */
    void reload_JList(ArrayList<String> data,JList object){
        DefaultListModel dlm = new DefaultListModel();
        
        for(String line : data){
            dlm.addElement(line);
        }
        
        object.setModel(dlm);
        
    }
    /**
     * Function fro reloading blank data to components
     * @param to_clear 
     */
    void reload_blank(JList to_clear){
        model_obj = new DefaultListModel();
        to_clear.setModel(model_obj);
        log("Reload blank object : "+to_clear.getName());
        
    }
    //------------------------- functions for implementing button presses
    /**
     * Reloads gui and init action for adding tick
     * @throws SQLException 
     * 
     */
    void buttonaction_addtick() throws SQLException{
        new GUI_addtick_window(window,true,window.database,null);
        reload_default_scene_tick();
    }
    /**
     * Implements edit of Tick element
     * @param tick_id
     * @throws SQLException 
     */
    void buttonaction_edittick(int tick_id) throws SQLException{
        Tick_Tick tt = new Tick_Tick(window.database.return_TB_collection(window.database.logged, "tick", tick_id));
        new GUI_addtick_window(window,true,window.database,tt);
        reload_default_scene_tick();
    }
    /**
     * Sets behaviour to mark done tick
     * @throws SQLException 
     */
    void buttonaction_markdone() throws SQLException{
        new GUI_markdone_window(window,true,window.database,window.TICK_list_selectedvalue);
        reload_default_scene_tick();
    }
    /**
     * Implements delete of Tick element
     * @throws SQLException 
     */
    void button_action_delete() throws SQLException{
        Database_Garbage_Collector dgc = new Database_Garbage_Collector(window.database);
        
        if(dgc.delete_tick(window.TICK_list_selectedvalue)){
            window.TICK_button_delete.setText("Deleted");
            window.TICK_button_delete.setEnabled(false);
            reload_default_scene_tick();
        }
    }
    /**
     * Implements coping to clipboard Tick data
     * @param tick_id
     * @return
     * @throws SQLException 
     */
    boolean button_action_toclipboard(int tick_id) throws SQLException{
        Database_Tick dt = new Database_Tick(window.database);
        String to_copy = "";
        ArrayList<String> data_to_copy = dt.view_tick(tick_id);
        
        for(String line: data_to_copy){
            to_copy = to_copy + line + "\n";
        }
        
        StringSelection data = new StringSelection(to_copy);
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        cb.setContents(data, data);
        
        return !data_to_copy.isEmpty();   
    }

    //----------------------------- functions for reloading window views
    /**
     * Function for reloading to defalut scene (SCENE)
     * @throws SQLException 
     */
    void reload_default_scene_scene() throws SQLException{
        reload_listscenes();
        window.SCENES_list_selectedvalue = -1;
        window.SCENES_listtick_selectedvalue = -1;
        window.SCENES_textarea_scenedata.setText("");
        window.SCENES_textarea_scenedata.setEditable(false);
        reload_blank(window.SCENES_listtick);
        
    }
    /**
     * Function of for reloading to default scene (LIST)
     * @throws SQLException 
     */
    void reload_default_scene_list() throws SQLException{
        
        window.LIST_listitems_selectedvalue = -1;
        window.LIST_listlist_selectedvalue = -1;
        window.LIST_listtick_selectedvalue = -1;
        
        window.LIST_button_deletelist.setText("Delete");
        window.LIST_button_sendasemail.setText("Send as email");
        window.LIST_button_edit.setText("Edit");
        window.LIST_button_copycontent.setText("Copy content");
        
        reload_listlist();
        window.LIST_button_addnewlist.setText("Add new list");
        reload_ticklist(0,window.LIST_listtick);
        reload_blank(window.LIST_listitems);
        
        window.LIST_listlist.setEnabled(true);
        
        window.LIST_button_addnewlist.setEnabled(true);
        
        window.LIST_button_addticktolist.setEnabled(false);
        window.LIST_button_copycontent.setEnabled(false);
        window.LIST_button_deletelist.setEnabled(false);
        window.LIST_button_deleteticklist.setEnabled(false);
        window.LIST_button_edit.setEnabled(false);
        window.LIST_button_sendasemail.setEnabled(false);
        window.LIST_textfield_listname.setVisible(false);
        window.LIST_textarea_listdet.setEditable(false);
    }
    /**
     * Function for reloading to default scene (TICK)
     * @throws SQLException 
     */
    void reload_default_scene_tick() throws SQLException{
        reload_ticklist(0,window.TICK_list_ticklist);
        reload_filters_combobox();
        window.TICK_button_delete.setText("Delete");
        window.TICK_button_delete.setEnabled(false);
        window.TICK_button_active_ticks.setText("Active Ticks");
        window.TICK_list_ticklist.clearSelection();
        window.TICK_list_selectedvalue = -1;
        window.TICK_button_addnewtick.setEnabled(true);
        window.TICK_button_edittick.setEnabled(false);
        window.TICK_button_sharetick.setEnabled(false);
        window.TICK_textarea_tickdetails.setEditable(false);
        window.TICK_button_unarchive.setVisible(false);
        window.TICK_button_unarchive.setEnabled(false);
        window.TICK_textarea_tickdetails.setText("");
        window.TICK_button_markdone.setEnabled(false);
        log("Tick scene reloaded");
    }
    /**
     * Function for reloading archved scene
     * @throws SQLException 
     */
    void reload_archived_scene_tick() throws SQLException{
        reload_ticklist(1,window.TICK_list_ticklist);
        window.TICK_button_delete.setEnabled(false);
        window.TICK_button_active_ticks.setText("Archived Ticks");
        window.TICK_button_addnewtick.setEnabled(false);
        window.TICK_list_ticklist.clearSelection();
        window.TICK_list_selectedvalue = -1;
        window.TICK_button_edittick.setEnabled(false);
        window.TICK_button_sharetick.setEnabled(false);
        window.TICK_textarea_tickdetails.setEditable(false);
        window.TICK_button_unarchive.setVisible(true);
        window.TICK_button_unarchive.setEnabled(false);
        window.TICK_textarea_tickdetails.setText("");
        window.TICK_button_markdone.setEnabled(false);
    }
    
    
    //----------------------- other functions
    /**
     * Function for loading textarea to database
     * @param to_load 
     */
    void load_textarea(ArrayList<String> to_load,JTextArea to_fill){
        String data = "";
        
        for(String line : to_load){
            data = data + line + "\n";
        }
        
        to_fill.setText(data);
    }
    
    /**
     * Behaviour on the end of the operation
     */
    void on_close(){
        System.out.print("\nWindow closed\n>");
        log("GUI closed");
    }
    /**
     * Function for loading choosen tick from database
     * @throws SQLException 
     */
    void tick_list_clicked() throws SQLException{
        if( window.TICK_list_selectedvalue != -1){
            if ( window.TICK_button_active_ticks.getText().equals("Active Ticks")){
                int tick_id = window.TICK_list_selectedvalue;
                Database_Tick dt = new Database_Tick(window.database);

                if ( dt.check_if_exists(tick_id) ){

                    ArrayList<String> to_view = dt.view_tick(tick_id);
                    load_textarea(to_view,window.TICK_textarea_tickdetails);
                    window.TICK_button_delete.setEnabled(true);
                    window.TICK_button_edittick.setEnabled(true);
                    window.TICK_button_sharetick.setEnabled(true);
                    window.TICK_button_markdone.setEnabled(true);
                }
            }
            else{
                int tick_id = window.TICK_list_selectedvalue;
                Database_Tick dt = new Database_Tick(window.database);

                if ( dt.check_if_exists(tick_id) ){

                    ArrayList<String> to_view = dt.view_tick(tick_id);
                    load_textarea(to_view,window.TICK_textarea_tickdetails);
                    window.TICK_button_edittick.setEnabled(false);
                    window.TICK_button_sharetick.setEnabled(false);
                    window.TICK_button_markdone.setEnabled(false);
                    window.TICK_button_unarchive.setEnabled(true);
                    window.TICK_button_unarchive.setVisible(true);
                    window.TICK_button_delete.setEnabled(false);
                }
            }
            
        }
    }
    /**
     * Function gets models for comboboxes
     * @param mode
     * @return DefaultComboBoxModel
     * modes:
     * category - returns name of elements in CATEGORY table
     * place - returns name of elements in PLACE table
     * hashtag table - returns name of elements in HASHTAG_TABLE table
     */
    DefaultComboBoxModel get_defaultcomboboxmodel(String mode,int option) throws SQLException{
        return new DefaultComboBoxModel(window.database.get_element_name(mode,option).toArray());
    }
    
    /**
     * Function for updating comboboxes
     * @param object_to_add
     * @param data 
     */
    void update_combobox(JComboBox object_to_add, ArrayList<String> data){
        DefaultComboBoxModel dlm = new DefaultComboBoxModel(data.toArray());
        object_to_add.setModel(dlm);
    }
    
    /**
     * Function for logging data
     * @param data 
     */
    void log(String data){
        window.database.log.add(data, "GUI WINDOW MANAGER "+version);
    }
    
}

/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.gui;

import com.jakubwawak.database.Database;
import com.jakubwawak.database.Database_Garbage_Collector;
import com.jakubwawak.database.Database_Link;
import com.jakubwawak.database.Database_List;
import com.jakubwawak.database.Database_Tick;
import com.jakubwawak.database.Database_Viewer;
import com.jakubwawak.database.Database_Watcher;
import com.jakubwawak.tick2.MDate_Object;
import com.jakubwawak.tick2.MDate_Parser;
import com.jakubwawak.tick2.MailSender;
import com.jakubwawak.tick2.Options;
import com.jakubwawak.tick2.Options_Viewer;
import com.jakubwawak.tick2.Share;
import com.jakubwawak.tick2.Tick_Address;
import com.jakubwawak.tick2.Tick_Brick;
import com.jakubwawak.tick2.Tick_Category;
import com.jakubwawak.tick2.Tick_HashtagT;
import com.jakubwawak.tick2.Tick_List;
import com.jakubwawak.tick2.Tick_Note;
import com.jakubwawak.tick2.Tick_Place;
import com.jakubwawak.tick2.Tick_Scene;
import com.jakubwawak.tick2.Tick_ShareObject;
import com.jakubwawak.tick2.Tick_Tag;
import com.jakubwawak.tick2.Tick_Tick;
import com.jakubwawak.tick2.Tick_User;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.mail.MessagingException;
 
/**
 *Console interface
 * @author jakubwawak
 */
public class CUI_Tick_Inteface {
    final String version = "v1.1.1";
    final String HEADER  = "CUI";
    boolean logged = false;
    Tick_User logged_user = null;
    
    int scene_selected = -1;
    int list_selected = -1;
    Options options;
    
    boolean run = true;
    Date actual_date = null;
    
    UI_Interface ui;
    Database database;
    Share tso;
    
    /**
     * Main constructor
     * @param database
     * @throws SQLException 
     */
    public CUI_Tick_Inteface(Database database) throws SQLException{
        this.database = database;
        ui = new UI_Interface();
        actual_date = new Date();
    }
    
    /**
     * CUI_Tick_Interface.run()
     * @throws SQLException 
     * Main run function of the interface
     */
    public void run() throws SQLException, MessagingException, IOException{
        welcome_screen();
        
        // welcome prompt
        while ( welcome_menu());    // setting the login or register option

        // login loop
        while ( !login_prompt() );  // in login_prompt method is closing the procedure
        
        // if we are here login was succesful
        
        CUI_startup_procedure();
    
        while ( run ){  // main loop of the program
            // showing main menu
            ui.interface_print("------------------------------------------------");
            options.run();
            String user_input = ui.interface_get();
            CUI_logic(user_input);   
        }
    }
    /**
     * CUI_Tick_Interface.CUI_startup_procedure()
     * Function for maintainting things to do on the startup
     */
    void CUI_startup_procedure() throws SQLException{
        ui.interface_print("Loading options data..");
        if ( options.run() == 1 ){
            ui.interface_print("Options record made and loaded");
        }
        else{
            ui.interface_print("Options data loaded");
            ui.interface_print("------------------------------------------------");
            if ( options.welcome_screen.equals("tick")){
                Database_Viewer dv = new Database_Viewer(database,database.logged,"tick");
                show_arraylist(dv.make_view());
                
            }
            else if ( options.welcome_screen.equals("scene")){
                Database_Viewer dv = new Database_Viewer(database,database.logged,"scene");
                show_arraylist(dv.make_view());
            }
            else if ( options.welcome_screen.equals("list")){
                Database_Viewer dv = new Database_Viewer(database,database.logged,"list");
                show_arraylist(dv.make_view());
            }
            else{
                ui.interface_print("No welcome screen set");
            }
            ui.interface_print("------------------------------------------------");
            // checking expiration dates of tick
            Database_Watcher dw = new Database_Watcher(database);
            
            if(dw.check_tick_expiration_date().size() > 0){
                List<Integer> tick_ids = dw.check_tick_expiration_date();
                Database_Tick dt = new Database_Tick(database);
                ui.interface_print("Found expired Ticks!");
                for (int index : tick_ids){
                    show_arraylist(dt.view_tick(index));
                }
            }
            else{
                ui.interface_print("No new expired ticks");
            }
        }
    }
    /**
     * CUI_Tick_Interface.CUI_logic(String user_input)
     * @param user_input
     * @throws SQLException 
     * Main logic of the program
     */
    void CUI_logic(String user_input) throws SQLException, MessagingException, IOException{
        List<String> words = Arrays.asList(user_input.split(" ")); 
        
        OUTER:
        for (String word : words) {
            // exit
            switch (word) {
                case "exit":
                    close();
                    break;
                case "help":
                    CUI_FUN_help(words);
                    break OUTER;
                case "add":
                    CUI_FUN_add(words);
                    break OUTER;
                case "me":
                    CUI_FUN_me(words);
                    break OUTER;
                case "show":
                    CUI_FUN_show(words);
                    break OUTER;
                case "link":
                    CUI_FUN_link(words);
                    break OUTER;
                case "scene":
                    CUI_FUN_scene(words);
                    break OUTER;
                case "tick":
                    CUI_FUN_tick(words);
                    break OUTER;
                case "delete":
                    CUI_FUN_delete(words);
                    break OUTER;
                case "lists":
                    CUI_FUN_lists(words);
                    break OUTER;
                case "share":
                    CUI_FUN_share(words);
                    break OUTER;
                case "options":
                    Options_Viewer ov = new Options_Viewer(options,ui,database);
                    ov.run();
                    break OUTER;
                case "clip":
                    CUI_FUN_clip(words);
                    break OUTER;
                case "clean":
                    CUI_FUN_clean(words);
                    break OUTER;
                case "gui":
                    ui.interface_print("Graphical user interface is starting...");
                    new GUI_main_window(database);
                    ui.interface_print("User interface started.");
                    break OUTER;
                case "again":
                    ui.interface_print("Executing : "+ui.last_string);
                    CUI_logic(ui.last_string);
                    break OUTER;
                case "resources-manager":
                    new GUI_manageresources_window(database);
                    break OUTER;
                default:
                    ui.interface_print("Wrong command");
                    echo_CUI_interface(words.toString());
                    break;
            }
        }
    }
    
    /**
     * CUI_Tick_Interface.login_prompt()
     * @return boolean
     * @throws SQLException 
     * Shows login prompt
     */
    boolean login_prompt() throws SQLException{
        ui.interface_print("Login into program...");
        ui.interface_print("User Login: ");
        String user_login = ui.interface_get();
        ui.interface_print("Password: ");
        String user_password = ui.get_password();
        
        // ending login procedure
        if (user_login.equals("exit") || user_password.equals("exit")){
            ui.interface_print("Login procedure suspended");
            close();
            System.exit(0);
        }
        
        logged_user = database.user_login(user_login, user_password);
        
        if ( logged_user != null){
            ui.interface_print("Logged!");
            tso = new Share(this.database);
            options = new Options(this.database);
            options.update_user_logins(logged_user);
            return true;
        }
        ui.interface_print("Wrong password or login.");
        return false;
    }
    /**
     * CUI_Tick_Interface.echo_module(String input)
     * @param input 
     * Function for showing echo 
     */
    void echo_CUI_interface(String input){
        ui.interface_print("echo: "+input);
        ui.interface_print("last string: "+ui.last_string);
        ui.interface_print("len: "+ Integer.toString(input.split(" ").length));
    }
    /**
     * CUI_Tick_Interface.close()
     * @throws SQLException 
     * Function closing program
     */
    void close() throws SQLException{
        if (database != null){
            database.close();
            database.log.add(ui.history);
        }
        database.log.add("CUI Closed", HEADER);
        ui.interface_print("Program exited");
        run = false;
    }
    
    /**
     * CUI_Tick_Interface.welcome_screen()
     * Shows welcome screen
     */
    void welcome_screen(){
        ui.interface_print("Tick Console User Interface (TCUI version "+version+")");
    }
    
    /**
     * CUI_Tick_Interface.welcome_menu()
     * @return
     * @throws SQLException 
     * Menu for login and registration
     */
    boolean welcome_menu() throws SQLException{
        ui.interface_print("1 - Login | 2 - Register | 3 - Exit | 4 - Info");
            String dec = ui.interface_get();
            if ( ui.last_und == 1){ // it's an int
                
                if (ui.last_input == 1){    // go next to login
                     return false;
                }
                else if (ui.last_input == 2){   // go to register
                    ui.interface_print("Welcome in Tick registration!");
                    Tick_User to_register = new Tick_User();
                    to_register.init_CUI();
                    database.register_user(to_register);
                    return false;
                }
                else if ( ui.last_input == 3){
                    close();
                    System.exit(0);
                }
                else if ( ui.last_input == 4){
                    info_screen();
                    return true;
                }
                else{
                    ui.interface_print("Wrong option");
                    return true;
                }
            }
            ui.interface_print("You have to enter the number of option");
            return true;
    }
    /**
     * CUI_Tick_Interface.info_screen()
     * Screen for showing info about the creators of the program
     */
    void info_screen(){
        ui.interface_print("Tick info: ");
        ui.interface_print("Made by JAKUB WAWAK");
        ui.interface_print("May 2021 - all rights reserved");
        ui.interface_print("Program for getting shit done");
        ui.interface_print("kubawawak@gmail.com");
    }
    
    //----------------------------------------FUNCTIONS OF THE INTERFACE
    /**
     * CUI_Tick_Inteface.help_screen(List<String> addons)
     * @param addons 
     * Shows help for the user
     */
    void CUI_FUN_help(List<String> addons){
        // help
        if ( addons.size() == 1){
            ui.interface_print("Help for the program: ");
            ui.interface_print("-----------------------------------------------------------");
            ui.interface_print("tick");
            ui.interface_print("    ( without arguments shows active ticks )");
            ui.interface_print("    - arch ( shows archived ticks )");
            ui.interface_print("    - listview ( shows ticks categorized by lists )");
            ui.interface_print("    - add ( add simple tick reminder )");
            ui.interface_print("    - expired ( shows expired tick)");
            ui.interface_print("    - clean ( deletes archived ticks ) ");
            ui.interface_print("tick option /tick_id/");
            ui.interface_print("    - link |  /place/ | /address/ | /hashtag_table/ | /category/ | /note/ ");
            ui.interface_print("          ( links tick to the choosen object ) ");
            ui.interface_print("    - mark | /done/ | or - done");
            ui.interface_print("          ( marks tick and gives it new atribute )");
            ui.interface_print("    - delete ( delete tick ) ");
            ui.interface_print("    - det ( shows details of the tick ) ");
            ui.interface_print("    - def  ( clearing links, setting default )");
            ui.interface_print("    - clip ( copies content of tick to clipboard )");
            ui.interface_print("    - serial ( shows serialized tick data )");
            ui.interface_print("    - unarch ( allows to 'undone' tick )");
            ui.interface_print("    - piro ( sets priority )");
            ui.interface_print("    - date ( sets date of tick execution )");
            ui.interface_print("    - note ( adds note to tick )");
            ui.interface_print("");
            ui.interface_print("                                          /eg. tick clip 1/");
            ui.interface_print("-----------------------------------------------------------");
            ui.interface_print("gui");
            ui.interface_print("    ( without arguments starts graphical interface )");
            ui.interface_print("-----------------------------------------------------------");
            ui.interface_print("again");
            ui.interface_print("    ( without arguments loads latest input to the user console ) ");
            ui.interface_print("-----------------------------------------------------------");
            ui.interface_print("clip");
            ui.interface_print("    ( without arguments shows last input )");
            ui.interface_print("    -again ( executes last user input again )");
            ui.interface_print("    -copy  ( copies to clipboard last user input ) ");
            ui.interface_print("                                        /eq. clip again/");
            ui.interface_print("-----------------------------------------------------------");
            ui.interface_print("share");
            ui.interface_print("    ( without arguments shows active shares to load )");
            ui.interface_print("    -load ( loads shares to your account )");
            ui.interface_print("    -check ( finds if is any new ticks shared by other users )");
            ui.interface_print("    -tick /tick_id/ ( shares tick by given id) ");
            ui.interface_print("                                         /eg. share tick 1/");
            ui.interface_print("-----------------------------------------------------------");
            ui.interface_print("add ");
            ui.interface_print("    - place | address | hashtable | tag | category | note ");
            ui.interface_print("");
            ui.interface_print("                                         /eg. add category/");
            ui.interface_print("-----------------------------------------------------------");
            ui.interface_print("delete ");
            ui.interface_print("    - place | address | hashtable | tag | category | note /data_id/");
            ui.interface_print("        ( delete object by given id ) ");
            ui.interface_print("");
            ui.interface_print("                                         /eg. delete place/");
            ui.interface_print("-----------------------------------------------------------");
            ui.interface_print("clean ");
            ui.interface_print("        ( deletes not used data like not linked notes or archived ticks )");
            ui.interface_print("-----------------------------------------------------------");
            ui.interface_print("show ");
            ui.interface_print("    - place | address | hashtable | tag | category | note ");
            ui.interface_print("");
            ui.interface_print("                                           /eg. show place/");
            ui.interface_print("-----------------------------------------------------------");
            ui.interface_print("link ");
            ui.interface_print("    - adrplp /address_id/ /place_id/   ( address to place )");
            ui.interface_print("    - taghsh    /tag_id/ /hashtag_table_id/( hashtag table to tag )");
            ui.interface_print("");
            ui.interface_print("                                     /eg. link adrplp 1 1/");
            ui.interface_print("-----------------------------------------------------------");
            ui.interface_print("scene ");
            ui.interface_print("    ( without parameters show active scenes )");
            ui.interface_print("    - add    ( inits scene maker ) ");
            ui.interface_print("    - delete ( delete scene by id )");
            ui.interface_print("    -/scene_id/ select ( shows tick in scene )");
            ui.interface_print("    -/scene_id/ copy ( copies ticks to clipboard )");
            ui.interface_print("    -/scene_id/ mail ( sends scene via e-mail ) ");
            ui.interface_print("");
            ui.interface_print("                                       /eg. scene 1 select/");
            ui.interface_print("-----------------------------------------------------------");
            ui.interface_print("lists ");
            ui.interface_print("    ( without parameters show lists of ticks )");
            ui.interface_print("    -/list_id/ delete ( delete list by given id )");
            ui.interface_print("    -/tick_id/ ticka ( adding one tick to list )");
            ui.interface_print("    -/list_id/ tickd ( deleting tick from list )");
            ui.interface_print("    -/list_id/ ( shows details of lists )");
            ui.interface_print("    -/list_id/ mail ( sends list with details of tick ) ");
            ui.interface_print("");
            ui.interface_print("                                        /eg. lists 1 ticka/");
            ui.interface_print("-----------------------------------------------------------");
            ui.interface_print("share ");
            ui.interface_print("    ( without parameters show share )");
            ui.interface_print("    -his ( shows history of shares ) ");
            ui.interface_print("    -load ( loads shared ticks )");
            ui.interface_print("    -check ( checks if there is a new tick to add ) ");
            ui.interface_print("share tick /tick_id/ ");
            ui.interface_print("    ( share tick to the other user )");
            ui.interface_print("                                         /eg. share tick 1/");
            ui.interface_print("-----------------------------------------------------------");
            ui.interface_print("options ");
            ui.interface_print("    ( manages main functionalities of the Tick program ) ");
            ui.interface_print("-----------------------------------------------------------");
            ui.interface_print("me ");
            ui.interface_print(" ( without parameters shows account )"); 
            ui.interface_print(" ( more functionality moved to options )");
            ui.interface_print("-----------------------------------------------------------");
            ui.interface_print("resources-manager");
            ui.interface_print(" ( window manager for managing data in program )");
            ui.interface_print("-----------------------------------------------------------");
        }
        // help add
        else if (addons.size() == 2 && addons.contains("add")){
            ui.interface_print("Help for add ");
            ui.interface_print("    - place | address | hashtable | tag | category | note ");
        }
        // help me
        else if (addons.size() == 2 && addons.contains("me")){
            ui.interface_print("Help for me ");
            ui.interface_print(" ( without parameters shows account )"); 
            ui.interface_print(" ( more functionality moved to options )");
        }
        // help show
        else if (addons.size() == 2 && addons.contains("show")){
            ui.interface_print("Help for show ");
            ui.interface_print("    - place | address | hashtable | tag | category | note ");
        }
        //help link
        else if (addons.size() == 2 && addons.contains("link")){
            ui.interface_print(" Help for link ");
            ui.interface_print("    Linking is used for connecting two information");
            ui.interface_print("    - adrplp /address_id/ /place_id/   ( address to place )");
            ui.interface_print("    - hshtag    /tag_id/ /hashtag_table_id/( hashtag table to tag )");
        }
        //help scene
        else if (addons.size() == 2 && addons.contains("scene")){
            ui.interface_print("Help for scene ");
            ui.interface_print("    ( without parameters show active scenes )");
            ui.interface_print("    - add    ( inits scene maker ) ");
            ui.interface_print("    - delete ( delete scene by id )");
            ui.interface_print("    - select /scene_id/ ( shows tick in scene )");
            ui.interface_print("    - copy /scene_id/ ( copies ticks to clipboard )");
        }
        // help tick
        else if ( addons.size() == 2 && addons.contains("tick")){
            ui.interface_print("Help for tick");
            ui.interface_print("    ( without arguments shows active ticks )");
            ui.interface_print("    - arch ( shows archived ticks )");
            ui.interface_print("    - listview ( shows ticks categorized by lists )");
            ui.interface_print("    - add ( add simple tick reminder )");
            ui.interface_print("    - expired ( shows expired tick)");
            ui.interface_print("tick option /tick_id/");
            ui.interface_print("    - link |  /place/ | /address/ | /hashtag_table/ | /category/ | /note/ ");
            ui.interface_print("          ( links tick to the choosen object ) ");
            ui.interface_print("    - mark | /done/ | or - done");
            ui.interface_print("          ( marks tick and gives it new atribute )");
            ui.interface_print("    - delete ( delete tick ) ");
            ui.interface_print("    - det ( shows details of the tick ) ");
            ui.interface_print("    - def  ( clearing links, setting default )");
            ui.interface_print("    - clip ( copies content of tick to clipboard )");
            ui.interface_print("    - serial ( shows serialized tick data )");
            ui.interface_print("    - unarch ( allows to 'undone' tick )");
            ui.interface_print("    - piro ( sets priority )");
            ui.interface_print("    - date ( sets date of tick execution )");
            ui.interface_print("    - note ( adds note to tick )");
            ui.interface_print("");
            ui.interface_print("                                          /eg. tick clip 1/");
        }
        //help gui
        else if ( addons.size() == 2 && addons.contains("gui")){
            ui.interface_print("    ( without arguments starts graphical interface )");
            ui.interface_print(" WARNING early alpha - not all features are ready and stable ");
        }
        // help delete
        else if ( addons.size() == 2 && addons.contains("delete")){
            ui.interface_print("Help for delete ");
            ui.interface_print("    - place | address | hashtable | tag | category | note /data_id/");
            ui.interface_print("        ( delete object by given id ) ");
        }
        // help lists
        else if ( addons.size() == 2 && addons.contains("lists")){
            ui.interface_print("Help for lists ");
            ui.interface_print("    ( without parameters show lists of ticks )");
            ui.interface_print("    -delete /list_id/ ( delete list by given id )");
            ui.interface_print("    -ticka /tick_id/ ( adding one tick to list )");
            ui.interface_print("    -tickd /list_id/ ( deleting tick from list )");
            ui.interface_print("    -/list_id/ ( shows details of lists )");
            ui.interface_print("    -/list_id/ mail ( sends list with details of tick ) ");
        }
        // help options
        else if ( addons.size() == 2 && addons.contains("options")){
            ui.interface_print("Help for options ");
            ui.interface_print("    ( manages main functionalities of the Tick program ) ");
            ui.interface_print("    More info in the options ");
        }
        // help share
        else if ( addons.size() == 2 && addons.contains("share")){
            ui.interface_print("Help for share ");
            ui.interface_print("    ( without parameters show share )");
            ui.interface_print("    -his ( shows history of shares ) ");
            ui.interface_print("    -load ( loads shared ticks )");
            ui.interface_print("    -check ( checks if there is a new tick to add ) ");
            ui.interface_print("share tick /tick_id/ ");
            ui.interface_print("    ( share tick to the other user )");
            ui.interface_print("                                         /eg. share tick 1/");
        }
        // help clean
        else if ( addons.size() == 2 && addons.contains("clean")){
            ui.interface_print("Help for clean ");
            ui.interface_print("        ( deletes not used data like not linked notes or archived ticks )");
        }
        // help clip
        else if ( addons.size() == 2 && addons.contains("clean")){
            ui.interface_print("Help for clip ");
            ui.interface_print("    ( without arguments shows last input )");
            ui.interface_print("    -again ( executes last user input again )");
            ui.interface_print("    -copy  ( copies to clipboard last user input ) ");
            ui.interface_print("                                        /eq. clip again/");
        }         
        else{
            ui.interface_print("Wrong option");
        }
    }
    /**
     * CUI_Tick_Interface.CUI_FUN_clean(List<String> addons)
     * @param addons 
     * Function implementing cleaning of the database
     */
    void CUI_FUN_clean(List<String> addons) throws SQLException{
        ui.interface_print("Are you sure to delete not used data? (y/n)");
        ui.interface_get();
        if ( ui.last_string.equals("y")){
            Database_Garbage_Collector dgc = new Database_Garbage_Collector(database);
            dgc.collect_garbage(1);
        }
        else{
            ui.interface_print("Cancelled");
        }
    }
    /**
     * CUI_Tick_Interface.CUI_FUN_add(List<String> addons)
     * @param addons
     * @throws SQLException 
     * Function for adding data to the database
     */
    void CUI_FUN_add(List<String> addons) throws SQLException{
        //   +         +         +         +       +        +
        //  place | address | hashtable | tag | category | note 
        // add
        if ( addons.size() == 1 ){
            ui.interface_print("No additional arguments. See help ( help add ) ");
        }
        // add address
        else if ( addons.size() == 2 && addons.contains("address")){
            Tick_Address to_add = new Tick_Address();
            to_add.init_CUI();
            if ( database.add_address(to_add) ) {
                ui.interface_print("Address added");
                ui.interface_print("Given id: "+Integer.toString(database.get_last_id("ADDRESS")));
            }
            else{
                ui.interface_print("Something goes wrong");
            }
        }
        // add place
        else if ( addons.size() == 2 && addons.contains("place")){
            Tick_Place to_add = new Tick_Place();
            to_add.init_CUI();
            to_add.owner_id = logged_user.owner_id;
            if ( database.add_place(to_add)){
                ui.interface_print("Place added");
                ui.interface_print("Given id: "+Integer.toString(database.get_last_id("PLACE")));
            }
            else{
                ui.interface_print("Something goes wrong");
            }
        }
        // add hashtable
        else if ( addons.size() == 2 && addons.contains("hashtable")){
            Tick_HashtagT  to_add = new Tick_HashtagT();
            to_add.init_CUI();
            to_add.owner_id = logged_user.owner_id;
            to_add.put_elements(to_add.wall_updater());
            if (database.add_hashtagT(to_add)){
                ui.interface_print("Hashtag Table added");
                ui.interface_print("Given id: "+Integer.toString(database.get_last_id("HASHTAG_TABLE")));
            }
            else{
                ui.interface_print("Something goes wrong");
            }
        }
        // add tag
        else if ( addons.size() == 2 && addons.contains("tag")){
            Tick_Tag to_add = new Tick_Tag();
            to_add.init_CUI();
            to_add.owner_id = logged_user.owner_id;
            to_add.put_elements(to_add.wall_updater());
            if (database.add_tag(to_add)){
                ui.interface_print("Tag added");
                ui.interface_print("Given id: "+Integer.toString(database.get_last_id("TAG")));
            }
            else{
                ui.interface_print("Something goes wrong");
            }
        }
        // add category
        else if ( addons.size() == 2 && addons.contains("category")){
            Tick_Category to_add = new Tick_Category();
            to_add.init_CUI();
            to_add.owner_id = logged_user.owner_id;
            to_add.put_elements(to_add.wall_updater());
            if (database.add_category(to_add)){
                ui.interface_print("Category added");
                ui.interface_print("Given id: "+Integer.toString(database.get_last_id("CATEGORY")));
            }
            else{
                ui.interface_print("Something goes wrong");
            }
        }
        // add note
        else if ( addons.size() == 2 && addons.contains("note")){
            Tick_Note to_add = new Tick_Note();
            to_add.init_CUI();
            to_add.owner_id = logged_user.owner_id;
            to_add.put_elements(to_add.wall_updater());
            if (database.add_note(to_add)){
                ui.interface_print("Note added");
                ui.interface_print("Given id: "+Integer.toString(database.get_last_id("NOTE")));
            }
            else{
                ui.interface_print("Something goes wrong");
            }
        }
        else{
            ui.interface_print("Wrong option");
        }
    }
    /**
     * CUI_Tick_Interface.CUI_FUN_me(List<String> addons)
     * @param addons
     * @throws SQLException 
     * Function of showing and changing user stuff
     */
    void CUI_FUN_me(List<String> addons) throws SQLException{
        // me
        Database_Link dl = new Database_Link(database);
        if ( addons.size() == 1){
            ui.interface_print("Info about your account:");
            logged_user.show();
            if ( logged_user.address_id == 1){
                ui.interface_print("No address linked");
            }
            else{
                ui.interface_print("Linked address:");
                Tick_Address to_show = dl.get_object_address(logged_user.address_id);
                show_arraylist(to_show.get_lines_to_show());
            }
        }
        else{
            ui.interface_print("Wrong option");
        }
    }
    
    void show_arraylist(ArrayList<String> to_show){
        if ( to_show == null){
            ui.interface_print("Data : null");
        }
        if (to_show.size() == 1){
            ui.interface_print("Empty");
        }
        else{
            for(String line: to_show){
                ui.interface_print(line);
            }
        }
    }
    /**
     * CUI_Tick_Interface.CUI_FUN_show(List<String> addons)
     * @param addons
     * @throws SQLException 
     * Function for showing data from database
     */
    void CUI_FUN_show(List<String> addons) throws SQLException{
        /**
         *     +        +          +        +       + 
         * - place | address | hashtable | tag | category | note 
         */
        // show
        if ( addons.size() == 1){
            ui.interface_print("No additional arguments. See help ( help show )");
        }
        // show place
        else if ( addons.size() == 2 && addons.contains("place")){
            Database_Viewer view = new Database_Viewer(database,logged_user,"place");
            show_arraylist(view.make_view());
        }
        // show address
        else if ( addons.size() == 2 && addons.contains("address")){
            Database_Viewer view = new Database_Viewer(database,logged_user,"address");
            show_arraylist(view.make_view());
        }
        // show hashtable
        else if ( addons.size() == 2 && addons.contains("hashtable")){
            Database_Viewer view = new Database_Viewer(database,logged_user,"hashtag table");
            show_arraylist(view.make_view());
        }
        // show tag
        else if ( addons.size() == 2 && addons.contains("tag")){
            Database_Viewer view = new Database_Viewer(database,logged_user,"tag");
            show_arraylist(view.make_view());
        }
        // show category
        else if ( addons.size() == 2 && addons.contains("category")){
            Database_Viewer view = new Database_Viewer(database,logged_user,"category");
            show_arraylist(view.make_view());
        }
        // show note
        else if ( addons.size() == 2 && addons.contains("note")){
            Database_Viewer view = new Database_Viewer(database,logged_user,"note");
            show_arraylist(view.make_view());
        }
        // show scene
        else if ( addons.size() == 2 && addons.contains("scene")){
            Database_Viewer view = new Database_Viewer(database,logged_user,"scene");
            show_arraylist(view.make_view());
        }
        else{
            ui.interface_print("Wrong option");
        }
    }
    /**
     * CUI_Tick_Interface.CUI_FUN_link(List<String> addons)
     * @param addons 
     * Function for linking two objects of data
     */
    void CUI_FUN_link(List<String> addons) throws SQLException{
        /**
         * - adrplp /address_id/ /place_id/              ( address to place )
           - hshtag    /tag_id/ /hashtag_table_id/       ( hashtag table to tag )
         */
        Database_Link linker = new Database_Link(database);
        if ( addons.size() == 1){
            ui.interface_print("No additional arguments. See help (help link)");
        }
        // link adrplp /address_id/ /place_id/
        else if ( addons.size() == 4 && addons.contains("adrplp")){
            // check if records exists
            if ( database.check_if_record_exists(ui.numbers.get(0), "address") 
                    && database.check_if_record_exists(ui.numbers.get(1), "place")){
                int address_id = ui.numbers.get(0);
                int place_id = ui.numbers.get(1);
                // preparing objects
                
                Tick_Address to_link_address = linker.get_object_address(address_id);
                Tick_Place to_link_place = linker.get_object_place(place_id);
                
                if ( linker.link_place_address(to_link_place, to_link_address) ){
                    ui.interface_print("Link succesfull");
                }
                else{
                    ui.interface_print("Link occured a problem");
                }
            }
            else{
                ui.interface_print("One of the objects not exist");
            }
        }
        // hshtag    /tag_id/ /hashtag_table_id/
        else if ( addons.size() == 4 && addons.contains("hshtag")){
            if ( database.check_if_record_exists(ui.numbers.get(0), "tag") 
                    && database.check_if_record_exists(ui.numbers.get(1), "hashtag table")){
                int tag_id = ui.numbers.get(0);
                int hashtag_table_id = ui.numbers.get(1);
                
                // preparing objects
                Tick_Tag to_link_tag = linker.get_object_tag(tag_id);
                Tick_HashtagT to_link_hashtagT = linker.get_object_hashtagT(hashtag_table_id);         
                
                if ( linker.link_tag_hashtagT(to_link_tag, to_link_hashtagT) ){
                    ui.interface_print("Link succesfull");
                }
                else{
                    ui.interface_print("Link occured a problem");
                }
            }
            else{
                ui.interface_print("One of the objects not exist");
            }
        }
        
        else{
            ui.interface_print("Wrong option");
        }
    }
    /**
     * CUI_Tick_Interface.CUI_FUN_scene(List<String> addons)
     * @param addons 
     * Adding functionality of scene
     */
    void CUI_FUN_scene(List<String> addons) throws SQLException, MessagingException, IOException{
        // scene
        if (addons.size() == 1){
            ui.interface_print("Currently active scenes:");
            Database_Viewer view = new Database_Viewer(database,logged_user,"scene");
            show_arraylist(view.make_view());
        }
        // scene add
        else if (addons.size() == 2 && addons.contains("add")){
            ui.interface_print("Welcome in the scene creator: ");
            ui.interface_print("Input '0' if you don't want to use this categorization");
            ui.interface_print("Input '-1' if you want to cancel");
            Database_Viewer scene_v = new Database_Viewer(database,logged_user,"scene view");
            ArrayList<String> lines_to_show = scene_v.make_view();
 
            // showing lines from database viewer
            show_arraylist(lines_to_show);
            // adding scene
            Tick_Scene to_add = new Tick_Scene();
            to_add.owner_id = database.logged.owner_id;
            to_add.init_CUI();
            
            if ( !to_add.stop_CUI() ){
                
                if (to_add.check_integrity(database)){
                    try{
                    if ( database.add_scene(to_add) ){
                        ui.interface_print("Scene added");
                        ui.interface_print("Scene id:" +database.get_last_id("scene"));
                    }
                    else{
                        ui.interface_print("Scene adding occured a problem");
                    }
                    }catch(SQLException e){
                        ui.interface_print("");
                    }
                }
                else{
                    ui.interface_print("Wrong id of one of the objects");
                    ui.interface_print("Do you want to repair scene data?");
                    if ( approval_window("scene")){
                        try{
                            String ret = to_add.repair(database);   // repairing datastructure
                            if(!ret.equals("")){
                               ui.interface_print("Scene integrity (edited): "+ret);
                            }
                            else{
                                ui.interface_print("Scene integrity check: OK");
                            }
                        if ( database.add_scene(to_add) ){
                            ui.interface_print("Scene added");
                            ui.interface_print("Scene id: "+Integer.toString(database.get_last_id("SCENE")));
                            scene_selected = database.get_last_id("SCENE");
                        }
                        else{
                            ui.interface_print("Scene adding occured a problem");
                        }
                        }catch(SQLException e){
                            ui.interface_print("Failed adding scene");
                            database.log.add("Failed to add scene "+e.toString(),HEADER+"E!!!");
                        }
                    }
                    else{
                        ui.interface_print("Stopped");
                    }
                    
                }
            }
            else{
                ui.interface_print("Stopped");
            }
            
        }
        // scene delete
        else if ( addons.size() == 2 && addons.contains("delete")){
            ui.interface_print("Choose scene to delete:");
            Database_Viewer view = new Database_Viewer(database,logged_user,"scene");
            show_arraylist(view.make_view());
            String i = ui.interface_get();
            
            if ( ui.int_flag ){
                if ( database.check_if_record_exists(ui.last_input, "scene")){
                    Database_Garbage_Collector dgc = new Database_Garbage_Collector(database);
                    if ( approval_window("scene")){
                        dgc.delete_scene(ui.last_input);
                        ui.interface_print("Scene deleted");
                    }
                    else{
                        ui.interface_print("Cancelled");
                    }
                }
                else{
                    ui.interface_print("Wrong scene id");
                }
            }
            else{
                ui.interface_print("Wrong input");
            }
        }
        
        // scene /scene_id/ select
        else if ( addons.size() == 3 && addons.contains("select") && ui.check_existance_int(addons)!= -1 ){
            int scene_id = ui.last_input;
            scene_selected = scene_id;
            if ( database.check_if_record_exists(scene_id, "scene") ){
                // we found scene with this id
                //Tick_Scene to_categorize = new Tick_Scene(database.return_TB_collection(logged_user,"scene",ui.last_input));
                ArrayList<Tick_Brick> tb = database.return_TB_collection(logged_user,"scene",ui.last_input);
                if( tb != null){
                    Tick_Scene ts = new Tick_Scene(tb);
                    Database_Viewer dv = new Database_Viewer(database,database.logged,"tick");
                    
                    dv.custom_query = ts.query_creator();
                    ui.interface_print("Showing ticks by scene");
                    show_arraylist(dv.make_view());
                }
                else{
                    ui.interface_print("Failed to reach scene from database");
                }
                //ui.interface_print(to_categorize.query_creator());
            }
            else{
                ui.interface_print("Wrong scene id");
            }
        }
        
        // scene /scene_id/ copy
        else if( addons.size() == 3 && addons.contains("copy") && ui.check_existance_int(addons)!= -1 ){
            int scene_id = ui.last_input;
            
            if ( database.check_if_record_exists(scene_id, "scene")){
                // we found scene with this id
                //Tick_Scene to_categorize = new Tick_Scene(database.return_TB_collection(logged_user,"scene",ui.last_input));
                ArrayList<Tick_Brick> tb = database.return_TB_collection(logged_user,"scene",ui.last_input);
                if( tb != null){
                    Tick_Scene ts = new Tick_Scene(tb);
                    Database_Viewer dv = new Database_Viewer(database,database.logged,"tick");
                    
                    dv.custom_query = ts.query_creator();
                    ArrayList<String> lines = dv.make_view();
                    String content = "";
                    for(String line : lines){
                        content = content + line+"\n";  
                    }
                    content = content + " Ticks by "+database.logged.owner_login+"\n" + "TICK";
                    StringSelection data = new StringSelection(content);
                    Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                    cb.setContents(data,data);
                }
                else{
                    ui.interface_print("Failed to reach scene from database");
                }
                //ui.interface_print(to_categorize.query_creator());
            }
            else{
                ui.interface_print("Wrong scene id");
            }
        }
        
        // scene /scene_id/ mail
        else if( addons.size() == 3 && addons.contains("mail") && ui.check_existance_int(addons)!= -1 ){
            int scene_id = ui.last_input;
            if ( database.check_if_record_exists(scene_id, "scene")){
                // found scene
                ArrayList<Tick_Brick> tb = database.return_TB_collection(database.logged, "scene", scene_id);
                // loading Tick_Brick to make object
                
                if ( tb != null){ // checking if Tick_Brick collection isn't empty
                    Tick_Scene ts = new Tick_Scene(tb);
                    Database_Viewer dv = new Database_Viewer(database,database.logged,"tick");
                    
                    dv.custom_query = ts.query_creator();
                    ArrayList<String> lines = dv.make_view();
                    String content = "";
                    for(String line : lines){
                        content = content + line+"\n";  
                    }
                    
                    // content now contains scene details
                    
                    ui.interface_get_w_prompt("Input reciver e-mail address:");
                    
                    if ( ui.last_string.contains("@") && ui.size == 1){
                        MailSender ms = new MailSender("New things to do from "+database.logged.owner_login,content,ui.raw_input);
                        ms.run();
                    }
                    else{
                        ui.interface_print("Wrong e-mail address");
                    }
                }
            }
        }

        else{
            ui.interface_print("Wrong option");
        }
    }
    /**
     * CUI_Tick_Interface.CUI_Tick_tick(List<String> addons)
     * @param addons 
     * Function for using functionality tick
     */
    void CUI_FUN_tick(List<String> addons) throws SQLException{
        /**
         * ( without parameters show active ticks ) DONE
                - add  ( add simple tick reminder ) DONE
                - arch ( shows archived ticks ) DONE
            tick /tick_id/
                 - link |  /place/ | /address/ | /hashtag_table/ | /category/ | /note/ 
                    ( links tick to the choosen object )  DONE
                - mark | /done/ | or - done
                    ( marks tick and gives it new atribute ) DONE
                - delete ( delete tick )
                - det  ( shows details of the tick ) DONE
                - def  ( clearing links, setting default ) DONE
                
         */
        // tick
        if ( addons.size() == 1 ){
            ui.interface_print("Showing active ticks: ");
            Database_Viewer dv = new Database_Viewer(database,database.logged,"tick");
            show_arraylist(dv.make_view());
        }
        // tick add
        else if ( addons.size() == 2 && addons.contains("add")){
            Tick_Tick to_add = new Tick_Tick();
            to_add.init_CUI();
            to_add.owner_id = database.logged.owner_id;
            to_add.wall_updater();
            Database_Tick adder = new Database_Tick(database);
            
            if ( adder.add_tick(to_add) ){
                ui.interface_print("Tick added");
                ui.interface_print("Tick id: "+database.get_last_id("TICK"));
            }
            else{
                ui.interface_print("Error adding tick");
            }
        }
        // tick listview
        else if ( addons.size() == 2 && addons.contains("listview")){
            Database_Viewer dv = new Database_Viewer(database,database.logged,"lists");
            dv = new Database_Viewer(database,database.logged,"list view");
            ui.interface_print("Detailed view of lists: ");
            show_arraylist(dv.make_view());
        }
        // tick arch
        else if ( addons.size() == 2 && addons.contains("arch")){
            ui.interface_print("Showing inactive ticks: ");
            Database_Viewer dv = new Database_Viewer(database,database.logged,"tick_done");
            show_arraylist(dv.make_view());
        }
        // tick expired
        else if ( addons.size() == 2 && addons.contains("expired")){
            Database_Watcher dw = new Database_Watcher(database);
            
            if(dw.check_tick_expiration_date().size() > 0){
                List<Integer> tick_ids = dw.check_tick_expiration_date();
                Database_Tick dt = new Database_Tick(database);
                ui.interface_print("Found expired Ticks!");
                for (int index : tick_ids){
                    show_arraylist(dt.view_tick(index));
                }
            }
            else{
                ui.interface_print("No new expired ticks");
            }
        }
        // tick clean
        else if ( addons.size() == 2 && addons.contains("clean")){
            Database_Garbage_Collector dgc = new Database_Garbage_Collector(database);
            if ( approval_window("archived ticks")){
                dgc.collect_garbage(2);
            }
            else{
                ui.interface_print("Cancelled");
            }
        }
        // tick /tick_id/ unarch
        else if ( addons.size() == 3 && addons.contains("unarch") && ui.check_existance_int(addons)!= -1){
            ui.interface_print("You trying to unarchive tick and making it not done again");
            if ( approval_window("tick done")){
                Database_Tick handler = new Database_Tick(database);
                if(handler.unmark_done(ui.last_input)){
                    ui.interface_print("Done");
                }
                else{
                    ui.interface_print("Failed to 'undone' tick, check log");
                }
            }
            else{
                ui.interface_print("Cancelled");
            }
        }
        // tick /tick_id/ note
        else if ( addons.size() == 3 && addons.contains("note") && ui.check_existance_int(addons)!= -1){
            Database_Tick dt = new Database_Tick(database);
            int tick_id = ui.last_input;
            if ( dt.check_if_exists(tick_id)){
                
                Tick_Note tn = new Tick_Note();
                Tick_Tick tt;
                
                tn.init_CUI();
                tn.owner_id = database.logged.owner_id;
                tn.wall_updater();
                
                if ( database.add_note(tn) ){
                    ui.interface_print("Note added");
                    ui.interface_print("Note id: "+Integer.toString(database.get_last_id("NOTE")));
                    tn = new Tick_Note(database.return_TB_collection(database.logged, "note", database.get_last_id("NOTE")));
                    tt = new Tick_Tick(database.return_TB_collection(database.logged, "tick", tick_id));
                    
                    Database_Link dl = new Database_Link(database);
                    
                    if ( dl.link_tick_note(tt, tn) ){
                        ui.interface_print("Note linked");
                    }
                    else{
                        ui.interface_print("Failed to link note, check log");
                    }
                }
                
                else{
                    ui.interface_print("Failed to add note");
                }
            }
            else{
                ui.interface_print("Wrong id");
            }
            
        }
        // tick /tick_id/ date
        else if ( addons.size() == 3 && addons.contains("date") && ui.check_existance_int(addons)!= -1){
            ui.interface_print("You are adding date of execution for tick: ");
            Database_Tick dt = new Database_Tick(database);
            if (dt.check_if_exists(ui.last_input)){
                dt.view_tick(ui.last_input);
                ui.interface_get_date();

                MDate_Parser date_parser = new MDate_Parser(ui.last_string);
                
                MDate_Object date_obj = date_parser.ret_date_object();
                
                if (  date_obj != null ){
                    // now we have right date
                    if ( dt.set_end_date(date_obj.parse_object().toString(), ui.last_input) ){
                        ui.interface_print("End date updated");
                    }
                    else{
                        ui.interface_print("Failed to update data, check log");
                    }
                }
                else{
                    ui.interface_print("Failed to parse data");
                }
            }
            else{
                ui.interface_print("Tick didn't exist");
            }
            
            
        }
        // tick /tick_id/ piro
        else if ( addons.size() == 3 && addons.contains("piro") && ui.check_existance_int(addons)!= -1){
            int tick_id = ui.last_input;
            ui.interface_print("Set tick priority on scale from 1 to 10:");
            ui.interface_get();
            
            if ( ui.last_input >= 1 && ui.last_input <= 10){
                Database_Tick dt = new Database_Tick(database);
                
                if ( dt.check_if_exists(tick_id) ){
                    if ( dt.update_data(ui.last_input, tick_id, "tick_priority") ) {
                        ui.interface_print("Priority updated");
                    }
                    else{
                        ui.interface_print("Failed to update priority");
                    }
                }
                else{
                    ui.interface_print("Wrong tick id");
                }
            }
            else{
                ui.interface_print("Wrong number from scale");
            }
        }
        // tick /tick_id/ link /eg. place/
        else if ( addons.size() == 4 && addons.contains("link") && ui.check_existance_int(addons)!= -1){
            int tick_id = ui.last_input;
            Database_Tick linker = new Database_Tick(database);
            if ( linker.check_if_exists(ui.last_input)){
                // here we have checked if tick exists
                String [] keys = new String[] {"place","address","hashtag_table","category","note"};
                String mode = ui.check_existance(addons, keys);
                // showing viewer for database
                if (  mode != null ){
                    Database_Viewer view = new Database_Viewer(database,database.logged,mode);
                    show_arraylist(view.make_view());
                    ui.interface_get();
                    int index = ui.last_input;
                    
                    if ( database.check_if_record_exists(index, mode) ){
                        // we have correct id
                        ui.interface_print("Tick id found: "+tick_id);
                        ui.interface_print("Mode: "+ mode);
                        ui.interface_print("Object id: "+index);
                        if ( linker.update_data(index,tick_id,mode)){
                            ui.interface_print("Data linked ("+mode+")");
                        }
                        else{
                            ui.interface_print("Unable to link data. Error");
                        }
                    }
                    else{
                        ui.interface_print("Wrong id for the object");
                    }
                }
                else{
                    ui.interface_print("Wrong object to link");
                }
                
            }
            else{
                ui.interface_print("No tick with given id");
            }
        }
        // tick /tick_id/ serial
        else if ( addons.size() == 3 && addons.contains("serial") && ui.check_existance_int(addons)!= -1){
            ui.interface_print("Data for tick_id:"+Integer.toString(ui.last_input));
            Database_Tick handler = new Database_Tick(database);
            ui.interface_print(handler.get_serialization(ui.last_input));
        }
        // tick /tick_id/ det
        else if ( addons.size() == 3 && addons.contains("det") && ui.check_existance_int(addons)!= -1){
            Database_Tick shower = new Database_Tick(database);
            System.out.println(ui.last_input);
            if ( database.check_if_record_exists(ui.last_input, "tick") ){
                if ( shower.view_tick(ui.last_input) != null){
                    show_arraylist(shower.view_tick(ui.last_input));
                }
                else{
                    ui.interface_print("Failed loading tick details");
                }
                
            }
            else{
                ui.interface_print("Wrong tick id");
            }
        }
        //tick /tick_id/ clip
        else if ( addons.size() == 3 && addons.contains("clip") && ui.check_existance_int(addons)!= -1){
            Database_Tick shower = new Database_Tick(database);
            System.out.println(ui.last_input);
            if ( database.check_if_record_exists(ui.last_input, "tick") ){
                if ( shower.view_tick(ui.last_input) != null){
                    ArrayList<String> lines = shower.view_tick(ui.last_input);
                    String content = "TICK\n";
                    for(String line : lines){
                        content = content + line + "\n";
                    }
                    content = content + "by "+database.logged.owner_login;
                    StringSelection data = new StringSelection(content);
                 Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                 cb.setContents(data, data);
                 ui.interface_print("Copied to clipboard");
                }
                else{
                    ui.interface_print("Failed loading tick to clipboard");
                }
                
            }
            else{
                ui.interface_print("Wrong tick id");
            }
        }
        // tick /tick_id/ mark done / tick /tick_id/ done
        else if ( addons.size() >= 3 && addons.contains("done") && ui.check_existance_int(addons)!= -1){
            if ( database.check_if_record_exists(ui.last_input, "tick")){
                ui.interface_print("Any notes for marking done this tick?");
                String note = ui.interface_get();
                if ( note.isBlank() ){
                     note = "no note";
                }
                Database_Tick dt = new Database_Tick(database);
                if (dt.mark_done(note, ui.last_input)){
                    ui.interface_print("Tick marked as done");
                    ui.interface_print("You can see it in the archve");
                }
                else{
                    ui.interface_print("Failed to mark as done");
                }
                
            }
            else{
                ui.interface_print("Wrong tick id");
            }
        }
        // tick /tick_id/ def
        else if ( addons.size() == 3 && addons.contains("def") && ui.check_existance_int(addons) != -1){
            if (database.check_if_record_exists(ui.last_input, "tick")){
                ui.interface_print("Are you sure to set default data to tick? (y/n)");
                String ans = ui.interface_get();
                
                if ( ans.equals("y") ){
                    Database_Tick dt = new Database_Tick(database);
                    
                    if ( dt.make_default(ui.last_input) ){
                        ui.interface_print("Tick set to default");
                    }
                    else{
                        ui.interface_print("Failed to set to default");
                        database.log.add("Failed to set default tick data",HEADER+"E!!!");
                    }
                }
                else{
                    ui.interface_print("Cancelled");
                }
            }
            else{
                ui.interface_print("Wrong tick id");
            }
        }
        // tick /tick_id/ delete
        else if ( addons.size() == 3 && addons.contains("delete") && ui.check_existance_int(addons) != -1){
            if ( database.check_if_record_exists(ui.last_input, "tick") ){
                
                if(approval_window("tick")){
                    Database_Garbage_Collector dgc = new Database_Garbage_Collector(database);
                    if (dgc.delete_tick(ui.last_input)){
                        ui.interface_print("Tick deleted");
                    }
                    else{
                        ui.interface_print("Unable to delete tick");
                    }
                }
                else{
                    ui.interface_print("Cancelled");
                }
            }
            else{
                ui.interface_print("Wrong tick id");
            }
        }
        else{
            ui.interface_print("Wrong arguments for tick. See: help tick ");
        }
    }
    
    /**
     * CUI_Tick_Interface.approval_window(String option)
     * @param option 
     */
    boolean approval_window(String option){
        ui.interface_print("This operation cause to update your data and change some records.");
        ui.interface_print("Are you sure to continue?(y/n)");
        String ans = ui.interface_get();
        return ans.equals("y");
    }
    
    /**
     * CUI_Tick_Interface.CUI_FUN_delete(List<String> addons)
     * @param addons 
     * Implements functionality of deleting data
     */
    void CUI_FUN_delete(List<String> addons) throws SQLException{
        Database_Garbage_Collector dgc = new Database_Garbage_Collector(database);
        // delete
        if ( addons.size() == 1){
            ui.interface_print("No enough options. See help delete");
        }
        // delete address /data_id/
        else if ( addons.size() == 3 && addons.contains("address") && ui.check_existance_int(addons) != -1){
            
            if ( approval_window("address")){
                if (dgc.delete_address(ui.last_input)){
                    ui.interface_print("Data deleted");
                }
                else{
                    ui.interface_print("Failed to delete data");
                }
            }
            else{
                ui.interface_print("Canelled");
            }
        }
        // delete place /data_id/
        else if ( addons.size() == 3 && addons.contains("place") && ui.check_existance_int(addons) != -1){
            
            if ( approval_window("place") ){
                if ( dgc.delete_place(ui.last_input) ){
                    ui.interface_print("Data deleted");
                }
                else{
                    ui.interface_print("Failed to delete data");
                }
            }
            else{
                ui.interface_print("Cancelled");
            }
        }
        // delete category /data_id/
        else if (addons.size() == 3 && addons.contains("category") && ui.check_existance_int(addons) != -1){
            
            if (approval_window("category")){
                if ( dgc.delete_category(ui.last_input)){
                    ui.interface_print("Data deleted");
                }
                else{
                    ui.interface_print("Failed to delete data");
                }
            }
        }
        // delete tag /data_id/
        else if (addons.size() == 3 && addons.contains("tag") && ui.check_existance_int(addons) != -1){
            
            if (approval_window("tag")){
                if ( dgc.delete_tag(ui.last_input)){
                    ui.interface_print("Data deleted");
                }
                else{
                    ui.interface_print("Failed to delete data");
                }
            }
        }
        //delete hashtag_table /data_id/
        else if (addons.size() == 3 && addons.contains("hashtag_table") && ui.check_existance_int(addons) != -1){
            
            if (approval_window("hashtag table")){
                if ( dgc.delete_hashtag_table(ui.last_input)){
                    ui.interface_print("Data deleted");
                }
                else{
                    ui.interface_print("Failed to delete data");
                }
            }
        }
        // delete note /data_id/
        else if (addons.size() == 3 && addons.contains("note") && ui.check_existance_int(addons) != -1){
            
            if (approval_window("note")){
                if ( dgc.delete_note(ui.last_input)){
                    ui.interface_print("Data deleted");
                }
                else{
                    ui.interface_print("Failed to delete data");
                }
            }
        }
        else{
            ui.interface_print("Wrong options. See help delete");
        }
    }
    
    /**
     * CUI_Tick_Interface.CUI_FUN_lists(List<String> addons)
     * @param addons
     * @throws SQLException 
     * Implements functionality of lists
     */
    void CUI_FUN_lists(List<String> addons) throws SQLException, MessagingException, IOException{
        Database_Viewer dv = new Database_Viewer(database,database.logged,"lists");
        Database_List dl = new Database_List(database);
        // lists
        if ( addons.size() == 1){
            ui.interface_print("Actual Lists:");
            show_arraylist(dv.make_view());
        }
        // lists add
        else if ( addons.size() == 2 && addons.contains("add")){
            Tick_List to_add = new Tick_List();
            to_add.init_CUI();
            
            if ( dl.add_list(to_add) ){
                ui.interface_print("List added");
                ui.interface_print("List id: "+database.get_last_id("LISTS"));
            }
            else{
                ui.interface_print("Failed to add list");
            }
        }
        // lists delete /list_id/
        else if (addons.size() == 3 && addons.contains("delete") && ui.check_existance_int(addons) != -1){
            if ( database.check_if_record_exists(ui.last_input, "list") ){
                if ( dl.delete_list(ui.last_input) ){
                    if ( approval_window("list")){
                        ui.interface_print("List deleted");
                    }
                    else{
                        ui.interface_print("Cancelled");
                    }
                    
                }
                else{
                    ui.interface_print("Failed to delete list");
                }
            }
        }
        
        // lists ticka /tick_id/ ( adding one tick to list )
        else if (addons.size() == 3 && addons.contains("ticka") && ui.check_existance_int(addons) != -1){
            if ( database.check_if_record_exists(ui.last_input,"tick") ){
                // tick exists
                int tick_id = ui.last_input;
                ui.interface_print("Choose list:");
                show_arraylist(dv.make_view());
                String index = ui.interface_get();
                if ( database.check_if_record_exists(ui.last_input,"list") ){
                    // list exists
                    if(dl.add_tick_to_list(tick_id,ui.last_input)){
                        ui.interface_print("Tick added to list");
                    }
                    else{
                        if (dl.check_tick_in_list(tick_id, ui.last_input)){
                            ui.interface_print("Tick already in list");
                        }
                        else{
                            ui.interface_print("Failed to add tick to list");
                        }
                        
                    }
                }
            }
            else{
                ui.interface_print("Wrong tick id");
            }
        }
        // lists tickd /list_id/ ( deleting tick from list )
        else if (addons.size() == 3 && addons.contains("tickd") && ui.check_existance_int(addons) != -1){
            if ( database.check_if_record_exists(ui.last_input, "list")){
                int list_id = ui.last_input;
                ui.interface_print("Choose tick to delete:");
                dv = new Database_Viewer(database,database.logged,"list view");
                
                show_arraylist(dv.make_view());
                
                String choose = ui.interface_get();
                
                if ( !ui.blank ){
                    if ( database.check_if_record_exists(ui.last_input, "tick")){
                        if ( approval_window("tick")){
                            if (dl.delete_tick_from_list(ui.last_input, list_id) ){
                                ui.interface_print("Tick from list deleted");
                            }
                            else{
                                ui.interface_print("Failed to delete list");
                            }
                            
                        }
                        else{
                            ui.interface_print("Cancelled");
                        }
                    }
                    else{
                        ui.interface_print("Wrong tick id");
                    }
                }
                else{
                    ui.interface_print("Blank input");
                }
            }
            else{
                ui.interface_print("Wrong list id");
            }
        }
        // lists det /list_id/
        else if (addons.size() == 3 && addons.contains("det") && ui.check_existance_int(addons) != -1){
            dv = new Database_Viewer(database,database.logged,"list view");
            ui.interface_print("Detailed view of lists: ");
            show_arraylist(dv.make_view());
        }
        // lists mail /list_id/
        else if (addons.size() == 3 && addons.contains("mail") && ui.check_existance_int(addons) != -1){
            if ( database.check_if_record_exists(ui.last_input,"lists")){
                
                String email = ui.interface_get_w_prompt("Input e-mail address:");
                if ( email.contains("@")){
                    dv = new Database_Viewer(database,database.logged,"list view");
                    MailSender ms = new MailSender("List of things to do!",ui.convert_array(dv.make_view()),email);
                    
                    ms.run();
                    
                    ui.interface_print("List send");
                }
                else{
                    ui.interface_print("Wrong e-mail address");
                }
            }
            else{
                ui.interface_print("Wrong list id");
            }
        }
        else{
            ui.interface_print("Wrong arguments. See help lists");
        }
    }
    /**
     * CUI_Tick_Interface.CUI_FUN_share(List<String> addons)
     * @param addons 
     * Function for adding share functionality
     */
    void CUI_FUN_share(List<String> addons) throws SQLException{
        
        // share
        if ( addons.size() == 1){
            show_arraylist(tso.return_data());
        }
        
        // share his
        else if ( addons.size() == 2 && addons.contains("his")){ 
            show_arraylist(tso.return_data_history());
            ui.interface_print("--------------------");
            show_arraylist(tso.return_my_data_history());
        }
        
        // share tick /tick_id/
        else if (addons.size() == 3 && addons.contains("tick") && ui.check_existance_int(addons) != -1){
            if ( database.check_if_record_exists(ui.last_input, "tick") ){
                // we found tick
                int tick_id = ui.last_input;
                ui.interface_print("Type user to share: ");
                String user_to = ui.interface_get();
                if (database.ret_owner_id(user_to) != -1){
                    ui.interface_print("User found");
                    
                    // showing summary
                    ui.interface_print("Summary:");
                    ui.interface_print("Tick id:" + Integer.toString(tick_id));
                    ui.interface_print("Tick det: " + database.ret_tick_name(tick_id));
                    ui.interface_print("User login: "+ user_to+"/"+Integer.toString(database.ret_owner_id(user_to)));
                    
                    ui.interface_print("------------");
                    
                    ui.interface_print("You are sure to share this tick? (y/n)");
                    String ques = ui.interface_get();
                    
                    if ( ques.equals("y") ){
                        if (tso.share_tick(tick_id, database.ret_owner_id(user_to)) == 1){
                            ui.interface_print("Tick shared");
                        } 
                        else{
                            ui.interface_print("Failed to share tick, check log");
                        }
                    }
                    
                }
                else{
                    ui.interface_print("Cant find user with that login");
                }
            }
            else{
                ui.interface_print("Wrong tick id");
            }  
        }
        
        // share check
        else if (addons.size() == 2 && addons.contains("check") ){
            tso.share_check_database();
            
            if ( tso.status == 1){
                ui.interface_print("Share check complete. Found new shares.");
                ui.interface_print("Details:");
                for(Tick_ShareObject tts : tso.to_do){
                    ui.interface_print(tts.show_details(database));
                }
            }
            else if ( tso.status == 0){
                ui.interface_print("Share check complete. No new shares");
            }
            else{
                ui.interface_print("Share check failed. Check log");
            }
        }
        // share load
        else if ( addons.size() == 2 && addons.contains("load")){
            show_arraylist(tso.return_data());
            ui.interface_print("You are sure to add these shares to your data? (y/n)");
            String choose = ui.interface_get();
            
            if(choose.equals("y")){
                if ( tso.share_load() == 1){
                    ui.interface_print("Loaded new ticks");
                    tso.status = -2;
                }
                else if ( tso.share_load() == -1){
                    ui.interface_print("Use /share check/ command first");
                }
                else{
                    ui.interface_print("Failed loading new ticks. Check log");
                }
            }
            else{
                ui.interface_print("Cancelled");
            }
        }
    }
    /**
     * CUI_Tick_Interface.CUI_FUN_clip(List<String> addons)
     * @param addons
     * @throws SQLException
     * @throws MessagingException
     * @throws IOException 
     * Implements clip functionality
     */
    void CUI_FUN_clip(List<String> addons) throws SQLException, MessagingException, IOException{
        // clip
        if ( addons.size() == 1 && addons.contains("clip")){
            ui.interface_print("Last input: "+ui.last_string);
        }
        // clip again
        else if ( addons.size() == 2 && addons.contains("again")){
            ui.interface_print("Using : "+ui.last_string);
            CUI_logic(ui.last_string);
        }
        // clip copy
        else if (addons.size() == 2 && addons.contains("copy")){
            ui.copy_to_clipboard();
            ui.interface_print("Copied last input to clipboard");
        }
    }
}

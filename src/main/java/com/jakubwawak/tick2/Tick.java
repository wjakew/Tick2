/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import com.jakubwawak.database.Database;
import com.jakubwawak.gui.CUI_Tick_Inteface;
import com.jakubwawak.gui.GUI_connection_window;
import com.jakubwawak.gui.GUI_login_window;
import com.jakubwawak.gui.message_window;
import java.io.IOException;
import java.sql.SQLException;
import javax.mail.MessagingException;

/**
 *Main program
 * @author jakubwawak
 */
public class Tick {

    /**
     * DEBUG:
     * 1 - debug is on, going to run Tests object
     * 0 - deployment state, running application
     */
    static int debug = 0;
    
    /**
     * DEBUG LOG:
     * 1 - log showing on screen
     * 0 - log not showing on screen, still saving to file
     */
    static int debug_log = 1;
    
    
    static final String version = "v2.0.1";
    static final String HEADER = "TICK MAIN";
    static final String build = "1309201REV1";
    
    static Tick_Log session_log;
    static Database database;
    static Configuration config;
    
    /**
     * Main program
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, SQLException, MessagingException {
        String header = " _____ _      _    ____  \n" +
                        "|_   _(_) ___| | _|___ \\ \n" +
                        "  | | | |/ __| |/ / __) |\n" +
                        "  | | | | (__|   < / __/ \n" +
                        "  |_| |_|\\___|_|\\_\\_____|\n";
        header = header+"by JAKUB WAWAK   "+version+"\n";
        System.out.println(header);
        
        // initialization of the modules
        // -- Tick_Log
        session_log = new Tick_Log(debug_log);
        // -- configuration data
        config = new Configuration("config.tick");
        
        
        if ( !config.error ){
                config.read_lines();
                if (config.read){
                    // config found data

                    config.translate();
                    // -- Database
                    if ( config.validation ){
                        database = new Database(session_log,config);
                        database.log.add("Configuration file validated","CONFIGURATION");
                        database.connect();
                    }
                    else{
                        database = new Database(session_log,null);
                        database.log.add("Configuration file not validated","CONFIGURATION");
                    }
            }
            else{
                database = new Database(session_log,null);
                database.log.add("Configuration file not found","CONFIGURATION");
            }
            // debug
            if ( debug == 1){
                System.out.println("CURRENT SESSION IS DEBUG");
                if (database.connected){
                    database.user_login("wjakew", "test");
                    new Tests(database);
                }
                else{
                    System.out.println("Failed to connect to database");
                }
            }
            else{
                if (database.connected){
                    if ( config != null && config.state.contains("cui")){
                        database.log.add("CUI SET",HEADER);
                        database.program_build = build;
                        database.program_version = version;
                        new CUI_Tick_Inteface(database).run();
                    }
                    else if (config != null && config.state.contains("gui")){
                        database.log.add("GUI SET",HEADER);
                        database.program_build = build;
                        database.program_version = version;
                        new GUI_login_window(database);
                    }
                    else if (config == null){
                        database.log.add("STATE NOT SET. CUI DEFAULT",HEADER);
                        new CUI_Tick_Inteface(database).run();
                    }
                    else{
                        database.log.add("CONFIG NOT VALID.STATE NOT SET. CUI DEFAULT",HEADER);
                        new CUI_Tick_Inteface(database).run();
                    }

                }
                else{
                    System.out.println("Failed to connect to the database. Check log file.");
                    session_log.add("Failed to connect to the database",  "TICK "+version);
                }
            }
            session_log.save();
        }
        else{
            database = new Database(session_log,config);
            // error reading the file
            new message_window(null,true,"Error while reading the file.\n Configuration file not found");
            new GUI_connection_window(null,true,config,database);
            // debug
            if ( debug == 1){
                System.out.println("CURRENT SESSION IS DEBUG");
                if (database.connected){
                    database.user_login("wjakew", "test");
                    new Tests(database);
                }
                else{
                    System.out.println("Failed to connect to database");
                }
            }
            else{
                if (database.connected){
                    if ( config != null && config.state.contains("cui")){
                        database.log.add("CUI SET",HEADER);
                        database.program_build = build;
                        database.program_version = version;
                        new CUI_Tick_Inteface(database).run();
                    }
                    else if (config != null && config.state.contains("gui")){
                        database.log.add("GUI SET",HEADER);
                        database.program_build = build;
                        database.program_version = version;
                        new GUI_login_window(database);
                    }
                    else if (config == null){
                        database.log.add("STATE NOT SET. CUI DEFAULT",HEADER);
                        new CUI_Tick_Inteface(database).run();
                    }
                    else{
                        database.log.add("CONFIG NOT VALID.STATE NOT SET. CUI DEFAULT",HEADER);
                        new CUI_Tick_Inteface(database).run();
                    }

                }
                else{
                    System.out.println("Failed to connect to the database. Check log file.");
                    session_log.add("Failed to connect to the database",  "TICK "+version);
                }
            }
            session_log.save();
        }
    }   
}

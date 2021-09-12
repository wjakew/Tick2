/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import com.jakubwawak.database.Database;
import com.jakubwawak.database.Database_Object_Updater;
import com.jakubwawak.gui.GUI_options_window;
import com.jakubwawak.gui.GUI_register_window;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

/**
 *Object for testing code
 * @author jakubwawak
 */
public class Tests {
    
    Database_Object_Updater dou;
    
    /**
     * Main constructor
     * @param database 
     */
    Tests(Database database) throws FileNotFoundException, IOException, SQLException{
        Options options = new Options(database);
        new GUI_options_window(null,true,database,options);
    }
    
}

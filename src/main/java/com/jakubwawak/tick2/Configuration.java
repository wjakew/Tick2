/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *Object for configuration
 * @author jakubwawak
 */
public class Configuration {
    
    /**
     * idea of the file
     * 
     * --------------sof
     * - configuration file for Tick2
     * - that how you can start comment
     * #databasename# tick_database 
     * #databaseip# localhost
     * #databaseuser# entrc_client
     * #databasepass# password
     * 
     * --------------eof
     *  # - start of line with data
     *  - - comment section
     */
    
    public String raw_path;
    
    ArrayList<String> lines;           // object for storing raw 
    BufferedReader reader;             // reader for reading data from file
    
    public boolean read;               // flag for checking if file is read
    public boolean validation;
    public boolean error;
    //--------------------database data--------------
    public String database_ip;
    public String database_name;
    public String database_user;
    public String database_pass;
    public String state;              // for using gui or cui interface
    //-----------------------------------------------
    /**
     * Constructor
     * @param configuration_path 
     */
    public Configuration(String configuration_path){
        
        raw_path = configuration_path;
        lines = new ArrayList<>();
        reader = null;
        read = false;
        
        database_ip = "";
        database_name = "";
        database_user = "";
        database_pass = "";
        state = "";
        validation = false;
        
        File test = new File(raw_path);
        error = !test.exists();
        if (!error){
            System.out.println("Configuration file found!");
        }
        else{
            System.out.println("Configuration file not found");
        }
    }
    
    /**
     * Function for saving configuration to file
     * @throws IOException 
     */
    public void save_to_file() throws IOException{
        /**
        * - configuration file for Tick2
        * - that how you can start comment
        * #databasename# tick_database 
        * #databaseip# localhost
        * #databaseuser# entrc_client
        * #databasepass# password
         */
        try{
            FileWriter writer = new FileWriter(raw_path);
            writer.write("- configuration file for Tick2\n");
            writer.write("#databasename# "+this.database_name+"\n");
            writer.write("#databaseip# "+this.database_ip+"\n");
            writer.write("#databaseuser# "+this.database_user+"\n");
            writer.write("#databasepass# "+this.database_pass+"\n");
            writer.write("#state# gui");
            writer.close();
        }catch(IOException e){
            error = true;
        }
    }
    /**
     * Function for trimming data
     * @param line 
     */
    String trim(String line){
        int size = line.length();
        String data = "";
        for(int i = 0 ; i < size ; i++){
            if ( line.charAt(i) != '-'){
                data = data + line.charAt(i);
            }
            else
                break;
        }
        return data;
    }
    
    /**
     * Function for decomposing line
     * @param line 
     */
    void decompose(String line){
        String raw = trim(line);
        if ( raw.length() >0 ){
            if ( raw.charAt(0) == '#' ){
                String [] elements = raw.split("#");
                /**
                 * now:
                 * index 1 - key
                 * index 2 - value
                 */
                System.out.println("raw: "+elements[1]+" | "+elements[2]);
                switch(elements[1]){
                    case "databaseip":
                        database_ip = elements[2].trim();
                        break;
                    case "databasename":
                        database_name = elements[2].trim();
                        break;
                    case "databaseuser":
                        database_user = elements[2].trim();
                        break;
                    case "databasepass":
                        database_pass = elements[2].trim();
                        break;
                    case "state":
                        state = elements[2];
                        break;
                    default:
                        break;
                }
            }
        }
    }
    
    /**
     * Function for translating data
     */
    public void translate(){
        if ( lines.size() > 0 ){
            for ( String line : lines){
                decompose(line);
            }
        }
        validation = validate_objects();
    }
    
    /**
     * Function for validating single field
     * @param field
     * @return 
     */
    boolean validate_field(String field){
        return !field.equals("");
    }
    
    /**
     * Function for validating data
     * @return boolean
     */
    boolean validate_data(){
        int counter = 0;
        if ( lines.size() > 0 ){
            for(String line : lines ){
                if (line.charAt(0) == '#'){
                    counter ++;
                }
            }
            if (counter >= 4){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Function for validation objects
     * @return Boolean
     */
    boolean validate_objects(){
        return validate_field(database_ip) && validate_field(database_name)
                && validate_field(database_user) && validate_field(database_pass)
                && validate_field(state);
    }
    
    /**
     * Function for reading lines
     */
    public void read_lines() throws FileNotFoundException, IOException{
        try{
            String line;
            reader = new BufferedReader(new FileReader(raw_path));
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            read = true;
            
        }catch(IOException e){
            read  = false;
        }
        reader.close();
    }
    
    /**
     * Function for showing raw data
     */
    void show_raw_data(){
        System.out.println("Showing raw data of /lines/:");
        int counter = 1;
        for (String line : lines){
            System.out.println("|"+counter+"| "+line);
            counter++;
        }
        System.out.println("End of the file");
    }
}

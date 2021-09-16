/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 *Object for storing log of the program
 * @author jakub
 */
public class Tick_Log {
    
    final String version = "v1.0.3";
    
    // set to 0 when deploying
    public int debug = 0;
    
    public ArrayList<String> log_lines;
    String LOG_SRC = "LOG_TICKPROGRAM_";
    public Date actual_date;
    File log_file;
    FileWriter writer;
    
    /**
     * Tic_Log() Constructor
     * @param debug
     */
    Tick_Log(int debug){
        this.debug = debug;
        log_lines = new ArrayList<>();
        actual_date = new Date();
        String actual_date_text = actual_date.toString().replaceAll(":","");
        LOG_SRC = LOG_SRC + actual_date_text +".txt";
        LOG_SRC = LOG_SRC.replaceAll("\\s", "");
        init();
    }
    
    /**
     * Tic_Log.init()
     * Initialization of the log content
     */
    void init(){
        log_lines.add("Log file from program TICK\n");
        log_lines.add("Log made by Tick_Log version "+version+"\n");
        log_lines.add("TIME OF START: "+ actual_date.toString()+"\n");
        log_lines.add("---------------------------------------\n");
    }
    
    /**
     * Tic_Log.add(String text,String header)
     * @param text
     * @param header 
     * Function for adding data to the log
     */
    public void add(String text,String header){
        String str = new Date().toString();
        String time = str.substring(11,19);
        String to_add = time + " : " + header + " ----> " + text+"\n";
        // if for showing errors on the screen
        if ( debug == 1 ){
            System.out.println(to_add);
        }
        log_lines.add(to_add);
    }
    /**
     * Tic_Log.add(String text)
     * @param lines 
     * Function for adding data to the log
     */
    public void add(ArrayList<String> lines){
        log_lines.addAll(lines);
    }
    
    /**
     * Function for getting header of log line
     * @return String
     */
    String get_header(String log){
        return log.split(":")[1].split("---->")[0];
    }
    
    /**
     * Function for getting details of log lone
     * @param log
     * @return String
     */
    String get_details(String log){
        return log.split(":")[1].split("---->")[1];
    }
    
    /**
     * Function for getting details of the header
     * Mainly function for showlog window
     * @param header_to_find
     * @return String
     */
    String get_detalis_from_header(String header_to_find){
        for(String line : log_lines){
            if ( get_header(line).equals(header_to_find) ){
                return get_details(line);
            }
        }
        return null;
    }
    
    /**
     * Tic_Log.close()
     * Closing the log content.
     */
    void close(){
        actual_date = new Date();
        log_lines.add("---------------------------------------\n");
        log_lines.add("TIME OF END: "+actual_date.toString()+"\n");
    }
    /**
     * Tic_Log.save()
     * @throws IOException
     * Saving log content to the file
     */
    void save() throws IOException{
        close();
        log_file = new File(LOG_SRC);
        writer = new FileWriter(LOG_SRC);
        
        for ( String line : log_lines ){
            writer.write(line);
        }
        writer.close();
    }
}

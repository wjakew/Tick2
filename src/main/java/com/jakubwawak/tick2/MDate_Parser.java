/*
Jakub Wawak
ALL RIGHTS RESERVED.
kubawawak@gmail.com
 */
package com.jakubwawak.tick2;

import java.util.Date;

/**
 *Parser for MDate_Object
 * @author jakubwawak
 */
public class MDate_Parser {
    
    String raw_date;            // field for storing raw format of the date
    
    String[] raw_date_parts;    // field for storing split parts 
    
    /**
     * modes:
     * 0 - parse system stop
     * 1 - parse system on: MDate_Parser(String date)
     * 2 - parse system on: MDate_Parser(Date date)
     */
    int mode = 0;
    
    
    boolean hour_format = false;
    boolean parse_fail = false;
    
    // fields for storing raw data of dates
    String raw_date_part = "";
    String raw_hour_part = "";
    
    // colletction of 'splitters' implemented in parser
    String [] splitters = {"-","/",":","."};
    
    
    /**
     * Coded formats:
     * DD-MM-YYYY 
     * DD-MM-YYYY HH:MM:SS
     * MM-DD-YYYY
     * MM-DD-YYYY HH:MM:SS
     * YYYY-MM-DD
     * YYYY-MM-DD HH:MM:SS
     * -------------------
     * DD/MM/YYYY 
     * DD/MM/YYYY HH:MM:SS
     * MM/DD/YYYY
     * MM/DD/YYYY HH:MM:SS
     * YYYY/MM/DD
     * YYYY/MM/DD HH:MM:SS
     * -------------------
     * Coded Objects:
     * SimpleDateFormat
     * java.Date
     * util.Date
     */
    
    // constructors for date formats
    
    /**
     * Constructor with date parameter given by String type
     * @param date 
     */
    public MDate_Parser(String date){
        mode = 1;
        raw_date = date;
        
        raw_date_parts = raw_date.split(" ");
        
        switch (raw_date_parts.length) {
            case 1:
                raw_date_part = raw_date;
                hour_format = false;
                break;
            case 2:
                raw_date_part = raw_date_parts[0];
                raw_hour_part = raw_date_parts[1];
                hour_format = true;
                break;
            default:
                parse_fail = true;
                break;
        }
    }
    
    /**
     * Parser constructor for Date object
     * eg. Tue Aug 25 15:34:02 CEST 2020
     * @param date 
     */
    public MDate_Parser(Date date){
        mode = 2;
        raw_date = date.toString();
        raw_date_parts = raw_date.split(" ");
        hour_format = true;
        raw_hour_part = raw_date_parts[3];
        raw_date_part = raw_date_parts[2]+"/"+raw_date_parts[1]+"/"+raw_date_parts[5];
    }
    /**
     * Parser constructor for Date.toString object
     * egTue Aug 25 15:34:02 CEST 2020
     * @param datetoString
     * @param distinguishingfeature 
     * NOTE: distinguishingfeature can be anything
     */
    public MDate_Parser(String datetoString,int distinguishingfeature){
        mode = 2;
        raw_date = datetoString;
        raw_date_parts = raw_date.split(" ");
        hour_format = true;
        raw_hour_part = raw_date_parts[3];
        raw_date_part = raw_date_parts[2]+"/"+raw_date_parts[1]+"/"+raw_date_parts[5];
    }
    
    /**
     * Function for showing arrays 
     * @param array
     * @return 
     */
    String visual_array(String[] array){
        String data_toRet = "";
        
        for(String element : array){
            data_toRet = data_toRet +","+ element;
        }
        if ( data_toRet.toCharArray()[0] == ','){
            data_toRet = data_toRet.substring(1);
        }
        
        return data_toRet;
    }
    
    String visual_array(int[] array){
        String data_toRet = "";
        
        for(int element : array){
            data_toRet = data_toRet + "," + Integer.toString(element);
        }
        
        if ( data_toRet.toCharArray()[0] == ','){
            data_toRet = data_toRet.substring(1);
        }
        
        return data_toRet;
    }
    
    /**
     * Function for showing data
     * @return String 
     */
    String visual_data(){
        String data_toRet = "Parser data for: " + raw_date + "\n";
        
        data_toRet = data_toRet +"raw_date_parts: "+ visual_array(raw_date_parts) + "\n";
        data_toRet = data_toRet +"raw_date_part: "+ raw_date_part + "\n";
        data_toRet = data_toRet +"raw_hour_part: "+ raw_hour_part + "\n";
        data_toRet = data_toRet + "hour format: " + Boolean.toString(hour_format);
        return data_toRet;
    }
    
    /**
     * Showing debug of arrays returned by parse_ function
     */
    void show_debug(){
        System.out.println(visual_array(parse_(raw_date_part)));
        System.out.println(visual_array(parse_(raw_hour_part)));
    }
    
    /**
     * Function checking if text is a number
     * @param text
     * @return int
     */
    int parse_number(String text){
        int num_to_Ret = -1;
        try{
            num_to_Ret = Integer.parseInt(text);
            return num_to_Ret;
        }catch(NumberFormatException e){
            return num_to_Ret;
        }
    }
    
    /**
     * Function for removing leading zero
     * @param data
     * @return 
     */
    String remove_leading0(String data){
        
        if ( data.toCharArray()[0] == '0'){
            return data.substring(1);
        }
        return data;
    }
    
    /**
     * Function for checking data in object arrays
     * @param array
     * @return 
     */
    boolean check_(int [] array){
        for(int num : array){
            if ( num == -1){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Parasing data with implemented splitters
     * @param data String
     * @return int [] 
     */
    int [] parse_(String data){
        
        switch (mode) {
            case 1:
            {
                int [] time_elements = new int [] {-1,-1,-1};
                
                for (String splitter : splitters){
                    
                    if (data.contains(splitter)){
                        
                        String []elements = data.split(splitter);
                        int i = 0;
                        for(String element : elements){
                            time_elements[i] = parse_number(element);
                            i++;
                        }
                        break;
                    }
                }
                return time_elements;
            }
            case 2:
            {
                // day year hh mm ss
                int [] time_elements = new int[] {-1,-1,-1,-1,-1};
                
                // day
                time_elements[0] = parse_number(raw_date_part.split("/")[0]);
                
                // year
                time_elements[1] = parse_number(raw_date_part.split("/")[2]);
                
                // hh
                time_elements[2] = parse_number(raw_hour_part.split(":")[0]);
                // mm
                time_elements[3] = parse_number(raw_hour_part.split(":")[1]);
                // ss
                time_elements[4] = parse_number(raw_hour_part.split(":")[2]);
                
                return time_elements;
            }
            default:
                return null;
        }
    }
    
    
    /**
     * Method returns Date object 
     * @return MDate_Object
     */
    public MDate_Object ret_date_object(){
        int [] date,time;
        if ( mode == 1){
            date = parse_(raw_date_part);
            if ( hour_format ){
                time = parse_(raw_hour_part);
            }
            else{
                time = new int []{0,0,0};
            }


            if ( check_(date) && check_(time) ){
                //MDate_Object(int n_day,int month_number,int year,int hours,int minutes,int seconds)
                return new MDate_Object(date[0],date[1],date[2],time[0],time[1],time[2]);
            }
            parse_fail = true;
            return null;
            }
        else if ( mode ==2 ){
            date = parse_("");  // day year hh mm ss
            //MDate_Object(int n_day,String s_day,String month,int year,int hours,int minutes, int seconds)
            //eg. Tue Aug 25 15:34:02 CEST 2020
            return new MDate_Object(date[0],raw_date_parts[0],raw_date_parts[1],date[1],date[2],date[3],date[4]);
        }
        else{
            return null;
        }

    }
    
}



    
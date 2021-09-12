/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import java.util.ArrayList;

/**
 *Object for storing data of the field with info
 * @author jakub
 */
public class Tick_Brick {
    final String version = "v1.0.0";
    
    int MAX = -2147483648;
    public int index_in_collection = -1;
    public boolean STOP = false;
    
    // fields for storing data
    public int data_int;
    public String data_string;
    public ArrayList<String> data_array;
    
    /**
     * category
     * modes:
     * 1 - int
     * 2 - string
     * 3 - array
     * -1 - error
     */
    public int category;
    
    public Tick_Brick(){
        data_int = MAX;
        data_string = null;
        category = 0;
        data_array = null;
        get_category();
    }
    
       
    public Tick_Brick(int data){
        data_int = data;
        data_string = null;
        data_array = null;
        get_category();
    }
    
    public Tick_Brick(String data){
        data_int = MAX;
        data_string = data;
        data_array = null;
        get_category();
    }
    
    public Tick_Brick(ArrayList<String> data){
        data_int = MAX;
        data_string = null;
        data_array = data;
        get_category();
    }
    
    public String s_get(){
        return data_string;
    }
    
    public int i_get(){
        return data_int;
    }
    
    public ArrayList<String> a_get(){
        return data_array;
    }

    /**
     * Tick_Brick.get_category()
     * Function for categorise data input
     */
    public void get_category(){
        if ( data_int != MAX ){ // category int
            category = 1;
        }
        else if ( data_string != null ){ // category string
            category = 2;
        }
        else if ( data_array != null ){ // category array
            category = 3;
        }
        else{
            category = -1;      // category error
        }
    }
    /**
     * Tick_Brick.categorize(String obj)
     * @param obj
     * @return boolean
     * Function for cateogrization string.
     */
    public boolean categorise(String obj){
        try{
            Integer i = Integer.parseInt(obj);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    /**
     * Tick_Brick.set_index_collection(int index)
     * @param index 
     * Function for passing index from collection where Tick_Brick is
     */
    public void set_index_collection(int index){
        index_in_collection = index;
    }
    
    public void show(){
        System.out.println("Tick_Brick version ("+version+"):");
        System.out.println("Category : "+Integer.toString(category));
        if ( category == 2){
            System.out.println("data_string : "+data_string);
        }
        else if ( category == 1){
            System.out.println("data_int : "+Integer.toString(data_int));
        }
        else if ( category == 3){
            if (data_array!= null){
                System.out.println("data_array:"+data_array.toString());
            }
            else{
                System.out.println("data_array: null");
            }
        }
    }
}

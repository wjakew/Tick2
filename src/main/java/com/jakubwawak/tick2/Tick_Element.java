/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import com.jakubwawak.gui.UI_Interface;
import java.util.ArrayList;

/**
 *Main object of the program, all other objects extends it
 * @author jakub
 */
public class Tick_Element {
    
    String tick_Element_Name;                       // name of the overriding element ( name of the class )
    public ArrayList<Tick_Brick> tick_Element_Elements;    // collection of the elements to add
    public int tick_Element_size;
    UI_Interface inter;
    
    
    public Tick_Element(String object_name){
        inter = new UI_Interface();
        tick_Element_Name = object_name;
        tick_Element_Elements = new ArrayList<>();
        tick_Element_size = 0;
        inter.tab ="    ";
    }
    
    public void put_elements(ArrayList<Tick_Brick> object_elements){
        tick_Element_Elements = object_elements;
        tick_Element_size = object_elements.size();
    }
    
    public void show_tick_wall(){
        System.out.println("Tick_Brick for object: "+ tick_Element_Name);
        for (Tick_Brick t : tick_Element_Elements){
            t.show();
        }
    }
}

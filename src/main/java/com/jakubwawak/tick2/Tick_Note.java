/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import java.util.ArrayList;

/**
 *
 * @author jakub
 */
public class Tick_Note extends Tick_Element{
    /**
     * CREATE TABLE NOTE
        (
        note_id INT AUTO_INCREMENT PRIMARY KEY,
        owner_id INT,
        note_content VARCHAR(100),
        setting1 VARCHAR(40),
        setting2 VARCHAR(40),
        setting3 VARCHAR(40),
        CONSTRAINT fk_note FOREIGN KEY (owner_id) REFERENCES OWN(owner_id)
        );
     */
    public int note_id;
    public int owner_id;
    public String note_content;
    String setting1;
    String setting2;
    String setting3;
    
    // main constructor
    public Tick_Note(){
        super("Tick_Note");
        note_id = -1;
        owner_id = -1;
        note_content = "";
        setting1 = "";
        setting2 = "";
        setting3 = "";
        super.put_elements(wall_updater());
    }
    // one argument constructor
    public Tick_Note(ArrayList<Tick_Brick> to_add){
        super("Tick_Note");
        note_id = to_add.get(0).i_get();
        owner_id = to_add.get(1).i_get();
        note_content = to_add.get(2).s_get();
        setting1 = to_add.get(3).s_get();
        setting2 = to_add.get(4).s_get();
        setting3 = to_add.get(5).s_get();
        super.put_elements(wall_updater());
    }
    /**
     * Tick_Note.wall_updater()
     * @return ArrayList
     * Returns 'brick wall'
     */
    public ArrayList<Tick_Brick> wall_updater(){
        ArrayList<Tick_Brick> to_ret = new ArrayList<>();
        
        to_ret.add(new Tick_Brick(note_id));
        to_ret.add(new Tick_Brick(owner_id));
        to_ret.add(new Tick_Brick(note_content));
        to_ret.add(new Tick_Brick(setting1));
        to_ret.add(new Tick_Brick(setting2));
        to_ret.add(new Tick_Brick(setting3));
        super.put_elements(to_ret);
        return to_ret;
    }
    
    /**
     * Tick_Category.init_CUI()
     * Interface for CUI
     */
    public void init_CUI(){
        super.inter.interface_print("Note Content: ");
        note_content = super.inter.interface_get();
        super.put_elements(wall_updater());
    }
    /**
     * Tick_Note.get_lines_to_show()
     * @return ArrayList
     * Returns lines of object content
     */
    public ArrayList<String> get_lines_to_show(){
        /**
         * id: /note_id/
         * Note Content:
         * /note_content/
         */
        ArrayList<String> to_ret = new ArrayList<>();
        to_ret.add("id: "+Integer.toString(note_id));
        to_ret.add("Note Content:\n"+note_content);
        return to_ret;
    }
    
}

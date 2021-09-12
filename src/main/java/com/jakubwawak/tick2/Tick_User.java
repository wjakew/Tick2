/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.tick2;

import java.util.ArrayList;

/**
 *Object for storing user info
 * @author jakub
 */
public class Tick_User extends Tick_Element{
    /**
     * CREATE TABLE OWN
        (
        owner_id INT AUTO_INCREMENT PRIMARY KEY,
        owner_login VARCHAR(30),
        address_id INT,
        owner_password VARCHAR(72),
        owner_name VARCHAR(70),
        owner_surname VARCHAR(70),
        owner_email_address VARCHAR(60),
        owner_age INT,
        owner_status INT,
        CONSTRAINT fk_own FOREIGN KEY (address_id) REFERENCES ADDRESS(address_id)
        );
     */
    
    public int owner_id;                           // id from the database
    public int address_id;                         // id of the address
    public String owner_login;                     // owner login from database
    public String owner_name;                      // owner name
    private String owner_surname;                  // --/--
    private String owner_password;
    public String owner_email_address;
    public int owner_age;
    public int owner_status;
    
    /**
     * Main constructor
     */
    public Tick_User(){
        super("Tick_User");
        owner_id = 0;                           // id from the database
        address_id = 1;                         // id of the address
        owner_login = "";                       // owner login from database
        owner_name = "";                        // owner name
        owner_surname = "";                     // --/--
        owner_email_address = "";
        owner_age = 0;
        owner_status = 0;
        owner_password = "";
        super.put_elements(wall_updater());
    }
    /**
     * Constructor for the database
     * @param to_add 
     */
    public Tick_User(ArrayList<Tick_Brick> to_add){
        super("Tick_User");
        owner_id = to_add.get(0).i_get();     
        owner_login = to_add.get(1).s_get();
        address_id = to_add.get(2).i_get(); 
        owner_password = to_add.get(3).s_get();
        owner_name = to_add.get(4).s_get();                 
        owner_surname = to_add.get(5).s_get();           
        owner_email_address = to_add.get(6).s_get();
        owner_age = to_add.get(7).i_get();
        owner_status = to_add.get(8).i_get();
        super.put_elements(wall_updater());
    }
    
    public void put_name(String name){
        this.owner_name = name;
    }
    
    public void put_surname(String surname){
        this.owner_surname = surname;
    }
    
    public void put_password(String password){
        this.owner_password = password;
    }
    
    /**
     * Tick_User.wall_updater()
     * @return ArrayList<Tick_Brick>
     * Returns collection of objects
     */
    public ArrayList<Tick_Brick> wall_updater(){
          ArrayList<Tick_Brick> to_ret = new ArrayList<>();
          to_ret.add(new Tick_Brick(owner_id));
          to_ret.add(new Tick_Brick(owner_login));
          to_ret.add(new Tick_Brick(address_id));
          to_ret.add(new Tick_Brick(owner_password));
          to_ret.add(new Tick_Brick(owner_name));
          to_ret.add(new Tick_Brick(owner_surname));
          to_ret.add(new Tick_Brick(owner_email_address));
          to_ret.add(new Tick_Brick(owner_age));
          to_ret.add(new Tick_Brick(owner_status));
          return to_ret;
    }
    
    /**
     * Tick_User.init_CUI()
     * Inits input to 
     */
    public void init_CUI(){
        
        super.inter.interface_print("Welcome in the Tick User!");
        super.inter.interface_print("Name:");
        owner_name = super.inter.interface_get();
        super.inter.interface_print("Surname::");
        owner_surname = super.inter.interface_get();
        super.inter.interface_print("Email:");
        owner_email_address = super.inter.interface_get();
        super.inter.interface_print("Age:");
        owner_age = Integer.parseInt(super.inter.interface_get());
        super.inter.interface_print("Login:");
        owner_login = super.inter.interface_get();
        super.inter.interface_print("Password:");
        owner_password = super.inter.get_password();
        super.put_elements(wall_updater()); // updates Tick_Brick collection
    }
    
    /**
     * Function for showing data
     */
    public void show(){
        System.out.println("Tick User info: ");
        System.out.println("    Login: "+owner_login);
        System.out.println("    unique id: "+Integer.toString(owner_id));
        System.out.println("    Name: "+owner_name+" Surname: "+owner_surname);
        System.out.println("    E-mail address: "+owner_email_address);
        System.out.println("    Age: "+Integer.toString(owner_age) + "Status: "+Integer.toString(owner_status));
    }

}

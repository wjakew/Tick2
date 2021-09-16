/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 *Object for communicating with user
 * @author jakub
 */
public class UI_Interface {
    final String version = "v1.1.5";
    
    Scanner sc;                            // main object for scanning 
    public ArrayList<String> history;      // main log of the input,output
    Console console;
    
    String[] not_get = new String[] {"clip","again","clip again"};
    List<String> avoid;
    
    
    final String PROMPT = ">";
    
    public String tab = "";                // variable used to 'move' print
    public int last_und;
    public boolean int_flag = false;       // variable used to flag if number found in input
    public boolean blank = true;           // variable set if input is blank
    public boolean number_input = false;   // variable for checking if input is only a number/float/double
    public int last_input;
    
    public String last_string;
    public String raw_input;
    public int size;
    
    public ArrayList<Integer> numbers; // collection for found 
                                // numbers in user input
    
    /**
     * Main constructor of the object.
     */
    public UI_Interface(){
        sc = new Scanner(System.in);
        history = new ArrayList<>();
        numbers = new ArrayList<>();
        last_und = -1;
        avoid = Arrays.asList(not_get);
        last_string = "";
        raw_input = "";
        size = 0;
        
    }
    
    /**
     * UI_Interface.count()
     * Function for counting words in raw input 
     */
    public void count(){
        for ( String word : raw_input.split(" ")){
            size++;
        }
    }
    
    /**
     * UI_Interface.interface_get_w_prompt(String prompt)
     * @param prompt
     * @return String
     * Function for getting user input with prompt
     */
    public String interface_get_w_prompt(String prompt){
        interface_print(prompt);
        return interface_get();
    }
    
    /**
     * UI_Interface.interface_get_date()
     * @return String
     * Function for getting date from user
     */
    public String interface_get_date(){
        interface_print("Accepted formats:");
        interface_print("DD/MM/YYYY");
        interface_print("or");
        interface_print("DD/MM/YYYY HH:MM:SS");
        return interface_get();
    }
    
    /**
     * UI_Interface.avoid_function()
     * @return boolean
     * 
     */
    public boolean avoid_function(){
        if ( raw_input !=null) {
            List<String> words_input = Arrays.asList(raw_input.split(" "));
        
            for(String word : words_input){
                if ( avoid.contains(word) ){
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    
    /**
     * UI_Tick_Interface.interface_get()
     * @return String 
     */
    public String interface_get(){
        size = 0;
        numbers.clear();
        System.out.print(tab+PROMPT);
        String input = sc.nextLine();
        history.add("UI - > USER INPUT : "+input+"\n");
        last_und = understand(input);
        get_numbers(input);
        if ( avoid_function() || !input.isEmpty() ){
            blank = false;
        }
        number_input = check_input_number(input);
        if ( !avoid.contains(input)){
            last_string = input;
        }
        raw_input = input;
        count();
        return input;
    }
    
    /**
     * Function for checking if input is a number
     * @param data
     * @return boolean
     */
    public boolean check_input_number(String data){
        try{
            Integer.parseInt(data);
            Double.parseDouble(data);
            Float.parseFloat(data);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    
    /**
     * UI_Interface.copy_to_clipboard()
     * @return boolean
     * Function copies last input to clipboard
     */
    public boolean copy_to_clipboard(){
        StringSelection data = new StringSelection(last_string);
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        cb.setContents(data, data);
        return true;
    }
    /**
     * UI_Tick_Interface.get_password()
     * @return String
     * Returns password typed by user
     */
    public String get_password(){
        Console console = System.console();
        if ( console == null){
            return interface_get();
        }
        else{
            char[] password = console.readPassword("Password: ");
            return String.copyValueOf(password);
        }
        
    }
    /**
     * UI_Tick_Interface.interface_print(String text)
     * @param text 
     * Function for printing
     */
    public void interface_print(String text){
        history.add("UI - > USER OUTPUT: "+text+"\n");
        System.out.println(tab+text);
    }
    public void interface_print(int number){
        history.add("UI -> USER OUTPUT: "+Integer.toString(number)+"\n");
        System.out.println(tab + Integer.toString(number));
    }
    /**
     * UI_Tick_Interface.interface_print()
     * Function for printing blank 
     */
    public void interface_print(){
        System.out.println("");
    }
    /**
     * UI_Tick_Interface.data_print()
     * Function for printing data from fields - for debug
     */
    public void data_print(){
        interface_print("Data (UI_Interface version "+this.version + ")");
        interface_print("last_und:"+Integer.toString(this.last_und));
        interface_print("last_input:"+Integer.toString(this.last_input));
        interface_print("last_string:" + this.last_string);
        interface_print("numbers (array):");
        numbers.forEach(i -> {
            interface_print(i);
        });
        interface_print("raw_input: "+this.raw_input);
        interface_print("size: "+Integer.toString(this.size));
        interface_print("last_string: "+this.last_string);
        interface_print();
        interface_print("blank_flag: "+Boolean.toString(this.blank));
        interface_print("int_flag: "+Boolean.toString(this.int_flag));  
    }
    /**
     * UI_Interface.history_print()
     * Function for printing history 
     */
    public void history_print(){
        interface_print("User interface history:");
        history.forEach(data -> {
            interface_print(data);
        });
    }
    /**
     * UI_Tick_Interface.array_print(ArrayList<String> to_show)
     * @param to_show 
     * Function for printing data from ArrayList
     */
    public void array_print(ArrayList<String> to_show){
        for(String line : to_show){
            interface_print(line);
        }
    }
    
    /**
     * UI_Interface.interface_set_indent(int amount)
     * @param amount 
     * Function simplifies making indent in interface
     */
    public void interface_set_indent(int amount){
        tab = "";
        for (int i = 0 ; i < amount ; i++){
            tab = tab + "   ";
        }
    }
    public void interface_reset_indent(){
        tab = "";
    }
    /**
     * UI_Tick_Interface.understand(String input)
     * @param input
     * @return Integer
     * Function for checking if input is integer (ret 1),
     * float (ret 2) or double (ret 3)
     */
    public int understand(String input){
        try{
            int a = Integer.parseInt(input);
            last_input = a;
            int_flag = true;
            return 1;
        }catch( NumberFormatException e){
            // it's not an int
            try{
                float a = Float.parseFloat(input);
                return 2;
            }catch( NumberFormatException f){
            // it's not a float
                try{
                    double a = Double.parseDouble(input);
                    return 3;
                }catch( NumberFormatException g){
                    //it's not a double
                        return 0;
                }
             }
        }
    }
    
    public void get_numbers(String input){
        // looping on words
        for (String word : input.split(" ")){
            try{
                int number = Integer.parseInt(word);
                numbers.add(number);
                int_flag = true;
            }catch(NumberFormatException e){
                int_flag = false;
            }
        }
        if ( !numbers.isEmpty() ){
            last_input = numbers.get(0);
        }
    }
    
    /**
     * UI_Tick_Interface.check_existance(String line, String [] keys)
     * @param line
     * @param keys
     * @return String
     * Method checks if in line is key word given in the array
     */
    public String check_existance(String line, String [] keys){
        List<String> user_input = new ArrayList<String>(Arrays.asList(line.split(" ")));
        List<String> given_keys = new ArrayList<String>(Arrays.asList(keys));
        
        for ( String word : user_input ){
            if ( given_keys.contains(word) ){
                return word;
            }
        }
        return null;
    }
    
    /**
     * UI_Interface.check_existance(List<String> user_input,String [] keys)
     * @param user_input
     * @param keys
     * @return String 
     * Function for checking if list has at least one common object with array
     */
    public String check_existance(List<String> user_input, String [] keys){
        List<String> given_keys = new ArrayList<String>(Arrays.asList(keys));
        for ( String word : user_input ){
            if ( given_keys.contains(word) ){
                return word;
            }
        }
        return null;
    }
    
    /**
     * UI_Interface.check_in_array(String to_check,String[] source)
     * @param to_check
     * @param source
     * @return boolean
     * Function check if string is in array
     */
    public boolean check_in_array(String to_check,String[] source){
        List<String> ch = Arrays.asList(source);
        return ch.contains(to_check);
    }
    
    /**
     * UI_Interface.fast_check_in_array(String [] source)
     * @param source
     * @return boolean
     * Function returns if last user input is in source array
     */
    public boolean fast_check_in_array(String [] source){
        return check_in_array(last_string,source);
    }
    
    /**
     * UI_Interface.check_existance_int( List<String> user_input )
     * @param user_input
     * @return int
     * Returns first found integer, if not found returns -1
     */
    public int check_existance_int ( List<String> user_input ){
        for ( String word : user_input ){
            try{
                Integer i = Integer.parseInt(word);
                return i; 
            }catch(NumberFormatException e){
            }
        }
        return -1;
    }
    
    /**
     * UI_Interface.convert_array(ArrayList<String> to_convert)
     * @param to_convert
     * @return String
     * Converting array to string
     */
    public String convert_array(ArrayList<String> to_convert){
        String to_ret = "";
        for(String line: to_convert){
            to_ret = to_ret + line + "\n";
        }
        return to_ret;
    }
}

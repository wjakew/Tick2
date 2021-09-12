/*
Jakub Wawak
ALL RIGHTS RESERVED.
kubawawak@gmail.com
 */
package com.jakubwawak.tick2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Object for storing data
 * @author jakubwawak
 */
public class MDate_Object {
    String version = "v0.0.3";
    
    String[] month_data = new String[] {"January","February","March",
                            "April","May","June","July","August","September",
                            "October","November","December"};
    
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    // ------------------date
    int day_number = 0;
    String day_name = "";
    
    int month_number = 0;
    String month_name = "";

    int year = 0;
    boolean leap_year = false;      // checking if it is a leap year
    
    // ------------------time
    int hours = 0;
    boolean time24 = true;          // checking format of the hour
    int minutes = 0;
    int seconds = 0;
    
    /**
     * Constructor of the object
     * @param n_day -   number of the day
     * @param s_day -   name of the day
     * @param month -   name of the month
     * @param year  -   number of the year
     * @param hours -   number of hours
     * @param minutes   - number of minutes
     * @param seconds   - number of seconds
     */
    
    public MDate_Object(int n_day,String s_day,String month,int year,int hours,int minutes, int seconds){
        
        day_number = n_day;
        day_name = s_day;
        month_name = month;
        this.year = year;
        
        this.hours = hours;
        time24 = check_24hour();

        this.minutes = minutes;
        this.seconds = seconds;
        
        // data fillers
        leap_year = leap_year_check();
        month_number = month_name_translator();
    }
    
    public MDate_Object(int n_day,int month_number,int year,int hours,int minutes,int seconds){
        day_number = n_day;
        day_name = "";
        this.month_number = month_number;
        this.year = year;
        
        this.hours = hours;
        time24 = check_24hour();

        this.minutes = minutes;
        this.seconds = seconds;
        
        // data fillers
        leap_year = leap_year_check();
        month_name = month_number_translator();
    }
    
    // functions for preparing data
    
    /**
     * Function for checking if year is leap
     * @return boolean (true if leap year) 
     */
    public boolean leap_year_check(){
        
        if ( year%4 == 0){
            if ( year%100 == 0){
                return year%400 ==0;
            }
            else{
                return true;
            }
        }
        return false;
    }
    
    /**
     * 24 hour time checker
     * @return 
     */
    public boolean check_24hour(){
        return hours >12;
    }
    
    /**
     * Function for counting days between dates
     * @param compare_to
     * @return long
     */
    public long count_days_to(MDate_Object compare_to){
        LocalDate date_obj = LocalDate.of(year, return_month_obj(), day_number);
        LocalDate compare_obj = LocalDate.of(compare_to.year, compare_to.return_month_obj(), compare_to.day_number);
        return ChronoUnit.DAYS.between(date_obj, compare_obj);
    }
    
    /**
     * Function returning date of the next day
     * @return MDate_Object
     */
    public MDate_Object next_date(){
        final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        //final Date date = format.parse(curDate);
        final Date date = this.parse_object();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return new MDate_Parser(calendar.getTime()).ret_date_object();
    }
    
    /**
     * Function for translating month to correspondent number
     * @return int
     */
    public int month_name_translator(){
        List<String> list = Arrays.asList(month_data); 
        
        if (list.contains(month_name)){
            return list.indexOf(month_name)+1;
        }
        else{
            for (String month : list){
                if ( month.contains(month_name)){
                    return list.indexOf(month)+1;
                }
            }
            return 0;
        }
    }
    
    public Month return_month_obj(){
        return Month.of(this.month_number);
    }
    /**
     * Function for addinfg leading 0 to number
     * @param number
     * @return String
     */
    public String number_0adder(int number){
        String num = Integer.toString(number);
        return "0"+num;
    }
    /**
     * Function for parasing object to Date object
     * @return Date
     */
    public Date parse_object(){
        try{
            //"yyyy-MM-dd'T'HH:mm:ss"
            String to_parse = Integer.toString(year)+"-"+Integer.toString(month_number)+"-"+Integer.toString(day_number);
            
            if ( month_number <10 ){
                to_parse = Integer.toString(year)+"-"+number_0adder(month_number)+"-"+Integer.toString(day_number);
            }
            
            
            to_parse = to_parse + "T" + Integer.toString(hours)+":"+Integer.toString(minutes)+":"+Integer.toString(seconds);
            return DATE_FORMAT.parse(to_parse);

        }catch(ParseException e){
            System.out.println(e.toString());
            return null;
        }
    }
    
    /**
     * Function for comparing two dates
     * @param to_compare
     * @return Integer
     * mode:
     * 1 - object is after the date to compare
     * 2 - object before the date to compare
     * 0 - objects the same
     */
    public int compare(Date to_compare){
        Date obj = this.parse_object();
        
        if ( obj.compareTo(to_compare) > 0 ){
            // obj is first, OBJ BEFORE TO_COMPARE
            return 1;
        }
        else if ( obj.compareTo(to_compare) < 0 ){
            // TO_COMPARE BEFORE OBJ
            return 2;
        }
        else{
            return 0;
        }
    }
    
    /**
     * Function for translating number of month to corresponded name
     * @return String
     */
    public String month_number_translator(){
        return month_data[this.month_number-1];
    }

    //----------end of functions for preparing data
    
    /**
     * Function for adding two objects type string
     * @param data
     * @param separator
     * @param source
     * @return String
     */
    public String string_adder(String data,String separator,String source){
        return source+ separator +data;
    }
    
    /**
     * Function for showing data from object
     * @return 
     */
    public String visual_data(){
        String data_to_Ret = "";
        data_to_Ret = string_adder(day_name,"",data_to_Ret);
        data_to_Ret = string_adder(Integer.toString(day_number),",",data_to_Ret);
        data_to_Ret = string_adder(month_name," ",data_to_Ret);
        data_to_Ret = string_adder(Integer.toString(year)," ",data_to_Ret);
        data_to_Ret = string_adder("("," ",data_to_Ret);
        data_to_Ret = string_adder(Integer.toString(hours),"",data_to_Ret);
        data_to_Ret = string_adder(Integer.toString(minutes),":",data_to_Ret);
        data_to_Ret = string_adder(Integer.toString(seconds),":",data_to_Ret);
        data_to_Ret = string_adder(")","",data_to_Ret);
        data_to_Ret = data_to_Ret + "\n----------------------\n" + "leap year: "+Boolean.toString(leap_year)+
                "\n"+"month in number: "+Integer.toString(month_number);
        
        
        return data_to_Ret;
    }
}

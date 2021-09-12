/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.gui;

import com.jakubwawak.database.Database;
import com.jakubwawak.database.Database_Object_Updater;
import com.jakubwawak.tick2.Tick_Category;
import com.jakubwawak.tick2.Tick_HashtagT;
import com.jakubwawak.tick2.Tick_Place;
import com.jakubwawak.tick2.Tick_Tag;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *Object for creating GUI window to addind data ( universal )
 * @author jakubwawak
 */
public class GUI_datainput_universal_window extends javax.swing.JDialog {

    /**
     * Creates new form GUI_datainput_universal_window
     * modes:
     * "place" - adding place
     * "category" - adding category
     * "hashtag table " - adding hashtag table
     * "tag" - adding tag
     * programs:
     * 1 - new to add
     * every other - id of the object to delete
     */
    Database database;
    String mode;
    int program;

    
    public GUI_datainput_universal_window(javax.swing.JDialog parent, boolean modal,Database database,String mode,int program) throws SQLException {
        super(parent, modal);
        this.database = database;
        this.mode = mode;
        this.program = program;
        initComponents();
        button_add.setVisible(true);
        load_components();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /**
     * Function for reloading data
     */
    void load_components() throws SQLException{      
        if (program == 1){
            // loading main components
            switch(mode){
                case "category":
                    textfield_name.setText("Name of the category");
                    textfield_note.setText("Note for the category");
                    button_optional1.setVisible(false);
                    button_optional2.setVisible(false);
                    break;
                
                case "place":
                    textfield_name.setText("Name of the place");
                    textfield_note.setEditable(false);
                    button_optional1.setText("Link address");
                    button_optional1.setVisible(false);
                    break;
                case "hashtag table":
                    textfield_name.setText("Name of the hashtag table");
                    textfield_note.setText("Note for the hashtag table");
                    textfield_note.setEditable(true);
                    button_optional1.setText("Link tags");
                    button_optional1.setVisible(false);
                    button_optional2.setVisible(false);
                    break;
                case "tag":
                    textfield_name.setText("Name of the tag");
                    textfield_note.setText("Note for the tag");
                    textfield_note.setEditable(true);
                    button_optional1.setVisible(false);
                    button_optional2.setVisible(false);
                    break;
                default:
                    break;
            }
        }
        else{
            switch(mode){
                case "category":
                    Tick_Category tc = new Tick_Category(database.return_TB_collection(database.logged, "category", program));
                    textfield_name.setText(tc.category_name);
                    textfield_note.setText(tc.category_note);
                    textfield_note.setEditable(true);
                    button_optional1.setVisible(false);
                    button_optional2.setVisible(false);
                    break;
                case "place":
                    Tick_Place tp = new Tick_Place(database.return_TB_collection(database.logged, "place", program));
                    textfield_name.setText(tp.place_name);
                    textfield_note.setEditable(false);
                    button_optional1.setText("Add address");
                    button_optional1.setVisible(true);
                    button_optional2.setVisible(false);
                    break;
                case "hashtag table":
                    Tick_HashtagT th = new Tick_HashtagT(database.return_TB_collection(database.logged, "hashtag table", program));
                    textfield_name.setText(th.hashtag_table_name);
                    textfield_note.setText(th.hashtag_table_note);
                    textfield_note.setEditable(true);
                    button_optional1.setText("Link tags");
                    button_optional1.setVisible(true);
                    button_optional2.setVisible(false);
                    break;
                case "tag":
                    Tick_Tag tt = new Tick_Tag(database.return_TB_collection(database.logged, "tag", program));
                    textfield_name.setText(tt.tag_name);
                    textfield_note.setText(tt.tag_note);
                    button_optional1.setVisible(false);
                    textfield_note.setEditable(true);
                    button_optional2.setVisible(false);
                    break;
                default:
                    break;
            }
        }
    }
    
    /**
     * Function for checking corectness of window
     * @return boolean
     */
    boolean check_if_correct(){
        if ( !textfield_name.getText().equals("") || !textfield_name.getText().equals("No name!") ){
            if ( textfield_note.getText().equals("")){
                textfield_note.setText("No note");
            }
            return true;
        }
        return false;
    }
    
    /**
     * Function for behaviour of succesfull addon of element
     * modes:
     * 1 - succesful
     * 2 - failed
     */
    void close_window_activity(int mode){
        if ( mode == 1){
            textfield_name.setEnabled(false);
            textfield_note.setEnabled(false);
            if ( this.mode.equals("hashtag table") || this.mode.equals("place") ){
                button_optional1.setVisible(true);
            }
            button_optional2.setVisible(false);
            button_add.setEnabled(false);
            if ( program != 1){
                button_add.setText("Updated");
                button_optional1.setEnabled(true);
            }
            else{
                button_add.setText("Done");
            }
        }
        else if (mode == 2){
            textfield_name.setEnabled(false);
            textfield_note.setEnabled(false);
            button_optional1.setVisible(false);
            button_optional2.setVisible(false);
            button_add.setEnabled(false);
            button_add.setText("Failed");
        }
    }
    
    /**
     * Function for gathering data from windows
     * @throws SQLException 
     */
    void gather_data() throws SQLException{

        if (program == 1){
            switch(mode){
                case "category":
                    Tick_Category tc = new Tick_Category();
                    tc.category_name = textfield_name.getText();
                    tc.category_note = textfield_note.getText();
                    tc.owner_id = database.logged.owner_id;
                    tc.wall_updater();
                    if ( database.add_category(tc) ){
                        close_window_activity(1);
                    }
                    else{
                        close_window_activity(2);
                    }
                    break;
                
                case "place":
                    Tick_Place tp = new Tick_Place();
                    tp.place_name = textfield_name.getText();
                    tp.owner_id = database.logged.owner_id;
                    tp.wall_updater();
                    if ( database.add_place(tp) ){
                        close_window_activity(1);
                    }
                    else{
                        close_window_activity(2);
                    }
                    break;
                case "hashtag table":
                    Tick_HashtagT th = new Tick_HashtagT();
                    th.hashtag_table_name = textfield_name.getText();
                    th.hashtag_table_note = textfield_note.getText();
                    th.owner_id = database.logged.owner_id;
                    th.wall_updater();
                    if ( database.add_hashtagT(th) ){
                        close_window_activity(1);
                    }
                    else{
                        close_window_activity(2);
                    }
                    
                    break;
                case "tag":
                    Tick_Tag tt = new Tick_Tag();
                    tt.tag_name = textfield_name.getText();
                    tt.tag_note = textfield_note.getText();
                    tt.owner_id = database.logged.owner_id;
                    tt.wall_updater();
                    if ( database.add_tag(tt) ){
                        close_window_activity(1);
                    }
                    else{
                        close_window_activity(2);
                    }
                    
                    break;
                default:
                    break;
            }
        }
        // edit data
        else{
            Database_Object_Updater dou = new Database_Object_Updater(database);
            switch(mode){
                case "category":
                    Tick_Category tc = new Tick_Category();
                    tc.category_id = program;
                    tc.category_name = textfield_name.getText();
                    tc.category_note = textfield_note.getText();
                    tc.owner_id = database.logged.owner_id;
                    tc.wall_updater();
                    
                    if ( dou.update_record(tc.wall_updater(), mode)){
                        close_window_activity(1);
                    }
                    else{
                        close_window_activity(2);
                    }
                    
                    break;
                
                case "place":
                    Tick_Place tp = new Tick_Place();
                    tp.place_id = program;
                    tp.place_name = textfield_name.getText();
                    tp.owner_id = database.logged.owner_id;
                    tp.wall_updater();
                    if ( dou.update_record(tp.wall_updater(), mode)){
                        close_window_activity(1);
                    }
                    else{
                        close_window_activity(2);
                    }
                    break;
                case "hashtag table":
                    Tick_HashtagT th = new Tick_HashtagT();
                    th.hashtag_table_id = program;
                    th.hashtag_table_name = textfield_name.getText();
                    th.hashtag_table_note = textfield_note.getText();
                    th.owner_id = database.logged.owner_id;
                    th.wall_updater();
                    if ( dou.update_record(th.wall_updater(), mode)){
                        close_window_activity(1);
                    }
                    else{
                        close_window_activity(2);
                    }
                    
                    break;
                case "tag":
                    Tick_Tag tt = new Tick_Tag();
                    tt.tag_id = program;
                    tt.tag_name = textfield_name.getText();
                    tt.tag_note = textfield_note.getText();
                    tt.owner_id = database.logged.owner_id;
                    tt.wall_updater();
                    if ( dou.update_record(tt.wall_updater(), mode)){
                        close_window_activity(1);
                    }
                    else{
                        close_window_activity(2);
                    }
                    
                    break;
                default:
                    break;
            }
        } 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        textfield_name = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        textfield_note = new javax.swing.JTextArea();
        button_optional1 = new javax.swing.JButton();
        button_add = new javax.swing.JButton();
        button_optional2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        textfield_name.setText("jTextField1");

        textfield_note.setColumns(20);
        textfield_note.setRows(5);
        jScrollPane1.setViewportView(textfield_note);

        button_optional1.setText("jButton1");
        button_optional1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_optional1ActionPerformed(evt);
            }
        });

        button_add.setText("Add");
        button_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_addActionPerformed(evt);
            }
        });

        button_optional2.setText("jButton1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textfield_name)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(button_optional1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_optional2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 106, Short.MAX_VALUE)
                        .addComponent(button_add)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(textfield_name, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_optional1)
                    .addComponent(button_add)
                    .addComponent(button_optional2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_addActionPerformed
        if ( check_if_correct() ){
            try {
                gather_data();
            } catch (SQLException ex) {
                new message_window_jdialog(this,true,"Error: "+ex.toString());
            }
        }
    }//GEN-LAST:event_button_addActionPerformed

    private void button_optional1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_optional1ActionPerformed
        switch(mode){
            case "hashtag table": 
                if ( program == 1 ){
                    int index = 0;
                    try {
                        index = database.get_hashtagtable_id_byname(textfield_name.getText());
                    } catch (SQLException ex) {
                        new message_window_jdialog(this,true,"Error: "+ex.toString());
                    }
                    try {
                        new GUI_addtags_window(this,true,database,index);
                    } catch (SQLException ex) {
                        new message_window_jdialog(this,true,"Error: "+ex.toString());
                    }
                }
                else{
                    try {
                        new GUI_addtags_window(this,true,database,program);
                    } catch (SQLException ex) {
                        new message_window_jdialog(this,true,"Error: "+ex.toString());
                        }
                    }
                break;
            case "place":
                if ( program == 1 ){
                    
                }
                break;
            default:
                break;
        }
    }//GEN-LAST:event_button_optional1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_add;
    private javax.swing.JButton button_optional1;
    private javax.swing.JButton button_optional2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField textfield_name;
    private javax.swing.JTextArea textfield_note;
    // End of variables declaration//GEN-END:variables
}

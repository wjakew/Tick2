/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.gui;

import com.jakubwawak.database.Database;
import com.jakubwawak.database.Database_Object_Updater;
import com.jakubwawak.database.Database_Scene;
import com.jakubwawak.tick2.Tick_Scene;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;

/**
 *Object for creating new GUI window for adding scene
 * @author jakubwawak
 */
public class GUI_addscene_window extends javax.swing.JDialog {

    /**
     * Creates new form GUI_addscene_window
     */
    Database database;
    Database_Scene ds;
    ArrayList<String> data;
    int program;
    int category_selectedvalue,place_selectedvalue,hashtagtable_selectedvalue;
    
    
    /**
     * Constructor
     * @param parent
     * @param modal
     * @param database
     * @param program
     * @throws SQLException
     * @throws SQLException 
     */
    public GUI_addscene_window(java.awt.Frame parent, boolean modal,Database database,int program) throws SQLException, SQLException {
        super(parent, modal);
        
        this.program = program;
        this.database = database;
        data = new ArrayList<>();
        ds = new Database_Scene(database);
        
        initComponents();
        setLocationRelativeTo(null);
        load_components();
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scenename_textfield = new javax.swing.JTextField();
        place_combobox = new javax.swing.JComboBox<>();
        category_combobox = new javax.swing.JComboBox<>();
        hashtagtable_combobox = new javax.swing.JComboBox<>();
        done_button = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        details_field = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        note_field = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Scene");

        scenename_textfield.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        scenename_textfield.setText("Scene Name");
        scenename_textfield.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                scenename_textfieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                scenename_textfieldFocusLost(evt);
            }
        });

        place_combobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        place_combobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                place_comboboxActionPerformed(evt);
            }
        });

        category_combobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        category_combobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                category_comboboxActionPerformed(evt);
            }
        });

        hashtagtable_combobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        hashtagtable_combobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hashtagtable_comboboxActionPerformed(evt);
            }
        });

        done_button.setText("Done");
        done_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                done_buttonActionPerformed(evt);
            }
        });

        details_field.setColumns(20);
        details_field.setRows(5);
        jScrollPane2.setViewportView(details_field);

        note_field.setColumns(20);
        note_field.setRows(5);
        note_field.setText("No notes");
        jScrollPane1.setViewportView(note_field);

        jLabel1.setText("---");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addComponent(jScrollPane2)
                            .addComponent(scenename_textfield, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                            .addComponent(place_combobox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(category_combobox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(hashtagtable_combobox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(done_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(157, 157, 157)
                        .addComponent(jLabel1)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scenename_textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(place_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(category_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(hashtagtable_combobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(done_button)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void scenename_textfieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_scenename_textfieldFocusGained
        if ( scenename_textfield.getText().equals("") || scenename_textfield.getText().equals("Scene name")){
            scenename_textfield.setText("");
        }
    }//GEN-LAST:event_scenename_textfieldFocusGained

    private void scenename_textfieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_scenename_textfieldFocusLost
        if ( !scenename_textfield.getText().equals("") || !scenename_textfield.getText().equals("Scene name")){
            data.add("Scene name: "+ scenename_textfield.getText());
            update_detailsfield();
        }
    }//GEN-LAST:event_scenename_textfieldFocusLost

    private void place_comboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_place_comboboxActionPerformed
        try {
            place_selectedvalue = database.get_place_id_byname(place_combobox.getSelectedItem().toString());
            System.out.println(place_selectedvalue+" : "+place_combobox.getSelectedItem().toString());
            if ( place_selectedvalue != -1 ){
                data.add("Selected place: "+place_combobox.getSelectedItem().toString());
                update_detailsfield();
            }
            else if ( place_combobox.getSelectedItem().toString().equals("Manage...")){
                new GUI_dataadd_universal_window(this,true,database,"place");
                place_combobox.setModel(get_defaultcomboboxmodel("place"));
            }
        } catch (SQLException ex) {
            new message_window_jdialog(this,true,"Error: "+ex.toString());
        }
    }//GEN-LAST:event_place_comboboxActionPerformed

    private void hashtagtable_comboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hashtagtable_comboboxActionPerformed
        try {
            hashtagtable_selectedvalue = database.get_hashtagtable_id_byname(hashtagtable_combobox.getSelectedItem().toString());
            System.out.println(hashtagtable_selectedvalue+" : "+hashtagtable_combobox.getSelectedItem().toString());
            if ( hashtagtable_selectedvalue != -1 ){
                data.add("Selected hashtag table: " + hashtagtable_combobox.getSelectedItem().toString());
                update_detailsfield();
            }
            else if ( hashtagtable_combobox.getSelectedItem().toString().equals("Manage...")){
                new GUI_dataadd_universal_window(this,true,database,"hashtag table");
                hashtagtable_combobox.setModel(get_defaultcomboboxmodel("hashtag table"));
                
            }
        } catch (SQLException ex) {
            new message_window_jdialog(this,true,"Error: "+ex.toString());
        }
    }//GEN-LAST:event_hashtagtable_comboboxActionPerformed

    private void category_comboboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_category_comboboxActionPerformed
        try {
            category_selectedvalue = database.get_category_id_byname(category_combobox.getSelectedItem().toString());
            System.out.println(category_selectedvalue+" : "+category_combobox.getSelectedItem().toString());
            if ( category_selectedvalue != -1 ){
                data.add("Selected place: "+category_combobox.getSelectedItem().toString());
                update_detailsfield();
            }
            else if ( category_combobox.getSelectedItem().toString().equals("Manage...")){
                new GUI_dataadd_universal_window(this,true,database,"category");
                category_combobox.setModel(get_defaultcomboboxmodel("category"));
                
            }
        } catch (SQLException ex) {
            new message_window_jdialog(this,true,"Error: "+ex.toString());
        }
    }//GEN-LAST:event_category_comboboxActionPerformed

    private void done_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_done_buttonActionPerformed
        try {
            boolean category_notused = false, place_notused = false,hashtagtable_notused = false;
            if ( category_combobox.getSelectedItem().toString().equals("Manage..") || category_combobox.getSelectedItem().toString().equals("None")){
                category_notused = true;
            }
            else{
                category_selectedvalue = database.get_category_id_byname(category_combobox.getSelectedItem().toString());
            }
            
            if ( place_combobox.getSelectedItem().toString().equals("Manage..") || place_combobox.getSelectedItem().toString().equals("None")){
                place_notused = true;
            }
            else{
                place_selectedvalue = database.get_place_id_byname(place_combobox.getSelectedItem().toString());
            }
            if ( hashtagtable_combobox.getSelectedItem().toString().equals("Manage..") || hashtagtable_combobox.getSelectedItem().toString().equals("None")){
                hashtagtable_notused = true;
            }
            else{
                hashtagtable_selectedvalue = database.get_hashtagtable_id_byname(hashtagtable_combobox.getSelectedItem().toString());
            }
            
            if ( category_selectedvalue != -1 && hashtagtable_selectedvalue != -1 && place_selectedvalue != -1){
                Tick_Scene ts = new Tick_Scene();
                
                ts.scene_name = scenename_textfield.getText();
                if ( category_notused ){
                    ts.category_id = -1;
                }
                else{
                    ts.category_id = category_selectedvalue;
                }
                
                if ( place_notused ){
                    ts.place_id = -1;
                }
                else{
                    ts.place_id = place_selectedvalue;
                }
                
                if ( hashtagtable_notused ){
                    ts.hashtag_table_id = -1;
                }
                else{
                    ts.hashtag_table_id = hashtagtable_selectedvalue;
                }
                 
                ts.owner_id = database.logged.owner_id;
                ts.scene_note = note_field.getText();
                if(program != 0){
                    ts.scene_id = program;
                }
                ts.wall_updater();
                
                if ( program == 0){
                    if (ds.add_scene(ts) ) {
                        mark_window_done(0);
                    }
                    else{
                        mark_window_done(1);
                        }
                }
                else{
                    // data to update
                    Database_Object_Updater dou = new Database_Object_Updater(database);
                    if ( dou.update_record(ts.wall_updater(), "scene")){
                        mark_window_done(2);
                    }
                    else{
                        mark_window_done(1);
                    }
                }
            }
 
        } catch (SQLException ex) {
            new message_window_jdialog(this,true,"Error: "+ex.toString());
        }
        
        
    }//GEN-LAST:event_done_buttonActionPerformed

    /**
     * Function gets models for comboboxes
     * @param mode
     * @return DefaultComboBoxModel
     * modes:
     * category - returns name of elements in CATEGORY table
     * place - returns name of elements in PLACE table
     * hashtag table - returns name of elements in HASHTAG_TABLE table
     */
    DefaultComboBoxModel get_defaultcomboboxmodel(String mode) throws SQLException{
        return new DefaultComboBoxModel(database.get_element_name(mode,1).toArray());
    }
    
    /**
     * Function for updating details in field
     */
    void update_detailsfield(){
        done_button.setText("Done");
        String content = "";
        
        for(String line : data){
            content = content + line + "\n";
        }
        
        details_field.setText(content);
    }
    
    /**
     * Function for loading components
     */
    void load_components() throws SQLException{
        if ( program == 0){
            category_selectedvalue = -1;
            place_selectedvalue = -1;
            hashtagtable_selectedvalue= -1;

            details_field.setEditable(false);

            data.add("Empty");
            place_combobox.setModel(get_defaultcomboboxmodel("place"));
            category_combobox.setModel(get_defaultcomboboxmodel("category"));
            hashtagtable_combobox.setModel(get_defaultcomboboxmodel("hashtag table"));
            update_detailsfield();
        }
        else {
            // edit mode
            Tick_Scene ts = ds.get_scene_object(program);
            scenename_textfield.setText(ts.scene_name);
            note_field.setText(ts.scene_note);
            place_combobox.setModel(get_defaultcomboboxmodel("place"));
            category_combobox.setModel(get_defaultcomboboxmodel("category"));
            hashtagtable_combobox.setModel(get_defaultcomboboxmodel("hashtag table"));
            data.add("Edit mode is set");
            data.add("Scene id: "+Integer.toString(ts.scene_id));
            update_detailsfield();
        }
    }
    
    /**
     * Function setting window behaviour
     * modes:
     * 0 - done
     * 1 - failed
     * 2 - updated
     */
    void mark_window_done(int mode){
        if ( mode == 0 || mode == 2){
            category_combobox.setEditable(false);
            hashtagtable_combobox.setEditable(false);
            place_combobox.setEditable(false);
            note_field.setEditable(false);
            scenename_textfield.setEditable(false);
            
            if ( mode == 0){
                done_button.setEnabled(false);
                done_button.setText("Scene added");
            }
            else {
                done_button.setEnabled(false);
                done_button.setText("Updated");
            }
        }
        // failed
        else if ( mode == 1){
            done_button.setText("Failed");
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> category_combobox;
    private javax.swing.JTextArea details_field;
    private javax.swing.JButton done_button;
    private javax.swing.JComboBox<String> hashtagtable_combobox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea note_field;
    private javax.swing.JComboBox<String> place_combobox;
    private javax.swing.JTextField scenename_textfield;
    // End of variables declaration//GEN-END:variables
}

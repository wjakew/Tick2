/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.gui;

import com.jakubwawak.database.Database;
import com.jakubwawak.database.Database_Tick;
import com.jakubwawak.tick2.Tick_Note;
import com.jakubwawak.tick2.Tick_Tick;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author jakubwawak
 */
public class GUI_addtick_moreoptions_window extends javax.swing.JDialog {

    /**
     * Creates new form GUI_addtick_moreoptions_window
     */
    Tick_Tick tick_to_extend;
    Database database;
    String[] data_priority;
    // data from the components
    String choosen_category = "" ,choosen_place = "",choosen_priority,choosen_hashtag_table;
    
    /**
     * Main constructor
     * @param parent
     * @param modal
     * @param to_handle
     * @param database
     * @throws SQLException 
     */
    public GUI_addtick_moreoptions_window(javax.swing.JDialog parent, boolean modal,Tick_Tick to_handle,Database database) throws SQLException {
        super(parent, modal);
        data_priority= new String[] {"1","2","3","4","5","6","7","8","9","10"};
        
        tick_to_extend = to_handle;
        this.database = database;
        initComponents();
        
        textarea_tickdata.setEditable(false);
        reload_window();
        
        setLocationRelativeTo(null);
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

        textfield_tickname = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        textarea_notes = new javax.swing.JTextArea();
        combobox_category = new javax.swing.JComboBox<>();
        combobox_place = new javax.swing.JComboBox<>();
        combobox_hashtagtable = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        button_done = new javax.swing.JButton();
        combobox_priority = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        textarea_tickdata = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        textfield_tickname.setText("jTextField1");

        textarea_notes.setColumns(20);
        textarea_notes.setRows(5);
        jScrollPane1.setViewportView(textarea_notes);

        combobox_category.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        combobox_category.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combobox_categoryActionPerformed(evt);
            }
        });

        combobox_place.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        combobox_place.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combobox_placeActionPerformed(evt);
            }
        });

        combobox_hashtagtable.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        combobox_hashtagtable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combobox_hashtagtableActionPerformed(evt);
            }
        });

        jLabel1.setText("Your actual Tick data:");

        button_done.setText("Done");
        button_done.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_doneActionPerformed(evt);
            }
        });

        combobox_priority.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        combobox_priority.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combobox_priorityActionPerformed(evt);
            }
        });

        textarea_tickdata.setColumns(20);
        textarea_tickdata.setRows(5);
        jScrollPane2.setViewportView(textarea_tickdata);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(textfield_tickname)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(combobox_category, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(combobox_place, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(combobox_hashtagtable, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(combobox_priority, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(button_done))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textfield_tickname, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(combobox_category, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combobox_place, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combobox_hashtagtable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(button_done)
                    .addComponent(combobox_priority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // action functions
    /**
     * Function for handling data from components ( category combobox )
     * @param evt 
     */
    private void combobox_categoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_categoryActionPerformed
        choosen_category = combobox_category.getSelectedItem().toString();
        if ( choosen_category.equals("Manage...")){
            try {
                new GUI_dataadd_universal_window(this,true,database,"category");
                combobox_category.setModel(update_combobox("category"));
            } catch (SQLException ex) {
                new message_window_jdialog(this,true,"Error: "+ex.toString());
            }
        }
    }//GEN-LAST:event_combobox_categoryActionPerformed

    private void combobox_placeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_placeActionPerformed
        choosen_place = combobox_place.getSelectedItem().toString();
        if ( choosen_place.equals("Manage...")){
            try {
                new GUI_dataadd_universal_window(this,true,database,"place");
                combobox_place.setModel(update_combobox("place"));
            } catch (SQLException ex) {
                new message_window_jdialog(this,true,"Error: "+ex.toString());
            }
        }
    }//GEN-LAST:event_combobox_placeActionPerformed

    private void combobox_hashtagtableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_hashtagtableActionPerformed
        choosen_hashtag_table = combobox_hashtagtable.getSelectedItem().toString();
        if ( choosen_hashtag_table.equals("Manage...")){
            try {
                new GUI_dataadd_universal_window(this,true,database,"hashtag table");
                combobox_hashtagtable.setModel(update_combobox("hashtag table"));
            } catch (SQLException ex) {
                new message_window_jdialog(this,true,"Error: "+ex.toString());
            }
        }
    }//GEN-LAST:event_combobox_hashtagtableActionPerformed

    private void combobox_priorityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_priorityActionPerformed
        choosen_priority = combobox_priority.getSelectedItem().toString();
        System.out.println(choosen_priority);
    }//GEN-LAST:event_combobox_priorityActionPerformed

    private void button_doneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_doneActionPerformed
        // updating data
        tick_to_extend.tick_name = textfield_tickname.getText();
        // updating note
        Tick_Note tn = new Tick_Note();
        tn.note_content = textarea_notes.getText();
        tn.owner_id = database.logged.owner_id;
        tn.wall_updater();
        try {
            database.add_note(tn);
            tick_to_extend.note_id = database.get_last_id("NOTE");
        } catch (SQLException ex) {
            new message_window_jdialog(this,true,"Error: "+ex.toString());
        }
        // updating categories
        if ( combobox_category.getSelectedItem().toString().equals("Manage...") ){
            tick_to_extend.category_id = 1;
        }
        else{
            try { 
                tick_to_extend.category_id = database.get_category_id_byname(combobox_category.getSelectedItem().toString());
            } catch (SQLException ex) {
                new message_window_jdialog(this,true,"Error: "+ex.toString());
            }
        }
        // updating place
        if ( combobox_place.getSelectedItem().toString().equals("Manage...")){
            tick_to_extend.place_id = 1;
        }
        else{
            try {
                tick_to_extend.place_id = database.get_place_id_byname(combobox_place.getSelectedItem().toString());
            } catch (SQLException ex) {
                new message_window_jdialog(this,true,"Error: "+ex.toString());
            }
        }
        // updating hashtagtable
        if ( combobox_hashtagtable.getSelectedItem().toString().equals("Manage...")){
            tick_to_extend.hashtag_table_id = 1;
        }
        else{
            try {
                tick_to_extend.hashtag_table_id = database.get_hashtagtable_id_byname(combobox_hashtagtable.getSelectedItem().toString());
            } catch (SQLException ex) {
                new message_window_jdialog(this,true,"Error: "+ex.toString());
            }
        }
        // updating priority
        tick_to_extend.tick_priority = Integer.parseInt(combobox_priority.getSelectedItem().toString());
        
        // wrapping tick
        tick_to_extend.wall_updater();
        try {
            update_textarea_actual_data();
            dispose();
        } catch (SQLException ex) {
            new message_window_jdialog(this,true,"Error: "+ex.toString());
        }
    }//GEN-LAST:event_button_doneActionPerformed
    /**
     * Function for updating model of jComboBox object
     * @param mode
     * @return DefaultComboBoxModel
     * modes:
     * category - returns name of elements in CATEGORY table
     * places - returns name of elements in PLACE table
     * hashtag - returns name of elements in HASHTAG_TABLE table
     */
    DefaultComboBoxModel update_combobox(String mode) throws SQLException{
        ArrayList<String> data_tofill = database.get_element_name(mode,0);
        return new DefaultComboBoxModel(data_tofill.toArray());
    }
    
    /**
     * Function for updating data on textarea used to show tick data
     * @throws SQLException 
     */
    void update_textarea_actual_data() throws SQLException{
        String to_fill = "";
        ArrayList<String> to_show = tick_to_extend.get_detailed_data(database);
        for (String line : to_show){
            
            to_fill = to_fill + line + "\n";
        }
        
        textarea_tickdata.setText(to_fill);
    }
    
    /**
     * Function for reloading all window content
     */
    void reload_window() throws SQLException{
        Database_Tick dt = new Database_Tick(database);
        textfield_tickname.setText(tick_to_extend.tick_name);
        textarea_notes.setText(dt.get_note(tick_to_extend.tick_id));
        combobox_category.setModel(update_combobox("category"));
        combobox_place.setModel(update_combobox("place"));
        combobox_hashtagtable.setModel(update_combobox("hashtag table"));
        combobox_priority.setModel(new DefaultComboBoxModel(data_priority));
        update_textarea_actual_data();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_done;
    private javax.swing.JComboBox<String> combobox_category;
    private javax.swing.JComboBox<String> combobox_hashtagtable;
    private javax.swing.JComboBox<String> combobox_place;
    private javax.swing.JComboBox<String> combobox_priority;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea textarea_notes;
    private javax.swing.JTextArea textarea_tickdata;
    private javax.swing.JTextField textfield_tickname;
    // End of variables declaration//GEN-END:variables
}

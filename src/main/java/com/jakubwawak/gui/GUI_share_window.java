/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.gui;

import com.jakubwawak.database.Database;
import com.jakubwawak.tick2.Share;
import com.jakubwawak.tick2.Tick_Tick;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *Object for creating sharing window
 * @author jakubwawak
 */
public class GUI_share_window extends javax.swing.JDialog {

    /**
     * Creates new form GUI_share_window
     */
    Database database;
    int tick_id;
    Share tso;
    
    
    /**
     * Main constructor
     * @param parent
     * @param modal
     * @param database
     * @param tick_id
     * @throws SQLException 
     */
    public GUI_share_window(java.awt.Frame parent, boolean modal,Database database,int tick_id) throws SQLException {
        super(parent, modal);
        this.tick_id = tick_id;
        this.database = database;
        tso = new Share(database);
        
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

        jScrollPane1 = new javax.swing.JScrollPane();
        textarea_tick_details = new javax.swing.JTextArea();
        textfield_user_login = new javax.swing.JTextField();
        button_share = new javax.swing.JButton();
        label_confirm_data = new javax.swing.JLabel();
        label_readytoshare = new javax.swing.JLabel();
        button_confirm = new javax.swing.JButton();
        button_history = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Share");

        textarea_tick_details.setColumns(20);
        textarea_tick_details.setRows(5);
        jScrollPane1.setViewportView(textarea_tick_details);

        textfield_user_login.setText("jTextField1");
        textfield_user_login.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textfield_user_loginFocusGained(evt);
            }
        });

        button_share.setText("Share");
        button_share.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_shareActionPerformed(evt);
            }
        });

        label_confirm_data.setText("jLabel1");

        label_readytoshare.setText("Ready to share");

        button_confirm.setText("Confirm");
        button_confirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_confirmActionPerformed(evt);
            }
        });

        button_history.setText("History");
        button_history.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_historyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addComponent(textfield_user_login)
                            .addComponent(button_share, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(label_confirm_data)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(157, 157, 157)
                        .addComponent(label_readytoshare)
                        .addGap(0, 144, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(button_confirm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(button_history)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(button_history)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textfield_user_login, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_share)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(label_confirm_data)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(label_readytoshare)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button_confirm, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_shareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_shareActionPerformed
        if(!textfield_user_login.getText().equals("User login") || !textfield_user_login.getText().equals("")){
            try {
                if ( database.ret_owner_id(textfield_user_login.getText()) != -1 ){
                    // we found user
                    label_confirm_data.setText("Found data, user login: "+textfield_user_login.getText()
                            +"("+Integer.toString(database.ret_owner_id(textfield_user_login.getText()))+")");
                    button_share.setEnabled(false);
                    textfield_user_login.setEnabled(false);
                    button_confirm.setEnabled(true);
                    button_confirm.setVisible(true);
                    label_readytoshare.setVisible(true);
                            
                }
                else{
                    textfield_user_login.setText("User login");
                    label_confirm_data.setText("Failed to find user");
                }
            } catch (SQLException ex) {
                Logger.getLogger(GUI_share_window.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        else{
            textfield_user_login.setText("Wrong user login");
        }
    }//GEN-LAST:event_button_shareActionPerformed

    private void button_historyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_historyActionPerformed
        if ( button_history.getText().equals("History")){
            // do history stuff
            ArrayList<String>  to_show = new ArrayList<>();
            String content = "";
            try {
                to_show = tso.return_data_history();
            } catch (SQLException ex) {
                Logger.getLogger(GUI_share_window.class.getName()).log(Level.SEVERE, null, ex);
            }
            if ( to_show.isEmpty() ){
                content = "Failed";
            }
            for(String line : to_show){
                content = content + line + "\n";
            }
            textarea_tick_details.setText(content);
            button_history.setText("Done");
            button_confirm.setEnabled(false);
            button_share.setEnabled(false);
            textfield_user_login.setEnabled(false);
        }
        else{
            try {
                load_components();
            } catch (SQLException ex) {
                new message_window_jdialog(this,true,"Error: "+ex.toString());
            }
        }
    }//GEN-LAST:event_button_historyActionPerformed

    private void button_confirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_confirmActionPerformed
        try {
            if (tso.share_tick(tick_id, database.ret_owner_id(textfield_user_login.getText())) == 1){
                button_confirm.setText("Tick shared");
                button_confirm.setEnabled(false);
            }
            else{
                button_confirm.setText("Failed to share tick, check log");
            }
        } catch (SQLException ex) {
            new message_window_jdialog(this,true,"Error: "+ex.toString());
        }
    }//GEN-LAST:event_button_confirmActionPerformed

    private void textfield_user_loginFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textfield_user_loginFocusGained
        textfield_user_login.setText("");
    }//GEN-LAST:event_textfield_user_loginFocusGained

    /**
     * Function for loading components
     */
    void load_components() throws SQLException{
        String filler = "";
        Tick_Tick tt = new Tick_Tick(database.return_TB_collection(database.logged, "tick", tick_id));
        for(String line : tt.get_detailed_data(database)){
            filler = filler + line + "\n";
        }
        textarea_tick_details.setText(filler);
        textfield_user_login.setText("User login");
        button_confirm.setEnabled(true);
        button_share.setEnabled(true);
        textfield_user_login.setEnabled(true);
        
        // setting initial window view
        label_readytoshare.setVisible(false);
        label_confirm_data.setVisible(false);
        button_confirm.setVisible(false);
        button_history.setText("History");
        
        
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_confirm;
    private javax.swing.JButton button_history;
    private javax.swing.JButton button_share;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_confirm_data;
    private javax.swing.JLabel label_readytoshare;
    private javax.swing.JTextArea textarea_tick_details;
    private javax.swing.JTextField textfield_user_login;
    // End of variables declaration//GEN-END:variables
}

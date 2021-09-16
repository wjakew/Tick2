/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.gui;

import com.jakubwawak.database.Database;
import com.jakubwawak.tick2.Configuration;
import java.io.IOException;

/**
 *Function for creating new configuration
 * @author kubaw
 */
public class GUI_connection_window extends javax.swing.JDialog {

    /**
     * Creates new form GUI_connection_window
     */
    Configuration config;
    Database database;
    public GUI_connection_window(java.awt.Frame parent, boolean modal,Configuration config,Database database) {
        super(parent, modal);
        this.config = config;
        this.database = database;
        initComponents();
        this.setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public GUI_connection_window(javax.swing.JDialog parent, boolean modal,Configuration config,Database database) {
        super(parent, modal);
        this.config = config;
        this.database = database;
        initComponents();
        this.setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public GUI_connection_window(Configuration config,Database database) {
        this.config = config;
        this.database = database;
        initComponents();
        this.setLocationRelativeTo(null);
        setVisible(true);
    }
    /**
     * Function for loading fields and connecting to databas
     */
    void load_object() throws IOException{
        if (validate_fields()){
            
            database.connect(field_ip.getText(),field_name.getText(),field_user.getText(),field_password.getText());
            if ( database.connected ){
                new message_window_jdialog(this,true,"Connected to database");
                config.raw_path = "config.tick";
                config.database_ip = field_ip.getText();
                config.database_name = field_name.getText();
                config.database_user = field_user.getText();
                config.database_pass = field_password.getText();
                config.state = "gui";
                config.save_to_file();
                dispose();
            }
        }
        else{
            new message_window_jdialog(this,true,"Fields not filled correctly");
        }
    }
    
    /**
     * Function for validating fields
     * @return Boolean
     */
    boolean validate_fields(){
        return !field_ip.getText().equals("") && !field_user.getText().equals("")
                && !field_name.getText().equals("") && !field_password.getText().equals("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        field_ip = new javax.swing.JTextField();
        field_user = new javax.swing.JTextField();
        field_name = new javax.swing.JTextField();
        field_password = new javax.swing.JPasswordField();
        button_connect = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Connection manager");

        jLabel1.setText("database ip");

        jLabel2.setText("database user");

        jLabel3.setText("database name");

        jLabel4.setText("database password");

        button_connect.setText("Connect");
        button_connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_connectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(field_ip)
                    .addComponent(field_user, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(field_name, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(field_password))
                .addContainerGap(40, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(button_connect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(field_ip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(field_user, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(field_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(field_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(button_connect)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_connectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_connectActionPerformed
        try {
            load_object();
        } catch (IOException ex) {
            new message_window_jdialog(this,true,"Error while saving the configuration file");
        }
    }//GEN-LAST:event_button_connectActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_connect;
    private javax.swing.JTextField field_ip;
    private javax.swing.JTextField field_name;
    private javax.swing.JPasswordField field_password;
    private javax.swing.JTextField field_user;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration//GEN-END:variables
}

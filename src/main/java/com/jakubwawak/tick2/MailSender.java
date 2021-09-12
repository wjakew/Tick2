/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */

package com.jakubwawak.tick2;
import java.io.IOException;
import java.util.*;  
import javax.mail.*;  
import javax.mail.internet.*;  
/**
 *MailSender object
 * @author jakub
 * Object for sending e-mails with notes
 */
public class MailSender {

    final String version = "v1.0.1";
    final String HOST = "smtp.gmail.com";
    final String USER="main.tes.instruments@gmail.com";
    private String password="";
    
    public Properties props;
    
    public ArrayList<String> log;
    
    public MimeMessage message;
    public String mail_email;
    public String mail_password;

    public String email_address;
    public String content;
    public String topic;
    public Date act;
    
    /**
     * Object for sending emails
     * @param topic
     * @param content
     * @param email_to
     * @throws MessagingException
     * @throws IOException 
     */
    public MailSender(String topic,String content,String email_to) throws MessagingException, IOException{

        System.out.println("MailSender by TPS ( Jakub Wawak )  "+version);
        mail_email = "main.tes.instruments@gmail.com";
        mail_password = "m5hdmM0I*bSjnHHyZX7f5QGs7PcZfT4j#YP$^i#y5yJ10!5HH@9$n0RI@2o1Dks$1gjxFA9";
        email_address = email_to;
        this.content = content;
        this.topic = topic;
        act = new Date();
        log = new ArrayList<>();
        System.out.println("Got email adress: "+email_address);
    }

    public void run(){
        prepare();
        Session actual_session = ret_session();
        
        try{
            
            compose(actual_session);
            
            System.out.println("Content of the message:");
            System.out.println(content);
            
            Transport.send(message); 
            
            System.out.println("Note sent to : "+email_address);
            
        }catch(MessagingException e){
            System.out.println(e.toString());
        } 
    }

    public void prepare(){
        password = mail_password;
        props = new Properties();  
        props.setProperty("mail.transport.protocol", "smtp");     
        props.setProperty("mail.host", "smtp.gmail.com");  
        props.put("mail.smtp.auth", "true");  
        props.put("mail.smtp.port", "465");  
        props.put("mail.debug", "true");  
        props.put("mail.smtp.socketFactory.port", "465");  
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");  
        props.put("mail.smtp.socketFactory.fallback", "false"); 
    }
    
    public Session ret_session(){
        return Session.getDefaultInstance(props,  
            new javax.mail.Authenticator() {  
              protected PasswordAuthentication getPasswordAuthentication() {  
            return new PasswordAuthentication(USER,password);  
              }}); 
    }
    
    public void compose(Session actual) throws AddressException, MessagingException{
        message = new MimeMessage(actual); 
        message.setFrom(new InternetAddress(USER));  
        message.addRecipient(Message.RecipientType.TO,new InternetAddress(email_address));  
        message.setSubject(topic);  
        message.setText(content);
    }
}

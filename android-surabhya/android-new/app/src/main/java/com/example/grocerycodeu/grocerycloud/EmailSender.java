package com.example.grocerycodeu.grocerycloud;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by saryal on 8/5/2015.
 */
public class EmailSender extends javax.mail.Authenticator {

    public EmailSender() {
    }

    ;

    public boolean sendEmail(String to, String username, String userKey) {
        Properties props = System.getProperties();
        String host = "smtp.gmail.com";
        String from = "gograspit@gmail.com";
        InternetAddress receiver = null;
        try {
            receiver = new InternetAddress(to);
        } catch (AddressException e) {
            e.printStackTrace();
        }
        String pass = "GoGraspItCodeU2015";
        String subject = "Welcome to APP_NAME family :)";
        String body = "Hello " + username + "!\n\n"
                + "A WELCOME MESSAGE \n" +
                "IF YOU DIDN NOT SIGN UP FOR THE APP AND YOUR EMAIL IS USED THAN PLEASE CLICK ON THIS LINK HTTPS://SOMEUREL/" + userKey + "\n"
                + "If you have any other questions. Please fell free to reply to this email. We will be more than happy to help you!!!!\n\n" +
                "--------------------\n" +
                "Regards,\n" +
                "WHAT EVER THE APP NAME IS -- FAMILY";

        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, receiver);
            message.setSubject(subject);
            message.setText(body);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            return true;
        } catch (AddressException ae) {
            ae.printStackTrace();
            return false;
        } catch (MessagingException me) {
            me.printStackTrace();
            return false;
        } catch (RuntimeException e) {
            e.getMessage();
            return false;
        }
    }
}




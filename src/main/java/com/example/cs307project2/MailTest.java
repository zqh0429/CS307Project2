package com.example.cs307project2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;


public class MailTest
{
   static int identify = HelloController.identify;
   static String email = HelloController.email;
   static int identify2 = Main.identify;
   public static void main(String[] args) throws MessagingException, IOException
   {

   }
   public static void send() throws MessagingException, IOException {
      int cnt = 0;
      String password = "qazwsxedc123A";
      for (int m = 0; m < 2; m++) {
         // read properties
         Properties props = new Properties();
         try (InputStream in = Files.newInputStream(Paths.get("mail.properties")))
         {
            props.load(in);
         }
         // read message info
         List<String> lines = Files.readAllLines(Paths.get("message.txt"), StandardCharsets.UTF_8);

         String from = lines.get(0);
         String to = email;
         System.out.println(to);
         String subject = lines.get(2);
         cnt++;
         System.out.println(cnt);
         Session mailSession = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
               //传入发件人的姓名和授权码
               return new PasswordAuthentication("12110849@mail.sustech.edu.cn",password);
            }
         });
         MimeMessage message = new MimeMessage(mailSession);
         // TODO 1: check the MimeMessage API to figure out how to set the sender, receiver, subject, and email body
         message.setFrom(new InternetAddress(from));
         message.setRecipient(Message.RecipientType.TO,new InternetAddress(to));
         message.setSubject(subject);
         message.setContent(String.valueOf(identify),"text/html;charset=UTF-8");
         // TODO 2: check the Session API to figure out how to connect to the mail server and send the message
         Transport transport = mailSession.getTransport();
         transport.connect();
         transport.sendMessage(message,message.getAllRecipients());
         transport.close();
      }
   }
   public static void send2() throws MessagingException, IOException {
      int cnt = 0;
      String password = "qazwsxedc123A";
      for (int m = 0; m < 2; m++) {
         // read properties
         Properties props = new Properties();
         try (InputStream in = Files.newInputStream(Paths.get("mail.properties")))
         {
            props.load(in);
         }
         // read message info
         List<String> lines = Files.readAllLines(Paths.get("message.txt"), StandardCharsets.UTF_8);

         String from = lines.get(0);
         String to = email;
         System.out.println(to);
         String subject = lines.get(2);
         cnt++;
         System.out.println(cnt);
         Session mailSession = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
               //传入发件人的姓名和授权码
               return new PasswordAuthentication("12110849@mail.sustech.edu.cn",password);
            }
         });
         MimeMessage message = new MimeMessage(mailSession);
         // TODO 1: check the MimeMessage API to figure out how to set the sender, receiver, subject, and email body
         message.setFrom(new InternetAddress(from));
         message.setRecipient(Message.RecipientType.TO,new InternetAddress(to));
         message.setSubject(subject);
         message.setContent(String.valueOf(identify2),"text/html;charset=UTF-8");
         // TODO 2: check the Session API to figure out how to connect to the mail server and send the message
         Transport transport = mailSession.getTransport();
         transport.connect();
         transport.sendMessage(message,message.getAllRecipients());
         transport.close();
      }
   }
}
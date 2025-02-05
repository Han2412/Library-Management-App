package com.example.project.ui.subFragments;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {

    public static void sendOTP(String receiver, String otp, Context context) {
        String senderEmail = "chiencon4709@gmail.com";
        String receiverEmail = receiver;
        String passwordSenderEmail = "bswd taor lewh yjxx";
        String host = "smtp.gmail.com";
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, passwordSenderEmail);
            }
        });

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));
            String subject = "Xác nhận mã OTP";
            String message = "Xin chào,\n\n"
                    + "Mã OTP của bạn là: " + otp + "\n\n"
                    + "Trân trọng,\n"
                    + "Lê Trọng Chiến.";
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(() -> {
            try {
                Transport.send(mimeMessage);
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Gui OTP thành công", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}

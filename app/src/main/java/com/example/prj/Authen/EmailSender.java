package com.example.prj.Authen;

import android.os.AsyncTask;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {
    public void sendEmail(final String recipientEmail, final String message) {
        final String senderEmail = ""; // Replace with your email
        final String senderPassword = ""; // Replace with your password

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, senderPassword);
                    }
                });

                try {
                    Message mimeMessage = new MimeMessage(session);
                    mimeMessage.setFrom(new InternetAddress(senderEmail));
                    mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                    mimeMessage.setSubject("OTP Verification");
                    mimeMessage.setText(message);

                    Transport.send(mimeMessage);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
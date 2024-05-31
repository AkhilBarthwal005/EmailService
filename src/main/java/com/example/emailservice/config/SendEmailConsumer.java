package com.example.emailservice.config;

import com.example.emailservice.dto.SendEmailDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Component
public class SendEmailConsumer {
    // this class is the consumer client for kafka
    // when ever we need to consume event from kafka we can use this class

    private final ObjectMapper objectMapper;
    private final EmailUtil emailUtil;

    public SendEmailConsumer(ObjectMapper objectMapper , EmailUtil emailUtil) {
        this.objectMapper = objectMapper;
        this.emailUtil = emailUtil;
    }


    @KafkaListener(topics = "sendEmail", groupId = "emailService")
    public void consumeEvent(String message){
        System.out.println("Message Consumed: "+message);
        // here we can write the code to send email
        SendEmailDTO sendEmailDTO = null;
        try{
            sendEmailDTO = objectMapper.readValue(message, SendEmailDTO.class);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        // send an email
        // SMTP protocol -> simple mail transfer protocol

        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("akhil.barthwal005@gmail.com", "pwpjemphcjybqmln");
            }
        };
        Session session = Session.getInstance(props, auth);

        emailUtil.sendEmail(session, sendEmailDTO.getTo(), sendEmailDTO.getSubject(), sendEmailDTO.getBody());

    }
}

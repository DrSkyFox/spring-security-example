package com.springsec.springsecurityexample.listners;

import java.util.UUID;


import com.springsec.springsecurityexample.events.OnRegistrationCompleteEvent;
import com.springsec.springsecurityexample.model.User;
import com.springsec.springsecurityexample.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationListener.class);

    private final IUserService service;

    private final JavaMailSender mailSender;

    private final Environment env;

    @Autowired
    public RegistrationListener(IUserService service, JavaMailSender mailSender, Environment env) {
        this.service = service;
        this.mailSender = mailSender;
        this.env = env;
    }

    // API

    @Override
    public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
        logger.info("Registration event start: event {}", event.toString());
        this.confirmRegistration(event);
    }

    private void confirmRegistration(final OnRegistrationCompleteEvent event) {
        logger.info("User registration complete. Send confirm message to email: {}", event.toString());
        final User user = event.getUser();
        final String token = UUID.randomUUID()
            .toString();
        logger.info("User registration complete. Send confirm message to email: email info: {},  token : {}", user.getEmail(), token);
        service.createVerificationTokenForUser(user, token);

        final SimpleMailMessage email = constructEmailMessage(event, user, token);
        logger.info("mail send...................................");
        mailSender.send(email);
        logger.info("mail sent..................................");
    }

    //

    private SimpleMailMessage constructEmailMessage(final OnRegistrationCompleteEvent event, final User user, final String token) {
        final String recipientAddress = user.getEmail();
        final String subject = "Registration Confirmation";
        final String confirmationUrl = event.getURL() + "/registrationConfirm?token=" + token;
        logger.info("confirm url: {}", confirmationUrl);
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("Please open the following URL to verify your account: \r\n" + confirmationUrl);
        email.setFrom(env.getProperty("spring.mail.fromAdress"));
        return email;
    }


}

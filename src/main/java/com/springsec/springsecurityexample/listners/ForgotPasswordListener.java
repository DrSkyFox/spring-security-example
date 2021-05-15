package com.springsec.springsecurityexample.listners;

import com.springsec.springsecurityexample.events.OnForgotPasswordEvent;
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

import java.util.UUID;

@Component
public class ForgotPasswordListener implements ApplicationListener<OnForgotPasswordEvent> {

    private final static Logger logger = LoggerFactory.getLogger(ForgotPasswordListener.class);

    private final IUserService service;

    private final JavaMailSender mailSender;

    private final Environment env;

    @Autowired
    public ForgotPasswordListener(IUserService service, JavaMailSender mailSender, Environment env) {
        this.service = service;
        this.mailSender = mailSender;
        this.env = env;
    }

    @Override
    public void onApplicationEvent(OnForgotPasswordEvent event) {
        this.forgotPassword(event);
    }

    private void forgotPassword(final OnForgotPasswordEvent event) {
        logger.info("User registration complete. Send confirm message to email: {}");
        final User user  = event.getUser();
        final String token = UUID.randomUUID()
                .toString();
        logger.info("User forgot password request complete. Send recovery password link message to email: email info: {},  token : {}", user.getEmail(), token);
        service.createPasswordResetTokenForUser(user, token);

        final SimpleMailMessage email = constructEmailMessage(event, user, token);
        logger.info("mail send...................................");
        mailSender.send(email);
        logger.info("mail sent..................................");

    }

    private SimpleMailMessage constructEmailMessage(final OnForgotPasswordEvent event, final User user, final String token) {
        final String recipientAddress = user.getEmail();
        final String subject = "Reset Password";
        final String resetUrl = event.getURL() + "/user/changePassword?id=" + user.getId() + "&token=" + token;
        logger.info("reset url: {}", resetUrl);
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("Please open the following URL to reset your password: \r\n" + resetUrl);
        email.setFrom(env.getProperty("spring.mail.fromAdress"));
        return email;
    }



}

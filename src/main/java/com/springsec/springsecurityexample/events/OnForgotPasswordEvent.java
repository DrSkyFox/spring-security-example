package com.springsec.springsecurityexample.events;

import com.springsec.springsecurityexample.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

public class OnForgotPasswordEvent extends ApplicationEvent implements EmailEvents {

    private static final Logger logger = LoggerFactory.getLogger(OnForgotPasswordEvent.class);

    private User user;
    private String appUrl;

    public OnForgotPasswordEvent(final User user, final String appUrl) {
        super(user);
        this.user = user;
        this.appUrl = appUrl;
        logger.info(user.toString(), appUrl);
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public String getURL() {
        return appUrl;
    }

    @Override
    public String toString() {
        return "OnForgotPasswordEvent{" +
                "user=" + user +
                ", appUrl='" + appUrl + '\'' +
                '}';
    }
}



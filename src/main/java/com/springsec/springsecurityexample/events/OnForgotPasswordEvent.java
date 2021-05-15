package com.springsec.springsecurityexample.events;

import com.springsec.springsecurityexample.model.User;
import org.springframework.context.ApplicationEvent;

public class OnForgotPasswordEvent extends ApplicationEvent implements EmailEvents {

    private User user;
    private String appUrl;

    public OnForgotPasswordEvent(final User user, final String appUrl) {
        super(user);
        this.user = user;
        this.appUrl = appUrl;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public String getURL() {
        return appUrl;
    }
}



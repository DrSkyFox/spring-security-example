package com.springsec.springsecurityexample.events;

import com.springsec.springsecurityexample.model.User;
import org.springframework.context.ApplicationEvent;

public class OnRegistrationCompleteEvent extends ApplicationEvent implements EmailEvents {
    private final String appUrl;
    private final User user;

    public OnRegistrationCompleteEvent(final User user, final String appUrl) {
        super(user);
        this.user = user;
        this.appUrl = appUrl;
    }

    @Override
    public User getUser() {
        return null;
    }

    @Override
    public String getURL() {
        return null;
    }
}

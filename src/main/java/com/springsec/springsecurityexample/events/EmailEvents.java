package com.springsec.springsecurityexample.events;

import com.springsec.springsecurityexample.model.User;

public interface EmailEvents {
    User getUser();
    String getURL();
}

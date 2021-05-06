package com.springsec.springsecurityexample.service;

import com.springsec.springsecurityexample.model.User;
import com.springsec.springsecurityexample.validation.EmailExistsException;

public interface IUserService {

    User registerNewUser(User user) throws EmailExistsException;

    User updateExistingUser(User user) throws EmailExistsException;
}

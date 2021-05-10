package com.springsec.springsecurityexample.controller;

import com.springsec.springsecurityexample.model.User;
import com.springsec.springsecurityexample.representative.UserRepr;
import com.springsec.springsecurityexample.service.IUserService;
import com.springsec.springsecurityexample.validation.EmailExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Calendar;

@Controller
public class RegistrationController {

    @Autowired
    private IUserService userService;


    public RegistrationController(IUserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "signup")
    public ModelAndView registrationForm() {
        return new ModelAndView("registrationPage", "user", new UserRepr());
    }

    @RequestMapping(value = "/reg")
    public ModelAndView registerUser(@Valid final UserRepr user, final BindingResult result) {
        if (result.hasErrors()) {
            return new ModelAndView("registrationPage", "user", user);
        }
        try {
            user.setCreated(Calendar.getInstance());
            userService.registerNewUser(new User(user));
        } catch (EmailExistsException e) {
            result.addError(new FieldError("user", "email", e.getMessage()));
            return new ModelAndView("registrationPage", "user", user);
        }
        return new ModelAndView("redirect:/login");
    }
}

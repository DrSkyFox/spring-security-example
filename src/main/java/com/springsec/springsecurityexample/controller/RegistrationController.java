package com.springsec.springsecurityexample.controller;

import com.springsec.springsecurityexample.events.OnRegistrationCompleteEvent;
import com.springsec.springsecurityexample.model.User;
import com.springsec.springsecurityexample.model.VerificationToken;
import com.springsec.springsecurityexample.persists.VerificationTokenRepository;
import com.springsec.springsecurityexample.representative.UserRepr;
import com.springsec.springsecurityexample.service.IUserService;
import com.springsec.springsecurityexample.validation.EmailExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;


@Controller
public class RegistrationController {

    @Autowired
    private IUserService userService;


    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public RegistrationController(IUserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "signup")
    public ModelAndView registrationForm() {
        return new ModelAndView("registrationPage", "user", new UserRepr());
    }

    @RequestMapping(value = "/reg")
    public ModelAndView registerUser(@Valid final UserRepr user, final BindingResult result, final HttpServletRequest request) {
        if (result.hasErrors()) {
            return new ModelAndView("registrationPage", "user", user);
        }
        try {
            User userToDB = new User(user);
            final User registered = userService.registerNewUser(userToDB);

            final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, appUrl));

        } catch (EmailExistsException e) {
            result.addError(new FieldError("user", "email", e.getMessage()));
            return new ModelAndView("registrationPage", "user", user);
        }
        return new ModelAndView("redirect:/login");
    }

    public ModelAndView confirmRegistration(final Model model,
                                            @RequestParam("token") final String token,
                                            final RedirectAttributes redirectAttributes) {
        final VerificationToken verificationToken = userService.getVerificationToken(token);
        if(verificationToken == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid account confirmation token.");
            return new ModelAndView("redirect:/login");
        }
        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if(verificationToken.getExpiryDate().getTime()-cal.getTime().getTime() <=0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your registration token has expired. Please register again.");
            return new ModelAndView("redirect:/login");
        }

        user.setEnabled(true);
        userService.saveRegisteredUser(user);
        redirectAttributes.addFlashAttribute("message", "Your account verified successfully");
        return new ModelAndView("redirect:/login");

    }


    private String genUrlReg(final HttpServletRequest request) {
        final String appUrl = "http://"
                + request.getServerName()
                + ":" + request.getServerPort()
                + request.getContextPath();
        return appUrl;
    }
}

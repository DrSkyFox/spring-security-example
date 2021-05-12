package com.springsec.springsecurityexample.controller;

import com.springsec.springsecurityexample.events.OnRegistrationCompleteEvent;
import com.springsec.springsecurityexample.model.User;
import com.springsec.springsecurityexample.model.VerificationToken;
import com.springsec.springsecurityexample.persists.VerificationTokenRepository;
import com.springsec.springsecurityexample.representative.UserRepr;
import com.springsec.springsecurityexample.service.IUserService;
import com.springsec.springsecurityexample.validation.EmailExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger =LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private final IUserService userService;

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
        logger.info("Request Registration New User");
        if (result.hasErrors()) {
            return new ModelAndView("registrationPage", "user", user);
        }
        try {
            user.setEnabled(false);
            logger.info("New User is {}", user);
            final User registered = userService.registerNewUser(new User(user));
            logger.info("Saved to DB");
            final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            logger.info("Registration event");
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
        logger.info("Confirmation request with incoming token value: {}" ,token);
        final VerificationToken verificationToken = userService.getVerificationToken(token);
        if(verificationToken == null) {
            logger.info("Token not found in DB");
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid account confirmation token.");
            return new ModelAndView("redirect:/login");
        }
        logger.info("Token found");
        final User user = verificationToken.getUser();
        logger.info("Got user {} with token {}", user, token);
        final Calendar cal = Calendar.getInstance();

        if(verificationToken.getExpiryDate().getTime()-cal.getTime().getTime() <=0) {
            logger.info("Token {} expired", token);
            redirectAttributes.addFlashAttribute("errorMessage", "Your registration token has expired. Please register again.");
            return new ModelAndView("redirect:/login");
        }

        user.setEnabled(true);
        logger.info("User is active : {}", user.getEnabled());
        userService.saveRegisteredUser(user);
        logger.info("Saving to DB");
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

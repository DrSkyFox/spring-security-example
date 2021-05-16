package com.springsec.springsecurityexample.controller;

import com.springsec.springsecurityexample.events.OnForgotPasswordEvent;
import com.springsec.springsecurityexample.events.OnRegistrationCompleteEvent;
import com.springsec.springsecurityexample.model.PasswordResetToken;
import com.springsec.springsecurityexample.model.User;
import com.springsec.springsecurityexample.model.VerificationToken;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.google.common.collect.ImmutableMap;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.UUID;


@Controller
public class RegistrationController {

    private static final Logger logger =LoggerFactory.getLogger(RegistrationController.class);

    private final IUserService userService;

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public RegistrationController(IUserService userService, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
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
            logger.info("Saved to DB:  {}", registered.toString());
            final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            logger.info("Registration event");
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, appUrl));

        } catch (EmailExistsException e) {
            result.addError(new FieldError("user", "email", e.getMessage()));
            return new ModelAndView("registrationPage", "user", user);
        }
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(value = "/registrationConfirm")
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

    //FORGOT PASSWORD

    @RequestMapping(value = "/user/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView resetPassword(final HttpServletRequest request, @RequestParam("email") final String userEmail, final RedirectAttributes redirectAttributes) {
        logger.info("User request reset password by email {}", userEmail);
        final User user = userService.findUserByEmail(userEmail);
        logger.info("User is {}", user.toString());
        if (user != null) {
            final String token = UUID.randomUUID()
                    .toString();
            logger.info("Token for reset {}", token);
            userService.createPasswordResetTokenForUser(user, token);
            logger.info("Token saved to DB");
            final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            logger.info("URL : {}", appUrl);
            logger.info("ForgotPassword event");
            eventPublisher.publishEvent(new OnForgotPasswordEvent(user, appUrl));
            logger.info("event is published");
        }

        redirectAttributes.addFlashAttribute("message", "You should receive an Password Reset Email shortly");
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(value = "/user/changePassword", method = RequestMethod.GET)
    public ModelAndView showChangePasswordPage(@RequestParam("id") final long id, @RequestParam("token") final String token, final RedirectAttributes redirectAttributes) {
        final PasswordResetToken passToken = userService.getPasswordResetToken(token);
        if (passToken == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid password reset token");
            return new ModelAndView("redirect:/login");
        }
        final User user = passToken.getUser();
        if (user.getId() != id) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid password reset token");
            return new ModelAndView("redirect:/login");
        }

        final Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate()
                .getTime()
                - cal.getTime()
                .getTime()) <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your password reset token has expired");
            return new ModelAndView("redirect:/login");
        }

        final ModelAndView view = new ModelAndView("resetPassword");
        view.addObject("token", token);
        return view;
    }

    @RequestMapping(value = "/user/savePassword", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView savePassword(@RequestParam("password") final String password, @RequestParam("passwordConfirmation") final String passwordConfirmation, @RequestParam("token") final String token, final RedirectAttributes redirectAttributes) {

        if (!password.equals(passwordConfirmation)) {
            return new ModelAndView("resetPassword", ImmutableMap.of("errorMessage", "Passwords do not match"));
        }
        final PasswordResetToken p = userService.getPasswordResetToken(token);
        if (p == null) {
            redirectAttributes.addFlashAttribute("message", "Invalid token");
        } else {
            final User user = p.getUser();
            if (user == null) {
                redirectAttributes.addFlashAttribute("message", "Unknown user");
            } else {
                userService.changeUserPassword(user, password);
                redirectAttributes.addFlashAttribute("message", "Password reset successfully");
            }
        }
        return new ModelAndView("redirect:/login");
    }

}

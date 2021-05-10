package com.springsec.springsecurityexample.representative;

import com.springsec.springsecurityexample.model.User;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Calendar;

public class UserRepr {

    private Long id;

    @Email
    @NotEmpty(message = "Email is required.")
    private String email;

    @NotEmpty(message = "Password is required.")
    private String password;

    @NotEmpty(message = "Password confirmation is required.")
    private String passwordConfirmation;

    private Calendar created = Calendar.getInstance();

    private Boolean enabled;


    public UserRepr(String email, String password, String passwordConfirmation, Calendar created, Boolean enabled) {
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.created = created;
        this.enabled = enabled;
    }

    public UserRepr(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.passwordConfirmation = null;
        this.created = user.getCreated();
        this.enabled = user.getEnabled();
    }

    public UserRepr() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Calendar getCreated() {
        return this.created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(final String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "UserRepr{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", passwordConfirmation='" + passwordConfirmation + '\'' +
                ", created=" + created +
                ", enabled=" + enabled +
                '}';
    }
}

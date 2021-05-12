package com.springsec.springsecurityexample.model;

import com.springsec.springsecurityexample.representative.UserRepr;

import java.util.Calendar;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64)
    private String email;

    @Column(length = 512)
    private String password;

    @Column
    private Calendar created;

    @Column
    private Boolean enabled;

    public User(String email, String password, Calendar created) {
        this.email = email;
        this.password = password;
        this.created = created;
    }

    public User(UserRepr userRepr) {
        this.email = userRepr.getEmail();
        this.password = userRepr.getPassword();
        this.created = userRepr.getCreated();
    }


    public User() {
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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", created=" + created +
                ", enabled=" + enabled +
                '}';
    }
}

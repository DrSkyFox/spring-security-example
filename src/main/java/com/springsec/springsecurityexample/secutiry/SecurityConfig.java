package com.springsec.springsecurityexample.secutiry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,
                                LssUserDetailsService userDetailsService,
                                PasswordEncoder passwordEncoder) throws Exception { // @formatter:off
        auth.
                inMemoryAuthentication().passwordEncoder(passwordEncoder()).
                withUser("root").password(passwordEncoder().encode("toot")).
                roles("SUPER_ADMIN");

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        auth.authenticationProvider(provider);

    } // @formatter:on


    @Override
    protected void configure(HttpSecurity http) throws Exception { // @formatter:off
        http
                .authorizeRequests()
                .antMatchers("/signup", "/reg", "/sendmail").permitAll()
                .antMatchers("/delete/**").hasRole("ADMIN")
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .loginProcessingUrl("/doLogin")
                .and().logout().permitAll().logoutUrl("/doLogout")
                .and()
                .csrf().disable();
        ;
    } // @formatter:on

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    public SecurityConfig() {
        super();
    }
}

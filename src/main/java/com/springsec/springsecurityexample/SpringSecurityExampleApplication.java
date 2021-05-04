package com.springsec.springsecurityexample;

import com.springsec.springsecurityexample.model.User;

import com.springsec.springsecurityexample.persists.InMemoryUserRepository;
import com.springsec.springsecurityexample.persists.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

@SpringBootApplication
@ComponentScan("com.springsec")
public class SpringSecurityExampleApplication {

    @Bean
    public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }

    @Bean
    public Converter<String, User> messageConverter() {
        return new Converter<String, User>() {
            @Override
            public User convert(String id) {
                return userRepository().findUser(Long.valueOf(id));
            }
        };
    }


    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityExampleApplication.class, args);
    }

}

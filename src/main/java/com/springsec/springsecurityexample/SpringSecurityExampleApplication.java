package com.springsec.springsecurityexample;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringSecurityExampleApplication {

//    @Bean
//    public UserRepository userRepository() {
//        return new InMemoryUserRepository();
//    }
//
//    @Bean
//    public Converter<String, User> messageConverter() {
//        return new Converter<String, User>() {
//            @Override
//            public User convert(String id) {
//                return userRepository().findUser(Long.valueOf(id));
//            }
//        };
//    }



    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityExampleApplication.class, args);
    }

}

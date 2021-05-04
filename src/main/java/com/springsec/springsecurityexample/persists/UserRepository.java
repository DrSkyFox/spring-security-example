package com.springsec.springsecurityexample.persists;


import com.springsec.springsecurityexample.model.User;

public interface UserRepository {

    Iterable<User> findAll();

    User save(User user);

    User findUser(Long id);

    void deleteUser(Long id);

}

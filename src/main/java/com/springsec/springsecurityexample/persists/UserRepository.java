package com.springsec.springsecurityexample.persists;


import com.springsec.springsecurityexample.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List findAll();

    User save(User user);

    void deleteById(Long id);

    User findByEmail(String email);

}

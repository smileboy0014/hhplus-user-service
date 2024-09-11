package com.hhplus.hhplususerservice.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    // User 관련
    Optional<User> getUser(Long userId);

    void deleteAll();

    Optional<User> saveUser(User user);

    List<User> getUsers();


}

package com.hhplus.hhplususerservice.domain.user.listener;

import com.hhplus.hhplususerservice.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserService userService;

}

package com.hhplus.hhplususerservice.domain.user.command;

import java.math.BigDecimal;

public class UserCommand {

    public record Create(
            Long userId,
            BigDecimal balance
    ) {
    }

}

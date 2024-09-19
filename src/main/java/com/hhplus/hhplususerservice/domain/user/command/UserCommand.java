package com.hhplus.hhplususerservice.domain.user.command;

import com.hhplus.hhplususerservice.domain.event.PaymentEvent;

import java.math.BigDecimal;

public class UserCommand {

    public record Create(
            Long userId,
            BigDecimal balance
    ) {

    }

    public record Use(
            String reservationId,
            String paymentId,
            String userId,
            String token,
            BigDecimal amount
    ) {
        public static UserCommand.Use toCommand(PaymentEvent event) {
            return new UserCommand.Use(event.getReservationId(), event.getPaymentId(), event.getUserId(),
                    event.getToken(), event.getAmount());
        }
    }

}

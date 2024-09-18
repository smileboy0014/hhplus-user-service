package com.hhplus.hhplususerservice.domain.outbox.command;

import com.hhplus.hhplususerservice.domain.outbox.Outbox;

public class OutboxCommand {
    public record Create(
            String messageId,
            Outbox.EventStatus status,
            String payload) {

        public Outbox toDomain() {
            return Outbox.builder()
                    .messageId(messageId)
                    .status(status)
                    .payload(payload)
                    .build();
        }
    }
}

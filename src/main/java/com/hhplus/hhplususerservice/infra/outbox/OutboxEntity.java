package com.hhplus.hhplususerservice.infra.outbox;

import com.hhplus.hhplususerservice.domain.outbox.Outbox;
import com.hhplus.hhplususerservice.infra.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "outbox")
public class OutboxEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outboxId;

    private String messageId;

    @Enumerated(EnumType.STRING)
    private Outbox.EventStatus status;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private int retryCount;

    @CreatedDate
    private LocalDateTime createdAt;

    public static OutboxEntity from(Outbox outbox) {
        return OutboxEntity.builder()
                .outboxId(outbox.getOutboxId())
                .messageId(outbox.getMessageId())
                .status(outbox.getStatus())
                .payload(outbox.getPayload())
                .retryCount(outbox.getRetryCount())
                .createdAt(outbox.getCreatedAt())
                .build();
    }

    public Outbox toDomain() {
        return Outbox.builder()
                .outboxId(outboxId)
                .messageId(messageId)
                .status(status)
                .payload(payload)
                .retryCount(retryCount)
                .createdAt(createdAt)
                .build();
    }
}

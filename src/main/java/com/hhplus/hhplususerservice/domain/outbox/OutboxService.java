package com.hhplus.hhplususerservice.domain.outbox;

import com.hhplus.hhplususerservice.domain.common.exception.CustomException;
import com.hhplus.hhplususerservice.domain.outbox.command.OutboxCommand;
import com.hhplus.hhplususerservice.domain.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.hhplus.hhplususerservice.domain.common.exception.ErrorCode.OUTBOX_IS_FAILED;
import static com.hhplus.hhplususerservice.domain.common.exception.ErrorCode.OUTBOX_IS_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final EventProducer eventProducer;

    @Transactional
    public Outbox save(OutboxCommand.Create command) {

        return outboxRepository.saveOutbox(command.toDomain()).orElseThrow(() ->
                new CustomException(OUTBOX_IS_FAILED, OUTBOX_IS_FAILED.getMsg()));
    }

    @Transactional
    public Outbox publish(String messageId) {
        Outbox outbox = outboxRepository.getOutbox(messageId).orElseThrow(() ->
                new CustomException(OUTBOX_IS_NOT_FOUND, OUTBOX_IS_NOT_FOUND.getMsg()));
        // outbox 메시지 발행 완료
        outbox.publish();
        outboxRepository.saveOutbox(outbox);

        return outbox;
    }

    @Transactional
    public void retryFailMessage() {
        // Outbox 테이블에서 재시도가 필요한 메시지 조회
        List<Outbox> retryOutboxes = outboxRepository.getRetryOutboxes();
        if (retryOutboxes.isEmpty()) {
            return;
        }

        for (Outbox outbox : retryOutboxes) {

            if (outbox.getRetryCount() >= 3) {
                // 이미 3회 이상 재시도한 메시지 실패 처리
                outbox.fail();
                outboxRepository.saveOutbox(outbox);
                continue;
            }
            try {
                // 재시도 로직, outbox message 발행 완료 변경, KafkaProducer 를 통해 메시지 재발행
                outbox.publish();
                outboxRepository.saveOutbox(outbox);
//                PaymentEvent payload = JsonUtils.toObject(outbox.getPayload(), PaymentEvent.class);
//                eventProducer.publish(PAYMENT_TOPIC, String.valueOf(payload.getReservationInfo().getReservationId()),
//                        outbox.getPayload());

            } catch (Exception e) {
                log.error("send retry exception -> outboxId: {}, error: {}", outbox.getOutboxId(), e.getMessage());
                outbox.restore();
                outboxRepository.saveOutbox(outbox);
            }
            // 재시도 횟수 추가
            outbox.plusRetryCount();
            outboxRepository.saveOutbox(outbox);
        }
    }
}

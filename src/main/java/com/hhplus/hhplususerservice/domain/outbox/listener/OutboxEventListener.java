package com.hhplus.hhplususerservice.domain.outbox.listener;

import com.hhplus.hhplususerservice.domain.event.PaymentEvent;
import com.hhplus.hhplususerservice.domain.outbox.OutboxService;
import com.hhplus.hhplususerservice.domain.producer.EventProducer;
import com.hhplus.hhplususerservice.support.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.hhplus.hhplususerservice.support.config.KafkaTopicConfig.KafkaConstants.PAYMENT_TOPIC;


@Component
@RequiredArgsConstructor
public class OutboxEventListener {

    private final OutboxService outboxService;
    private final EventProducer eventProducer;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutboxPayment(PaymentEvent event) {
        // Outbox data 생성
        outboxService.save(event.toOutboxCommand());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPaymentEvent(PaymentEvent event) {
        // outbox 메시지 발행 완료 채크
        outboxService.publish(event.getMessageId());
        // 이벤트 메시지 발행
        eventProducer.publish(PAYMENT_TOPIC, String.valueOf(event.getPaymentId()),
                JsonUtils.toJson(event));
    }
}

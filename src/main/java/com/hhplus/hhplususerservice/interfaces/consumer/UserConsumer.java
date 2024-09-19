package com.hhplus.hhplususerservice.interfaces.consumer;

import com.hhplus.hhplususerservice.domain.event.PaymentEvent;
import com.hhplus.hhplususerservice.domain.user.UserService;
import com.hhplus.hhplususerservice.domain.user.command.UserCommand;
import com.hhplus.hhplususerservice.support.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import static com.hhplus.hhplususerservice.support.config.KafkaTopicConfig.KafkaConstants.PAYMENT_TOPIC;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserConsumer {

    private final UserService userService;

    @KafkaListener(topics = PAYMENT_TOPIC, containerFactory = "kafkaListenerContainerFactory")
    public void handlePaymentEvent(String key, String message) {
        log.info("[KAFKA] :: CONSUMER:: Received PAYMENT_TOPIC, key: {}, payload: {}", key, message);
        PaymentEvent payload = JsonUtils.toObject(message, PaymentEvent.class);

        if (payload != null && payload.getStatus().equals(PaymentEvent.EventConstants.RESERVATION_COMPLETED)) {
            userService.usePoint(UserCommand.Use.toCommand(payload));
        }

//        ack.acknowledge(); //수동으로 offset 커밋
    }
}

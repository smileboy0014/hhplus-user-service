package com.hhplus.hhplususerservice.interfaces.consumer;

import com.hhplus.hhplususerservice.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.hhplus.hhplususerservice.support.config.KafkaTopicConfig.KafkaConstants.PAYMENT_TOPIC;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserConsumer {

    private final UserService userService;

    @KafkaListener(topics = PAYMENT_TOPIC, groupId = "hhplus-01")
    public void sendPaymentInfo(String key, String message) {
        log.info("[KAFKA] :: CONSUMER:: Received PAYMENT_TOPIC, key: {}, payload: {}", key, message);

    }
}

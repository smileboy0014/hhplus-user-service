package com.hhplus.hhplususerservice.domain.producer;

public interface EventProducer {

    void publish(String topic, String key, String payload);
}

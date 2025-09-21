package com.ridehub.user.broker;

import com.ridehub.avro.common.EventEnvelope;
import com.ridehub.kafka.broker.GenericKafkaProducer;
import com.ridehub.kafka.service.KafkaUtilityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * User-specific producer using Supplier pattern only.
 * Exposes only get() and send(...).
 */
@Component("kafkaProducer")
public class KafkaProducer extends GenericKafkaProducer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaUtilityService kafkaUtilityService) {
        super(kafkaUtilityService);
    }

    @Override
    public Message<EventEnvelope> get() {
        Message<EventEnvelope> message = super.get();
        if (message != null) {
            LOG.info("UserKafkaProducer: Supplying user event to kafkaProducer-out-0 binding");
        }
        return message;
    }

    /**
     * Generic send for any user-related event.
     * (Queues to Supplier; no direct StreamBridge or destination logic.)
     */
    public String send(String eventName, Object payload) {
        LOG.info("KafkaProducer: Queueing user event: {}", eventName);
        return queueEvent(eventName, payload, null);
    }
}

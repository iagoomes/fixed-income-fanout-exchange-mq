package br.com.iagoomes.application.service;

import br.com.iagoomes.domain.dto.FixedIncomeEventDto;
import br.com.iagoomes.infra.mqprovider.producer.FixedIncomeEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class FixedIncomeEventService {

    private final FixedIncomeEventProducer producer;

    public void publishRateChangeEvent(FixedIncomeEventDto event) {
        event.setEventType("rate_change");
        event.setTimestamp(LocalDateTime.now());
        log.info("Publishing rate change event for {}", event.getProductType());
        producer.publishEvent(event);
    }

    public void publishPricingUpdateEvent(FixedIncomeEventDto event) {
        event.setEventType("pricing_update");
        event.setTimestamp(LocalDateTime.now());
        log.info("Publishing pricing update event for {}", event.getProductType());
        producer.publishEvent(event);
    }

    public void publishGenericEvent(FixedIncomeEventDto event) {
        event.setTimestamp(LocalDateTime.now());
        log.info("Publishing generic event: {}", event.getEventType());
        producer.publishEvent(event);
    }

}

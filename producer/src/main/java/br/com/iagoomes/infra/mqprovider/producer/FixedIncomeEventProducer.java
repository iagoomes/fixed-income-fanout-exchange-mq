package br.com.iagoomes.infra.mqprovider.producer;

import br.com.iagoomes.domain.dto.FixedIncomeEventDto;
import br.com.iagoomes.infra.config.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FixedIncomeEventProducer {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;

    public void publishEvent(FixedIncomeEventDto event) {
        try {
            // Adiciona um ID único ao evento
            if (event.getEventId() == null) {
                event.setEventId(UUID.randomUUID().toString());
            }

            log.info("Publishing event: {} with ID: {}", event.getEventType(), event.getEventId());

            // Publica no Fanout Exchange (sem routing key!)
            rabbitTemplate.convertAndSend(
                    rabbitMQProperties.getExchange().getName(),
                    "",  // Empty routing key for fanout
                    event
            );

            log.info("Event published successfully to Fanout Exchange: {}",
                    rabbitMQProperties.getExchange().getName());

        } catch (Exception e) {
            log.error("Error publishing event: {}", event.getEventType(), e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }

}

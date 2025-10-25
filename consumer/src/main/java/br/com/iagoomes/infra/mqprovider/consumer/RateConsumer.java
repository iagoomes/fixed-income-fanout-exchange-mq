package br.com.iagoomes.infra.mqprovider.consumer;

import br.com.iagoomes.domain.dto.FixedIncomeEventDto;
import br.com.iagoomes.infra.config.RabbitMQFanoutConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RateConsumer {

    @RabbitListener(queues = RabbitMQFanoutConfig.RATE_QUEUE)
    public void handleRateChangeEvent(FixedIncomeEventDto event) {
        try {
            log.info("🏠 [RATE CONSUMER] Received event: {}", event.getEventType());
            log.info("   Product: {}, Event ID: {}", event.getProductType(), event.getEventId());

            if ("rate_change".equals(event.getEventType())) {
                log.info("   Taxa atualizada: {} {}% → {}%",
                        event.getProductType(),
                        event.getOldRate(),
                        event.getNewRate());
                log.info("   Razão: {}", event.getReason());
                log.info("   ✅ Registrado no sistema de taxas");
            } else {
                log.info("   Evento genérico recebido e registrado: {}", event.getDescription());
            }

        } catch (Exception e) {
            log.error("❌ Error processing event in Rate Consumer", e);
            throw e;
        }
    }

}

package br.com.iagoomes.infra.mqprovider.consumer;

import br.com.iagoomes.domain.dto.FixedIncomeEventDto;
import br.com.iagoomes.infra.config.RabbitMQFanoutConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PricingConsumer {

    @RabbitListener(queues = RabbitMQFanoutConfig.PRICING_QUEUE)
    public void handlePricingUpdateEvent(FixedIncomeEventDto event) {
        try {
            log.info("💰 [PRICING CONSUMER] Received event: {}", event.getEventType());
            log.info("   Product: {}, Event ID: {}", event.getProductType(), event.getEventId());

            if ("pricing_update".equals(event.getEventType())) {
                log.info("   Precificação atualizada: {} {} → {}",
                        event.getProductType(),
                        event.getOldPrice(),
                        event.getNewPrice());
                log.info("   ✅ Preços recalculados e atualizados");
            } else {
                log.info("   Evento genérico recebido: {}", event.getDescription());
                log.info("   Reprocessando precificações...");
            }

        } catch (Exception e) {
            log.error("❌ Error processing event in Pricing Consumer", e);
            throw e;
        }
    }

}

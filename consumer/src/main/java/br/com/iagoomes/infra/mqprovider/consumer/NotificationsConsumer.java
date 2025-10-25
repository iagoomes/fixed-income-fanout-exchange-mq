package br.com.iagoomes.infra.mqprovider.consumer;

import br.com.iagoomes.domain.dto.FixedIncomeEventDto;
import br.com.iagoomes.infra.config.RabbitMQFanoutConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationsConsumer {

    @RabbitListener(queues = RabbitMQFanoutConfig.NOTIFICATIONS_QUEUE)
    public void handleNotificationEvent(FixedIncomeEventDto event) {
        try {
            log.info("📧 [NOTIFICATIONS CONSUMER] Received event: {}", event.getEventType());
            log.info("   Event ID: {}, Severity: {}", event.getEventId(), event.getSeverity());

            String message = buildNotificationMessage(event);
            log.info("   Mensagem: {}", message);
            log.info("   ✅ Notificações enviadas via Email, SMS e Push");

        } catch (Exception e) {
            log.error("❌ Error processing event in Notifications Consumer", e);
            throw e;
        }
    }

    private String buildNotificationMessage(FixedIncomeEventDto event) {
        return switch (event.getEventType()) {
            case "rate_change" -> String.format("Taxa de %s alterada de %.2f%% para %.2f%%",
                    event.getProductType(), event.getOldRate(), event.getNewRate());
            case "pricing_update" -> String.format("Preço de %s atualizado para %.2f",
                    event.getProductType(), event.getNewPrice());
            default -> event.getDescription() != null ? event.getDescription() : "Novo evento";
        };
    }

}

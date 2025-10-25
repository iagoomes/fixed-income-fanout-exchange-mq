package br.com.iagoomes.infra.mqprovider.consumer;

import br.com.iagoomes.domain.dto.FixedIncomeEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuditConsumer {

    @RabbitListener(queues = "#{rabbitMQProperties.queues.audit.name}")
    public void handleAuditEvent(FixedIncomeEventDto event) {
        try {
            log.info("📝 [AUDIT CONSUMER] Received event: {}", event.getEventType());
            log.info("   Event ID: {}, Product: {}", event.getEventId(), event.getProductType());
            log.info("   Timestamp: {}", event.getTimestamp());

            // Simulate audit logging
            auditLog(event);
            log.info("   ✅ Evento registrado no audit log");

        } catch (Exception e) {
            log.error("❌ Error processing event in Audit Consumer", e);
            throw e;
        }
    }

    private void auditLog(FixedIncomeEventDto event) {
        // In a real scenario, this would persist to a database or log file
        log.debug("AUDIT: Event={}, Type={}, Product={}, EventId={}, Timestamp={}",
                event.getEventType(),
                event.getEventType(),
                event.getProductType(),
                event.getEventId(),
                event.getTimestamp());
    }

}

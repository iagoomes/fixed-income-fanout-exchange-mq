package br.com.iagoomes.infra.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQFanoutConfig {

    // Exchange
    public static final String FANOUT_EXCHANGE = "rf-events.fanout";

    // Queues
    public static final String RATE_QUEUE = "rf.rate.queue";
    public static final String PRICING_QUEUE = "rf.pricing.queue";
    public static final String NOTIFICATIONS_QUEUE = "rf.notifications.queue";
    public static final String AUDIT_QUEUE = "rf.audit.queue";

    // Fanout Exchange Bean
    @Bean
    public FanoutExchange fixedIncomeFanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE, true, false);
    }

    // Rate Queue
    @Bean
    public Queue rateQueue() {
        return new Queue(RATE_QUEUE, true);
    }

    @Bean
    public Binding rateBinding(Queue rateQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(rateQueue)
                .to(fanoutExchange);
    }

    // Pricing Queue
    @Bean
    public Queue pricingQueue() {
        return new Queue(PRICING_QUEUE, true);
    }

    @Bean
    public Binding pricingBinding(Queue pricingQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(pricingQueue)
                .to(fanoutExchange);
    }

    // Notifications Queue
    @Bean
    public Queue notificationsQueue() {
        return new Queue(NOTIFICATIONS_QUEUE, true);
    }

    @Bean
    public Binding notificationsBinding(Queue notificationsQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(notificationsQueue)
                .to(fanoutExchange);
    }

    // Audit Queue
    @Bean
    public Queue auditQueue() {
        return new Queue(AUDIT_QUEUE, true);
    }

    @Bean
    public Binding auditBinding(Queue auditQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(auditQueue)
                .to(fanoutExchange);
    }

}

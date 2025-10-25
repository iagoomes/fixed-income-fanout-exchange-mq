package br.com.iagoomes.infra.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQFanoutConfig {

    private final RabbitMQProperties rabbitMQProperties;

    // Fanout Exchange Bean
    @Bean
    public FanoutExchange fixedIncomeFanoutExchange() {
        return new FanoutExchange(
                rabbitMQProperties.getExchange().getName(),
                rabbitMQProperties.getExchange().isDurable(),
                rabbitMQProperties.getExchange().isAutoDelete()
        );
    }

    // Rate Queue
    @Bean
    public Queue rateQueue() {
        RabbitMQProperties.QueueConfig config = rabbitMQProperties.getQueues().getRate();
        return new Queue(config.getName(), config.isDurable(), config.isExclusive(), config.isAutoDelete());
    }

    @Bean
    public Binding rateBinding(Queue rateQueue, FanoutExchange fixedIncomeFanoutExchange) {
        return BindingBuilder.bind(rateQueue).to(fixedIncomeFanoutExchange);
    }

    // Pricing Queue
    @Bean
    public Queue pricingQueue() {
        RabbitMQProperties.QueueConfig config = rabbitMQProperties.getQueues().getPricing();
        return new Queue(config.getName(), config.isDurable(), config.isExclusive(), config.isAutoDelete());
    }

    @Bean
    public Binding pricingBinding(Queue pricingQueue, FanoutExchange fixedIncomeFanoutExchange) {
        return BindingBuilder.bind(pricingQueue).to(fixedIncomeFanoutExchange);
    }

    // Notifications Queue
    @Bean
    public Queue notificationsQueue() {
        RabbitMQProperties.QueueConfig config = rabbitMQProperties.getQueues().getNotifications();
        return new Queue(config.getName(), config.isDurable(), config.isExclusive(), config.isAutoDelete());
    }

    @Bean
    public Binding notificationsBinding(Queue notificationsQueue, FanoutExchange fixedIncomeFanoutExchange) {
        return BindingBuilder.bind(notificationsQueue).to(fixedIncomeFanoutExchange);
    }

    // Audit Queue
    @Bean
    public Queue auditQueue() {
        RabbitMQProperties.QueueConfig config = rabbitMQProperties.getQueues().getAudit();
        return new Queue(config.getName(), config.isDurable(), config.isExclusive(), config.isAutoDelete());
    }

    @Bean
    public Binding auditBinding(Queue auditQueue, FanoutExchange fixedIncomeFanoutExchange) {
        return BindingBuilder.bind(auditQueue).to(fixedIncomeFanoutExchange);
    }

}

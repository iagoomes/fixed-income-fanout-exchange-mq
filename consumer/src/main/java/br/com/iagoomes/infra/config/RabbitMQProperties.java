package br.com.iagoomes.infra.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMQProperties {

    private Exchange exchange = new Exchange();
    private Queues queues = new Queues();

    @Getter
    @Setter
    public static class Exchange {
        private String name;
        private String type = "fanout";
        private boolean durable = true;
        private boolean autoDelete = false;
    }

    @Getter
    @Setter
    public static class Queues {
        private QueueConfig rate = new QueueConfig();
        private QueueConfig pricing = new QueueConfig();
        private QueueConfig notifications = new QueueConfig();
        private QueueConfig audit = new QueueConfig();
    }

    @Getter
    @Setter
    public static class QueueConfig {
        private String name;
        private boolean durable = true;
        private boolean exclusive = false;
        private boolean autoDelete = false;
    }
}
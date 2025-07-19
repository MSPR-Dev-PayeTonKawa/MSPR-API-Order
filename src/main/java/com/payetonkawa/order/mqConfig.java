package com.payetonkawa.order;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class mqConfig {

    // Nom de la queue pour les messages de commande
    // Vous pouvez définir cette propriété dans application.yml : rabbitmq.queue.commande=commande.sync.queue
    @Bean
    public Queue commandeQueue() {
        // durable = true pour survivre aux redémarrages
        return new Queue("product.sync.queue", true);
    }

    // Configurer RabbitTemplate pour utiliser JSON (Jackson)
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}


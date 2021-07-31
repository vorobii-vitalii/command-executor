package org.vitalii.vorobii.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vitalii.vorobii.listener.MessageListener;

@Configuration
public class RabbitMessageBrokerConfig {

    @Value("${rabbit.mq.host}")
    private String rabbitMqHostName;

    @Value("${rabbit.mq.user}")
    private String rabbitMqUser;

    @Value("${rabbit.mq.password}")
    private String rabbitMqPassword;

    @Value("${rabbit.tasks.todo.queueName}")
    private String tasksToDoQueueName;

    @Value("${rabbit.tasks.done.queueName}")
    private String tasksDoneQueueName;

    @Autowired
    private MessageListener messageListener;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitMqHostName);

        connectionFactory.setUsername(rabbitMqUser);
        connectionFactory.setPassword(rabbitMqPassword);

        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());

        template.setRoutingKey(this.tasksDoneQueueName);
        template.setDefaultReceiveQueue(this.tasksDoneQueueName);

        return template;
    }

    @Bean
    public Queue tasksToDoQueue() {
        return new Queue(this.tasksToDoQueueName);
    }

    @Bean
    public Queue tasksDoneQueue() {
        return new Queue(this.tasksDoneQueueName);
    }

    @Bean
    public SimpleMessageListenerContainer todoTasksListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();

        container.setConnectionFactory(connectionFactory());
        container.setQueueNames(this.tasksToDoQueueName);
        container.setMessageListener(new MessageListenerAdapter(this.messageListener));

        return container;
    }

}

package org.vitalii.vorobii.config;

import org.springframework.amqp.core.*;
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

    private static final String TASKS_DONE_ROUTING_KEY = "tasks.done.queue.#";
    public static final String TASKS_DONE_EXCHANGE = "tasks.done.exchange";

    @Value("${rabbit.mq.host}")
    private String rabbitMqHostName;

    @Value("${rabbit.mq.user}")
    private String rabbitMqUser;

    @Value("${rabbit.mq.password}")
    private String rabbitMqPassword;

    @Value("${rabbit.tasks.todo.queueName}")
    private String tasksToDoQueueName;

    @Value("${tasks.done.queue.command}")
    private String tasksDoneQueueCommand;

    @Value("${tasks.done.queue.statistic}")
    private String tasksDoneQueueStatistics;

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

        template.setExchange(TASKS_DONE_EXCHANGE);
        template.setRoutingKey(TASKS_DONE_ROUTING_KEY);

        return template;
    }

    @Bean
    public TopicExchange tasksDoneExchange() {
        return new TopicExchange(TASKS_DONE_EXCHANGE);
    }

    @Bean
    public Queue tasksToDoQueue() {
        return new Queue(this.tasksToDoQueueName);
    }

    @Bean
    public Queue tasksDoneQueueCommandDefinition() {
        return new Queue(this.tasksDoneQueueCommand);
    }

    @Bean
    public Queue tasksDoneQueueStatisticsDefinition() {
        return new Queue(this.tasksDoneQueueStatistics);
    }

    @Bean
    public Binding commandBinding() {
        return BindingBuilder
                .bind(tasksDoneQueueCommandDefinition())
                .to(tasksDoneExchange())
                .with(TASKS_DONE_ROUTING_KEY);
    }

    @Bean
    public Binding statisticBinding() {
        return BindingBuilder
                .bind(tasksDoneQueueStatisticsDefinition())
                .to(tasksDoneExchange())
                .with(TASKS_DONE_ROUTING_KEY);
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

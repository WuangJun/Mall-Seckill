package com.unstoppable.config;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Author:WJ
 * Date:2023/2/15 14:14
 * Description:<>
 */

@Slf4j
@Configuration
public class RabbitmqConfig {

    @Autowired
    private CachingConnectionFactory connectionFactory;
    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    public static final String EMAIL_EXCHANGE_NAME = "kill.item.success.email.exchange";
    public static final String EMAIL_QUEUE_NAME = "kill.item.success.email.queue";
    public static final String EMAIL_ROUTINE_KEY = "kill.item.success.email.routing.key";

    //前一个消息队列
    public static final String EMAIL_DEAD_QUEUE_NAME = "kill.item.success.email.dead.queue";
    //前一个关键字
    public static final String EMAIL_DEAD_PROD_ROUTINE_KEY = "kill.item.success.email.dead.prod.routing.key";
    //前一个交换机
    public static final String EMAIL_DEAD_PROD_EXCHANGE_NAME = "kill.item.success.email.dead.prod.exchange";
    //最终的消息队列
    public static final String EMAIL_DEAD_REAL_QUEUE_NAME = "kill.item.success.email.dead.real.queue";
    //最终的关键字
    public static final String EMAIL_DEAD_ROUTINE_KEY = "kill.item.success.email.dead.routing.key";
    //最终的交换机
    public static final String EMAIL_DEAD_EXCHANGE_NAME = "kill.item.success.email.dead.exchange";

    /**
     * 单个消费者
     * @return
     */
    @Bean(name="singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainerFactory(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setBatchSize(1);
        return factory;
    }

    /**
     * 多个消费者
     * @return
     */
    @Bean(name = "multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainerFactory(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory,connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //确认消费模式-NONE
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(15);
        factory.setPrefetchCount(5);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(){
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.warn("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",exchange,routingKey,replyCode,replyText,message);
            }
        });
        return rabbitTemplate;
    }

    //构建异步发送邮箱通知的消息模型
    @Bean
    public Queue successEmailQueue(){
        return new Queue(EMAIL_QUEUE_NAME,true);
    }

    @Bean
    public TopicExchange successEmailExchange(){
        return new TopicExchange(EMAIL_EXCHANGE_NAME,true,false);
    }

    @Bean
    public Binding successEmailBinding(){
        return BindingBuilder.bind(successEmailQueue()).to(successEmailExchange()).with(EMAIL_ROUTINE_KEY);
    }

    //构建秒杀成功之后-订单超时未支付的死信队列消息模型

    @Bean
    public Queue successKillDeadQueue(){
        Map<String, Object> argsMap= Maps.newHashMap();
        argsMap.put("x-dead-letter-exchange",EMAIL_DEAD_EXCHANGE_NAME);
        argsMap.put("x-dead-letter-routing-key",EMAIL_DEAD_ROUTINE_KEY);
        return new Queue(EMAIL_DEAD_QUEUE_NAME,true,false,false,argsMap);
    }

    //基本交换机
    @Bean
    public TopicExchange successKillDeadProdExchange(){
        return new TopicExchange(EMAIL_DEAD_PROD_EXCHANGE_NAME,true,false);
    }

    //创建基本交换机+基本路由 -> 死信队列 的绑定
    @Bean
    public Binding successKillDeadProdBinding(){
        return BindingBuilder.bind(successKillDeadQueue()).to(successKillDeadProdExchange()).with(EMAIL_DEAD_PROD_ROUTINE_KEY);
    }

    //真正的队列
    @Bean
    public Queue successKillRealQueue(){
        return new Queue(EMAIL_DEAD_REAL_QUEUE_NAME,true);
    }

    //死信交换机
    @Bean
    public TopicExchange successKillDeadExchange(){
        return new TopicExchange(EMAIL_DEAD_EXCHANGE_NAME,true,false);
    }

    //死信交换机+死信路由->真正队列 的绑定
    @Bean
    public Binding successKillDeadBinding(){
        return BindingBuilder.bind(successKillRealQueue()).to(successKillDeadExchange()).with(EMAIL_DEAD_ROUTINE_KEY);
    }
}

package com.unstoppable.manager;

import com.unstoppable.config.RabbitmqConfig;
import com.unstoppable.entity.ItemKillSuccess;
import com.unstoppable.service.ItemKillSuccessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author:WJ
 * Date:2023/2/15 17:12
 * Description:<rabbitmq发送消息服务>
 */
@Slf4j
@Service
public class RabbitmqProductManager {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ItemKillSuccessService itemKillSuccessService;

    /**
     * 秒杀成功异步发送邮件通知消息
     */
    public void sendKillSuccessEmailMsg(String orderNo) throws Exception {
        if(orderNo==null){
            throw new Exception("orderNo is empty");
        }
        log.info("kill success, ready to send email, the orderNo is:{}",orderNo);

        String msg = "this is a killSuccess msg, and the orderNo is:"+orderNo;

        try {
            rabbitTemplate.convertAndSend(RabbitmqConfig.EMAIL_EXCHANGE_NAME,RabbitmqConfig.EMAIL_ROUTINE_KEY,orderNo.getBytes());

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void sendKillSuccessOrderExpireMsg(String orderNo) throws Exception {

        if(orderNo==null){
            throw new Exception("orderNo is empty");
        }
        log.info("kill success, ready to send email, and it will be sent to dead message queue the orderNo is:{}",orderNo);

        String msg = "this is a killSuccess msg, which in a dead message queue, and the orderNo is:"+orderNo;

        try {
            rabbitTemplate.convertAndSend(RabbitmqConfig.EMAIL_EXCHANGE_NAME, RabbitmqConfig.EMAIL_ROUTINE_KEY, orderNo, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties mp=message.getMessageProperties();
                    mp.setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                    //TODO：动态设置TTL(为了测试方便，暂且设置10s)
                    mp.setExpiration("10000");
                    return message;
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

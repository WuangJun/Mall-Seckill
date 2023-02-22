package com.unstoppable.manager;

import com.unstoppable.common.vo.MailContentVo;
import com.unstoppable.common.vo.MailSendVO;
import com.unstoppable.config.RabbitmqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author:WJ
 * Date:2023/2/15 18:01
 * Description:<接收消息服务>
 */
@Slf4j
@Service
public class RabbitmqConsumeManager {

    @Autowired
    private KillOrderManager killOrderManager;

    @RabbitListener(queues = {RabbitmqConfig.EMAIL_QUEUE_NAME},containerFactory = "singleListenerContainer")
    public void consumeEmailMsg(String orderNo){

        try {
            MailContentVo mailContentVo = killOrderManager.selectSendEmailInfoByOrderCode(orderNo);
            log.info("receive mail advise, the advice information is:{}", mailContentVo.toString());
            MailSendVO mailSendVO = new MailSendVO();
            mailSendVO.setSubject("order confirm");
            mailSendVO.setContent(mailContentVo.toString());
            mailSendVO.setTos(mailContentVo.getEmail());
            log.info("the email described as follows:{}",mailSendVO.toString());
            // TODO: 2023/2/16 发送邮件

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = {RabbitmqConfig.EMAIL_DEAD_REAL_QUEUE_NAME},containerFactory = "singleListenerContainer")
    public void consumeExpireOrder(String orderNo){

        try {
            MailContentVo mailContentVo = killOrderManager.selectSendEmailInfoByOrderCode(orderNo);
            log.info("receive mail expire advise!!!!!");
            // TODO: 2023/2/16 处理过期消息

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

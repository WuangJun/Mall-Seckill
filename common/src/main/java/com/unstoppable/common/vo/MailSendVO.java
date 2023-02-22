package com.unstoppable.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Author:WJ
 * Date:2023/2/15 18:51
 * Description:<>
 */
@Data
public class MailSendVO implements Serializable {

    //邮件主题
    private String subject;
    //邮件内容
    private String content;
    //接收人
    private String tos;
}

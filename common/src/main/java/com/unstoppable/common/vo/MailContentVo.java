package com.unstoppable.common.vo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * Author:WJ
 * Date:2023/2/15 18:20
 * Description:<>
 */
@Slf4j
@Data
public class MailContentVo implements Serializable {

    private String orderNo;

    private String userName;

    private String email;

    private String itemName;

}

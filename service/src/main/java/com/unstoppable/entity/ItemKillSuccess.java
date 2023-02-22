package com.unstoppable.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:WJ
 * Date:2023/2/11 16:06
 * Description:<>
 */
@Data
public class ItemKillSuccess implements Serializable {

    private String code;

    private Integer itemId;

    private Integer killId;

    private Integer userId;

    private Byte status;

    private Date createTime;
}

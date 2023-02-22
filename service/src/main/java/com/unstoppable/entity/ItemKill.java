package com.unstoppable.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:WJ
 * Date:2023/2/11 16:04
 * Description:<>
 */
@Data
public class ItemKill implements Serializable {

    private Integer id;

    private Integer itemId;

    private Integer total;

    private Date startTime;

    private Date endTime;

    private Integer isActive;
}

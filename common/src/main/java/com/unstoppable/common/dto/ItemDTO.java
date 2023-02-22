package com.unstoppable.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:WJ
 * Date:2023/2/12 0:37
 * Description:<>
 */
@Data
public class ItemDTO implements Serializable {
    private Integer id;

    private Integer itemId;

    private Integer total;

    private Date startTime;

    private Date endTime;

    private Integer isActive;

    private String itemName;

    private Integer canKill;
}

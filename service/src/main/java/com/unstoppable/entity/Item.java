package com.unstoppable.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:WJ
 * Date:2023/2/11 16:01
 * Description:<>
 */
@Data
public class Item implements Serializable {

    private Integer id;

    private String name;

    private String code;

    private Integer stock;

    private Date createTime;
}

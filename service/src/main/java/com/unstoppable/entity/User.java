package com.unstoppable.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Author:WJ
 * Date:2023/2/11 14:12
 * Description:<>
 */
@Data
public class User implements Serializable {

    private Integer id;

    private String userName;

    private String password;

    private String phone;

    private String email;

    private Byte isActive;


}

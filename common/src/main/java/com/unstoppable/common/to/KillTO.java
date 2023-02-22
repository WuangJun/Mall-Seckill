package com.unstoppable.common.to;

import lombok.Data;
import lombok.ToString;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * Author:WJ
 * Date:2023/2/12 10:35
 * Description:<>
 */
@Data
@ToString
public class KillTO implements Serializable {

    @NotNull
    private Integer killId;

    private Integer userId;

}

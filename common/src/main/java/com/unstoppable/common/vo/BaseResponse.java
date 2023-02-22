package com.unstoppable.common.vo;

import com.unstoppable.common.enums.StatusCode;
import lombok.Data;

/**
 * Author:WJ
 * Date:2023/2/12 10:26
 * Description:<>
 */

@Data
public class BaseResponse<T>{

    private Integer code;
    private String msg;
    private T data;

    public BaseResponse(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseResponse(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.msg = statusCode.getMsg();
    }

    public BaseResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}

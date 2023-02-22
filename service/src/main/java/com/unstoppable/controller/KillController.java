package com.unstoppable.controller;

import com.unstoppable.common.enums.StatusCode;
import com.unstoppable.common.to.KillTO;
import com.unstoppable.common.vo.BaseResponse;
import com.unstoppable.manager.KillManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Author:WJ
 * Date:2023/2/12 10:14
 * Description:<>
 */
@Slf4j
@RequestMapping("/kill")
@Controller
public class KillController {

    @Autowired
    private KillManager killManager;

    @ResponseBody
    @PostMapping("/execute")
    public BaseResponse execute(@RequestBody @Validated KillTO killTO, BindingResult result, HttpSession session){

        if (result.hasErrors()|| killTO.getKillId()<=0){

            log.info("---------param is invalid-------");
            return new BaseResponse(StatusCode.InvalidParams);
        }

        Object uId=session.getAttribute("uid");
        if (uId!= killTO.getUserId()){
            log.info("---------user not login-----");
            return new BaseResponse(StatusCode.UserNotLogin);
        }
        //Integer userId=dto.getUserId();
        Integer userId= (Integer)uId ;

        try {
            Boolean res=killManager.killItem(killTO.getKillId(),userId);
            if (!res){
                return new BaseResponse(StatusCode.Fail.getCode(),"------item cannot be gotten!------");
            }
            log.info("-------congratulation!!!!------");
            return new BaseResponse(StatusCode.Success);
        }catch (Exception e){
            log.info("there is an exception:{}",e.getMessage());
            return new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
    }

    /**
     * 用分布式锁优化逻辑
     * @param killTO
     * @param result
     * @return
     */
    @ResponseBody
    @PostMapping("/execute/lock")
    public BaseResponse executeLock(@RequestBody @Validated KillTO killTO, BindingResult result){

        if (result.hasErrors()|| killTO.getKillId()<=0){
            log.info("---------param is invalid-------");
            return new BaseResponse(StatusCode.InvalidParams);
        }

        if (killTO.getUserId()==null){
            log.info("---------user not login-----");
            return new BaseResponse(StatusCode.UserNotLogin);
        }
        //Integer userId=dto.getUserId();
        Integer userId= killTO.getUserId() ;

        try {
            // 没加分布式锁
            // Boolean res=killManager.killItem(killVO.getKillId(),userId);
            // 加redis
            // Boolean res=killManager.killItemV2(killVO.getKillId(),userId);
            // 加redisson
            // Boolean res=killManager.killItemV3(killVO.getKillId(),userId);
            Boolean res=killManager.killItemV4(killTO.getKillId(),userId);
            if (!res){
                return new BaseResponse(StatusCode.Fail.getCode(),"------item cannot be gotten!------");
            }
            log.info("-------congratulation!!!!------");
            return new BaseResponse(StatusCode.Success);
        }catch (Exception e){
            log.info("there is an exception:{}",e.getMessage());
            return new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
    }


    //抢购成功跳转页面
    @GetMapping("/execute/success")
    public String executeSuccess(){
        return "executeSuccess";
    }

    //抢购失败跳转页面
    @GetMapping("/execute/fail")
    public String executeFail(){
        return "executeFail";
    }
}

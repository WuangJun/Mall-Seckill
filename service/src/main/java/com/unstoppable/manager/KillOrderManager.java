package com.unstoppable.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unstoppable.common.vo.MailContentVo;
import com.unstoppable.entity.Item;
import com.unstoppable.entity.ItemKillSuccess;
import com.unstoppable.entity.User;
import com.unstoppable.service.ItemKillSuccessService;
import com.unstoppable.service.ItemService;
import com.unstoppable.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author:WJ
 * Date:2023/2/15 18:26
 * Description:<>
 */
@Slf4j
@Service
public class KillOrderManager {
    @Autowired
    private ItemKillSuccessService itemKillSuccessService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    public MailContentVo selectSendEmailInfoByOrderCode(String orderNo) throws Exception {
        if(orderNo==null){
            throw new Exception("the orderNo is empty");
        }
        MailContentVo emailVo = new MailContentVo();

        LambdaQueryWrapper<ItemKillSuccess> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ItemKillSuccess::getCode, orderNo);
        ItemKillSuccess itemKillSuccess = itemKillSuccessService.getOne(queryWrapper);
        if(itemKillSuccess==null){
            throw new Exception("the order is not find");
        }
        User user = userService.getById(itemKillSuccess.getUserId());
        emailVo.setUserName(user.getUserName());
        emailVo.setEmail(user.getEmail());
        Item item = itemService.getById(itemKillSuccess.getItemId());
        emailVo.setItemName(item.getName());
        return emailVo;
    }
}

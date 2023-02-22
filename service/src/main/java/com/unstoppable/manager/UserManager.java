package com.unstoppable.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unstoppable.entity.User;
import com.unstoppable.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Author:WJ
 * Date:2023/2/11 14:30
 * Description:<>
 */
@Service
public class UserManager {
    @Autowired
    private UserService userService;

    public User findByName(String username){
        if(StringUtils.containsWhitespace(username)){
            return null;
        }else {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserName,username);
            User userServiceOne = userService.getOne(queryWrapper);
            return userServiceOne;
        }

    }
}

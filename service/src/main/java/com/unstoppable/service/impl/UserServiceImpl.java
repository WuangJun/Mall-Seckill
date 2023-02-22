package com.unstoppable.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unstoppable.entity.User;
import com.unstoppable.mapper.UserMapper;
import com.unstoppable.service.UserService;
import org.springframework.stereotype.Service;

/**
 * Author:WJ
 * Date:2023/2/11 14:28
 * Description:<>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}

package com.unstoppable.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unstoppable.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

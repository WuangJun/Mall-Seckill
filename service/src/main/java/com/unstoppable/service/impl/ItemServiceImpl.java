package com.unstoppable.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unstoppable.entity.Item;
import com.unstoppable.mapper.ItemMapper;
import com.unstoppable.service.ItemService;
import org.springframework.stereotype.Service;

/**
 * Author:WJ
 * Date:2023/2/11 16:14
 * Description:<>
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {
}

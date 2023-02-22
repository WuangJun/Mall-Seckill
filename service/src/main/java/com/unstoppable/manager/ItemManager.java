package com.unstoppable.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unstoppable.common.dto.ItemDTO;
import com.unstoppable.entity.Item;
import com.unstoppable.entity.ItemKill;
import com.unstoppable.service.ItemKillService;
import com.unstoppable.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author:WJ
 * Date:2023/2/11 16:25
 * Description:<>
 */
@Slf4j
@Service
public class ItemManager {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemKillService itemKillService;

    public List<ItemDTO> selectAll(){
        LambdaQueryWrapper<ItemKill> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ItemKill::getIsActive,1);
        List<ItemKill> itemKills = itemKillService.list(queryWrapper);

        List<ItemDTO> itemDTOList = itemKills.stream().map(itemKill -> {
            ItemDTO itemDTO = new ItemDTO();
            BeanUtils.copyProperties(itemKill,itemDTO);
            String itemName = itemService.getById(itemKill.getItemId()).getName();
            itemDTO.setItemName(itemName);
            Date currentTime = new Date();
            if(currentTime.before(itemKill.getEndTime())&&currentTime.after(itemKill.getStartTime())&&itemKill.getTotal()>0){
                itemDTO.setCanKill(1);
            }else {
                itemDTO.setCanKill(0);
            }
            return itemDTO;

        }).collect(Collectors.toList());
        return itemDTOList;
    }

    public ItemDTO selectById(Integer itemKillId){
        ItemKill itemKill = itemKillService.getById(itemKillId);
        ItemDTO itemDTO = new ItemDTO();
        BeanUtils.copyProperties(itemKill,itemDTO);
        String itemName = itemService.getById(itemKill.getItemId()).getName();
        itemDTO.setItemName(itemName);
        Date currentTime = new Date();
        if(currentTime.before(itemKill.getEndTime())&&currentTime.after(itemKill.getStartTime())&&itemKill.getTotal()>0){
            itemDTO.setCanKill(1);
        }else {
            itemDTO.setCanKill(0);
        }
        return itemDTO;
    }

    public boolean updateById(Integer killId){
        ItemKill itemKill = itemKillService.getById(killId);
        if (itemKill.getTotal()<=0){
            log.info("updateTotal failure");
            return false;
        }
        itemKill.setTotal(itemKill.getTotal()-1);
        itemKillService.updateById(itemKill);
        return true;
    }
}

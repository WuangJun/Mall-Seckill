package com.unstoppable.controller;

import com.unstoppable.common.dto.ItemDTO;
import com.unstoppable.entity.ItemKill;
import com.unstoppable.manager.ItemManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Author:WJ
 * Date:2023/2/11 15:38
 * Description:<>
 */
@Slf4j
@Controller
@RequestMapping("item")
public class ItemController {

    @Autowired
    private ItemManager itemManager;

    @RequestMapping("list")
    public String list(ModelMap modelMap){
        try {
            List<ItemDTO> itemDTOList = itemManager.selectAll();
            modelMap.addAttribute("list",itemDTOList);

            log.info("the itemKillList had been obtained");
            return "list";
        }catch (Exception e){
            log.info("the itemKillList has wrong：{}",e.toString());
            return "error";
        }
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, ModelMap modelMap){
        if (id==null || id<=0){
            return "redirect:/base/error";
        }
        try {
            ItemDTO detail=itemManager.selectById(id);
            modelMap.put("detail",detail);
            log.info("the item detail is obtained");
            return "info";
        }catch (Exception e){
            log.info("the item detail is failure：id={}",id);
            return "redirect:/base/error";
        }

    }
}

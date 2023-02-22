package com.unstoppable.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Author:WJ
 * Date:2023/2/10 16:42
 * Description:<>
 */
@Controller
@RequestMapping("base")
public class BaseController {

    @GetMapping("/login")
    public String welcome(){

        // if (StringUtils.containsWhitespace(param)){
        //     param="这里是welcome";
        // }
        // modelMap.put("name",param);
        return "login";
    }


}

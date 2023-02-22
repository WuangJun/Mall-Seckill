package com.unstoppable.controller;

import com.unstoppable.entity.User;
import com.unstoppable.manager.UserManager;
import com.unstoppable.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * Author:WJ
 * Date:2023/2/11 14:10
 * Description:<>
 */
@Slf4j
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserManager userManager;

    @PostMapping("/login")
    public String login(@RequestParam String userName, @RequestParam String password, ModelMap modelMap, HttpSession session){

        User user = userManager.findByName(userName);
        if(user==null){
            log.info("The customer is null");
            modelMap.addAttribute("errorMsg","该用户不存在");
            return "login";
        }else if (user.getPassword().equals(password)){
            log.info("login success");
            session.setAttribute("uid",user.getId());
            return "redirect:/item/list";
        }else {
            log.info("The password is error");
            return "login";
        }
    }
}

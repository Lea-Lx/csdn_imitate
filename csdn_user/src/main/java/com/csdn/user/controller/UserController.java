package com.csdn.user.controller;

import com.csdn.entity.Result;
import com.csdn.entity.StatusCode;
import com.csdn.user.pojo.User;
import com.csdn.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(@PathVariable User user) {
        User user1 = userService.login(user);
        if(user1 != null) {
            return new Result(true, StatusCode.OK, "登陆成功", user1);
        }
        return new Result(false, StatusCode.OK, "登陆失败");
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public Result selectById(@PathVariable("id") String id) {
        User user = userService.selectById(id);
        return new Result(true, StatusCode.OK, "查询成功", user);
    }
}

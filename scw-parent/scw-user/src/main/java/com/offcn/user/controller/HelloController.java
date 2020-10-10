package com.offcn.user.controller;

import com.offcn.user.po.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "控制类测试")
public class HelloController {

    @ApiOperation("测试方法")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "name",value = "姓名"),
            @ApiImplicitParam(name = "email",value = "邮箱")
    })
    @GetMapping("/user")
    public String getUser(String name,String email){
        return "Hello  " + name + "---------你的邮箱是：" + email;
    }

    @ApiOperation("测试保存用户方法")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "name",value = "姓名"),
            @ApiImplicitParam(name = "email",value = "邮箱")
    })
    @PostMapping("/save")
    public User save(String name,String email){
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}

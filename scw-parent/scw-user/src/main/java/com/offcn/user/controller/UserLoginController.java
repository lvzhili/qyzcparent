package com.offcn.user.controller;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.user.component.SmsTemplate;
import com.offcn.user.po.TMember;
import com.offcn.user.service.UserService;
import com.offcn.user.vo.req.UserRegistVo;
import com.offcn.user.vo.resp.UserRespVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Api(tags = "用户注册和登录！")
@Slf4j
public class UserLoginController {

    @Autowired
    private SmsTemplate smsTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserService userService;

    @ApiOperation("根据id查询用户信息")
    @GetMapping("/findMemById")
    public AppResponse<UserRespVo> findMemById(@RequestParam("id") Integer id){
        TMember member = userService.findMemById(id);

        UserRespVo respVo = new UserRespVo();
        BeanUtils.copyProperties(member,respVo);

        return AppResponse.ok(respVo);
    }


    @ApiOperation("用户登录")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "username",value = "登录名",required = true),
            @ApiImplicitParam(name = "password",value = "密码",required = true)
    })
    @PostMapping("/login")
    public AppResponse<UserRespVo> login(String username, String password){

        //登录
        TMember loginMember = userService.login(username, password);
        if (loginMember == null){
            //登录失败
            AppResponse<UserRespVo> respVo = AppResponse.fail(null);
            respVo.setMessage("用户名或者密码错误");
            return respVo;
        }
        //登录成功，存储redis
        String token = UUID.randomUUID().toString().replace("-","");
        UserRespVo respVo = new UserRespVo();
        BeanUtils.copyProperties(loginMember,respVo);

        respVo.setAccessToken(token);

        stringRedisTemplate.opsForValue().set(token,loginMember.getId()+"",2,TimeUnit.HOURS);
        return AppResponse.ok(respVo);
    }

    @ApiOperation("获取注册验证码")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "phone",value = "手机号",required = true)
    })
    @PostMapping("/sendCode")
    public AppResponse<Object> sendCode(String phone){
        //生成验证码
        String code = UUID.randomUUID().toString().substring(0, 4);
        //存入redis
        stringRedisTemplate.opsForValue().set(phone,code,10000, TimeUnit.MINUTES);
        //发送短信，构造参数
        Map<String,String> querys = new HashMap<>();
        querys.put("mobile",phone);
        querys.put("param", "code:" + code);
        querys.put("tpl_id", "TP1711063");//短信模板

        String send = smsTemplate.sendCode(querys);

        if (send.equals("") || send.equals("fail")){
            return AppResponse.fail("短信发送失败");
        }
        return AppResponse.ok("短信发送成功");
    }
    @ApiOperation("短信验证码：成员注册")
    @PostMapping("/register")
    public AppResponse<Object> register(UserRegistVo registVo){
        //效验验证码
        String code = stringRedisTemplate.opsForValue().get(registVo.getLoginacct());
        if (!StringUtils.isEmpty(code)){
            //不为空
            boolean b = code.equalsIgnoreCase(registVo.getCode());
            if (b){
                //可以注册
                TMember member = new TMember();
                BeanUtils.copyProperties(registVo,member);

                try {
                    userService.registerUser(member);
                    log.debug("用户注册成功：{}" ,member.getLoginacct());
                    stringRedisTemplate.delete(registVo.getLoginacct());
                    return AppResponse.ok("注册成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    return AppResponse.fail("注册失败");
                }
            }else {
                return AppResponse.fail("验证码错误");
            }
        }else {
            return AppResponse.fail("验证码已过期，请重新获取");
        }
    }
}

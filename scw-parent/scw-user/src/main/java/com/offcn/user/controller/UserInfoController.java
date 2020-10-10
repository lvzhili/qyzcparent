package com.offcn.user.controller;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.user.po.TMemberAddress;
import com.offcn.user.service.UserService;
import com.offcn.user.vo.resp.UserAddressVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "获取会员信息/更新个人信息/获取用户收货地址")
public class UserInfoController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserService userService;

    @ApiOperation("获取用户地址")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "token",value = "用户令牌",required = true)
    })
    @PostMapping("/getAddress")
    public AppResponse<List<UserAddressVo>> getAddress(@RequestParam("accessToken") String token) {

        //获取用户id
        String s = stringRedisTemplate.opsForValue().get(token);
        if (StringUtils.isEmpty(s)) {
            return AppResponse.fail(null);
        }
        Integer mid = Integer.parseInt(s);
        //查询用户信息和地址
        List<TMemberAddress> addressList = userService.addressList(mid);
        //将地址封装到vo
        List<UserAddressVo> voList = new ArrayList<>();
        for (TMemberAddress tMemberAddress : addressList) {
            UserAddressVo addressVo = new UserAddressVo();
            addressVo.setAddress(tMemberAddress.getAddress());
            addressVo.setId(tMemberAddress.getId());
            voList.add(addressVo);
        }
        return AppResponse.ok(voList);
    }
}

package com.offcn.webui.service.impl;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.webui.service.MemberServiceFeign;
import com.offcn.webui.vo.resp.UserAddressVo;
import com.offcn.webui.vo.resp.UserRespVo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberServiceFeignException implements MemberServiceFeign {
    @Override
    public AppResponse<UserRespVo> login(String username, String password) {
        AppResponse<UserRespVo> fail = AppResponse.fail(null);
        fail.setMessage("用户名或者密码错误");
        return fail;
    }

    @Override
    public AppResponse<UserRespVo> findMemById(Integer id) {
        AppResponse<UserRespVo> appResponse = AppResponse.fail(null);
        appResponse.setMessage("获取用户信息失败");
        return appResponse;
    }

    @Override
    public AppResponse<List<UserAddressVo>> getAddress(String token) {
        AppResponse<List<UserAddressVo>> appResponse = AppResponse.fail(null);
        appResponse.setMessage("获取用户地址失败");
        return appResponse;
    }
}

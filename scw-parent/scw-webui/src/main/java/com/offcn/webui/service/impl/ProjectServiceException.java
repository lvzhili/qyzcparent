package com.offcn.webui.service.impl;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.webui.service.ProjectServiceFeign;
import com.offcn.webui.vo.resp.ProjectDetailVo;
import com.offcn.webui.vo.resp.ReturnPayConfirmVo;
import org.springframework.stereotype.Component;

@Component
public class ProjectServiceException implements ProjectServiceFeign {
    @Override
    public AppResponse getProject() {
        AppResponse appResponse = AppResponse.fail(null);
        appResponse.setMessage("项目查询失败");
        return appResponse;
    }

    @Override
    public AppResponse<ReturnPayConfirmVo> getReturnByRid(Integer returnId) {
        AppResponse appResponse = AppResponse.fail(null);
        appResponse.setMessage("获取项目回报详情失败");
        return appResponse;
    }

    @Override
    public AppResponse<ProjectDetailVo> getDetailProject(Integer pid) {
        AppResponse appResponse = AppResponse.fail(null);
        appResponse.setMessage("获取项目详细信息失败");
        return appResponse;
    }
}

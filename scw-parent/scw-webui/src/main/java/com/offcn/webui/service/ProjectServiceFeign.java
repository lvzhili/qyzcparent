package com.offcn.webui.service;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.webui.config.FeignConfig;
import com.offcn.webui.service.impl.ProjectServiceException;
import com.offcn.webui.vo.resp.ProjectDetailVo;
import com.offcn.webui.vo.resp.ProjectReturnVo;
import com.offcn.webui.vo.resp.ReturnPayConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "SCW-PROJECT",configuration = FeignConfig.class,fallback = ProjectServiceException.class)
public interface ProjectServiceFeign {
    @GetMapping("/project/getProject")
    public AppResponse getProject();

    @GetMapping("/project/returns/info/{returnId}")
    public AppResponse<ReturnPayConfirmVo> getReturnByRid(@PathVariable("returnId") Integer returnId);

    @GetMapping("/project/details/info/{projectId}")
    public AppResponse<ProjectDetailVo> getDetailProject(@PathVariable("projectId") Integer pid);
}

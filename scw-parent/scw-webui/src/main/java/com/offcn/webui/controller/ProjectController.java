package com.offcn.webui.controller;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.webui.service.MemberServiceFeign;
import com.offcn.webui.service.ProjectServiceFeign;
import com.offcn.webui.vo.resp.ProjectDetailVo;
import com.offcn.webui.vo.resp.ReturnPayConfirmVo;
import com.offcn.webui.vo.resp.UserAddressVo;
import com.offcn.webui.vo.resp.UserRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectServiceFeign projectService;

    @Autowired
    private MemberServiceFeign memberServiceFeign;

    @RequestMapping("/confirm/order/{num}")
    public String confirmOrder(@PathVariable("num")Integer num,Model model, HttpSession session){
        //查询用户是否登录
        UserRespVo respVo = (UserRespVo) session.getAttribute("sessionMember");

        if (respVo == null){
            session.setAttribute("preUrl","project/confirm/order/"+num);
            return "redirect:/login.html";
        }
        String accessToken = respVo.getAccessToken();
        //获取到令牌，远程调用潮汛地址
        AppResponse<List<UserAddressVo>> address = memberServiceFeign.getAddress(accessToken);
        List<UserAddressVo> addressVoList = address.getData();
        //将地址加入model中
        model.addAttribute("addresses",addressVoList);
        //获取回报信息对象，重新设置数量和金额
        ReturnPayConfirmVo payConfirmVo = (ReturnPayConfirmVo) session.getAttribute("returnConfirm");
        payConfirmVo.setNum(num);
        payConfirmVo.setTotalPrice(new BigDecimal(num*payConfirmVo.getSupportmoney() + payConfirmVo.getFreight()));

        session.setAttribute("returnConfirmSession",payConfirmVo);
        return "project/pay-step-2";
    }

    /**
     * 项目回报查询请求方法
     * @param projectId
     * @param returnId
     * @param model
     * @param session
     * @return
     */
    @RequestMapping("/confirm/returns/{projectId}/{returnId}")
    public String returnInfo(@PathVariable("projectId") Integer projectId,@PathVariable("returnId") Integer returnId,Model model,HttpSession session){
        //从Session中获取项目详细信息
        ProjectDetailVo detailVo = (ProjectDetailVo) session.getAttribute("DetailVo");
        //根据项目编号，获取项目汇报信息
        AppResponse<ReturnPayConfirmVo> confirmVoAppResponse = projectService.getReturnByRid(returnId);
        ReturnPayConfirmVo payConfirmVo = confirmVoAppResponse.getData();

        //设置项目回报id
        payConfirmVo.setProjectId(projectId);
        //设置项目名
        payConfirmVo.setProjectName(detailVo.getName());
        //根据项目发起方id获取发起方名称
        AppResponse<UserRespVo> memById = memberServiceFeign.findMemById(detailVo.getMemberid());
        UserRespVo respVo = memById.getData();
        //设置回报发起人名称
        payConfirmVo.setMemberName(respVo.getRealname());
        //存入sessiion中
        session.setAttribute("returnConfirm",payConfirmVo);
        //填入model中
        model.addAttribute("returnConfirm",payConfirmVo);
        return "project/pay-step-1";
    }
    /**
     * 获取项目详细信息
     * @param id
     * @param model
     * @param session
     * @return
     */
    @RequestMapping("/projectInfo")
    public String index(Integer id, Model model, HttpSession session){
        //远程调用项目详情信息
        AppResponse<ProjectDetailVo> detailProject = projectService.getDetailProject(id);
        ProjectDetailVo detailVo = detailProject.getData();

        model.addAttribute("DetailVo",detailVo);
        session.setAttribute("DetailVo",detailVo);
        return "project/project";
    }

}

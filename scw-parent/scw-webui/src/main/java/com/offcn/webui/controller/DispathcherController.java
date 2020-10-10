package com.offcn.webui.controller;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.webui.service.MemberServiceFeign;
import com.offcn.webui.service.ProjectServiceFeign;
import com.offcn.webui.vo.resp.ProjectVo;
import com.offcn.webui.vo.resp.UserRespVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class DispathcherController {

    @Autowired
    private MemberServiceFeign memberServiceFeign;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProjectServiceFeign projectService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/")
    public String toIndex(Model model){
        //先去redis中获取所有的项目
        List<ProjectVo> voList = (List<ProjectVo>) redisTemplate.opsForValue().get("projectList");
        //如果没有再redis中查到就去 远程调用去数据库查
        if (voList == null){
            AppResponse project = projectService.getProject();
            voList = (List<ProjectVo>) project.getData();
            //将查到的数据存入redis中
            redisTemplate.opsForValue().set("projectList",voList);
        }
        model.addAttribute("projectList",voList);
        return "index";
    }
    //登录
    @PostMapping("/doLogin")
    public String login(String loginacct, String password, HttpSession session){
        //远程调用登录方法，返回用户对象
        AppResponse<UserRespVo> respVoAppResponse = memberServiceFeign.login(loginacct, password);
        UserRespVo userRespVo = respVoAppResponse.getData();

        log.info("登录账号:{},密码:{}",loginacct,password);

        log.info("登录用户数据:{}",userRespVo);
        //未查到，重新到登录页面
        if (userRespVo == null){
            return "redirect:/login.html";
        }
        //查到后，存入redis中
        session.setAttribute("sessionMember",userRespVo);
        //获取头路径
        String url = (String) session.getAttribute("perUrl");
        //如果不存在，跳转到默认页面
        if (StringUtils.isEmpty(url)){
            return "redirect:/";
        }
        //如果存在，跳转到制定页面
        return "redirect:/" + url;
    }
}

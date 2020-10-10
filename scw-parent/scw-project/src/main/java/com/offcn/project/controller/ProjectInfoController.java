package com.offcn.project.controller;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.project.po.*;
import com.offcn.project.service.ProjectInfoService;
import com.offcn.project.vo.resp.ProjectDetailVo;
import com.offcn.project.vo.resp.ProjectVo;
import com.offcn.util.OssTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "项目基本功能模块（文件上传、项目信息获取等）")
@Slf4j
@RequestMapping("/project")
public class ProjectInfoController {

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private ProjectInfoService projectInfoService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("获取项目回报详细信息")
    @GetMapping("/returns/info/{returnId}")
    public AppResponse<TReturn> getReturnByRid(@PathVariable("returnId") Integer returnId){
        TReturn tReturn = projectInfoService.getReturn(returnId);
        return AppResponse.ok(tReturn);
    }

    @ApiOperation("获取所有的项目分类")
    @GetMapping("/getAllTypes")
    public AppResponse getAllTypes(){
        List<TType> typeList = projectInfoService.getAllProjectType();
        return AppResponse.ok(typeList);
    }

    @ApiOperation("获取所有的标签信息")
    @GetMapping("/getAllTags")
    public AppResponse getAllTags(){
        List<TTag> tagList = projectInfoService.getAllProjectTags();
        return AppResponse.ok(tagList);
    }

    @ApiOperation("获取项目详细信息")
    @GetMapping("/details/info/{projectId}")
    public AppResponse<ProjectDetailVo> getDetailProject(@PathVariable("projectId") Integer pid){
        TProject project = projectInfoService.getProjectByPid(pid);
        //创建返回对象vo
        ProjectDetailVo vo = new ProjectDetailVo();
        BeanUtils.copyProperties(project,vo);

        //根据项目id获取所有图片并给vo赋值
        List<TProjectImages> imagesList = projectInfoService.getProjectImages(pid);

        List<String> urlList = vo.getDetailsImage();

        if (urlList == null){
            urlList = new ArrayList<>();
        }
        for (TProjectImages tProjectImages : imagesList) {
            if (tProjectImages.getImgtype() == 0){
                vo.setHeaderImage(tProjectImages.getImgurl());
            }else {
                urlList.add(tProjectImages.getImgurl());
            }
        }
        vo.setDetailsImage(urlList);
        //获取项目所有回报给vo赋值
        List<TReturn> returnList = projectInfoService.getProjectReturn(project.getId());
        vo.setProjectReturns(returnList);

        return AppResponse.ok(vo);
    }

    @ApiOperation("获取所有的项目")
    @GetMapping("/getProject")
    public AppResponse getProject(){

        List<ProjectVo> voList = new ArrayList<>();
        //查询所有项目
        List<TProject> projectList = projectInfoService.getAll();
        //将项目存入vo中

        for (TProject tProject : projectList) {
            ProjectVo projectVo = new ProjectVo();
            BeanUtils.copyProperties(tProject,projectVo);

            //查询项目头图片，存入vo中
            List<TProjectImages> imagesList = projectInfoService.getProjectImages(tProject.getId());
            for (TProjectImages tProjectImages : imagesList) {
                if (tProjectImages.getImgtype() == 0){
                    projectVo.setHeaderImage(tProjectImages.getImgurl());
                }
            }
            voList.add(projectVo);
        }
        return AppResponse.ok(voList);
    }

    @ApiOperation("项目回报信息")
    @GetMapping("/details/returns/{projectId}")
    public AppResponse<List<TReturn>> getReturn(@PathVariable("projectId") Integer pid){
        List<TReturn> returnList = projectInfoService.getProjectReturn(pid);
        return AppResponse.ok(returnList);
    }
    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public AppResponse<Map<String,Object>> upload(@RequestParam("file")MultipartFile[] files) throws IOException {

        Map<String,Object> map = new HashMap<>();
        List<String> list = new ArrayList<>();

        if (files != null && files.length > 0){
            for (MultipartFile file : files) {
                if (!file.isEmpty()){
                    String upload = ossTemplate.upload(file.getInputStream(), file.getOriginalFilename());
                    list.add(upload);
                }
            }
        }
        map.put("urls",list);
        log.debug("ossTemplate信息：{},文件上传成功访问路径{}",ossTemplate,list);
        return AppResponse.ok(map);
    }
}

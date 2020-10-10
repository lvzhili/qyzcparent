package com.offcn.project.service.impl;

import com.alibaba.fastjson.JSON;
import com.offcn.project.contants.ProjectConstant;
import com.offcn.project.enums.ProjectImageTypeEnume;
import com.offcn.project.enums.ProjectStatusEnume;
import com.offcn.project.mapper.*;
import com.offcn.project.po.*;
import com.offcn.project.service.ProjectCreateService;
import com.offcn.project.vo.req.ProjectRedisStorageVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class ProjectCreateServiceImpl implements ProjectCreateService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TProjectMapper projectMapper;

    @Autowired
    private TProjectTagMapper projectTagMapper;

    @Autowired
    private TProjectTypeMapper projectTypeMapper;

    @Autowired
    private TProjectImagesMapper projectImagesMapper;

    @Autowired
    private TReturnMapper returnMapper;

    /**
     * 创建一个项目
     * @param id
     * @return
     */
    @Override
    public String initCreateProject(Integer id) {

        String projectToken = UUID.randomUUID().toString().replace("-","");

        //创建项目临时对象
        ProjectRedisStorageVo storageVo = new ProjectRedisStorageVo();
        storageVo.setMemberid(id);

        //转化为json
        String jsonStVo = JSON.toJSONString(storageVo);
        //将项目令牌存入redis中
        stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken,jsonStVo);
        return projectToken;
    }
    //将redis中的数据保存到数据库中
    @Override
    public void saveProjectInfo(ProjectStatusEnume status, ProjectRedisStorageVo storageVo) {
        //保存项目的基本信息
        TProject tProject = new TProject();
        BeanUtils.copyProperties(storageVo,tProject);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tProject.setCreatedate(df.format(new Date()));
        tProject.setStatus(status.getCode()+"");

        projectMapper.insertSelective(tProject);
        //获取刚存入基本信息表的id
        Integer projectId = tProject.getId();
        //保存projecttag中间表
        for (Integer tagid : storageVo.getTagids()) {
            TProjectTag tProjectTag = new TProjectTag(null,projectId,tagid);
            projectTagMapper.insertSelective(tProjectTag);
        }
        //保存project type中间表
        for (Integer typeid : storageVo.getTypeids()) {
            TProjectType tProjectType = new TProjectType(null,projectId,typeid);
            projectTypeMapper.insertSelective(tProjectType);
        }
        //保存project images中间表
        //保存头图
        String headerImage = storageVo.getHeaderImage();
        TProjectImages tProjectImages = new TProjectImages(null, projectId, headerImage, ProjectImageTypeEnume.HEADER.getCode());
        projectImagesMapper.insertSelective(tProjectImages);

        //保存详细图
        for (String detailImageUrl : storageVo.getDetailsImage()) {
            TProjectImages projectImages = new TProjectImages(null, projectId, detailImageUrl, ProjectImageTypeEnume.DETAILS.getCode());
            projectImagesMapper.insertSelective(projectImages);
        }
        //保存回报信息
        for (TReturn projectReturn : storageVo.getProjectReturns()) {
            projectReturn.setProjectid(projectId);
            returnMapper.insertSelective(projectReturn);
        }
        //删除临时数据
        stringRedisTemplate.delete(ProjectConstant.TEMP_PROJECT_PREFIX+storageVo.getProjectToken());
    }
}

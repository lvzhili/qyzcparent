package com.offcn.project.service;

import com.offcn.project.po.*;

import java.util.List;

public interface ProjectInfoService {
    //获取回报信息
    public List<TReturn> getProjectReturn(Integer pid);
    //获取所有项目
    public List<TProject> getAll();
    //根据id获取头图片
    public List<TProjectImages> getProjectImages(Integer id);
    //根据项目id获取项目
    public TProject getProjectByPid(Integer pid);
    //获取项目所有的标签
    public List<TTag> getAllProjectTags();
    //获取所有的项目分类
    public List<TType> getAllProjectType();
    //获取项目回报详细信息
    public TReturn getReturn(Integer returnId);
}

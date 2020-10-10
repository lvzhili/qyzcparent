package com.offcn.project.service.impl;

import com.offcn.project.mapper.*;
import com.offcn.project.po.*;
import com.offcn.project.service.ProjectInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProjectInfoServiceImpl implements ProjectInfoService {

    @Autowired
    private TReturnMapper returnMapper;

    @Autowired
    private TProjectMapper projectMapper;

    @Autowired
    private TProjectImagesMapper projectImagesMapper;

    @Autowired
    private TTagMapper tTagMapper;

    @Autowired
    private TTypeMapper tTypeMapper;

    @Override
    public List<TReturn> getProjectReturn(Integer pid) {

        TReturnExample example = new TReturnExample();
        TReturnExample.Criteria criteria = example.createCriteria();
        criteria.andProjectidEqualTo(pid);

        return returnMapper.selectByExample(example);
    }

    @Override
    public List<TProject> getAll() {
        return projectMapper.selectByExample(null);
    }

    @Override
    public List<TProjectImages> getProjectImages(Integer pid) {
        TProjectImagesExample example = new TProjectImagesExample();
        TProjectImagesExample.Criteria criteria = example.createCriteria();
        criteria.andProjectidEqualTo(pid);
        return projectImagesMapper.selectByExample(example);
    }

    @Override
    public TProject getProjectByPid(Integer pid) {
        return projectMapper.selectByPrimaryKey(pid);
    }

    @Override
    public List<TTag> getAllProjectTags() {
        return tTagMapper.selectByExample(null);
    }

    @Override
    public List<TType> getAllProjectType() {
        return tTypeMapper.selectByExample(null);
    }

    @Override
    public TReturn getReturn(Integer returnId) {
        return returnMapper.selectByPrimaryKey(returnId);
    }
}

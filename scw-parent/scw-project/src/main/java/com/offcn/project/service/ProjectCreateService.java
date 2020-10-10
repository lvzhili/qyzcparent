package com.offcn.project.service;

import com.offcn.project.enums.ProjectStatusEnume;
import com.offcn.project.vo.req.ProjectRedisStorageVo;

public interface ProjectCreateService {
    public String initCreateProject(Integer id);
    public void saveProjectInfo(ProjectStatusEnume status, ProjectRedisStorageVo storageVo);
}

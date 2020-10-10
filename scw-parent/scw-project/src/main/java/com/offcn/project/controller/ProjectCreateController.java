package com.offcn.project.controller;

import com.alibaba.fastjson.JSON;
import com.offcn.dycommon.response.AppResponse;
import com.offcn.project.contants.ProjectConstant;
import com.offcn.project.enums.ProjectStatusEnume;
import com.offcn.project.po.TReturn;
import com.offcn.project.service.ProjectCreateService;
import com.offcn.project.vo.req.ProjectBaseInfoVo;
import com.offcn.project.vo.req.ProjectRedisStorageVo;
import com.offcn.project.vo.req.ProjectReturnVo;
import com.offcn.util.BaseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/project")
@Api(tags = "项目基本功能模块（创建、保存、项目信息获取、文件上传等）")
@Slf4j
public class ProjectCreateController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProjectCreateService projectCreateService;

    @ApiOperation("项目保存第四步-保存并提交项目")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "accessToken",value = "用户令牌",required = true),
            @ApiImplicitParam(name = "projectToken",value = "项目标识",required = true),
            @ApiImplicitParam(name = "ops",value = "用户操作类型：0-保存草稿，1-提交审核",required = true),
    })
    @PostMapping("/submitProjectInfo")
    public AppResponse<Object> submitProjectInfo(String accessToken,String projectToken,String ops){
        //效验是否登录
        String memId = stringRedisTemplate.opsForValue().get(accessToken);
        if (StringUtils.isEmpty(memId)){
            return AppResponse.fail("无权限，请登录");
        }
        //根据项目Token，获取项目信息
        String jsonRedisVo = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX + projectToken);
        //转化为对象
        ProjectRedisStorageVo redisStorageVo = JSON.parseObject(jsonRedisVo, ProjectRedisStorageVo.class);
        //如果操作类型不为空
        if (!StringUtils.isEmpty(ops)){
            if (ops.equals("0")){
                //等于0时，保存草稿
                ProjectStatusEnume draft = ProjectStatusEnume.DRAFT;
                projectCreateService.saveProjectInfo(draft,redisStorageVo);
                return AppResponse.ok(null);
            }else if (ops.equals("1")){
                //等于1时，保存项目信息
                ProjectStatusEnume submitAuth = ProjectStatusEnume.SUBMIT_AUTH;
                projectCreateService.saveProjectInfo(submitAuth,redisStorageVo);
                return AppResponse.ok(null);
            }else {
                //其他情况，不支持此操作
                AppResponse<Object> fail = AppResponse.fail(null);
                fail.setMessage("不支持此操作");
                return fail;
            }
        }
        //操作类型为空，返回null
        return AppResponse.fail(null);
    }

    @ApiOperation("项目发起第三部-保存回报信息")
    @PostMapping("/saveReturnVo")
    public  AppResponse<Object> saveReturnVo(@RequestBody List<ProjectReturnVo> returnVoList){
        ProjectReturnVo returnVo = returnVoList.get(0);
        //从redis中获取之前的信息
        String stringVo = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX+returnVo.getProjectToken());
        //将json转换为对象
        ProjectRedisStorageVo redisStorageVo = JSON.parseObject(stringVo, ProjectRedisStorageVo.class);
        //将页面搜集的回报信息存入集合中
        List<TReturn> list = new ArrayList<>();
        //循环回报信息，存入集合中
        for (ProjectReturnVo projectReturnVo : returnVoList) {
            TReturn tReturn = new TReturn();
            BeanUtils.copyProperties(projectReturnVo,tReturn);
            list.add(tReturn);
        }
        //将回报信息保存在redisVo中，更新redis
        redisStorageVo.setProjectReturns(list);
        String redisVo = JSON.toJSONString(redisStorageVo);
        stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX+returnVo.getProjectToken(),redisVo);
        return AppResponse.ok("OK");
    }

    @ApiOperation("项目发起第二部-保存项目基本信息")
    @PostMapping("/saveBaseInfo")
    public AppResponse<String> saveBaseInfo(ProjectBaseInfoVo baseInfoVo){
        //从redis中获取之前的信息
        String storageVo = stringRedisTemplate.opsForValue().get(ProjectConstant.TEMP_PROJECT_PREFIX + baseInfoVo.getProjectToken());
        //将json转化为对象
        ProjectRedisStorageVo redisStorageVo = JSON.parseObject(storageVo, ProjectRedisStorageVo.class);
        //将redisStorageVo和baseInfoVo复制
        BeanUtils.copyProperties(baseInfoVo,redisStorageVo);
        //将对象redisStorageVo转为为字符串
        String redisVo = JSON.toJSONString(redisStorageVo);
        //更新redis
        stringRedisTemplate.opsForValue().set(ProjectConstant.TEMP_PROJECT_PREFIX+baseInfoVo.getProjectToken(),redisVo);
        return AppResponse.ok("OK");
    }

    @ApiOperation("项目发起第1步-阅读同意协议")
    @PostMapping("/init")
    public AppResponse<String> init(BaseVo baseVo){
        String accessToken = baseVo.getAccessToken();
        //通过登录令牌获取id
        String id = stringRedisTemplate.opsForValue().get(accessToken);

        if (StringUtils.isEmpty(id)){
            return AppResponse.fail("无权限，请登录");
        }

        int memId = Integer.parseInt(id);
        //将临时项目信息存储到redis中
        String projectToken = projectCreateService.initCreateProject(memId);
        return AppResponse.ok(projectToken);
    }
}

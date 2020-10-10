package com.offcn;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {UserStartApplication.class})
public class ScwUserApplicationTest {

    Logger logger = LoggerFactory.getLogger(getClass());//引入日志文件

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Test
    public void contextLoads(){
        stringRedisTemplate.opsForValue().set("msg","欢迎来到崩坏世界！");
        logger.debug("操作成功");
    }
}

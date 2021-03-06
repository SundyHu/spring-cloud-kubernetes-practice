package com.ryan.cloud.kubernetes.article.controller;

import com.ryan.cloud.kubernetes.common.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * IndexController
 *
 * @author hkc
 * @version 1.0.0
 * @date 2021-03-02 16:32
 */
@RestController
@Slf4j
public class IndexController {

    @Resource
    DiscoveryClient discoveryClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping(value = {"", "/", "/index"})
    public Result<Void> index(HttpServletRequest request) {

        log.info(" the discoveryClient is  " + discoveryClient);

        stringRedisTemplate.opsForValue().set("platformName", "SpringCloudKubernetesDevOps");

        return Result.ok();
    }
}

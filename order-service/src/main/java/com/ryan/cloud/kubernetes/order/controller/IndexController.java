package com.ryan.cloud.kubernetes.order.controller;

import com.ryan.cloud.kubernetes.common.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * IndexController
 *
 * @author hkc
 * @version 1.0.0
 * @date 2021-03-09 14:37
 */
@RestController
@Slf4j
public class IndexController {

    @GetMapping(value = {"", "/", "/index"})
    public Result<Void> index(HttpServletRequest request) {

        return Result.ok();
    }
}

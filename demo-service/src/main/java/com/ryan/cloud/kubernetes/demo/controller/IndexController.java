package com.ryan.cloud.kubernetes.demo.controller;

import com.ryan.cloud.kubernetes.common.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * IndexController
 *
 * @author Ryan
 * @version 1.0.0
 * @date 2021-02-26 15:13
 */
@RestController
@Slf4j
public class IndexController {

    private static final long serialVersionUID = 1L;

    @GetMapping(value = {"", "/", "/index"})
    public Result<Void> index() {

        return Result.ok();
    }
}

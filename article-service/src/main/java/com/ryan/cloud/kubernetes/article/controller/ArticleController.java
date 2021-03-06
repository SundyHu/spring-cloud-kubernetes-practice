package com.ryan.cloud.kubernetes.article.controller;

import com.ryan.cloud.kubernetes.article.model.Article;
import com.ryan.cloud.kubernetes.article.service.ArticleService;
import com.ryan.cloud.kubernetes.common.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * ArticleController
 *
 * @author hkc
 * @version 1.0.0
 * @date 2021-03-06 15:25
 */
@RestController
@RequestMapping(value = "/articles")
@Slf4j
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @GetMapping(value = "/list")
    public Result<List<Article>> list() {

        List<Article> articles = articleService.list();

        return Result.ok(articles);
    }
}

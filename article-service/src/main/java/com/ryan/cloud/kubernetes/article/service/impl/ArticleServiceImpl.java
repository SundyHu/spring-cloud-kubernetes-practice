package com.ryan.cloud.kubernetes.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryan.cloud.kubernetes.article.mapper.ArticleMapper;
import com.ryan.cloud.kubernetes.article.model.Article;
import com.ryan.cloud.kubernetes.article.service.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ArticleServiceImpl
 *
 * @author hkc
 * @version 1.0.0
 * @date 2021-03-06 15:24
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

}

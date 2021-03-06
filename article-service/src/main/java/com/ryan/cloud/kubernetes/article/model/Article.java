package com.ryan.cloud.kubernetes.article.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Article
 *
 * @author hkc
 * @version 1.0.0
 * @date 2021-03-06 15:21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_article")
public class Article extends Model<Article> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;

    private String author;
}

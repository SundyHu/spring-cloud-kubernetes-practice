package com.ryan.cloud.kubernetes.article;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * ArticleServiceApplication
 *
 * @author hkc
 * @version 1.0.0
 * @date 2021-03-02 16:32
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ArticleServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(ArticleServiceApplication.class, args);
    }
}

package com.ryan.cloud.kubernetes.product.controller;

import com.ryan.cloud.kubernetes.common.core.result.Result;
import com.ryan.cloud.kubernetes.product.model.Product;
import com.ryan.cloud.kubernetes.product.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * ProductController
 *
 * @author hkc
 * @version 1.0.0
 * @date 2021-03-08 15:29
 */
@RestController
@RequestMapping(value = "/products")
public class ProductController {

    @Resource
    private ProductService productService;

    @GetMapping(value = "/list")
    public Result<List<Product>> list() {

        List<Product> list = productService.list();

        return Result.ok(list);
    }
}

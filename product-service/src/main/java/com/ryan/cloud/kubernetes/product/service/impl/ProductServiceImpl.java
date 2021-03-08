package com.ryan.cloud.kubernetes.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ryan.cloud.kubernetes.product.mapper.ProductMapper;
import com.ryan.cloud.kubernetes.product.model.Product;
import com.ryan.cloud.kubernetes.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ProductServiceImpl
 *
 * @author hkc
 * @version 1.0.0
 * @date 2021-03-08 15:28
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

}

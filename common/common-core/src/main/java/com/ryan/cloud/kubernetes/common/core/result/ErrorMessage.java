package com.ryan.cloud.kubernetes.common.core.result;

/**
 * 通用错误信息
 *
 * @author Ryan
 * @version 1.0.0
 * @date 2021-02-26 14:31
 */
public interface ErrorMessage {

    /**
     * 获取响应码
     *
     * @return 响应码
     */
    Integer getCode();

    /**
     * 获取响应信息
     *
     * @return 响应信息
     */
    String getMessage();
}

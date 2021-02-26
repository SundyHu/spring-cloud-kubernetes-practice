package com.ryan.cloud.kubernetes.common.core.result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Result
 *
 * @author hkc
 * @version 1.0.0
 * @date 2021-02-26 14:56
 */
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    private T data;

    private Result() {
        this.code = SystemError.SUCCESS.getCode();
        this.message = SystemError.SUCCESS.getMessage();
    }

    private Result(T data) {
        this();
        this.data = data;
    }

    private Result(ErrorMessage errorMessage) {
        this.code = errorMessage.getCode();
        this.message = errorMessage.getMessage();
    }

    private Result(ErrorMessage errorMessage, T data) {
        this(errorMessage);
        this.data = data;
    }

    public static <T> Result<T> ok() {
        return new Result<>();
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(data);
    }

    public static <T> Result<T> failure() {
        return new Result<>(SystemError.FAILURE);
    }

    public static <T> Result<T> failure(T data) {
        return new Result<>(SystemError.FAILURE, data);
    }

    public static <T> Result<T> fallback() {
        return new Result<>(SystemError.SERVICE_FALLBACK);
    }

    public static <T> Result<T> fallback(T data) {
        return new Result<>(SystemError.SERVICE_FALLBACK, data);
    }
}

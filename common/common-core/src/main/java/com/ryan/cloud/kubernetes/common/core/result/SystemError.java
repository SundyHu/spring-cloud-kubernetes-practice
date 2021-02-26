package com.ryan.cloud.kubernetes.common.core.result;

/**
 * SystemError
 *
 * @author Ryan
 * @version 1.0.0
 * @date 2021-02-26 14:49
 */
public enum SystemError implements ErrorMessage {

    /**
     * SUCCESS：操作成功,
     * FAILURE：操作失败
     */
    SUCCESS(1, "操作成功"), FAILURE(0, "操作失败");

    private final Integer code;

    private final String message;

    SystemError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

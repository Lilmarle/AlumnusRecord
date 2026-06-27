package com.marler.alumnus.pojo;

import lombok.Data;

@Data
public class Result {
    private Integer code;
    private String message;
    private Object data;

    private Result() {}

    /**
     * 成功响应（带数据和自定义消息）
     * @param data    返回数据
     */
    public static Result success(Object data) {
        Result result = new Result();
        result.code = 1;
        result.message = "操作成功";
        result.data = data;
        return result;
    }

    /**
     * 成功响应（不带数据）
     */
    public static Result success() {
        return success(null);
    }

    /**
     * 成功响应（带自定义消息和数据）
     * @param message 自定义成功消息
     * @param data    返回数据
     */
    public static Result success(String message, Object data) {
        Result result = new Result();
        result.code = 1;
        result.message = message;
        result.data = data;
        return result;
    }

    public static Result error(Integer code, String message) {
        Result result = new Result();
        result.code = code;
        result.message = message;
        return result;
    }

    public static Result error(String message) {
        return error(400, message);
    }

    public static Result unauthorized(String message) {
        return error(401, message);
    }
}

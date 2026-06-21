package com.marler.alumnus.pojo;

import lombok.Data;

@Data
public class Result {
    private Integer code;
    private String message;
    private Object data;

    private Result() {}

    public static Result success(Object data) {
        Result result = new Result();
        result.code = 200;
        result.message = "操作成功";
        result.data = data;
        return result;
    }

    public static Result success() {
        return success(null);
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

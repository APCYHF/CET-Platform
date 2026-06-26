package com.cetprep.advice;

import lombok.Data;

/**
 * 统一响应结果封装
 */
@Data
public class Result<T> {
    private Integer code;//状态码
    private String msg;//提示信息
    private T data;//数据
    private Long timestamp;//时间戳

    public Result() {
        //每次创建 Result 对象时，自动记录当前时间
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        return error(500, msg);
    }

    public static <T> Result<T> error(int code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}

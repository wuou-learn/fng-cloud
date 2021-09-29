package com.fng.dto;

/**
 * @author wuou
 * @version 1.0.0
 * @date [2020/7/31 下午2:48]
 */
public class Result {
    /** 是否成功 */
    protected Integer code;

    /** 描述信息 */
    protected String message;

    /** 返回对象 */
    private Object data;

    /**
     * 默认返回结果
     */
    public Result(){
        this.message = "";
        this.code=1;
    }

    /**
     * 有参数：成功
     * @param data 返回数据data
     * @return result
     */
    public Result(Object data) {
        this.code=1;
        this.data=data;
        this.message ="成功";
    }

    /**
     * 失败
     * @param code code
     * @param message message
     * @return result
     */
    public Result(Integer code, String message) {
        this.code=code;
        this.message = message;
    }

    /**
     * 失败
     * @param code code
     * @param message message
     * @return result
     */
    public Result(Integer code, String message, Object data) {
        this.code=code;
        this.message = message;
        this.data=data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

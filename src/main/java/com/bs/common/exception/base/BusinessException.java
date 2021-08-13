package com.bs.common.exception.base;

/**
 * Describe: 业 务 异 常
 * Author: test-admin
 * CreateTime: 2019/10/23
 * */
public class BusinessException extends RuntimeException {

    /**
     * 异常消息
     * */
    protected final String message;

    /**
     * 业务异常
     * */
    public BusinessException(String message){
        this.message = message;
    }

    /**
     * 异常获取
     * */
    @Override
    public String getMessage() {
        return message;
    }



}

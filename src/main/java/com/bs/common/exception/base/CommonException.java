package com.bs.common.exception.base;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName CommonException
 * @Description 公共异常
 * @date 2021/8/2 17:40
 */
public class CommonException extends RuntimeException{

    protected Object data;

    public CommonException() {
        super();
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonException(String message) {
        super(message);
    }

    public <T>CommonException(String message,T data) {
        super(message);
        this.data = data;
    }

    public CommonException(String message, Object... objects) {
        super(convertMessage(message,objects));
    }

    public CommonException(Throwable cause) {
        super(cause);
    }

    public <T> T getExData(){
        return (T) this.data;
    }

    public static String convertMessage(String message,Object... objects) {
        if (message.indexOf("{}")>0){
            String s = message.replaceAll("\\{\\}", "%s");
            String format = null;
            try {
                format = String.format(s, objects);
            } catch (Exception e) {
                return message;
            }
            return format;
        }else {
            return message;
        }
    }



}

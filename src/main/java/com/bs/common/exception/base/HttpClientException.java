package com.bs.common.exception.base;



/**
 * @author admin
 */
public class HttpClientException extends CommonException {


    public HttpClientException() {
        super();
    }

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String message, Object... objects) {
        super(convertMessage(message,objects));
    }
}

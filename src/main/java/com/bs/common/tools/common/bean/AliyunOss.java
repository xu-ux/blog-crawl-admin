package com.bs.common.tools.common.bean;

import lombok.Data;

/**
 * @descriptions:
 * @author: xucl
 * @date: 2021/8/12
 * @version: 1.0
 */
@Data
public class AliyunOss {

    private String endpoint;
    private String publicEndpoint;
    private String internalEndpoint;
    private String callbackUrl;
    private String publicBucketName;
    private String accessKeyId;
    private String accessKeySecret;
}

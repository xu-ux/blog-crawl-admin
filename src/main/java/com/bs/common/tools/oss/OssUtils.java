package com.bs.common.tools.oss;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import javax.annotation.Nonnull;
import java.io.InputStream;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName OssUtils
 * @Description oss文件上传和下载工具
 * @date 2021/6/17 10:12
 */
@Slf4j
public class OssUtils implements OssConst{

    /**
     * 通过sts上传输入流至oss
     *
     * @param bucketName   存储桶名称
     * @param dir          文件目录
     * @param fileFullName 完整文件名称，含后缀
     * @param inputStream  输入流
     * @param type         文件类型
     * @return
     */
    public static String uploadInsToOss(@Nonnull String bucketName,
                                 @Nonnull String dir,
                                 @Nonnull String fileFullName,
                                 @Nonnull InputStream inputStream,
                                 MediaType type) {
        try {
            // 文件名称
            String objectName = dir.concat(fileFullName);
            // 返回地址
            String filePath = uploadCommon(bucketName, objectName, inputStream, type, null, OssConst.getEndpoint());
            log.info("文件上传至oss路径：" + filePath);
            return filePath;
        } catch (Exception e) {
            log.error("上传失败", e);
            return null;
        }
    }

    /**
     * 使用oss账户密钥通用上传
     *
     * @param bucketName   存储桶名称
     * @param objectName   文件对象名称
     * @param inputStream  输入流
     * @param type         文件类型
     * @param callBackInfo 回调信息，不需要回调填null
     * @param endpoint 节点
     * @return
     * @throws Exception
     */
    private static String uploadCommon(@Nonnull String bucketName, @Nonnull String objectName,
                                @Nonnull InputStream inputStream, @Nonnull MediaType type,
                                CallBackInfo callBackInfo, String endpoint) throws Exception {

        // 创建OSSClient实例
        OSS ossClient = new OSSClient(endpoint, OssConst.getAccessKeyId(), OssConst.getAccessKeySecret());

        PutObjectRequest putObjectRequest;
        if (null != type) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(type.toString());
            putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream, metadata);
        } else {
            putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
        }
        if (null != callBackInfo) {
            setCallback(putObjectRequest, callBackInfo);
        }
        PutObjectResult result = ossClient.putObject(putObjectRequest);

        // 关闭OSSClient
        ossClient.shutdown();
        // 返回地址
        String filePath = getFilePath(bucketName, OssConst.getPublicEndpoint(), objectName);
        return filePath;
    }

    /**
     * 设置回调地区和参数数据
     *
     * @param putObjectRequest
     * @param callBackInfo
     */
    private static void setCallback(PutObjectRequest putObjectRequest, CallBackInfo callBackInfo) {
        // 上传回调参数。
        Callback callback = new Callback();
        String url = callBackInfo.getCallBackUrl();
        callback.setCallbackUrl(StringUtils.isBlank(url) ? OssConst.getCallbackUrl() : url);
        //（可选）设置回调请求消息头中Host的值，即您的服务器配置Host的值。
        // callback.setCallbackHost("yourCallbackHost");
        // 设置发起回调时请求body的值
        callback.setCallbackBody(callBackInfo.getBody().toJSONString());
        // 设置发起回调请求的Content-Type为json
        callback.setCalbackBodyType(Callback.CalbackBodyType.JSON);
        // 设置发起回调请求的自定义参数，由Key和Value组成，Key必须以x:开始。
        // callback.addCallbackVar("x:var1", "value1");
        putObjectRequest.setCallback(callback);
    }



    /**
     * 装配OSS文件地址
     * @param bucketName
     * @param endPoint
     * @param objectName
     * @return
     */
    private static String getFilePath(String bucketName,String endPoint,String objectName){
        return "https://".concat(bucketName)
                .concat(".")
                .concat(endPoint)
                .concat("/")
                .concat(objectName);
    }


    /**
     * 回调信息
     */
    public static class CallBackInfo {

        /** 业务id **/
        private String businessId;
        /** 自定义回调地址>默认回调地址 **/
        private String callBackUrl;
        /** 自定义回调json参数 **/
        private JSONObject body;

        public String getBusinessId() {
            return businessId;
        }

        public CallBackInfo setBusinessId(String businessId) {
            this.businessId = businessId;
            return this;
        }

        public JSONObject getBody() {
            return body;
        }

        public CallBackInfo setBody(JSONObject body) {
            this.body = body;
            return this;
        }

        public String getCallBackUrl() {
            return callBackUrl;
        }

        public CallBackInfo setCallBackUrl(String callBackUrl) {
            this.callBackUrl = callBackUrl;
            return this;
        }
    }

}

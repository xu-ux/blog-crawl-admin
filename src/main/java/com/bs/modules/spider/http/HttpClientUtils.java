package com.bs.modules.spider.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.utils.TraceUtils;
import com.bs.common.exception.base.HttpClientException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.Header;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.checkerframework.checker.units.qual.K;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: xucl
 * @Date: 2020/11/18
 * @Description: <p>通用http调用工具</p>
 */
@Slf4j
public class HttpClientUtils {

    private static final RequestConfig requestConfig;

    private static final RequestConfig uploadConfig;

    static {
        requestConfig = RequestConfig.custom()
                // 客户端和服务器建立连接的timeout
                .setConnectTimeout(1000*60)
                // 指从连接池获取连接的timeout
                .setConnectionRequestTimeout(6000)
                // 客户端从服务器读取数据的timeout
                .setSocketTimeout(1000*60*3)
                .build();
        uploadConfig = RequestConfig.custom()
                // 客户端和服务器建立连接的timeout
                .setConnectTimeout(1000*60*20)
                // 指从连接池获取连接的timeout
                .setConnectionRequestTimeout(6000)
                // 客户端从服务器读取数据的timeout
                .setSocketTimeout(1000*60*20)
                .build();
    }


    public static HttpResponse doGet(String url) throws HttpClientException{
        HttpClient httpClient = HttpClients.createDefault();

        HttpGet get = new HttpGet(url);
        get.setConfig(requestConfig);

        try {
            HttpResponse response = httpClient.execute(get);
            return response;
        } catch (Exception e) {
            throw new HttpClientException("请求失败",e);
        }

    }

    /**
     * 发送get请求，接收json响应数据
     * @param url 访问地址，不拼接
     * @param param 表单query参数
     * @return
     */
    public static JSONObject doGet(String url, Map<String, String> param) throws HttpClientException {

        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();
            log.debug("-->>Http GET请求地址："+url);
            if(null != param){
                log.debug("-->>Http 请求参数："+param.toString());
            }

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setConfig(requestConfig);

            // 执行请求
            response = httpClient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
                log.debug("<<--Http 响应内容："+resultString);
            }else{
                log.error("<<--Http 响应状态码："+response.getStatusLine().getStatusCode());
                throw new HttpClientException("请求失败 状态码：{}",response.getStatusLine().getStatusCode());
            }

        } catch (IOException | URISyntaxException e) {
            log.error("Http 发送请求异常",e);
            throw new HttpClientException("发送请求失败 url:{}",url);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                log.error("Http 关闭流异常",e);
            }
        }
        return JSONObject.parseObject(resultString);
    }

    /**
     * 发送post请求，上传视频
     * @param url 请求地址，不拼接
     * @return
     */
    public static JSONObject doPostVideo(String url, byte[] bytes, String fileName) throws HttpClientException{
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            log.debug("-->>Http POST请求地址："+url);

            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(uploadConfig);
            // 创建参数列表

            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
//            multipartEntityBuilder.addBinaryBody("video",bytes);
            multipartEntityBuilder.addBinaryBody("video", bytes, ContentType.MULTIPART_FORM_DATA,fileName );

            httpPost.setEntity(multipartEntityBuilder.build());
            // 执行http请求
            response = httpClient.execute(httpPost);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
                log.debug("<<--Http 响应内容："+resultString);
            }else{
                log.error("<<--Http 响应状态码："+response.getStatusLine().getStatusCode());
                throw new HttpClientException("请求失败 状态码：{}",response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            log.error("Http 发送请求异常",e);
            throw new HttpClientException("发送请求失败 url:{}",url);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                log.error("Http 关闭流异常",e);
            }
        }
        return JSONObject.parseObject(resultString);
    }

    /**
     * 发送post请求，form-data数据传输
     * POST multipart/form-data
     * @param url 请求地址
     * @return 返回json数据
     */
    public static JSONObject doPostFormData(String url, Map<String, String> formData) throws HttpClientException{
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            log.debug("-->>Http POST请求地址："+url);

            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            // 创建参数列表
//            httpPost.setHeader("Content-Type", "multipart/form-data;charset=utf-8"); // 报错
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            if (formData != null) {
                log.debug("-->>Http POST form-data内容："+ JSON.toJSONString(formData));
                for (String key : formData.keySet()) {
                    multipartEntityBuilder.addTextBody(key,formData.get(key), ContentType.MULTIPART_FORM_DATA);
                }
            }

            httpPost.setEntity(multipartEntityBuilder.build());
            // 执行http请求
            response = httpClient.execute(httpPost);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
                log.debug("<<--Http 响应内容："+resultString);
            }else{
                log.error("<<--Http 响应状态码："+response.getStatusLine().getStatusCode());
                throw new HttpClientException("请求失败 状态码：{}",response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            log.error("Http 发送请求异常",e);
            throw new HttpClientException("发送请求失败 url:{}",url);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                log.error("Http 关闭流异常",e);
            }
        }
        return JSONObject.parseObject(resultString);
    }

    /**
     * 发送post请求，接收json响应数据
     * @param url 请求地址，不拼接
     * @param param 表单query参数
     * @return
     */
    public static JSONObject doPost(String url, Map<String, String> param) throws HttpClientException{
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            log.debug("-->>Http POST请求地址："+url);
            if (null != param){
                log.debug("-->>Http 请求参数："+param.toString());
            }

            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
//            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, HTTP.UTF_8);
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
                log.debug("<<--Http 响应内容："+resultString);
            }else{
                log.error("<<--Http 响应状态码："+response.getStatusLine().getStatusCode());
                throw new HttpClientException("请求失败 状态码：{}",response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            log.error("Http 发送请求异常",e);
            throw new HttpClientException("发送请求失败 url:{}",url);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                log.error("Http 关闭流异常",e);
            }
        }
        return JSONObject.parseObject(resultString);
    }

    /**
     * 发送post请求，接收json响应数据
     *
     * @param url 请求地址
     * @param json json入参
     * @return
     */
    public static JSONObject doPostJson(String url, String json) throws HttpClientException {
        if(StringUtils.isBlank(json)){
            log.error("-->>Http POST发送json数据，json不能为空，url:"+url);
            return null;
        }
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            log.debug("-->>Http POST请求地址："+url);
            log.debug("-->>Http 请求参数："+json);
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
                log.debug("<<--Http 响应内容："+resultString);
            }else{
                log.error("<<--Http 响应状态码："+response.getStatusLine().getStatusCode());
                throw new HttpClientException("请求失败 状态码：{}",response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            log.error("Http 发送请求异常",e);
            throw new HttpClientException("发送请求失败 url:{}",url);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                log.error("Http 关闭流异常",e);
            }
        }
        return JSONObject.parseObject(resultString);
    }

    /**
     * 发送post请求，接收json响应数据
     *
     * @param url 请求地址
     * @param json json入参
     * @return
     */
    public static JSONObject doPostJson(String url, String json,Map<String, String> headers) throws HttpClientException {
        if(StringUtils.isBlank(json)){
            log.error("-->>Http POST发送json数据，json不能为空，url:"+url);
            return null;
        }
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            log.debug("-->>Http POST请求地址："+url);
            log.debug("-->>Http 请求参数："+json);
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            if (MapUtils.isNotEmpty(headers)){
                for (Map.Entry<String, String> h:headers.entrySet()) {
                    httpPost.addHeader(h.getKey(),h.getValue());
                }
            }
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
                log.debug("<<--Http 响应内容："+resultString);
            }else{
                log.error("<<--Http 响应状态码："+response.getStatusLine().getStatusCode());
                throw new HttpClientException("请求失败 状态码：{}",response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            log.error("Http 发送请求异常",e);
            throw new HttpClientException("发送请求失败 url:{}",url);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                log.error("Http 关闭流异常",e);
            }
        }
        return JSONObject.parseObject(resultString);
    }

    /**
     * 验证图片的有效性
     * @param imageURL
     * @return
     */
    public static boolean checkImagesTrue(String imageURL)  {
        try {
            URL url = new URL(imageURL);
            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
            urlcon.setRequestMethod("POST");
            urlcon.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            if (urlcon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 下载oss网络图片
     *
     * @param imgUrl 图片地址
     * @return
     */
    public static byte[] downloadImage(String imgUrl){

        CloseableHttpClient httpClient = HttpClients.createDefault();
        //3,设置请求方式
        HttpGet get = new HttpGet(imgUrl);
        get.setConfig(requestConfig);
        get.setHeader("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 Edg/92.0.902.67");
        //4,执行请求, 获取响应信息
        byte[] data = new byte[0];
        try {
            HttpResponse response = httpClient.execute(get);
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toByteArray(entity);
                return data;
            }
        } catch (Exception e) {
            log.error("OSS文件转成Byte[]失败",e);
        }
        return data;
    }
}

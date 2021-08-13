package com.bs.modules.spider.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName ILocalFileService
 * @Description
 * @date 2021/8/4
 */
public interface ILocalFileService {

    /**
     * 上传editorMD的图片
     * @param image
     * @return 返回路径
     */
    String uploadImage(MultipartFile image);


    /**
     * 上传editorMD的图片
     * @param content 图片字节数组
     * @return 返回路径
     */
    String uploadImage(byte[] content,String fileKey);

    /**
     * 上传标签文件
     * @param file
     * @return
     */
    String uploadTagFile(MultipartFile file);
}

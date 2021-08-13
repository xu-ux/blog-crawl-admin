package com.bs.modules.spider.service.impl;

import cn.hutool.core.util.IdUtil;
import com.bs.common.config.proprety.LocalProperty;
import com.bs.common.tools.oss.OssConst;
import com.bs.modules.spider.service.ILocalFileService;
import com.bs.modules.spider.util.ArticleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName LocalFileServiceImpl
 * @Description
 * @date 2021/8/4
 */
@Slf4j
@Service
public class LocalFileServiceImpl implements ILocalFileService {

    @Autowired
    private LocalProperty localProperty;


    /**
     * 上传editorMD的图片
     *
     * @param image
     * @return 返回路径
     */
    @Override
    public String uploadImage(MultipartFile image) {
        FileOutputStream out = null;
        try {

            String fileSuffix = ArticleUtils.matchImgSuffix(image.getOriginalFilename());
            String fileNewName = IdUtil.createSnowflake(2, 1).nextIdStr().concat(fileSuffix);
            String dir = OssConst.getCrawleUploadImgDir();
            String fileKey = dir + fileNewName;

            String fullPath = localProperty.getRealPath().concat(fileKey);
            log.info("全路径：{}",fullPath);
            File fileF = new File(fullPath);
            if (!fileF.getParentFile().exists()){
                fileF.getParentFile().mkdirs();
            }
            if (!fileF.exists()) {
                try {
                    fileF.createNewFile();
                } catch (IOException e) {
                }
            }

            out = new FileOutputStream(fileF, true);
            out.write(image.getBytes());
            String url =  "/".concat(localProperty.getMdFilePath()).concat(fileKey);
            log.info("地址：{}",url);
            return url;
        } catch (Exception e) {
            log.error("上传失败",e);
            return null;
        }

    }

    /**
     * 上传editorMD的图片
     *
     * @param content
     * @param fileKey
     * @return 返回路径
     */
    @Override
    public String uploadImage(byte[] content, String fileKey) {
        FileOutputStream out = null;
        try {
            String fullPath = localProperty.getRealPath().concat(fileKey);
            File fileF = new File(fullPath);
            if (!fileF.getParentFile().exists()){
                fileF.getParentFile().mkdirs();
            }
            if (!fileF.exists()) {
                try {
                    fileF.createNewFile();
                } catch (IOException e) {
                }
            }
            out = new FileOutputStream(fileF, true);
            out.write(content);
            String url =  "/".concat(localProperty.getMdFilePath()).concat(fileKey);
            log.info("地址：{}",url);
            return url;
        } catch (Exception e) {
            log.error("上传失败",e);
            return "";
        }

    }

    /**
     * 上传标签文件
     *
     * @param file
     * @return
     */
    @Override
    public String uploadTagFile(MultipartFile file) {
        FileOutputStream out = null;
        try {
            String originalFilename = file.getOriginalFilename();
            // 获取文件后缀
            String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileNewName = IdUtil.createSnowflake(2, 1).nextIdStr().concat(fileSuffix);
            String dir = OssConst.getDirByDate();
            String fileKey = dir + fileNewName;
            String fullPath = localProperty.getRealTagFilePath().concat(fileKey);
            log.info("全路径：{}", fullPath);

            File fileF = new File(fullPath);
            if (!fileF.getParentFile().exists()) {
                fileF.getParentFile().mkdirs();
            }
            if (!fileF.exists()) {
                try {
                    fileF.createNewFile();
                } catch (IOException e) {
                }
            }

            out = new FileOutputStream(fileF, true);
            out.write(file.getBytes());
            String url = "/".concat(localProperty.getTagFilePath()).concat(fileKey);
            log.info("地址：{}", url);
            return url;
        }catch (Exception e){
            log.error("上传失败",e);
            return "";
        }
    }
}

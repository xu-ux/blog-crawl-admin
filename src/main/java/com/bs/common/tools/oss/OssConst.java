package com.bs.common.tools.oss;



import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.bs.common.tools.common.ReadYmlUtils;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * @Author xucl
 * @Version 1.0
 * @ClassName OssConstant
 * @Description oss常量
 * @date 2021/6/22 14:49
 */
public interface OssConst {

    /** 外网深圳节点 */
    static String getEndpoint(){
        return ReadYmlUtils.getConfig().getAliyunOss().getEndpoint();
    }

    static String getPublicEndpoint(){
        return ReadYmlUtils.getConfig().getAliyunOss().getPublicEndpoint();
    }

    static String getCallbackUrl(){
        return ReadYmlUtils.getConfig().getAliyunOss().getCallbackUrl();
    }

    /** 内网深圳节点 */
    static String getInternalEndpoint(){
        return ReadYmlUtils.getConfig().getAliyunOss().getInternalEndpoint();
    }

    /** 公共读存储桶 */
    static String getPublicBucketName(){
        return ReadYmlUtils.getConfig().getAliyunOss().getPublicBucketName();
    }

    /** oss账户，只允许后端使用 */
    static String getAccessKeyId(){
       return ReadYmlUtils.getConfig().getAliyunOss().getAccessKeyId();
    }

    /** oss账户密钥，只允许后端使用 */
    static String getAccessKeySecret(){
        return ReadYmlUtils.getConfig().getAliyunOss().getAccessKeySecret();
    }

    static String getDirByDate(@Nonnull String prefix){
        return prefix.concat("/").concat(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN)).concat("/");
    }

    static String getDirByDate(){
        return DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN).concat("/");
    }

    /** 图片目录 */
    static String getCrawleUploadImgDir(){
        return ReadYmlUtils.getConfig().getFilePath().getImgPath().concat(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN)).concat("/");
    }



}

package com.quanxiaoha.xiaohashu.oss.biz.strategy.impl;

import com.quanxiaoha.xiaohashu.oss.biz.strategy.FileStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: 犬小哈
 * @date: 2024/6/27 19:47
 * @version: v1.0.0
 * @description: TODO
 **/
@Slf4j
public class AliyunOSSFileStrategy implements FileStrategy  {

    @Override
    public String uploadFile(MultipartFile file, String bucketName) {
        log.info("## 上传文件至阿里云 OSS ...");
        return null;
    }
}

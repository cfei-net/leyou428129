package com.leyou.upload.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OSSConfig {

    /**
     * 构建阿里云上传的客户端
     * @return  OSS客户端
     */
    @Bean
    public OSS ossClient(OSSProperties prop){
        return new OSSClientBuilder().build(
                prop.getEndpoint(),
                prop.getAccessKeyId(),
                prop.getAccessKeySecret());
    }

}

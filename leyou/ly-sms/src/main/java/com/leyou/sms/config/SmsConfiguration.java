package com.leyou.sms.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SmsProperties.class)// 引入配置类
public class SmsConfiguration {

    /**
     * 发送短信的客户端
     * @param prop  注入的配置类
     * @return      发送短信的客户端
     */
    @Bean
    public IAcsClient client(SmsProperties prop){
        //配置类和发送短信客户端： 交给spring管理
        DefaultProfile profile = DefaultProfile.getProfile(
                prop.getRegionID(),
                prop.getAccessKeyID(),
                prop.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);
        return client;
    }
}

package com.leyou.user.config;

import com.leyou.common.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@Data
@ConfigurationProperties(prefix = "ly.jwt")// 读取SpringBoot的配置文件中的内容，设置进来
public class JwtProperties {

    private String pubKeyPath;

    // 公钥
    private PublicKey publicKey;


    // app 相关配置：
    private AppTokenPojo app = new AppTokenPojo();
    @Data
    public class AppTokenPojo{
        private Long id; // 服务id
        private String password; // 微服务自己的密码
    }

    /**
     * 这个方法等到所有：@ConfigurationProperties(prefix = "ly.jwt")属性注入之后，才去加载
     */
    @PostConstruct
    public void initMethod() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
    }
}
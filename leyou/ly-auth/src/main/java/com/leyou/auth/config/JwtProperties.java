package com.leyou.auth.config;

import com.leyou.common.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@ConfigurationProperties(prefix = "ly.jwt")// 读取SpringBoot的配置文件中的内容，设置进来
public class JwtProperties {

    private String pubKeyPath;
    private String priKeyPath;

    // 公钥和私钥：在什么时候赋值
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private CookiePojo cookie = new CookiePojo();

    private AppTokenPojo app = new AppTokenPojo();


    @Data
    public class AppTokenPojo{
        private Integer expire;
    }

    @Data
    public class CookiePojo {
        private Integer expire;
        private Integer refreshTime;
        private String cookieName;
        private String cookieDomain;
    }

    /**
     * 这个方法等到所有：@ConfigurationProperties(prefix = "ly.jwt")属性注入之后，才去加载
     */
    @PostConstruct
    public void initMethod() throws Exception {
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
    }
}
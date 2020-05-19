package com.leyou.common.test;

import com.leyou.common.auth.pojo.Payload;
import com.leyou.common.auth.pojo.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.auth.utils.RsaUtils;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.Assert.*;

public class RsaUtilsTest {

    private String publicKeyPath = "D:\\leyou-code\\428129\\rsakey\\leyou_key.pub";
    private String privateKeyPath = "D:\\leyou-code\\428129\\rsakey\\leyou_key";

    /**
     * 生成公钥和密钥
     */
    @Test
    public void generateKey() throws Exception {

        RsaUtils.generateKey(
                publicKeyPath, // 公钥路径
                privateKeyPath, // 私钥路径
                "passw0rd@abc", // 生成两把钥匙的密码：不要泄露
                2048);// 私钥的长度
    }

    /**
     * 获取公钥和私钥
     * @throws Exception
     */
    @Test
    public void getKey() throws Exception {
        PublicKey publicKey = RsaUtils.getPublicKey(publicKeyPath);
        System.out.println(publicKey);

        System.out.println("===============================");

        PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyPath);
        System.out.println(privateKey);
    }

    /**
     * 生成jwt
     * @throws Exception
     */
    @Test
    public void testCreateJwt() throws Exception {
        // 获取私钥
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyPath);
        // 封装载荷
        UserInfo userInfo = new UserInfo(29L, "小飞飞", "admin");
        // 生成jwt
        String jwtToken = JwtUtils.generateTokenExpireInMinutes(userInfo, privateKey, 5);
        System.out.println(jwtToken);
    }

    @Test
    public void testPaeseJwt() throws Exception {
        // token
        String token = "eyJhbGciOiJSUzI1NiJ9." +
                "eyJ1c2VyIjoie1wiaWRcIjoyOSxcInVzZXJuYW1lXCI6XCLlsI_po57po55cIixcInJvbGVcIjpcImFkb" +
                "WluXCJ9IiwianRpIjoiTTJSa1kyRmxZVGN0TnpKallpMDBZelJrTFRnM1l6UXRZbUptT1RNek1EaG1OVEl5I" +
                "iwiZXhwIjoxNTg5NTI5NDUwfQ." +
                "KAKXz-nx5mg43xenkNSaF3apTqhmLmJqaoM92GKm3q8Zj0ly59EiVXA0gGoxUy_PVrvKeY9KfaT5pfj6Px4m3p9sZEnvuEhSs3Eak1XbCehRe6E9ulf7iJ-DuU4q21YfdYIVgMUyCJhJtRoXMlNnsi-zimIgB3LxHIpj2WMdl6TXw27k7CO9Iz8L4aiyPLtaa5kmvnMkFRU3-xhx-AA6pTJxIYcWJuKwI13tWQKj7yAioDfK31rWak1x8sPdmP40k5HxalWB5LP-VUVqFG1zw_6EFdGuz4G7tckCC7C3ex3n98crArogukgB5rmsXbdLogAS64nKsn9DJ6vLKWDIMA";
        // 获取公钥
        PublicKey publicKey = RsaUtils.getPublicKey(publicKeyPath);
        // 解析
        Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, publicKey, UserInfo.class);
        System.out.println("=================================");
        System.out.println(payload.getUserInfo().getId());
        System.out.println(payload.getUserInfo().getUsername());
        System.out.println(payload.getUserInfo().getRole());
        System.out.println("====================================");
        System.out.println(payload);

    }

    @Test
    public void testgetUserInfo() throws UnsupportedEncodingException {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJ1c2VyIjoie1wiaWRcIjoyOSxcInVzZXJuYW1lXCI6XCJ4aWFvZmVpZmVpXCIsXCJyb2xlXCI6XCJhZG1pblwifSIsImp0aSI6IllqQTVNR1ZsTkdRdE5EWmlaQzAwTXpJekxXRmlZMkl0WmpBd01XSmlPV1E1TlRSaiIsImV4cCI6MTU4OTc5MDc0N30.WCpD3oSA9XPYMk4DTYRJ37sBMQH0y-rvv1RvUOzLL1jangrrTlfk2WAQBserHo5hFYeBPjgSHKDHaYCI-fWluMBdXa6AkVF8i6_ujOW9Eeiy0LroZ5RAdumbho8sMZNkoUxk6azxxm1CfNhMossg4XnY82w3oYztejQWglnsWV_0H3GUv9TbjinZMJGYlRcsF-UI5RnhNnDtE9UobJBxmYofKudAa7svKwsrCQdEWFU1UCnraWCX293vSiLCFbdn6kaOHziLM06KQFhOuL5wDCxXgQt3fem88101RbsULCjLpwLRFRq_6_ATWHCC9hXTq6UCjOMgcU8xl4eWnmAv4Q";
        Payload<UserInfo> infoFromToken = JwtUtils.getInfoFromToken(token, UserInfo.class);
        System.out.println(infoFromToken.getUserInfo());
    }
}
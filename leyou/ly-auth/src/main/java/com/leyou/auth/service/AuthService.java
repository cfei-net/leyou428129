package com.leyou.auth.service;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.ApplicationInfo;
import com.leyou.auth.mapper.ApplicationInfoMapper;
import com.leyou.common.auth.pojo.AppInfo;
import com.leyou.common.auth.pojo.Payload;
import com.leyou.common.auth.pojo.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.exception.LyException;
import com.leyou.common.exception.enums.ExceptionEnum;
import com.leyou.common.utils.CookieUtils;
import com.leyou.user.client.UserClient;
import com.leyou.user.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties prop;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ApplicationInfoMapper applicationInfoMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 用户登录请求
     * @param username      用户名
     * @param password      登录密码
     * @param request       请求对象： CookieUtils使用
     * @param response      响应对象： CookieUtils使用
     *       没有返回值：但是工具类把jwt写入了浏览器的cookie中
     */
    public void login(String username, String password,
                      HttpServletRequest request, HttpServletResponse response) {
        // 1、根据用户名和密码去查询用户：如果返回非空，说明用户名和密码是正确
        UserDTO userDTO = userClient.queryUserByUsernameAndPwd(username, password);
        if(userDTO == null){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        // 2、生成JWT
        // 2.1 构建载荷对象
        UserInfo userInfo = new UserInfo(userDTO.getId(), userDTO.getUsername(), "admin");

        // 3、写入浏览器中
        createJwtTokenWriteToCookie(userInfo, request, response);
    }

    /**
     * 生成jwt的token并且写入浏览器的cookie中
     * @param userInfo
     * @param request
     * @param response
     */
    private void createJwtTokenWriteToCookie(UserInfo userInfo, HttpServletRequest request, HttpServletResponse response) {
        // 生成token:  有效期是30分钟
        String token = JwtUtils.generateTokenExpireInMinutes(userInfo, prop.getPrivateKey(), prop.getCookie().getExpire());
        // 把token写入浏览器中
        CookieUtils.newCookieBuilder()
                .request(request)
                .response(response)
                .name(prop.getCookie().getCookieName())
                .value(token)
                .domain(prop.getCookie().getCookieDomain())
                .httpOnly(true)
                .build();
    }

    /**
     * 验证用户
     * @param request       请求对象
     * @return              JWT的载荷对象
     */
    public UserInfo verify(HttpServletRequest request,HttpServletResponse response) {
        // 1、获取cookie
        String token = CookieUtils.getCookieValue(request, prop.getCookie().getCookieName());
        // 2、解析cookie中的jwt
        Payload<UserInfo> payload = null;
        try {
            payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
        // ============2.0 判断token是否在黑名单中==============
        if(redisTemplate.hasKey(payload.getId())){
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
        // ============2.1 判断token的有效期还剩下多长时间===================
        UserInfo userInfo = payload.getUserInfo();
        Date expiration = payload.getExpiration();
        // 2.2 过期时间与当前时间比对，如果还剩下10分钟，生成一个新的
        DateTime refreshTime = new DateTime(expiration).minusMinutes(prop.getCookie().getRefreshTime());
        // 2.3 判断刷新时间点是否在当前时间之前
        if(refreshTime.isBeforeNow()){
            // 重新生成token写入浏览器
            createJwtTokenWriteToCookie(userInfo, request, response);
            log.info("【授权中心】token加钟成功。。。。。");
        }

        // 3、把jwt的载荷返回
        return userInfo;
    }

    /**
     * 用户退出
     * @param request
     * @param response
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 设置token的黑名单
        String token = CookieUtils.getCookieValue(request, prop.getCookie().getCookieName());
        if(token!=null){
            try {
                // 解析token
                Payload<Object> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
                // tokenId
                String tokenId = payload.getId();
                // token的失效时间
                Date expiration = payload.getExpiration();
                // 剩余的时间： 毫秒数
                long remainTime = expiration.getTime() - System.currentTimeMillis();
                // 把token设置进入redis中: 如果剩余时间大于10秒存入redis
                if(remainTime > 10000) {
                    redisTemplate.opsForValue().set(tokenId, "1", remainTime, TimeUnit.MILLISECONDS);
                }

            } catch (Exception e) {
                e.printStackTrace();
                log.error("用户退出： token异常： {}",e.getMessage());
            }
        }
        // 删除cookie
        CookieUtils.deleteCookie(prop.getCookie().getCookieName(),prop.getCookie().getCookieDomain(), response);
    }

    /**
     * 校验微服务的id： 看下服务的id和密码是否正确
     * @param id            服务ID
     * @param pwd           服务名称
     * @return              true： id和密码都正确 ;   false: id和密码不正确
     */
    public Boolean isUsable(Long id, String pwd){
        // 1、先根据服务id查询服务信息
        ApplicationInfo applicationInfo = applicationInfoMapper.selectByPrimaryKey(id);
        if(applicationInfo == null){
            return false;
        }
        // 2、校验服务的密码是否一致
        if(!passwordEncoder.matches(pwd, applicationInfo.getSecret())){
            return false;
        }
        // 3、其他情况就是true： id和密码一致
        return true;
    }

    /**
     * 生成token返回给每个微服务
     * @param id        ： 服务id
     * @param password  ： 微服务自己的密码
     * @return          ： 返回JWT
     */
    public String authorize(Long id, String password){
        // 1、校验id和密码
        ApplicationInfo applicationInfo = applicationInfoMapper.selectByPrimaryKey(id);
        if(applicationInfo == null){
            log.warn("【授权微服务】微服务申请token失败：服务id或者服务密码不正确");
            throw new LyException(ExceptionEnum.INVALID_SERVER_ID_SECRET);
        }
        // 2、校验服务的密码是否一致
        if(!passwordEncoder.matches(password, applicationInfo.getSecret())){
            log.warn("【授权微服务】微服务申请token失败：服务id或者服务密码不正确");
            throw new LyException(ExceptionEnum.INVALID_SERVER_ID_SECRET);
        }
        // 3、校验了id和密码都通过之后，就要生成token
        // 3.0 根据服务id查询他的服务列表
        List<Long> targetIdList = applicationInfoMapper.queryTargetIdList(id);
        // 3.1 构建载荷信息
        AppInfo appInfo = new AppInfo(id, applicationInfo.getServiceName(), targetIdList);
        // 3.2 构建jwt: token的有效期是 25小时
        return JwtUtils.generateTokenExpireInMinutes(appInfo, prop.getPrivateKey(), prop.getApp().getExpire());
    }
}

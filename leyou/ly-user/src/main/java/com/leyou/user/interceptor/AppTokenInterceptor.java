package com.leyou.user.interceptor;

import com.leyou.common.auth.pojo.AppInfo;
import com.leyou.common.auth.pojo.Payload;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.constants.LyConstants;
import com.leyou.user.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class AppTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties prop;

    /**
     * 前置通知
     * @param request   请求对象
     * @param response  响应对象
     * @param handler   对象
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1、获取请求头中的App的token
        String token = request.getHeader(LyConstants.APP_TOKEN_HEADER);
        // 2、校验token
        // 2.1 判空
        if(StringUtils.isBlank(token)){
            log.warn("【用户微服务】检查App【微服务】的请求中是否携带了token，因为token为空，阻止了往下执行！");
            // 阻止往下执行
            return false;
        }
        // 2.2 判断token是否合法
        Payload<AppInfo> payload = null;
        try {
            payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), AppInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【用户微服务】检查App【微服务】的请求中是否携带了token：异常：{}", e.getMessage());
            return false;
        }

        // 3、获取token中的targetList列表
        List<Long> targetList = payload.getUserInfo().getTargetList();

        // 4、判断targetList中是否包含当前微服务的id
        if(CollectionUtils.isEmpty(targetList) || !targetList.contains(prop.getApp().getId())){
            log.warn("【用户微服务】检查App【微服务】的请求中是否携带了token: 目录列表为空或者没有访问当前微服务的权限，阻止往下执行");
            return false;
        }

        // 5、如果包含了当前id，放行
        return true;
    }

}

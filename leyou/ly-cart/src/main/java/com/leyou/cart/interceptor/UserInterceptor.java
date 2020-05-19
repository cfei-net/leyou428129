package com.leyou.cart.interceptor;

import com.leyou.common.auth.pojo.Payload;
import com.leyou.common.auth.pojo.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.auth.utils.UserHolder;
import com.leyou.common.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

@Slf4j
@Component
public class UserInterceptor implements HandlerInterceptor {
    // cookie的名称
    private final static String COOKIE_NAME = "LY_TOKEN";

    /**
     * 前置方法： 把用户信息从cookie取出来，放入ThreadLocal
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 从cookie中取出token
            String token = CookieUtils.getCookieValue(request, COOKIE_NAME);
            // 获取token中的信息
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, UserInfo.class);
            // 获取用户
            UserInfo userInfo = payload.getUserInfo();
            // 存入ThreadLocal
            UserHolder.setUser(userInfo);
            // 放行
            log.info("【购物车微服务】拦截器获取当前用户，并且存入ThreadLocal中， 成功。");
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.info("【购物车微服务】拦截器获取当前用户，并且存入ThreadLocal中， 失败，异常：{}",e.getMessage());
            return false;
        }
    }

    /**
     * 后置方法： 执行到这个方法：删除ThreadLocal中的信息
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}

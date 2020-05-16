package com.leyou.geteway.filter;

import com.leyou.common.auth.pojo.Payload;
import com.leyou.common.auth.pojo.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.exception.LyException;
import com.leyou.common.exception.enums.ExceptionEnum;
import com.leyou.common.utils.JsonUtils;
import com.leyou.geteway.config.FilterProperties;
import com.leyou.geteway.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtProperties prop;

    @Autowired
    private FilterProperties filterProp;

    /**
     * 拦截所有请求
     * @param exchange ： 交换机
     * @param chain    ： 过滤器链
     * @return         ： Mono
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("【网关过滤器】执行了.......");
        // 0、获取request
        ServerHttpRequest request = exchange.getRequest();

        // 0.1 判断是否是白名单
        String path = request.getURI().getPath();// 访问的路径
        if(isAllowPath(path)){
            // 如果进来了，说明是白名单
            log.info("【网关微服务】白名单放行====> {}", path);
            return chain.filter(exchange);
        }

        // 1、获取cookie中的token
        HttpCookie cookie = request.getCookies().getFirst(prop.getCookie().getCookieName());
        try {
            if(cookie != null){
                // key
                String name = cookie.getName();
                // value
                String token = cookie.getValue();
                // 解析token
                Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);
                // 获取用户信息
                UserInfo user = payload.getUserInfo();
                Long userId = user.getId();
                String username = user.getUsername();
                String role = user.getRole();
                // 做权限校验
                String methodType = request.getMethodValue();// 方法的类型
                // TODO 权限校验我们这里没有
                log.info("【网关微服务】当前:{} ，id：{} ，角色：{} ， 访问了：{}, 方法类型：{}", username, userId, role, path, methodType);
            }else{
                // cookie为空不让访问
                throw new LyException(ExceptionEnum.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 阻止往下执行
            //return exchange.getResponse().setComplete();

            // 下面的这段代码只是为了把一些信息显示到前端
            // 网关过滤器自己携带异常信息展示
            ServerHttpResponse response = exchange.getResponse();
            // 设置状态码
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //指定编码，否则在浏览器中会中文乱码
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            //==========================================================================
            Map<String, Object> msg = new LinkedHashMap<>();
            msg.put("status", HttpStatus.UNAUTHORIZED.value());
            msg.put("message", String.format("【网关微服务】告警信息： %s", e.getMessage()));
            msg.put("timestamp", DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
            //==========================================================================
            // 把map转成json
            String resultStr = JsonUtils.toString(msg);
            // 处理返回的信息
            byte[] bits = resultStr.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bits);
            return response.writeWith(Mono.just(buffer));
        }
        // 放行
        return chain.filter(exchange);
    }

    private boolean isAllowPath(String path) {
        List<String> allowPaths = filterProp.getAllowPaths();
        for (String allowPath : allowPaths) {
            if(path.startsWith(allowPath)){
                // 在以白名单开头：放行
                return true;
            }
        }
        // 拦截
        return false;
    }

    /**
     * 排序： 有很多的过滤器，这个返回的数字越小，越先执行
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}

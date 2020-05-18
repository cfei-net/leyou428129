package com.leyou.auth.feign;

import com.leyou.auth.task.AppTokenScheduled;
import com.leyou.common.constants.LyConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthFeignInterceptor implements RequestInterceptor {

    @Autowired
    private AppTokenScheduled appTokenScheduled;

    /**
     * 所有的feign请求都会经过这个方法
     * @param template
     */
    @Override
    public void apply(RequestTemplate template) {
        template.header(LyConstants.APP_TOKEN_HEADER, appTokenScheduled.getToken());
    }
}

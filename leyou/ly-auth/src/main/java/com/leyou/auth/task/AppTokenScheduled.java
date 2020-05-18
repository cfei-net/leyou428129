package com.leyou.auth.task;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.service.AuthService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 这个类作用：   1）定时去授权中心申请token
 *             2） 每隔24小时去申请一次token
 *             3）token的有效期是25小时，在token失效之前有1个小时的空档期去申请新的token
 */
@Slf4j
@Component   // 这个类已经交给spring管理，所以只要spring活着，这个类就肯定活着，我们就可以让这个类持有token
public class AppTokenScheduled {

    @Autowired
    private JwtProperties prop; // 获取配置文件中的内容

    @Autowired
    private AuthService authService;

    // 24小时
    private static final long TOKEN_REFRESH_TIME = 86400000L;

    // 如果申请token失败的话，10秒一次去申请token
    private static final long TOKEN_SLEEP_TIME = 10000L;

    // 定义一个变量接收token
    @Getter // 提供一个getter方法，用于获取这个申请到的token
    private String token;

    /**
     * 每隔24小时去申请一次token
     * 注解的属性上：不能有包装类【语法】
     */
    @Scheduled(fixedDelay = TOKEN_REFRESH_TIME)
    public void autoGetAppToken(){
        while (true){
            try {
                log.info("【授权中心】定时申请token开始");
                this.token = authService.authorize(prop.getApp().getId(), prop.getApp().getPassword());
                log.info("【授权中心】定时申请token结束，并成功申请到token，保存到类的全局变量中。");
                break;// 如果成功申请到token，跳出死循环
            }catch (Exception e){
                e.printStackTrace();
                log.error("【授权中心】定时申请token失败： {}",e.getMessage());
            }
            // 如果失败了休眠10秒再次去申请
            try {
                Thread.sleep(TOKEN_SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}

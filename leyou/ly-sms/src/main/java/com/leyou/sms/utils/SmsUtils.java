package com.leyou.sms.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.leyou.common.exception.LyException;
import com.leyou.common.exception.enums.ExceptionEnum;
import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.config.SmsConstants;
import com.leyou.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Component
public class SmsUtils {

    // 发送短信的客户端
    @Autowired
    private IAcsClient client;

    @Autowired
    private SmsProperties prop;

    /**
     * 我们直接把阿里云生成好的代码拿过来改改就可以
     *
     * 发送短信的方法
     * @param phone     用户的手机号
     * @param num       随机验证码【我们系统生成】
     */
    public void sendSms(String phone, String num){
        // 1、配置类和发送短信客户端： 交给spring管理
        // 在配置类中
        // 2、配置：  很多的配置，我们不能硬编码，我们可以把这些配置放入配置类或者配置文件中
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(prop.getDomain());
        request.setVersion(prop.getVersion());
        request.setAction(prop.getAction());
        request.putQueryParameter(SmsConstants.SMS_PARAM_REGION_ID, prop.getRegionID());
        request.putQueryParameter(SmsConstants.SMS_PARAM_KEY_PHONE, phone);
        request.putQueryParameter(SmsConstants.SMS_PARAM_KEY_SIGN_NAME, prop.getSignName());
        request.putQueryParameter(SmsConstants.SMS_PARAM_KEY_TEMPLATE_CODE, prop.getVerifyCodeTemplate());
        // 发送验证码占位符
        String code = "{\"" + prop.getCode() + "\": \"" + num + "\"}";
        request.putQueryParameter(SmsConstants.SMS_PARAM_KEY_TEMPLATE_PARAM, code);

        // 3、发送短信操作
        try {
            CommonResponse response = client.getCommonResponse(request);
            // 响应结果
            Map<String, String> respMap = JsonUtils.toMap(response.getData(), String.class, String.class);

            //判断阿里云SMS短信发送是否成功
            if(StringUtils.equals(respMap.get(SmsConstants.SMS_RESPONSE_KEY_CODE), SmsConstants.OK)){
                log.info("【阿里云验证码短信】工具调用成功完成！");
                return;
            }else {
                log.error("【阿里云验证码短信】工具调用失败！失败原因为：{}", respMap.get(SmsConstants.SMS_RESPONSE_KEY_MESSAGE));
                //抛出异常是为了手动ACK回执，如果失败，消息会再次消费
                throw new LyException(ExceptionEnum.SEND_MESSAGE_ERROR);
            }
        } catch (ServerException e) {
            log.error("【阿里云验证码短信】工具调用失败！阿里云的SMS出现异常了！异常信息为：{}", e.getMessage());
            //抛出异常是为了手动ACK回执，如果失败，消息会再次消费
            throw new LyException(ExceptionEnum.SEND_MESSAGE_ERROR);

        } catch (ClientException e) {
            log.error("【阿里云验证码短信】工具调用失败！我们的短信工具类代码异常了！异常信息为：{}", e.getMessage());
            //抛出异常是为了手动ACK回执，如果失败，消息会再次消费
            throw new LyException(ExceptionEnum.SEND_MESSAGE_ERROR);
        }
    }
}

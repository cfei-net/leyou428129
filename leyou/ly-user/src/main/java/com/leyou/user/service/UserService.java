package com.leyou.user.service;

import com.leyou.common.constants.LyConstants;
import com.leyou.common.exception.LyException;
import com.leyou.common.exception.enums.ExceptionEnum;
import com.leyou.common.utils.BeanHelper;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;
import com.leyou.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.leyou.common.constants.LyConstants.USER_VERFYCODE_PRE;
import static com.leyou.common.constants.MQConstants.Exchange.SMS_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.RoutingKey.VERIFY_CODE_KEY;

@Slf4j
@Service
@Transactional
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 实现用户数据的校验，主要包括对：手机号、用户名的唯一性校验。
     * @param data  要校验的数据
     * @param type  要校验的数据类型：1，用户名；2，手机
     * @return
     */
    public Boolean checkData(String data, Integer type) {
        User record = new User();

        // 做判断
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        // 统计查询
        int count = userMapper.selectCount(record);

        // return count >0 ? false: true;
        // 如果count恒等于0 就返回true； 说明数据库中没有这条数据，用户可以使用
        return count == 0;
    }

    /**
     * 发送短信验证码
     * @param phone 手机号码
     * @return
     */
    public void sendSms(String phone) {
        // 1、生成6位数字的随机验证码
        String code = RandomStringUtils.randomNumeric(6);
        // 2、把验证码存入redis中：有效期 5分钟
        // key : 如果我们只是把手机号作为key，以后我们在其他用到redis的场景也用手机号作为key的话，就会覆盖，
        // 所以我们给他加了一点前缀： user:yanzma    user:dingd    user：gouwuche
        String key = USER_VERFYCODE_PRE + phone;
        redisTemplate.opsForValue().set(key ,code, 5, TimeUnit.MINUTES);
        // 3、封装发送短信的map
        Map<String, String> user = new HashMap<>();
        user.put("phone", phone);
        user.put("code", code);
        // 4、mq发送
        amqpTemplate.convertAndSend(SMS_EXCHANGE_NAME, VERIFY_CODE_KEY, user);
    }

    /**
     * 用户注册接口
     * @param user  用户接参
     * @param code  短信验证码
     */
    public void register(User user, String code) {
        // 1、从redis中获取保存好的短信验证码： 有效期5分钟
        String key = USER_VERFYCODE_PRE + user.getPhone();
        String cacheCode = redisTemplate.opsForValue().get(key);
        // 2、判断页面的短信验证码和redis中的验证码是否一致
        if(!StringUtils.equals(cacheCode, code)){
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        // 3、对用户的明文的密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 4、保存用户的数据
        int count = userMapper.insertSelective(user);
        if(count != 1){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 根据用户名和密码查询用户
     * @param username  用户名
     * @param password  密码
     * @return          用户的DTO对象
     */
    public UserDTO queryUserByUsernameAndPwd(String username, String password) {
        // 1、现根据用户名去查询用户
        User record = new User();
        record.setUsername(username);
        User user = userMapper.selectOne(record);
        // 2、判空
        if(user == null){
            log.warn("【用户微服务】查询用户失败，用户为空，查询参数：{}",username);
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        // 3、判断密码是否一致
        if(!passwordEncoder.matches(password, user.getPassword())){
            log.warn("【用户微服务】用户查询成功，但是密码错误，查询参数：{}",username);
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        // 4、返回DTO对象
        return BeanHelper.copyProperties(user, UserDTO.class);
    }

}

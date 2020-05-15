package com.leyou.user.entity;

import com.leyou.common.utils.RegexPatterns;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@Table(name = "tb_user")
public class User {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    // 这些注解只是作为一个标记
    @NotNull(message = "用户名称不能为空")
    @Pattern(regexp = RegexPatterns.USERNAME_REGEX, message = "用户名称格式不正确")
    private String username;

    @NotNull(message = "密码不能为空")
    @Length(min = 4, max = 30, message = "密码长度有误，只能是4-30位字符串")
    private String password;

    @NotNull(message = "手机号码不能为空")
    @Pattern(regexp = RegexPatterns.PHONE_REGEX, message = "手机号码格式不正确")
    private String phone;
    private Date createTime;
    private Date updateTime;
}
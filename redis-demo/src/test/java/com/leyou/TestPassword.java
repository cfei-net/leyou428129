package com.leyou;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPassword {


    @Test
    public void test(){
        // 加密后的密文中：包含了动态的盐
        // $2a$10$ZnmyRpDFjBtjFKiNvFCvvupPIuMbMSVWvPZ1cgs7EVX1gnd9/4kzC
        // $2a$10$H1w6LcjzYaeg9oAv/QabPucXOyIQ01v1qWIdenA3Kegg3CVi7x2nq
        // $2a$10$uME10I5ik7c3a5qderNpKOqJLHBCOlmQ0sKSqajw2N0jP/AMECvrm

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pwdEncoder = encoder.encode("123456");
        System.out.println(pwdEncoder);

        // 如何判断密文和明文一致:  不能解密： 只能判断是否一致
        boolean matches = encoder.matches("123456", "$2a$10$uME10I5ik7c3a5qderNpKOqJLHBCOlmQ0sKSqajw2N0jP/AMECvrm");
        System.out.println(matches);
    }
}

package com.leyou.common.exception.enums;

import lombok.Getter;

@Getter
public enum FeifeiTest {

    XIAOZHUZHU("小猪猪", 38),
    XIAOYAYA("小鸭鸭", 29),
    XIAOFEIFEI("小飞飞", 16);

    private String name;
    private int age;

    FeifeiTest(String name, int age){
        this.name = name;
        this.age = age;
    }
}

class Test{
    public static void main(String[] args) {
        System.out.println(FeifeiTest.XIAOFEIFEI.getName());
        System.out.println(FeifeiTest.XIAOZHUZHU.getAge());
    }
}

package com.leyou.geteway.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 做定时任务的测试
 * 定时任务是一个线程池： 默认是一个线程： 一般线程数和定时任务的方法数一样
 */
@Slf4j
//@Component
public class AppTokenHolder {

    private  int count = 1;
    /**
     * 固定延迟: 等上一次完全执行完之后，再计算时间去执行定时任务
     */
    /*@Scheduled(fixedDelay = 2000L)
    public void task1() throws InterruptedException {
        if(count == 2){
            Thread.sleep(5000l);
        }
        log.info("【固定延迟】执行了。。。。。第{}次", count);
        count ++;
    }*/



    /**
     * 固定频率:  不管任务是否阻塞：到了时间，一定会执行
     */
    /*@Scheduled(fixedRate = 2000L)
    public void task2() throws InterruptedException {
        if(count == 2){
            Thread.sleep(5000l);
        }
        log.info("【固定频率】执行了。。。。。第{}次", count);
        count ++;
    }*/


    /**
     * cron表达式： 效果与固定延迟一样: 等上一次完全执行完之后，再计算时间去执行定时任务
     */
    @Scheduled(cron = "0/2 * * * * ? ")
    public void task3() throws InterruptedException {
        if(count == 2){
            Thread.sleep(5000l);
        }
        log.info("【cron表达式】执行了。。。。。第{}次", count);
        count ++;
    }
}

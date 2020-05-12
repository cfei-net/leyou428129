package com.leyou.page.controller;

import com.leyou.page.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Slf4j
@Controller
public class PageController {

    @Autowired
    private PageService pageService;

    /**
     * 跳转到商品详情页
     * @param spuId     spu的ID
     * @return          Thymeleaf模板名称
     */
    @GetMapping("/item/{spuId}.html")
    public String item(@PathVariable("spuId") Long spuId,Model model){
        log.info("进来了===>{}", spuId);
        // ========================================
        Map<String, Object> data = pageService.findGoodsData(spuId);
        model.addAllAttributes(data);
        // ========================================
        return "item";
    }





    @GetMapping("/hello")
    public String hello(Model model){
        model.addAttribute("msg", "贝吉塔大战卡卡罗特");
        model.addAttribute("msg2", "小鸭鸭真的好骚啊！");
        return "hello"; // 模板的名称，默认的后缀 .html  ; 一般放到templates目录下
    }
}

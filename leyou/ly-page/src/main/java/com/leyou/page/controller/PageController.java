package com.leyou.page.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class PageController {

    /**
     * 跳转到商品详情页
     * @param spuId     spu的ID
     * @return          Thymeleaf模板名称
     */
    @GetMapping("/item/{spuId}.html")
    public String item(@PathVariable("spuId") Long spuId,Model model){
        log.info("进来了===>{}", spuId);
        // ========================================
        model.addAttribute("categories", null); // 三级分类对象集合
        model.addAttribute("brand", null); // 品牌对象
        model.addAttribute("spuName", null);// spu的名称
        model.addAttribute("subTitle", null);// spu的副标题
        model.addAttribute("detail", null);// SpuDetail对象
        model.addAttribute("skus", null);// spu对应的sku的集合
        model.addAttribute("specs", null);// 规格参数列表： 先查出spec_group: 然后在组内添加一个 params 的属性
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

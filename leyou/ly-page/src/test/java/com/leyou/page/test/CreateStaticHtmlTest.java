package com.leyou.page.test;

import com.leyou.page.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CreateStaticHtmlTest {

    @Autowired
    private PageService pageService;

    @Test
    public void createItemHtml() {

        List<Long> id = Arrays.asList(141L, 160L, 151L, 171L, 46L, 187L);
        for (Long spuId : id) {
            pageService.createItemHtml(spuId);
        }
    }
}
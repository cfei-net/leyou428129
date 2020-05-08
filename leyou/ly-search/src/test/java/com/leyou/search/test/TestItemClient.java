package com.leyou.search.test;

import com.leyou.LySearchApplication;
import com.leyou.item.client.ItemClient;
import com.leyou.item.entity.SpecParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchApplication.class)
public class TestItemClient {

    @Autowired
    private ItemClient itemClient;

    @Test
    public void test(){
        List<SpecParam> specParams = itemClient.findSpecParamByGroupId(null, 76L, true);
        for (SpecParam specParam : specParams) {
            System.out.println(specParam);
        }
    }
}

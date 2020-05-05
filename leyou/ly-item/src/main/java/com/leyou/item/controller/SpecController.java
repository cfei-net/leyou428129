package com.leyou.item.controller;

import com.leyou.item.entity.SpecGroup;
import com.leyou.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SpecController {
    @Autowired
    private SpecService specService;

    /**
     * 根据分类的id查询规格组
     * @param cid   分类的id
     * @return      规格组的列表
     */
    @GetMapping("/spec/groups/of/category")
    public ResponseEntity<List<SpecGroup>> findSpecGroupByCategoryId(@RequestParam("id") Long cid){
        List<SpecGroup> specGroupList = specService.findSpecGroupByCategoryId(cid);
        return ResponseEntity.ok(specGroupList);
    }
}

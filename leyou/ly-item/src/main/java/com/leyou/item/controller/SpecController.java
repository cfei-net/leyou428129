package com.leyou.item.controller;

import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.entity.SpecGroup;
import com.leyou.item.entity.SpecParam;
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

    /**
     * 查询规格参数：多个条件
     *
     * @param gid       规格组的id
     * @param cid       分类的id
     * @param searching 是否是搜索过滤参数
     * @return
     */
    @GetMapping("/spec/params")
    public ResponseEntity<List<SpecParam>> findSpecParamByGroupId(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "searching", required = false) Boolean searching
    ) {
        List<SpecParam> specParamList = specService.findSpecParamByGroupId(gid, cid, searching);
        return ResponseEntity.ok(specParamList);
    }


    /**
     * 根据分类id查询规格组及其组内的参数
     * @param cid
     * @return
     */
    @GetMapping("/spec/of/category")
    public ResponseEntity<List<SpecGroupDTO>> findSpecGroupByCid(@RequestParam("id") Long cid){
        List<SpecGroupDTO> specGroupList = specService.findSpecGroupByCid(cid);
        return ResponseEntity.ok(specGroupList);
    }
}

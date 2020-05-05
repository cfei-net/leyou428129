package com.leyou.item.controller;

import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/*

//@CrossOrigin("*")
@CrossOrigin({"http://manage.leyou.com",
        //"http://localhost:9001",
        "http://localhost:9002",
        "http://www.leyou.com"})  // 解决跨域用的注解:  * 代表所有
*/

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父id查询分类信息
     * @param pid   父id
     * @return      分类的dto
     */
    @GetMapping("/category/of/parent")
    public ResponseEntity<List<CategoryDTO>> findCategoryByPid(@RequestParam("pid") Long pid){
        List<CategoryDTO> categoryDTOList = categoryService.findCategoryByPid(pid);
        return ResponseEntity.ok(categoryDTOList);
    }

    /**
     * 根据分类的id集合查询分类列表
     * @param ids
     * @return
     */
    @GetMapping("/category/list")
    public ResponseEntity<List<CategoryDTO>> findCategoryListByIds(@RequestParam("ids") List<Long> ids){
        List<CategoryDTO> categoryDTOList = categoryService.findCategoryListByIds(ids);
        return ResponseEntity.ok(categoryDTOList);
    }
}

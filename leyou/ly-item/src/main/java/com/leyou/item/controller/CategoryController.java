package com.leyou.item.controller;

import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}

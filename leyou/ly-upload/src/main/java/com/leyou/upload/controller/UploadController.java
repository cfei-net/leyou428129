package com.leyou.upload.controller;

import com.leyou.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 图片的上传
     * @param file  图片
     * @return      返回图片的访问地址
     */
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file){
        String imgUrl = uploadService.uploadImage(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(imgUrl);
    }

    /**
     * 获取阿里云图片上传的签名
     * @return  返回签名信息
     */
    @GetMapping("/signature")
    public ResponseEntity<Map<String, String>> signature(){
        Map<String, String> msg = uploadService.signature();
        return ResponseEntity.ok(msg);
    }
}

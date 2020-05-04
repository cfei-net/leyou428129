package com.leyou.upload.service;

import com.leyou.common.constants.LyConstants;
import com.leyou.common.exception.LyException;
import com.leyou.common.exception.enums.ExceptionEnum;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class UploadService {

    // 允许上传的图片类型: mime类型
    private List<String> ALLOW_UPLOAD_IMAGE_TYPE = Arrays.asList("image/jpeg", "image/png");

    public String uploadImage(MultipartFile file) {

        // 判断图片上传的类型
        if(!ALLOW_UPLOAD_IMAGE_TYPE.contains(file.getContentType())){
            throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }

        // 上面一步控制的是一些友好的用户，如果用户改了后缀同样可以上传，这个问题也要解决
        // jdk中有个工具类，可以判断一个文件是否是图片
        BufferedImage read = null;
        try {
            read = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 判断
        if(read == null){
            throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }


        // 图片的名称:  8位随机数字+图片原来的名称
        String imageName = RandomStringUtils.randomNumeric(8) + file.getOriginalFilename();

        // 图片上传的目录： 必须自己手动在nginx哪里创建
        File imgDir = new File(LyConstants.IMAGE_PATH);

        try {
            // 把图片转移到图片服务器中
            file.transferTo(new File(imgDir, imageName));
        } catch (IOException e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.FILE_UPLOAD_ERROR);
        }

        // 返回图片的访问路径:  http://localhost/brand-logo/1.jpg
        return LyConstants.IMAGE_URL + imageName;
    }
}

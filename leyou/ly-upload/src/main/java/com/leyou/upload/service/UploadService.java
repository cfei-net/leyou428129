package com.leyou.upload.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.leyou.common.constants.LyConstants;
import com.leyou.common.exception.LyException;
import com.leyou.common.exception.enums.ExceptionEnum;
import com.leyou.upload.config.OSSProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
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


    @Autowired
    private OSS ossClient;

    @Autowired
    private OSSProperties prop;

    /**
     * 上传图片到阿里云
     * @return
     */
    public Map<String, String> signature() {
        try {
            long expireTime = prop.getExpireTime(); // 过期时间
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, prop.getMaxFileSize());// 图片上传大小
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, prop.getDir()); //上传到那个目录

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessId", prop.getAccessKeyId()); // 改成和前端一样的就可以
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", prop.getDir());
            respMap.put("host", prop.getHost()); // 图片上传的路径
            respMap.put("expire", String.valueOf(expireEndTime)); // 这里要注意：毫秒
            return respMap;

        } catch (Exception e) {
            e.printStackTrace();
            log.error("【图片微服务】获取阿里云图片上传签名失败：{}", e.getMessage());
            throw new LyException(ExceptionEnum.GET_OSS_SIGNATURE_ERROR);
        }
    }
}

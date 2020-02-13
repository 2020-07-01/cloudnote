package com.controller;

import com.Util.Json;
import com.Util.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.MultipartUpload;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.entity.Image;
import com.interceptorService.TokenUtil;
import com.oss.OSSUtil;
import com.service.ImageService;
import com.service.serviceImpl.ImageServiceImpl;
import org.hibernate.validator.constraints.pl.REGON;
import org.omg.PortableInterceptor.LOCATION_FORWARD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.print.PrinterAbortException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: UploadController
 * @description:
 * @create: 2020-02-08 15:39
 **/
@RequestMapping(value = "/resource")
@RestController
public class UploadController {


    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    OSSUtil ossUtil;

    @Autowired
    ImageServiceImpl imageService;

    /**
     * 上传图片
     * 运行机制：先挨个全部传递到后端运行后再依次反馈运行结果到前端
     * 重命名解决方案：
     * 如果不存在重命名，则正常存储
     * 如果存在重命名，则先将图片存储到
     *
     * @param uploadFile
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/upload_image.json")
    public void uploadImage(@RequestParam(value = "file", required = false) MultipartFile uploadFile, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getUserIdByToken(token);
        Map serviceData = imageService.uploadImage(uploadFile,userId);
        if(serviceData.get("true")!= null){
            HashMap<String, String> data = new HashMap<>();
            data.put("message", "上传成功!");
            Result result = new Result(true, "上传成功!", data);
            Json.toJson(result, response);

        }else {
            HashMap<String, String> data = new HashMap<>();
            Long image_size = uploadFile.getSize();
            data.put("wholeName",serviceData.get("wholeName").toString());
            data.put("imageSize",image_size.toString());
            data.put("message", "上传失败!");
            Result result = new Result(false, "图片名称重复!", data);
            Json.toJson(result, response);

        }

        /*  */

        //添加到mysql
        //Map serviceData = imageService.uploadImage(uploadFile);

        //添加到oss
        //Map ossdata = ossUtil.putObject(uploadFile, String.valueOf(userId));


    }


    /**
     * 重命名文件
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/rename.json")
    public void rename(@RequestBody String jsonString , HttpServletRequest request, HttpServletResponse response){
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getUserIdByToken(token);

        JSONObject jsonObject = JSON.parseObject(jsonString);
        String message = jsonObject.getString("message");
        String imageSizeString = jsonObject.getString("imageSize");
        Long imageSize = Long.parseLong(imageSizeString);
        String wholeName =  message.substring(message.lastIndexOf("：") + 1);

        //拷贝文件
        ossUtil.copy_delete_Image(wholeName,userId.toString());
        //从临时文件中删除图片
        String key = userId+"/temp/"+wholeName;
        ossUtil.deleteImage(key);
        //获取图片的信息
        String imageName = wholeName.substring(0,wholeName.lastIndexOf("."));
        String imageType = wholeName.substring(wholeName.lastIndexOf(".") + 1);
        String imagePath = getImagePath(wholeName,userId.toString(),imageType);
        Image image = new Image();
        image.setImageName(imageName);
        image.setImageType(imageType);
        image.setUserId(userId);
        image.setImagePath(imagePath);
        image.setImageSize(imageSize);
        //存储图片信息到数据库中
        imageService.uploadImage(image);


    }

    /**
     * 不重命名文件
     */
    @RequestMapping(value = "/noRename.json")
    public void noReanme(@RequestBody String jsonString , HttpServletRequest request, HttpServletResponse response){
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getUserIdByToken(token);

        JSONObject jsonObject = JSON.parseObject(jsonString);
        String message = jsonObject.getString("message");

        String wholeName =  message.substring(message.lastIndexOf("：") + 1);
        String key = userId+"/temp/"+wholeName;
        ossUtil.deleteImage(key);
    }




    /**
     * 设置图片的路径
     * 55/images/png/2020-01-01/1.png
     *
     * @param sourceFileName
     * @param userId
     * @return
     */
    private String getImagePath(String sourceFileName, String userId, String type) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
        return userId + "/" + "image" + "/" + date + "/" + type + "/" + sourceFileName;
    }

    /**
     * 获取image数据
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/get_images.json")
    public Object getImageList(@RequestParam(value = "page") String pageno, @RequestParam(value = "limit") String pagesize, @RequestParam(value = "token") String token, HttpServletRequest request, HttpServletResponse response) {

        Integer userId = tokenUtil.getUserIdByToken(token);
        List<Image> images = imageService.selectImage(userId);
        Map<String, Object> hashMap = new HashMap();
        hashMap.put("code", "0");
        hashMap.put("msg", "ok");
        hashMap.put("count", images.size());
        hashMap.put("data", images);

        return hashMap;
    }


    /**
     * 获取图片的url
     *
     * @param jsonString
     * @param request
     * @param response
     */
    @RequestMapping(value = "/get_image_url.json")
    public void getImageUrl(@RequestBody String jsonString, HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("token");
        Integer userId = tokenUtil.getUserIdByToken(token);
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String imagePath = jsonObject.getString("imagePath");
        Integer imageId = Integer.parseInt(jsonObject.getString("imageId"));

        String imageUrl = imageService.selecImageUrl(imageId);
        if (imageUrl != null) {
            Result result = new Result(true, imageUrl);
            Json.toJson(result, response);
        } else {
            Map ossdata = ossUtil.getUrl(imagePath);
            String url = ossdata.get("url").toString();
            String exporeDate = ossdata.get("expireDate").toString();

            Image image = new Image();
            image.setUserId(userId);
            image.setImageId(imageId);
            image.setImageUrl(url);
            image.setExpireDate(exporeDate);
        }

    }

    /**
     * 上传单个文件
     *
     * @param uploadFile
     * @param request
     * @param response
     * @throws IOException
     *//*
    @RequestMapping(value = "/upload-file")
    public void uploadFile(@RequestParam(value = "file", required = false) MultipartFile uploadFile, HttpServletRequest request, HttpServletResponse response) throws IOException {

        String token = request.getHeader("token");
        Integer userId = tokenUtil.getUserIdByToken(token);
        ossUtil.uploadFile(uploadFile, String.valueOf(userId));

        //返回值
        Result result = new Result();
        result.setCode("1");
        result.setMessage("ok");
        HashMap<String, String> img = new HashMap<>();
        img.put("src", "123");
        result.setData(img);
        Json.toJson(result, response);

    }*/


}
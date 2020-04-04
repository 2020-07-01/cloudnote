package com.service;

import com.entity.CNFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileService {

    //上传文件
    Map uploadFile(MultipartFile file, Integer accountId);

    //存储图片
    Map insertFile(CNFile file);

    //获取所有file列表/key
    List<CNFile> getFileList(CNFile file);

    //更新file
    boolean updateFile(CNFile file);






}

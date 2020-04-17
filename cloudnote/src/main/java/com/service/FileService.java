package com.service;

import com.entity.file.CNFile;
import com.entity.Condition;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FileService {

    //上传文件
    Map uploadFile(MultipartFile file, Integer accountId) throws IOException;

    //存储图片
    Map insertFile(CNFile file);

    //获取所有file列表/key
    List<CNFile> findFileList(Condition condition);

    //更新file
    boolean updateFile(CNFile file);






}

package com.mapper;

import com.entity.file.CNFile;
import com.entity.Condition;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMapper {

    //查询文件
    CNFile selectFile(CNFile cnFile);

    //存储文件
    void insertFile(CNFile cnFile);

    //获取file列表
    List<CNFile> getFileList(Condition condition);

    //更新file
    int updateFile(CNFile file);

    //删除文件
    int deleteFile(String fileId);

}

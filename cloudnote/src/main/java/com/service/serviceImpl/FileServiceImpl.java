package com.service.serviceImpl;

import com.cache.CacheService;
import com.entity.CNFile;
import com.entity.Condition;
import com.entity.Constant;
import com.mapper.FileMapper;
import com.oss.OSSUtil;
import com.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 文件上传
 * @Author yq
 * @Date 2020/4/4 17:19
 */
@Service
public class FileServiceImpl implements FileService {


    @Autowired
    FileMapper fileMapper;

    @Autowired
    OSSUtil ossUtil;

    @Autowired
    CacheService cacheService;

    /**
     * 上传文件
     *
     * @param file
     * @param accountId
     * @return
     */
    @Override
    public Map uploadFile(MultipartFile file, Integer accountId) throws IOException {

        HashMap result = new HashMap();
        String wholeName = file.getOriginalFilename();// 获取源文件名
        String fileName = wholeName.substring(0, wholeName.lastIndexOf("."));// 文件名
        CNFile repeatFile = new CNFile();
        repeatFile.setAccountId(accountId);
        repeatFile.setFileName(fileName);
        //不存在重名
        if (fileMapper.selectFile(repeatFile) == null) {
            String fileType = wholeName.substring(wholeName.lastIndexOf(".") + 1);// 文件类型
            Long fileSize = file.getSize();//文件大小
            String filePath = getFilePath(wholeName, accountId.toString(), fileType);// 生成图片在oss上的目录

            CNFile cnFile = new CNFile();
            cnFile.setAccountId(accountId);
            cnFile.setFileName(fileName);
            cnFile.setFilePath(filePath);
            cnFile.setFileSize(fileSize.toString());
            cnFile.setWholeName(wholeName);
            cnFile.setFileType(fileType);
            //存储到mysql
            fileMapper.insertFile(cnFile);
            // 存储到oss
            ossUtil.putObject(file, accountId.toString());
            result.put("true", "上传成功!");
        } else { //存在重名文件
            // 设置终止条件
            boolean p = true;
            int i = 1;
            String newFileName = "";
            while (p) {
                newFileName = fileName + "(" + i + ")";
                CNFile cnFile = new CNFile();
                cnFile.setAccountId(accountId);
                cnFile.setAccountId(accountId);
                cnFile.setFileName(newFileName);
                if (fileMapper.selectFile(cnFile) != null) {
                    i++;
                } else {
                    p = false;
                }
            }
            String fileType = wholeName.substring(wholeName.lastIndexOf(".") + 1);// 文件类型
            String newWholeName = newFileName + "." + fileType;
            // 将文件存储到缓存中
            Map cacheMap = cacheService.getValue(accountId.toString());
            Map fileCache = new HashMap();
            fileCache.put(Constant.CACHE_BYTE, file.getBytes());//存储二进制文件
            fileCache.put(Constant.CACHE_NEW_NAME, newWholeName);//存储新的文件名
            fileCache.put(Constant.CACHE_SIZE, file.getSize());//存储文件大小
            cacheMap.put(newWholeName, fileCache);

            result.put("false", "文件名重复!");
            result.put("message", "<br><p style=\"text-align: center;font-size: 14px\">已经存在重名文件，是否重命名为:</p>" + "<br><p style=\"text-align: center;font-weight: bold;\">" + newFileName + "." + fileType + "</p>");
            result.put("fail", newWholeName);

        }
        return result;
    }

    /**
     * 存储图片
     *
     * @param file
     * @return
     */
    @Override
    public Map insertFile(CNFile file) {
        HashMap result = new HashMap();
        fileMapper.insertFile(file);
        result.put("true", "SUCCESS");
        return result;
    }

    /**
     * 获取文件列表
     *
     * @param condition
     * @return
     */
    @Override
    public List<CNFile> getFileList(Condition condition) {

        List<CNFile> list = fileMapper.getFileList(condition);

        return list;
    }

    //更新file
    @Override
    public boolean updateFile(CNFile file) {
        Integer row = fileMapper.updateFile(file);
        if (row == 1) {
            return true;
        } else
            return false;
    }

    /**
     * 获取文件路径 55/images/png/2020-01-01/1.png
     *
     * @param sourceFileName
     * @param accountId
     * @return
     */
    private String getFilePath(String sourceFileName, String accountId, String type) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
        return accountId + "/" + "file" + "/" + date + "/" + type + "/" + sourceFileName;
    }


}

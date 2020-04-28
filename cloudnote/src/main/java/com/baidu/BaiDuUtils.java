package com.baidu;

import com.baidu.aip.contentcensor.AipContentCensor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description 内容审核工具
 * @Author yq
 * @Date 2020/4/21 10:49
 */
@Slf4j
@Component
public class BaiDuUtils {


    @Autowired
    BaiDuConfig baiduConfig;

    /**
     * 文本内容审核
     *
     * @param textContent
     * @return
     */
    public JSONObject checkTextContent(String textContent) {
        AipContentCensor client = baiduConfig.baiDuClient();
        JSONObject response = client.textCensorUserDefined(textContent);
        return response;
    }


    /**
     * 图片内容审核
     *
     * @param byteData
     * @return
     */
    public JSONObject checkImage(byte[] byteData) {
        try {
            AipContentCensor client = baiduConfig.baiDuClient();
            JSONObject response = client.imageCensorUserDefined(byteData, null);
            return response;
        } catch (Exception e) {
            log.error(e.getMessage(), new Throwable(e));
        }
        return null;
    }
}

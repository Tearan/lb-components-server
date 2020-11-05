package org.lb.drools.component.service;

import org.lb.drools.component.model.UrlParam;

/**
 * @ClassName FileMangerListenerService
 * @Description 监听文件上传下载事件
 * @Author Terran
 * @Date 2020/11/4 22:20
 * @Version 1.0
 */
public interface FileMangerListenerService {

    /**
     * 接收文件上传url
     * @param param 上传参数
     */
    void onUploadUrl(UrlParam param);

    /**
     * 接收文件下载url
     * @param param 下载参数
     */
    void onDownloadUrl(UrlParam param);
}

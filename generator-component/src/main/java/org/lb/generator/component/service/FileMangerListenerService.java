/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.service;

import org.lb.generator.component.model.UrlParam;

/**
 * 监听文件上传下载事件
 * @author Terran
 * @since  1.0
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

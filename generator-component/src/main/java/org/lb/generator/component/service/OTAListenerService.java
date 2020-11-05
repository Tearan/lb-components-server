package org.lb.generator.component.service;

import org.lb.generator.component.model.OTAPackage;

/**
 * @ClassName OTAListenerService
 * @Description OTA监听器
 * @Author Terran
 * @Date 2020/11/4 23:05
 * @Version 1.0
 */
public interface OTAListenerService {

    /**
     * 接收查询版本通知
     */
    void onQueryVersion();

    /**
     * 接收新版本通知
     *
     * @param pkg 新版本包信息
     */
    void onNewPackage(OTAPackage pkg);
}

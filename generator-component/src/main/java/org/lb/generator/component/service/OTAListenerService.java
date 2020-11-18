/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.service;

import org.lb.generator.component.model.OTAPackage;

/**
 * OTA监听器
 * @author Terran
 * @since  1.0
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

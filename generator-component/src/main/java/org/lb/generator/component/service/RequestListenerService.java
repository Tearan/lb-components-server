/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.service;

/**
 * 请求监听器
 * @author Terran
 * @since  1.0
 */
public interface RequestListenerService {

    /**
     * 请求执行完成通知
     * @param result 请求执行结果
     */
    void onFinish(String result);
}

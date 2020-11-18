/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.service;

import org.lb.generator.component.model.RawMessage;

/**
 * 原始消息接收监听器
 * @author Terran
 * @since  1.0
 */
public interface RawMessageListenerService {

    /**
     * 收到消息通知
     * @param message 原始消息
     */
    void onMessageReceived(RawMessage message);
}

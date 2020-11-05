package org.lb.generator.component.service;

import org.lb.generator.component.model.RawMessage;

/**
 * @ClassName RawMessageListenerService
 * @Description 原始消息接收监听器
 * @Author Terran
 * @Date 2020/11/4 22:37
 * @Version 1.0
 */
public interface RawMessageListenerService {

    /**
     * 收到消息通知
     * @param message 原始消息
     */
    void onMessageReceived(RawMessage message);
}

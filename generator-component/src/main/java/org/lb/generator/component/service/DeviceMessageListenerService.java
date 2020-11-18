/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.service;

import org.lb.generator.component.model.DeviceMessage;

/**
 * 设备消息监听器，用于接收平台下发的设备消息
 * @author Terran
 * @since  1.0
 */
public interface DeviceMessageListenerService {

    /**
     * 处理平台下发的设备消息
     * @param deviceMessage 设备消息内容
     */
    void onDeviceMessage(DeviceMessage deviceMessage);
}

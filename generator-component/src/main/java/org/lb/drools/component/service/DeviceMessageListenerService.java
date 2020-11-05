package org.lb.drools.component.service;

import org.lb.drools.component.model.DeviceMessage;

/**
 * @ClassName DeviceMessageListenerService
 * @Description 设备消息监听器，用于接收平台下发的设备消息
 * @Author Terran
 * @Date 2020/11/4 22:28
 * @Version 1.0
 */
public interface DeviceMessageListenerService {

    /**
     * 处理平台下发的设备消息
     * @param deviceMessage 设备消息内容
     */
    void onDeviceMessage(DeviceMessage deviceMessage);
}

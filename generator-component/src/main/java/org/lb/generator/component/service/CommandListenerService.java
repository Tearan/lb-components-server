package org.lb.generator.component.service;

import java.util.Map;

/**
 * @ClassName CommandListenerService
 * @Description 命令监听器，用于接收平台下发的命令
 * @Author Terran
 * @Date 2020/11/4 22:26
 * @Version 1.0
 */
public interface CommandListenerService {
    /**
     * 命令处理
     * @param requestId   请求id
     * @param serviceId   服务id
     * @param commandName 命令名
     * @param paras       命令参数
     */
    void onCommand(String requestId, String serviceId, String commandName, Map<String, Object> paras);
}

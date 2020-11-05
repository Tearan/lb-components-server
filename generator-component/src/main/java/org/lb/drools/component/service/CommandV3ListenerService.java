package org.lb.drools.component.service;

import org.lb.drools.component.model.CommandV3;

/**
 * @ClassName CommandV3ListenerService
 * @Description 命令监听器，用于接收平台下发的V3命令
 * @Author Terran
 * @Date 2020/11/4 22:27
 * @Version 1.0
 */
public interface CommandV3ListenerService {

    /**
     * 处理命令
     * @param commandV3
     */
    public void onCommandV3(CommandV3 commandV3);
}

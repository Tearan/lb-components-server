/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.service;

import org.lb.generator.component.model.CommandV3;

/**
 * 命令监听器，用于接收平台下发的V3命令
 * @author Terran
 * @since  1.0
 */
public interface CommandV3ListenerService {

    /**
     * 处理命令
     * @param commandV3 处理命令
     */
    public void onCommandV3(CommandV3 commandV3);
}

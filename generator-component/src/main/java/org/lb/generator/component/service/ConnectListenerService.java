/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.service;

/**
 * 连接监听器
 * @author Terran
 * @since  1.0
 */
public interface ConnectListenerService {

    /**
     * 连接丢失通知
     * @param cause 连接丢失原因
     */
    void connectionLost(Throwable cause);

    /**
     * 连接成功通知
     * @param reconnect 是否为重连
     * @param serverURI 服务端地址
     */
    void connectComplete(boolean reconnect, String serverURI);
}

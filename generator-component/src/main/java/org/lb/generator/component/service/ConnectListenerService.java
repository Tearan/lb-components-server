package org.lb.generator.component.service;

/**
 * @ClassName ConnectListenerService
 * @Description 连接监听器
 * @Author Terran
 * @Date 2020/11/4 22:32
 * @Version 1.0
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

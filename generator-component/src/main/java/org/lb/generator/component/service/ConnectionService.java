package org.lb.generator.component.service;

import org.lb.generator.component.model.RawMessage;


/**
 * @ClassName ConnectionService
 * @Description IOT连接，代表设备和平台之间的一个连接
 * @Author Terran
 * @Date 2020/11/4 22:30
 * @Version 1.0
 */
public interface ConnectionService {

    /**
     * 建立连接
     *
     * @return 连接建立结果，0表示成功，其他表示失败
     */
    int connect();

    /**
     * 发布消息
     *
     * @param message  消息
     * @param listener 发布监听器
     */
    void publishMessage(RawMessage message, ActionListenerService listener);

    /**
     * 关闭连接
     */
    void close();

    /**
     * 是否连接中
     *
     * @return true表示在连接中，false表示断连
     */
    boolean isConnected();

    /**
     * 设置连接监听器
     *
     * @param connectListener 连接监听器
     */
    void setConnectListener(ConnectListenerService connectListener);

    /**
     * @param topic          订阅自定义topic。注意SDK会自动订阅平台定义的topic
     * @param actionListener 监听订阅是否成功
     * @param qos            qos
     */
    void subscribeTopic(String topic, ActionListenerService actionListener, int qos);

}

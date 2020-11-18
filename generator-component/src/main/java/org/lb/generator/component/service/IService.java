/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.service;

import org.lb.generator.component.client.IotResult;
import org.lb.generator.component.client.rep.CommandRsp;
import org.lb.generator.component.client.req.Command;
import org.lb.generator.component.client.req.DeviceEvent;

import java.util.Map;

/**
 * 服务接口类
 * @author Terran
 * @since  1.0
 */
public interface IService {

    /**
     * 读属性回调
     * @param fields 指定读取的字段名，不指定则读取全部可读字段
     * @return 属性值，json格式
     */
    Map<String, Object> onRead(String... fields);

    /**
     * 写属性回调
     * @param properties 属性期望值
     * @return 操作结果jsonObject
     */
    IotResult onWrite(Map<String, Object> properties);

    /**
     * 命令回调
     * @param command 命令
     * @return 执行结果
     */
    CommandRsp onCommand(Command command);

    /**
     * 事件回调
     * @param deviceEvent 事件
     */
    void onEvent(DeviceEvent deviceEvent);
}

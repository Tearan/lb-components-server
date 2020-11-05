package org.lb.drools.component.service;


import org.lb.drools.component.model.ServiceProperty;

import java.util.List;

/**
 * @ClassName PropertyListener
 * @Description 属性监听器，用于接收平台下发的属性读写操作
 * @Author Terran
 * @Date 2020/11/4 17:22
 * @Version 1.0
 */
public interface PropertyListener {

    /**
     * 处理写属性操作
     *
     * @param requestId 请求id
     * @param services  服务属性列表
     */
    void onPropertiesSet(String requestId, List<ServiceProperty> services);

    /**
     * 处理读属性操作
     *
     * @param requestId 请求id
     * @param serviceId 服务id，可选
     */
    void onPropertiesGet(String requestId, String serviceId);
}

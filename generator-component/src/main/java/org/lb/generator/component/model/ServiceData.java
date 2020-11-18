/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.model;

import lombok.Data;

import java.util.Map;

/**
 * 设备属性（V3）
 * @author Terran
 * @since  1.0
 */
@Data
public class ServiceData {
    /**
     * 服务id，和设备模型里一致
     */
    private String serviceId;

    /**
     * 属性变化的时间，格式：yyyyMMddTHHmmssZ，可选，不带以平台收到的时间为准
     */
    private String eventTime;

    /**
     * 属性值，具体字段由设备模型定义
     */
    private Map<String, Object> serviceData;
}

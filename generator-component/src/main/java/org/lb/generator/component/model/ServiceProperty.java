/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * 服务的属性
 * @author Terran
 * @since  1.0
 */
@Data
public class ServiceProperty {

    /**
     * 服务id，和设备模型里一致
     */
    @JsonProperty("service_id")
    String serviceId;

    /**
     * 属性值，具体字段由设备模型定义
     */
    Map<String, Object> properties;

    /**
     * 属性变化的时间，格式：yyyyMMddTHHmmssZ，可选，不带以平台收到的时间为准
     */
    @JsonProperty("event_time")
    String eventTime;
}

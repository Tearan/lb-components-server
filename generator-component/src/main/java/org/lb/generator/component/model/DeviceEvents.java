/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.lb.generator.component.client.req.DeviceEvent;

import java.util.List;

/**
 * 设备事件
 * @author Terran
 * @since 1.0
 */
@Data
public class DeviceEvents {

    /** 设备id */
    @JsonProperty("object_device_id")
    String deviceId;

    /** 服务事件列表 */
    @JsonProperty("services")
    List<DeviceEvent> services;
}

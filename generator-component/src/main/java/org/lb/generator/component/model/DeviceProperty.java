/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.lb.generator.component.ProductParser.ServiceProperty;

import java.util.List;

/**
 * 设备属性
 * @author Terran
 * @since  1.0
 */
@Data
public class DeviceProperty {

    @JsonProperty("device_id")
    String deviceId;

    List<ServiceProperty> services;
}

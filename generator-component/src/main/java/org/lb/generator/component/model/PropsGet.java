/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 读属性操作
 * @author Terran
 * @since  1.0
 */
@Data
public class PropsGet {

    @JsonProperty("object_device_id")
    String deviceId;

    @JsonProperty("service_id")
    String serviceId;
}

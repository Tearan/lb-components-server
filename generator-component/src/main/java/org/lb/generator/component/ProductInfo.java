/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component;

import lombok.Data;

import java.util.Map;

/**
 * 产品信息表
 * @author Terran
 * @since  1.0
 */
@Data
public class ProductInfo {

    DeviceCapability deviceCapability;

    Map<String, DeviceService> serviceCapabilityMap;
}

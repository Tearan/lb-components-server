/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.model;

import lombok.Data;

import java.util.List;

/**
 * 设备属性内容
 * @author Terran
 * @since 1.0
 */
@Data
public class DeviceProperties {

    /** 服务属性列表 */
    List<ServiceProperty> services;
}

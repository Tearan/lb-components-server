/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;

/**
 * 设备上报数据格式（V3接口）
 * @author Terran
 * @since 1.0
 */
public class DevicePropertiesV3 {

    /** 消息类型 */
    @JsonView
    private String msgType;

    /** 上报的属性列表 */
    @JsonProperty("data")
    private List<ServiceData> serviceDatas;
}

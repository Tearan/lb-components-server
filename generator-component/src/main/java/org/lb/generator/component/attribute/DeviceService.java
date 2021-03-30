/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.attribute;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 设备服务能力
 * @author Terran
 * @since  1.0
 */
@Data
public class DeviceService implements Serializable {

    private static final long serialVersionUID = -2045537649692709978L;

    /**
     * 服务Id
     * 如果设备中同类型的服务类型只有一个则serviceId与serviceType相同， 如果有多个则增加编号，如三键开关 Switch01、Switch02、Switch03。
     */
    private String serviceId;

    /**
     * 服务类型
     * 与servicetype-capability.json中serviceType字段保持一致。
     */
    private String serviceType;

    /**
     * 标识服务字段类型
     * Master（主服务）, Mandatory（必选服务）, Optional（可选服务）
     * 目前本字段为非功能性字段，仅起到描述作用
     */
    private String option;

    /** 描述 */
    private String description;

    /** 上次更新时间 */
    private String lastModifyTime;

    /** 指示设备可以执行的命令 */
    private List<ServiceCommand> commands;

    /**
     * 服务属性
     * 用于设备影子、属性类型判断等
     */
    private List<ServiceProperty> properties;

    private List<Object> events;
}

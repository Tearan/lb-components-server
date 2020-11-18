/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.drools.component.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Terran
 * @since 1.0
 */
@Data
@ConfigurationProperties(prefix = "spring.drools")
public class DroolsProperties {

    /** 规则文件和决策表目录，多个目录使用逗号分割 */
    private String path;

    /** 轮询周期 - 单位：秒 */
    private Long update;

    /** 模式，stream 或 cloud */
    private String mode;

    /** 是否开启监听器：on = 开；off = 关闭 */
    private String listener;
}

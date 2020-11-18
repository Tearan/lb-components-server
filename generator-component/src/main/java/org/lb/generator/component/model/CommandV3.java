/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.model;

import lombok.Data;

import java.util.Map;

/**
 * @author Terran
 * @since 1.0
 */
@Data
public class CommandV3 {

    private String msgType;

    private String serviceId;

    private int mid;

    private String cmd;

    private Map<String, Object> paras;
}

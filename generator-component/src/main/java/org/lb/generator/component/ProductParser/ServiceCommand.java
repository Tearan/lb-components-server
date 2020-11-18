/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.ProductParser;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 执行的命令
 * @author Terran
 * @since  1.0
 */
@Data
public class ServiceCommand implements Serializable {

    private static final long serialVersionUID = -8726398850035913800L;

    private String commandName;

    private List<ServiceCommandPara> paras;

    private List<ServiceCommandResponse> responses;
}

/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.ProductParser;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 服务命令响应
 * @author Terran
 * @since  1.0
 */
@Data
public class ServiceCommandResponse implements Serializable {

    private static final long serialVersionUID = 4535630761027464385L;

    private String responseName;

    private List<ServiceCommandPara> paras;
}

/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.sdk;

import lombok.Data;
import org.lb.generator.component.annotation.Property;
import org.lb.generator.component.service.impl.AbstractServiceImpl;

/**
 * 此服务实现sdk信息
 * @author Terran
 * @since  1.0
 */
@Data
public class SdkInfo extends AbstractServiceImpl {

    @Property(writeable = false)
    private String type = "Java";

    @Property(writeable = false)
    private String version = "0.8.0";
}

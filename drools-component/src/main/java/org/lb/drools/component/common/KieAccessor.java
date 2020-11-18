/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.drools.component.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Terran
 * @since 1.0
 */
@Data
@Slf4j
public class KieAccessor implements InitializingBean {

    public static final String PATH_SPLIT = ",";

    private String path;

    private String mode;

    private Long update;

    private String listener;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (path == null || path.length() == 0){
            log.error("Please set base path(spring.drools.path = ***).");
        }
    }
}

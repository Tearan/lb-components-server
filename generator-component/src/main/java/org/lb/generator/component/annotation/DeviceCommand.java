/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.annotation;

import java.lang.annotation.*;

/**
 * 设备命令
 * @author Terran
 * @since 1.0
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DeviceCommand {

    /**
     * @return 命令名，不提供默认为方法名
     */
    String name() default "";
}

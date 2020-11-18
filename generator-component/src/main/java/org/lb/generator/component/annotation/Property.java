/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.annotation;

import java.lang.annotation.*;

/**
 * 属性
 * @author Terran
 * @since 1.0
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Property {
    /**
     * 属性是否可写。注：所有属性默认都可读
     * @return true表示可写
     */
    boolean writeable() default true;

    /**
     * @return 属性名，不提供默认为字段名
     */
    String name() default "";
}

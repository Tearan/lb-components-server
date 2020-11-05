package org.lb.drools.component.annotation;

import java.lang.annotation.*;

/**
 * @ClassName Property
 * @Description 属性
 * @Author Terran
 * @Date 2020/11/4 22:11
 * @Version 1.0
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

package org.lb.generator.component.annotation;

import java.lang.annotation.*;

/**
 * @ClassName DeviceCommand
 * @Description 设备命令
 * @Author Terran
 * @Date 2020/11/4 22:13
 * @Version 1.0
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

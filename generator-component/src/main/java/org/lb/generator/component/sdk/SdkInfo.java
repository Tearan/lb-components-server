package org.lb.generator.component.sdk;

import lombok.Data;
import org.lb.generator.component.annotation.Property;
import org.lb.generator.component.service.impl.AbstractServiceImpl;

/**
 * @ClassName SdkInfo
 * @Description 此服务实现sdk信息
 * @Author Terran
 * @Date 2020/11/4 23:07
 * @Version 1.0
 */
@Data
public class SdkInfo extends AbstractServiceImpl {

    @Property(writeable = false)
    private String type = "Java";

    @Property(writeable = false)
    private String version = "0.8.0";
}

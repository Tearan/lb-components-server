package org.lb.drools.component.model;

import lombok.Data;

import java.util.List;

/**
 * @ClassName DeviceProperties
 * @Description 设备属性内容
 * @Author Terran
 * @Date 2020/11/4 22:53
 * @Version 1.0
 */
@Data
public class DeviceProperties {

    /**
     * 服务属性列表
     */
    List<ServiceProperty> services;
}

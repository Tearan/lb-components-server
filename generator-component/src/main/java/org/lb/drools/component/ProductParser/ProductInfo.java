package org.lb.drools.component.ProductParser;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName ProductInfo
 * @Description 产品信息表
 * @Author Terran
 * @Date 2020/11/3 23:21
 * @Version 1.0
 */
@Data
public class ProductInfo {

    DeviceCapability deviceCapability;

    Map<String, DeviceService> serviceCapabilityMap;
}

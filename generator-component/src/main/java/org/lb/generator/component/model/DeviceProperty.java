package org.lb.generator.component.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.lb.generator.component.ProductParser.ServiceProperty;

import java.util.List;

/**
 * @ClassName DeviceProperty
 * @Description 设备属性
 * @Author Terran
 * @Date 2020/11/4 22:49
 * @Version 1.0
 */
@Data
public class DeviceProperty {

    @JsonProperty("device_id")
    String deviceId;

    List<ServiceProperty> services;
}

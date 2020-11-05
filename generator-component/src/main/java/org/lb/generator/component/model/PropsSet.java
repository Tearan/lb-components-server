package org.lb.generator.component.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName PropsSet
 * @Description 写属性操作
 * @Author Terran
 * @Date 2020/11/4 22:52
 * @Version 1.0
 */
@Data
public class PropsSet {

    @JsonProperty("object_device_id")
    String deviceId;

    List<ServiceProperty> services;
}

package org.lb.generator.component.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @ClassName PropsGet
 * @Description 读属性操作
 * @Author Terran
 * @Date 2020/11/4 22:52
 * @Version 1.0
 */
@Data
public class PropsGet {

    @JsonProperty("object_device_id")
    String deviceId;

    @JsonProperty("service_id")
    String serviceId;
}

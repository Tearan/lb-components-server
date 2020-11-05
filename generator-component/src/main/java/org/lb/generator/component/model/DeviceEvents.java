package org.lb.generator.component.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.lb.generator.component.client.req.DeviceEvent;

import java.util.List;

/**
 * @ClassName DeviceEvents
 * @Description 设备事件
 * @Author Terran
 * @Date 2020/11/4 22:54
 * @Version 1.0
 */
@Data
public class DeviceEvents {

    /** 设备id */
    @JsonProperty("object_device_id")
    String deviceId;

    /** 服务事件列表 */
    @JsonProperty("services")
    List<DeviceEvent> services;
}

package org.lb.drools.component.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName ServiceProperty
 * @Description 服务的属性
 * @Author Terran
 * @Date 2020/11/4 22:56
 * @Version 1.0
 */
@Data
public class ServiceProperty {

    /**
     * 服务id，和设备模型里一致
     */
    @JsonProperty("service_id")
    String serviceId;

    /**
     * 属性值，具体字段由设备模型定义
     */
    Map<String, Object> properties;

    /**
     * 属性变化的时间，格式：yyyyMMddTHHmmssZ，可选，不带以平台收到的时间为准
     */
    @JsonProperty("event_time")
    String eventTime;
}

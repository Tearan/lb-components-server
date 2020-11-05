package org.lb.generator.component.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;

/**
 * @ClassName DevicePropertiesV3
 * @Description 设备上报数据格式（V3接口）
 * @Author Terran
 * @Date 2020/11/4 22:46
 * @Version 1.0
 */
public class DevicePropertiesV3 {

    /**
     * 消息类型
     */
    @JsonView
    private String msgType;

    /**
     * 上报的属性列表
     */
    @JsonProperty("data")
    private List<ServiceData> serviceDatas;
}

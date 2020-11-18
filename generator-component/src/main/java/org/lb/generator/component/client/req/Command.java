/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.client.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.lb.generator.component.utils.JsonUtil;

import java.util.Map;

/**
 * 设备命令
 * @author Terran
 * @since 1.0
 */
public class Command {

    @JsonProperty("service_id")
    private String serviceId;

    @JsonProperty("command_name")
    private String commandName;

    @JsonProperty("object_device_id")
    private String deviceId;

    private Map<String, Object> paras;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Map<String, Object> getParas() {
        return paras;
    }

    public void setParas(Map<String, Object> paras) {
        this.paras = paras;
    }

    @Override
    public String toString() {
        return JsonUtil.convertObject2String(this);
    }
}

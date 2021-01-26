/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * 设备能力
 * @author Terran
 * @since  1.0
 */
@Data
public class DeviceCapability implements Serializable {

    private static final long serialVersionUID = -6552611626281802652L;

    public static final String FIELD_ID = "id";

    public static final String FIELD_APP_ID = "appId";

    public static final String FIELD_MANUFACTURER_ID = "manufacturerId";

    public static final String FIELD_MODEL = "model";

    public static final String FIELD_PROTOCOL_TYPE = "protocolType";

    public static final String FIELD_DEVICE_TYPE = "deviceType";

    public static final String FIELD_MANUFACTURER_NAME = "manufacturerName";

    public static final String FIELD_DESCRIPTION = "description";

    public static final String FIELD_ICON = "icon";

    public static final String FIELD_OM_CAPABILITY = "omCapability";

    public static final String FIELD_SERVICE_CAPABILITIES = "serviceTypeCapabilities";

    public static final String FIELD_LAST_MODIFY_TIME = "lastModifyTime";

    public static final String FIELD_PRODUCTID = "productId";

    public static final String FIELD_VERSION = "version";

    public static final String FIELD_NEWFLAG = "newFlag";

    public static final String FIELD_NODETYPE = "nodeType";

    private String id;

    /** 应用id */
    private String appId;

    /** 设备类型 */
    private String deviceType;

    /** 厂商Id */
    private String manufacturerId;

    /** 厂商名 */
    private String manufacturerName;

    /** 设备的型号 */
    private String model;

    /** 协议类型 */
    private String protocolType;

    /** 描述 */
    private String description;

    /**
     * 定制能力
     * 目前dm使用，定义设备的软件升级、固件升级和配置更新的能力
     */
    private transient ObjectNode omCapability;

    /** 图标 */
    private String icon;

    /** 最后更新时间 */
    private String lastModifyTime;

    /** 服务能力 */
    private List<DeviceService> serviceTypeCapabilities;

    /** 版本 */
    private String version;

    private boolean newFlag;

    /** 产品Id */
    private String productId;

    /** 节点类型，该字段暂时不用，保留 */
    private String nodeType;


    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException{
        objectOutputStream.defaultWriteObject();
        ObjectMapper objectMapper = new ObjectMapper();
        objectOutputStream.writeUTF(objectMapper.writeValueAsString(omCapability));
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException,ClassNotFoundException{
        objectInputStream.defaultReadObject();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(objectInputStream.readUTF());
        omCapability = (ObjectNode) jsonNode;
    }
}

package org.lb.generator.component;

import lombok.extern.slf4j.Slf4j;
import org.lb.generator.component.client.ClientConf;
import org.lb.generator.component.model.*;
import org.lb.generator.component.service.impl.AbstractServiceImpl;
import org.lb.generator.component.service.impl.DeviceClientServiceImpl;
import java.security.KeyStore;
import java.util.List;

/**
 *
 * IOT设备类，SDK的入口类，提供两种使用方式：
 *  * 1、面向物模型编程：根据物模型实现设备服务，SDK自动完成设备和平台之间的通讯。
 *  这种方式简单易用，适合大多数场景
 * @ClassName IotDevice
 * @Description IOT设备类
 * @Author Terran
 * @Date 2020/11/5 16:09
 * @Version 1.0
 */
@Slf4j
public class IotDevice extends AbstractDevice {

    /**
     * 构造函数，使用密码创建设备
     * @param serverUri    平台访问地址，比如ssl://iot-mqtts.cn-north-4.myhuaweicloud.com:8883
     * @param deviceId     设备id
     * @param deviceSecret 设备密码
     */
    public IotDevice(String serverUri, String deviceId, String deviceSecret) {
        super(serverUri, deviceId, deviceSecret);

    }

    /**
     * 构造函数，使用证书创建设备
     * @param serverUri   平台访问地址，比如ssl://iot-mqtts.cn-north-4.myhuaweicloud.com:8883
     * @param deviceId    设备id
     * @param keyStore    证书容器
     * @param keyPassword 证书密码
     */
    public IotDevice(String serverUri, String deviceId, KeyStore keyStore, String keyPassword) {
        super(serverUri, deviceId, keyStore, keyPassword);
    }

    /**
     * 构造函数，直接使用客户端配置创建设备，一般不推荐这种做法
     * @param clientConf 客户端配置
     */
    public IotDevice(ClientConf clientConf) {
        super(clientConf);
    }

    /**
     * 初始化，创建到平台的连接
     * @return 此接口为阻塞调用，如果连接成功，返回0；否则返回-1
     */
    public int init() {
        return super.init();
    }

    /**
     * 添加服务。用户基于AbstractService定义自己的设备服务，并添加到设备
     * @param serviceId     服务id，要和设备模型定义一致
     * @param deviceService 服务实例
     */
    public void addService(String serviceId, AbstractServiceImpl deviceService) {
        super.addService(serviceId, deviceService);
    }

    /**
     * 查询服务
     * @param serviceId 服务id
     * @return AbstractService 服务实例
     */
    public AbstractServiceImpl getService(String serviceId) {
        return super.getService(serviceId);
    }

    /**
     * 触发属性变化，SDK会上报变化的属性
     * @param serviceId  服务id
     * @param properties 属性列表
     */
    public void firePropertiesChanged(String serviceId, String... properties) {
        super.firePropertiesChanged(serviceId, properties);
    }

    /**
     * 触发多个服务的属性变化，SDK自动上报变化的属性到平台
     * @param serviceIds 发生变化的服务id列表
     */
    public void fireServicesChanged(List<String> serviceIds) {
        super.fireServicesChanged(serviceIds);
    }

    /**
     * 获取设备客户端。获取到设备客户端后，可以直接调用客户端提供的消息、属性、命令等接口
     * @return 设备客户端实例
     */
    public DeviceClientServiceImpl getClient() {
        return super.getClient();
    }
}

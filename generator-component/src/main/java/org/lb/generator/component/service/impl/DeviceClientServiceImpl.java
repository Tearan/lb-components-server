package org.lb.generator.component.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.lb.generator.component.client.ClientConf;
import org.lb.generator.component.client.IotResult;
import org.lb.generator.component.client.rep.CommandRsp;
import org.lb.generator.component.client.req.Command;
import org.lb.generator.component.client.req.DeviceEvent;
import org.lb.generator.component.model.*;
import org.lb.generator.component.service.*;
import org.lb.generator.component.utils.ExceptionUtil;
import org.lb.generator.component.utils.IotUtil;
import org.lb.generator.component.utils.JsonUtil;
import org.lb.generator.component.utils.RequestManagerUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 设备客户端，提供和平台的通讯能力，包括：
 * 消息：双向，异步，不需要定义模型
 * 属性：双向，设备可以上报属性，平台可以向设备读写属性，属性需要在模型定义
 * 命令：单向，同步，平台向设备调用设备的命令
 * 事件：双向、异步，需要在模型定义
 * 用户不能直接创建DeviceClient实例，只能先创建IoTDevice实例，然后通过IoTDevice的getClient接口获取DeviceClient实例
 * @ClassName DeviceClient
 * @Description TODO
 * @Author Terran
 * @Date 2020/11/4 17:20
 * @Version 1.0
 */
@Slf4j
public class DeviceClientServiceImpl implements RawMessageListenerService {

    private PropertyListener propertyListener;
    private CommandListenerService commandListener;
    private CommandV3ListenerService commandV3Listener;
    private DeviceMessageListenerService deviceMessageListener;

    private ClientConf clientConf;
    private ConnectionService connection;
    private RequestManagerUtil requestManager;
    private String deviceId;
    private Map<String, RawMessageListenerService> rawMessageListenerMap;
    private AbstractDevice device;

    private ScheduledExecutorService executorService;
    private int ClientThreadCount = 1;
    public static int connectFailedTime = 0;
    private static final String DEFAULT_GZIP_ENCODEING = "UTF-8";

    public DeviceClientServiceImpl(ClientConf clientConf, AbstractDevice device) {
        checkClientConf(clientConf);
        this.clientConf = clientConf;
        this.deviceId = clientConf.getDeviceId();
        this.requestManager = new RequestManagerUtil(this);
        this.connection = new MqttConnectionServiceImpl(clientConf, this);
        this.device = device;
        this.rawMessageListenerMap = new ConcurrentHashMap<>();

    }

    public ClientConf getClientConf() {
        return clientConf;
    }

    private void checkClientConf(ClientConf clientConf) throws IllegalArgumentException {
        if (clientConf == null) {
            throw new IllegalArgumentException("clientConf is null");
        }
        if (clientConf.getDeviceId() == null) {
            throw new IllegalArgumentException("clientConf.getDeviceId() is null");
        }
        if (clientConf.getSecret() == null && clientConf.getKeyStore() == null) {
            throw new IllegalArgumentException("secret and keystore is null");
        }
        if (clientConf.getServerUri() == null) {
            throw new IllegalArgumentException("clientConf.getSecret() is null");
        }
        if (!clientConf.getServerUri().startsWith("tcp://") && (!clientConf.getServerUri().startsWith("ssl://"))) {
            throw new IllegalArgumentException("invalid serverUri");
        }
    }

    /**
     * 和平台建立连接，此接口为阻塞调用，超时时长60s。连接成功时，SDK会自动向平台订阅系统定义的topic。
     *
     * @return 0表示连接成功，其他表示连接失败
     */
    public int connect() {

        synchronized (this) {
            if (executorService == null) {
                executorService = Executors.newScheduledThreadPool(ClientThreadCount);
            }
        }

        int ret = connection.connect();

        //退避机制重连
        while (ret != 0) {
            connectFailedTime++;
            try {
                if (connectFailedTime < 10) {
                    Thread.sleep(1000);
                } else if (connectFailedTime < 50) {
                    Thread.sleep(5000);
                } else {
                    Thread.sleep(120000);
                }
                this.connection = new MqttConnectionServiceImpl(clientConf, this);
                ret = connection.connect();
            } catch (InterruptedException e) {
                log.debug("connect failed" + connectFailedTime + "times");
            }
        }

        connectFailedTime = 0;

        return ret;
    }

    /**
     * 上报设备消息
     * 如果需要上报子设备消息，需要调用DeviceMessage的setDeviceId接口设置为子设备的设备id
     *
     * @param deviceMessage 设备消息
     * @param listener      监听器，用于接收上报结果
     */
    public void reportDeviceMessage(DeviceMessage deviceMessage, ActionListenerService listener) {
        String topic = "$oc/devices/" + this.deviceId + "/sys/messages/up";
        this.publishRawMessage(new RawMessage(topic, JsonUtil.convertObject2String(deviceMessage)), listener);
    }

    /**
     * 上报压缩后的设备消息
     * @param deviceMessage 设备消息
     * @param listener 监听器，用于接收上报结果
     */
    public void reportCompressedDeviceMessage(DeviceMessage deviceMessage, ActionListenerService listener) {
        String topic = "$oc/devices/" + this.deviceId + "/sys/messages/up?encoding=gzip";
        byte[] compress = IotUtil.compress(JsonUtil.convertObject2String(deviceMessage), DEFAULT_GZIP_ENCODEING);
        this.publishRawMessage(new RawMessage(topic, compress), listener);
    }

    /**
     * 上报设备消息，支持指定qos
     * @param deviceMessage 设备消息
     * @param listener 监听器，用于接收上报结果
     * @param qos 消息qos，0或1
     */
    public void reportDeviceMessage(DeviceMessage deviceMessage, ActionListenerService listener, int qos) {
        String topic = "$oc/devices/" + this.deviceId + "/sys/messages/up";
        if (qos != 0) {
            qos = 1;
        }
        this.publishRawMessage(new RawMessage(topic, JsonUtil.convertObject2String(deviceMessage), qos), listener);
    }

    /**
     * 订阅topic
     * @param topic    topic值
     * @param listener 监听器
     * @param qos      qos
     */
    public void subscribeTopic(String topic, ActionListenerService listener, int qos) {
        connection.subscribeTopic(topic, listener, qos);
    }

    /**
     * 发布原始消息，原始消息和设备消息（DeviceMessage）的区别是：
     * 1、可以自定义topic，该topic需要在平台侧配置
     * 2、不限制payload的格式
     * @param rawMessage 原始消息
     * @param listener   监听器
     */
    public void publishRawMessage(RawMessage rawMessage, ActionListenerService listener) {
        connection.publishMessage(rawMessage, listener);
    }

    /**
     * 上报设备属性
     * @param properties 设备属性列表
     * @param listener   发布监听器
     */
    public void reportProperties(List<ServiceProperty> properties, ActionListenerService listener) {
        String topic = "$oc/devices/" + this.deviceId + "/sys/properties/report";
        ObjectNode jsonObject = JsonUtil.createObjectNode();
        jsonObject.putPOJO("services", properties);
        RawMessage rawMessage = new RawMessage(topic, JsonUtil.convertObject2String(jsonObject));
        connection.publishMessage(rawMessage, listener);
    }

    /**
     * 上报压缩后的设备属性
     * @param properties 设备属性列表
     * @param listener   发布监听器
     */
    public void reportCompressedProperties(List<ServiceProperty> properties, ActionListenerService listener) {
        String topic = "$oc/devices/" + this.deviceId + "/sys/properties/report?encoding=gzip";
        ObjectNode jsonObject = JsonUtil.createObjectNode();
        jsonObject.putPOJO("services", properties);
        byte[] compress = IotUtil.compress(JsonUtil.convertObject2String(jsonObject), DEFAULT_GZIP_ENCODEING);
        connection.publishMessage(new RawMessage(topic, compress), listener);
    }

    /**
     * 向平台上报设备属性（V3接口）
     * @param devicePropertiesV3 设备上报的属性
     * @param listener 发布监听器
     */
    public void reportPropertiesV3(DevicePropertiesV3 devicePropertiesV3, ActionListenerService listener) {
        String topic = "/huawei/v1/devices/" + this.deviceId + "/data/json";
        RawMessage rawMessage = new RawMessage(topic, devicePropertiesV3.toString());
        connection.publishMessage(rawMessage, listener);
    }

    /**
     * 向平台上报设备属性（V3接口）
     * @param bytes 设备上报的码流
     * @param listener 发布监听器
     */
    public void reportBinaryV3(Byte[] bytes, ActionListenerService listener) {
        String deviceId = clientConf.getDeviceId();
        String topic = "/huawei/v1/devices/" + deviceId + "/data/binary";
        RawMessage rawMessage = new RawMessage(topic, Arrays.toString(bytes));
        connection.publishMessage(rawMessage, listener);
    }

    /**
     * 向平台上报V3命令响应
     * @param commandRspV3 命令响应结果
     * @param listener 发布监听器
     */
    public void responseCommandV3(CommandRspV3 commandRspV3, ActionListenerService listener) {
        String topic = "/huawei/v1/devices/" + deviceId + "/data/json";
        RawMessage rawMessage = new RawMessage(topic, JsonUtil.convertObject2String(commandRspV3));
        connection.publishMessage(rawMessage, listener);
    }

    /**
     * 向平台上报V3命令响应（码流）
     * @param bytes 响应码流
     * @param listener 发布监听器
     */
    public void responseCommandBinaryV3(Byte[] bytes, ActionListenerService listener) {
        String topic = "/huawei/v1/devices/" + deviceId + "/data/binary";
        RawMessage rawMessage = new RawMessage(topic, Arrays.toString(bytes));
        connection.publishMessage(rawMessage, listener);
    }

    /**
     * 批量上报子设备属性
     *
     * @param deviceProperties 子设备属性列表
     * @param listener         发布监听器
     */
    public void reportSubDeviceProperties(List<DeviceProperty> deviceProperties, ActionListenerService listener) {
        ObjectNode node = JsonUtil.createObjectNode();
        node.putPOJO("devices", deviceProperties);
        String topic = "$oc/devices/" + getDeviceId() + "/sys/gateway/sub_devices/properties/report";
        RawMessage rawMessage = new RawMessage(topic, node.toString());
        publishRawMessage(rawMessage, listener);
    }

    /**
     * 上报压缩后的批量子设备属性
     * @param deviceProperties 子设备属性列表
     * @param listener 发布监听器
     */
    public void reportCompressedSubDeviceProperties(List<DeviceProperty> deviceProperties,
                                                    ActionListenerService listener) {

        ObjectNode node = JsonUtil.createObjectNode();
        node.putPOJO("devices", deviceProperties);

        String topic = "$oc/devices/" + getDeviceId() + "/sys/gateway/sub_devices/properties/report?encoding=gzip";

        byte[] compress = IotUtil.compress(node.toString(), DEFAULT_GZIP_ENCODEING);
        publishRawMessage(new RawMessage(topic, compress), listener);

    }


    private void OnPropertiesSet(RawMessage message) {
        String requestId = IotUtil.getRequestId(message.getTopic());
        PropsSet propsSet = JsonUtil.convertJsonStringToObject(message.toString(), PropsSet.class);
        if (propsSet == null) {
            return;
        }

        /** 只处理直连设备的，子设备的由AbstractGateway处理*/
        if (propertyListener != null && (propsSet.getDeviceId() == null || propsSet.getDeviceId().equals(getDeviceId()))) {
            propertyListener.onPropertiesSet(requestId, propsSet.getServices());
            return;
        }
        device.onPropertiesSet(requestId, propsSet);
    }

    private void OnPropertiesGet(RawMessage message) {
        String requestId = IotUtil.getRequestId(message.getTopic());
        PropsGet propsGet = JsonUtil.convertJsonStringToObject(message.toString(), PropsGet.class);
        if (propsGet == null) {
            return;
        }
        if (propertyListener != null && (propsGet.getDeviceId() == null || propsGet.getDeviceId().equals(getDeviceId()))) {
            propertyListener.onPropertiesGet(requestId, propsGet.getServiceId());
            return;
        }
        device.onPropertiesGet(requestId, propsGet);
    }


    private void onCommand(RawMessage message) {
        String requestId = IotUtil.getRequestId(message.getTopic());
        Command command = JsonUtil.convertJsonStringToObject(message.toString(), Command.class);
        if (command == null) {
            log.error("invalid command");
            return;
        }

        if (commandListener != null && (command.getDeviceId() == null || command.getDeviceId().equals(getDeviceId()))) {
            commandListener.onCommand(requestId, command.getServiceId(),
                    command.getCommandName(), command.getParas());
            return;
        }

        device.onCommand(requestId, command);

    }

    private void onCommandV3(RawMessage message) {
        CommandV3 commandV3 = JsonUtil.convertJsonStringToObject(message.toString(), CommandV3.class);
        if (commandV3 == null) {
            log.error("invalid commandV3");
            return;
        }

        if (commandV3Listener != null) {
            commandV3Listener.onCommandV3(commandV3);
        }
    }


    private void onDeviceMessage(RawMessage message) {
        DeviceMessage deviceMessage = JsonUtil.convertJsonStringToObject(message.toString(),
                DeviceMessage.class);
        if (deviceMessage == null) {
            log.error("invalid deviceMessage: " + message.toString());
            return;
        }

        if (deviceMessageListener != null && (deviceMessage.getDeviceId() == null || deviceMessage.getDeviceId().equals(getDeviceId()))) {
            deviceMessageListener.onDeviceMessage(deviceMessage);
            return;
        }
        device.onDeviceMessage(deviceMessage);
    }

    private void onEvent(RawMessage message) {

        DeviceEvents deviceEvents = JsonUtil.convertJsonStringToObject(message.toString(), DeviceEvents.class);
        if (deviceEvents == null) {
            log.error("invalid events");
            return;
        }
        device.onEvent(deviceEvents);
    }

    private void onResponse(RawMessage message) {
        requestManager.onRequestResponse(message);
    }


    @Override
    public void onMessageReceived(RawMessage message) {

        if (executorService == null) {
            log.error("executionService is null");
            return;
        }

        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    String topic = message.getTopic();

                    RawMessageListenerService listener = rawMessageListenerMap.get(topic);
                    if (listener != null) {
                        listener.onMessageReceived(message);
                        return;
                    }

                    if (topic.contains("/messages/down")) {
                        onDeviceMessage(message);
                    } else if (topic.contains("sys/commands/request_id")) {
                        onCommand(message);

                    } else if (topic.contains("/sys/properties/set/request_id")) {
                        OnPropertiesSet(message);

                    } else if (topic.contains("/sys/properties/get/request_id")) {
                        OnPropertiesGet(message);

                    } else if (topic.contains("/desired/properties/get/response")) {
                        onResponse(message);
                    } else if (topic.contains("/sys/events/down")) {
                        onEvent(message);
                    } else if (topic.contains("/huawei/v1/devices") && topic.contains("/command/")) {
                        onCommandV3(message);
                    } else {
                        log.error("unknown topic: " + topic);
                    }

                } catch (Exception e) {
                    log.error(ExceptionUtil.getBriefStackTrace(e));
                }
            }
        }, 0, TimeUnit.MILLISECONDS);

    }

    public void close() {
        connection.close();
    }

    /**
     * 上报命令响应
     *
     * @param requestId  请求id，响应的请求id必须和请求的一致
     * @param commandRsp 命令响应
     */
    public void respondCommand(String requestId, CommandRsp commandRsp) {

        String topic = "$oc/devices/" + deviceId + "/sys/commands/response/request_id=" + requestId;
        RawMessage rawMessage = new RawMessage(topic, JsonUtil.convertObject2String(commandRsp));
        connection.publishMessage(rawMessage, null);
    }

    /**
     * 上报读属性响应
     *
     * @param requestId 请求id，响应的请求id必须和请求的一致
     * @param services  服务属性
     */
    public void respondPropsGet(String requestId, List<ServiceProperty> services) {

        DeviceProperties deviceProperties = new DeviceProperties();
        deviceProperties.setServices(services);

        String topic = "$oc/devices/" + deviceId + "/sys/properties/get/response/request_id=" + requestId;
        RawMessage rawMessage = new RawMessage(topic, JsonUtil.convertObject2String(deviceProperties));
        connection.publishMessage(rawMessage, null);
    }

    /**
     * 上报写属性响应
     *
     * @param requestId 请求id，响应的请求id必须和请求的一致
     * @param iotResult 写属性结果
     */
    public void respondPropsSet(String requestId, IotResult iotResult) {

        String topic = "$oc/devices/" + deviceId + "/sys/properties/set/response/request_id=" + requestId;
        RawMessage rawMessage = new RawMessage(topic, JsonUtil.convertObject2String(iotResult));
        connection.publishMessage(rawMessage, null);
    }

    /**
     * 获取设备id
     *
     * @return 设备id
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * 设置连接监听器，用户接收连接建立和断开事件
     * @param connectListener
     */
    public void setConnectListener(ConnectListenerService connectListener) {
        connection.setConnectListener(connectListener);
    }

    /**
     * 订阅自定义topic。系统topic由SDK自动订阅，此接口只能用于订阅自定义topic
     * @param topic              自定义topic
     * @param actionListener     订阅结果监听器
     * @param rawMessageListener 接收自定义消息的监听器
     * @param qos                qos
     */
    public void subscribeTopic(String topic, ActionListenerService actionListener, RawMessageListenerService rawMessageListener, int qos) {
        connection.subscribeTopic(topic, actionListener, qos);
        rawMessageListenerMap.put(topic, rawMessageListener);
    }

    /**
     * 设置属性监听器，用于接收平台下发的属性读写。
     * 此监听器只能接收平台到直连设备的请求，子设备的请求由AbstractGateway处理
     * @param propertyListener 属性监听器
     */
    public void setPropertyListener(PropertyListener propertyListener) {
        this.propertyListener = propertyListener;
    }

    /**
     * 设置命令监听器，用于接收平台下发的命令。
     * 此监听器只能接收平台到直连设备的请求，子设备的请求由AbstractGateway处理
     * @param commandListener 命令监听器
     */
    public void setCommandListener(CommandListenerService commandListener) {
        this.commandListener = commandListener;
    }

    /**
     * 设置消息监听器，用于接收平台下发的消息
     * 此监听器只能接收平台到直连设备的请求，子设备的请求由AbstractGateway处理
     *
     * @param deviceMessageListener 消息监听器
     */
    public void setDeviceMessageListener(DeviceMessageListenerService deviceMessageListener) {
        this.deviceMessageListener = deviceMessageListener;
    }

    /**
     * 设置命令监听器，用于接收V3命令
     * @param commandV3Listener 命令监听器
     */
    public void setCommandV3Listener(CommandV3ListenerService commandV3Listener) {
        this.commandV3Listener = commandV3Listener;
    }

    public void setDevice(AbstractDevice device) {
        this.device = device;
    }

    /**
     * 事件上报
     * @param event    事件
     * @param listener 监听器
     */
    public void reportEvent(DeviceEvent event, ActionListenerService listener) {

        DeviceEvents events = new DeviceEvents();
        events.setDeviceId(getDeviceId());
        events.setServices(Arrays.asList(event));
        String deviceId = clientConf.getDeviceId();
        String topic = "$oc/devices/" + deviceId + "/sys/events/up";
        RawMessage rawMessage = new RawMessage(topic, JsonUtil.convertObject2String(events));
        connection.publishMessage(rawMessage, listener);
    }

    public Future<?> scheduleTask(Runnable runnable) {
        return executorService.schedule(runnable, 0, TimeUnit.MILLISECONDS);
    }

    public Future<?> scheduleTask(Runnable runnable, long delay) {
        return executorService.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public Future<?> scheduleRoutineTask(Runnable runnable, long period) {
        return executorService.scheduleAtFixedRate(runnable, period, period, TimeUnit.MILLISECONDS);
    }

    public int getClientThreadCount() {
        return ClientThreadCount;
    }

    public void setClientThreadCount(int clientThreadCount) {
        ClientThreadCount = clientThreadCount;
    }
}

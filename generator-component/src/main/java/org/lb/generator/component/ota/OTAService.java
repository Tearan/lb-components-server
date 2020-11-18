/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.ota;

import lombok.extern.slf4j.Slf4j;
import org.lb.generator.component.client.req.DeviceEvent;
import org.lb.generator.component.model.OTAPackage;
import org.lb.generator.component.service.ActionListenerService;
import org.lb.generator.component.service.OTAListenerService;
import org.lb.generator.component.service.impl.AbstractServiceImpl;
import org.lb.generator.component.utils.IotUtil;
import org.lb.generator.component.utils.JsonUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * OTA服务类，提供设备升级相关接口，使用方法：
 * IoTDevice device = new IoTDevice(...
 * OTAService otaService = device.getOtaService();
 * otaService.setOtaListener(new OTAListener() {
 * 具体参见OTASample
 * @author Terran
 * @since  1.0
 */
@Slf4j
public class OTAService extends AbstractServiceImpl {

    //升级上报的错误码，用户也可以扩展自己的错误码

    //成功
    public static final int OTA_CODE_SUCCESS = 0;

    //设备使用中
    public static final int OTA_CODE_BUSY = 1;

    //信号质量差
    public static final int OTA_CODE_SIGNAL_BAD = 2;

    //已经是最新版本
    public static final int OTA_CODE_NO_NEED = 3;

    //电量不足
    public static final int OTA_CODE_LOW_POWER = 4;

    //剩余空间不足
    public static final int OTA_CODE_LOW_SPACE = 5;

    //下载超时
    public static final int OTA_CODE_DOWNLOAD_TIMEOUT = 6;

    //升级包校验失败
    public static final int OTA_CODE_CHECK_FAIL = 7;

    //升级包类型不支持
    public static final int OTA_CODE_UNKNOWN_TYPE = 8;

    //内存不足
    public static final int OTA_CODE_LOW_MEMORY = 9;

    //安装升级包失败
    public static final int OTA_CODE_INSTALL_FAIL = 10;

    // 内部异常
    public static final int OTA_CODE_INNER_ERROR = 255;

    private OTAListenerService otaListener;
    private ExecutorService executorService ;//OTA单独起一个线程处理

    /**
     * 设置OTA监听器
     * @param otaListener OTA监听器
     */
    public void setOtaListener(OTAListenerService otaListener) {
        if (executorService == null){
            executorService = Executors.newSingleThreadExecutor();
        }
        this.otaListener = otaListener;
    }


    /**
     * 上报升级状态
     * @param result      升级结果
     * @param progress    升级进度0-100
     * @param version     当前版本
     * @param description 具体失败的原因，可选参数
     */
    public void reportOtaStatus(int result, int progress, String version, String description) {
        Map<String, Object> node = new HashMap<>();
        node.put("result_code", result);
        node.put("progress", progress);
        if (description != null) {
            node.put("description", description);
        }
        node.put("version", version);
        DeviceEvent deviceEvent = new DeviceEvent();
        deviceEvent.setEventType("upgrade_progress_report");
        deviceEvent.setParas(node);
        deviceEvent.setServiceId("$ota");
        deviceEvent.setEventTime(IotUtil.getTimeStamp());
        getIotDevice().getClient().reportEvent(deviceEvent, new ActionListenerService() {
            @Override
            public void onSuccess(Object context) {
            }
            @Override
            public void onFailure(Object context, Throwable var2) {
                log.error("reportOtaStatus failed: " + var2.getMessage());
            }
        });
    }

    /**
     * 上报固件版本信息
     * @param version 固件版本
     */
    public void reportVersion(String version) {
        Map<String, Object> node = new HashMap<>();
        node.put("fw_version", version);
        node.put("sw_version", version);
        DeviceEvent deviceEvent = new DeviceEvent();
        deviceEvent.setEventType("version_report");
        deviceEvent.setParas(node);
        deviceEvent.setServiceId("$ota");
        deviceEvent.setEventTime(IotUtil.getTimeStamp());
        getIotDevice().getClient().reportEvent(deviceEvent, new ActionListenerService() {
            @Override
            public void onSuccess(Object context) {
            }
            @Override
            public void onFailure(Object context, Throwable var2) {
                log.error("reportVersion failed: " + var2.getMessage());
            }
        });
    }

    /**
     * 接收OTA事件处理
     * @param deviceEvent 服务事件
     */
    @Override
    public void onEvent(DeviceEvent deviceEvent) {
        if (otaListener == null) {
            log.info("otaListener is null");
            return;
        }
        if (deviceEvent.getEventType().equalsIgnoreCase("version_query")) {
            otaListener.onQueryVersion();
        } else if (deviceEvent.getEventType().equalsIgnoreCase("firmware_upgrade")
                || deviceEvent.getEventType().equalsIgnoreCase("software_upgrade")) {

            OTAPackage pkg = JsonUtil.convertMap2Object(deviceEvent.getParas(), OTAPackage.class);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    otaListener.onNewPackage(pkg);
                }
            });
        }
    }
}

package org.lb.drools.component.filemanager;

import lombok.extern.slf4j.Slf4j;
import org.lb.drools.component.client.req.DeviceEvent;
import org.lb.drools.component.model.UrlParam;
import org.lb.drools.component.service.ActionListenerService;
import org.lb.drools.component.service.FileMangerListenerService;
import org.lb.drools.component.service.impl.AbstractServiceImpl;
import org.lb.drools.component.utils.IotUtil;
import org.lb.drools.component.utils.JsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName FileManager
 * @Description 未完成
 * @Author Terran
 * @Date 2020/11/4 22:19
 * @Version 1.0
 */
@Slf4j
public class FileManager extends AbstractServiceImpl {

    private FileMangerListenerService fileMangerListenerService;

    /**
     * 获取文件上传url
     *
     * @param fileName 文件名
     */
    public void getUploadUrl(String fileName) {

        Map<String, Object> node = new HashMap<>();
        node.put("file_name", fileName);

        DeviceEvent deviceEvent = new DeviceEvent();
        deviceEvent.setEventType("get_upload_url");
        deviceEvent.setParas(node);
        deviceEvent.setServiceId("$file_manager");
        deviceEvent.setEventTime(IotUtil.getTimeStamp());

        getIotDevice().getClient().reportEvent(deviceEvent, new ActionListenerService() {
            @Override
            public void onSuccess(Object context) {

            }

            @Override
            public void onFailure(Object context, Throwable var2) {
                log.error("reportEvent failed: " + var2.getMessage());
            }
        });

    }

    /**
     * 获取文件下载url
     *
     * @param fileName 下载文件名
     */
    public void getDownloadUrl(String fileName) {

        Map<String, Object> node = new HashMap<>();
        node.put("file_name", fileName);

        DeviceEvent deviceEvent = new DeviceEvent();
        deviceEvent.setEventType("get_download_url");
        deviceEvent.setParas(node);
        deviceEvent.setServiceId("$file_manager");
        deviceEvent.setEventTime(IotUtil.getTimeStamp());

        getIotDevice().getClient().reportEvent(deviceEvent, new ActionListenerService() {
            @Override
            public void onSuccess(Object context) {

            }

            @Override
            public void onFailure(Object context, Throwable var2) {
                log.error("reportEvent failed: " + var2.getMessage());
            }
        });

    }

    /**
     * 接收文件处理事件
     *
     * @param deviceEvent 服务事件
     */
    @Override
    public void onEvent(DeviceEvent deviceEvent) {

        if (fileMangerListenerService == null) {
            log.info("fileMangerListener is null");
            return;
        }

        if (deviceEvent.getEventType().equalsIgnoreCase("get_upload_url_response")) {
            UrlParam urlParam = JsonUtil.convertMap2Object(deviceEvent.getParas(), UrlParam.class);
            fileMangerListenerService.onUploadUrl(urlParam);
        } else if (deviceEvent.getEventType().equalsIgnoreCase("get_download_url_response")) {
            UrlParam urlParam = JsonUtil.convertMap2Object(deviceEvent.getParas(), UrlParam.class);
            fileMangerListenerService.onDownloadUrl(urlParam);
        }
    }
}

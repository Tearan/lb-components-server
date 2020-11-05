package org.lb.generator.component.timesync;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.lb.generator.component.client.req.DeviceEvent;
import org.lb.generator.component.service.ActionListenerService;
import org.lb.generator.component.service.TimeSyncListenerService;
import org.lb.generator.component.service.impl.AbstractServiceImpl;
import org.lb.generator.component.utils.IotUtil;
import org.lb.generator.component.utils.JsonUtil;

import java.util.*;

/**
 *
 * * 时间同步服务，提供简单的时间同步服务，使用方法：
 *  *  IoTDevice device = new IoTDevice(...
 *  *  TimeSyncService timeSyncService = device.getTimeSyncService();
 *  *  timeSyncService.setListener(new TimeSyncListener() {
 *  *             @Override
 *  *             public void onTimeSyncResponse(long device_send_time, long server_recv_time, long server_send_time) {
 *  *                 long device_recv_time = System.currentTimeMillis();
 *  *                 long now = (server_recv_time + server_send_time + device_recv_time - device_send_time) / 2;
 *  *                 System.out.println("now is "+ new Date(now) );
 *  *             }
 *  *         });
 *  *  timeSyncService.RequestTimeSync()
 * @ClassName TimeSyncService
 * @Description TODO
 * @Author Terran
 * @Date 2020/11/4 22:59
 * @Version 1.0
 */
@Slf4j
public class TimeSyncService extends AbstractServiceImpl {

    private TimeSyncListenerService listener;

    public TimeSyncListenerService getListener() {
        return listener;
    }

    /**
     * 设置时间同步响应监听器
     * @param listener 监听器
     */
    public void setListener(TimeSyncListenerService listener) {
        this.listener = listener;
    }

    /**
     * 发起时间同步请求，使用TimeSyncListener接收响应
     */
    public void RequestTimeSync() {
        Map<String, Object> node = new HashMap<>();
        node.put("device_send_time", System.currentTimeMillis());
        DeviceEvent deviceEvent = new DeviceEvent();
        deviceEvent.setEventType("time_sync_request");
        deviceEvent.setParas(node);
        deviceEvent.setServiceId("$time_sync");
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

    @Override
    public void onEvent(DeviceEvent deviceEvent) {
        if (listener == null){
            return;
        }
        if (deviceEvent.getEventType().equalsIgnoreCase("time_sync_response")) {
            ObjectNode node = JsonUtil.convertMap2Object(deviceEvent.getParas(), ObjectNode.class);
            long device_send_time = node.get("device_send_time").asLong();
            long server_recv_time = node.get("server_recv_time").asLong();
            long server_send_time = node.get("server_send_time").asLong();
            listener.onTimeSyncResponse(device_send_time, server_recv_time, server_send_time);
        }
    }
}

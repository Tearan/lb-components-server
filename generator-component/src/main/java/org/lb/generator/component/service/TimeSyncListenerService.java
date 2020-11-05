package org.lb.generator.component.service;

/**
 * @ClassName TimeSyncListenerService
 * @Description 监听时间同步事件
 * @Author Terran
 * @Date 2020/11/5 10:04
 * @Version 1.0
 */
public interface TimeSyncListenerService {

    /**
     * 时间同步响应
     * 假设设备收到的设备侧时间为device_recv_time ，则设备计算自己的准确时间为：
     * (server_recv_time + server_send_time + device_recv_time - device_send_time) / 2
     * @param device_send_time 设备发送时间
     * @param server_recv_time 服务端接收时间
     * @param server_send_time 服务端响应发送时间
     */
    void onTimeSyncResponse(long device_send_time, long server_recv_time, long server_send_time);
}

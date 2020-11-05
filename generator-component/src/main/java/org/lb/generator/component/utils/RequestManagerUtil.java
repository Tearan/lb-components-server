package org.lb.generator.component.utils;

import lombok.extern.slf4j.Slf4j;
import org.lb.generator.component.service.impl.DeviceClientServiceImpl;
import org.lb.generator.component.client.req.IotRequest;
import org.lb.generator.component.model.RawMessage;
import org.lb.generator.component.service.RequestListenerService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @ClassName RequestManager
 * @Description 请求管理器
 * @Author Terran
 * @Date 2020/11/4 22:33
 * @Version 1.0
 */
@Slf4j
public class RequestManagerUtil {

    private DeviceClientServiceImpl iotClient;
    private ConcurrentMap<String, IotRequest> pendingRequests = new ConcurrentHashMap<>();

    /**
     * 构造函数
     *
     * @param client 客户端
     */
    public RequestManagerUtil(DeviceClientServiceImpl client) {
        this.iotClient = client;
    }

    /**
     * 执行同步请求
     *
     * @param iotRequest 请求参数
     * @return 请求执行结果
     */
    public Object executeSyncRequest(IotRequest iotRequest) {

        RawMessage rawMessage = iotRequest.getRawMessage();
        iotClient.publishRawMessage(rawMessage, null);
        pendingRequests.put(iotRequest.getRequestId(), iotRequest);
        iotRequest.runSync();
        return iotRequest.getResult();
    }

    /**
     * 执行异步请求
     *
     * @param iotRequest 请求参数
     * @param listener   请求监听器，用于接收请求完成通知
     */
    public void executeAsyncRequest(IotRequest iotRequest, RequestListenerService listener) {

        RawMessage rawMessage = iotRequest.getRawMessage();
        iotClient.publishRawMessage(rawMessage, null);
        pendingRequests.put(iotRequest.getRequestId(), iotRequest);
        iotRequest.runAync(listener);
    }

    /**
     * 请求响应回调，由sdk自动调用
     *
     * @param message 响应消息
     */
    public void onRequestResponse(RawMessage message) {
        String requestId = IotUtil.getRequestId(message.getTopic());
        IotRequest request = pendingRequests.remove(requestId);
        if (request == null) {
            log.error("request is null, requestId: " + requestId);
            return;
        }

        request.onFinish(message.toString());
    }
}

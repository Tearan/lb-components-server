package org.lb.generator.component.service;

/**
 * @ClassName RequestListenerService
 * @Description 请求监听器
 * @Author Terran
 * @Date 2020/11/4 22:36
 * @Version 1.0
 */
public interface RequestListenerService {

    /**
     * 请求执行完成通知
     * @param result 请求执行结果
     */
    void onFinish(String result);
}

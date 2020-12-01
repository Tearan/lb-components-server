/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.lb.generator.component.client.ClientConf;
import org.lb.generator.component.model.RawMessage;
import org.lb.generator.component.service.ActionListenerService;
import org.lb.generator.component.service.ConnectListenerService;
import org.lb.generator.component.service.ConnectionService;
import org.lb.generator.component.service.RawMessageListenerService;
import org.lb.generator.component.utils.ExceptionUtil;
import org.lb.generator.component.utils.IotUtil;
import javax.net.ssl.SSLContext;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * mqtt连接
 * @author Terran
 * @since  1.0
 */
@Slf4j
public class MqttConnectionServiceImpl implements ConnectionService {

    private static final int DEFAULT_QOS = 1;
    private static final int DEFAULT_SUBSCRIBE_QOS = 0;
    private static final int DEFAULT_CONNECT_TIMEOUT = 60;
    private static final int DEFAULT_KEEPLIVE = 120;
    private static final String connectType = "0";
    private static final String checkTimestamp = "0";
    private ClientConf clientConf;
    private boolean connectFinished = false;
    private MqttAsyncClient mqttAsyncClient;
    private ConnectListenerService connectListener;
    private RawMessageListenerService rawMessageListener;


    public MqttConnectionServiceImpl(ClientConf clientConf, RawMessageListenerService rawMessageListener) {
        this.clientConf = clientConf;
        this.rawMessageListener = rawMessageListener;
    }

    private MqttCallback callback = new MqttCallbackExtended() {
        @Override
        public void connectionLost(Throwable cause) {
            log.error("Connection lost.", cause);
            if (connectListener != null) {
                connectListener.connectionLost(cause);
            }
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            log.info("messageArrived topic:{},msg:{}", topic,message.toString());
            RawMessage rawMessage = new RawMessage(topic, message.toString());
            try {
                if (rawMessageListener != null) {
                    rawMessageListener.onMessageReceived(rawMessage);
                }
            } catch (Exception e) {
                log.error(ExceptionUtil.getBriefStackTrace(e));
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            log.info("Mqtt client connected. address:{}",serverURI);
            if (connectListener != null) {
                connectListener.connectComplete(reconnect, serverURI);
            }
        }
    };

    @Override
    public int connect() {
        try {
            String timeStamp = ZonedDateTime.ofInstant(Instant.now(),ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
            String clientId = null;
            if (clientConf.getScopeId() == null) {
                clientId = clientConf.getDeviceId() + "_" + connectType + "_" + checkTimestamp + "_" + timeStamp;
            } else {
                clientId = clientConf.getDeviceId() + "_" + connectType + "_" + clientConf.getScopeId();
            }
            try {
                mqttAsyncClient = new MqttAsyncClient(clientConf.getServerUri(), clientId, new MemoryPersistence());
            } catch (MqttException e) {
                log.error(ExceptionUtil.getBriefStackTrace(e));
            }

            DisconnectedBufferOptions bufferOptions = new DisconnectedBufferOptions();
            bufferOptions.setBufferEnabled(true);
            if (clientConf.getOfflineBufferSize() != null) {
                bufferOptions.setBufferSize(clientConf.getOfflineBufferSize());
            }
            mqttAsyncClient.setBufferOpts(bufferOptions);
            MqttConnectOptions options = new MqttConnectOptions();
            if (clientConf.getServerUri().contains("ssl:")) {
                try {
                    SSLContext sslContext = IotUtil.getSSLContext(clientConf);
                    options.setSocketFactory(sslContext.getSocketFactory());
                    options.setHttpsHostnameVerificationEnabled(false);
                } catch (Exception e) {
                    log.error(ExceptionUtil.getBriefStackTrace(e));
                    return -1;
                }
            }
            options.setCleanSession(false);
            options.setUserName(clientConf.getDeviceId());
            if (clientConf.getSecret() != null && !clientConf.getSecret().isEmpty()) {
                String passWord = IotUtil.sha256_mac(clientConf.getSecret(), timeStamp);
                options.setPassword(passWord.toCharArray());
            }
            options.setConnectionTimeout(DEFAULT_CONNECT_TIMEOUT);
            options.setKeepAliveInterval(DEFAULT_KEEPLIVE);
            options.setAutomaticReconnect(true);
            mqttAsyncClient.setCallback(callback);

            log.info("try to connect to " + clientConf.getServerUri());

            mqttAsyncClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    log.info("connect success:{}",clientConf.getServerUri());
                    synchronized (MqttConnectionServiceImpl.this) {
                        connectFinished = true;
                        MqttConnectionServiceImpl.this.notifyAll();
                    }
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    log.info("connect failed:{}",throwable.toString());
                    synchronized (MqttConnectionServiceImpl.this) {
                        connectFinished = true;
                        MqttConnectionServiceImpl.this.notifyAll();
                    }
                }
            });

            synchronized (this) {
                while (!connectFinished) {
                    try {
                        wait(DEFAULT_CONNECT_TIMEOUT * 1000);
                    } catch (InterruptedException e) {
                        log.error(ExceptionUtil.getBriefStackTrace(e));
                    }
                }
            }
        } catch (MqttException e) {
            log.error(ExceptionUtil.getBriefStackTrace(e));
        }
        return mqttAsyncClient.isConnected() ? 0 : -1;
    }

    @Override
    public void publishMessage(RawMessage message, ActionListenerService listener) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getPayload());
            mqttMessage.setQos(message.getQos() == 0 ? 0 : DEFAULT_QOS);

            mqttAsyncClient.publish(message.getTopic(), mqttMessage, message.getTopic(), new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    if (listener != null) {
                        listener.onSuccess(null);
                    }
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    log.error("publish message failed   " + message);
                    if (listener != null) {
                        listener.onFailure(null, throwable);
                    }

                }
            });
            log.info("publish message topic =  " + message.getTopic() + ", msg = " + message.toString());
        } catch (MqttException e) {
            log.error(ExceptionUtil.getBriefStackTrace(e));
            if (listener != null) {
                listener.onFailure(null, e);
            }
        }
    }

    @Override
    public void close() {
        if (mqttAsyncClient.isConnected()) {
            try {
                mqttAsyncClient.disconnect();
            } catch (MqttException e) {
                log.error(ExceptionUtil.getBriefStackTrace(e));
            }
        }
    }

    @Override
    public boolean isConnected() {
        if (mqttAsyncClient == null) {
            return false;
        }
        return mqttAsyncClient.isConnected();
    }

    @Override
    public void setConnectListener(ConnectListenerService connectListener) {
        this.connectListener = connectListener;
    }

    public void setRawMessageListener(RawMessageListenerService rawMessageListener) {
        this.rawMessageListener = rawMessageListener;
    }


    /**
     * 订阅指定主题
     * @param topic 主题
     */
    @Override
    public void subscribeTopic(String topic, ActionListenerService listener, int qos) {
        try {
            mqttAsyncClient.subscribe(topic, qos, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    if (listener != null) {
                        listener.onSuccess(topic);
                    }
                }
                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    log.error("subscribe topic failed:" + topic);
                    if (listener != null) {
                        listener.onFailure(topic, throwable);
                    }
                }
            });
        } catch (MqttException e) {
            log.error(ExceptionUtil.getBriefStackTrace(e));
            if (listener != null) {
                listener.onFailure(topic, e);
            }
        }

    }
}
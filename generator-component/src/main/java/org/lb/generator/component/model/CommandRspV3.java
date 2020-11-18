/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.model;

import lombok.Data;

/**
 * 命令响应V3
 * @author Terran
 * @since 1.0
 */
@Data
public class CommandRspV3 {

    /** 消息类型 固定值为：deviceRsp */
    private String msgType;

    /** 命令ID，把物联网平台下发命令时携带的“mid”返回给平台。*/
    private int mid;

    /** 命令执行的结果码: 【“0”表示执行成功。 “1”表示执行失败。】 */
    private int errcode;

    /** 命令的应答，具体字段在设备的产品模型中定义，可选。*/
    private Object body;

    public CommandRspV3(String msgType, int mid, int errcode) {
        this.msgType = msgType;
        this.mid = mid;
        this.errcode = errcode;
    }

    public CommandRspV3(String msgType, int mid, int errcode, Object body) {
        this.msgType = msgType;
        this.mid = mid;
        this.errcode = errcode;
        this.body = body;
    }

}

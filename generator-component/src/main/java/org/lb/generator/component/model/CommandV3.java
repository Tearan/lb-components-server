package org.lb.generator.component.model;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName CommandV3
 * @Description TODO
 * @Author Terran
 * @Date 2020/11/4 22:27
 * @Version 1.0
 */
@Data
public class CommandV3 {

    private String msgType;

    private String serviceId;

    private int mid;

    private String cmd;

    private Map<String, Object> paras;
}

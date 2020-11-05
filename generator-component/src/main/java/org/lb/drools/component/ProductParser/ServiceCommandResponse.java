package org.lb.drools.component.ProductParser;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ServiceCommandResponse
 * @Description 服务命令响应
 * @Author Terran
 * @Date 2020/11/3 23:00
 * @Version 1.0
 */
@Data
public class ServiceCommandResponse implements Serializable {

    private static final long serialVersionUID = 4535630761027464385L;

    private String responseName;

    private List<ServiceCommandPara> paras;
}

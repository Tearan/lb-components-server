package org.lb.drools.component.ProductParser;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ServiceCommand
 * @Description 执行的命令
 * @Author Terran
 * @Date 2020/11/3 22:57
 * @Version 1.0
 */
@Data
public class ServiceCommand implements Serializable {

    private static final long serialVersionUID = -8726398850035913800L;

    private String commandName;

    private List<ServiceCommandPara> paras;

    private List<ServiceCommandResponse> responses;
}

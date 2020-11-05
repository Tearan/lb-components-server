package org.lb.drools.component.ProductParser;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ServiceCommandPara
 * @Description 服务命令段落
 * @Author Terran
 * @Date 2020/11/3 22:58
 * @Version 1.0
 */
@Data
public class ServiceCommandPara implements Serializable {

    private static final long serialVersionUID = 4565399257931903508L;

    private String paraName;

    private String dataType;

    private boolean required;

    private String min;

    private String max;

    private double step;

    private int maxLength;

    private String unit;

    private List<String> enumList;

    private transient ObjectNode extendparam;

    /** 描述 */
    private String description;
}

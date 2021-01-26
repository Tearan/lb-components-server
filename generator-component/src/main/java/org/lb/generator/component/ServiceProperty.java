/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 服务操作控制
 * @author Terran
 * @since  1.0
 */
@Data
public class ServiceProperty implements Serializable {

    private static final long serialVersionUID = 8483808607881241605L;

    public static final String READ = "R";

    public static final String WRITE = "W";

    public static final String EXECUTE = "E";

    private String propertyName;

    //@Pattern(regexp = "(int|long|decimal|string|DateTime|jsonObject|enum|boolean|string list)")

    private String dataType;

    @JsonIgnore
    private String javaType;

    @JsonIgnore
    private String writeable;

    @JsonIgnore
    private String val;

    private boolean required;

    private String min;

    private String max;

    private double step;

    @JsonSetter(nulls = Nulls.SKIP)
    private int maxLength = 1024;

    // 访问方法：RWE 可读R，可写W，可观察E

    //@Pattern(regexp = "(RWE|RW|RE|WE|E|W|R)")
    private String method;

    private String unit;

    private List<String> enumList;

    /** 描述 */
    private String description;

    private transient ObjectNode extendparam;
}

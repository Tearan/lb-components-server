/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Terran
 * @since  1.0
 */
@Data
public class UrlParam {

    private String url;

    @JsonProperty("bucket_name")
    private String bucketName;

    @JsonProperty("object_name")
    private String objectName;

    private Integer expire;
}

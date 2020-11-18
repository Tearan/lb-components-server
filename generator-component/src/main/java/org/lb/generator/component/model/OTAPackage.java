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
public class OTAPackage {

    String url;

    String version;

    @JsonProperty("file_size")
    int fileSize;

    @JsonProperty("access_token")
    String token;

    int expires;

    String sign;
}

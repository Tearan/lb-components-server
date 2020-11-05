package org.lb.drools.component.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @ClassName UrlParam
 * @Description TODO
 * @Author Terran
 * @Date 2020/11/4 22:20
 * @Version 1.0
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

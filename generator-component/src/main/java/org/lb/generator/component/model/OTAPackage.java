package org.lb.generator.component.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @ClassName OTAPackage
 * @Description TODO
 * @Author Terran
 * @Date 2020/11/4 23:05
 * @Version 1.0
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

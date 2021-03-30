/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.generator.component.profile;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.lb.generator.component.attribute.DeviceCapability;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * @author Terran
 * @since  1.0
 */
@Slf4j
public class DeviceProfileParser {

    private static final String DEVICES_TITLE = "devices";

    public static List<DeviceCapability> getDeviceCapability(String filePath) {
        if (filePath == null) {
            log.debug("device capability path is null");
            return null;
        }

        JsonFactory factory = new JsonFactory();
        ObjectMapper objectMapper = new ObjectMapper(factory);

        File from = new File(filePath);
        TypeReference<HashMap<String, List<DeviceCapability>>> typeRef
                = new TypeReference<HashMap<String, List<DeviceCapability>>>() {};
        HashMap<String, List<DeviceCapability>> hm;

        try {
            hm = objectMapper.readValue(from, typeRef);
            if (hm == null) {
                log.debug("hm is null, read device capability failed.");
                return null;
            }
            return hm.get(DEVICES_TITLE);
        } catch (Exception e) {

        }
        return null;
    }
}

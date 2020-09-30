package org.lb.drools.component.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

/**
 * @ClassName KieAccessor
 * @Description TODO
 * @Author Terran
 * @Date 2020/9/28 11:08
 * @Version 1.0
 */
@Data
@Slf4j
public class KieAccessor implements InitializingBean {

    public static final String PATH_SPLIT = ",";

    private String path;

    private String mode;

    private Long update;

    private String listener;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (path == null || path.length() == 0){
            log.error("Please set base path(spring.drools.path = ***).");
        }
    }
}

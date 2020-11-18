/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.drools.component.config;

import org.lb.drools.component.KieSchedule;
import org.lb.drools.component.KieTemplate;
import org.lb.drools.component.common.DroolsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Terran
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(DroolsProperties.class)
public class DroolsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "kieTemplate")
    public KieTemplate kieTemplate(DroolsProperties droolsProperties){
        KieTemplate kieTemplate = new KieTemplate();
        kieTemplate.setPath(droolsProperties.getPath());
        kieTemplate.setMode(droolsProperties.getMode());
        kieTemplate.setUpdate(droolsProperties.getUpdate());
        kieTemplate.setListener(droolsProperties.getListener());
        return kieTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(name = "kieSchedule")
    public KieSchedule kieSchedule(KieTemplate kieTemplate){
        KieSchedule kieSchedule = new KieSchedule(kieTemplate);
        kieSchedule.execute();
        return kieSchedule;
    }

}

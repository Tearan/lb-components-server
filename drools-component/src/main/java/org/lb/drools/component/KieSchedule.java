/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.drools.component;

import org.lb.drools.component.utils.ScheduledThreadPoolExecutorUtil;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.TimeUnit;

/**
 * @author Terran
 * @since 1.0
 */
public class KieSchedule implements InitializingBean {

    private KieTemplate kieTemplate;

    public KieSchedule(KieTemplate kieTemplate){
        this.kieTemplate = kieTemplate;
    }

    public void execute(){
        Long update = kieTemplate.getUpdate();
        if (update == null || update == 0L){
            update = 30L;
        }
        ScheduledThreadPoolExecutorUtil.RULE_SCHEDULE.scheduleAtFixedRate(new RuleCache(kieTemplate),
                1,update, TimeUnit.SECONDS);

        ScheduledThreadPoolExecutorUtil.CACHE_KIE.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

            }
        },1,1,TimeUnit.SECONDS);
    }

    @Override
    public void afterPropertiesSet() {

    }
}

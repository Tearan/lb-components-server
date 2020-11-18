/*
 * Copyright 2020-2099 the original author or authors.
 */
package org.lb.drools.component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Terran
 * @since 1.0
 */
@Slf4j
public class RuleCache implements Runnable{

    private KieTemplate kieTemplate;

    RuleCache(KieTemplate kieTemplate){
        this.kieTemplate = kieTemplate;
    }

    @Override
    public void run() {
        log.info("Start updating rule file");
        kieTemplate.doRead0();
    }
}

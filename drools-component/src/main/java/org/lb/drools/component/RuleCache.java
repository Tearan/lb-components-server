package org.lb.drools.component;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName RuleCache
 * @Description TODO
 * @Author Terran
 * @Date 2020/9/28 14:39
 * @Version 1.0
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

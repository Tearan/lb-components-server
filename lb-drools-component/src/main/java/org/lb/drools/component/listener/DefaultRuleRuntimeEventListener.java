package org.lb.drools.component.listener;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;

/**
 * @ClassName DefaultRuleRuntimeEventListener
 * @Description TODO
 * @Author Terran
 * @Date 2020/9/28 14:15
 * @Version 1.0
 */
@Slf4j
public class DefaultRuleRuntimeEventListener implements RuleRuntimeEventListener {

    @Override
    public void objectInserted(ObjectInsertedEvent objectInsertedEvent) {
        log.info("Insert Object: {}; operation rule: {}",objectInsertedEvent.getFactHandle(),objectInsertedEvent.getRule());
    }

    @Override
    public void objectUpdated(ObjectUpdatedEvent objectUpdatedEvent) {
        log.info("Update object: {}; operation rule: {}",objectUpdatedEvent.getFactHandle(),objectUpdatedEvent.getRule());
    }

    @Override
    public void objectDeleted(ObjectDeletedEvent objectDeletedEvent) {
        log.info("Delete object: {}; operation rule: {}",objectDeletedEvent.getFactHandle(),objectDeletedEvent.getRule());
    }
}

package org.lb.drools.component.listener;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.event.rule.*;

/**
 * @ClassName DefaultAgendaEventListener
 * @Description TODO
 * @Author Terran
 * @Date 2020/9/28 14:15
 * @Version 1.0
 */
@Slf4j
public class DefaultAgendaEventListener implements AgendaEventListener {

    @Override
    public void matchCreated(MatchCreatedEvent matchCreatedEvent) {

    }

    @Override
    public void matchCancelled(MatchCancelledEvent matchCancelledEvent) {
        log.info("Matching rule: {}",matchCancelledEvent.getMatch());
    }

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent beforeMatchFiredEvent) {
        log.info("Start executing java code block, matching rule: {}, evaluation object: {}",
                beforeMatchFiredEvent.getMatch().getRule(),beforeMatchFiredEvent.getMatch().getFactHandles());
    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent afterMatchFiredEvent) {
        log.info("End execution of Java code block, matching rule: {}, evaluation object: {}",
                afterMatchFiredEvent.getMatch().getRule(),afterMatchFiredEvent.getMatch().getFactHandles());
    }

    @Override
    public void agendaGroupPopped(AgendaGroupPoppedEvent agendaGroupPoppedEvent) {

    }

    @Override
    public void agendaGroupPushed(AgendaGroupPushedEvent agendaGroupPushedEvent) {

    }

    @Override
    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent ruleFlowGroupActivatedEvent) {

    }

    @Override
    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent ruleFlowGroupActivatedEvent) {

    }

    @Override
    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent ruleFlowGroupDeactivatedEvent) {

    }

    @Override
    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent ruleFlowGroupDeactivatedEvent) {

    }
}

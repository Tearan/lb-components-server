package org.lb.drools.component.listener;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.event.process.*;

/**
 * @ClassName DefaultProcessEventListener
 * @Description TODO
 * @Author Terran
 * @Date 2020/9/28 14:15
 * @Version 1.0
 */
@Slf4j
public class DefaultProcessEventListener implements ProcessEventListener {

    @Override
    public void beforeSLAViolated(SLAViolatedEvent event) {

    }

    @Override
    public void afterSLAViolated(SLAViolatedEvent event) {

    }

    @Override
    public void beforeProcessStarted(ProcessStartedEvent processStartedEvent) {

    }

    @Override
    public void afterProcessStarted(ProcessStartedEvent processStartedEvent) {

    }

    @Override
    public void beforeProcessCompleted(ProcessCompletedEvent processCompletedEvent) {

    }

    @Override
    public void afterProcessCompleted(ProcessCompletedEvent processCompletedEvent) {

    }

    @Override
    public void beforeNodeTriggered(ProcessNodeTriggeredEvent processNodeTriggeredEvent) {

    }

    @Override
    public void afterNodeTriggered(ProcessNodeTriggeredEvent processNodeTriggeredEvent) {

    }

    @Override
    public void beforeNodeLeft(ProcessNodeLeftEvent processNodeLeftEvent) {

    }

    @Override
    public void afterNodeLeft(ProcessNodeLeftEvent processNodeLeftEvent) {

    }

    @Override
    public void beforeVariableChanged(ProcessVariableChangedEvent processVariableChangedEvent) {

    }

    @Override
    public void afterVariableChanged(ProcessVariableChangedEvent processVariableChangedEvent) {

    }
}

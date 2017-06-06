package net.ahm.careengine.activemeasure.qualitymeasure.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.ahm.careengine.eventprocessing.engine.drools.DroolsEventListener;

import org.drools.definition.rule.Rule;
import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaGroupPoppedEvent;
import org.drools.event.rule.AgendaGroupPushedEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.ObjectUpdatedEvent;
import org.drools.event.rule.RuleFlowGroupActivatedEvent;
import org.drools.event.rule.RuleFlowGroupDeactivatedEvent;

public class DroolsEventTestListener implements DroolsEventListener {
    private Map<String, AtomicInteger> rulesFired = new HashMap<String, AtomicInteger>();

    public Map<String, AtomicInteger> getRulesFired() {
        return rulesFired;
    }

    @Override
    public void activationCreated(ActivationCreatedEvent event) {
        // do nothing
    }

    @Override
    public void activationCancelled(ActivationCancelledEvent event) {
        // do nothing
    }

    @Override
    public void beforeActivationFired(BeforeActivationFiredEvent event) {
        // do nothing
    }

    @Override
    public void afterActivationFired(AfterActivationFiredEvent event) {
        Rule rule = event.getActivation().getRule();
        String fullRuleName = rule.getPackageName() + "/" + rule.getName();
        AtomicInteger firedCount = rulesFired.get(fullRuleName);

        if (firedCount == null) {
            firedCount = new AtomicInteger(0);
            rulesFired.put(fullRuleName, firedCount);
        }

        firedCount.incrementAndGet();
    }

    @Override
    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
        // do nothing
    }

    @Override
    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
        // do nothing
    }

    @Override
    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        // do nothing
    }

    @Override
    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        // do nothing
    }

    @Override
    public void beforeRuleFlowGroupDeactivated(
            RuleFlowGroupDeactivatedEvent event) {
        // do nothing
    }

    @Override
    public void afterRuleFlowGroupDeactivated(
            RuleFlowGroupDeactivatedEvent event) {
        // do nothing
    }

    @Override
    public void objectInserted(ObjectInsertedEvent event) {
        // do nothing
    }

    @Override
    public void objectUpdated(ObjectUpdatedEvent event) {
        // do nothing
    }

    @Override
    public void objectRetracted(ObjectRetractedEvent event) {
        // do nothing
    }
}

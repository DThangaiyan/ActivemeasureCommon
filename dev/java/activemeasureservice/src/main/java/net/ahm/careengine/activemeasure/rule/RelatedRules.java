package net.ahm.careengine.activemeasure.rule;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ahm.careengine.eventprocessing.engine.drools.rule.AbstractRelatedRules;

public class RelatedRules extends
        AbstractRelatedRules<RuleType, RuleDescription> {

    private Set<Long> allElementIds;
    private Set<Long> allUsedClassifierIds;

    public RelatedRules(Long id) {
        super(id);
    }

    @Override
    protected Map<RuleType, Set<RuleDescription>> createRuleTypeMap() {
        return new EnumMap<RuleType, Set<RuleDescription>>(RuleType.class);
    }

    @Override
    public void addRule(RuleDescription rule) {
        clearCachedValues();
        super.addRule(rule);
    }

    private void clearCachedValues() {
        allElementIds = null;
        allUsedClassifierIds = null;
    }

    public Set<Long> getAllElementIds() {
        if (allElementIds == null) {
            allElementIds = new HashSet<Long>();
            for (RuleDescription rule : getAllRules()) {
                allElementIds.addAll(rule.getElementIdsUsed());
            }
        }
        return allElementIds;
    }

    public Set<Long> getAllUsedClassifierIds() {
        if (allUsedClassifierIds == null) {
            allUsedClassifierIds = new HashSet<Long>();
            for (RuleDescription rule : getAllRules()) {
                allUsedClassifierIds.addAll(rule.getClassifierIdsUsed());
            }
        }
        return allUsedClassifierIds;
    }
}

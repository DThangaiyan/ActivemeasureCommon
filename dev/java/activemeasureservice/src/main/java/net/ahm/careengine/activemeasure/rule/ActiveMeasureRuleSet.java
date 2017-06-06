package net.ahm.careengine.activemeasure.rule;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ActiveMeasureRuleSet {
    private final Set<RuleDescription>    allRules;
    private final Map<Long, RelatedRules> qmRulesByMeasureId          = new HashMap<Long, RelatedRules>();
    private final Map<Long, RelatedRules> cdmRulesByCdmId             = new HashMap<Long, RelatedRules>();
    private final Map<Long, RelatedRules> classifierRulesById         = new HashMap<Long, RelatedRules>();
    private final Map<Long, RelatedRules> classifierCreationRulesById = new HashMap<Long, RelatedRules>();
    private final Map<Long, RelatedRules> classifierUsingRulesById    = new HashMap<Long, RelatedRules>();
    private final Map<Long, RelatedRules> rulesByRelatedElementId     = new HashMap<Long, RelatedRules>();
    private final RelatedRules            rulesByType                 = new RelatedRules(
                                                                              Long.MIN_VALUE);

    public ActiveMeasureRuleSet(Collection<RuleDescription> allRules) {
        this.allRules = new HashSet<RuleDescription>(allRules);
        for (RuleDescription rule : allRules) {
            rulesByType.addRule(rule);

            for (Long cdmId : rule.getRelatedEventLevelMeasureIds()) {
                addToMapOfRelatedRules(cdmId, rule, cdmRulesByCdmId);
            }
            for (Long measureId : rule.getRelatedMeasureIds()) {
                addToMapOfRelatedRules(measureId, rule, qmRulesByMeasureId);
            }
            for (Long classifierId : rule.getClassifierIdsUsed()) {
            	// Temporary fix: Filtering HCC rules with RuleType HCC and EVENT_FILTER(retract rules are tagged so). 
            	// This is because Classifier and HCC have the same WhenField.ID and the fix to WhenTypeUtility to filter    
                // based on whentype.type.contains cannot be pulled in yet (as the latest version of ruleappcommons is 
            	// on JDK 1.8) 
                if(rule.getRuleType() != RuleType.EVENT_FILTER && rule.getRuleType() != RuleType.HCC){
                    addToMapOfRelatedRules(classifierId, rule, classifierRulesById);
                    addToMapOfRelatedRules(classifierId, rule,
                            classifierUsingRulesById);
                }
            }
            for (Long classifierId : rule.getClassifierIdsCreated()) {
                    addToMapOfRelatedRules(classifierId, rule,
                            classifierCreationRulesById);
                    addToMapOfRelatedRules(classifierId, rule, classifierRulesById);
            }
            for (Long elementId : rule.getElementIdsUsed()) {
                addToMapOfRelatedRules(elementId, rule, rulesByRelatedElementId);
            }
        }
    }

    public Set<RuleDescription> getAllRules() {
        return allRules;
    }

    public Set<RuleDescription> getAllRulesByType(RuleType ruleType) {
        return rulesByType.getRules(ruleType);
    }

    private void addToMapOfRelatedRules(Long id, RuleDescription rule,
            Map<Long, RelatedRules> map) {
        RelatedRules relatedRules = map.get(id);
        if (relatedRules == null) {
            relatedRules = new RelatedRules(id);
            map.put(id, relatedRules);
        }
        relatedRules.addRule(rule);
    }

    public Map<Long, RelatedRules> getQaulityMeasuremRulesByMeasureId() {
        return qmRulesByMeasureId;
    }

    public Map<Long, RelatedRules> getCdmRulesByCdmId() {
        return cdmRulesByCdmId;
    }

    public Map<Long, RelatedRules> getClassifierRulesById() {
        return classifierRulesById;
    }

    public Map<Long, RelatedRules> getClassifierUsingRulesById() {
        return classifierUsingRulesById;
    }

    public Map<Long, RelatedRules> getClassifierCreationRulesById() {
        return classifierCreationRulesById;
    }

    public Map<Long, RelatedRules> getRulesByRelatedElementId() {
        return rulesByRelatedElementId;
    }
}

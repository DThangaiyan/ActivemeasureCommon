package net.ahm.careengine.activemeasure.rule;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.ahm.careengine.common.CECollectionsUtil;
import net.ahm.careengine.eventprocessing.engine.drools.rule.AbstractRuleDescription;

public class RuleDescription extends
        AbstractRuleDescription<RuleType, RuleDescription> {
    private Set<Long>       elementIdsUsed;
    private final Set<Long> relatedMeasureIds = new HashSet<Long>();
    private final Set<Long> relatedELMIds     = new HashSet<Long>();
    private Set<Long>       relatedFeedbackResponseIds;
    private Set<Long>       relatedFeedbackReferenceIds;
    private Set<Long>       relatedFeedbackFamilyIds;
    private Set<Long>       relatedHCCIds = new HashSet<Long>();
    private Set<Long>       relatedCCIds = new HashSet<Long>();
    private boolean         eventLevelMeasure = false;

    public RuleDescription(String ruleName, String packageName) {
        super(ruleName, packageName);
        setRuleType(RuleType.UNKNOWN);
    }

    public Set<Long> getElementIdsUsed() {
        return CECollectionsUtil.getNullSafeSet(elementIdsUsed);
    }

    public void addUsedElementId(Long elementIdToAdd) {
        if (elementIdsUsed == null) {
            elementIdsUsed = new HashSet<Long>();
        }
        if (elementIdToAdd != null) {
            elementIdsUsed.add(elementIdToAdd);
        }
    }

    public void addUsedElementIds(Iterable<Long> elementIdsToAdd) {
        for (Long elementId : elementIdsToAdd) {
            addUsedElementId(elementId);
        }
    }

    public Set<Long> getRelatedMeasureIds() {
        return relatedMeasureIds;
    }

    public void addRelatedMeasureId(Long measureId) {
        relatedMeasureIds.add(measureId);
    }

    public void addMultipleRelatedMeasureIds(Set<Long> measureIds) {
        relatedMeasureIds.addAll(measureIds);
    }

    @Override
    protected void postParentAssignement() {
        relatedMeasureIds.addAll(getParent().getRelatedMeasureIds());
        addUsedElementIds(getParent().getElementIdsUsed());
    }

    public Set<Long> getRelatedEventLevelMeasureIds() {
        return relatedELMIds;
    }

    public void addRelatedEventLevelMeasureId(Long elmId) {
        eventLevelMeasure = true;
        relatedELMIds.add(elmId);
    }

    public void addMultipleRelatedEventLevelMeasureIds(Set<Long> elmIds) {
        eventLevelMeasure = true;
        relatedELMIds.addAll(elmIds);
    }

    @Override
    protected void postChildAssignment(RuleDescription child) {
        if (RuleType.UNKNOWN.equals(getRuleType())) {
            setRuleType(RuleType.PARENT);
        }
    }

    public Set<Long> getRelatedFeedbackResponseIds() {
        return relatedFeedbackResponseIds == null ? Collections
                .<Long> emptySet() : relatedFeedbackResponseIds;
    }

    public Set<Long> getRelatedFeedbackReferenceIds() {
        return relatedFeedbackReferenceIds == null ? Collections
                .<Long> emptySet() : relatedFeedbackReferenceIds;
    }

    public Set<Long> getRelatedFeedbackFamilieids() {
        return relatedFeedbackFamilyIds == null ? Collections
                .<Long> emptySet() : relatedFeedbackFamilyIds;
    }

    public void addRelatedFeedbackResponseId(Long id) {
        if (relatedFeedbackResponseIds == null) {
            relatedFeedbackResponseIds = new HashSet<Long>();
        }
        if (id != null) {
            relatedFeedbackResponseIds.add(id);
        }
    }

    public void addRelatedFeedbackReferenceId(Long id) {
        if (relatedFeedbackReferenceIds == null) {
            relatedFeedbackReferenceIds = new HashSet<Long>();
        }
        if (id != null) {
            relatedFeedbackReferenceIds.add(id);
        }
    }

    public void addRelatedFeedbackFamilieId(Long id) {
        if(relatedFeedbackFamilyIds == null){
            relatedFeedbackFamilyIds = new HashSet<Long>();
        }
        if (id != null) {
            relatedFeedbackFamilyIds.add(id);
        }
    }

    public boolean isEventLevelMeasure() {
        return eventLevelMeasure;
    }

    public Set<Long> getRelatedCCIds() {
        return relatedCCIds;
    }

    public void addRelatedCCIds(Set<Long> ccIds) {
        relatedCCIds.addAll(ccIds);
    }
    
    public Set<Long> getRelatedHCCIds() {
        return relatedHCCIds;
    }

    public void addRelatedHCCIds(Set<Long> hccIds) {
        relatedHCCIds.addAll(hccIds);
    }
}

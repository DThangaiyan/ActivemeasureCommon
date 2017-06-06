package net.ahm.careengine.activemeasure.justification;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ahm.careengine.activemeasure.ActiveMeasureCommandConfiguration;
import net.ahm.careengine.activemeasure.ActiveMeasureCommandOutput;
import net.ahm.careengine.bean.PropertyChangeListenable;
import net.ahm.careengine.common.CECollectionsUtil;
import net.ahm.careengine.domain.classifier.Classifier;
import net.ahm.careengine.domain.classifier.ClassifierBuilder;
import net.ahm.careengine.domain.comorbidclinicalcondition.ComorbidClinicalConditionCategory;
import net.ahm.careengine.domain.comorbidclinicalcondition.HierarchicalClinicalConditionCategory;
import net.ahm.careengine.domain.comorbidclinicalcondition.HierarchicalClinicalConditionCategoryBuilder;
import net.ahm.careengine.domain.eligibility.CoverageType;
import net.ahm.careengine.domain.eligibility.PlanEligibility;
import net.ahm.careengine.domain.event.Label;
import net.ahm.careengine.domain.fact.ClassifiedFact;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.domain.impl.justification.DefaultRuleJustification;
import net.ahm.careengine.domain.impl.measure.active.DefaultFactLevelMeasureDenominator;
import net.ahm.careengine.domain.impl.measure.active.DefaultFactLevelMeasureNumerator;
import net.ahm.careengine.domain.justification.Justifiable;
import net.ahm.careengine.domain.justification.RuleType;
import net.ahm.careengine.domain.measures.active.ActiveMeasureBuilder;
import net.ahm.careengine.domain.measures.active.ActiveMeasureRuleContext;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureBuilder;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.measures.active.impl.MemberActiveMeasure;
import net.ahm.careengine.domain.member.MemberInfo;
import net.ahm.careengine.domain.temporal.ContiguousDays;
import net.ahm.careengine.eventprocessing.engine.drools.DroolsEventListener;
import net.ahm.careengine.util.OverlappedEvents;

import org.apache.log4j.Logger;
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
import org.drools.runtime.rule.Activation;

public class ActiveMeasureJustificationListener implements DroolsEventListener {
    private static final Logger                       LOGGER                              = Logger.getLogger(ActiveMeasureJustificationListener.class);

    @SuppressWarnings("unchecked")
    private static final Set<Class<? extends Object>> CLASSES_TO_IGNORE_IN_JUSTIFICATIONS = CECollectionsUtil
                                                                                                  .<Class<? extends Object>> unmodifiableSet(
                                                                                                          Date.class,
                                                                                                          DefaultFactLevelMeasureDenominator.class,
                                                                                                          DefaultFactLevelMeasureNumerator.class,
                                                                                                          MemberActiveMeasure.class);

    private final List<DefaultRuleJustification>      currentRuleJustifications           = new ArrayList<DefaultRuleJustification>();
    private String                                    currentRuleName                     = null;
    private String                                    currentRulePackage                  = null;
    private final ActiveMeasurePropertyChangeListener amPropChangeListener                = new ActiveMeasurePropertyChangeListener();
    private final List<PropertyChangeListenable>      propChangeListenables               = new ArrayList<PropertyChangeListenable>();
    private final ActiveMeasureCommandOutput          output;

    public ActiveMeasureJustificationListener(
            ActiveMeasureCommandOutput output,
            ActiveMeasureCommandConfiguration config) {
        this.output = output;
        registerPropertyChangeListener(config.getClassifierBuilder());
        registerPropertyChangeListener(config.getActiveMeasureBuilder());
        registerPropertyChangeListener(config.getFactLevelMeasureBuilder());
        registerPropertyChangeListener(config
                .getHierarchicalClinicalConditionCategoryBuilder());
    }

    @Override
    public void activationCancelled(ActivationCancelledEvent event) {
        //do nothing
    }

    @Override
    public void activationCreated(ActivationCreatedEvent event) {
        //do nothing
    }

    @Override
    public void afterActivationFired(AfterActivationFiredEvent event) {
        if (!currentRuleJustifications.isEmpty()) {
            Activation activation = event.getActivation();
            List<Fact> facts = extractFacts(activation);
            facts = dedupeFacts(facts);
            for (DefaultRuleJustification ruleJustification : currentRuleJustifications) {
                ruleJustification.setFacts(facts);
                if (RuleType.ELIGIBILITYEXCLUSION
                        .isMatchingRule(ruleJustification)) {
                    attachEligibilityRecords(ruleJustification);
                }
            }

            currentRuleJustifications.clear();
        }

        currentRuleName = null;
        currentRulePackage = null;
    }

    private List<Fact> dedupeFacts(List<Fact> facts) {
        if (facts == null || facts.size() < 2) {
            return facts;
        }

        Map<Fact, Fact> deduper = new IdentityHashMap<Fact, Fact>(facts.size());
        List<Fact> dedupedFacts = new ArrayList<Fact>(facts.size());
        for (Fact fact : facts) {
            if (deduper.put(fact, fact) == null) {
                dedupedFacts.add(fact);
            }
        }
        return dedupedFacts;
    }

    private List<Fact> extractFacts(Activation activation) {
        List<Fact> facts = new ArrayList<Fact>(activation.getObjects().size());
        List<Object> excludeObjects = new ArrayList<Object>();
        //boolean hasOverlappedEvent = false;
        for (Object obj : activation.getObjects()) {
            if (obj instanceof OverlappedEvents) {
                //hasOverlappedEvent = true;
                OverlappedEvents overlapEvent = (OverlappedEvents) obj;

                excludeObjects.add(overlapEvent.getAllScannedPrimaryEvents());
                excludeObjects.add(overlapEvent.getAllScannedSecondaryEvents());
                excludeObjects.add(overlapEvent.getPrimaryEvent());
                excludeObjects.add(overlapEvent.getSecondaryEvent());
                //break;
            } else if (obj instanceof Collection<?>) {
                Collection<?> collection = (Collection<?>) obj;
                for (Object innerObj : collection) {
                    if (innerObj instanceof OverlappedEvents) {
                        //hasOverlappedEvent = true;
                        OverlappedEvents overlapEvent = (OverlappedEvents) innerObj;

                        excludeObjects.add(overlapEvent.getAllScannedPrimaryEvents());
                        excludeObjects.add(overlapEvent.getAllScannedSecondaryEvents());
                        excludeObjects.add(overlapEvent.getPrimaryEvent());
                        excludeObjects.add(overlapEvent.getSecondaryEvent());
                        //break OUTER;
                    } else {
                        break;
                    }
                }
            }
        }

        for (Object obj : activation.getObjects()) {
            if (!excludeObjects.contains(obj))
                verifyAndAddFact(facts, obj);
        }
        return facts;
    }

    private void verifyAndAddFact(List<Fact> facts, Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof Collection<?>) {
            Collection<?> objs = (Collection<?>) obj;
            if (objs.isEmpty()) {
                return;
            }
            for (Object collectionObj : objs) {
                verifyAndAddFact(facts, collectionObj);
            }
            return;
        }
        if (!(obj instanceof Serializable)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Justification Ignored due to Serialization issue: rule "
                        + currentRulePackage
                        + "."
                        + currentRuleName
                        + " : "
                        + obj.getClass().getName());
            }
        }

        if (isIgnorable(obj)) {
            return;
        } else if (obj instanceof Classifier) {
            // JustifiedClassifier contains the real justification. Currently we
            // don't store justification independently for Classifier.
            // Hence keeping just a pointer in the form of classifier would not
            // capture complete justification tree.
            JustifiedClassifier jc = output
                    .getJustifiedClassifier((Classifier) obj);
            facts.add(jc);
            return;
        }  else if(obj instanceof ClassifiedFact){
            facts.add(((ClassifiedFact) obj).getFact());
            return;
        } else if (obj instanceof Fact) {
            facts.add((Fact) obj);
            return;
        } else if (obj instanceof HierarchicalClinicalConditionCategory) {
            // JustifiedHierarchicalClinicalConditionCategory contains the real
            // justification. Currently we
            // don't store justification independently for
            // HierarchicalClinicalConditionCategory.
            // Hence keeping just a pointer in the form of
            // hierarchicalClinicalConditionCategory would not
            // capture complete justification tree.
            JustifiedHierarchicalClinicalConditionCategory jc = output
                    .getJustifiedHierarchicalClinicalConditionCategory((HierarchicalClinicalConditionCategory) obj);
            facts.add(jc);
            return;

        } else if (obj instanceof ComorbidClinicalConditionCategory) {
            JustifiedComorbidClinicalConditionCategory jc = output
                    .getJustifiedComorbidClinicalConditionCategory((ComorbidClinicalConditionCategory) obj);

            facts.add(jc);
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            Class<? extends Object> classofObject = obj.getClass();
            if (!CLASSES_TO_IGNORE_IN_JUSTIFICATIONS.contains(classofObject)) {
                LOGGER.debug("Justification Ignored: rule "
                        + currentRulePackage + "." + currentRuleName + " : "
                        + classofObject.getName());
            }
        }
    }

    private boolean isIgnorable(Object obj) {
        return obj instanceof ActiveMeasureRuleContext;
    }

    @Override
    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        //do nothing
    }

    @Override
    public void afterRuleFlowGroupDeactivated(
            RuleFlowGroupDeactivatedEvent event) {
        //do nothing
    }

    @Override
    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(event.getClass().getName() + " : "
                    + event.getAgendaGroup().getName());
        }
    }

    @Override
    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(event.getClass().getName() + " : "
                    + event.getAgendaGroup().getName());
        }
    }

    @Override
    public void beforeActivationFired(BeforeActivationFiredEvent event) {
        currentRuleJustifications.clear();
        Rule rule = event.getActivation().getRule();
        currentRuleName = rule.getName();
        currentRulePackage = rule.getPackageName();
    }

    @Override
    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        //do nothing
    }

    @Override
    public void beforeRuleFlowGroupDeactivated(
            RuleFlowGroupDeactivatedEvent event) {
        //do nothing
    }

    @Override
    public void objectInserted(ObjectInsertedEvent event) {
        Object insertedObject = event.getObject();
        registerPropertyChangeListener(insertedObject);
        if (insertedObject instanceof ComorbidClinicalConditionCategory) {
            ComorbidClinicalConditionCategory cc = (ComorbidClinicalConditionCategory) insertedObject;
            DefaultRuleJustification ruleJustification = new DefaultRuleJustification();
            ruleJustification.setRuleType(RuleType.getRuleTypeStringForProperty(
ComorbidClinicalConditionCategory.PROR_CREATE_COMORBIDCLINICALCONDITIONCATEGORY));
            ruleJustification.setRuleName(currentRuleName);
            ruleJustification.setRulePackage(currentRulePackage);
            currentRuleJustifications.add(ruleJustification);
            output.addJustifiedComorbidClinicalConditionCategory(cc, ruleJustification);
        } 
    }

    private void registerPropertyChangeListener(Object object) {
        if (object instanceof PropertyChangeListenable) {
            PropertyChangeListenable pcl = (PropertyChangeListenable) object;
            pcl.addPropertyChangeListener(amPropChangeListener);
            propChangeListenables.add(pcl);
        }
    }

    @Override
    public void objectRetracted(ObjectRetractedEvent event) {
        //do nothing
    }

    @Override
    public void objectUpdated(ObjectUpdatedEvent event) {
        // do nothing
    }

    public void dispose() {
        for (PropertyChangeListenable listenable : propChangeListenables) {
            listenable.removePropertyChangeListener(amPropChangeListener);
        }
    }

    @SuppressWarnings("unchecked")
    private void attachEligibilityRecords(DefaultRuleJustification rj) {
        Collection<Fact> facts = rj.getFacts();
        if (facts == null) {
            return;
        }

        MemberInfo<Label> minfo = null;
        EnumSet<CoverageType> coverages = EnumSet.noneOf(CoverageType.class);
        for (Fact fact : facts) {
            if (fact instanceof MemberInfo) {
                minfo = (MemberInfo<Label>) fact;
                //break;
            } else if (fact instanceof ContiguousDays<?>) {
                ContiguousDays<?> cd = (ContiguousDays<?>) fact;
                Object label = cd.getLabel();
                if (label instanceof CoverageType) {
                    coverages.add((CoverageType) label);
                }
            }
        }

        if (minfo == null) {
            return;
        }

        for (CoverageType coverage : coverages) {
            Collection<PlanEligibility> eligRecords = minfo
                    .getAttribute(coverage.key());

            if (eligRecords == null) {
                continue;
            }

            for (PlanEligibility elig : eligRecords) {
                elig.addTo(facts);
            }
        }
    }

    private class ActiveMeasurePropertyChangeListener implements
            PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String propName = event.getPropertyName();

            RuleType ruleType = RuleType.getRuleTypeForProperty(propName);
            if (ruleType != null) {
                DefaultRuleJustification ruleJustification = new DefaultRuleJustification();
                ruleJustification.setRuleType(ruleType.getRuleTypeString());
                ruleJustification.setRuleName(currentRuleName);
                ruleJustification.setRulePackage(currentRulePackage);
                currentRuleJustifications.add(ruleJustification);

                Object source = event.getSource();
                if (source instanceof ActiveMeasureBuilder
                        || source instanceof FactLevelMeasureBuilder
                        || (ruleType == RuleType.NUMERATOR && source instanceof FactLevelMeasureDenominator)) {
                    if(event.getNewValue() instanceof Justifiable){
                        Justifiable justifiable = (Justifiable) event.getNewValue();
                        justifiable.addJustification(ruleJustification);
                    }
                } else if (source instanceof Justifiable) {
                    Justifiable measure = (Justifiable) source;
                    measure.addJustification(ruleJustification);
                } else if (source instanceof ClassifierBuilder) {
                    Classifier classifier = (Classifier) event.getNewValue();
                    output.addClassifierJustification(classifier,
                            ruleJustification);
                } else if (source instanceof HierarchicalClinicalConditionCategoryBuilder) {
                    HierarchicalClinicalConditionCategory hcc = (HierarchicalClinicalConditionCategory) event
                            .getNewValue();
                    output.addHCCJustification(hcc, ruleJustification);
                }
            }
        }
    }
}

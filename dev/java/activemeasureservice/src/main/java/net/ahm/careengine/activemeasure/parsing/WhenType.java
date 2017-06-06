package net.ahm.careengine.activemeasure.parsing;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.ahm.careengine.activemeasure.rule.RuleDescription;
import net.ahm.careengine.activemeasure.rule.RuleType;
import net.ahm.careengine.common.CECollectionsUtil;
import net.ahm.careengine.eventprocessing.engine.drools.parser.WhenFieldIF;
import net.ahm.careengine.eventprocessing.engine.drools.parser.WhenTypeIF;
import net.ahm.careengine.eventprocessing.engine.drools.parser.WhenTypeUtility;

import org.drools.lang.descr.AndDescr;

public enum WhenType implements WhenTypeIF<RuleType, RuleDescription> {
    ACTIVE_MEASURE(
            "ActiveMeasure",
            CECollectionsUtil
                    .unmodifiableSet(
                            RuleParseCommand.NEW_DENOMINATOR_ACTIVEMEASURE,
                            RuleParseCommand.NEW_DENOMINATOR_ACTIVEMEASURE_WITH_ALTERNATE_START_DATE,
                            RuleParseCommand.NEW_DENOMINATOR_ACTIVEMEASURE_WITH_ALTERNATE_END_DATE,
                            RuleParseCommand.NEW_DENOMINATOR_ACTIVEMEASURE_WITH_ALTERNATE_START_AND_END_DATE),
            Collections.singleton(WhenField.MEASURE_ID)) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            outputRuleDesc.addMultipleRelatedMeasureIds(ids);
        }
    },
    CLASSIFIERS_USED("Classifier", Collections.singleton(WhenField.ID)) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            outputRuleDesc.addMultipleUsedClassifierId(ids);
        }
    },
    CLASSIFIERS_CREATED("Classifier", CECollectionsUtil.unmodifiableSet(
            RuleParseCommand.GET_CLASSIFIER,
            RuleParseCommand.GET_CLASSIFIER_FOR_QUALITYMEASURE), null) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            outputRuleDesc.addMultipleCreatedClassifierIds(ids);
        }
    },
    MEMBER_INFO("MemberInfo", null) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
           // do nothing
        }
    },
    FLM_DENOMINATOR(
            "FactLevelMeasureDenominator",
            CECollectionsUtil
                    .unmodifiableSet(
            RuleParseCommand.NEW_DENOMINATOR_FACT_LEVEL_MEASURE_DENOMIATOR,
            RuleParseCommand.NEW_DENOMINATOR_FLM_DENOMIATOR_WITH_PRIMARY_FACT),
            Collections.singleton(WhenField.MEASURE_ID)) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            outputRuleDesc.addMultipleRelatedEventLevelMeasureIds(ids);
        }
    },
    FLM_NUMERATOR("FactLevelMeasureNumerator", Collections
            .singleton(WhenField.MEASURE_ID)) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            // do nothing
        }
    },
    PROCEDURE_EVENT("ProcedureEvent", false, Collections
            .singleton(WhenField.ELEMENTS)) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            outputRuleDesc.addUsedElementIds(ids);
        }
    },
    DIAGNOSTIC_EVENT("DiagnosticEvent", false, Collections
            .singleton(WhenField.ELEMENTS)) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            outputRuleDesc.addUsedElementIds(ids);
        }
    },
    LAB_EVENT("LabEvent", false, Collections.singleton(WhenField.ELEMENTS)) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            outputRuleDesc.addUsedElementIds(ids);
        }
    },
    LAB_RESULT_EVENT("LabResultEvent", false, Collections
            .singleton(WhenField.ELEMENTS)) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            outputRuleDesc.addUsedElementIds(ids);
        }
    },
    DRUG_EVENT("DrugEvent", false, Collections.singleton(WhenField.ELEMENTS)) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            outputRuleDesc.addUsedElementIds(ids);
        }
    },
    PATIENT_DERIVED_EVENT("PatientDerivedEvent", false, Collections
            .singleton(WhenField.ELEMENTS)) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            outputRuleDesc.addUsedElementIds(ids);
        }
    },
    BASE_ADT_EVENT("BaseAdmissionDischardTransferEvent",
            WhenField.BASE_ADT_FIELDS) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            outputRuleDesc.addUsedElementIds(ids);
        }
    },
    CLAIM_HEADER("ClaimHeader", WhenField.CLAIM_HEADER_FIELDS) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            outputRuleDesc.addUsedElementIds(ids);
        }
    },
    HIE_ENCOUNTER("HieEncounter", WhenField.HIE_ENCOUNTER_FIELDS){
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            outputRuleDesc.addUsedElementIds(ids);
        }
    },
    COLLECTION("java.util.Collection", true, Collections
            .singleton(WhenField.ELEMENTS)) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            outputRuleDesc.addUsedElementIds(ids);
        }
    },
    FEEDBACK_FAMILIES("Feedback", true, Collections
            .singleton(WhenField.FEEDBACK_FAMILIES)) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            for (Long id : ids) {
                outputRuleDesc.addRelatedFeedbackFamilieId(id);
            }
        }
    },
    FEEDBACK_RESPONSES("Feedback", true, Collections
            .singleton(WhenField.FEEDBACK_RESPONSES)) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            for (Long id : ids) {
                outputRuleDesc.addRelatedFeedbackResponseId(id);
            }
        }
    },
    FEEDBACK_REFERENCE_ID("Feedback", false, Collections
            .<WhenFieldIF> singleton(WhenField.FEEDBACK_REFERENCE_ID)) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            for (Long id : ids) {
                outputRuleDesc.addRelatedFeedbackReferenceId(id);
            }
        }
    },
    CC(
            "ComorbidClinicalConditionCategory",
            CECollectionsUtil
                    .unmodifiableSet(
            RuleParseCommand.SET_COMORBID_CLINICAL_CONDTION_CATEGORY),
            null) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            outputRuleDesc.addRelatedCCIds(ids);
        }
    },
    HCC(
            "HierarchicalClinicalConditionCategory",
            CECollectionsUtil
                    .unmodifiableSet(
            RuleParseCommand.GET_HIERARCHICAL_CLINICAL_CONDTION_CATEGORY,
            RuleParseCommand.GET_HIERARCHICAL_CLINICAL_CONDTION_CATEGORY_WITH_MEASURETYPE),
            null) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            outputRuleDesc.addRelatedHCCIds(ids);
        }
    },
    UNKNOWN("Unknown", null) {
        @Override
        public void addToOutputDesc(RuleDescription outputRuleDesc,
                Set<Long> ids) {
            // do nothing
        }
    };

    private final String                  type;
    private final boolean                 collect;
    private final Collection<WhenFieldIF> whenFields;
    private final Collection<String>      fieldNames;
    private final Collection<String>      possibleConsequenceStringStartDelimiters;

    @Override
    public Collection<String> getFieldNames() {
        return fieldNames;
    }

    @Override
    public boolean isCollect() {
        return collect;
    }

    @Override
    public boolean isUsedInConsequence() {
        return !possibleConsequenceStringStartDelimiters.isEmpty();
    }

    @Override
    public Collection<String> getPossibleConsequenceStringStartDelimiters() {
        return possibleConsequenceStringStartDelimiters;
    }

    private WhenType(String type, Collection<? extends WhenFieldIF> fieldNames) {
        this(type, false, null, fieldNames);
    }

    private WhenType(String type,
            Collection<String> possibleConsequenceStringStartDelimiters,
            Collection<? extends WhenFieldIF> fieldNames) {
        this(type, false, possibleConsequenceStringStartDelimiters, fieldNames);
    }

    private WhenType(String type, boolean collect,
            Collection<? extends WhenFieldIF> whenFields) {
        this(type, collect, null, whenFields);
    }

    @SuppressWarnings("unchecked")
    private WhenType(String type, boolean collect,
            Collection<String> possibleConsequenceStringStartDelimiters,
            Collection<? extends WhenFieldIF> whenFields) {
        this.type = type;
        this.whenFields = whenFields != null ? (Collection<WhenFieldIF>) whenFields
                : Collections
                .<WhenFieldIF> emptySet();
		final Set<String> tempFieldNameSet = new HashSet<String>(
                this.whenFields.size());
        for (WhenFieldIF wField : this.whenFields) {
            tempFieldNameSet.add(wField.getFieldName());
        }
        this.fieldNames = Collections.unmodifiableSet(tempFieldNameSet);
        this.collect = collect;
        this.possibleConsequenceStringStartDelimiters = possibleConsequenceStringStartDelimiters != null ? possibleConsequenceStringStartDelimiters
                : Collections.<String> emptySet();
    }

    @Override
    public String getType() {
        return type;
    }

    public static WhenType getWhenTypeFromInput(String inputType) {
        return WhenTypeUtility.getWhenTypeFromInput(inputType, values(),
                UNKNOWN);
    }

    @Override
    public Collection<WhenFieldIF> getFields() {
        return whenFields;
    }

    public void addToOutputDesc(RuleDescription outputRuleDesc,
            String consequenceStr, AndDescr lhs) {
        WhenTypeUtility.addToOutputDesc(outputRuleDesc, consequenceStr, lhs,
                this);
    }

    @Override
    public abstract void addToOutputDesc(RuleDescription outputRuleDesc,
            Set<Long> ids);
}

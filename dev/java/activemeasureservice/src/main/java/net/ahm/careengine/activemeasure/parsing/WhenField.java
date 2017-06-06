package net.ahm.careengine.activemeasure.parsing;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import net.ahm.careengine.eventprocessing.engine.drools.parser.WhenFieldIF;
import net.ahm.careengine.eventprocessing.engine.drools.parser.WhenFieldUtility;

public enum WhenField implements WhenFieldIF {
    ELEMENTS("elements", true), //
    PRINCIPAL_DIAGNOSIS_ELEMENTS("principalDiagnosisElements", true), //
    RELATED_DIAGNOSTIC_EVENT_ELEMENTS("relatedDiagnosticEventElements", true), //
    ALL_DIAGNOSTIC_EVENT_ELEMENTS("allDiagnosticEventElements", true), //
    RELATED_PROCEDURE_EVENT_ELEMENTS("relatedProcedureEventElements", true), //
    ADMIT_DIAGNOSIS_ELEMENTS("admitDiagnosisElements", true), //
    RELATED_DRUG_EVENT_ELEMENTS("relatedDrugEventElements", true), //
    RELATED_LAB_EVENT_ELEMENTS("relatedLabEventElements", true), //
    MEASURE_ID("measureId", false), //
    ID("id", false), //
    FEEDBACK_FAMILIES("feedbackFamilies", true), //
    FEEDBACK_RESPONSES("feedbackResponses", true), //
    FEEDBACK_REFERENCE_ID("feedbackReferenceId", false);

    public static final Set<WhenField> BASE_ADT_FIELDS      = Collections
                                                                    .unmodifiableSet(EnumSet
                                                                            .of(PRINCIPAL_DIAGNOSIS_ELEMENTS,
                                                                                    RELATED_DIAGNOSTIC_EVENT_ELEMENTS,
                                                                                    ALL_DIAGNOSTIC_EVENT_ELEMENTS,
                                                                                    RELATED_PROCEDURE_EVENT_ELEMENTS));
    public static final Set<WhenField> CLAIM_HEADER_FIELDS;
    public static final Set<WhenField> HIE_ENCOUNTER_FIELDS = BASE_ADT_FIELDS;

    static {
        Set<WhenField> claimHeaderSet = EnumSet.noneOf(WhenField.class);
        claimHeaderSet.addAll(BASE_ADT_FIELDS);
        claimHeaderSet.add(ADMIT_DIAGNOSIS_ELEMENTS);
        claimHeaderSet.add(RELATED_DRUG_EVENT_ELEMENTS);
        claimHeaderSet.add(RELATED_LAB_EVENT_ELEMENTS);
        CLAIM_HEADER_FIELDS = Collections.unmodifiableSet(claimHeaderSet);
    }

    private final String            fieldName;
    private final boolean           collection;
    private final String            startDelimiterIn;
    private final String            startDelimiterEquals;

    private WhenField(String fieldName, boolean collection) {
        this.fieldName = fieldName;
        this.collection = collection;
        startDelimiterIn = WhenFieldUtility.appendInWithLeadingSpace(fieldName);
        startDelimiterEquals = WhenFieldUtility
                .appendEqualsWithLeadingSpace(fieldName);
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public boolean isCollection() {
        return collection;
    }

    @Override
    public String getStartDelimiterIn() {
        return startDelimiterIn;
    }

    @Override
    public String getStartDelimiterEquals() {
        return startDelimiterEquals;
    }
}

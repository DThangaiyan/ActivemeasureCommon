package net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_Unplanned_Admission_For_Patients_with_Chronic_Conditions;

import java.util.Date;
import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ClassifierCreationTemplate;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.adt.BaseAdmissionDischardTransferEvent;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.interfaces.ClassLevelConditionAssertion;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

public abstract class AbstractLabelTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    private final String  ruleName;
    private final int     minCollectionSize;
    private final int     expectedElementId;
    private final long    classifierIdToCreate;
    private final boolean inpatient;
    private final boolean outpatient;

    public AbstractLabelTest(String ruleName, int minCollectionSize,
            int expectedElementId, boolean inpatient, boolean outpatient,
            long classifierIdToCreate) {
        this.ruleName = ruleName;
        this.minCollectionSize = minCollectionSize;
        this.expectedElementId = expectedElementId;
        this.inpatient = inpatient;
        this.outpatient = outpatient;
        this.classifierIdToCreate = classifierIdToCreate;
    }

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate adtEventTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        BaseAdmissionDischardTransferEvent.class, null,
                        createADTAssertions());

        DefaultStandardRuleTemplate rule = ActiveMeasureUtilFunctions
                .createDefaultStandardRuleTemplate(ruleName,
                        new ClassifierCreationTemplate(classifierIdToCreate),
                        ActiveMeasureUtilFunctions
                                .createCollectionEvaluationFragmentTemplate(
                                        ActiveMeasureUtilFunctions
                                                .createCollectionSize(null,
                                                        minCollectionSize),
                                        adtEventTemplate));
        rule.setSalience(100);
        return rule;
    }

    protected ClassLevelConditionAssertion[] createADTAssertions() {
        int size = 2;
        if (inpatient || outpatient) {
            size++;
        }

        ClassLevelConditionAssertion[] results = new ClassLevelConditionAssertion[size];
        results[0] = ActiveMeasureUtilFunctions
                .createAnyAttributeSingleComparisonFragmentTemplate(
                        new AttributeFragmentTemplate(
                                "allDiagnosticEventElements", TypeDescription
                                        .getTypeDescription(Set.class,
                                                Integer.class)),
                        UtilFunctions
                                .createIntegerLiteralFragmentTemplate(expectedElementId),
                        CommonOperators.CONTAINS, null);

        results[1] = ActiveMeasureUtilFunctions
                .createAnyAttributeSingleComparisonFragmentTemplate(
                        new AttributeFragmentTemplate("endDate",
                                TypeDescription.getTypeDescription(Date.class)),
                        ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition.MEASUREMENT_END_DATE
                                .getNamedVariableLiteralFragmentTemplate(),
                        CommonOperators.LESS_THAN_OR_EQUAL_TO, null);

        if (inpatient && outpatient) {
            results[2] = ActiveMeasureUtilFunctions
                    .createMultipleAttributeEvaluationFragmentTemplate(
                            Connector.OR, createInpatientAssertion(),
                            createOutpatientAssertion());
        } else if (inpatient) {
                results[2] = createInpatientAssertion();
        } else if (outpatient) {
                results[2] = createOutpatientAssertion();
        }

        return results;
    }

    private AnyAttributeSingleComparisonFragmentTemplate<Object> createOutpatientAssertion() {
        return ActiveMeasureUtilFunctions
                .createAnyAttributeSingleComparisonFragmentTemplate(
                        new AttributeFragmentTemplate("outpatient",
                                TypeDescription
                                        .getTypeDescription(Boolean.TYPE)),
                        ActiveMeasureUtilFunctions
                                .createBooleanLiteralExpressionFragmentTemplate(true),
                        CommonOperators.EQUAL_TO, null);
    }

    private AnyAttributeSingleComparisonFragmentTemplate<Object> createInpatientAssertion() {
        return ActiveMeasureUtilFunctions
                .createAnyAttributeSingleComparisonFragmentTemplate(
                        new AttributeFragmentTemplate("inpatient",
                                TypeDescription
                                        .getTypeDescription(Boolean.TYPE)),
                        ActiveMeasureUtilFunctions
                                .createBooleanLiteralExpressionFragmentTemplate(true),
                        CommonOperators.EQUAL_TO, null);
    }
}

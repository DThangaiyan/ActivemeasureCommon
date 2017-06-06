package net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_Unplanned_Admission_For_Patients_with_Chronic_Conditions;

import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassAttributeUpdaterTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NotExistenceExpression;
import net.ahm.rulesapp.templates.utils.TypeDescription;

public class NumExclPotentiallyPlannedProc extends
        AbstractActiveMeasuresRuleTemplateTest {

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate numAssertion = ActiveMeasureUtilFunctions
                .createFactLevelMeasureNumerator(DenomTest.MEASURE_ID);

        AnyAttributeSingleComparisonFragmentTemplate<?> primaryFactAssertion = ActiveMeasureUtilFunctions
                .createAnyAttributeSingleComparisonFragmentTemplate(
                        new AttributeFragmentTemplate(
                                ActiveMeasureUtilConstants.FACT_LEVEL_NUMERATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                                TypeDescription.getTypeDescription(Fact.class)),
                        new NotExistenceExpression(),
                        CommonOperators.NOT_EQUAL_TO, "$primaryFact");
        numAssertion.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments().add(primaryFactAssertion);

        FromEvaluationFragmentTemplate claimHeaderAssertion = new FromEvaluationFragmentTemplate(
                ActiveMeasureUtilFunctions
                        .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                                ClaimHeader.class,
                                null,
                                ActiveMeasureUtilFunctions
                                        .createAnyAttributeSingleComparisonFragmentTemplate(
                                                new AttributeFragmentTemplate(
                                                        "relatedProcedureEventElements",
                                                        TypeDescription
                                                                .getTypeDescription(
                                                                        Set.class,
                                                                        Integer.class)),
                                                ActiveMeasureUtilFunctions
                                                        .createIntegerLiteralFragmentTemplate(8831),
                                                CommonOperators.CONTAINS, null),
                                ActiveMeasureUtilFunctions
                                        .createAnyAttributeSingleComparisonFragmentTemplate(
                                                new AttributeFragmentTemplate(
                                                        "principalDiagnosisElements",
                                                        TypeDescription
                                                                .getTypeDescription(
                                                                        Set.class,
                                                                        Integer.class)),
                                                ActiveMeasureUtilFunctions
                                                        .createIntegerLiteralFragmentTemplate(7940),
                                                CommonOperators.NOT_CONTAINS,
                                                null)),
                primaryFactAssertion.getVariableExpression());

        AnyClassAttributeUpdaterTemplate action = ActiveMeasureUtilFunctions
                .createAnyClassAttributeUpdaterTempalate(numAssertion
                        .getVariableExpression(), ActiveMeasureUtilFunctions
                        .createBooleanAttributeSettingInstance(
                                "excludedFromNumerator", true));

        return ActiveMeasureUtilFunctions.createDefaultStandardRuleTemplate(
                "NUM_EXCL_POTENTIALLY_PLANNED_PROC", action, numAssertion,
                claimHeaderAssertion);
    }
}

package net.ahm.activemeasure.cdm.year2015.ACO.Ambulatory_Care_Sensitive_Conditions_Admissions_For_Heart_Failure;

import java.util.Date;
import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureNumerator;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NotExistenceExpression;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule NUM_EXCL_ADMISSIONS_HEART_FAILURE_SAME_DAY_DIFF_FACILITY
 */

//101.  | rule "NUM_EXCL_ADMISSIONS_HEART_FAILURE_SAME_DAY_DIFF_FACILITY"
//102.  |     dialect "mvel"
//103.  |     when
//104.  |         $flmNum : FactLevelMeasureNumerator( measureId == 127 , $primaryOrigFact : primaryFact != null )
//105.  |         $indexEvent : ClaimHeader( $indexEventStartDate : startDate != null , $medicalFacility : servicingOrgId != null , admitDiagnosisElements contains 7932 ) from $primaryOrigFact
//106.  |         ClaimHeader( this != $indexEvent , startDate == $indexEventStartDate , servicingOrgId != $medicalFacility , admitDiagnosisElements contains 7932 )
//107.  |     then
//108.  |         $flmNum.setExcludedFromNumerator( true );
//109.  | end

public class NumExclAdmissionsHeartFailureSameDayDiffFacilityTest extends
        AbstractActiveMeasuresRuleTemplateTest {
    private static int MEASURE_ID = 127;
    private static int ELEMENT_ID = 7932;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactTemplate = ActiveMeasureUtilFunctions
                .createFactLevelNumeratorPrimaryFactNullCheck();

        AnyClassMultipleAttributeEvaluationFragmentTemplate flmNumTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureNumerator.class,
                        "$flmNum",
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(MEASURE_ID),
                        primaryFactTemplate);

        AnyAttributeSingleComparisonFragmentTemplate<Date> indexEventStartDateTemplate = UtilFunctions
                .createAnyAttributeSingleComparisonFragmentTemplate(
                        new AttributeFragmentTemplate("startDate",
                                TypeDescription.getTypeDescription(Date.class)),
                        new NotExistenceExpression(),
                        CommonOperators.NOT_EQUAL_TO, "$indexEventStartDate");
        AnyAttributeSingleComparisonFragmentTemplate<Long> medicalFacilityTemplate = UtilFunctions
                .createAnyAttributeSingleComparisonFragmentTemplate(
                        new AttributeFragmentTemplate("servicingOrgId",
                                TypeDescription.getTypeDescription(Long.TYPE)),
                        new NotExistenceExpression(),
                        CommonOperators.NOT_EQUAL_TO, "$medicalFacility");
        AnyClassMultipleAttributeEvaluationFragmentTemplate indexEventTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ClaimHeader.class, "$indexEvent",
                        indexEventStartDateTemplate,
                        medicalFacilityTemplate,
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                "admitDiagnosisElements",
                                                TypeDescription
                                                        .getTypeDescription(
                                                                Set.class,
                                                                Integer.class)),
                                        UtilFunctions
                                                .createIntegerLiteralFragmentTemplate(ELEMENT_ID),
                                        CommonOperators.CONTAINS, null));

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "NUM_EXCL_ADMISSIONS_HEART_FAILURE_SAME_DAY_DIFF_FACILITY",
                        ActiveMeasureUtilFunctions
                                .createSetNumeratorExclusionToTrueTemplate(flmNumTemplate
                                        .getVariableExpression()),
                        flmNumTemplate,
                        new FromEvaluationFragmentTemplate(indexEventTemplate,
                                primaryFactTemplate.getVariableExpression()),
                        UtilFunctions
                                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                                        ClaimHeader.class,
                                        null,
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        new AttributeFragmentTemplate(
                                                                "this",
                                                                TypeDescription
                                                                        .getTypeDescription(ClaimHeader.class)),
                                                        indexEventTemplate
                                                                .getVariableExpression(),
                                                        CommonOperators.NOT_EQUAL_TO,
                                                        null),
                                        UtilFunctions
                                                .createDateAttributeAndVariableCheckFragment(
                                                        "startDate",
                                                        CommonOperators.EQUAL_TO,
                                                        indexEventStartDateTemplate
                                                                .getVariableExpression()),
                                        UtilFunctions
                                                .createObjectVariableCheckFragment(
                                                        "servicingOrgId",
                                                        CommonOperators.NOT_EQUAL_TO,
                                                        Long.TYPE,
                                                        medicalFacilityTemplate
                                                                .getVariableExpression()),
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        new AttributeFragmentTemplate(
                                                                "admitDiagnosisElements",
                                                                TypeDescription
                                                                        .getTypeDescription(
                                                                                Set.class,
                                                                                Integer.class)),
                                                        UtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(ELEMENT_ID),
                                                        CommonOperators.CONTAINS,
                                                        null)));
        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.Ambulatory_Care_Sensitive_Conditions_Admissions_For_Heart_Failure");
        return standardRuleInstance;
    }
}

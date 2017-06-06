package net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_30Day_Readmission_Skilled_Nursing_Facility;

import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureNumerator;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * This test case is mainly responsible for generate the JSON representation of
 * ACO35 rule for NUM_EXCL_OTHER_POTENTIAL_PROC_AND_NOT_PRINCIPAL_DIAG_CDM_ACUTE
 * 
 * This test case also generates the DRL version of ACO35 rule
 * NUM_EXCL_OTHER_POTENTIAL_PROC_AND_NOT_PRINCIPAL_DIAG_CDM_ACUTE
 * 
 * 
 * @author
 * 
 */
public class NumExclOtherPotentialProcAndNotPrincipalDiagCdmAcuteTest extends
        AbstractActiveMeasuresRuleTemplateTest {

//rule "NUM_EXCL_OTHER_POTENTIAL_PROC_AND_NOT_PRINCIPAL_DIAG_CDM_ACUTE"
//dialect "mvel"
//when
//    $flmNum : FactLevelMeasureNumerator( measureId == 128 )
//    ClaimHeader( principalDiagnosisElements excludes 7940 , relatedProcedureEventElements contains 8864 )
//then
//    $flmNum.setExcludedFromNumerator( true );
//end

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate flmNumeratorTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureNumerator.class,
                        ActiveMeasureUtilConstants.FACT_LEVEL_NUMERATOR,
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(128));

        DefaultStandardRuleTemplate rule = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "NUM_EXCL_OTHER_POTENTIAL_PROC_AND_NOT_PRINCIPAL_DIAG_CDM_ACUTE",
                        ActiveMeasureUtilFunctions
                                .createSetNumeratorExclusionToTrueTemplate(flmNumeratorTemplate
                                        .getVariableExpression()),
                        flmNumeratorTemplate,
                        UtilFunctions
                                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                                        ClaimHeader.class,
                                        null,
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        new AttributeFragmentTemplate(
                                                                "principalDiagnosisElements",
                                                                TypeDescription
                                                                        .getTypeDescription(
                                                                                Set.class,
                                                                                Integer.class)),
                                                        UtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(7940),
                                                        CommonOperators.NOT_CONTAINS,
                                                        null),
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        new AttributeFragmentTemplate(
                                                                "relatedProcedureEventElements",
                                                                TypeDescription
                                                                        .getTypeDescription(
                                                                                Set.class,
                                                                                Integer.class)),
                                                        UtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(8864),
                                                        CommonOperators.CONTAINS,
                                                        null)));
        rule.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_30Day_Readmission_Skilled_Nursing_Facility");
        return rule;
    }
}

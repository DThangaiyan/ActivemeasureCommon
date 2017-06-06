package net.ahm.activemeasure.cdm.Common;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.util.UtilFunctions;

//rule "COMMON_DENOM_EXCL_DIAG_HOSPICE_OR_CANCER_PSYCH_REHAB_OR_PROSTHESES_FROM_INDEX_EVENT"
//dialect "mvel"
//when
//    $flmDenom : FactLevelMeasureDenominator( measureId in ( 125, 128 ) , $primaryFact : primaryOriginationFact != null )
//    ClaimHeader( allDiagnosticEventElements contains 7935  || contains 7936  || contains 7937  || contains 8300 ) from $primaryFact
//then
//    $flmDenom.setExcludedFromDenominator( true );
//end

/**
 * Test case used to generate rule for
 * DENOM_EXCL_DIAG_CANCER_PSYCH_REHAB_OR_PROSTHESES_OR_HOSPICE
 * 
 * @author xsu
 * 
 */
public class DenomExclDiagHospiceOrCancerPsychRehabOrProstheseFromIndexEventTest
        extends AbstractActiveMeasuresRuleTemplateTest {

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactTemplate = ActiveMeasureUtilFunctions
                .createFactLevelDenominatorPrimaryFactNullCheck();

        AnyClassMultipleAttributeEvaluationFragmentTemplate denomTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR,
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(
                                        125, 128), primaryFactTemplate);

        AnyClassMultipleAttributeEvaluationFragmentTemplate claimHeaderTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ClaimHeader.class,
                        null,
                        UtilFunctions
                                .createMultipleAttributeEvaluationFragmentTemplate(
                                        Connector.OR,
                                        ActiveMeasureUtilFunctions
                                                .createElementContainsValueTemplate(
                                                        "allDiagnosticEventElements",
                                                        7935),
                                        ActiveMeasureUtilFunctions
                                                .createElementContainsValueTemplate(
                                                        "allDiagnosticEventElements",
                                                        7936),
                                        ActiveMeasureUtilFunctions
                                                .createElementContainsValueTemplate(
                                                        "allDiagnosticEventElements",
                                                        7937),
                                        ActiveMeasureUtilFunctions
                                                .createElementContainsValueTemplate(
                                                        "allDiagnosticEventElements",
                                                        8300)));

        DefaultStandardRuleTemplate rule = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "COMMON_DENOM_EXCL_DIAG_HOSPICE_OR_CANCER_PSYCH_REHAB_OR_PROSTHESES_FROM_INDEX_EVENT",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorExclusionToTrueTemplate(denomTemplate
                                        .getVariableExpression()),
                        denomTemplate,
                        new FromEvaluationFragmentTemplate(claimHeaderTemplate,
                                primaryFactTemplate.getVariableExpression()));
        rule.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.Common");
        return rule;
    }
}

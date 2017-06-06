package net.ahm.activemeasure.cdm.year2015.ACO.COPD_Or_Asthma_In_Adults;

import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

//rule "DENOM_COPD_AND_BRONCHITIS"
//dialect "mvel"
//when
//     ActiveMeasuresMemberInfo( ageAtMeasurementEndDate >= 40 , gender != null )
//     $claimHdr : ClaimHeader( principalDiagnosisElements contains 8833 , relatedDiagnosticEventElements contains 7931 , endDate <= measurementEndDate , endDate >= months12BeforeMeasurementEndDate )
//then
//    FactLevelMeasureDenominator flmDenominator= factLevelMeasureBuilder.newFactLevelMeasureDenominatorWithPrimaryFact(126, $claimHdr); insert(flmDenominator)
//end

public class DenomCopdAndBronchitisTest extends AbstractActiveMeasuresRuleTemplateTest {

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate claimHeaderCheck = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ClaimHeader.class,
                        "$claimHdr",
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                "principalDiagnosisElements",
                                                TypeDescription
                                                        .getTypeDescription(
                                                                Set.class,
                                                                Integer.class)),
                                        UtilFunctions
                                                .createIntegerLiteralFragmentTemplate(8833),
                                        CommonOperators.CONTAINS, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                "relatedDiagnosticEventElements",
                                                TypeDescription
                                                        .getTypeDescription(
                                                                Set.class,
                                                                Integer.class)),
                                        UtilFunctions
                                                .createIntegerLiteralFragmentTemplate(7931),
                                        CommonOperators.CONTAINS, null),
                        UtilFunctions
                                .createDateAttributeAndVariableCheckFragment(
                                        "endDate",
                                        CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                        ActiveMeasureUtilFunctions
                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MEASUREMENT_END_DATE)),
                        UtilFunctions
                                .createDateAttributeAndVariableCheckFragment(
                                        "endDate",
                                        CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                        ActiveMeasureUtilFunctions
                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MONTHS_12_BEFORE_MEASUREMENT_END_DATE)));

        DefaultStandardRuleTemplate ruleTemplate = UtilFunctions
                .createDefaultStandardRuleTemplate("DENOM_COPD_AND_BRONCHITIS",
                        ActiveMeasureUtilFunctions
                                .createFactLevelMeasureDenominatorCreationTemplate(
                                        126, claimHeaderCheck
                                                .getVariableExpression()),
                        ActiveMeasureUtilFunctions
                                .createActiveMeasuresMemberInfoFragment(40, 0,
                                        true, null),
                        claimHeaderCheck);
        ruleTemplate
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.COPD_Or_Asthma_In_Adults");
        return ruleTemplate;
    }
}

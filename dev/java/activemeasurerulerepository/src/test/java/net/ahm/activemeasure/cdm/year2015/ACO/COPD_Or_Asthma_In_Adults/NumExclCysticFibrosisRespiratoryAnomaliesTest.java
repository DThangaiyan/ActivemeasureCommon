package net.ahm.activemeasure.cdm.year2015.ACO.COPD_Or_Asthma_In_Adults;

import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

//rule "NUM_EXCL_CYSTIC_FIBROSIS_RESPIRATORY_ANOMALIES"
//dialect "mvel"
//when
//    $flmNum : FactLevelMeasureNumerator( measureId == 126 )
//    ClaimHeader( endDate <= measurementEndDate , allDiagnosticEventElements contains 8600 )
//then
//    $flmNum.setExcludedFromNumerator( true );
//end

public class NumExclCysticFibrosisRespiratoryAnomaliesTest extends AbstractActiveMeasuresRuleTemplateTest {

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate factLvlMeasuresMemberCheck = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        "$flmNum",
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(126));

        DefaultStandardRuleTemplate rule = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "NUM_EXCL_CYSTIC_FIBROSIS_RESPIRATORY_ANOMALIES",
                        ActiveMeasureUtilFunctions
                                .createSetNumeratorExclusionToTrueTemplate(factLvlMeasuresMemberCheck
                                        .getVariableExpression()),
                        factLvlMeasuresMemberCheck,
                        UtilFunctions
                                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                                        ClaimHeader.class,
                                        null,
                                        UtilFunctions
                                                .createDateAttributeAndVariableCheckFragment(
                                                        "endDate",
                                                        CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                                        ActiveMeasureUtilFunctions
                                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MEASUREMENT_END_DATE)),
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        new AttributeFragmentTemplate(
                                                                "allDiagnosticEventElements",
                                                                TypeDescription
                                                                        .getTypeDescription(
                                                                                Set.class,
                                                                                Integer.class)),
                                                        UtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(8600),
                                                        CommonOperators.CONTAINS,
                                                        null)));
        rule.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.COPD_Or_Asthma_In_Adults");
        return rule;
    }
}

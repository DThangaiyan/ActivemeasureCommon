package net.ahm.activemeasure.cdm.Common;

import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.FactLevelMeasureNumeratorCreationTemplate;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;

//rule "COMMON_NUM_UNPLANNED_CDM_ACUTE_DIAG"
//dialect "mvel"
//when
//     $flmDenom : FactLevelMeasureDenominator( measureId in ( 129, 130, 131 ) )
//     $claimHdr : ClaimHeader( allDiagnosticEventElements contains 7940 , endDate <= measurementEndDate , endDate >= months12BeforeMeasurementEndDate )
//then
//    FactLevelMeasureNumerator flmNumerator = $flmDenom.createFactLevelMeasureNumeratorWithPrimaryFact($claimHdr); insert(flmNumerator);
//end

public class CommonNumeratorUnplannedCdmActuteDiagTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate denomAssertion = ActiveMeasureUtilFunctions
                .createFactLevelMeasureDenominator(129, 130, 131);

        AnyClassMultipleAttributeEvaluationFragmentTemplate claimHeaderAssertion = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ClaimHeader.class,
                        "$claimHdr",
                        ActiveMeasureUtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                "allDiagnosticEventElements",
                                                TypeDescription
                                                        .getTypeDescription(
                                                                Set.class,
                                                                Integer.class)),
                                        ActiveMeasureUtilFunctions
                                                .createIntegerLiteralFragmentTemplate(7940),
                                        CommonOperators.CONTAINS, null),
                        ActiveMeasureUtilFunctions
                                .createDateAttributeAndVariableCheckFragment(
                                        "endDate",
                                        CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                        ActiveMeasureUtilFunctions
                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MEASUREMENT_END_DATE)),
                        ActiveMeasureUtilFunctions
                                .createDateAttributeAndVariableCheckFragment(
                                        "endDate",
                                        CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                        ActiveMeasureUtilFunctions
                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MONTHS_12_BEFORE_MEASUREMENT_END_DATE)));

        FactLevelMeasureNumeratorCreationTemplate numCreationTemplate = ActiveMeasureUtilFunctions
                .createFactLevelMeasureNumeratorCreationTemplate(
                        denomAssertion, claimHeaderAssertion);

        return ActiveMeasureUtilFunctions.createDefaultStandardRuleTemplate(
                "COMMON_NUM_UNPLANNED_CDM_ACUTE_DIAG", numCreationTemplate,
                denomAssertion, claimHeaderAssertion);
    }

}

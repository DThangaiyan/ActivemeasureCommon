package net.ahm.activemeasure.cdm.Common;

import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.diag.DiagnosticEvent;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;

//rule "COMMON_DENOM_EXCL_DIAG_HOSPICE"
//dialect "mvel"
//when
//    $flmDenom : FactLevelMeasureDenominator( measureId in ( 129, 130, 126, 127, 132 ) )
//    DiagnosticEvent( elements contains 8300 , endDate <= measurementEndDate , endDate >= months12BeforeMeasurementEndDate )
//then
//    $flmDenom.setExcludedFromDenominator( true );
//end

public class CommonDenomExclDiagHospiceTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate denomAssertion = ActiveMeasureUtilFunctions
                .createFactLevelMeasureDenominator(129, 130, 126, 127, 132);

        AnyClassMultipleAttributeEvaluationFragmentTemplate diagAssertion = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        DiagnosticEvent.class,
                        null,
                        ActiveMeasureUtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                "elements", TypeDescription
                                                        .getTypeDescription(
                                                                Set.class,
                                                                Integer.class)),
                                        ActiveMeasureUtilFunctions
                                                .createIntegerLiteralFragmentTemplate(8300),
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

        return ActiveMeasureUtilFunctions
                .createDefaultStandardRuleTemplate(
                        "COMMON_DENOM_EXCL_DIAG_HOSPICE",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorExclusionToTrueTemplate(denomAssertion
                                        .getVariableExpression()),
                        denomAssertion, diagAssertion);
    }
}

package net.ahm.activemeasure.cdm.Common;

import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.proc.ProcedureEvent;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;

//rule "COMMON_DENOM_EXCL_PROC_HOSPICE"
//dialect "mvel"
//when
//    $flmDenom : FactLevelMeasureDenominator( measureId in ( 129, 130, 126, 127, 132 ) )
//    ProcedureEvent( elements contains 2675 , endDate <= measurementEndDate , endDate >= months12BeforeMeasurementEndDate )
//then
//    $flmDenom.setExcludedFromDenominator( true );
//end

public class CommonDenomExclProcHospiceTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate denomAssertion = ActiveMeasureUtilFunctions
                .createFactLevelMeasureDenominator(129, 130, 126, 127, 132);

        AnyClassMultipleAttributeEvaluationFragmentTemplate procAssertion = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ProcedureEvent.class,
                        null,
                        ActiveMeasureUtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                "elements", TypeDescription
                                                        .getTypeDescription(
                                                                Set.class,
                                                                Integer.class)),
                                        ActiveMeasureUtilFunctions
                                                .createIntegerLiteralFragmentTemplate(2675),
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
                        "COMMON_DENOM_EXCL_PROC_HOSPICE",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorExclusionToTrueTemplate(denomAssertion
                                        .getVariableExpression()),
                        denomAssertion, procAssertion);
    }
}

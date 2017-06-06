package net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_30Day_Readmission_Skilled_Nursing_Facility;

import java.util.Date;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.DateDefinitionTemplate;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.bom.member.event.claim.ClaimType;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.ruleengine.DateTimeUnit;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.DateShiftFunction;
import net.ahm.rulesapp.util.UtilFunctions;

public class DenomAdmissionSnfWithin1DayTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    // private static final String admittingEvtForAcuteCare = ;

    // rule "DENOM_ADMISSION_SNF_WITHIN_1DAY"
    // dialect "mvel"
    // when
    //     ActiveMeasuresMemberInfo( ageAtMeasurementEndDate >= 65 )
    //     $admittingEventForAcuteCarePshyOrCAH : ClaimHeader( $admittingEventDischargeDate : endDate <= measurementEndDate , inpatient == true )
    //     $1DayAfterAdmittingEventDischargeDate : java.util.Date() from GlobalFunctions.getLaterDate($admittingEventDischargeDate, 1, DateTimeUnit.DAY)
    //     $snfAdmittingEvent : ClaimHeader( this != $admittingEventForAcuteCarePshyOrCAH , endDate <= measurementEndDate , startDate >= $admittingEventDischargeDate , startDate <= ( $1DayAfterAdmittingEventDischargeDate ) , claimType == ClaimType.SNF_NONSWING )
    // then
    //     FactLevelMeasureDenominator flmDenominator= factLevelMeasureBuilder.newFactLevelMeasureDenominatorWithPrimaryFact(128, $snfAdmittingEvent); insert(flmDenominator)
    // end

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyAttributeSingleComparisonFragmentTemplate<Date> admittingEventDischargeDateTemplate = UtilFunctions
                .createDateAttributeAndVariableCheckFragment(
                        "endDate",
                        CommonOperators.LESS_THAN_OR_EQUAL_TO,
                        ActiveMeasureGlobalContainer.INSTANCE
                                .getByDefinition(ActiveMeasureGlobalDefinition.MEASUREMENT_END_DATE));
        admittingEventDischargeDateTemplate
                .setVariableName("$admittingEventDischargeDate");

        AnyClassMultipleAttributeEvaluationFragmentTemplate admittingEventForAcuteCareTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ClaimHeader.class,
                        "$admittingEventForAcuteCarePshyOrCAH",
                        admittingEventDischargeDateTemplate, UtilFunctions
                                .createBooleanCheckFragment(
                                "inpatient", true, CommonOperators.EQUAL_TO));

        DateDefinitionTemplate dayAfterAdmittingEventDischargeTemplate = ActiveMeasureUtilFunctions
                .createDateDefinitionTemplate(
                        "$1DayAfterAdmittingEventDischargeDate",
                        DateShiftFunction.GET_LATER_DATE,
                        admittingEventDischargeDateTemplate
                                .getVariableExpression(), 1,
                        DateTimeUnit.DAY);

        AnyClassMultipleAttributeEvaluationFragmentTemplate snfAdmittingEventTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ClaimHeader.class,
                        "$snfAdmittingEvent",
                        UtilFunctions.createObjectVariableCheckFragment("this",
                                CommonOperators.NOT_EQUAL_TO,
                                ClaimHeader.class,
                                admittingEventForAcuteCareTemplate
                                        .getVariableExpression()),
                        UtilFunctions
                                .createDateAttributeAndVariableCheckFragment(
                                        "endDate",
                                        CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                        ActiveMeasureUtilFunctions
                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MEASUREMENT_END_DATE)),
                        UtilFunctions
                                .createDateAttributeAndVariableCheckFragment(
                                        "startDate",
                                        CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                        admittingEventDischargeDateTemplate
                                                .getVariableExpression()),
                        UtilFunctions
                                .createDateAttributeAndVariableCheckFragment(
                                        "startDate",
                                        CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                        dayAfterAdmittingEventDischargeTemplate
                                                .getVariableExpression()),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                "claimType",
                                                TypeDescription
                                                        .getTypeDescription(ClaimType.class)),
                                        UtilFunctions
                                                .createEnumLiteralExpressionFragmentTemplate(
                                                        ClaimType.class,
                                                        ClaimType.SNF_NONSWING),
                                        CommonOperators.EQUAL_TO, null));

        DefaultStandardRuleTemplate rule = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "DENOM_ADMISSION_SNF_WITHIN_1DAY",
                        ActiveMeasureUtilFunctions
                                .createFactLevelMeasureDenominatorCreationTemplate(
                                        128, snfAdmittingEventTemplate
                                                .getVariableExpression()),ActiveMeasureUtilFunctions
                                .createActiveMeasuresMemberInfoFragment(65, 0,
                                        false, null),
                        admittingEventForAcuteCareTemplate,
                        dayAfterAdmittingEventDischargeTemplate,
                        snfAdmittingEventTemplate);
        rule.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_30Day_Readmission_Skilled_Nursing_Facility");
        return rule;
    }
}

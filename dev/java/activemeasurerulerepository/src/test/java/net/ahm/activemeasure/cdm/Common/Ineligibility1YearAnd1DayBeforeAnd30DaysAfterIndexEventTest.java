package net.ahm.activemeasure.cdm.Common;

import java.util.Date;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.DateDefinitionTemplate;
import net.ahm.activemeasure.templates.MemberEnrollmentGapTemplate;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.ruleengine.DateTimeUnit;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NotExistenceExpression;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.DateShiftFunction;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * This used test class is used to generate the rule for
 * INELIGIBILITY_READMISSION.
 * 
 * <p>
 * The templates used here are simulating the following:
 * 
 * Checking some logic within a range (using
 * {@link SomeBehaviorBetweenTwoDatesTemplate})
 * 
 * The logic to be checked is is some enrollment gap in the member data (done by
 * {@link MemberEnrollmentGapTemplate}).
 * 
 * The range is defined by using {@link ClaimHeader#getEndDate()} as the
 * starting point, and push backward and forward some specific values (done by
 * {@link SingleDatedObjectWithTwoOffSetDateRangeTemplate}).
 * 
 * The centered {@link DatedObjectTemplate} will be coming from
 * {@link DenominatorsPrimaryFactDateTemplate}
 * <p>
 * 
 * @author xsu
 * 
 */
public class Ineligibility1YearAnd1DayBeforeAnd30DaysAfterIndexEventTest extends
        AbstractActiveMeasuresRuleTemplateTest {

//rule "COMMON_INELIGIBILITY_1GAP_1DAY_IN_12MO_PRIOR_AND_30DAYS_AFTER_INDEX_EVENT"
//dialect "mvel"
//when
//     $flmDenom : FactLevelMeasureDenominator( measureId in ( 125, 128 ) , $primaryFact : primaryOriginationFact != null )
//     ClaimHeader( $initialDischarge : endDate != null ) from $primaryFact
//    $12MonthsBeforeDischarge : java.util.Date() from GlobalFunctions.getEarlierDate($initialDischarge, 12, DateTimeUnit.MONTH)
//    $30DaysAfterDischarge : java.util.Date() from GlobalFunctions.getLaterDate($initialDischarge, 30, DateTimeUnit.DAY)
//    MemberInfo ( $initialGapDays2 : enrolmentGapDays ); java.util.Collection( size >=  1 ) from collect ( ContiguousDays( durationInDays >=  1) from GlobalFunctions.filterWholeDays( $initialGapDays2, $12MonthsBeforeDischarge, $30DaysAfterDischarge ) )
//then
//     $flmDenom.setEligible( false );
//end

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate denomTemplate = ActiveMeasureUtilFunctions
                .createFactLevelMeasureDenominator(125, 128);
        denomTemplate
                .setVariableName(ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR);
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactTemplate = ActiveMeasureUtilFunctions
                .createAnyAttributeSingleComparisonFragmentTemplate(
                        new AttributeFragmentTemplate(
                                ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                                TypeDescription.getTypeDescription(Fact.class)),
                        new NotExistenceExpression(),
                        CommonOperators.NOT_EQUAL_TO,
                        "$"
                                + ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME);
        denomTemplate.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments().add(primaryFactTemplate);

        AnyClassMultipleAttributeEvaluationFragmentTemplate claimHeaderTemplate= new AnyClassMultipleAttributeEvaluationFragmentTemplate();
        claimHeaderTemplate.setExpressionReturnTypeDescription(TypeDescription
                .getTypeDescription(ClaimHeader.class));
        claimHeaderTemplate.getMultipleAttributeEvaluationFragments()
                .setConnector(Connector.COMMA);
        AnyAttributeSingleComparisonFragmentTemplate<Object> endDateNotNullTemplate = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment("endDate",
                        CommonOperators.NOT_EQUAL_TO, Date.class);
        endDateNotNullTemplate.setVariableName("$initialDischarge");
        claimHeaderTemplate.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments().add(endDateNotNullTemplate);

        DateDefinitionTemplate beforeDateTemplate = ActiveMeasureUtilFunctions
                .createDateDefinitionTemplate("$12MonthsBeforeDischarge",
                        DateShiftFunction.GET_EARLIER_DATE,
                        endDateNotNullTemplate.getVariableExpression(), 12,
                        DateTimeUnit.MONTH);

        DateDefinitionTemplate afterDateTemplate = ActiveMeasureUtilFunctions
                .createDateDefinitionTemplate("$30DaysAfterDischarge",
                        DateShiftFunction.GET_LATER_DATE,
                        endDateNotNullTemplate.getVariableExpression(), 30,
                        DateTimeUnit.DAY);

        MemberEnrollmentGapTemplate memberEnrollmentGapLogic = new MemberEnrollmentGapTemplate(
                beforeDateTemplate.getVariableExpression(),
                afterDateTemplate.getVariableExpression(), 1,
                CommonOperators.GREATER_THAN_OR_EQUAL_TO, 1,
                CommonOperators.GREATER_THAN_OR_EQUAL_TO);

        DefaultStandardRuleTemplate rule = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "COMMON_INELIGIBILITY_1GAP_1DAY_IN_12MO_PRIOR_AND_30DAYS_AFTER_INDEX_EVENT",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorEligibilityToFalseTemplate(denomTemplate
                                        .getVariableExpression()),
                        denomTemplate,
                        new FromEvaluationFragmentTemplate(claimHeaderTemplate,
                                primaryFactTemplate.getVariableExpression()),
                        beforeDateTemplate, afterDateTemplate,
                        memberEnrollmentGapLogic);
        rule.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.Common.COMMON_INELIGIBILITY_1GAP_1DAY_IN_12MO_PRIOR_AND_30DAYS_AFTER_INDEX_EVENT");
        return rule;
	}
}

package net.ahm.activemeasure.medicalcase.Common;

import java.util.Date;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.DateDefinitionTemplate;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.medicalcase.MedicalCase;
import net.ahm.careengine.domain.member.ActiveMeasuresMemberInfo;
import net.ahm.careengine.domain.member.Gender;
import net.ahm.careengine.ruleengine.DateTimeUnit;
import net.ahm.rulesapp.templates.enums.CommonOperators;
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
 * Test case to generate JSON/DRL representation of the rule DENOM_EXCL_AGE_LESS_THAN_1_ADVERSE_EVENT_RATE
 */


//1.	|	rule "COMMON_DENOM_EXCL_AGE_LESS_THAN_1"
//2.	|	    dialect "mvel"
//3.	|	    when
//4.	|	         $flmDenom : FactLevelMeasureDenominator( $primaryOrigEvent : primaryOriginationFact != null , measureId in ( 84, 157, 85, 86, 159, 160 ) )
//5.	|	         $amInfo : ActiveMeasuresMemberInfo( gender != null , $birthDate : birthDate != null )
//6.	|	        $year1AfterBD : java.util.Date() from GlobalFunctions.getLaterDate($birthDate, 1, DateTimeUnit.YEAR)
//7.	|	         $mcPrimaryOrigEvent : MedicalCase( this == $primaryOrigEvent , medicalCaseStartDate < ( $year1AfterBD ) )
//8.	|	    then
//9.	|	         $flmDenom.setExcludedFromDenominator( true );
//10.	|	end

public class CommonDenomExclAgeLessThan1Test extends
        AbstractActiveMeasuresRuleTemplateTest {

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactExpr = ActiveMeasureUtilFunctions
                .createFactLevelDenominatorPrimaryFactNullCheck();

        AnyClassMultipleAttributeEvaluationFragmentTemplate flmDenomTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR,
                        primaryFactExpr, ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(84,
                                        157, 85, 86, 159, 160));

        AnyAttributeSingleComparisonFragmentTemplate<Object> birthDateExpression = UtilFunctions
                .createAnyAttributeSingleComparisonFragmentTemplate(
                        new AttributeFragmentTemplate("birthDate",
                                TypeDescription.getTypeDescription(Date.class)),
                        new NotExistenceExpression(),
                        CommonOperators.NOT_EQUAL_TO, "$birthDate");

        AnyClassMultipleAttributeEvaluationFragmentTemplate memberInfoTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ActiveMeasuresMemberInfo.class, null, UtilFunctions
                                .createObjectNullCheckFragment("gender",
                                        CommonOperators.NOT_EQUAL_TO,
                                        Gender.class), birthDateExpression);

        DateDefinitionTemplate afterDateTemplate = ActiveMeasureUtilFunctions
                .createDateDefinitionTemplate("$year1AfterBD",
                        DateShiftFunction.GET_LATER_DATE,
                        birthDateExpression.getVariableExpression(), 1,
                        DateTimeUnit.YEAR);

        // using from clause rather than this == $primaryEvent because it is
        // more performant
        AnyClassMultipleAttributeEvaluationFragmentTemplate medicalCaseTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MedicalCase.class,
                        null,
                        ActiveMeasureUtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        "medicalCaseStartDate",
                                        TypeDescription
                                                .getTypeDescription(Date.class),
                                        afterDateTemplate
                                                .getVariableExpression(),
                                        CommonOperators.LESS_THAN, null));

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "COMMON_DENOM_EXCL_AGE_LESS_THAN_1",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorExclusionToTrueTemplate(flmDenomTemplate
                                        .getVariableExpression()),
                        flmDenomTemplate,
                        memberInfoTemplate, afterDateTemplate,
                        new FromEvaluationFragmentTemplate(medicalCaseTemplate,
                                primaryFactExpr.getVariableExpression()));
        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.Common");

    return standardRuleInstance;
  }
}

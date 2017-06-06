package net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_30Day_Readmission_Skilled_Nursing_Facility;

import java.util.Date;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.DateDefinitionTemplate;
import net.ahm.activemeasure.templates.FactLevelMeasureNumeratorCreationTemplate;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.ruleengine.DateTimeUnit;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.util.DateShiftFunction;
import net.ahm.rulesapp.util.UtilFunctions;

public class Num30DayReadmnWithCdmAcuteDiagnosisTest extends
        AbstractActiveMeasuresRuleTemplateTest {

//rule "NUM_30DAY_READMN_WITH_CDM_ACUTE_DIAGNOSIS"
//    dialect "mvel"
//    when
//        $factLevelDenominator : FactLevelMeasureDenominator ( measureId == 128 , $primaryOriginationFact : primaryOriginationFact != null )
//        ClaimHeader ( $snfEventDischargeDate : endDate != null ) from $primaryOriginationFact
//        $30DaysAfterSNFEventDischargeDate : java.util.Date() from GlobalFunctions.getLaterDate($snfEventDischargeDate, 30, DateTimeUnit.DAY)
//        $numPrimaryFact : ClaimHeader ( allDiagnosticEventElements contains 7940 , startDate >= $snfEventDischargeDate , this != $primaryOriginationFact , startDate <= $30DaysAfterSNFEventDischargeDate )
//    then
//        FactLevelMeasureNumerator $factLevelNumerator = $factLevelDenominator.createFactLevelMeasureNumeratorWithPrimaryFact($numPrimaryFact);
//        insert($factLevelNumerator);
//    end

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactTemplate = ActiveMeasureUtilFunctions
                .createFactLevelDenominatorPrimaryFactNullCheck();

        AnyClassMultipleAttributeEvaluationFragmentTemplate denomTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR,
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(128),
                        primaryFactTemplate);

        AnyAttributeSingleComparisonFragmentTemplate<Object> snfEventDischargeDate = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment("endDate",
                        CommonOperators.NOT_EQUAL_TO, Date.class);
        snfEventDischargeDate.setVariableName("$snfEventDischargeDate");
        AnyClassMultipleAttributeEvaluationFragmentTemplate denomClaimHeader = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ClaimHeader.class, null, snfEventDischargeDate);

        FromEvaluationFragmentTemplate fromDenomTemplate = new FromEvaluationFragmentTemplate(
                denomClaimHeader, primaryFactTemplate.getVariableExpression());

        DateDefinitionTemplate dateMathTemplate = ActiveMeasureUtilFunctions
                .createDateDefinitionTemplate(
                        "$30DaysAfterSNFEventDischargeDate",
                        DateShiftFunction.GET_LATER_DATE,
                        snfEventDischargeDate.getVariableExpression(), 30,
                        DateTimeUnit.DAY);

        AnyClassMultipleAttributeEvaluationFragmentTemplate numPrimaryFact = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ClaimHeader.class,
                        "$numPrimaryFact",
                        ActiveMeasureUtilFunctions
                                .createElementContainsValueTemplate(
                                        "allDiagnosticEventElements", 7940),
                        UtilFunctions
                                .createDateAttributeAndVariableCheckFragment(
                                        "startDate",
                                        CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                        snfEventDischargeDate
                                                .getVariableExpression()),
                        UtilFunctions.createObjectVariableCheckFragment("this",
                                CommonOperators.NOT_EQUAL_TO, Fact.class,
                                primaryFactTemplate.getVariableExpression()),
                        UtilFunctions
                                .createDateAttributeAndVariableCheckFragment(
                                        "startDate",
                                        CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                        dateMathTemplate
                                                .getVariableExpression()));

        DefaultStandardRuleTemplate rule = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "NUM_30DAY_READMN_WITH_CDM_ACUTE_DIAGNOSIS",
                        new FactLevelMeasureNumeratorCreationTemplate(
                                denomTemplate.getVariableExpression(),
                                numPrimaryFact.getVariableExpression()),
                        denomTemplate, fromDenomTemplate, dateMathTemplate,
                        numPrimaryFact);
        rule.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_30Day_Readmission_Skilled_Nursing_Facility");
        return rule;
    }
}

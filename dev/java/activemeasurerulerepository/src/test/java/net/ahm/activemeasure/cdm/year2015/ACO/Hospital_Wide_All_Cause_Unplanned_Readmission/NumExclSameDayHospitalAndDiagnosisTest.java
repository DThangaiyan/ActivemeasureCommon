package net.ahm.activemeasure.cdm.year2015.ACO.Hospital_Wide_All_Cause_Unplanned_Readmission;

import java.util.Date;
import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.DateDefinitionTemplate;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.ruleengine.DateTimeUnit;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NotExistenceExpression;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.DateShiftFunction;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Simulating rule "NUM_EXCL_SAME_DAY_HOSPITAL_AND_DIAGNOSIS"
 * 
 * TODO revisit how this rule is done - not sure this is the best for the UI.
 * 
 * @author xsu
 * 
 */
public class NumExclSameDayHospitalAndDiagnosisTest extends
        AbstractActiveMeasuresRuleTemplateTest {

//    rule "NUM_EXCL_SAME_DAY_HOSPITAL_AND_DIAGNOSIS"
//    dialect "mvel"
//    when
//         $flmn : FactLevelMeasureNumerator( measureId == 125 , $numPrimaryFact : primaryFact != null , factLevelDenominator != null , $denomPrimaryFact : factLevelDenominator.primaryOriginationFact != null )
//         $denomClaimHeader : ClaimHeader( $denomPricipalElements : principalDiagnosisElements != null , $denomDischarge : endDate != null ) from $denomPrimaryFact
//        $dayAfterDischarge : java.util.Date() from GlobalFunctions.getLaterDate($denomDischarge, 1, DateTimeUnit.DAY)
//         $numClaimHeader : ClaimHeader( servicingOrgId == $denomClaimHeader.servicingOrgId , principalDiagnosisElements == $denomPricipalElements , startDate >= $denomDischarge , startDate < ( $dayAfterDischarge ) ) from $numPrimaryFact
//    then
//         $flmn.setExcludedFromNumerator( true );
//    end
    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate numeratorExpr = ActiveMeasureUtilFunctions
                .createFactLevelMeasureNumerator(125);
        AnyAttributeSingleComparisonFragmentTemplate<Object> numPrimaryFactExpr = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment(
                        ActiveMeasureUtilConstants.FACT_LEVEL_NUMERATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                        CommonOperators.NOT_EQUAL_TO, Fact.class);
        numPrimaryFactExpr.setVariableName("$numPrimaryFact");
        numeratorExpr
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(numPrimaryFactExpr);
        numeratorExpr
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(ActiveMeasureUtilFunctions.createObjectNullCheckFragment(
                        "factLevelDenominator", CommonOperators.NOT_EQUAL_TO,
                        FactLevelMeasureDenominator.class));
        numeratorExpr.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments().add(numPrimaryFactExpr);
        AnyAttributeSingleComparisonFragmentTemplate<Fact> denomPrimaryFactExpr = new AnyAttributeSingleComparisonFragmentTemplate<>();
        denomPrimaryFactExpr.setOperator(CommonOperators.NOT_EQUAL_TO);
        denomPrimaryFactExpr.setVariableName("$denomPrimaryFact");
        denomPrimaryFactExpr.setOperator(CommonOperators.NOT_EQUAL_TO);
        denomPrimaryFactExpr.setAttributeValue(new NotExistenceExpression());
        denomPrimaryFactExpr
                .setAttribute(new AttributeFragmentTemplate(
                        "factLevelDenominator",
                        TypeDescription
                                .getTypeDescription(FactLevelMeasureDenominator.class),
                        new AttributeFragmentTemplate(
                                ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                                TypeDescription.getTypeDescription(Fact.class))));
        numeratorExpr.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments().add(denomPrimaryFactExpr);

        //$denomClaimHeader : ClaimHeader( $denomPricipalElements : principalDiagnosisElements != null , $denomDischarge : endDate != null ) from $denomPrimaryFact
        AnyClassMultipleAttributeEvaluationFragmentTemplate denomClaimHeaderExpr = new AnyClassMultipleAttributeEvaluationFragmentTemplate();
        denomClaimHeaderExpr.setExpressionReturnTypeDescription(TypeDescription
                .getTypeDescription(ClaimHeader.class));
        denomClaimHeaderExpr.getMultipleAttributeEvaluationFragments()
                .setConnector(Connector.COMMA);
        denomClaimHeaderExpr.setVariableName("$denomClaimHeader");
        AnyAttributeSingleComparisonFragmentTemplate<Object> pricipalDiagnosisElements = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment("principalDiagnosisElements",
                        CommonOperators.NOT_EQUAL_TO, Set.class);
        pricipalDiagnosisElements.setVariableName("$denomPricipalElements");
        denomClaimHeaderExpr.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(pricipalDiagnosisElements);
        AnyAttributeSingleComparisonFragmentTemplate<Object> denomDischargeDate = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment("endDate",
                        CommonOperators.NOT_EQUAL_TO, Date.class);
        denomDischargeDate.setVariableName("$denomDischarge");
        denomClaimHeaderExpr.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments().add(denomDischargeDate);

        // $dayAfterDischarge : java.util.Date() from
        // GlobalFunctions.getLaterDate($denomDischarge, 1, DateTimeUnit.DAY)
        DateDefinitionTemplate dateMathExpr = ActiveMeasureUtilFunctions
                .createDateDefinitionTemplate("$dayAfterDischarge",
                        DateShiftFunction.GET_LATER_DATE,
                        denomDischargeDate.getVariableExpression(), 1,
                        DateTimeUnit.DAY);

        // $numClaimHeader : ClaimHeader( servicingOrgId ==
        // $denomClaimHeader.servicingOrgId , principalDiagnosisElements ==
        // $denomPricipalElements , startDate >= $denomDischarge , startDate < (
        // $dayAfterDischarge ) ) from $numPrimaryFact
        AnyClassMultipleAttributeEvaluationFragmentTemplate numClaimHeaderExpr = new AnyClassMultipleAttributeEvaluationFragmentTemplate();
        numClaimHeaderExpr.setVariableName("$numClaimHeader");
        numClaimHeaderExpr.setExpressionReturnTypeDescription(TypeDescription
                .getTypeDescription(ClaimHeader.class));
        numClaimHeaderExpr.getMultipleAttributeEvaluationFragments()
                .setConnector(Connector.COMMA);
        AnyAttributeSingleComparisonFragmentTemplate<Object> servicingOrgExpr = new AnyAttributeSingleComparisonFragmentTemplate<Object>();
        servicingOrgExpr
                .setAttribute(new AttributeFragmentTemplate("servicingOrgId",
                        TypeDescription.getTypeDescription(Long.TYPE)));
        servicingOrgExpr.setOperator(CommonOperators.EQUAL_TO);
        NamedVariableLiteralFragmentTemplate denomExpression = denomClaimHeaderExpr
                .getVariableExpression();
        denomExpression
                .setAttribute(new AttributeFragmentTemplate("servicingOrgId",
                        TypeDescription.getTypeDescription(Long.TYPE)));
        servicingOrgExpr.setAttributeValue(denomExpression);
        numClaimHeaderExpr.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments().add(servicingOrgExpr);

        numClaimHeaderExpr
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(ActiveMeasureUtilFunctions
                        .createObjectVariableCheckFragment(
                                "principalDiagnosisElements", TypeDescription
                                        .getTypeDescription(Set.class,
                                                Integer.class),
                                CommonOperators.EQUAL_TO,
                                pricipalDiagnosisElements
                                        .getVariableExpression()));
        numClaimHeaderExpr
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(ActiveMeasureUtilFunctions
                        .createObjectVariableCheckFragment("startDate",
                                Date.class,
                                CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                denomDischargeDate.getVariableExpression()));
        numClaimHeaderExpr
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(ActiveMeasureUtilFunctions
                        .createObjectVariableCheckFragment("startDate",
                                Date.class, CommonOperators.LESS_THAN,
                                dateMathExpr.getVariableExpression()));


        DefaultStandardRuleTemplate rule = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "NUM_EXCL_SAME_DAY_HOSPITAL_AND_DIAGNOSIS",
                        ActiveMeasureUtilFunctions
                                .createSetNumeratorExclusionToTrueTemplate(numeratorExpr
                                        .getVariableExpression()),
                        numeratorExpr,
                        new FromEvaluationFragmentTemplate(
                                denomClaimHeaderExpr, denomPrimaryFactExpr
                                        .getVariableExpression()),
                        dateMathExpr,
                        new FromEvaluationFragmentTemplate(numClaimHeaderExpr,
                                numPrimaryFactExpr.getVariableExpression()));
        rule.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.Hospital_Wide_All_Cause_Unplanned_Readmission");
        return rule;
    }
}

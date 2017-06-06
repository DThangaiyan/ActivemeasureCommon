package net.ahm.activemeasure.cdm.year2015.ACO.Hospital_Wide_All_Cause_Unplanned_Readmission;

import java.util.Date;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.DateDefinitionTemplate;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.ruleengine.DateTimeUnit;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.DateShiftFunction;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Simulate rule "NUM_READMISSION"
 * 
 * @author xsu
 * 
 */
public class NumReadmissionTest extends AbstractActiveMeasuresRuleTemplateTest {

//    rule "NUM_READMISSION"
//    dialect "mvel"
//    when
//         $flmd : FactLevelMeasureDenominator( measureId == 125 , $primaryFact : primaryOriginationFact != null )
//         ClaimHeader( $initialDischarge : endDate != null ) from $primaryFact
//        $30DaysAfterDischarge : java.util.Date() from GlobalFunctions.getLaterDate($initialDischarge, 30, DateTimeUnit.DAY)
//         $readmission : ClaimHeader( startDate >= $initialDischarge , inpatient == true , startDate <= ( $30DaysAfterDischarge ) , this != $primaryFact )
//    then
//        FactLevelMeasureNumerator flmNumerator = $flmd.createFactLevelMeasureNumeratorWithPrimaryFact($readmission); insert(flmNumerator);
//    end
    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate denominatorTemplate = ActiveMeasureUtilFunctions
                .createFactLevelMeasureDenominator(125);
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFact = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment(
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                        CommonOperators.NOT_EQUAL_TO, Fact.class);
        primaryFact.setVariableName("$primaryFact");
        denominatorTemplate.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments().add(primaryFact);

        AnyClassMultipleAttributeEvaluationFragmentTemplate initialClaimHeaderTemplate = new AnyClassMultipleAttributeEvaluationFragmentTemplate();
        initialClaimHeaderTemplate
                .setExpressionReturnTypeDescription(TypeDescription
                        .getTypeDescription(ClaimHeader.class));
        initialClaimHeaderTemplate.getMultipleAttributeEvaluationFragments()
                .setConnector(Connector.COMMA);
        AnyAttributeSingleComparisonFragmentTemplate<Object> initialDischargeTemplate = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment("endDate",
                        CommonOperators.NOT_EQUAL_TO, Date.class);
        initialDischargeTemplate.setVariableName("$initialDischarge");
        initialClaimHeaderTemplate.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(initialDischargeTemplate);

        DateDefinitionTemplate $30DaysAfterDischargeDateTemplate = ActiveMeasureUtilFunctions
                .createDateDefinitionTemplate("$30DaysAfterDischarge",
                        DateShiftFunction.GET_LATER_DATE,
                        initialDischargeTemplate.getVariableExpression(), 30,
                        DateTimeUnit.DAY);

        AnyClassMultipleAttributeEvaluationFragmentTemplate otherClaimHeaderTemplate = new AnyClassMultipleAttributeEvaluationFragmentTemplate();
        otherClaimHeaderTemplate
                .setExpressionReturnTypeDescription(TypeDescription
                        .getTypeDescription(ClaimHeader.class));
        otherClaimHeaderTemplate.getMultipleAttributeEvaluationFragments()
                .setConnector(Connector.COMMA);
        otherClaimHeaderTemplate.setVariableName("$readmission");
        otherClaimHeaderTemplate
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(ActiveMeasureUtilFunctions
                        .createObjectVariableCheckFragment("startDate",
                                Date.class,
                                CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                initialDischargeTemplate
                                        .getVariableExpression()));
        otherClaimHeaderTemplate
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(ActiveMeasureUtilFunctions.createBooleanCheckFragment(
                        "inpatient", true, CommonOperators.EQUAL_TO));
        otherClaimHeaderTemplate
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(ActiveMeasureUtilFunctions
                        .createObjectVariableCheckFragment("startDate",
                                Date.class,
                                CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                $30DaysAfterDischargeDateTemplate
                                        .getVariableExpression()));
        otherClaimHeaderTemplate
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(ActiveMeasureUtilFunctions
                        .createObjectVariableCheckFragment("this",
                                ClaimHeader.class,
                                CommonOperators.NOT_EQUAL_TO,
                                primaryFact.getVariableExpression()));

        DefaultStandardRuleTemplate rule = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "NUM_READMISSION",
                        ActiveMeasureUtilFunctions
                                .createFactLevelMeasureNumeratorCreationTemplate(
                                        denominatorTemplate,
                                        otherClaimHeaderTemplate),
                        denominatorTemplate,
                        new FromEvaluationFragmentTemplate(
                                initialClaimHeaderTemplate, primaryFact
                                        .getVariableExpression()),
                        $30DaysAfterDischargeDateTemplate,
                        otherClaimHeaderTemplate);
        rule.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.Hospital_Wide_All_Cause_Unplanned_Readmission");
        return rule;
    }
}

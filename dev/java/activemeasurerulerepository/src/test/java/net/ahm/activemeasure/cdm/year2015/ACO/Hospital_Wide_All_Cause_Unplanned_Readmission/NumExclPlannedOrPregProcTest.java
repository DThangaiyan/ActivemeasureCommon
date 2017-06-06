package net.ahm.activemeasure.cdm.year2015.ACO.Hospital_Wide_All_Cause_Unplanned_Readmission;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Simulating rule "NUM_EXCL_PLANNED_OR_PREG_PROC"
 * 
 * @author Suresh
 * 
 */
public class NumExclPlannedOrPregProcTest extends
        AbstractActiveMeasuresRuleTemplateTest {

//Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.Hospital_Wide_All_Cause_Unplanned_Readmission
//    rule "NUM_EXCL_PLANNED_OR_PREG_PROC"
//    dialect "mvel"
//    when
//        $factLevelNumerator : FactLevelMeasureNumerator ( measureId == 125 , $primaryFact : primaryFact != null )
//    ClaimHeader ( ( relatedProcedureEventElements contains 7938 || relatedProcedureEventElements contains 8825 ) ) from $primaryFact
//    then
//        $factLevelNumerator.setExcludedFromNumerator(true);
//    end
    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate numeratorTemplate = ActiveMeasureUtilFunctions
                .createFactLevelMeasureNumerator(125);
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFact = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment(
                        ActiveMeasureUtilConstants.FACT_LEVEL_NUMERATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                        CommonOperators.NOT_EQUAL_TO, Fact.class);
        primaryFact.setVariableName("$primaryFact");
        numeratorTemplate.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments().add(primaryFact);

        AnyClassMultipleAttributeEvaluationFragmentTemplate claimHeaderTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ClaimHeader.class, null, ActiveMeasureUtilFunctions
                                .createElementContainsValueTemplate(
                                        "relatedProcedureEventElements", 7938),
                        ActiveMeasureUtilFunctions
                                .createElementContainsValueTemplate(
                                        "relatedProcedureEventElements", 8825));
        claimHeaderTemplate.getMultipleAttributeEvaluationFragments()
                .setConnector(Connector.OR);


        DefaultStandardRuleTemplate rule = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "NUM_EXCL_PLANNED_OR_PREG_PROC",
                        ActiveMeasureUtilFunctions
                                .createSetNumeratorExclusionToTrueTemplate(numeratorTemplate
                                        .getVariableExpression()),
                        numeratorTemplate,
                        new FromEvaluationFragmentTemplate(claimHeaderTemplate,
                                primaryFact.getVariableExpression()));
        rule.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.Hospital_Wide_All_Cause_Unplanned_Readmission");
        return rule;
    }
}

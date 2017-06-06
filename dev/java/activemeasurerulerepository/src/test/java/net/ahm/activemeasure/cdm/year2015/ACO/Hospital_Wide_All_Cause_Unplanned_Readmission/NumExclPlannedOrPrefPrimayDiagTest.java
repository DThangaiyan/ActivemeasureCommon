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
 * Simulating the generation of rule "NUM_EXCL_PLANNED_OR_PREF_PRIMARY_DIAG"
 * 
 * @author xsu
 * 
 */
public class NumExclPlannedOrPrefPrimayDiagTest extends
        AbstractActiveMeasuresRuleTemplateTest {

//    rule "NUM_EXCL_PLANNED_OR_PREF_PRIMARY_DIAG"
//    dialect "mvel"
//    when
//        $factLevelNumerator : FactLevelMeasureNumerator ( measureId == 125 , $primaryFact : primaryFact != null )
//    ClaimHeader ( ( principalDiagnosisElements contains 8815 || principalDiagnosisElements contains 8836 ) ) from $primaryFact
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
                                        "principalDiagnosisElements", 8815),
                        ActiveMeasureUtilFunctions
                                .createElementContainsValueTemplate(
                                        "principalDiagnosisElements", 8836));
        claimHeaderTemplate.getMultipleAttributeEvaluationFragments()
                .setConnector(Connector.OR);

        DefaultStandardRuleTemplate rule = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "NUM_EXCL_PLANNED_OR_PREF_PRIMARY_DIAG",
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

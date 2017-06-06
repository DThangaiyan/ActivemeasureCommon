package net.ahm.activemeasure.cdm.Common;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NotExistenceExpression;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case used to generate rule for DENOM_EXCL_PROC_HOSPICE
 * 
 */
public class DenomExclProcHospiceFromIndexEventTest extends
        AbstractActiveMeasuresRuleTemplateTest {

//    rule "COMMON_DENOM_EXCL_PROC_HOSPICE_FROM_INDEX_EVENT"
//    dialect "mvel"
//    when
//        $factLevelDenominator : FactLevelMeasureDenominator ( measureId in ( 125, 128 ) , $primaryOriginationFact : primaryOriginationFact != null )
//    ClaimHeader ( relatedProcedureEventElements contains 2675 ) from $primaryOriginationFact
//    then
//        $factLevelDenominator.setExcludedFromDenominator(true);
//    end
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

        AnyClassMultipleAttributeEvaluationFragmentTemplate claimHeaderTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ClaimHeader.class, null, ActiveMeasureUtilFunctions
                                .createElementContainsValueTemplate(
                                        "relatedProcedureEventElements", 2675));


        // setExcludedFromDenominator( true );
        NamedVariableLiteralFragmentTemplate denomExpr = denomTemplate
                .getVariableExpression();


        DefaultStandardRuleTemplate rule = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "COMMON_DENOM_EXCL_PROC_HOSPICE_FROM_INDEX_EVENT",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorExclusionToTrueTemplate(denomExpr),
                        denomTemplate,
                        new FromEvaluationFragmentTemplate(claimHeaderTemplate,
                                primaryFactTemplate.getVariableExpression()));
        rule.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.Common");
        return rule;
    }
}

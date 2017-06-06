package net.ahm.activemeasure.cdm.year2015.ACO.Ambulatory_Care_Sensitive_Conditions_Admissions_For_Heart_Failure;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureNumerator;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule NUM_EXCL_CARDIAC_PROC
 */

//91. | rule "NUM_EXCL_CARDIAC_PROC"
//92. |     dialect "mvel"
//93. |     when
//94. |         $flmNum : FactLevelMeasureNumerator( measureId == 127 , $primaryOrigFact : primaryFact != null )
//95. |         ClaimHeader( relatedProcedureEventElements contains 7933 ) from $primaryOrigFact
//96. |     then
//97. |         $flmNum.setExcludedFromNumerator( true );
//98. | end
public class NumExclCardiacProcTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    private static final int MEASURE_ID = 127;
    private static final int ELEMENT_ID = 7933;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFact = ActiveMeasureUtilFunctions.createFactLevelDenominatorPrimaryFactNullCheck();

        AnyClassMultipleAttributeEvaluationFragmentTemplate numeratorTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureNumerator.class,
                        "$factLevelNumerator",
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(MEASURE_ID),
                        primaryFact);

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate("NUM_EXCL_CARDIAC_PROC",
                        ActiveMeasureUtilFunctions
                                .createSetNumeratorExclusionToTrueTemplate(numeratorTemplate
                                        .getVariableExpression()),
                        numeratorTemplate,
                        new FromEvaluationFragmentTemplate(
                                UtilFunctions
                                        .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                                                ClaimHeader.class,
                                                null,
                                                ActiveMeasureUtilFunctions
                                                        .createElementContainsValueTemplate(
                                                                "relatedProcedureEventElements",
                                                                ELEMENT_ID)),
                                primaryFact.getVariableExpression()));
        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.Ambulatory_Care_Sensitive_Conditions_Admissions_For_Heart_Failure");
        return standardRuleInstance;
    }
}

package net.ahm.activemeasure.medicalcase.year2015.aetnameasures.AdverseEvent_Rate_Acute_Inpatient_2015;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.FactLevelMeasureNumeratorCreationTemplate;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.medicalcase.MedicalCase;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule NUM_AVG_LENGTH_OF_STAY
 */

//78.	|	rule "NUM_ADVERSE_EVENT_RATE"
//79.	|	    dialect "mvel"
//80.	|	    when
//81.	|	        $flDenom : FactLevelMeasureDenominator( measureId == 85 , $primaryOrigin : primaryOriginationFact != null )
//82.	|	        $medicalCase : MedicalCase( this == $primaryOrigin , adverseEventEvaluationCode == "4" || == "1" )
//83.	|	    then
//84.	|	        FactLevelMeasureNumerator flmNumerator = $flDenom.createFactLevelMeasureNumeratorWithPrimaryFact($medicalCase); insert(flmNumerator);
//85.	|	end

public class NumAdverseEventRateTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    private static final int MEASURE_ID = 85;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactExpr = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment(
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                        CommonOperators.NOT_EQUAL_TO, Fact.class);
        primaryFactExpr.setVariableName("$primaryOrigEvent");

        AnyClassMultipleAttributeEvaluationFragmentTemplate flmDenomTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR,
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(MEASURE_ID),
                        primaryFactExpr);

        AttributeFragmentTemplate adverseEventEvaluationCodeTemplate = new AttributeFragmentTemplate(
                "adverseEventEvaluationCode",
                TypeDescription.getTypeDescription(Character.class));
        AnyClassMultipleAttributeEvaluationFragmentTemplate medicalCaseTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MedicalCase.class,
                        "$medicalCase",
                        ActiveMeasureUtilFunctions
                                .createMultipleAttributeEvaluationFragmentTemplate(
                                        Connector.OR,
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        adverseEventEvaluationCodeTemplate,
                                                        UtilFunctions
                                                                .createCharacterLiteralExpressionTemplate('4'),
                                                        CommonOperators.EQUAL_TO,
                                                        null),
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        adverseEventEvaluationCodeTemplate,
                                                        UtilFunctions
                                                                .createCharacterLiteralExpressionTemplate('1'),
                                                        CommonOperators.EQUAL_TO,
                                                        null)));

        // Rewritten to use a from clause rather than this == $primaryFact
        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate("NUM_ADVERSE_EVENT_RATE",
                        new FactLevelMeasureNumeratorCreationTemplate(
                                flmDenomTemplate.getVariableExpression(),
                                medicalCaseTemplate.getVariableExpression()),
                        flmDenomTemplate,
                        new FromEvaluationFragmentTemplate(medicalCaseTemplate,
                                primaryFactExpr.getVariableExpression()));

        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2015.aetnameasures.AdverseEvent_Rate_Acute_Inpatient_2015");
        return standardRuleInstance;
    }
}

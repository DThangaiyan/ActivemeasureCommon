package net.ahm.activemeasure.medicalcase.year2016.aetnameasures.Oupatient_Procedure_Adverse_Event_Rate;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.FactLevelMeasureNumeratorCreationTemplate;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.medicalcase.MedicalCase;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule
 * NUM_ADVERSE_EVENT_RATE
 */

// 104. | rule "NUM_ADVERSE_EVENT_RATE"
// 105. | dialect "mvel"
// 106. | when
// 107. |   $flmDenom : FactLevelMeasureDenominator( measureId == 160 , $primaryFact : primaryOriginationFact != null )
// 108. |   MedicalCase( adverseEventEvaluationCode == "C" ) from $primaryFact
// 109. | then
// 110. |   FactLevelMeasureNumerator flmNumerator = $flmDenom.createFactLevelMeasureNumeratorWithPrimaryFact($primaryFact); insert(flmNumerator);
// 111. | end

public class NumAdverseEventRateTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    private static final int MEASURE_ID = 160;

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

		AnyClassMultipleAttributeEvaluationFragmentTemplate medicalCaseTemplate = ActiveMeasureUtilFunctions
				.createAnyClassMultipleAttributeEvaluationFragmentTemplate(
						MedicalCase.class,
						"$medicalCase",
						ActiveMeasureUtilFunctions
								.createAnyAttributeSingleComparisonFragmentTemplate(
										"adverseEventEvaluationCode",
										TypeDescription
												.getTypeDescription(Character.class),
										UtilFunctions
												.createCharacterLiteralExpressionTemplate('C'),
										CommonOperators.EQUAL_TO, null));

		DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
				.createDefaultStandardRuleTemplate("NUM_ADVERSE_EVENT_RATE",
						new FactLevelMeasureNumeratorCreationTemplate(
								flmDenomTemplate.getVariableExpression(),
								primaryFactExpr.getVariableExpression()),
								flmDenomTemplate,
								 new FromEvaluationFragmentTemplate(medicalCaseTemplate,
										 primaryFactExpr.getVariableExpression()));
		standardRuleInstance
				.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2016.aetnameasures.Oupatient_Procedure_Adverse_Event_Rate");

		return standardRuleInstance;
	}
}

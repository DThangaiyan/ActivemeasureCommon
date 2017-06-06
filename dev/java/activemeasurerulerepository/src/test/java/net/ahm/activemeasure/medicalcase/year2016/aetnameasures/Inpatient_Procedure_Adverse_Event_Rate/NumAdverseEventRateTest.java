package net.ahm.activemeasure.medicalcase.year2016.aetnameasures.Inpatient_Procedure_Adverse_Event_Rate;

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
// 1. | rule "NUM_ADVERSE_EVENT_RATE"
// 2. | dialect "mvel"
// 3. | when
// 4. | $flmDenom : FactLevelMeasureDenominator( measureId == 159 , $primaryFact : primaryOriginationFact != null )
// 5. | MedicalCase( adverseEventEvaluationCode == "4" ) from $primaryFact
// 6. | then
// 7. | FactLevelMeasureNumerator flmNumerator =
// $flmDenom.createFactLevelMeasureNumeratorWithPrimaryFact($primaryFact);
// insert(flmNumerator);
// 8. | end

public class NumAdverseEventRateTest extends
		AbstractActiveMeasuresRuleTemplateTest {

	private static int MEASURE_ID = 159;
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
												.createCharacterLiteralExpressionTemplate('4'),
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
		.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2016.aetnameasures.Inpatient_Procedure_Adverse_Event_Rate");

		return standardRuleInstance;
	}
}

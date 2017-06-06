/**
 * 
 */
package net.ahm.activemeasure.medicalcase.year2016.aetnameasures.Oupatient_Procedure_Adverse_Event_Rate;

import java.util.Date;
import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.FactLevelMeasureDenominatorCreationTemplate;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.medicalcase.MedicalCase;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule
 * DENOM_POSCODE_ADVERSEEVENTEVALCODE_IN_PAST_23_MNTHS
 */

// 114. | rule "DENOM_POSCODE_ADVERSEEVENTEVALCODE_IN_PAST_23_MNTHS"
// 115. | dialect "mvel"
// 116. | when
// 117. | $medicalCase : MedicalCase( medicalCaseEndDate <
// months1BeforeMeasurementEndDate , placeOfServiceCategoryCode == "E" || == "O"
// || == "F" , medicalCaseEndDate >= months24BeforeMeasurementEndDate ,
// placeOfServiceCategoryCode != null , adverseEventEvaluationCode == "B" || ==
// "C" )
// 118. | then
// 119. | FactLevelMeasureDenominator flmDenominator=
// factLevelMeasureBuilder.newFactLevelMeasureDenominatorWithPrimaryFact(160,$medicalCase);
// insert(flmDenominator)
// 120. | end

public class DenomPoscodeAdverseeventevalcodeInPast23MnthsTest extends
		AbstractActiveMeasuresRuleTemplateTest {

	private static int MEASURE_ID = 160;

	@Override
	public DefaultStandardRuleTemplate getRuleInstance() {

		AttributeFragmentTemplate posAttributeTemplate = new AttributeFragmentTemplate(
				"placeOfServiceCategoryCode",
				TypeDescription.getTypeDescription(Set.class, Character.class));
		AttributeFragmentTemplate adverseEventEvaluationCodeTemplate = new AttributeFragmentTemplate(
				"adverseEventEvaluationCode",
				TypeDescription.getTypeDescription(Set.class, Character.class));

		AnyClassMultipleAttributeEvaluationFragmentTemplate condition = ActiveMeasureUtilFunctions
				.createAnyClassMultipleAttributeEvaluationFragmentTemplate(
						MedicalCase.class,
						"$medicalCase",
						ActiveMeasureUtilFunctions
								.createAnyAttributeSingleComparisonFragmentTemplate(
										new AttributeFragmentTemplate(
												"medicalCaseEndDate",
												TypeDescription
														.getTypeDescription(Date.class)),
										ActiveMeasureGlobalContainer.INSTANCE
												.getByDefinition(ActiveMeasureGlobalDefinition.MONTHS_1_BEFORE_MEASUREMENT_END_DATE),
										CommonOperators.LESS_THAN, null),
						UtilFunctions
								.createMultipleAttributeEvaluationFragmentTemplate(
										Connector.OR,
										UtilFunctions
												.createAnyAttributeSingleComparisonFragmentTemplate(
														posAttributeTemplate,
														ActiveMeasureUtilFunctions
																.createCharacterLiteralExpressionTemplate('E'),
														CommonOperators.EQUAL_TO,
														null),
										UtilFunctions
												.createAnyAttributeSingleComparisonFragmentTemplate(
														posAttributeTemplate,
														ActiveMeasureUtilFunctions
																.createCharacterLiteralExpressionTemplate('O'),
														CommonOperators.EQUAL_TO,
														null),
										UtilFunctions
												.createAnyAttributeSingleComparisonFragmentTemplate(
														posAttributeTemplate,
														ActiveMeasureUtilFunctions
																.createCharacterLiteralExpressionTemplate('F'),
														CommonOperators.EQUAL_TO,
														null)),
						ActiveMeasureUtilFunctions
								.createAnyAttributeSingleComparisonFragmentTemplate(
										new AttributeFragmentTemplate(
												"medicalCaseEndDate",
												TypeDescription
														.getTypeDescription(Date.class)),
										ActiveMeasureGlobalContainer.INSTANCE
												.getByDefinition(ActiveMeasureGlobalDefinition.MONTHS_24_BEFORE_MEASUREMENT_END_DATE),
										CommonOperators.GREATER_THAN_OR_EQUAL_TO,
										null),
						UtilFunctions.createObjectNullCheckFragment(
								posAttributeTemplate.getAttributeName(),
								CommonOperators.NOT_EQUAL_TO, Character.class),
						UtilFunctions
								.createMultipleAttributeEvaluationFragmentTemplate(
										Connector.OR,
										UtilFunctions
												.createAnyAttributeSingleComparisonFragmentTemplate(
														adverseEventEvaluationCodeTemplate,
														ActiveMeasureUtilFunctions
																.createCharacterLiteralExpressionTemplate('B'),
														CommonOperators.EQUAL_TO,
														null),
										UtilFunctions
												.createAnyAttributeSingleComparisonFragmentTemplate(
														adverseEventEvaluationCodeTemplate,
														ActiveMeasureUtilFunctions
																.createCharacterLiteralExpressionTemplate('C'),
														CommonOperators.EQUAL_TO,
														null)));
		FactLevelMeasureDenominatorCreationTemplate action = ActiveMeasureUtilFunctions
				.createFactLevelMeasureDenominatorCreationTemplate(MEASURE_ID,
						condition.getVariableExpression());
		DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
				.createDefaultStandardRuleTemplate(
						"DENOM_POSCODE_ADVERSEEVENTEVALCODE_IN_PAST_23_MNTHS",
						action, condition);
		standardRuleInstance
				.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2016.aetnameasures.Oupatient_Procedure_Adverse_Event_Rate");
		return standardRuleInstance;
	}
}

/**
 * 
 */
package net.ahm.activemeasure.medicalcase.year2016.aetnameasures.Physician_Day30_Readmission_Rate_All_Specialities;

import java.util.Date;
import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.FactLevelMeasureDenominatorCreationTemplate;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.medicalcase.MedicalCase;
import net.ahm.careengine.domain.medicalcase.MedicalCaseProviderInfo;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule
 * DENOM_POS_FACILITYEPDB_CD_IN_PAST_23_MNTHS
 */

// 74. | rule "DENOM_POS_FACILITYEPDB_CD_IN_PAST_23_MNTHS"
// 75. | dialect "mvel"
// 76. | when
// 77. | $medicalCase : MedicalCase( placeOfServiceCategoryCode == "I" ,
// medicalCaseEndDate < months1BeforeMeasurementEndDate , medicalCaseEndDate >=
// months24BeforeMeasurementEndDate , facility.EPDBSpecialtyCategoryCode ==
// "WHOS" || == "WCH" , diagnosisRelatedGroupElements contains 9222 || contains
// 9223 || contains 9224 || contains 9225 || contains 9226 || contains 9227 ||
// contains 9228 || contains 9229 || contains 9230 || contains 9231 || contains
// 9232 || contains 9233 || contains 9234 || contains 9235 || contains 9236 ||
// contains 9237 || contains 9238 || contains 9239 || contains 9240 || contains
// 9241 || contains 9242 )
// 78. | then
// 79. | FactLevelMeasureDenominator flmDenominator=
// factLevelMeasureBuilder.newFactLevelMeasureDenominatorWithPrimaryFact(157,$medicalCase);
// insert(flmDenominator)
// 80. | end

public class DenomPosFacilityepdbCdInPast23MnthsTest extends
		AbstractActiveMeasuresRuleTemplateTest {

	private static int MEASURE_ID = 157;

	@Override
	public DefaultStandardRuleTemplate getRuleInstance() {
		AttributeFragmentTemplate drgElementsAttributeTemplate = new AttributeFragmentTemplate(
				"diagnosisRelatedGroupElements",
				TypeDescription.getTypeDescription(Set.class, Integer.class));

		AnyClassMultipleAttributeEvaluationFragmentTemplate condition = ActiveMeasureUtilFunctions
				.createAnyClassMultipleAttributeEvaluationFragmentTemplate(
						MedicalCase.class,
						"$medicalCase",
						ActiveMeasureUtilFunctions
								.createAnyAttributeSingleComparisonFragmentTemplate(
										new AttributeFragmentTemplate(
												"placeOfServiceCategoryCode",
												TypeDescription
														.getTypeDescription(String.class)),
										UtilFunctions
												.createStringLiteralExpressionFragmentTemplate("I"),
										CommonOperators.EQUAL_TO, null),
						ActiveMeasureUtilFunctions
								.createAnyAttributeSingleComparisonFragmentTemplate(
										new AttributeFragmentTemplate(
												"medicalCaseEndDate",
												TypeDescription
														.getTypeDescription(Date.class)),
										ActiveMeasureGlobalContainer.INSTANCE
												.getByDefinition(ActiveMeasureGlobalDefinition.MONTHS_1_BEFORE_MEASUREMENT_END_DATE),
										CommonOperators.LESS_THAN, null),
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
						ActiveMeasureUtilFunctions
								.createMultipleAttributeEvaluationFragmentTemplate(
										Connector.OR,
										ActiveMeasureUtilFunctions
												.createAnyAttributeSingleComparisonFragmentTemplate(
														new AttributeFragmentTemplate(
																"facility",
																TypeDescription
																		.getTypeDescription(MedicalCaseProviderInfo.class),
																new AttributeFragmentTemplate(
																		"EPDBSpecialtyCategoryCode",
																		TypeDescription
																				.getTypeDescription(String.class))),
														UtilFunctions
																.createStringLiteralExpressionFragmentTemplate("WHOS"),
														CommonOperators.EQUAL_TO,
														null),
										ActiveMeasureUtilFunctions
												.createAnyAttributeSingleComparisonFragmentTemplate(
														new AttributeFragmentTemplate(
																"facility",
																TypeDescription
																		.getTypeDescription(MedicalCaseProviderInfo.class),
																new AttributeFragmentTemplate(
																		"EPDBSpecialtyCategoryCode",
																		TypeDescription
																				.getTypeDescription(String.class))),
														UtilFunctions
																.createStringLiteralExpressionFragmentTemplate("WCH"),
														CommonOperators.EQUAL_TO,
														null)),
										UtilFunctions
												.createMultipleAttributeEvaluationFragmentTemplate(
														Connector.OR,
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9222),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9223),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9224),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9225),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9226),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9227),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9228),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9229),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9230),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9231),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9232),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9233),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9234),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9235),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9236),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9237),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9238),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9239),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9240),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9241),
																		CommonOperators.CONTAINS,
																		null),
														UtilFunctions
																.createAnyAttributeSingleComparisonFragmentTemplate(
																		drgElementsAttributeTemplate,
																		ActiveMeasureUtilFunctions
																				.createIntegerLiteralFragmentTemplate(9242),
																		CommonOperators.CONTAINS,
																		null)));

		FactLevelMeasureDenominatorCreationTemplate action = ActiveMeasureUtilFunctions
				.createFactLevelMeasureDenominatorCreationTemplate(MEASURE_ID,
						condition.getVariableExpression());
		DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
				.createDefaultStandardRuleTemplate(
						"DENOM_POS_FACILITYEPDB_CD_IN_PAST_23_MNTHS", action,
						condition);
		standardRuleInstance
				.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2016.aetnameasures.Physician_Day30_Readmission_Rate_All_Specialities");

		return standardRuleInstance;
	}
}

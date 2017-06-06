package net.ahm.activemeasure.medicalcase.year2015.aetnameasures.Average_Length_of_Stay_2015;

import java.util.Date;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.medicalcase.MedicalCase;
import net.ahm.careengine.domain.medicalcase.MedicalCaseProviderInfo;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NotExistenceExpression;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule DENOM_AVERAGE_LENGTH_OF_STAY
 */

//1.  |   rule "DENOM_AVERAGE_LENGTH_OF_STAY"
//2.  |       dialect "mvel"
//3.  |       when
//4.  |           $medicalCase : MedicalCase( medicalCaseEndDate <= measurementEndDate , facility != null , facility.EPDBSpecialtyCategoryCode == "WHOS" || == "WCH" , placeOfServiceCategoryCode == "I" , medicalCaseEndDate >= months12BeforeMeasurementEndDate , placeOfServiceCategoryCode != null )
//5.  |       then
//6.  |           FactLevelMeasureDenominator flmDenominator= factLevelMeasureBuilder.newFactLevelMeasureDenominatorWithPrimaryFact(86,$medicalCase); insert(flmDenominator)
//7.  |   end

public class DenomAverageLengthOfStayTest extends
        AbstractActiveMeasuresRuleTemplateTest {
    private static int MEASURE_ID = 86;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AttributeFragmentTemplate facilityEPDBSpecialtyCategoryCode = new AttributeFragmentTemplate(
                "facility",
                TypeDescription
                        .getTypeDescription(MedicalCaseProviderInfo.class),
                new AttributeFragmentTemplate("EPDBSpecialtyCategoryCode",
                        TypeDescription.getTypeDescription(String.class)));

        AttributeFragmentTemplate medicalCaseEndDateAttribute = new AttributeFragmentTemplate(
                "medicalCaseEndDate",
                TypeDescription.getTypeDescription(Date.class));

        AttributeFragmentTemplate placeOfServiceCategoryCodeAttribute = new AttributeFragmentTemplate(
                "placeOfServiceCategoryCode",
                TypeDescription
                        .getTypeDescription(Character.class));

        AnyClassMultipleAttributeEvaluationFragmentTemplate medicalCaseTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MedicalCase.class,
                        "$medicalCase",
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        medicalCaseEndDateAttribute,
                                        ActiveMeasureUtilFunctions
                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MEASUREMENT_END_DATE),
                                        CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                        null), ActiveMeasureUtilFunctions
                                .createObjectNullCheckFragment("facility",
                                        CommonOperators.NOT_EQUAL_TO,
                                        MedicalCaseProviderInfo.class),
                        UtilFunctions
                                .createMultipleAttributeEvaluationFragmentTemplate(
                                        Connector.OR,
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        facilityEPDBSpecialtyCategoryCode,
                                                        UtilFunctions
                                                                .createStringLiteralExpressionFragmentTemplate("WHOS"),
                                                        CommonOperators.EQUAL_TO,
                                                        null),
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        facilityEPDBSpecialtyCategoryCode,
                                                        UtilFunctions
                                                                .createStringLiteralExpressionFragmentTemplate("WCH"),
                                                        CommonOperators.EQUAL_TO,
                                                        null)),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        placeOfServiceCategoryCodeAttribute,
                                        UtilFunctions
                                                .createCharacterLiteralExpressionTemplate('I'),
                                        CommonOperators.EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        medicalCaseEndDateAttribute,
                                        ActiveMeasureUtilFunctions
                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MONTHS_12_BEFORE_MEASUREMENT_END_DATE),
                                        CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                        null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        placeOfServiceCategoryCodeAttribute,
                                        new NotExistenceExpression(),
                                        CommonOperators.NOT_EQUAL_TO, null));

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "DENOM_AVERAGE_LENGTH_OF_STAY",
                        ActiveMeasureUtilFunctions
                                .createFactLevelMeasureDenominatorCreationTemplate(
                                        MEASURE_ID, medicalCaseTemplate
                                                .getVariableExpression()),
                        medicalCaseTemplate);
        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2015.aetnameasures.Average_Length_of_Stay_2015");
        return standardRuleInstance;
    }
}

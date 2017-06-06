package net.ahm.activemeasure.medicalcase.year2015.aetnameasures.Day30_Readmission_Rate_2015;

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
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule DENOM_30DAY_READMISSION_11_MNTHS
 */

//71.	|	rule "DENOM_30DAY_READMISSION_11_MNTHS"
//72.	|	    dialect "mvel"
//73.	|	    when
//74.	|	        $medicalCase : MedicalCase( medicalCaseEndDate < months1BeforeMeasurementEndDate , facility != null , facility.EPDBSpecialtyCategoryCode == "WHOS" || == "WCH" , placeOfServiceCategoryCode == "I" , medicalCaseEndDate >= months12BeforeMeasurementEndDate )
//75.	|	    then
//76.	|	        FactLevelMeasureDenominator flmDenominator= factLevelMeasureBuilder.newFactLevelMeasureDenominatorWithPrimaryFact(84,$medicalCase); insert(flmDenominator)
//77.	|	end


public class Denom30dayReadmission11MnthsTest extends
        AbstractActiveMeasuresRuleTemplateTest {
    private static final int MEASURE_ID = 84;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AttributeFragmentTemplate facilityEPDBSpecialtyCategoryCodeAttribute = new AttributeFragmentTemplate(
                "facility",
                TypeDescription
                        .getTypeDescription(MedicalCaseProviderInfo.class),
                new AttributeFragmentTemplate("EPDBSpecialtyCategoryCode",
                        TypeDescription.getTypeDescription(String.class)));

        AnyClassMultipleAttributeEvaluationFragmentTemplate flmTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MedicalCase.class,
                        "$medicalCase",
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                "medicalCaseEndDate",
                                                TypeDescription
                                                        .getTypeDescription(Date.class)),
                                        ActiveMeasureUtilFunctions
                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MONTHS_1_BEFORE_MEASUREMENT_END_DATE),
                                        CommonOperators.LESS_THAN, null),
                        ActiveMeasureUtilFunctions
                                .createObjectNullCheckFragment("facility",
                                        CommonOperators.NOT_EQUAL_TO,
                                        MedicalCaseProviderInfo.class),
                        UtilFunctions
                                .createMultipleAttributeEvaluationFragmentTemplate(
                                        Connector.OR,
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        facilityEPDBSpecialtyCategoryCodeAttribute,
                                                        UtilFunctions
                                                                .createStringLiteralExpressionFragmentTemplate("WHOS"),
                                                        CommonOperators.EQUAL_TO,
                                                        null),
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        facilityEPDBSpecialtyCategoryCodeAttribute,
                                                        UtilFunctions
                                                                .createStringLiteralExpressionFragmentTemplate("WCH"),
                                                        CommonOperators.EQUAL_TO,
                                                        null)),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                "placeOfServiceCategoryCode",
                                                TypeDescription
                                                        .getTypeDescription(Character.class)),
                                        ActiveMeasureUtilFunctions
                                                .createCharacterLiteralExpressionTemplate('I'),
                                        CommonOperators.EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                "medicalCaseEndDate",
                                                TypeDescription
                                                        .getTypeDescription(Date.class)),
                                        ActiveMeasureUtilFunctions
                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MONTHS_12_BEFORE_MEASUREMENT_END_DATE),
                                        CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                        null));

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "DENOM_30DAY_READMISSION_11_MNTHS",
                        ActiveMeasureUtilFunctions
                                .createFactLevelMeasureDenominatorCreationTemplate(
                                        MEASURE_ID,
                                        flmTemplate.getVariableExpression()),
                        flmTemplate);
        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2015.aetnameasures.Day30_Readmission_Rate_2015");
        return standardRuleInstance;
    }
}

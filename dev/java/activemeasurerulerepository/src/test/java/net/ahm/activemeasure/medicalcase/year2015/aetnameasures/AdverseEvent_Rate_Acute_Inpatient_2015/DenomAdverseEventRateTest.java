package net.ahm.activemeasure.medicalcase.year2015.aetnameasures.AdverseEvent_Rate_Acute_Inpatient_2015;

import java.util.Date;

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
 * Test case to generate JSON/DRL representation of the rule DENOM_ADVERSE_EVENT_RATE
 */

//69.	|	rule "DENOM_ADVERSE_EVENT_RATE"
//70.	|	    dialect "mvel"
//71.	|	    when
//72.	|	        $medicalCase : MedicalCase( medicalCaseEndDate >= months12BeforeMeasurementEndDate , facility != null , facility.EPDBSpecialtyCategoryCode == "WHOS" || == "WCH" , placeOfServiceCategoryCode == "I" , adverseEventExpected != null , adverseEventEvaluationCode == "1" || == "2" || == "4" , medicalCaseEndDate <= measurementEndDate )
//73.	|	    then
//74.	|	        FactLevelMeasureDenominator flmDenominator= factLevelMeasureBuilder.newFactLevelMeasureDenominatorWithPrimaryFact(85,$medicalCase); insert(flmDenominator)
//75.	|	end

public class DenomAdverseEventRateTest extends
        AbstractActiveMeasuresRuleTemplateTest {
    private static int MEASURE_ID = 85;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
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
                                                .getByDefinition(ActiveMeasureGlobalDefinition.MONTHS_12_BEFORE_MEASUREMENT_END_DATE),
                                        CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                        null),
                        ActiveMeasureUtilFunctions
                                .createObjectNullCheckFragment("facility",
                                        CommonOperators.NOT_EQUAL_TO,
                                        String.class),
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
                                .createObjectNullCheckFragment(
                                        "adverseEventExpected",
                                        CommonOperators.NOT_EQUAL_TO,
                                        String.class),
                        ActiveMeasureUtilFunctions
                                .createMultipleAttributeEvaluationFragmentTemplate(
                                        Connector.OR,
                                        ActiveMeasureUtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        new AttributeFragmentTemplate(
                                                                "adverseEventEvaluationCode",
                                                                TypeDescription
                                                                        .getTypeDescription(Character.class)),
                                                        ActiveMeasureUtilFunctions
                                                                .createCharacterLiteralExpressionTemplate('1'),
                                                        CommonOperators.EQUAL_TO,
                                                        null),
                                        ActiveMeasureUtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        new AttributeFragmentTemplate(
                                                                "adverseEventEvaluationCode",
                                                                TypeDescription
                                                                        .getTypeDescription(Character.class)),
                                                        ActiveMeasureUtilFunctions
                                                                .createCharacterLiteralExpressionTemplate('2'),
                                                        CommonOperators.EQUAL_TO,
                                                        null),
                                        ActiveMeasureUtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        new AttributeFragmentTemplate(
                                                                "adverseEventEvaluationCode",
                                                                TypeDescription
                                                                        .getTypeDescription(Character.class)),
                                                        ActiveMeasureUtilFunctions
                                                                .createCharacterLiteralExpressionTemplate('4'),
                                                        CommonOperators.EQUAL_TO,
                                                        null)),
                        ActiveMeasureUtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                "medicalCaseEndDate",
                                                TypeDescription
                                                        .getTypeDescription(Date.class)),
                                        ActiveMeasureGlobalContainer.INSTANCE
                                                .getByDefinition(ActiveMeasureGlobalDefinition.MEASUREMENT_END_DATE),
                                        CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                        null));

        FactLevelMeasureDenominatorCreationTemplate action = ActiveMeasureUtilFunctions
                .createFactLevelMeasureDenominatorCreationTemplate(MEASURE_ID,
                        condition.getVariableExpression());

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate("DENOM_ADVERSE_EVENT_RATE",
                        action, condition);
        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2015.aetnameasures.AdverseEvent_Rate_Acute_Inpatient_2015");
        return standardRuleInstance;
    }
}

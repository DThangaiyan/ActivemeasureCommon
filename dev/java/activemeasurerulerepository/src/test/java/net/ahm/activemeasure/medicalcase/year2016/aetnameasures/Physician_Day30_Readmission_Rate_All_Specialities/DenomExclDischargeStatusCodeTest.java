package net.ahm.activemeasure.medicalcase.year2016.aetnameasures.Physician_Day30_Readmission_Rate_All_Specialities;

import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.medicalcase.MedicalCase;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule DENOM_EXCL_DISCHARGE_STATUS_CODE
 */
//1.	|	rule "DENOM_EXCL_DISCHARGE_STATUS_CODE"
//2.	|	    dialect "mvel"
//3.	|	    when
//4.	|	        $flmDenom : FactLevelMeasureDenominator( measureId == 157 , $primaryOrigEvent : primaryOriginationFact != null )
//5.	|	        MedicalCase( dischargeStatusCode in ( "02", "07", "61", "20", "21", "22", "23", "24", "25", "26", "28", "29", "41", "42" ) ) from $primaryOrigEvent
//6.	|	    then
//7.	|	        $flmDenom.setExcludedFromDenominator( true );
//8.	|	end

public class DenomExclDischargeStatusCodeTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    private static final int      MEASURE_ID          = 157;
    private static final String[] DISCHARGE_STATUS_CODE = { "02", "07", "61",
            "20", "21", "22", "23", "24", "25", "26", "28", "29", "41", "42" };

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

        AttributeFragmentTemplate dischargeStatusCodeAttributeTemplate = new AttributeFragmentTemplate(
                "dischargeStatusCode",
                TypeDescription.getTypeDescription(Set.class, String.class));
        AnyClassMultipleAttributeEvaluationFragmentTemplate medicalCaseTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MedicalCase.class,
                        null,
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        dischargeStatusCodeAttributeTemplate,
                                        ActiveMeasureUtilFunctions
                                                .createStringCollectionLiteralFragmentTemplate(DISCHARGE_STATUS_CODE),
                                        CommonOperators.IN, null));
        FromEvaluationFragmentTemplate fromEvaluationFragmentTemplate = new FromEvaluationFragmentTemplate(
                medicalCaseTemplate,
                primaryFactExpr.getVariableExpression());

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "DENOM_EXCL_DISCHARGE_STATUS_CODE",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorExclusionToTrueTemplate(flmDenomTemplate
                                        .getVariableExpression()),
                        flmDenomTemplate,
                        fromEvaluationFragmentTemplate);
        standardRuleInstance.getConditions().add(flmDenomTemplate);

        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2016.aetnameasures.Physician_Day30_Readmission_Rate_All_Specialities");
        return standardRuleInstance;
    }
}

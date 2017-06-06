package net.ahm.activemeasure.medicalcase.year2015.aetnameasures.Day30_Readmission_Rate_2015;

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
import net.ahm.rulesapp.templates.libraries.NotExistenceExpression;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule DENOM_EXCL_DISCHARGE_STATUS_CODE
 */

//106.	|	rule "DENOM_EXCL_DISCHARGE_STATUS_CODE"
//107.	|	    dialect "mvel"
//108.	|	    when
//109.	|	        $flmDenom : FactLevelMeasureDenominator( measureId == 84 , $primaryOrigEvent : primaryOriginationFact != null )
//110.	|	        $medicalCase : MedicalCase( this == $primaryOrigEvent , dischargeStatusCode in ( "02", "07", "61", "20", "21", "22", "23", "24", "25", "26", "28", "29", "41", "42" ) )
//111.	|	    then
//112.	|	        $flmDenom.setExcludedFromDenominator( true );
//113.	|	end

public class DenomExclDischargeStatusCodeTest extends
        AbstractActiveMeasuresRuleTemplateTest {
    private static final int MEASURE_ID = 84;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactExpr = ActiveMeasureUtilFunctions
                .createAnyAttributeSingleComparisonFragmentTemplate(
                        new AttributeFragmentTemplate(
                                ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                                TypeDescription.getTypeDescription(Fact.class)),
                        new NotExistenceExpression(),
                        CommonOperators.NOT_EQUAL_TO, "$primaryOrigEvent");

        AnyClassMultipleAttributeEvaluationFragmentTemplate flmDenomTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR,
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(MEASURE_ID),
                        primaryFactExpr);

        AnyClassMultipleAttributeEvaluationFragmentTemplate medicalCaseTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MedicalCase.class,
                        null,
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                "dischargeStatusCode",
                                                TypeDescription
                                                        .getTypeDescription(String.class)),
                                        UtilFunctions
                                                .createStringCollectionLiteralFragmentTemplate(
                                                        "02", "07", "61", "20",
                                                        "21", "22", "23", "24",
                                                        "25", "26", "28", "29",
                                                        "41", "42"),
                                        CommonOperators.IN, null));

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "DENOM_EXCL_DISCHARGE_STATUS_CODE",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorExclusionToTrueTemplate(flmDenomTemplate
                                        .getVariableExpression()),
                        flmDenomTemplate,
                        new FromEvaluationFragmentTemplate(medicalCaseTemplate,
                                primaryFactExpr.getVariableExpression()));
        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2015.aetnameasures.Day30_Readmission_Rate_2015");

        return standardRuleInstance;
    }
}

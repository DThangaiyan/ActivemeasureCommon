package net.ahm.activemeasure.medicalcase.year2015.aetnameasures.AdverseEvent_Rate_Acute_Inpatient_2015;

import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
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
 * Test case to generate JSON/DRL representation of the rule DENOM_EXCL_DRG_CODE_ADVERSE_EVENT_RATE
 */

//100.	|	rule "DENOM_EXCL_DRG_CODE_ADVERSE_EVENT_RATE"
//101.	|	    dialect "mvel"
//102.	|	    when
//103.	|	        $flDenom : FactLevelMeasureDenominator( measureId == 85 , $primaryOriginEvent : primaryOriginationFact != null )
//104.	|	        $medicalCase : MedicalCase( this == $primaryOriginEvent , diagnosisRelatedGroupElements contains 9247 || contains 9243 )
//105.	|	    then
//106.	|	        $flDenom.setExcludedFromDenominator( true );
//107.	|	end

public class DenomExclDrgCodeAdverseEventRateTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    private static final int MEASURE_ID = 85;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactExpr = ActiveMeasureUtilFunctions
                .createFactLevelDenominatorPrimaryFactNullCheck();

        AnyClassMultipleAttributeEvaluationFragmentTemplate flmDenomTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR,
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(MEASURE_ID),
                        primaryFactExpr);

        AttributeFragmentTemplate drgElementsAttributeTemplate = new AttributeFragmentTemplate(
                "diagnosisRelatedGroupElements",
                TypeDescription.getTypeDescription(Set.class, Integer.class));
        AnyClassMultipleAttributeEvaluationFragmentTemplate relatedMedicalCaseTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MedicalCase.class,
                        "$medicalCase",
                        UtilFunctions
                                .createMultipleAttributeEvaluationFragmentTemplate(
                                        Connector.OR,
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        drgElementsAttributeTemplate,
                                                        ActiveMeasureUtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(9247),
                                                        CommonOperators.CONTAINS,
                                                        null),
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        drgElementsAttributeTemplate,
                                                        ActiveMeasureUtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(9243),
                                                        CommonOperators.CONTAINS,
                                                        null)));

        // rewritten to use a from clause to be more performant rather than
        // using this == $primaryOrigEvent
        FromEvaluationFragmentTemplate fromEvaluationFragmentTemplate = new FromEvaluationFragmentTemplate(
                relatedMedicalCaseTemplate,
                primaryFactExpr.getVariableExpression());

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "DENOM_EXCL_DRG_CODE_ADVERSE_EVENT_RATE",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorExclusionToTrueTemplate(flmDenomTemplate
                                        .getVariableExpression()),
                        flmDenomTemplate,
                        fromEvaluationFragmentTemplate);

        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2015.aetnameasures.AdverseEvent_Rate_Acute_Inpatient_2015");
        return standardRuleInstance;
    }
}

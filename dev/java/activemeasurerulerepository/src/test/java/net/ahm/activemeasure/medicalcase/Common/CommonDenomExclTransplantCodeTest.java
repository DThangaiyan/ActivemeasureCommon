package net.ahm.activemeasure.medicalcase.Common;

import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
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

//rule "COMMON_DENOM_EXCL_TRANSPLANT_CODES"
//2.	|	    dialect "mvel"
//3.	|	    when
//4.	|	        $flmDenom : FactLevelMeasureDenominator( measureId in ( 157, 159 ) , $primaryOrigEvent : primaryOriginationFact != null )
//5.	|	        $medicalCase : MedicalCase( this == $primaryOrigEvent , diagnosisRelatedGroupElements contains 9243 )
//6.	|	    then
//7.	|	        $flmDenom.setExcludedFromDenominator( true );
//8.	|	end


public class CommonDenomExclTransplantCodeTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactExpr = ActiveMeasureUtilFunctions
                .createFactLevelDenominatorPrimaryFactNullCheck();

        AnyClassMultipleAttributeEvaluationFragmentTemplate flmDenomTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR,
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(
                                        157, 159), primaryFactExpr);

        // Using from clause rather than this==$primaryFact for better
        // performance
        AnyClassMultipleAttributeEvaluationFragmentTemplate medicalCaseTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MedicalCase.class,
                        null,
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                "diagnosisRelatedGroupElements",
                                                TypeDescription
                                                        .getTypeDescription(
                                                                Set.class,
                                                                Integer.class)),
                                        UtilFunctions
                                                .createIntegerLiteralFragmentTemplate(9243),
                                        CommonOperators.CONTAINS, null));

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "COMMON_DENOM_EXCL_TRANSPLANT_CODES",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorExclusionToTrueTemplate(flmDenomTemplate
                                        .getVariableExpression()),
                        flmDenomTemplate,
                        new FromEvaluationFragmentTemplate(medicalCaseTemplate,
                                primaryFactExpr.getVariableExpression()));
        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.Common");

        return standardRuleInstance;
    }
}

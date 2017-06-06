package net.ahm.activemeasure.medicalcase.Common;

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

//123.	|	rule "COMMON_DENOM_EXCL_MEDICALCASE_PAYMENT_CODE"
//124.	|	    dialect "mvel"
//125.	|	    when
//126.	|	        $flmDenom : FactLevelMeasureDenominator( measureId in ( 84, 157, 85, 86, 159, 160 ) , $primaryOrigEvent : primaryOriginationFact != null )
//127.	|	        $medicalCase : MedicalCase( this == $primaryOrigEvent , medicalCasePaymentCode == "D" || == "X" )
//128.	|	    then
//129.	|	        $flmDenom.setExcludedFromDenominator( true );
//130.	|	end

public class CommonDenomExclMedicalcasePaymentCodeTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactExpression = ActiveMeasureUtilFunctions
                .createFactLevelDenominatorPrimaryFactNullCheck();

        AnyClassMultipleAttributeEvaluationFragmentTemplate flmDenomTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR,
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(84,
                                        157, 85, 86, 160),
                        primaryFactExpression);

        AttributeFragmentTemplate medicalCasePaymentCodeAttribute = new AttributeFragmentTemplate(
                "medicalCasePaymentCode",
                TypeDescription.getTypeDescription(Character.class));

        // using a from clause for better performance over this==$primaryEvent
        AnyClassMultipleAttributeEvaluationFragmentTemplate medicalCaseTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MedicalCase.class,
                        null,
                        UtilFunctions
                                .createMultipleAttributeEvaluationFragmentTemplate(
                                        Connector.OR,
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        medicalCasePaymentCodeAttribute,
                                                        UtilFunctions
                                                                .createCharacterLiteralExpressionTemplate('D'),
                                                        CommonOperators.EQUAL_TO,
                                                        null),
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        medicalCasePaymentCodeAttribute,
                                                        UtilFunctions
                                                                .createCharacterLiteralExpressionTemplate('X'),
                                                        CommonOperators.EQUAL_TO,
                                                        null)));

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "COMMON_DENOM_EXCL_MEDICALCASE_PAYMENT_CODE",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorExclusionToTrueTemplate(flmDenomTemplate
                                        .getVariableExpression()),
                        flmDenomTemplate,
                        new FromEvaluationFragmentTemplate(medicalCaseTemplate,
                                primaryFactExpression.getVariableExpression()));
        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.Common");

        return standardRuleInstance;
    }
}

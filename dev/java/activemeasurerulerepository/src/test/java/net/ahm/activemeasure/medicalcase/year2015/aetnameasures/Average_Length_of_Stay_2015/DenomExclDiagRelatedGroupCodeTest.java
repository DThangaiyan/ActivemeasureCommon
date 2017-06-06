package net.ahm.activemeasure.medicalcase.year2015.aetnameasures.Average_Length_of_Stay_2015;

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
 * Test case to generate JSON/DRL representation of the rule
 * DENOM_EXCL_DIAG_RELATED_GROUP_CODE
 */

//94.	|	rule "DENOM_EXCL_DIAG_RELATED_GROUP_CODE"
//95.	|	    dialect "mvel"
//96.	|	    when
//97.	|	        $flmDenom : FactLevelMeasureDenominator( measureId == 86 , $primaryOriginationFact : primaryOriginationFact != null )
//98.	|	        $medicalCase : MedicalCase( this == $primaryOriginationFact , diagnosisRelatedGroupElements contains 9243 || contains 9244 ) from $primaryOriginationFact
//99.	|	    then
//100.	|	        $flmDenom.setExcludedFromDenominator( true );
//101.	|	end

public class DenomExclDiagRelatedGroupCodeTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    private static final Integer MEASURE_ID = 86;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactExpression = ActiveMeasureUtilFunctions
                .createFactLevelDenominatorPrimaryFactNullCheck();

        AnyClassMultipleAttributeEvaluationFragmentTemplate flmDenomTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR,
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(MEASURE_ID),
                        primaryFactExpression);

        AttributeFragmentTemplate diagnosisRelatedGroupElementsAttribute = new AttributeFragmentTemplate(
                "diagnosisRelatedGroupElements",
                TypeDescription.getTypeDescription(Set.class, Integer.class));
        AnyClassMultipleAttributeEvaluationFragmentTemplate medicalCaseTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MedicalCase.class,
                        null,
                        UtilFunctions
                                .createMultipleAttributeEvaluationFragmentTemplate(
                                        Connector.OR,
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        diagnosisRelatedGroupElementsAttribute,
                                                        UtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(9243),
                                                        CommonOperators.CONTAINS,
                                                        null),
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        diagnosisRelatedGroupElementsAttribute,
                                                        UtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(9244),
                                                        CommonOperators.CONTAINS,
                                                        null)));

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "DENOM_EXCL_DIAG_RELATED_GROUP_CODE",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorExclusionToTrueTemplate(flmDenomTemplate
                                        .getVariableExpression()),
                        flmDenomTemplate,
                        new FromEvaluationFragmentTemplate(medicalCaseTemplate,
                                primaryFactExpression.getVariableExpression()));
        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2015.aetnameasures.Average_Length_of_Stay_2015");
        return standardRuleInstance;
    }
}

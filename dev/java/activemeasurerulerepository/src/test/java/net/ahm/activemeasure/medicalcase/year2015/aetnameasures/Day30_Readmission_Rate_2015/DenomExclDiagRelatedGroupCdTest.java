package net.ahm.activemeasure.medicalcase.year2015.aetnameasures.Day30_Readmission_Rate_2015;

import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.fact.Fact;
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
 * Test case to generate JSON/DRL representation of the rule DENOM_EXCL_DIAG_RELATED_GROUP_CD
 */

//116.	|	rule "DENOM_EXCL_DIAG_RELATED_GROUP_CD"
//117.	|	    dialect "mvel"
//118.	|	    when
//119.	|	        $flmDenom : FactLevelMeasureDenominator( measureId == 84 , $primaryOrigEvent : primaryOriginationFact != null )
//120.					$medicalCase : MedicalCase( this == $primaryOrigEvent , diagnosisRelatedGroupElements contains 9243 || contains 9244 || contains 9248 || contains 9245 || contains 9246 || contains 9247 )
//121.	|	    then
//122.	|	        $flmDenom.setExcludedFromDenominator( true );
//123.	|	end

public class DenomExclDiagRelatedGroupCdTest extends
        AbstractActiveMeasuresRuleTemplateTest {
    private static final int MEASURE_ID = 84;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyAttributeSingleComparisonFragmentTemplate<?> primaryFactExpr = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment(
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                        CommonOperators.NOT_EQUAL_TO, Fact.class);
        primaryFactExpr.setVariableName("$primaryOrigEvent");

        AnyClassMultipleAttributeEvaluationFragmentTemplate flmDenomTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR,
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(MEASURE_ID),
                        primaryFactExpr);

        AttributeFragmentTemplate drgElementsAttribute = new AttributeFragmentTemplate(
                "diagnosisRelatedGroupElements",
                TypeDescription.getTypeDescription(Set.class, Integer.class));

        AnyClassMultipleAttributeEvaluationFragmentTemplate medicalCaseFragment = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MedicalCase.class,
                        null,
                        UtilFunctions
                                .createMultipleAttributeEvaluationFragmentTemplate(
                                        Connector.OR,
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        drgElementsAttribute,
                                                        UtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(9243),
                                                        CommonOperators.CONTAINS,
                                                        null),
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        drgElementsAttribute,
                                                        UtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(9244),
                                                        CommonOperators.CONTAINS,
                                                        null),
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        drgElementsAttribute,
                                                        UtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(9248),
                                                        CommonOperators.CONTAINS,
                                                        null),
                                        UtilFunctions
                                                  .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        drgElementsAttribute,
                                                        UtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(9245),
                                                        CommonOperators.CONTAINS,
                                                        null),
                                        UtilFunctions
                                                  .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        drgElementsAttribute,
                                                        UtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(9246),
                                                        CommonOperators.CONTAINS,
                                                        null),
                                        UtilFunctions
                                                  .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        drgElementsAttribute,
                                                        UtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(9247),
                                                        CommonOperators.CONTAINS,
                                                        null)));

        // using a from clause rather than this==$primaryFact since it is more
        // performant
        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "DENOM_EXCL_DIAG_RELATED_GROUP_CD",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorExclusionToTrueTemplate(flmDenomTemplate
                                        .getVariableExpression()),
                        flmDenomTemplate,
                        new FromEvaluationFragmentTemplate(medicalCaseFragment,
                                primaryFactExpr.getVariableExpression()));

        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2015.aetnameasures.Day30_Readmission_Rate_2015");
        return standardRuleInstance;
    }
}

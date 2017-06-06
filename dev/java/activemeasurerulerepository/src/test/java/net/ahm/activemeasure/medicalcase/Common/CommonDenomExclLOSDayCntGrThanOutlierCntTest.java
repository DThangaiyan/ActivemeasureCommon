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
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NotExistenceExpression;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule COMMON_DENOM_EXCL_LOS_COUNT_GR_THAN_OUTLIER_LOS_COUNT
 */
//rule "COMMON_DENOM_EXCL_LOS_COUNT_GR_THAN_OUTLIER_LOS_COUNT"
//dialect "mvel"
//when
//	$flDenom : FactLevelMeasureDenominator( measureId in ( 84, 157, 85, 86, 159 ) , $primaryOriginEvent : primaryOriginationFact != null )
//	$medicalcase : MedicalCase( outlierLOSDayCount != null && > 0 , lengthOfStayDayCount != null && > 0 , this == $primaryOriginEvent , lengthOfStayDayCount > 	 $medicalcase.outlierLOSDayCount )
//then
//	$flDenom.setExcludedFromDenominator( true );
//end


public class CommonDenomExclLOSDayCntGrThanOutlierCntTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactExpression = ActiveMeasureUtilFunctions
                .createFactLevelDenominatorPrimaryFactNullCheck();

        AnyClassMultipleAttributeEvaluationFragmentTemplate denomTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR,
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(84,
                                        157, 85, 86, 159),
                        primaryFactExpression);

        AttributeFragmentTemplate outlierLOSDayCountAttribute = new AttributeFragmentTemplate(
                "outlierLOSDayCount",
                TypeDescription
                        .getTypeDescription(Long.class));
        AttributeFragmentTemplate lengthOfStayDayCountAttribute = new AttributeFragmentTemplate(
                "lengthOfStayDayCount",
                TypeDescription.getTypeDescription(Long.class));

        // using a from clause rather than this == $primaryFact for performance
        AnyClassMultipleAttributeEvaluationFragmentTemplate medicalCaseTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MedicalCase.class,
                        "$medicalcase",
                        UtilFunctions
                                .createMultipleAttributeEvaluationFragmentTemplate(
                                        Connector.AND,
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        outlierLOSDayCountAttribute,
                                                        new NotExistenceExpression(),
                                                        CommonOperators.NOT_EQUAL_TO,
                                                        null),
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        outlierLOSDayCountAttribute,
                                                        UtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(0),
                                                        CommonOperators.GREATER_THAN,
                                                        null)),
                        UtilFunctions
                                .createMultipleAttributeEvaluationFragmentTemplate(
                                        Connector.AND,
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        lengthOfStayDayCountAttribute,
                                                        new NotExistenceExpression(),
                                                        CommonOperators.NOT_EQUAL_TO,
                                                        null),
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        lengthOfStayDayCountAttribute,
                                                        UtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(0),
                                                        CommonOperators.GREATER_THAN,
                                                        null)));

        NamedVariableLiteralFragmentTemplate medicalCaseVariable = medicalCaseTemplate
                .getVariableExpression();
        medicalCaseVariable.setAttribute(outlierLOSDayCountAttribute);
        medicalCaseTemplate
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(ActiveMeasureUtilFunctions
                        .createAnyAttributeSingleComparisonFragmentTemplate(
                                lengthOfStayDayCountAttribute,
                                medicalCaseVariable,
                                CommonOperators.GREATER_THAN, null));

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "COMMON_DENOM_EXCL_LOS_COUNT_GR_THAN_OUTLIER_LOS_COUNT",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorExclusionToTrueTemplate(denomTemplate
                                        .getVariableExpression()),
                        denomTemplate,
                        new FromEvaluationFragmentTemplate(medicalCaseTemplate,
                                primaryFactExpression.getVariableExpression()));

        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.Common");
        return standardRuleInstance;
    }
}

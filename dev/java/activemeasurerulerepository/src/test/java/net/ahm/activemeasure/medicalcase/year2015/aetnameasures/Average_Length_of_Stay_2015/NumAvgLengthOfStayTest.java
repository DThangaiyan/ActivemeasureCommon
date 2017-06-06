package net.ahm.activemeasure.medicalcase.year2015.aetnameasures.Average_Length_of_Stay_2015;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.FactLevelMeasureNumeratorCreationTemplate;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.medicalcase.MedicalCase;
import net.ahm.rulesapp.templates.enums.CommonOperators;
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
 * Test case to generate JSON/DRL representation of the rule NUM_AVG_LENGTH_OF_STAY
 */

//124.	|	rule "NUM_AVG_LENGTH_OF_STAY"
//125.	|	    dialect "mvel"
//126.	|	    when
//127.	|	        $flmDenom : FactLevelMeasureDenominator( measureId == 86 , $primaryOriginationFact : primaryOriginationFact != null )
//128.	|	        $medicalCase : MedicalCase( this == $primaryOriginationFact , lengthOfStayDayCount != null , lengthOfStayDayCount > 0 , lengthOfStayDayCount < $medicalCase.outlierLOSDayCount )
//129.	|	    then
//130.	|	        FactLevelMeasureNumerator flmNumerator = $flmDenom.createFactLevelMeasureNumeratorWithPrimaryFact($medicalCase); insert(flmNumerator);
//131.	|	end

public class NumAvgLengthOfStayTest extends
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

        // using a from clause rather than this==$primaryFact
        AttributeFragmentTemplate lengthOfStayDayCountAttribute = new AttributeFragmentTemplate(
                "lengthOfStayDayCount",
                TypeDescription.getTypeDescription(Long.class));
        AnyClassMultipleAttributeEvaluationFragmentTemplate medicalCaseTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MedicalCase.class,
                        "$medicalCase",
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        lengthOfStayDayCountAttribute,
                                        new NotExistenceExpression(),
                                        CommonOperators.NOT_EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        lengthOfStayDayCountAttribute,
                                        UtilFunctions
                                                .createIntegerLiteralFragmentTemplate(0),
                                        CommonOperators.GREATER_THAN, null));
        NamedVariableLiteralFragmentTemplate medicateCaseOutlierLOSDayCountReference = medicalCaseTemplate
                .getVariableExpression();
        medicateCaseOutlierLOSDayCountReference
                .setAttribute(new AttributeFragmentTemplate(
                        "outlierLOSDayCount", TypeDescription
                        .getTypeDescription(Long.class)));
        medicalCaseTemplate
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(UtilFunctions
                        .createAnyAttributeSingleComparisonFragmentTemplate(
                                lengthOfStayDayCountAttribute,
                                medicateCaseOutlierLOSDayCountReference,
                                CommonOperators.LESS_THAN, null));

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate("NUM_AVG_LENGTH_OF_STAY",
                        new FactLevelMeasureNumeratorCreationTemplate(
                                flmDenomTemplate.getVariableExpression(),
                                medicalCaseTemplate.getVariableExpression()),
                        flmDenomTemplate,
                        new FromEvaluationFragmentTemplate(medicalCaseTemplate,
                                primaryFactExpression.getVariableExpression()));
        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2015.aetnameasures.Average_Length_of_Stay_2015");
        return standardRuleInstance;
    }
}

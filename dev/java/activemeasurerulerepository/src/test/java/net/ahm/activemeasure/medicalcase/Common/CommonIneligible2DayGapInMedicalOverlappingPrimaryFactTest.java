package net.ahm.activemeasure.medicalcase.Common;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.CoverageOvelappingContiguousDaysExpressable;
import net.ahm.activemeasure.templates.util.CoverageType;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.temporal.ContiguousDaysExpressable;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule COMMON_INELIGIBLE_2_DAY_GAP_IN_MEDICAL_OVERLAPPING_PRIMARY_FACT
 */

//74.	|	rule "COMMON_INELIGIBLE_2_DAY_GAP_IN_MEDICAL_OVERLAPPING_PRIMARY_FACT"
//75.	|	    dialect "mvel"
//76.	|	    when
//77.	|	         $flmd : FactLevelMeasureDenominator( measureId in ( 84, 85, 86, 159 ) , $primaryFact : primaryOriginationFact != null )
//78.	|	         $eventDays : ContiguousDaysExpressable( ) from $primaryFact
//79.	|	        MemberInfo ( $initialGapDays2 : enrolmentGapDays ); java.util.Collection( size >= 1 ) from collect ( ContiguousDays( durationInDays >= 2) from GlobalFunctions.allIntersectingButNotAdjacent( $initialGapDays2, $eventDays ))
//80.	|	    then
//81.	|	         $flmd.setEligible( false );
//82.	|	end

public class CommonIneligible2DayGapInMedicalOverlappingPrimaryFactTest extends
        AbstractActiveMeasuresRuleTemplateTest {
    private static final int EXPECTED_SIZE = 2;
    private static final int EXPCTED_COUNT = 1;

  @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactTemplate = ActiveMeasureUtilFunctions
                .createFactLevelDenominatorPrimaryFactNullCheck();

        AnyClassMultipleAttributeEvaluationFragmentTemplate flmDenomTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR,
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(84,
                                        85, 86, 159), primaryFactTemplate);

        AnyClassMultipleAttributeEvaluationFragmentTemplate contiuousDaysExpressableTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ContiguousDaysExpressable.class, "$eventDays");

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "COMMON_INELIGIBLE_2_DAY_GAP_IN_MEDICAL_OVERLAPPING_PRIMARY_FACT",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorEligibilityToFalseTemplate(flmDenomTemplate
                                        .getVariableExpression()),
                        flmDenomTemplate, new FromEvaluationFragmentTemplate(
                                contiuousDaysExpressableTemplate,
                                primaryFactTemplate.getVariableExpression()),
                        new CoverageOvelappingContiguousDaysExpressable(
                                CoverageType.MEDICAL,
                                CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                EXPCTED_COUNT,
                                CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                EXPECTED_SIZE, contiuousDaysExpressableTemplate
                                        .getVariableExpression()));
        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2015.aetnameasures.Common_Medical_Case");

        return standardRuleInstance;
    }
}

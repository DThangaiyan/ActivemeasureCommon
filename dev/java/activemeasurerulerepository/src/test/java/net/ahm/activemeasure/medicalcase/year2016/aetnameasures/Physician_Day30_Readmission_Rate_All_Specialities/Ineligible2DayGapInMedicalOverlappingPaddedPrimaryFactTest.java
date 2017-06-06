/**
 * 
 */
package net.ahm.activemeasure.medicalcase.year2016.aetnameasures.Physician_Day30_Readmission_Rate_All_Specialities;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.ContiguousDaysPaddingTemplate;
import net.ahm.activemeasure.templates.CoverageOvelappingContiguousDaysExpressable;
import net.ahm.activemeasure.templates.util.CoverageType;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.drug.DrugEvent;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.temporal.ContiguousDaysExpressable;
import net.ahm.careengine.ruleengine.DateTimeUnit;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassAttributeUpdaterTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule INELIGIBLE_2_DAY_GAP_IN_MEDICAL_OVERLAPPING_PADDED_PRIMARY_FACT
 */

//127.	|	rule "INELIGIBLE_2_DAY_GAP_IN_MEDICAL_OVERLAPPING_PADDED_PRIMARY_FACT"
//128.	|	    dialect "mvel"
//129.	|	    when
//130.	|	         $flmd : FactLevelMeasureDenominator( measureId == 157 , $primaryFact : primaryOriginationFact != null )
//131.	|	         $eventDays : ContiguousDaysExpressable( ) from $primaryFact
//132.	|	        $paddedEvent : ContiguousDays() from GlobalFunctions.getContiguousDaysWithNewDates($eventDays, 0, DateTimeUnit.DAY, 30, DateTimeUnit.DAY)
//133.	|	        MemberInfo ( $initialGapDays2 : enrolmentGapDays ); java.util.Collection( size >= 1 ) from collect ( ContiguousDays( durationInDays >= 2) from GlobalFunctions.allIntersectingButNotAdjacent( $initialGapDays2, $paddedEvent ))
//134.	|	    then
//135.	|	         $flmd.setEligible( false );
//136.	|	end

public class Ineligible2DayGapInMedicalOverlappingPaddedPrimaryFactTest extends AbstractActiveMeasuresRuleTemplateTest
{

	private static final int MEASURE_ID      		    = 157;

    private static final int          START_PUSH_AMOUNT = 0;
    private static final DateTimeUnit START_PUSH_UNIT   = DateTimeUnit.DAY;
    private static final int          END_PUSH_AMOUNT   = 30;
    private static final DateTimeUnit END_PUSH_UNIT     = DateTimeUnit.DAY;
    private static final int      	  EXPECTED_SIZE     = 2;
    private static final int      	  EXPCTED_COUNT     = 1;
    
	private static AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactExpr;
	private static AnyClassMultipleAttributeEvaluationFragmentTemplate flmTemplate;

  @Override
  public DefaultStandardRuleTemplate getRuleInstance() 
  {

    AnyClassMultipleAttributeEvaluationFragmentTemplate flmDenomTemplate = getFactLevelMeasureDenominatorTemplate();
    
    DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
            .createDefaultStandardRuleTemplate("INELIGIBLE_2_DAY_GAP_IN_MEDICAL_OVERLAPPING_PADDED_PRIMARY_FACT",
            		getActionTemplate(flmDenomTemplate.getVariableExpression()),
                    flmDenomTemplate,
                    getContiguousDaysExpressableTemplate(flmDenomTemplate),
                    getAfterDtTemplate(),
                    getCoverageOverLappingTemplate());

    standardRuleInstance.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2016.aetnameasures.Physician_Day30_Readmission_Rate_All_Specialities");
    return standardRuleInstance;
  }
  
  //$flmd : FactLevelMeasureDenominator( measureId == 157 , $primaryFact : primaryOriginationFact != null )
  private AnyClassMultipleAttributeEvaluationFragmentTemplate getFactLevelMeasureDenominatorTemplate()
  {
    AnyClassMultipleAttributeEvaluationFragmentTemplate flmnTemplate = new AnyClassMultipleAttributeEvaluationFragmentTemplate();

    flmnTemplate.setVariableName(ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR);
    flmnTemplate.setExpressionReturnTypeDescription(TypeDescription.getTypeDescription(FactLevelMeasureDenominator.class));

    flmnTemplate.getMultipleAttributeEvaluationFragments().setConnector(Connector.COMMA);
        flmnTemplate
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(ActiveMeasureUtilFunctions
                        .createMeasureIdInOrEqualToAttributeFagementTemplate(MEASURE_ID));
  
        primaryFactExpr = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment(
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                        CommonOperators.NOT_EQUAL_TO, Fact.class);
    primaryFactExpr.setVariableName("$primaryFact");
    flmnTemplate.getMultipleAttributeEvaluationFragments().getAttributeEvaluationFragments().add(primaryFactExpr);

    return flmnTemplate;
    }

  //$eventDays : ContiguousDaysExpressable( ) from $primaryFact
  private FromEvaluationFragmentTemplate getContiguousDaysExpressableTemplate(AnyClassMultipleAttributeEvaluationFragmentTemplate acM) 
  {
      
	    flmTemplate = new AnyClassMultipleAttributeEvaluationFragmentTemplate();		
        flmTemplate.setExpressionReturnTypeDescription(TypeDescription
                .getTypeDescription(ContiguousDaysExpressable.class));
				flmTemplate.setVariableName("$eventDays");		
		        FromEvaluationFragmentTemplate fromTemplate = new FromEvaluationFragmentTemplate(
		        		flmTemplate,
		        		primaryFactExpr.getVariableExpression());		        
		return fromTemplate;
  }

  //$paddedEvent : ContiguousDays() from GlobalFunctions.getContiguousDaysWithNewDates($eventDays, 0, DateTimeUnit.DAY, 30, DateTimeUnit.DAY)
  private ContiguousDaysPaddingTemplate getAfterDtTemplate() {
        ContiguousDaysPaddingTemplate template = new ContiguousDaysPaddingTemplate(
                UtilFunctions.createNamedVariableLiteralFragmentTemplate(
                        flmTemplate.getVariableExpression().getVariableName(),
                        ContiguousDaysExpressable.class, null),
                UtilFunctions
                        .createIntegerLiteralFragmentTemplate(START_PUSH_AMOUNT),
                UtilFunctions.createEnumLiteralExpressionFragmentTemplate(
                        DateTimeUnit.class, START_PUSH_UNIT), UtilFunctions
                        .createIntegerLiteralFragmentTemplate(END_PUSH_AMOUNT),
                UtilFunctions.createEnumLiteralExpressionFragmentTemplate(
                        DateTimeUnit.class, END_PUSH_UNIT));
        template.setVariableName("$paddedEvent");
        return template;
  }

  //MemberInfo ( $initialGapDays2 : enrolmentGapDays ); java.util.Collection( size >= 1 ) from collect ( 
  //	ContiguousDays( durationInDays >= 2) from GlobalFunctions.allIntersectingButNotAdjacent( $initialGapDays2, $paddedEvent ))
    private CoverageOvelappingContiguousDaysExpressable getCoverageOverLappingTemplate() {
        return new CoverageOvelappingContiguousDaysExpressable(
                CoverageType.MEDICAL, CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                EXPCTED_COUNT, CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                EXPECTED_SIZE,
                UtilFunctions.createNamedVariableLiteralFragmentTemplate(
                        "$paddedEvent", DrugEvent.class, null));
  }

    //$flmd.setEligible( false )
    private AnyClassAttributeUpdaterTemplate getActionTemplate(NamedVariableLiteralFragmentTemplate expression) {

        AnyClassAttributeUpdaterTemplate numeratorUpdater = new AnyClassAttributeUpdaterTemplate(
                expression);
        numeratorUpdater.getAttributeUpdateFragments().add(
                UtilFunctions.createBooleanAttributeSettingInstance("Eligible",
                        false));
        return numeratorUpdater;
    }
}

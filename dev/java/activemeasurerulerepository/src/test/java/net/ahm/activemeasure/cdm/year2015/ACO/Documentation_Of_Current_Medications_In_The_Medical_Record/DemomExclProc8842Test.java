/**
 * 
 */
package net.ahm.activemeasure.cdm.year2015.ACO.Documentation_Of_Current_Medications_In_The_Medical_Record;

import java.util.Date;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.proc.ProcedureEvent;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassAttributeUpdaterTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeSetterFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;

/**
 * Test case to generate JSON/DRL representation of the rule DENOM_EXCL_PROC_8842
 * 
 * @author MKrishnan
 *
 */

//1.	|	rule "DENOM_EXCL_PROC_8842"
//2.	|	    dialect "mvel"
//3.	|	    when
//4.	|	        $flm : FactLevelMeasureDenominator( measureId == 132 , $primaryFact : primaryOriginationFact != null )
//5.	|	        $denomEvent : ProcedureEvent( $denomStartDate : startDate != null , $denomEndDate : endDate != null ) from $primaryFact
//6.	|	        ProcedureEvent( this != $denomEvent , startDate == $denomStartDate , endDate == $denomEndDate , elements contains 8842 )
//7.	|	    then
//8.	|	        $flm.setExcludedFromDenominator( true );
//9.	|	end
public class DemomExclProc8842Test extends AbstractActiveMeasuresRuleTemplateTest {
    private static final int MEASURE_ID = 132;
    private static final int ELEMENT_ID = 8842;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        DefaultStandardRuleTemplate standardRuleInstance = new DefaultStandardRuleTemplate();
        standardRuleInstance.setRuleName("DENOM_EXCL_PROC_8842");
        standardRuleInstance.setRuleDescription("DENOM_EXCL_PROC_8842");

        AnyClassMultipleAttributeEvaluationFragmentTemplate denomTemplate = ActiveMeasureUtilFunctions
                .createFactLevelMeasureDenominator(MEASURE_ID);
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFact = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment(
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                        CommonOperators.NOT_EQUAL_TO, Fact.class);
        primaryFact.setVariableName("$primaryFact");
        denomTemplate.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments().add(primaryFact);
        standardRuleInstance.getConditions().add(denomTemplate);

        AnyAttributeSingleComparisonFragmentTemplate<Object> demonEventStartDateTemplate = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment("startDate",
                        CommonOperators.NOT_EQUAL_TO, Date.class);
        demonEventStartDateTemplate.setVariableName("$denomStartDate");
        AnyAttributeSingleComparisonFragmentTemplate<Object> denomEventEndDateTemplate = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment("endDate",
                        CommonOperators.NOT_EQUAL_TO, Date.class);
        denomEventEndDateTemplate.setVariableName("$denomEndDate");
        AnyClassMultipleAttributeEvaluationFragmentTemplate denomEventEvent = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ProcedureEvent.class, "$denomEvent",
                        demonEventStartDateTemplate, denomEventEndDateTemplate);
        FromEvaluationFragmentTemplate fromTemplate = new FromEvaluationFragmentTemplate(
                denomEventEvent, primaryFact.getVariableExpression());
        standardRuleInstance.getConditions().add(fromTemplate);

        AnyClassMultipleAttributeEvaluationFragmentTemplate otherProcTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ProcedureEvent.class,
                        null,
                        ActiveMeasureUtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate("this",
                                                TypeDescription
                                                        .getTypeDescription(ProcedureEvent.class)),
                                        new NamedVariableLiteralFragmentTemplate(
                                                denomEventEvent
                                                        .getVariableName(),
                                                denomEventEvent
                                                        .getExpressionReturnTypeDescription()),
                                        CommonOperators.NOT_EQUAL_TO, null),
                        ActiveMeasureUtilFunctions
                                .createObjectVariableCheckFragment("startDate",
                                        Date.class, CommonOperators.EQUAL_TO,
                                        demonEventStartDateTemplate
                                                .getVariableExpression()),
                        ActiveMeasureUtilFunctions
                                .createObjectVariableCheckFragment("endDate",
                                        Date.class, CommonOperators.EQUAL_TO,
                                        denomEventEndDateTemplate
                                                .getVariableExpression()),
                        ActiveMeasureUtilFunctions
                                .createElementContainsValueTemplate("elements",
                                        ELEMENT_ID));
        standardRuleInstance.getConditions().add(otherProcTemplate);

        AnyClassAttributeUpdaterTemplate actionTemplate = ActiveMeasureUtilFunctions
                .createAnyClassAttributeUpdaterTempalate(
                        denomTemplate.getVariableExpression(),
                        new AttributeSetterFragmentTemplate(
                                new AttributeFragmentTemplate(
                                        "excludedFromDenominator",
                                        TypeDescription
                                                .getTypeDescription(Boolean.TYPE)),
                                ActiveMeasureUtilFunctions
                                        .createBooleanLiteralExpressionFragmentTemplate(true)));
        standardRuleInstance.getActions().add(actionTemplate);

        return standardRuleInstance;
    }
}

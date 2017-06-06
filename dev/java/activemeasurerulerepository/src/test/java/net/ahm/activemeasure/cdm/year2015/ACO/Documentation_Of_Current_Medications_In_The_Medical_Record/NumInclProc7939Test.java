package net.ahm.activemeasure.cdm.year2015.ACO.Documentation_Of_Current_Medications_In_The_Medical_Record;

import java.util.Date;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.FactLevelMeasureNumeratorCreationTemplate;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.proc.ProcedureEvent;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule NUM_INCL_PROC_7939
 * 
 * @author MKrishnan
 *
 */

//1.	|	rule "NUM_INCL_PROC_7939"
//2.	|	    dialect "mvel"
//3.	|	    when
//4.	|	         $flmd : FactLevelMeasureDenominator( measureId == 132 , $primaryFact : primaryOriginationFact != null )
//5.	|	         ProcedureEvent( $denomEndDate : endDate != null , $denomStartDate : startDate != null ) from $primaryFact
//6.	|	         $numFact : ProcedureEvent( this != $primaryFact , startDate == $denomStartDate , endDate == $denomEndDate , elements contains 7939 )
//7.	|	    then
//8.	|	        FactLevelMeasureNumerator flmNumerator = $flmd.createFactLevelMeasureNumeratorWithPrimaryFact($numFact); insert(flmNumerator);
//9.	|	end
public class NumInclProc7939Test extends AbstractActiveMeasuresRuleTemplateTest {
    private static final int MEASURE_ID = 132;
    private static final int ELEMENT_ID = 7939;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate denomTemplate = ActiveMeasureUtilFunctions
                .createFactLevelMeasureDenominator(MEASURE_ID);
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFact = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment(
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                        CommonOperators.NOT_EQUAL_TO, Fact.class);
        primaryFact.setVariableName("$primaryFact");
        denomTemplate.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments().add(primaryFact);

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
                        ProcedureEvent.class, null, denomEventEndDateTemplate,
                        demonEventStartDateTemplate);
        FromEvaluationFragmentTemplate fromTemplate = new FromEvaluationFragmentTemplate(
                denomEventEvent, primaryFact.getVariableExpression());

        AnyClassMultipleAttributeEvaluationFragmentTemplate numFact = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ProcedureEvent.class,
                        "$numFact",
                        ActiveMeasureUtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate("this",
                                                TypeDescription
                                                        .getTypeDescription(ProcedureEvent.class)),
                                        new NamedVariableLiteralFragmentTemplate(
                                                primaryFact.getVariableName(),
                                                primaryFact
                                                        .getVariableExpression()
                                                        .getExpressionReturnTypeDescription()
                                                        .getType()),
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

        return UtilFunctions.createDefaultStandardRuleTemplate(
                "NUM_INCL_PROC_7939",
                new FactLevelMeasureNumeratorCreationTemplate(denomTemplate
                        .getVariableExpression(), numFact
                        .getVariableExpression()), denomTemplate, fromTemplate,
                numFact);
    }
}

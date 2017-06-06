/**
 * 
 */
package net.ahm.activemeasure.cdm.year2015.ACO.Documentation_Of_Current_Medications_In_The_Medical_Record;

import java.util.Date;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.diag.DiagnosticEvent;
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
import net.ahm.rulesapp.templates.utils.TypeDescription;
/**
 * Test case to generate JSON/DRL representation of the rule DENOM_EXCL_DIAG_8843
 * 
 * @author MKrishnan
 *
 */

//1.	|	rule "DENOM_EXCL_DIAG_8843"
//2.	|	    dialect "mvel"
//3.	|	    when
//4.	|	        $flm : FactLevelMeasureDenominator( measureId == 132 , $primaryFact : primaryOriginationFact != null )
//5.	|	        $denomEvent : ProcedureEvent( $denomStartDate : startDate != null , $denomEndDate : endDate != null ) from $primaryFact
//6.	|	        DiagnosticEvent( startDate == $denomStartDate , endDate == $denomEndDate , elements contains 8843 )
//7.	|	    then
//8.	|	        $flm.setExcludedFromDenominator( true );
//9.	|	end


public class DemomExclDiag8843Test extends AbstractActiveMeasuresRuleTemplateTest {
    private static final int MEASURE_ID = 132;
    private static final int ELEMENT_ID = 8843;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        DefaultStandardRuleTemplate standardRuleInstance = new DefaultStandardRuleTemplate();
        standardRuleInstance.setRuleName("DENOM_EXCL_DIAG_8843");
        standardRuleInstance.setRuleDescription("DENOM_EXCL_DIAG_8843");

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

        AnyAttributeSingleComparisonFragmentTemplate<Object> procStartDateTemplate = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment("startDate",
                        CommonOperators.NOT_EQUAL_TO, Date.class);
        procStartDateTemplate.setVariableName("$denomStartDate");
        AnyAttributeSingleComparisonFragmentTemplate<Object> procEndDateTemplate = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment("endDate",
                        CommonOperators.NOT_EQUAL_TO, Date.class);
        procEndDateTemplate.setVariableName("$denomEndDate");
        AnyClassMultipleAttributeEvaluationFragmentTemplate procedureTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ProcedureEvent.class, "$denomEvent", procStartDateTemplate,
                        procEndDateTemplate);

        FromEvaluationFragmentTemplate fromTemplate = new FromEvaluationFragmentTemplate(
                procedureTemplate, primaryFact.getVariableExpression());

        standardRuleInstance.getConditions().add(fromTemplate);

        AnyClassMultipleAttributeEvaluationFragmentTemplate diagnosticTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        DiagnosticEvent.class, null, ActiveMeasureUtilFunctions
                                .createObjectVariableCheckFragment("startDate",
                                        Date.class, CommonOperators.EQUAL_TO,
                                        procStartDateTemplate
                                                .getVariableExpression()),
                        ActiveMeasureUtilFunctions
                                .createObjectVariableCheckFragment("endDate",
                                        Date.class, CommonOperators.EQUAL_TO,
                                        procEndDateTemplate
                                                .getVariableExpression()),
                        ActiveMeasureUtilFunctions
                                .createElementContainsValueTemplate("elements",
                                        ELEMENT_ID));

        standardRuleInstance.getConditions().add(diagnosticTemplate);

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

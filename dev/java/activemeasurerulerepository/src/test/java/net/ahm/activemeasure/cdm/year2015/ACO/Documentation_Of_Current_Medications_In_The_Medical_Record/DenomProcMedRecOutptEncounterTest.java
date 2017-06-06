package net.ahm.activemeasure.cdm.year2015.ACO.Documentation_Of_Current_Medications_In_The_Medical_Record;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.proc.ProcedureEvent;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule NUM_INCL_PROC_7939
 * 
 * @author MKrishnan
 * 
 */

//1.	|	rule "DENOM_PROC_MED_REC_OUTPT_ ENCOUNTER"
//2.	|	    dialect "mvel"
//3.	|	    when
//4.	|	         ActiveMeasuresMemberInfo( ageAtMeasurementEndDate >= 18 )
//5.	|	         $procedureEvent : ProcedureEvent( endDate <= measurementEndDate && >= months12BeforeMeasurementEndDate , elements contains 7934 )
//6.	|	    then
//7.	|	        FactLevelMeasureDenominator flmDenominator= factLevelMeasureBuilder.newFactLevelMeasureDenominatorWithPrimaryFact(132, $procedureEvent); insert(flmDenominator)
//8.	|	end


public class DenomProcMedRecOutptEncounterTest extends AbstractActiveMeasuresRuleTemplateTest {
    private static int MEASURE_ID      = 132;
    private static int MEASUREMENT_AGE = 18;
    private static int ELEMENT_ID      = 7934;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate procedureEventTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ProcedureEvent.class,
                        "$procedureEvent",
                        ActiveMeasureUtilFunctions
                                .createDateAttributeAndVariableCheckFragment(
                                        "endDate",
                                        CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                        ActiveMeasureUtilFunctions
                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MEASUREMENT_END_DATE)),
                        ActiveMeasureUtilFunctions
                                .createDateAttributeAndVariableCheckFragment(
                                        "endDate",
                                        CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                        ActiveMeasureUtilFunctions
                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MONTHS_12_BEFORE_MEASUREMENT_END_DATE)),
                        ActiveMeasureUtilFunctions
                                .createElementContainsValueTemplate("elements",
                                        ELEMENT_ID));

        return UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "DENOM_PROC_MED_REC_OUTPT_ENCOUNTER",
                        ActiveMeasureUtilFunctions
                                .createFactLevelMeasureDenominatorCreationTemplate(
                                        MEASURE_ID, procedureEventTemplate
                                                .getVariableExpression()),
                ActiveMeasureUtilFunctions
                        .createActiveMeasuresMemberInfoFragment(
                                MEASUREMENT_AGE, 0, false,
                                null),
                        procedureEventTemplate);
    }
}

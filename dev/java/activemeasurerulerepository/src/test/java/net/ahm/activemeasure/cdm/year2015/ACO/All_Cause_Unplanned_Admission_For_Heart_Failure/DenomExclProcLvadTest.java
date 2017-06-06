package net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_Unplanned_Admission_For_Heart_Failure;

import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.proc.ProcedureEvent;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule DENOM_EXCL_PROC_LVAD
 * 
 * @author MKrishnan
 * 
 */

//1.	|	rule "DENOM_EXCL_PROC_LVAD"
//2.	|	    dialect "mvel"
//3.	|	    when
//4.	|	        $flm : FactLevelMeasureDenominator( measureId == 130 )
//5.	|	        ProcedureEvent( endDate <= measurementEndDate , elements contains 8853 )
//6.	|	    then
//7.	|	        $flm.setExcludedFromDenominator( true );
//8.	|	end

public class DenomExclProcLvadTest extends AbstractActiveMeasuresRuleTemplateTest {

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate flmDenomTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR,
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(130));

        DefaultStandardRuleTemplate rule = UtilFunctions
                .createDefaultStandardRuleTemplate("DENOM_EXCL_PROC_LVAD",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorExclusionToTrueTemplate(flmDenomTemplate
                                        .getVariableExpression()),
                        flmDenomTemplate,
                        UtilFunctions
                                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                                        ProcedureEvent.class,
                                        null,
                                        UtilFunctions
                                                .createDateAttributeAndVariableCheckFragment(
                                                        "endDate",
                                                        CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                                        ActiveMeasureUtilFunctions
                                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MEASUREMENT_END_DATE)),
                                        UtilFunctions
                                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                                        new AttributeFragmentTemplate(
                                                                "elements",
                                                                TypeDescription
                                                                        .getTypeDescription(
                                                                                Set.class,
                                                                                Integer.class)),
                                                        UtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(8853),
                                                        CommonOperators.CONTAINS,
                                                        null)));
        return rule;
    }
}

package net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_Unplanned_Admission_For_Heart_Failure;

import java.util.Date;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule DENOM_HEART_FAILURE_2_OUTPAT_ANY_DIAG
 * 
 * @author MKrishnan
 * 
 */

//1.	|	rule "DENOM_HEART_FAILURE_2_OUTPAT_ANY_DIAG"
//2.	|	    dialect "mvel"
//3.	|	    when
//4.	|	         ActiveMeasuresMemberInfo( ageAtMeasurementEndDate >= 65 , gender != null )
//5.	|	         $outPatClaimHeaderEvents : java.util.Collection( size >= 2 ) from collect ( ClaimHeader( endDate <= measurementEndDate , inpatient == false , allDiagnosticEventElements contains 7932 , endDate >= months24BeforeMeasurementEndDate ))
//6.	|	    then
//7.	|	        FactLevelMeasureDenominator flmDenominator= factLevelMeasureBuilder.newFactLevelMeasureDenominator(130); insert(flmDenominator)
//8.	|	end


public class DenomHeartFailure2OutpatAnyDiagTest extends AbstractActiveMeasuresRuleTemplateTest {
    private static final int MEASURE_ID      = 130;
    private static final int MEASUREMENT_AGE = 65;
    private static final int ELEMENT_ID      = 7932;
    private static final int COLLECTION_SIZE = 2;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        return UtilFunctions.createDefaultStandardRuleTemplate(
                "DENOM_HEART_FAILURE_2_OUTPAT_ANY_DIAG",
                ActiveMeasureUtilFunctions
                        .createFactLevelMeasureDenominatorCreationTemplate(
                                MEASURE_ID, null),
                        ActiveMeasureUtilFunctions
                                .createActiveMeasuresMemberInfoFragment(
                                        MEASUREMENT_AGE, 0, true, null),
                        UtilFunctions.createCollectionEvaluationFragmentTemplate(
                                ActiveMeasureUtilFunctions
                                        .createCollectionSize(null,
                                                COLLECTION_SIZE),
                                UtilFunctions
                                        .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                                                ClaimHeader.class,
                                                null,
                                                UtilFunctions
                                                        .createObjectVariableCheckFragment(
                                                                "endDate",
                                                                CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                                                Date.class,
                                                                ActiveMeasureUtilFunctions
                                                                        .getGlobalVarible(ActiveMeasureGlobalDefinition.MEASUREMENT_END_DATE)),
                                                UtilFunctions
                                                        .createBooleanCheckFragment(
                                                                "inpatient",
                                                                false,
                                                                CommonOperators.EQUAL_TO),
                                                ActiveMeasureUtilFunctions
                                                        .createElementContainsValueTemplate(
                                                                "allDiagnosticEventElements",
                                                                ELEMENT_ID),
                                                UtilFunctions
                                                        .createObjectVariableCheckFragment(
                                                                "endDate",
                                                                CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                                                Date.class,
                                                                ActiveMeasureUtilFunctions
                                                                        .getGlobalVarible(ActiveMeasureGlobalDefinition.MONTHS_24_BEFORE_MEASUREMENT_END_DATE)))));
    }
}

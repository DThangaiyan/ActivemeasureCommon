package net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_Unplanned_Admission_For_Heart_Failure;

import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule DENOM_HEART_FAILURE_1_INPAT_PRINICIPAL_DIAG
 * 
 * @author MKrishnan
 * 
 */

//1.	|	rule "DENOM_HEART_FAILURE_1_INPAT_PRINICIPAL_DIAG"
//2.	|	    dialect "mvel"
//3.	|	    when
//4.	|	         ActiveMeasuresMemberInfo( ageAtMeasurementEndDate >= 65 , gender != null )
//5.	|	         ClaimHeader( endDate <= measurementEndDate , principalDiagnosisElements contains 7932 , inpatient == true , endDate >= months24BeforeMeasurementEndDate )
//6.	|	    then
//7.	|	        FactLevelMeasureDenominator flmDenominator= factLevelMeasureBuilder.newFactLevelMeasureDenominator(130); insert(flmDenominator)
//8.	|	end

public class DenomHeartFailure11InpatPrincipalDiagTest extends
        AbstractActiveMeasuresRuleTemplateTest {
    private static final int MEASURE_ID = 130;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        DefaultStandardRuleTemplate rule = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "DENOM_HEART_FAILURE_1_INPAT_PRINICIPAL_DIAG",
                        ActiveMeasureUtilFunctions
                                .createFactLevelMeasureDenominatorCreationTemplate(
                                        MEASURE_ID, null),
                        ActiveMeasureUtilFunctions
                                .createActiveMeasuresMemberInfoFragment(65, 0,
                                        true, null),
                        UtilFunctions
                                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                                        ClaimHeader.class,
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
                                                                "principalDiagnosisElements",
                                                                TypeDescription
                                                                        .getTypeDescription(
                                                                                Set.class,
                                                                                Integer.class)),
                                                        UtilFunctions
                                                                .createIntegerLiteralFragmentTemplate(7932),
                                                        CommonOperators.CONTAINS,
                                                        null),
                                        UtilFunctions
                                                .createBooleanCheckFragment(
                                                        "inpatient",
                                                        true,
                                                        CommonOperators.EQUAL_TO),
                                        UtilFunctions
                                                .createDateAttributeAndVariableCheckFragment(
                                                        "endDate",
                                                        CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                                        ActiveMeasureUtilFunctions
                                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MONTHS_24_BEFORE_MEASUREMENT_END_DATE))));
        return rule;
    }
}

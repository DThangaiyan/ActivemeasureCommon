package net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_Unplanned_Admission_For_With_Diabetes;

import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.SimpleCollectionTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

//rule "DENOM_DIABETES_MELLITUS_2_OUTPAT"
//dialect "mvel"
//when
//     ActiveMeasuresMemberInfo( ageAtMeasurementEndDate >= 65 , gender != null )
//     $outPatClaimHeaderEvents : java.util.Collection( size >= 2 ) from collect ( ClaimHeader( endDate <= measurementEndDate , inpatient == false , allDiagnosticEventElements contains 8854 , endDate >= months24BeforeMeasurementEndDate )) 
//then
//    FactLevelMeasureDenominator flmDenominator= factLevelMeasureBuilder.newFactLevelMeasureDenominator(129); insert(flmDenominator)
//end

public class DenomDiabetesMellitus2InpatTest extends AbstractActiveMeasuresRuleTemplateTest {

    private static AnyClassMultipleAttributeEvaluationFragmentTemplate getClaimHeaderTemplate() {
        return UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ClaimHeader.class,
                        null,
                        UtilFunctions
                                .createDateAttributeAndVariableCheckFragment(
                                        "endDate",
                                        CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                        ActiveMeasureUtilFunctions
                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MEASUREMENT_END_DATE)),
                        UtilFunctions.createBooleanCheckFragment("inpatient",
                                false, CommonOperators.EQUAL_TO),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                "allDiagnosticEventElements",
                                                TypeDescription
                                                        .getTypeDescription(
                                                                Set.class,
                                                                Integer.class)),
                                        UtilFunctions
                                                .createIntegerLiteralFragmentTemplate(8854),
                                        CommonOperators.CONTAINS, null),
                        UtilFunctions
                                .createDateAttributeAndVariableCheckFragment(
                                        "endDate",
                                        CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                        ActiveMeasureUtilFunctions
                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MONTHS_24_BEFORE_MEASUREMENT_END_DATE)));
    }

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        DefaultStandardRuleTemplate ruleTemplate = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "DENOM_DIABETES_MELLITUS_2_INPAT",
                        ActiveMeasureUtilFunctions
                                .createFactLevelMeasureDenominatorCreationTemplate(
                                        129, null), ActiveMeasureUtilFunctions
                                .createActiveMeasuresMemberInfoFragment(65, 0,
                                        true, null),
                        new SimpleCollectionTemplate(
                                CommonOperators.GREATER_THAN_OR_EQUAL_TO, 2,
                                getClaimHeaderTemplate()));
        ruleTemplate
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_Unplanned_Admission_For_With_Diabetes");
        return ruleTemplate;
    }
}

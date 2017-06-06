package net.ahm.activemeasure.cdm.year2015.ACO.Ambulatory_Care_Sensitive_Conditions_Admissions_For_Heart_Failure;

import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.event.adt.model.DischargeDispositionStatus;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule DENOM_HEART_FAILURE
 */

//81. | rule "DENOM_HEART_FAILURE"
//82. |     dialect "mvel"
//83. |     when
//84. |          ActiveMeasuresMemberInfo( ageAtMeasurementEndDate >= 18 , gender != null )
//85. |          $claimHdr : ClaimHeader( principalDiagnosisElements contains 7932 , endDate <= measurementEndDate , dischargeDisposition == DischargeDispositionStatus.CD_01 || == DischargeDispositionStatus.CD_03 || == DischargeDispositionStatus.CD_04 || == DischargeDispositionStatus.CD_05 || == DischargeDispositionStatus.CD_06 || == DischargeDispositionStatus.CD_08 || == DischargeDispositionStatus.CD_50 || == DischargeDispositionStatus.CD_51 || == DischargeDispositionStatus.CD_61 || == DischargeDispositionStatus.CD_62 || == DischargeDispositionStatus.CD_63 || == DischargeDispositionStatus.CD_64 || == DischargeDispositionStatus.CD_65 || == DischargeDispositionStatus.CD_70 , endDate >= months12BeforeMeasurementEndDate )
//86. |     then
//87. |         FactLevelMeasureDenominator flmDenominator= factLevelMeasureBuilder.newFactLevelMeasureDenominatorWithPrimaryFact(127, $claimHdr); insert(flmDenominator)
//88. | end

public class DenomHeartFailureTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    private static int MEASURE_ID      = 127;
    private static int MEASUREMENT_AGE = 18;
    private static int ELEMENT_ID      = 7932;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate claimHeaderTemplate = getClaimHeaderTemplate();

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate("DENOM_HEART_FAILURE",
                        ActiveMeasureUtilFunctions
                                .createFactLevelMeasureDenominatorCreationTemplate(
                                        MEASURE_ID, claimHeaderTemplate
                                                .getVariableExpression()),
                        ActiveMeasureUtilFunctions
                                .createActiveMeasuresMemberInfoFragment(
                                        MEASUREMENT_AGE, 0, true, null),
                        claimHeaderTemplate);
        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.Ambulatory_Care_Sensitive_Conditions_Admissions_For_Heart_Failure");

        return standardRuleInstance;
    }

    private AnyClassMultipleAttributeEvaluationFragmentTemplate getClaimHeaderTemplate() {
        return UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ClaimHeader.class,
                        "$claimHdr",
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                "principalDiagnosisElements",
                                                TypeDescription
                                                        .getTypeDescription(
                                                                Set.class,
                                                                Integer.class)),
                                        UtilFunctions
                                                .createIntegerLiteralFragmentTemplate(ELEMENT_ID),
                                        CommonOperators.CONTAINS, null),
                        UtilFunctions
                                .createDateAttributeAndVariableCheckFragment(
                                        "endDate",
                                        CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                        ActiveMeasureUtilFunctions
                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MEASUREMENT_END_DATE)),
                        UtilFunctions
                                .createMultipleAttributeEvaluationFragmentTemplate(
                                        Connector.OR,
                                        ActiveMeasureUtilFunctions
                                                .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_01),
                                        ActiveMeasureUtilFunctions
                                                .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_03),
                                        ActiveMeasureUtilFunctions
                                                .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_04),
                                        ActiveMeasureUtilFunctions
                                                .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_05),
                                        ActiveMeasureUtilFunctions
                                                .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_06),
                                        ActiveMeasureUtilFunctions
                                                .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_08),
                                        ActiveMeasureUtilFunctions
                                                .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_50),
                                        ActiveMeasureUtilFunctions
                                                .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_51),
                                        ActiveMeasureUtilFunctions
                                                .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_61),
                                        ActiveMeasureUtilFunctions
                                                .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_62),
                                        ActiveMeasureUtilFunctions
                                                .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_63),
                                        ActiveMeasureUtilFunctions
                                                .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_64),
                                        ActiveMeasureUtilFunctions
                                                .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_65),
                                        ActiveMeasureUtilFunctions
                                                .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_70)),
                        UtilFunctions
                                .createDateAttributeAndVariableCheckFragment(
                                        "endDate",
                                        CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                        ActiveMeasureUtilFunctions
                                                .getGlobalVarible(ActiveMeasureGlobalDefinition.MONTHS_12_BEFORE_MEASUREMENT_END_DATE)));
    }
}

package net.ahm.activemeasure.cdm.year2015.ACO.Ambulatory_Care_Sensitive_Conditions_Admissions_For_Heart_Failure;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.FactLevelMeasureNumeratorCreationTemplate;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.event.adt.model.DischargeDispositionStatus;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule NUM_DISCHARGE_FROM_ACTUE_CARE_OR_CAH_WITH_INPAT
 */

//1.  | rule "NUM_DISCHARGE_FROM_ACTUE_CARE_OR_CAH_WITH_INPAT"
//2.  |     dialect "mvel"
//3.  |     when
//4.  |          $flm : FactLevelMeasureDenominator( measureId == 127 )
//5.  |          ClaimHeader( inpatient == true , dischargeDisposition == DischargeDispositionStatus.CD_01 || == DischargeDispositionStatus.CD_03 || == DischargeDispositionStatus.CD_04 || == DischargeDispositionStatus.CD_05 || == DischargeDispositionStatus.CD_06 || == DischargeDispositionStatus.CD_08 || == DischargeDispositionStatus.CD_50 || == DischargeDispositionStatus.CD_51 || == DischargeDispositionStatus.CD_61 || == DischargeDispositionStatus.CD_62 || == DischargeDispositionStatus.CD_63 || == DischargeDispositionStatus.CD_64 || == DischargeDispositionStatus.CD_65 || == DischargeDispositionStatus.CD_70 ) from $flm.primaryOriginationFact
//6.  |     then
//7.  |         FactLevelMeasureNumerator flmNumerator = $flm.createFactLevelMeasureNumeratorWithPrimaryFact($flm.primaryOriginationFact); insert(flmNumerator);
//8.  | end
public class NumDischargeFromActueCareOrCahWithInpatTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    private static int MEASURE_ID = 127;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate factLevelMeasureDenomTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        "$flm",
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(MEASURE_ID));

        NamedVariableLiteralFragmentTemplate flmPrimaryFact = factLevelMeasureDenomTemplate
                .getVariableExpression();
        flmPrimaryFact
                .setAttribute(new AttributeFragmentTemplate(
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                        TypeDescription.getTypeDescription(Fact.class)));

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "NUM_DISCHARGE_FROM_ACTUE_CARE_OR_CAH_WITH_INPAT",
                        new FactLevelMeasureNumeratorCreationTemplate(
                                factLevelMeasureDenomTemplate
                                        .getVariableExpression(),
                                flmPrimaryFact), factLevelMeasureDenomTemplate,
                        new FromEvaluationFragmentTemplate(
                                getClaimHeaderTemplate(),
                                flmPrimaryFact));

        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.Ambulatory_Care_Sensitive_Conditions_Admissions_For_Heart_Failure");
        return standardRuleInstance;
    }

    private AnyClassMultipleAttributeEvaluationFragmentTemplate getClaimHeaderTemplate() {
        return UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ClaimHeader.class,
                        null,
                        UtilFunctions.createBooleanCheckFragment("inpatient",
                                true, CommonOperators.EQUAL_TO),
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
                                                .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_70)));
    }
}

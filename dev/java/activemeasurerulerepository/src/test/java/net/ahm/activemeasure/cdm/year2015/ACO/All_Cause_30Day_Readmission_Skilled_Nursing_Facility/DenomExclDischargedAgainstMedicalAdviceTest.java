package net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_30Day_Readmission_Skilled_Nursing_Facility;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.event.adt.model.DischargeDispositionStatus;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.util.UtilFunctions;

public class DenomExclDischargedAgainstMedicalAdviceTest extends
        AbstractActiveMeasuresRuleTemplateTest {

//    rule "DENOM_EXCL_DISCHARGED_AGAINST_MEDICAL_ADVICE"
//    dialect "mvel"
//    when
//        $factLevelDenominator : FactLevelMeasureDenominator ( measureId == 128 , $primaryOriginationFact : primaryOriginationFact != null )
//    ClaimHeader ( dischargeDisposition == DischargeDispositionStatus.CD_07 ) from $primaryOriginationFact
//    then
//        $factLevelDenominator.setExcludedFromDenominator(true);
//    end
    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {


        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactTemplate = ActiveMeasureUtilFunctions
                .createFactLevelDenominatorPrimaryFactNullCheck();

        AnyClassMultipleAttributeEvaluationFragmentTemplate denomTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR,
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(128),
                        primaryFactTemplate);

        AnyClassMultipleAttributeEvaluationFragmentTemplate claimHeaderTemplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ClaimHeader.class,
                        null,
                        ActiveMeasureUtilFunctions
                                .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_07));

        DefaultStandardRuleTemplate rule = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "DENOM_EXCL_DISCHARGED_AGAINST_MEDICAL_ADVICE",
                        ActiveMeasureUtilFunctions
                                .createSetDenominatorExclusionToTrueTemplate(denomTemplate
                                        .getVariableExpression()),
                        denomTemplate,
                        new FromEvaluationFragmentTemplate(claimHeaderTemplate,
                                primaryFactTemplate.getVariableExpression()));
        rule.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_30Day_Readmission_Skilled_Nursing_Facility");
        return rule;
    }
}

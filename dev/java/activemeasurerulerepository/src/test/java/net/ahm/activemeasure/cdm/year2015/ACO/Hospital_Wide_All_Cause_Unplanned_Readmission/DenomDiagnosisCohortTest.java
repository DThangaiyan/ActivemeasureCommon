package net.ahm.activemeasure.cdm.year2015.ACO.Hospital_Wide_All_Cause_Unplanned_Readmission;

import java.util.Collection;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.careengine.common.CECollectionsUtil;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;

/**
 * This test case is mainly responsible for generate the JSON representation of
 * ACO08 rule for DENOM_DIAGNOSIS_COHORT
 * 
 * This test case also generates the DRL version of ACO08 rule
 * DENOM_DIAGNOSIS_COHORT
 * 
 * 
 * @author xsu
 * 
 */
public class DenomDiagnosisCohortTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    static final Collection<Integer> DIAG_ELEMENTS = CECollectionsUtil
                                                           .unmodifiableList(
                                                                   8850, 8851,
                                                                   8852, 8848);

//rule "DENOM_DIAGNOSIS_COHORT"
//dialect "mvel"
//when
//     ActiveMeasuresMemberInfo( ageAtMeasurementEndDate >= 65 )
//     $claimHeaderEvent : ClaimHeader( inpatient == true , endDate <= measurementEndDate  && >= months12BeforeMeasurementEndDate , dischargeDisposition == DischargeDispositionStatus.CD_01  || == DischargeDispositionStatus.CD_03  || == DischargeDispositionStatus.CD_04  || == DischargeDispositionStatus.CD_05  || == DischargeDispositionStatus.CD_06  || == DischargeDispositionStatus.CD_08  || == DischargeDispositionStatus.CD_50  || == DischargeDispositionStatus.CD_51  || == DischargeDispositionStatus.CD_61  || == DischargeDispositionStatus.CD_62  || == DischargeDispositionStatus.CD_63  || == DischargeDispositionStatus.CD_64  || == DischargeDispositionStatus.CD_65  || == DischargeDispositionStatus.CD_70 , allDiagnosticEventElements contains 8850  || contains 8851  || contains 8852  || contains 8848 )
//then
//    FactLevelMeasureDenominator flmDenominator= factLevelMeasureBuilder.newFactLevelMeasureDenominatorWithPrimaryFact(125, $claimHeaderEvent); insert(flmDenominator)
//end
    
    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        DefaultStandardRuleTemplate rule = new DefaultStandardRuleTemplate();
        rule.setRuleName("DENOM_DIAGNOSIS_COHORT");
        rule
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.Hospital_Wide_All_Cause_Unplanned_Readmission");

        DenomEventCohortBase.baseSetup(rule, DIAG_ELEMENTS,
                "allDiagnosticEventElements");
        return rule;
    }
}

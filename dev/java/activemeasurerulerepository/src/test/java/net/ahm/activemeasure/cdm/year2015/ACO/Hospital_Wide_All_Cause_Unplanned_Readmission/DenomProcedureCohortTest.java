package net.ahm.activemeasure.cdm.year2015.ACO.Hospital_Wide_All_Cause_Unplanned_Readmission;

import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.careengine.common.CECollectionsUtil;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;

/**
 * This test case is mainly responsible for generate the JSON representation of
 * ACO08 rule for DENOM_PROCEDURE_COHORT
 * 
 * This test case also generates the DRL version of ACO08 rule
 * DENOM_PROCEDURE_COHORT
 * 
 * 
 * @author ngopalan
 * 
 */
public class DenomProcedureCohortTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    static final Set<Integer> PROC_ELEMENTS = CECollectionsUtil
                                                    .unmodifiableSet(8849);

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        DefaultStandardRuleTemplate rule = new DefaultStandardRuleTemplate();
        rule.setRuleName("DENOM_PROCEDURE_COHORT");
        rule.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.Hospital_Wide_All_Cause_Unplanned_Readmission");

        DenomEventCohortBase.baseSetup(rule, PROC_ELEMENTS,
                "relatedProcedureEventElements");
        return rule;
    }
}

package net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_Unplanned_Admission_For_Patients_with_Chronic_Conditions;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.member.Gender;
import net.ahm.careengine.domain.member.MemberInfo;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassAttributeUpdaterTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;

public class DenomExclNoMemberGenderTest extends
        AbstractActiveMeasuresRuleTemplateTest {

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate denomAssertion = ActiveMeasureUtilFunctions
                .createFactLevelMeasureDenominator(DenomTest.MEASURE_ID);

        AnyClassMultipleAttributeEvaluationFragmentTemplate memberInfoAssertion = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MemberInfo.class, null, ActiveMeasureUtilFunctions
                                .createObjectNullCheckFragment("gender",
                                        CommonOperators.EQUAL_TO, Gender.class));

        AnyClassAttributeUpdaterTemplate action = ActiveMeasureUtilFunctions
                .createAnyClassAttributeUpdaterTempalate(denomAssertion
                        .getVariableExpression(), ActiveMeasureUtilFunctions
                        .createBooleanAttributeSettingInstance(
                                "excludedFromDenominator", true));

        return ActiveMeasureUtilFunctions.createDefaultStandardRuleTemplate(
                "DENOM_EXCL_NO_GENDER", action, denomAssertion,
                memberInfoAssertion);
    }
}

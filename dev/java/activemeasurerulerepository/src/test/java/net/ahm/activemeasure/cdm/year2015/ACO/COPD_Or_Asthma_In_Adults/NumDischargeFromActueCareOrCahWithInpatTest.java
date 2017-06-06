package net.ahm.activemeasure.cdm.year2015.ACO.COPD_Or_Asthma_In_Adults;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.FactLevelMeasureNumeratorCreationTemplate;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.event.adt.model.DischargeDispositionStatus;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.MultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

public class NumDischargeFromActueCareOrCahWithInpatTest extends AbstractActiveMeasuresRuleTemplateTest {

//    rule "NUM_DISCHARGE_FROM_ACTUE_CARE_OR_CAH_WITH_INPAT"
//    dialect "mvel"
//    when
//        $factLevelDenominator : FactLevelMeasureDenominator ( measureId == 126 , $primaryOriginationFact : primaryOriginationFact != null )
//    ClaimHeader ( inpatient == true , ( dischargeDisposition == DischargeDispositionStatus.CD_01 || dischargeDisposition == DischargeDispositionStatus.CD_02 || dischargeDisposition == DischargeDispositionStatus.CD_03 || dischargeDisposition == DischargeDispositionStatus.CD_04 || dischargeDisposition == DischargeDispositionStatus.CD_05 || dischargeDisposition == DischargeDispositionStatus.CD_06 || dischargeDisposition == DischargeDispositionStatus.CD_08 || dischargeDisposition == DischargeDispositionStatus.CD_50 || dischargeDisposition == DischargeDispositionStatus.CD_51 || dischargeDisposition == DischargeDispositionStatus.CD_61 || dischargeDisposition == DischargeDispositionStatus.CD_62 || dischargeDisposition == DischargeDispositionStatus.CD_63 || dischargeDisposition == DischargeDispositionStatus.CD_64 || dischargeDisposition == DischargeDispositionStatus.CD_65 || dischargeDisposition == DischargeDispositionStatus.CD_70 ) ) from $primaryOriginationFact
//    then
//        FactLevelMeasureNumerator $factLevelNumerator = $factLevelDenominator.createFactLevelMeasureNumeratorWithPrimaryFact($primaryOriginationFact);
//    insert($factLevelNumerator);
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
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(126),
                        primaryFactTemplate);

        AnyClassMultipleAttributeEvaluationFragmentTemplate claimHeaderTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        ClaimHeader.class, null, UtilFunctions
                                .createBooleanCheckFragment("inpatient", true,
                                        CommonOperators.EQUAL_TO),
                        checkDischargeConditions());

        DefaultStandardRuleTemplate rule = UtilFunctions
                .createDefaultStandardRuleTemplate(
                        "NUM_DISCHARGE_FROM_ACTUE_CARE_OR_CAH_WITH_INPAT",
                        new FactLevelMeasureNumeratorCreationTemplate(
                                denomTemplate.getVariableExpression(),
                                primaryFactTemplate.getVariableExpression()),
                        denomTemplate,
                        new FromEvaluationFragmentTemplate(claimHeaderTemplate,
                                primaryFactTemplate.getVariableExpression()));
        rule.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.Hospital_Wide_All_Cause_Unplanned_Readmission");
        return rule;
	}

    private static MultipleAttributeEvaluationFragmentTemplate checkDischargeConditions() {
        AttributeFragmentTemplate dischargeDispAttribute = new AttributeFragmentTemplate(
                "dischargeDisposition",
                TypeDescription
                        .getTypeDescription(DischargeDispositionStatus.class));

        return UtilFunctions.createMultipleAttributeEvaluationFragmentTemplate(
                        Connector.OR,
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        dischargeDispAttribute,
                                        UtilFunctions
                                                .createEnumLiteralExpressionFragmentTemplate(
                                                        DischargeDispositionStatus.class,
                                                        DischargeDispositionStatus.CD_01),
                                        CommonOperators.EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        dischargeDispAttribute,
                                        UtilFunctions
                                                .createEnumLiteralExpressionFragmentTemplate(
                                                        DischargeDispositionStatus.class,
                                                        DischargeDispositionStatus.CD_02),
                                        CommonOperators.EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        dischargeDispAttribute,
                                        UtilFunctions
                                                .createEnumLiteralExpressionFragmentTemplate(
                                                        DischargeDispositionStatus.class,
                                                        DischargeDispositionStatus.CD_03),
                                        CommonOperators.EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        dischargeDispAttribute,
                                        UtilFunctions
                                                .createEnumLiteralExpressionFragmentTemplate(
                                                        DischargeDispositionStatus.class,
                                                        DischargeDispositionStatus.CD_04),
                                        CommonOperators.EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        dischargeDispAttribute,
                                        UtilFunctions
                                                .createEnumLiteralExpressionFragmentTemplate(
                                                        DischargeDispositionStatus.class,
                                                        DischargeDispositionStatus.CD_05),
                                        CommonOperators.EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        dischargeDispAttribute,
                                        UtilFunctions
                                                .createEnumLiteralExpressionFragmentTemplate(
                                                        DischargeDispositionStatus.class,
                                                        DischargeDispositionStatus.CD_06),
                                        CommonOperators.EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        dischargeDispAttribute,
                                        UtilFunctions
                                                .createEnumLiteralExpressionFragmentTemplate(
                                                        DischargeDispositionStatus.class,
                                                        DischargeDispositionStatus.CD_08),
                                        CommonOperators.EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        dischargeDispAttribute,
                                        UtilFunctions
                                                .createEnumLiteralExpressionFragmentTemplate(
                                                        DischargeDispositionStatus.class,
                                                        DischargeDispositionStatus.CD_50),
                                        CommonOperators.EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        dischargeDispAttribute,
                                        UtilFunctions
                                                .createEnumLiteralExpressionFragmentTemplate(
                                                        DischargeDispositionStatus.class,
                                                        DischargeDispositionStatus.CD_51),
                                        CommonOperators.EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        dischargeDispAttribute,
                                        UtilFunctions
                                                .createEnumLiteralExpressionFragmentTemplate(
                                                        DischargeDispositionStatus.class,
                                                        DischargeDispositionStatus.CD_61),
                                        CommonOperators.EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        dischargeDispAttribute,
                                        UtilFunctions
                                                .createEnumLiteralExpressionFragmentTemplate(
                                                        DischargeDispositionStatus.class,
                                                        DischargeDispositionStatus.CD_62),
                                        CommonOperators.EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        dischargeDispAttribute,
                                        UtilFunctions
                                                .createEnumLiteralExpressionFragmentTemplate(
                                                        DischargeDispositionStatus.class,
                                                        DischargeDispositionStatus.CD_63),
                                        CommonOperators.EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        dischargeDispAttribute,
                                        UtilFunctions
                                                .createEnumLiteralExpressionFragmentTemplate(
                                                        DischargeDispositionStatus.class,
                                                        DischargeDispositionStatus.CD_64),
                                        CommonOperators.EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        dischargeDispAttribute,
                                        UtilFunctions
                                                .createEnumLiteralExpressionFragmentTemplate(
                                                        DischargeDispositionStatus.class,
                                                        DischargeDispositionStatus.CD_65),
                                        CommonOperators.EQUAL_TO, null),
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        dischargeDispAttribute,
                                        UtilFunctions
                                                .createEnumLiteralExpressionFragmentTemplate(
                                                        DischargeDispositionStatus.class,
                                                        DischargeDispositionStatus.CD_70),
                                        CommonOperators.EQUAL_TO, null));
    }
}

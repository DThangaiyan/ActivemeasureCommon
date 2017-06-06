package net.ahm.activemeasure.cdm.year2015.ACO.Hospital_Wide_All_Cause_Unplanned_Readmission;

import java.util.Date;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.DateDefinitionTemplate;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.event.adt.model.DischargeDispositionStatus;
import net.ahm.careengine.ruleengine.DateTimeUnit;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassAttributeUpdaterTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.DateShiftFunction;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Simulate rule DENOM_EXCL_ACUTE_CARE_TRANSFER
 * 
 * @author xsu
 */
public class DenomExcelAcuteCareTransferTest extends
        AbstractActiveMeasuresRuleTemplateTest {

//        rule "DENOM_EXCL_ACUTE_CARE_TRANSFER"
//        dialect "mvel"
//        when
//             $flm : FactLevelMeasureDenominator( $primaryFact : primaryOriginationFact, measureId == 125 )
//             $initialClaimHeader : ClaimHeader( dischargeDisposition == DischargeDispositionStatus.CD_05 , $initialDischarge : endDate) from $primaryFact
//            $dayAfterDischarge : java.util.Date() from GlobalFunctions.getLaterDate($initialDischarge, 1, DateTimeUnit.DAY)
//             ClaimHeader( this != $initialClaimHeader , startDate >= $initialDischarge , startDate <= ( $dayAfterDischarge ) , inpatient == true )
//        then
//             $flm.setExcludedFromDenominator( true );
//    end
    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        DefaultStandardRuleTemplate rule = new DefaultStandardRuleTemplate();
        rule.setRuleName("DENOM_EXCL_ACUTE_CARE_TRANSFER");
        rule.setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.cdm.year2015.ACO.Hospital_Wide_All_Cause_Unplanned_Readmission");


        AnyClassMultipleAttributeEvaluationFragmentTemplate denomTemplate = ActiveMeasureUtilFunctions.createFactLevelMeasureDenominator(125);
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactTemplate = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment(
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                        CommonOperators.NOT_EQUAL_TO, Fact.class);
        primaryFactTemplate.setVariableName("$primaryFact");
        denomTemplate.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments().add(primaryFactTemplate);
        rule.getConditions().add(denomTemplate);

        AnyClassMultipleAttributeEvaluationFragmentTemplate initialClaimHeaderTemplate = new AnyClassMultipleAttributeEvaluationFragmentTemplate();
        initialClaimHeaderTemplate.getMultipleAttributeEvaluationFragments()
                .setConnector(Connector.COMMA);
        initialClaimHeaderTemplate.setVariableName("$initialClaimHeader");
        initialClaimHeaderTemplate
                .setExpressionReturnTypeDescription(TypeDescription
                        .getTypeDescription(ClaimHeader.class));
        initialClaimHeaderTemplate
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(ActiveMeasureUtilFunctions
                        .createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_05));
        AnyAttributeSingleComparisonFragmentTemplate<Date> initialDischargeDateTemplate = ActiveMeasureUtilFunctions
                .createNamedAttributeFragmentWithoutCheck("$initialDischarge",
                        "endDate", Date.class);
        initialClaimHeaderTemplate
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(initialDischargeDateTemplate);

        FromEvaluationFragmentTemplate fromTemplate = new FromEvaluationFragmentTemplate(
                initialClaimHeaderTemplate,
                primaryFactTemplate.getVariableExpression());
        rule.getConditions().add(fromTemplate);

        DateDefinitionTemplate dateTemplate = ActiveMeasureUtilFunctions
                .createDateDefinitionTemplate("$dayAfterDischarge",
                        DateShiftFunction.GET_LATER_DATE,
                        initialDischargeDateTemplate.getVariableExpression(),
                        1, DateTimeUnit.DAY);
        rule.getConditions().add(dateTemplate);

        AnyClassMultipleAttributeEvaluationFragmentTemplate otherClaimHeaderTemplate = new AnyClassMultipleAttributeEvaluationFragmentTemplate();
        otherClaimHeaderTemplate.getMultipleAttributeEvaluationFragments()
                .setConnector(Connector.COMMA);
        otherClaimHeaderTemplate
                .setExpressionReturnTypeDescription(TypeDescription
                        .getTypeDescription(ClaimHeader.class));
        otherClaimHeaderTemplate
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(ActiveMeasureUtilFunctions
                        .createObjectVariableCheckFragment("this",
                                ClaimHeader.class,
                                CommonOperators.NOT_EQUAL_TO,
                                initialClaimHeaderTemplate
                                        .getVariableExpression()));
        otherClaimHeaderTemplate
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(ActiveMeasureUtilFunctions
                        .createObjectVariableCheckFragment("startDate",
                                Date.class,
                                CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                initialDischargeDateTemplate
                                        .getVariableExpression()));
        otherClaimHeaderTemplate
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(ActiveMeasureUtilFunctions
                        .createObjectVariableCheckFragment("startDate",
                                Date.class,
                                CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                dateTemplate.getVariableExpression()));
        otherClaimHeaderTemplate
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(ActiveMeasureUtilFunctions.createBooleanCheckFragment(
                        "inpatient", true, CommonOperators.EQUAL_TO));
        rule.getConditions().add(otherClaimHeaderTemplate);

        AnyClassAttributeUpdaterTemplate denominatorUpdater = new AnyClassAttributeUpdaterTemplate(
                denomTemplate.getVariableExpression());
        denominatorUpdater.getAttributeUpdateFragments().add(
                UtilFunctions.createBooleanAttributeSettingInstance(
                        "excludedFromDenominator", true));
        rule.getActions().add(denominatorUpdater);

        return rule;
    }
}

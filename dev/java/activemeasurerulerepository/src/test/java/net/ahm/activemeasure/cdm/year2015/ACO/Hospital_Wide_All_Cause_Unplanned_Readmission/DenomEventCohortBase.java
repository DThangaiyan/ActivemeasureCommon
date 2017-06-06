package net.ahm.activemeasure.cdm.year2015.ACO.Hospital_Wide_All_Cause_Unplanned_Readmission;

import java.util.Collection;
import java.util.Set;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.FactLevelMeasureDenominatorCreationTemplate;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.event.adt.model.DischargeDispositionStatus;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.MultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * This test case is mainly responsible for generate the JSON representation of
 * ACO08 rule for DENOM_COHORT
 * 
 * Test Base for DENOM_COHORT
 * 
 * 
 * @author ngopalan
 * 
 */
public abstract class DenomEventCohortBase extends
        AbstractActiveMeasuresRuleTemplateTest {

    public static void baseSetup(
            DefaultStandardRuleTemplate ruleTemplate,
            Collection<Integer> cohortElements,
            String cohortElementAttributeName) {

        // ActiveMeasuresMemberInfo( ageAtMeasurementEndDate >= 65 )
        AnyClassMultipleAttributeEvaluationFragmentTemplate activeMeasuresMemberCheck = ActiveMeasureUtilFunctions
                .createActiveMeasuresMemberInfoFragment(65, 0, false, null);

        ruleTemplate.getConditions().add(activeMeasuresMemberCheck);

		AnyClassMultipleAttributeEvaluationFragmentTemplate claimHeaderEventCheck = createCondition(
				ClaimHeader.class, "$claimHeaderEvent");

		// inpatient == true
		claimHeaderEventCheck.getMultipleAttributeEvaluationFragments()
				.getAttributeEvaluationFragments()
				.add(UtilFunctions.createBooleanCheckFragment("inpatient",
                        true, CommonOperators.EQUAL_TO));

		// endDate <= measurementEndDate && >= months12BeforeMeasurementEndDate
        claimHeaderEventCheck
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(UtilFunctions
                        .createDateAttributeAndVariableCheckFragment(
                                "endDate",
                                CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                ActiveMeasureUtilFunctions
                                        .getGlobalVarible(ActiveMeasureGlobalDefinition.MEASUREMENT_END_DATE)));
        claimHeaderEventCheck
                .getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(UtilFunctions
                        .createDateAttributeAndVariableCheckFragment(
                                "endDate",
                                CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                ActiveMeasureUtilFunctions
                                        .getGlobalVarible(ActiveMeasureGlobalDefinition.MONTHS_12_BEFORE_MEASUREMENT_END_DATE)));

		MultipleAttributeEvaluationFragmentTemplate dischargeDispositionChecks = new MultipleAttributeEvaluationFragmentTemplate();
		dischargeDispositionChecks.setConnector(Connector.OR);

		dischargeDispositionChecks
				.getAttributeEvaluationFragments()
				.add(ActiveMeasureUtilFunctions
						.createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_01));
		dischargeDispositionChecks
				.getAttributeEvaluationFragments()
				.add(ActiveMeasureUtilFunctions
						.createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_03));
		dischargeDispositionChecks
				.getAttributeEvaluationFragments()
				.add(ActiveMeasureUtilFunctions
						.createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_04));
		dischargeDispositionChecks
				.getAttributeEvaluationFragments()
				.add(ActiveMeasureUtilFunctions
						.createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_05));
		dischargeDispositionChecks
				.getAttributeEvaluationFragments()
				.add(ActiveMeasureUtilFunctions
						.createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_06));
		dischargeDispositionChecks
				.getAttributeEvaluationFragments()
				.add(ActiveMeasureUtilFunctions
						.createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_08));

		dischargeDispositionChecks
				.getAttributeEvaluationFragments()
				.add(ActiveMeasureUtilFunctions
						.createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_50));
		dischargeDispositionChecks
				.getAttributeEvaluationFragments()
				.add(ActiveMeasureUtilFunctions
						.createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_51));
		dischargeDispositionChecks
				.getAttributeEvaluationFragments()
				.add(ActiveMeasureUtilFunctions
						.createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_61));
		dischargeDispositionChecks
				.getAttributeEvaluationFragments()
				.add(ActiveMeasureUtilFunctions
						.createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_62));

		dischargeDispositionChecks
				.getAttributeEvaluationFragments()
				.add(ActiveMeasureUtilFunctions
						.createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_63));
		dischargeDispositionChecks
				.getAttributeEvaluationFragments()
				.add(ActiveMeasureUtilFunctions
						.createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_64));
		dischargeDispositionChecks
				.getAttributeEvaluationFragments()
				.add(ActiveMeasureUtilFunctions
						.createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_65));
		dischargeDispositionChecks
				.getAttributeEvaluationFragments()
				.add(ActiveMeasureUtilFunctions
						.createDischargeDispositionStatusFragment(DischargeDispositionStatus.CD_70));

		claimHeaderEventCheck.getMultipleAttributeEvaluationFragments()
				.getAttributeEvaluationFragments()
				.add(dischargeDispositionChecks);

        if (cohortElements.size() > 1) {
            MultipleAttributeEvaluationFragmentTemplate cohortEventElementsCheck = new MultipleAttributeEvaluationFragmentTemplate();
            cohortEventElementsCheck.setConnector(Connector.OR);
            for (int cohortElement : cohortElements) {
                cohortEventElementsCheck.getAttributeEvaluationFragments().add(
                        ActiveMeasureUtilFunctions
                                .createElementContainsValueTemplate(
                                        cohortElementAttributeName, cohortElement));
            }
            claimHeaderEventCheck.getMultipleAttributeEvaluationFragments()
                    .getAttributeEvaluationFragments()
                    .add(cohortEventElementsCheck);
        } else if (cohortElements.size() == 1) {
            AnyAttributeSingleComparisonFragmentTemplate<Set<Integer>> cohortEventElementsCheck = ActiveMeasureUtilFunctions
                    .createElementContainsValueTemplate(cohortElementAttributeName,
                            cohortElements.iterator().next());
            claimHeaderEventCheck.getMultipleAttributeEvaluationFragments()
                    .getAttributeEvaluationFragments()
                    .add(cohortEventElementsCheck);
        }

        ruleTemplate.getConditions().add(claimHeaderEventCheck);

        FactLevelMeasureDenominatorCreationTemplate actionInstance = new FactLevelMeasureDenominatorCreationTemplate();
        actionInstance.setMeasureId(125);
        actionInstance.setPrimaryFactTemplate(claimHeaderEventCheck
                .getVariableExpression());
        ruleTemplate.getActions().add(actionInstance);
	}
}

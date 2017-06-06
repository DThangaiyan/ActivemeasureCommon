package net.ahm.activemeasure.cdm.year2015.ACO.All_Cause_Unplanned_Admission_For_Patients_with_Chronic_Conditions;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.classifier.Classifier;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.CollectionEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

public class DenomTest extends AbstractActiveMeasuresRuleTemplateTest {
    // the actual classifiers for this measure have not been created yet -
    // 2/12/2016
    public static final int CHRONIC_AMI_CLASSIFIER_ID                 = -1;
    public static final int CHRONIC_ALZHEIMERS_DEMENTIA_CLASSIFIER_ID = -2;
    public static final int CHRONIC_A_FIB_CLASSIFIER_ID               = -3;
    public static final int CHRONIC_CKD_CLASSIFIER_ID                 = -4;
    public static final int CHRONIC_COPD_OR_ASTHMA_CLASSIFIER_ID      = -5;
    public static final int CHRONIC_DEPRESSION_CLASSIFIER_ID          = -6;
    public static final int CHRONIC_HEART_FAILURE_CLASSIFIER_ID       = -7;
    public static final int CHRONIC_STROKE_OR_TIA_CLASSIFIER_ID       = -8;

    public static final int  MEASURE_ID                                = 131;

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        DefaultStandardRuleTemplate rule = new DefaultStandardRuleTemplate();
        rule.setRuleName("DENOM_2_OR_MORE_CHRONIC_DISEASE_GROUPS");

        rule.getConditions().add(
                ActiveMeasureUtilFunctions
                        .createActiveMeasuresMemberInfoFragment(65, -1, false,
                                null));


        AnyClassMultipleAttributeEvaluationFragmentTemplate classifierTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        Classifier.class,
                        null,
                        ActiveMeasureUtilFunctions
                                .createMultipleAttributeEvaluationFragmentTemplate(
                                        Connector.OR,
                                        createClassifierIdAssertions(
                                                CHRONIC_AMI_CLASSIFIER_ID,
                                                CHRONIC_ALZHEIMERS_DEMENTIA_CLASSIFIER_ID,
                                                CHRONIC_A_FIB_CLASSIFIER_ID,
                                                CHRONIC_CKD_CLASSIFIER_ID,
                                                CHRONIC_COPD_OR_ASTHMA_CLASSIFIER_ID,
                                                CHRONIC_DEPRESSION_CLASSIFIER_ID,
                                                CHRONIC_HEART_FAILURE_CLASSIFIER_ID,
                                                CHRONIC_STROKE_OR_TIA_CLASSIFIER_ID)));

        CollectionEvaluationFragmentTemplate classifierCountTemplate = ActiveMeasureUtilFunctions
                .createCollectionEvaluationFragmentTemplate(
                        ActiveMeasureUtilFunctions
                                .createCollectionSize(null, 2),
                        classifierTemplate);
        rule.getConditions().add(classifierCountTemplate);

        rule.getActions().add(
                ActiveMeasureUtilFunctions
                        .createFactLevelMeasureDenominatorCreationTemplate(
                                MEASURE_ID, null));
        return rule;
    }

    static AnyAttributeSingleComparisonFragmentTemplate<?>[] createClassifierIdAssertions(
            int... classifierIdArray) {
        final int size = classifierIdArray.length;
        AnyAttributeSingleComparisonFragmentTemplate<?>[] result = new AnyAttributeSingleComparisonFragmentTemplate[size];

        for (int i = 0; i < size; i++) {
            result[i] = ActiveMeasureUtilFunctions
                    .createAnyAttributeSingleComparisonFragmentTemplate(
                            new AttributeFragmentTemplate("id", TypeDescription
                                    .getTypeDescription(Long.TYPE)),
                            UtilFunctions
                                    .createIntegerLiteralFragmentTemplate(classifierIdArray[i]),
                            CommonOperators.EQUAL_TO, null);
        }

        return result;
    }
}

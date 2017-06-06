package net.ahm.activemeasure.util;

import java.util.Set;

import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition;
import net.ahm.activemeasure.templates.util.BaseActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassAttributeUpdaterTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.Operator;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Provide some reusable functions
 * 
 * @author xsu
 * 
 */
public class ActiveMeasureUtilFunctions extends BaseActiveMeasureUtilFunctions {

    public static NamedVariableLiteralFragmentTemplate getGlobalVarible(
            ActiveMeasureGlobalDefinition definition) {
        return ActiveMeasureGlobalContainer.INSTANCE.getByDefinition(definition);
    }

    public static AnyAttributeSingleComparisonFragmentTemplate<Object> createFactLevelDenominatorPrimaryFactNullCheck() {
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactExpr = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment(
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                        CommonOperators.NOT_EQUAL_TO, Fact.class);
        primaryFactExpr
                .setVariableName("$"
                        + ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME);

        return primaryFactExpr;
    }

    public static AnyAttributeSingleComparisonFragmentTemplate<Object> createFactLevelNumeratorPrimaryFactNullCheck() {
        AnyAttributeSingleComparisonFragmentTemplate<Object> primaryFactExpr = ActiveMeasureUtilFunctions
                .createObjectNullCheckFragment(
                        ActiveMeasureUtilConstants.FACT_LEVEL_NUMERATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                        CommonOperators.NOT_EQUAL_TO, Fact.class);
        primaryFactExpr
                .setVariableName("$"
                        + ActiveMeasureUtilConstants.FACT_LEVEL_NUMERATOR_PRIMARY_FACT_ATTRIBUTE_NAME);

        return primaryFactExpr;
    }

    public static <T> AnyAttributeSingleComparisonFragmentTemplate<T> createMeasureIdInOrEqualToAttributeFagementTemplate(
            Integer... measureIds) {
        if (measureIds.length == 1) {
            return createAnyAttributeSingleComparisonFragmentTemplate(
                    ActiveMeasureUtilConstants.MEASURE_ID,
                    TypeDescription.getTypeDescription(Long.TYPE),
                    createIntegerLiteralFragmentTemplate(measureIds[0]),
                    CommonOperators.EQUAL_TO, null);
        } else {
            return createAnyAttributeSingleComparisonFragmentTemplate(
                    ActiveMeasureUtilConstants.MEASURE_ID,
                    TypeDescription.getTypeDescription(Long.TYPE),
                    createIntegerCollectionLiteralFragmentTemplate(measureIds),
                    CommonOperators.IN, null);
        }
    }

    public static AnyClassAttributeUpdaterTemplate createSetDenominatorExclusionToTrueTemplate(
            NamedVariableLiteralFragmentTemplate denomVaribleTemplate) {
        return ActiveMeasureUtilFunctions
                .createAnyClassAttributeUpdaterTempalate(denomVaribleTemplate,
                        UtilFunctions.createBooleanAttributeSettingInstance(
                                "excludedFromDenominator", true));
    }

    public static AnyClassAttributeUpdaterTemplate createSetNumeratorExclusionToTrueTemplate(
            NamedVariableLiteralFragmentTemplate numeratorVaribleTemplate) {
        return ActiveMeasureUtilFunctions
                .createAnyClassAttributeUpdaterTempalate(
                        numeratorVaribleTemplate, UtilFunctions
                                .createBooleanAttributeSettingInstance(
                                        "excludedFromNumerator", true));
    }

    public static AnyClassAttributeUpdaterTemplate createSetDenominatorEligibilityToFalseTemplate(
            NamedVariableLiteralFragmentTemplate denomVaribleTemplate) {
        return ActiveMeasureUtilFunctions
                .createAnyClassAttributeUpdaterTempalate(denomVaribleTemplate,
                        UtilFunctions.createBooleanAttributeSettingInstance(
                                "eligible", false));
    }

    public static AnyAttributeSingleComparisonFragmentTemplate<Set<Integer>> createElementContainsValueTemplate(
            String attributeName, int expectedValue) {
        return createElementValueTemplate(attributeName, expectedValue,
                CommonOperators.CONTAINS);
    }

    public static AnyAttributeSingleComparisonFragmentTemplate<Set<Integer>> createElementExcludesValueTemplate(
            String attributeName, int expectedValue) {
        return createElementValueTemplate(attributeName, expectedValue,
                CommonOperators.NOT_CONTAINS);
    }

    private static AnyAttributeSingleComparisonFragmentTemplate<Set<Integer>> createElementValueTemplate(
            String attributeName, int expectedValue, Operator operator) {
        return UtilFunctions
                .createAnyAttributeSingleComparisonFragmentTemplate(
                        new AttributeFragmentTemplate(attributeName,
                                TypeDescription.getTypeDescription(Set.class,
                                        Integer.class)),
                        UtilFunctions
                                .createIntegerLiteralFragmentTemplate(expectedValue),
                        operator, null);
    }
}

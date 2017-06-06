package net.ahm.activemeasure.templates.util;

import java.util.Collection;

import net.ahm.activemeasure.templates.ActiveMeasureDenominatorCreationTemplate;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.ContiguousDaysPaddingTemplate;
import net.ahm.activemeasure.templates.DateDefinitionTemplate;
import net.ahm.activemeasure.templates.FactLevelMeasureDenominatorCreationTemplate;
import net.ahm.activemeasure.templates.FactLevelMeasureNumeratorCreationTemplate;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureNumerator;
import net.ahm.careengine.domain.member.ActiveMeasuresMemberInfo;
import net.ahm.careengine.domain.member.Gender;
import net.ahm.careengine.event.adt.model.DischargeDispositionStatus;
import net.ahm.careengine.ruleengine.DateTimeUnit;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.interfaces.ActualValueExpression;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.DateShiftFunctionTemplate;
import net.ahm.rulesapp.templates.libraries.IntegerCollectionLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.DateShiftFunction;
import net.ahm.rulesapp.util.Operator;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Provide some reusable functions
 * 
 * @author xsu
 * 
 */
public class BaseActiveMeasureUtilFunctions extends UtilFunctions {

    public static final String GENDER                      = "gender";
    public static final String AGE_AT_MEASUREMENT_END_DATE = "ageAtMeasurementEndDate";

    /**
     * 
     * @param code
     * @return dischargeDisposition==code (parameter)
     */
    public static AnyAttributeSingleComparisonFragmentTemplate<DischargeDispositionStatus> createDischargeDispositionStatusFragment(
            DischargeDispositionStatus code) {
        return createAnyAttributeSingleComparisonFragmentTemplate(
                new AttributeFragmentTemplate(
                        "dischargeDisposition",
                        TypeDescription
                                .getTypeDescription(DischargeDispositionStatus.class)),
                createEnumLiteralExpressionFragmentTemplate(
                        DischargeDispositionStatus.class, code),
                CommonOperators.EQUAL_TO, null);
    }

    public static AnyAttributeSingleComparisonFragmentTemplate<Integer> createMultipleMeasureIds(
            Integer... measureIds) {
        AnyAttributeSingleComparisonFragmentTemplate<Integer> multipleMeasureIdInClause = new AnyAttributeSingleComparisonFragmentTemplate<Integer>();

        IntegerCollectionLiteralFragmentTemplate integerCollLiteralTemp = createIntegerCollectionLiteralFragmentTemplate(measureIds);
        multipleMeasureIdInClause.setAttribute(new AttributeFragmentTemplate(
                ActiveMeasureUtilConstants.MEASURE_ID, TypeDescription
                        .getTypeDescription(Long.TYPE)));
        multipleMeasureIdInClause.setOperator(CommonOperators.IN);
        multipleMeasureIdInClause.setAttributeValue(integerCollLiteralTemp);

        return multipleMeasureIdInClause;
    }

    public static AnyAttributeSingleComparisonFragmentTemplate<Integer> createSingleMeasureIds(
            Integer measureId) {
        return createAnyAttributeSingleComparisonFragmentTemplate(
                new AttributeFragmentTemplate(
                        ActiveMeasureUtilConstants.MEASURE_ID,
                        TypeDescription.getTypeDescription(Long.TYPE)),
                createIntegerLiteralFragmentTemplate(measureId),
                CommonOperators.EQUAL_TO, null);
    }

    @Deprecated
    public static AnyAttributeSingleComparisonFragmentTemplate<String> setConditions(
            String varibleName, String AttrName, Class<?> attrClass, Operator optr) {
        NamedVariableLiteralFragmentTemplate denomEventTemplate1 = new NamedVariableLiteralFragmentTemplate(
                varibleName, String.class);
        AnyAttributeSingleComparisonFragmentTemplate<String> stringComp = new AnyAttributeSingleComparisonFragmentTemplate<String>();
        stringComp.setAttribute(new AttributeFragmentTemplate(AttrName,
                TypeDescription.getTypeDescription(attrClass)));
        stringComp.setOperator(optr);
        stringComp.setAttributeValue(denomEventTemplate1);
        return stringComp;
    }

    public static <T> AnyAttributeSingleComparisonFragmentTemplate<T> createAnyAttributeSingleComparisonFragmentTemplate(
            String attributeName, TypeDescription attributeType,
            ActualValueExpression value,
            Operator operator, String variableName) {
        return createAnyAttributeSingleComparisonFragmentTemplate(
                new AttributeFragmentTemplate(attributeName, attributeType),
                value, operator, variableName);
    }

    public static AnyClassMultipleAttributeEvaluationFragmentTemplate createFactLevelMeasureDenominator(
            Integer... measureIds) {
        return createFactLevelMeasureX(
                ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR,
                FactLevelMeasureDenominator.class, measureIds);
    }

    private static AnyClassMultipleAttributeEvaluationFragmentTemplate createFactLevelMeasureX(
            String variabaleName, Class<?> patternClass, Integer... measureIds) {
        AnyClassMultipleAttributeEvaluationFragmentTemplate denomTemplate = new AnyClassMultipleAttributeEvaluationFragmentTemplate();
        denomTemplate.setExpressionReturnTypeDescription(TypeDescription
                .getTypeDescription(patternClass));
        denomTemplate.setVariableName(variabaleName);
        denomTemplate.getMultipleAttributeEvaluationFragments().setConnector(
                Connector.COMMA);

        if (measureIds == null || measureIds.length < 1) {
            // do nothing
        } else if (measureIds.length == 1) {
            AnyAttributeSingleComparisonFragmentTemplate<Integer> measureIdTemplate = createSingleMeasureIds(measureIds[0]);
            denomTemplate.getMultipleAttributeEvaluationFragments()
                    .getAttributeEvaluationFragments().add(measureIdTemplate);
        } else if (measureIds.length > 1) {
            AnyAttributeSingleComparisonFragmentTemplate<Integer> measureIdTemplate = createMultipleMeasureIds(measureIds);
            denomTemplate.getMultipleAttributeEvaluationFragments()
                    .getAttributeEvaluationFragments().add(measureIdTemplate);
        }

        return denomTemplate;
    }

    public static AnyClassMultipleAttributeEvaluationFragmentTemplate createFactLevelMeasureNumerator(
            Integer... measureIds) {
        return createFactLevelMeasureX(
                ActiveMeasureUtilConstants.FACT_LEVEL_NUMERATOR,
                FactLevelMeasureNumerator.class, measureIds);
    }

    public static <T> AnyAttributeSingleComparisonFragmentTemplate<T> createNamedAttributeFragmentWithoutCheck(
            String variableName, String attributeName, Class<T> classPattern) {
        TypeDescription type = TypeDescription.getTypeDescription(classPattern);
        validateTypeDescription(type);
        AnyAttributeSingleComparisonFragmentTemplate<T> result = new AnyAttributeSingleComparisonFragmentTemplate<T>();
        result.setAttribute(new AttributeFragmentTemplate(attributeName, type));
        result.setVariableName(variableName);
        return result;
    }

    private static void validateTypeDescription(TypeDescription type) {
        if (type.isCollection() || type.isArray()) {
            throw new IllegalArgumentException("Can't use " + type
                    + " without the contained type");
        }
    }

    public static AnyAttributeSingleComparisonFragmentTemplate<Object> createObjectVariableCheckFragment(
            String elementAttributeName, Class<?> attributeClassPattern,
            Operator operator,
            NamedVariableLiteralFragmentTemplate variableExpression) {
        TypeDescription type = TypeDescription
                .getTypeDescription(attributeClassPattern);
        validateTypeDescription(type);
        AnyAttributeSingleComparisonFragmentTemplate<Object> result = new AnyAttributeSingleComparisonFragmentTemplate<Object>();
        result.setAttribute(new AttributeFragmentTemplate(elementAttributeName,
                type));
        result.setOperator(operator);
        result.setAttributeValue(variableExpression);
        return result;
    }

    public static AnyAttributeSingleComparisonFragmentTemplate<Object> createObjectVariableCheckFragment(
            String elementAttributeName, TypeDescription attributeType,
            Operator operator,
            NamedVariableLiteralFragmentTemplate variableExpression) {
        AnyAttributeSingleComparisonFragmentTemplate<Object> result = new AnyAttributeSingleComparisonFragmentTemplate<Object>();
        result.setAttribute(new AttributeFragmentTemplate(elementAttributeName,
                attributeType));
        result.setOperator(operator);
        result.setAttributeValue(variableExpression);
        return result;
    }

    public static AnyClassMultipleAttributeEvaluationFragmentTemplate createActiveMeasuresMemberInfoFragment(
            int minAge, int maxAge, boolean validateGender,
            Gender expectedGender) {
        AnyClassMultipleAttributeEvaluationFragmentTemplate flmTemplate = new AnyClassMultipleAttributeEvaluationFragmentTemplate();
        flmTemplate.setExpressionReturnTypeDescription(TypeDescription
                .getTypeDescription(ActiveMeasuresMemberInfo.class));
        flmTemplate.getMultipleAttributeEvaluationFragments().setConnector(
                Connector.COMMA);

        if (minAge > 0) {
            flmTemplate
                    .getMultipleAttributeEvaluationFragments()
                    .getAttributeEvaluationFragments()
                    .add(createIntegerCheckFragment(
                            AGE_AT_MEASUREMENT_END_DATE, minAge,
                            CommonOperators.GREATER_THAN_OR_EQUAL_TO));
        }

        if (maxAge > 0) {
            flmTemplate
                    .getMultipleAttributeEvaluationFragments()
                    .getAttributeEvaluationFragments()
                    .add(createIntegerCheckFragment(
                            AGE_AT_MEASUREMENT_END_DATE, maxAge,
                            CommonOperators.LESS_THAN_OR_EQUAL_TO));
        }

        if (validateGender) {
            flmTemplate
                    .getMultipleAttributeEvaluationFragments()
                    .getAttributeEvaluationFragments()
                    .add(createObjectNullCheckFragment(GENDER,
                            CommonOperators.NOT_EQUAL_TO, String.class));
        }

        if (expectedGender != null) {
            flmTemplate
                    .getMultipleAttributeEvaluationFragments()
                    .getAttributeEvaluationFragments()
                    .add(createAnyAttributeSingleComparisonFragmentTemplate(
                            new AttributeFragmentTemplate(GENDER,
                                    TypeDescription
                                            .getTypeDescription(Gender.class)),
                            createEnumLiteralExpressionFragmentTemplate(
                                    Gender.class, expectedGender),
                            CommonOperators.EQUAL_TO, null));
        }

        return flmTemplate;
    }

    public static AnyClassMultipleAttributeEvaluationFragmentTemplate createCollectionSize(
            String collectionName, int minumumSize) {
        return createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                Collection.class,
                collectionName,
                createAnyAttributeSingleComparisonFragmentTemplate(
                        new AttributeFragmentTemplate("size", TypeDescription
                                .getTypeDescription(Integer.TYPE)),
                        createIntegerLiteralFragmentTemplate(minumumSize),
                        CommonOperators.GREATER_THAN_OR_EQUAL_TO, null));
    }

    public static FactLevelMeasureNumeratorCreationTemplate createFactLevelMeasureNumeratorCreationTemplate(
            AnyClassMultipleAttributeEvaluationFragmentTemplate denomTemplate,
            AnyClassMultipleAttributeEvaluationFragmentTemplate primaryFact) {
        FactLevelMeasureNumeratorCreationTemplate numCreationTemplate = new FactLevelMeasureNumeratorCreationTemplate(
                denomTemplate.getVariableExpression(),
                primaryFact != null ? primaryFact.getVariableExpression()
                        : null);
        return numCreationTemplate;
    }

    public static DateDefinitionTemplate createDateDefinitionTemplate(
            String variableName, DateShiftFunction dateShiftFunction,
            NamedVariableLiteralFragmentTemplate inputDateVariable,
            int ammount,
            DateTimeUnit unit) {
        DateShiftFunctionTemplate dateShiftFunctionTemplate = new DateShiftFunctionTemplate(
                inputDateVariable, dateShiftFunction,
                createIntegerLiteralFragmentTemplate(ammount),
                createEnumLiteralExpressionFragmentTemplate(DateTimeUnit.class,
                        unit));
        DateDefinitionTemplate template = new DateDefinitionTemplate(
                dateShiftFunctionTemplate);
        template.setVariableName(variableName);
        return template;
    }

    public static ContiguousDaysPaddingTemplate createContiguousDaysPaddingTemplate(
            String variableName,
            ActualValueExpression sourceContiguousDaysExpressable,
            int startPushAmount, DateTimeUnit startPushUnit, int endPushAmount,
            DateTimeUnit endPushUnit) {
        ContiguousDaysPaddingTemplate template = new ContiguousDaysPaddingTemplate(
                sourceContiguousDaysExpressable,
                createIntegerLiteralFragmentTemplate(startPushAmount),
                createEnumLiteralExpressionFragmentTemplate(DateTimeUnit.class,
                        startPushUnit),
                createIntegerLiteralFragmentTemplate(endPushAmount),
                createEnumLiteralExpressionFragmentTemplate(DateTimeUnit.class,
                        endPushUnit));
        template.setVariableName(variableName);
        return template;
    }

    public static ActiveMeasureDenominatorCreationTemplate createActiveMeasureDenominatorCreationTemplate(
            long measureId,
            NamedVariableLiteralFragmentTemplate alternateStartDate,
            NamedVariableLiteralFragmentTemplate alternateEndDate) {
        ActiveMeasureDenominatorCreationTemplate template = new ActiveMeasureDenominatorCreationTemplate(
                measureId);
        if (alternateEndDate != null) {
            template.setAlternateEndDate(alternateEndDate);
        }
        if (alternateStartDate != null) {
            template.setAlternateStartDate(alternateStartDate);
        }
        return template;
    }

    public static FactLevelMeasureDenominatorCreationTemplate createFactLevelMeasureDenominatorCreationTemplate(
            long measureId,
            NamedVariableLiteralFragmentTemplate variableExpressableTemplate) {
        FactLevelMeasureDenominatorCreationTemplate template = new FactLevelMeasureDenominatorCreationTemplate();
        template.setMeasureId(measureId);
        template.setPrimaryFactTemplate(variableExpressableTemplate);
        return template;
    }
}

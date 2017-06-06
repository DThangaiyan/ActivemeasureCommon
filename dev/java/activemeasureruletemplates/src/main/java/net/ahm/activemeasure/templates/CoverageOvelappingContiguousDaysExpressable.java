package net.ahm.activemeasure.templates;

import java.util.Collection;
import java.util.Set;

import net.ahm.activemeasure.templates.util.CoverageType;
import net.ahm.careengine.domain.member.MemberInfo;
import net.ahm.careengine.domain.temporal.ContiguousDays;
import net.ahm.careengine.domain.temporal.Temporal;
import net.ahm.careengine.util.GlobalFunctions;
import net.ahm.rulesapp.templates.implementations.AbstractExecutableTemplate;
import net.ahm.rulesapp.templates.interfaces.ActualValueExpression;
import net.ahm.rulesapp.templates.interfaces.Condition;
import net.ahm.rulesapp.templates.interfaces.ExecutableTemplate;
import net.ahm.rulesapp.templates.interfaces.Template;
import net.ahm.rulesapp.templates.interfaces.TypedExpression;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.DroolsExpressionContext;
import net.ahm.rulesapp.util.ExecutableExpressionsIF;
import net.ahm.rulesapp.util.Operator;
import net.ahm.rulesapp.util.TemplateType;
import net.ahm.rulesapp.util.UtilFunctions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Template(id = CoverageOvelappingContiguousDaysExpressable.serialVersionUID, description = "Check coverage gaps overlapping a ContiguousDaysExpressable", type = TemplateType.CONDITION)
public class CoverageOvelappingContiguousDaysExpressable extends
        AbstractExecutableTemplate implements Condition {
    static final long                   serialVersionUID = 75556375769509338L;

    private final CoverageType          coverageType;
    private final ActualValueExpression intersectingContiguousDaysExpressable;
    private final Operator              expectedGapCountOperator;
    private final Operator              expectedGapSizeOperator;
    private final int                   expectedGapCount;
    private final int                   expectedGapSize;

    @JsonCreator
    public CoverageOvelappingContiguousDaysExpressable(
            @JsonProperty("coverageType") CoverageType coverageType,
            @JsonProperty("expectedGapCountOperator") Operator expectedGapCountOperator,
            @JsonProperty("expectedGapCount") int expectedGapCount,
            @JsonProperty("expectedGapSizeOperator") Operator expectedGapSizeOperator,
            @JsonProperty("expectedGapSize") int expectedGapSize,
            @JsonProperty("intersectingContiguousDaysExpressable") ActualValueExpression intersectingContiguousDaysExpressable) {
        super();
        this.coverageType = coverageType;
        this.intersectingContiguousDaysExpressable = intersectingContiguousDaysExpressable;
        this.expectedGapCountOperator = expectedGapCountOperator;
        this.expectedGapSizeOperator = expectedGapSizeOperator;
        this.expectedGapCount = expectedGapCount;
        this.expectedGapSize = expectedGapSize;
    }

    public CoverageType getCoverageType() {
        return coverageType;
    }

    public ActualValueExpression getIntersectingContiguousDaysExpressable() {
        return intersectingContiguousDaysExpressable;
    }

    public Operator getExpectedGapCountOperator() {
        return expectedGapCountOperator;
    }

    public Operator getExpectedGapSizeOperator() {
        return expectedGapSizeOperator;
    }

    public int getExpectedGapCount() {
        return expectedGapCount;
    }

    public int getExpectedGapSize() {
        return expectedGapSize;
    }

    @Override
    public String toString() {
        return "CoverageOvelappingContiguousDaysExpressable [coverageType="
                + coverageType + ", intersectingContiguousDaysExpressable="
                + intersectingContiguousDaysExpressable
                + ", expectedGapCountOperator=" + expectedGapCountOperator
                + ", expectedGapSizeOperator=" + expectedGapSizeOperator
                + ", expectedGapCount=" + expectedGapCount
                + ", expectedGapSize=" + expectedGapSize + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((coverageType == null) ? 0 : coverageType.hashCode());
        result = prime * result + expectedGapCount;
        result = prime
                * result
                + ((expectedGapCountOperator == null) ? 0
                        : expectedGapCountOperator.hashCode());
        result = prime * result + expectedGapSize;
        result = prime
                * result
                + ((expectedGapSizeOperator == null) ? 0
                        : expectedGapSizeOperator.hashCode());
        result = prime
                * result
                + ((intersectingContiguousDaysExpressable == null) ? 0
                        : intersectingContiguousDaysExpressable.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CoverageOvelappingContiguousDaysExpressable other = (CoverageOvelappingContiguousDaysExpressable) obj;
        if (coverageType != other.coverageType)
            return false;
        if (expectedGapCount != other.expectedGapCount)
            return false;
        if (expectedGapCountOperator != other.expectedGapCountOperator)
            return false;
        if (expectedGapSize != other.expectedGapSize)
            return false;
        if (expectedGapSizeOperator != other.expectedGapSizeOperator)
            return false;
        if (intersectingContiguousDaysExpressable == null) {
            if (other.intersectingContiguousDaysExpressable != null)
                return false;
        } else if (!intersectingContiguousDaysExpressable
                .equals(other.intersectingContiguousDaysExpressable))
            return false;
        return true;
    }

    @Override
    public ExecutableExpressionsIF getExecutableExpressions() {
        return new ExecutableExpressionsImpl();
    }

    @Override
    public Set<Class<?>> getUsedClasses() {
        ExecutableTemplate[] innerTemplates = getInnerTemplates(1);
        Set<Class<?>> resultSet = innerTemplates[0].getUsedClasses();
        for (int i = 1; i < innerTemplates.length; i++) {
            resultSet.addAll(innerTemplates[i].getUsedClasses());
        }
        return resultSet;
    }

    private ExecutableTemplate[] getInnerTemplates(int number) {
        ExecutableTemplate[] results = new ExecutableTemplate[2];

        AnyAttributeSingleComparisonFragmentTemplate<?> attributeTemplate = coverageType
                .getAnyAttributeSingleComparisonFragmentTemplateForGap(number);

        NamedVariableLiteralFragmentTemplate gapVar = attributeTemplate
                .getVariableExpression();

        results[0] = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MemberInfo.class, null, attributeTemplate);

        AnyClassMultipleAttributeEvaluationFragmentTemplate collectionTemaplate = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        Collection.class,
                        null,
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                "size",
                                                TypeDescription
                                                        .getTypeDescription(Integer.TYPE)),
                                        UtilFunctions
                                                .createIntegerLiteralFragmentTemplate(expectedGapCount),
                                        expectedGapCountOperator, null));

        FromEvaluationFragmentTemplate fromTemplate = new FromEvaluationFragmentTemplate(
                UtilFunctions
                        .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                                ContiguousDays.class,
                                null,
                                UtilFunctions
                                        .createAnyAttributeSingleComparisonFragmentTemplate(
                                                new AttributeFragmentTemplate(
                                                        "durationInDays",
                                                        TypeDescription
                                                                .getTypeDescription(Long.TYPE)),
                                                UtilFunctions
                                                        .createIntegerLiteralFragmentTemplate(expectedGapSize),
                                                expectedGapSizeOperator, null)),
                new AllIntersectingButNotAdjacentTemplate(gapVar,
                        intersectingContiguousDaysExpressable));

        results[1] = UtilFunctions
                .createCollectionEvaluationFragmentTemplate(
                collectionTemaplate, fromTemplate);

        return results;
    }

    private class ExecutableExpressionsImpl implements ExecutableExpressionsIF {
        @Override
        public String getDroolsExpression(DroolsExpressionContext ctx) {
            ExecutableTemplate[] innerTemplates = getInnerTemplates(ctx
                    .getNextIdnumber());
            StringBuilder sb = new StringBuilder();
            boolean firstTemplate = true;
            for (ExecutableTemplate template : innerTemplates) {
                if (firstTemplate) {
                    firstTemplate = false;
                } else {
                    sb.append(NEW_LINE_CHAR)
                            .append(ctx.getStartOfLinePadding());
                }
                sb.append(template.getExecutableExpressions()
                        .getDroolsExpression(ctx));
            }
            return sb.toString();
        }

    }

    private static class AllIntersectingButNotAdjacentTemplate extends
            AbstractExecutableTemplate implements ActualValueExpression {
        /** this should not really be serialized */
        private static final long     serialVersionUID = 1L;
        private final TypedExpression intialCollection;
        private final TypedExpression contiguousDaysExpressable;

        protected AllIntersectingButNotAdjacentTemplate(
                TypedExpression intialCollection,
                TypedExpression contiguousDaysExpressable) {
            super();
            this.intialCollection = intialCollection;
            this.contiguousDaysExpressable = contiguousDaysExpressable;
        }

        @Override
        public TypeDescription getExpressionReturnTypeDescription() {
            return TypeDescription.getTypeDescription(Collection.class,
                    Temporal.class);
        }

        @Override
        public ExecutableExpressionsIF getExecutableExpressions() {
            return new ExecutableExpressionsImpl2();
        }

        @Override
        public Set<Class<?>> getUsedClasses() {
            Set<Class<?>> results = intialCollection.getUsedClasses();
            results.addAll(contiguousDaysExpressable.getUsedClasses());
            results.addAll(getExpressionReturnTypeDescription()
                    .getUsedClasses());
            results.add(GlobalFunctions.class);
            return results;
        }

        private class ExecutableExpressionsImpl2 implements
                ExecutableExpressionsIF {
            @Override
            public String getDroolsExpression(DroolsExpressionContext ctx) {
                StringBuilder sb = new StringBuilder(
                        "GlobalFunctions.allIntersectingButNotAdjacent( ");
                sb.append(
                        intialCollection.getExecutableExpressions()
                                .getDroolsExpression(ctx)).append(COMMA_CHAR)
                        .append(SPACE_CHAR);
                sb.append(
                        contiguousDaysExpressable.getExecutableExpressions()
                                .getDroolsExpression(ctx)).append(SPACE_CHAR)
                        .append(CLOSE_PARENTHESES_CHAR);
                return sb.toString();
            }
        }
    }
}

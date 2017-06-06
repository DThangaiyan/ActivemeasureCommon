package net.ahm.activemeasure.templates;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ahm.careengine.domain.member.MemberInfo;
import net.ahm.careengine.domain.temporal.ContiguousDays;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.implementations.AbstractExecutableTemplate;
import net.ahm.rulesapp.templates.interfaces.Condition;
import net.ahm.rulesapp.templates.interfaces.Template;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.CollectionEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.IntegerLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NotExistenceExpression;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.DroolsExpressionContext;
import net.ahm.rulesapp.util.ExecutableExpressionsIF;
import net.ahm.rulesapp.util.Operator;
import net.ahm.rulesapp.util.TemplateType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This template provides the structure to allow the user to check the member
 * enrollment gap logic (within the specified date range).
 * 
 * <p>
 * There is [at least] [2] gaps of [at least] [1] [days] in a member's
 * enrollment data
 * 
 * <p>
 * 
 * @author xsu
 * 
 */
@Template(id = MemberEnrollmentGapTemplate.serialVersionUID, description = "Member Enrollment Gaps Check Template", type = TemplateType.FRAGMENT)
public class MemberEnrollmentGapTemplate extends AbstractExecutableTemplate
        implements Condition {
    static final long                                                 serialVersionUID                       = -6260306731722175708L;

    private static final String                                       enrollmentGapDaysAttributeVariableName = "$initialGapDays";

    private final int                                                 gapCount;
    private final Operator                                            gapCountCheckOperator;
    private final int                                                 gapDurationInDays;
    private final Operator                                            gapDurationCheckOperator;
    private final NamedVariableLiteralFragmentTemplate earlierDateVariable;
    private final NamedVariableLiteralFragmentTemplate laterDateVariable;

	public int getGapCount() {
		return gapCount;
	}

	public Operator getGapCountCheckOperator() {
		return gapCountCheckOperator;
	}

	public int getGapDurationInDays() {
		return gapDurationInDays;
	}

    @JsonCreator
    public MemberEnrollmentGapTemplate(
            @JsonProperty("earlierDateVariable") NamedVariableLiteralFragmentTemplate earlierDateVariable,
            @JsonProperty("laterDateVariable") NamedVariableLiteralFragmentTemplate laterDateVariable,
            @JsonProperty("gapCount") int gapCount,
            @JsonProperty("gapCountCheckOperator") Operator gapCountCheckOperator,
            @JsonProperty("gapDurationInDays") int gapDurationInDays,
            @JsonProperty("gapDurationCheckOperator") Operator gapDurationCheckOperator) {
        this.earlierDateVariable = earlierDateVariable;
        this.laterDateVariable = laterDateVariable;
        this.gapCount = gapCount;
        this.gapCountCheckOperator = gapCountCheckOperator;
        this.gapDurationInDays = gapDurationInDays;
        this.gapDurationCheckOperator = gapDurationCheckOperator;
    }

	@Override
	public ExecutableExpressionsIF getExecutableExpressions() {
		return new InnerExecutableExpressions();
	}

	@Override
	public Set<Class<?>> getUsedClasses() {
        final CollectionEvaluationFragmentTemplate contiguousDayCollection = new CollectionEvaluationFragmentTemplate();
        final AnyClassMultipleAttributeEvaluationFragmentTemplate memberInstance = new AnyClassMultipleAttributeEvaluationFragmentTemplate();
        setupData(contiguousDayCollection, memberInstance,
                enrollmentGapDaysAttributeVariableName);

		Set<Class<?>> result = new HashSet<Class<?>>();
		result.addAll(memberInstance.getUsedClasses());
		result.addAll(contiguousDayCollection.getUsedClasses());
		return result;
	}

	public Operator getGapDurationCheckOperator() {
		return gapDurationCheckOperator;
	}

    @Override
    public String toString() {
        return "MemberEnrollmentGapTemplate [gapCount=" + gapCount
                + ", gapCountCheckOperator=" + gapCountCheckOperator
                + ", gapDurationInDays=" + gapDurationInDays
                + ", gapDurationCheckOperator=" + gapDurationCheckOperator
                + ", erlierDateVariable=" + getEarlierDateVariable()
                + ", laterDateVariable=" + getLaterDateVariable() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((earlierDateVariable == null) ? 0 : earlierDateVariable
                        .hashCode());
        result = prime * result + gapCount;
        result = prime
                * result
                + ((gapCountCheckOperator == null) ? 0 : gapCountCheckOperator
                        .hashCode());
        result = prime
                * result
                + ((gapDurationCheckOperator == null) ? 0
                        : gapDurationCheckOperator.hashCode());
        result = prime * result + gapDurationInDays;
        result = prime
                * result
                + ((laterDateVariable == null) ? 0 : laterDateVariable
                        .hashCode());
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
        MemberEnrollmentGapTemplate other = (MemberEnrollmentGapTemplate) obj;
        if (earlierDateVariable == null) {
            if (other.earlierDateVariable != null)
                return false;
        } else if (!earlierDateVariable.equals(other.earlierDateVariable))
            return false;
        if (gapCount != other.gapCount)
            return false;
        if (gapCountCheckOperator != other.gapCountCheckOperator)
            return false;
        if (gapDurationCheckOperator != other.gapDurationCheckOperator)
            return false;
        if (gapDurationInDays != other.gapDurationInDays)
            return false;
        if (laterDateVariable == null) {
            if (other.laterDateVariable != null)
                return false;
        } else if (!laterDateVariable.equals(other.laterDateVariable))
            return false;
        return true;
    }


    private class InnerExecutableExpressions implements ExecutableExpressionsIF {
		@Override
        public String getDroolsExpression(DroolsExpressionContext ctx) {
            final CollectionEvaluationFragmentTemplate contiguousDayCollection = new CollectionEvaluationFragmentTemplate();
            final AnyClassMultipleAttributeEvaluationFragmentTemplate memberInstance = new AnyClassMultipleAttributeEvaluationFragmentTemplate();
            setupData(
                    contiguousDayCollection,
                    memberInstance,
                    enrollmentGapDaysAttributeVariableName
                            + ctx.getNextIdnumber());

			// Drools expression is the fragment which itself does not work.
            StringBuilder droolsExpression = new StringBuilder();
            droolsExpression.append(memberInstance.getExecutableExpressions()
                            .getDroolsExpression(ctx)).append(NEW_LINE_CHAR);
            droolsExpression.append(ctx.getStartOfLinePadding())
                    .append(contiguousDayCollection.getExecutableExpressions()
                            .getDroolsExpression(ctx));
            return droolsExpression.toString();
		}
	}

    private final void setupData(
            final CollectionEvaluationFragmentTemplate contiguousDayCollection,
            final AnyClassMultipleAttributeEvaluationFragmentTemplate memberInstance,
            String varName) {
        // memberInstance setup
        memberInstance.setExpressionReturnTypeDescription(TypeDescription
                .getTypeDescription(MemberInfo.class));
        AnyAttributeSingleComparisonFragmentTemplate<Object> enrollmentGapDayCheck = new AnyAttributeSingleComparisonFragmentTemplate<Object>();
        enrollmentGapDayCheck.setVariableName(varName);
        enrollmentGapDayCheck.setAttribute(new AttributeFragmentTemplate(
                "enrolmentGapDays", TypeDescription.getTypeDescription(
                        Collection.class, ContiguousDays.class)));
        enrollmentGapDayCheck.setOperator(CommonOperators.NOT_EQUAL_TO);
        NotExistenceExpression nullExpression = new NotExistenceExpression();
        enrollmentGapDayCheck.setAttributeValue(nullExpression);
        memberInstance.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments().add(enrollmentGapDayCheck);

        AnyClassMultipleAttributeEvaluationFragmentTemplate collectionInstance = new AnyClassMultipleAttributeEvaluationFragmentTemplate();
        collectionInstance.setExpressionReturnTypeDescription(TypeDescription
                .getTypeDescription(Collection.class, ContiguousDays.class));

        AnyAttributeSingleComparisonFragmentTemplate<Integer> sizeComparison = new AnyAttributeSingleComparisonFragmentTemplate<Integer>();
        sizeComparison.setAttribute(new AttributeFragmentTemplate("size",
                TypeDescription.getTypeDescription(Integer.TYPE)));
        sizeComparison.setOperator(gapCountCheckOperator);

        IntegerLiteralFragmentTemplate sizeLiteral = new IntegerLiteralFragmentTemplate();
        sizeLiteral.setValue(gapCount);
        sizeComparison.setAttributeValue(sizeLiteral);
        collectionInstance.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments().add(sizeComparison);

        contiguousDayCollection.setCollectionFragment(collectionInstance);

        AnyClassMultipleAttributeEvaluationFragmentTemplate continuousDaysInstance = new AnyClassMultipleAttributeEvaluationFragmentTemplate();
        continuousDaysInstance
                .setExpressionReturnTypeDescription(TypeDescription
                        .getTypeDescription(ContiguousDays.class));
        AnyAttributeSingleComparisonFragmentTemplate<Integer> contiguousDaysDurationIndays = new AnyAttributeSingleComparisonFragmentTemplate<Integer>();
        contiguousDaysDurationIndays
                .setAttribute(new AttributeFragmentTemplate("durationInDays",
                        TypeDescription.getTypeDescription(Integer.TYPE)));
        IntegerLiteralFragmentTemplate integerLiteral = new IntegerLiteralFragmentTemplate();
        integerLiteral.setValue(gapDurationInDays);
        contiguousDaysDurationIndays.setAttributeValue(integerLiteral);
        contiguousDaysDurationIndays.setOperator(gapDurationCheckOperator);
        continuousDaysInstance.getMultipleAttributeEvaluationFragments()
                .getAttributeEvaluationFragments()
                .add(contiguousDaysDurationIndays);

        FromEvaluationFragmentTemplate fromTemplate = new FromEvaluationFragmentTemplate(
                continuousDaysInstance,
                new FilterWholeDaysFromCollectionTemplate(varName,
                        getEarlierDateVariable().getVariableName(),
                        getLaterDateVariable().getVariableName()));

        contiguousDayCollection.setContentsFragment(fromTemplate);
    }

    public NamedVariableLiteralFragmentTemplate getEarlierDateVariable() {
        return earlierDateVariable;
    }

    public NamedVariableLiteralFragmentTemplate getLaterDateVariable() {
        return laterDateVariable;
    }
}

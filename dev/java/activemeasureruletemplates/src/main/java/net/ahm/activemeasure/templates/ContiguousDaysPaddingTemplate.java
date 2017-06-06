package net.ahm.activemeasure.templates;

import java.util.HashSet;
import java.util.Set;

import net.ahm.careengine.domain.temporal.ContiguousDays;
import net.ahm.careengine.ruleengine.DateTimeUnit;
import net.ahm.careengine.util.GlobalFunctions;
import net.ahm.rulesapp.templates.implementations.AbstractExecutableTemplate;
import net.ahm.rulesapp.templates.interfaces.ActualValueExpression;
import net.ahm.rulesapp.templates.interfaces.Condition;
import net.ahm.rulesapp.templates.interfaces.ExecutableTemplate;
import net.ahm.rulesapp.templates.interfaces.Template;
import net.ahm.rulesapp.templates.interfaces.VariableExpressable;
import net.ahm.rulesapp.templates.libraries.EnumLiteralExpressionFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.DroolsExpressionContext;
import net.ahm.rulesapp.util.ExecutableExpressionsIF;
import net.ahm.rulesapp.util.TemplateType;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Template(id = ContiguousDaysPaddingTemplate.serialVersionUID, description = "Get a new ContiguousDays by padding a ContiguousDaysExpressable", type = TemplateType.CONDITION)
public class ContiguousDaysPaddingTemplate extends AbstractExecutableTemplate
        implements VariableExpressable, Condition {
    static final long                                                 serialVersionUID = 9133044625282325261L;

    private String                                                    variableName;

    private final ActualValueExpression                               sourceContiguousDaysExpressable;
    private final ActualValueExpression                               startPushAmount;
    private final EnumLiteralExpressionFragmentTemplate<DateTimeUnit> startPushUnit;
    private final ActualValueExpression                               endPushAmount;
    private final EnumLiteralExpressionFragmentTemplate<DateTimeUnit> endPushUnit;

    @JsonCreator
    public ContiguousDaysPaddingTemplate(
            @JsonProperty("sourceContiguousDaysExpressable") ActualValueExpression sourceContiguousDaysExpressable,
            @JsonProperty("startPushAmount") ActualValueExpression startPushAmount,
            @JsonProperty("startPushUnit") EnumLiteralExpressionFragmentTemplate<DateTimeUnit> startPushUnit,
            @JsonProperty("endPushAmount") ActualValueExpression endPushAmount,
            @JsonProperty("endPushUnit") EnumLiteralExpressionFragmentTemplate<DateTimeUnit> endPushUnit) {
        this.sourceContiguousDaysExpressable = sourceContiguousDaysExpressable;
        this.startPushAmount = startPushAmount;
        this.startPushUnit = startPushUnit;
        this.endPushAmount = endPushAmount;
        this.endPushUnit = endPushUnit;
    }

    @Override
    public TypeDescription getExpressionReturnTypeDescription() {
        return TypeDescription.getTypeDescription(ContiguousDays.class);
    }

    @Override
    public ExecutableExpressionsIF getExecutableExpressions() {
        return new ExecutableExpressionsImpl();
    }

    @Override
    public Set<Class<?>> getUsedClasses() {
        Set<Class<?>> results = new HashSet<Class<?>>();
        results.add(ContiguousDays.class);
        results.add(GlobalFunctions.class);
        results.addAll(sourceContiguousDaysExpressable.getUsedClasses());
        results.addAll(startPushAmount.getUsedClasses());
        results.addAll(startPushUnit.getUsedClasses());
        results.addAll(endPushAmount.getUsedClasses());
        results.addAll(endPushUnit.getUsedClasses());
        return results;
    }

    @Override
    public String getVariableName() {
        return variableName;
    }

    @Override
    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public boolean hasVariableName() {
        return StringUtils.isNotBlank(getVariableName());
    }

    @Override
    public NamedVariableLiteralFragmentTemplate getVariableExpression() {
        if (hasVariableName()) {
            return new NamedVariableLiteralFragmentTemplate(getVariableName(),
                    getExpressionReturnTypeDescription());
        }
        return null;
    }

    @Override
    public String toString() {
        return "ContiguousDaysPaddingTemplate [variableName=" + variableName
                + ", sourceContiguousDaysExpressable="
                + sourceContiguousDaysExpressable + ", startPushAmount="
                + startPushAmount + ", startPushUnit=" + startPushUnit
                + ", endPushAmount=" + endPushAmount + ", endPushUnit="
                + endPushUnit + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((endPushAmount == null) ? 0 : endPushAmount.hashCode());
        result = prime * result
                + ((endPushUnit == null) ? 0 : endPushUnit.hashCode());
        result = prime
                * result
                + ((sourceContiguousDaysExpressable == null) ? 0
                        : sourceContiguousDaysExpressable.hashCode());
        result = prime * result
                + ((startPushAmount == null) ? 0 : startPushAmount.hashCode());
        result = prime * result
                + ((startPushUnit == null) ? 0 : startPushUnit.hashCode());
        result = prime * result
                + ((variableName == null) ? 0 : variableName.hashCode());
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
        ContiguousDaysPaddingTemplate other = (ContiguousDaysPaddingTemplate) obj;
        if (endPushAmount == null) {
            if (other.endPushAmount != null)
                return false;
        } else if (!endPushAmount.equals(other.endPushAmount))
            return false;
        if (endPushUnit == null) {
            if (other.endPushUnit != null)
                return false;
        } else if (!endPushUnit.equals(other.endPushUnit))
            return false;
        if (sourceContiguousDaysExpressable == null) {
            if (other.sourceContiguousDaysExpressable != null)
                return false;
        } else if (!sourceContiguousDaysExpressable
                .equals(other.sourceContiguousDaysExpressable))
            return false;
        if (startPushAmount == null) {
            if (other.startPushAmount != null)
                return false;
        } else if (!startPushAmount.equals(other.startPushAmount))
            return false;
        if (startPushUnit == null) {
            if (other.startPushUnit != null)
                return false;
        } else if (!startPushUnit.equals(other.startPushUnit))
            return false;
        if (variableName == null) {
            if (other.variableName != null)
                return false;
        } else if (!variableName.equals(other.variableName))
            return false;
        return true;
    }

    public ActualValueExpression getSourceContiguousDaysExpressable() {
        return sourceContiguousDaysExpressable;
    }

    public ActualValueExpression getStartPushAmount() {
        return startPushAmount;
    }

    public EnumLiteralExpressionFragmentTemplate<DateTimeUnit> getStartPushUnit() {
        return startPushUnit;
    }

    public ActualValueExpression getEndPushAmount() {
        return endPushAmount;
    }

    public EnumLiteralExpressionFragmentTemplate<DateTimeUnit> getEndPushUnit() {
        return endPushUnit;
    }

    private class ExecutableExpressionsImpl implements ExecutableExpressionsIF {
        @Override
        public String getDroolsExpression(DroolsExpressionContext ctx) {
            StringBuilder sb = new StringBuilder(variableName);
            sb.append(" : ContiguousDays() from GlobalFunctions.getContiguousDaysWithNewDates( ");

            appendField(sb, sourceContiguousDaysExpressable, ctx);
            appendField(sb, startPushAmount, ctx);
            appendField(sb, startPushUnit, ctx);
            appendField(sb, endPushAmount, ctx);
            sb.append(endPushUnit.getExecutableExpressions()
                    .getDroolsExpression(ctx));
            sb.append(SPACE_CHAR).append(CLOSE_PARENTHESES_CHAR);
            return sb.toString();
        }

        private void appendField(StringBuilder sb,
                ExecutableTemplate expression, DroolsExpressionContext ctx) {
            sb.append(
                    expression.getExecutableExpressions().getDroolsExpression(
                            ctx))
                    .append(COMMA_CHAR).append(SPACE_CHAR);
        }
    }
}

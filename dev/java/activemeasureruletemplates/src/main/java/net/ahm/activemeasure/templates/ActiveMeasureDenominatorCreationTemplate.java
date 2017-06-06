/**
 * 
 */
package net.ahm.activemeasure.templates;

import java.util.HashSet;
import java.util.Set;

import net.ahm.careengine.domain.measures.active.ActiveMeasure;
import net.ahm.careengine.domain.measures.active.ActiveMeasureBuilder;
import net.ahm.rulesapp.templates.implementations.AbstractExecutableTemplate;
import net.ahm.rulesapp.templates.interfaces.Template;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.util.DroolsExpressionContext;
import net.ahm.rulesapp.util.ExecutableExpressionsIF;
import net.ahm.rulesapp.util.TemplateType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This rule template is responsible to defining the actions that are
 * responsible to creating {@link ActiveMeasure}.
 * 
 * The fragment basically takes an active measure Id and two optional dates.
 * 
 * @author DHerrington
 * 
 */
@Template(id = ActiveMeasureDenominatorCreationTemplate.serialVersionUID, description = "Create new Active Measure Denominator", type = TemplateType.ACTION)
public class ActiveMeasureDenominatorCreationTemplate extends
        AbstractExecutableTemplate {
    static final long                            serialVersionUID = -6128367146174117216L;

    private final long                           measureId;
    private NamedVariableLiteralFragmentTemplate alternateStartDate;
    private NamedVariableLiteralFragmentTemplate alternateEndDate;

    @JsonCreator
    public ActiveMeasureDenominatorCreationTemplate(
            @JsonProperty("measureId") long measureId) {
        this.measureId = measureId;
    }

    @Override
    public ExecutableExpressionsIF getExecutableExpressions() {
        return new ExecutableExpressionsImpl();
    }

    @Override
    public Set<Class<?>> getUsedClasses() {
		Set<Class<?>> results = new HashSet<Class<?>>();
        results.add(ActiveMeasureBuilder.class);
        if (hasAlternateEndDate()) {
            results.addAll(alternateEndDate.getUsedClasses());
        }
        if (hasAlternateStartDate()) {
            results.addAll(alternateStartDate.getUsedClasses());
        }
        return results;
    }

    @JsonIgnore
    private boolean hasAlternateStartDate() {
        return alternateStartDate != null;
    }

    @JsonIgnore
    private boolean hasAlternateEndDate() {
        return alternateEndDate != null;
    }

    @JsonIgnore
    private boolean hasBothAlternateDates() {
        return hasAlternateStartDate() && hasAlternateEndDate();
    }

    public NamedVariableLiteralFragmentTemplate getAlternateStartDate() {
        return alternateStartDate;
    }

    public void setAlternateStartDate(
            NamedVariableLiteralFragmentTemplate alternateStartDate) {
        this.alternateStartDate = alternateStartDate;
    }

    public NamedVariableLiteralFragmentTemplate getAlternateEndDate() {
        return alternateEndDate;
    }

    public void setAlternateEndDate(
            NamedVariableLiteralFragmentTemplate alternateEndDate) {
        this.alternateEndDate = alternateEndDate;
    }

    public long getMeasureId() {
        return measureId;
    }

    @Override
    public String toString() {
        return "ActiveMeasureDenominatorCreationTemplate [measureId="
                + measureId + ", alternateStartDate=" + alternateStartDate
                + ", alternateEndDate=" + alternateEndDate + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((alternateEndDate == null) ? 0 : alternateEndDate.hashCode());
        result = prime
                * result
                + ((alternateStartDate == null) ? 0 : alternateStartDate
                        .hashCode());
        result = prime * result + (int) (measureId ^ (measureId >>> 32));
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
        ActiveMeasureDenominatorCreationTemplate other = (ActiveMeasureDenominatorCreationTemplate) obj;
        if (alternateEndDate == null) {
            if (other.alternateEndDate != null)
                return false;
        } else if (!alternateEndDate.equals(other.alternateEndDate))
            return false;
        if (alternateStartDate == null) {
            if (other.alternateStartDate != null)
                return false;
        } else if (!alternateStartDate.equals(other.alternateStartDate))
            return false;
        if (measureId != other.measureId)
            return false;
        return true;
    }

    private class ExecutableExpressionsImpl implements ExecutableExpressionsIF {
        @Override
        public String getDroolsExpression(DroolsExpressionContext ctx) {
            String varName = "mam" + ctx.getNextIdnumber();
            StringBuilder sb = new StringBuilder("ActiveMeasure ").append(
                    varName).append(" = activeMeasureBuilder.newActiveMeasure");
            if (hasBothAlternateDates()) {
                sb.append("WithAlternateStartAndEndDate");
            } else if (hasAlternateStartDate()) {
                sb.append("WithAlternateStartDate");
            } else if (hasAlternateEndDate()) {
                sb.append("WithAlternateEndDate");
            }
            sb.append(OPEN_PARENTHESES_CHAR).append(SPACE_CHAR)
                    .append(measureId);

            if (hasAlternateStartDate()) {
                sb.append(SPACE_CHAR)
                        .append(COMMA_CHAR)
                        .append(SPACE_CHAR)
                        .append(alternateStartDate.getExecutableExpressions()
                                .getDroolsExpression(ctx));
            }
            if (hasAlternateEndDate()) {
                sb.append(SPACE_CHAR)
                        .append(COMMA_CHAR)
                        .append(SPACE_CHAR)
                        .append(alternateEndDate.getExecutableExpressions()
                                .getDroolsExpression(ctx));
            }

            sb.append(" );").append(NEW_LINE_CHAR)
                    .append(ctx.getStartOfLinePadding()).append("insert(")
                    .append(varName).append(")");
            return sb.toString();
        }
    }
}

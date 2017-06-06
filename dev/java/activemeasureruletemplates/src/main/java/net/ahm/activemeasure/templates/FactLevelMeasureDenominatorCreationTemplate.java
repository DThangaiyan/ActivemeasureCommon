package net.ahm.activemeasure.templates;

import java.util.HashSet;
import java.util.Set;

import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureBuilder;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.rulesapp.templates.implementations.AbstractExecutableTemplate;
import net.ahm.rulesapp.templates.interfaces.Action;
import net.ahm.rulesapp.templates.interfaces.Template;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.util.DroolsExpressionContext;
import net.ahm.rulesapp.util.ExecutableExpressionsIF;
import net.ahm.rulesapp.util.TemplateType;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This rule template is responsible to defining the actions that are
 * responsible to creating {@link FactLevelMeasureDenominator}.
 * 
 * The fragment basically takes an active measure ID and {@link Fact} and create
 * a {@link FactLevelMeasureDenominator}
 * 
 * @author xsu
 * 
 */
@Template(id = FactLevelMeasureDenominatorCreationTemplate.serialVersionUID, description = "Create new Fact Level Denominator", type = TemplateType.ACTION)
public class FactLevelMeasureDenominatorCreationTemplate extends
        AbstractExecutableTemplate implements Action {
    protected static final long                  serialVersionUID = 820656884959752657L;

    private static final Logger                  logger           = Logger.getLogger(FactLevelMeasureDenominatorCreationTemplate.class);

    private long                                 measureId;
    private NamedVariableLiteralFragmentTemplate primaryFactTemplate;

    @JsonIgnore
    public String getFactLevelDenominatorVariableName() {
        return ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR;
    }

    public long getMeasureId() {
        return measureId;
    }

    public void setMeasureId(long measureId) {
        this.measureId = measureId;
    }

    public void setPrimaryFactTemplate(
            NamedVariableLiteralFragmentTemplate variableExpressableTemplate) {
        this.primaryFactTemplate = variableExpressableTemplate;
    }

    public NamedVariableLiteralFragmentTemplate getPrimaryFactTemplate() {
        return primaryFactTemplate;
    }

    @Override
    public ExecutableExpressionsIF getExecutableExpressions() {
        return new InnerExecutableExpressions();
    }

    @Override
    public Set<Class<?>> getUsedClasses() {
        Set<Class<?>> result = new HashSet<Class<?>>();
        result.add(FactLevelMeasureDenominator.class);
        result.add(FactLevelMeasureBuilder.class);
        if (hasPrimaryFact()) {
            result.addAll(primaryFactTemplate.getUsedClasses());
        }
        return result;
    }

    private boolean hasPrimaryFact() {
        return primaryFactTemplate != null;
    }

    private class InnerExecutableExpressions implements ExecutableExpressionsIF {
        @Override
        public String getDroolsExpression(DroolsExpressionContext ctx) {
            // Drools expression is the fragment which itself does not work.
            String varName = getFactLevelDenominatorVariableName()
                    + ctx.getNextIdnumber();
            StringBuilder droolsExpression = new StringBuilder(
                    "FactLevelMeasureDenominator ").append(varName);
            if (hasPrimaryFact()) {
                droolsExpression
                        .append(" = factLevelMeasureBuilder.newFactLevelMeasureDenominatorWithPrimaryFact(")
                        .append(measureId + ", ")
                        .append(primaryFactTemplate.getVariableName());
            } else {
                droolsExpression
                        .append(" = factLevelMeasureBuilder.newFactLevelMeasureDenominator(")
                        .append(measureId);
            }
            droolsExpression.append(");").append(NEW_LINE_CHAR)
                    .append(ctx.getStartOfLinePadding()).append("insert(")
                    .append(varName).append(");");

            String result = droolsExpression.toString();
            if (logger.isTraceEnabled()) {
                logger.trace("Retriveing drools expression:\n" + result);
            }

            return droolsExpression.toString();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) measureId;

        result = prime
                * result
                + ((primaryFactTemplate == null) ? 0 : primaryFactTemplate
                        .hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FactLevelMeasureDenominatorCreationTemplate)) {
            return false;
        }
        FactLevelMeasureDenominatorCreationTemplate other = (FactLevelMeasureDenominatorCreationTemplate) obj;
        if (measureId != other.measureId) {
            return false;
        }
        if (primaryFactTemplate == null) {
            if (other.primaryFactTemplate != null) {
                return false;
            }
        } else if (!primaryFactTemplate.equals(other.primaryFactTemplate)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Creating denominator "
                + getFactLevelDenominatorVariableName() + " with measure id: "
                + measureId + "\n");
        if (hasPrimaryFact()) {
            result.append(primaryFactTemplate.toString());
        }
        return result.toString();
    }
}

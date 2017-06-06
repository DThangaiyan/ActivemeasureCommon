package net.ahm.activemeasure.templates;

import java.util.Set;

import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureNumerator;
import net.ahm.rulesapp.templates.implementations.AbstractExecutableTemplate;
import net.ahm.rulesapp.templates.interfaces.Action;
import net.ahm.rulesapp.templates.interfaces.Template;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.util.DroolsExpressionContext;
import net.ahm.rulesapp.util.ExecutableExpressionsIF;
import net.ahm.rulesapp.util.TemplateType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This template is responsible for creating a {@link FactLevelMeasureNumerator}
 * with the necessary attributes setups.
 * 
 * This template always create {@link FactLevelMeasureNumerator} for a specified
 * {@link FactLevelMeasureDenominator}.
 * 
 * @author xsu
 * 
 */
@Template(id = FactLevelMeasureNumeratorCreationTemplate.serialVersionUID, description = "Create Numberator for Denominator with...", type = TemplateType.ACTION)
public class FactLevelMeasureNumeratorCreationTemplate extends
        AbstractExecutableTemplate implements Action {
    protected static final long                        serialVersionUID = 820656884959752656L;

    private final NamedVariableLiteralFragmentTemplate denomTemplate;
    private final NamedVariableLiteralFragmentTemplate primaryFactTemplate;

    @JsonCreator
    public FactLevelMeasureNumeratorCreationTemplate(
            @JsonProperty("denomTemplate") NamedVariableLiteralFragmentTemplate denomTemplate,
            @JsonProperty("primaryFact") NamedVariableLiteralFragmentTemplate primaryFact) {
        this.denomTemplate = denomTemplate;
        this.primaryFactTemplate = primaryFact;
    }

	@Override
	public ExecutableExpressionsIF getExecutableExpressions() {
		return new InnerExecutableExpressions();
	}

    private boolean hasPrimaryFact() {
        return primaryFactTemplate != null;
    }

    @Override
    public Set<Class<?>> getUsedClasses() {
        Set<Class<?>> results = denomTemplate.getUsedClasses();
        results.add(FactLevelMeasureNumerator.class);
        if (hasPrimaryFact()) {
            results.addAll(primaryFactTemplate.getUsedClasses());
        }
        return results;
    }

    private class InnerExecutableExpressions implements ExecutableExpressionsIF {

        @Override
        public String getDroolsExpression(DroolsExpressionContext ctx) {
            StringBuilder droolsExpressionBuilder = new StringBuilder(
                    FactLevelMeasureNumerator.class.getSimpleName());

            final String numeratorVariableName = getNumeratorVariableName()
                    + ctx.getNextIdnumber();

            droolsExpressionBuilder.append(SPACE_CHAR)
                    .append(numeratorVariableName).append(" = ")
                    .append(denomTemplate.getExecutableExpressions()
                            .getDroolsExpression(ctx));

            if (!hasPrimaryFact()) {
                droolsExpressionBuilder
                        .append("createFactLevelMeasureNumerator(");
            } else {
                droolsExpressionBuilder
                        .append(".createFactLevelMeasureNumeratorWithPrimaryFact(");
                droolsExpressionBuilder.append(primaryFactTemplate
                        .getExecutableExpressions().getDroolsExpression(ctx));
            }

            droolsExpressionBuilder.append(CLOSE_PARENTHESES_CHAR)
                    .append(SEMICOLON_CHAR)
                    .append(NEW_LINE_CHAR);

            // insert numerator
            droolsExpressionBuilder.append(ctx.getStartOfLinePadding())
                    .append("insert(")
                    .append(numeratorVariableName).append(");");

            return droolsExpressionBuilder.toString();
        }
    }

    @JsonIgnore
    public String getNumeratorVariableName() {
        return ActiveMeasureUtilConstants.FACT_LEVEL_NUMERATOR;
    }

    public NamedVariableLiteralFragmentTemplate getDenomTemplate() {
        return denomTemplate;
    }

    public NamedVariableLiteralFragmentTemplate getPrimaryFactTemplate() {
        return primaryFactTemplate;
    }

    @Override
    public String toString() {
        return "FactLevelMeasureNumeratorCreationTemplate [denomTemplate="
                + denomTemplate + ", primaryFactTemplate="
                + primaryFactTemplate + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((denomTemplate == null) ? 0 : denomTemplate
                        .hashCode());
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        FactLevelMeasureNumeratorCreationTemplate other = (FactLevelMeasureNumeratorCreationTemplate) obj;
        if (denomTemplate == null) {
            if (other.denomTemplate != null) {
                return false;
            }
        } else if (!denomTemplate.equals(other.denomTemplate)) {
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
}

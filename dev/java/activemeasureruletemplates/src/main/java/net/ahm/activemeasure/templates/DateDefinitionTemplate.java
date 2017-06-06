package net.ahm.activemeasure.templates;

import java.util.Date;
import java.util.Set;

import net.ahm.rulesapp.templates.implementations.AbstractExecutableTemplate;
import net.ahm.rulesapp.templates.interfaces.Condition;
import net.ahm.rulesapp.templates.interfaces.Template;
import net.ahm.rulesapp.templates.interfaces.VariableExpressable;
import net.ahm.rulesapp.templates.libraries.DateShiftFunctionTemplate;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.DroolsExpressionContext;
import net.ahm.rulesapp.util.ExecutableExpressionsIF;
import net.ahm.rulesapp.util.TemplateType;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Template(id = DateDefinitionTemplate.serialVersionUID, description = "Defining a Date based on a shift from another Date", type = TemplateType.CONDITION)
public class DateDefinitionTemplate extends AbstractExecutableTemplate
        implements VariableExpressable, Condition {
    static final long                       serialVersionUID = 4002791257988588064L;

    private String                          variableName;
    private final DateShiftFunctionTemplate dateShiftFunctionTemplate;

    @JsonCreator
    public DateDefinitionTemplate(
            @JsonProperty("dateShiftFunctionTemplate") DateShiftFunctionTemplate dateShiftFunctionTemplate) {
        this.dateShiftFunctionTemplate = dateShiftFunctionTemplate;
    }

    @Override
    public TypeDescription getExpressionReturnTypeDescription() {
        return TypeDescription.getTypeDescription(Date.class);
    }

    @Override
    public ExecutableExpressionsIF getExecutableExpressions() {
        return new ExecutableExpressionsImpl();
    }

    @Override
    public Set<Class<?>> getUsedClasses() {
        Set<Class<?>> resultSet = dateShiftFunctionTemplate.getUsedClasses();
        resultSet.add(Date.class);
        return resultSet;
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

    public DateShiftFunctionTemplate getDateShiftFunctionTemplate() {
        return dateShiftFunctionTemplate;
    }

    @Override
    public String toString() {
        return "DateDefinitionTemplate [variableName=" + variableName
                + ", dateShiftFunctionTemplate=" + dateShiftFunctionTemplate
                + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((dateShiftFunctionTemplate == null) ? 0 : dateShiftFunctionTemplate
                        .hashCode());
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
        DateDefinitionTemplate other = (DateDefinitionTemplate) obj;
        if (dateShiftFunctionTemplate == null) {
            if (other.dateShiftFunctionTemplate != null)
                return false;
        } else if (!dateShiftFunctionTemplate.equals(other.dateShiftFunctionTemplate))
            return false;
        if (variableName == null) {
            if (other.variableName != null)
                return false;
        } else if (!variableName.equals(other.variableName))
            return false;
        return true;
    }

    private class ExecutableExpressionsImpl implements ExecutableExpressionsIF {

        @Override
        public String getDroolsExpression(DroolsExpressionContext ctx) {
            StringBuilder sb = new StringBuilder(variableName);
            sb.append(" : java.util.Date() from ");
            sb.append(dateShiftFunctionTemplate.getExecutableExpressions()
                    .getDroolsExpression(ctx));
            return sb.toString();
        }
    }
}

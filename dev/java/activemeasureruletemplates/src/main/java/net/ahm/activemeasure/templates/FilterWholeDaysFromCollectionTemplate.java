package net.ahm.activemeasure.templates;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import net.ahm.careengine.common.CECollectionsUtil;
import net.ahm.careengine.domain.temporal.ContiguousDays;
import net.ahm.careengine.domain.temporal.Temporal;
import net.ahm.careengine.util.GlobalFunctions;
import net.ahm.rulesapp.templates.implementations.AbstractExecutableTemplate;
import net.ahm.rulesapp.templates.interfaces.ActualValueExpression;
import net.ahm.rulesapp.templates.interfaces.Template;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.DroolsExpressionContext;
import net.ahm.rulesapp.util.ExecutableExpressionsIF;
import net.ahm.rulesapp.util.TemplateType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This template is <b>internal use<b> only for now.
 * 
 * It will be used to invoke a specific function call.
 * 
 * @author xsu
 * 
 */

@Template(id = FilterWholeDaysFromCollectionTemplate.serialVersionUID, description = "Filter whole days from collection", type = TemplateType.FRAGMENT)
public class FilterWholeDaysFromCollectionTemplate extends
        AbstractExecutableTemplate implements ActualValueExpression {
    protected static final long serialVersionUID = 5191361164816346873L;

    private final String        startDateVariableName;
    private final String        endDateVariableName;
    private final String        collectionSourceVariableName;

    @JsonCreator
	public FilterWholeDaysFromCollectionTemplate(
            @JsonProperty("collectionSourceVariableName") String collectionSourceVariableName,
            @JsonProperty("startDateVariableName") String startDateVariableName,
            @JsonProperty("endDateVariableName") String endDateVariableName) {
		this.startDateVariableName = startDateVariableName;
		this.endDateVariableName = endDateVariableName;
		this.collectionSourceVariableName = collectionSourceVariableName;
	}

	@Override
	public ExecutableExpressionsIF getExecutableExpressions() {
		return new InnerExecutableExpressions();
	}

    @Override
    public Set<Class<?>> getUsedClasses() {
        return CECollectionsUtil.<Class<?>> newSet(GlobalFunctions.class,
                Collection.class, ContiguousDays.class, Temporal.class,
                Date.class);
    }

	public String getStartDateVariableName() {
		return startDateVariableName;
	}

	public String getEndDateVariableName() {
		return endDateVariableName;
	}

	public String getCollectionSourceVariableName() {
		return collectionSourceVariableName;
	}

	private class InnerExecutableExpressions implements ExecutableExpressionsIF {
		@Override
        public String getDroolsExpression(DroolsExpressionContext ctx) {
			// Drools expression is the fragment which itself does not work.
			StringBuilder droolsExpression = new StringBuilder();
            droolsExpression.append("GlobalFunctions.filterWholeDays(")
                    .append(getCollectionSourceVariableName()).append(", ")
                    .append(getStartDateVariableName()).append(", ")
                    .append(getEndDateVariableName()).append(" )");
            return droolsExpression.toString();
		}
	}

    @Override
    public TypeDescription getExpressionReturnTypeDescription() {
        return TypeDescription.getTypeDescription(Collection.class,
                ContiguousDays.class);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((collectionSourceVariableName == null) ? 0
                        : collectionSourceVariableName.hashCode());
        result = prime
                * result
                + ((endDateVariableName == null) ? 0 : endDateVariableName
                        .hashCode());
        result = prime
                * result
                + ((startDateVariableName == null) ? 0 : startDateVariableName
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
        FilterWholeDaysFromCollectionTemplate other = (FilterWholeDaysFromCollectionTemplate) obj;
        if (collectionSourceVariableName == null) {
            if (other.collectionSourceVariableName != null)
                return false;
        } else if (!collectionSourceVariableName
                .equals(other.collectionSourceVariableName))
            return false;
        if (endDateVariableName == null) {
            if (other.endDateVariableName != null)
                return false;
        } else if (!endDateVariableName.equals(other.endDateVariableName))
            return false;
        if (startDateVariableName == null) {
            if (other.startDateVariableName != null)
                return false;
        } else if (!startDateVariableName.equals(other.startDateVariableName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "FilterWholeDaysFromCollectionTemplate [startDateVariableName="
                + startDateVariableName + ", endDateVariableName="
                + endDateVariableName + ", collectionSourceVariableName="
                + collectionSourceVariableName + "]";
    }
}

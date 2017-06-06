package net.ahm.activemeasure.templates;

import java.util.HashSet;
import java.util.Set;

import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer;
import net.ahm.careengine.domain.classifier.Classifier;
import net.ahm.careengine.domain.measures.active.ActiveMeasureClassifierBuilder;
import net.ahm.rulesapp.templates.implementations.AbstractExecutableTemplate;
import net.ahm.rulesapp.templates.interfaces.Action;
import net.ahm.rulesapp.templates.interfaces.Template;
import net.ahm.rulesapp.util.DroolsExpressionContext;
import net.ahm.rulesapp.util.ExecutableExpressionsIF;
import net.ahm.rulesapp.util.TemplateType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This template is used to create a new {@linkplain Classifier} instance and
 * add it to the working memory.
 * 
 * @author DHerrington
 * 
 */
@Template(id = ClassifierCreationTemplate.serialVersionUID, description = "Create a new Classifier", type = TemplateType.ACTION)
public class ClassifierCreationTemplate extends AbstractExecutableTemplate
        implements Action {
    static final long             serialVersionUID         = -5166977409438709718L;
    protected static final String CLASSIFIER_VARIBALE_NAME = "$classifier";

    private final long            id;

    @JsonCreator
    public ClassifierCreationTemplate(@JsonProperty("id") long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ClassifierCreationTemplate [id=" + id + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
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
        ClassifierCreationTemplate other = (ClassifierCreationTemplate) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public ExecutableExpressionsIF getExecutableExpressions() {
        return new InnerExecutableExpressions();
    }

    @Override
    public Set<Class<?>> getUsedClasses() {
		Set<Class<?>> results = new HashSet<Class<?>>();
        results.add(Classifier.class);
        results.add(ActiveMeasureClassifierBuilder.class);
        return results;
    }

    private class InnerExecutableExpressions implements ExecutableExpressionsIF {
        @Override
        public String getDroolsExpression(DroolsExpressionContext ctx) {
            String varName = CLASSIFIER_VARIBALE_NAME + ctx.getNextIdnumber();
            return new StringBuilder("Classifier ")
                    .append(varName)
                    .append(" = ")
                    .append(ActiveMeasureGlobalContainer.ActiveMeasureGlobalDefinition.CLASSIFIER_BUILDER
                            .getVariableName())
                    .append(".getClassifierForQualityMeasure( ").append(id)
                    .append(" );").append(NEW_LINE_CHAR)
                    .append(ctx.getStartOfLinePadding()).append("insert( ")
                    .append(varName)
                    .append(" ) ").toString();
        }
    }
}

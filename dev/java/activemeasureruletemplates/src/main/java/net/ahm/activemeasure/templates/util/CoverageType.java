package net.ahm.activemeasure.templates.util;

import java.util.Collection;

import net.ahm.careengine.domain.temporal.ContiguousDays;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;

public enum CoverageType {
    MEDICAL("$initialGapDays", "enrolmentGapDays"), //
    PHARMACY("$initialGapDays", "PharmacyEnrolmentGapDays");

    private final String gapAttributeName;
    private final String variableName;

    private CoverageType(String variableName, String gapAttributeName) {
        this.gapAttributeName = gapAttributeName;
        this.variableName = variableName;
    }

    public AttributeFragmentTemplate getGapAttribute() {
        return new AttributeFragmentTemplate(gapAttributeName,
                TypeDescription.getTypeDescription(Collection.class,
                        ContiguousDays.class));
    }

    public String getVaribleName(int number) {
        return variableName + number;
    }

    public AnyAttributeSingleComparisonFragmentTemplate<Collection<ContiguousDays<?>>> getAnyAttributeSingleComparisonFragmentTemplateForGap(
            int number) {
		AnyAttributeSingleComparisonFragmentTemplate<Collection<ContiguousDays<?>>> template = new AnyAttributeSingleComparisonFragmentTemplate<Collection<ContiguousDays<?>>>();
        template.setAttribute(getGapAttribute());
        template.setVariableName(getVaribleName(number));
        return template;
    }
}

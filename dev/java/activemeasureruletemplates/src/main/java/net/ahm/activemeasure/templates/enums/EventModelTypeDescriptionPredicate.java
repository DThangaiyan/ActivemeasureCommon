package net.ahm.activemeasure.templates.enums;

import net.ahm.careengine.domain.event.Event;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.templates.utils.TypeDescriptionPredicate;

public enum EventModelTypeDescriptionPredicate implements
        TypeDescriptionPredicate {
    EVENT(Event.class);

    private final TypeDescription expectedType;

    private EventModelTypeDescriptionPredicate(Class<?> typeClass) {
        this.expectedType = TypeDescription.getTypeDescription(typeClass);
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public boolean test(TypeDescription typeDescription) {
        return expectedType.equals(typeDescription);
    }
}

package net.ahm.activemeasure.templates.enums;

import java.util.Collections;
import java.util.Set;

import net.ahm.careengine.domain.event.Event;
import net.ahm.rulesapp.templates.interfaces.AccumulateFunction;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.templates.utils.TypeDescriptionPredicate;

public enum EventAccumulateFunctions implements AccumulateFunction {
    LATEST_EVENT_FROM_START_DATE("latestEventFromStartDate",
            EventModelTypeDescriptionPredicate.EVENT, Event.class), //
    LATEST_EVENT_FROM_END_DATE("latestEventFromEndDate",
            EventModelTypeDescriptionPredicate.EVENT, Event.class), //
    FIRST_EVENT_FROM_START_DATE("firstEventFromStartDate",
            EventModelTypeDescriptionPredicate.EVENT, Event.class), //
    FIRST_EVENT_FROM_END_DATE("firstEventFromEndDate",
            EventModelTypeDescriptionPredicate.EVENT, Event.class);

    private EventAccumulateFunctions(String droolsFuction,
            Set<TypeDescriptionPredicate> expectedTypePredicates,
            TypeDescription returnTypeDescription) {
        this.expectedTypePredicates = expectedTypePredicates;
        this.returnType = returnTypeDescription;
        this.droolsFunction = droolsFuction;
    }

    private EventAccumulateFunctions(String droolsFuction,
            TypeDescriptionPredicate expectedTypePredicate,
            Class<?> returnedType) {
        this(droolsFuction, Collections.singleton(expectedTypePredicate),
                TypeDescription.getTypeDescription(returnedType));
    }

    private final Set<? extends TypeDescriptionPredicate> expectedTypePredicates;
    private final TypeDescription                         returnType;
    private final String                                  droolsFunction;

    @Override
    public Set<? extends TypeDescriptionPredicate> getExpectedTypePredicates() {
        return expectedTypePredicates;
    }

    @Override
    public TypeDescription getReturnTypeDescription() {
        return returnType;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getDroolsFunction() {
        return droolsFunction;
    }

    @Override
    public boolean test(TypeDescription typeDescription) {
        return typeDescription.getTypeDescriptionPredicates().containsAll(
                getExpectedTypePredicates());
    }

}

package net.ahm.activemeasure.templates.enums;

import java.util.EnumSet;
import java.util.Set;

import net.ahm.rulesapp.templates.utils.TypeDescriptionPredicate;
import net.ahm.rulesapp.templates.utils.TypeDescriptionPredicateFactory;

import org.kohsuke.MetaInfServices;

@MetaInfServices(TypeDescriptionPredicateFactory.class)
public class EventModelTypeDescriptionPredicateFactory implements
        TypeDescriptionPredicateFactory {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Set<TypeDescriptionPredicate> getAll() {
        return (Set) EnumSet.allOf(EventModelTypeDescriptionPredicate.class);
    }
}

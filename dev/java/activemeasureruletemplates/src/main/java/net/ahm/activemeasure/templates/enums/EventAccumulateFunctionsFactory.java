package net.ahm.activemeasure.templates.enums;

import java.util.EnumSet;
import java.util.Set;

import net.ahm.rulesapp.templates.interfaces.AccumulateFunction;
import net.ahm.rulesapp.templates.interfaces.AccumulateFunctionFactory;

import org.kohsuke.MetaInfServices;

@MetaInfServices(AccumulateFunctionFactory.class)
public class EventAccumulateFunctionsFactory implements
        AccumulateFunctionFactory {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Set<AccumulateFunction> getAll() {
        return (Set) EnumSet.allOf(EventAccumulateFunctions.class);
    }
}

package net.ahm.careengine.activemeasure.qualitymeasure;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import net.ahm.careengine.activemeasure.ActiveMeasureCommandConfiguration;
import net.ahm.careengine.activemeasure.ActiveMeasureCommandOutput;
import net.ahm.careengine.activemeasure.ActiveMeasureType;
import net.ahm.careengine.activemeasure.TypedActiveMeasureEngine;
import net.ahm.careengine.activemeasure.qualitymeasure.listener.DroolsEventTestListener;
import net.ahm.careengine.eventprocessing.engine.drools.DroolsEventListener;

public class QualityMeasureTestEngine extends TypedActiveMeasureEngine {

    protected QualityMeasureTestEngine() {
        this(EnumSet.allOf(ActiveMeasureType.class));
    }

    protected QualityMeasureTestEngine(Set<ActiveMeasureType> measureTypes) {
        super(measureTypes);
    }

    protected QualityMeasureTestEngine(String urlString,
            String resourcesTypeString,
            String expectedPackageNameBeginingString,
            Set<ActiveMeasureType> measureTypes) {
        super(urlString, resourcesTypeString,
                expectedPackageNameBeginingString, measureTypes);
    }

    private static ThreadLocal<DroolsEventTestListener> LOCAL_LISTENER = new ThreadLocal<DroolsEventTestListener>() {
                                                          @Override
                                                          protected DroolsEventTestListener initialValue() {
                                                              return new DroolsEventTestListener();
                                                          }
                                                      };

    public static QualityMeasureTestEngine getInstance() {
        return new QualityMeasureTestEngine();
    }

    public static QualityMeasureTestEngine getEngine(String urlString,
            String resourcesTypeString,
            String expectedPackageNameBeginingString,
            Set<ActiveMeasureType> measureTypes) {
        return new QualityMeasureTestEngine();
    }

    @Override
    public Collection<DroolsEventListener> getDroolsEventListeners(
            ActiveMeasureCommandOutput output,
            ActiveMeasureCommandConfiguration configuration) {
        Collection<DroolsEventListener> listenerCollection = super
                .getDroolsEventListeners(output, configuration);

        DroolsEventTestListener testListener = getTestListener();
        testListener.getRulesFired().clear();
        listenerCollection.add(testListener);

        return listenerCollection;
    }

    public DroolsEventTestListener getTestListener() {
        return LOCAL_LISTENER.get();
    }
}

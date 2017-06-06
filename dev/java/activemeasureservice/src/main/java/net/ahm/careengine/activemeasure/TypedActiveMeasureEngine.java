package net.ahm.careengine.activemeasure;

import java.util.Set;


public class TypedActiveMeasureEngine extends BaseActiveMeasureEngine {
    private final Set<ActiveMeasureType> measureTypes;

    public static TypedActiveMeasureEngine getEngine(
            Set<ActiveMeasureType> measureTypes) {
        return new TypedActiveMeasureEngine(measureTypes);
    }

    private static String getUrlString(boolean useLocalFiles,
            Set<ActiveMeasureType> measureTypes) {
        if (useLocalFiles) {
            return ActiveMeasureType.getAllDrlFileLocations(measureTypes);
        } else {
            return getURLStringFromProperties();
        }
    }

    private static boolean useLocalFiles(String resourceTypeString) {
        return DRL_FILES_IN_RESOURCES.equals(resourceTypeString)
                || PACKAGE_FILES_IN_RESOURCES.equals(resourceTypeString);
    }

    protected TypedActiveMeasureEngine(String urlString,
            String resourcesTypeString, String expectedPackageNameBegingString,
            Set<ActiveMeasureType> measureTypes) {
        super(urlString, null, resourcesTypeString,
                getUsernameFromProperties(), getPasswordFromProperties(),
                expectedPackageNameBegingString);
        this.measureTypes = measureTypes;
    }

    protected TypedActiveMeasureEngine(String resourceTypeString,
            Set<ActiveMeasureType> measureTypes) {
        this(getUrlString(useLocalFiles(resourceTypeString), measureTypes),
                resourceTypeString, ActiveMeasureType
                        .getAllPackageNameStarts(measureTypes), measureTypes);
    }

    protected TypedActiveMeasureEngine(Set<ActiveMeasureType> measureTypes) {
        this(getResourceTypeFromProperties(), measureTypes);
    }

    public Set<ActiveMeasureType> getActiveMeasureTypes() {
        return measureTypes;
    }
}

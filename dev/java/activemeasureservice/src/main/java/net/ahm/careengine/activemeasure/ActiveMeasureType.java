package net.ahm.careengine.activemeasure;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import net.ahm.careengine.dvo.ProductType;
import net.ahm.careengine.function.Function;
import net.ahm.careengine.util.StringUtilities;

public enum ActiveMeasureType {
    SHARED("shared"), //
    QUALITY_MEASURE("qualitymeasures", SHARED, ProductType.AQM), //
    CDM("cdm", SHARED, ProductType.ACDM), //
    MEDICAL_CASE("medicalcase", SHARED, ProductType.ACASE);

    private static final String                             COMMON_PACKAGE_NAME_START = "net.ahm.activemeasure.";
    private static final String                             FILE_LOCATION_START       = "classpath:activeMeasuresPackages/";
    private static final String                             FILE_LOCATION_END         = ".*.drl";
    private static final String                             COMMA                     = ",";
    public static final Set<ActiveMeasureType>              ALL_TYPES                 = Collections
                                                                                              .unmodifiableSet(EnumSet
                                                                                                      .allOf(ActiveMeasureType.class));
    public static final Map<ProductType, ActiveMeasureType> MEASURE_TYPES_BY_PRODUCT_TYPE;
    static {
		Map<ProductType, ActiveMeasureType> tempMap = new EnumMap<ProductType, ActiveMeasureType>(
                ProductType.class);
        for (ActiveMeasureType measureType : ActiveMeasureType.values()) {
            ProductType productType = measureType.getRelatedProduct();
            if (productType != null) {
                tempMap.put(productType, measureType);
            }
        }
        MEASURE_TYPES_BY_PRODUCT_TYPE = Collections.unmodifiableMap(tempMap);
    }

    private final String                                    packageNameStart;
    private final String                                    drlFileLocation;
    private final ActiveMeasureType                         additionalType;
    private final ProductType                               relatedProduct;

    private ActiveMeasureType(String measurePrefix,
            ActiveMeasureType measureType, ProductType relatedProduct) {
        this.packageNameStart = COMMON_PACKAGE_NAME_START + measurePrefix + ".";
        this.drlFileLocation = FILE_LOCATION_START + measurePrefix
                + FILE_LOCATION_END;
        this.additionalType = measureType;
        this.relatedProduct = relatedProduct;
    }

    private ActiveMeasureType(String measurePrefix) {
        this(measurePrefix, null, null);
    }

    public String getPackageNameStart() {
        return packageNameStart;
    }

    public String getDrlFileLocation() {
        return drlFileLocation;
    }

    public ProductType getRelatedProduct() {
        return relatedProduct;
    }

    public static String getAllPackageNameStarts(
            Iterable<ActiveMeasureType> measureTypes) {
        return StringUtilities.convertToString(measureTypes,
                PackageNameSartFunction.INSTANCE, COMMA);
    }

    public static String getAllDrlFileLocations(
            Iterable<ActiveMeasureType> measureTypes) {
        return StringUtilities.convertToString(measureTypes,
                DrlFileLocationFunction.INSTANCE, COMMA);
    }

    public static Set<ActiveMeasureType> getAllMeasureTypes(
            Iterable<ActiveMeasureType> initialTypes) {
        Set<ActiveMeasureType> resultSet = EnumSet
                .noneOf(ActiveMeasureType.class);

        for (ActiveMeasureType type : initialTypes) {
            addActiveMeasureTypeAndRelatedTypes(type, resultSet);
        }

        return resultSet;
    }

    public static Set<ActiveMeasureType> getAllRelatedMeasureTypes(
            ProductType productType) {
        Set<ActiveMeasureType> resultSet = EnumSet
                .noneOf(ActiveMeasureType.class);

        ActiveMeasureType relatedType = getRelatedMeasureType(productType);
        if (relatedType != null) {
            resultSet.add(relatedType);
            addActiveMeasureTypeAndRelatedTypes(relatedType, resultSet);
        }

        return resultSet;
    }

    public static ActiveMeasureType getRelatedMeasureType(
            ProductType productType) {
        return MEASURE_TYPES_BY_PRODUCT_TYPE.get(productType);
    }

    public static boolean hasRelatedActiveMeasureType(ProductType productType) {
        return getRelatedMeasureType(productType) != null;
    }

    private static void addActiveMeasureTypeAndRelatedTypes(
            ActiveMeasureType rootType, Set<ActiveMeasureType> set) {
        if (rootType != null) {
            set.add(rootType);
            addActiveMeasureTypeAndRelatedTypes(rootType.additionalType, set);
        }
    }

    private static enum PackageNameSartFunction implements
            net.ahm.careengine.function.Function<ActiveMeasureType, String> {
        INSTANCE;

        @Override
        public String apply(ActiveMeasureType amt) {
            return amt.getPackageNameStart();
        }
    }

    private static enum DrlFileLocationFunction implements
            Function<ActiveMeasureType, String> {
        INSTANCE;

        @Override
        public String apply(ActiveMeasureType amt) {
            return amt.getDrlFileLocation();
        }
    }
}

package net.ahm.careengine.activemeasure;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import net.ahm.careengine.dvo.ProductType;

import org.apache.commons.collections.CollectionUtils;

public class SimpleTypedEngineSupplier implements ActiveMeasureEngineSupplier {

    private final TypedActiveMeasureEngine                   engineForAllTypes    = new TypedActiveMeasureEngine(
                                                                                          ActiveMeasureType.ALL_TYPES);
	private final Map<ProductType, TypedActiveMeasureEngine> enginesByProductType = new EnumMap<ProductType, TypedActiveMeasureEngine>(
                                                                                          ProductType.class);
    private final boolean                                    lazyInitilazation;

    public SimpleTypedEngineSupplier(boolean lazyInitilazation) {
        this.lazyInitilazation = lazyInitilazation;
        if (!lazyInitilazation) {
            for (ProductType productType : ActiveMeasureType.MEASURE_TYPES_BY_PRODUCT_TYPE
                    .keySet()) {
                TypedActiveMeasureEngine tempEngine = createEngineByProductType(productType);
                enginesByProductType.put(productType, tempEngine);
            }
        }
    }

    public SimpleTypedEngineSupplier() {
        this(false);
    }

    private TypedActiveMeasureEngine createEngineByProductType(
            ProductType productType) {
        Set<ActiveMeasureType> relatedTypes = ActiveMeasureType
                .getAllRelatedMeasureTypes(productType);
        if (!CollectionUtils.isEmpty(relatedTypes)) {
            return new TypedActiveMeasureEngine(relatedTypes);
        } else {
            return null;
        }
    }

    @Override
    public ActiveMeasureEngine getEngineForAllTypes() {
        return engineForAllTypes;
    }

    @Override
    public ActiveMeasureEngine getEngineByProductType(ProductType productType) {
        TypedActiveMeasureEngine engine = enginesByProductType.get(productType);
        if (lazyInitilazation && engine == null
                && ActiveMeasureType.hasRelatedActiveMeasureType(productType)) {
            engine = createEngineByProductType(productType);
            enginesByProductType.put(productType, engine);
        }
        return engine;
    }
}

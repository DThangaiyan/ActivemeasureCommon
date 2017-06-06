package net.ahm.careengine.activemeasure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import net.ahm.careengine.dvo.ProductType;

import org.junit.Test;

public class ActiveMeasureTypeTest {
    private static final Set<ProductType> USED_PRODUCT_TYPE_SET;
    static {
        Set<ProductType> tempSet = EnumSet.noneOf(ProductType.class);
        for (ActiveMeasureType type : ActiveMeasureType.ALL_TYPES) {
            if (type.getRelatedProduct() != null) {
                tempSet.add(type.getRelatedProduct());
            }
        }
        USED_PRODUCT_TYPE_SET = Collections.unmodifiableSet(tempSet);
    }

    @Test
    public void testGetAllRelatedMeasureTypes_AQM() {
        innerTestGetAllRelatedMeasureTypes(ProductType.AQM, EnumSet.of(
                ActiveMeasureType.SHARED, ActiveMeasureType.QUALITY_MEASURE));
    }

    @Test
    public void testGetAllRelatedMeasureTypes_ACDM() {
        innerTestGetAllRelatedMeasureTypes(ProductType.ACDM,
                EnumSet.of(ActiveMeasureType.SHARED, ActiveMeasureType.CDM));
    }

    @Test
    public void testGetAllRelatedMeasureTypes_ACASE() {
        innerTestGetAllRelatedMeasureTypes(ProductType.ACASE, EnumSet.of(
                ActiveMeasureType.SHARED, ActiveMeasureType.MEDICAL_CASE));
    }

    @Test
    public void testGetAllRelatedMeasureTypes_other() {
        for (ProductType product : ProductType.values()) {
            if (!USED_PRODUCT_TYPE_SET.contains(product)) {
                innerTestGetAllRelatedMeasureTypes(product,
                        Collections.<ActiveMeasureType> emptySet());
            }
        }
    }

    private void innerTestGetAllRelatedMeasureTypes(ProductType product,
            Set<ActiveMeasureType> expectedTypes) {
        Set<ActiveMeasureType> actualSet = ActiveMeasureType
                .getAllRelatedMeasureTypes(product);
        assertEquals("Not the expected Set of ActiveMeasureTypes",
                expectedTypes, actualSet);
    }

    @Test
    public void testGetRelatedMeasureType_ACDM() {
        assertEquals("Not the epected ActiveMeasureType",
                ActiveMeasureType.CDM,
                ActiveMeasureType.getRelatedMeasureType(ProductType.ACDM));
    }

    @Test
    public void testGetRelatedMeasureType_AQM() {
        assertEquals("Not the epected ActiveMeasureType",
                ActiveMeasureType.QUALITY_MEASURE,
                ActiveMeasureType.getRelatedMeasureType(ProductType.AQM));
    }

    @Test
    public void testGetRelatedMeasureType_ACASE() {
        assertEquals("Not the epected ActiveMeasureType",
                ActiveMeasureType.MEDICAL_CASE,
                ActiveMeasureType.getRelatedMeasureType(ProductType.ACASE));
    }

    @Test
    public void testGetRelatedMeasureType_other() {
        for (ProductType productType : ProductType.values()) {
            if (!USED_PRODUCT_TYPE_SET.contains(productType)) {
                assertNull("Not the epected ActiveMeasureType",
                        ActiveMeasureType.getRelatedMeasureType(productType));
            }
        }
    }
}

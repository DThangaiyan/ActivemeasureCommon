package net.ahm.careengine.activemeasure;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import net.ahm.careengine.command.CommandIF;
import net.ahm.careengine.dvo.ProductType;

import org.junit.Test;

public abstract class AbstractSimpleTypedEngineSupplierTest {
    private final ActiveMeasureEngineSupplier engineSupplier;

    protected AbstractSimpleTypedEngineSupplierTest(
            ActiveMeasureEngineSupplier engineSupplier) {
        this.engineSupplier = engineSupplier;
    }

    @Test
    public void testGetEngineForAllTypes() {
        final CommandIF<?, ?, ?> engine = engineSupplier
                .getEngineForAllTypes();
        assertNotNull("The engine should not be null", engine);

        assertSame(
                "Two calls to get the same type of engine should return the same engine",
                engine, engineSupplier.getEngineForAllTypes());
    }

    @Test
    public void testGetEngineByProductType_ACDM() {
        ProductType productType = ProductType.ACDM;
        innerTestGetEnginebyProductType(productType);
    }

    @Test
    public void testGetEngineByProductType_ACASE() {
        ProductType productType = ProductType.ACASE;
        innerTestGetEnginebyProductType(productType);
    }

    @Test
    public void testGetEngineByProductType_ACQM() {
        ProductType productType = ProductType.AQM;
        innerTestGetEnginebyProductType(productType);
    }

    private void innerTestGetEnginebyProductType(ProductType productType) {
        final CommandIF<?, ?, ?> engine = engineSupplier
                .getEngineByProductType(productType);
        assertNotNull("The engine for the product type:" + productType
                + " should not be null", engine);

        assertSame("Two calls to get a " + productType
                + " engine should return the same engine", engine,
                engineSupplier.getEngineByProductType(productType));
    }

    @Test
    public void testGetEngineByProductType_SEGMENTATION() {
        assertNoEngineForProductType(ProductType.SEGMENTATION);
    }

    @Test
    public void testGetEngineByProductType_MPP() {
        assertNoEngineForProductType(ProductType.MPP);
    }

    @Test
    public void testGetEngineByProductType_CDM() {
        assertNoEngineForProductType(ProductType.CDM);
    }

    private void assertNoEngineForProductType(ProductType productType) {
        assertNull("There should be no engine for " + productType
                + " for the first call",
                engineSupplier.getEngineByProductType(productType));
        assertNull("There should be no engine for " + productType
                + " for the second time called",
                engineSupplier.getEngineByProductType(productType));
    }
}

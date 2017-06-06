package net.ahm.careengine.activemeasure;

public class LazyLoadedSimpleTypedEngineSupplierTest extends
        AbstractSimpleTypedEngineSupplierTest {
    public LazyLoadedSimpleTypedEngineSupplierTest() {
        super(new SimpleTypedEngineSupplier(false));
    }
}

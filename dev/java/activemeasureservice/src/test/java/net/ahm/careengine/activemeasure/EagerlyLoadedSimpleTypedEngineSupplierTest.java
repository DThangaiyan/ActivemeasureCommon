package net.ahm.careengine.activemeasure;

public class EagerlyLoadedSimpleTypedEngineSupplierTest extends
        AbstractSimpleTypedEngineSupplierTest {
    public EagerlyLoadedSimpleTypedEngineSupplierTest() {
        super(new SimpleTypedEngineSupplier(true));
    }
}

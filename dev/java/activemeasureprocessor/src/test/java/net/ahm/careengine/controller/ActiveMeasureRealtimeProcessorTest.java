package net.ahm.careengine.controller;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = "/META-INF/activemeasurebeans.xml")
public class ActiveMeasureRealtimeProcessorTest extends
        AbstractJUnit4SpringContextTests {
    @Autowired
    ActiveMeasureRealtimeProcessor activeMeasureRealtimeProcessor;

    @Test
    public void testProcessorNotNull() throws Exception {
        assertNotNull("Processor should have been initialized by spring.",
                activeMeasureRealtimeProcessor);
        assertNotNull(
                "Rule engineSupplier used by the processor should have been initialized by spring.",
                activeMeasureRealtimeProcessor.engineSupplier);
        assertNotNull("The engineSupplier should have returned a value",
                activeMeasureRealtimeProcessor.engineSupplier
                        .getEngineForAllTypes());
        assertNotNull(
                "Persistence command should have been initialized by spring.",
                activeMeasureRealtimeProcessor.activeMeasurePersistenceCommand);
    }
}

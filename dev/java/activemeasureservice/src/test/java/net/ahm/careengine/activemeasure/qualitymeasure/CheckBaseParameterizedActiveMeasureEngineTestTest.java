package net.ahm.careengine.activemeasure.qualitymeasure;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class CheckBaseParameterizedActiveMeasureEngineTestTest {

    @Test
    public void testUSE_HARDCODED_ENGINE_value() {
        assertFalse("This value should never be checked in as true",
                BaseParameterizedActiveMeasureEngineTest.USE_HARDCODED_ENGINE);
    }
}

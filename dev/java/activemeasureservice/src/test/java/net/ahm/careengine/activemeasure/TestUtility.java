package net.ahm.careengine.activemeasure;

import org.apache.log4j.Logger;

public class TestUtility {
    private static final String RUN_RULES_COVERAGE_TEST_PROPERTY_NAME = "run.rules.coverage.test";
    private static final Logger LOGGER                                = Logger.getLogger(TestUtility.class);

    private TestUtility() {
        super();
    }

    public static boolean runActiveMeasuresCoverageTest() {
        String property = System
                .getProperty(RUN_RULES_COVERAGE_TEST_PROPERTY_NAME);
        LOGGER.error("The value of " + RUN_RULES_COVERAGE_TEST_PROPERTY_NAME
                + " was " + property);
        return "true".equalsIgnoreCase(property);
    }
}

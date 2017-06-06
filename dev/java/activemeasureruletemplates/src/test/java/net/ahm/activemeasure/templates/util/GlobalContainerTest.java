package net.ahm.activemeasure.templates.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Date;

import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer;
import net.ahm.careengine.domain.classifier.ClassifierBuilder;
import net.ahm.careengine.domain.measures.active.ActiveMeasureBuilder;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureBuilder;

import org.easymock.EasyMock;
import org.junit.Test;

public class GlobalContainerTest {

    @Test
    public void testIsExpectedVariable_Dates() {
        assertTrue(ActiveMeasureGlobalContainer.INSTANCE.isExpectedVariable(
                "measurementEndDate", new Date()));

        assertTrue(ActiveMeasureGlobalContainer.INSTANCE.isExpectedVariable(
                "measurementEndDate", new java.sql.Date(1L)));

        assertTrue(ActiveMeasureGlobalContainer.INSTANCE.isExpectedVariable(
                "measurementEndDate", new Timestamp(1L)));
    }

    @Test
    public void testIsExpectedVariable_ActiveMeasureBuilder() {
        assertTrue(ActiveMeasureGlobalContainer.INSTANCE.isExpectedVariable(
                "activeMeasureBuilder",
                EasyMock.createNiceMock(ActiveMeasureBuilder.class)));
    }

    @Test
    public void testIsExpectedVariable_ClassifierBuilder() {
        assertTrue(ActiveMeasureGlobalContainer.INSTANCE.isExpectedVariable(
                "classifierBuilder",
                EasyMock.createNiceMock(ClassifierBuilder.class)));
    }

    @Test
    public void testIsExpectedVariable_FactLevelMeasureBuilder() {
        assertTrue(ActiveMeasureGlobalContainer.INSTANCE.isExpectedVariable(
                "factLevelMeasureBuilder",
                EasyMock.createNiceMock(FactLevelMeasureBuilder.class)));
    }

    @Test
    public void testIsExpectedVariable_Object() {
        assertFalse(ActiveMeasureGlobalContainer.INSTANCE.isExpectedVariable(
                "measurementEndDate", new Object()));
        assertFalse(ActiveMeasureGlobalContainer.INSTANCE.isExpectedVariable(
                "activeMeasureBuilder", new Object()));
        assertFalse(ActiveMeasureGlobalContainer.INSTANCE.isExpectedVariable(
                "classifierBuilder", new Object()));
        assertFalse(ActiveMeasureGlobalContainer.INSTANCE.isExpectedVariable(
                "factLevelMeasureBuilder", new Object()));
    }
}

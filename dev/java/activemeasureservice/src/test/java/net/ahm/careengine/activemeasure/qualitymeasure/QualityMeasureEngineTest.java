package net.ahm.careengine.activemeasure.qualitymeasure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer;
import net.ahm.careengine.activemeasure.ActiveMeasureCommandConfiguration;
import net.ahm.careengine.activemeasure.ActiveMeasureCommandInput;
import net.ahm.careengine.activemeasure.ActiveMeasureCommandOutput;
import net.ahm.careengine.activemeasure.BaseActiveMeasureEngine;
import net.ahm.careengine.domain.event.proc.ProcedureEvent;
import net.ahm.careengine.domain.impl.comorbidclinicalcondition.DefaultHierarchicalClinicalConditionCategoryBuilder;
import net.ahm.careengine.domain.impl.comorbidclinicalcondition.DefaultRiskMeasureWeightsBuilder;
import net.ahm.careengine.domain.impl.event.FallbackEventDateStrategy;
import net.ahm.careengine.domain.impl.measure.active.DefaultActiveMeasureBuilder;
import net.ahm.careengine.domain.impl.measure.active.DefaultActiveMeasureClassifierBuilder;
import net.ahm.careengine.domain.impl.measure.active.DefaultFactLevelMeasureBuilder;
import net.ahm.careengine.domain.impl.member.DefaultActiveMeasureMemberInfo;
import net.ahm.careengine.domain.impl.member.DefaultMemberProfile;
import net.ahm.careengine.domain.impl.proc.DefaultProcedureEvent;
import net.ahm.careengine.domain.measures.active.ActiveMeasure;
import net.ahm.careengine.domain.member.ActiveMeasuresMemberInfo;
import net.ahm.careengine.domain.member.Gender;
import net.ahm.careengine.ruleengine.DateTimeUnit;
import net.ahm.careengine.util.CareEngineTestUtilities;
import net.ahm.careengine.util.GlobalFunctions;

import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Test;

public class QualityMeasureEngineTest {

    @Test
    public void testExecute() throws Exception {
        InstrumentedEngine engine = new InstrumentedEngine();

        Date measurementEndDate = new Date();
        DefaultMemberProfile profile = new DefaultMemberProfile();
        ActiveMeasureCommandInput input = new ActiveMeasureCommandInput();
        input.setMemberProfile(profile);
        profile.setMemberInfo(getMemberInfo(measurementEndDate));
        profile.setProcedureEvents(getProcedureEvents(measurementEndDate));

        final DefaultActiveMeasureBuilder activeMeasureBuilder = new DefaultActiveMeasureBuilder();
        final DefaultFactLevelMeasureBuilder flmBuilder = new DefaultFactLevelMeasureBuilder();
        ActiveMeasureCommandOutput output = new ActiveMeasureCommandOutput(
                activeMeasureBuilder, flmBuilder);

        ActiveMeasureCommandConfiguration configuration = new ActiveMeasureCommandConfiguration();
        configuration.setMeasurementEndDate(measurementEndDate);
        configuration.setMeasurementStartDate(measurementEndDate);
        configuration.setActiveMeasureBuilder(activeMeasureBuilder);
        configuration.setClassifierBuilder( new DefaultActiveMeasureClassifierBuilder());
        configuration.setFactLevelMeasureBuilder(flmBuilder);
        configuration.setHierarchicalClinicalConditionCategoryBuilder(
                new DefaultHierarchicalClinicalConditionCategoryBuilder());
        configuration
                .setRiskStratificationMeasureWeightsBuilder(new DefaultRiskMeasureWeightsBuilder());

        engine.execute(input, output, configuration);

        boolean hasMeasurement2015ACO19 = false;
        for (ActiveMeasure measure : output.getActiveMeasures()) {
            // FIXME remove the hard coded line below
            final long expectedMeasureId = 13;
            if (measure.getMeasureId() == expectedMeasureId) {
                hasMeasurement2015ACO19 = true;
                assertEquals("Measure " + expectedMeasureId
                        + " should be marked as excluded.", true,
                        measure.isExcludedFromDenominator());

                assertTrue("Measure " + expectedMeasureId
                        + " should have been marked as compliant.",
                        measure.isInNumerator());
            }
        }

        assertTrue(
                "Expecting Active Measure for ACO19 being generated but it was not.",
                hasMeasurement2015ACO19);

        CareEngineTestUtilities.assertNoErrorMessages(
                "The following unexprected globals were created",
                engine.unexpectedVariables);

        assertEquals(
                "The expected globals did not match the actual ones created",
                engine.expectedVariables, engine.actualVariables);
    }

    private List<ProcedureEvent> getProcedureEvents(Date measurementEndDate) {
        DefaultProcedureEvent event = createProcedureEvent(8684,
                measurementEndDate, 2, DateTimeUnit.MONTH);
        DefaultProcedureEvent event8021 = createProcedureEvent(8021,
                measurementEndDate, 2, DateTimeUnit.MONTH);
        DefaultProcedureEvent event8538 = createProcedureEvent(8538,
                measurementEndDate, 2, DateTimeUnit.MONTH);
        List<ProcedureEvent> events = new ArrayList<ProcedureEvent>();
        events.add(event);
        events.add(event8021);
        events.add(event8538);
        return events;
    }

    private DefaultProcedureEvent createProcedureEvent(int elementId,
            Date measurementEndDate, int pushBack, DateTimeUnit amountUnit) {
        DefaultProcedureEvent event = new DefaultProcedureEvent();

        event.setElements(new ArrayList<Integer>(Arrays.asList(elementId)));
        event.setEndDate(GlobalFunctions.getEarlierDate(measurementEndDate,
                pushBack, amountUnit));
        event.setEventDateStrategy(new FallbackEventDateStrategy(event
                .getRawStartDate(), event.getRawEndDate(), event
                .getReportedDate()));
        return event;
    }

    private ActiveMeasuresMemberInfo getMemberInfo(Date measurementEndDate) {
        DefaultActiveMeasureMemberInfo member = new DefaultActiveMeasureMemberInfo();
        member.setBirthDate(GlobalFunctions.getEarlierDate(measurementEndDate,
                60, DateTimeUnit.YEAR));
        member.setGender(Gender.MALE);

        return member;
    }

    static class InstrumentedEngine extends BaseActiveMeasureEngine {
        protected final Set<String> expectedVariables;
        protected final Set<String> actualVariables;
        protected final Set<String> unexpectedVariables = new HashSet<>();

        protected InstrumentedEngine() {
            expectedVariables = ActiveMeasureGlobalContainer.INSTANCE
                    .getAllVariablesByName().keySet();
            actualVariables = new HashSet<>(expectedVariables.size());
        }

        @Override
        protected void addGlobalToSession(StatelessKnowledgeSession session,
                String globalName, Object value) {
            if (!ActiveMeasureGlobalContainer.INSTANCE.isExpectedVariable(globalName, value)) {
                unexpectedVariables.add("Did not expect the global:"
                        + globalName + " to have the value:" + value);
            }
            actualVariables.add(globalName);
            super.addGlobalToSession(session, globalName, value);
        }
    }
}

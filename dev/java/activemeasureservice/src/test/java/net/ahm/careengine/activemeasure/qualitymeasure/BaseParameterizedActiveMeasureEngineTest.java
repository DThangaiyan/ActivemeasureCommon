package net.ahm.careengine.activemeasure.qualitymeasure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.ahm.careengine.activemeasure.ActiveMeasureCommandConfiguration;
import net.ahm.careengine.activemeasure.ActiveMeasureCommandOutput;
import net.ahm.careengine.activemeasure.ActiveMeasureJSONPersister;
import net.ahm.careengine.activemeasure.ActiveMeasurePersistenceCommandConfiguration;
import net.ahm.careengine.activemeasure.ActiveMeasurePersistenceCommandInput;
import net.ahm.careengine.activemeasure.ActiveMeasurePersistenceCommandOutput;
import net.ahm.careengine.activemeasure.ActiveMeasureRunSettingDVO;
import net.ahm.careengine.activemeasure.ActiveMeasureType;
import net.ahm.careengine.activemeasure.MemberActiveMeasureDVO;
import net.ahm.careengine.activemeasure.TestUtility;
import net.ahm.careengine.activemeasure.qualitymeasure.listener.DroolsEventTestListener;
import net.ahm.careengine.command.activemeasure.ActiveMeasurePersistenceCommand;
import net.ahm.careengine.domain.measures.active.ActiveMeasure;
import net.ahm.careengine.domain.measures.active.ActiveMeasureHolder;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureHolder;
import net.ahm.careengine.message.Status;
import net.ahm.careengine.service.activemeasure.ActiveMeasureCommandOutputIF;
import net.ahm.careengine.testframework.AbstractTestCase;
import net.ahm.careengine.testframework.activemeasure.ActiveMeasureTestCase;
import net.ahm.careengine.testframework.activemeasure.ActiveMeasureTestCaseReader;
import net.ahm.cev4.activemeasures.config.DefaultActiveMeasureRunSetting;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.component.file.FileComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.log4j.Logger;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public abstract class BaseParameterizedActiveMeasureEngineTest {
    private static final CamelContext              CAMEL                   = new DefaultCamelContext();
    private static final Map<String, Integer>      AGGREGATED_RULE_COUNTER = new HashMap<String, Integer>();

    private static final String                    RULE_COVERAGE_LOCATION  = "target";
    private static final String                    RULE_COVERAGE_FILE      = "covered-rules.txt";
    private static ActiveMeasurePersistenceCommand ACTIVE_MEASURE_PERSISTENCE_COMMAND;
    private static final Logger                    LOGGER                  = Logger.getLogger(BaseParameterizedActiveMeasureEngineTest.class);

    static final boolean                           USE_HARDCODED_ENGINE    = false;

    private ActiveMeasureTestCase                  testCase;

    private static class EngineHolder {
        private static final QualityMeasureTestEngine ENGINE;

        static final String                           PACKAGE_DRL_FILES          = "classpath:activeMeasuresPackages/*.drl";
        static final String                           LATEST_PACKAGE_FROM_GUVNOR = "http://192.168.4.112:8090/guvnor-5.5.0.Final-jboss-as-7.0/rest/packages";

        static {
            if (USE_HARDCODED_ENGINE) {
                ENGINE = new QualityMeasureTestEngine(PACKAGE_DRL_FILES,
                        QualityMeasureTestEngine.DRL_FILES_IN_RESOURCES,
                        "net.ahm.activemeasure.", ActiveMeasureType.ALL_TYPES);
            } else {
                // this engine is configured based on property files
                ENGINE = QualityMeasureTestEngine.getInstance();
            }
        }
    }

    @BeforeClass
    public static void setup() throws Exception {
        ACTIVE_MEASURE_PERSISTENCE_COMMAND = new ActiveMeasurePersistenceCommand();
        ACTIVE_MEASURE_PERSISTENCE_COMMAND
                .setPersister(new ActiveMeasureJSONPersister());
    }
    /**
     * Record coverage into a file. Using camel is over killing here but it
     * meant to be a small research on what Camel is capable for other areas.
     * 
     * Also the file producing code in this method is NOT thread safe. When unit
     * tests are executed in parallel, the result is not accurate.
     * 
     * @throws Exception
     */
    @AfterClass
    public static void shutdown() throws Exception {
        if (!TestUtility.runActiveMeasuresCoverageTest()) {
            return;
        }
        try {
            CAMEL.addComponent("file", new FileComponent());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Exception durring shutdown", e);
        }
        // start Camel
        CAMEL.start();
        // get the file component
        @SuppressWarnings("rawtypes")
        Component component = CAMEL.getComponent("file");

        @SuppressWarnings("rawtypes")
        Endpoint endpoint = component.createEndpoint("file://"
                + RULE_COVERAGE_LOCATION);
        @SuppressWarnings("rawtypes")
        Producer producer = endpoint.createProducer();

        // create an Exchange that we want to send to the endpoint
        Exchange exchange = endpoint.createExchange();

        // output filename
        exchange.getIn().setHeader(FileComponent.HEADER_FILE_NAME,
                RULE_COVERAGE_FILE);

        StringBuilder builder = new StringBuilder();

        // set the all available rules
        Collection<KnowledgePackage> packages = getKnowledgePackages();
        for (KnowledgePackage pkg : packages) {
            Collection<Rule> rules = pkg.getRules();
            for (Rule rule : rules) {
                String key = pkg.getName() + "/" + rule.getName();
                if (!AGGREGATED_RULE_COUNTER.containsKey(key)) {
                    AGGREGATED_RULE_COUNTER.put(key, 0);
                }
            }
        }

        for (String ruleName : AGGREGATED_RULE_COUNTER.keySet()) {
            builder.append(ruleName + "\t"
                    + AGGREGATED_RULE_COUNTER.get(ruleName) + "\n");
        }

        exchange.getIn().setBody(builder.toString());

        producer.process(exchange);

        producer.stop();
    }

    private static Collection<KnowledgePackage> getKnowledgePackages() {
        QualityMeasureTestEngine engine = EngineHolder.ENGINE;
        if (engine != null && engine.getKnowledgeBase() != null
                && engine.getKnowledgeBase().getKnowledgePackages() != null) {
            return engine.getKnowledgeBase().getKnowledgePackages();
        }

        return Collections.emptySet();
    }

    @Test
    public void testExecute() throws Exception {
        LOGGER.debug("starting test case " + testCase.getTestCaseID());
        AbstractTestCase.vaildateTestCase(testCase);

        ActiveMeasureCommandConfiguration configuration = testCase
                .getConfiguration();

        ActiveMeasureCommandOutput actualOutput = new ActiveMeasureCommandOutput(
                (ActiveMeasureHolder) configuration.getActiveMeasureBuilder(),
                (FactLevelMeasureHolder) configuration
                        .getFactLevelMeasureBuilder());
        // record the rules being fired.

        LOGGER.debug("about to start engine");
        getEngine().execute(testCase.getInput(), actualOutput,
                configuration);
        LOGGER.debug("finished engine");

        if (TestUtility.runActiveMeasuresCoverageTest()) {
            recordFiredRules(getEngine().getTestListener());
        }

        if (isPersisting()) {
            persistAllResults(actualOutput, configuration);
        }
        testCase.assertResults(actualOutput);
    }

    protected QualityMeasureTestEngine getEngine() {
        return EngineHolder.ENGINE;
    }

    /**
     * Child class can decide whether we should persist the result or not. By
     * default it will not.
     * 
     * @return
     */
    public boolean isPersisting() {
        return false;
    }

    private void recordFiredRules(DroolsEventTestListener testListener) {
        Map<String, AtomicInteger> rulesFired = testListener.getRulesFired();
        for (Map.Entry<String, AtomicInteger> entry : rulesFired.entrySet()) {
            addToAggregatedRuleCounter(entry.getKey(), entry.getValue().get());
        }
    }

    /**
     * persist the result through persister
     * 
     * @param actualOutput
     * @param configuration
     *            used by the rule engine
     * @throws Exception
     */
    private void persistAllResults(ActiveMeasureCommandOutputIF actualOutput,
            ActiveMeasureCommandConfiguration configuration) throws Exception {
        ActiveMeasurePersistenceCommandInput tobePersisted = new ActiveMeasurePersistenceCommandInput();
        tobePersisted.setRunId(-1); // Indicator to not persist Run/RunSettings
        tobePersisted.setAccountId(configuration.getAccountId());
        tobePersisted.setRunDate(new Date());
        tobePersisted.setRunStatus(Status.Complete.name());
        tobePersisted.setRunMode("BT");

        List<ActiveMeasureRunSettingDVO> runSettingDvos = new ArrayList<ActiveMeasureRunSettingDVO>();
        ActiveMeasureRunSettingDVO runSettingDvo = new ActiveMeasureRunSettingDVO();
        MemberActiveMeasureDVO memberDvo = new MemberActiveMeasureDVO();
        runSettingDvo.getMemberActiveMeasures().add(memberDvo);
        runSettingDvo
                .setActiveMeasureRunSetting(new DefaultActiveMeasureRunSetting());
        runSettingDvos.add(runSettingDvo);
        for (ActiveMeasure measure : actualOutput.getActiveMeasures()) {
            memberDvo.addActiveMeasure(measure);
        }

        tobePersisted.setRunSettingResults(runSettingDvos);
        ACTIVE_MEASURE_PERSISTENCE_COMMAND.execute(tobePersisted,
                new ActiveMeasurePersistenceCommandOutput(),
                new ActiveMeasurePersistenceCommandConfiguration());
    }

    protected void addToAggregatedRuleCounter(String key, int count) {
        if (AGGREGATED_RULE_COUNTER.containsKey(key)) {
            AGGREGATED_RULE_COUNTER.put(key, AGGREGATED_RULE_COUNTER.get(key)
                    + count);
        } else {
            AGGREGATED_RULE_COUNTER.put(key, count);
        }
    }

    public static Collection<Object[]> createParameters(String[] sourceFileNames) throws Exception {
        List<Object[]> result = new LinkedList<Object[]>();
        
        ActiveMeasureTestCaseReader reader;
        for (String sourceFileName: sourceFileNames){
	        reader = new ActiveMeasureTestCaseReader(sourceFileName);
	
	        Collection<ActiveMeasureTestCase> testcases = reader.getTestData()
	                .values();
	        final AtomicInteger testcaseCount = new AtomicInteger(testcases.size());
	
	        for (ActiveMeasureTestCase testcase : testcases) {
	            result.add(new Object[] { testcase, testcase.getTestCaseID(),
	                    testcaseCount, sourceFileName.substring(sourceFileName.lastIndexOf('/')+1) });
	        }
        }
        return result;
    }


    /**
     * @param testCase
     * @param testCaseId
     *            This value is used solely for numbering a run.
     * @param writer
     *            A possibly null writer for the justifications.
     * @param testCasesLeft
     *            An AtomicInteger that shows the number of testCases left to be
     * 
     * @see #createParameters(String, String)
     */
    public BaseParameterizedActiveMeasureEngineTest(
            ActiveMeasureTestCase testCase, Integer testCaseId,
            AtomicInteger testCasesLeft, String fileName) {
        this.testCase = testCase;
    }
}
package net.ahm.careengine.activemeasure.qualitymeasure;

import static org.junit.Assert.assertNotEquals;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

import net.ahm.careengine.activemeasure.TestUtility;
import net.ahm.careengine.testframework.TestCoverageAggregator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class QualityMeasureEngineCoverageTest {
    private static final Integer ZERO = new Integer(0);

    private final Integer        executionCount;
    private final String         ruleName;

    public QualityMeasureEngineCoverageTest(String ruleName,
            Integer executionCount) {
        this.executionCount = executionCount;
        this.ruleName = ruleName;
    }

    @Test
    public void testIfRuleWasFired() {
        assertNotEquals("The Rule " + ruleName + " was not tested", ZERO,
                executionCount);
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() throws Exception {
        Collection<Object[]> results = new TreeSet<Object[]>(
                new ParameterSorter());

        if (!TestUtility.runActiveMeasuresCoverageTest()) {
            Object[] dummyResult = { "dummy rule", -1 };
            results.add(dummyResult);
            return results;
        }

        TestCoverageAggregator aggregator = new TestCoverageAggregator();
        Map<String, Integer> ruleExecutions = aggregator.aggregateCoverage(
                "final-coverage-result.csv", "target/covered-rules.txt");


        for (Map.Entry<String, Integer> entry : ruleExecutions.entrySet()) {
            results.add(new Object[] { entry.getKey(), entry.getValue() });
        }

        return results;
    }

    private static class ParameterSorter implements Comparator<Object[]> {
        @Override
        public int compare(Object[] o1, Object[] o2) {
            return String.CASE_INSENSITIVE_ORDER.compare((String) o1[0],
                    (String) o2[0]);
        }
    }
}

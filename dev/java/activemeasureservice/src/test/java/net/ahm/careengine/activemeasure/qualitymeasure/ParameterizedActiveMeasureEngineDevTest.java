package net.ahm.careengine.activemeasure.qualitymeasure;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import net.ahm.careengine.activemeasure.ActiveMeasureType;
import net.ahm.careengine.testframework.activemeasure.ActiveMeasureTestCase;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ParameterizedActiveMeasureEngineDevTest extends
        BaseParameterizedActiveMeasureEngineTest {
    private static final boolean OVERRIDE_ENGINE_CONFIGURATION = false;

    @Parameters(name = "File {3} - test case {1}")
    public static Collection<Object[]> data() throws Exception {
    	String[] files = {"target" + File.separatorChar + "test-classes"
                + File.separatorChar +"qm-test-data.xlsx"};
        return createParameters(files);
    }

    public ParameterizedActiveMeasureEngineDevTest(
            ActiveMeasureTestCase testCase, Integer testCaseId,
            AtomicInteger atomicIntger,  String fileName) {
        super(testCase, testCaseId, atomicIntger, fileName);
    }

    @Override
    public boolean isPersisting() {
        return false;
    }

    @Override
    protected QualityMeasureTestEngine getEngine() {
        if (OVERRIDE_ENGINE_CONFIGURATION) {
            return new QualityMeasureTestEngine(
                    "http://192.168.4.112:8090/guvnor-5.5.0.Final-jboss-as-7.0/rest/packages",
                    QualityMeasureTestEngine.ALL_LATEST,
                    "net.ahm.activemeasure.", ActiveMeasureType.ALL_TYPES);
        } else {
            return super.getEngine();
        }
    }
}
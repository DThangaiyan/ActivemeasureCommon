package net.ahm.careengine.command.activemeasure;

import static org.junit.Assert.assertNotNull;
import net.ahm.careengine.activemeasure.ActiveMeasurePersistenceCommandConfiguration;
import net.ahm.careengine.activemeasure.ActiveMeasurePersistenceCommandInput;
import net.ahm.careengine.activemeasure.ActiveMeasurePersistenceCommandOutput;
import net.ahm.careengine.dao.output.activemeasure.ActiveMeasureOutputDAO;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = "/META-INF/activemeasurebeans.xml")
public class ActiveMeasurePersistenceCommandTest extends
        AbstractJUnit4SpringContextTests {
    @Autowired
    private ActiveMeasurePersistenceCommand activeMeasurePersistenceCommand;

    @Autowired
    private ActiveMeasureOutputDAO activeMeasureOutputDAO;

    @Test
    public void testCommandNotNull() throws Exception {
        assertNotNull(
                "Spring should have initialized the command but it did not.",
                activeMeasurePersistenceCommand);
    }

    @Test
    public void testDAONotNull() throws Exception {
        assertNotNull("Spring should have initialized the DAO but it did not.",
                activeMeasureOutputDAO);
    }

    /**
     * This test is to make sure we do not run into any exceptions such as NPE
     * before executing the SQL. However because we are passing empty objects,
     * the data integrity violation is expected.
     * 
     * @throws Exception
     */
    @Test(expected = DataIntegrityViolationException.class)
    @Ignore
    public void testPersisterInitialization() throws Exception {
        activeMeasurePersistenceCommand.execute(
                new ActiveMeasurePersistenceCommandInput(),
                new ActiveMeasurePersistenceCommandOutput(),
                new ActiveMeasurePersistenceCommandConfiguration());
    }
}

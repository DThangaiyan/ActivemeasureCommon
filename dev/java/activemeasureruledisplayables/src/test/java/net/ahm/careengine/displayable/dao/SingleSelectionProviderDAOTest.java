package net.ahm.careengine.displayable.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import net.ahm.rulesapp.displayables.type.SingleSelection;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = "/META-INF/amRulesDisplayablesBeans.xml")
public class SingleSelectionProviderDAOTest extends
        AbstractJUnit4SpringContextTests {
    private static final Logger      LOGGER = Logger.getLogger(SingleSelectionProviderDAOTest.class);

    @Autowired
    private SingleSelectionProviderDAO dao;

    @Test
    public void testSetup() {
        assertNotNull(dao);
    }

    @Test
    public void testGetMeasureIds() {
        List<SingleSelection> measures = dao.getMeasureIds();

        assertNotNull(measures);
        assertFalse(measures.isEmpty());
        LOGGER.debug(measures);
    }
}

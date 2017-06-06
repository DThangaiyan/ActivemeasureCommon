package net.ahm.careengine.displayable.json;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import net.ahm.activemeasure.templates.util.ActiveMeasureGlobalContainer;
import net.ahm.careengine.displayable.io.JsonFileUtil;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ActiveMeasureGlobalContainerTest {
    private final ObjectMapper  mapper          = DisplayableJsonMapper
                                                        .getJsonObjectMapper();
    private static final String DESTINATION_DIR = "src/main/resources/globals";

    @Test
    public void testJson() throws Exception {
        ActiveMeasureGlobalContainer container = ActiveMeasureGlobalContainer.INSTANCE;
        String jsonString = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(container);

        assertNotNull(jsonString);
        assertFalse(StringUtils.isBlank(jsonString));

        JsonFileUtil.saveAsFile(jsonString, "activeMeasuresGlobals",
                DESTINATION_DIR);
    }
}

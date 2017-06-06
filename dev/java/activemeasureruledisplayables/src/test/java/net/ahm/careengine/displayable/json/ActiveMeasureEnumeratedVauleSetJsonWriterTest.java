package net.ahm.careengine.displayable.json;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Map.Entry;

import net.ahm.careengine.displayable.ActiveMeasuresSingleEnumeratedValueSetBuilder;
import net.ahm.careengine.displayable.CareEngineEnumeratedValueSetBuilderContext;
import net.ahm.careengine.displayable.io.JsonFileUtil;
import net.ahm.rulesapp.displayables.type.EnumeratedValueSet;
import net.ahm.rulesapp.displayables.type.EnumeratedValueSetBuilder;
import net.ahm.rulesapp.displayables.type.json.EnumeratedVauleSetJsonWriter;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = "/META-INF/amRulesDisplayablesBeans.xml")
public class ActiveMeasureEnumeratedVauleSetJsonWriterTest extends
        AbstractJUnit4SpringContextTests {
    private static final String                           DESTINATION_DIR = "src/main/resources/enumdefinitions";

    @Autowired
    private CareEngineEnumeratedValueSetBuilderContext ctx;

    private EnumeratedVauleSetJsonWriter createEnumeratedVauleSetJsonWriter() {
        return new EnumeratedVauleSetJsonWriter(
                DisplayableJsonMapper.getJsonObjectMapper(),
                new EnumeratedValueSetBuilder<>(
                        ActiveMeasuresSingleEnumeratedValueSetBuilder.ALL_BUILDERS,
                        ctx));
    }

    @Test
    public void testContextCreated() {
        assertNotNull(ctx);
    }

    @Test
    public void testCreateAllEnumeratedValueSetsJson() throws Exception {
        Map<EnumeratedValueSet, String> jsonMap = createEnumeratedVauleSetJsonWriter()
                .createAllEnumeratedValueSetsJson();

        assertNotNull(jsonMap);

        for (Entry<EnumeratedValueSet, String> entry : jsonMap.entrySet()) {
            final EnumeratedValueSet valueSet = entry.getKey();
            assertNotNull(valueSet);
            final String jsonValue = entry.getValue();
            assertFalse(StringUtils.isBlank(jsonValue));

            assertTrue(jsonValue.contains(valueSet.getName()));

            JsonFileUtil.saveAsFile(jsonValue, valueSet.getName(),
                    DESTINATION_DIR);
        }
    }
}


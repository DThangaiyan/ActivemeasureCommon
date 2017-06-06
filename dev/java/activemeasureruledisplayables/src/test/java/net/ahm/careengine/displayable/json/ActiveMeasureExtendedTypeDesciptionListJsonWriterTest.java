package net.ahm.careengine.displayable.json;

import static org.junit.Assert.assertFalse;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import net.ahm.careengine.displayable.ActiveMeasureModelInfo;
import net.ahm.careengine.displayable.ActiveMeasuresSingleEnumeratedValueSetBuilder;
import net.ahm.careengine.displayable.CareEngineEnumeratedValueSetBuilderContext;
import net.ahm.careengine.displayable.dao.SingleSelectionProviderDAO;
import net.ahm.careengine.displayable.io.JsonFileUtil;
import net.ahm.rulesapp.displayables.type.AttributeMapper;
import net.ahm.rulesapp.displayables.type.EnumeratedValueSet;
import net.ahm.rulesapp.displayables.type.EnumeratedValueSetBuilder;
import net.ahm.rulesapp.displayables.type.ExtendedTypeDesciptionBuilder;
import net.ahm.rulesapp.displayables.type.json.ExtendedTypeDesciptionListJsonWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class ActiveMeasureExtendedTypeDesciptionListJsonWriterTest {
    static final Logger                                LOGGER          = Logger.getLogger(ActiveMeasureExtendedTypeDesciptionListJsonWriterTest.class);
    public static final String                         DESTINATION_DIR = "src/main/resources/typedefinitions";
    static final String                                FINAL_FILE_NAME = "activeMeasuresRulesTypes";

    private final ExtendedTypeDesciptionListJsonWriter writer          = new ExtendedTypeDesciptionListJsonWriter(
                                                                               new ExtendedTypeDesciptionBuilder(),
                                                                               DisplayableJsonMapper
                                                                                       .getJsonObjectMapper());
    private final EnumeratedValueSetBuilder<?, ?>      enumBuilder;

    public ActiveMeasureExtendedTypeDesciptionListJsonWriterTest() {
        CareEngineEnumeratedValueSetBuilderContext ctx = new CareEngineEnumeratedValueSetBuilderContext();
        SingleSelectionProviderDAO dao = new SingleSelectionProviderDAO();
        dao.setJdbcTemplate(new MockJdbcTemplate());
        ctx.setDao(dao);
        enumBuilder = new EnumeratedValueSetBuilder<>(
                EnumSet.allOf(ActiveMeasuresSingleEnumeratedValueSetBuilder.class),
                ctx);
    }

    @Test
    public void testBuildJson() throws Exception {
        List<EnumeratedValueSet> enumeratrionValues = enumBuilder
                .createAllEnumeratedValueSets();
        AttributeMapper mapper = new AttributeMapper(enumeratrionValues);
        String jsonString = writer.generateJson(
                ActiveMeasureModelInfo.ACTIVE_MEASURES_ROOT_CLASS_SET,
                ActiveMeasureModelInfo.STATIC_UTIL_CLASSES,
                ActiveMeasureModelInfo.ADDITIONAL_CLASSES_USED, mapper);

        assertFalse(StringUtils.isBlank(jsonString));

        JsonFileUtil.saveAsFile(jsonString, FINAL_FILE_NAME, DESTINATION_DIR);
    }

    private static class MockJdbcTemplate extends JdbcTemplate {
        @Override
        public <T> List<T> query(String sql, RowMapper<T> rowMapper)
                throws DataAccessException {
            return Collections.emptyList();
        }
    }
}

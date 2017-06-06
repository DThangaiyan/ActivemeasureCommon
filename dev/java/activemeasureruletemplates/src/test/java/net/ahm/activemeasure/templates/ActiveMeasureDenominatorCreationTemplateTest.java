package net.ahm.activemeasure.templates;

import static net.ahm.rulesapp.util.ExecutableExpressionsIF.NEW_LINE_CHAR;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import net.ahm.rulesapp.templates.libraries.BaseTemplateTest;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.util.DroolsExpressionContext;
import net.ahm.rulesapp.util.ExecutableExpressionsIF;

import org.junit.Test;

public class ActiveMeasureDenominatorCreationTemplateTest extends BaseTemplateTest {
    private static final long   MEASURE_ID     = 125L;
    private static final String START_DATE_VAR = "$startDate";
    private static final String END_DATE_VAR   = "$endDate";

    @Override
    public ActiveMeasureDenominatorCreationTemplate getTemplateInstance() {
        return new ActiveMeasureDenominatorCreationTemplate(MEASURE_ID);
    }

    @Override
    protected void additionalAssertionsOnExecutableExpressions(
            String droolsExpression) {
        assertEquals(
                "ActiveMeasure mam1 = activeMeasureBuilder.newActiveMeasure( "
                        + MEASURE_ID + " );" + NEW_LINE_CHAR
                        + "insert(mam1)", droolsExpression);
    }

    @Test
    public void testGetExecutableExpressions_withStartDate() throws Exception {
        final ActiveMeasureDenominatorCreationTemplate template = getTemplateInstance();
        template.setAlternateStartDate(new NamedVariableLiteralFragmentTemplate(
                START_DATE_VAR, Date.class));
        ExecutableExpressionsIF expressions = template
                .getExecutableExpressions();

        assertEquals(
                "ActiveMeasure mam1 = activeMeasureBuilder.newActiveMeasureWithAlternateStartDate( "
                        + MEASURE_ID
                        + " , "
                        + START_DATE_VAR
                        + " );"
                        + NEW_LINE_CHAR + "insert(mam1)",
                expressions.getDroolsExpression(new DroolsExpressionContext()));
    }

    @Test
    public void testGetExecutableExpressions_withEndDate() throws Exception {
        final ActiveMeasureDenominatorCreationTemplate template = getTemplateInstance();
        template.setAlternateEndDate(new NamedVariableLiteralFragmentTemplate(
                END_DATE_VAR, Date.class));
        ExecutableExpressionsIF expressions = template
                .getExecutableExpressions();

        assertEquals(
                "ActiveMeasure mam1 = activeMeasureBuilder.newActiveMeasureWithAlternateEndDate( "
                        + MEASURE_ID
                        + " , "
                        + END_DATE_VAR
                        + " );"
                        + NEW_LINE_CHAR + "insert(mam1)",
                expressions.getDroolsExpression(new DroolsExpressionContext()));
    }

    @Test
    public void testGetExecutableExpressions_withBothDates() throws Exception {
        final ActiveMeasureDenominatorCreationTemplate template = getTemplateInstance();
        template.setAlternateStartDate(new NamedVariableLiteralFragmentTemplate(
                START_DATE_VAR, Date.class));
        template.setAlternateEndDate(new NamedVariableLiteralFragmentTemplate(
                END_DATE_VAR, Date.class));
        ExecutableExpressionsIF expressions = template
                .getExecutableExpressions();

        assertEquals(
                "ActiveMeasure mam1 = activeMeasureBuilder.newActiveMeasureWithAlternateStartAndEndDate( "
                        + MEASURE_ID
                        + " , "
                        + START_DATE_VAR
                        + " , "
                        + END_DATE_VAR + " );" + NEW_LINE_CHAR + "insert(mam1)",
                expressions.getDroolsExpression(new DroolsExpressionContext()));
    }
}

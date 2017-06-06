package net.ahm.activemeasure.templates;

import static org.junit.Assert.assertEquals;
import net.ahm.careengine.domain.temporal.ContiguousDaysExpressable;
import net.ahm.careengine.ruleengine.DateTimeUnit;
import net.ahm.rulesapp.templates.libraries.BaseTemplateTest;
import net.ahm.rulesapp.util.UtilFunctions;

public class ContiguousDaysPaddingTemplateTest extends BaseTemplateTest {
    private static final String       RETURN_VAR_NAME   = "$paddedEvent";
    private static final String       INITIAL_VAR_NAME  = "$event";
    private static final int          START_PUSH_AMOUNT = 12;
    private static final DateTimeUnit START_PUSH_UNIT   = DateTimeUnit.MONTH;
    private static final int          END_PUSH_AMOUNT   = 7;
    private static final DateTimeUnit END_PUSH_UNIT     = DateTimeUnit.DAY;

    @Override
    public ContiguousDaysPaddingTemplate getTemplateInstance() {
        ContiguousDaysPaddingTemplate template = new ContiguousDaysPaddingTemplate(
                UtilFunctions
                        .createNamedVariableLiteralFragmentTemplate(
                                INITIAL_VAR_NAME,
                                ContiguousDaysExpressable.class, null),
                UtilFunctions
                        .createIntegerLiteralFragmentTemplate(START_PUSH_AMOUNT),
                UtilFunctions.createEnumLiteralExpressionFragmentTemplate(
                        DateTimeUnit.class, START_PUSH_UNIT), UtilFunctions
                        .createIntegerLiteralFragmentTemplate(END_PUSH_AMOUNT),
                UtilFunctions.createEnumLiteralExpressionFragmentTemplate(
                        DateTimeUnit.class, END_PUSH_UNIT));
        template.setVariableName(RETURN_VAR_NAME);
        return template;
    }

    @Override
    protected void additionalAssertionsOnExecutableExpressions(
            String droolsExpression) {
        assertEquals(
                RETURN_VAR_NAME
                        + " : ContiguousDays() from GlobalFunctions.getContiguousDaysWithNewDates( "
                        + INITIAL_VAR_NAME + ", " + START_PUSH_AMOUNT
                        + ", DateTimeUnit." + START_PUSH_UNIT + ", "
                        + END_PUSH_AMOUNT + ", DateTimeUnit." + END_PUSH_UNIT
                        + " )", droolsExpression);
    }
}

package net.ahm.activemeasure.templates;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import net.ahm.careengine.ruleengine.DateTimeUnit;
import net.ahm.rulesapp.templates.libraries.BaseTemplateTest;
import net.ahm.rulesapp.templates.libraries.DateShiftFunctionTemplate;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.util.DateShiftFunction;
import net.ahm.rulesapp.util.UtilFunctions;

public class DateDefinitionTemplateTest extends BaseTemplateTest {
    private static final String VAR_NAME = "$dateName";

    @Override
    public DateDefinitionTemplate getTemplateInstance() {
        DateDefinitionTemplate template = new DateDefinitionTemplate(
                new DateShiftFunctionTemplate(
                        new NamedVariableLiteralFragmentTemplate(
                                "measurementEndDate", Date.class),
                        DateShiftFunction.GET_EARLIER_DATE,
                        UtilFunctions.createIntegerLiteralFragmentTemplate(30),
                        UtilFunctions
                                .createEnumLiteralExpressionFragmentTemplate(
                                        DateTimeUnit.class, DateTimeUnit.YEAR)));
        template.setVariableName(VAR_NAME);
        return template;
    }

    @Override
    protected void additionalAssertionsOnExecutableExpressions(
            String droolsExpression) {
        assertEquals(
                VAR_NAME
                        + " : java.util.Date() from BaseCommonGlobalFunctions.getEarlierDate( measurementEndDate, 30, DateTimeUnit.YEAR )",
                droolsExpression);

    }
}

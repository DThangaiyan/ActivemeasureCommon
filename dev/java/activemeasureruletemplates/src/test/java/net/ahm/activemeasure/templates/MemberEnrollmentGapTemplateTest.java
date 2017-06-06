package net.ahm.activemeasure.templates;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.interfaces.ExecutableTemplate;
import net.ahm.rulesapp.templates.libraries.BaseTemplateTest;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.util.ExecutableExpressionsIF;

/**
 * Test case for {@link MemberEnrollmentGapTemplate}
 * 
 * @author xsu
 * 
 */
public class MemberEnrollmentGapTemplateTest extends BaseTemplateTest {

    @Override
    protected void additionalAssertionsOnExecutableExpressions(
            String droolsExpression) {
        assertEquals(
                "MemberInfo ( $initialGapDays1 : enrolmentGapDays != null )"
                        + ExecutableExpressionsIF.NEW_LINE_CHAR
                        + "Collection ( size >= 2 ) from collect ( ContiguousDays ( durationInDays >= 1 ) from GlobalFunctions.filterWholeDays($initialGapDays1, $startDate, $endDate ) )",
                droolsExpression);
    }

    @Override
    public ExecutableTemplate getTemplateInstance() {
        MemberEnrollmentGapTemplate instance = new MemberEnrollmentGapTemplate(
                new NamedVariableLiteralFragmentTemplate("$startDate",
                        Date.class), new NamedVariableLiteralFragmentTemplate(
                        "$endDate", Date.class), 2,
                CommonOperators.GREATER_THAN_OR_EQUAL_TO, 1,
                CommonOperators.GREATER_THAN_OR_EQUAL_TO);
		return instance;
	}
}

package net.ahm.activemeasure.templates;

import static org.junit.Assert.assertEquals;
import net.ahm.activemeasure.templates.util.CoverageType;
import net.ahm.careengine.domain.event.drug.DrugEvent;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.libraries.BaseTemplateTest;
import net.ahm.rulesapp.util.DroolsExpressionContext;
import net.ahm.rulesapp.util.ExecutableExpressionsIF;
import net.ahm.rulesapp.util.Operator;
import net.ahm.rulesapp.util.UtilFunctions;

public class CoverageOvelappingContiguousDaysExpressableTest extends
        BaseTemplateTest {

    private static final Operator EXPECTED_SIZE_OPERATOR  = CommonOperators.LESS_THAN_OR_EQUAL_TO;
    private static final Operator EXPECTED_COUNT_OPERATOR = CommonOperators.GREATER_THAN_OR_EQUAL_TO;
    private static final int      EXPECTED_SIZE           = 2;
    private static final int      EXPCTED_COUNT           = 1;
    private static final String   VAR_NAME                = "$event";
    private static final String   EXPECTED_DROOLS_STRING;

    static {
        DroolsExpressionContext ctx = new DroolsExpressionContext();
        EXPECTED_DROOLS_STRING = "MemberInfo ( $initialGapDays1 : enrolmentGapDays )"
                + ExecutableExpressionsIF.NEW_LINE_CHAR
                + "Collection ( size "
                + EXPECTED_COUNT_OPERATOR.getDroolsExpression(ctx)
                + " "
                + EXPCTED_COUNT
                + " ) from collect ( ContiguousDays ( durationInDays "
                + EXPECTED_SIZE_OPERATOR.getDroolsExpression(ctx)
                + " "
                + EXPECTED_SIZE
                + " ) from GlobalFunctions.allIntersectingButNotAdjacent( $initialGapDays1, "
                + VAR_NAME + " ) )";
    }

    @Override
    public CoverageOvelappingContiguousDaysExpressable getTemplateInstance() {
        return new CoverageOvelappingContiguousDaysExpressable(
                CoverageType.MEDICAL, EXPECTED_COUNT_OPERATOR, EXPCTED_COUNT,
                EXPECTED_SIZE_OPERATOR, EXPECTED_SIZE,
                UtilFunctions.createNamedVariableLiteralFragmentTemplate(
                        VAR_NAME, DrugEvent.class, null));
    }

    @Override
    protected void additionalAssertionsOnExecutableExpressions(
            String droolsExpression) {
        assertEquals(EXPECTED_DROOLS_STRING, droolsExpression);
    }
}

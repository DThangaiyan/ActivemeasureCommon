package net.ahm.activemeasure.templates;

import static org.junit.Assert.assertEquals;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.BaseTemplateTest;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.DroolsExpressionContext;
import net.ahm.rulesapp.util.ExecutableExpressionsIF;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Test case for {@link FactLevelMeasureDenominatorCreationTemplate}
 * 
 * @author xsu
 * 
 */
public class FactLevelMeasureDenominatorCreationTemplateTest extends
        BaseTemplateTest {
    private static final Logger logger = Logger.getLogger(FactLevelMeasureDenominatorCreationTemplateTest.class);

    @Test
    public void testDroolsWithoutPrimaryFact() throws Exception {
        FactLevelMeasureDenominatorCreationTemplate instance = getTemplateInstance();
        instance.setMeasureId(125);
        String droolsExpression = instance.getExecutableExpressions()
                .getDroolsExpression(new DroolsExpressionContext());
        logger.debug("Drools expression: " + droolsExpression);
        assertEquals(
                "FactLevelMeasureDenominator $factLevelDenominator1 = factLevelMeasureBuilder.newFactLevelMeasureDenominator(125);"
                        + ExecutableExpressionsIF.NEW_LINE_CHAR
                        + "insert($factLevelDenominator1);",
                droolsExpression);
    }

    @Override
    @Test
    public void testGetExecutableExpressions() throws Exception {
        FactLevelMeasureDenominatorCreationTemplate instance = getTemplateInstance();
        instance.setMeasureId(125);
        AnyClassMultipleAttributeEvaluationFragmentTemplate claimHdrClassTemplate = new AnyClassMultipleAttributeEvaluationFragmentTemplate();
        claimHdrClassTemplate.setVariableName("$claimHdrPrimaryFact");
        claimHdrClassTemplate
                .setExpressionReturnTypeDescription(TypeDescription
                        .getTypeDescription(ClaimHeader.class));
        instance.setPrimaryFactTemplate(claimHdrClassTemplate
                .getVariableExpression());
        String droolsExpression = instance.getExecutableExpressions()
                .getDroolsExpression(new DroolsExpressionContext());
        logger.debug("Drools expression: " + droolsExpression);
        assertEquals(
                "FactLevelMeasureDenominator $factLevelDenominator1 = factLevelMeasureBuilder.newFactLevelMeasureDenominatorWithPrimaryFact(125, $claimHdrPrimaryFact);"
                        + ExecutableExpressionsIF.NEW_LINE_CHAR
                        + "insert($factLevelDenominator1);",
                droolsExpression);
    }

    @Override
    public FactLevelMeasureDenominatorCreationTemplate getTemplateInstance() {
        FactLevelMeasureDenominatorCreationTemplate instance = new FactLevelMeasureDenominatorCreationTemplate();
        return instance;
    }

    @Override
    protected void additionalAssertionsOnExecutableExpressions(
            String droolsExpression) {
        // No extra actions
    }
}

package net.ahm.activemeasure.templates;

import static org.junit.Assert.assertEquals;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.BaseTemplateTest;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

import org.apache.log4j.Logger;

/**
 * Test case for {@link CreateNumeratorForDenominatorTemplate}
 * 
 * @author xsu
 * 
 */
public class FactLevelMeasureNumeratorCreationTemplateTest extends
        BaseTemplateTest {
    private static final Logger logger = Logger.getLogger(FactLevelMeasureNumeratorCreationTemplateTest.class);

    @Override
    public FactLevelMeasureNumeratorCreationTemplate getTemplateInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate denomInstance = UtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        "$flmd",
                        UtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                ActiveMeasureUtilConstants.MEASURE_ID,
                                                TypeDescription
                                                        .getTypeDescription(Long.TYPE)),
                                        UtilFunctions
                                                .createIntegerLiteralFragmentTemplate(138),
                                        CommonOperators.EQUAL_TO, null));

        AnyClassMultipleAttributeEvaluationFragmentTemplate claimHdrClassTemplate = new AnyClassMultipleAttributeEvaluationFragmentTemplate();
        claimHdrClassTemplate.setVariableName("$claimHdrPrimaryFact");
        claimHdrClassTemplate
                .setExpressionReturnTypeDescription(TypeDescription
                        .getTypeDescription(ClaimHeader.class));
        FactLevelMeasureNumeratorCreationTemplate numInstance = new FactLevelMeasureNumeratorCreationTemplate(
                denomInstance.getVariableExpression(),
                claimHdrClassTemplate.getVariableExpression());
        return numInstance;
    }

    @Override
    protected void additionalAssertionsOnExecutableExpressions(
            String droolsExpression) {
        assertEquals(
                "FactLevelMeasureNumerator $factLevelNumerator1 = $flmd.createFactLevelMeasureNumeratorWithPrimaryFact($claimHdrPrimaryFact);\n"
                        + "insert($factLevelNumerator1);", droolsExpression);
        logger.debug("Drools expression:\n" + droolsExpression);
    }
}

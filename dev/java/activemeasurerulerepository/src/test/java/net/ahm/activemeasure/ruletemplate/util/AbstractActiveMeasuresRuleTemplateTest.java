package net.ahm.activemeasure.ruletemplate.util;

import net.ahm.activemeasure.ruletemplate.json.ActiveMeasureJSONMapper;
import net.ahm.rulesapp.json.RulesAppRepositoryJsonMapper;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.AbstractRuleTemplateTest;
import net.ahm.rulesapp.util.TemplateRegistry;

public abstract class AbstractActiveMeasuresRuleTemplateTest extends
        AbstractRuleTemplateTest {
    private static final TemplateRegistry REGISTRY = TemplateRegistry
                                                           .getInstance("amtemplatemap,templatemap,templatemap_test");

    @Override
    public TemplateRegistry getTemplateRegistry() {
        return REGISTRY;
    }

    @Override
    protected RulesAppRepositoryJsonMapper getJSONMapper() {
        return ActiveMeasureJSONMapper.getInstance();
    }

    protected static AnyClassMultipleAttributeEvaluationFragmentTemplate createCondition(
            Class<?> classPattern, String variableName) {
        AnyClassMultipleAttributeEvaluationFragmentTemplate anyClassMultipleAttributeEvaluationFragmentInstance = new AnyClassMultipleAttributeEvaluationFragmentTemplate();
        anyClassMultipleAttributeEvaluationFragmentInstance
                .setVariableName(variableName);
        anyClassMultipleAttributeEvaluationFragmentInstance
                .setExpressionReturnTypeDescription(TypeDescription
                        .getTypeDescription(classPattern));
        anyClassMultipleAttributeEvaluationFragmentInstance
                .getMultipleAttributeEvaluationFragments().setConnector(
                        Connector.COMMA);
        return anyClassMultipleAttributeEvaluationFragmentInstance;
    }
}

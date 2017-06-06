package net.ahm.careengine.activemeasure.parsing;

import net.ahm.careengine.activemeasure.ActiveMeasureCommandConfiguration;
import net.ahm.careengine.activemeasure.ActiveMeasureCommandInput;
import net.ahm.careengine.activemeasure.ActiveMeasureCommandOutput;
import net.ahm.careengine.activemeasure.BaseActiveMeasureEngine;
import net.ahm.careengine.activemeasure.rule.ActiveMeasureRuleSet;
import net.ahm.careengine.activemeasure.rule.RuleDescription;
import net.ahm.careengine.activemeasure.rule.RuleType;
import net.ahm.careengine.eventprocessing.engine.drools.parser.AbstractRulesParser;
import net.ahm.careengine.eventprocessing.engine.drools.parser.RuleParseCommandIF;

public class RuleParser
        extends
        AbstractRulesParser<RuleType, RuleDescription, ActiveMeasureCommandInput, ActiveMeasureCommandOutput, ActiveMeasureCommandConfiguration, BaseActiveMeasureEngine> {

    public RuleParser(BaseActiveMeasureEngine engine) {
        super(engine);
    }

    public RuleParser() {
        this(BaseActiveMeasureEngine.getInstance());
    }

    @Override
    protected RuleParseCommandIF<? super RuleType, ? super RuleDescription>[] getRuleParseCommands() {
        return RuleParseCommand.values();
    }

    @Override
    protected RuleDescription createRuleDescription(String ruleName,
            String packageName) {
        return new RuleDescription(ruleName, packageName);
    }

    public ActiveMeasureRuleSet getActiveMeasureRuleSet() throws Exception {
        return new ActiveMeasureRuleSet(getAllRuleDescriptions());
    }
}

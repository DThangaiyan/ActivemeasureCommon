package net.ahm.careengine.activemeasure.parsing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ahm.careengine.activemeasure.rule.ActiveMeasureRuleSet;
import net.ahm.careengine.activemeasure.rule.RelatedRules;
import net.ahm.careengine.activemeasure.rule.RuleDescription;
import net.ahm.careengine.activemeasure.rule.RuleType;
import net.ahm.careengine.eventprocessing.engine.drools.rule.RuleTypeIF;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

public class RuleValidationTest {
    static final Logger          LOGGER                                  = Logger.getLogger(RuleParserTest.class);
    static final long            EVENT_FILTER_RULE_MIN_SALIENCE          = 1000;
    static final long            CLASSIFIER_RULE_CLASSIFIER_MIN_SALIENCE = 100;
    private RuleParser           parser                                  = new RuleParser();
    private ActiveMeasureRuleSet ruleSet;
    private static Set<String> exclusion = new HashSet<String>(Arrays.asList("Retract_HCC"));

    @Test
    public void testEventFilterRuleMinimumSalience() throws Exception {
        Collection<RuleDescription> allRules = getAllRules();

        assertFalse("The collection should not be empty", allRules.isEmpty());
        assertMinimumSalience(allRules, RuleType.EVENT_FILTER,
                EVENT_FILTER_RULE_MIN_SALIENCE);
    }

    private Collection<RuleDescription> getAllRules() throws Exception {
        ActiveMeasureRuleSet ruleSet = getActiveMeasureRuleSet();
        return ruleSet.getAllRules();
    }

    @Test
    public void testClassifierRuleMinimumSalience() throws Exception {
        Collection<RuleDescription> allRules = getAllRules();

        assertFalse("The collection should not be empty", allRules.isEmpty());
        assertMinimumSalience(allRules, RuleType.CLASSIFIER,
                CLASSIFIER_RULE_CLASSIFIER_MIN_SALIENCE);
    }

    @Test
    public void testChainedClassifierSalience() throws Exception {
        ActiveMeasureRuleSet ruleSet = getActiveMeasureRuleSet();
        Map<Long, RelatedRules> classifierUsingRulesMap = ruleSet
                .getClassifierUsingRulesById();

        Map<Long, RelatedRules> classifierCreatingRulesMap = ruleSet
                .getClassifierCreationRulesById();

        for (Map.Entry<Long, RelatedRules> entry : classifierUsingRulesMap
                .entrySet()) {
            Long classifierId = entry.getKey();
            RelatedRules singleClassifierCreationRules = classifierCreatingRulesMap
                    .get(classifierId);
            if (singleClassifierCreationRules == null) {
                fail("While there are rules using classifer " + classifierId
                        + ", can't find a related rule to creat the classifier");
            }
            Set<RuleDescription> classiferCreationRulesSet = singleClassifierCreationRules.getRules(RuleType.CLASSIFIER);

            assertFalse(
                    "Could not find rules for the creation of the classifer id:"
                            + classifierId, classiferCreationRulesSet.isEmpty());

            for (RuleDescription classifierUsingRule : entry.getValue()
                    .getAllRules()) {
                for (RuleDescription classifierCreationRule : classiferCreationRulesSet) {
                    if(classifierUsingRule.getRuleType() != RuleType.HCC 
                            && !RuleParserUtil.containsAny(exclusion, classifierUsingRule.getRuleName())){
                        assertMinimumValue(
                                "The salience for the rule "
                                        + classifierCreationRule
                                        + " needs to be higher than "
                                        + classifierUsingRule,
                                classifierUsingRule.getSalience() + 1,
                                classifierCreationRule.getSalience());
                    }
                }
            }
        }
    }

    private ActiveMeasureRuleSet getActiveMeasureRuleSet() throws Exception {
        if (ruleSet == null) {
            ruleSet = parser.getActiveMeasureRuleSet();
        }
        return ruleSet;
    }

    static void assertMinimumSalience(Collection<RuleDescription> allRules,
            RuleTypeIF ruleType, long minimumSalience) {
        for (RuleDescription rule : allRules) {
            if (ruleType.equals(rule.getRuleType())
                    && !RuleParserUtil.containsAny(exclusion,
                            rule.getRuleName())) {
                assertMinimumValue("The salience of the " + ruleType + " rule "
                        + rule.getPackageName() + rule.getRuleName()
                        + " was not correct",
                        minimumSalience, rule.getSalience());
            }
        }
    }

    static void assertMinimumValue(String errorMessage, long minimumValue,
            long actualValue) {
        if (actualValue < minimumValue) {
            StringBuilder sb = new StringBuilder();
            boolean hasErrorMessage = StringUtils.isNotEmpty(errorMessage);
            if (hasErrorMessage) {
                sb.append(errorMessage);
                sb.append('[');
            }

            sb.append("The actual value ").append(actualValue);
            sb.append(" was less than ").append(minimumValue);

            if (hasErrorMessage) {
                sb.append(']');
            }
            fail(sb.toString());
        }
    }

    @Test
    public void testParentRuleType() throws Exception {
        Collection<RuleDescription> allRules = getAllRules();
        for (RuleDescription rule : allRules) {
            if (rule.hasParentRule()) {
                RuleDescription parentRule = rule.getParent();
                assertFalse("Parent rule type is not set to PARENT!",
                        !parentRule.getRuleType().equals(RuleType.PARENT));
            }
        }
    }
}

package net.ahm.careengine.activemeasure.rule;

import java.util.Collection;
import java.util.Collections;

import net.ahm.careengine.activemeasure.parsing.RuleParseCommand;
import net.ahm.careengine.common.CECollectionsUtil;
import net.ahm.careengine.eventprocessing.engine.drools.rule.RuleTypeIF;
import net.ahm.careengine.eventprocessing.engine.drools.rule.RuleTypeUtility;

public enum RuleType implements RuleTypeIF {
    DENOMINATOR(RuleParseCommand.NEW_DENOMINATOR_ACTIVEMEASURE,
            RuleParseCommand.NEW_DENOMINATOR_FLM_DENOMIATOR_WITH_PRIMARY_FACT,
            RuleParseCommand.NEW_DENOMINATOR_FACT_LEVEL_MEASURE_DENOMIATOR), //
    DENOMINATOR_EXCLUSION("setExcludedFromDenominator"), //
    ELIGIBILITY_EXCLUSION("setEligible"), //
    NUMERATOR("setInNumerator",
            "createFactLevelMeasureNumeratorWithPrimaryFact(",
            "createFactLevelMeasureNumerator(", "setExpectedNumeratorCount"), //
    CLASSIFIER(RuleParseCommand.GET_CLASSIFIER,
            RuleParseCommand.GET_CLASSIFIER_FOR_QUALITYMEASURE), //
    EVENT_FILTER("retract("), //
    UNKNOWN,
    // for CDM rules:
    NUMERATOR_EXCLUSION("setExcludedFromNumerator("), //
    CC(RuleParseCommand.SET_COMORBID_CLINICAL_CONDTION_CATEGORY), //
    HCC(
            RuleParseCommand.GET_HIERARCHICAL_CLINICAL_CONDTION_CATEGORY,
            RuleParseCommand.GET_HIERARCHICAL_CLINICAL_CONDTION_CATEGORY_WITH_MEASURETYPE), //
    PARENT;

    private final Collection<String> matchingDrlStrings;

    private RuleType(String... s) {
        if (s != null) {
            this.matchingDrlStrings = CECollectionsUtil.unmodifiableSet(s);
        } else {
            this.matchingDrlStrings = Collections.emptySet();
        }
    }

    @Override
    public Collection<String> getMatchingDrlStrings() {
        return matchingDrlStrings;
    }

    public static RuleType getRuleTypeFromThenDrlClause(String drlThenClause) {
        return RuleTypeUtility.getRuleTypeFromThenDrlClause(drlThenClause,
                values(), UNKNOWN);
    }
}

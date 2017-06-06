package net.ahm.careengine.activemeasure.parsing;

import net.ahm.careengine.activemeasure.rule.RuleDescription;
import net.ahm.careengine.activemeasure.rule.RuleType;
import net.ahm.careengine.eventprocessing.engine.drools.parser.RuleParseCommandIF;

import org.apache.commons.lang.StringUtils;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.RuleDescr;

public enum RuleParseCommand implements
        RuleParseCommandIF<RuleType, RuleDescription> {
    MEASURE_AND_ELEMENT_AND_CLASSIFIER_IDS {
        @Override
        public void parse(RuleDescr inputRuleDesc,
                RuleDescription outputRuleDescription) {
            AndDescr lhs = inputRuleDesc.getLhs();

            Object consequence = inputRuleDesc.getConsequence();
            String consequenceStr = StringUtils.EMPTY;
            if (consequence instanceof String) {
                consequenceStr = (String) consequence;
            }
            for (WhenType whenType : WhenType.values()) {
                whenType.addToOutputDesc(outputRuleDescription, consequenceStr,
                        lhs);
            }
        }
    },
    RULE_TYPE {
        @Override
        public void parse(RuleDescr inputRuleDesc,
                RuleDescription outputRuleDescription) {
            Object consequence = inputRuleDesc.getConsequence();
            if (consequence instanceof String) {
                outputRuleDescription.setRuleType(RuleType
                        .getRuleTypeFromThenDrlClause((String) consequence));
            }
        }
    };

    public static final String NEW_DENOMINATOR_ACTIVEMEASURE                                   = "newActiveMeasure(";
    public static final String NEW_DENOMINATOR_ACTIVEMEASURE_WITH_ALTERNATE_START_DATE         = "newActiveMeasureWithAlternateStartDate(";
    public static final String NEW_DENOMINATOR_ACTIVEMEASURE_WITH_ALTERNATE_END_DATE           = "newActiveMeasureWithAlternateEndDate(";
    public static final String NEW_DENOMINATOR_ACTIVEMEASURE_WITH_ALTERNATE_START_AND_END_DATE = "newActiveMeasureWithAlternateStartAndEndDate(";

    public static final String NEW_DENOMINATOR_FACT_LEVEL_MEASURE_DENOMIATOR                   = "newFactLevelMeasureDenominator(";

    public static final String NEW_DENOMINATOR_FLM_DENOMIATOR_WITH_PRIMARY_FACT                = "newFactLevelMeasureDenominatorWithPrimaryFact(";
    public static final String GET_CLASSIFIER_FOR_QUALITYMEASURE                               = "getClassifierForQualityMeasure(";
    public static final String GET_HIERARCHICAL_CLINICAL_CONDTION_CATEGORY                     = "getHierarchicalClinicalConditionCategory(";
    public static final String GET_HIERARCHICAL_CLINICAL_CONDTION_CATEGORY_WITH_MEASURETYPE    = "getHierarchicalClinicalConditionCategoryWithMeasureType(";
    public static final String SET_COMORBID_CLINICAL_CONDTION_CATEGORY                         = "setComorbidClinicalConditionId(";
}

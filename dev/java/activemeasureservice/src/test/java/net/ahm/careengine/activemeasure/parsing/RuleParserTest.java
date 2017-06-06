package net.ahm.careengine.activemeasure.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ahm.careengine.activemeasure.BaseActiveMeasureEngine;
import net.ahm.careengine.activemeasure.rule.ActiveMeasureRuleSet;
import net.ahm.careengine.activemeasure.rule.RelatedRules;
import net.ahm.careengine.activemeasure.rule.RuleDescription;
import net.ahm.careengine.activemeasure.rule.RuleType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

public class RuleParserTest {
    private static final String CLASSIFIER = "CLASSIFIER";
    private static final String CC = "Diagnosis_To_CC";
    private static final Set<String> HCC = new HashSet<String>(Arrays.asList("CC_To_HCC", "Comob_HCC"));
    private static final String COMMON_PACKAGE = "net.ahm.activemeasure.qualitymeasures.Common";
    private static final String CDM_BASE_FOLDER = "net.ahm.activemeasure.cdm";
    private static final String MEDICALCASE_BASE_FOLDER = "net.ahm.activemeasure.medicalcase";
    static final Logger         LOGGER          = Logger.getLogger(RuleParserTest.class);

    private final RuleParser          parser          = new RuleParser();

    @Test
    public void testParser() throws Exception {
        ActiveMeasureRuleSet amRulesSet = parser.getActiveMeasureRuleSet();
        Set<RuleDescription> allRules = amRulesSet.getAllRules();

        assertFalse("The collection should not be empty", allRules.isEmpty());

        for (RuleDescription rule : allRules) {

            assertRuleName(rule);
            assertRuleType(rule);
            assertParentSetCorrectly(rule);
            assertMeasureId(rule);
            assertCdmId(rule);
            assertCdmIdForCDMDenomInConsequence(rule);
            assertElementId(rule);
            assertClassifierId(rule);
            assertChildRulesForParent(rule);
            assertFeedback(rule);
            assertCCId(rule);
        }
    }

    public void printElements() throws Exception{
        ActiveMeasureRuleSet amRulesSet = parser.getActiveMeasureRuleSet();
        Set<RuleDescription> allRules = amRulesSet.getAllRules();
        Set<Long> allElements = new HashSet<Long>();
        System.out.println("----------------------------------------------");
        for (RuleDescription rule : allRules){
            Set<Long> elements = rule.getElementIdsUsed();
            allElements.addAll(elements);
            Set<Long> measureIds = rule.getRelatedMeasureIds();
            Set<Long> classifierIds = rule.getClassifierIdsUsed();
            String hasChildren = rule.getChildren().isEmpty() ? "N" : "Y";
            System.out.println(rule.getPackageName() + ":" + rule.getRuleType()
                    + ":" + rule.getRuleName() + ":"
                    + Arrays.toString(measureIds.toArray()) + ":"
                    + Arrays.toString(elements.toArray()) + ":"
                    + Arrays.toString(classifierIds.toArray()) + ":"
                    + hasChildren);
        }
        System.out.println("----------------------------------------------");
        System.out.println("allElements=" + Arrays.toString(allElements.toArray()));
    }

    public static void main(String[] args) throws Exception{
        RuleParserTest rpt = new RuleParserTest();
        rpt.printElements();
    }

    static void assertFeedback(RuleDescription rule) {
        final String ruleName = rule.getRuleName();
        if (ruleName.contains("FDBK")) {
            if (rule.getRelatedFeedbackFamilieids().isEmpty()
                    && rule.getRelatedFeedbackReferenceIds().isEmpty()
                    && rule.getRelatedFeedbackResponseIds().isEmpty()) {
                fail("The rule " + rule.getPackageName() + "." + ruleName
                        + " did not have any feedback information");
            }
        }
    }

    static void assertParentSetCorrectly(RuleDescription rule) {
        String errorMessage = "The rule " + rule
                + " did not have its parent set correctly";
        if (rule.hasParentRule()) {
            assertNotNull(errorMessage, rule.getParent());
        } else {
            assertNull(errorMessage, rule.getParent());
        }
    }

    static void assertMeasureId(RuleDescription rule) {
        String ruleName = rule.getRuleName();
        if (!rule.getPackageName().contains(CDM_BASE_FOLDER)
                && !(ruleName.contains("LABEL") || ruleName
                        .contains(CLASSIFIER))
                && !ruleName.contains("BASE_DENOM")
                && !rule.getPackageName().contains(
                        COMMON_PACKAGE)
                && !rule.getPackageName().contains(CDM_BASE_FOLDER)
                && !rule.getRuleType().equals(RuleType.EVENT_FILTER)
                && !rule.getPackageName().contains(MEDICALCASE_BASE_FOLDER)
                && !rule.isEventLevelMeasure()) {
            assertFalse("The rule " + rule
                    + " does not have any measureId set!", rule
                    .getRelatedMeasureIds().isEmpty());
        }
    }

    static void assertMultipleElementId(RuleDescription rule) {
        String ruleName = rule.getRuleName();
        if (!rule.getPackageName().contains(CDM_BASE_FOLDER)
                && !(ruleName.contains("COMMON_LABEL") || ruleName
                        .contains(CLASSIFIER))
                && !ruleName.contains("BASE_DENOM")
                && !rule.getPackageName().contains(
                        COMMON_PACKAGE)
                && !rule.getPackageName().contains(CDM_BASE_FOLDER)
                && !rule.getRuleType().equals(RuleType.EVENT_FILTER)
                && !rule.isEventLevelMeasure()) {
            assertFalse("The rule " + rule
                    + " does not have any measureId set!", rule
                    .getRelatedMeasureIds().isEmpty());
        }
    }

    static void assertCdmId(RuleDescription rule) {
        if (rule.getPackageName().contains(CDM_BASE_FOLDER)
                && !rule.getRuleName().contains("LABEL")
                && !rule.getRuleName().contains(CLASSIFIER)
                && !rule.getRuleType().equals(RuleType.HCC)
                && !rule.getRuleType().equals(RuleType.CC)
                && !rule.getRuleType().equals(RuleType.EVENT_FILTER)) {
            assertFalse("The rule " + rule + " does not have any cdm ids set!",
                    rule.getRelatedEventLevelMeasureIds().isEmpty());
            // Disabled because CDMs use measure Ids
            // assertTrue("The rule " + rule
            // + "should not have any measure ids set!", rule
            // .getRelatedMeasureIds().isEmpty());
            assertTrue("The rule " + rule
                    + "should not have any created clasiifier ids set!", rule
                    .getClassifierIdsCreated().isEmpty());
        }
    }
    
    static void assertCCId(RuleDescription rule) {
        if (rule.getPackageName().contains(CDM_BASE_FOLDER)
                && rule.getRuleType().equals(RuleType.CC)) {
            assertFalse("The rule " + rule + " does not have any cc id set!",
                    rule.getRelatedCCIds().isEmpty());
        }
    }

    static void assertCdmIdForCDMDenomInConsequence(RuleDescription rule) {
        if (rule.getPackageName().contains(CDM_BASE_FOLDER)) {
            if (rule.getRuleName().equalsIgnoreCase(
                    "ACO08_65_DENIMINATOR_INCLUSION")) {
                assertTrue("The rule " + rule
                        + " does not have any cdm ids set!", rule
                        .getRelatedEventLevelMeasureIds().size() == 2);
                // disabled because cdms use measure ids
                // assertTrue("The rule " + rule
                // + "should not have any measure ids set!", rule
                // .getRelatedMeasureIds().isEmpty());
                assertTrue("The rule " + rule
                        + "should not have any used clasiifier ids set!", rule
                        .getClassifierIdsUsed().isEmpty());
                assertTrue("The rule " + rule
                        + "should not have any created clasiifier ids set!",
                        rule.getClassifierIdsCreated().isEmpty());
            }
        }
    }

    static void assertElementId(RuleDescription rule) {
        String ruleName = rule.getRuleName();
        if (ruleName
                .equalsIgnoreCase("COMMON_LABEL_ACO_DIABETES_DIAG_DIAG_1_OVERLAP_REINCLUDE")) {
            assertFalse("The rule " + rule
                    + " does not have any elementId set!", !(rule
                    .getElementIdsUsed().size() == 3));
        } else if (ruleName
                .equalsIgnoreCase("DENOM_ACO_COLORECTAL_SCREENING_2014")) {
            assertFalse("The rule " + rule
                    + " does not have any elementId set!", rule
                    .getElementIdsUsed().isEmpty());
        }
    }

    static void assertClassifierId(RuleDescription rule) {
        String ruleName = rule.getRuleName();
        if (ruleName
                .equalsIgnoreCase("DENOM_ACO_DIABETES_HEIDS_ENCOUNTER_1_12MO_OVERLAP")) {
            assertEquals("The rule " + rule
                    + " does not have any classifier set!", 1, rule
                    .getClassifierIdsUsed().size());
        } else if (ruleName.contains("COMMON_LABEL_")
               /* || rule.getRuleType().toString()
                        .equals(CLASSIFIER)*/) {
            assertFalse("The rule " + rule
                    + "does not have any classifiers set", rule
                    .getClassifierIdsCreated().isEmpty());
            assertTrue(
                    "The rule " + rule + "does not have any measure ids set",
                    rule.getRelatedMeasureIds().isEmpty());
            assertTrue("The rule " + rule + "does not have any cdm ids set",
                    rule.getRelatedEventLevelMeasureIds().isEmpty());
        } else if(rule.getRuleType().toString()
                .equals(CLASSIFIER)) {
            assertFalse("The rule " + rule
                    + "does not have any classifiers set", rule
                    .getClassifierIdsCreated().isEmpty());
          }
       }

    static class ParserEngine extends BaseActiveMeasureEngine {

        public ParserEngine() {
            super(
                    "http://192.168.4.112:8090/guvnor-5.5.0.Final-jboss-as-7.0/rest/packages",
                    null, ALL_DRL, getUsernameFromProperties(),
                    getPasswordFromProperties(),
                    getExpectedPackageNameBeginingStringFromProperties());
        }
    }

    @Test
    public void testGetActiveMeasureRuleSet() throws Exception {
        ActiveMeasureRuleSet amRulesSet = parser.getActiveMeasureRuleSet();

        assertElementIds(amRulesSet);
        assertCdmIds(amRulesSet);
        assertClassifierIds(amRulesSet);
        assertCreatedClassifierIds(amRulesSet);
        assertUsedClassifierIds(amRulesSet);
        assertQualityMeasureIds(amRulesSet);
    }

    static void assertElementIds(ActiveMeasureRuleSet amRulesSet) {
        boolean emptyMap = true;
        for (Map.Entry<Long, RelatedRules> entry : amRulesSet
                .getRulesByRelatedElementId().entrySet()) {
            emptyMap = false;

            boolean emptyRuleSet = true;
            for (RuleDescription rule : entry.getValue().getAllRules()) {
                assertTrue(
                        "The rule " + rule
                                + " did not have the expected elementId:"
                                + entry.getKey(), rule.getElementIdsUsed()
                                .contains(entry.getKey()));
                emptyRuleSet = false;
            }

            assertFalse(
                    "There should be some Rules for the ID " + entry.getKey(),
                    emptyRuleSet);
        }
        assertFalse("There should be values in the Map", emptyMap);
    }

    static void assertQualityMeasureIds(ActiveMeasureRuleSet amRulesSet) {
        boolean emptyMap = true;
        for (Map.Entry<Long, RelatedRules> entry : amRulesSet
                .getQaulityMeasuremRulesByMeasureId().entrySet()) {
            emptyMap = false;

            boolean emptyRuleSet = true;
            for (RuleDescription rule : entry.getValue().getAllRules()) {
                assertTrue("The rule " + rule
                        + " did not have the expected QM Id:" + entry.getKey(),
                        rule.getRelatedMeasureIds().contains(entry.getKey()));
                emptyRuleSet = false;
            }

            assertFalse(
                    "There should be some Rules for the ID " + entry.getKey(),
                    emptyRuleSet);
        }
        assertFalse("There should be values in the Map", emptyMap);
    }

    static void assertClassifierIds(ActiveMeasureRuleSet amRulesSet) {
        boolean emptyMap = true;
        for (Map.Entry<Long, RelatedRules> entry : amRulesSet
                .getClassifierRulesById().entrySet()) {
            emptyMap = false;

            boolean emptyRuleSet = true;
            for (RuleDescription rule : entry.getValue().getAllRules()) {
                assertTrue(
                        "The rule " + rule
                                + " did not have the expected Classifier Id:"
                                + entry.getKey(),
                        rule.getClassifierIdsUsed().contains(entry.getKey())
                                || rule.getClassifierIdsCreated().contains(
                                        entry.getKey()));
                emptyRuleSet = false;
            }

            assertFalse(
                    "There should be some Rules for the ID " + entry.getKey(),
                    emptyRuleSet);
        }
        assertFalse("There should be values in the Map", emptyMap);
    }

    static void assertCreatedClassifierIds(ActiveMeasureRuleSet amRulesSet) {
        boolean emptyMap = true;
        for (Map.Entry<Long, RelatedRules> entry : amRulesSet
                .getClassifierCreationRulesById().entrySet()) {
            emptyMap = false;

            boolean emptyRuleSet = true;
            for (RuleDescription rule : entry.getValue().getAllRules()) {
                assertTrue(
                        "The rule " + rule
                                + " did not have the expected Classifier Id:"
                                + entry.getKey(),
                        rule.getClassifierIdsCreated().contains(entry.getKey()));
                emptyRuleSet = false;
            }

            assertFalse(
                    "There should be some Rules for the ID " + entry.getKey(),
                    emptyRuleSet);
        }
        assertFalse("There should be values in the Map", emptyMap);
    }

    static void assertUsedClassifierIds(ActiveMeasureRuleSet amRulesSet) {
        boolean emptyMap = true;
        for (Map.Entry<Long, RelatedRules> entry : amRulesSet
                .getClassifierUsingRulesById().entrySet()) {
            emptyMap = false;

            boolean emptyRuleSet = true;
            for (RuleDescription rule : entry.getValue().getAllRules()) {
                assertTrue(
                        "The rule " + rule
                                + " did not have the expected Classifier Id:"
                                + entry.getKey(), rule.getClassifierIdsUsed()
                                .contains(entry.getKey()));
                emptyRuleSet = false;
            }

            assertFalse(
                    "There should be some Rules for the ID " + entry.getKey(),
                    emptyRuleSet);
        }
        assertFalse("There should be values in the Map", emptyMap);
    }

    static void assertCdmIds(ActiveMeasureRuleSet amRulesSet) {
        boolean emptyMap = true;
        for (Map.Entry<Long, RelatedRules> entry : amRulesSet
                .getCdmRulesByCdmId().entrySet()) {
            emptyMap = false;

            boolean emptyRuleSet = true;
            for (RuleDescription rule : entry.getValue().getAllRules()) {
                assertTrue("The rule " + rule
                        + " did not have the expected cdmId:" + entry.getKey(),
                        rule.getRelatedEventLevelMeasureIds().contains(entry.getKey()));
                emptyRuleSet = false;
            }

            assertFalse(
                    "There should be some Rules for the ID " + entry.getKey(),
                    emptyRuleSet);
        }
        assertFalse("There should be values in the Map", emptyMap);
    }

    static void assertRuleName(RuleDescription rule) {
        assertFalse("The rule name is empty!",
                StringUtils.isEmpty(rule.getRuleName()));
    }

    static void assertPackageName(RuleDescription rule) {
        assertFalse("The package name is empty!",
                StringUtils.isEmpty(rule.getPackageName()));
    }

    static void assertRuleType(RuleDescription rule) {
        assertNotNull("The rule type is not set!", (rule.getRuleType()));
    }

    static void assertChildRulesForParent(RuleDescription rule) {
        if (rule.hasParentRule()) {
            RuleDescription parentRule = rule.getParent();
            assertFalse("Child rules cannot be empty for parent rule "
                    + parentRule.getRuleName(), parentRule.getChildren()
                    .isEmpty());
        }
    }
}

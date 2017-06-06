package net.ahm.activemeasure.medicalcase.year2015.aetnameasures.Day30_Readmission_Rate_2015;

import java.util.Collection;

import net.ahm.activemeasure.ruletemplate.util.AbstractActiveMeasuresRuleTemplateTest;
import net.ahm.activemeasure.templates.ActiveMeasureUtilConstants;
import net.ahm.activemeasure.templates.FactLevelMeasureNumeratorCreationTemplate;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.medicalcase.MedicalCase;
import net.ahm.careengine.domain.medicalcase.MedicalCaseAffiliation;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.implementations.DefaultStandardRuleTemplate;
import net.ahm.rulesapp.templates.libraries.AnyAttributeSingleComparisonFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AnyClassMultipleAttributeEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.AttributeFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.FromEvaluationFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.libraries.NotExistenceExpression;
import net.ahm.rulesapp.templates.utils.TypeDescription;
import net.ahm.rulesapp.util.UtilFunctions;

/**
 * Test case to generate JSON/DRL representation of the rule NUM_READMISSION
 */

//82.	|	rule "NUM_READMISSION"
//83.	|	    dialect "mvel"
//84.	|	    when
//85.	|	        $flmDenom : FactLevelMeasureDenominator( measureId == 84 , primaryOriginationFact != null )
//86.	|	        $medicalCase : MedicalCase( $affiliationCollection : medicalCaseAffiliationCollection != null , medicalCaseAffiliationCollection.empty == false ) from $flmDenom.primaryOriginationFact
//87.	|	        $affiliation : MedicalCaseAffiliation( affiliationTypeCode == "A" ) from $affiliationCollection
//88.	|	        $relatedMedicalCase : MedicalCase( this != $medicalCase , medicalCaseSkey == $affiliation.affiliatedMedicalCaseSkey , diagnosisRelatedGroupElements excludes 9244 && excludes 9245 && excludes 9247 && excludes 9246 )
//89.	|	    then
//90.	|	        FactLevelMeasureNumerator flmNumerator = $flmDenom.createFactLevelMeasureNumeratorWithPrimaryFact($relatedMedicalCase); insert(flmNumerator);
//91.	|	end

public class NumReadmissionTest extends AbstractActiveMeasuresRuleTemplateTest {

    private static final int MEASURE_ID = 84;
    static final String      medicalCaseAffiliationCollection = "medicalCaseAffiliationCollection";

    @Override
    public DefaultStandardRuleTemplate getRuleInstance() {
        AnyClassMultipleAttributeEvaluationFragmentTemplate flmDenomTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        FactLevelMeasureDenominator.class,
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR,
                        ActiveMeasureUtilFunctions
                                .createMeasureIdInOrEqualToAttributeFagementTemplate(MEASURE_ID),
                        ActiveMeasureUtilFunctions
                                .createObjectNullCheckFragment(
                                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                                        CommonOperators.NOT_EQUAL_TO,
                                        Fact.class));

        TypeDescription medicalCaseAffiliationCollectionType = TypeDescription.getTypeDescription(Collection.class, MedicalCaseAffiliation.class);
        AnyAttributeSingleComparisonFragmentTemplate<Collection<MedicalCaseAffiliation>> affCollectionAttr = ActiveMeasureUtilFunctions
                .createAnyAttributeSingleComparisonFragmentTemplate(
                        medicalCaseAffiliationCollection,
                        medicalCaseAffiliationCollectionType,
                        new NotExistenceExpression(),
                        CommonOperators.NOT_EQUAL_TO, "$affiliationCollection");

        AnyClassMultipleAttributeEvaluationFragmentTemplate flmTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MedicalCase.class,
                        "$medicalCase",
                        affCollectionAttr,
                        ActiveMeasureUtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        new AttributeFragmentTemplate(
                                                medicalCaseAffiliationCollection,
                                                medicalCaseAffiliationCollectionType,
                                                new AttributeFragmentTemplate(
                                                        "empty",
                                                        TypeDescription
                                                                .getTypeDescription(Boolean.TYPE))),
                                        UtilFunctions
                                                .createBooleanLiteralExpressionFragmentTemplate(false),
                                        CommonOperators.EQUAL_TO, null));
        NamedVariableLiteralFragmentTemplate fldPrimeFact = flmDenomTemplate
                .getVariableExpression();
        fldPrimeFact
                .setAttribute(new AttributeFragmentTemplate(
                        ActiveMeasureUtilConstants.FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME,
                        TypeDescription.getTypeDescription(Fact.class)));

        AnyClassMultipleAttributeEvaluationFragmentTemplate affiliationTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MedicalCaseAffiliation.class,
                        "$affiliation",
                        ActiveMeasureUtilFunctions
                                .createAnyAttributeSingleComparisonFragmentTemplate(
                                        "affiliationTypeCode",
                                        TypeDescription
                                                .getTypeDescription(String.class),
                                        UtilFunctions
                                                .createStringLiteralExpressionFragmentTemplate("A"),
                                        CommonOperators.EQUAL_TO, null));
        new AnyClassMultipleAttributeEvaluationFragmentTemplate();

        NamedVariableLiteralFragmentTemplate affiliationAffiliatedMedicalCaseSkey = affiliationTemplate
                .getVariableExpression();
        affiliationAffiliatedMedicalCaseSkey
                .setAttribute(new AttributeFragmentTemplate(
                        "affiliatedMedicalCaseSkey", TypeDescription
                                .getTypeDescription(String.class)));

        AnyClassMultipleAttributeEvaluationFragmentTemplate relatedMedicalCaseTemplate = ActiveMeasureUtilFunctions
                .createAnyClassMultipleAttributeEvaluationFragmentTemplate(
                        MedicalCase.class, "$relatedMedicalCase",
                        ActiveMeasureUtilFunctions
                                .createObjectVariableCheckFragment("this",
                                        MedicalCase.class,
                                        CommonOperators.NOT_EQUAL_TO,
                                        flmTemplate.getVariableExpression()),
                        UtilFunctions.createObjectVariableCheckFragment(
                                "medicalCaseSkey", CommonOperators.EQUAL_TO,
                                String.class,
                                affiliationAffiliatedMedicalCaseSkey),
                        UtilFunctions
                                .createMultipleAttributeEvaluationFragmentTemplate(
                                        Connector.AND,
                                        ActiveMeasureUtilFunctions
                                                .createElementExcludesValueTemplate(
                                                        "diagnosisRelatedGroupElements",
                                                        9244),
                                        ActiveMeasureUtilFunctions
                                                .createElementExcludesValueTemplate(
                                                        "diagnosisRelatedGroupElements",
                                                        9245),
                                        ActiveMeasureUtilFunctions
                                                .createElementExcludesValueTemplate(
                                                        "diagnosisRelatedGroupElements",
                                                        9247),
                                        ActiveMeasureUtilFunctions
                                                .createElementExcludesValueTemplate(
                                                        "diagnosisRelatedGroupElements",
                                                        9246)));

        DefaultStandardRuleTemplate standardRuleInstance = UtilFunctions
                .createDefaultStandardRuleTemplate("NUM_READMISSION",
                        new FactLevelMeasureNumeratorCreationTemplate(
                                flmDenomTemplate.getVariableExpression(),
                                relatedMedicalCaseTemplate
                                        .getVariableExpression()),
                        flmDenomTemplate,
                        new FromEvaluationFragmentTemplate(flmTemplate,
                                fldPrimeFact),
                        new FromEvaluationFragmentTemplate(affiliationTemplate,
                                affCollectionAttr.getVariableExpression()),
                        relatedMedicalCaseTemplate);
        standardRuleInstance
                .setRuleDescription("Simulating the rule in package: net.ahm.activemeasure.medicalcase.year2015.aetnameasures.Day30_Readmission_Rate_2015");
        return standardRuleInstance;
    }
}

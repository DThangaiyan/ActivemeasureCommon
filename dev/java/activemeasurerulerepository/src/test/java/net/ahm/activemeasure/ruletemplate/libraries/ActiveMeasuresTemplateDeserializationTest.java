package net.ahm.activemeasure.ruletemplate.libraries;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ahm.activemeasure.ruletemplate.json.ActiveMeasureJSONMapper;
import net.ahm.activemeasure.templates.ActiveMeasureDenominatorCreationTemplate;
import net.ahm.activemeasure.templates.ClassifierCreationTemplate;
import net.ahm.activemeasure.templates.CoverageOvelappingContiguousDaysExpressable;
import net.ahm.activemeasure.templates.FactLevelMeasureNumeratorCreationTemplate;
import net.ahm.activemeasure.templates.FilterWholeDaysFromCollectionTemplate;
import net.ahm.activemeasure.templates.MemberEnrollmentGapTemplate;
import net.ahm.activemeasure.templates.util.BaseActiveMeasureUtilFunctions;
import net.ahm.activemeasure.templates.util.CoverageType;
import net.ahm.activemeasure.util.ActiveMeasureUtilFunctions;
import net.ahm.careengine.domain.event.Event;
import net.ahm.careengine.domain.event.lab.LabEvent;
import net.ahm.careengine.domain.event.proc.ProcedureEvent;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.ruleengine.DateTimeUnit;
import net.ahm.rulesapp.json.RulesAppRepositoryJsonMapper;
import net.ahm.rulesapp.templates.enums.CommonOperators;
import net.ahm.rulesapp.templates.interfaces.ExecutableTemplate;
import net.ahm.rulesapp.templates.libraries.TemplateDeserializationTest;
import net.ahm.rulesapp.util.DateShiftFunction;
import net.ahm.rulesapp.util.TemplateRegistry;
import net.ahm.rulesapp.util.UtilFunctions;

import org.junit.runners.Parameterized.Parameters;

public class ActiveMeasuresTemplateDeserializationTest extends
        TemplateDeserializationTest {
    private static final TemplateRegistry             REGISTRY          = TemplateRegistry
                                                                                .getInstance("amtemplatemap,templatemap");
    private static final TemplateRegistry             COVERAGE_REGISTRY = TemplateRegistry
                                                                                .getInstance("amtemplatemap");
    private static final RulesAppRepositoryJsonMapper MAPPER            = ActiveMeasureJSONMapper
                                                                                .getInstance();

    public ActiveMeasuresTemplateDeserializationTest(Class<?> templateClass,
            String testDescription, ExecutableTemplate template,
            TemplateRegistry registry, RulesAppRepositoryJsonMapper mapper) {
        super(templateClass, testDescription, template, registry, mapper);
    }

    @Parameters(name = "testing the template {0} : {1}")
    public static Collection<Object[]> data() throws Exception {
        return createData(REGISTRY, COVERAGE_REGISTRY, MAPPER, true);
    }

    protected static Collection<Object[]> createData(
            TemplateRegistry testCaseRegistry,
            TemplateRegistry coverageRegistry,
            RulesAppRepositoryJsonMapper mapper, boolean checkCoverage) {
        Map<Class<?>, List<TestData>> testDataMap = new HashMap<>();

        addTestDataToMap(
                new TestData(
                        "ContiguousDaysPaddingTemplate",
                        ActiveMeasureUtilFunctions
                                .createContiguousDaysPaddingTemplate(
                                        "$paddedDays",
                                        ActiveMeasureUtilFunctions
                                                .createNamedVariableLiteralFragmentTemplate(
                                                        "$pivotEvent",
                                                        Event.class, null), 6,
                                        DateTimeUnit.MONTH, 2,
                                        DateTimeUnit.YEAR), testCaseRegistry, mapper),
                testDataMap);

        addTestDataToMap(
                new TestData(
                        "medical coverage overlap test",
                        new CoverageOvelappingContiguousDaysExpressable(
                                CoverageType.MEDICAL,
                                CommonOperators.LESS_THAN,
                                40,
                                CommonOperators.GREATER_THAN,
                                5,
                                UtilFunctions
                                        .createNamedVariableLiteralFragmentTemplate(
                                                "$procEvent",
                                                ProcedureEvent.class, null)),
                        testCaseRegistry, mapper), testDataMap);

        addTestDataToMap(
                new TestData(
                        "medical coverage overlap test",
                        new CoverageOvelappingContiguousDaysExpressable(
                                CoverageType.PHARMACY,
                                CommonOperators.GREATER_THAN_OR_EQUAL_TO,
                                1,
                                CommonOperators.LESS_THAN_OR_EQUAL_TO,
                                7,
                                UtilFunctions
                                        .createNamedVariableLiteralFragmentTemplate(
                                                "$labEvent", LabEvent.class,
                                                null)), testCaseRegistry,
                        mapper), testDataMap);

        addTestDataToMap(new TestData("Denom creation template with no dates",
                        new ActiveMeasureDenominatorCreationTemplate(
                                NUMER_PROVIDER.getAndIncrement()),
                testCaseRegistry, mapper), testDataMap);
        addTestDataToMap(
                new TestData(
                        "Denom creation template with start date",
                        ActiveMeasureUtilFunctions
                                .createActiveMeasureDenominatorCreationTemplate(
                                        NUMER_PROVIDER.getAndIncrement(),
                                        UtilFunctions
                                                .createNamedVariableLiteralFragmentTemplate(
                                                        "$startDate",
                                                        Date.class, null), null),
                        testCaseRegistry, mapper), testDataMap);
        addTestDataToMap(
                new TestData(
                        "Denom creation template with end date",
                        ActiveMeasureUtilFunctions
                                .createActiveMeasureDenominatorCreationTemplate(
                                        NUMER_PROVIDER.getAndIncrement(), null,
                                        UtilFunctions
                                                .createNamedVariableLiteralFragmentTemplate(
                                                        "$endDate", Date.class,
                                                        null)),
                        testCaseRegistry, mapper), testDataMap);
        addTestDataToMap(
                new TestData(
                        "Denom creation template with both dates",
                        ActiveMeasureUtilFunctions
                                .createActiveMeasureDenominatorCreationTemplate(
                                        NUMER_PROVIDER.getAndIncrement(),
                                        UtilFunctions
                                                .createNamedVariableLiteralFragmentTemplate(
                                                        "$altStartDate",
                                                        Date.class, null),
                                        UtilFunctions
                                                .createNamedVariableLiteralFragmentTemplate(
                                                        "$altEndDate",
                                                        Date.class, null)),
                        testCaseRegistry, mapper), testDataMap);

        addTestDataToMap(
                new TestData("classifier creation",
                        new ClassifierCreationTemplate(NUMER_PROVIDER
                                .getAndIncrement()),
                        testCaseRegistry, mapper), testDataMap);

        addTestDataToMap(
                new TestData(
                        "date shifting",
                        BaseActiveMeasureUtilFunctions
                                .createDateDefinitionTemplate(
                                        "$dateVar",
                                        DateShiftFunction.GET_EARLIER_DATE,
                                        UtilFunctions
                                                .createNamedVariableLiteralFragmentTemplate(
                                                        "$initialDate",
                                                        Date.class, null), 20,
                                        DateTimeUnit.DAY), testCaseRegistry,
                        mapper), testDataMap);

        addTestDataToMap(
                new TestData(
                        "create FactLevelMeasureDenominatorCreationTemplate without a primary fact",
                        ActiveMeasureUtilFunctions
                                .createFactLevelMeasureDenominatorCreationTemplate(
                                        NUMER_PROVIDER.getAndIncrement(), null),
                        testCaseRegistry, mapper), testDataMap);
        addTestDataToMap(
                new TestData(
                        "create FactLevelMeasureDenominatorCreationTemplate with a primary fact",
                        ActiveMeasureUtilFunctions
                                .createFactLevelMeasureDenominatorCreationTemplate(
                                        NUMER_PROVIDER.getAndIncrement(),
                                        ActiveMeasureUtilFunctions
                                                .createNamedVariableLiteralFragmentTemplate(
                                                        "$primaryFact",
                                                        Fact.class, null)),
                        testCaseRegistry, mapper), testDataMap);

        addTestDataToMap(
                new TestData(
                        "FactLevelMeasureNumeratorCreationTemplate without a primary fact",
                        new FactLevelMeasureNumeratorCreationTemplate(
                                ActiveMeasureUtilFunctions
                                        .createNamedVariableLiteralFragmentTemplate(
                                                "$denom",
                                                FactLevelMeasureDenominator.class,
                                                null), null), testCaseRegistry,
                        mapper), testDataMap);
        addTestDataToMap(
                new TestData(
                        "FactLevelMeasureNumeratorCreationTemplate with a primary fact",
                        new FactLevelMeasureNumeratorCreationTemplate(
                                ActiveMeasureUtilFunctions
                                        .createNamedVariableLiteralFragmentTemplate(
                                                "$denom",
                                                FactLevelMeasureDenominator.class,
                                                null),
                                ActiveMeasureUtilFunctions
                                        .createNamedVariableLiteralFragmentTemplate(
                                                "$primaryFact", Fact.class,
                                                null)), testCaseRegistry,
                        mapper), testDataMap);

        addTestDataToMap(new TestData("filter whole days",
                new FilterWholeDaysFromCollectionTemplate("$initialTemporals",
                        "$stardDate", "$endDate"), testCaseRegistry, mapper),
                testDataMap);

        addTestDataToMap(
                new TestData(
                        "MemberEnrollmentGapTemplate",
                        new MemberEnrollmentGapTemplate(
                                ActiveMeasureUtilFunctions
                                        .createNamedVariableLiteralFragmentTemplate(
                                                "$startDate", Date.class, null),
                                ActiveMeasureUtilFunctions
                                        .createNamedVariableLiteralFragmentTemplate(
                                                "$endDate", Date.class, null),
                                1, CommonOperators.EQUAL_TO, 45,
                                CommonOperators.GREATER_THAN),
                        testCaseRegistry, mapper), testDataMap);

        addCoverageData(testDataMap, coverageRegistry, mapper, checkCoverage);

        return convert(testDataMap);
    }
}

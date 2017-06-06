package net.ahm.activemeasure.templates.util;

import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;

import net.ahm.careengine.domain.classifier.ClassifierBuilder;
import net.ahm.careengine.domain.comorbidclinicalcondition.HierarchicalClinicalConditionCategoryBuilder;
import net.ahm.careengine.domain.comorbidclinicalcondition.RiskStratificationMeasureWeightsBuilder;
import net.ahm.careengine.domain.measures.active.ActiveMeasureBuilder;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureBuilder;
import net.ahm.rulesapp.templates.implementations.AbstractGlobalContainer;
import net.ahm.rulesapp.templates.interfaces.GlobalDefinitionIF;
import net.ahm.rulesapp.templates.libraries.NamedVariableLiteralFragmentTemplate;
import net.ahm.rulesapp.templates.utils.TypeDescription;

public class ActiveMeasureGlobalContainer extends AbstractGlobalContainer {
    private static final long                        serialVersionUID = 1L;
    public static final ActiveMeasureGlobalContainer INSTANCE         = new ActiveMeasureGlobalContainer();

    public static ActiveMeasureGlobalContainer getInstance() {
        return INSTANCE;
    }
    
    private ActiveMeasureGlobalContainer() {
        super(ActiveMeasureGlobalDefinition.ALL_DEFINITIONS);
    }

    @Override
    public boolean equals(Object obj) {
        // since this is just an immutable version of the parent class with
        // fixed contents we are using the parent's method implementation
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        // since this is just an immutable version of the parent class with
        // fixed contents we are using the parent's method implementation
        return super.hashCode();
    }

    public static enum ActiveMeasureGlobalDefinition implements
            GlobalDefinitionIF {
        MEASUREMENT_START_DATE("measurementStartDate"), //
        MEASUREMENT_END_DATE("measurementEndDate"), //
        FACT_LEVEL_MEASURE_BUILDER("factLevelMeasureBuilder",
                FactLevelMeasureBuilder.class), //
        CLASSIFIER_BUILDER("classifierBuilder", ClassifierBuilder.class), //
        ACTIVE_MEASURE_BUILDER("activeMeasureBuilder",
                ActiveMeasureBuilder.class), //
        YEARS_10_BEFORE_MEASUREMENT_END_DATE("years10BeforeMeasurementEndDate"), //
        YEARS_5_BEFORE_MEASUREMENT_END_DATE("years5BeforeMeasurementEndDate"), //
        YEARS_3_BEFORE_MEASUREMENT_END_DATE("years3BeforeMeasurementEndDate"), //
        MONTHS_27_BEFORE_MEASUREMENT_END_DATE(
                "months27BeforeMeasurementEndDate"), //
        MONTHS_25_BEFORE_MEASUREMENT_END_DATE(
                "months25BeforeMeasurementEndDate"), //
        MONTHS_24_BEFORE_MEASUREMENT_END_DATE(
                "months24BeforeMeasurementEndDate"), //
        MONTHS_13_BEFORE_MEASUREMENT_END_DATE(
                "months13BeforeMeasurementEndDate"), //
        MONTHS_12_BEFORE_MEASUREMENT_END_DATE(
                "months12BeforeMeasurementEndDate"), //
        MONTHS_11_BEFORE_MEASUREMENT_END_DATE(
                "months11BeforeMeasurementEndDate"), //
        MONTHS_6_BEFORE_MEASUREMENT_END_DATE("months6BeforeMeasurementEndDate"), //
        MONTHS_3_BEFORE_MEASUREMENT_END_DATE("months3BeforeMeasurementEndDate"), //
        MONTHS_1_BEFORE_MEASUREMENT_END_DATE("months1BeforeMeasurementEndDate"), HCCCATEGORY_BUILDER(
                "hccCategoryBuilder",
                HierarchicalClinicalConditionCategoryBuilder.class), RISKSTRATIFICATIONMEASUREWEIGHTS_BUILDER(
                "riskStratificationMeasureWeightsBuilder",
                RiskStratificationMeasureWeightsBuilder.class);
        ;

        private final String                        variableName;
        private final TypeDescription               type;

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public static final Set<GlobalDefinitionIF> ALL_DEFINITIONS = Collections
                                                                            .unmodifiableSet((Set) EnumSet
                                                                                               .allOf(ActiveMeasureGlobalDefinition.class));

        private ActiveMeasureGlobalDefinition(String varName, Class<?> type) {
            this.variableName = varName;
            this.type = TypeDescription.getTypeDescription(type);
        }

        private ActiveMeasureGlobalDefinition(String varName) {
            this(varName, Date.class);
        }

        @Override
        public String getVariableName() {
            return variableName;
        }

        @Override
        public TypeDescription getType() {
            return type;
        }

        @Override
        public NamedVariableLiteralFragmentTemplate getNamedVariableLiteralFragmentTemplate() {
            return new NamedVariableLiteralFragmentTemplate(getVariableName(),
                    getType());
        }
    }
}

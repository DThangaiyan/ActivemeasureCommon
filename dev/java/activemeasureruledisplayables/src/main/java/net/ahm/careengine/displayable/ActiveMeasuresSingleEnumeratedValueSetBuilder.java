package net.ahm.careengine.displayable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ahm.careengine.common.CECollectionsUtil;
import net.ahm.careengine.displayable.dao.SingleSelectionProviderDAO;
import net.ahm.careengine.domain.classifier.Classifier;
import net.ahm.careengine.domain.measures.active.ActiveMeasure;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureNumerator;
import net.ahm.careengine.domain.measures.active.MeasureRelated;
import net.ahm.careengine.domain.medicalcase.MedicalCase;
import net.ahm.rulesapp.displayables.type.RelatedAttribute;
import net.ahm.rulesapp.displayables.type.SingleSelection;
import net.ahm.rulesapp.templates.utils.TypeDescription;

import org.apache.commons.lang.WordUtils;

public enum ActiveMeasuresSingleEnumeratedValueSetBuilder implements
        CommonSingleEnumeratedValueSetBuilderIF {
    CLASSIFIER_IDS(Long.TYPE, new RelatedAttribute("id", Classifier.class)) {
        @Override
        public List<SingleSelection> apply(SingleSelectionProviderDAO dao) {
            return dao.getClassifiers();
        }
    },
    DIAGNOSIS_RELATED_GROUP_ELEMENT_IDS(
            CommonSingleEnumeratedValueSetBuilder.DIAGNOSIS_RELATED_GROUP_ELEMENT_IDS,
            new RelatedAttribute("diagnosisRelatedGroupElements",
                    MedicalCase.class)) {
        @Override
        public List<SingleSelection> apply(SingleSelectionProviderDAO dao) {
            return CommonSingleEnumeratedValueSetBuilder.DIAGNOSIS_RELATED_GROUP_ELEMENT_IDS
                    .apply(dao);
        }
    },
    MEASURE_IDS(Long.TYPE, new RelatedAttribute("measureId",
            MeasureRelated.class), new RelatedAttribute("measureId",
            ActiveMeasure.class), new RelatedAttribute("measureId",
            FactLevelMeasureDenominator.class), new RelatedAttribute(
            "measureId", FactLevelMeasureNumerator.class)) {
        @Override
        public List<SingleSelection> apply(SingleSelectionProviderDAO dao) {
            return dao.getMeasureIds();
        }
    };

    private final TypeDescription                         typeDescription;
    private final List<RelatedAttribute>                  relatedAttributes;
    private final CommonSingleEnumeratedValueSetBuilderIF parent;

    private static final char[]          WORD_DELIMITERS = CECollectionsUtil
                                                                 .toCharArray(WORD_DELIMITER_SET);

    private ActiveMeasuresSingleEnumeratedValueSetBuilder(
            CommonSingleEnumeratedValueSetBuilderIF parent, Class<?> type,
            List<RelatedAttribute> otherAttributes,
            RelatedAttribute... relatedAttributes) {
        this.parent = parent;
        typeDescription = TypeDescription.getTypeDescription(type);
        List<RelatedAttribute> tempList = new ArrayList<RelatedAttribute>(
                otherAttributes);
        if (relatedAttributes != null && relatedAttributes.length > 0) {
            Collections.addAll(tempList, relatedAttributes);
        }
        if (!tempList.isEmpty()) {
            this.relatedAttributes = Collections.unmodifiableList(tempList);
        } else {
            this.relatedAttributes = Collections.emptyList();
        }
    }

    private ActiveMeasuresSingleEnumeratedValueSetBuilder(
            RelatedAttribute... relatedAttributes) {
        this(Integer.TYPE, relatedAttributes);
    }

    private ActiveMeasuresSingleEnumeratedValueSetBuilder(
            CommonSingleEnumeratedValueSetBuilder parent,
            RelatedAttribute... relatedAttributes) {
        this(parent, parent.getTypeDescription().getType(), parent
                .getRelatedAttributes(), relatedAttributes);
    }

    private ActiveMeasuresSingleEnumeratedValueSetBuilder(Class<?> type,
            RelatedAttribute... relatedAttributes) {
        this(null, type, Collections.<RelatedAttribute> emptyList(),
                relatedAttributes);
    }

    @Override
    public List<RelatedAttribute> getRelatedAttributes() {
        return relatedAttributes;
    }

    @Override
    public TypeDescription getTypeDescription() {
        return typeDescription;
    }

    @Override
    public String getName() {
        return WordUtils.capitalizeFully(name(), WORD_DELIMITERS);
    }

    @Override
    public List<SingleSelection> getItems(
            CareEngineEnumeratedValueSetBuilderContext ctx) {
        return apply(ctx.getDao());
    }

    @Override
    public abstract List<SingleSelection> apply(SingleSelectionProviderDAO dao);

    public static final Set<CommonSingleEnumeratedValueSetBuilderIF> ALL_BUILDERS;
    static {
		Set<CommonSingleEnumeratedValueSetBuilderIF> tempSet = new HashSet<CommonSingleEnumeratedValueSetBuilderIF>();
		Set<CommonSingleEnumeratedValueSetBuilderIF> parentSet = new HashSet<CommonSingleEnumeratedValueSetBuilderIF>();

        for(ActiveMeasuresSingleEnumeratedValueSetBuilder builder : values()){
            tempSet.add(builder);
            if (builder.parent != null) {
                parentSet.add(builder.parent);
            }
        }

        for (CommonSingleEnumeratedValueSetBuilder commonBuilder : CommonSingleEnumeratedValueSetBuilder
                .values()) {
            if (!parentSet.contains(commonBuilder)) {
                tempSet.add(commonBuilder);
            }
        }

        ALL_BUILDERS = Collections.unmodifiableSet(tempSet);
    }
}

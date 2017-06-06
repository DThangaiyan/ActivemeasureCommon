package net.ahm.careengine.displayable;

import java.util.Set;

import net.ahm.careengine.common.CECollectionsUtil;
import net.ahm.careengine.domain.classifier.Classifier;
import net.ahm.careengine.domain.fact.ClassifiedFact;
import net.ahm.careengine.domain.measures.active.ActiveMeasure;
import net.ahm.careengine.domain.measures.active.ActiveMeasureRuleContext;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureNumerator;
import net.ahm.careengine.domain.medicalcase.MedicalCase;
import net.ahm.careengine.domain.member.ActiveMeasuresMemberInfo;

public class ActiveMeasureModelInfo {

    public static final Set<Class<?>> ACTIVE_MEASURES_ROOT_CLASS_SET = CECollectionsUtil
                                                                             .<Class<?>> unmodifiableSetFromSeedCollection(
                                                                                     CommonModelInfo.COMMON_ROOT_CLASS_SET,
                                                                                     MedicalCase.class,
                                                                                     ActiveMeasuresMemberInfo.class,
                                                                                     ActiveMeasure.class,
                                                                                     FactLevelMeasureDenominator.class,
                                                                                     FactLevelMeasureNumerator.class,
                                                                                     Classifier.class,
                                                                                     ClassifiedFact.class,
                                                                                     ActiveMeasureRuleContext.class);
    public static final Set<Class<?>> STATIC_UTIL_CLASSES            = CommonModelInfo.COMMON_STATIC_UTIL_CLASSES;
    public static final Set<Class<?>> ADDITIONAL_CLASSES_USED        = CommonModelInfo.COMMON_ADDITIONAL_CLASSES_USED;
}

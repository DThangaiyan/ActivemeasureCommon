package net.ahm.careengine.displayable;

import java.util.Set;

import net.ahm.activemeasure.templates.util.CoverageType;
import net.ahm.careengine.common.CECollectionsUtil;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.event.clinical.HieEncounter;
import net.ahm.careengine.domain.event.diag.DiagnosticEvent;
import net.ahm.careengine.domain.event.drug.DrugDispensingEvent;
import net.ahm.careengine.domain.event.drug.DrugEvent;
import net.ahm.careengine.domain.event.lab.LabResultEvent;
import net.ahm.careengine.domain.event.pdd.PatientDerivedEvent;
import net.ahm.careengine.domain.event.proc.ProcedureEvent;
import net.ahm.careengine.util.GlobalFunctions;
import net.ahm.rulesapp.templates.enums.Connector;
import net.ahm.rulesapp.templates.interfaces.AccumulateFunction;
import net.ahm.rulesapp.util.DateShiftFunction;
import net.ahm.rulesapp.util.Operator;

public class CommonModelInfo {

    public static final Set<Class<?>> COMMON_ROOT_CLASS_SET      = CECollectionsUtil
                                                                             .<Class<?>> unmodifiableSet(
                                                                                     DiagnosticEvent.class,
                                                                                     DrugEvent.class,
                                                                                     DrugDispensingEvent.class,
                                                                                     LabResultEvent.class,
                                                                                     ClaimHeader.class,
                                                                                     HieEncounter.class,
                                                                                     ProcedureEvent.class,
                                                                                     PatientDerivedEvent.class);
    public static final Set<Class<?>> COMMON_STATIC_UTIL_CLASSES     = CECollectionsUtil
                                                                             .<Class<?>> unmodifiableSet(GlobalFunctions.class);
    public static final Set<Class<?>> COMMON_ADDITIONAL_CLASSES_USED = CECollectionsUtil
                                                                             .<Class<?>> unmodifiableSet(
                                                                                     DateShiftFunction.class,
                                                                                     Operator.class,
                                                                                     Connector.class,
                                                                                     CoverageType.class,
                                                                                     AccumulateFunction.class);
}

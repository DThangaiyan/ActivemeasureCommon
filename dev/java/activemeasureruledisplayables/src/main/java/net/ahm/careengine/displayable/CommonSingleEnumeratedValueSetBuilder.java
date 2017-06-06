package net.ahm.careengine.displayable;

import java.util.Collections;
import java.util.List;

import net.ahm.careengine.common.CECollectionsUtil;
import net.ahm.careengine.displayable.dao.SingleSelectionProviderDAO;
import net.ahm.careengine.domain.event.CodifiedEvent;
import net.ahm.careengine.domain.event.Event;
import net.ahm.careengine.domain.event.adt.BaseAdmissionDischardTransferEvent;
import net.ahm.careengine.domain.event.claim.ClaimHeader;
import net.ahm.careengine.domain.event.clinical.HieEncounter;
import net.ahm.careengine.domain.event.diag.DiagnosticEvent;
import net.ahm.careengine.domain.event.drug.DrugDispensingEvent;
import net.ahm.careengine.domain.event.drug.DrugEvent;
import net.ahm.careengine.domain.event.lab.LabEvent;
import net.ahm.careengine.domain.event.lab.LabResultEvent;
import net.ahm.careengine.domain.event.pdd.PatientDerivedEvent;
import net.ahm.careengine.domain.event.proc.ProcedureEvent;
import net.ahm.rulesapp.displayables.type.RelatedAttribute;
import net.ahm.rulesapp.displayables.type.SingleSelection;
import net.ahm.rulesapp.templates.utils.TypeDescription;

import org.apache.commons.lang.WordUtils;

public enum CommonSingleEnumeratedValueSetBuilder implements
        CommonSingleEnumeratedValueSetBuilderIF {
    ALL_EVENT_ELEMENT_IDS(new RelatedAttribute("elements", CodifiedEvent.class)) {
        @Override
        public List<SingleSelection> apply(SingleSelectionProviderDAO dao) {
            return dao.getAllEventElements();
        }
    },
    DIAGNOSIS_RELATED_GROUP_ELEMENT_IDS(new RelatedAttribute(
            "diagnosisRelatedGroupElements", ClaimHeader.class)) {
        @Override
        public List<SingleSelection> apply(SingleSelectionProviderDAO dao) {
            return dao.getDrgElements();
        }
    },
    DIAGNOSTIC_EVENT_ELEMENT_IDS(new RelatedAttribute("elements",
            DiagnosticEvent.class), new RelatedAttribute(
            "allDiagnosticEventElements",
            BaseAdmissionDischardTransferEvent.class), new RelatedAttribute(
            "principalDiagnosisElements",
            BaseAdmissionDischardTransferEvent.class), new RelatedAttribute(
            "relatedDiagnosticEventElements",
            BaseAdmissionDischardTransferEvent.class), new RelatedAttribute(
            "allDiagnosticEventElements", ClaimHeader.class),
            new RelatedAttribute("principalDiagnosisElements",
                    ClaimHeader.class), new RelatedAttribute(
                    "relatedDiagnosticEventElements", ClaimHeader.class),
            new RelatedAttribute("admitDiagnosisElements", ClaimHeader.class),
            new RelatedAttribute("allDiagnosticEventElements",
                    HieEncounter.class), new RelatedAttribute(
                    "principalDiagnosisElements", HieEncounter.class),
            new RelatedAttribute("relatedDiagnosticEventElements",
                    HieEncounter.class)) {
        @Override
        public List<SingleSelection> apply(SingleSelectionProviderDAO dao) {
            return dao.getDiagnosticEventElements();
        }
    },
    DRUG_EVENT_ELEMENT_IDS(new RelatedAttribute("elements", DrugEvent.class),
            new RelatedAttribute("elements", DrugDispensingEvent.class),
            new RelatedAttribute("relatedDrugEventElements", ClaimHeader.class)) {
        @Override
        public List<SingleSelection> apply(SingleSelectionProviderDAO dao) {
            return dao.getDrugEventElements();
        }
    },
    LAB_EVENT_ELEMENT_IDS(new RelatedAttribute("elements", LabEvent.class),
            new RelatedAttribute("elements", LabResultEvent.class),
            new RelatedAttribute("relatedLabEventElements", ClaimHeader.class)) {
        @Override
        public List<SingleSelection> apply(SingleSelectionProviderDAO dao) {
            return dao.getLabEventElements();
        }
    },
    PATIENT_DERIVED_EVENT_ELEMENT_IDS(new RelatedAttribute("elements",
            PatientDerivedEvent.class)) {
        @Override
        public List<SingleSelection> apply(SingleSelectionProviderDAO dao) {
            return dao.getPatientDerivedEventElements();
        }
    },
    PROCEDURE_EVENT_ELEMENT_IDS(new RelatedAttribute("elements",
            ProcedureEvent.class), new RelatedAttribute(
            "relatedProcedureEventElements",
            BaseAdmissionDischardTransferEvent.class), new RelatedAttribute(
            "relatedProcedureEventElements", ClaimHeader.class),
            new RelatedAttribute("relatedProcedureEventElements",
                    HieEncounter.class)) {
        @Override
        public List<SingleSelection> apply(SingleSelectionProviderDAO dao) {
            return dao.getProcedureEventElements();
        }
    },
    SPECIALTY_GROUP_CODES(String.class, new RelatedAttribute(
            "careProviderSpecialtyGroupCode", Event.class),
            new RelatedAttribute("careProviderSpecialtyGroupCode",
                    CodifiedEvent.class), new RelatedAttribute(
                    "careProviderSpecialtyGroupCode", HieEncounter.class),
            new RelatedAttribute("careProviderSpecialtyGroupCode",
                    ClaimHeader.class), new RelatedAttribute(
                    "careProviderSpecialtyGroupCode", DiagnosticEvent.class),
            new RelatedAttribute("careProviderSpecialtyGroupCode",
                    DrugEvent.class),
            new RelatedAttribute("careProviderSpecialtyGroupCode",
                    DrugDispensingEvent.class), new RelatedAttribute(
                    "careProviderSpecialtyGroupCode", ProcedureEvent.class),
            new RelatedAttribute("careProviderSpecialtyGroupCode",
                    PatientDerivedEvent.class), new RelatedAttribute(
                    "careProviderSpecialtyGroupCode", LabEvent.class),
            new RelatedAttribute("careProviderSpecialtyGroupCode",
                    LabResultEvent.class)) {
        @Override
        public List<SingleSelection> apply(SingleSelectionProviderDAO dao) {
            return dao.getSpecialties();
        }
    };

    private final TypeDescription        typeDescription;
    private final List<RelatedAttribute> relatedAttributes;

    private static final char[]          WORD_DELIMITERS = CECollectionsUtil
                                                                 .toCharArray(WORD_DELIMITER_SET);

    private CommonSingleEnumeratedValueSetBuilder(Class<?> type,
            RelatedAttribute... relatedAttributes) {
        typeDescription = TypeDescription.getTypeDescription(type);
        if (relatedAttributes == null || relatedAttributes.length == 0) {
            this.relatedAttributes = Collections.emptyList();
        } else {
            this.relatedAttributes = CECollectionsUtil.unmodifiableList(relatedAttributes);
        }
    }

    private CommonSingleEnumeratedValueSetBuilder(
            RelatedAttribute... relatedAttributes) {
        this(Integer.TYPE, relatedAttributes);
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
}

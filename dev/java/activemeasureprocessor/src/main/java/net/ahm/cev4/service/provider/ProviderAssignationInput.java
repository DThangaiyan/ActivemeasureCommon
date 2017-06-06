package net.ahm.cev4.service.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import net.ahm.careengine.domain.measures.active.ActiveMeasure;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.member.MemberProfile;
import net.ahm.careengine.provider.ProviderAssignationEnum;
import net.ahm.cev4.activemeasures.config.ActiveMeasurePeriodType;

public class ProviderAssignationInput {
	private MemberProfile memberProfile;
	private Date measurementStartDate;
	private Date measurementEndDate;
	private ActiveMeasurePeriodType measurementPeriodType;
	private Date runDate;
	private Collection<ActiveMeasure> activeMeasures = new ArrayList<ActiveMeasure>();
	private Collection<FactLevelMeasureDenominator> factLevelMeasureDenoms = new ArrayList<FactLevelMeasureDenominator>();
	private ProviderAssignationEnum providerAssignation;


	public MemberProfile getMemberProfile() {
		return memberProfile;
	}
	public void setMemberProfile(MemberProfile memberProfile) {
		this.memberProfile = memberProfile;
	}
	public Date getMeasurementStartDate() {
		return measurementStartDate;
	}
	public void setMeasurementStartDate(Date measurementStartDate) {
		this.measurementStartDate = measurementStartDate;
	}
	public Date getMeasurementEndDate() {
		return measurementEndDate;
	}
	public void setMeasurementEndDate(Date measurementEndDate) {
		this.measurementEndDate = measurementEndDate;
	}
	public Date getRunDate() {
		return runDate;
	}
	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}
	public Collection<ActiveMeasure> getActiveMeasures() {
		return activeMeasures;
	}
	public void setActiveMeasures(Collection<ActiveMeasure> activeMeasures) {
		this.activeMeasures.addAll(activeMeasures);
	}
	public Collection<FactLevelMeasureDenominator> getFactLevelMeasureDenoms() {
		return factLevelMeasureDenoms;
	}
	public void setFactLevelMeasureDenoms(Collection<FactLevelMeasureDenominator> factLevelMeasureDenoms) {
		this.factLevelMeasureDenoms.addAll(factLevelMeasureDenoms);
	}
	public ActiveMeasurePeriodType getMeasurementPeriodType() {
		return measurementPeriodType;
	}
	public void setMeasurementPeriodType(ActiveMeasurePeriodType measurementPeriodType) {
		this.measurementPeriodType = measurementPeriodType;
	}
	public ProviderAssignationEnum getProviderAssignation() {
		return providerAssignation;
	}
	public void setProviderAssignation(ProviderAssignationEnum providerAssignation) {
		this.providerAssignation = providerAssignation;
	}

}

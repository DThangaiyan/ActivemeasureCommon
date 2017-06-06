package net.ahm.cev4.service.provider;

import java.util.Calendar;
import java.util.Date;

import net.ahm.careengine.common.Constants;
import net.ahm.careengine.domain.impl.provider.DefaultProviderAssignation;
import net.ahm.careengine.domain.measures.active.ActiveMeasure;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.member.MemberProfile;
import net.ahm.careengine.domain.provider.MemberProviderRelation;
import net.ahm.careengine.provider.ProviderAssignationEnum;
import net.ahm.careengine.util.CEDateUtil;
import net.ahm.cev4.activemeasures.config.ActiveMeasurePeriodType;

public class PcpAssignationService {
	
	private Date pcpHistoryGoLiveDate;
	
	public PcpAssignationService(){
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.SEPTEMBER, 22, 0, 0, 0);
		this.pcpHistoryGoLiveDate = cal.getTime();
	}
	
	private void assignPcpFromHistoricalRelationships(ProviderAssignationInput input, Date measurementEndDate, DefaultProviderAssignation providerAssignation){
		MemberProfile profile = input.getMemberProfile();
		if (profile.getHistoricalPcpRelationships().size() > 0){
			
			for (MemberProviderRelation mpr : profile.getHistoricalPcpRelationships()){
				Date startDate = mpr.getStartDate();
				if (startDate == null){
					startDate = Constants.INDEFINATE_PAST;
				}

				if (startDate.after(measurementEndDate)) continue;
				Date endDate = mpr.getEndDate();
				if (endDate == null){
					providerAssignation.setCareProvider(mpr.getCareProvider());
					providerAssignation.setCurrent(true);
					break;
				}
				//MPR.endDate is assumed to be exclusive
				if (measurementEndDate.before(endDate)){
					providerAssignation.setCareProvider(mpr.getCareProvider());
					break;
				}
			}
		}
	}
	
	private void assignCurrentWinnerPcp(ProviderAssignationInput input, DefaultProviderAssignation providerAssignation){
		MemberProfile profile = input.getMemberProfile();
		if (profile.getCurrentPcpRelationships().size() > 0){
			MemberProviderRelation currentMpr = null;
			for (MemberProviderRelation mpr : profile.getCurrentPcpRelationships()){
				if (mpr.getCareProvider().isFiltered()) continue;
				currentMpr = mpr;
				break;
			}
			if (currentMpr != null){
				providerAssignation.setCareProvider(currentMpr.getCareProvider());
				providerAssignation.setCurrent(true);
			}
		}
	}
	
	public void assignProviders(ProviderAssignationInput input){
		MemberProfile profile = input.getMemberProfile();
		
		Date measurementEndDate = input.getMeasurementEndDate();
		ActiveMeasurePeriodType measurementPeriodType = input.getMeasurementPeriodType();
		measurementPeriodType = (measurementPeriodType==null?ActiveMeasurePeriodType.CALENDAR:measurementPeriodType);
		Date runDate = input.getRunDate();
		if (runDate == null) runDate = new Date();
		runDate = CEDateUtil.truncateTimeFromDate(runDate);
		DefaultProviderAssignation providerAssignation = new DefaultProviderAssignation();
		providerAssignation.setPcp(true);
		providerAssignation.setProviderAssignationCode(ProviderAssignationEnum.PCP.getCode());
		
		if (runDate.after(measurementEndDate)){
			assignPcpFromHistoricalRelationships(input, measurementEndDate, providerAssignation);
			if (providerAssignation.getCareProvider() == null && measurementPeriodType.isRolling()){
				assignCurrentWinnerPcp(input, providerAssignation);
			}
		}
		else if (profile.getCurrentPcpRelationships().size() > 0){
			assignCurrentWinnerPcp(input, providerAssignation);
		}
		
		if (providerAssignation.getCareProvider() != null){
			for (ActiveMeasure measure : input.getActiveMeasures()){
				measure.setProviderAssignation(providerAssignation);
			}
			for (FactLevelMeasureDenominator flmDenom : input.getFactLevelMeasureDenoms()){
				flmDenom.setProviderAssignation(providerAssignation);
			}
		}
	}

	public Date getPcpHistoryGoLiveDate() {
		return pcpHistoryGoLiveDate;
	}

	public void setPcpHistoryGoLiveDate(Date pcpHistoryGoLiveDate) {
		this.pcpHistoryGoLiveDate = pcpHistoryGoLiveDate;
	}

}

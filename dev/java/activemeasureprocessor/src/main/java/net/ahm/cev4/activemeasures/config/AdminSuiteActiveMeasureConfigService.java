package net.ahm.cev4.activemeasures.config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.ahm.careengine.dvo.ProductType;
import net.ahm.careengine.util.CEDateUtil;
import net.ahm.cev4.activemeasures.config.adminsuite.ActiveMeasureAccountPackage;
import net.ahm.cev4.activemeasures.config.adminsuite.ActiveMeasureMeasurementPeriod;
import net.ahm.cev4.activemeasures.dao.config.adminsuite.AdminSuiteActiveMeasureConfigDAO;
import net.ahm.cev4.util.TimeOffset;

public class AdminSuiteActiveMeasureConfigService implements ActiveMeasureConfigService {
	private static final Logger log = Logger.getLogger(AdminSuiteActiveMeasureConfigService.class);
	
	protected AdminSuiteActiveMeasureConfigDAO activeMeasureConfigDao;

	@Override
	public List<ActiveMeasureRunSetting> getActiveMeasureRunSettings(int bussSupplierId, Date runDate, String runMode, Collection<ProductType> products) {
		//load supplier program, acctpkg, box settings
		Collection<ActiveMeasureAccountPackage> amAcctPkgs = activeMeasureConfigDao.loadActiveMeasureAccountPackages(bussSupplierId, products);
		return buildAmRunSettings(bussSupplierId, amAcctPkgs, runDate, runMode);
	}

	private List<ActiveMeasureRunSetting> buildAmRunSettings(int bussSupplierId, Collection<ActiveMeasureAccountPackage> amAcctPkgs, Date runDate, String runMode){
		List<ActiveMeasureRunSetting> _returnList = new ArrayList<ActiveMeasureRunSetting>();
		if (amAcctPkgs == null || amAcctPkgs.isEmpty()) return _returnList;
		Calendar runCal = Calendar.getInstance();
		runCal.setTime(runDate);
		runCal = CEDateUtil.truncateTimeFromDate(runCal);
		//boolean rollingExists = false;
		for (ActiveMeasureAccountPackage amAcctPkg : amAcctPkgs){
			Collection<? extends ActiveMeasureRunSetting> configRuns = buildAmStdRunSettings(bussSupplierId, amAcctPkg, runDate, runCal, runMode);
			_returnList.addAll(configRuns);
			//if (amAcctPkg.isRollingMode()) rollingExists = true; //to be used after loop
		}
		
		_returnList = dedupeAmStdRunSettings(_returnList);
		
		/*if (rollingExists){
			ActiveMeasureRunSetting rollingRun = buildAmRollingRunSettings(bussSupplierId, amAcctPkgs, runDate, runCal, runMode);
			if (rollingRun != null) _returnList.add(rollingRun);
		}*/
		
		if (log.isDebugEnabled()){
			log.debug("buildAmRunSettings()");
			for (ActiveMeasureRunSetting amRun : _returnList){
				log.debug(amRun);
			}
		}
		
		return _returnList;
	}
	
	private List<ActiveMeasureRunSetting> dedupeAmStdRunSettings(List<ActiveMeasureRunSetting> amRuns){
		List<ActiveMeasureRunSetting> _retList = new ArrayList<ActiveMeasureRunSetting>();
		Map<ProductType, Map<ActiveMeasurePeriodType, Map<Long, Map<Long, DefaultActiveMeasureRunSetting>>>> prodMap = new HashMap<ProductType, Map<ActiveMeasurePeriodType, Map<Long, Map<Long, DefaultActiveMeasureRunSetting>>>>();
		//Map<Long, Map<Long, DefaultActiveMeasureRunSetting>> tempMap = new HashMap<Long, Map<Long, DefaultActiveMeasureRunSetting>>();
		for (ActiveMeasureRunSetting amRun : amRuns){
			Map<ActiveMeasurePeriodType, Map<Long, Map<Long, DefaultActiveMeasureRunSetting>>> periodMap = prodMap.get(amRun.getProduct());
			if (periodMap == null){
				periodMap = new HashMap<ActiveMeasurePeriodType, Map<Long, Map<Long, DefaultActiveMeasureRunSetting>>>();
				prodMap.put(amRun.getProduct(), periodMap);
			}
			Map<Long, Map<Long, DefaultActiveMeasureRunSetting>> tempMap = periodMap.get(amRun.getMeasurementPeriodType());
			if (tempMap == null){
				tempMap = new HashMap<Long, Map<Long, DefaultActiveMeasureRunSetting>>();
				periodMap.put(amRun.getMeasurementPeriodType(), tempMap);
			}
			Long startTime = amRun.getMeasurementStartDate().getTime();
			Long endTime = amRun.getMeasurementEndDate().getTime();
			
			Map<Long, DefaultActiveMeasureRunSetting> startMap = tempMap.get(startTime);
			if (startMap == null){
				startMap = new HashMap<Long, DefaultActiveMeasureRunSetting>();
				tempMap.put(startTime, startMap);
			}
			DefaultActiveMeasureRunSetting sharedRun = startMap.get(endTime);
			if (sharedRun == null){
				sharedRun = new DefaultActiveMeasureRunSetting();
				sharedRun.setProduct(amRun.getProduct());
				sharedRun.setMeasurementPeriodType(amRun.getMeasurementPeriodType());
				sharedRun.setMeasurementStartDate(amRun.getMeasurementStartDate());
				sharedRun.setMeasurementEndDate(amRun.getMeasurementEndDate());
				sharedRun.setRunDate(amRun.getRunDate());
				sharedRun.setRunMode(amRun.getRunMode());
				sharedRun.setSupplierId(amRun.getSupplierId());
				sharedRun.setLatestRun(amRun.isLatestRun());
				sharedRun.setMonthlyLatestRun(amRun.isMonthlyLatestRun());
				
				startMap.put(endTime, sharedRun);
				_retList.add(sharedRun);
			}
			
			sharedRun.getActiveMeasurePackage().addAll(amRun.getActiveMeasurePackage());
		}
		
		return _retList;
	}
	
	private Collection<? extends ActiveMeasureRunSetting> buildAmStdRunSettings(int bussSupplierId, ActiveMeasureAccountPackage acctPkg, Date runDate, Calendar runCal, String runMode) {
		List<ActiveMeasureRunSetting> _returnList = new ArrayList<ActiveMeasureRunSetting>();
		
		for (ActiveMeasureMeasurementPeriod measurementPeriod : acctPkg.getMeasurementPeriods()){
			Collection<? extends ActiveMeasureRunSetting> configRuns = null;
			switch(measurementPeriod.getMeasurementPeriodType()){
			case CALENDAR:
				configRuns = buildAmCalendarRunSettings(bussSupplierId, acctPkg, measurementPeriod, runDate, runCal, runMode);
				break;
			case ROLLING:
				configRuns = buildAmRollingDailyRunSettings(bussSupplierId, acctPkg, measurementPeriod, runDate, runCal, runMode);
				break;
			case ROLLING_QTRLY:
				configRuns = buildAmRollingQuarterlyRunSettings(bussSupplierId, acctPkg, measurementPeriod, runDate, runCal, runMode);
				break;
			case ROLLING_MONTHLY:
				configRuns = buildAmRollingMonthlyRunSettings(bussSupplierId, acctPkg, measurementPeriod, runDate, runCal, runMode);
				break;
			}
			if (configRuns != null) _returnList.addAll(configRuns);
			
		}
		
		return _returnList;
	}

    private Collection<? extends ActiveMeasureRunSetting> buildAmCalendarRunSettings(int bussSupplierId, ActiveMeasureAccountPackage acctPkg, ActiveMeasureMeasurementPeriod measurementPeriod, Date runDate, Calendar runCal, String runMode) {
        List<ActiveMeasureRunSetting> _returnList = new ArrayList<ActiveMeasureRunSetting>();
        assert measurementPeriod.getMeasurementPeriodType() == ActiveMeasurePeriodType.CALENDAR;
        
        if (measurementPeriod.getRunStartDate() != null){
        	if (measurementPeriod.getRunStartDate().after(runCal.getTime())) return _returnList;
        }
        if (measurementPeriod.getRunEndDate() != null){
        	if (measurementPeriod.getRunEndDate().before(runCal.getTime())) return _returnList;
        }
        
        TimeOffset runLag = measurementPeriod.getRunLag();

        int runYear = runCal.get(Calendar.YEAR);

        Calendar tempStartCal = Calendar.getInstance();
        tempStartCal.setTime(measurementPeriod.getMeasurementStartDateTemplate());
        int tempStartYear = tempStartCal.get(Calendar.YEAR);

        Calendar tempEndCal = Calendar.getInstance();
        tempEndCal.setTime(measurementPeriod.getMeasurementEndDateTemplate());
        int tempEndYear = tempEndCal.get(Calendar.YEAR);

        int yearDiff = tempEndYear - tempStartYear;

        int decrementingRunYear = runYear;
        while (true) {
            tempEndCal.set(Calendar.YEAR, decrementingRunYear);
            tempStartCal.set(Calendar.YEAR, decrementingRunYear - yearDiff);
            Calendar tempEndCalWithLag = runLag != null ? runLag
                    .plus(tempEndCal) : tempEndCal;
            if (runCal.after(tempEndCalWithLag) || runCal.before(tempStartCal)) {
                break;
            }
            DefaultActiveMeasureRunSetting amRun = new DefaultActiveMeasureRunSetting();
            amRun.setProduct(acctPkg.getProduct());
            //amRun.setAccountPackageId(acctPkg.getAccountPackageId());
            amRun.setSupplierId(bussSupplierId);
            amRun.setMeasurementPeriodType(measurementPeriod.getMeasurementPeriodType());
            amRun.setMeasurementStartDate(tempStartCal.getTime());
            amRun.setMeasurementEndDate(tempEndCal.getTime());
            amRun.setRunDate(runDate);
            amRun.setRunMode(runMode);
            amRun.setLatestRun(false);
            amRun.setMonthlyLatestRun(false);
            identifyAndSetPackage(amRun, acctPkg, runCal);
            if (!amRun.getActiveMeasurePackage().isEmpty()) _returnList.add(amRun);
            decrementingRunYear--;
        }
        int incrementingRunYear = runYear + 1;
        while (true) {
            tempEndCal.set(Calendar.YEAR, incrementingRunYear);
            tempStartCal.set(Calendar.YEAR, incrementingRunYear - yearDiff);
            Calendar tempEndCalWithLag = runLag != null ? runLag
                    .plus(tempEndCal) : tempEndCal;
            if (runCal.before(tempStartCal) || runCal.after(tempEndCalWithLag)) {
                break;
            }
            DefaultActiveMeasureRunSetting amRun = new DefaultActiveMeasureRunSetting();
            amRun.setProduct(acctPkg.getProduct());
            //amRun.setAccountPackageId(acctPkg.getAccountPackageId());
            amRun.setMeasurementPeriodType(measurementPeriod.getMeasurementPeriodType());
            amRun.setMeasurementStartDate(tempStartCal.getTime());
            amRun.setMeasurementEndDate(tempEndCal.getTime());
            amRun.setRunDate(runDate);
            amRun.setRunMode(runMode);
            amRun.setLatestRun(false);
            amRun.setMonthlyLatestRun(false);
            identifyAndSetPackage(amRun, acctPkg, runCal);
            if (!amRun.getActiveMeasurePackage().isEmpty()) _returnList.add(amRun);
            incrementingRunYear++;
        }

        return _returnList;

    }
    
    private Collection<? extends ActiveMeasureRunSetting> buildAmRollingDailyRunSettings(int bussSupplierId, ActiveMeasureAccountPackage acctPkg, ActiveMeasureMeasurementPeriod measurementPeriod, Date runDate, Calendar runCal, String runMode) {
    	List<ActiveMeasureRunSetting> _returnList = new ArrayList<ActiveMeasureRunSetting>();
    	assert measurementPeriod.getMeasurementPeriodType() == ActiveMeasurePeriodType.ROLLING;
    	
    	if (measurementPeriod.getRunStartDate() != null){
        	if (measurementPeriod.getRunStartDate().after(runCal.getTime())) return _returnList;
        }
        if (measurementPeriod.getRunEndDate() != null){
        	if (measurementPeriod.getRunEndDate().before(runCal.getTime())) return _returnList;
        }

		// We want to consider data that comes in on the measurement end day.
        // Date measurementEndDt = CEDateUtil.truncateTimeFromDate(runDate);
		Date measurementStartDt = CEDateUtil.addDays(CEDateUtil.add(runDate, Calendar.MONTH, -12), 1);
    	
        DefaultActiveMeasureRunSetting amRun = new DefaultActiveMeasureRunSetting();
		amRun.setProduct(acctPkg.getProduct());
		amRun.setMeasurementPeriodType(measurementPeriod.getMeasurementPeriodType());
		amRun.setMeasurementStartDate(measurementStartDt);
        amRun.setMeasurementEndDate(runDate);
        amRun.setRunDate(runDate);
        amRun.setRunMode(runMode);
        amRun.setSupplierId(bussSupplierId);
        amRun.setLatestRun(false);
        amRun.setMonthlyLatestRun(false);
        identifyAndSetPackage(amRun, acctPkg, runCal);
		
		if (!amRun.getActiveMeasurePackage().isEmpty()) _returnList.add(amRun);
		
		return _returnList;

	}
    
    private Collection<? extends ActiveMeasureRunSetting> buildAmRollingQuarterlyRunSettings(int bussSupplierId, ActiveMeasureAccountPackage acctPkg, ActiveMeasureMeasurementPeriod measurementPeriod, Date runDate, Calendar runCal, String runMode) {
    	List<ActiveMeasureRunSetting> _returnList = new ArrayList<ActiveMeasureRunSetting>();
    	assert measurementPeriod.getMeasurementPeriodType() == ActiveMeasurePeriodType.ROLLING_QTRLY;
    	
    	if (measurementPeriod.getRunStartDate() != null){
        	if (measurementPeriod.getRunStartDate().after(runCal.getTime())) return _returnList;
        }
        if (measurementPeriod.getRunEndDate() != null){
        	if (measurementPeriod.getRunEndDate().before(runCal.getTime())) return _returnList;
        }
    	
    	int runMonth = runCal.get(Calendar.MONTH);
    	int periodEndMonth = -1;
    	int periodEndYear = runCal.get(Calendar.YEAR);
    	if (runMonth == Calendar.JANUARY) periodEndYear = periodEndYear - 1;
    	switch(runMonth){
    	case Calendar.FEBRUARY:
    	case Calendar.MARCH:
    	case Calendar.APRIL: periodEndMonth = Calendar.JANUARY; break;
    	case Calendar.MAY:
    	case Calendar.JUNE:
    	case Calendar.JULY: periodEndMonth = Calendar.APRIL; break;
    	case Calendar.AUGUST:
    	case Calendar.SEPTEMBER:
    	case Calendar.OCTOBER: periodEndMonth = Calendar.JULY; break;
    	case Calendar.NOVEMBER:
    	case Calendar.DECEMBER:
    	case Calendar.JANUARY: periodEndMonth = Calendar.OCTOBER; break;
    	default:
    		return _returnList;
    	}
    	    	
    	
    	Calendar measurementEndCal = Calendar.getInstance();
    	measurementEndCal = CEDateUtil.truncateTimeFromDate(measurementEndCal);
    	measurementEndCal.set(periodEndYear, periodEndMonth, 1);
    	measurementEndCal.add(Calendar.DATE, -1); //LAST DAY of the QTR
    	
		Date measurementEndDt = measurementEndCal.getTime();
		Date measurementStartDt = CEDateUtil.addDays(CEDateUtil.add(measurementEndDt, Calendar.MONTH, -12), 1);
    	
        DefaultActiveMeasureRunSetting amRun = new DefaultActiveMeasureRunSetting();
		amRun.setProduct(acctPkg.getProduct());
		amRun.setMeasurementPeriodType(measurementPeriod.getMeasurementPeriodType());
		amRun.setMeasurementStartDate(measurementStartDt);
        amRun.setMeasurementEndDate(measurementEndDt);
        amRun.setRunDate(runDate);
        amRun.setRunMode(runMode);
        amRun.setSupplierId(bussSupplierId);
        amRun.setLatestRun(false);
        amRun.setMonthlyLatestRun(false);
        identifyAndSetPackage(amRun, acctPkg, runCal);
		
		if (!amRun.getActiveMeasurePackage().isEmpty()) _returnList.add(amRun);
		
		return _returnList;

	}
    
    private Collection<? extends ActiveMeasureRunSetting> buildAmRollingMonthlyRunSettings(int bussSupplierId, ActiveMeasureAccountPackage acctPkg, ActiveMeasureMeasurementPeriod measurementPeriod, Date runDate, Calendar runCal, String runMode) {
    	List<ActiveMeasureRunSetting> _returnList = new ArrayList<ActiveMeasureRunSetting>();
    	assert measurementPeriod.getMeasurementPeriodType() == ActiveMeasurePeriodType.ROLLING_MONTHLY;
    	
    	if (measurementPeriod.getRunStartDate() != null){
        	if (measurementPeriod.getRunStartDate().after(runCal.getTime())) return _returnList;
        }
        if (measurementPeriod.getRunEndDate() != null){
        	if (measurementPeriod.getRunEndDate().before(runCal.getTime())) return _returnList;
        }
    	
    	Calendar measurementEndCal = (Calendar) runCal.clone();
    	measurementEndCal.set(Calendar.DATE, 1);
    	measurementEndCal.add(Calendar.DATE, -1); //LAST day of prev month
    	
		Date measurementEndDt = measurementEndCal.getTime();
		Date measurementStartDt = CEDateUtil.addDays(CEDateUtil.add(measurementEndDt, Calendar.MONTH, -12), 1);
    	
        DefaultActiveMeasureRunSetting amRun = new DefaultActiveMeasureRunSetting();
		amRun.setProduct(acctPkg.getProduct());
		amRun.setMeasurementPeriodType(measurementPeriod.getMeasurementPeriodType());
		amRun.setMeasurementStartDate(measurementStartDt);
        amRun.setMeasurementEndDate(measurementEndDt);
        amRun.setRunDate(runDate);
        amRun.setRunMode(runMode);
        amRun.setSupplierId(bussSupplierId);
        amRun.setLatestRun(false);
        amRun.setMonthlyLatestRun(false);
        identifyAndSetPackage(amRun, acctPkg, runCal);
		
		if (!amRun.getActiveMeasurePackage().isEmpty()) _returnList.add(amRun);
		
		return _returnList;

	}
	
	private void identifyAndSetPackage(DefaultActiveMeasureRunSetting amRun, ActiveMeasureAccountPackage acctPkg, Calendar runCal){
		List<Long> measures = acctPkg.getActiveMeasurePackage(amRun.getMeasurementStartDate(), amRun.getMeasurementEndDate(), amRun.getMeasurementPeriodType(), runCal.getTime());
		amRun.setActiveMeasureIds(measures);
	}
		
	
	public AdminSuiteActiveMeasureConfigDAO getActiveMeasureConfigDao() {
		return activeMeasureConfigDao;
	}

	public void setActiveMeasureConfigDao(
			AdminSuiteActiveMeasureConfigDAO activeMeasureConfigDao) {
		this.activeMeasureConfigDao = activeMeasureConfigDao;
	}

}

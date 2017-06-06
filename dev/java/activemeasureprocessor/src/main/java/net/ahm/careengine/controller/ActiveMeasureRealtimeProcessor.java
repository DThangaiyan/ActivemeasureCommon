package net.ahm.careengine.controller;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.ahm.careengine.activemeasure.ActiveMeasureCommandConfiguration;
import net.ahm.careengine.activemeasure.ActiveMeasureCommandInput;
import net.ahm.careengine.activemeasure.ActiveMeasureCommandOutput;
import net.ahm.careengine.activemeasure.ActiveMeasureEngine;
import net.ahm.careengine.activemeasure.ActiveMeasureEngineSupplier;
import net.ahm.careengine.activemeasure.ActiveMeasurePersistenceCommandConfiguration;
import net.ahm.careengine.activemeasure.ActiveMeasurePersistenceCommandInput;
import net.ahm.careengine.activemeasure.ActiveMeasurePersistenceCommandOutput;
import net.ahm.careengine.activemeasure.ActiveMeasureRealtimeRun;
import net.ahm.careengine.activemeasure.ActiveMeasureRunSettingDVO;
import net.ahm.careengine.activemeasure.MemberActiveMeasureDVO;
import net.ahm.careengine.bom.CEException;
import net.ahm.careengine.bom.DataType;
import net.ahm.careengine.bom.MHRBatchDVO;
import net.ahm.careengine.bom.member.MemberHealthRecordIF;
import net.ahm.careengine.bom.member.MemberLockData;
import net.ahm.careengine.command.CommandIF;
import net.ahm.careengine.common.ActiveMeasuresPerfLog;
import net.ahm.careengine.common.CommonServicesAdapter;
import net.ahm.careengine.common.DelegateMessageParser;
import net.ahm.careengine.dao.SupplierDAO;
import net.ahm.careengine.dao.SupplierRunDAO;
import net.ahm.careengine.dao.output.activemeasure.ActiveMeasureOutputDAO;
import net.ahm.careengine.dao.person.PersonDataDAO;
import net.ahm.careengine.domain.comorbidclinicalcondition.RiskStratificationMeasureWeightsBuilder;
import net.ahm.careengine.domain.eligibility.PlanEligibility;
import net.ahm.careengine.domain.impl.comorbidclinicalcondition.CacheUsingActiveMeasureHCCBuilder;
import net.ahm.careengine.domain.impl.comorbidclinicalcondition.DefaultHierarchicalClinicalConditionCategoryBuilder;
import net.ahm.careengine.domain.impl.comorbidclinicalcondition.DefaultRiskMeasureWeightsBuilder;
import net.ahm.careengine.domain.impl.eligibility.SimpleCompositeEligibility;
import net.ahm.careengine.domain.impl.measure.active.CacheUsingActiveMeasureClassifierBuilder;
import net.ahm.careengine.domain.impl.measure.active.DefaultActiveMeasureBuilder;
import net.ahm.careengine.domain.impl.measure.active.DefaultFactLevelMeasureBuilder;
import net.ahm.careengine.domain.impl.member.DefaultMemberProfile;
//import net.ahm.careengine.domain.impl.provider.DefaultProviderAssignation;
import net.ahm.careengine.domain.justification.RuleJustification;
import net.ahm.careengine.domain.mapper.CEv2Mapper;
import net.ahm.careengine.domain.measures.active.ActiveMeasure;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureNumerator;
import net.ahm.careengine.domain.measures.active.impl.MemberActiveMeasure;
//import net.ahm.careengine.domain.measures.active.impl.MemberActiveMeasure;
import net.ahm.careengine.domain.member.MemberProfile;
//import net.ahm.careengine.domain.provider.MemberProviderRelation;
//import net.ahm.careengine.domain.provider.ProviderAssignation;
import net.ahm.careengine.domain.refactor.DefaultMemberProfileRefactorer;
import net.ahm.careengine.dvo.ProductType;
import net.ahm.careengine.message.CareEngineMessage;
import net.ahm.careengine.message.CareEngineMessageIF;
import net.ahm.careengine.message.ExecutionMode;
import net.ahm.careengine.message.Status;
import net.ahm.careengine.message.SupplierBatchDVO;
import net.ahm.careengine.message.SupplierRunSetting;
import net.ahm.careengine.provider.ProviderAssignationEnum;
import net.ahm.careengine.schema.messages.proto.activemeasure.RunActiveMeasures;
import net.ahm.careengine.schema.messages.proto.activemeasure.RunStandardActiveMeasures;
import net.ahm.careengine.schema.types.common.CareEngineMember;
import net.ahm.careengine.schema.types.proto.activemeasure.AmAccountPackage;
import net.ahm.careengine.util.CEDateUtil;
import net.ahm.careengine.util.CacheManagerImpl;
import net.ahm.careengine.util.ICacheManager;
import net.ahm.careengine.util.SimpleProfilerUtil;
import net.ahm.careengine.util.ThreadLocalSimpleDateFormat;
import net.ahm.cev4.activemeasures.config.ActiveMeasureConfigService;
import net.ahm.cev4.activemeasures.config.ActiveMeasurePeriodType;
import net.ahm.cev4.activemeasures.config.ActiveMeasureRunSetting;
import net.ahm.cev4.activemeasures.config.BoxManager;
import net.ahm.cev4.activemeasures.config.DefaultActiveMeasureRunSetting;
import net.ahm.cev4.activemeasures.dao.config.batch.ActiveMeasureBatchConfigDao;
import net.ahm.cev4.service.provider.ProviderAssignationInput;
import net.ahm.cev4.service.provider.ProviderAssignationService;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class ActiveMeasureRealtimeProcessor implements MessageProcessorIF, DelegateMessageParser {
    protected final static Logger                                                                                                                  LOGGER                         = Logger.getLogger(ActiveMeasureRealtimeProcessor.class);
    protected final static Logger PERF_LOGGER = Logger.getLogger("performance");

    protected SupplierRunDAO                                                                                                                       supplierRunDAO;
    protected CommonServicesAdapter                                                                                                                csa                            = new CommonServicesAdapter();

    /**
     * Instantiated by spring framework
     */
    protected ActiveMeasureEngineSupplier                                                                                                          engineSupplier;

    /**
     * Instantiated by spring framework
     */
    protected CommandIF<ActiveMeasurePersistenceCommandInput, ActiveMeasurePersistenceCommandOutput, ActiveMeasurePersistenceCommandConfiguration> activeMeasurePersistenceCommand;
    protected ActiveMeasureConfigService                                                                                                           activeMeasureConfigService     = null;
    protected BoxManager                                                                                                                           boxManager;
    private JAXBContext                                                                                                                            amJAXBContext                  = null;
    //currently being set in init() method rather then from spring beans.xml
    private CEv2Mapper                                                                                                                             modelMapper                    = null;
    private DefaultMemberProfileRefactorer                                                                                                         memberProfileRefactorer        = new DefaultMemberProfileRefactorer();
    private ActiveMeasureOutputDAO                                                                                                                 activeMeasureOutputDAO;
    protected SupplierDAO                                                                                                                          supplierDAO;
    private Date                                                                                                                                   adminSuiteToCERefreshDate;
    protected Date                                                                                                                                 ruleRefreshDate;
    protected CommandIF<ActiveMeasurePersistenceCommandInput, ActiveMeasurePersistenceCommandOutput, ActiveMeasurePersistenceCommandConfiguration> justificationPersistenceCommand;
    protected int                                                                                                                                  rollingModeSkipToleranceInDays = 5;
    protected boolean                                                                                                                              neverSkipRollingMode           = false;
    private PersonDataDAO                                                                                                                          personDataDAO;
    private ActiveMeasureBatchConfigDao                                                                                                            activeMeasureBatchConfigDao;
    private ProviderAssignationService providerAssignationService;

    @Override
    @Transactional(propagation=Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED,
    rollbackFor={InterruptedException.class, RuntimeException.class }, timeout=7500)
    public Object processMessage(CareEngineMessageIF messageWrapper) throws InterruptedException {
        CareEngineMessage msg = (CareEngineMessage) messageWrapper.getPayload();
        ActiveMeasuresPerfLog perfLogHldr = new ActiveMeasuresPerfLog(ExecutionMode.REALTIMEACTIVEMEASURE.name());
        long start = System.currentTimeMillis();
        SimpleProfilerUtil.reset();
        Long[] members = msg.getMemberIds();
        if (members != null && members.length > 0)
        	perfLogHldr.setCeRunScheduleId(members[0]); //memberid
        else
        	throw new CEException("Member not specified in request.");
        try{
        
	        Object originalRequest = msg.getOriginalRequest();
	
	        SupplierRunSetting srs = getSupplierRunSetting(msg);
	        int accountId = srs.getRunSettings().getAccountId();
	        long endSRS = System.currentTimeMillis();
	        perfLogHldr.setSrsTime(endSRS - start);
	        
	        perfLogHldr.setSupplierBatchId(srs.getAhmSupplierId());
	        LOGGER.info("processMessage(): supp: " + srs.getAhmSupplierId() + ", mid:" + members[0] + ", runDate: " + msg.getCeRunDate());
	        List<ActiveMeasureRunSetting> amRunSettings = null;
	
	        String runMode = "RT";
	        boolean returnResults = true;
	        if (originalRequest instanceof RunActiveMeasures){
	            RunActiveMeasures custReq = (RunActiveMeasures)originalRequest;
	            returnResults = custReq.getRunConfig().isReturnResults();
	            amRunSettings = getCustomActiveMeasureRunSettings(srs.getAhmSupplierId(), custReq, msg.getCeRunDate());
	            runMode = "CS";
	        } else {
				Set<ProductType> runProducts = new HashSet<ProductType>();
	            if (originalRequest instanceof RunStandardActiveMeasures) {
	                RunStandardActiveMeasures stdReq = (RunStandardActiveMeasures) originalRequest;
	                returnResults = stdReq.getStandardRunConfig().isReturnResults();
	                for (String prod : stdReq.getStandardRunConfig().getProduct()){
	                	ProductType runProduct = ProductType.getProductTypeByCode(prod);
	                	if (runProduct != null) runProducts.add(runProduct);
	                }
	            }
	            List<Long> memberIds = msg.getSupplierBatchDVO().getMemberIds();
	            amRunSettings = getActiveMeasureRunSettings(accountId, srs.getAhmSupplierId(), memberIds.get(0), msg.getCeRunDate(), runMode, runProducts);
	        }
	        LOGGER.info("runMode: " + runMode);
	        long endAmSettings = System.currentTimeMillis();
	        perfLogHldr.setAmrsTime(endAmSettings - endSRS);
	
	        if (amRunSettings == null || amRunSettings.isEmpty()){
	            return null;
	        }
	
	        List<ActiveMeasureRunSettingDVO> runSettingDvos = doSkipLogic(msg, amRunSettings, runMode);
	        List<Long> executionMembers = extractExecutionMemberIds(runSettingDvos);
	        int totalExecutionMembers = executionMembers.size();
	        long endSkipLogic = System.currentTimeMillis();
	        perfLogHldr.setSkipLogicTime(endSkipLogic - endAmSettings);
	
	        if (LOGGER.isInfoEnabled()) LOGGER.info("runMembers: " + Arrays.toString(msg.getMemberIds()) + " [" + totalExecutionMembers + "/" + msg.getMemberIds().length + "]");
	
	        if (totalExecutionMembers == 0){
	        	return prepareResponse(runSettingDvos, false);
	        }
	
	        long startODS = System.currentTimeMillis();
	        Set<ProductType> executionProducts = extractExecutionProducts(runSettingDvos);
	        Map<ProductType, ProviderAssignationEnum> provAssigs = srs.getProductProviderAssignations(executionProducts);
	        List<MemberHealthRecordIF> mhrInLegacyFormat = fetchMemberData(msg, executionProducts, provAssigs);
	        long endODS = System.currentTimeMillis();
	        perfLogHldr.setOdsGetTime(endODS - startODS);
	        //fakeFeedbackData(mhrInLegacyFormat);
	        //fakeMprData(mhrInLegacyFormat);
	
	        List<DefaultMemberProfile> memberProfiles = mapLegacyData(mhrInLegacyFormat);
	
	        for (DefaultMemberProfile profile : memberProfiles){
	            refactor(profile);
	        }
	        long endMapAndRefactor = System.currentTimeMillis();
	        perfLogHldr.setMapAndRefactorTime(endMapAndRefactor - endODS);
	        
	        this.executeRules(srs.getRunSettings().getAccountId(), runSettingDvos, memberProfiles, srs, provAssigs);
	        long ruleEnd = System.currentTimeMillis();
	        perfLogHldr.setRulesRunTime(ruleEnd - endMapAndRefactor);
	        
	        if (doPersist(originalRequest)){
	        	if (totalExecutionMembers > 0){
	        		long saveStart = System.currentTimeMillis();
	        		saveResults(msg, runSettingDvos, srs.getRunSettings().isSaveActiveMeasureJustification());
	        		long saveEnd = System.currentTimeMillis();
	                perfLogHldr.setResultsSaveTime(saveEnd - saveStart);
	        	}
	        	else{
	        		LOGGER.warn("Skip Save Operation");
	        	}
	        }
	        
	        return prepareResponse(runSettingDvos, returnResults);
        }
        finally{
        	if (PERF_LOGGER.isInfoEnabled()){
        		long end = System.currentTimeMillis();
                perfLogHldr.setTotalRunTime(end - start);
                PERF_LOGGER.info(perfLogHldr.toString() + " memberid:" + members[0]);
        	}
        }
    }
    
    protected void saveResults(CareEngineMessage msg, List<ActiveMeasureRunSettingDVO> runSettingDvos, boolean justificationPersistanceFlag){
    	LOGGER.info("Inside saveResults()");
    	if (runSettingDvos == null || runSettingDvos.size() == 0){
    		LOGGER.info("saveResults(): Input is Empty");
    		return;
    	}
    	
    	/*for (ActiveMeasureRunSettingDVO runSettingDvo : runSettingDvos){
    		runSettingDvo.setSupplierId(msg.getAhmSupplierId());
    		ActiveMeasureRunSetting rs = runSettingDvo.getActiveMeasureRunSetting();
    		if (rs instanceof DefaultActiveMeasureRunSetting){
    			((DefaultActiveMeasureRunSetting) rs).setRunDate(msg.getCeRunDate());
    		}
    	}*/
    	
    	List<ActiveMeasureRunSettingDVO> finalRunSettingResults = new ArrayList<ActiveMeasureRunSettingDVO>();
    	for (ActiveMeasureRunSettingDVO runSettingDvo : runSettingDvos){
			ActiveMeasureRunSettingDVO finalRunSettingDvo = null;
			for (MemberActiveMeasureDVO memberDvo : runSettingDvo.getMemberActiveMeasures()){
				if (runSettingDvo.getSkippedMembers().contains(memberDvo.getMemberId())) continue;
				if (finalRunSettingDvo == null){
					finalRunSettingDvo = new ActiveMeasureRunSettingDVO();
					finalRunSettingDvo.setActiveMeasureRunSetting(runSettingDvo.getActiveMeasureRunSetting());
					finalRunSettingResults.add(runSettingDvo);
				}
				finalRunSettingDvo.addMemberActiveMeasure(memberDvo);
			}
		}
    	
    	String runMode = "RT";
    	if (msg.getOriginalRequest() instanceof RunActiveMeasures){
    		runMode = "CS";
    	}

    	if (finalRunSettingResults.size() == 0){
    		LOGGER.info("saveResults(): No member execution to save");
    		return;
    	}
		ActiveMeasurePersistenceCommandInput tobePersisted = new ActiveMeasurePersistenceCommandInput();
		tobePersisted.setRunId(0);
		tobePersisted.setAccountId(msg.getSupplierRunSetting().getRunSettings().getAccountId());
        tobePersisted.setRunDate(msg.getCeRunDate());
        tobePersisted.setRunStatus(Status.Complete.name());
        tobePersisted.setRunMode(runMode);
        tobePersisted.setRunSettingResults(finalRunSettingResults);
        tobePersisted.setSaveJustification(justificationPersistanceFlag);
        try {
            activeMeasurePersistenceCommand.execute(tobePersisted, new ActiveMeasurePersistenceCommandOutput(), new ActiveMeasurePersistenceCommandConfiguration());
        } catch (Exception e) {
            LOGGER.error(e);
            throw new CEException("Error executing persistence command", e);
        }
        /*try {
            justificationPersistenceCommand.execute(tobePersisted, null, null);
        } catch (Exception e) {
            log.error(e);
            throw new CEException("Error executing justification persistence command", e);
        }*/
    }

    protected void executeRules(long accountId,
            List<ActiveMeasureRunSettingDVO> runSettingDvos,
            List<? extends MemberProfile> memberProfiles, SupplierRunSetting srs,
            Map<ProductType, ProviderAssignationEnum> provAssigs) {
        LOGGER.info("In executeRules");
        Map<Long, MemberProfile> profileMap = new HashMap<Long, MemberProfile>();
        for (MemberProfile profile : memberProfiles) {
            profileMap.put(profile.getMemberInfo().getFactId(), profile);
        }

        for (ActiveMeasureRunSettingDVO runSettingDvo : runSettingDvos) {
        	long amRunStart = System.currentTimeMillis();
            ActiveMeasureRunSetting amRunSetting = runSettingDvo.getActiveMeasureRunSetting();
            ProviderAssignationEnum provAssig = provAssigs.get(amRunSetting.getProduct());
            Date ceRunDate = amRunSetting.getRunDate();
            LOGGER.info("AmRun: " + amRunSetting + ", provAssig: " + provAssig);
            Date measurementStartDt = amRunSetting.getMeasurementStartDate();
			Date measurementEndDt = amRunSetting.getMeasurementEndDate();
			if (measurementEndDt == null){
				LOGGER.warn("MeasurementEndDt is NULL. Control should never reach here since new changes ensures CE UI will always populate the dates in CE UI layer.");
				measurementEndDt = CEDateUtil.truncateTimeFromDate(ceRunDate);
	        	measurementStartDt = CEDateUtil.addDays(CEDateUtil.add(measurementEndDt, Calendar.MONTH, -12), 1);
	        }
			
			for (MemberActiveMeasureDVO memberDvo : runSettingDvo.getMemberActiveMeasures()) {
                MemberProfile profile = profileMap.get(memberDvo.getMemberId());
                memberDvo.setMemberInfo(profile.getMemberInfo());
                logMemberProfile(profile);
                
                MemberProfile boxedProfile = boxManager.applyBox(profile,
                        amRunSetting.getBoxParameters());
                ActiveMeasureCommandOutput output = executeRules(
                        amRunSetting, boxedProfile, measurementStartDt, measurementEndDt, ceRunDate);
                if (LOGGER.isInfoEnabled()){
	                if (output.getActiveMeasures().isEmpty()) LOGGER.info("No AM fired");
	                if (output.getFactLevelMeasureDenominators().isEmpty()) LOGGER.info("No FLM fired");
                }
                applyPackage(amRunSetting, output, memberDvo);
                //TODO enhance assignProviders to consider ProviderSetting per supplier-product. In addition change to accommodate CASE & CDM also.
                assignProviders(profile, amRunSetting, measurementStartDt, measurementEndDt, memberDvo, srs, provAssig);
            }
			SimpleProfilerUtil.recordTimeInMillis(amRunSetting.getMeasurementPeriodType().getCode(), amRunStart);
        }
        return;
    }

    private void assignProviders(MemberProfile profile,
            ActiveMeasureRunSetting amRunSetting, Date measurementStartDt, Date measurementEndDt, MemberActiveMeasureDVO memberDvo, SupplierRunSetting srs, ProviderAssignationEnum provAssig) {
    	
    	if (provAssig == null) return;
    	Collection<ActiveMeasure> measures = memberDvo.getActiveMeasures();
    	Collection<FactLevelMeasureDenominator> flmDenoms = memberDvo.getFactLevelMeasureDenominators();
		if ((measures == null || measures.isEmpty())
				&& (flmDenoms == null || flmDenoms.isEmpty()))
			return;
        ProviderAssignationInput input = new ProviderAssignationInput();

        input.setMeasurementStartDate(measurementStartDt);
        input.setMeasurementEndDate(measurementEndDt);
        input.setRunDate(amRunSetting.getRunDate());
        input.setMemberProfile(profile);
        input.setActiveMeasures(measures);
        input.setFactLevelMeasureDenoms(flmDenoms);
        input.setMeasurementPeriodType(amRunSetting.getMeasurementPeriodType());
        input.setProviderAssignation(provAssig);
        providerAssignationService.assignProviders(input);
    }

    private void applyPackage(ActiveMeasureRunSetting amRunSetting, ActiveMeasureCommandOutput output, MemberActiveMeasureDVO memberDvo){
    	LOGGER.info("In applyPkg");
    	Collection<ActiveMeasure> allQualityMeasures = output.getActiveMeasures();
    	Collection<FactLevelMeasureDenominator> allCareMeasures = output.getFactLevelMeasureDenominators();
    	
		Set<Long> measurePackage = amRunSetting.getActiveMeasurePackage();

        for (ActiveMeasure firedMeasure : allQualityMeasures){
            boolean inPkg = true;
            if (measurePackage.contains(firedMeasure.getMeasureId())){
            	memberDvo.addActiveMeasure(firedMeasure);
            } else {
                inPkg = false;
            }
            if (LOGGER.isDebugEnabled()){
	            LOGGER.debug(firedMeasure + (inPkg ? "" : ", but discarded because of package"));
	            LOGGER.debug("Justifications");
                for (RuleJustification justification : firedMeasure.getJustifications()){
                    LOGGER.debug(justification.toString());
                }
            }
        }
        for (FactLevelMeasureDenominator firedMeasure : allCareMeasures){
            boolean inPkg = true;
            if (measurePackage.contains(firedMeasure.getMeasureId())){
            	memberDvo.addFactLevelMeasureDenominator(firedMeasure);
            } else {
                inPkg = false;
            }
            if (LOGGER.isDebugEnabled()){
	            LOGGER.debug(firedMeasure + (inPkg ? "" : ", but discarded because of package"));
	            LOGGER.debug("Justifications");
                for (RuleJustification justification : firedMeasure.getJustifications()){
                    LOGGER.debug(justification.toString());
                }
            }
        }
		return;
	}

    private ActiveMeasureCommandOutput executeRules(
            ActiveMeasureRunSetting amSetting, MemberProfile profile, Date measurementStartDt, Date measurementEndDt, Date ceRunDate) {
        ActiveMeasureCommandInput input = new ActiveMeasureCommandInput();
        input.setMemberProfile(profile);

        final DefaultActiveMeasureBuilder activeMeasureBuilder = new DefaultActiveMeasureBuilder();
        final DefaultFactLevelMeasureBuilder flmBuilder = new DefaultFactLevelMeasureBuilder();
        final RiskStratificationMeasureWeightsBuilder riskStratificationMeasureWeightsBuilder = new DefaultRiskMeasureWeightsBuilder();
        ActiveMeasureCommandOutput output = new ActiveMeasureCommandOutput(
                activeMeasureBuilder, flmBuilder);

        ActiveMeasureCommandConfiguration config = new ActiveMeasureCommandConfiguration();

        config.updateFrom(amSetting, measurementStartDt, measurementEndDt);
        config.setActiveMeasureBuilder(activeMeasureBuilder);
        config.setClassifierBuilder(new CacheUsingActiveMeasureClassifierBuilder(modelMapper.getCacheManager()));
        config.setHierarchicalClinicalConditionCategoryBuilder(new CacheUsingActiveMeasureHCCBuilder(
				modelMapper.getCacheManager()));
        config.setFactLevelMeasureBuilder(flmBuilder);

        config.setRiskStratificationMeasureWeightsBuilder(riskStratificationMeasureWeightsBuilder);

        try {
            ProductType productType = amSetting.getProduct();
            ActiveMeasureEngine engine = engineSupplier
                    .getEngineByProductType(productType);
            if (engine == null) {
                throw new IllegalArgumentException(getClass().getSimpleName()
                        + " does not support product " + productType);
            } else {
                engine.execute(input, output, config);
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new CEException("Error executing rules", e);
        }
        return output;
    }

    private List<ActiveMeasureRealtimeRun> prepareResponse(List<ActiveMeasureRunSettingDVO> runSettingDvos, boolean returnResults){
        if (runSettingDvos == null || runSettingDvos.isEmpty()) {
            return null;
        }

		List<ActiveMeasureRealtimeRun> _return = new ArrayList<ActiveMeasureRealtimeRun>();

    	for (ActiveMeasureRunSettingDVO runSettingDvo : runSettingDvos){
        	ActiveMeasureRealtimeRun responseRun = new ActiveMeasureRealtimeRun();
        	responseRun.setActiveMeasureRunSetting(runSettingDvo.getActiveMeasureRunSetting());
        	responseRun.setSkipped(runSettingDvo.getSkippedMembers().size()>0);
        	if (returnResults){
				List<ActiveMeasure> result = new ArrayList<ActiveMeasure>();
    			for (MemberActiveMeasureDVO memberDvo: runSettingDvo.getMemberActiveMeasures()){
    				result.addAll(memberDvo.getActiveMeasures());
    				for (FactLevelMeasureDenominator flm : memberDvo.getFactLevelMeasureDenominators()){
    					MemberActiveMeasure flmAm = new MemberActiveMeasure();
    					flmAm.setMeasureId(flm.getMeasureId());
    					flmAm.setInNumerator(flm.isInNumerator());
    					flmAm.setExcludedFromDenominator(flm.isExcludedFromDenominator());
    					flmAm.setEligible(flm.isEligible());
    					flmAm.getJustifications().addAll(flm.getJustifications());
    					for (FactLevelMeasureNumerator flmNum : flm.getNumerators())
    						flmAm.getJustifications().addAll(flmNum.getJustifications());
    					result.add(flmAm);
    				}
    			}
    			responseRun.setActiveMeasures(result);
        	}
        	_return.add(responseRun);
        }

    	return _return;
    }

    public static void main(String args[]){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
        Calendar dt = Calendar.getInstance();
        dt.set(2013, Calendar.FEBRUARY, 28, 23, 59, 59);
        dt.set(Calendar.MILLISECOND, 999);
        System.out.println("Before=" + sdf.format(dt.getTime()));
        dt.add(Calendar.MONTH, -12);
        dt.set(Calendar.HOUR_OF_DAY, 0);
        dt.set(Calendar.MINUTE, 0);
        dt.set(Calendar.SECOND, 0);
        dt.set(Calendar.MILLISECOND, 0);
        dt.add(Calendar.DATE, 1);
        System.out.println("After=" + sdf.format(dt.getTime()));
        System.out.println("Done");
    }

    protected SupplierRunSetting getSupplierRunSetting(CareEngineMessage message) throws CEException {
        LOGGER.info("In getSRS");
        try{
            //CacheManagerImpl _cache = CacheManagerImpl.getInstance();

            SupplierBatchDVO supplierBatchDVO = new SupplierBatchDVO();
            supplierBatchDVO.setStatusCode(Status.Pending);
            supplierBatchDVO.setMemberIds(Arrays.asList(message.getMemberIds()));

            Date ceRunDate = message.getCeRunDate();
            SupplierRunSetting srs = supplierRunDAO.getAmSupplierRunSetting(message.getAhmSupplierId(), ceRunDate, false, null);
            
            if (message.getOriginalRequest() instanceof RunStandardActiveMeasures){
            	RunStandardActiveMeasures custReq = (RunStandardActiveMeasures)message.getOriginalRequest();
	            
            if( custReq.isSaveJustification() != null ) {
            	srs.getRunSettings().setSaveActiveMeasureJustification(custReq.isSaveJustification());
            } 
            }
            
            message.setSupplierRunSetting(srs);
            message.setSupplierBatchDVO(supplierBatchDVO);

            //_cache.initializeSupplierCache(srs.getAhmSupplierId(), ceRunDate, srs.getRunSettings().isCurrentRun());

            return srs;
        }
        catch(Exception e){
            LOGGER.fatal("getSupplierRunSetting(): Unable to get supplier run settings for member " + message.getMemberIds()[0] + " with supplier " + message.getAhmSupplierId() + " source message was: " + message.getMessageText(), e);
            throw new CEException("Unable to get supplier run settings for member", e);
        }
    }

    protected List<MemberHealthRecordIF> fetchMemberData(CareEngineMessage message, Set<ProductType> executionProducts, Map<ProductType, ProviderAssignationEnum> provAssigs) throws CEException {
        LOGGER.info("In setupBatch");
        MHRBatchDVO mhrBatchDvo = null;
        SupplierRunSetting srs = message.getSupplierRunSetting();
        boolean shouldFetchHie = srs.getRunSettings().shouldFetchHieData();
        boolean shouldFetchCarePlanData = srs.getRunSettings().isCPPurchased();
        shouldFetchCarePlanData = false;
        boolean shouldFetchFinancialData = srs.isFinanceClaimsflag();
        shouldFetchFinancialData = false;
        Set<DataType> optionalDataTypes = srs.getRunSettings().getOptionalDataTypes();
        
        if (DataType.COVERAGE.name().equals(srs.getEligibilityODSSource())){
        	optionalDataTypes.add(DataType.COVERAGE);
        }
        else optionalDataTypes.add(DataType.ELIGIBILITY);
        
        if (provAssigs.containsValue(ProviderAssignationEnum.PCP)){
        	optionalDataTypes.add(DataType.MEMBERPCP);
        }
        optionalDataTypes.add(DataType.MHSFDBK);
        
        if (executionProducts.contains(ProductType.ACASE)){
        	optionalDataTypes.add(DataType.MEDICAL_CASE);
        }
        
        Set<DataType> excludeData = EnumSet.noneOf(DataType.class);
        excludeData.add(DataType.MHS);
        excludeData.add(DataType.MEMBERMODEL);
        excludeData.add(DataType.CDM);
        if (srs.getExcludedClaims() != null) excludeData.addAll(srs.getExcludedClaims());

        int accountId = srs.getRunSettings().getAccountId();
        Long[] memberIds = message.getMemberIds();
        if (memberIds != null && memberIds.length > 0){
	        mhrBatchDvo = csa.getMemberHealthRecords(Arrays.asList(message.getMemberIds()), 
					0 ,
					null, 
					null, 
					null, 
					-1, 
					excludeData,
					shouldFetchHie,
					shouldFetchCarePlanData,
					optionalDataTypes,
					accountId,
					shouldFetchFinancialData,
					srs.getRunSettings().getMasterId(), shouldFetchFinancialData
					);
        }
        if (mhrBatchDvo == null || mhrBatchDvo.getMemberList() == null || mhrBatchDvo.getMemberList().isEmpty()) {
            throw new CEException("Invalid Member or Error getting Member Data from database.");
        }
        return mhrBatchDvo.getMemberList();
    }

	protected Map<Long, MemberLockData> getMemberLockData(CareEngineMessage message){
		Map<Long, MemberLockData> _returnMap = new HashMap<Long, MemberLockData>();
		List<Long> memberIds = message.getSupplierBatchDVO().getMemberIds();
		List<MemberLockData> memberDirtyBitList = supplierDAO.getMemberLockData(memberIds);
		for (MemberLockData mlock : memberDirtyBitList) _returnMap.put(mlock.getMemberId(), mlock);
		return _returnMap;
	}

	protected List<ActiveMeasureRunSettingDVO> doSkipLogic(CareEngineMessage msg, List<ActiveMeasureRunSetting> amRunSettings, String runMode){
		LOGGER.info("Inside skipLogic: ");
		if (amRunSettings == null || amRunSettings.size() == 0){
			return new ArrayList<ActiveMeasureRunSettingDVO>();
		}
		List<ActiveMeasureRunSettingDVO> runSettingDvos = new ArrayList<ActiveMeasureRunSettingDVO>(amRunSettings.size());
		
		List<Long> memberIds = msg.getSupplierBatchDVO().getMemberIds();
		Date runDate = msg.getCeRunDate();
		
        Map<Long, MemberLockData> memberLockMap = null;
        
        if ("CS".equals(runMode)){
        	memberLockMap = new HashMap<Long, MemberLockData>();
        }
        else{
        	reloadRefreshDates();
        	memberLockMap = getMemberLockData(msg);
        }

		for (ActiveMeasureRunSetting amRunSetting : amRunSettings){
			LOGGER.info("skipLogic");
			Date lastRunDate = amRunSetting.getPrevRunDetailSetting()==null?null:amRunSetting.getPrevRunDetailSetting().getRunDate();
			ActiveMeasureRunSettingDVO runSettingDvo = new ActiveMeasureRunSettingDVO();
			runSettingDvo.setActiveMeasureRunSetting(amRunSetting);
			runSettingDvos.add(runSettingDvo);
			
			for (Long memberId : memberIds){
				MemberLockData memberLock = memberLockMap.get(memberId);
				if (shouldSkipMemberExecution(amRunSetting, lastRunDate, memberLock, runDate)){
					if (LOGGER.isInfoEnabled()){
						LOGGER.info("skipLogic.SkippedMember: " + ", amRun=" + amRunSetting + ", rDt=" + runDate + ", lrd=" + lastRunDate + ", asRefDt=" + this.adminSuiteToCERefreshDate + ", ruleRefDt=" + this.ruleRefreshDate + ", ml: " + memberLock);
					}
					runSettingDvo.addSkipMember(memberId);
				}
				else{
					MemberActiveMeasureDVO memberDvo = new MemberActiveMeasureDVO();
		            runSettingDvo.addMemberActiveMeasure(memberDvo);
		            memberDvo.setMemberId(memberId);
					memberDvo.setLastRunDate(lastRunDate);
				}
			}
		}
		return runSettingDvos;
	}

	protected boolean shouldSkipMemberExecution(ActiveMeasureRunSetting amRunSetting, Date lastRunDate, MemberLockData memberLock, Date runDate){
		if ("CS".equals(amRunSetting.getRunMode())) return false; //for custom runs
		if (lastRunDate == null) return false;
		if (memberLock == null || memberLock.getDirtyBitLastModifiedDate() == null) return false;
		if (memberLock.getDirtyBitLastModifiedDate().after(lastRunDate)) return false;
		if (this.adminSuiteToCERefreshDate != null){
			if (this.adminSuiteToCERefreshDate.after(lastRunDate)) return false;
		}
		if (this.ruleRefreshDate != null){
			if (this.ruleRefreshDate.after(lastRunDate)) return false;
		}

		switch (amRunSetting.getMeasurementPeriodType())
		{
		case ROLLING:
			if (this.neverSkipRollingMode) return false;
			long daysSinceLastRun = CEDateUtil.getDaysBetween(lastRunDate, runDate);
			if (daysSinceLastRun > this.rollingModeSkipToleranceInDays) return false;
			break;
		case ROLLING_QTRLY:
		case ROLLING_MONTHLY:
		case CALENDAR: break;
		}
		return true;
	}

	protected List<Long> extractExecutionMemberIds(List<ActiveMeasureRunSettingDVO> runSettings){
		Set<Long> memberIds = new HashSet<Long>();
		for (ActiveMeasureRunSettingDVO runSetting : runSettings){
			for (MemberActiveMeasureDVO memberDvo : runSetting.getMemberActiveMeasures()){
				memberIds.add(memberDvo.getMemberId());
			}
		}
		return new ArrayList<Long>(memberIds);
	}
	
	protected Set<ProductType> extractExecutionProducts(List<ActiveMeasureRunSettingDVO> runSettingDvos) {
		Set<ProductType> products = new HashSet<ProductType>();
		if (runSettingDvos == null) return products;
		for (ActiveMeasureRunSettingDVO rsDvo : runSettingDvos){
			products.add(rsDvo.getActiveMeasureRunSetting().getProduct());
		}
		return products;
	}

    private List<DefaultMemberProfile> mapLegacyData(List<MemberHealthRecordIF> mhrList) {
    	LOGGER.info("In mapLegacyData");
        List<DefaultMemberProfile> memberProfiles = new ArrayList<DefaultMemberProfile>();
        for (MemberHealthRecordIF mhr : mhrList){
            DefaultMemberProfile profile = modelMapper.mapMemberHealthRecord(mhr);
            memberProfiles.add(profile);
        }
        return memberProfiles;
    }

    private void refactor(DefaultMemberProfile profile){
    	LOGGER.info("In refactor");
        memberProfileRefactorer.refactor(profile);
    }


    @Override
    public void runPostProcess(CareEngineMessageIF messageWrapper) throws InterruptedException {
        // TODO Auto-generated method stub
    }

    @Override
    public Object onOptimisticLock(CareEngineMessageIF messageWrapper) throws InterruptedException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void init() {
    	if (modelMapper != null){
    		LOGGER.info("Already Initialized");
    		return;
    	}
    	ICacheManager cache = CacheManagerImpl.getInstance();
        modelMapper = new CEv2Mapper();
        modelMapper.setCacheManager(cache);
        modelMapper.init();
        reloadRefreshDates();
        try {
            amJAXBContext = JAXBContext.newInstance("net.ahm.careengine.schema.messages.proto.activemeasure:net.ahm.careengine.schema.types.proto.activemeasure:net.ahm.careengine.schema.types.common");
        } catch (JAXBException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }
    
    private void reloadRefreshDates(){
		ICacheManager cache = CacheManagerImpl.getInstance();
		this.adminSuiteToCERefreshDate = cache.getObject(ICacheManager.ADMIN_SUITE_REFRESH_DATE);
		this.ruleRefreshDate = cache.getObject(ICacheManager.GUVNOR_REFRESH_DATE);
		LOGGER.info("reload: asRefDt=" + formatyyyyMMdd_HHmmss(this.adminSuiteToCERefreshDate) + ", ruleRefDt=" + formatyyyyMMdd_HHmmss(this.ruleRefreshDate));
	}
	
	private String formatyyyyMMdd_HHmmss(Date date) {
		if (date == null) return null;
		return ThreadLocalSimpleDateFormat.yyyyMMdd_HH_mm_ss.get().format(date);
	}

    private boolean doPersist(Object originalRequest){
        if (originalRequest instanceof RunActiveMeasures) {
            //RunActiveMeasures custReq = (RunActiveMeasures)originalRequest;
            //return custReq.getRunConfig().isPersist();
            return false; //dont persist individual custom run
        } else if (originalRequest instanceof RunStandardActiveMeasures) {
            RunStandardActiveMeasures stdReq = (RunStandardActiveMeasures) originalRequest;
            return stdReq.getStandardRunConfig().isPersist();
        }
        return false;
    }

    public SupplierRunDAO getSupplierRunDAO() {
        return supplierRunDAO;
    }

    public void setSupplierRunDAO(SupplierRunDAO supplierRunDAO) {
        this.supplierRunDAO = supplierRunDAO;
    }

    public ActiveMeasureEngineSupplier getEngineSupplier() {
        return engineSupplier;
    }

    public void setEngineSupplier(ActiveMeasureEngineSupplier engineSupplier) {
        this.engineSupplier = engineSupplier;
    }

    private void logMemberProfile(MemberProfile profile) {
        LOGGER.info("MemberInfo: " + profile.getMemberInfo());
        if (!LOGGER.isDebugEnabled())
            return;
        if (profile instanceof DefaultMemberProfile) {
            LOGGER.debug("Eligibility Data");
            for (PlanEligibility elig : ((DefaultMemberProfile) profile)
                    .getEligibilityRecords()) {
                logEligibility(elig);
            }
        }
        LOGGER.debug("Feedback Data");
        LOGGER.debug(profile.getFeedbacks());
        LOGGER.debug("Current MPR");
        LOGGER.debug(profile.getCurrentPcpRelationships());
        LOGGER.debug("Historical MPR");
        LOGGER.debug(profile.getHistoricalPcpRelationships());
    }

    private void logEligibility(PlanEligibility elig){
    	if (!LOGGER.isDebugEnabled()) return;
        LOGGER.debug(elig.getClass().getSimpleName() + ": factId=" + elig.getFactId() + ", startDate=" + CEDateUtil.formatYYYYMMDD(elig.getStartDate()) + ", endDate=" + CEDateUtil.formatYYYYMMDD(elig.getEndDate()));
        if (elig instanceof SimpleCompositeEligibility){
            LOGGER.debug("components:");
            SimpleCompositeEligibility composite = (SimpleCompositeEligibility)elig;
            for (PlanEligibility comp : composite.getComponents())
                logEligibility(comp);
        }
    }

    public ActiveMeasureConfigService getActiveMeasureConfigService() {
        return activeMeasureConfigService;
    }

    public void setActiveMeasureConfigService(
            ActiveMeasureConfigService activeMeasureConfigService) {
        this.activeMeasureConfigService = activeMeasureConfigService;
    }

    public BoxManager getBoxManager() {
        return boxManager;
    }

    public void setBoxManager(BoxManager boxManager) {
        this.boxManager = boxManager;
    }

    @Override
    public CareEngineMessage parseRequest(String xml) {
        CareEngineMessage msg = null;

        Unmarshaller unmarshaller;
        try {
            unmarshaller = amJAXBContext.createUnmarshaller();
            JAXBElement<?> jaxb = (JAXBElement<?>) unmarshaller.unmarshal(new StringReader(xml));
            msg = new CareEngineMessage();
            msg.setExecutionMode(ExecutionMode.REALTIMEACTIVEMEASURE);

            Object reqObj = jaxb.getValue();
            Map<Long, Integer> memberSupplierMap = new HashMap<Long, Integer>();
            CareEngineMember member = null;
            if (reqObj instanceof RunStandardActiveMeasures){
                RunStandardActiveMeasures request = (RunStandardActiveMeasures)reqObj;
                member = (CareEngineMember)request.getMember();
                msg.setCeRunDate(request.getStandardRunConfig().getRunDate().getTime());
            } else if (reqObj instanceof RunActiveMeasures) {
                RunActiveMeasures request = (RunActiveMeasures)reqObj;
                member = (CareEngineMember)request.getMember();
            }

            Long supp = member.getSupplierId();
            memberSupplierMap.put(member.getMemberId(), supp.intValue());
            msg.setMemberSupplier(memberSupplierMap);
            msg.setMemberSupplier();
            msg.setOriginalRequest(reqObj);

        } catch (JAXBException e) {
            LOGGER.error(e);
            return null;
        }

        return msg;
    }

    @Override
    public String getRootNamespaceURI() {
        return "http://careengine.ahm.net/schema/messages/proto/activemeasure";
    }

    private List<ActiveMeasureRunSetting> getActiveMeasureRunSettings(long accountId, int bussSuppId, long memberId, Date runDate, String runMode, Collection<ProductType> products){
    	//Identify candidate run settings active as of today.
		List<ActiveMeasureRunSetting> amRunSettings = activeMeasureConfigService.getActiveMeasureRunSettings(bussSuppId, runDate, runMode, products);
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug("RT: Candidate Run Settings:");
			for (ActiveMeasureRunSetting run : amRunSettings) LOGGER.debug("Candidate: " + run);
		}
		
		//For above candidate run settings, identify run settings for which BT run happened anytime in past. 
		//In RT, member should run only for run settings where BT run occured in past.
		List<ActiveMeasureRunSetting> latestAmRunSettings = this.activeMeasureBatchConfigDao.getLatestAmRunSettings(amRunSettings, accountId, memberId);
		
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug("RT: Candidate Run Settings with BT run in past:");
			for (ActiveMeasureRunSetting run : latestAmRunSettings) LOGGER.debug("Candidate: " + run);
		}

		Map<ProductType, Map<ActiveMeasurePeriodType, Map<Long, Map<Long, ActiveMeasureRunSetting>>>> productMap = new HashMap<ProductType, Map<ActiveMeasurePeriodType, Map<Long, Map<Long, ActiveMeasureRunSetting>>>>();
		for (ActiveMeasureRunSetting amRun : amRunSettings){
			Map<ActiveMeasurePeriodType, Map<Long, Map<Long, ActiveMeasureRunSetting>>> periodTypeMap = productMap.get(amRun.getProduct());
			if (periodTypeMap == null){
				periodTypeMap = new HashMap<ActiveMeasurePeriodType, Map<Long, Map<Long, ActiveMeasureRunSetting>>>();
				productMap.put(amRun.getProduct(), periodTypeMap);
			}
			Map<Long, Map<Long, ActiveMeasureRunSetting>> amRunMap = periodTypeMap.get(amRun.getMeasurementPeriodType());
			if (amRunMap == null){
				amRunMap = new HashMap<Long, Map<Long, ActiveMeasureRunSetting>>();
				periodTypeMap.put(amRun.getMeasurementPeriodType(), amRunMap);
			}

			Long startDt = null;
			Long endDt = null;
			if (amRun.getMeasurementPeriodType()==ActiveMeasurePeriodType.ROLLING){
				startDt = 0L;
				endDt = 0L;
			}
			else{
				startDt = amRun.getMeasurementStartDate().getTime();
				endDt = amRun.getMeasurementEndDate().getTime();
			}
			Map<Long, ActiveMeasureRunSetting> endMap = amRunMap.get(startDt);
			if (endMap == null){
				endMap = new HashMap<Long, ActiveMeasureRunSetting>();
				amRunMap.put(startDt, endMap);
			}
			endMap.put(endDt, amRun);
		}
		
		List<ActiveMeasureRunSetting> retList = new ArrayList<ActiveMeasureRunSetting>();
		for (ActiveMeasureRunSetting latestAmRun : latestAmRunSettings){
			Map<ActiveMeasurePeriodType, Map<Long, Map<Long, ActiveMeasureRunSetting>>> periodTypeMap = productMap.get(latestAmRun.getProduct());
			if (periodTypeMap == null) continue;
			Map<Long, Map<Long, ActiveMeasureRunSetting>> amRunMap = periodTypeMap.get(latestAmRun.getMeasurementPeriodType());
			if (amRunMap == null) continue;
			
			Long startDt = null;
			Long endDt = null;
			if (latestAmRun.getMeasurementPeriodType()==ActiveMeasurePeriodType.ROLLING){
				startDt = 0L;
				endDt = 0L;
			}
			else{
				startDt = latestAmRun.getMeasurementStartDate().getTime();
				endDt = latestAmRun.getMeasurementEndDate().getTime();
			}

			ActiveMeasureRunSetting amRun = null;
			Map<Long, ActiveMeasureRunSetting> endMap = amRunMap.get(startDt);
			if (endMap != null){
				amRun = endMap.get(endDt);
			}
			if (amRun != null){
				DefaultActiveMeasureRunSetting editableRun = (DefaultActiveMeasureRunSetting)amRun;
				editableRun.setPrevRunDetailSetting(DefaultActiveMeasureRunSetting.class.cast(latestAmRun));
				editableRun.setPrevRunDetailSettingId(latestAmRun.getRunDetailSettingId());
				editableRun.setRunId(latestAmRun.getRunId());
				editableRun.setRunDetailSettingId(latestAmRun.getRunDetailSettingId());
				editableRun.setRunMode(latestAmRun.getRunMode());
				retList.add(editableRun);
			}
		}
		
		return retList;
	}

    private List<ActiveMeasureRunSetting> getCustomActiveMeasureRunSettings(long bussSuppId, RunActiveMeasures custReq, Date runDate){
        List<ActiveMeasureRunSetting> _returnList = new ArrayList<ActiveMeasureRunSetting>();
        AmAccountPackage pkg = custReq.getRunConfig().getAccountPackage();
        DefaultActiveMeasureRunSetting amRun = new DefaultActiveMeasureRunSetting();
        amRun.setRunId(0l);
        amRun.setRunDetailSettingId(0l);
        amRun.setSupplierId(bussSuppId);
        amRun.setProduct(ProductType.getProductTypeByCode(pkg.getProduct()));
        amRun.setMeasurementPeriodType(ActiveMeasurePeriodType.CALENDAR);
        amRun.setMeasurementStartDate(pkg.getMeasurementStartDate().getTime());
        amRun.setMeasurementEndDate(pkg.getMeasurementEndDate().getTime());
        amRun.setRunDate(runDate);
        amRun.setRunMode("CS");
        amRun.setLatestRun(false);
        amRun.setMonthlyLatestRun(false);
        amRun.setActiveMeasureIds(pkg.getMeasureId());
        _returnList.add(amRun);
        return _returnList;
    }

    public CEv2Mapper getModelMapper() {
        return modelMapper;
    }

    public void setModelMapper(CEv2Mapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Required by spring
     * 
     * @param activeMeasurePersistenceCommand
     */
    public void setActiveMeasurePersistenceCommand(CommandIF<ActiveMeasurePersistenceCommandInput, ActiveMeasurePersistenceCommandOutput, ActiveMeasurePersistenceCommandConfiguration> activeMeasurePersistenceCommand) {
        this.activeMeasurePersistenceCommand = activeMeasurePersistenceCommand;
    }
    
    public ActiveMeasureOutputDAO getActiveMeasureOutputDAO() {
		return activeMeasureOutputDAO;
	}

	public void setActiveMeasureOutputDAO(ActiveMeasureOutputDAO activeMeasureOutputDAO) {
		this.activeMeasureOutputDAO = activeMeasureOutputDAO;
	}

	public SupplierDAO getSupplierDAO() {
		return supplierDAO;
	}

	public void setSupplierDAO(SupplierDAO supplierDAO) {
		this.supplierDAO = supplierDAO;
	}

	public CommandIF<ActiveMeasurePersistenceCommandInput, ActiveMeasurePersistenceCommandOutput, ActiveMeasurePersistenceCommandConfiguration> getJustificationPersistenceCommand() {
		return justificationPersistenceCommand;
	}

	public void setJustificationPersistenceCommand(
			CommandIF<ActiveMeasurePersistenceCommandInput, ActiveMeasurePersistenceCommandOutput, ActiveMeasurePersistenceCommandConfiguration> justificationPersistenceCommand) {
		this.justificationPersistenceCommand = justificationPersistenceCommand;
	}
	
	public int getRollingModeSkipToleranceInDays() {
		return rollingModeSkipToleranceInDays;
	}

	public void setRollingModeSkipToleranceInDays(int rollingModeSkipToleranceInDays) {
		this.rollingModeSkipToleranceInDays = rollingModeSkipToleranceInDays;
	}

	public boolean isNeverSkipRollingMode() {
		return neverSkipRollingMode;
	}

	public void setNeverSkipRollingMode(boolean neverSkipRollingMode) {
		this.neverSkipRollingMode = neverSkipRollingMode;
	}

	public PersonDataDAO getPersonDataDAO() {
		return personDataDAO;
	}

	public void setPersonDataDAO(PersonDataDAO personDataDAO) {
		this.personDataDAO = personDataDAO;
	}

	public ActiveMeasureBatchConfigDao getActiveMeasureBatchConfigDao() {
		return activeMeasureBatchConfigDao;
	}

	public void setActiveMeasureBatchConfigDao(
			ActiveMeasureBatchConfigDao activeMeasureBatchConfigDao) {
		this.activeMeasureBatchConfigDao = activeMeasureBatchConfigDao;
	}

	public ProviderAssignationService getProviderAssignationService() {
		return providerAssignationService;
	}

	public void setProviderAssignationService(ProviderAssignationService providerAssignationService) {
		this.providerAssignationService = providerAssignationService;
	}
}

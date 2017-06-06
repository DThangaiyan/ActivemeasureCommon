package net.ahm.careengine.controller;


import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ahm.careengine.activemeasure.ActiveMeasureCommandConfiguration;
import net.ahm.careengine.activemeasure.ActiveMeasureCommandInput;
import net.ahm.careengine.activemeasure.ActiveMeasureCommandOutput;
import net.ahm.careengine.activemeasure.ActiveMeasureEngine;
import net.ahm.careengine.activemeasure.ActiveMeasureEngineSupplier;
import net.ahm.careengine.activemeasure.ActiveMeasurePersistenceCommandConfiguration;
import net.ahm.careengine.activemeasure.ActiveMeasurePersistenceCommandInput;
import net.ahm.careengine.activemeasure.ActiveMeasurePersistenceCommandOutput;
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
import net.ahm.careengine.common.ExecutionWorkflow;
import net.ahm.careengine.dao.AdminDAO;
import net.ahm.careengine.dao.SupplierDAO;
import net.ahm.careengine.dao.SupplierRunBatchDAO;
import net.ahm.careengine.dao.SupplierRunDAO;
import net.ahm.careengine.dao.output.activemeasure.ActiveMeasureOutputDAO;
import net.ahm.careengine.domain.comorbidclinicalcondition.RiskStratificationMeasureWeightsBuilder;
import net.ahm.careengine.domain.impl.comorbidclinicalcondition.CacheUsingActiveMeasureHCCBuilder;
import net.ahm.careengine.domain.impl.comorbidclinicalcondition.DefaultHierarchicalClinicalConditionCategoryBuilder;
import net.ahm.careengine.domain.impl.comorbidclinicalcondition.DefaultRiskMeasureWeightsBuilder;
import net.ahm.careengine.domain.impl.measure.active.CacheUsingActiveMeasureClassifierBuilder;
import net.ahm.careengine.domain.impl.measure.active.DefaultActiveMeasureBuilder;
import net.ahm.careengine.domain.impl.measure.active.DefaultFactLevelMeasureBuilder;
import net.ahm.careengine.domain.impl.member.DefaultMemberProfile;
import net.ahm.careengine.domain.justification.RuleJustification;
import net.ahm.careengine.domain.mapper.CEv2Mapper;
import net.ahm.careengine.domain.measures.active.ActiveMeasure;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.member.MemberProfile;
import net.ahm.careengine.domain.refactor.DefaultMemberProfileRefactorer;
import net.ahm.careengine.dvo.ProductType;
import net.ahm.careengine.handler.RunResultsHandler;
import net.ahm.careengine.healthagen.HealthagenRestService;
import net.ahm.careengine.message.CareEngineMessage;
import net.ahm.careengine.message.CareEngineMessageIF;
import net.ahm.careengine.message.ExecutionMode;
import net.ahm.careengine.message.RunSettings;
import net.ahm.careengine.message.Status;
import net.ahm.careengine.message.SupplierBatchDVO;
import net.ahm.careengine.message.SupplierRunSetting;
import net.ahm.careengine.provider.ProviderAssignationEnum;
import net.ahm.careengine.starschema.CDMBatchNotification;
import net.ahm.careengine.starschema.StarSchemaMessageUtil;
import net.ahm.careengine.util.CEDateUtil;
import net.ahm.careengine.util.CacheManagerImpl;
import net.ahm.careengine.util.ICacheManager;
import net.ahm.careengine.util.SimpleProfilerUtil;
import net.ahm.careengine.util.ThreadLocalSimpleDateFormat;
import net.ahm.cev4.activemeasures.config.ActiveMeasureRunSetting;
import net.ahm.cev4.activemeasures.config.BoxManager;
import net.ahm.cev4.activemeasures.dao.config.batch.ActiveMeasureBatchConfigDao;
import net.ahm.cev4.service.provider.ProviderAssignationInput;
import net.ahm.cev4.service.provider.ProviderAssignationService;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class ActiveMeasureBatchProcessor implements MessageProcessorIF {

    private final static Logger                                                                                                                    LOGGER                         = Logger.getLogger(ActiveMeasureBatchProcessor.class);
    protected final static Logger                                                                                                                  PERF_LOGGER                    = Logger.getLogger("performance");

    protected SupplierRunBatchDAO                                                                                                                  supplierBatchDAO;
    protected SupplierRunDAO                                                                                                                       supplierRunDAO;
    protected CommonServicesAdapter                                                                                                                csa                            = new CommonServicesAdapter();
    protected CEv2Mapper                                                                                                                           modelMapper                    = null;
    protected DefaultMemberProfileRefactorer                                                                                                       memberProfileRefactorer        = new DefaultMemberProfileRefactorer();
    protected ActiveMeasureEngineSupplier                                                                                                          engineSupplier;
    protected CommandIF<ActiveMeasurePersistenceCommandInput, ActiveMeasurePersistenceCommandOutput, ActiveMeasurePersistenceCommandConfiguration> activeMeasurePersistenceCommand;
    protected BoxManager                                                                                                                           boxManager;
    //protected PcpAssignationService                                                                                                                pcpAssignationService;
    protected ActiveMeasureBatchConfigDao                                                                                                          activeMeasureBatchConfigDao;
    protected RunResultsHandler                                                                                                                    runResultsHandler;
    protected ActiveMeasureOutputDAO                                                                                                               activeMeasureOutputDAO;
    protected SupplierDAO                                                                                                                          supplierDAO;
    protected Date                                                                                                                                 adminSuiteToCERefreshDate;
    protected Date                                                                                                                                 ruleRefreshDate;
    protected CommandIF<ActiveMeasurePersistenceCommandInput, ActiveMeasurePersistenceCommandOutput, ActiveMeasurePersistenceCommandConfiguration> justificationPersistenceCommand;
    protected int                                                                                                                                  rollingModeSkipToleranceInDays = 10;
    protected boolean                                                                                                                              neverSkipRollingMode           = false;
    private long                                                                                                                                   lastCeRunId                    = -1;
    private HealthagenRestService healthagenRestService = null;
    private AdminDAO adminDAO;
    private boolean saveToOracle = true;
    private boolean saveToHadoop = false;
    private ProviderAssignationService providerAssignationService;
    private RabbitTemplate rabbitCDMTemplate = null;

	@Override
	@Transactional(propagation=Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED,
	rollbackFor={InterruptedException.class, RuntimeException.class }, timeout=7500)
	public Object processMessage(CareEngineMessageIF messageWrapper) throws InterruptedException {
		CareEngineMessage msg = (CareEngineMessage) messageWrapper.getPayload();
        ActiveMeasuresPerfLog perfLogHldr = new ActiveMeasuresPerfLog(
                ExecutionMode.ACTIVEMEASURE.name());
		long start = System.currentTimeMillis();
		int totalMembers = 0;
		int totalExecutionMembers = 0;
		try{
			SimpleProfilerUtil.reset();
			
            perfLogHldr.setCeRunScheduleId(msg.getCareEngineRunScheduleId());
            perfLogHldr.setSupplierBatchId(msg.getSupplierRunBatchId());
			
			this.updateBatchStartTime(msg.getCareEngineRunScheduleId());
			SupplierBatchDVO supplierBatch = this.getSupplierBatch(msg.getSupplierRunBatchId());
			if (isSupplierBatchProcessed(supplierBatch)){
				return null;
			}
			boolean success = this.updateSupplierBatchStatus(supplierBatch, Status.InProgress);
			if (!success){
				return null;
			}
			
			msg.setSupplierBatchDVO(supplierBatch);
			totalMembers = supplierBatch.getMemberIds().size();
			
			SupplierRunSetting srs = this.getSupplierRunSetting(msg.getSupplierBatchDVO().getSupplierRunScheduleId());
			int accountId = srs.getRunSettings().getAccountId();
			reloadConfiguration(msg.getCareEngineRunScheduleId(), accountId);
			long endSRS = System.currentTimeMillis();
			LOGGER.info("processMessage(): supplier: " + srs.getAhmSupplierId() + ", batch: " + msg.getCareEngineRunScheduleId() + "/" + msg.getSupplierRunBatchId() + ", runDate: " + msg.getCeRunDate());
            perfLogHldr.setSrsTime(endSRS - start);
			
			Date ceRunDate = srs.getRunSettings().getRunClock();
			msg.setCeRunDate(ceRunDate);
			msg.setSupplierRunSetting(srs);
			
			List<ActiveMeasureRunSetting> amRunSettings = null;
			amRunSettings = this.activeMeasureBatchConfigDao.getActiveMeasureRunSettings(accountId, msg.getCareEngineRunScheduleId(), srs.getAhmSupplierId(), true);
			long endAmSettings = System.currentTimeMillis();
            perfLogHldr.setAmrsTime(endAmSettings - endSRS);
			
            String runMode = "BT";
            if (!amRunSettings.isEmpty()) runMode = amRunSettings.get(0).getRunMode();
			
			List<ActiveMeasureRunSettingDVO> runSettingDvos = doSkipLogic(msg, amRunSettings, runMode);
			long endSkipLogic = System.currentTimeMillis();
            perfLogHldr.setSkipLogicTime(endSkipLogic - endAmSettings);
            
            List<Long> executionMembers = extractExecutionMemberIds(runSettingDvos);
            totalExecutionMembers = executionMembers.size();
            perfLogHldr.setMemberCount(totalExecutionMembers);
            
            if (LOGGER.isInfoEnabled()) LOGGER.info("executionMembers: batch: " + msg.getCareEngineRunScheduleId() + "/" + msg.getSupplierRunBatchId() + " [" + totalExecutionMembers + "/" + totalMembers + "]");
			
            if (totalExecutionMembers > 0){
				long startODS = System.currentTimeMillis();
				Set<ProductType> executionProducts = extractExecutionProducts(runSettingDvos);
				Map<ProductType, ProviderAssignationEnum> provAssigs = srs.getProductProviderAssignations(executionProducts);
				List<MemberHealthRecordIF> mhrInLegacyFormat = this.fetchMemberData(msg, executionMembers, executionProducts, provAssigs);
				long endODS = System.currentTimeMillis();
                perfLogHldr.setOdsGetTime(endODS - startODS);
				List<DefaultMemberProfile> memberProfiles = this.mapLegacyData(mhrInLegacyFormat);
				
				LOGGER.info("In refactor");
				for (DefaultMemberProfile profile : memberProfiles){
		            refactor(profile);
		        }
				long endMapAndRefactor = System.currentTimeMillis();
                perfLogHldr.setMapAndRefactorTime(endMapAndRefactor - endODS);
				
				long ruleStart = System.currentTimeMillis();
				ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
				long cpuStartTimeInNanos = threadMxBean.getCurrentThreadCpuTime();
				this.executeRules(runSettingDvos, memberProfiles, srs, provAssigs);
				long cpuEndTimeInNanos = threadMxBean.getCurrentThreadCpuTime();
				long ruleEnd = System.currentTimeMillis();
                perfLogHldr.setRulesRunTime(ruleEnd - ruleStart);
                perfLogHldr
                        .setRulesRunCpuTime((cpuEndTimeInNanos - cpuStartTimeInNanos) / 1000000);
            }
            else{
            	LOGGER.warn("processMessage(): No members to run: batch: " + msg.getCareEngineRunScheduleId() + "/" + msg.getSupplierRunBatchId());
            }
			
			boolean cancelled = this.isAnySupplierBatchCancelled(msg.getCareEngineRunScheduleId());
			
			if (!cancelled){
				long saveStart = System.currentTimeMillis();
				this.saveResults(msg, runSettingDvos, runMode, srs.getRunSettings().isSaveActiveMeasureJustification());
				long saveEnd = System.currentTimeMillis();
                perfLogHldr.setResultsSaveTime(saveEnd - saveStart);
                
                saveStart = System.currentTimeMillis();
				this.saveResultsToHealthagen(msg, runSettingDvos, runMode, srs.getRunSettings().isSaveActiveMeasureJustification());
				saveEnd = System.currentTimeMillis();
                perfLogHldr.setHealthagenSaveTime(saveEnd - saveStart);
				
				boolean updated = this.updateSupplierBatchStatus(supplierBatch, Status.Complete);
				
	            
				if (!updated){
					throw new CEException("Unable to update supplierRunBatch to Complete. ");
				}
			}
			else{
				LOGGER.warn("processMessage(): Batch is cancelled");
			}
		}
		catch(Exception ex){
			LOGGER.error(ex);
			try{
				SupplierBatchDVO supplierBatch = this.getSupplierBatch(msg.getSupplierRunBatchId());
				if (supplierBatch != null) {
					boolean success = this.updateSupplierBatchStatus(supplierBatch, Status.Error);
					if (!success){
						LOGGER.error("Unable to set the batch in error: " + supplierBatch.getSupplierRunBatchId());
					}
				}
			}
			catch(Exception innerEx){
				LOGGER.error(innerEx);
			}
			if (ex instanceof InterruptedException) throw (InterruptedException)ex;
			if (ex instanceof RuntimeException) throw (RuntimeException)ex;
			throw new CEException("Unhandled Exception", ex);
		}
		finally{
			if (PERF_LOGGER.isInfoEnabled()) {
				long end = System.currentTimeMillis();
                perfLogHldr.setTotalRunTime(end - start);
                perfLogHldr.setTimeOnQueue(msg.getJmsMessageTimeOnQueue());
                PERF_LOGGER.info(perfLogHldr.toString());// SimpleProfiler.log() is
                                                     // pushed to .toString()
			    PERF_LOGGER.info("Batch " + msg.getCareEngineRunScheduleId() + "/" + msg.getSupplierRunBatchId() + " with " + totalExecutionMembers + "/" + totalMembers + " members processed in " + (end-start) + " milliseconds");
			}
		}
		return null;
	}


	@Override
	@Transactional(propagation=Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED,
	rollbackFor={InterruptedException.class, RuntimeException.class }, timeout=7500)
	public void runPostProcess(CareEngineMessageIF messageWrapper) throws InterruptedException {
		LOGGER.info("In runPostProcess");
		CareEngineMessage msg = (CareEngineMessage) messageWrapper.getPayload();
		
		LOGGER.info("before updatebatchendtime"+msg);
		updateBatchEndTime(msg);
		
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
		this.adminSuiteToCERefreshDate = cache.getObject(ICacheManager.ADMIN_SUITE_REFRESH_DATE);
//		this.ruleRefreshDate = this.adminDAO.getGuvnorRefreshDate();
		this.ruleRefreshDate = cache.getObject(ICacheManager.GUVNOR_REFRESH_DATE);
		try {
			rabbitCDMTemplate = StarSchemaMessageUtil.loadQueue(StarSchemaMessageUtil.CDM_BATCH_QUEUE);
		} catch (Exception e) {
			LOGGER.warn("init: error in loading rabbitCDMTemplate", e);
			rabbitCDMTemplate = null;
		}
		LOGGER.info("init(): adminSuiteToCERefreshDate=" + this.adminSuiteToCERefreshDate + ", ruleRefreshDate=" + this.ruleRefreshDate);
	}
	
	private void reloadConfiguration(long currentCeRunId, long accountId){
		if (this.lastCeRunId  != currentCeRunId){
			ICacheManager cache = CacheManagerImpl.getInstance();
			this.lastCeRunId = currentCeRunId;
			this.adminSuiteToCERefreshDate = cache.getObject(ICacheManager.ADMIN_SUITE_REFRESH_DATE);
			this.ruleRefreshDate = cache.getObject(ICacheManager.GUVNOR_REFRESH_DATE);
			String saveConfig = null;
			if (adminDAO != null){
				saveConfig = this.adminDAO.getAmSaveConfig(accountId);
			}
			//default
			this.saveToHadoop = false; //this.healthagenRestService != null && this.healthagenRestService.isHealthagenEnabled()?true:false;
			this.saveToOracle = true;
			
			if (saveConfig != null) {
				this.saveToOracle = saveConfig.contains("ORACLE");
				this.saveToHadoop = saveConfig.contains("HADOOP");
			}
			LOGGER.info("reload: asRefDt=" + formatyyyyMMdd_HHmmss(this.adminSuiteToCERefreshDate) + ", ruleRefDt=" + formatyyyyMMdd_HHmmss(this.ruleRefreshDate) + ", saveToOracle=" + saveToOracle + ", saveToHadoop=" + saveToHadoop + ", saveConfig=" + saveConfig);
		}
	}
	
	private String formatyyyyMMdd_HHmmss(Date date) {
		if (date == null) return null;
		return ThreadLocalSimpleDateFormat.yyyyMMdd_HH_mm_ss.get().format(date);
	}

	private void saveResults(CareEngineMessage msg, List<ActiveMeasureRunSettingDVO> runSettingResults, String runMode, boolean saveJustification){
		LOGGER.info("In saveResults: saveToOracle=" + this.saveToOracle + " ,saveJustification=" +saveJustification);
		if (!this.saveToOracle) return;
		ActiveMeasurePersistenceCommandInput tobePersisted = new ActiveMeasurePersistenceCommandInput();
		tobePersisted.setRunId(msg.getCareEngineRunScheduleId()); 
		tobePersisted.setAccountId(msg.getSupplierRunSetting().getRunSettings().getAccountId());
        tobePersisted.setRunDate(msg.getCeRunDate());
        tobePersisted.setRunStatus(Status.Complete.name());
        tobePersisted.setRunMode(runMode);
        tobePersisted.setRunSettingResults(runSettingResults);
        tobePersisted.setSaveJustification(saveJustification);
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
	
	private void saveResultsToHealthagen(CareEngineMessage msg, List<ActiveMeasureRunSettingDVO> runSettingResults, String runMode, boolean saveJustification){
		LOGGER.info("In saveToHgen: saveToHadoop=" + this.saveToHadoop + " ,saveJustification=" +saveJustification);
		if (!this.saveToHadoop) return;
		ActiveMeasurePersistenceCommandInput tobePersisted = new ActiveMeasurePersistenceCommandInput();
		tobePersisted.setRunId(msg.getCareEngineRunScheduleId()); 
		tobePersisted.setAccountId(msg.getSupplierRunSetting().getRunSettings().getAccountId());
        tobePersisted.setRunDate(msg.getCeRunDate());
        tobePersisted.setRunStatus(Status.Complete.name());
        tobePersisted.setRunMode(runMode);
        tobePersisted.setRunSettingResults(runSettingResults);
        tobePersisted.setSaveJustification(saveJustification);
        try {
        	if (this.saveToOracle){
        		//when saving to oracle, submit to hgen and forget
        		healthagenRestService.queueActiveMeasureBatchMessage(tobePersisted);
        	}
        	else{
        		healthagenRestService.postActiveMeasureBatchMessage(tobePersisted);
        	}
        } catch (Exception e) {
            LOGGER.error("saveResultsToHealthagen error", e);
            throw new CEException("Error persisting to healthagen", e);
        }
	}
	
	private void executeRules(List<ActiveMeasureRunSettingDVO> runSettingDvos, List<? extends MemberProfile> memberProfiles, SupplierRunSetting srs, Map<ProductType, ProviderAssignationEnum> provAssigs){
		LOGGER.info("In executeRules");
		Map<Long, MemberProfile> profileMap = new HashMap<Long, MemberProfile>(memberProfiles.size() * 2);
		for (MemberProfile profile : memberProfiles){
			profileMap.put(profile.getMemberInfo().getFactId(), profile);
		}
		
		for (ActiveMeasureRunSettingDVO runSettingDvo : runSettingDvos){
			ActiveMeasureRunSetting amRunSetting = runSettingDvo.getActiveMeasureRunSetting();
			ProviderAssignationEnum provAssig = provAssigs.get(amRunSetting.getProduct());
			long amRunStart = System.currentTimeMillis();
			if (LOGGER.isInfoEnabled()) LOGGER.info("AmRun: " + amRunSetting + ", provAssig: " + provAssig);
			
			Date measurementStartDt = amRunSetting.getMeasurementStartDate();
			Date measurementEndDt = amRunSetting.getMeasurementEndDate();
			Date ceRunDate = amRunSetting.getRunDate();
			if (measurementEndDt == null){
				LOGGER.warn("MeasurementEndDt is NULL. Control should never reach here since new changes ensures CE UI will always populate the dates in CE UI layer.");
				measurementEndDt = CEDateUtil.truncateTimeFromDate(ceRunDate);
	        	measurementStartDt = CEDateUtil.addDays(CEDateUtil.add(measurementEndDt, Calendar.MONTH, -12), 1);
	        }
			
			int memberNumber = 0;
			for (MemberActiveMeasureDVO memberDvo : runSettingDvo.getMemberActiveMeasures()){
				memberNumber++;
				//if (memberDvo.isExecutionSkipped()) continue;
				
				MemberProfile profile = profileMap.get(memberDvo.getMemberId());
				if (profile == null){
					LOGGER.warn("executeRules: Null Profile. Please Check the Issue: " + memberDvo.getMemberId());
					continue;
				}
				if (LOGGER.isDebugEnabled()) LOGGER.debug("executeRules.MemberInfo: " + profile.getMemberInfo());

				memberDvo.setMemberInfo(profile.getMemberInfo());
				
	            MemberProfile boxedProfile = boxManager.applyBox(profile, amRunSetting.getBoxParameters());
	            ActiveMeasureCommandOutput output = executeRules(amRunSetting, measurementStartDt, measurementEndDt, boxedProfile, ceRunDate, memberNumber);
	            
	            
	            applyPackage(amRunSetting, output, memberDvo);
	            //TODO enhance assignProviders to consider ProviderSetting per supplier-product. In addition change to accommodate CASE & CDM also.
	            assignProviders(profile, amRunSetting, measurementStartDt, measurementEndDt, memberDvo, srs, provAssig);
	            
	            if(!srs.getRunSettings().isSaveActiveMeasureJustification()){
	            	memberDvo.clearAllJustifications();
	            }
			}
			SimpleProfilerUtil.recordTimeInMillis(amRunSetting.getMeasurementPeriodType().getCode(), amRunStart);
			
		}
		return;
	}

    private ActiveMeasureCommandOutput executeRules(
            ActiveMeasureRunSetting amSetting, Date measurementStartDt,
            Date measurementEndDt, MemberProfile profile, Date ceRunDate,
            int memberNumber) {
    	LOGGER.info("exec mid: " + (profile.getMemberInfo()!=null?profile.getMemberInfo().getFactId():null));
        ActiveMeasureCommandInput input = new ActiveMeasureCommandInput();
        input.setMemberProfile(profile);

        final DefaultActiveMeasureBuilder activeMeasureBuilder = new DefaultActiveMeasureBuilder();
        final DefaultFactLevelMeasureBuilder flmBuilder = new DefaultFactLevelMeasureBuilder();
		final CacheUsingActiveMeasureHCCBuilder hccCategoryBuilder = new CacheUsingActiveMeasureHCCBuilder(
				modelMapper.getCacheManager());
		final RiskStratificationMeasureWeightsBuilder riskStratificationMeasureWeightsBuilder = new DefaultRiskMeasureWeightsBuilder();
        ActiveMeasureCommandOutput output = new ActiveMeasureCommandOutput(
                activeMeasureBuilder, flmBuilder);

        ActiveMeasureCommandConfiguration config = new ActiveMeasureCommandConfiguration();

        config.updateFrom(amSetting, measurementStartDt, measurementEndDt);
        config.setActiveMeasureBuilder(activeMeasureBuilder);

        config.setClassifierBuilder(new CacheUsingActiveMeasureClassifierBuilder(
                modelMapper.getCacheManager()));

        config.setFactLevelMeasureBuilder(flmBuilder);
        config.setHierarchicalClinicalConditionCategoryBuilder(hccCategoryBuilder);
        config.setRiskStratificationMeasureWeightsBuilder(riskStratificationMeasureWeightsBuilder);
        long start = System.currentTimeMillis();
        try {
            ProductType productType = amSetting.getProduct();

            ActiveMeasureEngine engine = engineSupplier
                    .getEngineByProductType(productType);

            if (engine == null) {
                throw new IllegalArgumentException(getClass().getSimpleName()
                        + " does not support product " + productType
                        + ". amRun: " + amSetting);
            } else {
                engine.execute(input, output, config);
            }

        } catch (Exception e) {
            LOGGER.error("execute error member=" + profile.getMemberInfo(), e);
            throw new CEException("Error executing rules", e);
        } finally {
            if (PERF_LOGGER.isDebugEnabled()) {
                long end = System.currentTimeMillis();
                PERF_LOGGER
                        .debug("Member: " + profile.getMemberInfo().getFactId()
                                + "\tOrder: " + memberNumber
                                + "\tAmRunSetting: " + amSetting.getRunId()
                                + "\tRuleTime: " + (end - start));
            }
        }
        return output;
    }

    private void applyPackage(ActiveMeasureRunSetting amRunSetting, ActiveMeasureCommandOutput output, MemberActiveMeasureDVO memberDvo) {
        LOGGER.debug("In applyPkg");
        Collection<ActiveMeasure> allQualityMeasures = output.getActiveMeasures();
        Collection<FactLevelMeasureDenominator> allCareMeasures = output.getFactLevelMeasureDenominators();
        
        Set<Long> measurePackage = amRunSetting.getActiveMeasurePackage();
        
        if ("CS".equals(amRunSetting.getRunMode()) && (measurePackage == null || measurePackage.isEmpty())){
            LOGGER.debug("applyPkg: Custom Run");
            for (ActiveMeasure firedMeasure : allQualityMeasures) memberDvo.addActiveMeasure(firedMeasure);
            memberDvo.addAllFactLevelMesaureDenominators(allCareMeasures);
            return;
        }

        for (ActiveMeasure firedMeasure : allQualityMeasures) {
            boolean inPkg = true;
            if (measurePackage.contains(firedMeasure.getMeasureId())) {
            	memberDvo.addActiveMeasure(firedMeasure);
            } else {
                inPkg = false;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(firedMeasure
                        + (inPkg ? "" : ", but discarded because of package"));
                LOGGER.debug("Justifications");
                for (RuleJustification justification : firedMeasure
                        .getJustifications()) {
                    LOGGER.debug(justification.toString());
                }
            }
        }
        for (FactLevelMeasureDenominator firedMeasure : allCareMeasures) {
            boolean inPkg = true;
            if (measurePackage.contains(firedMeasure.getMeasureId())) {
            	memberDvo.addFactLevelMeasureDenominator(firedMeasure);
            } else {
                inPkg = false;
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(firedMeasure
                        + (inPkg ? "" : ", but discarded because of package"));
                LOGGER.debug("Justifications");
                for (RuleJustification justification : firedMeasure
                        .getJustifications()) {
                    LOGGER.debug(justification.toString());
                }
            }
        }
        return;
    }

	private void assignProviders(MemberProfile profile, ActiveMeasureRunSetting amRunSetting, Date measurementStartDt, Date measurementEndDt, MemberActiveMeasureDVO memberDvo, SupplierRunSetting srs, ProviderAssignationEnum provAssig){
		if (provAssig == null) return;
		LOGGER.debug("In assignPrvd");
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
	
	private List<DefaultMemberProfile> mapLegacyData(List<MemberHealthRecordIF> mhrList) {
		try {
			LOGGER.info("In mapLegacy");
			List<DefaultMemberProfile> memberProfiles = new ArrayList<DefaultMemberProfile>();
			for (MemberHealthRecordIF mhr : mhrList){
				if(mhr.isValid()){
					DefaultMemberProfile profile = modelMapper.mapMemberHealthRecord(mhr);
					memberProfiles.add(profile);
				}else{
					LOGGER.warn("Skipping Invalid Member:" + mhr.getMemberId() + ", dob=" + mhr.getPatientDateOfBirth() + ", gender=" + mhr.getGender());
				}
			}
			return memberProfiles;
		} catch (RuntimeException e) {
			LOGGER.error("mapLegacyData", e);
			throw e;
		}
		catch (Exception e) {
			LOGGER.error("mapLegacyData", e);
			throw new RuntimeException(e);
		}
    }
	
    private void refactor(DefaultMemberProfile profile){
        try {
        	//LOGGER.info("In refactor");
			memberProfileRefactorer.refactor(profile);
		} catch (RuntimeException e) {
			LOGGER.error("refactor error memberid:" + profile.getMemberInfo()!=null?profile.getMemberInfo().getFactId():null, e);
			throw e;
		}
		catch (Exception e) {
			LOGGER.error("refactor error memberid:" + profile.getMemberInfo()!=null?profile.getMemberInfo().getFactId():null, e);
			throw new RuntimeException(e);
		}
    }
	
	protected List<MemberHealthRecordIF> fetchMemberData(CareEngineMessage message, List<Long> memberIds, Set<ProductType> executionProducts, Map<ProductType, ProviderAssignationEnum> provAssigs) {
        LOGGER.info("In setupBatch");
        MHRBatchDVO mhrBatchDvo = null;
        SupplierRunSetting srs = message.getSupplierRunSetting();
        RunSettings rs = srs.getRunSettings();
        boolean shouldFetchCarePlanData = srs.getRunSettings().isCPPurchased();
        shouldFetchCarePlanData = false;
        boolean shouldFetchFinancialData = srs.isFinanceClaimsflag();
        shouldFetchFinancialData = false;
        Set<DataType> optionalDataTypes = rs.getOptionalDataTypes();
        
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

        /*mhrBatchDvo = csa.getMemberHealthRecords(memberIds, 
        										message.getSupplierRunSetting().getProjectId() ,
        										rs.getDateOfServiceStart(), 
        										rs.getDateOfServiceEnd(), 
        										rs.getRunClock(), 
        										message.getSupplierBatchDVO().getSupplierRunBatchId(), 
        										excludeData,
        										rs.shouldFetchHieData(),
        										shouldFetchCarePlanData,
        										optionalDataTypes,
        										rs.getAccountId(),
        										shouldFetchFinancialData,
        										srs.getRunSettings().getMasterId()
        										);*/
        mhrBatchDvo = csa.getMemberHealthRecords(memberIds, 
				message.getSupplierRunSetting().getProjectId() ,
				rs.getDateOfServiceStart(), 
				rs.getDateOfServiceEnd(), 
				rs.getRunClock(), 
				message.getSupplierBatchDVO().getSupplierRunBatchId(), 
				excludeData,
				rs.shouldFetchHieData(),
				shouldFetchCarePlanData,
				optionalDataTypes,
				rs.getAccountId(),
				shouldFetchFinancialData,
				srs.getRunSettings().getMasterId(), shouldFetchFinancialData
				);
        if (mhrBatchDvo == null || mhrBatchDvo.getMemberList() == null) {
            return new ArrayList<MemberHealthRecordIF>();
        }
        return mhrBatchDvo.getMemberList();
    }

	protected Map<Long, MemberLockData> getMemberLockData(CareEngineMessage message){
		LOGGER.info("In memberLockData");
		Map<Long, MemberLockData> _returnMap = new HashMap<Long, MemberLockData>();
		List<Long> memberIds = message.getSupplierBatchDVO().getMemberIds();
		List<MemberLockData> memberDirtyBitList = supplierDAO.getMemberLockData(memberIds);
		for (MemberLockData mlock : memberDirtyBitList) _returnMap.put(mlock.getMemberId(), mlock);
		return _returnMap;
	}
	
	protected List<ActiveMeasureRunSettingDVO> doSkipLogic(CareEngineMessage msg, List<ActiveMeasureRunSetting> amRunSettings, String runMode){
		LOGGER.info("In doSkipLogic");
		if (amRunSettings == null || amRunSettings.size() == 0){
			return new ArrayList<ActiveMeasureRunSettingDVO>();
		}
		
		List<ActiveMeasureRunSettingDVO> runSettingDvos = new ArrayList<ActiveMeasureRunSettingDVO>(amRunSettings.size());
		
		List<Long> memberIds = msg.getSupplierBatchDVO().getMemberIds();
		Date runDate = msg.getCeRunDate();
		
		//Map<ActiveMeasureRunSetting, Map<Long, MemberActiveMeasureRun>> lastMemberRuns = null;
        Map<Long, MemberLockData> memberLockMap = null;
        
        if ("CS".equals(runMode)){
        	//lastMemberRuns = new LinkedHashMap<ActiveMeasureRunSetting, Map<Long,MemberActiveMeasureRun>>();
        	/*for (ActiveMeasureRunSetting amRunSetting : amRunSettings){
    			Map<Long, MemberActiveMeasureRun> runMap = new HashMap<Long, MemberActiveMeasureRun>();
    			lastMemberRuns.put(amRunSetting, runMap);
        	}*/
        	memberLockMap = new HashMap<Long, MemberLockData>();
        }
        else{
        	//lastMemberRuns = getLastMemberActiveMeasureRuns(msg, amRunSettings);
        	memberLockMap = getMemberLockData(msg);
        }
        
		for (ActiveMeasureRunSetting amRunSetting : amRunSettings){
			if (LOGGER.isInfoEnabled()) 
				LOGGER.info("skipLogic.AmRun: batch: " + msg.getCareEngineRunScheduleId() + "/" + msg.getSupplierRunBatchId() + ", " + amRunSetting + ", runDate=" + runDate);
			ActiveMeasureRunSettingDVO runSettingDvo = new ActiveMeasureRunSettingDVO();
			runSettingDvo.setActiveMeasureRunSetting(amRunSetting);
			runSettingDvos.add(runSettingDvo);
			
			Date lastRunDate = null;
			if (amRunSetting.getPrevRunDetailSetting() != null){
				lastRunDate = amRunSetting.getPrevRunDetailSetting().getRunDate();
			}
			//Map<Long, MemberActiveMeasureRun> lastMemberRunsForThisAm = lastMemberRuns.get(amRunSetting);
			
			for (Long memberId : memberIds){
				MemberLockData memberLock = memberLockMap.get(memberId);
				if (shouldSkipMemberExecution(amRunSetting, lastRunDate, memberLock, runDate)){
					if (LOGGER.isInfoEnabled()){
						LOGGER.info("doSkipLogic().SkippedMember: lrd=" + lastRunDate + ", ml=" + memberLock + ", asRefDt=" + this.adminSuiteToCERefreshDate + ", ruleRefDt=" + this.ruleRefreshDate);
					}
					runSettingDvo.addSkipMember(memberId);
				}
				else{
					MemberActiveMeasureDVO memberDvo = new MemberActiveMeasureDVO();
		            memberDvo.setMemberId(memberId);
					//MemberActiveMeasureRun lastRun = lastMemberRunsForThisAm.get(memberId);
					memberDvo.setLastRunDate(lastRunDate);
					runSettingDvo.addMemberActiveMeasure(memberDvo);
				}
			}
		}
		return runSettingDvos;
	}
	
	protected boolean shouldSkipMemberExecution(ActiveMeasureRunSetting amRunSetting, Date lastRunDate, MemberLockData memberLock, Date runDate){
		if (saveToHadoop) return false;
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
			//Rolling Mode
			if (this.neverSkipRollingMode) return false;
			long daysSinceLastRun = CEDateUtil.getDaysBetween(lastRunDate, runDate);
			if (daysSinceLastRun > this.rollingModeSkipToleranceInDays) return false;
			break;
		case ROLLING_QTRLY:
		case ROLLING_MONTHLY:
			if (amRunSetting.getPrevRunDetailSetting() != null){
				if (amRunSetting.getPrevRunDetailSetting().getMeasurementEndDate().compareTo(amRunSetting.getMeasurementEndDate()) != 0){
					return false;
				}
			}
			else{
				return false;
			}
			break;
		case CALENDAR: break;
		}
		return true;
	}
	
	/*public static void main(String[] args){
		Calendar cal1 = CEDateUtil.truncateTimeFromDate(Calendar.getInstance());
		Calendar cal2 = CEDateUtil.truncateTimeFromDate(Calendar.getInstance());
		cal1.set(2015, Calendar.DECEMBER, 31);
		cal2.set(2015, Calendar.JANUARY, 1);
		System.out.println(CEDateUtil.getDaysBetween(cal2.getTime(), cal1.getTime()));
		System.out.println(CEDateUtil.improvedGetDaysBetween(cal2.getTime(), cal1.getTime()));
	}*/
	
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
	
	protected SupplierRunSetting getSupplierRunSetting(long supplierRunScheduleId){
		LOGGER.debug("In getSRS");
        try{

            SupplierRunSetting srs = supplierRunDAO.getAmSupplierRunSetting(supplierRunScheduleId);

            return srs;
        }
        catch(Exception e){
            LOGGER.fatal("getSupplierRunSetting(): Unable to get supplier run settings for supplierRunScheduleId " + supplierRunScheduleId, e);
            throw new CEException("Unable to get supplier run settings for supplierRunScheduleId " + supplierRunScheduleId, e);
        }
	}
	
	
	protected SupplierBatchDVO getSupplierBatch(long supplierRunBatchId){
		SupplierBatchDVO supplierBatch = supplierBatchDAO.getSupplierRunBatchDvo(supplierRunBatchId);
		
		LOGGER.info("getSupplierBatch: Run in Pending or Error : " + supplierBatch.getSupplierRunBatchId() + ": " + supplierBatch.getStatusCode());
		
		return supplierBatch;
	}
	
	protected boolean isSupplierBatchProcessed(SupplierBatchDVO supplierBatch){
		if (supplierBatch.getStatusCode() != Status.Pending && supplierBatch.getStatusCode() !=  Status.Error) {
			LOGGER.error("isSupplierBatchProcessed: Run Already in Progress or Complete. " + supplierBatch.getSupplierRunBatchId()  + ", " + supplierBatch.getStatusCode());
			return true;
		}
		return false;
	}
	
	protected boolean updateSupplierBatchStatus(SupplierBatchDVO supplierBatch, Status status){
		boolean updated = supplierBatchDAO.updateSupplierRunBatch(supplierBatch, status);
		if (!updated) {
			LOGGER.error("updateSupplierBatchStatus: Unable to update supplierRunBatch to status. " + supplierBatch.getSupplierRunBatchId()  + ", " + supplierBatch.getStatusCode());
		}
		return updated;
	}
	
	protected void updateBatchStartTime(long careengineRunScheduleId){
		supplierRunDAO.updateRunStartTime(careengineRunScheduleId, new Date(), Status.InProgress);
	}
	protected void updateBatchEndTime(CareEngineMessage msg){
	LOGGER.info("Inside start updateBatchEndTime"+msg);
	LOGGER.info("Inside careenginemessage"+msg.getSupplierRunSetting());   
	LOGGER.info("Inside careenginemessage"+msg.getSupplierRunSetting().getRunSettings());  
	LOGGER.info("Inside careenginemessage"+msg.getSupplierRunSetting().getRunSettings().getAccountId());  
		long accountId = msg.getSupplierRunSetting().getRunSettings().getAccountId();
		LOGGER.info("Inside updateBatchEndTime account Id"+accountId);
		long careengineRunScheduleId = msg.getCareEngineRunScheduleId();
		LOGGER.info("Inside updateBatchEndTime careengineRunScheduleId"+careengineRunScheduleId);
		SupplierRunSetting srs = msg.getSupplierRunSetting();
		LOGGER.info("Inside updateBatchEndTime getSupplierRunSetting"+ msg.getSupplierRunSetting());
		Map<Status, Integer> statusCounts = supplierRunDAO.getBatchCountByStatus(careengineRunScheduleId);
		boolean pending = statusCounts.containsKey(Status.Pending);
		boolean inprogress = statusCounts.containsKey(Status.InProgress);
		boolean cancelled = statusCounts.containsKey(Status.Cancelled);
		
		if (pending || inprogress){
			//skip
		}
		else if (cancelled){
			this.activeMeasureBatchConfigDao.updateMeasureRunStatus(accountId, careengineRunScheduleId, Status.Cancelled.name());
		}
		else{
			boolean errored = statusCounts.containsKey(Status.Error);
			Status status = errored?Status.Error:Status.Complete;
			Date updateDt = new Date();
			int cerunUpdCnt = supplierRunDAO.updateRunEndTime(careengineRunScheduleId, updateDt, status, Status.InProgress);
			if (cerunUpdCnt < 1){
				LOGGER.error("updateBatchEndTime(): Could not set status to " + status + ", cerunUpdCnt=" + cerunUpdCnt);
				//status = Status.Error;
				return;
			}
			
			/*if (status == Status.Complete){
				try{
					this.activeMeasureOutputDAO.updateMemberLastRunDetails(msg.getSupplierRunSetting().getRunSettings().getAccountId(), msg.getCareEngineRunScheduleId());
				}
				catch(Exception ex){
					log.error(ex);
					status = Status.Error;
					supplierRunDAO.updateRunEndTime(careengineRunScheduleId, updateDt, status, Status.Complete);
				}
			}*/
			
			int amrunUpdCnt = this.activeMeasureBatchConfigDao.updateMeasureRunStatus(accountId, careengineRunScheduleId, status.name());
			if (status != Status.Error && cerunUpdCnt > 0 && amrunUpdCnt > 0){
				boolean unlockRun = true;
				if (srs.getRunSettings().getRunProducts().contains(ProductType.ACDM)){
					unlockRun = notifyCDMEtl(msg);
					if (!unlockRun){
						LOGGER.error("updateBatchEndTime(): Could not release master supplier: error in notifying CDM ETL");
					}
				}
				if (unlockRun){
					runResultsHandler.releaseMasterSupplierLock(srs.getRunSettings().getMasterId(), ExecutionWorkflow.ACTIVEMEASURE.getLockNumber());
				}
			}
			else{
				LOGGER.error("updateBatchEndTime(): Could not release master supplier: error=" + statusCounts.get(Status.Error) + ", cerunUpdCnt=" + cerunUpdCnt + ", amrunUpdCnt=" + amrunUpdCnt);
			}
		}
		LOGGER.info("Inside end updateBatchEndTime");
	}

	protected boolean notifyCDMEtl(CareEngineMessage message){
		LOGGER.info("notifyCDMEtl()");
		if (!this.saveToOracle){
			LOGGER.warn("notifyCDMEtl: TODO confirm if Non-Oracle accounts need to notify.. For now skipping notification");
			return true;
		}
		if (rabbitCDMTemplate == null){
			LOGGER.warn("notifyCDMEtl: rabbitCDMTemplate is null. For now skipping notification");
			return true;
		}
		boolean success = false;
		try{
			SupplierRunSetting srs = message.getSupplierRunSetting();
			CDMBatchNotification cdmBatchNotify = new CDMBatchNotification(message.getCareEngineRunScheduleId(),
					(long)srs.getRunSettings().getAccountId(), 
					-1, 
					true,
					false,
					ProductType.ACDM);
			StarSchemaMessageUtil.sendCDMNotification(rabbitCDMTemplate, cdmBatchNotify);
			success = true;
		}
		catch(Exception ex){
			LOGGER.warn("notifyCDMEtl: ", ex);
		}
		return success;
	}
	
	protected boolean isAnySupplierBatchCancelled(long careengineRunScheduleId){
		int canceledcount = supplierRunDAO.getBatchCountByStatus(careengineRunScheduleId, Status.Cancelled);
		return canceledcount > 0;
	}

	public SupplierRunBatchDAO getSupplierBatchDAO() {
		return supplierBatchDAO;
	}

	public void setSupplierBatchDAO(SupplierRunBatchDAO supplierBatchDAO) {
		this.supplierBatchDAO = supplierBatchDAO;
	}

	public SupplierRunDAO getSupplierRunDAO() {
		return supplierRunDAO;
	}

	public void setSupplierRunDAO(SupplierRunDAO supplierRunDAO) {
		this.supplierRunDAO = supplierRunDAO;
	}

	public CEv2Mapper getModelMapper() {
		return modelMapper;
	}

	public void setModelMapper(CEv2Mapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public DefaultMemberProfileRefactorer getMemberProfileRefactorer() {
		return memberProfileRefactorer;
	}

	public void setMemberProfileRefactorer(DefaultMemberProfileRefactorer memberProfileRefactorer) {
		this.memberProfileRefactorer = memberProfileRefactorer;
	}

    public ActiveMeasureEngineSupplier getEngineSupplier() {
        return engineSupplier;
    }

    public void setEngineSupplier(ActiveMeasureEngineSupplier engineSupplier) {
        this.engineSupplier = engineSupplier;
    }

	public CommandIF<ActiveMeasurePersistenceCommandInput, ActiveMeasurePersistenceCommandOutput, ActiveMeasurePersistenceCommandConfiguration> getActiveMeasurePersistenceCommand() {
		return activeMeasurePersistenceCommand;
	}

	public void setActiveMeasurePersistenceCommand(
			CommandIF<ActiveMeasurePersistenceCommandInput, ActiveMeasurePersistenceCommandOutput, ActiveMeasurePersistenceCommandConfiguration> activeMeasurePersistenceCommand) {
		this.activeMeasurePersistenceCommand = activeMeasurePersistenceCommand;
	}

	public BoxManager getBoxManager() {
		return boxManager;
	}

	public void setBoxManager(BoxManager boxManager) {
		this.boxManager = boxManager;
	}

	public ActiveMeasureBatchConfigDao getActiveMeasureBatchConfigDao() {
		return activeMeasureBatchConfigDao;
	}

	public void setActiveMeasureBatchConfigDao(ActiveMeasureBatchConfigDao activeMeasureBatchConfigDao) {
		this.activeMeasureBatchConfigDao = activeMeasureBatchConfigDao;
	}

	public RunResultsHandler getRunResultsHandler() {
		return runResultsHandler;
	}

	public void setRunResultsHandler(RunResultsHandler runResultsHandler) {
		this.runResultsHandler = runResultsHandler;
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

	public HealthagenRestService getHealthagenRestService() {
		return healthagenRestService;
	}

	public void setHealthagenRestService(HealthagenRestService healthagenRestService) {
		this.healthagenRestService = healthagenRestService;
	}

	public AdminDAO getAdminDAO() {
		return adminDAO;
	}

	public void setAdminDAO(AdminDAO adminDAO) {
		this.adminDAO = adminDAO;
	}


	public ProviderAssignationService getProviderAssignationService() {
		return providerAssignationService;
	}


	public void setProviderAssignationService(ProviderAssignationService providerAssignationService) {
		this.providerAssignationService = providerAssignationService;
	}


	public RabbitTemplate getRabbitCDMTemplate() {
		return rabbitCDMTemplate;
	}


	public void setRabbitCDMTemplate(RabbitTemplate rabbitCDMTemplate) {
		this.rabbitCDMTemplate = rabbitCDMTemplate;
	}

}

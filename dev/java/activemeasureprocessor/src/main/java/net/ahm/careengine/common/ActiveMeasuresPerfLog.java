package net.ahm.careengine.common;

import net.ahm.careengine.util.SimpleProfilerUtil;

import org.apache.commons.lang.StringUtils;

public class ActiveMeasuresPerfLog extends BatchPerfLog {

    private long ceRunScheduleId    = 0l;

    private long amrsTime           = 0l;

    private long skipLogicTime      = 0l;

    private long mapAndRefactorTime = 0l;

    private long rulesRunCpuTime    = 0l;
    
    protected long healthagenSaveTime = 0l;

    public ActiveMeasuresPerfLog(String processorType) {
        super(processorType);
    }

    public long getCeRunScheduleId() {
        return ceRunScheduleId;
    }

    public void setCeRunScheduleId(long ceRunScheduleId) {
        this.ceRunScheduleId = ceRunScheduleId;
    }

    public long getAmrsTime() {
        return amrsTime;
    }

    public void setAmrsTime(long amrsTime) {
        this.amrsTime = amrsTime;
    }

    public long getSkipLogicTime() {
        return skipLogicTime;
    }

    public void setSkipLogicTime(long skipLogicTime) {
        this.skipLogicTime = skipLogicTime;
    }

    public long getMapAndRefactorTime() {
        return mapAndRefactorTime;
    }

    public void setMapAndRefactorTime(long mapAndRefactorTime) {
        this.mapAndRefactorTime = mapAndRefactorTime;
    }

    public long getRulesRunCpuTime() {
        return rulesRunCpuTime;
    }

    public void setRulesRunCpuTime(long ruleRunCpuTime) {
        this.rulesRunCpuTime = ruleRunCpuTime;
    }
    
    public long getHealthagenSaveTime() {
		return healthagenSaveTime;
	}

	public void setHealthagenSaveTime(long healthagenSaveTime) {
		this.healthagenSaveTime = healthagenSaveTime;
	}

    @Override
    public String toString() {
        StringBuffer logStrBuf = new StringBuffer();

        if (StringUtils.isNotBlank(this.processorType)) {
            logStrBuf.append(LOGConstants.CEBATCHID)
            		.append(CELOGSEPARATOR).append(this.ceRunScheduleId)
            		.append(LOGConstants.SPACE).append(LOGConstants.SUPPLIERBATCHID)
                    .append(CELOGSEPARATOR).append(this.supplierBatchId)
                    .append(LOGConstants.SPACE).append(LOGConstants.COUNT)
                    .append(CELOGSEPARATOR).append(this.memberCount)
                    .append(LOGConstants.SPACE)
                    .append(LOGConstants.PROCESSORTYPE).append(CELOGSEPARATOR)
                    .append(this.processorType).append(LOGConstants.SPACE)
                    .append(LOGConstants.SRS).append(CELOGSEPARATOR)
                    .append(this.srsTime).append(LOGConstants.SPACE)
                    .append(LOGConstants.AMRS).append(CELOGSEPARATOR)
                    .append(this.amrsTime).append(LOGConstants.SPACE)
                    .append(LOGConstants.SKIPLOGIC).append(CELOGSEPARATOR)
                    .append(this.skipLogicTime).append(LOGConstants.SPACE)
                    .append(LOGConstants.ODSGET).append(CELOGSEPARATOR)
                    .append(this.odsGetTime).append(LOGConstants.SPACE)
                    .append(LOGConstants.MAPANDREFACTOR).append(CELOGSEPARATOR)
                    .append(this.mapAndRefactorTime).append(LOGConstants.SPACE)
                    .append(LOGConstants.RULES).append(CELOGSEPARATOR)
                    .append(this.rulesRunTime).append(LOGConstants.SPACE)
                    .append(LOGConstants.RULESINCPU).append(CELOGSEPARATOR)
                    .append(this.rulesRunCpuTime).append(LOGConstants.SPACE)
                    .append(LOGConstants.SAVE).append(CELOGSEPARATOR)
                    .append(this.resultsSaveTime).append(LOGConstants.SPACE)
                    .append("Healthagen").append(CELOGSEPARATOR)
                    .append(this.healthagenSaveTime).append(LOGConstants.SPACE)
                    .append(LOGConstants.TOTAL).append(CELOGSEPARATOR)
                    .append(this.totalRunTime).append(LOGConstants.SPACE)
                    .append(LOGConstants.TIMEONQUEUEINMS)
                    .append(CELOGSEPARATOR).append(this.timeOnQueue);
            SimpleProfilerUtil.log(logStrBuf, true);
        }
        return logStrBuf.toString();
    }

    public static void main(String[] args) {
        System.out
                .println(".."
                        + new ActiveMeasuresPerfLog(
                                "net.ahm.careengine.controller.ActiveMeasureBatchProcessor")
                                .toString());
    }
}

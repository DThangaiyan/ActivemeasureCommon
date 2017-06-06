package net.ahm.careengine.common;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Date;

import net.ahm.careengine.message.ExecutionMode;
import net.ahm.careengine.util.CEDateUtil;
import net.ahm.careengine.util.SimpleProfilerUtil;
import net.ahm.careengine.util.ThreadLocalSimpleDateFormat;

import org.junit.Test;

public class BatchPerfLogTest {

    static final Date   DATE;
    final static String formatDateStr;
    static {
        try {
            DATE = ThreadLocalSimpleDateFormat.MMddyyyy.get().parse(
                    "05/21/2015");
            formatDateStr = CEDateUtil.formatYYYYMMDD(DATE);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPerLogToString_ActiveMeasureBatch() {
        ActiveMeasuresPerfLog amPerfLog = new ActiveMeasuresPerfLog(ExecutionMode.ACTIVEMEASURE.name());
        final long healthagenTime = 45l;

        final String expectedLog = "SupplierBatchId:123 MemberCount:1 ProcessorType:ACTIVEMEASURE SRS:10 AMSettings:20 SkipLogic:30 ODSGET:40 MapAndRefactor:50 Rules:60 RulesInCpu:100 Save:70 Healthagen:"
                + healthagenTime + " Total:270 TimeOnQueueInMS:280";

        amPerfLog.setSupplierBatchId(123l);
        amPerfLog.setMemberCount(1l);
        amPerfLog.setCeRunScheduleId(12l);
        amPerfLog.setSrsTime(10l);
        amPerfLog.setAmrsTime(20l);
        amPerfLog.setSkipLogicTime(30l);
        amPerfLog.setOdsGetTime(40l);
        amPerfLog.setMapAndRefactorTime(50l);
        amPerfLog.setRulesRunTime(60l);
        amPerfLog.setRulesRunCpuTime(100l);
        amPerfLog.setResultsSaveTime(70l);
        amPerfLog.setTotalRunTime(270l);
        amPerfLog.setHealthagenSaveTime(healthagenTime);
        amPerfLog.setTimeOnQueue(280l);
        SimpleProfilerUtil.recordTimeInMillis(formatDateStr, 100000);

        final String actualPerfString = amPerfLog.toString();
        assertTrue(actualPerfString.contains(expectedLog));

        assertTrue(actualPerfString.contains(formatDateStr));
    }

    @Test
    public void testPerLogToString_Realtime() {
        BatchPerfLog perfLog = new BatchPerfLog(ExecutionMode.REALTIME.name());
        String expectedLog = "Member:250 ProcessorType:REALTIME SRS:10 Lock:5 ODSGET:40 Rules:60 Save:70 ModelEngine:55 Unlock:5 Total:270 TimeOnQueueInMS:280";

        perfLog.setMemberIds(250l);
        perfLog.setSrsTime(10l);
        perfLog.setLockTime(5l);
        perfLog.setOdsGetTime(40l);
        perfLog.setRulesRunTime(60l);
        perfLog.setResultsSaveTime(70l);
        perfLog.setModelSaveTime(55l);
        perfLog.setUnlockTime(5l);
        perfLog.setTotalRunTime(270l);
        perfLog.setTimeOnQueue(280l);

        assertTrue((perfLog.toString()).equals(expectedLog));
    }

    @Test
    public void testPerLogToString_Batch() {
        BatchPerfLog perfLog = new BatchPerfLog(ExecutionMode.BATCH.name());
        String expectedLog = "SupplierBatchId:123 MemberCount:0 ProcessorType:BATCH SRS:10 Lock:5 ODSGET:40 Rules:60 Save:70 ModelEngine:55 Unlock:5 Total:270 TimeOnQueueInMS:280";

        perfLog.setSupplierBatchId(123l);
        perfLog.setSrsTime(10l);
        perfLog.setLockTime(5l);
        perfLog.setOdsGetTime(40l);
        perfLog.setRulesRunTime(60l);
        perfLog.setResultsSaveTime(70l);
        perfLog.setModelSaveTime(55l);
        perfLog.setUnlockTime(5l);
        perfLog.setTotalRunTime(270l);
        perfLog.setTimeOnQueue(280l);

        assertTrue((perfLog.toString()).equals(expectedLog));
    }


}

package net.ahm.careengine.command.activemeasure;

import java.util.Date;

import net.ahm.careengine.activemeasure.ActiveMeasureCommandOutput;
import net.ahm.careengine.domain.event.EventSourceType;
import net.ahm.careengine.domain.event.diag.DiagnosticEvent;
import net.ahm.careengine.domain.impl.diag.DefaultDiagnosticEvent;
import net.ahm.careengine.domain.impl.justification.DefaultRuleJustification;
import net.ahm.careengine.domain.impl.measure.active.DefaultActiveMeasureBuilder;
import net.ahm.careengine.domain.impl.measure.active.DefaultFactLevelMeasureBuilder;
import net.ahm.careengine.domain.impl.measure.active.DefaultFactLevelMeasureDenominator;
import net.ahm.careengine.domain.justification.RuleJustification;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.measures.active.impl.MemberActiveMeasure;
import net.ahm.careengine.util.ThreadLocalSimpleDateFormat;

import org.junit.Assert;
import org.junit.Test;


public class ActiveMeasureCommandOuputTest  {

    @Test
    public void testJustificationClearedOut() throws Exception {
    	
        DefaultActiveMeasureBuilder activeMeasureBuilder = new DefaultActiveMeasureBuilder();
        activeMeasureBuilder.addActiveMeasure(buildMemberActiveMesure(1, 1, parseDate("2016-10-25")));
        DefaultFactLevelMeasureBuilder flmBuilder = new DefaultFactLevelMeasureBuilder();
        flmBuilder.addFactLevelMeasureDenominator(buildFactLevelMeasureDenom(1,parseDate("2016-10-25")));
        ActiveMeasureCommandOutput output = new ActiveMeasureCommandOutput(
                activeMeasureBuilder, flmBuilder);

        Assert.assertTrue("ActiveMeasure Justification should not be empty", !output.getActiveMeasures().iterator().next().getJustifications().isEmpty());

        output.clearAllJustifications();
    	
    	
        Assert.assertFalse("ActiveMeasure is empty", output.getActiveMeasures().isEmpty());
        Assert.assertTrue("ActiveMeasure should not be empty", output.getActiveMeasures().iterator().next().getMeasureId() == 1);

        Assert.assertTrue("ActiveMeasure Justification should be empty", output.getActiveMeasures().iterator().next().getJustifications().isEmpty());
        Assert.assertTrue("FactLevelDenominator Justification should be empty", output.getFactLevelMeasureDenominators().iterator().next().getJustifications().isEmpty());
    }
    


    private MemberActiveMeasure buildMemberActiveMesure(long measureId, long factId, Date eventDate) {
    	MemberActiveMeasure mam = new MemberActiveMeasure();
    	RuleJustification rj = new DefaultRuleJustification();
    	rj.getFacts().add(createDiagEvent(factId, eventDate));
    	mam.setMeasureId(measureId);
    	mam.getJustifications().add(rj);
    	return mam;
	}

   	protected DiagnosticEvent createDiagEvent(long factId, Date eventDate){
   		DefaultDiagnosticEvent itm = new DefaultDiagnosticEvent();
   		itm.addEventSourceType(EventSourceType.CLAIM);
   		itm.setFactId(factId);
        itm.setStartDate(eventDate);
    	return itm;
    }
    	
    private FactLevelMeasureDenominator buildFactLevelMeasureDenom(long factId, Date eventDate) {
    	FactLevelMeasureDenominator flmDenom = new DefaultFactLevelMeasureDenominator(factId, null);
    	RuleJustification rj = new DefaultRuleJustification();
    	rj.getFacts().add(createDiagEvent(factId, eventDate));
    	flmDenom.addJustification(rj);
    	flmDenom.setExcludedFromDenominator(true);
    	return flmDenom;
	}

	protected Date parseDate(String date){
		try{
			if (date != null){
				return ThreadLocalSimpleDateFormat.yyyy_MM_dd.get().parse(date);
			}
			return null;
		}
		catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
}
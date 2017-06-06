package net.ahm.cev4.service.provider;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.*;

import net.ahm.careengine.domain.event.EventSourceType;
import net.ahm.careengine.domain.event.diag.DiagnosticEvent;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.domain.impl.diag.DefaultDiagnosticEvent;
import net.ahm.careengine.domain.impl.justification.DefaultRuleJustification;
import net.ahm.careengine.domain.impl.measure.active.DefaultFactLevelMeasureDenominator;
import net.ahm.careengine.domain.impl.provider.DefaultCareProvider;
import net.ahm.careengine.domain.justification.RuleJustification;
import net.ahm.careengine.domain.measures.active.ActiveMeasure;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureNumerator;
import net.ahm.careengine.domain.measures.active.impl.MemberActiveMeasure;
import net.ahm.careengine.domain.provider.CareProvider;
import net.ahm.careengine.provider.ProviderAssignationEnum;
import net.ahm.careengine.util.ThreadLocalSimpleDateFormat;
import net.ahm.cev4.activemeasures.config.ActiveMeasurePeriodType;

public class ProviderAssignationServiceTest {
	
	@Test
	public void test_001_NullMeasureTest(){
		ProviderAssignationInput input = createInput(ProviderAssignationEnum.MRPROV, null, null);
		ProviderAssignationService provService = new ProviderAssignationService();
		provService.assignProviders(input);
		
		input.setProviderAssignation(ProviderAssignationEnum.MFPROV);
		provService.assignProviders(input);
	}

	@Test
	public void test_002_EmptyRuleJustificationTest(){
		ProviderAssignationService provService = new ProviderAssignationService();
		
		ActiveMeasure am = createActiveMeasure();
		FactLevelMeasureDenominator flmD = createFactLevelMeasureDenominator();
		
		ProviderAssignationEnum assigCode = ProviderAssignationEnum.MFPROV;
		ProviderAssignationInput input = createInput(assigCode, am, flmD);
		provService.assignProviders(input);
		assertNull(assigCode + ": AM: ProviderAssignation must be null when justification is empty", am.getProviderAssignation());
		assertNull(assigCode + ": MRP: FLMD: ProviderAssignation must be null when justification is empty", flmD.getProviderAssignation());
		
		assigCode = ProviderAssignationEnum.MRPROV;
		input.setProviderAssignation(assigCode);
		provService.assignProviders(input);
		assertNull(assigCode + ": AM: ProviderAssignation must be null when justification is empty", am.getProviderAssignation());
		assertNull(assigCode + ": FLMD: ProviderAssignation must be null when justification is empty", flmD.getProviderAssignation());
	}
	
	@Test
	public void test_003_EmptyFactsTest(){
		ProviderAssignationService provService = new ProviderAssignationService();
		RuleJustification rj = createRuleJustification("NUMERATOR");
		ActiveMeasure am = createActiveMeasure(rj);
		FactLevelMeasureDenominator flmD = createFactLevelMeasureDenominator(rj);
		
		ProviderAssignationEnum assigCode = ProviderAssignationEnum.MRPROV;
		ProviderAssignationInput input = createInput(assigCode, am, flmD);
		provService.assignProviders(input);
		assertNull(assigCode + ": AM: ProviderAssignation must be null when fact list is empty", am.getProviderAssignation());
		assertNull(assigCode + ": MRP: FLMD: ProviderAssignation must be null when fact list is empty", flmD.getProviderAssignation());
		
		assigCode = ProviderAssignationEnum.MFPROV;
		input.setProviderAssignation(assigCode);
		provService.assignProviders(input);
		assertNull(assigCode + ": AM: ProviderAssignation must be null when fact list is empty", am.getProviderAssignation());
		assertNull(assigCode + ": FLMD: ProviderAssignation must be null when fact list is empty", flmD.getProviderAssignation());
	}
	
	@Test
	public void test_004_NullCareProviderTest(){
		ProviderAssignationService provService = new ProviderAssignationService();
		DiagnosticEvent diagEvent1 = createDiagEvent(1, parseDate("2015-03-01"), 0l);
		RuleJustification rj = createRuleJustification("NUMERATOR", diagEvent1);
		ActiveMeasure am = createActiveMeasure(rj);
		FactLevelMeasureDenominator flmD = createFactLevelMeasureDenominator(rj);
		
		ProviderAssignationEnum assigCode = ProviderAssignationEnum.MRPROV;
		ProviderAssignationInput input = createInput(assigCode, am, flmD);
		provService.assignProviders(input);
		assertNull(assigCode + ": AM: ProviderAssignation must be null when all facts have null provider", am.getProviderAssignation());
		assertNull(assigCode + ": MRP: FLMD: ProviderAssignation must be null when all facts have null provider", flmD.getProviderAssignation());
		
		assigCode = ProviderAssignationEnum.MFPROV;
		input.setProviderAssignation(assigCode);
		provService.assignProviders(input);
		assertNull(assigCode + ": AM: ProviderAssignation must be null when all facts have null provider", am.getProviderAssignation());
		assertNull(assigCode + ": FLMD: ProviderAssignation must be null when all facts have null provider", flmD.getProviderAssignation());
	}
	
	@Test
	public void test_005_SingleCareProviderInNumeratorTest(){
		ProviderAssignationService provService = new ProviderAssignationService();
		
		DiagnosticEvent diagEvent1 = createDiagEvent(1, parseDate("2015-03-01"), 1234l);
		RuleJustification rj1 = createRuleJustification("NUMERATOR", diagEvent1);
		
		DiagnosticEvent diagEvent2 = createDiagEvent(1, parseDate("2015-06-01"), 5678l);
		RuleJustification rj2 = createRuleJustification("DENOM", diagEvent2);
		
		ActiveMeasure am = createActiveMeasure(rj1, rj2);
		FactLevelMeasureDenominator flmD = createFactLevelMeasureDenominator(rj1, rj2);
		
		ProviderAssignationEnum assigCode = ProviderAssignationEnum.MRPROV;
		ProviderAssignationInput input = createInput(assigCode, am, flmD);
		provService.assignProviders(input);
		assertEquals(assigCode + ": AM: Numerator Provider should win", diagEvent1.getCareProvider().getCareProviderId(), am.getProviderAssignation().getCareProvider().getCareProviderId());
		assertEquals(assigCode + ": MRP: FLMD: Numerator Provider should win", diagEvent1.getCareProvider().getCareProviderId(), flmD.getProviderAssignation().getCareProvider().getCareProviderId());
		
		assigCode = ProviderAssignationEnum.MFPROV;
		input.setProviderAssignation(assigCode);
		provService.assignProviders(input);
		assertEquals(assigCode + ": AM: Numerator Provider should win", diagEvent1.getCareProvider().getCareProviderId(), am.getProviderAssignation().getCareProvider().getCareProviderId());
		assertEquals(assigCode + ": MRP: FLMD: Numerator Provider should win", diagEvent1.getCareProvider().getCareProviderId(), flmD.getProviderAssignation().getCareProvider().getCareProviderId());
	}
	
	@Test
	public void test_006_MultipleCareProviderInNumeratorTest(){
		ProviderAssignationService provService = new ProviderAssignationService();
		
		DiagnosticEvent diagEvent1 = createDiagEvent(1, parseDate("2015-03-01"), 1234l); //most freq
		DiagnosticEvent diagEvent2 = createDiagEvent(1, parseDate("2015-04-01"), 1234l);
		DiagnosticEvent diagEvent3 = createDiagEvent(1, parseDate("2015-06-01"), 5678l); //most recent
		RuleJustification rj1 = createRuleJustification("NUMERATOR", diagEvent1, diagEvent2, diagEvent3);
		
		DiagnosticEvent diagEvent4 = createDiagEvent(1, parseDate("2015-03-01"), 9999l); //most freq & recent but in denom
		DiagnosticEvent diagEvent5 = createDiagEvent(1, parseDate("2015-04-01"), 9999l);
		DiagnosticEvent diagEvent6 = createDiagEvent(1, parseDate("2015-06-01"), 9999l);
		RuleJustification rj2 = createRuleJustification("DENOM", diagEvent4, diagEvent5, diagEvent6);
		
		ActiveMeasure am = createActiveMeasure(rj1, rj2);
		FactLevelMeasureDenominator flmD = createFactLevelMeasureDenominator(rj1, rj2);
		
		ProviderAssignationEnum assigCode = ProviderAssignationEnum.MRPROV;
		ProviderAssignationInput input = createInput(assigCode, am, flmD);
		provService.assignProviders(input);
		assertEquals(assigCode + ": AM: Numerator Most Recent Provider should win", diagEvent3.getCareProvider().getCareProviderId(), am.getProviderAssignation().getCareProvider().getCareProviderId());
		assertEquals(assigCode + ": MRP: FLMD: Numerator Most Recent Provider should win", diagEvent3.getCareProvider().getCareProviderId(), flmD.getProviderAssignation().getCareProvider().getCareProviderId());
		
		assigCode = ProviderAssignationEnum.MFPROV;
		input.setProviderAssignation(assigCode);
		provService.assignProviders(input);
		assertEquals(assigCode + ": AM: Numerator Most Frequent Provider should win", diagEvent1.getCareProvider().getCareProviderId(), am.getProviderAssignation().getCareProvider().getCareProviderId());
		assertEquals(assigCode + ": MRP: FLMD: Numerator Most Frequent Provider should win", diagEvent1.getCareProvider().getCareProviderId(), flmD.getProviderAssignation().getCareProvider().getCareProviderId());
	}
	
	@Test
	public void test_007_MultipleCareProviderInDenomTest(){
		ProviderAssignationService provService = new ProviderAssignationService();
		
		DiagnosticEvent diagEvent1 = createDiagEvent(1, parseDate("2015-03-01"), 0l); //null prov
		DiagnosticEvent diagEvent2 = createDiagEvent(1, parseDate("2015-04-01"), 0l);
		DiagnosticEvent diagEvent3 = createDiagEvent(1, parseDate("2015-06-01"), 0l);
		RuleJustification rj1 = createRuleJustification("NUMERATOR", diagEvent1, diagEvent2, diagEvent3);
		
		DiagnosticEvent diagEvent4 = createDiagEvent(1, parseDate("2015-03-01"), 1234l); //most freq
		DiagnosticEvent diagEvent5 = createDiagEvent(1, parseDate("2015-04-01"), 1234l);
		DiagnosticEvent diagEvent6 = createDiagEvent(1, parseDate("2015-06-01"), 5678l); //most recent
		RuleJustification rj2 = createRuleJustification("DENOM", diagEvent4, diagEvent5, diagEvent6);
		
		ActiveMeasure am = createActiveMeasure(rj1, rj2);
		FactLevelMeasureDenominator flmD = createFactLevelMeasureDenominator(rj1, rj2);
		
		ProviderAssignationEnum assigCode = ProviderAssignationEnum.MRPROV;
		ProviderAssignationInput input = createInput(assigCode, am, flmD);
		provService.assignProviders(input);
		assertEquals(assigCode + ": AM: Denom Most Recent Provider should win", diagEvent6.getCareProvider().getCareProviderId(), am.getProviderAssignation().getCareProvider().getCareProviderId());
		assertEquals(assigCode + ": MRP: FLMD: Denom Most Recent Provider should win", diagEvent6.getCareProvider().getCareProviderId(), flmD.getProviderAssignation().getCareProvider().getCareProviderId());
		
		assigCode = ProviderAssignationEnum.MFPROV;
		input.setProviderAssignation(assigCode);
		provService.assignProviders(input);
		assertEquals(assigCode + ": AM: Denom Most Frequent Provider should win", diagEvent4.getCareProvider().getCareProviderId(), am.getProviderAssignation().getCareProvider().getCareProviderId());
		assertEquals(assigCode + ": MRP: FLMD: Denom Most Frequent Provider should win", diagEvent4.getCareProvider().getCareProviderId(), flmD.getProviderAssignation().getCareProvider().getCareProviderId());
	}
	
	@Test
	public void test_008_CareProviderInTieTest(){
		ProviderAssignationService provService = new ProviderAssignationService();
		
		DiagnosticEvent diagEvent1 = createDiagEvent(1, parseDate("2015-03-01"), 0l); //null prov
		DiagnosticEvent diagEvent2 = createDiagEvent(1, parseDate("2015-04-01"), 0l);
		DiagnosticEvent diagEvent3 = createDiagEvent(1, parseDate("2015-06-01"), 0l);
		RuleJustification rj1 = createRuleJustification("NUMERATOR", diagEvent1, diagEvent2, diagEvent3);
		
		DiagnosticEvent diagEvent4 = createDiagEvent(1, parseDate("2015-03-01"), 1234l); //most recent & freq
		DiagnosticEvent diagEvent5 = createDiagEvent(1, parseDate("2015-07-01"), 1234l);
		DiagnosticEvent diagEvent6 = createDiagEvent(1, parseDate("2015-06-01"), 5678l); //most recent & freq
		DiagnosticEvent diagEvent7 = createDiagEvent(1, parseDate("2015-07-01"), 5678l);
		RuleJustification rj2 = createRuleJustification("DENOM", diagEvent4, diagEvent5, diagEvent6, diagEvent7);
		
		ActiveMeasure am = createActiveMeasure(rj1, rj2);
		FactLevelMeasureDenominator flmD = createFactLevelMeasureDenominator(rj1, rj2);
		
		ProviderAssignationEnum assigCode = ProviderAssignationEnum.MRPROV;
		ProviderAssignationInput input = createInput(assigCode, am, flmD);
		provService.assignProviders(input);
		assertEquals(assigCode + ": AM: On Tie Provider with lower id should win", diagEvent4.getCareProvider().getCareProviderId(), am.getProviderAssignation().getCareProvider().getCareProviderId());
		assertEquals(assigCode + ": MRP: FLMD: On Tie Provider with lower id should win", diagEvent4.getCareProvider().getCareProviderId(), flmD.getProviderAssignation().getCareProvider().getCareProviderId());
		
		assigCode = ProviderAssignationEnum.MFPROV;
		input.setProviderAssignation(assigCode);
		provService.assignProviders(input);
		assertEquals(assigCode + ": AM: On Tie Provider with lower id should win", diagEvent4.getCareProvider().getCareProviderId(), am.getProviderAssignation().getCareProvider().getCareProviderId());
		assertEquals(assigCode + ": MRP: FLMD: On Tie Provider with lower id should win", diagEvent4.getCareProvider().getCareProviderId(), flmD.getProviderAssignation().getCareProvider().getCareProviderId());
	}
	
	protected ProviderAssignationInput createInput(ProviderAssignationEnum assigCode, ActiveMeasure mam, FactLevelMeasureDenominator flmD){
		ProviderAssignationInput input = new ProviderAssignationInput();
		input.setMeasurementStartDate(parseDate("2015-01-01"));
		input.setMeasurementEndDate(parseDate("2015-12-31"));
		input.setRunDate(parseDate("2015-09-21"));
		input.setMeasurementPeriodType(ActiveMeasurePeriodType.CALENDAR);
		input.setProviderAssignation(assigCode);
		if (mam != null){
			input.setActiveMeasures(Arrays.asList(mam));
		}
		if (flmD != null){
			input.setFactLevelMeasureDenoms(Arrays.asList(flmD));
		}
		return input;
	}
	
	protected ActiveMeasure createActiveMeasure(RuleJustification... rjs){
		MemberActiveMeasure mam = new MemberActiveMeasure(13);
		if (rjs != null) 
			for (RuleJustification rj : rjs)
				mam.addJustification(rj);
		return mam;
	}
	
	protected FactLevelMeasureDenominator createFactLevelMeasureDenominator(RuleJustification... rjs){
		DefaultFactLevelMeasureDenominator flmD = new DefaultFactLevelMeasureDenominator(125, null);
		if (rjs != null) 
			for (RuleJustification rj : rjs){
				if ("NUMERATOR".equals(rj.getRuleType())){
					FactLevelMeasureNumerator flmN = flmD.createFactLevelMeasureNumerator();
					flmN.addJustification(rj);
				}
				else{
					flmD.addJustification(rj);
				}
			}
		return flmD;
	}
	
	protected RuleJustification createRuleJustification(String ruleType, Fact... facts){
		DefaultRuleJustification rj = new DefaultRuleJustification();
		rj.setRuleType(ruleType);
		if (facts != null && facts.length>0)
			rj.setFacts(Arrays.asList(facts));
		return rj;
	}

	protected DiagnosticEvent createDiagEvent(long factId, Date eventDate, long providerId){
		DefaultDiagnosticEvent itm = new DefaultDiagnosticEvent();
		itm.addEventSourceType(EventSourceType.CLAIM);

        itm.setFactId(factId);
        itm.setStartDate(eventDate);
        itm.setCareProvider(createCareProvider(providerId));
		return itm;
	}
	
	protected CareProvider createCareProvider(long providerId){
    	if (providerId == 0l) return null;
    	DefaultCareProvider provider = new DefaultCareProvider();
        provider.setCareProviderId(providerId);
        return provider;
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

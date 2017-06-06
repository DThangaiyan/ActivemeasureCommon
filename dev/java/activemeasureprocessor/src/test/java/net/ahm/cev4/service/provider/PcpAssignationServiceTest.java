package net.ahm.cev4.service.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import net.ahm.careengine.domain.impl.member.DefaultMemberProfile;
import net.ahm.careengine.domain.impl.provider.DefaultCareProvider;
import net.ahm.careengine.domain.impl.provider.DefaultMemberProviderRelation;
import net.ahm.careengine.domain.measures.active.ActiveMeasure;
import net.ahm.careengine.domain.measures.active.impl.MemberActiveMeasure;
import net.ahm.careengine.domain.provider.MemberProviderRelation;
import net.ahm.careengine.util.ThreadLocalSimpleDateFormat;
import net.ahm.cev4.activemeasures.config.ActiveMeasurePeriodType;

public class PcpAssignationServiceTest {
	
	private static PcpAssignationService pcpAssignationService = new PcpAssignationService();
	
	private DefaultMemberProfile profile;
	private ProviderAssignationInput input;
	private MemberActiveMeasure measure;
	
	@Before
	public void setup(){
		profile = new DefaultMemberProfile();
		input = new ProviderAssignationInput();
		measure = new MemberActiveMeasure();
		List<ActiveMeasure> measures = new ArrayList<ActiveMeasure>();
		measures.add(measure);
		input.setMemberProfile(profile);
		input.setActiveMeasures(measures);
	}
	
	@Test
	public void testAssignPcp1(){
		input.setMeasurementStartDate(parseDate("2013-01-01"));
		input.setMeasurementEndDate(parseDate("2013-12-31"));
		input.setRunDate(parseDate("2015-01-01"));
		input.setMeasurementPeriodType(ActiveMeasurePeriodType.CALENDAR);
		
		List<MemberProviderRelation> history = Arrays.asList(
				createMPR(1l, false, "2000-01-01", "2013-12-01"),
				createMPR(2l, false, "2013-12-01", "2014-01-01"),
				createMPR(3l, false, "2014-01-01", null)
				);
		profile.setHistoricalPcpRelationships(history);
		
		pcpAssignationService.assignProviders(input);	
	
		assertNotNull("provider cannot be null", measure.getProviderAssignation());
		assertEquals("provider 2 is expected", 2l, measure.getProviderAssignation().getCareProvider().getCareProviderId());
	}
	
	@Test
	public void testAssignPcp1a(){
		input.setMeasurementStartDate(parseDate("2013-01-01"));
		input.setMeasurementEndDate(parseDate("2013-12-31"));
		input.setRunDate(parseDate("2015-01-01"));
		input.setMeasurementPeriodType(ActiveMeasurePeriodType.CALENDAR);
		
		List<MemberProviderRelation> history = Arrays.asList(
				createMPR(1l, false, "2000-01-01", "2013-12-01"),
				createMPR(2l, false, "2013-12-01", "2013-12-31"),
				createMPR(3l, false, "2013-12-31", null)
				);
		profile.setHistoricalPcpRelationships(history);
		
		pcpAssignationService.assignProviders(input);	
	
		assertNotNull("provider cannot be null", measure.getProviderAssignation());
		assertEquals("provider 3 is expected", 3l, measure.getProviderAssignation().getCareProvider().getCareProviderId());
	}
	
	@Test
	public void testAssignPcp2(){
		input.setMeasurementStartDate(parseDate("2012-09-22"));
		input.setMeasurementEndDate(parseDate("2013-09-21"));
		input.setRunDate(parseDate("2015-01-01"));
		input.setMeasurementPeriodType(ActiveMeasurePeriodType.CALENDAR);
		
		List<MemberProviderRelation> history = Arrays.asList(
				createMPR(1l, false, "2000-01-01", "2013-12-01"),
				createMPR(2l, false, "2013-12-01", "2014-01-01"),
				createMPR(3l, false, "2014-01-01", null)
				);
		profile.setHistoricalPcpRelationships(history);
		
		pcpAssignationService.assignProviders(input);	
	
		assertNotNull("provider cannot be null", measure.getProviderAssignation());
		assertEquals("provider 1 is expected", 1l, measure.getProviderAssignation().getCareProvider().getCareProviderId());
	}
	
	@Test
	public void testAssignPcp3(){
		input.setMeasurementStartDate(parseDate("2014-01-01"));
		input.setMeasurementEndDate(parseDate("2014-12-31"));
		input.setRunDate(parseDate("2015-01-01"));
		input.setMeasurementPeriodType(ActiveMeasurePeriodType.CALENDAR);
		
		List<MemberProviderRelation> history = Arrays.asList(
				createMPR(3l, false, "2014-01-01", null),
				createMPR(2l, false, "2013-12-01", "2014-01-01"),
				createMPR(1l, false, "2000-01-01", "2013-12-01")
				);
		profile.setHistoricalPcpRelationships(history);
		
		pcpAssignationService.assignProviders(input);	
	
		assertNotNull("provider cannot be null", measure.getProviderAssignation());
		assertEquals("provider 3 is expected", 3l, measure.getProviderAssignation().getCareProvider().getCareProviderId());
	}
	
	@Test
	public void testAssignPcp4(){
		input.setMeasurementStartDate(parseDate("2013-01-01"));
		input.setMeasurementEndDate(parseDate("2013-12-31"));
		input.setRunDate(parseDate("2015-01-01"));
		input.setMeasurementPeriodType(ActiveMeasurePeriodType.CALENDAR);
		
		List<MemberProviderRelation> history = Arrays.asList(
				createMPR(1l, false, "2000-01-01", "2013-12-01"),
				createMPR(2l, true, "2013-12-01", "2014-01-01"),
				createMPR(3l, false, "2014-01-01", null)
				);
		profile.setHistoricalPcpRelationships(history);
		
		pcpAssignationService.assignProviders(input);	
	
		assertNotNull("provider cannot be null even when opted out for historical runs", measure.getProviderAssignation());
		assertEquals("provider 2 is expected", 2l, measure.getProviderAssignation().getCareProvider().getCareProviderId());
	}
	
	@Test
	public void testAssignPcp5(){
		input.setMeasurementStartDate(parseDate("2014-01-01"));
		input.setMeasurementEndDate(parseDate("2014-12-31"));
		input.setRunDate(parseDate("2015-01-01"));
		input.setMeasurementPeriodType(ActiveMeasurePeriodType.CALENDAR);
		
		List<MemberProviderRelation> history = Arrays.asList(
				createMPR(1l, false, "2000-01-01", "2013-12-01"),
				createMPR(2l, false, "2013-12-01", "2014-01-01"),
				createMPR(3l, false, "2014-01-01", "2014-07-01")
				);
		profile.setHistoricalPcpRelationships(history);
		
		pcpAssignationService.assignProviders(input);	
	
		assertNull("no matching provider. Hence assigmnet should be null", measure.getProviderAssignation());
	}
	
	@Test
	public void testAssignPcp6(){
		input.setMeasurementStartDate(parseDate("2013-01-01"));
		input.setMeasurementEndDate(parseDate("2013-12-31"));
		input.setRunDate(parseDate("2013-11-01"));
		input.setMeasurementPeriodType(ActiveMeasurePeriodType.CALENDAR);
		
		List<MemberProviderRelation> current = Arrays.asList(
				createMPR(1l, false, null, null)
		);
		profile.setCurrentPcpRelationships(current);
		
		pcpAssignationService.assignProviders(input);	
	
		assertNotNull("provider cannot be null", measure.getProviderAssignation());
		assertEquals("provider 1 is expected", 1l, measure.getProviderAssignation().getCareProvider().getCareProviderId());
	}
	
	@Test
	public void testAssignPcp7(){
		input.setMeasurementStartDate(parseDate("2012-09-22"));
		input.setMeasurementEndDate(parseDate("2013-09-21"));
		input.setRunDate(parseDate("2015-01-01"));
		input.setMeasurementPeriodType(ActiveMeasurePeriodType.CALENDAR);
		
		List<MemberProviderRelation> current = Arrays.asList(
				createMPR(1l, false, null, null)
		);
		profile.setCurrentPcpRelationships(current);
		
		pcpAssignationService.assignProviders(input);	
	
		assertNull("provider should be null", measure.getProviderAssignation());
	}
	
	@Test
	public void testAssignPcp8(){
		input.setMeasurementStartDate(parseDate("2013-01-01"));
		input.setMeasurementEndDate(parseDate("2013-12-31"));
		input.setRunDate(parseDate("2013-11-01"));
		input.setMeasurementPeriodType(ActiveMeasurePeriodType.CALENDAR);
		
		List<MemberProviderRelation> current = Arrays.asList(
				createMPR(1l, true, null, null)
		);
		profile.setCurrentPcpRelationships(current);
		
		pcpAssignationService.assignProviders(input);	
	
		assertNull("filtered provider cannot be assigned for current runs", measure.getProviderAssignation());
	}
	
	@Test
	public void testAssignPcp9(){
		input.setMeasurementStartDate(parseDate("2012-09-22"));
		input.setMeasurementEndDate(parseDate("2013-09-21"));
		input.setRunDate(parseDate("2013-09-21"));
		input.setMeasurementPeriodType(ActiveMeasurePeriodType.ROLLING);
		
		List<MemberProviderRelation> current = Arrays.asList(
				createMPR(1l, false, null, null)
		);
		profile.setCurrentPcpRelationships(current);
		
		pcpAssignationService.assignProviders(input);	
	
		assertNotNull("provider cannot be null", measure.getProviderAssignation());
		assertEquals("provider 1 is expected", 1l, measure.getProviderAssignation().getCareProvider().getCareProviderId());
	}
	
	@Test
	public void testAssignPcp10(){
		input.setMeasurementStartDate(parseDate("2012-09-01"));
		input.setMeasurementEndDate(parseDate("2013-08-31"));
		input.setRunDate(parseDate("2013-09-21"));
		input.setMeasurementPeriodType(ActiveMeasurePeriodType.ROLLING_MONTHLY);
		
		List<MemberProviderRelation> current = Arrays.asList(
				createMPR(1l, false, null, null)
		);
		profile.setCurrentPcpRelationships(current);
		
		pcpAssignationService.assignProviders(input);	
	
		assertNotNull("provider cannot be null", measure.getProviderAssignation());
		assertEquals("provider 1 is expected", 1l, measure.getProviderAssignation().getCareProvider().getCareProviderId());
	}
	
	private MemberProviderRelation createMPR(long careProvId, boolean filtered, String startDate, String endDate){
		DefaultMemberProviderRelation mpr = new DefaultMemberProviderRelation();
		DefaultCareProvider careProv = new DefaultCareProvider();
		careProv.setCareProviderId(careProvId);
		careProv.setFiltered(filtered);
		
		mpr.setCareProvider(careProv);
		mpr.setStartDate(parseDate(startDate));
		mpr.setEndDate(parseDate(endDate));
		
		return mpr;
	}
	
	
	private Date parseDate(String date){
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

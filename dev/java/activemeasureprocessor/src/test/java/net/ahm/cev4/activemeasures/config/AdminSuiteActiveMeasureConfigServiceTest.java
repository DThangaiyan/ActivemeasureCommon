package net.ahm.cev4.activemeasures.config;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.ahm.careengine.dvo.ProductType;
import net.ahm.careengine.util.ThreadLocalSimpleDateFormat;
import net.ahm.cev4.activemeasures.config.adminsuite.ActiveMeasureAccountPackage;
import net.ahm.cev4.activemeasures.config.adminsuite.ActiveMeasureMeasurementPeriod;
import net.ahm.cev4.activemeasures.dao.config.adminsuite.AdminSuiteActiveMeasureConfigDAO;
import net.ahm.cev4.util.CalendarTimeOffset;

public class AdminSuiteActiveMeasureConfigServiceTest {
	private static ThreadLocalSimpleDateFormat sdf = ThreadLocalSimpleDateFormat.yyyy_MM_dd;
	
	private AdminSuiteActiveMeasureConfigService configService;
	
	private ActiveMeasureMeasurementPeriod periodJAN_DEC;
	private ActiveMeasureMeasurementPeriod periodJUL_JUN;
	private ActiveMeasureMeasurementPeriod periodROLL;
	private ActiveMeasureMeasurementPeriod periodROLLQTR;
	private ActiveMeasureMeasurementPeriod periodROLLMTH;
	
	private List<ActiveMeasureAccountPackage> amAcctPkgs = new ArrayList<ActiveMeasureAccountPackage>();
	
	@Before
	public void setup(){
		amAcctPkgs.clear();
		configService = new AdminSuiteActiveMeasureConfigService();
		configService.setActiveMeasureConfigDao(new MockActiveMeasureConfigDAO());
		periodJAN_DEC = new ActiveMeasureMeasurementPeriod(dt("2000-01-01"), dt("2000-12-31"), 1L, new CalendarTimeOffset(90, Calendar.DATE), dt("2000-01-01"), null, ActiveMeasurePeriodType.CALENDAR); 
		periodJUL_JUN = new ActiveMeasureMeasurementPeriod(dt("2000-07-01"), dt("2001-06-30"), 2L, new CalendarTimeOffset(90, Calendar.DATE), dt("2000-07-01"), null, ActiveMeasurePeriodType.CALENDAR);
		periodROLL = new ActiveMeasureMeasurementPeriod(null, null, 3L, null, dt("2000-01-01"), null, ActiveMeasurePeriodType.ROLLING);
		periodROLLQTR = new ActiveMeasureMeasurementPeriod(null, null, 4L, null, dt("2000-01-01"), null, ActiveMeasurePeriodType.ROLLING_QTRLY);
		periodROLLMTH = new ActiveMeasureMeasurementPeriod(null, null, 5L, null, dt("2000-01-01"), null, ActiveMeasurePeriodType.ROLLING_MONTHLY);
	}
	
	@Test
	public void test1(){
		ActiveMeasureAccountPackage pkg = new ActiveMeasureAccountPackage();
		pkg.setAccountPackageId(1);
		pkg.setProduct(ProductType.AQM);
		pkg.addMeasurePeriod(periodJAN_DEC);
		
		pkg.addMeasure(1, dt("2014-01-01"), dt("2014-12-31"));
		pkg.addMeasure(2, dt("2015-01-01"), dt("2015-12-31"));
		
		amAcctPkgs.add(pkg);
		
		List<ActiveMeasureRunSetting> amRunSettings = configService.getActiveMeasureRunSettings(1, dt("2015-01-16"), "BT", Collections.singleton(ProductType.AQM));
		Assert.assertEquals("Jan-Dec(2014), Jan-Dec(2015)", 2, amRunSettings.size());
	}
	
	@Test
	public void test2(){
		ActiveMeasureAccountPackage pkg = new ActiveMeasureAccountPackage();
		pkg.setAccountPackageId(1);
		pkg.setProduct(ProductType.AQM);
		pkg.addMeasurePeriod(periodJAN_DEC);
		
		pkg.addMeasure(1, dt("2014-01-01"), dt("2014-12-31"));
		pkg.addMeasure(2, dt("2015-01-01"), dt("2015-12-31"));
		
		amAcctPkgs.add(pkg);
		
		List<ActiveMeasureRunSetting> amRunSettings = configService.getActiveMeasureRunSettings(1, dt("2015-04-16"), "BT", Collections.singleton(ProductType.AQM));
		Assert.assertEquals("Jan-Dec(2015)", 1, amRunSettings.size());
	}
	
	@Test
	public void test3(){
		ActiveMeasureAccountPackage pkg = new ActiveMeasureAccountPackage();
		pkg.setAccountPackageId(1);
		pkg.setProduct(ProductType.AQM);
		pkg.addMeasurePeriod(periodJUL_JUN);
		
		pkg.addMeasure(1, dt("2014-01-01"), dt("2014-12-31"));
		pkg.addMeasure(2, dt("2015-01-01"), dt("2015-12-31"));
		
		amAcctPkgs.add(pkg);
		
		List<ActiveMeasureRunSetting> amRunSettings = configService.getActiveMeasureRunSettings(1, dt("2015-01-16"), "BT", Collections.singleton(ProductType.AQM));
		Assert.assertEquals("Jul-Jun(2014-2015)", 1, amRunSettings.size());
		Assert.assertEquals("Jul-Jun(2014-2015): StartDate=2014-07-01", "2014-07-01", fmt(amRunSettings.get(0).getMeasurementStartDate()));
		Assert.assertEquals("Jul-Jun(2014-2015): EndDate=2015-06-30", "2015-06-30", fmt(amRunSettings.get(0).getMeasurementEndDate()));
		Assert.assertEquals("Only 2014 version", 1, amRunSettings.get(0).getActiveMeasurePackage().size());
		Assert.assertTrue("Only 2014 version", amRunSettings.get(0).getActiveMeasurePackage().contains(1l));
	}
	
	@Test
	public void test4(){
		ActiveMeasureAccountPackage pkg = new ActiveMeasureAccountPackage();
		pkg.setAccountPackageId(1);
		pkg.setProduct(ProductType.AQM);
		pkg.addMeasurePeriod(periodROLL);
		
		pkg.addMeasure(1, dt("2014-01-01"), dt("2014-12-31"));
		pkg.addMeasure(2, dt("2015-01-01"), dt("2015-12-31"));
		
		amAcctPkgs.add(pkg);
		
		pkg = new ActiveMeasureAccountPackage();
		pkg.setAccountPackageId(2);
		pkg.setProduct(ProductType.AQM);
		pkg.addMeasurePeriod(periodROLL);
		
		pkg.addMeasure(3, dt("2015-01-01"), dt("2015-12-31"));
		
		amAcctPkgs.add(pkg);
		
		List<ActiveMeasureRunSetting> amRunSettings = configService.getActiveMeasureRunSettings(1, dt("2015-01-16"), "BT", Collections.singleton(ProductType.AQM));
		Assert.assertEquals("Rolling(2015-01-16)", 1, amRunSettings.size());
		Assert.assertEquals("Rolling(2015-01-16): StartDate=2014-01-17", "2014-01-17", fmt(amRunSettings.get(0).getMeasurementStartDate()));
		Assert.assertEquals("Rolling(2015-01-16): EndDate=2015-01-16", "2015-01-16", fmt(amRunSettings.get(0).getMeasurementEndDate()));
		Assert.assertEquals("Only 2015 measures", 2, amRunSettings.get(0).getActiveMeasurePackage().size());
		Assert.assertFalse("Only 2015 measures: -1", amRunSettings.get(0).getActiveMeasurePackage().contains(1L));
		Assert.assertTrue("Only 2015 measures: +2", amRunSettings.get(0).getActiveMeasurePackage().contains(2L));
		Assert.assertTrue("Only 2015 measures: +3", amRunSettings.get(0).getActiveMeasurePackage().contains(3L));

	}
	
	@Test
	public void test5_ROLLING_QTR(){
		ActiveMeasureAccountPackage pkg = new ActiveMeasureAccountPackage();
		pkg.setAccountPackageId(1);
		pkg.setProduct(ProductType.AQM);
		pkg.addMeasurePeriod(periodROLLQTR);
		
		pkg.addMeasure(1, dt("2015-01-01"), dt("2015-12-31"));
		pkg.addMeasure(2, dt("2016-01-01"), dt("2016-12-31"));
		
		amAcctPkgs.add(pkg);
		Collection<ProductType> products = Collections.singleton(ProductType.AQM);
		
		String[] runDate 			= {"2016-01-21", "2016-02-21", "2016-03-21", "2016-04-21", "2016-05-21", "2016-06-21", "2016-07-21", "2016-08-21", "2016-09-21", "2016-10-21", "2016-11-21", "2016-12-21"}; 
		String[] expectedQtrEndDate = {"2015-09-30", "2015-12-31", "2015-12-31", "2015-12-31", "2016-03-31", "2016-03-31", "2016-03-31", "2016-06-30", "2016-06-30", "2016-06-30", "2016-09-30", "2016-09-30"};
		Long[] expectedMeasureId = {1L, 1L, 1L, 1L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L};
		
		for (int i=0; i<runDate.length; i++){
			List<ActiveMeasureRunSetting> amRunSettings = configService.getActiveMeasureRunSettings(1, dt(runDate[i]), "BT", products);
			Assert.assertEquals("ROLL_QTR: runDate=" + runDate[i] + " should evaluate QTR ending " + expectedQtrEndDate[i], 1, amRunSettings.size());
			Assert.assertEquals("ROLL_QTR: runDate=" + runDate[i] + " should evaluate QTR ending " + expectedQtrEndDate[i], expectedQtrEndDate[i], fmt(amRunSettings.get(0).getMeasurementEndDate()));
			Assert.assertEquals("ROLL_QTR ending " + expectedQtrEndDate + " should contain only 1 measure", 1, amRunSettings.get(0).getActiveMeasurePackage().size());
			Assert.assertTrue("ROLL_QTR ending " + expectedQtrEndDate + " should pick measureid " + expectedMeasureId[i], amRunSettings.get(0).getActiveMeasurePackage().contains(expectedMeasureId[i]));
		}
	}
	
	@Test
	public void test6_ROLLING_MTH(){
		ActiveMeasureAccountPackage pkg = new ActiveMeasureAccountPackage();
		pkg.setAccountPackageId(1);
		pkg.setProduct(ProductType.AQM);
		pkg.addMeasurePeriod(periodROLLMTH);
		
		pkg.addMeasure(1, dt("2015-01-01"), dt("2015-12-31"));
		pkg.addMeasure(2, dt("2016-01-01"), dt("2016-12-31"));
		
		amAcctPkgs.add(pkg);
		Collection<ProductType> products = Collections.singleton(ProductType.AQM);
		
		String[] runDate 			= {"2016-01-21", "2016-02-21", "2016-03-21", "2016-04-21", "2016-05-21", "2016-06-21", "2016-07-21", "2016-08-21", "2016-09-21", "2016-10-21", "2016-11-21", "2016-12-21"}; 
		String[] expectedMthEndDate = {"2015-12-31", "2016-01-31", "2016-02-29", "2016-03-31", "2016-04-30", "2016-05-31", "2016-06-30", "2016-07-31", "2016-08-31", "2016-09-30", "2016-10-31", "2016-11-30"};
		Long[] expectedMeasureId = {1L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L};
		
		for (int i=0; i<runDate.length; i++){
			List<ActiveMeasureRunSetting> amRunSettings = configService.getActiveMeasureRunSettings(1, dt(runDate[i]), "BT", products);
			Assert.assertEquals("ROLL_MTH: runDate=" + runDate[i] + " should evaluate MONTH ending " + expectedMthEndDate[i], 1, amRunSettings.size());
			Assert.assertEquals("ROLL_MTH: runDate=" + runDate[i] + " should evaluate MONTH ending " + expectedMthEndDate[i], expectedMthEndDate[i], fmt(amRunSettings.get(0).getMeasurementEndDate()));
			Assert.assertEquals("ROLL_MTH ending " + expectedMthEndDate + " should contain only 1 measure", 1, amRunSettings.get(0).getActiveMeasurePackage().size());
			Assert.assertTrue("ROLL_MTH ending " + expectedMthEndDate + " should pick measureid " + expectedMeasureId[i], amRunSettings.get(0).getActiveMeasurePackage().contains(expectedMeasureId[i]));
		}
	}
	
	@Test
	public void test7(){
		ActiveMeasureAccountPackage pkg = new ActiveMeasureAccountPackage();
		pkg.setAccountPackageId(1);
		pkg.setProduct(ProductType.AQM);
		pkg.addMeasurePeriod(periodJAN_DEC);
		pkg.addMeasurePeriod(periodROLL);
		pkg.addMeasurePeriod(periodROLLQTR);
		pkg.addMeasurePeriod(periodROLLMTH);
		
		pkg.addMeasure(1, dt("2014-01-01"), dt("2014-12-31"));
		pkg.addMeasure(2, dt("2015-01-01"), dt("2015-12-31"));
		pkg.addMeasure(3, dt("2015-01-01"), dt("2015-12-31"));
		pkg.addMeasure(4, dt("2015-01-01"), dt("2015-12-31"));
		pkg.addMeasure(5, dt("2016-01-01"), dt("2016-12-31"));
		
		amAcctPkgs.add(pkg);
		
		List<ActiveMeasureRunSetting> amRunSettings = configService.getActiveMeasureRunSettings(1, dt("2015-10-05"), "BT", Collections.singleton(ProductType.AQM));
		Assert.assertEquals("Total", 4, amRunSettings.size());
		Assert.assertEquals("3 measures", 3, amRunSettings.get(0).getActiveMeasurePackage().size());
		Assert.assertEquals("3 measures", 3, amRunSettings.get(1).getActiveMeasurePackage().size());
		Assert.assertEquals("3 measures", 3, amRunSettings.get(2).getActiveMeasurePackage().size());
		Assert.assertEquals("3 measures", 3, amRunSettings.get(3).getActiveMeasurePackage().size());

	}
	
	private Date dt(String yyyy_mm_dd){
		try {
			return sdf.get().parse(yyyy_mm_dd);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String fmt(Date yyyy_mm_dd){
		return sdf.get().format(yyyy_mm_dd);
	}
	
	private class MockActiveMeasureConfigDAO implements AdminSuiteActiveMeasureConfigDAO{

		@Override
		public Collection<ActiveMeasureAccountPackage> loadActiveMeasureAccountPackages(int bussSupplierId, Collection<ProductType> products) {
			return amAcctPkgs;
		}

		@Override
		public Map<Integer, Collection<ActiveMeasureAccountPackage>> loadActiveMeasureAccountPackages(Collection<Integer> bussSupplierIds, Collection<ProductType> products) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}

package net.ahm.activemeasure.templates;

import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureNumerator;

public class ActiveMeasureUtilConstants {
	public static final String FACT_LEVEL_DENOMINATOR = "$factLevelDenominator";

	/**
	 * represent the
	 * {@link FactLevelMeasureDenominator#getPrimaryOriginationFact()}
	 */
	public static final String FACT_LEVEL_DENOMINATOR_PRIMARY_FACT_ATTRIBUTE_NAME = "primaryOriginationFact";

	/**
	 * represent the {@link FactLevelMeasureNumerator#getPrimaryFact()}
	 */
	public static final String FACT_LEVEL_NUMERATOR_PRIMARY_FACT_ATTRIBUTE_NAME = "primaryFact";

	public static final String FACT_LEVEL_NUMERATOR = "$factLevelNumerator";

	public static final String MEASURE_ID = "measureId";
}

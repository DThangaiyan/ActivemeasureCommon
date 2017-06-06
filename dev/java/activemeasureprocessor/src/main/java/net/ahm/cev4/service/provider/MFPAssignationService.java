package net.ahm.cev4.service.provider;

import java.util.Comparator;

import net.ahm.careengine.provider.ProviderAssignationEnum;
import net.ahm.cev4.service.provider.AbstractJustificationBasedAssignationService.ProviderStats;

public class MFPAssignationService extends AbstractJustificationBasedAssignationService implements Comparator<ProviderStats> {

	@Override
	public ProviderAssignationEnum getProviderAssignationCode() {
		return ProviderAssignationEnum.MFPROV;
	}

	@Override
	protected Comparator<? super ProviderStats> getComparator() {
		return this;
	}
	
	@Override
	public int compare(ProviderStats o1, ProviderStats o2) {
		//order by count desc, recentDate desc, careProviderId asc (nulls last)
		if (o2 == null) return -1;
		if (o1 == null) return 1;
		int cmp = o2.getCount()-o1.getCount();
		if (cmp != 0) return cmp;
		cmp = o2.getMaxDate().compareTo(o1.getMaxDate());
		if (cmp != 0) return cmp;
		if (o1.getCareProvider().getCareProviderId() < o2.getCareProvider().getCareProviderId())
			return -1;
		return 1;
	}

}

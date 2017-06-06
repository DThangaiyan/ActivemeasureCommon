package net.ahm.cev4.service.provider;

import org.apache.log4j.Logger;

public class ProviderAssignationService {
	private static final Logger log = Logger.getLogger(ProviderAssignationService.class);
	
	private PcpAssignationService pcpAssignationService = new PcpAssignationService();
	private MRPAssignationService mrpAssignationService = new MRPAssignationService();
	private MFPAssignationService mfpAssignationService = new MFPAssignationService();

	public void assignProviders(ProviderAssignationInput input){
		if (input.getProviderAssignation() == null){
			log.warn("assignProviders: Null ProviderAssignationCode");
			return;
		}
		switch(input.getProviderAssignation()){
		case PCP:
			pcpAssignationService.assignProviders(input);
			return;
		case MRPROV:
			mrpAssignationService.assignProviders(input);
			return;
		case MFPROV:
			mfpAssignationService.assignProviders(input);
			return;
		case PRV_ELEMENTS:
		case SPECIALTY:
		default:
				log.warn("assignProviders: Unsupported ProviderAssignationCode=" + input.getProviderAssignation());
		}
	}

	public PcpAssignationService getPcpAssignationService() {
		return pcpAssignationService;
	}

	public void setPcpAssignationService(PcpAssignationService pcpAssignationService) {
		this.pcpAssignationService = pcpAssignationService;
	}

	public MRPAssignationService getMrpAssignationService() {
		return mrpAssignationService;
	}

	public void setMrpAssignationService(MRPAssignationService mrpAssignationService) {
		this.mrpAssignationService = mrpAssignationService;
	}

	public MFPAssignationService getMfpAssignationService() {
		return mfpAssignationService;
	}

	public void setMfpAssignationService(MFPAssignationService mfpAssignationService) {
		this.mfpAssignationService = mfpAssignationService;
	}
}

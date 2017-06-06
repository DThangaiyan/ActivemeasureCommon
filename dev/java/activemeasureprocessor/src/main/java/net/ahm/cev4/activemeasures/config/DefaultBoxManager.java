package net.ahm.cev4.activemeasures.config;

import net.ahm.careengine.domain.member.MemberProfile;

public class DefaultBoxManager implements BoxManager {

	@Override
	public MemberProfile applyBox(MemberProfile inputProfile, BoxParameters box) {
		return inputProfile;
	}

}

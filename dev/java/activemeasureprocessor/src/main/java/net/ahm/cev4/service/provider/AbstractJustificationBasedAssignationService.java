package net.ahm.cev4.service.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ahm.careengine.activemeasure.justification.JustifiedClassifier;
import net.ahm.careengine.domain.event.Event;
import net.ahm.careengine.domain.fact.Fact;
import net.ahm.careengine.domain.fact.InferredFact;
import net.ahm.careengine.domain.impl.provider.DefaultProviderAssignation;
import net.ahm.careengine.domain.justification.RuleJustification;
import net.ahm.careengine.domain.measures.active.ActiveMeasure;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureDenominator;
import net.ahm.careengine.domain.measures.active.FactLevelMeasureNumerator;
import net.ahm.careengine.domain.provider.CareProvider;
import net.ahm.careengine.provider.ProviderAssignationEnum;
import net.ahm.careengine.util.OverlappedEvents;

/**
 * Abstract Justification Based Provider Assignation
 * @author mkitawat
 *
 */
public abstract class AbstractJustificationBasedAssignationService {
	static final String RJ_NUMERATOR = "NUMERATOR";
	static final String RJ_DENOM = "DENOM";

	public void assignProviders(ProviderAssignationInput input){
		for (ActiveMeasure measure : input.getActiveMeasures()){
			DefaultProviderAssignation providerAssignation = assignProvider(measure.getJustifications());
			if (providerAssignation != null) {
				measure.setProviderAssignation(providerAssignation);
			}
		}
		for (FactLevelMeasureDenominator flmDenom : input.getFactLevelMeasureDenoms()){
			List<RuleJustification> justifications = new ArrayList<RuleJustification>(
					flmDenom.getJustifications());
			for (FactLevelMeasureNumerator careNum : flmDenom.getNumerators()){
				justifications.addAll(careNum.getJustifications());
			}
			DefaultProviderAssignation providerAssignation = assignProvider(justifications);
			if (providerAssignation != null) {
				flmDenom.setProviderAssignation(providerAssignation);
			}
		}
	}
	
	public DefaultProviderAssignation assignProvider(Collection<RuleJustification> rjs){
		if (rjs == null || rjs.isEmpty()) return null;
		ProviderStats provstat = null;
		Map<String, Collection<RuleJustification>> rjsByRuleTyp = groupByRuleType(rjs);
		rjs = rjsByRuleTyp.get(RJ_NUMERATOR);
		provstat = findProvider(rjs);
		if (provstat == null){
			rjs = rjsByRuleTyp.get(RJ_DENOM);
			provstat = findProvider(rjs);
		}
		DefaultProviderAssignation providerAssignation = null;
		if (provstat != null){
			CareProvider cp = provstat.getCareProvider();
			providerAssignation = new DefaultProviderAssignation();
			providerAssignation.setPcp(false);
			providerAssignation.setCurrent(!cp.isFiltered());
			providerAssignation.setCareProvider(cp);
			providerAssignation.setProviderAssignationCode(getProviderAssignationCode().getCode());
		}
		return providerAssignation;
	}
	
	protected ProviderStats findProvider(Collection<RuleJustification> rjs){
		List<ProviderStats> provs = extractProviderStats(rjs);
		if (provs.isEmpty()) return null;
		if (provs.size() == 1) return provs.get(0);
		return Collections.min(provs, getComparator());
	}
	
	public abstract ProviderAssignationEnum getProviderAssignationCode();
	
	protected abstract Comparator<? super ProviderStats> getComparator();

	protected List<ProviderStats> extractProviderStats(Collection<RuleJustification> rjs){
		if (rjs == null) return Collections.emptyList();
		Map<Long, ProviderStats> stats = new HashMap<Long, ProviderStats>();
		extractProviderStats(rjs, stats);
		List<ProviderStats> provs = new ArrayList<ProviderStats>(stats.size());
		provs.addAll(stats.values());
		return provs;
	}
	
	protected void extractProviderStats(Collection<RuleJustification> rjs, Map<Long, ProviderStats> stats){
		if (rjs == null) return;
		for (RuleJustification rj : rjs){
			Collection<Fact> facts = rj.getFacts();
			if (facts == null || facts.isEmpty()) continue;
			for (Fact fact : facts){
				extractProviderStats(fact, stats);
			}
		}
	}
	
	protected void extractProviderStats(Fact fact, Map<Long, ProviderStats> stats){
		if (fact instanceof Event){
			extractProviderStats((Event) fact, stats);
		}
		else if (fact instanceof InferredFact){
			extractProviderStats((InferredFact) fact, stats);
		}
	}
	
	protected void extractProviderStats(Event event, Map<Long, ProviderStats> stats){
		CareProvider cp = event.getCareProvider();
		if (cp == null || cp.getCareProviderId() == 0l) return;
		//if (cp.isFiltered()) return;
		ProviderStats stat = stats.get(cp.getCareProviderId());
		if (stat == null){
			stat = new ProviderStats(cp);
			stats.put(cp.getCareProviderId(), stat);
		}
		stat.updateStats(event);
	}
	
	protected void extractProviderStats(InferredFact iFact, Map<Long, ProviderStats> stats){
		if (iFact instanceof OverlappedEvents){
			OverlappedEvents ovrlap = (OverlappedEvents)iFact;
			if (ovrlap.getPrimaryEvent() != null)
				extractProviderStats(ovrlap.getPrimaryEvent(), stats);
			if (ovrlap.getSecondaryEvent() != null)
				extractProviderStats(ovrlap.getSecondaryEvent(), stats);
		}
		else if (iFact instanceof JustifiedClassifier){
			extractProviderStats(((JustifiedClassifier)iFact).getJustifications(), stats);
		}
	}
	
	protected Map<String, Collection<RuleJustification>> groupByRuleType(Collection<RuleJustification> rjs){
		Map<String, Collection<RuleJustification>> grouper = new HashMap<String, Collection<RuleJustification>>();
		for (RuleJustification rj : rjs){
			Collection<RuleJustification> accm = grouper.get(rj.getRuleType());
			if (accm == null){
				accm = new ArrayList<RuleJustification>();
				grouper.put(rj.getRuleType(), accm);
			}
			accm.add(rj);
		}
		return grouper;
	}
	
	protected static class ProviderStats{
		private CareProvider careProvider;
		private Date maxDate = new Date(0);
		private int count;
		
		ProviderStats(CareProvider careProvider){
			this.setCareProvider(careProvider);
		}
		
		public void updateStats(Event event){
			Date eventStartDt = event.getStartDate();
			if (eventStartDt != null && eventStartDt.after(maxDate)){
				maxDate = eventStartDt;
			}
			count++;
		}

		public CareProvider getCareProvider() {
			return careProvider;
		}

		public void setCareProvider(CareProvider careProvider) {
			this.careProvider = careProvider;
		}

		public Date getMaxDate() {
			return maxDate;
		}

		public void setMaxDate(Date maxDate) {
			this.maxDate = maxDate;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}
	}
	
}

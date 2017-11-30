package edu.ls3.magus.web.composer.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import edu.ls3.magus.cl.fmconfigurator.ContextStateModel;
import edu.ls3.magus.cl.fmconfigurator.DomainModels;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.FlowComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.NonfunctionalMetricType;
import edu.ls3.magus.configuration.Configuration;
import edu.ls3.magus.eval.generators.owls.UtilityClass;
import edu.ls3.magus.web.composer.services.RequestContextStateModel;

public class MashupContextStateModelUpdateProcess extends Process {
	final private String mashupRunningInstanceUri;
	final private RequestContextStateModel contextStateModel;

	public MashupContextStateModelUpdateProcess(String mashupRunningInstanceUri,
			RequestContextStateModel contextStateModel) {
		this.mashupRunningInstanceUri = mashupRunningInstanceUri;
		this.contextStateModel = contextStateModel;
	}

	public class RequirementStatus {
		public boolean isFunctionalSatisfied;
		public boolean adaptationRecommended;
		public Map<String, Boolean> nfSatisfaction;
	}

	public RequirementStatus updateContextStateModel() throws Exception {
		if (!mashupRunningInstanceUri.startsWith(Configuration.domainAddress)) {
			throw new IllegalArgumentException("Only mashup families on magus online site is supported.");
		}

		String systemAddress = mashupRunningInstanceUri.replace(Configuration.domainAddress,
				Configuration.defaultDeploymentDirectory);

		if (!systemAddress.endsWith("/")) {
			systemAddress = systemAddress + "/";
		}

		final String requestInfoAddress = systemAddress + "requestInformation.xml";

		RequestInformation requestInfo = readRequestInfo(requestInfoAddress);

		GeneratedMashupInfo mashupInfo = readGeneratedMashupInfo(requestInfo.mashupInstanceUri);

		final DomainModels domainModels;
		try {
			domainModels = DomainModels.readFromConfigurationFile(mashupInfo.mashupFamilyURI);
		} catch (Exception ex) {
			throw new IOException("Reading configuration file failed with the following message: " + ex.getMessage(),
					ex);
		}

		ContextStateModel cms = covertContextStateModel(contextStateModel, domainModels);

		UtilityClass.writeFile(new File(systemAddress + "/contextStateModel.xml"), cms.serializeToXml());

		GeneratedMashup mashup = generateMashup(domainModels, mashupInfo.selectedFeaturesUuids.toArray(new String[0]));
		FlowComponentNode fcn = mashup.fcn;

		Map<String, Boolean> nfSatisfaction = new HashMap<>();
		boolean adaptationRecommended = false;

		for (String nfmtString : requestInfo.constraints.keySet()) {
			Optional<NonfunctionalMetricType> metricTypeOpt = NonfunctionalMetricType.getMetricByName(nfmtString);
			if (metricTypeOpt.isPresent()) {
				final NonfunctionalMetricType metricType = metricTypeOpt.get();
				double currentValue = metricType.getAggregatedValue(cms.getServiceNonfunctionalMap().getAnnotationMap(),
						fcn);
				final boolean nfSatisfied = currentValue <= requestInfo.constraints.get(nfmtString);
				adaptationRecommended = nfSatisfied || !nfSatisfied;
				nfSatisfaction.put(nfmtString, nfSatisfied);
			}
		}

		boolean isFunctionalSatisfied = true;

		for (String serviceUri : mashup.usedServiceURIs) {
			isFunctionalSatisfied = isFunctionalSatisfied && cms.getServiceAvailabilty().get(serviceUri);
		}

		adaptationRecommended = adaptationRecommended || !isFunctionalSatisfied;

		RequirementStatus rs = new RequirementStatus();

		rs.isFunctionalSatisfied = isFunctionalSatisfied;
		rs.nfSatisfaction = nfSatisfaction;
		rs.adaptationRecommended = adaptationRecommended;

		return rs;

	}

}

package edu.ls3.magus.web.composer.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import edu.ls3.magus.cl.fmconfigurator.ContextStateModel;
import edu.ls3.magus.cl.fmconfigurator.DomainModels;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.BpelNode;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.FlowComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.NonfunctionalMetricType;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.configuration.Configuration;
import edu.ls3.magus.eval.generators.owls.UtilityClass;
import edu.ls3.magus.web.composer.services.NonfunctionalProperty;
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
		public Map<String, Double> nfValue;
	}

	public RequirementStatus updateContextStateModel() throws Exception {
		if (!mashupRunningInstanceUri.startsWith(Configuration.domainAddress)) {
			throw new IllegalArgumentException("Only mashup families on magus online site is supported.");
		}

		String systemAddress = mashupRunningInstanceUri.replace(Configuration.domainAddress,
				Configuration.deploymentDirectory);

		if (!systemAddress.endsWith("/")) {
			systemAddress = systemAddress + "/";
		}

		final String requestInfoAddress = systemAddress + "requestInformation.xml";

		RequestInformation requestInfo = readRequestInfo(requestInfoAddress);

		GeneratedMashupInfo mashupInfo = readGeneratedMashupInfo(requestInfo.mashupInstanceUri);

		final String configurationFileAddress = findSystemAddress(mashupInfo.mashupFamilyURI);

		final DomainModels domainModels;
		try {
			domainModels = DomainModels.readFromConfigurationFile(configurationFileAddress);
		} catch (Exception ex) {
			throw new IOException("Reading configuration file failed with the following message: " + ex.getMessage(),
					ex);
		}

		ContextStateModel cms = covertContextStateModel(contextStateModel, domainModels);

		UtilityClass.writeFile(new File(systemAddress + "/contextStateModel.xml"), cms.serializeToXml());

		String bpelXml = UtilityClass.readFile(new File(systemAddress + "/bpel.xml"), Charset.defaultCharset());
		FlowComponentNode fcn = BpelNode.readFromBpelXml(bpelXml, domainModels.getServiceCollection());

		Map<String, Boolean> nfSatisfaction = new HashMap<>();
		Map<String, Double> nfValue = new HashMap<>();
		boolean adaptationRecommended = false;

		boolean isFunctionalSatisfied = true;

		for (Service service : fcn.getAllInvokedServices()) {
			isFunctionalSatisfied = isFunctionalSatisfied && cms.getServiceAvailabilty().get(service.getURI());
		}

		if (isFunctionalSatisfied) {
			for (String nfmtString : requestInfo.constraints.keySet()) {
				Optional<NonfunctionalMetricType> metricTypeOpt = NonfunctionalMetricType.getMetricByName(nfmtString);
				if (metricTypeOpt.isPresent()) {
					final NonfunctionalMetricType metricType = metricTypeOpt.get();
					double currentValue = metricType
							.getAggregatedValue(cms.getServiceNonfunctionalMap().getAnnotationMap(), fcn);
					final boolean nfSatisfied;
					if ("g".equals(requestInfo.relations.get(nfmtString))) {
						nfSatisfied = currentValue >= requestInfo.constraints.get(nfmtString);
					} else {
						nfSatisfied = currentValue <= requestInfo.constraints.get(nfmtString);
					}

					adaptationRecommended = adaptationRecommended || !nfSatisfied;
					nfSatisfaction.put(nfmtString, nfSatisfied);
					nfValue.put(nfmtString, currentValue);
				}
			}
		}

		adaptationRecommended = adaptationRecommended || !isFunctionalSatisfied;

		String mashupStatusXml = UtilityClass.readFile(new File(systemAddress + "/mashupStatus.xml"),
				Charset.defaultCharset());
		MashupStatus mashupStatus = readMashupStatus(mashupStatusXml);

		mashupStatus.isWorking = isFunctionalSatisfied;

		mashupStatus.nfProperties.clear();

		for (String nfKey : nfValue.keySet()) {
			mashupStatus.nfProperties.add(new NonfunctionalProperty(nfKey, nfValue.get(nfKey)));
		}

		mashupStatusXml = createMashupStatusXml(mashupStatus);
		UtilityClass.writeFile(new File(systemAddress + "/mashupStatus.xml"), mashupStatusXml);

		RequirementStatus rs = new RequirementStatus();

		rs.isFunctionalSatisfied = isFunctionalSatisfied;
		rs.nfSatisfaction = nfSatisfaction;
		rs.nfValue = nfValue;
		rs.adaptationRecommended = adaptationRecommended;

		return rs;

	}

}

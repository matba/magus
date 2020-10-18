package edu.ls3.magus.web.composer.core;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import edu.ls3.magus.cl.fmconfigurator.ContextStateModel;
import edu.ls3.magus.cl.fmconfigurator.DomainModels;
import edu.ls3.magus.cl.fmconfigurator.FeatureModelConfigurationMashupGeneration;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModelConfiguration;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.OperationNode;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.configuration.Configuration;
import edu.ls3.magus.eval.generators.owls.UtilityClass;

public class GenerateMashupProcess extends Process {
	private String mashupFamilyURI;
	private String[] selectedFeaturesUUIDs;

	public GenerateMashupProcess(String mashupFamilyURI, String[] selectedFeaturesUUIDs) {
		this.mashupFamilyURI = mashupFamilyURI;
		this.selectedFeaturesUUIDs = selectedFeaturesUUIDs;
	}

	public GenerationProcessResponse generateMashup() throws Exception {

		final String configurationFileAddress = findSystemAddress(mashupFamilyURI);
		File configurationFile = new File(configurationFileAddress);

		if (!configurationFile.exists()) {
			throw new IOException("Configuration file does not exists.");
		}

		DomainModels domainModel;
		try {
			domainModel = DomainModels.readFromConfigurationFile(configurationFileAddress);
		} catch (Exception ex) {
			throw new IOException("Reading configuration file failed with the following message: " + ex.getMessage(),
					ex);
		}

		ContextStateModel contextStateModel = new ContextStateModel();

		for (Service service : domainModel.getServiceCollection().getServices()) {
			contextStateModel.getServiceAvailabilty().put(service.getURI(), true);

		}

		FeatureModelConfiguration fmc = new FeatureModelConfiguration(selectedFeaturesUUIDs,
				domainModel.getFeatureModel());
		FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(domainModel,
				fmc, contextStateModel);

		try {
			String bpelXml = fmcmg.buildServiceMashup()
					.serializeToBpel(domainModel.getFeatureModelAnnotation().findEntities(fmc));
			String workflowJSON = OperationNode.serializedToJSON(fmcmg.getServiceMashupWorkflow());
			return new GenerationProcessResponse(bpelXml, workflowJSON);

		} catch (Exception ex) {
			throw new Exception("Generating mashup failed with the following message: " + ex.getMessage(), ex);
		}

	}

	public String saveAndCreateUri(String bpelCode) throws IOException {
		String mashupUUID = UUID.randomUUID().toString();

		String relativeAddressFragment = Configuration.generatedServiceMashupDirectory + mashupUUID;

		String mashupDirectoryAddress = Configuration.deploymentDirectory
				+ Configuration.defaultDeploymentDirectory + relativeAddressFragment;

		File mashupDirectory = new File(mashupDirectoryAddress);

		mashupDirectory.mkdirs();

		UtilityClass.writeFile(new File(mashupDirectoryAddress + "/bpel.xml"), bpelCode);

		StringBuilder sb = new StringBuilder();

		sb.append(mashupFamilyURI);

		for (String uuid : selectedFeaturesUUIDs) {
			sb.append(System.lineSeparator());
			sb.append(uuid);
		}

		UtilityClass.writeFile(new File(mashupDirectoryAddress + "/mashupInfo.txt"), sb.toString());

		return Configuration.domainAddress + relativeAddressFragment;
	}

	public class GenerationProcessResponse {
		public String bpelXml;
		public String workflowJSON;

		public GenerationProcessResponse(String bpelXml, String workflowJSON) {
			this.bpelXml = bpelXml;
			this.workflowJSON = workflowJSON;
		}
	}

}

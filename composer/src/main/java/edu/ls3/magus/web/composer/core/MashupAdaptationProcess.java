package edu.ls3.magus.web.composer.core;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import edu.ls3.magus.cl.fmconfigurator.ContextStateModel;
import edu.ls3.magus.cl.fmconfigurator.DomainModels;
import edu.ls3.magus.cl.fmconfigurator.FeatureModelConfigurationMashupGeneration;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureAtomicSetMap;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModel;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModelConfiguration;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.FlowComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.NonfunctionalConstraint;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.NonfunctionalMetricType;
import edu.ls3.magus.configuration.Configuration;
import edu.ls3.magus.eval.generators.owls.UtilityClass;
import edu.ls3.magus.utility.Holder;
import edu.ls3.magus.web.composer.services.NonfunctionalProperty;

public class MashupAdaptationProcess extends Process {
	final private String runningInstanceUri;

	public MashupAdaptationProcess(String runningInstanceUri) {
		this.runningInstanceUri = runningInstanceUri;
	}
	
	
	public AdaptationResult adaptMashup()
			throws Exception {
		if (!runningInstanceUri.startsWith(Configuration.domainAddress)) {
			throw new IllegalArgumentException("Only mashup families on magus online site is supported.");
		}

		String systemAddress = runningInstanceUri.replace(Configuration.domainAddress,
				Configuration.defaultDeploymentDirectory);

		if (!systemAddress.endsWith(File.pathSeparator)) {
			systemAddress = systemAddress + File.pathSeparator;
		}

		final String requestInfoAddress = systemAddress + "requestInformation.xml";

		RequestInformation requestInfo = readRequestInfo(requestInfoAddress);

		GeneratedMashupInfo mashupInfo = readGeneratedMashupInfo(requestInfo.mashupInstanceUri);
		
		final String configurationFileAddress = findSystemAddress(mashupInfo.mashupFamilyURI);

		final DomainModels domainModels;
		try {
			domainModels = DomainModels.readFromConfigurationFile(configurationFileAddress);
		} catch (Exception ex) {
			throw new IOException(
					"Reading configuration file failed with the following message: " + ex.getMessage(), ex);
		}
		
		final File configurationFile = new File(configurationFileAddress);
		final String mashupDirectoryAddress = configurationFile.getParent();
		final File mashupTrainingDirectory = new File(mashupDirectoryAddress + File.pathSeparator
				+ Configuration.trainingSetDirectory);
		if(!mashupTrainingDirectory.exists()) {
			throw new IOException("Training set has not been generated for this service mashup family");
		}
		
		TrainingSetInfo trainingSetInfo = 
				readTrainingSet(mashupTrainingDirectory.getAbsolutePath(), domainModels);
		

		FeatureModelConfiguration fmc = new FeatureModelConfiguration(mashupInfo.selectedFeaturesUuids.toArray(new String[0]),
				domainModels.getFeatureModel());
		
		Holder<Integer> holder = new Holder<>(new Integer(0));
		
		File cmsFile = new File(systemAddress + "/contextStateModel.xml");
		String cmsXml = UtilityClass.readFile(cmsFile, Charset.defaultCharset());
		
		ContextStateModel contextStateModel = ContextStateModel.readFromFile(cmsXml, NonfunctionalMetricType.getAllSupportedTypes(),
				domainModels.getServiceCollection());
		
		List<NonfunctionalConstraint> constraints = new ArrayList<>();
		for(String nfString : requestInfo.constraints.keySet()) {
			Optional<NonfunctionalMetricType> metricType = NonfunctionalMetricType.getAllSupportedTypes().stream().filter(value -> value.getCode().equals(nfString)).findAny();
			if(metricType.isPresent()) {
				NonfunctionalConstraint nfc = new NonfunctionalConstraint(metricType.get(), requestInfo.constraints.get(nfString), true);
				constraints.add(nfc);
			}
		}
		
		FeatureModelConfiguration alternateFMC = fmc.findAlternateConfigurationNF(domainModels, contextStateModel, constraints, trainingSetInfo.trainingSet, trainingSetInfo.fasm, holder);
		
		if(alternateFMC == null) {
			throw new Exception("There is no alternate feature model configuration which can satisfy user's requirements.");
		}
		
		FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(domainModels, alternateFMC);
		
		FlowComponentNode fcn = fmcmg.buildServiceMashup();
		
		List<NonfunctionalProperty> predictedNonfunctionalAfterAdaptation = new ArrayList<>();
		
		
		for (String nfmtString : requestInfo.constraints.keySet()) {
			Optional<NonfunctionalMetricType> metricTypeOpt = NonfunctionalMetricType.getMetricByName(nfmtString);
			if (metricTypeOpt.isPresent()) {
				final NonfunctionalMetricType metricType = metricTypeOpt.get();
				double currentValue = metricType.getAggregatedValue(contextStateModel.getServiceNonfunctionalMap().getAnnotationMap(),
						fcn);
				predictedNonfunctionalAfterAdaptation.add(new NonfunctionalProperty(metricType.getCode(), currentValue));
			}
		}
		
		AdaptationResult result = new AdaptationResult(fcn.serializeToBpel(Collections.emptyList()), 
				alternateFMC.getSelectedFeatureUUIDs().toArray(new  String[0]), 
				predictedNonfunctionalAfterAdaptation.toArray(new NonfunctionalProperty[0]));
		

		return result;
	}

	public class AdaptationResult {
		final public String bpelCodeXml;
		final public String[] providedFeaturesUuidAfterAdaptation;
		final public NonfunctionalProperty[] predictedNonfunctionalAfterAdaptation;

		public AdaptationResult(String bpelCodeXml, String[] providedFeaturesUuidAfterAdaptation,
				NonfunctionalProperty[] predictedNonfunctionalAfterAdaptation) {
			this.bpelCodeXml = bpelCodeXml;
			this.providedFeaturesUuidAfterAdaptation = providedFeaturesUuidAfterAdaptation;
			this.predictedNonfunctionalAfterAdaptation = predictedNonfunctionalAfterAdaptation;
		}

		

	}
}

package edu.ls3.magus.web.composer.core;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.ls3.magus.cl.fmconfigurator.ContextStateModel;
import edu.ls3.magus.cl.fmconfigurator.DomainModels;
import edu.ls3.magus.cl.fmconfigurator.FeatureModelConfigurationMashupGeneration;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureAtomicSetMap;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModelConfiguration;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.BpelNode;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.FlowComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.OperationNode;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.NonfunctionalMetric;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.NonfunctionalMetricType;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.ServiceNonfunctionalAnnotation;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.ServiceNonfunctionalAnnotationMap;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.configuration.Configuration;
import edu.ls3.magus.eval.generators.owls.UtilityClass;
import edu.ls3.magus.web.composer.services.NonfunctionalProperty;
import edu.ls3.magus.web.composer.services.RequestContextStateModel;
import edu.ls3.magus.web.composer.services.RequestServiceStateModel;

public class Process {

	protected String findSystemAddress(String mashupFamilyURI) {
		final String mashupFamilyDirectory;
		final String addressfragment;

		if (!mashupFamilyURI.startsWith(Configuration.domainAddress)) {
			throw new IllegalArgumentException("Only mashup families on magus online site is supported.");
		}

		final String defaultRepositoriesAddress = Configuration.domainAddress
				+ Configuration.defaultRepositoriesWebAddress;
		if (mashupFamilyURI.startsWith(defaultRepositoriesAddress)) {
			mashupFamilyDirectory = Configuration.deploymentDirectory + Configuration.defaultDeploymentDirectory
					+ Configuration.defaultRepositoriesWebAddress;
			addressfragment = mashupFamilyURI.substring(defaultRepositoriesAddress.length());
		} else {
			mashupFamilyDirectory = Configuration.deploymentDirectory + Configuration.repositorySystemAddress;
			final int index = Configuration.domainAddress.length() + Configuration.repositoryWebAddress.length();
			addressfragment = mashupFamilyURI.substring(index);
		}

		final String configurationFileAddress = mashupFamilyDirectory + addressfragment;
		return configurationFileAddress;
	}

	protected ContextStateModel covertContextStateModel(RequestContextStateModel requestContextStateModel,
			DomainModels domainModels) {
		final Map<String, Boolean> serviceAvailability = new HashMap<>();

		final Map<Service, ServiceNonfunctionalAnnotation> annotationMap = new HashMap<>();

		for (RequestServiceStateModel sm : requestContextStateModel.serviceStateModel) {
			serviceAvailability.put(sm.serviceURI, sm.isAvailable);

			Service curService = domainModels.getServiceCollection().getServiceByURI(sm.serviceURI);
			Map<NonfunctionalMetricType, NonfunctionalMetric> annotation = new HashMap<>();

			for (NonfunctionalProperty nfp : sm.nonfunctionalProperties) {
				Optional<NonfunctionalMetricType> typeOpt = NonfunctionalMetricType.getAllSupportedTypes().stream()
						.filter(value -> value.getCode().equals(nfp.nonfunctionalPropertyID)).findAny();
				if (typeOpt.isPresent()) {
					annotation.put(typeOpt.get(), new NonfunctionalMetric(typeOpt.get(), nfp.value, 0));
				}
			}

			ServiceNonfunctionalAnnotation snfa = new ServiceNonfunctionalAnnotation(curService, annotation);
			annotationMap.put(curService, snfa);
		}

		ServiceNonfunctionalAnnotationMap serviceNonfunctionalAnnotationMap = new ServiceNonfunctionalAnnotationMap(
				annotationMap);
		final ContextStateModel contextStateModel = new ContextStateModel(serviceAvailability,
				serviceNonfunctionalAnnotationMap);
		return contextStateModel;

	}

	public class GeneratedMashup {
		public FlowComponentNode fcn;
		public String[] usedServiceURIs;

	}

	protected GeneratedMashup generateMashup(DomainModels domainModel, String[] selectedFeaturesUUIDs)
			throws Exception {
		ContextStateModel contextStateModel = new ContextStateModel();

		for (Service service : domainModel.getServiceCollection().getServices()) {
			contextStateModel.getServiceAvailabilty().put(service.getURI(), true);

		}

		FeatureModelConfiguration fmc = new FeatureModelConfiguration(selectedFeaturesUUIDs,
				domainModel.getFeatureModel());
		FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(domainModel,
				fmc, contextStateModel);

		FlowComponentNode fcn = fmcmg.buildServiceMashup();

		List<OperationNode> optimizedGraph = fmcmg.getServiceMashupWorkflow();

		List<String> usedServices = new ArrayList<String>();
		for (OperationNode on : optimizedGraph) {
			String curServiceURI = on.getCalledService().getCalledService().getURI();
			if (curServiceURI.length() == 0)
				continue;
			if (!usedServices.contains(curServiceURI))
				usedServices.add(curServiceURI);
		}
		String[] usedServiceURIs = usedServices.toArray(new String[0]);

		GeneratedMashup mashup = new GeneratedMashup();
		mashup.fcn = fcn;
		mashup.usedServiceURIs = usedServiceURIs;
		return mashup;

	}

	protected GeneratedMashupInfo readGeneratedMashupInfo(String mashupInstanceUri) throws IOException {
		GeneratedMashupInfo result = new GeneratedMashupInfo();

		if (!mashupInstanceUri.startsWith(Configuration.domainAddress)) {
			throw new IllegalArgumentException("Only mashup families on magus online site is supported.");
		}

		result.systemDirectory = mashupInstanceUri.replace(Configuration.domainAddress,
				Configuration.deploymentDirectory + Configuration.defaultDeploymentDirectory);

		if (result.systemDirectory.endsWith(File.separator)) {
			result.systemDirectory = result.systemDirectory.substring(0,
					result.systemDirectory.length() - File.separator.length());
		}

		final String configurationFileAddress = result.systemDirectory + File.separator + "mashupInfo.txt";

		final String configuration = UtilityClass.readFile(new File(configurationFileAddress),
				Charset.defaultCharset());

		final String[] configurationArray = configuration.split(System.lineSeparator());

		result.mashupFamilyURI = configurationArray[0];

		result.selectedFeaturesUuids = Arrays.asList(configurationArray).subList(1, configurationArray.length);

		return result;

	}

	protected class GeneratedMashupInfo {
		public String mashupFamilyURI;
		public List<String> selectedFeaturesUuids;
		public String systemDirectory;
	}

	protected RequestInformation readRequestInfo(String address)
			throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		String xml = UtilityClass.readFile(new File(address), Charset.defaultCharset());

		RequestInformation requestInformation = new RequestInformation();
		requestInformation.criticalFeatureUuids = new HashSet<>();
		requestInformation.constraints = new HashMap<>();
		requestInformation.relations = new HashMap<>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		Document doc = dBuilder.parse(is);

		String rootExpression = "//*[name()='requestInformation']";
		XPath xPath = XPathFactory.newInstance().newXPath();
		Node root = (Node) xPath.compile(rootExpression).evaluate(doc, XPathConstants.NODE);

		requestInformation.mashupInstanceUri = root.getAttributes().getNamedItem("instanceUri").getNodeValue();

		NodeList childNodes = root.getChildNodes();

		for (int rootChildCntr = 0; rootChildCntr < childNodes.getLength(); rootChildCntr++) {
			if (childNodes.item(rootChildCntr).getNodeName().equals("criticalFeatures")) {
				NodeList featureNodes = childNodes.item(rootChildCntr).getChildNodes();
				for (int featureCntr = 0; featureCntr < featureNodes.getLength(); featureCntr++) {
					if (featureNodes.item(featureCntr).getNodeName().equals("feature")) {
						requestInformation.criticalFeatureUuids.add(
								featureNodes.item(featureCntr).getAttributes().getNamedItem("uuid").getNodeValue());

					}
				}

			} else if (childNodes.item(rootChildCntr).getNodeName().equals("constraints")) {
				NodeList constraintsNodes = childNodes.item(rootChildCntr).getChildNodes();
				for (int constraintsCntr = 0; constraintsCntr < constraintsNodes.getLength(); constraintsCntr++) {
					if (constraintsNodes.item(constraintsCntr).getNodeName().equals("constraint")) {
						requestInformation.constraints.put(
								constraintsNodes.item(constraintsCntr).getAttributes().getNamedItem("id")
										.getNodeValue(),
								Double.valueOf(constraintsNodes.item(constraintsCntr).getAttributes()
										.getNamedItem("value").getNodeValue()));
						requestInformation.relations.put(
								constraintsNodes.item(constraintsCntr).getAttributes().getNamedItem("id")
										.getNodeValue(),
								constraintsNodes.item(constraintsCntr).getAttributes().getNamedItem("relation")
										.getNodeValue());
					}
				}

			}
		}

		return requestInformation;
	}

	protected class RequestInformation {
		public String mashupInstanceUri;
		public Set<String> criticalFeatureUuids;
		public Map<String, Double> constraints;
		public Map<String, String> relations;
	}

	public TrainingSetInfo readTrainingSet(String trainingSetDirectory, DomainModels domainModel)
			throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
		Map<FeatureModelConfiguration, FlowComponentNode> trainingSet = new HashMap<>();
		File configurationFile = new File(trainingSetDirectory + File.separator + Configuration.configurationFileName);
		if (!configurationFile.exists()) {
			throw new IOException("Configuration file for training set does not exist.");

		}

		String configuration = UtilityClass.readFile(configurationFile, Charset.defaultCharset());

		String[] configurationLines = configuration.split(System.lineSeparator());

		if (configurationLines.length % 2 != 0) {
			throw new IOException("Invalid Configuration file for training set.");
		}

		for (int lineCntr = 0; lineCntr < configurationLines.length; lineCntr += 2) {
			String uuid = configurationLines[lineCntr];
			final String[] featureUuids = configurationLines[lineCntr + 1].split(",");
			String bpelCode = UtilityClass.readFile(new File(trainingSetDirectory + File.separator + "FM" + uuid),
					Charset.defaultCharset());
			final FlowComponentNode fcn = BpelNode.readFromBpelXml(bpelCode, domainModel.getServiceCollection());
			final FeatureModelConfiguration fmc = new FeatureModelConfiguration(featureUuids,
					domainModel.getFeatureModel());
			trainingSet.put(fmc, fcn);
		}

		TrainingSetInfo result = new TrainingSetInfo();
		result.trainingSet = trainingSet;

		File fasmFile = new File(trainingSetDirectory + File.separator + "fasm");
		String xml = UtilityClass.readFile(fasmFile, Charset.defaultCharset());
		result.fasm = FeatureAtomicSetMap.readFromXml(xml, domainModel.getFeatureModel());

		return result;
	}

	protected class TrainingSetInfo {
		public Map<FeatureModelConfiguration, FlowComponentNode> trainingSet;
		public FeatureAtomicSetMap fasm;
	}

	protected String createMashupStatusXml(MashupStatus status)
			throws TransformerException, ParserConfigurationException {
		String result = "";
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();

		Element rootElement = doc.createElement("requestInformation");

		Attr attr = doc.createAttribute("xmlns:xsi");
		attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
		rootElement.setAttributeNode(attr);

		rootElement.setAttribute("working", String.valueOf(status.isWorking));

		doc.appendChild(rootElement);

		Element criticalFeaturesElement = doc.createElement("criticalFeatures");

		for (String featureUuid : status.mashupFeatures) {
			Element featureElement = doc.createElement("feature");
			featureElement.setAttribute("uuid", featureUuid);
			criticalFeaturesElement.appendChild(featureElement);
		}

		rootElement.appendChild(criticalFeaturesElement);

		if (status.isWorking) {

			Element constraintsElement = doc.createElement("constraints");

			for (NonfunctionalProperty nfp : status.nfProperties) {
				Element nonFunctionalElement = doc.createElement("constraint");
				nonFunctionalElement.setAttribute("id", nfp.nonfunctionalPropertyID);
				nonFunctionalElement.setAttribute("value", String.valueOf(nfp.value));
				constraintsElement.appendChild(nonFunctionalElement);
			}

			rootElement.appendChild(constraintsElement);
		}

		StringWriter output = new StringWriter();

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(doc), new StreamResult(output));

		result = output.toString();
		return result;
	}

	protected MashupStatus readMashupStatus(String xml)
			throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		MashupStatus mashupStatus = new MashupStatus();
		mashupStatus.mashupFeatures = new ArrayList<>();
		mashupStatus.nfProperties = new ArrayList<>();
		mashupStatus.isWorking = true;

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		Document doc = dBuilder.parse(is);

		String rootExpression = "//*[name()='requestInformation']";
		XPath xPath = XPathFactory.newInstance().newXPath();
		Node root = (Node) xPath.compile(rootExpression).evaluate(doc, XPathConstants.NODE);

		mashupStatus.isWorking = Boolean.valueOf(root.getAttributes().getNamedItem("working").getNodeValue());

		NodeList childNodes = root.getChildNodes();

		for (int rootChildCntr = 0; rootChildCntr < childNodes.getLength(); rootChildCntr++) {
			if (childNodes.item(rootChildCntr).getNodeName().equals("criticalFeatures")) {
				NodeList featureNodes = childNodes.item(rootChildCntr).getChildNodes();
				for (int featureCntr = 0; featureCntr < featureNodes.getLength(); featureCntr++) {
					if (featureNodes.item(featureCntr).getNodeName().equals("feature")) {
						mashupStatus.mashupFeatures.add(
								featureNodes.item(featureCntr).getAttributes().getNamedItem("uuid").getNodeValue());

					}
				}

			} else if (childNodes.item(rootChildCntr).getNodeName().equals("constraints")) {
				NodeList constraintsNodes = childNodes.item(rootChildCntr).getChildNodes();
				for (int constraintsCntr = 0; constraintsCntr < constraintsNodes.getLength(); constraintsCntr++) {
					if (constraintsNodes.item(constraintsCntr).getNodeName().equals("constraint")) {
						mashupStatus.nfProperties.add(new NonfunctionalProperty(
								constraintsNodes.item(constraintsCntr).getAttributes().getNamedItem("id")
										.getNodeValue(),
								Double.valueOf(constraintsNodes.item(constraintsCntr).getAttributes()
										.getNamedItem("value").getNodeValue())));
					}
				}

			}
		}

		return mashupStatus;
	}

	protected class MashupStatus {
		public boolean isWorking;
		public List<String> mashupFeatures;
		public List<NonfunctionalProperty> nfProperties;
	}
}

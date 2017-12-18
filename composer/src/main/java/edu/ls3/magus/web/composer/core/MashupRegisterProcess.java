package edu.ls3.magus.web.composer.core;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.ls3.magus.cl.fmconfigurator.ContextStateModel;
import edu.ls3.magus.cl.fmconfigurator.DomainModels;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.NonfunctionalMetricType;
import edu.ls3.magus.configuration.Configuration;
import edu.ls3.magus.eval.generators.owls.UtilityClass;
import edu.ls3.magus.web.composer.services.NonfunctionalConstraint;
import edu.ls3.magus.web.composer.services.NonfunctionalProperty;
import edu.ls3.magus.web.composer.services.RequestContextStateModel;
import edu.ls3.magus.web.composer.services.RequestServiceStateModel;

public class MashupRegisterProcess extends Process {
	private String mashupInstanceUri;

	private String[] criticalFeatureUUIDs;
	private NonfunctionalConstraint[] constraints;

	public MashupRegisterProcess(String mashupInstanceUri, String[] criticalFeatureUUIDs,
			NonfunctionalConstraint[] constraints) {
		this.mashupInstanceUri = mashupInstanceUri;
		this.criticalFeatureUUIDs = criticalFeatureUUIDs;
		this.constraints = constraints;
	}

	public String registerMashup() throws IOException, ParserConfigurationException, TransformerException {

		if (!mashupInstanceUri.startsWith(Configuration.domainAddress)) {
			throw new IllegalArgumentException("Only mashup families on magus online site is supported.");
		}

		GeneratedMashupInfo mashupInfo = readGeneratedMashupInfo(mashupInstanceUri);

		final String configurationFileAddress = findSystemAddress(mashupInfo.mashupFamilyURI);

		final DomainModels domainModels;
		try {
			domainModels = DomainModels.readFromConfigurationFile(configurationFileAddress);
		} catch (Exception ex) {
			throw new IOException("Reading configuration file failed with the following message: " + ex.getMessage(),
					ex);
		}

		String mashupUUID = UUID.randomUUID().toString();

		String relativeAddressFragment = Configuration.runningServiceMashupDirectory + mashupUUID;

		String mashupDirectoryAddress = Configuration.deploymentDirectory + relativeAddressFragment;

		File mashupDirectory = new File(mashupDirectoryAddress);

		mashupDirectory.mkdirs();

		String mashupSystemAddress = mashupInstanceUri.replace(Configuration.domainAddress,
				Configuration.deploymentDirectory);

		if (!mashupSystemAddress.endsWith("/")) {
			mashupSystemAddress += "/";
		}
		String bpelSystemAddress = mashupSystemAddress + "bpel.xml";
		FileUtils.copyFile(new File(bpelSystemAddress), new File(mashupDirectoryAddress + "/bpel.xml"));

		List<String> serviceUris = domainModels.getServiceCollection().getServices().stream()
				.filter(value -> value.getInvocationService() == null).map(value -> value.getURI())
				.collect(Collectors.toList());

		List<RequestServiceStateModel> csmList = new ArrayList<>();
		for (String serviceUri : serviceUris) {
			RequestServiceStateModel r = new RequestServiceStateModel();
			r.serviceURI = serviceUri;
			r.isAvailable = true;
			r.nonfunctionalProperties = new NonfunctionalProperty[constraints.length];
			for (int csCntr = 0; csCntr < constraints.length; csCntr++) {
				r.nonfunctionalProperties[csCntr] = new NonfunctionalProperty();
				Optional<NonfunctionalMetricType> metricTypeOpt = NonfunctionalMetricType
						.getMetricByName(constraints[csCntr].nonfunctionalPropertyID);
				r.nonfunctionalProperties[csCntr].nonfunctionalPropertyID = constraints[csCntr].nonfunctionalPropertyID;
				if (metricTypeOpt.isPresent()) {
					r.nonfunctionalProperties[csCntr].value = metricTypeOpt.get().getNeutralValue();
				} else {
					r.nonfunctionalProperties[csCntr].value = 0;
				}
			}
			csmList.add(r);
		}

		RequestContextStateModel rcsm = new RequestContextStateModel();
		rcsm.serviceStateModel = csmList.toArray(new RequestServiceStateModel[0]);

		ContextStateModel csm = covertContextStateModel(rcsm, domainModels);

		UtilityClass.writeFile(new File(mashupDirectoryAddress + "/contextStateModel.xml"), csm.serializeToXml());

		MashupStatus mashupStatus = new MashupStatus();
		mashupStatus.mashupFeatures = mashupInfo.selectedFeaturesUuids;
		mashupStatus.nfProperties = new ArrayList<>();
		mashupStatus.isWorking = true;
		for (NonfunctionalConstraint np : constraints) {
			Optional<NonfunctionalMetricType> metricTypeOpt = NonfunctionalMetricType
					.getMetricByName(np.nonfunctionalPropertyID);
			if (metricTypeOpt.isPresent()) {
				mashupStatus.nfProperties.add(
						new NonfunctionalProperty(np.nonfunctionalPropertyID, metricTypeOpt.get().getNeutralValue()));
			} else {
				mashupStatus.nfProperties.add(new NonfunctionalProperty(np.nonfunctionalPropertyID, 0));
			}
		}

		String mashupStatusXml = createMashupStatusXml(mashupStatus);
		UtilityClass.writeFile(new File(mashupDirectoryAddress + "/mashupStatus.xml"), mashupStatusXml);

		final String requestInfo = createRequestInfoXml();

		UtilityClass.writeFile(new File(mashupDirectoryAddress + "/requestInformation.xml"), requestInfo);

		return Configuration.domainAddress + relativeAddressFragment;
	}

	private String createRequestInfoXml() throws ParserConfigurationException, TransformerException {
		String result = "";
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();

		Element rootElement = doc.createElement("requestInformation");

		Attr attr = doc.createAttribute("xmlns:xsi");
		attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
		rootElement.setAttributeNode(attr);

		rootElement.setAttribute("instanceUri", mashupInstanceUri);

		doc.appendChild(rootElement);

		Element criticalFeaturesElement = doc.createElement("criticalFeatures");

		for (String featureUuid : criticalFeatureUUIDs) {
			Element featureElement = doc.createElement("feature");
			featureElement.setAttribute("uuid", featureUuid);
			criticalFeaturesElement.appendChild(featureElement);
		}

		rootElement.appendChild(criticalFeaturesElement);

		Element constraintsElement = doc.createElement("constraints");

		for (NonfunctionalConstraint nfp : constraints) {
			Element nonFunctionalElement = doc.createElement("constraint");
			nonFunctionalElement.setAttribute("id", nfp.nonfunctionalPropertyID);
			nonFunctionalElement.setAttribute("value", String.valueOf(nfp.value));
			nonFunctionalElement.setAttribute("relation", nfp.relation);
			constraintsElement.appendChild(nonFunctionalElement);
		}

		rootElement.appendChild(constraintsElement);

		StringWriter output = new StringWriter();

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(doc), new StreamResult(output));

		result = output.toString();
		return result;
	}

}

package edu.ls3.magus.web.composer.core;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.ls3.magus.cl.fmconfigurator.ContextStateModel;
import edu.ls3.magus.cl.fmconfigurator.DomainModels;
import edu.ls3.magus.configuration.Configuration;
import edu.ls3.magus.eval.generators.owls.UtilityClass;
import edu.ls3.magus.web.composer.services.NonfunctionalProperty;
import edu.ls3.magus.web.composer.services.RequestContextStateModel;

public class MashupRegisterProcess extends Process {
    private String mashupInstanceUri;
    private RequestContextStateModel contextStateModel;
    private String[] criticalFeatureUUIDs;
    private NonfunctionalProperty[] constraints;

    public MashupRegisterProcess(String mashupInstanceUri, RequestContextStateModel contextStateModel,
            String[] criticalFeatureUUIDs, NonfunctionalProperty[] constraints) {
        this.mashupInstanceUri = mashupInstanceUri;
        this.contextStateModel = contextStateModel;
        this.criticalFeatureUUIDs = criticalFeatureUUIDs;
        this.constraints = constraints;
    }

    public String registerMashup() throws IOException, ParserConfigurationException, TransformerException {

        if (!mashupInstanceUri.startsWith(Configuration.domainAddress)) {
            throw new IllegalArgumentException("Only mashup families on magus online site is supported.");
        }

        String systemAddress = mashupInstanceUri.replace(Configuration.domainAddress,
                Configuration.defaultDeploymentDirectory);

        if (!systemAddress.endsWith("/")) {
            systemAddress = systemAddress + "/";
        }

        final String configurationFileAddress = systemAddress + "mashupInfo.txt";

        final GeneratedMashupInfo mashupInfo = readGeneratedMashupInfo(configurationFileAddress);

        final String mashupFamilyUri = mashupInfo.mashupFamilyURI;

        final String mashupFamilyAddress = findSystemAddress(mashupFamilyUri);

        final DomainModels domainModels;
        try {
            domainModels = DomainModels.readFromConfigurationFile(mashupFamilyAddress);
        } catch (Exception ex) {
            throw new IOException("Reading configuration file failed with the following message: " + ex.getMessage(),
                    ex);
        }

        ContextStateModel cms = covertContextStateModel(contextStateModel, domainModels);

        String mashupUUID = UUID.randomUUID().toString();

        String relativeAddressFragment = Configuration.runningServiceMashupDirectory + mashupUUID;

        String mashupDirectoryAddress = Configuration.deploymentDirectory + relativeAddressFragment;

        File mashupDirectory = new File(mashupDirectoryAddress);

        mashupDirectory.mkdirs();

        UtilityClass.writeFile(new File(mashupDirectoryAddress + "/contextStateModel.xml"), cms.serializeToXml());

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

        for (NonfunctionalProperty nfp : constraints) {
            Element nonFunctionalElement = doc.createElement("constraint");
            nonFunctionalElement.setAttribute("id", nfp.nonfunctionalPropertyID);
            nonFunctionalElement.setAttribute("value", String.valueOf(nfp.value));
            constraintsElement.appendChild(nonFunctionalElement);
        }

        constraintsElement.appendChild(criticalFeaturesElement);

        StringWriter output = new StringWriter();

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(doc), new StreamResult(output));

        result = output.toString();
        return result;
    }

}

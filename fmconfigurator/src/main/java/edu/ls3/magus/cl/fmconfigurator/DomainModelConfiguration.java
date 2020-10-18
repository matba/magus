package edu.ls3.magus.cl.fmconfigurator;

import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DomainModelConfiguration {
	private List<String> contextModelAddresses;
	private String featureModelAddress;
	private List<String> serviceAddresses;

	public DomainModelConfiguration(List<String> contextModelAddresses,String featureModelAddress,List<String> serviceAddresses ){
		this.contextModelAddresses = contextModelAddresses;
		this.featureModelAddress = featureModelAddress;
		this.serviceAddresses = serviceAddresses;
	}

	public String serializeToConfigurationFileXml() throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException{
		String result="";
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();

		Element rootElement = doc.createElement("configuration");

		Attr attr = doc.createAttribute("xmlns:xsi");
		attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
		rootElement.setAttributeNode(attr);

		attr = doc.createAttribute("xsi:noNamespaceSchemaLocation");
		attr.setValue("http://bashari.ca/magus/resources/schema/servicemashupconfiguration.xsd");
		rootElement.setAttributeNode(attr);

		doc.appendChild(rootElement);

		Element ontologiesElement = doc.createElement("ontologies");

		for(String s : contextModelAddresses)
		{
			Element ontologyElement = doc.createElement("ontology");

			attr = doc.createAttribute("address");
			attr.setValue(s);
			ontologyElement.setAttributeNode(attr);

			ontologiesElement.appendChild(ontologyElement);
		}


		rootElement.appendChild(ontologiesElement);

		Element fmElement = doc.createElement("featuremodel");

		attr = doc.createAttribute("address");
		attr.setValue(featureModelAddress);
		fmElement.setAttributeNode(attr);

		rootElement.appendChild(fmElement);

		Element servicesElement = doc.createElement("services");

		for(String s : serviceAddresses)
		{
			Element serviceElement = doc.createElement("service");

			attr = doc.createAttribute("address");
			attr.setValue(s);
			serviceElement.setAttributeNode(attr);

			servicesElement.appendChild(serviceElement);
		}


		rootElement.appendChild(servicesElement);


		StringWriter output = new StringWriter();

	    Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    transformer.transform(new DOMSource(doc), new StreamResult(output));

	    result = output.toString();
		return result;

	}
}

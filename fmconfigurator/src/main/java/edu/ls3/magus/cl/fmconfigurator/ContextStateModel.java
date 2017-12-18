package edu.ls3.magus.cl.fmconfigurator;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
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

import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.NonfunctionalMetric;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.NonfunctionalMetricType;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.ServiceNonfunctionalAnnotation;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.ServiceNonfunctionalAnnotationMap;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.cl.mashupconfigurator.service.ServiceCollection;

public class ContextStateModel {
	private Map<String, Boolean> serviceAvailabilty;
	private ServiceNonfunctionalAnnotationMap serviceNonfunctionalMap;

	public ContextStateModel() {
		setServiceAvailabilty(new HashMap<String, Boolean>());
	}

	public ContextStateModel(ServiceCollection serviceCollection) {
		setServiceAvailabilty(new HashMap<String, Boolean>());
		for (Service s : serviceCollection.getServices())
			serviceAvailabilty.put(s.getURI(), true);
	}

	public ContextStateModel(ServiceCollection serviceCollection,
			ServiceNonfunctionalAnnotationMap serviceNonfunctionalMap) {
		this(serviceCollection);
		setServiceNonfunctionalMap(serviceNonfunctionalMap);
	}

	public ContextStateModel(Map<String, Boolean> serviceAvailability,
			ServiceNonfunctionalAnnotationMap serviceNonfunctionalAnnotationMap) {
		setServiceAvailabilty(serviceAvailability);
		setServiceNonfunctionalMap(serviceNonfunctionalAnnotationMap);
	}

	public Map<String, Boolean> getServiceAvailabilty() {
		return serviceAvailabilty;
	}

	public void setServiceAvailabilty(Map<String, Boolean> serviceAvailabilty) {
		this.serviceAvailabilty = serviceAvailabilty;
	}

	public ServiceNonfunctionalAnnotationMap getServiceNonfunctionalMap() {
		return serviceNonfunctionalMap;
	}

	private void setServiceNonfunctionalMap(ServiceNonfunctionalAnnotationMap serviceNonfunctionalMap) {
		this.serviceNonfunctionalMap = serviceNonfunctionalMap;
	}

	public String serializeToXml() throws ParserConfigurationException, TransformerException {
		String result = "";
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();

		Element rootElement = doc.createElement("contextStateModel");

		Attr attr = doc.createAttribute("xmlns:xsi");
		attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
		rootElement.setAttributeNode(attr);

		doc.appendChild(rootElement);

		Element availabilityElement = doc.createElement("availability");

		for (String serviceUri : serviceAvailabilty.keySet()) {
			Element serviceElement = doc.createElement("service");
			serviceElement.setAttribute("uri", serviceUri);
			serviceElement.setAttribute("available", serviceAvailabilty.get(serviceUri).toString());
			availabilityElement.appendChild(serviceElement);
		}

		rootElement.appendChild(availabilityElement);

		Element nonfunctionalElement = doc.createElement("nonfunctional");

		for (Service service : serviceNonfunctionalMap.getAnnotationMap().keySet()) {
			Element serviceElement = doc.createElement("service");
			serviceElement.setAttribute("uri", service.getURI());

			for (NonfunctionalMetricType metricType : serviceNonfunctionalMap.getAnnotationMap().get(service)
					.getAnnotation().keySet()) {
				Element propertyElement = doc.createElement("property");
				propertyElement.setAttribute("name", metricType.getCode());
				propertyElement.setAttribute("value", String.valueOf(serviceNonfunctionalMap.getAnnotationMap()
						.get(service).getAnnotation().get(metricType).getAverage()));

				serviceElement.appendChild(propertyElement);
			}

			nonfunctionalElement.appendChild(serviceElement);

		}

		rootElement.appendChild(nonfunctionalElement);

		StringWriter output = new StringWriter();

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(doc), new StreamResult(output));

		result = output.toString();
		return result;

	}

	public static ContextStateModel readFromFile(String xml, Set<NonfunctionalMetricType> supportedMetricTypes,
			ServiceCollection serviceCollection)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		Document doc = dBuilder.parse(is);

		Map<String, Boolean> serviceAvailabilty = new HashMap<>();
		Map<Service, ServiceNonfunctionalAnnotation> annotationMap = new HashMap<>();

		String rootExpression = "//*[name()='contextStateModel']";
		XPath xPath = XPathFactory.newInstance().newXPath();
		Node root = (Node) xPath.compile(rootExpression).evaluate(doc, XPathConstants.NODE);

		NodeList childNodes = root.getChildNodes();

		for (int rootChildCntr = 0; rootChildCntr < childNodes.getLength(); rootChildCntr++) {
			if (childNodes.item(rootChildCntr).getNodeName().equals("availability")) {
				NodeList avaliabilityNodes = childNodes.item(rootChildCntr).getChildNodes();
				for (int availabilityCntr = 0; availabilityCntr < avaliabilityNodes.getLength(); availabilityCntr++) {
					if (avaliabilityNodes.item(availabilityCntr).getNodeName().equals("service")) {
						final String serviceUri = avaliabilityNodes.item(availabilityCntr).getAttributes()
								.getNamedItem("uri").getNodeValue();
						final String availabilityStr = avaliabilityNodes.item(availabilityCntr).getAttributes()
								.getNamedItem("available").getNodeValue();
						serviceAvailabilty.put(serviceUri, Boolean.valueOf(availabilityStr));

					}

				}

			} else if (childNodes.item(rootChildCntr).getNodeName().equals("nonfunctional")) {
				NodeList nonfunctionNodes = childNodes.item(rootChildCntr).getChildNodes();
				for (int nonfunctionalCntr = 0; nonfunctionalCntr < nonfunctionNodes.getLength(); nonfunctionalCntr++) {
					if (nonfunctionNodes.item(nonfunctionalCntr).getNodeName().equals("service")) {
						final String serviceUri = nonfunctionNodes.item(nonfunctionalCntr).getAttributes()
								.getNamedItem("uri").getNodeValue();
						Map<NonfunctionalMetricType, NonfunctionalMetric> annotation = new HashMap<>();
						NodeList serviceNodes = nonfunctionNodes.item(nonfunctionalCntr).getChildNodes();
						for (int serviceCntr = 0; serviceCntr < serviceNodes.getLength(); serviceCntr++) {
							if (serviceNodes.item(serviceCntr).getNodeName().equals("property")) {
								final String metricName = serviceNodes.item(serviceCntr).getAttributes()
										.getNamedItem("name").getNodeValue();
								final String metricValueStr = serviceNodes.item(serviceCntr).getAttributes()
										.getNamedItem("value").getNodeValue();
								Optional<NonfunctionalMetricType> metricTypeOpt = supportedMetricTypes.stream()
										.filter(value -> value.getCode().equals(metricName)).findAny();
								final NonfunctionalMetricType metricType;
								if (!metricTypeOpt.isPresent()) {
									continue;
								}
								metricType = metricTypeOpt.get();
								final double metricValue = Double.valueOf(metricValueStr);
								NonfunctionalMetric nfMetric = new NonfunctionalMetric(metricType, metricValue, 0);
								annotation.put(metricType, nfMetric);

							}
						}

						Service service = serviceCollection.getServiceByURI(serviceUri);
						annotationMap.put(service, new ServiceNonfunctionalAnnotation(service, annotation));

					}
				}

			}
		}

		return new ContextStateModel(serviceAvailabilty, new ServiceNonfunctionalAnnotationMap(annotationMap));
	}

	@Override
	public ContextStateModel clone() {
		Map<String, Boolean> serviceAvailability = new HashMap<String, Boolean>();
		serviceAvailability.putAll(getServiceAvailabilty());

		return new ContextStateModel(serviceAvailability, serviceNonfunctionalMap.clone());
	}
}

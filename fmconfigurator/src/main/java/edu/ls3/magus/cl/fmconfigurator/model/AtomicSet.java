package edu.ls3.magus.cl.fmconfigurator.model;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
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

import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.ServiceNonfunctionalAnnotation;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;

public class AtomicSet {
	private Set<Feature> featureList;
	private Set<Feature> mainFeatureList;
	private boolean isSingleSelectionStateFeature; // These atomic sets are always selected or unselected in feature model configurations
	
	
	public AtomicSet(){
		featureList = new HashSet<Feature>();
		mainFeatureList = new HashSet<Feature>();
		isSingleSelectionStateFeature=false;
	}
	
	// Getters and Setters
	public Set<Feature> getFeatureList() {
		return featureList;
	}

	@SuppressWarnings("unused")
	private void setFeatureList(Set<Feature> featureList) {
		this.featureList = featureList;
	}
	
	@Override
	public String toString() {
		
		return getFeatureList().toString();
	}

	public Set<Feature> getMainFeatureList() {
		return mainFeatureList;
	}



	public boolean isSingleSelectionStateFeature() {
		return isSingleSelectionStateFeature;
	}

	public void setSingleSelectionStateFeature(boolean isSingleSelectionStateFeature) {
		this.isSingleSelectionStateFeature = isSingleSelectionStateFeature;
	}
	
	
	public static String serializeToXml(Set<AtomicSet> atomicSets) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();

        Element rootElement = doc.createElement("AtomicSets");

        Attr attr = doc.createAttribute("xmlns:xsi");
        attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
        rootElement.setAttributeNode(attr);

        doc.appendChild(rootElement);

        for(AtomicSet atomicSet : atomicSets) {
        	rootElement.appendChild(atomicSet.serializeToXmlNode(doc));
        }
        
        
        
        StringWriter output = new StringWriter();

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(doc), new StreamResult(output));

        String result = output.toString();
        return result;

	}

	private Node serializeToXmlNode(Document doc) {
		Element atomicSetElement = doc.createElement("AtomicSet");
		atomicSetElement.setAttribute("singleSelection", String.valueOf(isSingleSelectionStateFeature));
		for(Feature feature : featureList) {
			Element featureElement = doc.createElement("Feature");
			featureElement.setAttribute("uuid", feature.getUuid());
			featureElement.setAttribute("isMain", String.valueOf( mainFeatureList.contains(feature)?true:false));
			atomicSetElement.appendChild(featureElement);
		}
		
		return atomicSetElement;
	}
	
	
	public static Set<AtomicSet> readFromXml(String xml, FeatureModel fm) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
		Set<AtomicSet> result = new HashSet<>();
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        Document doc = dBuilder.parse(is);

        String rootExpression = "//*[name()='AtomicSets']";
        XPath xPath = XPathFactory.newInstance().newXPath();
        Node root = (Node) xPath.compile(rootExpression).evaluate(doc, XPathConstants.NODE);

        NodeList childNodes = root.getChildNodes();
        
        for (int rootChildCntr = 0; rootChildCntr < childNodes.getLength(); rootChildCntr++) {
        	if (childNodes.item(rootChildCntr).getNodeName().equals("AtomicSet")) {
        		AtomicSet atomicSet = new AtomicSet();
        		NodeList atomicSetNodes = childNodes.item(rootChildCntr).getChildNodes();
        		atomicSet.setSingleSelectionStateFeature(Boolean.valueOf(childNodes.item(rootChildCntr).getAttributes()
                        .getNamedItem("singleSelection").getNodeValue()));
        		for(int atomicSetCntr =0; atomicSetCntr < atomicSetNodes.getLength(); atomicSetCntr++) {
        			if(atomicSetNodes.item(atomicSetCntr).getNodeName().equals("Feature")) {
        				Node featureNode = atomicSetNodes.item(atomicSetCntr);
        				String uuid = featureNode.getAttributes().getNamedItem("uuid").getNodeValue();
        				boolean isMainFeature = Boolean.valueOf(featureNode.getAttributes()
        						.getNamedItem("isMain").getNodeValue());
        				Feature feature = fm.getFeaturesUUIDMap().get(uuid);
        				atomicSet.getFeatureList().add(feature);
        				if(isMainFeature) {
        					atomicSet.getMainFeatureList().add(feature);
        				}
        			}
        		}
        		
        		result.add(atomicSet);
        	}
        }
		
		return result;
	}
	
}

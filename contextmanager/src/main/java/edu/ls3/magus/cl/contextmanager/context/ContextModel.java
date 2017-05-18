package edu.ls3.magus.cl.contextmanager.context;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.ls3.magus.cl.contextmanager.basic.Instance;
import edu.ls3.magus.cl.contextmanager.basic.InstanceMap;
import edu.ls3.magus.cl.contextmanager.basic.InstanceType;
import edu.ls3.magus.cl.contextmanager.basic.InstanceTypeMap;
import edu.ls3.magus.cl.contextmanager.basic.StateFactType;
import edu.ls3.magus.cl.contextmanager.basic.StateFactTypeMap;

public class ContextModel {
	private InstanceTypeMap instanceTypes;
	private StateFactTypeMap factTypes;
	private InstanceMap instances;
	private String baseURI;
	
	public ContextModel(){
		instanceTypes = new InstanceTypeMap();
		factTypes = new StateFactTypeMap();
		setInstances(new InstanceMap());
		
		
	}
	
	public ContextModel(InstanceTypeMap types, StateFactTypeMap factTypes, List<Instance> vars)
	{
		this.setInstanceTypes(types);
		this.setFactTypes(factTypes);
		this.setVars(vars);
		setInstances(new InstanceMap());
	}
	public List<Instance> getVars() {
		List<Instance> result = new ArrayList<Instance>(getInstances().getInstanceMap().values());
		return result;
	}
	public void setVars(List<Instance> vars) {
		for(Instance i : vars){
			getInstances().getInstanceMap().put(i.getURI(), i);
		}
		
	}
	public StateFactTypeMap getFactTypes() {
		return factTypes;
	}
	private void setFactTypes(StateFactTypeMap factTypes) {
		this.factTypes = factTypes;
	}
	public InstanceTypeMap getInstanceTypes() {
		return instanceTypes;
	}
	private void setInstanceTypes(InstanceTypeMap types) {
		this.instanceTypes = types;
	}
	public InstanceMap getInstances() {
		return instances;
	}

	private void setInstances(InstanceMap instances) {
		this.instances = instances;
	}
	public void createSimpleContext() throws URISyntaxException{
		for(URI itUri : instanceTypes.getInstanceTypeMap().keySet()){
			getInstances().getInstanceMap().put( new URI( "http://ls3.rnet.ryerson.ca/people/mahdi/magus/owl/vars#v"+instanceTypes.getInstanceTypeMap().get(itUri).getTypeName()),new Instance(instanceTypes.getInstanceTypeMap().get(itUri), "v"+instanceTypes.getInstanceTypeMap().get(itUri).getTypeName() , new URI( "http://ls3.rnet.ryerson.ca/people/mahdi/magus/owl/vars#v"+instanceTypes.getInstanceTypeMap().get(itUri).getTypeName() )));
		}
	}
	public Instance[] getInstanceByName(String  name)
	{
		List<Instance> result = new ArrayList<Instance>();
		for(Instance i: getInstances().getInstanceMap().values())
			if(name.equals( i.getName()))
				result.add(i);
		
		if(result.size()==0)
			return null;
			
		return result.toArray(new Instance[0]);
	}
	public StateFactType[] getInstaceFactTypeByName(String  name)
	{
		List<StateFactType> result = new ArrayList<StateFactType>();
		for(StateFactType i: factTypes.getStateFactTypeList())
			if(name.equals( i.getTypeName()))
				result.add(i);
		
		if(result.size()==0)
			return null;
			
		return result.toArray(new StateFactType[0]);
	}
	public List<Instance> filterVarsByType(InstanceType tp){
		List<Instance> result = new ArrayList<Instance>();
		for(Instance i : getVars())
			if(i.getType().equals(tp))
				result.add(i);
		
		return result;
	}
	
	
	public void AddToContextModel(String xml) throws Exception{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		Document doc = dBuilder.parse(is);
		
		
		
		String rootExpression = "//*[name()='rdf:RDF']";
		XPath xPath =  XPathFactory.newInstance().newXPath();
		Node root = (Node) xPath.compile(rootExpression).evaluate(doc, XPathConstants.NODE);
		
		
		String baseURI = root.getAttributes().getNamedItem("xml:base").getNodeValue();
				 
		this.baseURI = baseURI; 
				 
				
		 NodeList childNodes =  root.getChildNodes();
		 
		 for(int  i = 0; i< childNodes.getLength(); i++)
		 {
			 if(childNodes.item(i).getNodeName().equals("owl:Class") ){
				  InstanceType newInstanceType = readContextType(childNodes.item(i),baseURI);
				  this.getInstanceTypes().add( newInstanceType);
				 
			 }
			 
		 }
		 
		 
		 for(int  i = 0; i< childNodes.getLength(); i++)
		 {
			 if(childNodes.item(i).getNodeName().equals("owl:ObjectProperty")){
				 StateFactType newFactType = readContextFactType(childNodes.item(i),baseURI,childNodes.item(i));
				 this.getFactTypes().add(newFactType);
				 
			 }
			 if(childNodes.item(i).getNodeName().equals("owl:Thing") ){
				 
				 Instance newInstnace = readInstance(childNodes.item(i),baseURI);
				 this.getInstances().add(newInstnace);
				 
			 }
		 }
	}

	private Instance readInstance(Node item, String baseURI) throws Exception {
		
		NodeList childNodes =  item.getChildNodes();
		String instanceName = item.getAttributes().getNamedItem("rdf:about").getNodeValue() ;
		String instanceURI = instanceName;
		if(instanceName.startsWith("#")){
			instanceURI = baseURI+instanceName;
			instanceName = instanceName.substring(1);
		}
		InstanceType instanceType =null;
		
		
		for(int i = 0; i< childNodes.getLength(); i++)
		{
			 if(childNodes.item(i).getNodeName().equals("rdf:type" )){
				 String domainText =  childNodes.item(i).getAttributes().getNamedItem("rdf:resource").getNodeValue();
				 if( domainText.startsWith("#")){
					 domainText = baseURI+domainText;
				 }
				 instanceType= getInstanceTypes().get(new URI(domainText));
				 //newInstance.type = {uri:domainText, name:  utility.getEntityFragment(domainText)};
			 }
			 
		}
		
		
		return new Instance(instanceType, instanceName, new URI( instanceURI));
	}

	private StateFactType readContextFactType(Node item, String baseURI, Node item2) throws URISyntaxException, Exception {
		
		NodeList childNodes =  item.getChildNodes();
		String factTypeName = item.getAttributes().getNamedItem("rdf:ID").getNodeValue();
		String factTypeURI = baseURI + "#"+ factTypeName;
		
		InstanceType param1 = null;
		InstanceType param2 = null;
		
		for(int i = 0; i< childNodes.getLength(); i++)
		{
			 if(childNodes.item(i).getNodeName()=="rdfs:domain" ){
				 String domainText = childNodes.item(i).getAttributes().getNamedItem("rdf:resource").getNodeValue();
				 if( domainText.startsWith("#")){
					 domainText = baseURI+domainText;
				 }
				 param1 = getInstanceTypes().get(new URI( domainText));
				 
			 }
			 
			 if(childNodes.item(i).getNodeName()=="rdfs:range" ){
				 String domainText = childNodes.item(i).getAttributes().getNamedItem("rdf:resource").getNodeValue();
				 if( domainText.startsWith("#")){
					 domainText = baseURI+domainText;
				 }
				 param2 = getInstanceTypes().get(new URI( domainText));
				 
			 }
		
		}
		InstanceType[] params = {param1, param2};
		
		StateFactType result = new StateFactType(factTypeName, new URI(factTypeURI), params);
		return result;
	}

	private InstanceType readContextType(Node item, String baseURI) throws URISyntaxException {
		
		String typeName = item.getAttributes().getNamedItem("rdf:ID").getNodeValue();
		String typeURI = baseURI+"#"+typeName;
		
		InstanceType result = new InstanceType(typeName, new URI(typeURI));
		return result;
		
	}

	public Set<StateFactType> filterStateFactTypeByInstanceType(Set<InstanceType> instanceTypeList) {
		Set<StateFactType> result = new HashSet<StateFactType>();
		
		for(StateFactType sft: getFactTypes().getStateFactTypeList()){
			boolean isMatch = true;
			
			for(InstanceType it:  sft.getParams())
				if(!instanceTypeList.contains(it))
				{
					isMatch = false;
					break;
				}
			if(isMatch)
				result.add(sft);
		}
		
		return result;
	}

	public String serializeToXml() throws ParserConfigurationException, TransformerException {
		
		
		String result="";
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document xmlDoc = docBuilder.newDocument();
		
		Element rdfNode = xmlDoc.createElement("rdf:RDF");
		
		
		
		rdfNode.setAttribute("xmlns", baseURI); 
		rdfNode.setAttribute("xmlns:rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		rdfNode.setAttribute("xmlns:owl", "http://www.w3.org/2002/07/owl#"); 
		rdfNode.setAttribute("xmlns:rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		rdfNode.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema#"); 
		rdfNode.setAttribute("xml:base", baseURI); 
		
		List<InstanceType> ct = getInstanceTypes().getTypesAsList();
		for(int classCntr=0;classCntr<ct.size();classCntr++){
			Element classNode =  xmlDoc.createElement("owl:Class");
			
			classNode.setAttribute("rdf:ID",ct.get(classCntr).getTypeName());
			
			rdfNode.appendChild(classNode);
			
		}
		
		List<StateFactType> ft =getFactTypes().getStateFactTypeList();
		for(int propertyCntr=0;propertyCntr<ft.size();propertyCntr++){
			Element propertyNode =  xmlDoc.createElement("owl:ObjectProperty");
			
			propertyNode.setAttribute("rdf:ID", ft.get(propertyCntr).getTypeName());
			
			Element typeNode = xmlDoc.createElement("rdf:type");
			typeNode.setAttribute("rdf:resource","http://www.w3.org/2002/07/owl#ObjectProperty");
			
			Element domainNode = xmlDoc.createElement("rdfs:domain");
			if( ft.get(propertyCntr).getParams()[0].getURI().toString().startsWith(baseURI))
				domainNode.setAttribute("rdf:resource",ft.get(propertyCntr).getParams()[0].getURI().toString().substring(baseURI.length())) ;
			else
				domainNode.setAttribute("rdf:resource",ft.get(propertyCntr).getParams()[0].getURI().toString()) ;
			
			
			Element resourceNode = xmlDoc.createElement("rdfs:range");
			if(ft.get(propertyCntr).getParams()[1].getURI().toString().startsWith(baseURI))
				resourceNode.setAttribute("rdf:resource",ft.get(propertyCntr).getParams()[1].getURI().toString().substring(baseURI.length())) ;
			else
				resourceNode.setAttribute("rdf:resource",ft.get(propertyCntr).getParams()[1].getURI().toString()) ;
			
			
			propertyNode.appendChild(typeNode);
			propertyNode.appendChild(domainNode);
			propertyNode.appendChild(resourceNode);
				
				
			rdfNode.appendChild(propertyNode);
			
		}
		
		List<Instance> it =getInstances().getAsList();
		for(int instanceCntr=0;instanceCntr<it.size();instanceCntr++){
			Element instanceNode =  xmlDoc.createElement("owl:Thing");
			
			if(it.get(instanceCntr).getURI().toString().startsWith(baseURI))
				instanceNode.setAttribute("rdf:about",it.get(instanceCntr).getURI().toString().substring(baseURI.length())) ;
			else
				instanceNode.setAttribute("rdf:about",it.get(instanceCntr).getURI().toString()) ;
			
			Element typeNode = xmlDoc.createElement("rdf:type");
			
			if(it.get(instanceCntr).getType().getURI().toString().startsWith(baseURI))
				typeNode.setAttribute("rdf:resource",it.get(instanceCntr).getType().getURI().toString().substring(baseURI.length())) ;
			else
				typeNode.setAttribute("rdf:resource",it.get(instanceCntr).getType().getURI().toString()) ;
			
			instanceNode.appendChild(typeNode);
			
			rdfNode.appendChild(instanceNode);
			
		}
		
		xmlDoc.appendChild(rdfNode);
		
		
		StringWriter output = new StringWriter();

	    Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    transformer.transform(new DOMSource(xmlDoc), new StreamResult(output));

	    result = output.toString();
	    
		return result;
	}
	
}

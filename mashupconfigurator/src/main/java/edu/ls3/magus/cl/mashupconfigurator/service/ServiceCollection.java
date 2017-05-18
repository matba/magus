package edu.ls3.magus.cl.mashupconfigurator.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.ls3.magus.cl.contextmanager.basic.Condition;
import edu.ls3.magus.cl.contextmanager.basic.Instance;
import edu.ls3.magus.cl.contextmanager.basic.InstanceType;
import edu.ls3.magus.cl.contextmanager.basic.InstanceTypeMap;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstance;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstanceS;
import edu.ls3.magus.cl.contextmanager.basic.StateFactType;
import edu.ls3.magus.cl.contextmanager.basic.StateFactTypeMap;
import edu.ls3.magus.cl.contextmanager.context.ContextModel;

import edu.ls3.magus.utility.UtilityClass;





public class ServiceCollection {
	private List<Service> services;
	
	public ServiceCollection()
	{
		setServices(new ArrayList<Service>());
	}
	
	public ServiceCollection( List<Service> services)
	{
		setServices(services);
	}

	
	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}
	
	
	
	public static Service readServiceOld(File fXmlFile, StateFactTypeMap statefacttypes, InstanceTypeMap types,List<URI> invInputList, Map<URI,Instance>  invInpMap,Service invocationService) throws Exception {
		
		//File fXmlFile = new File("D:\\tt\\owlintact2\\requestShipping.owl");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		
		String baseURI = "";
		
		String rootExpression = "//*[name()='rdf:RDF']";
		XPath xPath =  XPathFactory.newInstance().newXPath();
		Node root = (Node) xPath.compile(rootExpression).evaluate(doc, XPathConstants.NODE);
		baseURI = root.getAttributes().getNamedItem("xml:base").getNodeValue();
		//System.out.println("Base URI: " +baseURI);
		
		
		
		
		
		
		String serviceName ="empty" ;
		
		
		
		String serviceNameExpression = "//*[name()='rdf:RDF']//*[name()='service:Service']";
		
		
		xPath =  XPathFactory.newInstance().newXPath();
		Node service = (Node) xPath.compile(serviceNameExpression).evaluate(doc, XPathConstants.NODE);
		serviceName = service.getAttributes().getNamedItem("rdf:ID").getNodeValue();
		//System.out.println("Service Name: " +serviceName);
		
		
		Map<URI,Instance> inputs  = new HashMap<URI,Instance>();
		Map<URI,Instance> outputs = new HashMap<URI,Instance>();
		List<URI> inputList = new ArrayList<URI>();
		List<URI> outputList = new ArrayList<URI>();
		Map<URI,Instance> vars= new HashMap<URI,Instance>();
		List<URI> varList = new ArrayList<URI>();
		Map<URI,Instance> serviceVars = new HashMap<URI, Instance>();
		
		if(invInputList!=null){
			inputs.putAll(invInpMap);
			inputList.addAll(invInputList);
			
			
		}
		
		String serviceInputsExpression = "/*[name()='rdf:RDF']/*[name()='service:Service']/*[name()='service:describedBy']/*[name()='process:AtomicProcess']/*[name()='process:hasInput']/*[name()='process:Input']";
		xPath =  XPathFactory.newInstance().newXPath();
		NodeList serviceInpList = (NodeList) xPath.compile(serviceInputsExpression).evaluate(doc, XPathConstants.NODESET);
		for(int cnt =0; cnt < serviceInpList.getLength(); cnt++){
			Node curNode = serviceInpList.item(cnt);
			String nodeID = curNode.getAttributes().getNamedItem("rdf:ID").getNodeValue();
			//System.out.println("Input ID: " +nodeID);
			URI paramtypeURI =null;
			NodeList curChildNodes = curNode.getChildNodes();
			for(int ccnt =0 ; ccnt<curChildNodes.getLength(); ccnt++)
				if(curChildNodes.item(ccnt).getNodeName().equals("process:parameterType"))
				{
					Node paramTypeNode = curChildNodes.item(ccnt);
					 paramtypeURI = new URI(paramTypeNode.getTextContent().trim());
					//.out.println("Input Type:" + paramtypeURI.toString());
					break;
				}
			InstanceType paramType = types.addORGet(paramtypeURI);
			Instance newInput = new Instance(paramType, nodeID, new URI( baseURI+"#"+nodeID));
			inputs.put(new URI( baseURI+"#"+nodeID),newInput);
			inputList.add(new URI( baseURI+"#"+nodeID));
			
		}
		
		String serviceOutputssExpression = "/*[name()='rdf:RDF']/*[name()='service:Service']/*[name()='service:describedBy']/*[name()='process:AtomicProcess']/*[name()='process:hasOutput']/*[name()='process:Output']";
		xPath =  XPathFactory.newInstance().newXPath();
		NodeList serviceOutList = (NodeList) xPath.compile(serviceOutputssExpression).evaluate(doc, XPathConstants.NODESET);
		for(int cnt =0; cnt < serviceOutList.getLength(); cnt++){
			Node curNode = serviceOutList.item(cnt);
			String nodeID = curNode.getAttributes().getNamedItem("rdf:ID").getNodeValue();
			//System.out.println("Output ID: " +nodeID);
			URI paramtypeURI =null;
			NodeList curChildNodes = curNode.getChildNodes();
			for(int ccnt =0 ; ccnt<curChildNodes.getLength(); ccnt++)
				if(curChildNodes.item(ccnt).getNodeName().equals("process:parameterType"))
				{
					Node paramTypeNode = curChildNodes.item(ccnt);
					 paramtypeURI = new URI(paramTypeNode.getTextContent().trim());
					//System.out.println("Input Type:" + paramtypeURI.toString());
					break;
				}
			InstanceType paramType = types.addORGet(paramtypeURI);
			Instance newOutput = new Instance(paramType, nodeID, new URI( baseURI+"#"+nodeID));
			outputs.put(new URI( baseURI+"#"+nodeID),newOutput);
			outputList.add(new URI( baseURI+"#"+nodeID));
		}
		
		
		//Add Local Vars
		String serviceLocalVarsExpression = "/*[name()='rdf:RDF']/*[name()='service:Service']/*[name()='service:describedBy']/*[name()='process:AtomicProcess']/*[name()='process:hasPrecondition']/*[name()='expr:SWRL-Condition']//*/*[name()='swrl:AtomList']/*[name()='rdf:first']/*[name()='swrl:ClassAtom']";
		xPath =  XPathFactory.newInstance().newXPath();
		NodeList serviceLVList = (NodeList) xPath.compile(serviceLocalVarsExpression).evaluate(doc, XPathConstants.NODESET);
		
		for(int cnt=0; cnt< serviceLVList.getLength(); cnt++){
			Node curNode = serviceLVList.item(cnt);
			String varNodeExpression ="*[name()='swrl:argument1']/*[name()='swrl:Variable']";
			xPath =  XPathFactory.newInstance().newXPath();
			Node varNode = (Node) xPath.compile(varNodeExpression).evaluate(curNode, XPathConstants.NODE);
			if(varNode==null)
				continue;
			String nodeID = varNode.getAttributes().getNamedItem("rdf:ID").getNodeValue();
			
			//System.out.println("Var ID: " +nodeID);
			
			String classPredicateNodeExpression ="*[name()='swrl:classPredicate']";
			xPath =  XPathFactory.newInstance().newXPath();
			Node classPredicateNode = (Node) xPath.compile(classPredicateNodeExpression).evaluate(curNode, XPathConstants.NODE);
			String vartypeURI = classPredicateNode.getAttributes().getNamedItem("rdf:resource").getNodeValue();
			vartypeURI = UtilityClass.Clean(vartypeURI);
			if(!UtilityClass.IsAbsolute(vartypeURI))
			{
				vartypeURI = baseURI + vartypeURI;		
			}
		
			InstanceType varType = types.addORGet(new URI(vartypeURI));
			Instance newVar = new Instance(varType, nodeID, new URI( baseURI+"#"+nodeID));
			//System.out.println(baseURI+"#"+nodeID);
			vars.put(new URI( baseURI+"#"+nodeID),newVar);
			varList.add(new URI( baseURI+"#"+nodeID));
		}
		
		serviceVars.putAll(inputs);
		serviceVars.putAll(outputs);
		serviceVars.putAll(vars);
		
		//Get Preconditions
		List<StateFactInstanceS> precList = new ArrayList<StateFactInstanceS>();
		String servicePrecExpression = "/*[name()='rdf:RDF']/*[name()='service:Service']/*[name()='service:describedBy']/*[name()='process:AtomicProcess']/*[name()='process:hasPrecondition']/*[name()='expr:SWRL-Condition']//*/*[name()='swrl:AtomList']/*[name()='rdf:first']/*[name()='swrl:IndividualPropertyAtom']";
		xPath =  XPathFactory.newInstance().newXPath();
		NodeList servicePrecList = (NodeList) xPath.compile(servicePrecExpression).evaluate(doc, XPathConstants.NODESET);
		for(int cnt=0; cnt< servicePrecList.getLength(); cnt++){
			Node curNode = servicePrecList.item(cnt);
			String factURI =null;
			String param1URI = null;
			String param2URI = null;
			boolean isNot = false;
			
			NodeList curChildNodes = curNode.getChildNodes();
			for(int ccnt =0 ; ccnt<curChildNodes.getLength(); ccnt++){
				if(curChildNodes.item(ccnt).getNodeName().equals("swrl:propertyPredicate")){
					factURI = curChildNodes.item(ccnt).getAttributes().getNamedItem("rdf:resource").getNodeValue();
					factURI=UtilityClass.Clean(factURI);
				}
				if(curChildNodes.item(ccnt).getNodeName().equals("swrl:argument1")){
					if(curChildNodes.item(ccnt).getAttributes().getNamedItem("rdf:resource")==null){
						param1URI = curChildNodes.item(ccnt).getChildNodes().item(1).getAttributes().getNamedItem("rdf:ID").getNodeValue();
					}else{
						param1URI = curChildNodes.item(ccnt).getAttributes().getNamedItem("rdf:resource").getNodeValue();
						param1URI=UtilityClass.Clean(param1URI);
					}
				}
					
				if(curChildNodes.item(ccnt).getNodeName().equals("swrl:argument2"))
				{
					if(curChildNodes.item(ccnt).getAttributes().getNamedItem("rdf:resource")==null){
						param2URI = curChildNodes.item(ccnt).getChildNodes().item(1).getAttributes().getNamedItem("rdf:ID").getNodeValue();
					}else{
						param2URI = curChildNodes.item(ccnt).getAttributes().getNamedItem("rdf:resource").getNodeValue();
						param2URI=UtilityClass.Clean(param2URI);
					}
				}
			}
			
			if((factURI ==null)||(param1URI ==null)||(param2URI ==null))
				throw new Exception("Wrong SWRL Fact");
			
			if(!UtilityClass.IsAbsolute(factURI)){
				if(factURI.startsWith("!"))
				{
					isNot =true;
					factURI= factURI.substring(1);
				}
				factURI = baseURI+"#"+factURI;
				
			}else{
				int t = factURI.lastIndexOf("#");
				if((factURI.length()>t+2) && factURI.charAt(t+1)=='!'){
					isNot =true;
					factURI = factURI.substring(0,t+1) + factURI.substring(t+2);
				}
			}
			
			if(!UtilityClass.IsAbsolute(param1URI))
				param1URI = baseURI+"#"+param1URI;
			if(!UtilityClass.IsAbsolute(param2URI))
				param2URI = baseURI+"#"+param2URI;
			
			
			if(!serviceVars.containsKey(new URI(param1URI)) || !serviceVars.containsKey(new URI(param2URI)))
				throw new Exception("Wrong SWRL Fact");
			Instance param1Instance = serviceVars.get(new URI(param1URI));
			Instance param2Instance = serviceVars.get(new URI(param2URI));
			Instance[] paramArray = {param1Instance,param2Instance};
			StateFactType factType=null;
			if(statefacttypes.exists(new URI(factURI))){
				factType = statefacttypes.get(new URI(factURI));		
			}else
			{
				InstanceType[] paramTypeArray = {param1Instance.getType(),param2Instance.getType()};
				factType = new StateFactType(UtilityClass.getLocalName(new URI( factURI)),new URI( factURI) , paramTypeArray);
				statefacttypes.add(factType);
			}
			StateFactInstanceS stateFact = new StateFactInstanceS( new StateFactInstance(factType,  paramArray),isNot);
			precList.add(stateFact);
		}
		Condition precondition = new Condition(  precList);
		
		
		//Get Effectconditions
		List<StateFactInstanceS> effList = new ArrayList<StateFactInstanceS>();
		String serviceeffExpression = "/*[name()='rdf:RDF']/*[name()='service:Service']/*[name()='service:describedBy']/*[name()='process:AtomicProcess']/*[name()='process:hasResult']/*[name()='expr:SWRL-Condition']//*/*[name()='swrl:AtomList']/*[name()='rdf:first']/*[name()='swrl:IndividualPropertyAtom']";
		xPath =  XPathFactory.newInstance().newXPath();
		NodeList serviceeffList = (NodeList) xPath.compile(serviceeffExpression).evaluate(doc, XPathConstants.NODESET);
		for(int cnt=0; cnt< serviceeffList.getLength(); cnt++){
			Node curNode = serviceeffList.item(cnt);
			String factURI =null;
			String param1URI = null;
			String param2URI = null;
			boolean isNot = false;
			
			NodeList curChildNodes = curNode.getChildNodes();
			for(int ccnt =0 ; ccnt<curChildNodes.getLength(); ccnt++){
				if(curChildNodes.item(ccnt).getNodeName().equals("swrl:propertyPredicate")){
					factURI = curChildNodes.item(ccnt).getAttributes().getNamedItem("rdf:resource").getNodeValue();
					factURI = UtilityClass.Clean(factURI);
				}
				if(curChildNodes.item(ccnt).getNodeName().equals("swrl:argument1")){
					if(curChildNodes.item(ccnt).getAttributes().getNamedItem("rdf:resource")==null){
						param1URI = curChildNodes.item(ccnt).getChildNodes().item(1).getAttributes().getNamedItem("rdf:ID").getNodeValue();
					}else{
						param1URI = curChildNodes.item(ccnt).getAttributes().getNamedItem("rdf:resource").getNodeValue();
						param1URI = UtilityClass.Clean(param1URI);
					}
				}
					
				if(curChildNodes.item(ccnt).getNodeName().equals("swrl:argument2"))
				{
					if(curChildNodes.item(ccnt).getAttributes().getNamedItem("rdf:resource")==null){
						param2URI = curChildNodes.item(ccnt).getChildNodes().item(1).getAttributes().getNamedItem("rdf:ID").getNodeValue();
					}else{
						param2URI = curChildNodes.item(ccnt).getAttributes().getNamedItem("rdf:resource").getNodeValue();
						param2URI = UtilityClass.Clean(param2URI);
					}
				}
			}
			
			if((factURI ==null)||(param1URI ==null)||(param2URI ==null))
				throw new Exception("Wrong SWRL Fact");
			if(!UtilityClass.IsAbsolute(factURI)){
				if(factURI.startsWith("!"))
				{
					isNot =true;
					factURI= factURI.substring(1);
				}
				factURI = baseURI+"#"+factURI;
				
			}else{
				int t = factURI.lastIndexOf("#");
				if((factURI.length()>t+2) && factURI.charAt(t+1)=='!'){
					isNot =true;
					factURI = factURI.substring(0,t+1) + factURI.substring(t+2);
				}
			}
			if(!UtilityClass.IsAbsolute(param1URI))
				param1URI = baseURI+"#"+param1URI;
			if(!UtilityClass.IsAbsolute(param2URI))
				param2URI = baseURI+"#"+param2URI;
			
			
			if(!serviceVars.containsKey(new URI(param1URI)) || !serviceVars.containsKey(new URI(param2URI)))
				throw new Exception("Wrong SWRL Fact");
			Instance param1Instance = serviceVars.get(new URI(param1URI));
			Instance param2Instance = serviceVars.get(new URI(param2URI));
			Instance[] paramArray = {param1Instance,param2Instance};
			StateFactType factType=null;
			if(statefacttypes.exists(new URI(factURI))){
				factType = statefacttypes.get(new URI(factURI));
				
			}else
			{
				InstanceType[] paramTypeArray = {param1Instance.getType(),param2Instance.getType()};
				factType = new StateFactType(UtilityClass.getLocalName(new URI( factURI)),new URI( factURI) , paramTypeArray);
				statefacttypes.add(factType);
			}
			StateFactInstanceS stateFact = new StateFactInstanceS( new StateFactInstance(factType,  paramArray), isNot);
			effList.add(stateFact);
		}
		Condition effectcondition = new Condition(  effList);
		
		
		Service s = new Service(serviceName, precondition, effectcondition,inputs, outputs,vars,new HashMap<URI, Instance>() , inputList,outputList,varList,new ArrayList<URI>(),invocationService,"");
		return s;
	}
	public Map<Service,Map<URI, Instance>> getExecutableServices(Condition curCondition,ContextModel cm) {
		
		Map<Service,Map<URI, Instance> > result = new HashMap<Service,Map<URI, Instance>>();
		for(Service s: getServices())
		{
			//System.out.println(s.getName());
			//s.printPrecondition();
			//System.out.println("------------");
			if(s.getName().equals("servd812f5ddService"))
				System.out.println(s.getName());
			Map<URI, Instance> m  =s.getExecutionAssignment(cm, curCondition);
			if(m!=null)
				result.put(s,m);
		}		
		return result;
	}
	public List<StateFactType> getAllUsedFactTypes(){
		List<StateFactType> result =new ArrayList<StateFactType>();
		
		for(Service s: getServices()){
			for(StateFactInstanceS sfis:s.getPrecondition().getConditions() ) 
				if(!result.contains(sfis.getStateFactInstance().getType()))
					result.add(sfis.getStateFactInstance().getType());
			for(StateFactInstanceS sfis:s.getPostcondition().getConditions() ) 
				if(!result.contains(sfis.getStateFactInstance().getType()))
					result.add(sfis.getStateFactInstance().getType());
			if(s.getReceiveService()!=null){
				s= s.getReceiveService();
				for(StateFactInstanceS sfis:s.getPrecondition().getConditions() ) 
					if(!result.contains(sfis.getStateFactInstance().getType()))
						result.add(sfis.getStateFactInstance().getType());
				for(StateFactInstanceS sfis:s.getPostcondition().getConditions() ) 
					if(!result.contains(sfis.getStateFactInstance().getType()))
						result.add(sfis.getStateFactInstance().getType());
			}
		}
		return result;
		
		
	}
	public Service getServiceByName(String string) {
		
		
		for(Service s : getServices())
			if(s.getName().equals(string))
				return s;
		return null;
	}
	
	public Service getServiceByURI(String uri) {
		
		
		for(Service s : getServices())
			if(s.getURI().equals(uri))
				return s;
		return null;
	}

	public ServiceCollection FilterByUsedType(List<InstanceType> insType) {
		
		ServiceCollection result = new ServiceCollection();
		
		for(Service s:getServices())
		{
			if(s.useOnlyTypes(insType))
				result.getServices().add(s);
		}
		
		return result;
		

	}
	public List<InstanceType> GetAllNotPreconditionVars(List<InstanceType> insType) {
		
		List<InstanceType> result =  new ArrayList<InstanceType>();
		
		for(Service s:getServices())
		{
			for(StateFactInstanceS c: s.getPrecondition().getConditions()){
				if(c.isNot()){
					if(!result.contains(c.getStateFactInstance().getParams()[0].getType()) && !insType.contains( c.getStateFactInstance().getParams()[0].getType())){
						result.add( c.getStateFactInstance().getParams()[0].getType());
					}
					if(!result.contains(c.getStateFactInstance().getParams()[1].getType()) && !insType.contains( c.getStateFactInstance().getParams()[1].getType())){
						result.add( c.getStateFactInstance().getParams()[1].getType());
					}
				}
				
			}
			
		}
		
		return result;
		

	}

	public List<Service> FindEquivalentServices(Service inputService, Map<String, Boolean> serviceAvailabilty) {
		
		List<Service> result = new ArrayList<Service>();
		
		for(Service sv: getServices()){
			if(serviceAvailabilty.get(sv.getURI())&& inputService.isEquivalent(sv)){
				result.add(sv);
			}
		}
		
		return result;
	}

	public ServiceCollection FilterByAvailability(Map<String, Boolean> serviceAvailabilty) {
		
		ServiceCollection result = new ServiceCollection();
		
		for(Service s:getServices())
		{
			if(serviceAvailabilty.get(s.getURI()))
				result.getServices().add(s);
		}
		
		return result;
	}
	
	public void writeToDirectory(String directoryAddress) throws ParserConfigurationException, IOException, TransformerException{
		if(!directoryAddress.endsWith("/"))
			directoryAddress = directoryAddress+"/";
		
		for(Service s: getServices()){
			String serialized = s.serializeToOwls();
			UtilityClass.writeFile(new File(directoryAddress+s.getName()+".xml"), serialized);
		}
		
	}
}

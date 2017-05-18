package edu.ls3.magus.cl.mashupconfigurator.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.ls3.magus.cl.contextmanager.basic.Condition;
import edu.ls3.magus.cl.contextmanager.basic.Instance;
import edu.ls3.magus.cl.contextmanager.basic.InstanceType;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstance;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstanceS;
import edu.ls3.magus.cl.contextmanager.basic.StateFactType;
import edu.ls3.magus.cl.contextmanager.context.ContextModel;
import edu.ls3.magus.utility.UtilityClass;





public class Service {
	private String name;
	private Condition precondition;
	private Condition postcondition;
	private Map<URI,Instance> inputs  = new HashMap<URI,Instance>();
	private Map<URI,Instance> outputs = new HashMap<URI,Instance>();
	private Map<URI,Instance> vars= new HashMap<URI,Instance>();
	private Map<URI,Instance> contextVars= new HashMap<URI,Instance>();
	private List<URI> inputList = new ArrayList<URI>();
	private List<URI> outputList = new ArrayList<URI>();
	private List<URI> varList = new ArrayList<URI>();
	private List<URI> contextVarList = new ArrayList<URI>();
	private Service receiveService;
	private Service invocationService;
	private String uri;
	
	public Condition getPrecondition() {
		return precondition;
	}

	public void setPrecondition(Condition precondition) {
		this.precondition = precondition;
	}

	public Condition getPostcondition() {
		return postcondition;
	}

	public void setPostcondition(Condition postcondition) {
		this.postcondition = postcondition;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Service(String name, Condition precondition, Condition postcondition,Map<URI,Instance> inputs,Map<URI,Instance> outputs,Map<URI,Instance> vars,Map<URI,Instance> contextVars, List<URI> inputList, List<URI> outputList, List<URI> varList,List<URI> contextVarList, Service invocationService,String uri){
		this.setName(name);
		this.precondition=precondition;
		this.postcondition=postcondition;
		this.setInputs(inputs);
		this.setOutputs(outputs);
		this.setVars(vars);
		this.setContextVars(contextVars);
		this.setInputList(inputList);
		this.setOutputList(outputList);
		this.setVarList(varList);
		this.setContextVarList(contextVarList);
		this.receiveService = null;
		this.invocationService = invocationService;
		this.uri =uri;
	}

	/**
	 * Compares two services to see if one should be executed before the other one
	 * @param s the other service
	 * @return return a positive value if the this service should be executed before
	 * input service, return zero if they are not comparable and return a negative 
	 * value if the input service should be executed before this service
	 */
	public int compare(Service s){
		if(this.getPostcondition().haveIntersection(s.getPrecondition()))
			return 1;
		if(this.getPrecondition().haveIntersection(s.getPostcondition()))
			return -1;
		return 0;
		
	}
	
	

	public Map<URI,Instance> getInputs() {
		return inputs;
	}

	public void setInputs(Map<URI,Instance> inputs) {
		this.inputs = inputs;
	}

	public Map<URI,Instance> getOutputs() {
		return outputs;
	}

	public void setOutputs(Map<URI,Instance> outputs) {
		this.outputs = outputs;
	}

	public List<URI> getInputList() {
		return inputList;
	}

	public void setInputList(List<URI> inputList) {
		this.inputList = inputList;
	}

	public List<URI> getOutputList() {
		return outputList;
	}

	public void setOutputList(List<URI> outputList) {
		this.outputList = outputList;
	}

	public Map<URI,Instance> getVars() {
		return vars;
	}

	private void setVars(Map<URI,Instance> vars) {
		this.vars = vars;
	}

	public List<URI> getVarList() {
		return varList;
	}

	private void setVarList(List<URI> varList) {
		this.varList = varList;
	}
	
	public Service getReceiveService() {
		return receiveService;
	}

	public void setReceiveService(Service recieveService) {
		this.receiveService = recieveService;
	}

	public Service getInvocationService() {
		return invocationService;
	}

	public void setInvocationService(Service invocationService) {
		this.invocationService = invocationService;
	}

	public Map<URI, Instance> getExecutionAssignment(ContextModel cm, Condition curCondition) {
		// Check if there is an assignment to vars in the service for which
		//precondition of the service become subset of curcondition
		return getExecutionAssignmentRec(cm, curCondition, new HashMap<URI, Instance>());
	}
	
	private Map<URI, Instance> getExecutionAssignmentRec(ContextModel cm, Condition curCondition, Map<URI, Instance> assigned){
		URI uri = findUnassignedVar(assigned);
		if(uri==null){
			Condition finalcond =  getPrecondition(assigned);
			if(finalcond.subset(curCondition))
				return assigned;
			else 
				return null;
		}
		else{
//			boolean assignmentfound =false;
			Instance i = getInstanceByURI(uri);
			
			
			List<Instance> possibleAssigments = cm.filterVarsByType(i.getType());
			for(Instance ins: possibleAssigments){
				HashMap<URI, Instance> newAssingment = new HashMap<URI, Instance>();
				newAssingment.putAll(assigned);
				newAssingment.put(uri, ins);
				Map<URI, Instance>  r = getExecutionAssignmentRec(cm,curCondition,newAssingment);
				if(r!=null)
					return r;
				
			}
		}
		return null;
	}

	private Instance getInstanceByURI(URI uri) {
		if(inputList.contains(uri))
			return inputs.get(uri);
		if(varList.contains(uri))
			return vars.get(uri);
		if(outputList.contains(uri))
			return outputs.get(uri);
		if(contextVarList.contains(uri))
			return contextVars.get(uri);
		return null;
	}

	private Condition getPrecondition(Map<URI, Instance> assigned) {
		
		List<StateFactInstanceS> result = new ArrayList<StateFactInstanceS>();
		for(StateFactInstanceS sfis: getPrecondition().getConditions()){
			StateFactInstance sfi = sfis.getStateFactInstance();
			List<Instance> newParamsList = new ArrayList<Instance>();
			Instance[] newParams;
			for(int cnt=0;cnt<sfi.getParams().length;cnt++)
				newParamsList.add(assigned.get(sfi.getParams()[cnt].getURI()));
			
			newParams = newParamsList.toArray(new Instance[0]);
			StateFactInstance newSfi = new StateFactInstance(sfi.getType(), newParams);
			result.add(new StateFactInstanceS(newSfi, sfis.isNot()));
		}
		
		return new Condition( result);
	}
	
	private Condition getPostcondition(Map<URI, Instance> assigned) {
		
		List<StateFactInstanceS> result = new ArrayList<StateFactInstanceS>();
		for(StateFactInstanceS sfis: getPostcondition().getConditions()){
			StateFactInstance sfi = sfis.getStateFactInstance();
			List<Instance> newParamsList = new ArrayList<Instance>();
			Instance[] newParams;
			for(int cnt=0;cnt<sfi.getParams().length;cnt++)
				newParamsList.add(assigned.get(sfi.getParams()[cnt].getURI()));
			
			newParams = newParamsList.toArray(new Instance[0]);
			StateFactInstance newSfi = new StateFactInstance(sfi.getType(), newParams);
			result.add(new StateFactInstanceS(newSfi, sfis.isNot()));
		}
		
		return new Condition( result);
	}

	private URI findUnassignedVar(Map<URI, Instance> assigned) {
		
		for(URI u: inputList)
			if(assigned.get(u)==null)
				return u;
		for(URI u: varList)
			if(assigned.get(u)==null)
				return u;
		for(URI u: outputList)
			if(assigned.get(u)==null)
				return u;
		return null;
	}

	public Condition getContextAfterExc(Condition curCondition, Map<URI, Instance> map) {
		
		Condition result = new Condition(curCondition.getConditions());
		//Map<URI, Instance> allVars = getAllVars();
		Condition x=getPostcondition(map);
		for(StateFactInstanceS sfis:x.getConditions())
		{
			if(result.hasFact(sfis))
				continue;
			if(result.hasNotFact(sfis)){
				result.removeNot(sfis);
				continue;
			}	
			
			result.getConditions().add(sfis);
		}
		return result;
	}

	private Map<URI, Instance> getAllVars() {
		
		HashMap<URI, Instance> result = new HashMap<URI, Instance>();
		result.putAll(inputs);
		result.putAll(outputs);
		result.putAll(vars);
		return result;
	}
	@SuppressWarnings("unused")
	private Map<URI, Instance> getAllVarsWithContext() {
		
		HashMap<URI, Instance> result = new HashMap<URI, Instance>();
		result.putAll(inputs);
		result.putAll(outputs);
		result.putAll(vars);
		return result;
	}
	
	
	public void printPrecondition(){
		for(StateFactInstanceS sfis: getPrecondition().getConditions())
		{
			System.out.println(sfis.getStateFactInstance().getType().getTypeName());
			
		}
	}

	
	public static List<Service> parseService(String owlFile, ContextModel cm) throws Exception{
		
		List<Service> result = new ArrayList<Service>();
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(owlFile));
		Document doc = dBuilder.parse(is);
		
		String baseURI = "";
		
		String rootExpression = "//*[name()='rdf:RDF']";
		XPath xPath =  XPathFactory.newInstance().newXPath();
		Node root = (Node) xPath.compile(rootExpression).evaluate(doc, XPathConstants.NODE);
		baseURI = root.getAttributes().getNamedItem("xml:base").getNodeValue();
		//System.out.println("Base URI: " +baseURI);
		
		
		
		
		
		
		
		
		
		
		String serviceNameExpression = "//*[name()='rdf:RDF']//*[name()='service:Service']";
		
		
		xPath =  XPathFactory.newInstance().newXPath();
		NodeList serviceList = (NodeList) xPath.compile(serviceNameExpression).evaluate(doc, XPathConstants.NODESET);
		
		if(serviceList.getLength()>2)
			throw new Exception("Unexpected Service Annotation File ");
		
		//boolean hasCallback = false;
		
//		if(serviceList.getLength()>1)
//			hasCallback = true;
		
		for(int svcntr=0; svcntr < serviceList.getLength(); svcntr++ ){
			Node service = serviceList.item(svcntr);
			String serviceName ="empty" ;
			serviceName = service.getAttributes().getNamedItem("rdf:ID").getNodeValue();
			//System.out.println("Service Name: " +serviceName);
			
			if(!serviceName.endsWith("Callback")){
			
				Service s = readServicePart(service,serviceName, baseURI, cm, null,null, null);
				result.add( s);
			}
		}
		
		
		for(int svcntr=0; svcntr < serviceList.getLength(); svcntr++ ){
			Node service = serviceList.item(svcntr);
			String serviceName ="empty" ;
			serviceName = service.getAttributes().getNamedItem("rdf:ID").getNodeValue();
			//System.out.println("Service Name: " +serviceName);
			
			if(serviceName.endsWith("Callback")){
			
				Service s = readServicePart(service,serviceName, baseURI, cm, result.get(0),result.get(0).getInputList(),result.get(0).getInputs());
				//result.add( s);
				result.get(0).setReceiveService(s);
			}
		}
		
		return result;
		
	}
	
	
	private static Service readServicePart(Node service, String serviceName,String baseURI ,ContextModel cm, Service invocationService, List<URI> invInputList, Map<URI, Instance> invInpMap)throws Exception{
		
		
		XPath xPath =  XPathFactory.newInstance().newXPath();
		Map<URI,Instance> inputs  = new HashMap<URI,Instance>();
		Map<URI,Instance> outputs = new HashMap<URI,Instance>();
		List<URI> inputList = new ArrayList<URI>();
		List<URI> outputList = new ArrayList<URI>();
		Map<URI,Instance> vars= new HashMap<URI,Instance>();
		List<URI> varList = new ArrayList<URI>();
		Map<URI,Instance> contextVars= new HashMap<URI,Instance>();
		List<URI> contextVarList = new ArrayList<URI>();
		Map<URI,Instance> serviceVars = new HashMap<URI, Instance>();
		
		if(invInputList!=null){
			inputs.putAll(invInpMap);
			inputList.addAll(invInputList);
			
			
		}
		
		String serviceInputsExpression = ".//*[name()='service:describedBy']/*[name()='process:AtomicProcess']/*[name()='process:hasInput']/*[name()='process:Input']";
		xPath =  XPathFactory.newInstance().newXPath();
		NodeList serviceInpList = (NodeList) xPath.compile(serviceInputsExpression).evaluate(service, XPathConstants.NODESET);
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
			InstanceType paramType = cm.getInstanceTypes().get(paramtypeURI);
			Instance newInput = new Instance(paramType, nodeID, new URI( baseURI+"#"+nodeID));
			inputs.put(new URI( baseURI+"#"+nodeID),newInput);
			inputList.add(new URI( baseURI+"#"+nodeID));
			
		}
		
		String serviceOutputssExpression = ".//*[name()='service:describedBy']/*[name()='process:AtomicProcess']/*[name()='process:hasOutput']/*[name()='process:Output']";
		xPath =  XPathFactory.newInstance().newXPath();
		NodeList serviceOutList = (NodeList) xPath.compile(serviceOutputssExpression).evaluate(service, XPathConstants.NODESET);
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
			InstanceType paramType = cm.getInstanceTypes().get(paramtypeURI);
			Instance newOutput = new Instance(paramType, nodeID, new URI( baseURI+"#"+nodeID));
			outputs.put(new URI( baseURI+"#"+nodeID),newOutput);
			outputList.add(new URI( baseURI+"#"+nodeID));
		}
		
		
		//Add Local Vars
		String serviceLocalVarsExpression = ".//*[name()='service:describedBy']/*[name()='process:AtomicProcess']/*[name()='process:hasPrecondition']/*[name()='expr:SWRL-Condition']//*/*[name()='swrl:AtomList']/*[name()='rdf:first']/*[name()='swrl:ClassAtom']";
		xPath =  XPathFactory.newInstance().newXPath();
		NodeList serviceLVList = (NodeList) xPath.compile(serviceLocalVarsExpression).evaluate(service, XPathConstants.NODESET);
		
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
		
			InstanceType varType = cm.getInstanceTypes().get(new URI(vartypeURI));
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
		String servicePrecExpression = ".//*[name()='service:describedBy']/*[name()='process:AtomicProcess']/*[name()='process:hasPrecondition']/*[name()='expr:SWRL-Condition']//*/*[name()='swrl:AtomList']/*[name()='rdf:first']/*[name()='swrl:IndividualPropertyAtom']";
		xPath =  XPathFactory.newInstance().newXPath();
		NodeList servicePrecList = (NodeList) xPath.compile(servicePrecExpression).evaluate(service, XPathConstants.NODESET);
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
			
			
			
			
			Instance param1Instance = null;
			if(serviceVars.containsKey(new URI(param1URI))){
				param1Instance = serviceVars.get(new URI(param1URI));
			}
			else 
				if(cm.getInstances().contains(new URI(param1URI))){
					param1Instance = cm.getInstances().get(new URI(param1URI));
					contextVarList.add(new URI(param1URI));
					contextVars.put(new URI(param1URI), cm.getInstances().get(new URI(param1URI)));
				}
			
			Instance param2Instance = null;
			if(serviceVars.containsKey(new URI(param2URI))){
				param2Instance = serviceVars.get(new URI(param2URI));
			}
			else 
				if(cm.getInstances().contains(new URI(param2URI))){
					param2Instance = cm.getInstances().get(new URI(param2URI));
					contextVarList.add(new URI(param2URI));
					contextVars.put(new URI(param2URI), cm.getInstances().get(new URI(param2URI)));
					
				}
			
			if((param1Instance==null) || (param2Instance==null))
				throw new Exception("Wrong SWRL Fact");
			
			Instance[] paramArray = {param1Instance,param2Instance};
			StateFactType factType=null;
			if(cm.getFactTypes().exists(new URI(factURI))){
				factType = cm.getFactTypes().get(new URI(factURI));		
			}else
			{
//				InstanceType[] paramTypeArray = {param1Instance.getType(),param2Instance.getType()};
//				factType = new StateFactType(UtilityClass.getLocalName(new URI( factURI)),new URI( factURI) , paramTypeArray);
//				statefacttypes.add(factType);
				throw new Exception("Fact Type Used in "+ serviceName +" does not exists!");
			}
			StateFactInstanceS stateFact = new StateFactInstanceS( new StateFactInstance(factType,  paramArray),isNot);
			precList.add(stateFact);
		}
		Condition precondition = new Condition(  precList);
		
		
		//Get Effectconditions
		List<StateFactInstanceS> effList = new ArrayList<StateFactInstanceS>();
		String serviceeffExpression = ".//*[name()='service:describedBy']/*[name()='process:AtomicProcess']/*[name()='process:hasResult']/*[name()='expr:SWRL-Condition']//*/*[name()='swrl:AtomList']/*[name()='rdf:first']/*[name()='swrl:IndividualPropertyAtom']";
		xPath =  XPathFactory.newInstance().newXPath();
		NodeList serviceeffList = (NodeList) xPath.compile(serviceeffExpression).evaluate(service, XPathConstants.NODESET);
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
			
			Instance param1Instance = null;
			if(serviceVars.containsKey(new URI(param1URI))){
				param1Instance = serviceVars.get(new URI(param1URI));
				
			}
			else 
				if(cm.getInstances().contains(new URI(param1URI))){
					param1Instance = cm.getInstances().get(new URI(param1URI));
					contextVarList.add(new URI(param1URI));
					contextVars.put(new URI(param1URI), cm.getInstances().get(new URI(param1URI)));
				}
			
			Instance param2Instance = null;
			if(serviceVars.containsKey(new URI(param2URI))){
				param2Instance = serviceVars.get(new URI(param2URI));
			}
			else 
				if(cm.getInstances().contains(new URI(param2URI))){
					param2Instance = cm.getInstances().get(new URI(param2URI));
					contextVarList.add(new URI(param2URI));
					contextVars.put(new URI(param2URI), cm.getInstances().get(new URI(param2URI)));
				}
			
			if((param1Instance==null) || (param2Instance==null))
				throw new Exception("Wrong SWRL Fact");
			 
			
			Instance[] paramArray = {param1Instance,param2Instance};
			StateFactType factType=null;
			if(cm.getFactTypes().exists(new URI(factURI))){
				factType =cm.getFactTypes().get(new URI(factURI));
				
			}else
			{
//				InstanceType[] paramTypeArray = {param1Instance.getType(),param2Instance.getType()};
//				factType = new StateFactType(UtilityClass.getLocalName(new URI( factURI)),new URI( factURI) , paramTypeArray);
//				statefacttypes.add(factType);
				throw new Exception("Fact Type Used in "+ serviceName +" does not exists!");
			}
			StateFactInstanceS stateFact = new StateFactInstanceS( new StateFactInstance(factType,  paramArray), isNot);
			effList.add(stateFact);
		}
		Condition effectcondition = new Condition(  effList);
		
		
		Service s = new Service(serviceName, precondition, effectcondition,inputs, outputs,vars,contextVars, inputList,outputList,varList,contextVarList,invocationService,baseURI);
		return s;
	}

	public String getURI() {
		return uri;
	}

	public void setURI(String uri) {
		this.uri = uri;
	}
	
	public List<InstanceType> getAllUsedTypes(){
		List<InstanceType> result = new ArrayList<InstanceType>();
		
		for(Instance i: this.getAllVars().values())
			if(!result.contains(i.getType()))
				result.add(i.getType());
		
		if(getReceiveService()!=null){
			List<InstanceType> r2 = getReceiveService().getAllUsedTypes();
			UtilityClass.addNewOnes(result,r2);
			
		}
		
		return result;

	}
	
	public boolean useOnlyTypes(List<InstanceType> insList){
		return  UtilityClass.includesAll(insList, getAllUsedTypes());
	}

	public Map<URI,Instance> getContextVars() {
		return contextVars;
	}

	public void setContextVars(Map<URI,Instance> contextVars) {
		this.contextVars = contextVars;
	}

	public List<URI> getContextVarList() {
		return contextVarList;
	}

	public void setContextVarList(List<URI> contextVarList) {
		this.contextVarList = contextVarList;
	}

	public boolean isEquivalent(Service sv) {
		if((sv.getReceiveService()==null)&&(getReceiveService()==null))
			return isEquivalent(new HashMap<URI, URI>(),new HashMap<URI, URI>(), sv);
		if((sv.getReceiveService()!=null)&&(getReceiveService()!=null))
			return isEquivalent(new HashMap<URI, URI>(),new HashMap<URI, URI>(), sv)&& getReceiveService().isEquivalent(new HashMap<URI, URI>(),new HashMap<URI, URI>(), sv.getReceiveService());
		return false;
	}
	
	private boolean isEquivalent(Map<URI,URI> mapping,Map<URI,URI> reverseMapping , Service sv){
		if((getInputList().size()!=sv.getInputList().size())||(getOutputList().size()!=sv.getOutputList().size())||(getPrecondition().getConditions().size()!=sv.getPrecondition().getConditions().size())||(getPostcondition().getConditions().size()!=sv.getPostcondition().getConditions().size()))
			return false;
		
		for(URI curURI:  getInputList()){
			if(mapping.containsKey(curURI))
				continue;
			
			for(URI svCurURI: sv.getInputList()){
				if(!reverseMapping.containsKey(svCurURI)&& getInputs().get(curURI).getType().equals(sv.getInputs().get(svCurURI).getType())){
					
					Map<URI,URI> newMapping = new HashMap<URI, URI>();
					Map<URI,URI> newReverseMapping = new HashMap<URI, URI>();
					newMapping.putAll(mapping);
					newReverseMapping.putAll(newReverseMapping);
					newMapping.put(curURI, svCurURI);
					newReverseMapping.put(svCurURI, curURI);
					if(isEquivalent(newMapping, newReverseMapping, sv))
						return true;
				}
			}
			return false;
		}
		for(URI curURI:  getOutputList()){
			if(mapping.containsKey(curURI))
				continue;
			
			for(URI svCurURI: sv.getOutputList()){
				if(!reverseMapping.containsKey(svCurURI)&& getOutputs().get(curURI).getType().equals(sv.getOutputs().get(svCurURI).getType())){
					
					Map<URI,URI> newMapping = new HashMap<URI, URI>();
					Map<URI,URI> newReverseMapping = new HashMap<URI, URI>();
					newMapping.putAll(mapping);
					newReverseMapping.putAll(newReverseMapping);
					newMapping.put(curURI, svCurURI);
					newReverseMapping.put(svCurURI, curURI);
					if(isEquivalent(newMapping, newReverseMapping, sv))
						return true;
				}
			}
			return false;
		}
		
		for(StateFactInstanceS sfis: getPrecondition().getConditions() ){
			boolean correspondingPreconditionFound = false;
			for(StateFactInstanceS svSfis: sv.getPrecondition().getConditions() ){
				if(sfis.isNot()==svSfis.isNot()){
					StateFactInstance sfi = sfis.getStateFactInstance();
					StateFactInstance svSfi = svSfis.getStateFactInstance();
					
					if(sfi.getType().equals(svSfi.getType())){
						boolean isMatch = true;
						for(int cnt=0; cnt< sfi.getParams().length;cnt++){
							if(mapping.containsKey(sfi.getParams()[cnt].getURI()))
							{
								if( !mapping.get(sfi.getParams()[cnt].getURI()).equals(svSfi.getParams()[cnt].getURI() ))
									isMatch = false;
								
							}
							if(getContextVars().containsKey(sfi.getParams()[cnt].getURI())){
								if( !sfi.getParams()[cnt].getURI().equals(svSfi.getParams()[cnt].getURI() ))
									isMatch = false;
							}
						}
						if(isMatch){
							correspondingPreconditionFound=true;
							break;
						}
					}
					
				
					
				}
			}
			
			if(!correspondingPreconditionFound)
				return false;
		}
		
		for(StateFactInstanceS sfis: getPostcondition().getConditions() ){
			boolean correspondingPreconditionFound = false;
			for(StateFactInstanceS svSfis: sv.getPostcondition().getConditions() ){
				if(sfis.isNot()==svSfis.isNot()){
					StateFactInstance sfi = sfis.getStateFactInstance();
					StateFactInstance svSfi = svSfis.getStateFactInstance();
					
					if(sfi.getType().equals(svSfi.getType())){
						boolean isMatch = true;
						for(int cnt=0; cnt< sfi.getParams().length;cnt++){
							if(mapping.containsKey(sfi.getParams()[cnt].getURI()))
							{
								if( !mapping.get(sfi.getParams()[cnt].getURI()).equals(svSfi.getParams()[cnt].getURI() ))
									isMatch = false;
								
							}
							if(getContextVars().containsKey(sfi.getParams()[cnt].getURI())){
								if( !sfi.getParams()[cnt].getURI().equals(svSfi.getParams()[cnt].getURI() ))
									isMatch = false;
							}
						}
						if(isMatch){
							correspondingPreconditionFound=true;
							break;
						}
					}
					
				
					
				}
			}
			
			if(!correspondingPreconditionFound)
				return false;
		}
		
		return true;
	}
	
	public String serializeToOwls() throws ParserConfigurationException, TransformerException{
		String result="";
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		
		Element rootElement = doc.createElement("rdf:RDF");
		
		Attr attr = doc.createAttribute("xmlns:process");
		attr.setValue("http://www.daml.org/services/owl-s/1.2/Process.owl#");
		rootElement.setAttributeNode(attr);
		
		attr = doc.createAttribute("xmlns:service");
		attr.setValue("http://www.daml.org/services/owl-s/1.2/Service.owl#");
		rootElement.setAttributeNode(attr);
		
		attr = doc.createAttribute("xmlns:rdf");
		attr.setValue("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		rootElement.setAttributeNode(attr);
		
		attr = doc.createAttribute("xmlns:rdfs");
		attr.setValue("http://www.w3.org/2000/01/rdf-schema#");
		rootElement.setAttributeNode(attr);
		
		attr = doc.createAttribute("xmlns:owl");
		attr.setValue("http://www.w3.org/2002/07/owl#");
		rootElement.setAttributeNode(attr);
		
		attr = doc.createAttribute("xmlns:daml");
		attr.setValue("http://www.daml.org/2001/03/daml+oil");
		rootElement.setAttributeNode(attr);
		
		attr = doc.createAttribute("xmlns:expr");
		attr.setValue("http://www.daml.org/services/owl-s/1.2/generic/Expression.owl#");
		rootElement.setAttributeNode(attr);
		
		attr = doc.createAttribute("xmlns:swrl");
		attr.setValue("http://www.w3.org/2003/11/swrl#");
		rootElement.setAttributeNode(attr);
		
		attr = doc.createAttribute("xml:base");
		attr.setValue(getURI());
		rootElement.setAttributeNode(attr);
		
		doc.appendChild(rootElement);
		
		rootElement.appendChild(createServiceElement(doc));
		
		if(getReceiveService()!=null){
			rootElement.appendChild(getReceiveService().createServiceElement(doc));
		}
		
		
		StringWriter output = new StringWriter();

	    Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    transformer.transform(new DOMSource(doc), new StreamResult(output));

	    result = output.toString();
		return result;
	}

	private Node createServiceElement(Document doc) {
		
		Element serviceNode = doc.createElement("service:Service");
		
		serviceNode.setAttribute("rdf:ID",getName());
		
		Element describedByNode = doc.createElement("service:describedBy");
		
		Element processNode = doc.createElement("process:AtomicProcess");
		
		processNode.setAttribute("rdf:ID",getName()+"Process");
		
		Element describesNode= doc.createElement("service:describes");
		describesNode.setAttribute("rdf:resource","#"+getName());
		
		processNode.appendChild(describesNode);
		
		for(int inputCntr=0; inputCntr< getInputList().size(); inputCntr++){
			
			Instance curInput = getInputs().get(getInputList().get(inputCntr));
			processNode.appendChild(createInputOutputNode(doc,curInput,"Input"));			
			
		}
		for(int outputCntr=0; outputCntr<getOutputList().size(); outputCntr++){
			
			Instance curOutput = getOutputs().get(getOutputList().get(outputCntr));
			processNode.appendChild(createInputOutputNode(doc,curOutput,"Output"));			
			
		}
		if((getPrecondition().getConditions().size()>0)||(getInputList().size()>0)||(getVarList().size()>0))
			processNode.appendChild(createPreconditionNode(doc));
		if((getPostcondition().getConditions().size()>0))
			processNode.appendChild(createEffectNode(doc));
		
		describedByNode.appendChild(processNode)	;		
		serviceNode.appendChild(describedByNode);
		return serviceNode;
	}

	private Node createEffectNode(Document doc) {
		Element effectNode= doc.createElement("process:hasResult");
		Element swrlConditionNode= doc.createElement("expr:SWRL-Condition");
		swrlConditionNode.setAttribute("rdf:ID",getName()+"Eff");
		Element expressionObjectNode = doc.createElement("expr:expressionObject");
		
		List<Element> conds = new ArrayList<Element>();
		
		
		for(int effectCntr=0; effectCntr< getPostcondition().getConditions().size(); effectCntr++){
			Element ipAtomNode = doc.createElement("swrl:IndividualPropertyAtom");
			
			Element propertyPredicateNode = doc.createElement("swrl:propertyPredicate");
			if(  getPostcondition().getConditions().get(effectCntr).isNot())
				propertyPredicateNode.setAttribute("rdf:resource",  getPostcondition().getConditions().get(effectCntr).getStateFactInstance().getType().getURI().toString().substring(0,  getPostcondition().getConditions().get(effectCntr).getStateFactInstance().getType().getURI().toString().length()- getPostcondition().getConditions().get(effectCntr).getStateFactInstance().getType().getTypeName().length())+"!"+getPostcondition().getConditions().get(effectCntr).getStateFactInstance().getType().getTypeName());
			else
				propertyPredicateNode.setAttribute("rdf:resource", getPostcondition().getConditions().get(effectCntr).getStateFactInstance().getType().getURI().toString());
			
			Element argument1Node = doc.createElement("swrl:argument1");
			argument1Node.setAttribute("rdf:resource",getPostcondition().getConditions().get(effectCntr).getStateFactInstance().getParams()[0].getURI().toString());
			
			Element argument2Node = doc.createElement("swrl:argument2");
			argument2Node.setAttribute("rdf:resource", getPostcondition().getConditions().get(effectCntr).getStateFactInstance().getParams()[1].getURI().toString());
			
			ipAtomNode.appendChild(propertyPredicateNode);
			ipAtomNode.appendChild(argument1Node);
			ipAtomNode.appendChild(argument2Node);
			
			conds.add(ipAtomNode);
		}
		
		Element atomListNode= recNodeCreator(doc,conds);
		
		expressionObjectNode.appendChild(atomListNode);
		
		swrlConditionNode.appendChild(expressionObjectNode);
		
		
		Element expressionLanguageNode = doc.createElement("expr:expressionLanguage");
		expressionLanguageNode.setAttribute("rdf:resource","http://www.daml.org/services/owl-s/1.2/generic/Expression.owl#SWRL");
		swrlConditionNode.appendChild(expressionLanguageNode);
		effectNode.appendChild(swrlConditionNode);
		return effectNode;
	}

	private Node createPreconditionNode(Document doc) {
		Element preconditionNode= doc.createElement("process:hasPrecondition");
		Element swrlConditionNode= doc.createElement("expr:SWRL-Condition");
		swrlConditionNode.setAttribute("rdf:ID",getName()+"Prec");
		Element expressionObjectNode = doc.createElement("expr:expressionObject");
		
		List<Element> conds =new ArrayList<Element>();
		
		for(int inputCntr=0; inputCntr< getInputList().size(); inputCntr++){
			Element classAtomNode = doc.createElement("swrl:ClassAtom");
			
			Element classPredicateNode = doc.createElement("swrl:classPredicate");
			classPredicateNode.setAttribute("rdf:resource",getInputs().get(getInputList().get(inputCntr)).getType().getURI().toString());
			Element argument1Node =  doc.createElement("swrl:argument1");
			argument1Node.setAttribute("rdf:resource",getInputs().get(getInputList().get(inputCntr)).getURI().toString());
			classAtomNode.appendChild(classPredicateNode);
			classAtomNode.appendChild(argument1Node);
			conds.add(classAtomNode);
		}
		for(int varCntr=0; varCntr< getVarList().size(); varCntr++){
			Element classAtomNode = doc.createElement("swrl:ClassAtom");
			
			Element classPredicateNode = doc.createElement("swrl:classPredicate");
			classPredicateNode.setAttribute("rdf:resource",getVars().get(getVarList().get(varCntr)).getType().getURI().toString());
			Element argument1Node =  doc.createElement("swrl:argument1");
			
			Element ElementiableNode =  doc.createElement("swrl:Variable");
			ElementiableNode.setAttribute("rdf:ID",getVars().get(getVarList().get(varCntr)).getName());
			argument1Node.appendChild(ElementiableNode);
			
			classAtomNode.appendChild(classPredicateNode);
			classAtomNode.appendChild(argument1Node);
			conds.add(classAtomNode);
		}
		for(int precCntr=0; precCntr<getPrecondition().getConditions().size(); precCntr++){
			Element ipAtomNode = doc.createElement("swrl:IndividualPropertyAtom");
			
			Element propertyPredicateNode = doc.createElement("swrl:propertyPredicate");
			if( getPrecondition().getConditions().get(precCntr).isNot())
				propertyPredicateNode.setAttribute("rdf:resource", getPrecondition().getConditions().get(precCntr).getStateFactInstance().getType().getURI().toString().substring(0, getPrecondition().getConditions().get(precCntr).getStateFactInstance().getType().getURI().toString().length()-getPrecondition().getConditions().get(precCntr).getStateFactInstance().getType().getTypeName().length())+"!"+getPrecondition().getConditions().get(precCntr).getStateFactInstance().getType().getTypeName());
			else
				propertyPredicateNode.setAttribute("rdf:resource", getPrecondition().getConditions().get(precCntr).getStateFactInstance().getType().getURI().toString());
			
			Element argument1Node = doc.createElement("swrl:argument1");
			argument1Node.setAttribute("rdf:resource", getPrecondition().getConditions().get(precCntr).getStateFactInstance().getParams()[0].getURI().toString());
			
			Element argument2Node = doc.createElement("swrl:argument2");
			argument2Node.setAttribute("rdf:resource", getPrecondition().getConditions().get(precCntr).getStateFactInstance().getParams()[1].getURI().toString());
			
			ipAtomNode.appendChild(propertyPredicateNode);
			ipAtomNode.appendChild(argument1Node);
			ipAtomNode.appendChild(argument2Node);
			
			conds.add(ipAtomNode);
		}
		
		Element atomListNode= recNodeCreator(doc,conds);
		
		expressionObjectNode.appendChild(atomListNode);
		
		swrlConditionNode.appendChild(expressionObjectNode);
		
		
		Element expressionLanguageNode = doc.createElement("expr:expressionLanguage");
		expressionLanguageNode.setAttribute("rdf:resource","http://www.daml.org/services/owl-s/1.2/generic/Expression.owl#SWRL");
		swrlConditionNode.appendChild(expressionLanguageNode);
		preconditionNode.appendChild(swrlConditionNode);
		return preconditionNode;
	}

	private Element recNodeCreator(Document doc, List<Element> conds) {
		Element atomListNode = doc.createElement("swrl:AtomList");
		
		Element firstNode = doc.createElement("rdf:first");
		firstNode.appendChild(conds.get(0));
		atomListNode.appendChild(firstNode);
		
		Element restNode = doc.createElement("rdf:rest");
		
		if(conds.size()>1){
			restNode.appendChild(recNodeCreator(doc,conds.subList(1, conds.size())));
		}
		else{
			restNode.setAttribute("rdf:resource","http://www.w3.org/1999/02/22-rdf-syntax-ns#nil");
		}
		atomListNode.appendChild(restNode);
		
		return atomListNode;
	}

	private Node createInputOutputNode(Document doc,Instance curInput, String text) {
		Element hasInputNode = doc.createElement("process:has"+text);
		
		Element inputNode = doc.createElement("process:"+text);
		
		inputNode.setAttribute("rdf:ID",curInput.getName());
		
		Element parameterTypeNode = doc.createElement("process:parameterType");
		parameterTypeNode.setAttribute("rdf:datatype","http://www.w3.org/2001/XMLSchema#anyURI");
		
		Node parameterTypeTextNode = doc.createTextNode(curInput.getType().getURI().toString());
		
		parameterTypeNode.appendChild(parameterTypeTextNode);
		
		
		inputNode.appendChild(parameterTypeNode);
		
		hasInputNode.appendChild(inputNode);
		return hasInputNode;
	}
	
}

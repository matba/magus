package edu.ls3.magus.cl.fmconfigurator.model;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

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

import edu.ls3.magus.cl.contextmanager.basic.Instance;
import edu.ls3.magus.cl.contextmanager.basic.InstanceMap;
import edu.ls3.magus.cl.contextmanager.basic.InstanceType;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstance;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstanceS;
import edu.ls3.magus.cl.contextmanager.basic.StateFactType;
import edu.ls3.magus.cl.contextmanager.context.ContextModel;
import edu.ls3.magus.cl.exceptions.PreSetFeatureSelection;
import edu.ls3.magus.cl.exceptions.UnsuccessfulMashupGeneration;
import edu.ls3.magus.cl.fmconfigurator.ContextStateModel;
import edu.ls3.magus.cl.fmconfigurator.DomainModels;
import edu.ls3.magus.cl.fmconfigurator.FeatureModelConfigurationMashupGeneration;
import edu.ls3.magus.cl.fmconfigurator.FeatureModelConfigurator;
import edu.ls3.magus.cl.fmconfigurator.FeatureSelectionStatus;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.FlowComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.utility.Holder;

public class FeatureModel {
	private Feature rootFeature;
	private IntegrityConstraintSet integrityConstraints;
	private InstanceMap featureEntities;
	
	public FeatureModel()
	{
		setRootFeature(null);
		setIntegrityConstraints(new IntegrityConstraintSet());
	}
	
	
	public Map<String,Feature> getFeaturesUUIDMap(){
		Map<String,Feature> result = new HashMap<String, Feature>();
		List<Feature> fl= this.getRootFeature().getFeatureAsList();
		for(Feature f:fl){
			result.put(f.getUuid(), f);
		}
		return result;
		
	}
	
	public Map<String,Feature> getFeaturesVarNameUUIDMap(){
		Map<String,Feature> result = new HashMap<String, Feature>();
		List<Feature> fl= this.getRootFeature().getFeatureAsList();
		for(Feature f:fl){
			result.put(f.getUuidVarName(), f);
		}
		return result;
		
	}
	
	
    public static FeatureModel parse(String xml, FeatureAnnotationSet fma,ContextModel cm) throws URISyntaxException, Exception{
		
	
		FeatureModel result= new FeatureModel();
		
		result.setFeatureEntities(new InstanceMap());
		
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		Document doc = dBuilder.parse(is);
		
//		String baseURI = "";
		
		String rootExpression = "//*[name()='featureModel']";
		XPath xPath =  XPathFactory.newInstance().newXPath();
		Node root = (Node) xPath.compile(rootExpression).evaluate(doc, XPathConstants.NODE);
		
		NodeList childNodes =  root.getChildNodes();
		 
		for(int i = 0; i< childNodes.getLength(); i++)
		{
			 if(childNodes.item(i).getNodeName().equals("feature")   ){
				 result.setRootFeature(dfsFeatureRead(childNodes.item(i)));
			 
			 }
		 
			if(childNodes.item(i).getNodeName().equals("annotations" )){
				 NodeList annotationChilds =childNodes.item(i).getChildNodes();
				 fma.setBaseURI( childNodes.item(i).getAttributes().getNamedItem("baseURI").getNodeValue());
				 for(int acntr = 0; acntr< annotationChilds.getLength(); acntr++)
				 {
					 if(annotationChilds.item(acntr).getNodeName().equals("annotation") ){
						 Feature curFeature= result.findFeatureByName(annotationChilds.item(acntr).getAttributes().getNamedItem("feature").getNodeValue());
						 FeatureAnnotation newAnnotation = readAnnotationEntities(curFeature, annotationChilds.item(acntr), fma.getBaseURI(),cm);
						 result.getFeatureEntities().getInstanceMap().putAll(newAnnotation.getEntities().getInstanceMap());
						 //console.log(newAnnotation.feature);
						 //console.log(featureModel.findFeaturebyName(newAnnotation.feature).uuid);
						 fma.getAnnotationMap().put(curFeature, newAnnotation);
						 
					 }
					 
				 }
			 }
			 if(childNodes.item(i).getNodeName().equals("integrityconstraints" )){
				 NodeList icChilds =childNodes.item(i).getChildNodes();
				 for(int acntr = 0; acntr< icChilds.getLength(); acntr++)
				 {
					 if(icChilds.item(acntr).getNodeName().equals("integrityconstraint" )){
						 
						 String type =  icChilds.item(acntr).getAttributes().getNamedItem("type").getNodeValue();
						 String sourceFeatureName =  icChilds.item(acntr).getAttributes().getNamedItem("source").getNodeValue();
						 String targetFeatureName  =  icChilds.item(acntr).getAttributes().getNamedItem("target").getNodeValue();
						 Feature sourceFeature = result.findFeatureByName(sourceFeatureName);
						 Feature targetFeature = result.findFeatureByName(targetFeatureName);
						 //console.log(newAnnotation.feature);
						 //console.log(featureModel.findFeaturebyName(newAnnotation.feature).uuid);
							 result.getIntegrityConstraints().getIntegrityConstraints().add(new IntegrityConstraint(sourceFeature, type, targetFeature)) ;
							 
						 }
						 
					 }
				 }
		 }
		for(int i = 0; i< childNodes.getLength(); i++)
		{
			
			if(childNodes.item(i).getNodeName().equals("annotations" )){
				 NodeList annotationChilds =childNodes.item(i).getChildNodes();
				 fma.setBaseURI( childNodes.item(i).getAttributes().getNamedItem("baseURI").getNodeValue());
				 for(int acntr = 0; acntr< annotationChilds.getLength(); acntr++)
				 {
					 if(annotationChilds.item(acntr).getNodeName().equals("annotation") ){
						 Feature curFeature= result.findFeatureByName(annotationChilds.item(acntr).getAttributes().getNamedItem("feature").getNodeValue());
						 FeatureAnnotation curFA = fma.getAnnotationMap().get(curFeature);
						 readAnnotationFacts(curFeature, annotationChilds.item(acntr), fma.getBaseURI(), cm, curFA, result.getFeatureEntities());
						 
					 }
				 }
			}
		}
		
		
		return result;
	}

	private Feature findFeatureByName(String FeatureName) {

		return this.getRootFeature().findFeatureByName(FeatureName);
	}


	private static FeatureAnnotation readAnnotationEntities(Feature feature, Node item,String baseURI, ContextModel cm) throws URISyntaxException, Exception {
		
		
		FeatureAnnotation  result = new FeatureAnnotation(feature);
		
		
		
		NodeList annChilds = item.getChildNodes();
		for(int acntr = 0; acntr< annChilds.getLength(); acntr++)
		{
			 if(annChilds.item(acntr).getNodeName().equals("entities" )){
				 NodeList entitiesChilds = annChilds.item(acntr).getChildNodes();
				 for(int ecntr = 0; ecntr< entitiesChilds.getLength(); ecntr++)
				 {
					 if(entitiesChilds.item(ecntr).getNodeName().equals("entity" )){
						 Node entityElement =  entitiesChilds.item(ecntr);
						 String entityName = entityElement.getAttributes().getNamedItem("name").getNodeValue();
						 String entityTypeURI = entityElement.getAttributes().getNamedItem("type").getNodeValue();
						 String io = entityElement.getAttributes().getNamedItem("io").getNodeValue();
						 
						 InstanceType it = cm.getInstanceTypes().get(new URI( entityTypeURI));
						 URI entityURI = new URI(baseURI+"#"+entityName);
						 Instance newEntity = new Instance(it, entityName, entityURI);
						 newEntity.setIo(io);
						 result.getEntities().getInstanceMap().put(entityURI,newEntity);
						 
						 
					 
					 }
				 }
			 }
			 
			 
		}
		
		
		
		return result;
		
	}
	
	
	private static void readAnnotationFacts(Feature feature, Node item,String baseURI, ContextModel cm, FeatureAnnotation fa, InstanceMap fmInstances ) throws Exception{
		NodeList annChilds = item.getChildNodes();
		for(int acntr = 0; acntr< annChilds.getLength(); acntr++)
		{
		
			if((annChilds.item(acntr).getNodeName().equals("precondition"))||(annChilds.item(acntr).getNodeName().equals("effect"  ))){
				 boolean isPrecondition = true;
				 if(annChilds.item(acntr).getNodeName().equals("effect")  )
					 isPrecondition = false;
				 
				 NodeList peChilds = annChilds.item(acntr).getChildNodes();
				 for(int pcntr = 0; pcntr< peChilds.getLength(); pcntr++)
				 {
					 
					if(peChilds.item(pcntr).getNodeName().equals("facts" )){
						 NodeList factsChilds = peChilds.item(pcntr).getChildNodes();
						 for(int ccntr = 0; ccntr< factsChilds.getLength(); ccntr++)
						 {
							 if(factsChilds.item(ccntr).getNodeName().equals("fact" )){
								 Node factElement=factsChilds.item(ccntr);
								 
								 String factURI = factElement.getAttributes().getNamedItem("fact").getNodeValue();
								 
								 String argument1URI = factElement.getAttributes().getNamedItem("firstEntity").getNodeValue();
								 if( argument1URI.startsWith("#")){
									 argument1URI = baseURI+argument1URI;
								 }
								 
								 String argument2URI = factElement.getAttributes().getNamedItem("secondEntity").getNodeValue();
								 if( argument2URI.startsWith("#")){
									 argument2URI = baseURI+argument2URI;
								 }
								 
								 StateFactType newsft = cm.getFactTypes().get(new URI( factURI));
								 
								 URI argument1u = new URI(argument1URI);
								 Instance argument1 = null;
								 if(fmInstances.contains(argument1u))
									 argument1 = fmInstances.get(argument1u);
								 else
									 if(cm.getInstances().contains(argument1u))
										 argument1 = cm.getInstances().get(argument1u);
								 
								 URI argument2u = new URI(argument2URI);
								 Instance argument2 = null;
								 if(fmInstances.contains(argument2u))
									 argument2 = fmInstances.get(argument2u);
								 else
									 if(cm.getInstances().contains(argument2u))
										 argument2 = cm.getInstances().get(argument2u);
								
								 if((argument1==null)||(argument2==null)){
									 throw new Exception("An unknown instance was used in FM annotation");
								 }
								 
								 Instance[] params = {argument1,argument2};
								 
								 StateFactInstance sfi = new StateFactInstance(newsft, params);
								 
								 StateFactInstanceS curFact= new StateFactInstanceS(sfi, false);
								 
								 if (isPrecondition)
									 fa.getPreconditions().getFacts().add(curFact);
								 else
									 fa.getEffects().getFacts().add(curFact);
							 
							 }
						 }
					 }
				 }
			}
		 }
	}

	private static Feature dfsFeatureRead(Node item) {
		
		Feature result = null;
		String name = item.getAttributes().getNamedItem("name").getNodeValue() ;
		String uuid = item.getAttributes().getNamedItem("uuid").getNodeValue() ;
		boolean optional = false;
		if(item.getAttributes().getNamedItem("type").getNodeValue().equals("optional"))
			optional =true;
		
		boolean alternative = false;
		boolean orgroup = false;
		for(int ccntr =0; ccntr< item.getChildNodes().getLength(); ccntr++){
			if((item.getChildNodes().getLength() >0)&& item.getChildNodes().item(ccntr).getNodeName().equals("alternative")  ){
				alternative =true;
				item = item.getChildNodes().item(ccntr);
			}
		}
		if(!alternative){
			for(int ccntr =0; ccntr< item.getChildNodes().getLength(); ccntr++){
				if((item.getChildNodes().getLength() >0)&& item.getChildNodes().item(ccntr).getNodeName().equals("orgroup")  ){
					orgroup =true;
					item = item.getChildNodes().item(ccntr);
				}
			}
		
		}
		
		result = new Feature(name, optional, alternative, orgroup,uuid)	;
		result.setUuid(uuid);
		
		
//		if(alternative || orgroup)
//			item = item.getFirstChild();
		
		for(int ccntr =0; ccntr< item.getChildNodes().getLength(); ccntr++){
			if(item.getChildNodes().item(ccntr).getNodeName().equals("feature")){
				Feature cf = dfsFeatureRead(item.getChildNodes().item(ccntr));
				result.getChildren().add(cf);
			}
		}
		
		
		
		
		return result;
	}


	private InstanceMap getFeatureEntities() {
		return featureEntities;
	}


	private void setFeatureEntities(InstanceMap featureEntities) {
		this.featureEntities = featureEntities;
	}


	public List<Feature> getFeatureList() {
		
		
		if(this.getRootFeature()==null){
			return new ArrayList<Feature>();
		}
		
		return this.getRootFeature().getFeatureAsList();
	}


	public Feature getRootFeature() {
		return rootFeature;
	}


	private void setRootFeature(Feature rootFeature) {
		this.rootFeature = rootFeature;
	}


	public IntegrityConstraintSet getIntegrityConstraints() {
		return integrityConstraints;
	}


	private void setIntegrityConstraints(IntegrityConstraintSet integrityConstraints) {
		this.integrityConstraints = integrityConstraints;
	}
	
	public static FeatureModel parseFamaTextFormat(String input) throws Exception{
		FeatureModel result= new FeatureModel();
		
		result.setFeatureEntities(new InstanceMap());
		
		String[] lines = input.split(System.getProperty("line.separator"));
		// A feature is processed if all of its child features has been added to it
		
		Queue<String> processingQueue = new LinkedList<String>();
		Map<String, List<String>> structureLines = new HashMap<String, List<String>>();
		List<String> integrityConstraints = new ArrayList<String>();
		Map<String,Feature> featureMap = new HashMap<String, Feature>();
		
		String rootFeatureName = "";
		
		
		for(String curLine:lines){
			String trimmedCurLine = curLine.trim();
			if(trimmedCurLine.startsWith("%"))
				continue;
			if(trimmedCurLine.endsWith(";"))
				trimmedCurLine = trimmedCurLine.substring(0, trimmedCurLine.length()-1);
				
			if(trimmedCurLine.contains(":")){
				//Structural Constraint
				String[] features = trimmedCurLine.split(":");
				if(features.length>2){
					throw new Exception("Parsing Error, Unexpected number of : charecter");
				}
				String parentFeature = features[0].trim();
				
				if(!parentFeature.startsWith("'") || !parentFeature.endsWith("'"))
					throw new Exception("Parsing Error, Unexpected character");
				parentFeature = parentFeature.substring(1, parentFeature.length()-1);
				
				
				
				
				if(structureLines.isEmpty()){
					rootFeatureName = parentFeature;
				}
				
				if(!structureLines.containsKey(parentFeature))
					structureLines.put(parentFeature, new ArrayList<String>());
				
				structureLines.get(parentFeature).add(features[1].trim());
				
				
				
			}
			else{
				integrityConstraints.add(trimmedCurLine.substring(0,curLine.length()-1).trim());
			}
		}
		
		processingQueue.add(rootFeatureName);
		Feature rootFeature = new Feature(rootFeatureName, false, false, false, UUID.randomUUID().toString());
		result.rootFeature = rootFeature;
		featureMap.put(rootFeatureName, rootFeature);
		int auxCntr =1;
		
		while(!processingQueue.isEmpty()){
			String curFeatureName = processingQueue.remove();
			Feature curFeature = null;
			curFeature = featureMap.get(curFeatureName);
			
			if(!structureLines.containsKey(curFeatureName))
				continue;
			
			
			for(String curLine : structureLines.get(curFeatureName)){
				
				if(curLine.contains(",")){
					boolean isAlternative = false;
					boolean isOrGroup = false;
					if(curLine.startsWith("[1,1]"))
						isAlternative = true;
					else
						isOrGroup = true;
					Feature gparentFeature = curFeature;
					if(structureLines.get(curFeatureName).size()>1){
						while(featureMap.containsKey("A"+auxCntr))
							auxCntr++;
						
						gparentFeature = new Feature("A"+auxCntr, false, isAlternative, isOrGroup, UUID.randomUUID().toString());
						featureMap.put("A"+auxCntr, gparentFeature);
						curFeature.getChildren().add(gparentFeature);
					}
					else{
						curFeature.setAlternative(isAlternative);
						curFeature.setOrGroup(isOrGroup);
					}
					curLine = curLine.substring(curLine.lastIndexOf("]")+1).trim();
					curLine = curLine.substring(1, curLine.length()-1);
					String[] childFeatures = curLine.split("'\\s+'");
					for(String childFeature: childFeatures){
						Feature newFeature = new Feature(childFeature, false, isAlternative, isOrGroup, UUID.randomUUID().toString());
						featureMap.put(childFeature, newFeature);
						gparentFeature.getChildren().add(newFeature);
						processingQueue.add(childFeature);
					}
				}
				else{
					boolean isOptional = false;
					if(curLine.startsWith("[")){
						isOptional = true;
						curLine = curLine.substring(1, curLine.length()-1);
						
					}
					curLine = curLine.substring(1, curLine.length()-1);
					Feature newFeature = new Feature(curLine, isOptional, false, false, UUID.randomUUID().toString());
					featureMap.put(curLine, newFeature);
					curFeature.getChildren().add(newFeature);
					processingQueue.add(curLine);
					
				}
			}
			
		}
		
		for(String curLine: integrityConstraints){
			curLine = curLine.substring(1, curLine.length()-1);
			if(curLine.split("'\\s+REQUIRES\\s+'").length==2){
				String[] fts = curLine.split("'\\s+REQUIRES\\s+'");
				IntegrityConstraint ic = new IntegrityConstraint(featureMap.get(fts[0]), "requires", featureMap.get(fts[1]));
				result.getIntegrityConstraints().getIntegrityConstraints().add(ic);
			}
			if(curLine.split("'\\s+EXCLUDES\\s+'").length==2){
				String[] fts = curLine.split("'\\s+EXCLUDES\\s+'");
				IntegrityConstraint ic = new IntegrityConstraint(featureMap.get(fts[0]), "excludes", featureMap.get(fts[1]));
				result.getIntegrityConstraints().getIntegrityConstraints().add(ic);
			}
			
		}
		
		
		
		return result;
		
	}
	
	public String serializeToXml(FeatureAnnotationSet anns) throws ParserConfigurationException, TransformerException{
		String result="";
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		
		Element rootElement = doc.createElement("featureModel");
		
		Attr attr = doc.createAttribute("xmlns:xsi");
		attr.setValue("http://www.w3.org/2001/XMLSchema-instance");
		rootElement.setAttributeNode(attr);
		
		attr = doc.createAttribute("xsi:noNamespaceSchemaLocation");
		attr.setValue("http://magus.online/resources/schema/featureModel.xsd");
		rootElement.setAttributeNode(attr);
		
		doc.appendChild(rootElement);
		
		rootElement.appendChild(createFeatureElement(doc,rootFeature));
		rootElement.appendChild(createIntegrityConstraintsElement(doc));
		rootElement.appendChild(createAnnotationElement(doc,anns));
		
		StringWriter output = new StringWriter();

	    Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    transformer.transform(new DOMSource(doc), new StreamResult(output));

	    result = output.toString();
		return result;
	}


	private Node createAnnotationElement(Document doc, FeatureAnnotationSet anns) {
		Element AnnsNode = doc.createElement("annotations");
		
		Attr attr = doc.createAttribute("baseURI");
		attr.setValue(anns.getBaseURI());
		AnnsNode.setAttributeNode(attr);
		
		List<Feature> featureList = getFeatureList();
		
		for(int icCntr=0;icCntr<featureList.size(); icCntr++){
			FeatureAnnotation curAnn = anns.getAnnotationMap().get(featureList.get(icCntr));
			
			
			Element AnnNode = doc.createElement("annotation");
			AnnNode.setAttribute("feature",featureList.get(icCntr).getName()) ;
			
			Element EntitiesNode =  doc.createElement("entities");
			
			for(int encntr=0; encntr<curAnn.getEntities().getAsList().size();encntr++){
				Element varCurEntity = doc.createElement("entity");

				
				varCurEntity.setAttribute("name",curAnn.getEntities().getAsList().get(encntr).getName()) ;
				varCurEntity.setAttribute("type",curAnn.getEntities().getAsList().get(encntr).getType().getURI().toString()) ;
				varCurEntity.setAttribute("io",curAnn.getEntities().getAsList().get(encntr).getIo() ) ;
				
				EntitiesNode.appendChild(varCurEntity);
			}
			
			AnnNode.appendChild(EntitiesNode);
			
			

			Element preconditionsNode =  doc.createElement("precondition");
			Element factsNode = doc.createElement("facts");
			
			for(int pccntr=0; pccntr<curAnn.getPreconditions().getFacts().size();pccntr++){
				Element varCurEntity = doc.createElement("fact");
				varCurEntity.setAttribute("fact",curAnn.getPreconditions().getFacts().get(pccntr).getStateFactInstance().getType().getURI().toString()) ;
				if(curAnn.getPreconditions().getFacts().get(pccntr).getStateFactInstance().getParams()[0].getURI().toString().startsWith(anns.getBaseURI()))
					varCurEntity.setAttribute("firstEntity",curAnn.getPreconditions().getFacts().get(pccntr).getStateFactInstance().getParams()[0].getURI().toString().substring(anns.getBaseURI().length())) ;
				else
					varCurEntity.setAttribute("firstEntity",curAnn.getPreconditions().getFacts().get(pccntr).getStateFactInstance().getParams()[0].getURI().toString()) ;
				if(curAnn.getPreconditions().getFacts().get(pccntr).getStateFactInstance().getParams()[1].getURI().toString().startsWith(anns.getBaseURI()))
					varCurEntity.setAttribute("secondEntity",curAnn.getPreconditions().getFacts().get(pccntr).getStateFactInstance().getParams()[1].getURI().toString().substring(anns.getBaseURI().length())) ;
				else
					varCurEntity.setAttribute("secondEntity",curAnn.getPreconditions().getFacts().get(pccntr).getStateFactInstance().getParams()[1].getURI().toString()) ;
				
				
				factsNode.appendChild(varCurEntity);
			}
			preconditionsNode.appendChild(factsNode);
			AnnNode.appendChild(preconditionsNode);
			
			
			
			Element effectsNode =  doc.createElement("effect");
			factsNode = doc.createElement("facts");
			
			for(int efcntr=0; efcntr<curAnn.getEffects().getFacts().size();efcntr++){
				Element varCurEntity = doc.createElement("fact");
				varCurEntity.setAttribute("fact",curAnn.getEffects().getFacts().get(efcntr).getStateFactInstance().getType().getURI().toString()) ;
				if(curAnn.getEffects().getFacts().get(efcntr).getStateFactInstance().getParams()[0].getURI().toString().startsWith(anns.getBaseURI()))
					varCurEntity.setAttribute("firstEntity",curAnn.getEffects().getFacts().get(efcntr).getStateFactInstance().getParams()[0].getURI().toString().substring(anns.getBaseURI().length())) ;
				else
					varCurEntity.setAttribute("firstEntity",curAnn.getEffects().getFacts().get(efcntr).getStateFactInstance().getParams()[0].getURI().toString()) ;
				if(curAnn.getEffects().getFacts().get(efcntr).getStateFactInstance().getParams()[1].getURI().toString().startsWith(anns.getBaseURI()))
					varCurEntity.setAttribute("secondEntity",curAnn.getEffects().getFacts().get(efcntr).getStateFactInstance().getParams()[1].getURI().toString().substring(anns.getBaseURI().length())) ;
				else
					varCurEntity.setAttribute("secondEntity",curAnn.getEffects().getFacts().get(efcntr).getStateFactInstance().getParams()[1].getURI().toString()) ;
				
				
				factsNode.appendChild(varCurEntity);
			}
			effectsNode.appendChild(factsNode);
			AnnNode.appendChild(effectsNode);
			
			
			AnnsNode.appendChild(AnnNode);
		}
		
		return AnnsNode;
	}


	private Node createIntegrityConstraintsElement(Document doc) {
		Element ICSNode = doc.createElement("integrityconstraints");
		
		for(int icCntr=0;icCntr<getIntegrityConstraints().getIntegrityConstraints().size(); icCntr++){
			IntegrityConstraint curIC =getIntegrityConstraints().getIntegrityConstraints().get(icCntr);
			
			
			Element ICNode = doc.createElement("integrityconstraint");
			
			Attr attr = doc.createAttribute("type");
			attr.setValue(curIC.getType());
			ICNode.setAttributeNode(attr);
			
			attr = doc.createAttribute("source");
			attr.setValue(curIC.getSourceFeature().getName());
			ICNode.setAttributeNode(attr);
			
			attr = doc.createAttribute("target");
			attr.setValue(curIC.getTargetFeature().getName());
			ICNode.setAttributeNode(attr);
 
			ICSNode.appendChild(ICNode);
		}
		
		return ICSNode;
	}


	private Node createFeatureElement(Document doc, Feature feature) {
		Element featureModelNode = doc.createElement("feature");
		Element placeToAdd = featureModelNode;
		
		Attr attr = doc.createAttribute("name");
		attr.setValue(feature.getName());
		featureModelNode.setAttributeNode(attr);
		
		attr = doc.createAttribute("uuid");
		attr.setValue(feature.getUuid());
		featureModelNode.setAttributeNode(attr);
		
		if(feature.isOptional()){
			attr = doc.createAttribute("type");
			attr.setValue("optional");
			featureModelNode.setAttributeNode(attr);
			
		}
		else{
			attr = doc.createAttribute("type");
			attr.setValue("mandatory");
			featureModelNode.setAttributeNode(attr);
		}
		
		if(feature.isAlternative()){
			placeToAdd= doc.createElement("alternative");
			featureModelNode.appendChild(placeToAdd);
		}
		if(feature.isOrGroup()){
			placeToAdd= doc.createElement("orgroup");
			featureModelNode.appendChild(placeToAdd);
		}
		
		for(int ccntr=0; ccntr<feature.getChildren().size(); ccntr++)
			placeToAdd.appendChild(createFeatureElement(doc,feature.getChildren().get(ccntr)));
		
		return featureModelNode;
	}


	public List<Feature> getFeatureAnncestors(Feature f) {
		
		List<Feature> flst = getFeatureList();
		List<Feature> result = new ArrayList<Feature>();
		boolean isParentFound = true;
		Feature curFeature= f;
		while(isParentFound){
			isParentFound = false;
			for(Feature cf: flst){
				if(cf.getChildren().contains(curFeature))
				{
					isParentFound = true;
					result.add(cf);
					curFeature = cf;
					break;
				}
			}
		}
		
		return result;
	}
	
	public List<FeatureModelConfiguration> getAllValidConfiguration(int threshold) throws Exception{
		return getAllValidConfiguration(threshold,0);
	}
	
	public List<FeatureModelConfiguration> getAllValidConfiguration(int threshold,long timeout) throws Exception{
		
		boolean isTimeoutEnabled = false;
		long stTimeMillis = System.currentTimeMillis();
		
		if(timeout>0){
			isTimeoutEnabled =true;
			
		}
		
		
		List<FeatureModelConfiguration> result = new ArrayList<FeatureModelConfiguration>();
		
		List<DecisionPoint> decisionPoints = new ArrayList<DecisionPoint>();
		int dpPointer = -1;
		Queue<DecisionPoint> processingQueue = new LinkedList<DecisionPoint>();
		Boolean allDone = false;
		
		List<Feature> curConfiguration = new ArrayList<Feature>();
		
		DecisionPoint rootDP = new DecisionPoint(rootFeature,false);
		processingQueue.add(rootDP);
		
		//fill decision points
		while(!processingQueue.isEmpty()){
			
			if(isTimeoutEnabled&&(System.currentTimeMillis()-stTimeMillis > timeout))
			{
				System.out.println("Timeout Enabled");
				return  new ArrayList<FeatureModelConfiguration>();
			}
			
			
			DecisionPoint newDP = processingQueue.remove();
			Feature curFeature = newDP.getFeature();
			
			
			if(!curFeature.isAlternative()&& !curFeature.isOrGroup()){
				for(Feature childFeature: curFeature.getChildren())
					if(!childFeature.isOptional()){
						DecisionPoint nnDP = new DecisionPoint(childFeature,false);
						processingQueue.add(nnDP);
						newDP.getChildren().add(nnDP);
						
					}
					
			}
			
			if(!newDP.getPossibleSelection().isEmpty()){
				List<Feature> curSelection = newDP.getPossibleSelection().get(0);
				newDP.setCurrentSelection(curSelection);
				newDP.getPossibleSelection().remove(0);
				for(Feature childFeature: curSelection){
					DecisionPoint nnDP = new DecisionPoint(childFeature,false);
					processingQueue.add(nnDP);
					newDP.getChildren().add(nnDP);
				}
				
			}
			decisionPoints.add(newDP);
			dpPointer++;
		}
		
		//fill first configuration
		curConfiguration = rootDP.getCurConfiguration();
		FeatureModelConfiguration nfmc  = new FeatureModelConfiguration(curConfiguration);
		if(nfmc.hasValidIntegrityConstraints(this)){
			result.add(nfmc);
			
		}
		//System.out.println(nfmc);
		
		
		
		
		//update decision points
		while(!allDone){
			if(isTimeoutEnabled&&(System.currentTimeMillis()-stTimeMillis > timeout))
			{
				System.out.println("Timeout Enabled");
				return  new ArrayList<FeatureModelConfiguration>();
			}
			
			curConfiguration = new ArrayList<Feature>();
			DecisionPoint cDP = decisionPoints.get(dpPointer);
			
			while((dpPointer>=0) && decisionPoints.get(dpPointer).getPossibleSelection().isEmpty()){
				
				
				dpPointer--;
			}
			
			
			if((dpPointer<0))
			{
				allDone = true;
				continue;
			}
			cDP = decisionPoints.get(dpPointer);
			
			for(Feature ft : cDP.getCurrentSelection()){
				DecisionPoint featureDP = null;
				for(DecisionPoint dp: decisionPoints )
					if(dp.getFeature().equals(ft)){
						featureDP= dp;
						break;
					}
								
				removeFeature(decisionPoints,featureDP);
			}
			
			if(!cDP.getFeature().isAlternative()&& !cDP.getFeature().isOrGroup()){
				for(Feature ft : cDP.getFeature().getChildren()){
					if(!ft.isOptional())
					{
						DecisionPoint featureDP = null;
						for(DecisionPoint dp: decisionPoints )
							if(dp.getFeature().equals(ft)){
								featureDP= dp;
								break;
							}
										
						removeFeature(decisionPoints,featureDP);
					}
				}
			}	
			
			List<Feature> curSelection = cDP.getPossibleSelection().get(0);
			cDP.getProcessedSelection().add(cDP.getCurrentSelection());
			cDP.setCurrentSelection(curSelection);
			cDP.getPossibleSelection().remove(0);
			
			cDP.setChildren(new ArrayList<DecisionPoint>());
			
			Feature cf = cDP.getFeature();
			if(!cf.isAlternative()&& !cf.isOrGroup()){
				for(Feature childFeature: cf.getChildren())
					if(!childFeature.isOptional()){
						DecisionPoint nnDP = new DecisionPoint(childFeature,false);
						processingQueue.add(nnDP);
						cDP.getChildren().add(nnDP);
					}
					
			}
			
			for(Feature childFeature: curSelection){
				DecisionPoint nnDP = new DecisionPoint(childFeature,false);
				processingQueue.add(nnDP);
				cDP.getChildren().add(nnDP);
			}
			
			while(dpPointer+1< decisionPoints.size()){
				DecisionPoint curDP =  decisionPoints.get(dpPointer+1);
				for(DecisionPoint featureDP: curDP.getChildren())
					removeFeature(decisionPoints, featureDP);
				DecisionPoint nnDP = new DecisionPoint(curDP.getFeature(),false);
				curDP.setProcessedSelection(nnDP.getProcessedSelection());
				curDP.setCurrentSelection(nnDP.getCurrentSelection());
				curDP.setPossibleSelection(nnDP.getPossibleSelection());
				curDP.setChildren(nnDP.getChildren());
			
				processingQueue.add(curDP);
				decisionPoints.remove(dpPointer+1);
			}
			
			
//			System.out.println();
			
//			System.out.println(rootDP.toString2(""));
			
			
			
			while(!processingQueue.isEmpty()){
				DecisionPoint newDP = processingQueue.remove();
				Feature curFeature = newDP.getFeature();
				
				if(!curFeature.isAlternative()&& !curFeature.isOrGroup()){
					for(Feature childFeature: curFeature.getChildren())
						if(!childFeature.isOptional()){
							DecisionPoint nnDP = new DecisionPoint(childFeature,false);
							processingQueue.add(nnDP);
							newDP.getChildren().add(nnDP);
						}
						
				}
				
				if(!newDP.getPossibleSelection().isEmpty()){
					curSelection = newDP.getPossibleSelection().get(0);
					newDP.setCurrentSelection(curSelection);
					newDP.getPossibleSelection().remove(0);
					for(Feature childFeature: curSelection){
						DecisionPoint nnDP = new DecisionPoint(childFeature,false);
						processingQueue.add(nnDP);
						newDP.getChildren().add(nnDP);
					}
				}
				decisionPoints.add(newDP);
				dpPointer++;
//				System.out.println();
//				
//				System.out.println(rootDP.toString2(""));
				
			}
			
			//fill first configuration
			curConfiguration = rootDP.getCurConfiguration();
			nfmc  = new FeatureModelConfiguration(curConfiguration);
//			System.out.println();
//			System.out.println();
//			System.out.println();
//			System.out.println(rootDP.toString2(""));
//			System.out.println();
//			System.out.println(decisionPoints.toString());
//			System.out.println();
//			System.out.println(nfmc);
//			System.out.println(result.size());
			
//			if(result.contains(nfmc))
//				throw new Exception("A configuration found that already that has been already found");
			
			
			//Filter those that do not respect integrity constraints
			if(nfmc.hasValidIntegrityConstraints(this)){
				result.add(nfmc);
				
			}
			
			//result.add(nfmc);
			//System.out.println(nfmc);
			if((threshold>-1)&&(result.size()>threshold)){
				System.out.println("Threshhold activated!");
				break;
			}
			
			
			
		}
		
		//Filter those that do not respect integrity constraints
//		List<FeatureModelConfiguration> filteredResult = new ArrayList<FeatureModelConfiguration>();
//		for(FeatureModelConfiguration fmc : result)
//				if(fmc.hasValidIntegrityConstraints(this)){
//					filteredResult.add(fmc);
//					
//				}
		
		
		
		return result;
	}
	
	public FeatureModelConfiguration getARandomConfiguration() throws Exception{
		

		List<DecisionPoint> decisionPoints = new ArrayList<DecisionPoint>();
		int dpPointer = -1;
		Queue<DecisionPoint> processingQueue = new LinkedList<DecisionPoint>();
		Boolean allDone = false;
		
		List<Feature> curConfiguration = new ArrayList<Feature>();
		
		DecisionPoint rootDP = new DecisionPoint(rootFeature,true);
		processingQueue.add(rootDP);
		
		//fill decision points
		while(!processingQueue.isEmpty()){
			DecisionPoint newDP = processingQueue.remove();
			Feature curFeature = newDP.getFeature();
			
			
			if(!curFeature.isAlternative()&& !curFeature.isOrGroup()){
				for(Feature childFeature: curFeature.getChildren())
					if(!childFeature.isOptional()){
						DecisionPoint nnDP = new DecisionPoint(childFeature,true);
						processingQueue.add(nnDP);
						newDP.getChildren().add(nnDP);
						
					}
					
			}
			
			if(!newDP.getPossibleSelection().isEmpty()){
				List<Feature> curSelection = newDP.getPossibleSelection().get(0);
				newDP.setCurrentSelection(curSelection);
				newDP.getPossibleSelection().remove(0);
				for(Feature childFeature: curSelection){
					DecisionPoint nnDP = new DecisionPoint(childFeature,true);
					processingQueue.add(nnDP);
					newDP.getChildren().add(nnDP);
				}
				
			}
			decisionPoints.add(newDP);
			dpPointer++;
		}		
		//fill first configuration
		curConfiguration = rootDP.getCurConfiguration();
		FeatureModelConfiguration nfmc  = new FeatureModelConfiguration(curConfiguration);
		
		
		if(nfmc.hasValidIntegrityConstraints(this)){
			return nfmc;
			
		}
		else
		{

			//update decision points
			while(!allDone){
				curConfiguration = new ArrayList<Feature>();
				DecisionPoint cDP = decisionPoints.get(dpPointer);
				
				while((dpPointer>=0) && decisionPoints.get(dpPointer).getPossibleSelection().isEmpty()){
					
					
					dpPointer--;
				}
				
				
				if((dpPointer<0))
				{
					allDone = true;
					continue;
				}
				cDP = decisionPoints.get(dpPointer);
				
				for(Feature ft : cDP.getCurrentSelection()){
					DecisionPoint featureDP = null;
					for(DecisionPoint dp: decisionPoints )
						if(dp.getFeature().equals(ft)){
							featureDP= dp;
							break;
						}
									
					removeFeature(decisionPoints,featureDP);
				}
				
				if(!cDP.getFeature().isAlternative()&& !cDP.getFeature().isOrGroup()){
					for(Feature ft : cDP.getFeature().getChildren()){
						if(!ft.isOptional())
						{
							DecisionPoint featureDP = null;
							for(DecisionPoint dp: decisionPoints )
								if(dp.getFeature().equals(ft)){
									featureDP= dp;
									break;
								}
											
							removeFeature(decisionPoints,featureDP);
						}
					}
				}	
				
				List<Feature> curSelection = cDP.getPossibleSelection().get(0);
				cDP.getProcessedSelection().add(cDP.getCurrentSelection());
				cDP.setCurrentSelection(curSelection);
				cDP.getPossibleSelection().remove(0);
				
				cDP.setChildren(new ArrayList<DecisionPoint>());
				
				Feature cf = cDP.getFeature();
				if(!cf.isAlternative()&& !cf.isOrGroup()){
					for(Feature childFeature: cf.getChildren())
						if(!childFeature.isOptional()){
							DecisionPoint nnDP = new DecisionPoint(childFeature,true);
							processingQueue.add(nnDP);
							cDP.getChildren().add(nnDP);
						}
						
				}
				
				for(Feature childFeature: curSelection){
					DecisionPoint nnDP = new DecisionPoint(childFeature,true);
					processingQueue.add(nnDP);
					cDP.getChildren().add(nnDP);
				}
				
				while(dpPointer+1< decisionPoints.size()){
					DecisionPoint curDP =  decisionPoints.get(dpPointer+1);
					for(DecisionPoint featureDP: curDP.getChildren())
						removeFeature(decisionPoints, featureDP);
					DecisionPoint nnDP = new DecisionPoint(curDP.getFeature(),true);
					curDP.setProcessedSelection(nnDP.getProcessedSelection());
					curDP.setCurrentSelection(nnDP.getCurrentSelection());
					curDP.setPossibleSelection(nnDP.getPossibleSelection());
					curDP.setChildren(nnDP.getChildren());
				
					processingQueue.add(curDP);
					decisionPoints.remove(dpPointer+1);
				}
				
				
//				System.out.println();
				
//				System.out.println(rootDP.toString2(""));
				
				
				
				while(!processingQueue.isEmpty()){
					DecisionPoint newDP = processingQueue.remove();
					Feature curFeature = newDP.getFeature();
					
					if(!curFeature.isAlternative()&& !curFeature.isOrGroup()){
						for(Feature childFeature: curFeature.getChildren())
							if(!childFeature.isOptional()){
								DecisionPoint nnDP = new DecisionPoint(childFeature,true);
								processingQueue.add(nnDP);
								newDP.getChildren().add(nnDP);
							}
							
					}
					
					if(!newDP.getPossibleSelection().isEmpty()){
						curSelection = newDP.getPossibleSelection().get(0);
						newDP.setCurrentSelection(curSelection);
						newDP.getPossibleSelection().remove(0);
						for(Feature childFeature: curSelection){
							DecisionPoint nnDP = new DecisionPoint(childFeature,true);
							processingQueue.add(nnDP);
							newDP.getChildren().add(nnDP);
						}
					}
					decisionPoints.add(newDP);
					dpPointer++;
//					System.out.println();
//					
//					System.out.println(rootDP.toString2(""));
					
				}
				
				//fill first configuration
				curConfiguration = rootDP.getCurConfiguration();
				nfmc  = new FeatureModelConfiguration(curConfiguration);
//				System.out.println();
//				System.out.println();
//				System.out.println();
//				System.out.println(rootDP.toString2(""));
//				System.out.println();
//				System.out.println(decisionPoints.toString());
//				System.out.println();
//				System.out.println(nfmc);
//				System.out.println(result.size());
				
//				if(result.contains(nfmc))
//					throw new Exception("A configuration found that already that has been already found");
				
				
				//Filter those that do not respect integrity constraints
				if(nfmc.hasValidIntegrityConstraints(this)){
					return nfmc;
					
				}
				
				
				
				
			}
			
			throw new Exception("Random Configuration cannot be found");
		
		}
		
	}


	public void removeFeature(List<DecisionPoint> decisionPoints, DecisionPoint featureDP) {
		
		decisionPoints.remove(featureDP);

		for(DecisionPoint dp : featureDP.getChildren())
			removeFeature(decisionPoints, dp);
		
	}


	public AdaptationResult adapt(DomainModels dm,FeatureModelConfiguration fmc, FlowComponentNode fcn, Service s,ContextStateModel contextStateModel) throws Exception {
		AdaptationResult ar = new AdaptationResult();
		if((s!=null) && !contextStateModel.getServiceAvailabilty().get(s.getURI())){
			long curTime = System.currentTimeMillis();
			List<Service> altServices = dm.getServiceCollection().FindEquivalentServices(s, contextStateModel.getServiceAvailabilty());
			long duration = System.currentTimeMillis()-curTime;
			ar.setServiceAdaptationTime(duration);
			if(altServices.size()!=0){
			
				ar.setAdaptationType(AdaptationResult.SERVICE_ADAPTATION);
				ar.setReplacementService(altServices.get(0));
				
				return ar;
			}
		
		}
		
		// Try replanning
		long curTime = System.currentTimeMillis();
		FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(dm, fmc,contextStateModel);
		boolean alternatePlanExists = true;
		long duration = 0;
		try{
			fmcmg.convertToPDDL();
			fmcmg.Callplanner();
			duration = System.currentTimeMillis()-curTime;
			ar.setWorkflowAdaptationTime(duration);
			fmcmg.AnalyzePlan();
			fmcmg.OptimizeGraph();
			fmcmg.GenerateBPEL();
			
		}
		catch (UnsuccessfulMashupGeneration ex) {
			alternatePlanExists = false;
			duration = System.currentTimeMillis()-curTime;
			ar.setWorkflowAdaptationTime(duration);

		}

		if(alternatePlanExists){
			ar.setAdaptationType(AdaptationResult.WORKFLOW_ADAPTATION);
			ar.setAlternateFlow(fmcmg.getServiceMashupBPELProcess());
			
			return ar;
		}
				
		// Try feature adaptation
		curTime = System.currentTimeMillis();
		Holder<Integer> holder = new Holder<Integer>(0);				
		ar.setAlternateFeatureModelConfiguration(fmc.findAlternateConfiguration(dm,contextStateModel,holder));
		ar.setNoOfTries(holder.value);
		duration = System.currentTimeMillis()-curTime;
		
		ar.setFeatureAdaptationTime(duration);
		
		if(ar.getAlternateFeatureModelConfiguration()==null){
			ar.setAdaptationType(AdaptationResult.SERVICE_FAILURE);
		}
		else
		{
			ar.setAdaptationType(AdaptationResult.FEATURE_ADAPTATION);
		}
		
		return ar;
	}


	public int getDistance(FeatureModelConfiguration featureModelConfiguration1,
			FeatureModelConfiguration featureModelConfiguration2) {
		Set<Feature> allFeatures = new HashSet<Feature>();
		allFeatures.addAll(featureModelConfiguration1.getFeatureList());
		allFeatures.addAll(featureModelConfiguration2.getFeatureList());
		int distance = 0;
		
		for(Feature f: allFeatures)
			if(!featureModelConfiguration1.getFeatureList().contains(f)|| !featureModelConfiguration2.getFeatureList().contains(f))
				distance++;
		
		return distance;
		
	}


	public List<FeatureModelConfiguration> getAllValidConfiguration(int threshold, int fmcSize, int fmcVariation) throws Exception {
		List<FeatureModelConfiguration> fmcs = getAllValidConfiguration(threshold);
		List<FeatureModelConfiguration> result = new ArrayList<FeatureModelConfiguration>();
		
		for(FeatureModelConfiguration fmc:fmcs){
			int noOfFeatures = fmc.getFeatureList().size();
			if((noOfFeatures<fmcSize-fmcVariation ) ||(noOfFeatures>fmcSize+fmcVariation ))
				continue;
			result.add(fmc);
		}
		
		return result;
	}

	public FeatureAtomicSetMap findAtomicSets(){
		FeatureAtomicSetMap result = new FeatureAtomicSetMap();
		
		Set<Feature> processedFeatures = new HashSet<Feature>();
		Queue<Feature> processingQueue = new LinkedList<Feature>();
		
		processingQueue.add(getRootFeature());
		
		while(!processingQueue.isEmpty()){
			
			
			Feature curFeature = processingQueue.remove();
			
			if(!processedFeatures.contains(curFeature) && (!curFeature.isGroupingFeature() || (curFeature.isGroupingFeature() && curFeature.getChildren().size()<2)) ){
				
				
				AtomicSet curAS = new AtomicSet();
				
				Set<Feature> atomicFeaturesMain = getAtomicFeatures(curFeature);
				Set<Feature> atomicFeatures = new HashSet<Feature>();
				atomicFeatures.addAll(atomicFeaturesMain);

				Feature pf = getFeatureParent(curFeature);
				
				while((pf!=null )&&(pf.isGroupingFeature())){
					if(!atomicFeatures.contains(pf))
						atomicFeatures.add(pf);
					pf =getFeatureParent(pf);
				}
				
				
				curAS.getFeatureList().addAll(atomicFeatures);
				curAS.getMainFeatureList().addAll(atomicFeaturesMain);
				processedFeatures.addAll(atomicFeatures);
				
				for(Feature f: atomicFeatures)
					result.getFasMap().put(f, curAS);
			
			}
			
			
			for(Feature f: curFeature.getChildren()){
				processingQueue.add(f);
			}
						
			
		}
		 
		
		return result;
	}


	private Set<Feature> getAtomicFeatures(Feature curFeature) {
		Set<Feature> atomicFeatures =  new HashSet<Feature>();
		atomicFeatures.add(curFeature);
		
		for(Feature f: curFeature.getChildren()){
			if(((curFeature.isAlternative()|| curFeature.isOrGroup())&&
					(curFeature.getChildren().size()<2)) 
					||					
					((!curFeature.isAlternative()&& !curFeature.isOrGroup())&&
					(!f.isOptional()))){
				
				atomicFeatures.addAll( getAtomicFeatures(f));
				
			}
		}
		
		
		
		return atomicFeatures;
	}


	public List<FeatureModelConfiguration> generateRegressionConfigurations(int noOfObservationPerRegressand, FeatureAtomicSetMap fasm) throws Exception {
		return generateRegressionConfigurations(noOfObservationPerRegressand, fasm, new ArrayList<FeatureModelConfiguration>());
	}
	
	
	public List<FeatureModelConfiguration> generateRegressionConfigurations(int noOfObservationPerRegressand, FeatureAtomicSetMap fasm, List<FeatureModelConfiguration> exclusions) throws Exception {
		
		List<FeatureModelConfiguration> result = new ArrayList<FeatureModelConfiguration>();
		
		List<AtomicSet> allAS =  fasm.getAllAtomicSets(false);
		
		AtomicSet rootAtomicSet = fasm.getFasMap().get(this.getRootFeature());
		
		// make sure there are configurations with and without atomic set except root atomic set
		for(AtomicSet as : allAS){
			if(as.equals(rootAtomicSet)|| as.isSingleSelectionStateFeature())
				continue;
			
			
			
			//System.out.println("Investigating "+ as.getFeatureList());
			
			boolean isSelectedInObservations = false;
			
			for(FeatureModelConfiguration fmc : result)
				if(fmc.getFeatureAtomicSetStatus(as.getFeatureList()))
				{
					isSelectedInObservations = true;
					break;
				}
			
			
			
			if(!isSelectedInObservations){
				
				
				//System.out.println("Generating configurations with the atomic set selected.");
			
				FeatureModelConfigurator fmcrSelected = new FeatureModelConfigurator(this);
				
				
				fmcrSelected.setFeatureSetSelectionStatus(as, FeatureSelectionStatus.Selected);
				
				List<FeatureModelConfiguration> fmcListSelected = fmcrSelected.configureRestOfFeauresRandomly(noOfObservationPerRegressand/2);
				
				fmcListSelected.removeAll(exclusions);
				
				if(fmcListSelected.isEmpty()){
					as.setSingleSelectionStateFeature(true);
					continue;
				}
				
				
				
//				for(FeatureModelConfiguration fmc: fmcListSelected){
//					System.out.println(fmc.toString());
//				}
				
				result.addAll(fmcListSelected);
			}
			
			boolean isUnselectedInObservations = false;
			
			for(FeatureModelConfiguration fmc : result)
				if(!fmc.getFeatureAtomicSetStatus(as.getFeatureList()))
				{
					isUnselectedInObservations = true;
					break;
				}
			
			if(!isUnselectedInObservations){
				
				//System.out.println("Generating configurations with the atomic set unselected.");
			
				FeatureModelConfigurator fmcrUnselected = new FeatureModelConfigurator(this);
				
				
				fmcrUnselected.setFeatureSetSelectionStatus(as, FeatureSelectionStatus.Unselected);
				
				List<FeatureModelConfiguration> fmcListUnselected = fmcrUnselected.configureRestOfFeauresRandomly(noOfObservationPerRegressand/2);
				
				fmcListUnselected.removeAll(exclusions);
				
				if(fmcListUnselected.isEmpty()){
					as.setSingleSelectionStateFeature(true);
					continue;
				}
				
//				if(fmcListUnselected.isEmpty()){
//					throw new Exception("An atomic set "+as.getFeatureList() + " cannot be unselected!");
//				}
				
				
//				for(FeatureModelConfiguration fmc: fmcListUnselected){
//					System.out.println(fmc.toString());
//				}
				
				result.addAll(fmcListUnselected);	
			}
			
			
		}
		
		 
		
		
		// find and fix multicollinearity situations
		
		Boolean multicollinearityExists = true;
		
		while(multicollinearityExists){
			
			multicollinearityExists =false;
			
			for(AtomicSet as1 : allAS){
				for(AtomicSet as2 : allAS){
					if(as1.equals(as2) || as1.isSingleSelectionStateFeature() || as2.isSingleSelectionStateFeature())
						continue;
					Boolean sameStatusForAllObservations = true;
					
					for(FeatureModelConfiguration fmc:result){
						if(fmc.getFeatureAtomicSetStatus(as1.getFeatureList())!= fmc.getFeatureAtomicSetStatus(as2.getFeatureList()))
						{
							sameStatusForAllObservations = false;
							break;
						}
					}
					
					if(sameStatusForAllObservations){
						multicollinearityExists = true;
						
						AtomicSet selectedAtomicSet ,unselectedAtomicSet;
						
						if(as1.equals(rootAtomicSet)){
							selectedAtomicSet = as1;
							unselectedAtomicSet = as2;
						}
						else{
							selectedAtomicSet = as2;
							unselectedAtomicSet = as1;
						}
						
						FeatureModelConfigurator fmcr = new FeatureModelConfigurator(this);
						boolean unselectableCombination = false;
						
						try{
							fmcr.setFeatureSetSelectionStatus(selectedAtomicSet, FeatureSelectionStatus.Selected);
							fmcr.setFeatureSetSelectionStatus(unselectedAtomicSet, FeatureSelectionStatus.Unselected);
						}
						catch(PreSetFeatureSelection ex)
						{
							unselectableCombination = true;
						}
						
						List<FeatureModelConfiguration> fmcList = new ArrayList<FeatureModelConfiguration>();
						
						if(!unselectableCombination)
							fmcList = fmcr.configureRestOfFeauresRandomly(2);						
						
						if(fmcList.isEmpty()){
							if(!selectedAtomicSet.equals(rootAtomicSet)){
								fmcr = new FeatureModelConfigurator(this);
								unselectableCombination = false;
								try{
									fmcr.setFeatureSetSelectionStatus(selectedAtomicSet, FeatureSelectionStatus.Unselected);
									fmcr.setFeatureSetSelectionStatus(unselectedAtomicSet, FeatureSelectionStatus.Selected);
								}
								catch(PreSetFeatureSelection ex)
								{
									unselectableCombination = true;
								}
								
								if(!unselectableCombination)
									fmcList = fmcr.configureRestOfFeauresRandomly(2);
								
								
								
							}
							
							
							
						}
						
						fmcList.removeAll(exclusions);
						
						if(fmcList.isEmpty()){
							fasm.mergeAtomicSets(as1,as2);
							 allAS =  fasm.getAllAtomicSets(false);
							continue;
							//throw new Exception("An atomic set "+selectedAtomicSet.getFeatureList() +" and "+ selectedAtomicSet.getFeatureList()+" cannot have different selection status!");
						}
						
						result.addAll(fmcList);
						
						
						break;
					}
				}
			}
			
		}
		
		if(result.size()< noOfObservationPerRegressand*allAS.size()+1)
		{
			//FeatureModelConfigurator fmcr = new FeatureModelConfigurator(this);
			List<FeatureModelConfiguration> newexclusions = new ArrayList<FeatureModelConfiguration>();
			newexclusions.addAll(result);
			newexclusions.addAll(exclusions);
			result.addAll(generateRandomFeatureModelConfiguration(noOfObservationPerRegressand*allAS.size()-result.size()+1,  newexclusions));
			
		}
		//generate random configurations for rest
		
		
		
		return result;
	}


	public Feature getFeatureParent(Feature f) {
		List<Feature> flst = getFeatureList();
		Feature result = null;
		for(Feature cf: flst)
			if(cf.getChildren().contains(f))
			{
				result=cf;
				break;
			}
		
		
		
		return result;
	}


	public List<FeatureModelConfiguration> generateRandomFeatureModelConfiguration(int numberOfConfigurations, List<FeatureModelConfiguration> exclusions) throws Exception {
		
		List<FeatureModelConfiguration> allconf = getAllValidConfiguration(numberOfConfigurations+exclusions.size());
		
		if((allconf.size()-exclusions.size())<numberOfConfigurations){
			allconf.removeAll(exclusions);
			return allconf;
		}
		
		List<FeatureModelConfiguration> result = new ArrayList<FeatureModelConfiguration>();
		
		while(result.size()<numberOfConfigurations)
		{
			FeatureModelConfiguration fmc = getARandomConfiguration();
			
			if(!result.contains(fmc)&&!exclusions.contains(fmc))
				result.add(fmc);
		}
		
		return result;
		
	}
	
	public List<FeatureModelConfiguration> generateRandomFeatureModelConfiguration(int numberOfConfigurations) throws Exception {
		return generateRandomFeatureModelConfiguration(numberOfConfigurations,new ArrayList<FeatureModelConfiguration>());
	}


	public FeatureAtomicSetMap findActualAtomicSets() throws Exception {
		
		FeatureAtomicSetMap fasm = findAtomicSets();
		
		List<FeatureModelConfiguration> result = new ArrayList<FeatureModelConfiguration>();
		
		List<AtomicSet> allAS =  fasm.getAllAtomicSets(false);
		
		AtomicSet rootAtomicSet = fasm.getFasMap().get(this.getRootFeature());
		
		// make sure there are configurations with and without atomic set except root atomic set
		for(AtomicSet as : allAS){
			if(as.equals(rootAtomicSet)|| as.isSingleSelectionStateFeature())
				continue;
			
			
			
			//System.out.println("Investigating "+ as.getFeatureList());
			
			boolean isSelectedInObservations = false;
			
			for(FeatureModelConfiguration fmc : result)
				if(fmc.getFeatureAtomicSetStatus(as.getFeatureList()))
				{
					isSelectedInObservations = true;
					break;
				}
			
			
			
			if(!isSelectedInObservations){
				
				
				//System.out.println("Generating configurations with the atomic set selected.");
			
				FeatureModelConfigurator fmcrSelected = new FeatureModelConfigurator(this);
				
				
				fmcrSelected.setFeatureSetSelectionStatus(as, FeatureSelectionStatus.Selected);
				
				List<FeatureModelConfiguration> fmcListSelected = fmcrSelected.configureRestOfFeauresRandomly(1);
				
				
				
				if(fmcListSelected.isEmpty()){
					as.setSingleSelectionStateFeature(true);
					continue;
				}
				
				
				
//				for(FeatureModelConfiguration fmc: fmcListSelected){
//					System.out.println(fmc.toString());
//				}
				
				result.addAll(fmcListSelected);
			}
			
			boolean isUnselectedInObservations = false;
			
			for(FeatureModelConfiguration fmc : result)
				if(!fmc.getFeatureAtomicSetStatus(as.getFeatureList()))
				{
					isUnselectedInObservations = true;
					break;
				}
			
			if(!isUnselectedInObservations){
				
				//System.out.println("Generating configurations with the atomic set unselected.");
			
				FeatureModelConfigurator fmcrUnselected = new FeatureModelConfigurator(this);
				
				
				fmcrUnselected.setFeatureSetSelectionStatus(as, FeatureSelectionStatus.Unselected);
				
				List<FeatureModelConfiguration> fmcListUnselected = fmcrUnselected.configureRestOfFeauresRandomly(1);
				
				
				
				if(fmcListUnselected.isEmpty()){
					as.setSingleSelectionStateFeature(true);
					continue;
				}
				
//				if(fmcListUnselected.isEmpty()){
//					throw new Exception("An atomic set "+as.getFeatureList() + " cannot be unselected!");
//				}
				
				
//				for(FeatureModelConfiguration fmc: fmcListUnselected){
//					System.out.println(fmc.toString());
//				}
				
				result.addAll(fmcListUnselected);	
			}
			
			
		}
		
		 
		
		
		// find and fix multicollinearity situations
		
		Boolean multicollinearityExists = true;
		
		while(multicollinearityExists){
			
			multicollinearityExists =false;
			
			for(AtomicSet as1 : allAS){
				for(AtomicSet as2 : allAS){
					if(as1.equals(as2) || as1.isSingleSelectionStateFeature() || as2.isSingleSelectionStateFeature())
						continue;
					Boolean sameStatusForAllObservations = true;
					
					for(FeatureModelConfiguration fmc:result){
						if(fmc.getFeatureAtomicSetStatus(as1.getFeatureList())!= fmc.getFeatureAtomicSetStatus(as2.getFeatureList()))
						{
							sameStatusForAllObservations = false;
							break;
						}
					}
					
					if(sameStatusForAllObservations){
						multicollinearityExists = true;
						
						AtomicSet selectedAtomicSet ,unselectedAtomicSet;
						
						if(as1.equals(rootAtomicSet)){
							selectedAtomicSet = as1;
							unselectedAtomicSet = as2;
						}
						else{
							selectedAtomicSet = as2;
							unselectedAtomicSet = as1;
						}
						
						FeatureModelConfigurator fmcr = new FeatureModelConfigurator(this);
						boolean unselectableCombination = false;
						
						try{
							fmcr.setFeatureSetSelectionStatus(selectedAtomicSet, FeatureSelectionStatus.Selected);
							fmcr.setFeatureSetSelectionStatus(unselectedAtomicSet, FeatureSelectionStatus.Unselected);
						}
						catch(PreSetFeatureSelection ex)
						{
							unselectableCombination = true;
						}
						
						List<FeatureModelConfiguration> fmcList = new ArrayList<FeatureModelConfiguration>();
						
						if(!unselectableCombination)
							fmcList = fmcr.configureRestOfFeauresRandomly(2);						
						
						if(fmcList.isEmpty()){
							if(!selectedAtomicSet.equals(rootAtomicSet)){
								fmcr = new FeatureModelConfigurator(this);
								unselectableCombination = false;
								try{
									fmcr.setFeatureSetSelectionStatus(selectedAtomicSet, FeatureSelectionStatus.Unselected);
									fmcr.setFeatureSetSelectionStatus(unselectedAtomicSet, FeatureSelectionStatus.Selected);
								}
								catch(PreSetFeatureSelection ex)
								{
									unselectableCombination = true;
								}
								
								if(!unselectableCombination)
									fmcList = fmcr.configureRestOfFeauresRandomly(2);
								
								
								
							}
							
							
							
						}
						
						
						
						if(fmcList.isEmpty()){
							fasm.mergeAtomicSets(as1,as2);
							 allAS =  fasm.getAllAtomicSets(false);
							continue;
							//throw new Exception("An atomic set "+selectedAtomicSet.getFeatureList() +" and "+ selectedAtomicSet.getFeatureList()+" cannot have different selection status!");
						}
						
						result.addAll(fmcList);
						
						
						break;
					}
				}
			}
			
		}
		return fasm;
	}
	
	
	
	
	
}

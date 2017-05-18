package edu.ls3.magus.eval.generators.owls;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OntologyGen {
	private  final String templateAddress ="D:\\Development\\BPLECONS\\templates\\ontology";
	private HashMap<String,String> templates;
	private List<String> classes;
	private HashMap<String, List<String>> properties;
	private String uri;
	
	public OntologyGen(String uri, int classNo, int propertyNo){
		this.uri= uri;
		setClasses(new ArrayList<String>());
		for(int cnt=0; cnt<classNo ; cnt++){
			getClasses().add("c" + UUID.randomUUID().toString().substring(1,10).replaceAll("-", ""));
		}
		setProperties(new HashMap<String, List<String>>());
		for(int cnt=0; cnt<propertyNo; cnt++)
		{
			List<String> temp = new ArrayList<String>(2);
			int fno =UtilityClass.randInt(0, classNo-1);
			int sno = UtilityClass.randInt(0, classNo-1);
			while(fno==sno)
				sno = UtilityClass.randInt(0, classNo-1);
			temp.add( getClasses().get(fno ));
			temp.add( getClasses().get( sno));
			getProperties().put("p" + UUID.randomUUID().toString().substring(1,10).replaceAll("-", ""), temp);
		}
	}
	public OntologyGen()
	{
		setClasses(new ArrayList<String>());
		setProperties(new HashMap<String, List<String>>());
	}
	public List<String> getClasses() {
		return classes;
	}

	private void setClasses(List<String> classes) {
		this.classes = classes;
	}

	public HashMap<String, List<String>> getProperties() {
		return properties;
	}

	private void setProperties(HashMap<String, List<String>> properties) {
		this.properties = properties;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	private void initTemplates() throws IOException{
		File folder= new File(templateAddress);
    	templates = new HashMap<String, String>();
    	
    	for (final File fXmlFile : folder.listFiles()) {
    		String temp = UtilityClass.readFile(fXmlFile, StandardCharsets.US_ASCII );
    		//temp = temp.replaceAll("--SERVICE NAME--", serviceName);
    		//temp = temp.replaceAll("--SERVICE NAME LOWERC--", serviceName.toLowerCase());
    		String fname = fXmlFile.getName().replaceFirst("[.][^.]+$", "");
    		templates.put(fname, temp);
    		//System.out.println(fname);
    		//System.out.println(temp);
    	}
    	
	}
	
	public String serializetoRDFXML() throws IOException{
		initTemplates();
		String result = templates.get("ontology");
		result = result.replaceAll("--BASE URI--", uri);
		for(String clss: classes){
			String inpStr = templates.get("class");
			
			inpStr = inpStr.replaceAll("--CLASS ID--", clss);
			
			
			result = result.replaceAll("--CLASSES--", inpStr+System.lineSeparator()+ "--CLASSES--");
		}
		
		result = result.replaceAll("--CLASSES--", "");
		
		for(String prp: properties.keySet()){
			String inpStr = templates.get("property");
			
			inpStr = inpStr.replaceAll("--PROPERTY ID--", prp);
			inpStr = inpStr.replaceAll("--PROPERTY DOMAIN--", properties.get(prp).get(0));
			inpStr = inpStr.replaceAll("--PROPERTY RANGE--", properties.get(prp).get(1));
			result = result.replaceAll("--PROPERTIES--", inpStr+System.lineSeparator()+ "--PROPERTIES--");
		}
		
		result = result.replaceAll("--PROPERTIES--", "");
		
		return result;
	}
	public static OntologyGen readfromRDFXML(File inp) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException{
		OntologyGen result = new OntologyGen();
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inp);
		
		String baseURI = "";
		
		String rootExpression = "//*[name()='rdf:RDF']";
		XPath xPath =  XPathFactory.newInstance().newXPath();
		Node root = (Node) xPath.compile(rootExpression).evaluate(doc, XPathConstants.NODE);
		baseURI = root.getAttributes().getNamedItem("xml:base").getNodeValue();
		System.out.println("Base URI: " +baseURI);
		result.uri = baseURI;
		
		
		String classNameExpression = "//*[name()='rdf:RDF']//*[name()='owl:Class']";
		
		
		xPath =  XPathFactory.newInstance().newXPath();
		NodeList classtags = (NodeList) xPath.compile(classNameExpression).evaluate(doc, XPathConstants.NODESET);
		for(int cnt =0; cnt < classtags.getLength(); cnt++){
			Node curNode = classtags.item(cnt);
			String classname ="";
			classname = curNode.getAttributes().getNamedItem("rdf:ID").getNodeValue();
			result.classes.add(classname);
			System.out.println("Class Name: " +classname);
		}
		
		String propertyNameExpression = "//*[name()='rdf:RDF']//*[name()='owl:ObjectProperty']";
		xPath =  XPathFactory.newInstance().newXPath();
		NodeList propertytags = (NodeList) xPath.compile(propertyNameExpression).evaluate(doc, XPathConstants.NODESET);
		for(int cnt =0; cnt < propertytags.getLength(); cnt++){
			Node curNode = propertytags.item(cnt);
			String propertyName ="";
			propertyName = curNode.getAttributes().getNamedItem("rdf:ID").getNodeValue();
			System.out.println("Property Name: " +propertyName);
			List<String> args = new ArrayList<String>();
			NodeList curChildNodes = curNode.getChildNodes();
			for(int ccnt =0 ; ccnt<curChildNodes.getLength(); ccnt++){
				if(curChildNodes.item(ccnt).getNodeName().equals("rdfs:domain"))
				{
					Node paramTypeNode = curChildNodes.item(ccnt);
					String prpdomain = UtilityClass.Clean( paramTypeNode.getAttributes().getNamedItem("rdf:resource").getNodeValue());
					System.out.println("Property Domain:" + prpdomain);
					args.add(prpdomain);
					
				}
				if(curChildNodes.item(ccnt).getNodeName().equals("rdfs:range"))
				{
					Node paramTypeNode = curChildNodes.item(ccnt);
					String prprange =  UtilityClass.Clean(paramTypeNode.getAttributes().getNamedItem("rdf:resource").getNodeValue());
					System.out.println("Property Range:" + prprange);
					args.add(prprange);
					
				}
			}
			result.properties.put(propertyName, args);
		}
		
		
		return result;
	}
	
	 public static void main( String[] args ) throws Exception
     {
		//OntologyGen o = new OntologyGen("http://ls3.rnet.ryerson.ca/people/mahdi/magus/owl/eval.owl",100,5000);
		OntologyGen o = new OntologyGen("http://ls3.rnet.ryerson.ca/people/mahdi/magus/owl/eval.owl",30,600);
		UtilityClass.writeFile(new File("D:\\Development\\BPLECONS\\dataset3\\ontology.xml"), o.serializetoRDFXML()); 
     }
	
	
	
}

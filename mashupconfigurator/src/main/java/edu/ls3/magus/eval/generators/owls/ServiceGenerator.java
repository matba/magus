package edu.ls3.magus.eval.generators.owls;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ServiceGenerator {
	static final String homeAddress="/home/mbashari/";
	private  final static String templateAddressUbuntu =homeAddress+"BPLECONS/templates/service";
	private  final static String templateAddress ="D:\\Development\\BPLECONS\\templates\\service";
	private HashMap<String,String> templates;
	private Set<String> inputs;
	private Set<String> outputs;
	private Set<String> preconditions;
	private Map<String, Boolean> notMapPrecondition;
	private Set<String> effects;
	private Map<String, Boolean> notMapEffect;
	private String serviceName;
	public static ServiceGenerator GenerateService(OntologyGen on, Set<String> preconditionSet, Map<String,Boolean> NotMap ,  int precNo, int effNo, String serviceName){
		
		ServiceGenerator result = new ServiceGenerator();
		result.serviceName = serviceName;
		for(int cnt=0; (cnt<precNo) && (cnt< preconditionSet.size()); cnt++){
			String pname = (String) preconditionSet.toArray()[UtilityClass.randInt(0, preconditionSet.size()-1)];
			if(result.preconditions.contains(pname)){
				cnt--;
				continue;
			}
			
			result.preconditions.add(pname);
			result.notMapPrecondition.put(pname, NotMap.get(pname));
			
			if(!result.inputs.contains(on.getProperties().get(pname).get(0)))
				result.inputs.add(on.getProperties().get(pname).get(0));
			
			if(!result.inputs.contains(on.getProperties().get(pname).get(1)))
				result.inputs.add(on.getProperties().get(pname).get(1));
		}
		Set<String> cons = findPropertiesWithThisType(on, result.inputs);
		for(int cnt=0; cnt<effNo; cnt++){
			String pname = (String) cons.toArray()[UtilityClass.randInt(0, cons.size()-1)];
			if(result.effects.contains(pname)){
				cnt--;
				continue;
			}
			//boolean not =false;
			boolean not = (UtilityClass.randInt(1, 4)==2)?true:false;
			if(result.preconditions.contains(pname) && (result.notMapPrecondition.get(pname)==not))
			{
				cnt--;
				continue;
			}
			
			result.effects.add(pname);
			result.notMapEffect.put(pname, not);
			if(!result.inputs.contains(on.getProperties().get(pname).get(0)) && !result.outputs.contains(on.getProperties().get(pname).get(0)))
				result.outputs.add(on.getProperties().get(pname).get(0));
			
			if(!result.inputs.contains(on.getProperties().get(pname).get(1)) && !result.outputs.contains(on.getProperties().get(pname).get(1)))
				result.outputs.add(on.getProperties().get(pname).get(1));
			
		}
		
		
		return result;
	}
	private static Set<String> findPropertiesWithThisType(OntologyGen on,
			Set<String> inputs2) {
		
		if(inputs2.size()==0)
			return on.getProperties().keySet();
		Set<String> result = new HashSet<String>();
		for(String k :on.getProperties().keySet()){
			//if(exc.contains(k))
			//	continue;
			if(inputs2.contains(on.getProperties().get(k).get(0)) || inputs2.contains(on.getProperties().get(k).get(1)))
				result.add(k);
			
		}
		return result;
	}
	public ServiceGenerator()
	{
		inputs = new HashSet<String>();
		outputs = new HashSet<String>();
		preconditions = new HashSet<String>();
		effects = new HashSet<String>();
		serviceName ="NS";
		notMapPrecondition = new HashMap<String, Boolean>();
		notMapEffect = new HashMap<String, Boolean>();
	}
	
	private void initTemplates(String templateAddress) throws IOException{
		File folder= new File(templateAddress);
    	templates = new HashMap<String, String>();
    	
    	for (final File fXmlFile : folder.listFiles()) {
    		String temp = UtilityClass.readFile(fXmlFile, StandardCharsets.US_ASCII );
    		temp = temp.replaceAll("--SERVICE NAME--", serviceName);
    		temp = temp.replaceAll("--SERVICE NAME LOWERC--", serviceName.toLowerCase());
    		String fname = fXmlFile.getName().replaceFirst("[.][^.]+$", "");
    		templates.put(fname, temp);
    		//System.out.println(fname);
    		//System.out.println(temp);
    	}
    	
	}
	
	public String owlsSerialize(OntologyGen on,String templateAddress) throws IOException{
		initTemplates(templateAddress);
		String st = templates.get("service");
		for(String inps: inputs){
			String inpStr = templates.get("input");
			inpStr = inpStr.replaceAll("--OUTPUT TYPE URI--", on.getUri()+"#"+inps);
			inpStr = inpStr.replaceAll("--OUTPUT ID--", "var"+inps);
			st = st.replaceAll("--INPUT TAGS--", inpStr+System.lineSeparator()+ "--INPUT TAGS--");
		}
		
		st = st.replaceAll("--INPUT TAGS--", "");
		
		
		
		for(String outs: outputs){
			String inpStr = templates.get("output");
			inpStr = inpStr.replaceAll("--OUTPUT TYPE URI--", on.getUri()+"#"+outs);
			inpStr = inpStr.replaceAll("--OUTPUT ID--", "var"+outs);
			st = st.replaceAll("--OUTPUT TAGS--", inpStr+System.lineSeparator()+ "--OUTPUT TAGS--");
		}
			
		st = st.replaceAll("--OUTPUT TAGS--", "");
		
		if(preconditions.isEmpty())
			st = st.replaceAll("--PREC EL--", "");
		else
		{
			String preStr =  templates.get("precondition");
			
			boolean firstTime = true;
			
			
			for(String pre: preconditions){
				String conStr = templates.get("condition");
				
				
				
				String wpre = pre;
				if(notMapPrecondition.get(pre))
					wpre = "!"+pre;
				
				conStr = conStr.replaceAll("--PROPERTY NAME--",  wpre);
				conStr = conStr.replaceAll("--ARG1--", "var"+on.getProperties().get(pre).get(0));
				conStr = conStr.replaceAll("--ARG2--", "var"+on.getProperties().get(pre).get(1));
				
				if(!firstTime){
					preStr = preStr.replaceAll("--REST--", templates.get("rest"));
				}
				firstTime = false;
				preStr = preStr.replaceAll("--CONDITION--", conStr);
				
			}
			
			preStr = preStr.replaceAll("--REST--", templates.get("restnil"));
			st = st.replaceAll("--PREC EL--",  preStr);
		}
		
		//EFFECTS
		
		if(effects.isEmpty())
			st = st.replaceAll("--RESULT EL--", "");
		else
		{
			String preStr =  templates.get("effect");
			
			boolean firstTime = true;
			
			
			for(String pre: effects){
				String conStr = templates.get("condition");
				
				String wpre = pre;
				if(notMapEffect.get(pre))
					wpre = "!"+pre;
				
				
				conStr = conStr.replaceAll("--PROPERTY NAME--", wpre);
				conStr = conStr.replaceAll("--ARG1--", "var"+on.getProperties().get(pre).get(0));
				conStr = conStr.replaceAll("--ARG2--", "var"+on.getProperties().get(pre).get(1));
				
				if(!firstTime){
					preStr = preStr.replaceAll("--REST--", templates.get("rest"));
				}
				firstTime = false;
				preStr = preStr.replaceAll("--CONDITION--", conStr);
				
			}
			
			preStr = preStr.replaceAll("--REST--", templates.get("restnil"));
			st = st.replaceAll("--RESULT EL--",  preStr);
		}
		
		
		
		
		
		
		return st;
	}
	 
	 public static void mainWindows( String[] args ) throws Exception
	     {
				 
		 	OntologyGen on = OntologyGen.readfromRDFXML(new File("D:\\Development\\BPLECONS\\ds\\ontology.xml"));
		 	
		 	String p1, p2;
		 	p1 = (String) on.getProperties().keySet().toArray()[0];
		 	p2 = (String) on.getProperties().keySet().toArray()[1];
		 		
		 	
		 	
		 	System.out.println(p1);
		 	System.out.println(p2);
		 	
		 	int[] reqNo = {3,6,9};
		 	Integer[] nos ={100,200,400,500,600,800,1000};
	//	 	
	///	 	int[] reqNo = {3};
	//Integer[] nos ={100};
		 	for(int rno:reqNo){
			 	edu.ls3.magus.utility.UtilityClass.createFolder("D:\\Development\\BPLECONS\\ds2\\services\\r"+rno);
		 		for(Integer no: nos){
			 		Set<String> ps = new HashSet<String>();
			 		Map<String, Boolean> psm= new HashMap<String, Boolean>();
				 	ps.add( p1); 
				 	ps.add(p2);
				 	psm.put(p1, false);
				 	psm.put(p2, false);
				 	
				 	List<ServiceGenerator> lst = new ArrayList<ServiceGenerator>();
				 	
				 	for(int cnt=0; cnt<no; cnt++)
				 	{
				 		
				 		int noOfPreconditions = UtilityClass.randInt(1,rno-2);
				 		int noOfEffects = rno-noOfPreconditions;
				 		ServiceGenerator sg = ServiceGenerator.GenerateService(on, ps,psm, noOfPreconditions,noOfEffects, "serv"+ UUID.randomUUID().toString().substring(1,10).replaceAll("-", ""));
				 		lst.add(sg);
				 		for(String eff : sg.effects){
				 			if(!ps.contains(eff)){
					 			 ps.add(eff);
					 			 psm.put(eff, sg.notMapEffect.get(eff));
				 			}
				 			else{
				 				if(psm.get(eff)!=sg.notMapEffect.get(eff)){
				 					psm.remove(eff);
				 					psm.put(eff, sg.notMapEffect.get(eff));
				 				}
				 			}
				 		
				 		}
				 		
				 		
				 	}
				 	
				 	//UtilityClass.writeFile(new File("D:\\prob.txt"), sb.toString());
				 	File dir = new File("D:\\Development\\BPLECONS\\ds2\\services\\r"+rno+"\\s"+no.toString());
				 	dir.mkdir();
				 	for(int cnt=0; cnt<lst.size(); cnt++)
				 	{
				 		File outf = new File("D:\\Development\\BPLECONS\\ds2\\services\\r"+rno+"\\s"+no.toString()+"\\"+lst.get(cnt).serviceName+".xml");
				 		UtilityClass.writeFile(outf, lst.get(cnt).owlsSerialize(on,templateAddress));
				 	}
			 	}
		 	}
			//System.out.println(sg.owlsSerialize(on)); 
		 	//sg.initTemplates();
	    
	     }
	public static void main( String[] args ) throws Exception
     {
			 
	 	OntologyGen on = OntologyGen.readfromRDFXML(new File(homeAddress+ "BPLECONS/ds/ontology.xml"));
	 	
	 	String p1, p2;
//	 	p1 = (String) on.getProperties().keySet().toArray()[0];
//	 	p2 = (String) on.getProperties().keySet().toArray()[1];
	 		
	 	p1= "p7f9d1560";
	 	p2= "pfc4299ba";
	 	
	 	System.out.println(p1);
	 	System.out.println(p2);
	 	
	 	int[] reqNo = {3,6,9};
	 	//Integer[] nos ={100,200,400,500,600,800,1000,5000,10000};
	 	//Integer[] nos ={5000,10000};
	 	Integer[] nos ={2000,3000,4000,6000,7000,8000,9000};
//	 	
///	 	int[] reqNo = {3};
//Integer[] nos ={100};
	 	for(int rno:reqNo){
		 	edu.ls3.magus.utility.UtilityClass.createFolder(homeAddress+"BPLECONS/ds/services/r"+rno);
	 		for(Integer no: nos){
		 		Set<String> ps = new HashSet<String>();
		 		Map<String, Boolean> psm= new HashMap<String, Boolean>();
			 	ps.add( p1); 
			 	ps.add(p2);
			 	psm.put(p1, false);
			 	psm.put(p2, false);
			 	
			 	List<ServiceGenerator> lst = new ArrayList<ServiceGenerator>();
			 	
			 	for(int cnt=0; cnt<no; cnt++)
			 	{
			 		
			 		int noOfPreconditions = UtilityClass.randInt(1,rno-2);
			 		int noOfEffects = rno-noOfPreconditions;
			 		ServiceGenerator sg = ServiceGenerator.GenerateService(on, ps,psm, noOfPreconditions,noOfEffects, "serv"+ UUID.randomUUID().toString().substring(1,10).replaceAll("-", ""));
			 		lst.add(sg);
			 		for(String eff : sg.effects){
			 			if(!ps.contains(eff)){
				 			 ps.add(eff);
				 			 psm.put(eff, sg.notMapEffect.get(eff));
			 			}
			 			else{
			 				if(psm.get(eff)!=sg.notMapEffect.get(eff)){
			 					psm.remove(eff);
			 					psm.put(eff, sg.notMapEffect.get(eff));
			 				}
			 			}
			 		
			 		}
			 		
			 		
			 	}
			 	
			 	//UtilityClass.writeFile(new File("D:\\prob.txt"), sb.toString());
			 	File dir = new File(homeAddress+ "BPLECONS/ds/services/r"+rno+"/s"+no.toString());
			 	dir.mkdir();
			 	for(int cnt=0; cnt<lst.size(); cnt++)
			 	{
			 		File outf = new File(homeAddress+ "BPLECONS/ds/services/r"+rno+"/s"+no.toString()+"/"+lst.get(cnt).serviceName+".xml");
			 		UtilityClass.writeFile(outf, lst.get(cnt).owlsSerialize(on,templateAddressUbuntu));
			 	}
		 	}
	 	}
		//System.out.println(sg.owlsSerialize(on)); 
	 	//sg.initTemplates();
    
     }
	
}

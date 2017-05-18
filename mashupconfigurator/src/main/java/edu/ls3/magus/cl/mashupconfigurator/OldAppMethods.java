//package edu.ls3.magus.cl.mashupconfigurator;
//
//import java.io.File;
//import java.net.URI;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.UUID;
//
//import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.OperationNode;
//import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.ServiceCall;
//import edu.ls3.magus.cl.mashupconfigurator.commons.Condition;
//import edu.ls3.magus.cl.mashupconfigurator.commons.DomainModels;
//import edu.ls3.magus.cl.mashupconfigurator.commons.Instance;
//import edu.ls3.magus.cl.mashupconfigurator.commons.Service;
//import edu.ls3.magus.cl.mashupconfigurator.commons.StateFactInstance;
//import edu.ls3.magus.cl.mashupconfigurator.commons.StateFactInstanceS;
//import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.ExecutionTime;
//import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.ServiceNonfunctionalAnnotation;
//import edu.ls3.magus.cl.planning.Problem;
//import edu.ls3.magus.cl.planning.ProblemDomain;
//import edu.ls3.magus.utilitypackage.UtilityClass;
//import jxl.Workbook;
//import jxl.write.Label;
//import jxl.write.WritableSheet;
//import jxl.write.WritableWorkbook;
////import pddl4j.ErrorManager;
////import pddl4j.PDDLObject;
////import pddl4j.Parser;
////import pddl4j.ErrorManager.Message;
////import pddl4j.graphplan.Graphplan;
////import pddl4j.graphplan.Plan;
//
//public class OldAppMethods {
//	static final int ITERATIONS =10;
//	static final int ITERATIONS_FM =20;
//	//static final String homeAddress="/home/matt/";
//	static final String homeAddress="/home/mbashari/";
//	static int filecnt=6000;
//	
//	/**
//     * Old version uses graph plan
//     * @throws Exception
//     */
//    public static void ee4() throws Exception{
//    	int noOfServices = 200;
//    	
//    	UtilityClass.createFolder("D:\\temp");
//    	File dir = new File("D:\\temp");
//    	for(File fl: dir.listFiles() )
//    	{
//    		fl.delete();
//    	}
//    	String curPost = "";
//    
//    	boolean ftime =true;
//    	
//    	Integer[] nos ={ 10, 20, 40, 60, 80,100};
//    	Integer[] reqNoList={2,4,6};
////    	Integer[] nos ={100};
////    	Integer[] reqNoList={6};
//    	//Integer[] nos ={200};
//    	
//    	
//    	for(int reqcnt=0; reqcnt<reqNoList.length; reqcnt++){
//    		
//	    	
//
//	    	//DomainModels dm = DomainModels.ReadModels("D:\\tt\\owlintact2");
//	    	DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\ds\\services\\r6\\s"+noOfServices);
//	    	dm.getContextModel().createSimpleContext();
//	    	
//	    	for(int nocnt=0; nocnt <nos.length; nocnt++){
//	    		File folder = new File("D:\\Development\\BPLECONS\\ds\\featureModelFF"+noOfServices+"\\r"+reqNoList[reqcnt]+"\\"+nos[nocnt]);
//		    	File[] fl =folder.listFiles();
//	    		
//	    		
//	    		System.out.println("****Evaluation for no of feature "+nos[nocnt]+ "and no of reqs "+reqNoList[reqcnt]);
//		    	for(int flcnt =0; flcnt <fl.length ;flcnt++ ){
//		    		System.out.println("Iteration: "+flcnt );
//	    		
//	    			
//	    			
//	    			
//	    			
//			    	//Add Vars
//
//			    	
//			    	long curTime = System.currentTimeMillis();
//			    	ProblemDomain pd = new ProblemDomain(dm);
//			    	String problemDomainpddl =  pd.PDDL3Serialize(curPost);
//			    	long serializationDuration = System.currentTimeMillis()-curTime;
//			    	File a = fl[flcnt];
//			    	System.out.println("File name: "+a.getName());
//			    	Problem pr = Problem.readFromFile(a, dm);
//			    	String problempddl = pr.PDDL3Serialize(curPost);
//			    	
//			    	try{
//			    		UtilityClass.writeFile(new File("D:\\temp\\pt"+curPost+".pddl"),problempddl);
//				    	UtilityClass.writeFile(new File("D:\\temp\\pta"+curPost+".pddl"),problemDomainpddl);
//			    	}
//			    	catch(Exception ex){
//			    		System.out.println("Exception in writing files!");
//			    		curPost= UUID.randomUUID().toString();
//			    		curPost= curPost.replaceAll("-", "");
//			    		flcnt--;
//			    		continue;
//			    	}
//			    	
//			    	
//			    	
//			 
//
//			    	curTime = System.currentTimeMillis();
//			    	//System.out.println(problem);
//			    	//System.out.println(problemDomain);
//			    	
//			    	//GraphPlan gp = new GraphPlan();
//			    	
//			    	//List<String[]> rawplan =gp.init2(problemDomain ,problem, 50, ftime,false);
//			    	
//			    	
//			    	ftime=false;
//			    	
//			    	
//			    	Properties options = Graphplan.getParserOptions();
//			    	Parser parser = new Parser(options);
//	                PDDLObject domain = parser.parse(new File("D:\\temp\\pta"+curPost+".pddl"));
//	                PDDLObject problem = parser.parse(new File("D:\\temp\\pt"+curPost+".pddl"));
//	                PDDLObject pb = null;
//	                List<String[]> rawplan =null;
//	                if (domain != null && problem != null) {
//	                    pb = parser.link(domain, problem);
//	                }
//	                // Gets the error manager of the pddl compiler
//	                ErrorManager mgr = parser.getErrorManager();
//	                // If the compilation produces errors we print it and stop
//	                
//	                if (mgr.contains(Message.ERROR)) {
//	                    mgr.print(Message.ALL);
//	                    throw new Exception("Error parsing");
//	                }
//	                // else we print the warning and start the planning process
//	                else {
//	                    
//	                    mgr.print(Message.WARNING);
//	                    
//	                   // System.out.println("\nParsing domain \"" + domain.getDomainName() + "\" done successfully ...");
//	                    //System.out.println("Parsing problem \"" + problem.getProblemName() + "\" done successfully ...\n");
//	                    curTime = System.currentTimeMillis();
//	                    
//	                    Graphplan planner = new Graphplan(pb);
//	                    
//	                    planner.preprocessing();
//	                    
//	                    Plan plan = planner.solve(20);
//	                    
//	                    //plan = planner.solve();
//	                    
//	                    if (plan != Plan.FAILURE) {
//	                       // System.out.println("\nfound plan as follows:\n");
//	                       // Graphplan.printPlan(plan);
//	                    	rawplan =Graphplan.getPlan(plan);
//	                    } else {
//	                        System.out.println("\n ****no solution plan found\n");
//	                       // throw new Exception("Plan not found!");
//	                      //  itcnt--;
//	                        continue;
////	                        wb.close();
////	                        return;
//	                    }
//	                }
//	                long planningDuration = System.currentTimeMillis()-curTime;
//	                	
//			    	
//	//		    	
//	//		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
//	//		    	{
//	//		
//	//					String[] s = rawplan.get(cnt);
//	//					for(String str : s)
//	//						System.out.print(str + " ");
//	//					System.out.println();
//	//		    	}
//			    	List<ServiceCall> plan  = new ArrayList<ServiceCall>();
//			    	for(String[] sl :rawplan)
//			    	{
//			    		Service calledService = null;
//			    		for(Service s : dm.getServices())
//			    			if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
//			    			{
//			    				calledService = s;
//			    				break;
//			    			}
//			    		if(calledService==null)
//							throw new Exception("Service does not exists");
//			    		int cnt =1;
//			    		HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
//			    		HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
//			    		HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();
//			    		
//			    		for(URI uri: calledService.getInputList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break; 
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsin.put(uri, par);
//			    			cnt++;
//			    		}
//			    		for(URI uri: calledService.getOutputList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break;
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsout.put(uri, par);
//			    			cnt++;
//			    		}
//			    		for(URI uri: calledService.getVarList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break;
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsvar.put(uri, par);
//			    			cnt++;
//			    		}
//			    		
//			    		
//			    		plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
//			    	}
//			    	
//			    	
//			    	int folderno = plan.size()/5;
//			    	UtilityClass.createFolder("D:\\Development\\BPLECONS\\ds\\featureModelSize\\s"+folderno);
//			    	UtilityClass.writeFile(new File("D:\\Development\\BPLECONS\\ds\\featureModelSize\\s"+folderno+"\\"+ a.getName()), pr.Serialize());
//			    	
////			    	Condition pre =pr.getInitialState();
////			    	Condition eff = pr.getGoalState();
////			    	
////			    	//to add condition and effect
////			    	
////			    	curTime =System.currentTimeMillis();
////			    	
////
////			    	List<GraphNode> optimizedGraph = GraphNode.convertToGraph(plan, pre, eff);
////			    	GraphNode.optimizeNew(optimizedGraph);
//			    	
//			    	long optimizationDuration =System.currentTimeMillis()-curTime;
//			    	
//			    	
//					
//					
//	    		}
//	    		
//	    	}
//	   
//    	}
//
//    }
//    /*
//     * Opitimization on example problem using new optimization
//     */
//    public static void eval8() throws Exception{
//    	boolean ftime =true;
//    	DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\tt\\owlintact2");
//    	dm.getContextModel().createSimpleContext();
//    	
//    	List<StateFactInstanceS> gsl = new ArrayList<StateFactInstanceS>();
//    	List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();
//    	Instance[] params = {dm.getContextModel().getInstanceByName("vCustomer")[0],dm.getContextModel().getInstanceByName("vPurchaseOrder")[0]} ;
//    	isl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("purchased")[0],params),false));
//    	
//    	Instance[] params3 = {dm.getContextModel().getInstanceByName("vInvoice")[0],dm.getContextModel().getInstanceByName("vTaxInfo")[0]} ;
//    	gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("IncludesTaxInfo")[0],params3),false));
//    	Instance[] params4 = {dm.getContextModel().getInstanceByName("vInvoice")[0],dm.getContextModel().getInstanceByName("vShippingInfo")[0]} ;
//    	gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("IncludesShippingPrice")[0],params4),false));
//    	Instance[] params2 = {dm.getContextModel().getInstanceByName("vPurchaseOrder")[0],dm.getContextModel().getInstanceByName("vInvoice")[0]} ;
//    	gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("hasInvoice")[0],params2),false));
//		
//    	Condition pre = new Condition(isl);
//    	Condition eff = new Condition(gsl);
//    	
//    	Problem pr = new Problem(dm.getContextModel(), pre, eff);
//    	ProblemDomain pd = new ProblemDomain(dm);
//    	String problemPDDL = pr.PDDL3Serialize(null);
//    	String problemDomainPDDL =  pd.PDDL3Serialize(null);
//    	
//    	UtilityClass.writeFile(new File("pt.pddl"),problemPDDL);
//    	UtilityClass.writeFile(new File("pta.pddl"),problemDomainPDDL);
//    	
//    	long curTime = System.currentTimeMillis();
//    	
//    	Properties options = Graphplan.getParserOptions();
//    	Parser parser = new Parser(options);
//        PDDLObject domain = parser.parse(new File("pta.pddl"));
//        PDDLObject problem = parser.parse(new File("pt.pddl"));
//        PDDLObject pb = null;
//        List<String[]> rawplan =null;
//        if (domain != null && problem != null) {
//            pb = parser.link(domain, problem);
//        }
//        // Gets the error manager of the pddl compiler
//        ErrorManager mgr = parser.getErrorManager();
//        // If the compilation produces errors we print it and stop
//        
//        if (mgr.contains(Message.ERROR)) {
//            mgr.print(Message.ALL);
//            throw new Exception("Error parsing");
//        }
//        // else we print the warning and start the planning process
//        else {
//            
//            mgr.print(Message.WARNING);
//            
//           // System.out.println("\nParsing domain \"" + domain.getDomainName() + "\" done successfully ...");
//            //System.out.println("Parsing problem \"" + problem.getProblemName() + "\" done successfully ...\n");
//            
//            Graphplan planner = new Graphplan(pb);
//            
//            planner.preprocessing();
//            Plan plan = planner.solve(100);
//            
//          //  plan = planner.solve();
//            
//            if (plan != Plan.FAILURE) {
//               // System.out.println("\nfound plan as follows:\n");
//                rawplan =Graphplan.getPlan(plan);
//            } else {
//                System.out.println("\nno solution plan found\n");
//                throw new Exception("Plan not found!");
//            }
//        }
//        
//        long duration = System.currentTimeMillis()-curTime;
//        System.out.println("Planning time: "+ duration);
//        
//        List<ServiceCall> plan  = new ArrayList<ServiceCall>();
//        
//        
//    	for(String[] sl :rawplan)
//    	{
//    		Service calledService = null;
//    		for(Service s : dm.getServices())
//    			if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
//    			{
//    				calledService = s;
//    				break;
//    			}
//    		if(calledService==null)
//				throw new Exception("Service does not exists");
//    		int cnt =1;
//    		HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
//    		HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
//    		HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();
//    		
//    		for(URI uri: calledService.getInputList()){
//    			Instance par = null;
//    			
//    			for(Instance n: dm.getContextModel().getVars())
//    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//    					par = n;
//    					break;
//    				}
//    			if(par==null)
//    				throw new Exception("Variable does not exists");
//    			paramsin.put(uri, par);
//    			cnt++;
//    		}
//    		for(URI uri: calledService.getOutputList()){
//    			Instance par = null;
//    			
//    			for(Instance n: dm.getContextModel().getVars())
//    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//    					par = n;
//    					break;
//    				}
//    			if(par==null)
//    				throw new Exception("Variable does not exists");
//    			paramsout.put(uri, par);
//    			cnt++;
//    		}
//    		for(URI uri: calledService.getVarList()){
//    			Instance par = null;
//    			
//    			for(Instance n: dm.getContextModel().getVars())
//    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//    					par = n;
//    					break;
//    				}
//    			if(par==null)
//    				throw new Exception("Variable does not exists");
//    			paramsvar.put(uri, par);
//    			cnt++;
//    		}
//    		
//    		
//    		plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
//    	}
//    	
////    	for(ServiceCall sc:plan)
////    		System.out.println(sc.getCalledService().getName());
//    	
//    	List<OperationNode> optimizedGraph = OperationNode.convertToGraph(plan, pre, eff);
//    	
//    	//UtilityClass.writeFile(new File("D:\\bo.txt"), GraphNode.serialize(optimizedGraph,dm));
//    	
//    	Map<Service, ServiceNonfunctionalAnnotation> annotation = new HashMap<Service, ServiceNonfunctionalAnnotation>();
//    	
//    	for(Service s: dm.getServices())
//    		annotation.put(s, new ServiceNonfunctionalAnnotation(s));
//    	
//    	
//    	
//    	ExecutionTime.GenerateExecutionTime(annotation, 200, 50, 50, 30);
//    	
//    	
//    	
//    	
//    	
//    	
//    	
//    	System.out.println("Execution Time(Before Optimization): "+ ExecutionTime.findExecutionTime(annotation, optimizedGraph));
//    	
//    	
//    	
//    	
//    	OperationNode.optimizeNew(optimizedGraph);
//    	System.out.println("Execution Time(After Optimization): "+ ExecutionTime.findExecutionTime(annotation, optimizedGraph));
//    	
//    	//UtilityClass.writeFile(new File("D:\\ao.txt"), GraphNode.serialize(optimizedGraph,dm));
//    	
//    	List<OperationNode> processedNodes = new ArrayList<OperationNode>();
//		LinkedList<OperationNode> gl= new LinkedList<OperationNode>();
//		
//		for(OperationNode g: optimizedGraph){
//			processedNodes.add(g);
//			gl.add(g);
//		}
//		int cnt =0;
//		while(!gl.isEmpty()){
//			OperationNode curg= gl.removeFirst();
//			String gname = "emptynode";
//			if(curg.getCalledService()!=null)
//			{
//				gname = curg.getCalledService().getCalledService().getName();
//			}
//			for(OperationNode ng : curg.getEdges()){
//				if(!processedNodes.contains(ng)){
//					gl.addLast(ng);
//					processedNodes.add(ng);
//				}
//				String dname = "emptynode";
//				if(ng.getCalledService()!=null)
//				{
//					dname = ng.getCalledService().getCalledService().getName();
//				}
//				System.out.println("There is a edge between "+gname+" and "+dname+" .");
//				cnt++;
//			}
//		}
//		System.out.println("total number of edges 2: "+cnt);
//		String graphGV = OperationNode.serializedToGV(optimizedGraph);
//		edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("D:\\test.gv"),graphGV);
//    	
//    }
//    
//    /*
//     * Opitimization on example problem using new optimization
//     */
//    public static void eval8Ubuntu() throws Exception{
//    	boolean ftime =true;
//    	DomainModels dm = DomainModels.ReadModels("/home/mbashari/BPLECONS/tt/owlintact2");
//    	dm.getContextModel().createSimpleContext();
//    	
//    	List<StateFactInstanceS> gsl = new ArrayList<StateFactInstanceS>();
//    	List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();
//    	Instance[] params = {dm.getContextModel().getInstanceByName("vCustomer")[0],dm.getContextModel().getInstanceByName("vPurchaseOrder")[0]} ;
//    	isl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("purchased")[0],params),false));
//    	
//    	Instance[] params3 = {dm.getContextModel().getInstanceByName("vInvoice")[0],dm.getContextModel().getInstanceByName("vTaxInfo")[0]} ;
//    	gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("IncludesTaxInfo")[0],params3),false));
//    	Instance[] params4 = {dm.getContextModel().getInstanceByName("vInvoice")[0],dm.getContextModel().getInstanceByName("vShippingInfo")[0]} ;
//    	gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("IncludesShippingPrice")[0],params4),false));
//    	Instance[] params2 = {dm.getContextModel().getInstanceByName("vPurchaseOrder")[0],dm.getContextModel().getInstanceByName("vInvoice")[0]} ;
//    	gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("hasInvoice")[0],params2),false));
//		
//    	Condition pre = new Condition(isl);
//    	Condition eff = new Condition(gsl);
//    	
//    	Problem pr = new Problem(dm.getContextModel(), pre, eff);
//    	ProblemDomain pd = new ProblemDomain(dm);
//    	String problemPDDL = pr.PDDL3Serialize(null);
//    	String problemDomainPDDL =  pd.PDDL3Serialize(null);
//    	
//    	UtilityClass.writeFile(new File("/home/mbashari/temp/pt.pddl"),problemPDDL);
//    	UtilityClass.writeFile(new File("/home/mbashari/temp/pta.pddl"),problemDomainPDDL);
//    	
//    	long curTime = System.currentTimeMillis();
//    	
//    	Properties options = Graphplan.getParserOptions();
//    	Parser parser = new Parser(options);
//        PDDLObject domain = parser.parse(new File("/home/mbashari/temp/pta.pddl"));
//        PDDLObject problem = parser.parse(new File("/home/mbashari/temp/pt.pddl"));
//        PDDLObject pb = null;
//        List<String[]> rawplan =null;
//        if (domain != null && problem != null) {
//            pb = parser.link(domain, problem);
//        }
//        // Gets the error manager of the pddl compiler
//        ErrorManager mgr = parser.getErrorManager();
//        // If the compilation produces errors we print it and stop
//        
//        if (mgr.contains(Message.ERROR)) {
//            mgr.print(Message.ALL);
//            throw new Exception("Error parsing");
//        }
//        // else we print the warning and start the planning process
//        else {
//            
//            mgr.print(Message.WARNING);
//            
//           // System.out.println("\nParsing domain \"" + domain.getDomainName() + "\" done successfully ...");
//            //System.out.println("Parsing problem \"" + problem.getProblemName() + "\" done successfully ...\n");
//            
//            Graphplan planner = new Graphplan(pb);
//            
//            planner.preprocessing();
//            Plan plan = planner.solve(100);
//            
//          //  plan = planner.solve();
//            
//            if (plan != Plan.FAILURE) {
//               // System.out.println("\nfound plan as follows:\n");
//                rawplan =Graphplan.getPlan(plan);
//            } else {
//                System.out.println("\nno solution plan found\n");
//                throw new Exception("Plan not found!");
//            }
//        }
//        
//        long duration = System.currentTimeMillis()-curTime;
//        System.out.println("Planning time: "+ duration);
//        
//        List<ServiceCall> plan  = new ArrayList<ServiceCall>();
//        
//        
//    	for(String[] sl :rawplan)
//    	{
//    		Service calledService = null;
//    		for(Service s : dm.getServices())
//    			if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
//    			{
//    				calledService = s;
//    				break;
//    			}
//    		if(calledService==null)
//				throw new Exception("Service does not exists");
//    		int cnt =1;
//    		HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
//    		HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
//    		HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();
//    		
//    		for(URI uri: calledService.getInputList()){
//    			Instance par = null;
//    			
//    			for(Instance n: dm.getContextModel().getVars())
//    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//    					par = n;
//    					break;
//    				}
//    			if(par==null)
//    				throw new Exception("Variable does not exists");
//    			paramsin.put(uri, par);
//    			cnt++;
//    		}
//    		for(URI uri: calledService.getOutputList()){
//    			Instance par = null;
//    			
//    			for(Instance n: dm.getContextModel().getVars())
//    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//    					par = n;
//    					break;
//    				}
//    			if(par==null)
//    				throw new Exception("Variable does not exists");
//    			paramsout.put(uri, par);
//    			cnt++;
//    		}
//    		for(URI uri: calledService.getVarList()){
//    			Instance par = null;
//    			
//    			for(Instance n: dm.getContextModel().getVars())
//    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//    					par = n;
//    					break;
//    				}
//    			if(par==null)
//    				throw new Exception("Variable does not exists");
//    			paramsvar.put(uri, par);
//    			cnt++;
//    		}
//    		
//    		
//    		plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
//    	}
//    	
////    	for(ServiceCall sc:plan)
////    		System.out.println(sc.getCalledService().getName());
//    	
//    	List<OperationNode> optimizedGraph = OperationNode.convertToGraph(plan, pre, eff);
//    	
//    	//UtilityClass.writeFile(new File("D:\\bo.txt"), GraphNode.serialize(optimizedGraph,dm));
//    	
//    	Map<Service, ServiceNonfunctionalAnnotation> annotation = new HashMap<Service, ServiceNonfunctionalAnnotation>();
//    	
//    	for(Service s: dm.getServices())
//    		annotation.put(s, new ServiceNonfunctionalAnnotation(s));
//    	
//    	
//    	
//    	ExecutionTime.GenerateExecutionTime(annotation, 200, 50, 50, 30);
//    	
//    	
//    	
//    	
//    	
//    	
//    	
//    	System.out.println("Execution Time(Before Optimization): "+ ExecutionTime.findExecutionTime(annotation, optimizedGraph));
//    	
//    	
//    	
//    	
//    	OperationNode.optimizeNew(optimizedGraph);
//    	System.out.println("Execution Time(After Optimization): "+ ExecutionTime.findExecutionTime(annotation, optimizedGraph));
//    	
//    	//UtilityClass.writeFile(new File("D:\\ao.txt"), GraphNode.serialize(optimizedGraph,dm));
//    	
//    	List<OperationNode> processedNodes = new ArrayList<OperationNode>();
//		LinkedList<OperationNode> gl= new LinkedList<OperationNode>();
//		
//		for(OperationNode g: optimizedGraph){
//			processedNodes.add(g);
//			gl.add(g);
//		}
//		int cnt =0;
//		while(!gl.isEmpty()){
//			OperationNode curg= gl.removeFirst();
//			String gname = "emptynode";
//			if(curg.getCalledService()!=null)
//			{
//				gname = curg.getCalledService().getCalledService().getName();
//			}
//			for(OperationNode ng : curg.getEdges()){
//				if(!processedNodes.contains(ng)){
//					gl.addLast(ng);
//					processedNodes.add(ng);
//				}
//				String dname = "emptynode";
//				if(ng.getCalledService()!=null)
//				{
//					dname = ng.getCalledService().getCalledService().getName();
//				}
//				System.out.println("There is a edge between "+gname+" and "+dname+" .");
//				cnt++;
//			}
//		}
//		System.out.println("total number of edges 2: "+cnt);
//		String graphGV = OperationNode.serializedToGV(optimizedGraph);
//		edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("/home/mbashari/test.gv"),graphGV);
//    	
//    }
//    public static void eval8d2() throws Exception{
//    	boolean ftime =true;
//    	DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\ds\\services\\r6\\s200");
//    	dm.getContextModel().createSimpleContext();
//    	
//    	
//    	Problem pr = Problem.readFromFile(new File("D:\\Development\\BPLECONS\\ds\\featureModelSize\\s1\\n029-32.txt"), dm);
//    	ProblemDomain pd = new ProblemDomain(dm);
//    	String problemPDDL = pr.PDDL3Serialize(null);
//    	String problemDomainPDDL =  pd.PDDL3Serialize(null);
//    	
//    	UtilityClass.writeFile(new File("pt.pddl"),problemPDDL);
//    	UtilityClass.writeFile(new File("pta.pddl"),problemDomainPDDL);
//    	
//    	long curTime = System.currentTimeMillis();
//    	
//    	Properties options = Graphplan.getParserOptions();
//    	Parser parser = new Parser(options);
//        PDDLObject domain = parser.parse(new File("pta.pddl"));
//        PDDLObject problem = parser.parse(new File("pt.pddl"));
//        PDDLObject pb = null;
//        List<String[]> rawplan =null;
//        if (domain != null && problem != null) {
//            pb = parser.link(domain, problem);
//        }
//        // Gets the error manager of the pddl compiler
//        ErrorManager mgr = parser.getErrorManager();
//        // If the compilation produces errors we print it and stop
//        
//        if (mgr.contains(Message.ERROR)) {
//            mgr.print(Message.ALL);
//            throw new Exception("Error parsing");
//        }
//        // else we print the warning and start the planning process
//        else {
//            
//            mgr.print(Message.WARNING);
//            
//           // System.out.println("\nParsing domain \"" + domain.getDomainName() + "\" done successfully ...");
//            //System.out.println("Parsing problem \"" + problem.getProblemName() + "\" done successfully ...\n");
//            
//            Graphplan planner = new Graphplan(pb);
//            
//            planner.preprocessing();
//            Plan plan = planner.solve(100);
//            
//          //  plan = planner.solve();
//            
//            if (plan != Plan.FAILURE) {
//               // System.out.println("\nfound plan as follows:\n");
//                rawplan =Graphplan.getPlan(plan);
//            } else {
//                System.out.println("\nno solution plan found\n");
//                throw new Exception("Plan not found!");
//            }
//        }
//        
//        long duration = System.currentTimeMillis()-curTime;
//        System.out.println("Planning time: "+ duration);
//        
//        List<ServiceCall> plan  = new ArrayList<ServiceCall>();
//        
//        
//    	for(String[] sl :rawplan)
//    	{
//    		Service calledService = null;
//    		for(Service s : dm.getServices())
//    			if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
//    			{
//    				calledService = s;
//    				break;
//    			}
//    		if(calledService==null)
//				throw new Exception("Service does not exists");
//    		int cnt =1;
//    		HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
//    		HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
//    		HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();
//    		
//    		for(URI uri: calledService.getInputList()){
//    			Instance par = null;
//    			
//    			for(Instance n: dm.getContextModel().getVars())
//    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//    					par = n;
//    					break;
//    				}
//    			if(par==null)
//    				throw new Exception("Variable does not exists");
//    			paramsin.put(uri, par);
//    			cnt++;
//    		}
//    		for(URI uri: calledService.getOutputList()){
//    			Instance par = null;
//    			
//    			for(Instance n: dm.getContextModel().getVars())
//    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//    					par = n;
//    					break;
//    				}
//    			if(par==null)
//    				throw new Exception("Variable does not exists");
//    			paramsout.put(uri, par);
//    			cnt++;
//    		}
//    		for(URI uri: calledService.getVarList()){
//    			Instance par = null;
//    			
//    			for(Instance n: dm.getContextModel().getVars())
//    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//    					par = n;
//    					break;
//    				}
//    			if(par==null)
//    				throw new Exception("Variable does not exists");
//    			paramsvar.put(uri, par);
//    			cnt++;
//    		}
//    		
//    		
//    		plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
//    	}
//    	
////    	for(ServiceCall sc:plan)
////    		System.out.println(sc.getCalledService().getName());
//
//    	
//    	Condition pre =pr.getInitialState();
//    	Condition eff = pr.getGoalState();
//    	
//    	List<OperationNode> optimizedGraph = OperationNode.convertToGraph(plan, pre, eff);
//    	
//    	//UtilityClass.writeFile(new File("D:\\bo.txt"), GraphNode.serialize(optimizedGraph,dm));
//    	
//    	Map<Service, ServiceNonfunctionalAnnotation> annotation = new HashMap<Service, ServiceNonfunctionalAnnotation>();
//    	
//    	for(Service s: dm.getServices())
//    		annotation.put(s, new ServiceNonfunctionalAnnotation(s));
//    	
//    	
//    	
//    	ExecutionTime.GenerateExecutionTime(annotation, 200, 50, 50, 30);
//    	
//    	
//    	
//    	
//    	System.out.println("Safeness: "+ (OperationNode.safe2(optimizedGraph)?"SAFE":"NOT SAFE"));
//    	
//    	
//    	System.out.println("Execution Time(Before Optimization): "+ ExecutionTime.findExecutionTime(annotation, optimizedGraph));
//    	
//    	
//    	edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("D:\\test1.gv"),OperationNode.serializedToGV(optimizedGraph));
//    	
//    	
//    	OperationNode.optimizeNew(optimizedGraph);
//    	System.out.println("Execution Time(After Optimization): "+ ExecutionTime.findExecutionTime(annotation, optimizedGraph));
//    	
//    	//UtilityClass.writeFile(new File("D:\\ao.txt"), GraphNode.serialize(optimizedGraph,dm));
//    	
////    	List<GraphNode> processedNodes = new ArrayList<GraphNode>();
////		LinkedList<GraphNode> gl= new LinkedList<GraphNode>();
////		
////		for(GraphNode g: optimizedGraph){
////			processedNodes.add(g);
////			gl.add(g);
////		}
////		int cnt =0;
////		while(!gl.isEmpty()){
////			GraphNode curg= gl.removeFirst();
////			String gname = "emptynode";
////			if(curg.getCalledService()!=null)
////			{
////				gname = curg.getCalledService().getCalledService().getName();
////			}
////			for(GraphNode ng : curg.getEdges()){
////				if(!processedNodes.contains(ng)){
////					gl.addLast(ng);
////					processedNodes.add(ng);
////				}
////				String dname = "emptynode";
////				if(ng.getCalledService()!=null)
////				{
////					dname = ng.getCalledService().getCalledService().getName();
////				}
////				System.out.println("There is a edge between "+gname+" and "+dname+" .");
////				cnt++;
////			}
////		}
////		System.out.println("total number of edges 2: "+cnt);
//		String graphGV = OperationNode.serializedToGV(optimizedGraph);
//		edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("D:\\test.gv"),graphGV);
//    	
//    }
//    /*
//     * Performs evaluation on efficiency of method based on the number of features
//     */
//    public static void eval6() throws Exception{
//    	WritableWorkbook wb = Workbook.createWorkbook(new File("d:\\evaluationfm1.xls"));
//		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
//    	int offset =0;
//    	boolean ftime =true;
//    	
//    	Integer[] nos ={10,20,60,80,100};
//    	
//    		
//    	curSheet.addCell(new Label(0,0,"Number of Features"));
//    	//curSheet.addCell(new Label(1,0,"PDDL Generation"));
//    	curSheet.addCell(new Label(2,0,"Planning"));
//    	//curSheet.addCell(new Label(3,0,"Optimization"));
//    	
//    	
//    	for(int itcnt =0; itcnt <ITERATIONS_FM ;itcnt++ ){
//    		
//    		for(int nocnt=0; nocnt <nos.length; nocnt++){
//    			int row =offset+ nocnt+1;
//    			int col =0;
//    			curSheet.addCell(new Label(col++,row, ""+nos[nocnt]));
//    			
//    			long curTime = System.currentTimeMillis();
//    			
//		    	//DomainModels dm = DomainModels.ReadModels("D:\\tt\\owlintact2");
//		    	DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\dataset3\\s500");
//		    	dm.getContextModel().createSimpleContext();
//		    	//Add Vars
//		    	
//		    	
//		    	
//		    	
////		    	List<StateFactInstanceS> gsl = new ArrayList<StateFactInstanceS>();
////		    	List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();
//		    	
////		    	String cond1 = "p7f9d1560";
////		    	String cond2 = "pfc4299ba";
////		    	
////		    	String postCond1 = dm.getContextModel().getFactTypes().getStateFactTypeList().get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0, dm.getContextModel().getFactTypes().getStateFactTypeList().size())).getTypeName();
////		    	String postCond2 = dm.getContextModel().getFactTypes().getStateFactTypeList().get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0, dm.getContextModel().getFactTypes().getStateFactTypeList().size())).getTypeName();
////		    	//String postCond1 = "p3e2f7d26";
////		    	//String postCond2 = "pc550de6b";
////		    	System.out.println("*****"+postCond1);
////		    	System.out.println("*****"+postCond2);
////		    	
////		    	Instance[] params = {dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond1)[0].getParams()[0].getTypeName())[0],dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond1)[0].getParams()[1].getTypeName())[0]} ;
////		    	isl.add( new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName(cond1)[0],params),false));
////		    	Instance[] params2 ={dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond2)[0].getParams()[0].getTypeName())[0],dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond2)[0].getParams()[1].getTypeName())[0]} ;
////		    	isl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName(cond2)[0],params2),false));
////		    	
////		    	
////		    	Instance[] params3 = {dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(postCond1)[0].getParams()[0].getTypeName())[0],dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(postCond1)[0].getParams()[1].getTypeName())[0]} ;
////		    	gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName(postCond1)[0],params3),false));
////		    	Instance[] params4 = {dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(postCond2)[0].getParams()[0].getTypeName())[0],dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(postCond2)[0].getParams()[1].getTypeName())[0]} ;
////		    	gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName(postCond2)[0],params4),false));
////		    	
//		    	
//		// 
//		//    	Instance[] params = {dm.getContextModel().getInstanceByName("vCustomer")[0],dm.getContextModel().getInstanceByName("vPurchaseOrder")[0]} ;
//		//    	isl.add(new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("purchased")[0],params));
//		//    	
//		//    	Instance[] params3 = {dm.getContextModel().getInstanceByName("vInvoice")[0],dm.getContextModel().getInstanceByName("vTaxInfo")[0]} ;
//		//    	gsl.add(new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("IncludesTaxInfo")[0],params3));
//		//    	Instance[] params4 = {dm.getContextModel().getInstanceByName("vInvoice")[0],dm.getContextModel().getInstanceByName("vShippingInfo")[0]} ;
//		//    	gsl.add(new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("IncludesShippingPrice")[0],params4));
//		//    	Instance[] params2 = {dm.getContextModel().getInstanceByName("vPurchaseOrder")[0],dm.getContextModel().getInstanceByName("vInvoice")[0]} ;
//		//    	gsl.add(new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("hasInvoice")[0],params2));
//		//    	
////				Condition initialState = new Condition(isl);
////				Condition goalState = new Condition(gsl);
////		    	Problem pr = new Problem(dm.getContextModel(), initialState, goalState);
//		    	ProblemDomain pd = new ProblemDomain(dm);
//		    	
//		    	//String problem = pr.PDDLserialize();
//		    	String problemDomain =  pd.PDDLserialize();
//		    	long serializationDuration = System.currentTimeMillis()-curTime;
//		    	curTime = System.currentTimeMillis();
//		    	//System.out.println(problem);
//		    	//System.out.println(problemDomain);
//		    	
//		    	String problempddl = UtilityClass.readFile(new File("D:\\fmproblems\\f"+nos[nocnt]+"\\n"+Integer.toString( itcnt+1)+".pddl"),Charset.defaultCharset());
//		    	String problemDomainpddl =  pd.PDDL3Serialize(null);
//		    	
//		    	UtilityClass.writeFile(new File("pt.pddl"),problempddl);
//		    	UtilityClass.writeFile(new File("pta.pddl"),problemDomainpddl);
//		    	
//		    	
//		    	
//		    	//System.out.println(problem);
//		    	//System.out.println(problemDomain);
//		    	
//		    	//GraphPlan gp = new GraphPlan();
//		    	
//		    	//List<String[]> rawplan =gp.init2(problemDomain ,problem, 50, ftime,false);
//		    	
//		    	
//		    	ftime=false;
//		    	
//		    	curTime = System.currentTimeMillis();
//		    	Properties options = Graphplan.getParserOptions();
//		    	Parser parser = new Parser(options);
//                PDDLObject domain = parser.parse(new File("pta.pddl"));
//                PDDLObject problem = parser.parse(new File("pt.pddl"));
//                PDDLObject pb = null;
//                List<String[]> rawplan =null;
//                if (domain != null && problem != null) {
//                    pb = parser.link(domain, problem);
//                }
//                // Gets the error manager of the pddl compiler
//                ErrorManager mgr = parser.getErrorManager();
//                // If the compilation produces errors we print it and stop
//                
//                if (mgr.contains(Message.ERROR)) {
//                    mgr.print(Message.ALL);
//                    throw new Exception("Error parsing");
//                }
//                // else we print the warning and start the planning process
//                else {
//                    
//                    mgr.print(Message.WARNING);
//                    
//                   // System.out.println("\nParsing domain \"" + domain.getDomainName() + "\" done successfully ...");
//                    //System.out.println("Parsing problem \"" + problem.getProblemName() + "\" done successfully ...\n");
//                    
//                    Graphplan planner = new Graphplan(pb);
//                    
//                    planner.preprocessing();
//                    Plan plan = planner.solve(100);
//                    
//                  //  plan = planner.solve();
//                    
//                    if (plan != Plan.FAILURE) {
//                       // System.out.println("\nfound plan as follows:\n");
//                        rawplan =Graphplan.getPlan(plan);
//                    } else {
//                        System.out.println("\nno solution plan found\n");
//                        throw new Exception("Plan not found!");
//                    }
//                }
//                long planningDuration = System.currentTimeMillis()-curTime;
////		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
////		    	{
////		    		boolean isequal = false;
////		    		for(int cnt2=0; cnt2<cnt ; cnt2++  )
////		    		{
////		    			
////		    			if(rawplan.get(cnt2).length != rawplan.get(cnt).length){
////		    				continue;
////		    			}
////		    			
////		    			boolean tqual = true;
////		    			for(int cnt3=0; cnt3< rawplan.get(cnt2).length; cnt3++)
////		    				if(!rawplan.get(cnt2)[cnt3].equals(rawplan.get(cnt)[cnt3]))
////		    					tqual = false;
////		    			if(tqual){
////		    				isequal =true;
////		    				break;
////		    			}
////		    		}
////		    		if(isequal){
////		    			rawplan.remove(cnt);
////		    			cnt--;
////		    		}
////		    		
////		    	}
//		    	
////		    	
////		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
////		    	{
////		
////					String[] s = rawplan.get(cnt);
////					for(String str : s)
////						System.out.print(str + " ");
////					System.out.println();
////		    	}
//		    	List<ServiceCall> plan  = new ArrayList<ServiceCall>();
//		    	for(String[] sl :rawplan)
//		    	{
//		    		Service calledService = null;
//		    		for(Service s : dm.getServices())
//		    			if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
//		    			{
//		    				calledService = s;
//		    				break;
//		    			}
//		    		if(calledService==null)
//						throw new Exception("Service does not exists");
//		    		int cnt =1;
//		    		HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
//		    		HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
//		    		HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();
//		    		
//		    		for(URI uri: calledService.getInputList()){
//		    			Instance par = null;
//		    			
//		    			for(Instance n: dm.getContextModel().getVars())
//		    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//		    					par = n;
//		    					break;
//		    				}
//		    			if(par==null)
//		    				throw new Exception("Variable does not exists");
//		    			paramsin.put(uri, par);
//		    			cnt++;
//		    		}
//		    		for(URI uri: calledService.getOutputList()){
//		    			Instance par = null;
//		    			
//		    			for(Instance n: dm.getContextModel().getVars())
//		    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//		    					par = n;
//		    					break;
//		    				}
//		    			if(par==null)
//		    				throw new Exception("Variable does not exists");
//		    			paramsout.put(uri, par);
//		    			cnt++;
//		    		}
//		    		for(URI uri: calledService.getVarList()){
//		    			Instance par = null;
//		    			
//		    			for(Instance n: dm.getContextModel().getVars())
//		    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//		    					par = n;
//		    					break;
//		    				}
//		    			if(par==null)
//		    				throw new Exception("Variable does not exists");
//		    			paramsvar.put(uri, par);
//		    			cnt++;
//		    		}
//		    		
//		    		
//		    		plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
//		    	}
//		    	
//		    	
//
//		    	
////		    	List<GraphNode> optimizedGraph =  GraphNode.optimize(plan);
////		    	long optimizationDuration = System.currentTimeMillis()-curTime;
////		    	
////		    	List<GraphNode> processedNodes = new ArrayList<GraphNode>();
////				LinkedList<GraphNode> gl= new LinkedList<GraphNode>();
////				
////				for(GraphNode g: optimizedGraph){
////					processedNodes.add(g);
////					gl.add(g);
////				}
////				int cnt =0;
////				while(!gl.isEmpty()){
////					GraphNode curg= gl.removeFirst();
////					String gname = "emptynode";
////					if(curg.getCalledService()!=null)
////					{
////						gname = curg.getCalledService().getCalledService().getName();
////					}
////					for(GraphNode ng : curg.getEdges()){
////						if(!processedNodes.contains(ng)){
////							gl.addLast(ng);
////							processedNodes.add(ng);
////						}
////						String dname = "emptynode";
////						if(ng.getCalledService()!=null)
////						{
////							dname = ng.getCalledService().getCalledService().getName();
////						}
////						System.out.println("There is a edge between "+gname+" and "+dname+" .");
////						cnt++;
////					}
////				}
////				System.out.println("total number of edges 2: "+cnt);
////				StringBuilder sb = new StringBuilder();
////				sb.append(" digraph G {"+ System.lineSeparator());
////				
////				gl= new LinkedList<GraphNode>();
////				HashSet<GraphNode> finalNodes = new HashSet<GraphNode>();
////				processedNodes = new ArrayList<GraphNode>();
////				
////				for(GraphNode g: optimizedGraph){
////					gl.add(g);
////					processedNodes.add(g);
////					String gname = "emptynode";
////					if(g.getCalledService()!=null)
////					{
////						gname = g.getCalledService().getCalledService().getName();
////					}
////					sb.append(  "Start -> "+gname.replaceAll("Service", "")+";"+System.lineSeparator());
////				}
////				while(!gl.isEmpty()){
////					GraphNode curg= gl.removeFirst();
////					String gname = "emptynode";
////					if(curg.getCalledService()!=null)
////					{
////						gname = curg.getCalledService().getCalledService().getName();
////					}
////					if( curg.getEdges().size() ==0)
////					{
////						if(!finalNodes.contains(curg))
////						   sb.append(  gname.replaceAll("Service", "")+" -> End;"+System.lineSeparator());
////						finalNodes.add(curg);
////						continue;
////					}
////					for(GraphNode ng : curg.getEdges()){
////						if(!processedNodes.contains(ng)){
////							processedNodes.add(ng);
////							gl.addLast(ng);
////						}
////						String dname = "emptynode";
////						if(ng.getCalledService()!=null)
////						{
////							dname = ng.getCalledService().getCalledService().getName();
////						}
////						sb.append(  gname.replaceAll("Service", "")+" -> "+dname.replaceAll("Service", "")+";"+System.lineSeparator());
////					}
////				}
////				sb.append("}");
////				edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("D:\\test.gv"),sb.toString());
//		//    	for(URI k: vars.keySet()){
//		//    		System.out.println( vars.get(k).getType().getTypeName() +" ("+vars.get(k).getName()+");");
//		//    	}
//			//	curSheet.addCell(new jxl.write.Number(col++,row,serializationDuration));
//				curSheet.addCell(new jxl.write.Number(col++,row,planningDuration));
//				//curSheet.addCell(new jxl.write.Number(col++,row,optimizationDuration));
//    		}
//    		offset+= nos.length;
//    	}
//		
//		wb.write();
//		wb.close();
//		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("d:\\operators.txt"), sb.toString());
//		//read an xml node using xpath
//		//Node node = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
//		
//		
//		
//		//System.out.println(baseURI);
//		// /rdf:RDF/service:Service/service:describedBy/process:AtomicProcess/process:hasInput/process:Input/rdfs:label/
//    }
//    
//
//    /*
//     * Performs evaluation on efficiency of method based on the number of feature(OLD VERSION)
//     */
//    public static void eval7() throws Exception{
//
//    	WritableWorkbook wb = Workbook.createWorkbook(new File("d:\\evaluationfmn.xls"));
//		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
//    	int offset =0;
//    	boolean ftime =true;
//    	
//    	Integer[] nos ={10,20,40, 60,80,100};
//    	
//    		
//    	curSheet.addCell(new Label(0,0,"Number of Features"));
//    	//curSheet.addCell(new Label(1,0,"PDDL Generation"));
//    	curSheet.addCell(new Label(1,0,"Planning"));
//    	//curSheet.addCell(new Label(3,0,"Optimization"));
//    	
//    	
//    	
//    		
//		for(int nocnt=0; nocnt <nos.length; nocnt++){
//			double averagetime = 0;
//			System.out.println("No of features: "+ nos[nocnt]);
//			
//			for(int itcnt =0; itcnt <ITERATIONS_FM ;itcnt++ ){
//				System.out.println("Iteration: "+ itcnt);
//				int row =offset+ itcnt+1;
//    			int col =0;
//    			curSheet.addCell(new Label(col++,row, ""+nos[nocnt]));
//    			
//    			long curTime = System.currentTimeMillis();
//    			
//		    	//DomainModels dm = DomainModels.ReadModels("D:\\tt\\owlintact2");
//		    	DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\dataset3\\s500");
//		    	dm.getContextModel().createSimpleContext();
//		    	//Add Vars
//		    	
//		    	
//		    	
//		    	
////		    	List<StateFactInstanceS> gsl = new ArrayList<StateFactInstanceS>();
////		    	List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();
//		    	
////		    	String cond1 = "p7f9d1560";
////		    	String cond2 = "pfc4299ba";
////		    	
////		    	String postCond1 = dm.getContextModel().getFactTypes().getStateFactTypeList().get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0, dm.getContextModel().getFactTypes().getStateFactTypeList().size())).getTypeName();
////		    	String postCond2 = dm.getContextModel().getFactTypes().getStateFactTypeList().get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0, dm.getContextModel().getFactTypes().getStateFactTypeList().size())).getTypeName();
////		    	//String postCond1 = "p3e2f7d26";
////		    	//String postCond2 = "pc550de6b";
////		    	System.out.println("*****"+postCond1);
////		    	System.out.println("*****"+postCond2);
////		    	
////		    	Instance[] params = {dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond1)[0].getParams()[0].getTypeName())[0],dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond1)[0].getParams()[1].getTypeName())[0]} ;
////		    	isl.add( new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName(cond1)[0],params),false));
////		    	Instance[] params2 ={dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond2)[0].getParams()[0].getTypeName())[0],dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond2)[0].getParams()[1].getTypeName())[0]} ;
////		    	isl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName(cond2)[0],params2),false));
////		    	
////		    	
////		    	Instance[] params3 = {dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(postCond1)[0].getParams()[0].getTypeName())[0],dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(postCond1)[0].getParams()[1].getTypeName())[0]} ;
////		    	gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName(postCond1)[0],params3),false));
////		    	Instance[] params4 = {dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(postCond2)[0].getParams()[0].getTypeName())[0],dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(postCond2)[0].getParams()[1].getTypeName())[0]} ;
////		    	gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName(postCond2)[0],params4),false));
////		    	
//		    	
//		// 
//		//    	Instance[] params = {dm.getContextModel().getInstanceByName("vCustomer")[0],dm.getContextModel().getInstanceByName("vPurchaseOrder")[0]} ;
//		//    	isl.add(new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("purchased")[0],params));
//		//    	
//		//    	Instance[] params3 = {dm.getContextModel().getInstanceByName("vInvoice")[0],dm.getContextModel().getInstanceByName("vTaxInfo")[0]} ;
//		//    	gsl.add(new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("IncludesTaxInfo")[0],params3));
//		//    	Instance[] params4 = {dm.getContextModel().getInstanceByName("vInvoice")[0],dm.getContextModel().getInstanceByName("vShippingInfo")[0]} ;
//		//    	gsl.add(new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("IncludesShippingPrice")[0],params4));
//		//    	Instance[] params2 = {dm.getContextModel().getInstanceByName("vPurchaseOrder")[0],dm.getContextModel().getInstanceByName("vInvoice")[0]} ;
//		//    	gsl.add(new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("hasInvoice")[0],params2));
//		//    	
////				Condition initialState = new Condition(isl);
////				Condition goalState = new Condition(gsl);
////		    	Problem pr = new Problem(dm.getContextModel(), initialState, goalState);
//		    	ProblemDomain pd = new ProblemDomain(dm);
//		    	
//		    	//String problem = pr.PDDLserialize();
//		    	String problemDomain =  pd.PDDLserialize();
//		    	long serializationDuration = System.currentTimeMillis()-curTime;
//		    	curTime = System.currentTimeMillis();
//		    	//System.out.println(problem);
//		    	//System.out.println(problemDomain);
//		    	
//		    	File folder = new File("D:\\fmproblems\\f"+nos[nocnt]+"");
//		    	File[] fl =folder.listFiles();
//		    	
//		    	
//		    	
//		    	String problempddl = UtilityClass.readFile(fl[UtilityClass.randInt(0, fl.length-1)],Charset.defaultCharset());
//		    	String problemDomainpddl =  pd.PDDL3Serialize(null);
//		    	
//		    	UtilityClass.writeFile(new File("pt.pddl"),problempddl);
//		    	UtilityClass.writeFile(new File("pta.pddl"),problemDomainpddl);
//		    	
//		    	
//		    	
//		    	//System.out.println(problem);
//		    	//System.out.println(problemDomain);
//		    	
//		    	//GraphPlan gp = new GraphPlan();
//		    	
//		    	//List<String[]> rawplan =gp.init2(problemDomain ,problem, 50, ftime,false);
//		    	
//		    	
//		    	ftime=false;
//		    	
//		    	curTime = System.currentTimeMillis();
//		    	Properties options = Graphplan.getParserOptions();
//		    	Parser parser = new Parser(options);
//                PDDLObject domain = parser.parse(new File("pta.pddl"));
//                PDDLObject problem = parser.parse(new File("pt.pddl"));
//                PDDLObject pb = null;
//                List<String[]> rawplan =null;
//                if (domain != null && problem != null) {
//                    pb = parser.link(domain, problem);
//                }
//                // Gets the error manager of the pddl compiler
//                ErrorManager mgr = parser.getErrorManager();
//                // If the compilation produces errors we print it and stop
//                
//                if (mgr.contains(Message.ERROR)) {
//                    mgr.print(Message.ALL);
//                    throw new Exception("Error parsing");
//                }
//                // else we print the warning and start the planning process
//                else {
//                    
//                    mgr.print(Message.WARNING);
//                    
//                   // System.out.println("\nParsing domain \"" + domain.getDomainName() + "\" done successfully ...");
//                    //System.out.println("Parsing problem \"" + problem.getProblemName() + "\" done successfully ...\n");
//                    
//                    Graphplan planner = new Graphplan(pb);
//                    
//                    planner.preprocessing();
//                    Plan plan = planner.solve(100);
//                    
//                    //plan = planner.solve();
//                    
//                    if (plan != Plan.FAILURE) {
//                       // System.out.println("\nfound plan as follows:\n");
//                        rawplan =Graphplan.getPlan(plan);
//                    } else {
//                        System.out.println("\nno solution plan found\n");
//                        throw new Exception("Plan not found!");
//                    }
//                }
//                long planningDuration = System.currentTimeMillis()-curTime;
////		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
////		    	{
////		    		boolean isequal = false;
////		    		for(int cnt2=0; cnt2<cnt ; cnt2++  )
////		    		{
////		    			
////		    			if(rawplan.get(cnt2).length != rawplan.get(cnt).length){
////		    				continue;
////		    			}
////		    			
////		    			boolean tqual = true;
////		    			for(int cnt3=0; cnt3< rawplan.get(cnt2).length; cnt3++)
////		    				if(!rawplan.get(cnt2)[cnt3].equals(rawplan.get(cnt)[cnt3]))
////		    					tqual = false;
////		    			if(tqual){
////		    				isequal =true;
////		    				break;
////		    			}
////		    		}
////		    		if(isequal){
////		    			rawplan.remove(cnt);
////		    			cnt--;
////		    		}
////		    		
////		    	}
//		    	
////		    	
////		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
////		    	{
////		
////					String[] s = rawplan.get(cnt);
////					for(String str : s)
////						System.out.print(str + " ");
////					System.out.println();
////		    	}
//		    	List<ServiceCall> plan  = new ArrayList<ServiceCall>();
//		    	for(String[] sl :rawplan)
//		    	{
//		    		Service calledService = null;
//		    		for(Service s : dm.getServices())
//		    			if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
//		    			{
//		    				calledService = s;
//		    				break;
//		    			}
//		    		if(calledService==null)
//						throw new Exception("Service does not exists");
//		    		int cnt =1;
//		    		HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
//		    		HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
//		    		HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();
//		    		
//		    		for(URI uri: calledService.getInputList()){
//		    			Instance par = null;
//		    			
//		    			for(Instance n: dm.getContextModel().getVars())
//		    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//		    					par = n;
//		    					break;
//		    				}
//		    			if(par==null)
//		    				throw new Exception("Variable does not exists");
//		    			paramsin.put(uri, par);
//		    			cnt++;
//		    		}
//		    		for(URI uri: calledService.getOutputList()){
//		    			Instance par = null;
//		    			
//		    			for(Instance n: dm.getContextModel().getVars())
//		    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//		    					par = n;
//		    					break;
//		    				}
//		    			if(par==null)
//		    				throw new Exception("Variable does not exists");
//		    			paramsout.put(uri, par);
//		    			cnt++;
//		    		}
//		    		for(URI uri: calledService.getVarList()){
//		    			Instance par = null;
//		    			
//		    			for(Instance n: dm.getContextModel().getVars())
//		    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//		    					par = n;
//		    					break;
//		    				}
//		    			if(par==null)
//		    				throw new Exception("Variable does not exists");
//		    			paramsvar.put(uri, par);
//		    			cnt++;
//		    		}
//		    		
//		    		
//		    		plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
//		    	}
//		    	
//		    	
//				curSheet.addCell(new jxl.write.Number(col++,row,planningDuration));
//				System.out.println("Duration: "+ planningDuration);
//				
//				averagetime+=planningDuration;
//				//curSheet.addCell(new jxl.write.Number(col++,row,optimizationDuration));
//    		}
//			curSheet.addCell(new jxl.write.Number(10,nocnt,averagetime/ITERATIONS_FM));
//    		offset+= ITERATIONS_FM;
//    	}
//		
//		wb.write();
//		wb.close();
//		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("d:\\operators.txt"), sb.toString());
//		//read an xml node using xpath
//		//Node node = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
//		
//		
//		
//		//System.out.println(baseURI);
//		// /rdf:RDF/service:Service/service:describedBy/process:AtomicProcess/process:hasInput/process:Input/rdfs:label/
//    }
//    
//   
//    /***
//     * Performs evaluation on efficiency of method based on the number of services (NEW VERSION)
//     * @throws Exception
//     */
//    public static void eval5() throws Exception{
//    	WritableWorkbook wb = Workbook.createWorkbook(new File("e:\\evaluation.xls"));
//		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
//    	int offset =0;
//    	boolean ftime =true;
//    	
//    	Integer[] nos ={200,400,600,800,1000};
//    	
//    	//Integer[] nos ={200};
//    	
//    	curSheet.addCell(new Label(1,0,"PDDL Generation"));
//    	curSheet.addCell(new Label(2,0,"Planning"));
//    	curSheet.addCell(new Label(3,0,"Optimization"));
//    	
//    	
//    	for(int itcnt =0; itcnt <ITERATIONS ;itcnt++ ){
//    		System.out.println("Iteration: "+itcnt );
//    		for(int nocnt=0; nocnt <nos.length; nocnt++){
//    			int row =offset+ nocnt+1;
//    			int col =0;
//    			curSheet.addCell(new Label(col++,row,"Iteration: "+itcnt +" no of services: "+ nos[nocnt]));
//    			
//    			long curTime = System.currentTimeMillis();
//    			
//		    	//DomainModels dm = DomainModels.ReadModels("D:\\tt\\owlintact2");
//		    	DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\dataset\\s"+nos[nocnt]);
//		    	dm.getContextModel().createSimpleContext();
//		    	//Add Vars
//		    	
//		    	
//		    	
//		    	
//		    	List<StateFactInstanceS> gsl = new ArrayList<StateFactInstanceS>();
//		    	List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();
//		    	
//		    	String cond1 = "p7f9d1560";
//		    	String cond2 = "pfc4299ba";
//		    	
//		    	String postCond1 = dm.getContextModel().getFactTypes().getStateFactTypeList().get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0, dm.getContextModel().getFactTypes().getStateFactTypeList().size()-1)).getTypeName();
//		    	String postCond2 = dm.getContextModel().getFactTypes().getStateFactTypeList().get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0, dm.getContextModel().getFactTypes().getStateFactTypeList().size()-1)).getTypeName();
//		    	//String postCond1 = "p3e2f7d26";
//		    	//String postCond2 = "pc550de6b";
//		    	//System.out.println("*****"+postCond1);
//		    	//System.out.println("*****"+postCond2);
//		    	
//		    	Instance[] params = {dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond1)[0].getParams()[0].getTypeName())[0],dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond1)[0].getParams()[1].getTypeName())[0]} ;
//		    	isl.add( new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName(cond1)[0],params),false));
//		    	Instance[] params2 ={dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond2)[0].getParams()[0].getTypeName())[0],dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond2)[0].getParams()[1].getTypeName())[0]} ;
//		    	isl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName(cond2)[0],params2),false));
//		    	
//		    	
//		    	Instance[] params3 = {dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(postCond1)[0].getParams()[0].getTypeName())[0],dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(postCond1)[0].getParams()[1].getTypeName())[0]} ;
//		    	gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName(postCond1)[0],params3),false));
//		    	Instance[] params4 = {dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(postCond2)[0].getParams()[0].getTypeName())[0],dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(postCond2)[0].getParams()[1].getTypeName())[0]} ;
//		    	gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName(postCond2)[0],params4),false));
//		    	
//		    	
//		// 
//		//    	Instance[] params = {dm.getContextModel().getInstanceByName("vCustomer")[0],dm.getContextModel().getInstanceByName("vPurchaseOrder")[0]} ;
//		//    	isl.add(new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("purchased")[0],params));
//		//    	
//		//    	Instance[] params3 = {dm.getContextModel().getInstanceByName("vInvoice")[0],dm.getContextModel().getInstanceByName("vTaxInfo")[0]} ;
//		//    	gsl.add(new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("IncludesTaxInfo")[0],params3));
//		//    	Instance[] params4 = {dm.getContextModel().getInstanceByName("vInvoice")[0],dm.getContextModel().getInstanceByName("vShippingInfo")[0]} ;
//		//    	gsl.add(new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("IncludesShippingPrice")[0],params4));
//		//    	Instance[] params2 = {dm.getContextModel().getInstanceByName("vPurchaseOrder")[0],dm.getContextModel().getInstanceByName("vInvoice")[0]} ;
//		//    	gsl.add(new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("hasInvoice")[0],params2));
//		//    	
//				Condition initialState = new Condition(isl);
//				Condition goalState = new Condition(gsl);
//		    	Problem pr = new Problem(dm.getContextModel(), initialState, goalState);
//		    	ProblemDomain pd = new ProblemDomain(dm);
//		    	
//		    	String problempddl = pr.PDDL3Serialize(null);
//		    	String problemDomainpddl =  pd.PDDL3Serialize(null);
//		    	
//		    	UtilityClass.writeFile(new File("pt.pddl"),problempddl);
//		    	UtilityClass.writeFile(new File("pta.pddl"),problemDomainpddl);
//		    	
//		    	
//		    	long serializationDuration = System.currentTimeMillis()-curTime;
//		    	curTime = System.currentTimeMillis();
//		    	//System.out.println(problem);
//		    	//System.out.println(problemDomain);
//		    	
//		    	//GraphPlan gp = new GraphPlan();
//		    	
//		    	//List<String[]> rawplan =gp.init2(problemDomain ,problem, 50, ftime,false);
//		    	
//		    	
//		    	ftime=false;
//		    	
//		    	curTime = System.currentTimeMillis();
//		    	Properties options = Graphplan.getParserOptions();
//		    	Parser parser = new Parser(options);
//                PDDLObject domain = parser.parse(new File("pta.pddl"));
//                PDDLObject problem = parser.parse(new File("pt.pddl"));
//                PDDLObject pb = null;
//                List<String[]> rawplan =null;
//                if (domain != null && problem != null) {
//                    pb = parser.link(domain, problem);
//                }
//                // Gets the error manager of the pddl compiler
//                ErrorManager mgr = parser.getErrorManager();
//                // If the compilation produces errors we print it and stop
//                
//                if (mgr.contains(Message.ERROR)) {
//                    mgr.print(Message.ALL);
//                    throw new Exception("Error parsing");
//                }
//                // else we print the warning and start the planning process
//                else {
//                    
//                    mgr.print(Message.WARNING);
//                    
//                   // System.out.println("\nParsing domain \"" + domain.getDomainName() + "\" done successfully ...");
//                    //System.out.println("Parsing problem \"" + problem.getProblemName() + "\" done successfully ...\n");
//                    
//                    Graphplan planner = new Graphplan(pb);
//                    
//                    planner.preprocessing();
//                    Plan plan = planner.solve(100);
//                    
//                    //plan = planner.solve();
//                    
//                    if (plan != Plan.FAILURE) {
//                       // System.out.println("\nfound plan as follows:\n");
//                        rawplan =Graphplan.getPlan(plan);
//                    } else {
//                        System.out.println("\nno solution plan found\n");
//                        throw new Exception("Plan not found!");
//                    }
//                }
//                long planningDuration = System.currentTimeMillis()-curTime;
////		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
////		    	{
////		    		boolean isequal = false;
////		    		for(int cnt2=0; cnt2<cnt ; cnt2++  )
////		    		{
////		    			
////		    			if(rawplan.get(cnt2).length != rawplan.get(cnt).length){
////		    				continue;
////		    			}
////		    			
////		    			boolean tqual = true;
////		    			for(int cnt3=0; cnt3< rawplan.get(cnt2).length; cnt3++)
////		    				if(!rawplan.get(cnt2)[cnt3].equals(rawplan.get(cnt)[cnt3]))
////		    					tqual = false;
////		    			if(tqual){
////		    				isequal =true;
////		    				break;
////		    			}
////		    		}
////		    		if(isequal){
////		    			rawplan.remove(cnt);
////		    			cnt--;
////		    		}
////		    		
////		    	}
//		    	
////		    	
////		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
////		    	{
////		
////					String[] s = rawplan.get(cnt);
////					for(String str : s)
////						System.out.print(str + " ");
////					System.out.println();
////		    	}
//		    	List<ServiceCall> plan  = new ArrayList<ServiceCall>();
//		    	for(String[] sl :rawplan)
//		    	{
//		    		Service calledService = null;
//		    		for(Service s : dm.getServices())
//		    			if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
//		    			{
//		    				calledService = s;
//		    				break;
//		    			}
//		    		if(calledService==null)
//						throw new Exception("Service does not exists");
//		    		int cnt =1;
//		    		HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
//		    		HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
//		    		HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();
//		    		
//		    		for(URI uri: calledService.getInputList()){
//		    			Instance par = null;
//		    			
//		    			for(Instance n: dm.getContextModel().getVars())
//		    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//		    					par = n;
//		    					break;
//		    				}
//		    			if(par==null)
//		    				throw new Exception("Variable does not exists");
//		    			paramsin.put(uri, par);
//		    			cnt++;
//		    		}
//		    		for(URI uri: calledService.getOutputList()){
//		    			Instance par = null;
//		    			
//		    			for(Instance n: dm.getContextModel().getVars())
//		    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//		    					par = n;
//		    					break;
//		    				}
//		    			if(par==null)
//		    				throw new Exception("Variable does not exists");
//		    			paramsout.put(uri, par);
//		    			cnt++;
//		    		}
//		    		for(URI uri: calledService.getVarList()){
//		    			Instance par = null;
//		    			
//		    			for(Instance n: dm.getContextModel().getVars())
//		    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//		    					par = n;
//		    					break;
//		    				}
//		    			if(par==null)
//		    				throw new Exception("Variable does not exists");
//		    			paramsvar.put(uri, par);
//		    			cnt++;
//		    		}
//		    		
//		    		
//		    		plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
//		    	}
//		    	
//		    	
//
//		    	
////		    	List<GraphNode> optimizedGraph =  GraphNode.optimize(plan);
////		    	long optimizationDuration = System.currentTimeMillis()-curTime;
////		    	
////		    	List<GraphNode> processedNodes = new ArrayList<GraphNode>();
////				LinkedList<GraphNode> gl= new LinkedList<GraphNode>();
////				
////				for(GraphNode g: optimizedGraph){
////					processedNodes.add(g);
////					gl.add(g);
////				}
////				int cnt =0;
////				while(!gl.isEmpty()){
////					GraphNode curg= gl.removeFirst();
////					String gname = "emptynode";
////					if(curg.getCalledService()!=null)
////					{
////						gname = curg.getCalledService().getCalledService().getName();
////					}
////					for(GraphNode ng : curg.getEdges()){
////						if(!processedNodes.contains(ng)){
////							gl.addLast(ng);
////							processedNodes.add(ng);
////						}
////						String dname = "emptynode";
////						if(ng.getCalledService()!=null)
////						{
////							dname = ng.getCalledService().getCalledService().getName();
////						}
////						System.out.println("There is a edge between "+gname+" and "+dname+" .");
////						cnt++;
////					}
////				}
////				System.out.println("total number of edges 2: "+cnt);
////				StringBuilder sb = new StringBuilder();
////				sb.append(" digraph G {"+ System.lineSeparator());
////				
////				gl= new LinkedList<GraphNode>();
////				HashSet<GraphNode> finalNodes = new HashSet<GraphNode>();
////				processedNodes = new ArrayList<GraphNode>();
////				
////				for(GraphNode g: optimizedGraph){
////					gl.add(g);
////					processedNodes.add(g);
////					String gname = "emptynode";
////					if(g.getCalledService()!=null)
////					{
////						gname = g.getCalledService().getCalledService().getName();
////					}
////					sb.append(  "Start -> "+gname.replaceAll("Service", "")+";"+System.lineSeparator());
////				}
////				while(!gl.isEmpty()){
////					GraphNode curg= gl.removeFirst();
////					String gname = "emptynode";
////					if(curg.getCalledService()!=null)
////					{
////						gname = curg.getCalledService().getCalledService().getName();
////					}
////					if( curg.getEdges().size() ==0)
////					{
////						if(!finalNodes.contains(curg))
////						   sb.append(  gname.replaceAll("Service", "")+" -> End;"+System.lineSeparator());
////						finalNodes.add(curg);
////						continue;
////					}
////					for(GraphNode ng : curg.getEdges()){
////						if(!processedNodes.contains(ng)){
////							processedNodes.add(ng);
////							gl.addLast(ng);
////						}
////						String dname = "emptynode";
////						if(ng.getCalledService()!=null)
////						{
////							dname = ng.getCalledService().getCalledService().getName();
////						}
////						sb.append(  gname.replaceAll("Service", "")+" -> "+dname.replaceAll("Service", "")+";"+System.lineSeparator());
////					}
////				}
////				sb.append("}");
////				edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("D:\\test.gv"),sb.toString());
//		//    	for(URI k: vars.keySet()){
//		//    		System.out.println( vars.get(k).getType().getTypeName() +" ("+vars.get(k).getName()+");");
//		//    	}
//				curSheet.addCell(new jxl.write.Number(col++,row,serializationDuration));
//				curSheet.addCell(new jxl.write.Number(col++,row,planningDuration));
//				
//				//curSheet.addCell(new jxl.write.Number(col++,row,optimizationDuration));
//    		}
//    		offset+= nos.length;
//    	}
//		
//		wb.write();
//		wb.close();
//		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("d:\\operators.txt"), sb.toString());
//		//read an xml node using xpath
//		//Node node = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
//		
//		
//		
//		//System.out.println(baseURI);
//		// /rdf:RDF/service:Service/service:describedBy/process:AtomicProcess/process:hasInput/process:Input/rdfs:label/
//    }
//    /***
//     * Performs evaluation on efficiency of method based on the number of services (NEW VERSION)
//     * @throws Exception
//     */
//    public static void eval10() throws Exception{
//    	WritableWorkbook wb = Workbook.createWorkbook(new File("d:\\evaluationsvs.xls"));
//		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
//    	int offset =0;
//    	int colOffset=0;
//    	boolean ftime =true;
//    	
//    	Integer[] nos ={100};//, 200,400,600,800,1000};
//    	Integer[] reqNoList={3};//,6,9};
//    	
//    	//Integer[] nos ={200};
//    	
//    	
//    	for(int reqcnt=0; reqcnt<reqNoList.length; reqcnt++){
//    		 offset =0;
//	    	
//	    	
//	    	curSheet.addCell(new Label(colOffset+1,0,"PDDL Generation"));
//	    	curSheet.addCell(new Label(colOffset+2,0,"Planning"));
//	    	curSheet.addCell(new Label(colOffset+3,0,"Optimization"));
//	    	
//	    	for(int nocnt=0; nocnt <nos.length; nocnt++){
//	    		double averageSerializationTime =0;
//	    		double averagePlanningTime =0;
//	    		double averageOptimizationTime =0;
//	    		System.out.println("****Evaluation for no of services "+nos[nocnt]+ "and no of reqs "+reqNoList[reqcnt]);
//		    	for(int itcnt =0; itcnt <ITERATIONS ;itcnt++ ){
//		    		System.out.println("Iteration: "+itcnt );
//	    		
//	    			int row =offset+ itcnt+1;
//	    			int col =colOffset;
//	    			curSheet.addCell(new Label(col++,row,"Iteration: "+itcnt +" no of services: "+ nos[nocnt]));
//	    			
//	    			
//	    			
//			    	//DomainModels dm = DomainModels.ReadModels("D:\\tt\\owlintact2");
//			    	DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\ds\\services\\r"+reqNoList[reqcnt]+"\\s"+nos[nocnt]);
//			    	dm.getContextModel().createSimpleContext();
//			    	//Add Vars
//			    	
//			    	
//			    	
//			    	
//			    	
//			    	
//			    	
//			    	
//			    	File folder = new File("D:\\Development\\BPLECONS\\ds\\featureModelFS\\r"+reqNoList[reqcnt]+"\\s"+nos[nocnt]);
//			    	File[] fl =folder.listFiles();
//			    	long curTime = System.currentTimeMillis();
//			    	ProblemDomain pd = new ProblemDomain(dm);
//			    	String problemDomainpddl =  pd.PDDL3Serialize(null);
//			    	long serializationDuration = System.currentTimeMillis()-curTime;
//			    	
//			    	Problem pr = Problem.readFromFile(fl[UtilityClass.randInt(0, fl.length-1)], dm);
//			    	String problempddl = pr.PDDL3Serialize(null);
//			    	
//			    	
//			    	UtilityClass.writeFile(new File("pt.pddl"),problempddl);
//			    	UtilityClass.writeFile(new File("pta.pddl"),problemDomainpddl);
//			    	
//			    	
//			    	
//			    	averageSerializationTime+=serializationDuration;
//			    	curTime = System.currentTimeMillis();
//			    	//System.out.println(problem);
//			    	//System.out.println(problemDomain);
//			    	
//			    	//GraphPlan gp = new GraphPlan();
//			    	
//			    	//List<String[]> rawplan =gp.init2(problemDomain ,problem, 50, ftime,false);
//			    	
//			    	
//			    	ftime=false;
//			    	
//			    	
//			    	Properties options = Graphplan.getParserOptions();
//			    	Parser parser = new Parser(options);
//	                PDDLObject domain = parser.parse(new File("pta.pddl"));
//	                PDDLObject problem = parser.parse(new File("pt.pddl"));
//	                PDDLObject pb = null;
//	                List<String[]> rawplan =null;
//	                if (domain != null && problem != null) {
//	                    pb = parser.link(domain, problem);
//	                }
//	                // Gets the error manager of the pddl compiler
//	                ErrorManager mgr = parser.getErrorManager();
//	                // If the compilation produces errors we print it and stop
//	                
//	                if (mgr.contains(Message.ERROR)) {
//	                    mgr.print(Message.ALL);
//	                    throw new Exception("Error parsing");
//	                }
//	                // else we print the warning and start the planning process
//	                else {
//	                    
//	                    mgr.print(Message.WARNING);
//	                    
//	                   // System.out.println("\nParsing domain \"" + domain.getDomainName() + "\" done successfully ...");
//	                    //System.out.println("Parsing problem \"" + problem.getProblemName() + "\" done successfully ...\n");
//	                    curTime = System.currentTimeMillis();
//	                    
//	                    Graphplan planner = new Graphplan(pb);
//	                    
//	                    planner.preprocessing();
//	                    
//	                    Plan plan = planner.solve(100);
//	                    
//	                    //plan = planner.solve();
//	                    
//	                    if (plan != Plan.FAILURE) {
//	                       // System.out.println("\nfound plan as follows:\n");
//	                        rawplan =Graphplan.getPlan(plan);
//	                    } else {
//	                        System.out.println("\nno solution plan found\n");
//	                        throw new Exception("Plan not found!");
//	                    }
//	                }
//	                long planningDuration = System.currentTimeMillis()-curTime;
//	                
//	                averagePlanningTime+=planningDuration;
//			    	
//			    	
//	//		    	
//	//		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
//	//		    	{
//	//		
//	//					String[] s = rawplan.get(cnt);
//	//					for(String str : s)
//	//						System.out.print(str + " ");
//	//					System.out.println();
//	//		    	}
//			    	List<ServiceCall> plan  = new ArrayList<ServiceCall>();
//			    	for(String[] sl :rawplan)
//			    	{
//			    		Service calledService = null;
//			    		for(Service s : dm.getServices())
//			    			if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
//			    			{
//			    				calledService = s;
//			    				break;
//			    			}
//			    		if(calledService==null)
//							throw new Exception("Service does not exists");
//			    		int cnt =1;
//			    		HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
//			    		HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
//			    		HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();
//			    		
//			    		for(URI uri: calledService.getInputList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break;
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsin.put(uri, par);
//			    			cnt++;
//			    		}
//			    		for(URI uri: calledService.getOutputList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break;
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsout.put(uri, par);
//			    			cnt++;
//			    		}
//			    		for(URI uri: calledService.getVarList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break;
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsvar.put(uri, par);
//			    			cnt++;
//			    		}
//			    		
//			    		
//			    		plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
//			    	}
//			    	
//			    	Condition pre =pr.getInitialState();
//			    	Condition eff = pr.getGoalState();
//			    	
//			    	//to add condition and effect
//			    	
//			    	curTime =System.currentTimeMillis();
//			    	
//
//			    	List<OperationNode> optimizedGraph = OperationNode.convertToGraph(plan, pre, eff);
//			    	OperationNode.optimizeNew(optimizedGraph);
//			    	
//			    	long optimizationDuration =System.currentTimeMillis()-curTime;
//			    	averageOptimizationTime +=optimizationDuration;
//			    	
//					curSheet.addCell(new jxl.write.Number(col++,row,serializationDuration));
//					curSheet.addCell(new jxl.write.Number(col++,row,planningDuration));
//					curSheet.addCell(new jxl.write.Number(col++,row,optimizationDuration));
//					
//	    		}
//	    		offset+= ITERATIONS;
//	    		curSheet.addCell(new jxl.write.Number(colOffset+15,nocnt,averageSerializationTime/ ITERATIONS));
//	    		curSheet.addCell(new jxl.write.Number(colOffset+16,nocnt,averagePlanningTime/ ITERATIONS));
//	    		curSheet.addCell(new jxl.write.Number(colOffset+17,nocnt,averageOptimizationTime/ ITERATIONS));
//	    		//wb.write();
//	    	}
//	    	colOffset+=4;
//		
//    	}
//		wb.write();
//		wb.close();
//		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("d:\\operators.txt"), sb.toString());
//		//read an xml node using xpath
//		//Node node = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
//		
//		
//		
//		//System.out.println(baseURI);
//		// /rdf:RDF/service:Service/service:describedBy/process:AtomicProcess/process:hasInput/process:Input/rdfs:label/
//    }
//    /***
//     * Performs evaluation on efficiency of method based on the number of features (NEW VERSION)
//     * @throws Exception
//     */
//    public static void eval11() throws Exception{
//    	UtilityClass.createFolder("D:\\temp");
//    	File dir = new File("D:\\temp");
//    	for(File fl: dir.listFiles() )
//    	{
//    		fl.delete();
//    	}
//    	String curPost = "";
//    	WritableWorkbook wb = Workbook.createWorkbook(new File("d:\\evaluationfms.xls"));
//		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
//    	int offset =0;
//    	int colOffset=0;
//    	boolean ftime =true;
//    	
//    	Integer[] nos ={ 10, 20, 40, 60, 80,100};
//    	Integer[] reqNoList={2,4,6};
////    	Integer[] nos ={60};
////    	Integer[] reqNoList={6};
//    	//Integer[] nos ={200};
//    	
//    	
//    	for(int reqcnt=0; reqcnt<reqNoList.length; reqcnt++){
//    		 offset =0;
//	    	
//	    	
//	    	curSheet.addCell(new Label(colOffset+1,0,"PDDL Generation"));
//	    	curSheet.addCell(new Label(colOffset+2,0,"Planning"));
//	    	curSheet.addCell(new Label(colOffset+3,0,"Optimization"));
//	    	
//
//	    	//DomainModels dm = DomainModels.ReadModels("D:\\tt\\owlintact2");
//	    	DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\ds\\services\\r6\\s200");
//	    	dm.getContextModel().createSimpleContext();
//	    	
//	    	for(int nocnt=0; nocnt <nos.length; nocnt++){
//	    		double averageSerializationTime =0;
//	    		double averagePlanningTime =0;
//	    		double averageOptimizationTime =0;
//	    		System.out.println("****Evaluation for no of feature "+nos[nocnt]+ "and no of reqs "+reqNoList[reqcnt]);
//		    	for(int itcnt =0; itcnt <ITERATIONS ;itcnt++ ){
//		    		System.out.println("Iteration: "+itcnt );
//	    		
//	    			int row =offset+ itcnt+1;
//	    			int col =colOffset;
//	    			curSheet.addCell(new Label(col++,row,"Iteration: "+itcnt +" no of services: "+ nos[nocnt]));
//	    			
//	    			
//	    			
//			    	//Add Vars
//
//			    	File folder = new File("D:\\Development\\BPLECONS\\ds\\featureModelFF\\r"+reqNoList[reqcnt]+"\\"+nos[nocnt]);
//			    	File[] fl =folder.listFiles();
//			    	long curTime = System.currentTimeMillis();
//			    	ProblemDomain pd = new ProblemDomain(dm);
//			    	String problemDomainpddl =  pd.PDDL3Serialize(curPost);
//			    	long serializationDuration = System.currentTimeMillis()-curTime;
//			    	File a = fl[UtilityClass.randInt(0, fl.length-1)];
//			    	System.out.println("File name: "+a.getName());
//			    	Problem pr = Problem.readFromFile(a, dm);
//			    	String problempddl = pr.PDDL3Serialize(curPost);
//			    	
//			    	try{
//			    		UtilityClass.writeFile(new File("D:\\temp\\pt"+curPost+".pddl"),problempddl);
//				    	UtilityClass.writeFile(new File("D:\\temp\\pta"+curPost+".pddl"),problemDomainpddl);
//			    	}
//			    	catch(Exception ex){
//			    		System.out.println("Exception in writing files!");
//			    		curPost= UUID.randomUUID().toString();
//			    		curPost= curPost.replaceAll("-", "");
//			    		itcnt--;
//			    		continue;
//			    	}
//			    	
//			    	
//			    	
//			    	averageSerializationTime+=serializationDuration;
//			    	curTime = System.currentTimeMillis();
//			    	//System.out.println(problem);
//			    	//System.out.println(problemDomain);
//			    	
//			    	//GraphPlan gp = new GraphPlan();
//			    	
//			    	//List<String[]> rawplan =gp.init2(problemDomain ,problem, 50, ftime,false);
//			    	
//			    	
//			    	ftime=false;
//			    	
//			    	
//			    	Properties options = Graphplan.getParserOptions();
//			    	Parser parser = new Parser(options);
//	                PDDLObject domain = parser.parse(new File("D:\\temp\\pta"+curPost+".pddl"));
//	                PDDLObject problem = parser.parse(new File("D:\\temp\\pt"+curPost+".pddl"));
//	                PDDLObject pb = null;
//	                List<String[]> rawplan =null;
//	                if (domain != null && problem != null) {
//	                    pb = parser.link(domain, problem);
//	                }
//	                // Gets the error manager of the pddl compiler
//	                ErrorManager mgr = parser.getErrorManager();
//	                // If the compilation produces errors we print it and stop
//	                
//	                if (mgr.contains(Message.ERROR)) {
//	                    mgr.print(Message.ALL);
//	                    throw new Exception("Error parsing");
//	                }
//	                // else we print the warning and start the planning process
//	                else {
//	                    
//	                    mgr.print(Message.WARNING);
//	                    
//	                   // System.out.println("\nParsing domain \"" + domain.getDomainName() + "\" done successfully ...");
//	                    //System.out.println("Parsing problem \"" + problem.getProblemName() + "\" done successfully ...\n");
//	                    curTime = System.currentTimeMillis();
//	                    
//	                    Graphplan planner = new Graphplan(pb);
//	                    
//	                    planner.preprocessing();
//	                    
//	                    Plan plan = planner.solve(20);
//	                    
//	                    //plan = planner.solve();
//	                    
//	                    if (plan != Plan.FAILURE) {
//	                       // System.out.println("\nfound plan as follows:\n");
//	                       // Graphplan.printPlan(plan);
//	                    	rawplan =Graphplan.getPlan(plan);
//	                    } else {
//	                        System.out.println("\n ****no solution plan found\n");
//	                       // throw new Exception("Plan not found!");
//	                        itcnt--;
//	                        continue;
////	                        wb.close();
////	                        return;
//	                    }
//	                }
//	                long planningDuration = System.currentTimeMillis()-curTime;
//	                
//	                averagePlanningTime+=planningDuration;
//			    	
//			    	
//	//		    	
//	//		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
//	//		    	{
//	//		
//	//					String[] s = rawplan.get(cnt);
//	//					for(String str : s)
//	//						System.out.print(str + " ");
//	//					System.out.println();
//	//		    	}
//			    	List<ServiceCall> plan  = new ArrayList<ServiceCall>();
//			    	for(String[] sl :rawplan)
//			    	{
//			    		Service calledService = null;
//			    		for(Service s : dm.getServices())
//			    			if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
//			    			{
//			    				calledService = s;
//			    				break;
//			    			}
//			    		if(calledService==null)
//							throw new Exception("Service does not exists");
//			    		int cnt =1;
//			    		HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
//			    		HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
//			    		HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();
//			    		
//			    		for(URI uri: calledService.getInputList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break; 
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsin.put(uri, par);
//			    			cnt++;
//			    		}
//			    		for(URI uri: calledService.getOutputList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break;
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsout.put(uri, par);
//			    			cnt++;
//			    		}
//			    		for(URI uri: calledService.getVarList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break;
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsvar.put(uri, par);
//			    			cnt++;
//			    		}
//			    		
//			    		
//			    		plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
//			    	}
//			    	
////			    	Condition pre =pr.getInitialState();
////			    	Condition eff = pr.getGoalState();
////			    	
////			    	//to add condition and effect
////			    	
////			    	curTime =System.currentTimeMillis();
////			    	
////
////			    	List<GraphNode> optimizedGraph = GraphNode.convertToGraph(plan, pre, eff);
////			    	GraphNode.optimizeNew(optimizedGraph);
//			    	
//			    	long optimizationDuration =System.currentTimeMillis()-curTime;
//			    	averageOptimizationTime +=optimizationDuration;
//			    	
//					curSheet.addCell(new jxl.write.Number(col++,row,serializationDuration));
//					curSheet.addCell(new jxl.write.Number(col++,row,planningDuration));
//					curSheet.addCell(new jxl.write.Number(col++,row,optimizationDuration));
//					
//	    		}
//	    		offset+= ITERATIONS;
//	    		curSheet.addCell(new jxl.write.Number(colOffset+15,nocnt,averageSerializationTime/ ITERATIONS));
//	    		curSheet.addCell(new jxl.write.Number(colOffset+16,nocnt,averagePlanningTime/ ITERATIONS));
//	    		curSheet.addCell(new jxl.write.Number(colOffset+17,nocnt,averageOptimizationTime/ ITERATIONS));
//	    		//wb.write();
//	    	}
//	    	colOffset+=4;
//		
//    	}
//		wb.write();
//		wb.close();
//		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("d:\\operators.txt"), sb.toString());
//		//read an xml node using xpath
//		//Node node = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
//		
//		
//		
//		//System.out.println(baseURI);
//		// /rdf:RDF/service:Service/service:describedBy/process:AtomicProcess/process:hasInput/process:Input/rdfs:label/
//    }
//    public static void eval12() throws Exception{
//    	UtilityClass.createFolder("D:\\temp");
//    	File dir = new File("D:\\temp");
//    	for(File fl: dir.listFiles() )
//    	{
//    		fl.delete();
//    	}
//    	String curPost = "";
//    	WritableWorkbook wb = Workbook.createWorkbook(new File("d:\\evaluationlen.xls"));
//		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
//    	int offset =0;
//    	int colOffset=0;
//    	boolean ftime =true;
////    	
////    	Integer[] nos ={ 10, 20, 40, 60, 80,100};
////    	Integer[] reqNoList={2,4,6};
////    	Integer[] nos ={60};
////    	Integer[] reqNoList={6};
//    	//Integer[] nos ={200};
//    	
//    	
//    	
//    		 offset =0;
//	    	
//	    	
//	    	curSheet.addCell(new Label(colOffset+1,0,"PDDL Generation"));
//	    	curSheet.addCell(new Label(colOffset+2,0,"Planning"));
//	    	curSheet.addCell(new Label(colOffset+3,0,"Optimization"));
//	    	
//
//	    	//DomainModels dm = DomainModels.ReadModels("D:\\tt\\owlintact2");
//	    	DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\ds\\services\\r6\\s200");
//	    	dm.getContextModel().createSimpleContext();
//	    	
//	    	for(int nocnt=0; nocnt <7; nocnt++){
//	    		double averageSerializationTime =0;
//	    		double averagePlanningTime =0;
//	    		double averageOptimizationTime =0;
//	    		System.out.println("****Evaluation for no  "+nocnt);
//		    	for(int itcnt =0; itcnt <ITERATIONS ;itcnt++ ){
//		    		System.out.println("Iteration: "+itcnt );
//	    		
//	    			int row =offset+ itcnt+1;
//	    			int col =colOffset;
//	    			curSheet.addCell(new Label(col++,row,"Iteration: "+itcnt +" no : "+nocnt));
//	    			
//	    			
//	    			
//			    	//Add Vars
//
//			    	File folder = new File("D:\\Development\\BPLECONS\\ds\\featureModelSize\\s"+nocnt);
//			    	File[] fl =folder.listFiles();
//			    	long curTime = System.currentTimeMillis();
//			    	ProblemDomain pd = new ProblemDomain(dm);
//			    	String problemDomainpddl =  pd.PDDL3Serialize(curPost);
//			    	long serializationDuration = System.currentTimeMillis()-curTime;
//			    	File a = fl[UtilityClass.randInt(0, fl.length-1)];
//			    	System.out.println("File name: "+a.getName());
//			    	Problem pr = Problem.readFromFile(a, dm);
//			    	String problempddl = pr.PDDL3Serialize(curPost);
//			    	
//			    	try{
//			    		UtilityClass.writeFile(new File("D:\\temp\\pt"+curPost+".pddl"),problempddl);
//				    	UtilityClass.writeFile(new File("D:\\temp\\pta"+curPost+".pddl"),problemDomainpddl);
//			    	}
//			    	catch(Exception ex){
//			    		System.out.println("Exception in writing files!");
//			    		curPost= UUID.randomUUID().toString();
//			    		curPost= curPost.replaceAll("-", "");
//			    		itcnt--;
//			    		continue;
//			    	}
//			    	
//			    	
//			    	
//			    	averageSerializationTime+=serializationDuration;
//			    	curTime = System.currentTimeMillis();
//			    	//System.out.println(problem);
//			    	//System.out.println(problemDomain);
//			    	
//			    	//GraphPlan gp = new GraphPlan();
//			    	
//			    	//List<String[]> rawplan =gp.init2(problemDomain ,problem, 50, ftime,false);
//			    	
//			    	
//			    	ftime=false;
//			    	
//			    	
//			    	Properties options = Graphplan.getParserOptions();
//			    	Parser parser = new Parser(options);
//	                PDDLObject domain = parser.parse(new File("D:\\temp\\pta"+curPost+".pddl"));
//	                PDDLObject problem = parser.parse(new File("D:\\temp\\pt"+curPost+".pddl"));
//	                PDDLObject pb = null;
//	                List<String[]> rawplan =null;
//	                if (domain != null && problem != null) {
//	                    pb = parser.link(domain, problem);
//	                }
//	                // Gets the error manager of the pddl compiler
//	                ErrorManager mgr = parser.getErrorManager();
//	                // If the compilation produces errors we print it and stop
//	                
//	                if (mgr.contains(Message.ERROR)) {
//	                    mgr.print(Message.ALL);
//	                    throw new Exception("Error parsing");
//	                }
//	                // else we print the warning and start the planning process
//	                else {
//	                    
//	                    mgr.print(Message.WARNING);
//	                    
//	                   // System.out.println("\nParsing domain \"" + domain.getDomainName() + "\" done successfully ...");
//	                    //System.out.println("Parsing problem \"" + problem.getProblemName() + "\" done successfully ...\n");
//	                    curTime = System.currentTimeMillis();
//	                    
//	                    Graphplan planner = new Graphplan(pb);
//	                    
//	                    planner.preprocessing();
//	                    
//	                    Plan plan = planner.solve(20);
//	                    
//	                    //plan = planner.solve();
//	                    
//	                    if (plan != Plan.FAILURE) {
//	                       // System.out.println("\nfound plan as follows:\n");
//	                       // Graphplan.printPlan(plan);
//	                    	rawplan =Graphplan.getPlan(plan);
//	                    } else {
//	                        System.out.println("\n ****no solution plan found\n");
//	                       // throw new Exception("Plan not found!");
//	                        itcnt--;
//	                        continue;
////	                        wb.close();
////	                        return;
//	                    }
//	                }
//	                long planningDuration = System.currentTimeMillis()-curTime;
//	                
//	                averagePlanningTime+=planningDuration;
//			    	
//			    	
//	//		    	
//	//		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
//	//		    	{
//	//		
//	//					String[] s = rawplan.get(cnt);
//	//					for(String str : s)
//	//						System.out.print(str + " ");
//	//					System.out.println();
//	//		    	}
//			    	List<ServiceCall> plan  = new ArrayList<ServiceCall>();
//			    	for(String[] sl :rawplan)
//			    	{
//			    		Service calledService = null;
//			    		for(Service s : dm.getServices())
//			    			if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
//			    			{
//			    				calledService = s;
//			    				break;
//			    			}
//			    		if(calledService==null)
//							throw new Exception("Service does not exists");
//			    		int cnt =1;
//			    		HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
//			    		HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
//			    		HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();
//			    		
//			    		for(URI uri: calledService.getInputList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break; 
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsin.put(uri, par);
//			    			cnt++;
//			    		}
//			    		for(URI uri: calledService.getOutputList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break;
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsout.put(uri, par);
//			    			cnt++;
//			    		}
//			    		for(URI uri: calledService.getVarList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break;
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsvar.put(uri, par);
//			    			cnt++;
//			    		}
//			    		
//			    		
//			    		plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
//			    	}
//			    	
////			    	Condition pre =pr.getInitialState();
////			    	Condition eff = pr.getGoalState();
////			    	
////			    	//to add condition and effect
////			    	
////			    	curTime =System.currentTimeMillis();
////			    	
////
////			    	List<GraphNode> optimizedGraph = GraphNode.convertToGraph(plan, pre, eff);
////			    	GraphNode.optimizeNew(optimizedGraph);
//			    	
//			    	long optimizationDuration =System.currentTimeMillis()-curTime;
//			    	averageOptimizationTime +=optimizationDuration;
//			    	
//					curSheet.addCell(new jxl.write.Number(col++,row,serializationDuration));
//					curSheet.addCell(new jxl.write.Number(col++,row,planningDuration));
//					curSheet.addCell(new jxl.write.Number(col++,row,optimizationDuration));
//					
//	    		}
//	    		offset+= ITERATIONS;
//	    		curSheet.addCell(new jxl.write.Number(colOffset+15,nocnt,averageSerializationTime/ ITERATIONS));
//	    		curSheet.addCell(new jxl.write.Number(colOffset+16,nocnt,averagePlanningTime/ ITERATIONS));
//	    		curSheet.addCell(new jxl.write.Number(colOffset+17,nocnt,averageOptimizationTime/ ITERATIONS));
//	    		//wb.write();
//	    	}
//	    	colOffset+=4;
//		
//    	
//		wb.write();
//		wb.close();
//		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("d:\\operators.txt"), sb.toString());
//		//read an xml node using xpath
//		//Node node = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
//		
//		
//		
//		//System.out.println(baseURI);
//		// /rdf:RDF/service:Service/service:describedBy/process:AtomicProcess/process:hasInput/process:Input/rdfs:label/
//    }
//    /**
//     * Old method(uses graphplan)
//     * @throws Exception
//     */
//    public static void eval13() throws Exception{
//    	UtilityClass.createFolder("D:\\temp");
//    	File dir = new File("D:\\temp");
//    	for(File fl: dir.listFiles() )
//    	{
//    		fl.delete();
//    	}
//    	String curPost = "";
//    	WritableWorkbook wb = Workbook.createWorkbook(new File("d:\\evaluationlen.xls"));
//		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
//    	int offset =0;
//    	int colOffset=0;
//    	boolean ftime =true;
////    	
////    	Integer[] nos ={ 10, 20, 40, 60, 80,100};
////    	Integer[] reqNoList={2,4,6};
////    	Integer[] nos ={60};
////    	Integer[] reqNoList={6};
//    	//Integer[] nos ={200};
//    	
//    	
//    	
//    		 offset =0;
//	    	
//	    	
//	    	curSheet.addCell(new Label(colOffset+1,0,"PDDL Generation"));
//	    	curSheet.addCell(new Label(colOffset+2,0,"Planning"));
//	    	curSheet.addCell(new Label(colOffset+3,0,"Optimization"));
//	    	
//
//	    	//DomainModels dm = DomainModels.ReadModels("D:\\tt\\owlintact2");
//	    	DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\ds\\services\\r6\\s1000");
//	    	dm.getContextModel().createSimpleContext();
//	    	
//	    	for(int nocnt=0; nocnt <7; nocnt++){
//	    		double averageSerializationTime =0;
//	    		double averagePlanningTime =0;
//	    		double averageOptimizationTime =0;
//	    		System.out.println("****Evaluation for no  "+nocnt);
//		    	for(int itcnt =0; itcnt <ITERATIONS ;itcnt++ ){
//		    		System.out.println("Iteration: "+itcnt );
//	    		
//	    			int row =offset+ itcnt+1;
//	    			int col =colOffset;
//	    			curSheet.addCell(new Label(col++,row,"Iteration: "+itcnt +" no : "+nocnt));
//	    			
//	    			
//	    			
//			    	//Add Vars
//
//			    	File folder = new File("D:\\Development\\BPLECONS\\ds\\featureModelSize\\s"+nocnt);
//			    	File[] fl =folder.listFiles();
//			    	long curTime = System.currentTimeMillis();
//			    	ProblemDomain pd = new ProblemDomain(dm);
//			    	String problemDomainpddl =  pd.PDDL3Serialize(curPost);
//			    	long serializationDuration = System.currentTimeMillis()-curTime;
//			    	File a = fl[UtilityClass.randInt(0, fl.length-1)];
//			    	System.out.println("File name: "+a.getName());
//			    	Problem pr = Problem.readFromFile(a, dm);
//			    	String problempddl = pr.PDDL3Serialize(curPost);
//			    	
//			    	try{
//			    		UtilityClass.writeFile(new File("D:\\temp\\pt"+curPost+".pddl"),problempddl);
//				    	UtilityClass.writeFile(new File("D:\\temp\\pta"+curPost+".pddl"),problemDomainpddl);
//			    	}
//			    	catch(Exception ex){
//			    		System.out.println("Exception in writing files!");
//			    		curPost= UUID.randomUUID().toString();
//			    		curPost= curPost.replaceAll("-", "");
//			    		itcnt--;
//			    		continue;
//			    	}
//			    	
//			    	
//			    	
//			    	averageSerializationTime+=serializationDuration;
//			    	curTime = System.currentTimeMillis();
//			    	//System.out.println(problem);
//			    	//System.out.println(problemDomain);
//			    	
//			    	//GraphPlan gp = new GraphPlan();
//			    	
//			    	//List<String[]> rawplan =gp.init2(problemDomain ,problem, 50, ftime,false);
//			    	
//			    	
//			    	ftime=false;
//			    	
//			    	
//			    	Properties options = Graphplan.getParserOptions();
//			    	Parser parser = new Parser(options);
//	                PDDLObject domain = parser.parse(new File("D:\\temp\\pta"+curPost+".pddl"));
//	                PDDLObject problem = parser.parse(new File("D:\\temp\\pt"+curPost+".pddl"));
//	                PDDLObject pb = null;
//	                List<String[]> rawplan =null;
//	                if (domain != null && problem != null) {
//	                    pb = parser.link(domain, problem);
//	                }
//	                // Gets the error manager of the pddl compiler
//	                ErrorManager mgr = parser.getErrorManager();
//	                // If the compilation produces errors we print it and stop
//	                
//	                if (mgr.contains(Message.ERROR)) {
//	                    mgr.print(Message.ALL);
//	                    throw new Exception("Error parsing");
//	                }
//	                // else we print the warning and start the planning process
//	                else {
//	                    
//	                    mgr.print(Message.WARNING);
//	                    
//	                   // System.out.println("\nParsing domain \"" + domain.getDomainName() + "\" done successfully ...");
//	                    //System.out.println("Parsing problem \"" + problem.getProblemName() + "\" done successfully ...\n");
//	                    curTime = System.currentTimeMillis();
//	                    
//	                    Graphplan planner = new Graphplan(pb);
//	                    
//	                    planner.preprocessing();
//	                    
//	                    Plan plan = planner.solve(20);
//	                    
//	                    //plan = planner.solve();
//	                    
//	                    if (plan != Plan.FAILURE) {
//	                       // System.out.println("\nfound plan as follows:\n");
//	                       // Graphplan.printPlan(plan);
//	                    	rawplan =Graphplan.getPlan(plan);
//	                    } else {
//	                        System.out.println("\n ****no solution plan found\n");
//	                       // throw new Exception("Plan not found!");
//	                        itcnt--;
//	                        continue;
////	                        wb.close();
////	                        return;
//	                    }
//	                }
//	                long planningDuration = System.currentTimeMillis()-curTime;
//	                
//	                averagePlanningTime+=planningDuration;
//			    	
//			    	
//	//		    	
//	//		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
//	//		    	{
//	//		
//	//					String[] s = rawplan.get(cnt);
//	//					for(String str : s)
//	//						System.out.print(str + " ");
//	//					System.out.println();
//	//		    	}
//			    	List<ServiceCall> plan  = new ArrayList<ServiceCall>();
//			    	for(String[] sl :rawplan)
//			    	{
//			    		Service calledService = null;
//			    		for(Service s : dm.getServices())
//			    			if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
//			    			{
//			    				calledService = s;
//			    				break;
//			    			}
//			    		if(calledService==null)
//							throw new Exception("Service does not exists");
//			    		int cnt =1;
//			    		HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
//			    		HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
//			    		HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();
//			    		
//			    		for(URI uri: calledService.getInputList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break; 
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsin.put(uri, par);
//			    			cnt++;
//			    		}
//			    		for(URI uri: calledService.getOutputList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break;
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsout.put(uri, par);
//			    			cnt++;
//			    		}
//			    		for(URI uri: calledService.getVarList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break;
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsvar.put(uri, par);
//			    			cnt++;
//			    		}
//			    		
//			    		
//			    		plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
//			    	}
//			    	
//			    	Condition pre =pr.getInitialState();
//			    	Condition eff = pr.getGoalState();
//			    	
//			    	//to add condition and effect
//			    	
//			    	curTime =System.currentTimeMillis();
//			    	
//
//			    	List<OperationNode> optimizedGraph = OperationNode.convertToGraph(plan, pre, eff);
//			    	System.out.println("Staring Optimization");
//			    	OperationNode.optimizeNew(optimizedGraph);
//			    	System.out.println("Staring Done");
//			    	long optimizationDuration =System.currentTimeMillis()-curTime;
//			    	averageOptimizationTime +=optimizationDuration;
//			    	
//					curSheet.addCell(new jxl.write.Number(col++,row,serializationDuration));
//					curSheet.addCell(new jxl.write.Number(col++,row,planningDuration));
//					curSheet.addCell(new jxl.write.Number(col++,row,optimizationDuration));
//					
//	    		}
//	    		offset+= ITERATIONS;
//	    		curSheet.addCell(new jxl.write.Number(colOffset+15,nocnt,averageSerializationTime/ ITERATIONS));
//	    		curSheet.addCell(new jxl.write.Number(colOffset+16,nocnt,averagePlanningTime/ ITERATIONS));
//	    		curSheet.addCell(new jxl.write.Number(colOffset+17,nocnt,averageOptimizationTime/ ITERATIONS));
//	    		//wb.write();
//	    	}
//	    	colOffset+=4;
//		
//    	
//		wb.write();
//		wb.close();
//		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("d:\\operators.txt"), sb.toString());
//		//read an xml node using xpath
//		//Node node = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
//		
//		
//		
//		//System.out.println(baseURI);
//		// /rdf:RDF/service:Service/service:describedBy/process:AtomicProcess/process:hasInput/process:Input/rdfs:label/
//    }
//    
//    /**
//    * Old method(uses graphplan)
//    * @throws Exception
//    */
//    public static void eval14() throws Exception{
//    	UtilityClass.createFolder("D:\\temp");
//    	File dir = new File("D:\\temp");
//    	for(File fl: dir.listFiles() )
//    	{
//    		fl.delete();
//    	}
//    	String curPost = "";
//    	WritableWorkbook wb = Workbook.createWorkbook(new File("d:\\evaluationpotimization.xls"));
//		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
//    	int offset =0;
//    	int colOffset=0;
//    	boolean ftime =true;
////    	
////    	Integer[] nos ={ 10, 20, 40, 60, 80,100};
////    	Integer[] reqNoList={2,4,6};
////    	Integer[] nos ={60};
////    	Integer[] reqNoList={6};
//    	//Integer[] nos ={200};
//    	
//    	
//    	
//    		 offset =0;
//	    	
//	    	
//	    	curSheet.addCell(new Label(colOffset+1,0,"PDDL Generation"));
//	    	curSheet.addCell(new Label(colOffset+2,0,"Planning"));
//	    	curSheet.addCell(new Label(colOffset+3,0,"Optimization"));
//	    	
//
//	    	//DomainModels dm = DomainModels.ReadModels("D:\\tt\\owlintact2");
//	    	DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\ds\\services\\r6\\s200");
//	    	dm.getContextModel().createSimpleContext();
//	    	//UtilityClass.writeFile(new File("D:\\bo.txt"), GraphNode.serialize(optimizedGraph,dm));
//	    	
//	    	Map<Service, ServiceNonfunctionalAnnotation> annotation = new HashMap<Service, ServiceNonfunctionalAnnotation>();
//	    	
//	    	for(Service s: dm.getServices())
//	    		annotation.put(s, new ServiceNonfunctionalAnnotation(s));
//	    	
//	    	
//	    	
//	    	ExecutionTime.GenerateExecutionTime(annotation, 200, 50, 50, 30);
//	    	
//	    	
//	    	for(int nocnt=0; nocnt <6; nocnt++){
//	    		double averageSerializationTime =0;
//	    		double averagePlanningTime =0;
//	    		double averageOptimizationTime =0;
//	    		double averageBTime =0;
//	    		double averageATime =0;
//	    		System.out.println("****Evaluation for no  "+nocnt);
//		    	for(int itcnt =0; itcnt <ITERATIONS ;itcnt++ ){
//		    		System.out.println("Iteration: "+itcnt );
//	    		
//	    			int row =offset+ itcnt+1;
//	    			int col =colOffset;
//	    			
//	    			
//	    			
//			    	//Add Vars
//
//			    	File folder = new File("D:\\Development\\BPLECONS\\ds\\featureModelSize\\s"+nocnt);
//			    	File[] fl =folder.listFiles();
//			    	long curTime = System.currentTimeMillis();
//			    	ProblemDomain pd = new ProblemDomain(dm);
//			    	String problemDomainpddl =  pd.PDDL3Serialize(curPost);
//			    	long serializationDuration = System.currentTimeMillis()-curTime;
//			    	File a = fl[UtilityClass.randInt(0, fl.length-1)];
//			    	curSheet.addCell(new Label(col++,row,a.getName()+ "Iteration: "+itcnt +" no : "+nocnt));
//	    			
//			    	System.out.println("File name: "+a.getName());
//			    	Problem pr = Problem.readFromFile(a, dm);
//			    	String problempddl = pr.PDDL3Serialize(curPost);
//			    	
//			    	try{
//			    		UtilityClass.writeFile(new File("D:\\temp\\pt"+curPost+".pddl"),problempddl);
//				    	UtilityClass.writeFile(new File("D:\\temp\\pta"+curPost+".pddl"),problemDomainpddl);
//			    	}
//			    	catch(Exception ex){
//			    		System.out.println("Exception in writing files!");
//			    		curPost= UUID.randomUUID().toString();
//			    		curPost= curPost.replaceAll("-", "");
//			    		itcnt--;
//			    		continue;
//			    	}
//			    	
//			    	
//			    	
//			    	averageSerializationTime+=serializationDuration;
//			    	curTime = System.currentTimeMillis();
//			    	//System.out.println(problem);
//			    	//System.out.println(problemDomain);
//			    	
//			    	//GraphPlan gp = new GraphPlan();
//			    	
//			    	//List<String[]> rawplan =gp.init2(problemDomain ,problem, 50, ftime,false);
//			    	
//			    	
//			    	ftime=false;
//			    	
//			    	
//			    	Properties options = Graphplan.getParserOptions();
//			    	Parser parser = new Parser(options);
//	                PDDLObject domain = parser.parse(new File("D:\\temp\\pta"+curPost+".pddl"));
//	                PDDLObject problem = parser.parse(new File("D:\\temp\\pt"+curPost+".pddl"));
//	                PDDLObject pb = null;
//	                List<String[]> rawplan =null;
//	                if (domain != null && problem != null) {
//	                    pb = parser.link(domain, problem);
//	                }
//	                // Gets the error manager of the pddl compiler
//	                ErrorManager mgr = parser.getErrorManager();
//	                // If the compilation produces errors we print it and stop
//	                
//	                if (mgr.contains(Message.ERROR)) {
//	                    mgr.print(Message.ALL);
//	                    throw new Exception("Error parsing");
//	                }
//	                // else we print the warning and start the planning process
//	                else {
//	                    
//	                    mgr.print(Message.WARNING);
//	                    
//	                   // System.out.println("\nParsing domain \"" + domain.getDomainName() + "\" done successfully ...");
//	                    //System.out.println("Parsing problem \"" + problem.getProblemName() + "\" done successfully ...\n");
//	                    curTime = System.currentTimeMillis();
//	                    
//	                    Graphplan planner = new Graphplan(pb);
//	                    
//	                    planner.preprocessing();
//	                    
//	                    Plan plan = planner.solve(20);
//	                    
//	                    //plan = planner.solve();
//	                    
//	                    if (plan != Plan.FAILURE) {
//	                       // System.out.println("\nfound plan as follows:\n");
//	                       // Graphplan.printPlan(plan);
//	                    	rawplan =Graphplan.getPlan(plan);
//	                    } else {
//	                        System.out.println("\n ****no solution plan found\n");
//	                       // throw new Exception("Plan not found!");
//	                        itcnt--;
//	                        continue;
////	                        wb.close();
////	                        return;
//	                    }
//	                }
//	                long planningDuration = System.currentTimeMillis()-curTime;
//	                
//	                averagePlanningTime+=planningDuration;
//			    	
//			    	
//	//		    	
//	//		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
//	//		    	{
//	//		
//	//					String[] s = rawplan.get(cnt);
//	//					for(String str : s)
//	//						System.out.print(str + " ");
//	//					System.out.println();
//	//		    	}
//			    	List<ServiceCall> plan  = new ArrayList<ServiceCall>();
//			    	for(String[] sl :rawplan)
//			    	{
//			    		Service calledService = null;
//			    		for(Service s : dm.getServices())
//			    			if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
//			    			{
//			    				calledService = s;
//			    				break;
//			    			}
//			    		if(calledService==null)
//							throw new Exception("Service does not exists");
//			    		int cnt =1;
//			    		HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
//			    		HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
//			    		HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();
//			    		
//			    		for(URI uri: calledService.getInputList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break; 
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsin.put(uri, par);
//			    			cnt++;
//			    		}
//			    		for(URI uri: calledService.getOutputList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break;
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsout.put(uri, par);
//			    			cnt++;
//			    		}
//			    		for(URI uri: calledService.getVarList()){
//			    			Instance par = null;
//			    			
//			    			for(Instance n: dm.getContextModel().getVars())
//			    				if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
//			    					par = n;
//			    					break;
//			    				}
//			    			if(par==null)
//			    				throw new Exception("Variable does not exists");
//			    			paramsvar.put(uri, par);
//			    			cnt++;
//			    		}
//			    		
//			    		
//			    		plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
//			    	}
//			    	
//			    	
//			    	
//			    	
//			    	
//			    	Condition pre =pr.getInitialState();
//			    	Condition eff = pr.getGoalState();
//			    	
//			    	//to add condition and effect
//			    	
//			    	
//			    	
//			    
//
//			    	List<OperationNode> optimizedGraph = OperationNode.convertToGraph(plan, pre, eff);
//			    	
//			    	double beforeTime = ExecutionTime.findExecutionTime(annotation, optimizedGraph);
//			    	averageBTime+=beforeTime;
//
//			    	//UtilityClass.writeFile(new File("D:\\"+a.getName()+"(a).gv"), GraphNode.serializedToGV( optimizedGraph));
//			    	
//			    	
//			    	curTime =System.currentTimeMillis();
//			    	boolean isSafe = OperationNode.safe2(optimizedGraph);
//			    	System.out.println("Safeness: "+ (isSafe?"SAFE":"NOT SAFE"));
//			    	if(!isSafe)
//			    	{
//			    		itcnt--;
//			    		continue;
//			    	}
//			    	
//			    	OperationNode.optimizeNew(optimizedGraph);
//			    	
//			    	//UtilityClass.writeFile(new File("D:\\"+a.getName()+"(b).gv"), GraphNode.serializedToGV( optimizedGraph));
//			    	long optimizationDuration =System.currentTimeMillis()-curTime;
//			    	averageOptimizationTime +=optimizationDuration;
//			    	
//			    	double afterTime = ExecutionTime.findExecutionTime(annotation, optimizedGraph);
//			    	averageATime+=afterTime;
//			    	
//					curSheet.addCell(new jxl.write.Number(col++,row,serializationDuration));
//					curSheet.addCell(new jxl.write.Number(col++,row,planningDuration));
//					curSheet.addCell(new jxl.write.Number(col++,row,optimizationDuration));
//					curSheet.addCell(new jxl.write.Number(col++,row,beforeTime));
//					curSheet.addCell(new jxl.write.Number(col++,row,afterTime));
//	    		}
//	    		offset+= ITERATIONS;
//	    		curSheet.addCell(new jxl.write.Number(colOffset+15,nocnt,averageSerializationTime/ ITERATIONS));
//	    		curSheet.addCell(new jxl.write.Number(colOffset+16,nocnt,averagePlanningTime/ ITERATIONS));
//	    		curSheet.addCell(new jxl.write.Number(colOffset+17,nocnt,averageOptimizationTime/ ITERATIONS));
//	    		curSheet.addCell(new jxl.write.Number(colOffset+18,nocnt,averageBTime/ ITERATIONS));
//	    		curSheet.addCell(new jxl.write.Number(colOffset+19,nocnt,averageATime/ ITERATIONS));
//	    		
//	    		//wb.write();
//	    	}
//	    	colOffset+=4;
//		
//    	
//		wb.write();
//		wb.close();
//		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("d:\\operators.txt"), sb.toString());
//		//read an xml node using xpath
//		//Node node = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
//		
//		
//		
//		//System.out.println(baseURI);
//		// /rdf:RDF/service:Service/service:describedBy/process:AtomicProcess/process:hasInput/process:Input/rdfs:label/
//    }
//}

package edu.ls3.magus.cl.fmconfigurator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import edu.ls3.magus.cl.contextmanager.basic.Condition;
import edu.ls3.magus.cl.contextmanager.basic.Instance;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstance;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstanceS;
import edu.ls3.magus.cl.contextmanager.context.ContextModel;
import edu.ls3.magus.cl.exceptions.UnsuccessfulMashupGeneration;
import edu.ls3.magus.cl.fmconfigurator.model.AdaptationResult;
import edu.ls3.magus.cl.fmconfigurator.model.AtomicSet;
import edu.ls3.magus.cl.fmconfigurator.model.AtomicSetNFAnnotationMap;
import edu.ls3.magus.cl.fmconfigurator.model.Feature;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureAnnotationSet;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureAtomicSetMap;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModel;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModelConfiguration;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.ComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.FlowComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.Node;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.OperationNode;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.ServiceCall;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.ExecutionTimeType;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.NonfunctionalConstraint;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.NonfunctionalMetric;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.ReliabilityType;
import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.ServiceNonfunctionalAnnotationMap;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.cl.mashupconfigurator.service.ServiceCollection;
import edu.ls3.magus.cl.planning.Problem;
import edu.ls3.magus.cl.planning.ProblemDomain;
import edu.ls3.magus.configuration.Configuration;
import edu.ls3.magus.generators.ServiceGenerator;
import edu.ls3.magus.utility.Holder;
import edu.ls3.magus.utility.SimpleLogger;
import edu.ls3.magus.utility.UtilityClass;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


/**
 * Hello world!
 *.
 *
 */
public class App 
{
	static final int ITERATIONS =20;
	static final int ITERATIONS_FM =20;
	static final String homeAddress="/home/mbashari/";
	//static final String homeAddress="/home/mbashari/";
	static int filecnt=6000;
	public static void main( String[] args ) throws Exception
	{
		//EvaluateMethodForBPELGeneration();
		//CallStepsForSample();
		//EvaluationBPELGenerationEfficiency();
		//readFamaWritXml();
		//readFamaWritXml2();
		//writeAllPossibleConfiguraiton2();
		//createServicesForASampleFM();
		//generateServiceForFM();
		//generateServiceForFM();
		//createEvaluationDirs();
		//writeAllPossibleConfiguraiton2();
		//runEvaluationRobustness();
		//fixNumberOfServices();
		//createConfigurationFileForSMFamilies();
		//runEvaluationFMSize();
		//testFMs();
		//runEvaluationServiceSize();
		//createDatasetHtml();
		//findAllAtomicSetsForSample();
		//testFeatureContributionEstimation();
		//evaluateNonfunctionalHeuristicPrecisionInTermsofTrainingDatasize();
//		testLinearRegression();
//		compareConfigurations();
//		generateServiceForFM2();
//		testSMFamily();
//		ssreadFamaWritXml2();
//		evaluateNonfunctionalHeuristicPrecisionInTermsofTrainingDatasizeDis();
//		readFamaWritXml3();
//		testGeneratedFM();
//		evaluateNonfunctionalHeuristicPrecisionInTermsofTrainingDatasizeDis1();
		//runEvaluationFMSizeNF();
		testFeatureContributionEstimation3();
	}
	
	public static void testLinearRegression() throws Exception{
		String[] lines = UtilityClass.readFileInLines("/home/mbashari/Dropbox/test.csv");
		double[][] x  = UtilityClass.readArrayFromCSV(lines);
		
		String[] ylines =  UtilityClass.readFileInLines("/home/mbashari/Dropbox/ys.csv");
		double[] y =  new double[ylines.length];
		for(int rcntr=0; rcntr<y.length;rcntr++)
			y[rcntr] = Double.valueOf(ylines[rcntr]);
		
//        String nxCsv = UtilityClass.writeAsCSV(x);
//        UtilityClass.writeFile(new File("/home/mbashari/testx.csv"), nxCsv);
//        
//        StringBuilder sb = new StringBuilder();
//        
//        for(int cntr=0; cntr<y.length; cntr++)
//        {
//        	sb.append(y[cntr]);
//        	if(cntr!= y.length-1)
//        		sb.append(System.lineSeparator());
//        	
//        }
//        UtilityClass.writeFile(new File("/home/mbashari/testy.csv"), sb.toString());
        
		
//		double[][] nx = new double[x.length-1][x[0].length-1];
//        for(int i =0;i<x.length;i++)
//        { 
//        	int newrow = i;
//        	if(i==2)
//        		continue;
//        	if(i>2)
//        		newrow = i-1;
//        	
//        	
//            int newColIdx = 0;
//            for(int j =0;j<x[i].length;j++)
//            {
//                if(j!=17)
//                {
//                    nx[newrow][newColIdx] = x[i][j];
//                    newColIdx++;
//                }               
//            }
//        }
//        
//        double[] ny = new double[y.length-1];
//        
//        int nYIdx = 0;
//        for(int j =0;j<y.length;j++)
//        {
//            if(j!=2)
//            {
//                ny[nYIdx] = y[j];
//                nYIdx++;
//            }               
//        }
//		
//        if(UtilityClass.hasDuplicateRows(nx))
//        	System.out.println("Has duplicate rows");
//        
//        if(UtilityClass.hasDuplicateCols(nx))
//        	System.out.println("Has duplicate cols");
//        
//        String nxCsv = UtilityClass.writeAsCSV(nx);
//        UtilityClass.writeFile(new File("/home/mbashari/test2.csv"), nxCsv);
        
        
        
        
		int numberOfFailure = 0;
		
		for(int ccntr=0; ccntr<50; ccntr++){
        
			OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
			
			regression.setNoIntercept(true);
			
			
			regression.newSampleData(y, x);
			
			double[] beta =null;
			
			boolean successful = true;
			
			try{
			
				beta = regression.estimateRegressionParameters();
			
			}
			catch(SingularMatrixException ex){
				System.out.println("Unsuccessfull Mashup Generation");
				successful = false;
				numberOfFailure++;
			}
			if(successful){
				System.out.println("----------------------------");
				for(int cntr =0; cntr<beta.length; cntr++)
					System.out.println(beta[cntr]);
				System.out.println("----------------------------");
				
			}
				
			
			UtilityClass.DoubleShuffle(x, y);
		}
		
		System.out.println("Number of failures : " + numberOfFailure);
	}
	
	public static void ee() throws Exception{
		StringBuilder sb = new StringBuilder();
		sb.append("(define (domain ptb)");
		sb.append(System.lineSeparator());
		sb.append("(:requirements :strips :typing  :negative-preconditions )");
		sb.append(System.lineSeparator());

		sb.append("(:types ");
		sb.append(System.lineSeparator());
		sb.append("tp ) ");
		sb.append(System.lineSeparator());
		sb.append("(:predicates ");
		sb.append(System.lineSeparator());
		for(int cnt=0; cnt<150; cnt++){
			sb.append("\t(x"+cnt+" ?tp)");
			sb.append(System.lineSeparator());
		}
		sb.append(")");
		sb.append(System.lineSeparator());

		for(int cnt=1; cnt<150; cnt++){
			sb.append("(:action a"+cnt+"");
			sb.append(System.lineSeparator());
			sb.append("\t:parameters (");
			sb.append(System.lineSeparator());
			sb.append("\t\t?vtp - tp");
			sb.append(System.lineSeparator());
			sb.append("\t)");
			sb.append(System.lineSeparator());
			sb.append("\t:precondition");
			sb.append(System.lineSeparator());
			sb.append("\t\t(x"+Integer.toString(cnt-1)+" ?vtp)");
			sb.append(System.lineSeparator());

			sb.append("\t:effect");
			sb.append(System.lineSeparator());
			sb.append("\t\t(x"+cnt+" ?vtp)");
			sb.append(System.lineSeparator());

			sb.append(")");
			sb.append(System.lineSeparator());
		}
		sb.append(")");
		sb.append(System.lineSeparator());
		UtilityClass.writeFile(new File("D:\\ptb.pddl"), sb.toString());
	}
	/**
	 * Find number of possible services to select in different steps of planning
	 * @throws Exception
	 */
	public static void FindNumberOfPossibleActionInEachStep() throws Exception{
		DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\dataset3\\s200");
		dm.getContextModel().createSimpleContext();
		List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();


		String cond1 = "p7f9d1560";
		String cond2 = "pfc4299ba";

//		String postCond1 = dm.getContextModel().getFactTypes().getStateFactTypeList().get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0, dm.getContextModel().getFactTypes().getStateFactTypeList().size()-1)).getTypeName();
//		String postCond2 = dm.getContextModel().getFactTypes().getStateFactTypeList().get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0, dm.getContextModel().getFactTypes().getStateFactTypeList().size()-1)).getTypeName();
		//String postCond1 = "p3e2f7d26";
		//String postCond2 = "pc550de6b";
		//System.out.println("*****"+postCond1);
		//System.out.println("*****"+postCond2);

		Instance[] params = {dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond1)[0].getParams()[0].getTypeName())[0],dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond1)[0].getParams()[1].getTypeName())[0]} ;
		isl.add( new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName(cond1)[0],params),false));
		Instance[] params2 ={dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond2)[0].getParams()[0].getTypeName())[0],dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond2)[0].getParams()[1].getTypeName())[0]} ;
		isl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName(cond2)[0],params2),false));


		Condition curCondition = new Condition(isl);

		int itcnt=0;
		while(itcnt<100){
			Map<Service,Map<URI, Instance>>  vs = dm.getServiceCollection().getExecutableServices(curCondition,dm.getContextModel());
			System.out.println("Iteration " +itcnt +" possible services: "+ vs.size());
			if(vs.size()==0){
				System.out.println("No more services!");
				break;

			}

			Service[] ss = vs.keySet().toArray(new Service[0]);
			Service selectedService = ss[UtilityClass.randInt(0, ss.length-1)];
			System.out.println("Selected Service: " + selectedService.getName());    		
			curCondition =  selectedService.getContextAfterExc(curCondition,vs.get(selectedService));
			System.out.println("Size of facts: " +curCondition.getConditions().size());
			itcnt++;
		}

	}


	/**
	 * Creates PDDL file for example problem
	 * @throws Exception
	 */
	public static void CreatePDDLFileForSample() throws Exception{
		//boolean ftime =true;
		DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\tt\\owlintact2");
		dm.getContextModel().createSimpleContext();

		List<StateFactInstanceS> gsl = new ArrayList<StateFactInstanceS>();
		List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();
		Instance[] params = {dm.getContextModel().getInstanceByName("vCustomer")[0],dm.getContextModel().getInstanceByName("vPurchaseOrder")[0]} ;
		isl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("purchased")[0],params),false));

		Instance[] params3 = {dm.getContextModel().getInstanceByName("vInvoice")[0],dm.getContextModel().getInstanceByName("vTaxInfo")[0]} ;
		gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("IncludesTaxInfo")[0],params3),false));
		Instance[] params4 = {dm.getContextModel().getInstanceByName("vInvoice")[0],dm.getContextModel().getInstanceByName("vShippingInfo")[0]} ;
		gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("IncludesShippingPrice")[0],params4),false));
		Instance[] params2 = {dm.getContextModel().getInstanceByName("vPurchaseOrder")[0],dm.getContextModel().getInstanceByName("vInvoice")[0]} ;
		gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("hasInvoice")[0],params2),false));

		Condition pre = new Condition(isl);
		Condition eff = new Condition(gsl);

		Problem pr = new Problem(dm.getContextModel(), pre, eff);
		ProblemDomain pd = new ProblemDomain(dm.getContextModel(),dm.getServiceCollection());
		String problem = pr.PDDL3Serialize(null);
		String problemDomain =  pd.PDDL3Serialize(null);

		UtilityClass.writeFile(new File("D:\\pt.pddl"),problem);
		UtilityClass.writeFile(new File("D:\\pta.pddl"),problemDomain);

	}




	public static void CallStepsForSample() throws Exception{
//		 ftime =true;
		DomainModels dm = DomainModels.ReadModels(homeAddress+ "BPLECONS/tt/owlintact4");
		dm.getContextModel().createSimpleContext();

		List<StateFactInstanceS> gsl = new ArrayList<StateFactInstanceS>();
		List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();
		Instance[] params = {dm.getContextModel().getInstanceByName("vCustomer")[0],dm.getContextModel().getInstanceByName("vPurchaseOrder")[0]} ;
		isl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("Ordered")[0],params),false));

		Instance[] params3 = {dm.getContextModel().getInstanceByName("vInvoice")[0],dm.getContextModel().getInstanceByName("vTaxInfo")[0]} ;
		gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("IncludesTaxInfo")[0],params3),false));
		Instance[] params4 = {dm.getContextModel().getInstanceByName("vInvoice")[0],dm.getContextModel().getInstanceByName("vSchedule")[0]} ;
		gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("IncludesShippingSchedule")[0],params4),false));
		Instance[] params2 = {dm.getContextModel().getInstanceByName("vPurchaseOrder")[0],dm.getContextModel().getInstanceByName("vInvoice")[0]} ;
		gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("HasInvoice")[0],params2),false));
		Instance[] params5 = {dm.getContextModel().getInstanceByName("vInvoice")[0],dm.getContextModel().getInstanceByName("vProductionSchedule")[0]} ;
		gsl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName("IncludesProductionSchedule")[0],params5),false));
		
		Condition pre = new Condition(isl);
		Condition eff = new Condition(gsl);

		Problem pr = new Problem(dm.getContextModel(), pre, eff);
		ProblemDomain pd = new ProblemDomain(dm.getContextModel(),dm.getServiceCollection());
		String problemPDDL = pr.PDDL3Serialize(null);
		String problemDomainPDDL =  pd.PDDL3Serialize(null);

		String problemAddress = homeAddress+ "temp/pt.pddl";
		String domainAddress = homeAddress+ "temp/pta.pddl";

		UtilityClass.writeFile(new File(problemAddress),problemPDDL);
		UtilityClass.writeFile(new File(domainAddress),problemDomainPDDL);

		long curTime = System.currentTimeMillis();
		List<String[]> rawplan = Callplanner(problemAddress, domainAddress);
		long duration = System.currentTimeMillis()-curTime;

		System.out.println("Planning time: "+ duration);



		List<ServiceCall> plan  = new ArrayList<ServiceCall>();


		for(String[] sl :rawplan)
		{
			Service calledService = null;
			for(Service s : dm.getServiceCollection().getServices()){
				if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
				{
					calledService = s;
					break;
				}
				if((s.getReceiveService()!=null)&&(s.getReceiveService().getName().toLowerCase().equals(sl[0].toLowerCase()))){
					calledService = s.getReceiveService();
					break;
				}
			}
			if(calledService==null)
				throw new Exception("Service does not exists");
			int cnt =1;
			HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
			HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
			HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();

			for(URI uri: calledService.getInputList()){
				Instance par = null;

				for(Instance n: dm.getContextModel().getVars())
					if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
						par = n;
						break;
					}
				if(par==null)
					throw new Exception("Variable does not exists");
				paramsin.put(uri, par);
				cnt++;
			}
			for(URI uri: calledService.getOutputList()){
				Instance par = null;

				for(Instance n: dm.getContextModel().getVars())
					if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
						par = n;
						break;
					}
				if(par==null)
					throw new Exception("Variable does not exists");
				paramsout.put(uri, par);
				cnt++;
			}
			for(URI uri: calledService.getVarList()){
				Instance par = null;

				for(Instance n: dm.getContextModel().getVars())
					if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
						par = n;
						break;
					}
				if(par==null)
					throw new Exception("Variable does not exists");
				paramsvar.put(uri, par);
				cnt++;
			}


			plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
		}

		//    	for(ServiceCall sc:plan)
		//    		System.out.println(sc.getCalledService().getName());

		List<OperationNode> optimizedGraph = OperationNode.convertToGraph(plan, pre, eff);

		//UtilityClass.writeFile(new File("D:\\bo.txt"), GraphNode.serialize(optimizedGraph,dm));

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
		OperationNode.optimizeNew(optimizedGraph);


		List<OperationNode> processedNodes = new ArrayList<OperationNode>();
		LinkedList<OperationNode> gl= new LinkedList<OperationNode>();

		for(OperationNode g: optimizedGraph){
			processedNodes.add(g);
			gl.add(g);
		}
		int cnt =0;
		while(!gl.isEmpty()){
			OperationNode curg= gl.removeFirst();
			String gname = "emptynode";
			if(curg.getCalledService()!=null)
			{
				gname = curg.getCalledService().getCalledService().getName();
			}
			for(OperationNode ng : curg.getEdges()){
				if(!processedNodes.contains(ng)){
					gl.addLast(ng);
					processedNodes.add(ng);
				}
				String dname = "emptynode";
				if(ng.getCalledService()!=null)
				{
					dname = ng.getCalledService().getCalledService().getName();
				}
				System.out.println("There is a edge between "+gname+" and "+dname+" .");
				cnt++;
			}
		}
		System.out.println("total number of edges 2: "+cnt);
		String graphGV = OperationNode.serializedToGV(optimizedGraph);
		String graphJson = OperationNode.serializedToJSON(optimizedGraph);
		edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "test.gv"),graphGV);
		edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "test.json"),graphJson);

		String gs = OperationNode.serialize(optimizedGraph);
		edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "wf1.txt"),gs);

		System.out.println("Trying Optimization No 1");
		ComponentNode n = OperationNode.BPELAlgorithmNo1(optimizedGraph);
	
		String nstr = n.serializeToGV(true);
		edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "workflowreturn1.gv"),nstr);
		
		System.out.println("Flows "+n.GetNoOfFlows()+" Sequence "+ n.GetNoOfSequence() + " Link "+ n.GetNoOfLink());
		
		//List<OperationNode> workflow1 = n.convertToWorkflow();
		//String gf = OperationNode.serializedToGV(workflow1);
		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "workflowreturn1.gv"),gf);


		System.out.println("Trying Optimization No 2");
		FlowComponentNode fcn =FlowComponentNode.convertToFlowWithLink(optimizedGraph);
		fcn.OptimizeNo1();
		  
		System.out.println("Flows "+fcn.GetNoOfFlows()+" Sequence "+ fcn.GetNoOfSequence() + " Link "+ fcn.GetNoOfLink());
		
		nstr = fcn.serializeToGV(true);
		edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "workflowreturn2.gv"),nstr);
		
		//List<OperationNode> workflow2 = fcn.convertToWorkflow();
		//gf = OperationNode.serializedToGV(workflow2);
		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "workflowreturn2.gv"),gf);
		
		    	
		System.out.println("Trying Optimization No 3");
		FlowComponentNode fcn1 =FlowComponentNode.convertToFlowWithLink(optimizedGraph);
		fcn1.OptimizeNo2();

		System.out.println("Flows "+fcn1.GetNoOfFlows()+" Sequence "+ fcn1.GetNoOfSequence() + " Link "+ fcn1.GetNoOfLink());

		
		nstr = fcn1.serializeToGV(true);
		edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "workflowreturn3.gv"),nstr);
		//List<OperationNode> workflow3 = fcn1.convertToWorkflow();
		// gf = OperationNode.serializedToGV(workflow3);
		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "workflowreturn3.gv"),gf);

		//String s = fcn1.serializeToGV(true);
		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "testblock.gv"),s);
		//    	System.out.println("Execution Time(After Optimization): "+ ExecutionTime.findExecutionTime(annotation, optimizedGraph));
		//    	

		//UtilityClass.writeFile(new File("D:\\ao.txt"), GraphNode.serialize(optimizedGraph,dm));



	}


	/*
	 * Checking Workflow read and write function
	 */
	public static void CheckGraphReadAndWrite() throws Exception{
		DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\tt\\owlintact2");
		dm.getContextModel().createSimpleContext();
		List<OperationNode> optimizedGraph = OperationNode.readFromFile(new File("D:\\bo.txt"), dm.getContextModel(), dm.getServiceCollection());
		//UtilityClass.writeFile(new File("D:\\bo.txt"), GraphNode.serialize(optimizedGraph,dm,pre,eff));

		//System.out.println(GraphNode.safe(optimizedGraph));

		OperationNode.optimizeNew(optimizedGraph);

		UtilityClass.writeFile(new File("D:\\ao.txt"), OperationNode.serialize(optimizedGraph));

		List<OperationNode> processedNodes = new ArrayList<OperationNode>();
		LinkedList<OperationNode> gl= new LinkedList<OperationNode>();

		for(OperationNode g: optimizedGraph){
			processedNodes.add(g);
			gl.add(g);
		}
		int cnt =0;
		while(!gl.isEmpty()){
			OperationNode curg= gl.removeFirst();
			String gname = "emptynode";
			if(curg.getCalledService()!=null)
			{
				gname = curg.getCalledService().getCalledService().getName();
			}
			for(OperationNode ng : curg.getEdges()){
				if(!processedNodes.contains(ng)){
					gl.addLast(ng);
					processedNodes.add(ng);
				}
				String dname = "emptynode";
				if(ng.getCalledService()!=null)
				{
					dname = ng.getCalledService().getCalledService().getName();
				}
				System.out.println("There is a edge between "+gname+" and "+dname+" .");
				cnt++;
			}
		}
		System.out.println("total number of edges 2: "+cnt);
		String graphGV = OperationNode.serializedToGV(optimizedGraph);
		edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("D:\\test.gv"),graphGV);
	}

	/**
	 * Finds average number of conditions for different feature model configurations
	 * @throws Exception
	 */
//	public static void FindAverageConditionForDifferentFMSize() throws Exception{
//		boolean ftime =true;
//		DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\dataset3\\s1000");
//		dm.getContextModel().createSimpleContext();
//		FeatureAnnotationSet fas = FeatureAnnotationSet.readAnnotationSet("D:\\fas.ser");
//		int noOfFeatures = 100;
//		int wsum =0;
//		int min=-1;
//		int max=-1;
//		for(int itcnt=1; itcnt<100; itcnt++){
//
//			List<Feature> flc = new ArrayList<Feature>();
//			Feature fl[] = fas.getAnnotationMap().keySet().toArray(new Feature[0]);
//
//
//
//			for(int cnt=0; cnt<noOfFeatures; cnt++){
//				flc.add(fl[UtilityClass.randInt(0, fl.length-1)]);
//			}
//			FeatureModelConfiguration fmc = new FeatureModelConfiguration(flc);
//
//			Condition pre = fas.findPrecondition(fmc);
//			Condition eff = fas.findEffect(fmc);
//			int sum = pre.getConditions().size()+eff.getConditions().size();
//			wsum +=sum;
//			if((min==-1) || (sum<min))
//				min =sum;
//			if((max==-1) || (max<sum))
//				max =sum;
//			//System.out.println(itcnt + " : "+ sum);
//			System.out.println(sum);
//			//    	List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();
//			//    	
//			//    	String cond1 = "p7f9d1560";
//			//    	String cond2 = "pfc4299ba";
//			//    	
//			//    	String postCond1 = dm.getContextModel().getFactTypes().getStateFactTypeList().get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0, dm.getContextModel().getFactTypes().getStateFactTypeList().size()-1)).getTypeName();
//			//    	String postCond2 = dm.getContextModel().getFactTypes().getStateFactTypeList().get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0, dm.getContextModel().getFactTypes().getStateFactTypeList().size()-1)).getTypeName();
//			//    	//String postCond1 = "p3e2f7d26";
//			//    	//String postCond2 = "pc550de6b";
//			//    	//System.out.println("*****"+postCond1);
//			//    	//System.out.println("*****"+postCond2);
//			//    	
//			//    	Instance[] params = {dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond1)[0].getParams()[0].getTypeName())[0],dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond1)[0].getParams()[1].getTypeName())[0]} ;
//			//    	isl.add( new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName(cond1)[0],params),false));
//			//    	Instance[] params2 ={dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond2)[0].getParams()[0].getTypeName())[0],dm.getContextModel().getInstanceByName("v"+dm.getContextModel().getInstaceFactTypeByName(cond2)[0].getParams()[1].getTypeName())[0]} ;
//			//    	isl.add(new StateFactInstanceS( new StateFactInstance(dm.getContextModel().getInstaceFactTypeByName(cond2)[0],params2),false));
//			//    	
//			//    	
//			//    	if(pre.getConditions().size()==0)
//			//    		continue;
//			//    	if(eff.getConditions().size()==0)
//			//    		continue;
//			//    	
//			//    	pre.getConditions().add(isl.get(0));
//			//    	pre.getConditions().add(isl.get(1));
//			//    	//Problem pr = new Problem(dm.getContextModel(), new Condition(isl), eff);
//			//    	Problem pr = new Problem(dm.getContextModel(), pre, eff);
//			//    	ProblemDomain pd = new ProblemDomain(dm);
//			//    	
//			//    	String problem = pr.PDDLserialize();
//			//    	String problemDomain =  pd.PDDLserialize();
//			//    	//long serializationDuration = System.currentTimeMillis()-curTime;
//			//    	//curTime = System.currentTimeMillis();
//			//    	//System.out.println(problem);
//			//    	//System.out.println(problemDomain);
//			//    	
//			//    	GraphPlan gp = new GraphPlan();
//			//    	List<String[]> rawplan =null;
//			//    	try{
//			//    	 rawplan =gp.init2(problemDomain ,problem, 50, ftime,false);
//			//    	}
//			//    	catch(PlanNotFoundException ex){
//			//    		System.out.println("No");
//			//    		
//			//    		continue;
//			//    	}
//			//    	finally{
//			//    		ftime=false;
//			//    	}
//			//    	System.out.println("Yes");
//
//		}
//		System.out.println( "Average : "+ wsum/100);
//		System.out.println( "Min : "+ min);
//		System.out.println( "Max : "+ max);
//		//    	//long planningDuration = System.currentTimeMillis()-curTime;
//		//    	//curTime = System.currentTimeMillis();
//		//    	for(int cnt =0; cnt< rawplan.size(); cnt++)
//		//    	{
//		//    		boolean isequal = false;
//		//    		for(int cnt2=0; cnt2<cnt ; cnt2++  )
//		//    		{
//		//    			
//		//    			if(rawplan.get(cnt2).length != rawplan.get(cnt).length){
//		//    				continue;
//		//    			}
//		//    			
//		//    			boolean tqual = true;
//		//    			for(int cnt3=0; cnt3< rawplan.get(cnt2).length; cnt3++)
//		//    				if(!rawplan.get(cnt2)[cnt3].equals(rawplan.get(cnt)[cnt3]))
//		//    					tqual = false;
//		//    			if(tqual){
//		//    				isequal =true;
//		//    				break;
//		//    			}
//		//    		}
//		//    		if(isequal){
//		//    			rawplan.remove(cnt);
//		//    			cnt--;
//		//    		}
//		//    		
//		//    	}
//		//    	
//		//    	
//		//    	for(int cnt =0; cnt< rawplan.size(); cnt++)
//		//    	{
//		//
//		//			String[] s = rawplan.get(cnt);
//		//			for(String str : s)
//		//				System.out.print(str + " ");
//		//			System.out.println();
//		//    	}
//
//	}


	/***
	 * Performs evaluation on efficiency of method based on the number of services
	 * @throws Exception
	 */
	//    public static void eval1() throws Exception{
	//    	WritableWorkbook wb = Workbook.createWorkbook(new File("d:\\exceloutput\\evaluation.xls"));
	//		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
	//    	int offset =0;
	//    	boolean ftime =true;
	//    	
	//    	Integer[] nos ={200,400,600,800,1000};
	//    	
	//    		
	//    	
	//    	curSheet.addCell(new Label(1,0,"PDDL Generation"));
	//    	curSheet.addCell(new Label(2,0,"Planning"));
	//    	curSheet.addCell(new Label(3,0,"Optimization"));
	//    	
	//    	
	//    	for(int itcnt =0; itcnt <ITERATIONS ;itcnt++ ){
	//    		
	//    		for(int nocnt=0; nocnt <nos.length; nocnt++){
	//    			int row =offset+ nocnt+1;
	//    			int col =0;
	//    			curSheet.addCell(new Label(col++,row,"Iteration: "+itcnt +" no of services: "+ nos[nocnt]));
	//    			
	//    			long curTime = System.currentTimeMillis();
	//    			
	//		    	//DomainModels dm = DomainModels.ReadModels("D:\\tt\\owlintact2");
	//		    	DomainModels dm = DomainModels.ReadModels("D:\\dataset\\s"+nos[nocnt]);
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
	//		    	String postCond1 = dm.getContextModel().getFactTypes().getStateFactTypeList().get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0, dm.getContextModel().getFactTypes().getStateFactTypeList().size())).getTypeName();
	//		    	String postCond2 = dm.getContextModel().getFactTypes().getStateFactTypeList().get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0, dm.getContextModel().getFactTypes().getStateFactTypeList().size())).getTypeName();
	//		    	//String postCond1 = "p3e2f7d26";
	//		    	//String postCond2 = "pc550de6b";
	//		    	System.out.println("*****"+postCond1);
	//		    	System.out.println("*****"+postCond2);
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
	//		    	String problem = pr.PDDLserialize();
	//		    	String problemDomain =  pd.PDDLserialize();
	//		    	long serializationDuration = System.currentTimeMillis()-curTime;
	//		    	curTime = System.currentTimeMillis();
	//		    	//System.out.println(problem);
	//		    	//System.out.println(problemDomain);
	//		    	
	//		    	GraphPlan gp = new GraphPlan();
	//		    	
	//		    	List<String[]> rawplan =gp.init2(problemDomain ,problem, 50, ftime,false);
	//		    	ftime=false;
	//		    	long planningDuration = System.currentTimeMillis()-curTime;
	//		    	curTime = System.currentTimeMillis();
	//		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
	//		    	{
	//		    		boolean isequal = false;
	//		    		for(int cnt2=0; cnt2<cnt ; cnt2++  )
	//		    		{
	//		    			
	//		    			if(rawplan.get(cnt2).length != rawplan.get(cnt).length){
	//		    				continue;
	//		    			}
	//		    			
	//		    			boolean tqual = true;
	//		    			for(int cnt3=0; cnt3< rawplan.get(cnt2).length; cnt3++)
	//		    				if(!rawplan.get(cnt2)[cnt3].equals(rawplan.get(cnt)[cnt3]))
	//		    					tqual = false;
	//		    			if(tqual){
	//		    				isequal =true;
	//		    				break;
	//		    			}
	//		    		}
	//		    		if(isequal){
	//		    			rawplan.remove(cnt);
	//		    			cnt--;
	//		    		}
	//		    		
	//		    	}
	//		    	
	//		    	
	//		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
	//		    	{
	//		
	//					String[] s = rawplan.get(cnt);
	//					for(String str : s)
	//						System.out.print(str + " ");
	//					System.out.println();
	//		    	}
	//		    	List<ServiceCall> plan  = new ArrayList<ServiceCall>();
	//		    	for(String[] sl :rawplan)
	//		    	{
	//		    		Service calledService = null;
	//		    		for(Service s : dm.getServices())
	//		    			if(s.getName().equals(sl[0]))
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
	//		    				if(n.getName().equals(sl[cnt])){
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
	//		    				if(n.getName().equals(sl[cnt])){
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
	//		    				if(n.getName().equals(sl[cnt])){
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
	//		    	List<GraphNode> optimizedGraph =  GraphNode.optimize(plan);
	//		    	long optimizationDuration = System.currentTimeMillis()-curTime;
	//		    	
	//		    	List<GraphNode> processedNodes = new ArrayList<GraphNode>();
	//				LinkedList<GraphNode> gl= new LinkedList<GraphNode>();
	//				
	//				for(GraphNode g: optimizedGraph){
	//					processedNodes.add(g);
	//					gl.add(g);
	//				}
	//				int cnt =0;
	//				while(!gl.isEmpty()){
	//					GraphNode curg= gl.removeFirst();
	//					String gname = "emptynode";
	//					if(curg.getCalledService()!=null)
	//					{
	//						gname = curg.getCalledService().getCalledService().getName();
	//					}
	//					for(GraphNode ng : curg.getEdges()){
	//						if(!processedNodes.contains(ng)){
	//							gl.addLast(ng);
	//							processedNodes.add(ng);
	//						}
	//						String dname = "emptynode";
	//						if(ng.getCalledService()!=null)
	//						{
	//							dname = ng.getCalledService().getCalledService().getName();
	//						}
	//						System.out.println("There is a edge between "+gname+" and "+dname+" .");
	//						cnt++;
	//					}
	//				}
	//				System.out.println("total number of edges 2: "+cnt);
	//				StringBuilder sb = new StringBuilder();
	//				sb.append(" digraph G {"+ System.lineSeparator());
	//				
	//				gl= new LinkedList<GraphNode>();
	//				HashSet<GraphNode> finalNodes = new HashSet<GraphNode>();
	//				processedNodes = new ArrayList<GraphNode>();
	//				
	//				for(GraphNode g: optimizedGraph){
	//					gl.add(g);
	//					processedNodes.add(g);
	//					String gname = "emptynode";
	//					if(g.getCalledService()!=null)
	//					{
	//						gname = g.getCalledService().getCalledService().getName();
	//					}
	//					sb.append(  "Start -> "+gname.replaceAll("Service", "")+";"+System.lineSeparator());
	//				}
	//				while(!gl.isEmpty()){
	//					GraphNode curg= gl.removeFirst();
	//					String gname = "emptynode";
	//					if(curg.getCalledService()!=null)
	//					{
	//						gname = curg.getCalledService().getCalledService().getName();
	//					}
	//					if( curg.getEdges().size() ==0)
	//					{
	//						if(!finalNodes.contains(curg))
	//						   sb.append(  gname.replaceAll("Service", "")+" -> End;"+System.lineSeparator());
	//						finalNodes.add(curg);
	//						continue;
	//					}
	//					for(GraphNode ng : curg.getEdges()){
	//						if(!processedNodes.contains(ng)){
	//							processedNodes.add(ng);
	//							gl.addLast(ng);
	//						}
	//						String dname = "emptynode";
	//						if(ng.getCalledService()!=null)
	//						{
	//							dname = ng.getCalledService().getCalledService().getName();
	//						}
	//						sb.append(  gname.replaceAll("Service", "")+" -> "+dname.replaceAll("Service", "")+";"+System.lineSeparator());
	//					}
	//				}
	//				sb.append("}");
	//				edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("D:\\test.gv"),sb.toString());
	//		//    	for(URI k: vars.keySet()){
	//		//    		System.out.println( vars.get(k).getType().getTypeName() +" ("+vars.get(k).getName()+");");
	//		//    	}
	//				curSheet.addCell(new jxl.write.Number(col++,row,serializationDuration));
	//				curSheet.addCell(new jxl.write.Number(col++,row,planningDuration));
	//				curSheet.addCell(new jxl.write.Number(col++,row,optimizationDuration));
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




	/***
	 * Performs evaluation on efficiency of method based on the number of services (NEW VERSION)
	 * @throws Exception
	 */
	public static void EvaluateMethodBasedOnNumberOfServices() throws Exception{
		WritableWorkbook wb = Workbook.createWorkbook(new File(homeAddress+"evaluationsvs.xls"));
		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
		int offset =0;
		int colOffset=0;
//		boolean ftime =true;

		//Integer[] nos ={100, 200,400,600,800,1000,5000,10000};
		Integer[] nos ={1000, 2000,3000,4000,5000,6000,7000,8000,9000,10000};
		Integer[] reqNoList={3,6,9};

		//Integer[] nos ={200};


		for(int reqcnt=0; reqcnt<reqNoList.length; reqcnt++){
//			offset =0;


			curSheet.addCell(new Label(colOffset+1,0,"PDDL Generation"));
			curSheet.addCell(new Label(colOffset+2,0,"Planning"));
			curSheet.addCell(new Label(colOffset+3,0,"Optimization"));

			for(int nocnt=0; nocnt <nos.length; nocnt++){
				double averageSerializationTime =0;
				double averagePlanningTime =0;
				double averageOptimizationTime =0;
				System.out.println("****Evaluation for no of services "+nos[nocnt]+ "and no of reqs "+reqNoList[reqcnt]);
				for(int itcnt =0; itcnt <ITERATIONS ;itcnt++ ){
					System.out.println("Iteration: "+itcnt );

					int row =offset+ itcnt+1;
					int col =colOffset;
					curSheet.addCell(new Label(col++,row,"Iteration: "+itcnt +" no of services: "+ nos[nocnt]));



					//DomainModels dm = DomainModels.ReadModels("D:\\tt\\owlintact2");
					DomainModels dm = DomainModels.ReadModels(homeAddress+"BPLECONS/ds/services/r"+reqNoList[reqcnt]+"/s"+nos[nocnt]);
					dm.getContextModel().createSimpleContext();
					//Add Vars








					File folder = new File(homeAddress+"BPLECONS/ds/featureModelFS/r"+reqNoList[reqcnt]+"/s"+nos[nocnt]);
					File[] fl =folder.listFiles();
					long curTime = System.currentTimeMillis();
					ProblemDomain pd = new ProblemDomain(dm.getContextModel(),dm.getServiceCollection());
					String problemDomainpddl =  pd.PDDL3Serialize(null);
					long serializationDuration = System.currentTimeMillis()-curTime;

					Problem pr = Problem.readFromFile(fl[UtilityClass.randInt(0, fl.length-1)], dm.getContextModel());
					String problempddl = pr.PDDL3Serialize(null);
					String problemAddress = homeAddress+"temp/pt.pddl";
					String domainAddress = homeAddress+"temp/pta.pddl";





					averageSerializationTime+=serializationDuration;
					curTime = System.currentTimeMillis();			    	
					UtilityClass.writeFile(new File(problemAddress),problempddl);
					UtilityClass.writeFile(new File(domainAddress),problemDomainpddl);

					List<String[]> rawplan = Callplanner(problemAddress, domainAddress);



					long planningDuration = System.currentTimeMillis()-curTime;

					averagePlanningTime+=planningDuration;




					//		    	
					//		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
					//		    	{
					//		
					//					String[] s = rawplan.get(cnt);
					//					for(String str : s)
					//						System.out.print(str + " ");
					//					System.out.println();
					//		    	}
					List<ServiceCall> plan  = new ArrayList<ServiceCall>();
					for(String[] sl :rawplan)
					{
						Service calledService = null;
						for(Service s : dm.getServiceCollection().getServices())
							if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
							{
								calledService = s;
								break;
							}
						if(calledService==null)
							throw new Exception("Service does not exists");
						int cnt =1;
						HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
						HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
						HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();

						for(URI uri: calledService.getInputList()){
							Instance par = null;

							for(Instance n: dm.getContextModel().getVars())
								if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
									par = n;
									break;
								}
							if(par==null)
								throw new Exception("Variable does not exists");
							paramsin.put(uri, par);
							cnt++;
						}
						for(URI uri: calledService.getOutputList()){
							Instance par = null;

							for(Instance n: dm.getContextModel().getVars())
								if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
									par = n;
									break;
								}
							if(par==null)
								throw new Exception("Variable does not exists");
							paramsout.put(uri, par);
							cnt++;
						}
						for(URI uri: calledService.getVarList()){
							Instance par = null;

							for(Instance n: dm.getContextModel().getVars())
								if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
									par = n;
									break;
								}
							if(par==null)
								throw new Exception("Variable does not exists");
							paramsvar.put(uri, par);
							cnt++;
						}


						plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
					}

//					Condition pre =pr.getInitialState();
//					Condition eff = pr.getGoalState();

					//to add condition and effect

					curTime =System.currentTimeMillis();


//					List<OperationNode> optimizedGraph = OperationNode.convertToGraph(plan, pre, eff);
					//	GraphNode.optimizeNew(optimizedGraph);

					long optimizationDuration =System.currentTimeMillis()-curTime;
					averageOptimizationTime +=optimizationDuration;

					curSheet.addCell(new jxl.write.Number(col++,row,serializationDuration));
					curSheet.addCell(new jxl.write.Number(col++,row,planningDuration));
					curSheet.addCell(new jxl.write.Number(col++,row,optimizationDuration));

				}
				offset+= ITERATIONS;
				curSheet.addCell(new jxl.write.Number(colOffset+15,nocnt,averageSerializationTime/ ITERATIONS));
				curSheet.addCell(new jxl.write.Number(colOffset+16,nocnt,averagePlanningTime/ ITERATIONS));
				curSheet.addCell(new jxl.write.Number(colOffset+17,nocnt,averageOptimizationTime/ ITERATIONS));
				//wb.write();
			}
			colOffset+=4;

		}
		wb.write();
		wb.close();
		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("d:\\operators.txt"), sb.toString());
		//read an xml node using xpath
		//Node node = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);



		//System.out.println(baseURI);
		// /rdf:RDF/service:Service/service:describedBy/process:AtomicProcess/process:hasInput/process:Input/rdfs:label/
	}


	private static List<String[]> Callplanner(String problemAddress, String domainAddress) throws Exception {
		
		List<String[]> rawplan = new ArrayList<String[]>();

		Process p = Runtime.getRuntime().exec(homeAddress+ "FF-v2.32/ff -o "+domainAddress+" -f "+problemAddress);
		p.waitFor();

		BufferedReader reader = 
				new BufferedReader(new InputStreamReader(p.getInputStream()));

		String line = "";			
		boolean inPlan =false;
		boolean planRead = false;
		while ((line = reader.readLine())!= null) {
			System.out.println(line);
			if(line.equals("**PLAN**")){
				inPlan=true;
				continue;
			}
			if(line.equals("**PLANEND**")){
				inPlan=false;
				planRead =true;
			}
			if(inPlan){
				rawplan.add(line.split(" "));
			}

		}

		if(!planRead )
		{
			System.out.println("Plan not found!");
			//throw new Exception("Plan not found!");
			return null;
		}
		return rawplan;
	}


	/***
	 * Performs evaluation on efficiency of method based on the number of features (NEW VERSION)
	 * @throws Exception
	 */
	public static void EvaluateMethodBasedOnNumberOfFeatures() throws Exception{
		//    	UtilityClass.createFolder("D:\\temp");
		//    	File dir = new File("D:\\temp");
		//    	for(File fl: dir.listFiles() )
		//    	{
		//    		fl.delete();
		//    	}
		//	String curPost = "";
		WritableWorkbook wb = Workbook.createWorkbook(new File(homeAddress+"evaluationfmsFinal.xls"));
		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
		int offset =0;
		int colOffset=0;
//		boolean ftime =true;

		Integer[] nos ={ 50,100,150,200,250,300,350,400,450,500};
		Integer[] reqNoList={2,8,16};

		//    	Integer[] nos ={ 10, 20, 40, 60, 80,100};
		//    	Integer[] reqNoList={2,4,6};
		//    	Integer[] nos ={60};
		//    	Integer[] reqNoList={6};
		//Integer[] nos ={200};


		for(int reqcnt=0; reqcnt<reqNoList.length; reqcnt++){
			offset =0;


			curSheet.addCell(new Label(colOffset+1,0,"PDDL Generation"));
			curSheet.addCell(new Label(colOffset+2,0,"Planning"));
			curSheet.addCell(new Label(colOffset+3,0,"Optimization"));


			//DomainModels dm = DomainModels.ReadModels("D:\\tt\\owlintact2");
			DomainModels dm = DomainModels.ReadModels(homeAddress+ "BPLECONS/ds/services/r6/s1000");
			dm.getContextModel().createSimpleContext();

			for(int nocnt=0; nocnt <nos.length; nocnt++){
				double averageSerializationTime =0;
				double averagePlanningTime =0;
				double averageOptimizationTime =0;
				System.out.println("****Evaluation for no of feature "+nos[nocnt]+ "and no of reqs "+reqNoList[reqcnt]);
				for(int itcnt =0; itcnt <ITERATIONS_FM ;itcnt++ ){
					System.out.println("Iteration: "+itcnt );

					int row =offset+ itcnt+1;
					int col =colOffset;
					curSheet.addCell(new Label(col++,row,"Iteration: "+itcnt +" no of services: "+ nos[nocnt]));



					//Add Vars

					File folder = new File(homeAddress+ "BPLECONS/ds/featureModelFF1000/r"+reqNoList[reqcnt]+"/"+nos[nocnt]);
					File[] fl =folder.listFiles();
					long curTime = System.currentTimeMillis();
					ProblemDomain pd = new ProblemDomain(dm.getContextModel(),dm.getServiceCollection());
					String problemDomainpddl =  pd.PDDL3Serialize("");
					long serializationDuration = System.currentTimeMillis()-curTime;
					File a = fl[UtilityClass.randInt(0, fl.length-1)];
					System.out.println("File name: "+a.getName());
					Problem pr = Problem.readFromFile(a, dm.getContextModel());
					String problempddl = pr.PDDL3Serialize("");
					String problemAddress = homeAddress+"temp/pt.pddl";
					String domainAddress = homeAddress+"temp/pta.pddl";


					UtilityClass.writeFile(new File(problemAddress),problempddl);
					UtilityClass.writeFile(new File(domainAddress),problemDomainpddl);



					averageSerializationTime+=serializationDuration;
					curTime = System.currentTimeMillis();
					//System.out.println(problem);
					//System.out.println(problemDomain);

					//GraphPlan gp = new GraphPlan();

					//List<String[]> rawplan =gp.init2(problemDomain ,problem, 50, ftime,false);

					List<String[]> rawplan = Callplanner(problemAddress, domainAddress);

					long planningDuration = System.currentTimeMillis()-curTime;

					averagePlanningTime+=planningDuration;


					//		    	
					//		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
					//		    	{
					//		
					//					String[] s = rawplan.get(cnt);
					//					for(String str : s)
					//						System.out.print(str + " ");
					//					System.out.println();
					//		    	}
					List<ServiceCall> plan  = new ArrayList<ServiceCall>();
					for(String[] sl :rawplan)
					{
						Service calledService = null;
						for(Service s : dm.getServiceCollection().getServices())
							if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
							{
								calledService = s;
								break;
							}
						if(calledService==null)
							throw new Exception("Service does not exists");
						int cnt =1;
						HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
						HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
						HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();

						for(URI uri: calledService.getInputList()){
							Instance par = null;

							for(Instance n: dm.getContextModel().getVars())
								if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
									par = n;
									break; 
								}
							if(par==null)
								throw new Exception("Variable does not exists");
							paramsin.put(uri, par);
							cnt++;
						}
						for(URI uri: calledService.getOutputList()){
							Instance par = null;

							for(Instance n: dm.getContextModel().getVars())
								if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
									par = n;
									break;
								}
							if(par==null)
								throw new Exception("Variable does not exists");
							paramsout.put(uri, par);
							cnt++;
						}
						for(URI uri: calledService.getVarList()){
							Instance par = null;

							for(Instance n: dm.getContextModel().getVars())
								if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
									par = n;
									break;
								}
							if(par==null)
								throw new Exception("Variable does not exists");
							paramsvar.put(uri, par);
							cnt++;
						}


						plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
					}



					//			    	Condition pre =pr.getInitialState();
					//			    	Condition eff = pr.getGoalState();
					//			    	
					//			    	//to add condition and effect
					//			    	
					//			    	curTime =System.currentTimeMillis();
					//			    	
					//
					//			    	List<GraphNode> optimizedGraph = GraphNode.convertToGraph(plan, pre, eff);
					//			    	GraphNode.optimizeNew(optimizedGraph);

					long optimizationDuration =System.currentTimeMillis()-curTime;
					averageOptimizationTime +=optimizationDuration;

					//			    	int folderno = plan.size()/10;
					//			    	UtilityClass.createFolder(homeAddress+ "BPLECONS/ds/featureModelSize/s"+folderno);
					//			    	UtilityClass.writeFile(new File(homeAddress+ "BPLECONS/ds/featureModelSize/s"+folderno+"/"+ a.getName()), pr.Serialize());


					curSheet.addCell(new jxl.write.Number(col++,row,serializationDuration));
					curSheet.addCell(new jxl.write.Number(col++,row,planningDuration));
					curSheet.addCell(new jxl.write.Number(col++,row,optimizationDuration));

				}
				offset+= ITERATIONS_FM;
				curSheet.addCell(new jxl.write.Number(colOffset+14,nocnt,nos[nocnt]));
				curSheet.addCell(new jxl.write.Number(colOffset+15,nocnt,averageSerializationTime/ ITERATIONS_FM));
				curSheet.addCell(new jxl.write.Number(colOffset+16,nocnt,averagePlanningTime/ ITERATIONS_FM));
				curSheet.addCell(new jxl.write.Number(colOffset+17,nocnt,averageOptimizationTime/ ITERATIONS_FM));
				//wb.write();
			}
			colOffset+=4;

		}
		wb.write();
		wb.close();
		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("d:\\operators.txt"), sb.toString());
		//read an xml node using xpath
		//Node node = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);



		//System.out.println(baseURI);
		// /rdf:RDF/service:Service/service:describedBy/process:AtomicProcess/process:hasInput/process:Input/rdfs:label/
	}

	/**
	 * Evaluates optimization based on time and and execution time
	 * @throws Exception
	 */
	public static void EvaluateMethodBasedonlinengthOfWorkflow() throws Exception{

		WritableWorkbook wb = Workbook.createWorkbook(new File(homeAddress+ "evaluationlinennew.xls"));
		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
		int offset =0;
		int colOffset=0;
//		boolean ftime =true;
		//    	
		//    	Integer[] nos ={ 10, 20, 40, 60, 80,100};
		//    	Integer[] reqNoList={2,4,6};
		//    	Integer[] nos ={60};
		//    	Integer[] reqNoList={6};
		//Integer[] nos ={200};



		offset =0;


		curSheet.addCell(new Label(colOffset+1,0,"PDDL Generation"));
		curSheet.addCell(new Label(colOffset+2,0,"Planning"));
		curSheet.addCell(new Label(colOffset+3,0,"Optimization"));
		curSheet.addCell(new Label(colOffset+4,0,"Before"));
		curSheet.addCell(new Label(colOffset+5,0,"After"));

		//DomainModels dm = DomainModels.ReadModels("D:/tt/owlintact2");
		DomainModels dm = DomainModels.ReadModels(homeAddress+ "BPLECONS/ds/services/r6/s1000");
		dm.getContextModel().createSimpleContext();


		
		
		ServiceNonfunctionalAnnotationMap  am = new ServiceNonfunctionalAnnotationMap(dm.getServiceCollection().getServices());


		am.generateNonfunctionRandomly(ExecutionTimeType.getInstance(), 200, 50, 50, 30);


		int fileCnt = 0;
		for(int nocnt=0; nocnt <15; nocnt++){
			fileCnt+=10;
			double averageSerializationTime =0;
			double averagePlanningTime =0;
			double averageOptimizationTime =0;
			double averageBTime =0;
			double averageATime =0;
			System.out.println("****Evaluation for no  "+fileCnt);
			for(int itcnt =0; itcnt <ITERATIONS ;itcnt++ ){
				System.out.println("Iteration: "+itcnt );

				int row =offset+ itcnt+1;
				int col =colOffset;
				curSheet.addCell(new Label(col++,row,"Iteration: "+itcnt +" no : "+fileCnt));



				//Add Vars
				DecimalFormat df= new DecimalFormat("000");
				File folder = new File(homeAddress+ "BPLECONS/ds/featureModelSZ/l"+df.format(fileCnt) );
				if(!folder.exists())
					continue;
				File[] fl =folder.listFiles();
				long curTime = System.currentTimeMillis();
				ProblemDomain pd = new ProblemDomain(dm.getContextModel(),dm.getServiceCollection());
				String problemDomainpddl =  pd.PDDL3Serialize("");
				long serializationDuration = System.currentTimeMillis()-curTime;
				File a = fl[UtilityClass.randInt(0, fl.length-1)];
				System.out.println("File name: "+a.getName());
				Problem pr = Problem.readFromFile(a, dm.getContextModel());
				String problempddl = pr.PDDL3Serialize("");

				String problemAddress = homeAddress+"temp/pt.pddl";
				String domainAddress = homeAddress+"temp/pta.pddl";


				UtilityClass.writeFile(new File(problemAddress),problempddl);
				UtilityClass.writeFile(new File(domainAddress),problemDomainpddl);



				averageSerializationTime+=serializationDuration;
				curTime = System.currentTimeMillis();
				//System.out.println(problem);
				//System.out.println(problemDomain);

				//GraphPlan gp = new GraphPlan();

				//List<String[]> rawplan =gp.init2(problemDomain ,problem, 50, ftime,false);

				List<String[]> rawplan = Callplanner(problemAddress, domainAddress);

				long planningDuration = System.currentTimeMillis()-curTime;

				averagePlanningTime+=planningDuration;
				//		    	
				//		    	for(int cnt =0; cnt< rawplan.size(); cnt++)
				//		    	{
				//		
				//					String[] s = rawplan.get(cnt);
				//					for(String str : s)
				//						System.out.print(str + " ");
				//					System.out.println();
				//		    	}
				List<ServiceCall> plan  = new ArrayList<ServiceCall>();
				for(String[] sl :rawplan)
				{
					Service calledService = null;
					for(Service s : dm.getServiceCollection().getServices())
						if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
						{
							calledService = s;
							break;
						}
					if(calledService==null)
						throw new Exception("Service does not exists");
					int cnt =1;
					HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
					HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
					HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();

					for(URI uri: calledService.getInputList()){
						Instance par = null;

						for(Instance n: dm.getContextModel().getVars())
							if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
								par = n;
								break; 
							}
						if(par==null)
							throw new Exception("Variable does not exists");
						paramsin.put(uri, par);
						cnt++;
					}
					for(URI uri: calledService.getOutputList()){
						Instance par = null;

						for(Instance n: dm.getContextModel().getVars())
							if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
								par = n;
								break;
							}
						if(par==null)
							throw new Exception("Variable does not exists");
						paramsout.put(uri, par);
						cnt++;
					}
					for(URI uri: calledService.getVarList()){
						Instance par = null;

						for(Instance n: dm.getContextModel().getVars())
							if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
								par = n;
								break;
							}
						if(par==null)
							throw new Exception("Variable does not exists");
						paramsvar.put(uri, par);
						cnt++;
					}


					plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
				}

				Condition pre =pr.getInitialState();
				Condition eff = pr.getGoalState();

				//to add condition and effect





				List<OperationNode> optimizedGraph = OperationNode.convertToGraph(plan, pre, eff);
				//			    	if(!GraphNode.safe2(optimizedGraph)){
				//			    		//System.out.println("**************NOT SAFE");
				//			    		throw new Exception("NOT SAFE!");
				//			    	}
				//			    	else{


				double beforeTime = ExecutionTimeType.findExecutionTime(am.getAnnotationMap(), optimizedGraph);
				averageBTime+=beforeTime;

				System.out.println("Starting Optimization");
				curTime =System.currentTimeMillis();

				//System.out.println("Staring Optimization");
				OperationNode.optimizeNew2(optimizedGraph);



				//			    	UtilityClass.writeFile(new File("/home/mbashari/graphs/out"+filecnt+".gv"), GraphNode.serializedToGV(optimizedGraph));
				//					filecnt++;
				//}

				long optimizationDuration =System.currentTimeMillis()-curTime;
				averageOptimizationTime +=optimizationDuration;

				System.out.println("Optimization Done");

				double afterTime = ExecutionTimeType.findExecutionTime(am.getAnnotationMap(), optimizedGraph);
				averageATime+=afterTime;


				curSheet.addCell(new jxl.write.Number(col++,row,serializationDuration));
				curSheet.addCell(new jxl.write.Number(col++,row,planningDuration));
				curSheet.addCell(new jxl.write.Number(col++,row,optimizationDuration));
				curSheet.addCell(new jxl.write.Number(col++,row,beforeTime));
				curSheet.addCell(new jxl.write.Number(col++,row,afterTime));


			}
			offset+= ITERATIONS;
			curSheet.addCell(new jxl.write.Number(colOffset+14,nocnt,fileCnt));
			curSheet.addCell(new jxl.write.Number(colOffset+15,nocnt,averageSerializationTime/ ITERATIONS));
			curSheet.addCell(new jxl.write.Number(colOffset+16,nocnt,averagePlanningTime/ ITERATIONS));
			curSheet.addCell(new jxl.write.Number(colOffset+17,nocnt,averageOptimizationTime/ ITERATIONS));
			curSheet.addCell(new jxl.write.Number(colOffset+18,nocnt,averageBTime/ ITERATIONS));
			curSheet.addCell(new jxl.write.Number(colOffset+19,nocnt,averageATime/ ITERATIONS));


			//wb.write();
		}
		colOffset+=4;


		wb.write();
		wb.close();
		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("d:\\operators.txt"), sb.toString());
		//read an xml node using xpath
		//Node node = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);



		//System.out.println(baseURI);
		// /rdf:RDF/service:Service/service:describedBy/process:AtomicProcess/process:hasInput/process:Input/rdfs:label/
	}

	/**
	 * Evaluates bpel generation based on size and number of links
	 * @throws Exception
	 */
	public static void EvaluateMethodForBPELGeneration() throws Exception{

		WritableWorkbook wb = Workbook.createWorkbook(new File(homeAddress+ "evaluationbpel.xls"));
		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
		int offset =0;
		int colOffset=0;
//		boolean ftime =true;
		//     	
		//     	Integer[] nos ={ 10, 20, 40, 60, 80,100};
		//     	Integer[] reqNoList={2,4,6};
		//     	Integer[] nos ={60};
		//     	Integer[] reqNoList={6};
		//Integer[] nos ={200};



		offset =0;


		curSheet.addCell(new Label(colOffset+1,0,"PDDL Generation"));
		curSheet.addCell(new Label(colOffset+2,0,"Planning"));
		curSheet.addCell(new Label(colOffset+3,0,"Optimization"));
		curSheet.addCell(new Label(colOffset+4,0,"Flow"));
		curSheet.addCell(new Label(colOffset+5,0,"Sequence"));
		curSheet.addCell(new Label(colOffset+6,0,"Link"));
		curSheet.addCell(new Label(colOffset+7,0,"Flow"));
		curSheet.addCell(new Label(colOffset+8,0,"Sequence"));
		curSheet.addCell(new Label(colOffset+9,0,"Link"));
		curSheet.addCell(new Label(colOffset+10,0,"Flow"));
		curSheet.addCell(new Label(colOffset+11,0,"Sequence"));
		curSheet.addCell(new Label(colOffset+12,0,"Link"));

		//DomainModels dm = DomainModels.ReadModels("D:/tt/owlintact2");
		DomainModels dm = DomainModels.ReadModels(homeAddress+ "BPLECONS/ds/services/r6/s1000");
		dm.getContextModel().createSimpleContext();

		int fileCnt = 0;
		for(int nocnt=0; nocnt <15; nocnt++){
			fileCnt+=10;
			double averageSerializationTime =0;
			double averagePlanningTime =0;
			double averageOptimizationTime =0;
			double averageFlow1 = 0;
			double averageSequence1 = 0;
			double averageLink1=0;
			double averageFlow2 = 0;
			double averageSequence2 = 0;
			double averageLink2=0;
			double averageFlow3 = 0;
			double averageSequence3 = 0;
			double averageLink3=0;

			System.out.println("****Evaluation for no  "+fileCnt);
					for(int itcnt =0; itcnt <ITERATIONS ;itcnt++ ){
						System.out.println("Iteration: "+itcnt );

						int row =offset+ itcnt+1;
						int col =colOffset;
						curSheet.addCell(new Label(col++,row,"Iteration: "+itcnt +" no : "+fileCnt));
						//Add Vars
						DecimalFormat df= new DecimalFormat("000");
						File folder = new File(homeAddress+ "BPLECONS/ds/featureModelSZ/l"+df.format(fileCnt) );
						if(!folder.exists())
							continue;
						File[] fl =folder.listFiles();
						long curTime = System.currentTimeMillis();
						ProblemDomain pd = new ProblemDomain(dm.getContextModel(),dm.getServiceCollection());
						String problemDomainpddl =  pd.PDDL3Serialize("");
						long serializationDuration = System.currentTimeMillis()-curTime;
						File a = fl[UtilityClass.randInt(0, fl.length-1)];
						System.out.println("File name: "+a.getName());
						Problem pr = Problem.readFromFile(a, dm.getContextModel());
						String problempddl = pr.PDDL3Serialize("");

						String problemAddress = homeAddress+"temp/pt.pddl";
						String domainAddress = homeAddress+"temp/pta.pddl";


						UtilityClass.writeFile(new File(problemAddress),problempddl);
						UtilityClass.writeFile(new File(domainAddress),problemDomainpddl);



						averageSerializationTime+=serializationDuration;
						curTime = System.currentTimeMillis();

						List<String[]> rawplan = Callplanner(problemAddress, domainAddress);

						long planningDuration = System.currentTimeMillis()-curTime;

						averagePlanningTime+=planningDuration;

						List<ServiceCall> plan  = processRawPlan(rawplan, dm, pr);





						Condition pre =pr.getInitialState();
						Condition eff = pr.getGoalState();

						List<OperationNode> optimizedGraph = OperationNode.convertToGraph(plan, pre, eff);	
						System.out.println("Starting Optimization");
						curTime =System.currentTimeMillis();

						OperationNode.optimizeNew2(optimizedGraph);

						long optimizationDuration =System.currentTimeMillis()-curTime;
						averageOptimizationTime +=optimizationDuration;

						System.out.println("Optimization Done");

						System.out.println("Trying Optimization No 1");
						ComponentNode n = OperationNode.BPELAlgorithmNo1(optimizedGraph);


						System.out.println("Trying Optimization No 2");
						FlowComponentNode fcn =FlowComponentNode.convertToFlowWithLink(optimizedGraph);
						fcn.OptimizeNo1();

						System.out.println("Trying Optimization No 3");
						FlowComponentNode fcn1 =FlowComponentNode.convertToFlowWithLink(optimizedGraph);
						fcn1.OptimizeNo2();

						curSheet.addCell(new jxl.write.Number(col++,row,serializationDuration));
						curSheet.addCell(new jxl.write.Number(col++,row,planningDuration));
						curSheet.addCell(new jxl.write.Number(col++,row,optimizationDuration));

						curSheet.addCell(new jxl.write.Number(col++,row,n.GetNoOfFlows()));
						curSheet.addCell(new jxl.write.Number(col++,row,n.GetNoOfSequence()));
						curSheet.addCell(new jxl.write.Number(col++,row,n.GetNoOfLink()));

						curSheet.addCell(new jxl.write.Number(col++,row,fcn.GetNoOfFlows()));
						curSheet.addCell(new jxl.write.Number(col++,row,fcn.GetNoOfSequence()));
						curSheet.addCell(new jxl.write.Number(col++,row,fcn.GetNoOfLink()));

						curSheet.addCell(new jxl.write.Number(col++,row,fcn1.GetNoOfFlows()));
						curSheet.addCell(new jxl.write.Number(col++,row,fcn1.GetNoOfSequence()));
						curSheet.addCell(new jxl.write.Number(col++,row,fcn1.GetNoOfLink()));

						averageFlow1+=n.GetNoOfFlows();
						averageSequence1+=n.GetNoOfSequence();
						averageLink1+=n.GetNoOfLink();

						averageFlow2+=fcn.GetNoOfFlows();
						averageSequence2+=fcn.GetNoOfSequence();
						averageLink2+=fcn.GetNoOfLink();


						averageFlow3+=fcn1.GetNoOfFlows();
						averageSequence3+=fcn1.GetNoOfSequence();
						averageLink3+=fcn1.GetNoOfLink();
					}
					offset+= ITERATIONS;
					curSheet.addCell(new jxl.write.Number(colOffset+14,nocnt,fileCnt));
					curSheet.addCell(new jxl.write.Number(colOffset+15,nocnt,averageSerializationTime/ ITERATIONS));
					curSheet.addCell(new jxl.write.Number(colOffset+16,nocnt,averagePlanningTime/ ITERATIONS));
					curSheet.addCell(new jxl.write.Number(colOffset+17,nocnt,averageOptimizationTime/ ITERATIONS));


					curSheet.addCell(new jxl.write.Number(colOffset+18,nocnt,averageFlow1/ ITERATIONS));
					curSheet.addCell(new jxl.write.Number(colOffset+17,nocnt,averageSequence1/ ITERATIONS));
					curSheet.addCell(new jxl.write.Number(colOffset+19,nocnt,averageLink1/ ITERATIONS));

					curSheet.addCell(new jxl.write.Number(colOffset+20,nocnt,averageFlow2/ ITERATIONS));
					curSheet.addCell(new jxl.write.Number(colOffset+21,nocnt,averageSequence2/ ITERATIONS));
					curSheet.addCell(new jxl.write.Number(colOffset+22,nocnt,averageLink2/ ITERATIONS));

					curSheet.addCell(new jxl.write.Number(colOffset+23,nocnt,averageFlow3/ ITERATIONS));
					curSheet.addCell(new jxl.write.Number(colOffset+24,nocnt,averageSequence3/ ITERATIONS));
					curSheet.addCell(new jxl.write.Number(colOffset+25,nocnt,averageLink3/ ITERATIONS));


					//wb.write();
		}
		colOffset+=4;


		wb.write();
		wb.close();
		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File("d:\\operators.txt"), sb.toString());
		//read an xml node using xpath
		//Node node = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);



		//System.out.println(baseURI);
		// /rdf:RDF/service:Service/service:describedBy/process:AtomicProcess/process:hasInput/process:Input/rdfs:label/
	}

	private static List<ServiceCall> processRawPlan(List<String[]> rawplan, DomainModels dm, Problem pr) throws Exception {
		List<ServiceCall> plan = new ArrayList<ServiceCall>();
		for(String[] sl :rawplan)
		{
			Service calledService = null;
			for(Service s : dm.getServiceCollection().getServices())
				if(s.getName().toLowerCase().equals(sl[0].toLowerCase()))
				{
					calledService = s;
					break;
				}
			if(calledService==null)
				throw new Exception("Service does not exists");
			int cnt =1;
			HashMap<URI,Instance> paramsin = new HashMap<URI, Instance>();
			HashMap<URI,Instance> paramsout = new HashMap<URI, Instance>();
			HashMap<URI,Instance> paramsvar = new HashMap<URI, Instance>();

			for(URI uri: calledService.getInputList()){
				Instance par = null;

				for(Instance n: dm.getContextModel().getVars())
					if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
						par = n;
						break; 
					}
				if(par==null)
					throw new Exception("Variable does not exists");
				paramsin.put(uri, par);
				cnt++;
			}
			for(URI uri: calledService.getOutputList()){
				Instance par = null;

				for(Instance n: dm.getContextModel().getVars())
					if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
						par = n;
						break;
					}
				if(par==null)
					throw new Exception("Variable does not exists");
				paramsout.put(uri, par);
				cnt++;
			}
			for(URI uri: calledService.getVarList()){
				Instance par = null;

				for(Instance n: dm.getContextModel().getVars())
					if(n.getName().toLowerCase().equals(sl[cnt].toLowerCase())){
						par = n;
						break;
					}
				if(par==null)
					throw new Exception("Variable does not exists");
				paramsvar.put(uri, par);
				cnt++;
			}


			plan.add(new ServiceCall(calledService,paramsin, paramsout,paramsvar));
		}


		return plan;
	}
	/**
	 * Converts service models to a textual format for debugging
	 * @throws Exception
	 */
	public static void SerializeServiceModelToTextualNotation() throws Exception{
		DomainModels dm = DomainModels.ReadModels("/home/mbashari/BPLECONS/ds/services/r6/s1000");
		//DomainModels dm = DomainModels.ReadModels("D:\\Development\\BPLECONS\\ds\\services\\r6\\s200");
		dm.getContextModel().createSimpleContext();

		Map<String, List<String>> poseff= new HashMap<String, List<String>>();
		Map<String, List<String>> negeff= new HashMap<String, List<String>>();

		Map<String, List<String>> preposeff= new HashMap<String, List<String>>();
		Map<String, List<String>> prenegeff= new HashMap<String, List<String>>();
		for(Service s: dm.getServiceCollection().getServices()){
			for(StateFactInstanceS sfis: s.getPostcondition().getConditions()){
				Map<String, List<String>> map= poseff;
				Map<String, List<String>> othermap= negeff;
				if(sfis.isNot()){
					map= negeff;
					othermap= poseff;
				}
				if(!map.containsKey(sfis.getStateFactInstance().getType().getTypeName()))
				{
					map.put(sfis.getStateFactInstance().getType().getTypeName(),new ArrayList<String>());
					othermap.put(sfis.getStateFactInstance().getType().getTypeName(),new ArrayList<String>());
					preposeff.put(sfis.getStateFactInstance().getType().getTypeName(),new ArrayList<String>());
					prenegeff.put(sfis.getStateFactInstance().getType().getTypeName(),new ArrayList<String>());
				}
				map.get(sfis.getStateFactInstance().getType().getTypeName()).add(s.getName());
			}
		}
		for(Service s: dm.getServiceCollection().getServices()){
			for(StateFactInstanceS sfis: s.getPrecondition().getConditions()){
				Map<String, List<String>> map= preposeff;
				Map<String, List<String>> othermap= prenegeff;
				if(sfis.isNot()){
					map= prenegeff;
					othermap= preposeff;
				}
				if(!map.containsKey(sfis.getStateFactInstance().getType().getTypeName()))
				{
					map.put(sfis.getStateFactInstance().getType().getTypeName(),new ArrayList<String>());
					othermap.put(sfis.getStateFactInstance().getType().getTypeName(),new ArrayList<String>());
					poseff.put(sfis.getStateFactInstance().getType().getTypeName(),new ArrayList<String>());
					negeff.put(sfis.getStateFactInstance().getType().getTypeName(),new ArrayList<String>());
				}
				map.get(sfis.getStateFactInstance().getType().getTypeName()).add(s.getName());
			}
		}
		StringBuilder sb = new StringBuilder();
		for(String str: poseff.keySet()){
			sb.append(str);
			sb.append(System.lineSeparator());
			for(String str1: poseff.get(str)){
				sb.append("\te+"+str1);
				sb.append(System.lineSeparator());
			}
			for(String str1: negeff.get(str)){
				sb.append("\te-"+str1);
				sb.append(System.lineSeparator());
			}
			for(String str1: preposeff.get(str)){
				sb.append("\tp+"+str1);
				sb.append(System.lineSeparator());
			}
			for(String str1: prenegeff.get(str)){
				sb.append("\tp-"+str1);
				sb.append(System.lineSeparator());
			}

		}

		UtilityClass.writeFile(new File("/home/mbashari/svs.txt"), sb.toString());
	}
	/**
	 * Converts service models to a textual format for debugging
	 * @throws Exception
	 */
	public static void SerializeServiceModelToTextualNotation2() throws Exception{
		DomainModels dm = DomainModels.ReadModels("/home/mbashari/BPLECONS/ds/services/r6/s1000");
		dm.getContextModel().createSimpleContext();
		StringBuilder sb = new StringBuilder();
		for(Service s: dm.getServiceCollection().getServices()){
			sb.append(s.getName());
			sb.append(System.lineSeparator());
			for(StateFactInstanceS str1: s.getPostcondition().getConditions()){
				sb.append("\te");
				if(str1.isNot())
					sb.append("-");
				else
					sb.append("+");

				sb.append(str1.getStateFactInstance().getType().getTypeName());
				sb.append(System.lineSeparator());
			}
			for(StateFactInstanceS str1: s.getPrecondition().getConditions()){
				sb.append("\tp");
				if(str1.isNot())
					sb.append("-");
				else
					sb.append("+");

				sb.append(str1.getStateFactInstance().getType().getTypeName());
				sb.append(System.lineSeparator());
			}
		}

		UtilityClass.writeFile(new File("/home/mbashari/svs2.txt"), sb.toString());

	}

	public static void populateWorkflowLenFolder() throws Exception{

		int noOfServices = 1000;
		int filecnt=6400;
		int iteration = 40;

		DomainModels dm = DomainModels
				.ReadModels(homeAddress+ "BPLECONS/ds/services/r6/s"+noOfServices);
		dm.getContextModel().createSimpleContext();
		for(int size=300; size<=320; size+=20){

			for(int itcnt=0;itcnt<iteration ;itcnt++){

				//System.out.println("Iteration: "+itcnt +"Trying to find a fmc for reqno: "+reqNoListFeature[ reqcnt]+" and feature model configuration size: "+numOfFeatures[fcnt]);

				System.out.println("**Size is"+size+" iteration "+itcnt);
				ProblemDomain pd = new ProblemDomain(dm.getContextModel(),dm.getServiceCollection());
				String problemDomainpddl =  pd.PDDL3Serialize("");



				Problem pr  = DoAWalkNew(dm, size, 400);
				String problempddl = pr.PDDL3Serialize("");

				String problemAddress = homeAddress+"temp/pt.pddl";
				String domainAddress = homeAddress+"temp/pta.pddl";


				UtilityClass.writeFile(new File(problemAddress),problempddl);
				UtilityClass.writeFile(new File(domainAddress),problemDomainpddl);




				List<String[]> rawplan = Callplanner(problemAddress, domainAddress);

				if(rawplan==null)
					continue;

				int folderno = rawplan.size()/5;
				UtilityClass.createFolder(homeAddress+"BPLECONS/ds/featureModelSZ/s"+folderno);
				UtilityClass.writeFile(new File(homeAddress+"BPLECONS/ds/featureModelSZ/s"+folderno+"/"+ filecnt+".txt"), pr.Serialize());
				System.out.println("BPLECONS/ds/featureModelSZ/s"+folderno+"/"+ filecnt+".txt");
				filecnt++;

			}
		}

	}
	private static Problem DoAWalkNew(DomainModels dm, int size,int iterations ) {

		

		List<Service> alreadySelectedService = new ArrayList<Service>();
		List<StateFactInstanceS> isl = new ArrayList<StateFactInstanceS>();
		String cond1 = "p7f9d1560";
		String cond2 = "pfc4299ba";

//		String postCond1 = dm
//				.getContextModel()
//				.getFactTypes()
//				.getStateFactTypeList()
//				.get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0,
//						dm.getContextModel().getFactTypes()
//						.getStateFactTypeList().size() - 1))
//				.getTypeName();
//		String postCond2 = dm
//				.getContextModel()
//				.getFactTypes()
//				.getStateFactTypeList()
//				.get(edu.ls3.magus.eval.generators.owls.UtilityClass.randInt(0,
//						dm.getContextModel().getFactTypes()
//						.getStateFactTypeList().size() - 1))
//				.getTypeName();
		// String postCond1 = "p3e2f7d26";
		// String postCond2 = "pc550de6b";
		// System.out.println("*****"+postCond1);
		// System.out.println("*****"+postCond2);

		Instance[] params = {
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
								.getInstaceFactTypeByName(cond1)[0]
										.getParams()[0].getTypeName())[0],
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
								.getInstaceFactTypeByName(cond1)[0]
										.getParams()[1].getTypeName())[0] };
		isl.add(new StateFactInstanceS(new StateFactInstance(dm
				.getContextModel().getInstaceFactTypeByName(cond1)[0], params),
				false));
		Instance[] params2 = {
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
								.getInstaceFactTypeByName(cond2)[0]
										.getParams()[0].getTypeName())[0],
				dm.getContextModel().getInstanceByName(
						"v"
								+ dm.getContextModel()
								.getInstaceFactTypeByName(cond2)[0]
										.getParams()[1].getTypeName())[0] };
		isl.add(new StateFactInstanceS(
				new StateFactInstance(dm.getContextModel()
						.getInstaceFactTypeByName(cond2)[0], params2), false));


		Condition curCondition = new Condition(isl);
		Condition preCondition = new Condition(isl);

//		List<Condition> preConditionList =new ArrayList<Condition>();
//		List<Condition> effConditionList =new ArrayList<Condition>();
//		List<Integer> preIterationList = new ArrayList<Integer>();
//		List<Integer> effIterationList = new ArrayList<Integer>();
		int itcnt=0;
		boolean done =false;
//		boolean startCounting =false;

		while(itcnt<iterations){

			//(ef==1)||
			if((itcnt==size-1)||done){


				Problem pr= new Problem(dm.getContextModel(), preCondition,curCondition);

				return pr;
			}


			Map<Service, Map<URI, Instance>> vs = dm
					.getServiceCollection().getExecutableServices(curCondition,dm.getContextModel());

			for(Service sv: alreadySelectedService)
				if(vs.containsKey(sv))
					vs.remove(vs);
			if(vs.size()==0){
				done =true;
				continue;
			}
			Service[] ss = vs.keySet().toArray(new Service[0]);
			Service selectedService = ss[UtilityClass.randInt(0,
					ss.length - 1)];
			alreadySelectedService.add(selectedService);
			curCondition = selectedService.getContextAfterExc(curCondition,
					vs.get(selectedService));

			itcnt++;

		}

		Problem pr= new Problem(dm.getContextModel(), preCondition,curCondition);

		return pr;
	}

	public static void bpeltest1() throws Exception{
		DomainModels dm = DomainModels.ReadModels(homeAddress+ "BPLECONS/tt/owlintact2");
		dm.getContextModel().createSimpleContext();
		List<OperationNode> graph = OperationNode.readFromFile(new File(homeAddress+ "wf1.txt"),dm.getContextModel(),dm.getServiceCollection());
		ComponentNode n = OperationNode.BPELAlgorithmNo1(graph);
		String s = n.serializeToGV();
		edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "testblock1.gv"),s);
	}

	public static void EvaluationBPELGenerationEfficiency() throws Exception{
		DomainModels dm = DomainModels.ReadModels(homeAddress+ "BPLECONS/ds/services/r6/s1000");
		dm.getContextModel().createSimpleContext();
		WritableWorkbook wb = Workbook.createWorkbook(new File(homeAddress+ "evaluationbpelnew.xls"));
		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
		
		int offset =0;
		int colOffset=0;



		ServiceNonfunctionalAnnotationMap am = new ServiceNonfunctionalAnnotationMap(dm.getServiceCollection().getServices());
		

		am.generateNonfunctionRandomly(ExecutionTimeType.getInstance(), 200, 50, 50, 30);
	
		curSheet.addCell(new Label(colOffset+1,0,"Flow"));
		curSheet.addCell(new Label(colOffset+2,0,"Sequence"));
		curSheet.addCell(new Label(colOffset+3,0,"Link"));
		curSheet.addCell(new Label(colOffset+4,0,"Flow"));
		curSheet.addCell(new Label(colOffset+5,0,"Sequence"));
		curSheet.addCell(new Label(colOffset+6,0,"Link"));
		curSheet.addCell(new Label(colOffset+7,0,"Flow"));
		curSheet.addCell(new Label(colOffset+8,0,"Sequence"));
		curSheet.addCell(new Label(colOffset+9,0,"Link"));
		curSheet.addCell(new Label(colOffset+10,0,"Execution Time 1"));
		curSheet.addCell(new Label(colOffset+11,0,"Execution Time 2"));
		curSheet.addCell(new Label(colOffset+12,0,"Execution Time 3"));
		curSheet.addCell(new Label(colOffset+13,0,"Generation Time 1"));
		curSheet.addCell(new Label(colOffset+14,0,"Generation Time 2"));
		curSheet.addCell(new Label(colOffset+15,0,"Generation Time 3"));

		for(int workflowsizeCntr=1; workflowsizeCntr<11; workflowsizeCntr++){

			int workflowsize = workflowsizeCntr*10;
			double averageFlow1 = 0;
			double averageSequence1 = 0;
			double averageLink1=0;
			double averageFlow2 = 0;
			double averageSequence2 = 0;
			double averageLink2=0;
			double averageFlow3 = 0;
			double averageSequence3 = 0;
			double averageLink3=0;
			double averageExecutionTime1 = 0;
			double averageExecutionTime2=0;
			double averageExecutionTime3=0;
			double averageGenerationTime1 = 0;
			double averageGenerationTime2=0;
			double averageGenerationTime3=0;

			System.out.println("****Evaluation for no  "+workflowsize);
			for(int itcnt =0; itcnt <ITERATIONS ;itcnt++ ){
				System.out.println("Iteration: "+itcnt );

				int row =offset+ itcnt+1;
				int col =colOffset;
				curSheet.addCell(new Label(col++,row,"Iteration: "+itcnt +" no : "+workflowsize));

				List<Service> usedServices = new ArrayList<Service>();

				Node generatedBPEL = FlowComponentNode.createARandomFlow(workflowsize, usedServices, dm.getServiceCollection());
				if(generatedBPEL instanceof ComponentNode){
					System.out.println("Orginal generated bpel");
					System.out.println("Flows "+((ComponentNode) generatedBPEL).GetNoOfFlows()+" Sequence "+ ((ComponentNode) generatedBPEL).GetNoOfSequence() + " Link "+ ((ComponentNode) generatedBPEL).GetNoOfLink());
					String s = ((ComponentNode) generatedBPEL).serializeToGV();
					edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "testblockgen.gv"),s);
					List<OperationNode> wf =((ComponentNode) generatedBPEL).convertToWorkflow2();
					String graphGV = OperationNode.serializedToGV(wf);

					edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "testgen.gv"),graphGV);

					System.out.println("Trying Optimization No 1");
					
					long curTime = System.currentTimeMillis();
					
					ComponentNode n = OperationNode.BPELAlgorithmNo1(wf);
					long generationTime1 = System.currentTimeMillis()-curTime;
					
					 s = ((ComponentNode) n).serializeToGV();
					edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "testblockgen1.gv"),s);
					
					List<OperationNode> wf1 = n.convertToWorkflow();
					
					
					 graphGV = OperationNode.serializedToGV(wf1);

					edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "testgena.gv"),graphGV);
					
					double executionTime1 = ExecutionTimeType.findExecutionTime(am.getAnnotationMap(), wf1);
					

					System.out.println("Flows "+n.GetNoOfFlows()+" Sequence "+ n.GetNoOfSequence() + " Link "+ n.GetNoOfLink());
//
					System.out.println("Trying Optimization No 2");
					FlowComponentNode fcn =FlowComponentNode.convertToFlowWithLink(wf);
					
					curTime = System.currentTimeMillis();
					fcn.OptimizeNo1();
					long generationTime2 = System.currentTimeMillis()-curTime;
					
					List<OperationNode> wf2 = n.convertToWorkflow();
					double executionTime2 = ExecutionTimeType.findExecutionTime(am.getAnnotationMap(), wf2);

					System.out.println("Flows "+fcn.GetNoOfFlows()+" Sequence "+ fcn.GetNoOfSequence() + " Link "+ fcn.GetNoOfLink());

					System.out.println("Trying Optimization No 3");
					FlowComponentNode fcn1 =FlowComponentNode.convertToFlowWithLink(wf);
					
					curTime = System.currentTimeMillis();
					fcn1.OptimizeNo2();
					long generationTime3 = System.currentTimeMillis()-curTime;
					
					
					List<OperationNode> wf3 = n.convertToWorkflow();
					double executionTime3 = ExecutionTimeType.findExecutionTime(am.getAnnotationMap(), wf3);

					System.out.println("Flows "+fcn1.GetNoOfFlows()+" Sequence "+ fcn1.GetNoOfSequence() + " Link "+ fcn1.GetNoOfLink());
//					

					curSheet.addCell(new jxl.write.Number(col++,row,n.GetNoOfFlows()));
					curSheet.addCell(new jxl.write.Number(col++,row,n.GetNoOfSequence()));
					curSheet.addCell(new jxl.write.Number(col++,row,n.GetNoOfLink()));

					curSheet.addCell(new jxl.write.Number(col++,row,fcn.GetNoOfFlows()));
					curSheet.addCell(new jxl.write.Number(col++,row,fcn.GetNoOfSequence()));
					curSheet.addCell(new jxl.write.Number(col++,row,fcn.GetNoOfLink()));

					curSheet.addCell(new jxl.write.Number(col++,row,fcn1.GetNoOfFlows()));
					curSheet.addCell(new jxl.write.Number(col++,row,fcn1.GetNoOfSequence()));
					curSheet.addCell(new jxl.write.Number(col++,row,fcn1.GetNoOfLink()));
					
					curSheet.addCell(new jxl.write.Number(col++,row,executionTime1));
					curSheet.addCell(new jxl.write.Number(col++,row,executionTime2));
					curSheet.addCell(new jxl.write.Number(col++,row,executionTime3));
					curSheet.addCell(new jxl.write.Number(col++,row,generationTime1));
					curSheet.addCell(new jxl.write.Number(col++,row,generationTime2));
					curSheet.addCell(new jxl.write.Number(col++,row,generationTime3));

					averageFlow1+=n.GetNoOfFlows();
					averageSequence1+=n.GetNoOfSequence();
					averageLink1+=n.GetNoOfLink();

					averageFlow2+=fcn.GetNoOfFlows();
					averageSequence2+=fcn.GetNoOfSequence();
					averageLink2+=fcn.GetNoOfLink();


					averageFlow3+=fcn1.GetNoOfFlows();
					averageSequence3+=fcn1.GetNoOfSequence();
					averageLink3+=fcn1.GetNoOfLink();
					
					averageExecutionTime1+= executionTime1;
					averageExecutionTime2+= executionTime2;
					averageExecutionTime3+= executionTime3;
					
					averageGenerationTime1+= generationTime1;
					averageGenerationTime2+= generationTime2;
					averageGenerationTime3+= generationTime3;
					
				}
				else{
					System.out.println("Not a component node");
				}
			}
			offset+= ITERATIONS;
			//curSheet.addCell(new jxl.write.Number(colOffset+14,workflowsizeCntr*4+1,workflowsize));
			curSheet.addCell(new jxl.write.Number(colOffset+17,workflowsizeCntr*4+2,workflowsize));
			//curSheet.addCell(new jxl.write.Number(colOffset+14,workflowsizeCntr*4+3,workflowsize));


			curSheet.addCell(new jxl.write.Number(colOffset+18,workflowsizeCntr*4+1,averageFlow1/ ITERATIONS));
			curSheet.addCell(new jxl.write.Number(colOffset+19,workflowsizeCntr*4+1,averageSequence1/ ITERATIONS));
			curSheet.addCell(new jxl.write.Number(colOffset+20,workflowsizeCntr*4+1,averageLink1/ ITERATIONS));

			curSheet.addCell(new jxl.write.Number(colOffset+21,workflowsizeCntr*4+2,averageFlow2/ ITERATIONS));
			curSheet.addCell(new jxl.write.Number(colOffset+22,workflowsizeCntr*4+2,averageSequence2/ ITERATIONS));
			curSheet.addCell(new jxl.write.Number(colOffset+23,workflowsizeCntr*4+2,averageLink2/ ITERATIONS));

			curSheet.addCell(new jxl.write.Number(colOffset+24,workflowsizeCntr*4+3,averageFlow3/ ITERATIONS));
			curSheet.addCell(new jxl.write.Number(colOffset+25,workflowsizeCntr*4+3,averageSequence3/ ITERATIONS));
			curSheet.addCell(new jxl.write.Number(colOffset+26,workflowsizeCntr*4+3,averageLink3/ ITERATIONS));
			
			
			curSheet.addCell(new jxl.write.Number(colOffset+28,workflowsizeCntr+1,workflowsize));
			curSheet.addCell(new jxl.write.Number(colOffset+29,workflowsizeCntr+1,averageExecutionTime1/ ITERATIONS));
			curSheet.addCell(new jxl.write.Number(colOffset+30,workflowsizeCntr+1,averageExecutionTime2/ ITERATIONS));
			curSheet.addCell(new jxl.write.Number(colOffset+31,workflowsizeCntr+1,averageExecutionTime3/ ITERATIONS));
			curSheet.addCell(new jxl.write.Number(colOffset+32,workflowsizeCntr+1,workflowsize));
			curSheet.addCell(new jxl.write.Number(colOffset+33,workflowsizeCntr+1, ((double) averageGenerationTime1)/ ITERATIONS));
			curSheet.addCell(new jxl.write.Number(colOffset+34,workflowsizeCntr+1, ((double) averageGenerationTime2)/ ITERATIONS));
			curSheet.addCell(new jxl.write.Number(colOffset+35,workflowsizeCntr+1, ((double) averageGenerationTime3)/ ITERATIONS));
		}
		wb.write();
		wb.close();
	}
	
	public static void readWriteFMXml() throws Exception{
		String fmStr =UtilityClass.readFile("/home/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/orderprocessing/featureModel.xml");
		//String fmStr =UtilityClass.readFile("/home/mbashari/serializedFM.txt");
		String cmStr= UtilityClass.readFile("/home/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/orderprocessing/contextModel.xml");
		ContextModel cm = new ContextModel();
		cm.AddToContextModel(cmStr);
		FeatureAnnotationSet fma = new FeatureAnnotationSet();
		FeatureModel fm = FeatureModel.parse(fmStr, fma, cm);
		String result = fm.serializeToXml(fma);
		UtilityClass.writeFile(new File("/home/mbashari/serializedFM2.txt"), result);
	}
	
	public static void readFamaWritXml() throws Exception{
		///home/mbashari/fms/fm100/FeatureModel0.afm
		//String fmStr =UtilityClass.readFile("/home/mbashari/fms/fm50/FeatureModel0.afm");
		
		String cmStr= UtilityClass.readFile("/home/mbashari/EVAL_FOLDER/eval-rep/ontology.xml");
		ContextModel cm = new ContextModel();
		cm.AddToContextModel(cmStr);
		
		File fmDir = new File("/home/mbashari/EVAL_FOLDER/fms/fm30-3/");
		int cntr=0;
		
		for(File f : fmDir.listFiles()){
			String fmStr =UtilityClass.readFile( f.getAbsolutePath());
			FeatureModel fm = FeatureModel.parseFamaTextFormat(fmStr);
			FeatureAnnotationSet fma = FeatureAnnotationSet.createRandomAnnotationSet(cm, fm, "http://magus.online/eval/svs/fm"+cntr, 2.0f, 1.0f, 0.2f, 0.8f, 1.0f, 1.0f);
			
			String result = fm.serializeToXml(fma);
			
			UtilityClass.writeFile(new File("/home/mbashari/EVAL_FOLDER/evaluation/service/featuremodels/fm30-3/fm3"+String.format("%03d", cntr)+".xml"), result);
			cntr++;
		}
		
//		String fmStr =UtilityClass.readFile("/home/mbashari/fms/fm100/FeatureModel0.afm");
//		//String fmStr =UtilityClass.readFile("/home/mbashari/serializedFM.txt");
//		
//		
//		FeatureModel fm = FeatureModel.parseFamaTextFormat(fmStr);
//		FeatureAnnotationSet fma = FeatureAnnotationSet.createRandomAnnotationSet(cm, fm, "http://magus.online/test", 2.0f, 1.0f, 0.2f, 0.8f, 1.0f, 1.0f);
//		
//		String result = fm.serializeToXml(fma);
//		UtilityClass.writeFile(new File("/home/mbashari/serializedFM3.txt"), result);
	}
	
	public static void readFamaWritXml2() throws Exception{
		///home/mbashari/fms/fm100/FeatureModel0.afm
		//String fmStr =UtilityClass.readFile("/home/mbashari/fms/fm50/FeatureModel0.afm");
		
		String cmStr= UtilityClass.readFile("/home/mbashari/EVAL_FOLDER/eval-rep/ontology.xml");
		ContextModel cm = new ContextModel();
		cm.AddToContextModel(cmStr);
		
		
		int[] fmSizeList = {60,90,120,150,180,210,240};
		
		for(int fmSize: fmSizeList){
			File fmDir = new File("/home/mbashari/EVAL_FOLDER/fmdiffsizes/"+String.valueOf(fmSize)+"/");
			int cntr=0;
			UtilityClass.createFolder("/home/mbashari/EVAL_FOLDER/fmdiffsizesann/"+String.valueOf(fmSize)+"/");
			
			for(File f : fmDir.listFiles()){
				
				
				String fmStr =UtilityClass.readFile( f.getAbsolutePath());
				FeatureModel fm = FeatureModel.parseFamaTextFormat(fmStr);
				
				
				
				List<FeatureModelConfiguration> fmcs = fm.getAllValidConfiguration(10000);
				System.out.println(fmcs.size());
				
				if(fmcs.size()<500)
				{
					System.out.println("Too SMALL Skipping");
					continue;
				}
				
				if(fmcs.size()>2000)
				{
					System.out.println("Too BIG Skipping");
					continue;
				}
				
				FeatureAnnotationSet fma = FeatureAnnotationSet.createRandomAnnotationSet(cm, fm, "http://magus.online/eval/svs/fm"+cntr, 2.0f, 1.0f, 0.2f, 0.8f, 1.0f, 1.0f);
				
				String result = fm.serializeToXml(fma);
				
				UtilityClass.writeFile(new File("/home/mbashari/EVAL_FOLDER/fmdiffsizesann/"+String.valueOf(fmSize)+"/"+String.format("%03d", cntr)+".xml"), result);
				cntr++;
			}
			
		}
		
//		String fmStr =UtilityClass.readFile("/home/mbashari/fms/fm100/FeatureModel0.afm");
//		//String fmStr =UtilityClass.readFile("/home/mbashari/serializedFM.txt");
//		
//		
//		FeatureModel fm = FeatureModel.parseFamaTextFormat(fmStr);
//		FeatureAnnotationSet fma = FeatureAnnotationSet.createRandomAnnotationSet(cm, fm, "http://magus.online/test", 2.0f, 1.0f, 0.2f, 0.8f, 1.0f, 1.0f);
//		
//		String result = fm.serializeToXml(fma);
//		UtilityClass.writeFile(new File("/home/mbashari/serializedFM3.txt"), result);
	}
	
	public static void readFamaWritXml3() throws Exception{
		///home/mbashari/fms/fm100/FeatureModel0.afm
		//String fmStr =UtilityClass.readFile("/home/mbashari/fms/fm50/FeatureModel0.afm");
		
		String cmStr= UtilityClass.readFile("/home/mbashari/EVAL_FOLDER/eval-rep/ontology.xml");
		ContextModel cm = new ContextModel();
		cm.AddToContextModel(cmStr);
		
		int[] asl = {10,20,30,40,50};
		
		for(int as: asl)
			UtilityClass.createFolder("/home/mbashari/EVAL_FOLDER/fmdiffsizesann1/"+String.valueOf(as)+"/");
		
		File fmDir = new File("/home/mbashari/EVAL_FOLDER/fmdiffsizes2/0/");
		int cntr=0;
		
		
		for(File f : fmDir.listFiles()){
			
			
			String fmStr =UtilityClass.readFile( f.getAbsolutePath());
			FeatureModel fm = FeatureModel.parseFamaTextFormat(fmStr);
			
			List<FeatureModelConfiguration> fmcs = fm.getAllValidConfiguration(10000,10000);
			System.out.println(fmcs.size());
			
			if((fmcs.size()<300)||(fmcs.size()>10000))
			{
				System.out.println("Skipping");
				continue;
			}
			
			FeatureAtomicSetMap fasm = fm.findAtomicSets();
			
			
			
			int noOfAS1 = fasm.getAllAtomicSets(true).size();
			
			if(noOfAS1<36)
				continue;
			
			FeatureAtomicSetMap rfasm = fm.findActualAtomicSets();
			
			int noOfAS = rfasm.getAllAtomicSets(true).size();
			
			System.out.println("Number of Atomic Set: "+ noOfAS1);
			System.out.println("Number of Actual Atomic Set: "+ noOfAS);
							
			if(noOfAS<10)
			{
				System.out.println("Too SMALL Skipping");
				continue;
			}
			
			if(noOfAS>50)
			{
				System.out.println("Too BIG Skipping");
				continue;
			}
			
			FeatureAnnotationSet fma = FeatureAnnotationSet.createRandomAnnotationSet(cm, fm, "http://magus.online/eval/svs/fm"+cntr, 2.0f, 1.0f, 0.2f, 0.8f, 1.0f, 1.0f);
			
			String result = fm.serializeToXml(fma);
			
			int rndedASSize=  (int) (Math.round(((double) noOfAS)/10)*10);
			
			UtilityClass.writeFile(new File("/home/mbashari/EVAL_FOLDER/fmdiffsizesann1/"+String.valueOf(rndedASSize)+"/"+String.format("%03d", cntr)+".xml"), result);
			cntr++;
		}
			
		
		
//		String fmStr =UtilityClass.readFile("/home/mbashari/fms/fm100/FeatureModel0.afm");
//		//String fmStr =UtilityClass.readFile("/home/mbashari/serializedFM.txt");
//		
//		
//		FeatureModel fm = FeatureModel.parseFamaTextFormat(fmStr);
//		FeatureAnnotationSet fma = FeatureAnnotationSet.createRandomAnnotationSet(cm, fm, "http://magus.online/test", 2.0f, 1.0f, 0.2f, 0.8f, 1.0f, 1.0f);
//		
//		String result = fm.serializeToXml(fma);
//		UtilityClass.writeFile(new File("/home/mbashari/serializedFM3.txt"), result);
	}
	
	
	public static void testGeneratedFM() throws Exception{
		///home/mbashari/fms/fm100/FeatureModel0.afm
		//String fmStr =UtilityClass.readFile("/home/mbashari/fms/fm50/FeatureModel0.afm");
		
		String cmStr= UtilityClass.readFile("/home/mbashari/EVAL_FOLDER/eval-rep/ontology.xml");
		ContextModel cm = new ContextModel();
		cm.AddToContextModel(cmStr);
		
		
		
		File fmDir = new File("/home/mbashari/EVAL_FOLDER/fmdiffsizesann1/10/");
		//int cntr=0;
		
		
		for(File f : fmDir.listFiles()){
			
			
			String fmStr =UtilityClass.readFile( f.getAbsolutePath());
			FeatureAnnotationSet fma = new FeatureAnnotationSet();
			
			FeatureModel fm = FeatureModel.parse(fmStr, fma, cm);
			
			FeatureAtomicSetMap fasm = fm.findAtomicSets();
			
			FeatureAtomicSetMap rfasm = fm.findActualAtomicSets();
			
			List<FeatureModelConfiguration> fmcs = fm.getAllValidConfiguration(10000,10000);
			System.out.println(f.getName()+" Number of configuration : "+ fmcs.size()+" Estimated Atomic Sets: "+ fasm.getAllAtomicSets(true).size() +" Actual Atomic Sets: "+ rfasm.getAllAtomicSets(true).size());
			
//			if(fmcs.size()==0)
//				f.delete();
			
		}
			
		
		
//		String fmStr =UtilityClass.readFile("/home/mbashari/fms/fm100/FeatureModel0.afm");
//		//String fmStr =UtilityClass.readFile("/home/mbashari/serializedFM.txt");
//		
//		
//		FeatureModel fm = FeatureModel.parseFamaTextFormat(fmStr);
//		FeatureAnnotationSet fma = FeatureAnnotationSet.createRandomAnnotationSet(cm, fm, "http://magus.online/test", 2.0f, 1.0f, 0.2f, 0.8f, 1.0f, 1.0f);
//		
//		String result = fm.serializeToXml(fma);
//		UtilityClass.writeFile(new File("/home/mbashari/serializedFM3.txt"), result);
	}
	
	public static void writeAllPossibleConfiguraiton() throws Exception{
		
		//String fmStr =UtilityClass.readFile("/home/mbashari/featuremodel.xml");
		String fmStr =UtilityClass.readFile("/home/mbashari/EVAL_FOLDER/fmdiffsizesann/240/001.xml");
		//String fmStr =UtilityClass.readFile("/home/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/orderprocessing/featureModel.xml");
		//String fmStr =UtilityClass.readFile("/home/mbashari/serializedFM3.txt");
		String cmStr= UtilityClass.readFile("/home/mbashari/EVAL_FOLDER/eval-rep/ontology.xml");
		//String cmStr= UtilityClass.readFile("/home/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/orderprocessing/orderprocessing.xml");
		
		
		ContextModel cm = new ContextModel();
		cm.AddToContextModel(cmStr);
		FeatureAnnotationSet fma = new FeatureAnnotationSet();
		
		FeatureModel fm = FeatureModel.parse(fmStr, fma, cm);
		
		List<FeatureModelConfiguration> fmcs = fm.getAllValidConfiguration(-1);
		System.out.println(fmcs.size());
//		for(FeatureModelConfiguration fmc : fmcs){
//			System.out.println(fmc.toString());
//		}
		
//		for(int cnt1 =0; cnt1<fmcs.size();cnt1++){
//			for(int cnt2=cnt1+1; cnt2<fmcs.size(); cnt2++){
//				if(fmcs.get(cnt1).equals(fmcs.get(cnt2)))
//					System.out.println("Identical Configuration Found");
//			}
//		}
	}
	
	public static void writeAllPossibleConfiguraiton2() throws Exception{
		String cmStr= UtilityClass.readFile("/home/mbashari/EVAL_FOLDER/eval-rep/ontology.xml");
		ContextModel cm = new ContextModel();
		cm.AddToContextModel(cmStr);
		
		File fmDir = new File("/home/mbashari/EVAL_FOLDER/evaluation/service/featuremodels/DATA/");
		//int cntr=0;
		
		int[] sizes=  {400,800,1200,1600,2000};
		
		for(File f : fmDir.listFiles()){
			String fmStr =UtilityClass.readFile(f.getAbsolutePath());
			//FeatureAnnotationSet fma = new FeatureAnnotationSet();
			//FeatureModel fm = FeatureModel.parse(fmStr, fma, cm);
			FeatureModel fm = FeatureModel.parseFamaTextFormat(fmStr);
			System.out.println(f.getName());
			List<FeatureModelConfiguration> fmcs = fm.getAllValidConfiguration(10000);
			System.out.println(fmcs.size());
			System.out.println();
			int fmcsSize = fmcs.size();
			
			for(int scntr=0; scntr< sizes.length; scntr++){
				if((fmcsSize>=(sizes[scntr]-200))&&(fmcsSize<=(sizes[scntr]+200))){
					System.out.println("copying to corresponding folder");
					
					FeatureAnnotationSet fma = FeatureAnnotationSet.createRandomAnnotationSet(cm, fm, "http://magus.online/eval/svs/f"+f.getName().substring(0,f.getName().lastIndexOf(".")), 2.0f, 1.0f, 0.2f, 0.8f, 1.0f, 1.0f);
					
					String result = fm.serializeToXml(fma);
					
					UtilityClass.writeFile(new File("/home/mbashari/EVAL_FOLDER/evaluation/fmsize/featureModelOnly/"+String.valueOf(sizes[scntr])+"/"+f.getName().substring(0,f.getName().lastIndexOf("."))+".xml"), result);
					
					//Files.copy( f.toPath(), new File( "/home/mbashari/EVAL_FOLDER/evaluation/fmsize/featureModelOnly/"+String.valueOf(sizes[scntr])+"/"+f.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
					break;
				}
			}
			
		}
		
		
	}
	
	
	
	
	
	public static void createServicesForASampleFM() throws Exception{
//		String fmStr =UtilityClass.readFile("/home/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/test/featureModel.xml");
//		String fmStr =UtilityClass.readFile("/home/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/orderprocessing/featureModel.xml");
		//String fmStr =UtilityClass.readFile("/home/mbashari/serializedFM3.txt");
		String fmStr =UtilityClass.readFile("/home/mbashari/evaluation/service/featuremodels/fm048.xml");
		String cmStr= UtilityClass.readFile("/home/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/test/contextModel.xml");
		//String cmStr= UtilityClass.readFile("/home/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/orderprocessing/orderprocessing.xml");
		ContextModel cm = new ContextModel();
		cm.AddToContextModel(cmStr);
		FeatureAnnotationSet fma = new FeatureAnnotationSet();
		
		FeatureModel fm = FeatureModel.parse(fmStr, fma, cm);
		
		String baseURI= "http://magus.online/test/services/";
		
		 
		
		
		float preconditionAvg = 1;
		float preconditionStdDev=1;
		float effectAvg=2;
		float effectStdDev=1;
		float lengthynessFactorAvg=0.5f;
		float lengthynessFactorStdDev=0.2f;
		float notRatio=0.2f;
		ServiceCollection sc = ServiceGenerator.GenerateService(cm, fm, fma, baseURI, preconditionAvg, preconditionStdDev, effectAvg, effectStdDev, lengthynessFactorAvg, lengthynessFactorStdDev, notRatio,null,null);
		
		sc.writeToDirectory(homeAddress+"test2");
		
	}
	
	
	public static void fixNumberOfServices() throws Exception{
		String sourceDir ="/home/mbashari/EVAL_FOLDER/evaluation/fmsize/smfamilyNE/";
		String targetDir ="/home/mbashari/EVAL_FOLDER/evaluation/fmsize/smfamily/";
		
		int adjustToNo =250;
		
		int[] repositorySize = {400,800,1200,1600,2000};
		

		for(int rcntr =0; rcntr<repositorySize.length; rcntr++){
			System.out.println(repositorySize[rcntr]);
			File[] smFamilyDirectories = new File(sourceDir+String.valueOf(repositorySize[rcntr])).listFiles(new FileFilter() {
				
				public boolean accept(File pathname) {
					
					return pathname.isDirectory();
				}
			});
			
			UtilityClass.createFolder(targetDir+String.valueOf(repositorySize[rcntr]));
			
			for(File curDir: smFamilyDirectories){
				
				DomainModels dm = DomainModels.readFromDirectory(curDir);
				
				adjustNoOfServices(dm,adjustToNo);
				UtilityClass.createFolder(targetDir+String.valueOf(repositorySize[rcntr])+"/"+curDir.getName());
				
				dm.writeToDirectory(targetDir+String.valueOf(repositorySize[rcntr])+"/"+curDir.getName());
				
			}
		}
	}
	
	private static void adjustNoOfServices(DomainModels dm, int adjustToNo) throws Exception {
		int svsSize =dm.getServiceCollection().getServices().size();
		
//		if(svsSize>adjustToNo)
//			throw new Exception("Services larger than it should");
		
		int noToAdd = adjustToNo-svsSize;
		
		HashSet<String> usedNames = new HashSet<String>();
		
		for(int cntr=0; cntr<noToAdd; cntr++){
			int service1idx  = UtilityClass.randInt(0, svsSize-1);
			int service2idx  = UtilityClass.randInt(0, svsSize-1);
			
			Service service1 = dm.getServiceCollection().getServices().get(service1idx);
			Service service2 = dm.getServiceCollection().getServices().get(service2idx);
						
			String name = service1.getName()+service2.getName();
			
			while(usedNames.contains(name)){
				service1idx  = UtilityClass.randInt(0, svsSize-1);
				service2idx  = UtilityClass.randInt(0, svsSize-1);
				service1 = dm.getServiceCollection().getServices().get(service1idx);
				service2 = dm.getServiceCollection().getServices().get(service2idx);
							
				name = service1.getName()+service2.getName();
			}
			
			
			
			int noOfPre = (int) Math.round( UtilityClass.randValueNormalP(1, 1));
			int noOfEff = (int) Math.round( UtilityClass.CuttedRandValueNormal(2, 1, 1));
			
			
			List<URI> varList = new ArrayList<URI>();
			Map<URI, Instance> vars = new HashMap<URI, Instance>();
			Map<URI, Instance> contextVars = new HashMap<URI, Instance>();
			List<URI> contextVarList = new ArrayList<URI>();
			
			URI serviceUri = new URI(service1.getURI().toString()+service2.getName());
			
			List<StateFactInstanceS> servicePrecondition = new ArrayList<StateFactInstanceS>();
			List<StateFactInstanceS> serviceEffects = new ArrayList<StateFactInstanceS>();
			
			Map<URI, Instance> inputs = new HashMap<URI, Instance>();
			Map<URI, Instance> outputs= new HashMap<URI, Instance>();
			List<URI> inputList = new ArrayList<URI>();
			List<URI> outputList= new ArrayList<URI>();
			
			Map<URI,Instance> replacementMap =  new HashMap<URI, Instance>();
			Map<URI,URI> inputInstanceMap = new HashMap<URI, URI>(); 
			
			for(Instance ins: service1.getInputs().values()){
				
				
				
				URI uri = new URI(serviceUri+"#"+ins.getName());
				
				
				
				Instance newInstance = new Instance(ins.getType(), ins.getName(), uri);
				
				inputList.add(uri);
				inputs.put(uri, newInstance);
				replacementMap.put(uri, newInstance);
				inputInstanceMap.put(ins.getURI(), uri);
			}
			
			
			for(Instance ins: service2.getInputs().values()){
				
				
				
				URI uri = new URI(serviceUri+"#"+ins.getName());
				
				if(!replacementMap.containsKey(uri))
				{
					Instance newInstance = new Instance(ins.getType(), ins.getName(), uri);
					
					inputList.add(uri);
					inputs.put(uri, newInstance);
					replacementMap.put(uri, newInstance);
				}
				inputInstanceMap.put(ins.getURI(), uri);
				
				
			}
			
			for(Instance ins: service1.getOutputs().values()){
				
				
				
				URI uri = new URI(serviceUri+"#"+ins.getName());
				
				if(!replacementMap.containsKey(uri))
				{
					Instance newInstance = new Instance(ins.getType(), ins.getName(), uri);
					
					outputList.add(uri);
					outputs.put(uri, newInstance);
					replacementMap.put(uri, newInstance);
				}
				inputInstanceMap.put(ins.getURI(), uri);
				
				
				
			}
			
			
			for(Instance ins: service2.getOutputs().values()){
				
				
				
				URI uri = new URI(serviceUri+"#"+ins.getName());
				
				if(!replacementMap.containsKey(uri))
				{
					Instance newInstance = new Instance(ins.getType(), ins.getName(), uri);
					
					outputList.add(uri);
					outputs.put(uri, newInstance);
					replacementMap.put(uri, newInstance);
				}
				inputInstanceMap.put(ins.getURI(), uri);
				
				
			}
			
			List<StateFactInstanceS> servicePreconditionAll = new ArrayList<StateFactInstanceS>();
			List<StateFactInstanceS> serviceEffectsAll = new ArrayList<StateFactInstanceS>();
			
			for(StateFactInstanceS sfis: service1.getPrecondition().getConditions()){
				servicePreconditionAll.add(new StateFactInstanceS(sfis.getStateFactInstance().replaceParams(replacementMap,inputInstanceMap), sfis.isNot()));
			}
			
			for(StateFactInstanceS sfis: service2.getPrecondition().getConditions()){
				servicePreconditionAll.add(new StateFactInstanceS(sfis.getStateFactInstance().replaceParams(replacementMap,inputInstanceMap), sfis.isNot()));
			}
			

			for(StateFactInstanceS sfis: service1.getPostcondition().getConditions()){
				serviceEffectsAll.add(new StateFactInstanceS(sfis.getStateFactInstance().replaceParams(replacementMap,inputInstanceMap), sfis.isNot()));
			}
			
			for(StateFactInstanceS sfis: service2.getPostcondition().getConditions()){
				serviceEffectsAll.add(new StateFactInstanceS(sfis.getStateFactInstance().replaceParams(replacementMap,inputInstanceMap), sfis.isNot()));
			}
			
			
			
			if(noOfPre<servicePreconditionAll.size()){
				Set<Integer> pi = UtilityClass.randIntSet(servicePreconditionAll.size()-1, noOfPre);
				for(int i: pi)
					servicePrecondition.add(servicePreconditionAll.get(i));
			}
			else
			{
				servicePrecondition.addAll(servicePreconditionAll);
			}
			
			if(noOfEff<serviceEffectsAll.size()){
				Set<Integer> pi = UtilityClass.randIntSet(serviceEffectsAll.size()-1, noOfEff);
				for(int i: pi)
					serviceEffects.add(serviceEffectsAll.get(i));
			}
			else
			{
				serviceEffects.addAll(serviceEffectsAll);
			}
			
			Condition precondition = new Condition(servicePrecondition);
			Condition postcondition = new Condition(serviceEffects);
			
			Service s = new Service(name, precondition, postcondition, inputs, outputs, vars, contextVars, inputList, outputList, varList, contextVarList, null, serviceUri.toString());
			
			dm.getServiceCollection().getServices().add(s);
			
		}
	}

	public static void generateServiceForFM() throws Exception{
		String jarAddress = App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		System.out.println(jarAddress);
		String dirAddress = jarAddress.substring(0 ,jarAddress.lastIndexOf('/')+1);
		String tempAddress =jarAddress.substring(0 ,jarAddress.lastIndexOf('/')); 
		String upDirAddress = tempAddress.substring(0 ,tempAddress.lastIndexOf('/')+1);
		
		String resultDir  = UtilityClass.readFile(dirAddress+ "rconf.txt");
		
		resultDir = upDirAddress+resultDir+"/";
		
		System.out.println(dirAddress);
		Configuration.plannerAddress = dirAddress;
		Configuration.tempFolder = dirAddress+ "temp/";
		SimpleLogger log= new SimpleLogger(dirAddress+"log.txt", true);
		 
		log.log("Starting");
		 
		String lengthynessFactorString  = UtilityClass.readFile(dirAddress+ "conf.txt");
		log.log("Lengthy factor was read as "+lengthynessFactorString);
		 
		 
		String fmStr =UtilityClass.readFile(dirAddress+ "fm.xml");
		String cmStr= UtilityClass.readFile(dirAddress+ "contextModel.xml");
		//String cmStr= UtilityClass.readFile("/homes/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/orderprocessing/orderprocessing.xml");
		ContextModel cm = new ContextModel();
		cm.AddToContextModel(cmStr);
		FeatureAnnotationSet fma = new FeatureAnnotationSet();
		
		FeatureModel fm = FeatureModel.parse(fmStr, fma, cm);
		
		
		List<FeatureModelConfiguration> fmcs = fm.getAllValidConfiguration(-1);
		
		
		String finalDirAddress = resultDir+String.valueOf(fmcs.size())+"/";
		File finalDirAddressf = new File(finalDirAddress);
		
		
		int cntr=1;
		
		while(finalDirAddressf.exists()){
			finalDirAddress = resultDir+String.valueOf(fmcs.size())+"-"+String.valueOf(cntr)+"/";
			finalDirAddressf = new File(finalDirAddress);
			cntr++;
		}
		
		finalDirAddressf.mkdirs();
		
		Files.copy( (new File(dirAddress+ "fm.xml")).toPath(),(new File(finalDirAddress+ "fm.xml")).toPath() , StandardCopyOption.REPLACE_EXISTING);
		Files.copy( (new File(dirAddress+ "contextModel.xml")).toPath(),(new File(finalDirAddress+ "contextModel.xml")).toPath() , StandardCopyOption.REPLACE_EXISTING);
		
		String baseURI= "http://magus.online/evaluation/service/service/";
		
		(new File(finalDirAddress+"services")).mkdirs();
		
		
		float preconditionAvg = 1;
		float preconditionStdDev=1;
		float effectAvg=2;
		float effectStdDev=1;
		float lengthynessFactorAvg=Float.valueOf(lengthynessFactorString);
		float lengthynessFactorStdDev=0.2f;
		float notRatio=0.2f;
		ServiceGenerator.GenerateService(cm, fm, fma, baseURI, preconditionAvg, preconditionStdDev, effectAvg, effectStdDev, lengthynessFactorAvg, lengthynessFactorStdDev, notRatio,finalDirAddress+"services",log);
		
		
		String servicesDir = finalDirAddress+"services";

		List<String> serviceAddresses = new ArrayList<String>();
		File sv = new File(servicesDir);
		for(File f3:sv.listFiles())
			serviceAddresses.add("services/"+f3.getName());
		
		List<String> contextModelAddresses = new ArrayList<String>();
		contextModelAddresses.add("contextModel.xml");
		
		DomainModelConfiguration dmc  = new DomainModelConfiguration(contextModelAddresses, "fm.xml", serviceAddresses);
		
		
		
		
		UtilityClass.writeFile(new File(dirAddress+ "configuration.xml"), dmc.serializeToConfigurationFileXml());
		 
	}
	
	
	public static void generateServiceForFM2() throws Exception{
		
		
		String jarAddress ="";
		
	   
		jarAddress= URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").getPath(), "UTF-8");
	   
		
		
		System.out.println(jarAddress);
		String dirAddress = jarAddress.substring(0 ,jarAddress.lastIndexOf('/')+1);

		
		int fmSize  = Integer.valueOf( UtilityClass.readFile(dirAddress+ "rconf.txt").trim());
		
		
		int stIndex = Integer.valueOf(UtilityClass.readFile(dirAddress+ "stidx.txt").trim());
		int enIndex = Integer.valueOf(UtilityClass.readFile(dirAddress+ "enidx.txt").trim());
		
		System.out.println(fmSize);
		System.out.println("Hello");
		
		System.out.println(dirAddress);
		Configuration.plannerAddress = dirAddress;
		Configuration.tempFolder = dirAddress+ "temp/";
		SimpleLogger log= new SimpleLogger(dirAddress+"log.txt", true);
		 
		

		 
		log.log("Starting");
		 
		String lengthynessFactorString  ="2.0f";
		 
		String cmStr= UtilityClass.readFile( dirAddress+"contextModel.xml");
		ContextModel cm = new ContextModel();
		cm.AddToContextModel(cmStr);
		
		
		String fmDir = dirAddress+"fmdiffsizesann/"+fmSize+"/";
		
		UtilityClass.createFolder(dirAddress+String.valueOf(fmSize)+"/");
		
		for(int fmCntr=stIndex; fmCntr<enIndex; fmCntr++){
			String tgtDirAddress = dirAddress+String.valueOf(fmSize)+"/sm"+String.valueOf(fmCntr+1);
			UtilityClass.createFolder(tgtDirAddress);
			String servicesDir = tgtDirAddress+"/services";
			UtilityClass.createFolder(servicesDir);
			
			
			String fmAddress = fmDir+String.format("%03d", fmCntr) +".xml";
			String fmStr =UtilityClass.readFile(fmAddress);
			
			UtilityClass.copyFile(fmAddress, tgtDirAddress+"/fm.xml");
			UtilityClass.copyFile(dirAddress+"contextModel.xml", tgtDirAddress+"/contextModel.xml");
			
			FeatureAnnotationSet fma = new FeatureAnnotationSet();
			
			FeatureModel fm = FeatureModel.parse(fmStr, fma, cm);
			
			
			
			String baseURI= "http://magus.online/evaluation/service/service/";
			
			float preconditionAvg = 1;
			float preconditionStdDev=1;
			float effectAvg=2;
			float effectStdDev=1;
			float lengthynessFactorAvg=Float.valueOf(lengthynessFactorString);
			float lengthynessFactorStdDev=0.2f;
			float notRatio=0.2f;
			ServiceGenerator.GenerateService(cm, fm, fma, baseURI, preconditionAvg, preconditionStdDev, effectAvg, effectStdDev, lengthynessFactorAvg, lengthynessFactorStdDev, notRatio,servicesDir,log);
			
			
			
			List<String> serviceAddresses = new ArrayList<String>();
			File sv = new File(servicesDir);
			for(File f3:sv.listFiles())
				serviceAddresses.add("services/"+f3.getName());
			

			List<String> contextModelAddresses = new ArrayList<String>();
			contextModelAddresses.add("contextModel.xml");
			
			DomainModelConfiguration dmc  = new DomainModelConfiguration(contextModelAddresses, "fm.xml", serviceAddresses);
			
			UtilityClass.writeFile(new File(tgtDirAddress+ "/configuration.xml"), dmc.serializeToConfigurationFileXml());
		}
			
			
		
		 
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		 
	}
	
	public static void runEvaluationServiceSize() throws Exception{
		WritableWorkbook wb = Workbook.createWorkbook(new File(homeAddress+ "evaluationServiceSize.xls"));
		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
		
		String evaluationDirectory ="/home/mbashari/EVAL_FOLDER/evaluation/service/smfamily/";
		int[] repositorySize = {100,150,200,250,300,350,400};
		//int[] repositorySize = {400};
		int repositoryIterationSize = 250;
		int fmcSize = 15;
		int fmcVariation =2;
		
		int offset =0;
		int colOffset=0;
		
		int offsetTbl1 =5;
		int colOffsetTbl1=20;
		
		int offsetTbl2 =5;
		int colOffsetTbl2=40;
		
		int offsetTbl3 =5;
		int colOffsetTbl3=11;
		
		
		curSheet.addCell(new Label(colOffset+1,0,"Mashup Family Name"));
		curSheet.addCell(new Label(colOffset+2,0,"Configuration No"));
		curSheet.addCell(new Label(colOffset+3,0,"Failed Service"));
		curSheet.addCell(new Label(colOffset+4,0,"Adaptation type"));
		curSheet.addCell(new Label(colOffset+5,0,"Number of Services"));
		curSheet.addCell(new Label(colOffset+6,0,"Service Replacement Time"));
		curSheet.addCell(new Label(colOffset+7,0,"Replanning Time"));
		curSheet.addCell(new Label(colOffset+8,0,"Feature model reconfiguration"));
		curSheet.addCell(new Label(colOffset+9,0,"Feature Distance"));
		curSheet.addCell(new Label(colOffset+10,0,"Number of tries"));
		
		
		
		curSheet.addCell(new Number(colOffsetTbl3+3,offsetTbl3,3));
		curSheet.addCell(new Number(colOffsetTbl3+4,offsetTbl3,2));
		curSheet.addCell(new Number(colOffsetTbl3+5,offsetTbl3,2));
		curSheet.addCell(new Number(colOffsetTbl3+6,offsetTbl3,100));
		offsetTbl3++;
		
		curSheet.addCell(new Label(colOffsetTbl3+1,offsetTbl3,"Number of Confs"));
		curSheet.addCell(new Label(colOffsetTbl3+2,offsetTbl3,"Number of Possible Configuration"));
		curSheet.addCell(new Label(colOffsetTbl3+3,offsetTbl3,"Average of Workflow Adaptation (Failed)"));
		curSheet.addCell(new Label(colOffsetTbl3+4,offsetTbl3,"Average of Workflow Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl3+5,offsetTbl3,"Average of Feature Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl3+6,offsetTbl3,"Average of Feature Adaptation (Failed)"));
		offsetTbl3++;
		
		
		curSheet.addCell(new Label(colOffsetTbl1+1,offsetTbl1,"Number of Possible Configuration"));
		curSheet.addCell(new Label(colOffsetTbl1+2,offsetTbl1,"Number of Service Lookups"));
		curSheet.addCell(new Label(colOffsetTbl1+3,offsetTbl1,"Average Service Lookup time"));
		curSheet.addCell(new Label(colOffsetTbl1+4,offsetTbl1,"Number of Workflow Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl1+5,offsetTbl1,"Average of Workflow Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl1+6,offsetTbl1,"Number of Feature Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl1+7,offsetTbl1,"Average of Feature Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl1+8,offsetTbl1,"Number of Workflow Adaptation (Failed)"));
		curSheet.addCell(new Label(colOffsetTbl1+9,offsetTbl1,"Average of Workflow Adaptation (Failed)"));
		curSheet.addCell(new Label(colOffsetTbl1+10,offsetTbl1,"Number of Feature Adaptation (Failed)"));
		curSheet.addCell(new Label(colOffsetTbl1+11,offsetTbl1,"Average of Feature Adaptation (Failed)"));
		curSheet.addCell(new Label(colOffsetTbl1+12,offsetTbl1,"Service Lookup"));
		curSheet.addCell(new Label(colOffsetTbl1+13,offsetTbl1,"Workflow Adaptation"));
		curSheet.addCell(new Label(colOffsetTbl1+14,offsetTbl1,"Feature Adaptation "));
		curSheet.addCell(new Label(colOffsetTbl1+15,offsetTbl1,"Adaptation Failure"));
		
		curSheet.addCell(new Label(colOffsetTbl2+1,offsetTbl2,"Number of Feature Change"));
		curSheet.addCell(new Label(colOffsetTbl2+2,offsetTbl2,"Occurences"));
		
		Map<Integer,Integer> featureChangeOccurenceMap = new HashMap<Integer, Integer>();
		
		offsetTbl1++;
		offsetTbl2++;
		
		offset++;
		int row = offset;
		
		for(int rcntr =0; rcntr<repositorySize.length; rcntr++){
			System.out.println(repositorySize[rcntr]);
			
			long cntServiceLookup=0;
			long sumServiceLookup=0;
			long avgServiceLookup=0;
			
			long serviceLookupCntr=0;
			
			long cntPlanningDurationSucc=0;
			long sumPlanningDurationSucc=0;
			long avgPlanningDurationSucc=0;
			
			long cntPlanningDurationFail=0;
			long sumPlanningDurationFail=0;
			long avgPlanningDurationFail=0;
			
			long cntFeatureAdaptationDurationSucc=0;
			long sumFeatureAdaptationDurationSucc=0;
			long avgFeatureAdaptationDurationSucc=0;
			
			long cntFeatureAdaptationDurationFail=0;
			long sumFeatureAdaptationDurationFail=0;
			long avgFeatureAdaptationDurationFail=0;
			
			int evalCntr=0;
			
			File[] smFamilyDirectories = new File(evaluationDirectory+String.valueOf(repositorySize[rcntr])).listFiles(new FileFilter() {
				
				public boolean accept(File pathname) {
					
					return pathname.isDirectory();
				}
			});
			
			int maxSMIterationNo = repositoryIterationSize/smFamilyDirectories.length;
			
			
			for(File curDir: smFamilyDirectories){
				
				DomainModels dm = DomainModels.readFromDirectory(curDir);
				ContextStateModel contextStateModel = new ContextStateModel(dm.getServiceCollection());
				List<FeatureModelConfiguration> fmcs = dm.getFeatureModel().getAllValidConfiguration(-1,fmcSize,fmcVariation);
				if(fmcs.size()==0)
					continue;
				
//				int lrow = row;
				
				for(int iterationCntr=0; iterationCntr<maxSMIterationNo; iterationCntr++ ){
					
					
					int featureModelConfigurationNo = UtilityClass.randInt(0, fmcs.size()-1);
					

					
					FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(dm, fmcs.get(featureModelConfigurationNo),contextStateModel);
					
					
					FlowComponentNode fcn =	fmcmg.buildServiceMashup();
					
					List<Service> mashupServices = fcn.findAllCalledServices();
					
					Service s = mashupServices.get(UtilityClass.randInt(0, mashupServices.size()-1));
					
					//System.out.println(  iterationCntr+"/" + maxSMIterationNo + " Rep: "+String.valueOf(repositorySize[rcntr])+ " SM: "+ curDir.getName() +" No Of Conf:" + featureModelConfigurationNo +" Services: "+ mashupServices.size()+" Service Name: "+ s.getName());
					
					//System.out.println("Failed service: "+s.getName());
					contextStateModel.getServiceAvailabilty().put(s.getURI(),false);
					AdaptationResult ar = dm.getFeatureModel().adapt(dm,fmcs.get(featureModelConfigurationNo),fcn, s,contextStateModel);
					contextStateModel.getServiceAvailabilty().put(s.getURI(),true);
					
					
					cntServiceLookup++;
					sumServiceLookup += ar.getServiceAdaptationTime();
					
					if(ar.getAdaptationType() > AdaptationResult.SERVICE_ADAPTATION){
						if(ar.getAdaptationType()> AdaptationResult.WORKFLOW_ADAPTATION){
							
							cntPlanningDurationFail++;
							sumPlanningDurationFail+=ar.getWorkflowAdaptationTime();
							
							if(ar.getAdaptationType()> AdaptationResult.FEATURE_ADAPTATION)
							{
								cntFeatureAdaptationDurationFail ++;
								sumFeatureAdaptationDurationFail += ar.getFeatureAdaptationTime();
							}
							else
							{
								cntFeatureAdaptationDurationSucc ++;
								sumFeatureAdaptationDurationSucc += ar.getFeatureAdaptationTime();
								Integer featureChange = dm.getFeatureModel().getDistance(fmcs.get(featureModelConfigurationNo),ar.getAlternateFeatureModelConfiguration());
								if(!featureChangeOccurenceMap.containsKey(featureChange))
									featureChangeOccurenceMap.put(featureChange, 0);
								featureChangeOccurenceMap.put(featureChange, featureChangeOccurenceMap.get(featureChange)+1);
							}
						}
						else{
							cntPlanningDurationSucc++;
							sumPlanningDurationSucc+=ar.getWorkflowAdaptationTime();
						}
					}
					else{
						serviceLookupCntr++;
					}
					
					
					//System.out.println("Adaptation type: "+ar.getAdaptationType());
					curSheet.addCell(new Label(colOffset+1,row,curDir.toString()));
					curSheet.addCell(new Label(colOffset+2,row, String.valueOf(featureModelConfigurationNo)));
					curSheet.addCell(new Label(colOffset+3,row,s.getName()));
					curSheet.addCell(new Number(colOffset+4,row,ar.getAdaptationType()));
					curSheet.addCell(new Number(colOffset+5,row,repositorySize[rcntr]));
					curSheet.addCell(new Number(colOffset+6,row,ar.getServiceAdaptationTime()));
					if(ar.getAdaptationType()!=AdaptationResult.SERVICE_ADAPTATION){
						curSheet.addCell(new Number(colOffset+7,row,ar.getWorkflowAdaptationTime()));
						if(ar.getAdaptationType()!=AdaptationResult.WORKFLOW_ADAPTATION){
							curSheet.addCell(new Number(colOffset+8,row,ar.getFeatureAdaptationTime()));
							if(ar.getAdaptationType()==AdaptationResult.FEATURE_ADAPTATION){
								curSheet.addCell(new Number(colOffset+9,row,dm.getFeatureModel().getDistance(fmcs.get(featureModelConfigurationNo),ar.getAlternateFeatureModelConfiguration())));
								curSheet.addCell(new Number(colOffset+10,row,ar.getNoOfTries()));
							}
						}					
					}
					
					row++;
					
					evalCntr++;
					
					
					
				}
				
//				String beginRow = String.valueOf(lrow);
//				String endRow = String.valueOf(row-1);
				
				curSheet.addCell(new Number(colOffsetTbl3+1,offsetTbl3,repositorySize[rcntr]));
				curSheet.addCell(new Label(colOffsetTbl3+2,offsetTbl3,curDir.toString()));				
//				curSheet.addCell(new Formula(colOffsetTbl3+3, offsetTbl3,"AVERAGE(H"+beginRow+":H"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+4, offsetTbl3,"AVERAGE(H"+beginRow+":H"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+5, offsetTbl3,"AVERAGE(I"+beginRow+":I"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+6, offsetTbl3,"AVERAGE(I"+beginRow+":I"+endRow+")"));
				
//				curSheet.addCell(new Label(colOffsetTbl3+3, offsetTbl3,"AVERAGEIFS(H"+beginRow+":H"+endRow+",E"+beginRow+":E"+endRow+",\"=3\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+4, offsetTbl3,"AVERAGEIFS(H"+beginRow+":H"+endRow+",E"+beginRow+":E"+endRow+",\"=2\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+5, offsetTbl3,"AVERAGEIFS(I"+beginRow+":I"+endRow+",E"+beginRow+":E"+endRow+",\"=3\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+6, offsetTbl3,"AVERAGEIFS(I"+beginRow+":I"+endRow+",E"+beginRow+":E"+endRow+",\"=100\")"));
//				
				
				offsetTbl3++;
				
				
			}
			
			if(cntServiceLookup>0)
				avgServiceLookup = sumServiceLookup/cntServiceLookup;
			
			if(cntPlanningDurationSucc>0)
				avgPlanningDurationSucc = sumPlanningDurationSucc/cntPlanningDurationSucc;
			
			if(cntFeatureAdaptationDurationSucc>0)
				avgFeatureAdaptationDurationSucc = sumFeatureAdaptationDurationSucc/cntFeatureAdaptationDurationSucc;
			
			if(cntPlanningDurationFail>0)
				avgPlanningDurationFail = sumPlanningDurationFail/cntPlanningDurationFail;
			
			if(cntFeatureAdaptationDurationFail>0)
				avgFeatureAdaptationDurationFail = sumFeatureAdaptationDurationFail/cntFeatureAdaptationDurationFail;
			
			float serviceLookupPercentage = ((float) serviceLookupCntr) /evalCntr;
			float workflowAdaptationPercentage = ((float) cntPlanningDurationSucc) /evalCntr;
			float featureAdaptationPercentage = ((float) cntFeatureAdaptationDurationSucc) /evalCntr;
			float adaptationFailPercentage = ((float) cntFeatureAdaptationDurationFail) /evalCntr;
			
			
			
			curSheet.addCell(new Label(colOffsetTbl1+1,offsetTbl1,String.valueOf(repositorySize[rcntr])));
			curSheet.addCell(new Label(colOffsetTbl1+2,offsetTbl1, String.valueOf(cntServiceLookup)));
			curSheet.addCell(new Label(colOffsetTbl1+3,offsetTbl1,String.valueOf(avgServiceLookup)));
			curSheet.addCell(new Label(colOffsetTbl1+4,offsetTbl1,String.valueOf(cntPlanningDurationSucc)));
			curSheet.addCell(new Label(colOffsetTbl1+5,offsetTbl1,String.valueOf(avgPlanningDurationSucc)));
			curSheet.addCell(new Label(colOffsetTbl1+6,offsetTbl1,String.valueOf(cntFeatureAdaptationDurationSucc)));
			curSheet.addCell(new Label(colOffsetTbl1+7,offsetTbl1,String.valueOf(avgFeatureAdaptationDurationSucc)));
			curSheet.addCell(new Label(colOffsetTbl1+8,offsetTbl1,String.valueOf(cntPlanningDurationFail)));
			curSheet.addCell(new Label(colOffsetTbl1+9,offsetTbl1,String.valueOf(avgPlanningDurationFail)));
			curSheet.addCell(new Label(colOffsetTbl1+10,offsetTbl1,String.valueOf(cntFeatureAdaptationDurationFail)));
			curSheet.addCell(new Label(colOffsetTbl1+11,offsetTbl1,String.valueOf(avgFeatureAdaptationDurationFail)));
			curSheet.addCell(new jxl.write.Number(colOffsetTbl1+12,offsetTbl1,serviceLookupPercentage));
			curSheet.addCell(new jxl.write.Number(colOffsetTbl1+13,offsetTbl1,workflowAdaptationPercentage));
			curSheet.addCell(new jxl.write.Number(colOffsetTbl1+14,offsetTbl1,featureAdaptationPercentage));
			curSheet.addCell(new jxl.write.Number(colOffsetTbl1+15,offsetTbl1,adaptationFailPercentage));
			offsetTbl1++;
		}
		
//		int cntr=0;
		for(Integer fno: featureChangeOccurenceMap.keySet()){
			curSheet.addCell(new Label(colOffsetTbl2+1,offsetTbl2,String.valueOf(fno)));
			curSheet.addCell(new Label(colOffsetTbl2+2,offsetTbl2, String.valueOf(featureChangeOccurenceMap.get(fno))));
			offsetTbl2++;
		}
		
		wb.write();
		wb.close();
		
	}
	
	public static void runEvaluationFMSize() throws Exception{
		WritableWorkbook wb = Workbook.createWorkbook(new File(homeAddress+ "evaluationFMSize.xls"));
		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
		
		String evaluationDirectory ="/home/mbashari/EVAL_FOLDER/evaluation/fmsize/smfamily/";
		int[] repositorySize = {400,800,1200,1600,2000};
		//int[] repositorySize = {400};
		int repositoryIterationSize = 1000;
		int fmcSize = 15;
		int fmcVariation =1;
		
		int offset =0;
		int colOffset=0;
		
		int offsetTbl1 =5;
		int colOffsetTbl1=20;
		
		int offsetTbl2 =5;
		int colOffsetTbl2=40;
		
		int offsetTbl3 =5;
		int colOffsetTbl3=11;
		
		
		curSheet.addCell(new Label(colOffset+1,0,"Mashup Family Name"));
		curSheet.addCell(new Label(colOffset+2,0,"Configuration No"));
		curSheet.addCell(new Label(colOffset+3,0,"Failed Service"));
		curSheet.addCell(new Label(colOffset+4,0,"Adaptation type"));
		curSheet.addCell(new Label(colOffset+5,0,"Number of Confs"));
		curSheet.addCell(new Label(colOffset+6,0,"Service Replacement Time"));
		curSheet.addCell(new Label(colOffset+7,0,"Replanning Time"));
		curSheet.addCell(new Label(colOffset+8,0,"Feature model reconfiguration"));
		curSheet.addCell(new Label(colOffset+9,0,"Feature Distance"));
		curSheet.addCell(new Label(colOffset+10,0,"Number of tries"));
		
		
		
		curSheet.addCell(new Number(colOffsetTbl3+3,offsetTbl3,3));
		curSheet.addCell(new Number(colOffsetTbl3+4,offsetTbl3,2));
		curSheet.addCell(new Number(colOffsetTbl3+5,offsetTbl3,2));
		curSheet.addCell(new Number(colOffsetTbl3+6,offsetTbl3,100));
		offsetTbl3++;
		
		curSheet.addCell(new Label(colOffsetTbl3+1,offsetTbl3,"Number of Confs"));
		curSheet.addCell(new Label(colOffsetTbl3+2,offsetTbl3,"Number of Possible Configuration"));
		curSheet.addCell(new Label(colOffsetTbl3+3,offsetTbl3,"Average of Workflow Adaptation (Failed)"));
		curSheet.addCell(new Label(colOffsetTbl3+4,offsetTbl3,"Average of Workflow Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl3+5,offsetTbl3,"Average of Feature Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl3+6,offsetTbl3,"Average of Feature Adaptation (Failed)"));
		offsetTbl3++;
		
		
		curSheet.addCell(new Label(colOffsetTbl1+1,offsetTbl1,"Number of Possible Configuration"));
		curSheet.addCell(new Label(colOffsetTbl1+2,offsetTbl1,"Number of Service Lookups"));
		curSheet.addCell(new Label(colOffsetTbl1+3,offsetTbl1,"Average Service Lookup time"));
		curSheet.addCell(new Label(colOffsetTbl1+4,offsetTbl1,"Number of Workflow Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl1+5,offsetTbl1,"Average of Workflow Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl1+6,offsetTbl1,"Number of Feature Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl1+7,offsetTbl1,"Average of Feature Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl1+8,offsetTbl1,"Number of Workflow Adaptation (Failed)"));
		curSheet.addCell(new Label(colOffsetTbl1+9,offsetTbl1,"Average of Workflow Adaptation (Failed)"));
		curSheet.addCell(new Label(colOffsetTbl1+10,offsetTbl1,"Number of Feature Adaptation (Failed)"));
		curSheet.addCell(new Label(colOffsetTbl1+11,offsetTbl1,"Average of Feature Adaptation (Failed)"));
		curSheet.addCell(new Label(colOffsetTbl1+12,offsetTbl1,"Service Lookup"));
		curSheet.addCell(new Label(colOffsetTbl1+13,offsetTbl1,"Workflow Adaptation"));
		curSheet.addCell(new Label(colOffsetTbl1+14,offsetTbl1,"Feature Adaptation "));
		curSheet.addCell(new Label(colOffsetTbl1+15,offsetTbl1,"Adaptation Failure"));
		
		curSheet.addCell(new Label(colOffsetTbl2+1,offsetTbl2,"Number of Feature Change"));
		curSheet.addCell(new Label(colOffsetTbl2+2,offsetTbl2,"Occurences"));
		
		Map<Integer,Integer> featureChangeOccurenceMap = new HashMap<Integer, Integer>();
		
		offsetTbl1++;
		offsetTbl2++;
		
		offset++;
		int row = offset;
		
		for(int rcntr =0; rcntr<repositorySize.length; rcntr++){
			System.out.println(repositorySize[rcntr]);
			
			long cntServiceLookup=0;
			long sumServiceLookup=0;
			long avgServiceLookup=0;
			
			long serviceLookupCntr=0;
			
			long cntPlanningDurationSucc=0;
			long sumPlanningDurationSucc=0;
			long avgPlanningDurationSucc=0;
			
			long cntPlanningDurationFail=0;
			long sumPlanningDurationFail=0;
			long avgPlanningDurationFail=0;
			
			long cntFeatureAdaptationDurationSucc=0;
			long sumFeatureAdaptationDurationSucc=0;
			long avgFeatureAdaptationDurationSucc=0;
			
			long cntFeatureAdaptationDurationFail=0;
			long sumFeatureAdaptationDurationFail=0;
			long avgFeatureAdaptationDurationFail=0;
			
			int evalCntr=0;
			
			File[] smFamilyDirectories = new File(evaluationDirectory+String.valueOf(repositorySize[rcntr])).listFiles(new FileFilter() {
				
				public boolean accept(File pathname) {
					
					return pathname.isDirectory();
				}
			});
			
			int maxSMIterationNo = repositoryIterationSize/smFamilyDirectories.length;
			
			
			for(File curDir: smFamilyDirectories){
				
				DomainModels dm = DomainModels.readFromDirectory(curDir);
				ContextStateModel contextStateModel = new ContextStateModel(dm.getServiceCollection());
				List<FeatureModelConfiguration> fmcs = dm.getFeatureModel().getAllValidConfiguration(-1,fmcSize,fmcVariation);
				if(fmcs.size()==0)
					continue;
				
//				int lrow = row;
				
				for(int iterationCntr=0; iterationCntr<maxSMIterationNo; iterationCntr++ ){
					
					
					int featureModelConfigurationNo = UtilityClass.randInt(0, fmcs.size()-1);
					

					
					FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(dm, fmcs.get(featureModelConfigurationNo),contextStateModel);
					
					
					FlowComponentNode fcn =	fmcmg.buildServiceMashup();
					
					List<Service> mashupServices = fcn.findAllCalledServices();
					
					Service s = mashupServices.get(UtilityClass.randInt(0, mashupServices.size()-1));
					
					//System.out.println(  iterationCntr+"/" + maxSMIterationNo + " Rep: "+String.valueOf(repositorySize[rcntr])+ " SM: "+ curDir.getName() +" No Of Conf:" + featureModelConfigurationNo +" Services: "+ mashupServices.size()+" Service Name: "+ s.getName());
					
					//System.out.println("Failed service: "+s.getName());
					contextStateModel.getServiceAvailabilty().put(s.getURI(),false);
					AdaptationResult ar = dm.getFeatureModel().adapt(dm,fmcs.get(featureModelConfigurationNo),fcn, s,contextStateModel);
					contextStateModel.getServiceAvailabilty().put(s.getURI(),true);
					
					
					cntServiceLookup++;
					sumServiceLookup += ar.getServiceAdaptationTime();
					
					if(ar.getAdaptationType() > AdaptationResult.SERVICE_ADAPTATION){
						if(ar.getAdaptationType()> AdaptationResult.WORKFLOW_ADAPTATION){
							
							cntPlanningDurationFail++;
							sumPlanningDurationFail+=ar.getWorkflowAdaptationTime();
							
							if(ar.getAdaptationType()> AdaptationResult.FEATURE_ADAPTATION)
							{
								cntFeatureAdaptationDurationFail ++;
								sumFeatureAdaptationDurationFail += ar.getFeatureAdaptationTime();
							}
							else
							{
								cntFeatureAdaptationDurationSucc ++;
								sumFeatureAdaptationDurationSucc += ar.getFeatureAdaptationTime();
								Integer featureChange = dm.getFeatureModel().getDistance(fmcs.get(featureModelConfigurationNo),ar.getAlternateFeatureModelConfiguration());
								if(!featureChangeOccurenceMap.containsKey(featureChange))
									featureChangeOccurenceMap.put(featureChange, 0);
								featureChangeOccurenceMap.put(featureChange, featureChangeOccurenceMap.get(featureChange)+1);
							}
						}
						else{
							cntPlanningDurationSucc++;
							sumPlanningDurationSucc+=ar.getWorkflowAdaptationTime();
						}
					}
					else{
						serviceLookupCntr++;
					}
					
					
					//System.out.println("Adaptation type: "+ar.getAdaptationType());
					curSheet.addCell(new Label(colOffset+1,row,curDir.toString()));
					curSheet.addCell(new Label(colOffset+2,row, String.valueOf(featureModelConfigurationNo)));
					curSheet.addCell(new Label(colOffset+3,row,s.getName()));
					curSheet.addCell(new Number(colOffset+4,row,ar.getAdaptationType()));
					curSheet.addCell(new Number(colOffset+5,row,repositorySize[rcntr]));
					curSheet.addCell(new Number(colOffset+6,row,ar.getServiceAdaptationTime()));
					if(ar.getAdaptationType()!=AdaptationResult.SERVICE_ADAPTATION){
						curSheet.addCell(new Number(colOffset+7,row,ar.getWorkflowAdaptationTime()));
						if(ar.getAdaptationType()!=AdaptationResult.WORKFLOW_ADAPTATION){
							curSheet.addCell(new Number(colOffset+8,row,ar.getFeatureAdaptationTime()));
							if(ar.getAdaptationType()==AdaptationResult.FEATURE_ADAPTATION){
								curSheet.addCell(new Number(colOffset+9,row,dm.getFeatureModel().getDistance(fmcs.get(featureModelConfigurationNo),ar.getAlternateFeatureModelConfiguration())));
								curSheet.addCell(new Number(colOffset+10,row,ar.getNoOfTries()));
							}
						}					
					}
					
					row++;
					
					evalCntr++;
					
					
					
				}
				
//				String beginRow = String.valueOf(lrow);
//				String endRow = String.valueOf(row-1);
				
				curSheet.addCell(new Number(colOffsetTbl3+1,offsetTbl3,repositorySize[rcntr]));
				curSheet.addCell(new Label(colOffsetTbl3+2,offsetTbl3,curDir.toString()));				
//				curSheet.addCell(new Formula(colOffsetTbl3+3, offsetTbl3,"AVERAGE(H"+beginRow+":H"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+4, offsetTbl3,"AVERAGE(H"+beginRow+":H"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+5, offsetTbl3,"AVERAGE(I"+beginRow+":I"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+6, offsetTbl3,"AVERAGE(I"+beginRow+":I"+endRow+")"));
				
//				curSheet.addCell(new Label(colOffsetTbl3+3, offsetTbl3,"AVERAGEIFS(H"+beginRow+":H"+endRow+",E"+beginRow+":E"+endRow+",\"=3\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+4, offsetTbl3,"AVERAGEIFS(H"+beginRow+":H"+endRow+",E"+beginRow+":E"+endRow+",\"=2\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+5, offsetTbl3,"AVERAGEIFS(I"+beginRow+":I"+endRow+",E"+beginRow+":E"+endRow+",\"=3\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+6, offsetTbl3,"AVERAGEIFS(I"+beginRow+":I"+endRow+",E"+beginRow+":E"+endRow+",\"=100\")"));
//				
				
				offsetTbl3++;
				
				
			}
			
			if(cntServiceLookup>0)
				avgServiceLookup = sumServiceLookup/cntServiceLookup;
			
			if(cntPlanningDurationSucc>0)
				avgPlanningDurationSucc = sumPlanningDurationSucc/cntPlanningDurationSucc;
			
			if(cntFeatureAdaptationDurationSucc>0)
				avgFeatureAdaptationDurationSucc = sumFeatureAdaptationDurationSucc/cntFeatureAdaptationDurationSucc;
			
			if(cntPlanningDurationFail>0)
				avgPlanningDurationFail = sumPlanningDurationFail/cntPlanningDurationFail;
			
			if(cntFeatureAdaptationDurationFail>0)
				avgFeatureAdaptationDurationFail = sumFeatureAdaptationDurationFail/cntFeatureAdaptationDurationFail;
			
			float serviceLookupPercentage = ((float) serviceLookupCntr) /evalCntr;
			float workflowAdaptationPercentage = ((float) cntPlanningDurationSucc) /evalCntr;
			float featureAdaptationPercentage = ((float) cntFeatureAdaptationDurationSucc) /evalCntr;
			float adaptationFailPercentage = ((float) cntFeatureAdaptationDurationFail) /evalCntr;
			
			
			
			curSheet.addCell(new Label(colOffsetTbl1+1,offsetTbl1,String.valueOf(repositorySize[rcntr])));
			curSheet.addCell(new Label(colOffsetTbl1+2,offsetTbl1, String.valueOf(cntServiceLookup)));
			curSheet.addCell(new Label(colOffsetTbl1+3,offsetTbl1,String.valueOf(avgServiceLookup)));
			curSheet.addCell(new Label(colOffsetTbl1+4,offsetTbl1,String.valueOf(cntPlanningDurationSucc)));
			curSheet.addCell(new Label(colOffsetTbl1+5,offsetTbl1,String.valueOf(avgPlanningDurationSucc)));
			curSheet.addCell(new Label(colOffsetTbl1+6,offsetTbl1,String.valueOf(cntFeatureAdaptationDurationSucc)));
			curSheet.addCell(new Label(colOffsetTbl1+7,offsetTbl1,String.valueOf(avgFeatureAdaptationDurationSucc)));
			curSheet.addCell(new Label(colOffsetTbl1+8,offsetTbl1,String.valueOf(cntPlanningDurationFail)));
			curSheet.addCell(new Label(colOffsetTbl1+9,offsetTbl1,String.valueOf(avgPlanningDurationFail)));
			curSheet.addCell(new Label(colOffsetTbl1+10,offsetTbl1,String.valueOf(cntFeatureAdaptationDurationFail)));
			curSheet.addCell(new Label(colOffsetTbl1+11,offsetTbl1,String.valueOf(avgFeatureAdaptationDurationFail)));
			curSheet.addCell(new jxl.write.Number(colOffsetTbl1+12,offsetTbl1,serviceLookupPercentage));
			curSheet.addCell(new jxl.write.Number(colOffsetTbl1+13,offsetTbl1,workflowAdaptationPercentage));
			curSheet.addCell(new jxl.write.Number(colOffsetTbl1+14,offsetTbl1,featureAdaptationPercentage));
			curSheet.addCell(new jxl.write.Number(colOffsetTbl1+15,offsetTbl1,adaptationFailPercentage));
			offsetTbl1++;
		}
		
//		int cntr=0;
		for(Integer fno: featureChangeOccurenceMap.keySet()){
			curSheet.addCell(new Label(colOffsetTbl2+1,offsetTbl2,String.valueOf(fno)));
			curSheet.addCell(new Label(colOffsetTbl2+2,offsetTbl2, String.valueOf(featureChangeOccurenceMap.get(fno))));
			offsetTbl2++;
		}
		
		wb.write();
		wb.close();
		
	}
	
	
	
	public static void runEvaluationRobustness() throws Exception{
		WritableWorkbook wb = Workbook.createWorkbook(new File(homeAddress+ "evaluationRobust.xls"));
		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
		
		String evaluationDirectory ="/home/mbashari/EVAL_FOLDER/evaluation/fmsize/smfamily/";
		int[] repositorySize = {800};
		//int[] repositorySize = {400};
		int repositoryIterationSize = 250;
		int fmcSize = 15;
		int fmcVariation =1;
		
		int offset =0;
		int colOffset=0;
		
		int offsetTbl1 =5;
		int colOffsetTbl1=20;
		
		int offsetTbl2 =5;
		int colOffsetTbl2=40;
		
		int offsetTbl3 =5;
		int colOffsetTbl3=11;
		
		
		curSheet.addCell(new Label(colOffset+1,0,"Mashup Family Name"));
		curSheet.addCell(new Label(colOffset+2,0,"Configuration No"));
		curSheet.addCell(new Label(colOffset+3,0,"Failed Service"));
		curSheet.addCell(new Label(colOffset+4,0,"Adaptation type"));
		curSheet.addCell(new Label(colOffset+5,0,"Number of Confs"));
		curSheet.addCell(new Label(colOffset+6,0,"Service Replacement Time"));
		curSheet.addCell(new Label(colOffset+7,0,"Replanning Time"));
		curSheet.addCell(new Label(colOffset+8,0,"Feature model reconfiguration"));
		curSheet.addCell(new Label(colOffset+9,0,"Feature Distance"));
		curSheet.addCell(new Label(colOffset+10,0,"Number of tries"));
		
		
		
		curSheet.addCell(new Number(colOffsetTbl3+3,offsetTbl3,3));
		curSheet.addCell(new Number(colOffsetTbl3+4,offsetTbl3,2));
		curSheet.addCell(new Number(colOffsetTbl3+5,offsetTbl3,2));
		curSheet.addCell(new Number(colOffsetTbl3+6,offsetTbl3,100));
		offsetTbl3++;
		
		curSheet.addCell(new Label(colOffsetTbl3+1,offsetTbl3,"Number of Confs"));
		curSheet.addCell(new Label(colOffsetTbl3+2,offsetTbl3,"Number of Possible Configuration"));
		curSheet.addCell(new Label(colOffsetTbl3+3,offsetTbl3,"Number of Failed Services"));
		curSheet.addCell(new Label(colOffsetTbl3+4,offsetTbl3,"Average of Workflow Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl3+5,offsetTbl3,"Average of Feature Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl3+6,offsetTbl3,"Average of Feature Adaptation (Failed)"));
		offsetTbl3++;
		
		
		curSheet.addCell(new Label(colOffsetTbl1+1,offsetTbl1,"Number of Possible Configuration"));
		curSheet.addCell(new Label(colOffsetTbl1+2,offsetTbl1,"Number of Service Lookups"));
		curSheet.addCell(new Label(colOffsetTbl1+3,offsetTbl1,"Average Service Lookup time"));
		curSheet.addCell(new Label(colOffsetTbl1+4,offsetTbl1,"Number of Workflow Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl1+5,offsetTbl1,"Average of Workflow Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl1+6,offsetTbl1,"Number of Feature Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl1+7,offsetTbl1,"Average of Feature Adaptation (Successful)"));
		curSheet.addCell(new Label(colOffsetTbl1+8,offsetTbl1,"Number of Workflow Adaptation (Failed)"));
		curSheet.addCell(new Label(colOffsetTbl1+9,offsetTbl1,"Average of Workflow Adaptation (Failed)"));
		curSheet.addCell(new Label(colOffsetTbl1+10,offsetTbl1,"Number of Feature Adaptation (Failed)"));
		curSheet.addCell(new Label(colOffsetTbl1+11,offsetTbl1,"Average of Feature Adaptation (Failed)"));
		curSheet.addCell(new Label(colOffsetTbl1+12,offsetTbl1,"Service Lookup"));
		curSheet.addCell(new Label(colOffsetTbl1+13,offsetTbl1,"Workflow Adaptation"));
		curSheet.addCell(new Label(colOffsetTbl1+14,offsetTbl1,"Feature Adaptation "));
		curSheet.addCell(new Label(colOffsetTbl1+15,offsetTbl1,"Adaptation Failure"));
		
		curSheet.addCell(new Label(colOffsetTbl2+1,offsetTbl2,"Number of Feature Change"));
		curSheet.addCell(new Label(colOffsetTbl2+2,offsetTbl2,"Occurences"));
		
		Map<Integer,Integer> featureChangeOccurenceMap = new HashMap<Integer, Integer>();
		
		offsetTbl1++;
		offsetTbl2++;
		
		offset++;
		int row = offset;
		
		for(int fcntr=1; fcntr<5; fcntr++){
		
			for(int rcntr =0; rcntr<repositorySize.length; rcntr++){
				System.out.println(repositorySize[rcntr]);
				
				long cntServiceLookup=0;
				long sumServiceLookup=0;
				long avgServiceLookup=0;
				
				long serviceLookupCntr=0;
				
				long cntPlanningDurationSucc=0;
				long sumPlanningDurationSucc=0;
				long avgPlanningDurationSucc=0;
				
				long cntPlanningDurationFail=0;
				long sumPlanningDurationFail=0;
				long avgPlanningDurationFail=0;
				
				long cntFeatureAdaptationDurationSucc=0;
				long sumFeatureAdaptationDurationSucc=0;
				long avgFeatureAdaptationDurationSucc=0;
				
				long cntFeatureAdaptationDurationFail=0;
				long sumFeatureAdaptationDurationFail=0;
				long avgFeatureAdaptationDurationFail=0;
				
				int evalCntr=0;
				
				File[] smFamilyDirectories = new File(evaluationDirectory+String.valueOf(repositorySize[rcntr])).listFiles(new FileFilter() {
					
					public boolean accept(File pathname) {
						
						return pathname.isDirectory();
					}
				});
				
				int maxSMIterationNo = repositoryIterationSize/smFamilyDirectories.length;
				
				
				for(File curDir: smFamilyDirectories){
					
					DomainModels dm = DomainModels.readFromDirectory(curDir);
					ContextStateModel contextStateModel = new ContextStateModel(dm.getServiceCollection());
					List<FeatureModelConfiguration> fmcs = dm.getFeatureModel().getAllValidConfiguration(-1,fmcSize,fmcVariation);
					if(fmcs.size()==0)
						continue;
					
//					int lrow = row;
					
					for(int iterationCntr=0; iterationCntr<maxSMIterationNo; iterationCntr++ ){
						
						
						int featureModelConfigurationNo = UtilityClass.randInt(0, fmcs.size()-1);
						
	
						
						FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(dm, fmcs.get(featureModelConfigurationNo),contextStateModel);
						
						
						FlowComponentNode fcn =	fmcmg.buildServiceMashup();
						
						List<Service> mashupServices = fcn.findAllCalledServices();
						
						List<Service> failedServices = new ArrayList<Service>();
						
						Set<Integer> failedServiceIndices= UtilityClass.randIntSet(mashupServices.size()-1,(int) Math.min(fcntr, mashupServices.size()));
						
						for(int indx : failedServiceIndices)
							failedServices.add(mashupServices.get(indx));
						
						
						//System.out.println(  iterationCntr+"/" + maxSMIterationNo + " Rep: "+String.valueOf(repositorySize[rcntr])+ " SM: "+ curDir.getName() +" No Of Conf:" + featureModelConfigurationNo +" Services: "+ mashupServices.size()+" Service Name: "+ s.getName());
						
						//System.out.println("Failed service: "+s.getName());
						for(Service s: failedServices)
							contextStateModel.getServiceAvailabilty().put(s.getURI(),false);
						AdaptationResult ar = dm.getFeatureModel().adapt(dm,fmcs.get(featureModelConfigurationNo),fcn, null,contextStateModel);
						for(Service s: failedServices)
							contextStateModel.getServiceAvailabilty().put(s.getURI(),true);
						
						
						cntServiceLookup++;
						sumServiceLookup += ar.getServiceAdaptationTime();
						
						if(ar.getAdaptationType() > AdaptationResult.SERVICE_ADAPTATION){
							if(ar.getAdaptationType()> AdaptationResult.WORKFLOW_ADAPTATION){
								
								cntPlanningDurationFail++;
								sumPlanningDurationFail+=ar.getWorkflowAdaptationTime();
								
								if(ar.getAdaptationType()> AdaptationResult.FEATURE_ADAPTATION)
								{
									cntFeatureAdaptationDurationFail ++;
									sumFeatureAdaptationDurationFail += ar.getFeatureAdaptationTime();
								}
								else
								{
									cntFeatureAdaptationDurationSucc ++;
									sumFeatureAdaptationDurationSucc += ar.getFeatureAdaptationTime();
									Integer featureChange = dm.getFeatureModel().getDistance(fmcs.get(featureModelConfigurationNo),ar.getAlternateFeatureModelConfiguration());
									if(!featureChangeOccurenceMap.containsKey(featureChange))
										featureChangeOccurenceMap.put(featureChange, 0);
									featureChangeOccurenceMap.put(featureChange, featureChangeOccurenceMap.get(featureChange)+1);
								}
							}
							else{
								cntPlanningDurationSucc++;
								sumPlanningDurationSucc+=ar.getWorkflowAdaptationTime();
							}
						}
						else{
							serviceLookupCntr++;
						}
						
						
						//System.out.println("Adaptation type: "+ar.getAdaptationType());
						curSheet.addCell(new Label(colOffset+1,row,curDir.toString()));
						curSheet.addCell(new Label(colOffset+2,row, String.valueOf(featureModelConfigurationNo)));
						curSheet.addCell(new Number(colOffset+3,row,fcntr));
						curSheet.addCell(new Number(colOffset+4,row,ar.getAdaptationType()));
						curSheet.addCell(new Number(colOffset+5,row,repositorySize[rcntr]));
						curSheet.addCell(new Number(colOffset+6,row,ar.getServiceAdaptationTime()));
						if(ar.getAdaptationType()!=AdaptationResult.SERVICE_ADAPTATION){
							curSheet.addCell(new Number(colOffset+7,row,ar.getWorkflowAdaptationTime()));
							if(ar.getAdaptationType()!=AdaptationResult.WORKFLOW_ADAPTATION){
								curSheet.addCell(new Number(colOffset+8,row,ar.getFeatureAdaptationTime()));
								if(ar.getAdaptationType()==AdaptationResult.FEATURE_ADAPTATION){
									curSheet.addCell(new Number(colOffset+9,row,dm.getFeatureModel().getDistance(fmcs.get(featureModelConfigurationNo),ar.getAlternateFeatureModelConfiguration())));
									curSheet.addCell(new Number(colOffset+10,row,ar.getNoOfTries()));
								}
							}					
						}
						
						row++;
						
						evalCntr++;
						
						
						
					}
					
					
					
					curSheet.addCell(new Number(colOffsetTbl3+1,offsetTbl3,repositorySize[rcntr]));
					curSheet.addCell(new Label(colOffsetTbl3+2,offsetTbl3,curDir.toString()));
					curSheet.addCell(new Number(colOffsetTbl3+3,offsetTbl3,fcntr));
	//				curSheet.addCell(new Formula(colOffsetTbl3+3, offsetTbl3,"AVERAGE(H"+beginRow+":H"+endRow+")"));
	//				curSheet.addCell(new Formula(colOffsetTbl3+4, offsetTbl3,"AVERAGE(H"+beginRow+":H"+endRow+")"));
	//				curSheet.addCell(new Formula(colOffsetTbl3+5, offsetTbl3,"AVERAGE(I"+beginRow+":I"+endRow+")"));
	//				curSheet.addCell(new Formula(colOffsetTbl3+6, offsetTbl3,"AVERAGE(I"+beginRow+":I"+endRow+")"));
					
	//				curSheet.addCell(new Label(colOffsetTbl3+3, offsetTbl3,"AVERAGEIFS(H"+beginRow+":H"+endRow+",E"+beginRow+":E"+endRow+",\"=3\")"));
	//				curSheet.addCell(new Label(colOffsetTbl3+4, offsetTbl3,"AVERAGEIFS(H"+beginRow+":H"+endRow+",E"+beginRow+":E"+endRow+",\"=2\")"));
	//				curSheet.addCell(new Label(colOffsetTbl3+5, offsetTbl3,"AVERAGEIFS(I"+beginRow+":I"+endRow+",E"+beginRow+":E"+endRow+",\"=3\")"));
	//				curSheet.addCell(new Label(colOffsetTbl3+6, offsetTbl3,"AVERAGEIFS(I"+beginRow+":I"+endRow+",E"+beginRow+":E"+endRow+",\"=100\")"));
	//				
					
					offsetTbl3++;
					
					
				}
				
				if(cntServiceLookup>0)
					avgServiceLookup = sumServiceLookup/cntServiceLookup;
				
				if(cntPlanningDurationSucc>0)
					avgPlanningDurationSucc = sumPlanningDurationSucc/cntPlanningDurationSucc;
				
				if(cntFeatureAdaptationDurationSucc>0)
					avgFeatureAdaptationDurationSucc = sumFeatureAdaptationDurationSucc/cntFeatureAdaptationDurationSucc;
				
				if(cntPlanningDurationFail>0)
					avgPlanningDurationFail = sumPlanningDurationFail/cntPlanningDurationFail;
				
				if(cntFeatureAdaptationDurationFail>0)
					avgFeatureAdaptationDurationFail = sumFeatureAdaptationDurationFail/cntFeatureAdaptationDurationFail;
				
				float serviceLookupPercentage = ((float) serviceLookupCntr) /evalCntr;
				float workflowAdaptationPercentage = ((float) cntPlanningDurationSucc) /evalCntr;
				float featureAdaptationPercentage = ((float) cntFeatureAdaptationDurationSucc) /evalCntr;
				float adaptationFailPercentage = ((float) cntFeatureAdaptationDurationFail) /evalCntr;
				
				
				
				curSheet.addCell(new Label(colOffsetTbl1+1,offsetTbl1,String.valueOf(repositorySize[rcntr])));
				curSheet.addCell(new Label(colOffsetTbl1+2,offsetTbl1, String.valueOf(cntServiceLookup)));
				curSheet.addCell(new Label(colOffsetTbl1+3,offsetTbl1,String.valueOf(avgServiceLookup)));
				curSheet.addCell(new Label(colOffsetTbl1+4,offsetTbl1,String.valueOf(cntPlanningDurationSucc)));
				curSheet.addCell(new Label(colOffsetTbl1+5,offsetTbl1,String.valueOf(avgPlanningDurationSucc)));
				curSheet.addCell(new Label(colOffsetTbl1+6,offsetTbl1,String.valueOf(cntFeatureAdaptationDurationSucc)));
				curSheet.addCell(new Label(colOffsetTbl1+7,offsetTbl1,String.valueOf(avgFeatureAdaptationDurationSucc)));
				curSheet.addCell(new Label(colOffsetTbl1+8,offsetTbl1,String.valueOf(cntPlanningDurationFail)));
				curSheet.addCell(new Label(colOffsetTbl1+9,offsetTbl1,String.valueOf(avgPlanningDurationFail)));
				curSheet.addCell(new Label(colOffsetTbl1+10,offsetTbl1,String.valueOf(cntFeatureAdaptationDurationFail)));
				curSheet.addCell(new Label(colOffsetTbl1+11,offsetTbl1,String.valueOf(avgFeatureAdaptationDurationFail)));
				curSheet.addCell(new jxl.write.Number(colOffsetTbl1+12,offsetTbl1,serviceLookupPercentage));
				curSheet.addCell(new jxl.write.Number(colOffsetTbl1+13,offsetTbl1,workflowAdaptationPercentage));
				curSheet.addCell(new jxl.write.Number(colOffsetTbl1+14,offsetTbl1,featureAdaptationPercentage));
				curSheet.addCell(new jxl.write.Number(colOffsetTbl1+15,offsetTbl1,adaptationFailPercentage));
				offsetTbl1++;
			}
			
		}
		
		wb.write();
		wb.close();
		
	}
	
	
	public static void testFMs() throws Exception{
		
		
		String evaluationDirectory ="/home/mbashari/EVAL_FOLDER/evaluation/fmsize/smfamily/";
		//int[] repositorySize = {400,800,1200,1600,2000};
		int[] repositorySize = {400};
		
		
		
		
		
		
		for(int rcntr =0; rcntr<repositorySize.length; rcntr++){
			
			
			File[] smFamilyDirectories = new File[1];
			
			smFamilyDirectories[0] = new File( evaluationDirectory+String.valueOf(repositorySize[rcntr])+"/536/");
			
			for(File curDir: smFamilyDirectories){
				int failedConfNo = 0;
				DomainModels dm = DomainModels.readFromDirectory(curDir);
				List<FeatureModelConfiguration> fmcs = dm.getFeatureModel().getAllValidConfiguration(-1);
				
				for(int fmcCntr=0; fmcCntr<fmcs.size(); fmcCntr++ ){
					
					
					
					FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(dm, fmcs.get(fmcCntr));
					
					try{
					
						fmcmg.buildServiceMashup();
					
					}
					catch(UnsuccessfulMashupGeneration ex){
						failedConfNo++;
					}
					
					System.out.println("SM: "+curDir.getName() +" Conf: "+fmcCntr+"/"+fmcs.size() + " Failed confs: "+failedConfNo );
					
					
				}
				
			}
			
			
		}
		
	}
	
	@SuppressWarnings("unused")
	private static void createConfigurationFileForSMFamilies() throws IOException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		
		String rootAddress = "/home/mbashari/EVAL_FOLDER/evaluation/fmsize/smfamily";
		
		
		File[] smMainDirectories = new File(rootAddress).listFiles(new FileFilter() {
			
			public boolean accept(File pathname) {
				
				return pathname.isDirectory();
			}
		});
		
		for(File f: smMainDirectories){
			File[] smDirectories = f.listFiles(new FileFilter() {
				
				public boolean accept(File pathname) {
					
					return pathname.isDirectory();
				}
			});
			for(File f2: smDirectories){
				String servicesDir = f2.getAbsolutePath();
				if(servicesDir.endsWith("/"))
				{
					servicesDir = servicesDir+"services";
				}
				else{
					servicesDir = servicesDir+"/services";
				}
				List<String> serviceAddresses = new ArrayList<String>();
				File sv = new File(servicesDir);
				for(File f3:sv.listFiles())
					serviceAddresses.add("services/"+f3.getName());
				
				List<String> contextModelAddresses = new ArrayList<String>();
				contextModelAddresses.add("contextModel.xml");
				
				DomainModelConfiguration dmc  = new DomainModelConfiguration(contextModelAddresses, "fm.xml", serviceAddresses);
				
				String inp = f2.getAbsolutePath();
				if(!inp.endsWith("/"))
					inp = inp+"/";
				inp = inp+"configuration.xml";
				
				UtilityClass.writeFile(new File(inp), dmc.serializeToConfigurationFileXml());
				
					
				
			}
			
			
		}
		
	}
	@SuppressWarnings("unused")
	private static void createEvaluationDirs() throws IOException{
		
		
		int[] sizes=  {400,800,1200,1600,2000};
		
		String curFMDir = "/home/mbashari/EVAL_FOLDER/evaluation/fmsize/featureModelOnly/";
		String outputDir = "/home/mbashari/fmeval/";
		File sampleDir = new File("/home/mbashari/fmeval/sampleFolder");
		
		for(int scntr =0; scntr<sizes.length; scntr++){
			String curFMSizeOutputDir = outputDir+"evaluation"+String.valueOf(sizes[scntr])+"/" ;
			(new File(curFMSizeOutputDir)).mkdirs();
						
			File curFMDirFile = new File(curFMDir+String.valueOf(sizes[scntr]));
			
			int fcntr=1;
			for(File f: curFMDirFile.listFiles()){
				String curFMFileAdd = curFMSizeOutputDir+"evalpackagefm"+String.valueOf(fcntr);
				
				FileUtils.copyDirectory(sampleDir,new File(curFMFileAdd));
				
				UtilityClass.writeFile(new File(curFMFileAdd+"/rconf.txt"), String.valueOf(sizes[scntr]));
				
				Files.copy(f.toPath(), (new File(curFMFileAdd+"/fm.xml")).toPath());
				
				
				
				
				
				fcntr++;
			}
		}
		
		
	}
	@SuppressWarnings("unused")
	private static void createDatasetHtml() throws IOException{
		
		String baseAddress = "http://magus.online/experiments/1/";
		StringBuilder sb = new StringBuilder();
		
		sb.append("<!doctype html>");
		sb.append(System.lineSeparator());
		
		sb.append("<html lang=\"en\">");
		sb.append(System.lineSeparator());
		
		sb.append("<head>");
		sb.append(System.lineSeparator());
		
		sb.append("<meta charset=\"utf-8\">");
		sb.append(System.lineSeparator());
		
		sb.append("<title>Experiment Dataset</title>");
		sb.append(System.lineSeparator());
		
		
		sb.append("<meta name=\"description\" content=\"Experiment Dataset\">");
		sb.append(System.lineSeparator());
		
		sb.append("<meta name=\"author\" content=\"Mahdi Bashari\">");
		sb.append(System.lineSeparator());
		
		sb.append("</head>");
		sb.append(System.lineSeparator());
		
		sb.append("<body>");
		sb.append(System.lineSeparator());
		
		
		
		sb.append("<h1>Experiments Dataset</h1>&nbsp; <a href=\""+baseAddress+"dataset.tar.gz\" > Download All</a><br/>");
		sb.append(System.lineSeparator());
		sb.append("<h2>Experiment 1 </h2><br/>");
		sb.append(System.lineSeparator());
		sb.append("<ul>");
		sb.append(System.lineSeparator());
		
		String[] ds = {"400","800","1200","1600","2000"};
		
		for(String str : ds){
			sb.append("<li>");
			sb.append(System.lineSeparator());
			
			sb.append("Service Mashups with Feature model with "+str +" number of possible configurations  &nbsp;<a href=\""+baseAddress+"fmcsizes/"+str+".tar.gz\" >Download All</a>");
			sb.append("<ul>");
			sb.append(System.lineSeparator());
			
			for(int cntr =1 ;cntr<11;cntr++){
				sb.append("<li>");
				sb.append(System.lineSeparator());
				
				sb.append("Service Mashups Family "+cntr +" &nbsp; <a href=\"http://magus.online/?conf="+baseAddress+"fmcsizes/"+str+"/sm"+cntr+"/configuration.xml\" >Open in magus.online</a>");
				
				sb.append("</li>");
				sb.append(System.lineSeparator());
				
			}
			
			sb.append("</ul>");
			sb.append(System.lineSeparator());
			
			sb.append("</li>");
			sb.append(System.lineSeparator());
		}
 		
		sb.append("</ul>");
		sb.append(System.lineSeparator());
		sb.append("<h2>Experiment 2 </h2>");
		sb.append(System.lineSeparator());
		
		
		  

		  
		  
		String[]  ds2 = {"100","150","200","250","300","350","400"};
		
		sb.append("<ul>");
		sb.append(System.lineSeparator());
		
		for(String str : ds2){
			sb.append("<li>");
			sb.append(System.lineSeparator());
			
			sb.append("Service Mashups Family with "+str +" services  &nbsp;<a href=\""+baseAddress+"svssizes/"+str+".tar.gz\" >Download</a>&nbsp; <a href=\"http://magus.online/?conf="+baseAddress+"svssizes/"+str+"/configuration.xml\" >Open in magus.online</a><br/>");			
			
			sb.append("</li>");
			sb.append(System.lineSeparator());
		}

		sb.append("</ul>");
		sb.append(System.lineSeparator());
		

		
		sb.append("</body>");
		sb.append(System.lineSeparator());
		
		sb.append("</html>");
		sb.append(System.lineSeparator());
		
		UtilityClass.writeFile(new File("/home/mbashari/test.html"), sb.toString());
	}

	
	@SuppressWarnings("unused")
	private static void findAllAtomicSetsForSample() throws Exception{
		//String fmStr =UtilityClass.readFile("/home/mbashari/featuremodel.xml");
		String fmStr =UtilityClass.readFile("/home/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/uploadimage/featureModel.xml");
		//String fmStr =UtilityClass.readFile("/home/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/orderprocessing/featureModel.xml");
		//String fmStr =UtilityClass.readFile("/home/mbashari/serializedFM3.txt");
		String cmStr= UtilityClass.readFile("/home/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/uploadimage/contextModel.xml");
		//String cmStr= UtilityClass.readFile("/home/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/orderprocessing/orderprocessing.xml");
		
		
		ContextModel cm = new ContextModel();
		cm.AddToContextModel(cmStr);
		FeatureAnnotationSet fma = new FeatureAnnotationSet();
		
		FeatureModel fm = FeatureModel.parse(fmStr, fma, cm);
		
		FeatureAtomicSetMap fasm = fm.findAtomicSets();
		
		for(Feature f: fasm.getFasMap().keySet())
		{
			System.out.print(f.toString());
			System.out.print(":");
			System.out.println(fasm.getFasMap().get(f).getFeatureList().toString());
			System.out.println();
		}
		
		
	}
	
	public static void testRandomFeatureGeneration() throws Exception{
		DomainModels dm = DomainModels.readFromDirectory(new File( "/home/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/uploadimage/"));
		FeatureModelConfiguration fmc = dm.getFeatureModel().getARandomConfiguration();
		System.out.println(fmc);
	}
	
	
	public static void testFeatureContributionEstimation() throws Exception{
		
		DomainModels dm = DomainModels.readFromDirectory(new File( "/home/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/uploadimage/"));
		
		
		Map<String, Double> serviceResponseTimes = new HashMap<String, Double>();
		
		serviceResponseTimes.put("ObjectDetection", 500d);
		serviceResponseTimes.put("TextExtraction", 400d);
		serviceResponseTimes.put("FilterObjects", 100d);
		serviceResponseTimes.put("BlurObjects", 200d);
		serviceResponseTimes.put("DetectTextProfanity", 150d);
		serviceResponseTimes.put("DetectNudity", 600d);
		serviceResponseTimes.put("UploadImgUr", 200d);
		serviceResponseTimes.put("UploadTinyPic", 200d);
		serviceResponseTimes.put("GenerateTagExternal", 500d);
		serviceResponseTimes.put("GenerateTagMetadata", 100d);
		serviceResponseTimes.put("DetectProfanity", 550d);
		serviceResponseTimes.put("WatermarkImage", 250d);
		
		ServiceNonfunctionalAnnotationMap annotationMap = new ServiceNonfunctionalAnnotationMap(dm.getServiceCollection().getServices());
		
		
		annotationMap.generateAnnotationFromMap(serviceResponseTimes,ExecutionTimeType.getInstance());
		

		//ExecutionTime.GenerateExecutionTime(annotationMap, 200, 50, 50, 30);
		
		
		
		FeatureAtomicSetMap fasm= dm.getFeatureModel().findAtomicSets();
		
		List<FeatureModelConfiguration> adequateTrainingList = dm.getFeatureModel().generateRegressionConfigurations(1, fasm);
		
		System.out.println("Number of training set size:"+ adequateTrainingList.size());
		
		
		List<AtomicSet> asl =  fasm.getAllAtomicSets(false);
		
		for(AtomicSet as:asl)
			System.out.println(as.getFeatureList());
		
		
		
		Map<AtomicSet, Double> cv = new HashMap<AtomicSet, Double>();
		
		dm.findAtomicSetContributionValueOLS(fasm, annotationMap, ExecutionTimeType.getInstance(), adequateTrainingList, new HashMap<FeatureModelConfiguration, FlowComponentNode>(),cv);
		
		for(AtomicSet as: cv.keySet()){
			System.out.println(as.getFeatureList()+" : "+cv.get(as));
		}
	}
	
public static void testFeatureContributionEstimation2() throws Exception{
		
		DomainModels dm = DomainModels.readFromDirectory(new File( "/home/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/uploadimage/"));
		
		
		
		
		Map<String, Double> serviceReliability = new HashMap<String, Double>();
		
		serviceReliability.put("ObjectDetection", 0.991d);
		serviceReliability.put("TextExtraction", 0.985d);
		serviceReliability.put("FilterObjects", 0.998d);
		serviceReliability.put("BlurObjects", 0.993d);
		serviceReliability.put("DetectTextProfanity", 0.99d);
		serviceReliability.put("DetectNudity", 0.991d);
		serviceReliability.put("UploadImgUr", 0.990d);
		serviceReliability.put("UploadTinyPic", 0.983d);
		serviceReliability.put("GenerateTagExternal", 0.992d);
		serviceReliability.put("GenerateTagMetadata", 0.981d);
		serviceReliability.put("DetectProfanity", 0.989d);
		serviceReliability.put("WatermarkImage", 0.997d);
		
		ServiceNonfunctionalAnnotationMap annotationMap = new ServiceNonfunctionalAnnotationMap(dm.getServiceCollection().getServices());
		
		
		annotationMap.generateAnnotationFromMap(serviceReliability,ReliabilityType.getInstance());
		
		
		
		
		//ExecutionTime.GenerateExecutionTime(annotationMap, 200, 50, 50, 30);
		
		System.out.println("Number of possible configurations:"+ dm.getFeatureModel().getAllValidConfiguration(-1).size());
		
		FeatureAtomicSetMap fasm= dm.getFeatureModel().findAtomicSets();
		
		List<FeatureModelConfiguration> adequateTrainingList = dm.getFeatureModel().generateRegressionConfigurations(1, fasm);
		
		System.out.println("Number of training set size:"+ adequateTrainingList.size());
		
		List<AtomicSet> asl =  fasm.getAllAtomicSets(false);
		
		int numberOfAtomicSet = asl.size();
		
		for(AtomicSet as:asl)
			System.out.println(as.getFeatureList());
		
		Map<AtomicSet, Double> cv = new HashMap<AtomicSet, Double>();
				
		dm.findAtomicSetContributionValueOLS(fasm, annotationMap, ReliabilityType.getInstance(), adequateTrainingList, new HashMap<FeatureModelConfiguration, FlowComponentNode>(),cv);
				
		
		for(AtomicSet as: cv.keySet()){
			System.out.println(as.getFeatureList()+" : "+cv.get(as));
		}
		
		List<Feature> flc = new ArrayList<Feature>();
		
		String[] featureNames = {"Upload Image","Storage","Editting","Face Blur","Tagging","Metadata - based","Filtering","Profanity"}; 
		
		Feature taggingFeature = dm.getFeatureModel().findFeatureByName("Tagging");
		
		for(String fn : featureNames){
			flc.add(dm.getFeatureModel().findFeatureByName(fn));
			
		}
		
		FeatureModelConfiguration smfmc = new FeatureModelConfiguration(flc);
		smfmc.getCriticalFeatureSet().add(taggingFeature);
		
		double estimatedReliability = smfmc.estimateNonfunctionalValue(fasm, cv, ReliabilityType.getInstance());
		
		System.out.println("Estimated Reliability for Feature Model Configuration : " +estimatedReliability);
		
		FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(dm, smfmc);
		
		FlowComponentNode smfcn =  fmcmg.buildServiceMashup();
		
		double actualReliability = ReliabilityType.getInstance().getAggregatedValue(annotationMap.getAnnotationMap(), smfcn);
		
		System.out.println("Actual Reliability for Feature Model Configuration : " +actualReliability);
		
		System.out.println("Reduction in Object dectection reliability");
		
		
		serviceReliability.put("ObjectDetection", 0.941d);
		
		annotationMap = new ServiceNonfunctionalAnnotationMap(dm.getServiceCollection().getServices());
			
			
		annotationMap.generateAnnotationFromMap(serviceReliability,ReliabilityType.getInstance());
		
		actualReliability = ReliabilityType.getInstance().getAggregatedValue(annotationMap.getAnnotationMap(), smfcn);
		
		System.out.println("Reduced Reliability for Feature Model Configuration : " +actualReliability);
		
		cv = new HashMap<AtomicSet, Double>();
		
		dm.findAtomicSetContributionValueOLS(fasm, annotationMap, ReliabilityType.getInstance(), adequateTrainingList, new HashMap<FeatureModelConfiguration, FlowComponentNode>(),cv);
				
		
		for(AtomicSet as: cv.keySet()){
			System.out.println(as.getFeatureList()+" : "+cv.get(as));
		}
		
		estimatedReliability = smfmc.estimateNonfunctionalValue(fasm, cv, ReliabilityType.getInstance());
		
		System.out.println("Reduced Estimated Reliability for Feature Model Configuration : " +estimatedReliability);
		
		
		ContextStateModel contextStateModelMain = new ContextStateModel(dm.getServiceCollection(), annotationMap);
		
		NonfunctionalConstraint nfc  = new NonfunctionalConstraint(ReliabilityType.getInstance(), 0.9d, false);
		
		List<NonfunctionalConstraint> nfcList = new ArrayList<NonfunctionalConstraint>();
		nfcList.add(nfc);
		
		Map<FeatureModelConfiguration,FlowComponentNode> trainingListMap = new HashMap<FeatureModelConfiguration, FlowComponentNode>();
		
		
		for(FeatureModelConfiguration fmc: adequateTrainingList){
			fmcmg = new FeatureModelConfigurationMashupGeneration(dm, fmc, contextStateModelMain);
			try{
			FlowComponentNode fcn =	fmcmg.buildServiceMashup();
			trainingListMap.put(fmc, fcn);
			}
			catch(UnsuccessfulMashupGeneration ex){
				System.out.println("UNSUCCESSFUL MASHUP GENERATION!");
			}
			
			
		}
		
		long curTime = System.currentTimeMillis();
		Holder<Integer> holder = new Holder<Integer>(0);				
		FeatureModelConfiguration alternateFMC =  smfmc.findAlternateConfigurationNF(dm,contextStateModelMain,nfcList,trainingListMap,fasm, holder);
		
		System.out.println(alternateFMC);
		
		estimatedReliability = alternateFMC.estimateNonfunctionalValue(fasm, cv, ReliabilityType.getInstance());
		
		System.out.println("Estimated Reliability for Feature Model Configuration After Adaptation: " +estimatedReliability);
		
		
		fmcmg = new FeatureModelConfigurationMashupGeneration(dm, alternateFMC, contextStateModelMain);
		
		smfcn =	fmcmg.buildServiceMashup();
		
		
		actualReliability = ReliabilityType.getInstance().getAggregatedValue(annotationMap.getAnnotationMap(), smfcn);
		
		System.out.println("Actual Reliability for Feature Model Configuration After Adaptation: " +actualReliability);
		
		
		
		
		
		
		
	}
	

public static void testFeatureContributionEstimation3() throws Exception{
	
	DomainModels dm = DomainModels.readFromDirectory(new File( "/home/mbashari/Dropbox/Thesis/impl/magus/composer/src/main/webapp/repositories/uploadimage/"));
	
	
	
	
	Map<String, Double> serviceReliability = new HashMap<String, Double>();
	
	serviceReliability.put("ObjectDetection", 0.991d);
	serviceReliability.put("TextExtraction", 0.985d);
	serviceReliability.put("FilterObjects", 0.998d);
	serviceReliability.put("BlurObjects", 0.993d);
	serviceReliability.put("DetectTextProfanity", 0.99d);
	serviceReliability.put("DetectNudity", 0.991d);
	serviceReliability.put("UploadImgUr", 0.990d);
	serviceReliability.put("UploadTinyPic", 0.983d);
	serviceReliability.put("GenerateTagExternal", 0.992d);
	serviceReliability.put("GenerateTagMetadata", 0.981d);
	serviceReliability.put("DetectProfanity", 0.989d);
	serviceReliability.put("WatermarkImage", 0.997d);
	
	ServiceNonfunctionalAnnotationMap annotationMap = new ServiceNonfunctionalAnnotationMap(dm.getServiceCollection().getServices());
	
	
	annotationMap.generateAnnotationFromMap(serviceReliability,ReliabilityType.getInstance());
	
	
	
	List<String[]> fms = new ArrayList<String[]>();
	
	fms.add(new String[] {"Upload Image", "Storage", "Tagging", "External", "Filtering", "Profanity", "Editting", "Watermark", "Face Blur"});
	fms.add(new String[] {"Upload Image", "Storage", "Tagging", "External"});
	fms.add(new String[] {"Upload Image", "Storage", "Editting", "Watermark", "Filtering", "Nudity", "Profanity"});
	fms.add(new String[] {"Upload Image", "Storage", "Editting", "Watermark", "Filtering", "Nudity"});
	fms.add(new String[] {"Upload Image", "Storage", "Tagging", "Metadata - based", "Editting", "Face Blur"});
	fms.add(new String[] {"Upload Image", "Storage", "Filtering", "Profanity", "Tagging", "External"});
	fms.add(new String[] {"Upload Image", "Storage", "Tagging", "External", "Filtering", "Nudity", "Editting", "Watermark", "Face Blur"});
	fms.add(new String[] {"Upload Image", "Storage", "Filtering", "Nudity"});
	fms.add(new String[] {"Upload Image", "Storage", "Tagging", "Metadata - based", "Editting", "Watermark", "Face Blur"});
	fms.add(new String[] {"Upload Image", "Storage", "Filtering", "Nudity", "Profanity"});
	fms.add(new String[] {"Upload Image", "Storage", "Tagging", "External", "Filtering", "Profanity", "Editting", "Face Blur"});
	
	
	List<FeatureModelConfiguration> fmlist = new ArrayList<FeatureModelConfiguration>();
	
	for(String[] fmc: fms)
		fmlist.add(FeatureModelConfiguration.getFeatureModelConfigurationByFeatureNames(fmc,dm.getFeatureModel()));
	
	ContextStateModel contextStateModelMain = new ContextStateModel(dm.getServiceCollection(), annotationMap);
	
	for(FeatureModelConfiguration fmc: fmlist){
		FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(dm, fmc, contextStateModelMain);
		try{
		FlowComponentNode fcn =	fmcmg.buildServiceMashup();
		
		System.out.println(ReliabilityType.getInstance().getAggregatedValue(annotationMap.getAnnotationMap(), fcn));
		
		}
		catch(UnsuccessfulMashupGeneration ex){
			System.out.println("UNSUCCESSFUL MASHUP GENERATION!");
		}
		
		
	}
	
}
	public static void evaluateNonfunctionalHeuristicPrecisionInTermsofTrainingDatasize() throws Exception{
		WritableWorkbook wb = Workbook.createWorkbook(new File(homeAddress+ "evaluationPrecisionTraing.xls"));
		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
		
		//String evaluationDirectory ="/home/mbashari/EVAL_FOLDER/evaluation/fmsize/smfamily/";
		int[] repositorySize = {1200};
		//int[] repositorySize = {400};
//		int repositoryIterationSize = 1000;
		
		
		int offset =0;
		int colOffset=0;
		
		

		
		
		
		curSheet.addCell(new Label(colOffset+1,0,"Mashup Family Name"));
		curSheet.addCell(new Label(colOffset+2,0,"Iteration Numer"));
		curSheet.addCell(new Label(colOffset+3,0,"Number of Training"));
		curSheet.addCell(new Label(colOffset+4,0,"Number of atomic set"));
		
		curSheet.addCell(new Label(colOffset+6,0,"Average Squared Error"));
//		curSheet.addCell(new Label(colOffset+8,0,"Feature model reconfiguration"));
//		curSheet.addCell(new Label(colOffset+9,0,"Feature Distance"));
//		curSheet.addCell(new Label(colOffset+10,0,"Number of tries"));
		
		
		
//		curSheet.addCell(new Number(colOffsetTbl3+3,offsetTbl3,3));
//		curSheet.addCell(new Number(colOffsetTbl3+4,offsetTbl3,2));
//		curSheet.addCell(new Number(colOffsetTbl3+5,offsetTbl3,2));
//		curSheet.addCell(new Number(colOffsetTbl3+6,offsetTbl3,100));
//		offsetTbl3++;
//		
//		curSheet.addCell(new Label(colOffsetTbl3+1,offsetTbl3,"Number of Confs"));
//		curSheet.addCell(new Label(colOffsetTbl3+2,offsetTbl3,"Number of Possible Configuration"));
//		curSheet.addCell(new Label(colOffsetTbl3+3,offsetTbl3,"Average of Workflow Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl3+4,offsetTbl3,"Average of Workflow Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl3+5,offsetTbl3,"Average of Feature Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl3+6,offsetTbl3,"Average of Feature Adaptation (Failed)"));
//		offsetTbl3++;
//		
//		
//		curSheet.addCell(new Label(colOffsetTbl1+1,offsetTbl1,"Number of Possible Configuration"));
//		curSheet.addCell(new Label(colOffsetTbl1+2,offsetTbl1,"Number of Service Lookups"));
//		curSheet.addCell(new Label(colOffsetTbl1+3,offsetTbl1,"Average Service Lookup time"));
//		curSheet.addCell(new Label(colOffsetTbl1+4,offsetTbl1,"Number of Workflow Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+5,offsetTbl1,"Average of Workflow Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+6,offsetTbl1,"Number of Feature Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+7,offsetTbl1,"Average of Feature Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+8,offsetTbl1,"Number of Workflow Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+9,offsetTbl1,"Average of Workflow Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+10,offsetTbl1,"Number of Feature Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+11,offsetTbl1,"Average of Feature Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+12,offsetTbl1,"Service Lookup"));
//		curSheet.addCell(new Label(colOffsetTbl1+13,offsetTbl1,"Workflow Adaptation"));
//		curSheet.addCell(new Label(colOffsetTbl1+14,offsetTbl1,"Feature Adaptation "));
//		curSheet.addCell(new Label(colOffsetTbl1+15,offsetTbl1,"Adaptation Failure"));
//		
//		curSheet.addCell(new Label(colOffsetTbl2+1,offsetTbl2,"Number of Feature Change"));
//		curSheet.addCell(new Label(colOffsetTbl2+2,offsetTbl2,"Occurences"));
//		
//		Map<Integer,Integer> featureChangeOccurenceMap = new HashMap<Integer, Integer>();
//		
//		offsetTbl1++;
//		offsetTbl2++;
		
		offset++;
		int row = offset;
		
		for(int rcntr =0; rcntr<repositorySize.length; rcntr++){
			System.out.println(repositorySize[rcntr]);
			
//			long cntServiceLookup=0;
//			long sumServiceLookup=0;
//			long avgServiceLookup=0;
//			
//			long serviceLookupCntr=0;
//			
//			long cntPlanningDurationSucc=0;
//			long sumPlanningDurationSucc=0;
//			long avgPlanningDurationSucc=0;
//			
//			long cntPlanningDurationFail=0;
//			long sumPlanningDurationFail=0;
//			long avgPlanningDurationFail=0;
//			
//			long cntFeatureAdaptationDurationSucc=0;
//			long sumFeatureAdaptationDurationSucc=0;
//			long avgFeatureAdaptationDurationSucc=0;
//			
//			long cntFeatureAdaptationDurationFail=0;
//			long sumFeatureAdaptationDurationFail=0;
//			long avgFeatureAdaptationDurationFail=0;
			
//			int evalCntr=0;
			
//			File[] smFamilyDirectories = new File(evaluationDirectory+String.valueOf(repositorySize[rcntr])).listFiles(new FileFilter() {
//				
//				public boolean accept(File pathname) {
//					
//					return pathname.isDirectory();
//				}
//			});
//			
			File[] smFamilyDirectories ={new File("/home/mbashari/EVAL_FOLDER/evaluation/fmsize/smfamily/1200/1024")};
			
			//int maxSMIterationNo = repositoryIterationSize/smFamilyDirectories.length;
			
			
			for(File curDir: smFamilyDirectories){
				
				DomainModels dm = DomainModels.readFromDirectory(curDir);
				
				List<FeatureModelConfiguration> fmcs = dm.getFeatureModel().getAllValidConfiguration(-1);
				System.out.println("All possible configuration: "+fmcs.size());
				
				Map<FeatureModelConfiguration,FlowComponentNode> serviceMashupCache = new HashMap<FeatureModelConfiguration, FlowComponentNode>();
				
//				List<FeatureModelConfiguration> fmcs = dm.getFeatureModel().getAllValidConfiguration(-1,fmcSize,fmcVariation);
//				if(fmcs.size()==0)
//					continue;
				
//				int lrow = row;
				int maxSMIterationNo = 1;
				
				for(int iterationCntr=0; iterationCntr<maxSMIterationNo; iterationCntr++ ){
					
					
					// generate nonfunctional for services
					
					ServiceNonfunctionalAnnotationMap snam = new ServiceNonfunctionalAnnotationMap(dm.getServiceCollection().getServices());
					
					snam.generateNonfunctionRandomly(ExecutionTimeType.getInstance(),200, 50, 50, 30);
					
					// select 100 feature model configuration randomly for evaluation
					
					
					
					// for number of atomic set to number of atomic set *10 find the heuristic for estimation
					
					FeatureAtomicSetMap fasm = dm.getFeatureModel().findAtomicSets();
					
					
					
					int iterationNumber=300;
					
					List<FeatureModelConfiguration> adequateTrainingList = dm.getFeatureModel().generateRegressionConfigurations(1, fasm);
					
					List<AtomicSet> asl =  fasm.getAllAtomicSets(false);
					
					int numberOfAtomicSet = asl.size();
					
					List<FeatureModelConfiguration> testFmcs = dm.getFeatureModel().generateRandomFeatureModelConfiguration(100,adequateTrainingList);
					
					List<FeatureModelConfiguration> newExclusions = new ArrayList<FeatureModelConfiguration>();
					
					newExclusions.addAll(testFmcs);
					newExclusions.addAll(adequateTrainingList);
					
					List<FeatureModelConfiguration> additionalTrainingList = dm.getFeatureModel().generateRandomFeatureModelConfiguration(300,newExclusions);
					
					
					
					for(int sampleSizeCntr=0;sampleSizeCntr<iterationNumber;sampleSizeCntr=sampleSizeCntr+5){
						List<FeatureModelConfiguration> trainingList = new ArrayList<FeatureModelConfiguration>();
						trainingList.addAll(adequateTrainingList);
						trainingList.addAll(additionalTrainingList.subList(0,Math.min(sampleSizeCntr, additionalTrainingList.size())));

						Map<AtomicSet, Double> cv = new HashMap<AtomicSet, Double>();
						dm.findAtomicSetContributionValueOLS(fasm, snam, ExecutionTimeType.getInstance(), trainingList, serviceMashupCache,cv);
						
						double[] actualYValue= new double[testFmcs.size()];
						double[] estimatedYValue= new double[testFmcs.size()];
						
						for(int testCntr =0; testCntr<testFmcs.size();testCntr++ ){
							
							actualYValue[testCntr] = dm.findFeatureModelConfigurationNonfunctionalValue(testFmcs.get(testCntr),ExecutionTimeType.getInstance(),snam.getAnnotationMap(),serviceMashupCache);
							
							estimatedYValue[testCntr] = testFmcs.get(testCntr).estimateNonfunctionalValue(fasm,cv,ExecutionTimeType.getInstance());
							
							
							
						}
						
						double[] errorsSquared =  new double[testFmcs.size()];
						
						for(int cntr=0; cntr<testFmcs.size();cntr++)
							errorsSquared[cntr]= Math.pow( actualYValue[cntr]-estimatedYValue[cntr],2);
						
						double errSqrdAverage  = UtilityClass.findAverage(errorsSquared);
						
						double errSqRoot= Math.sqrt(errSqrdAverage);
						
						//System.out.println("Adaptation type: "+ar.getAdaptationType());
						curSheet.addCell(new Label(colOffset+1,row,curDir.toString()));
						curSheet.addCell(new Number(colOffset+2,row, iterationCntr));
						curSheet.addCell(new Number(colOffset+3,row, trainingList.size()));
						curSheet.addCell(new Number(colOffset+4,row, numberOfAtomicSet));
						
						curSheet.addCell(new Number(colOffset+6,row, errSqRoot));
						
						row++;
						
					}
					
					
					
					
					

					
					
					
					
					
					
					
					
//					evalCntr++;
					
					
					
				}
				
//				String beginRow = String.valueOf(lrow);
//				String endRow = String.valueOf(row-1);
				
//				curSheet.addCell(new Number(colOffsetTbl3+1,offsetTbl3,repositorySize[rcntr]));
//				curSheet.addCell(new Label(colOffsetTbl3+2,offsetTbl3,curDir.toString()));				
//				curSheet.addCell(new Formula(colOffsetTbl3+3, offsetTbl3,"AVERAGE(H"+beginRow+":H"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+4, offsetTbl3,"AVERAGE(H"+beginRow+":H"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+5, offsetTbl3,"AVERAGE(I"+beginRow+":I"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+6, offsetTbl3,"AVERAGE(I"+beginRow+":I"+endRow+")"));
				
//				curSheet.addCell(new Label(colOffsetTbl3+3, offsetTbl3,"AVERAGEIFS(H"+beginRow+":H"+endRow+",E"+beginRow+":E"+endRow+",\"=3\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+4, offsetTbl3,"AVERAGEIFS(H"+beginRow+":H"+endRow+",E"+beginRow+":E"+endRow+",\"=2\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+5, offsetTbl3,"AVERAGEIFS(I"+beginRow+":I"+endRow+",E"+beginRow+":E"+endRow+",\"=3\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+6, offsetTbl3,"AVERAGEIFS(I"+beginRow+":I"+endRow+",E"+beginRow+":E"+endRow+",\"=100\")"));
//				
				
//				offsetTbl3++;
				
				
			}
			
			
			
			
			
//			curSheet.addCell(new Label(colOffsetTbl1+1,offsetTbl1,String.valueOf(repositorySize[rcntr])));
//					
//			offsetTbl1++;
		}
		
//		int cntr=0;
		
		
		wb.write();
		wb.close();
		
	}
	
	
	public static void evaluateNonfunctionalHeuristicPrecisionInTermsofTrainingDatasizeNew() throws Exception{
		
		
		String jarAddress ="";
		
		   
		jarAddress= URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").getPath(), "UTF-8");
	   
		
		
		System.out.println(jarAddress);
		String dirAddress = jarAddress.substring(0 ,jarAddress.lastIndexOf('/')+1);
		
		Configuration.plannerAddress = dirAddress;
		Configuration.tempFolder = dirAddress+ "temp/";
		
		SimpleLogger log= new SimpleLogger(dirAddress+"log.txt", true);
		
//		dirAddress = "/home/mbashari/EVAL_FOLDER/";
		
		
		WritableWorkbook wb = Workbook.createWorkbook(new File(dirAddress+ "evaluationPrecTraining.xls"));
		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
		
		String evaluationDirectory =dirAddress+"fmFeatureSizes/";
		int[] fmSizeList = {30,60,90,120,150,180,210,240};
		//int[] repositorySize = {400};
//		int repositoryIterationSize = 1000;
		
		int offset =0;
		int colOffset=0;
		
		

		
		
		curSheet.addCell(new Label(colOffset+1,0,"Mashup Family Name"));
		curSheet.addCell(new Label(colOffset+2,0,"Iteration Numer"));
		curSheet.addCell(new Label(colOffset+3,0,"Number of Training"));
		curSheet.addCell(new Label(colOffset+4,0,"Number of atomic set"));
		curSheet.addCell(new Label(colOffset+5,0,"Number of features"));		
		curSheet.addCell(new Label(colOffset+6,0,"Average Squared Error"));
		curSheet.addCell(new Label(colOffset+7,0,"R Squared"));
		curSheet.addCell(new Label(colOffset+8,0,"Test Set Std Dev"));
//		curSheet.addCell(new Label(colOffset+8,0,"Feature model reconfiguration"));
//		curSheet.addCell(new Label(colOffset+9,0,"Feature Distance"));
//		curSheet.addCell(new Label(colOffset+10,0,"Number of tries"));
		
		
		
//		curSheet.addCell(new Number(colOffsetTbl3+3,offsetTbl3,3));
//		curSheet.addCell(new Number(colOffsetTbl3+4,offsetTbl3,2));
//		curSheet.addCell(new Number(colOffsetTbl3+5,offsetTbl3,2));
//		curSheet.addCell(new Number(colOffsetTbl3+6,offsetTbl3,100));
//		offsetTbl3++;
//		
//		curSheet.addCell(new Label(colOffsetTbl3+1,offsetTbl3,"Number of Confs"));
//		curSheet.addCell(new Label(colOffsetTbl3+2,offsetTbl3,"Number of Possible Configuration"));
//		curSheet.addCell(new Label(colOffsetTbl3+3,offsetTbl3,"Average of Workflow Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl3+4,offsetTbl3,"Average of Workflow Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl3+5,offsetTbl3,"Average of Feature Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl3+6,offsetTbl3,"Average of Feature Adaptation (Failed)"));
//		offsetTbl3++;
//		
//		
//		curSheet.addCell(new Label(colOffsetTbl1+1,offsetTbl1,"Number of Possible Configuration"));
//		curSheet.addCell(new Label(colOffsetTbl1+2,offsetTbl1,"Number of Service Lookups"));
//		curSheet.addCell(new Label(colOffsetTbl1+3,offsetTbl1,"Average Service Lookup time"));
//		curSheet.addCell(new Label(colOffsetTbl1+4,offsetTbl1,"Number of Workflow Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+5,offsetTbl1,"Average of Workflow Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+6,offsetTbl1,"Number of Feature Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+7,offsetTbl1,"Average of Feature Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+8,offsetTbl1,"Number of Workflow Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+9,offsetTbl1,"Average of Workflow Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+10,offsetTbl1,"Number of Feature Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+11,offsetTbl1,"Average of Feature Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+12,offsetTbl1,"Service Lookup"));
//		curSheet.addCell(new Label(colOffsetTbl1+13,offsetTbl1,"Workflow Adaptation"));
//		curSheet.addCell(new Label(colOffsetTbl1+14,offsetTbl1,"Feature Adaptation "));
//		curSheet.addCell(new Label(colOffsetTbl1+15,offsetTbl1,"Adaptation Failure"));
//		
//		curSheet.addCell(new Label(colOffsetTbl2+1,offsetTbl2,"Number of Feature Change"));
//		curSheet.addCell(new Label(colOffsetTbl2+2,offsetTbl2,"Occurences"));
//		
//		Map<Integer,Integer> featureChangeOccurenceMap = new HashMap<Integer, Integer>();
//		
//		offsetTbl1++;
//		offsetTbl2++;
		
		offset++;
		int row = offset;
		
		for(int rcntr =0; rcntr<fmSizeList.length; rcntr++){
			System.out.println(fmSizeList[rcntr]);
			
			log.log("Current fm size: "+fmSizeList[rcntr]);
//			long cntServiceLookup=0;
//			long sumServiceLookup=0;
//			long avgServiceLookup=0;
//			
//			long serviceLookupCntr=0;
//			
//			long cntPlanningDurationSucc=0;
//			long sumPlanningDurationSucc=0;
//			long avgPlanningDurationSucc=0;
//			
//			long cntPlanningDurationFail=0;
//			long sumPlanningDurationFail=0;
//			long avgPlanningDurationFail=0;
//			
//			long cntFeatureAdaptationDurationSucc=0;
//			long sumFeatureAdaptationDurationSucc=0;
//			long avgFeatureAdaptationDurationSucc=0;
//			
//			long cntFeatureAdaptationDurationFail=0;
//			long sumFeatureAdaptationDurationFail=0;
//			long avgFeatureAdaptationDurationFail=0;
			
//			int evalCntr=0;
			
			File[] smFamilyDirectories = new File(evaluationDirectory+String.valueOf(fmSizeList[rcntr])).listFiles(new FileFilter() {
				
				public boolean accept(File pathname) {
					
					return pathname.isDirectory();
				}
			});
//			
//			File[] smFamilyDirectories ={new File("/home/mbashari/EVAL_FOLDER/evaluation/fmsize/smfamily/1200/1024")};
			
			//int maxSMIterationNo = repositoryIterationSize/smFamilyDirectories.length;
			
			
			for(File curDir: smFamilyDirectories){
				
				DomainModels dm = DomainModels.readFromDirectory(curDir);
				
				log.log("Current sm family: "+curDir);
				
				List<FeatureModelConfiguration> fmcs = dm.getFeatureModel().getAllValidConfiguration(-1);
				System.out.println("All possible configuration: "+fmcs.size());
				
				Map<FeatureModelConfiguration,FlowComponentNode> serviceMashupCache = new HashMap<FeatureModelConfiguration, FlowComponentNode>();
				
//				List<FeatureModelConfiguration> fmcs = dm.getFeatureModel().getAllValidConfiguration(-1,fmcSize,fmcVariation);
//				if(fmcs.size()==0)
//					continue;
				
//				int lrow = row;
				int maxSMIterationNo = 200;
				
				for(int iterationCntr=0; iterationCntr<maxSMIterationNo; iterationCntr++ ){
					
					
					// generate nonfunctional for services
					
					ServiceNonfunctionalAnnotationMap snam = new ServiceNonfunctionalAnnotationMap(dm.getServiceCollection().getServices());
					
					snam.generateNonfunctionRandomly(ExecutionTimeType.getInstance(),200, 50, 50, 30);
					
					// select 100 feature model configuration randomly for evaluation
					
					
					
					// for number of atomic set to number of atomic set *10 find the heuristic for estimation
					
					FeatureAtomicSetMap fasm = dm.getFeatureModel().findAtomicSets();
					
					
					
					int iterationNumber=1;
					
					List<FeatureModelConfiguration> adequateTrainingList = dm.getFeatureModel().generateRegressionConfigurations(1, fasm);
					
					List<AtomicSet> asl =  fasm.getAllAtomicSets(false);
					
					int numberOfAtomicSet = asl.size();
					
					List<FeatureModelConfiguration> testFmcs = dm.getFeatureModel().generateRandomFeatureModelConfiguration(100,adequateTrainingList);
					
					List<FeatureModelConfiguration> newExclusions = new ArrayList<FeatureModelConfiguration>();
					
					newExclusions.addAll(testFmcs);
					newExclusions.addAll(adequateTrainingList);
					
					List<FeatureModelConfiguration> additionalTrainingList = dm.getFeatureModel().generateRandomFeatureModelConfiguration(300,newExclusions);
					
					
					
					for(int sampleSizeCntr=0;sampleSizeCntr<iterationNumber;sampleSizeCntr=sampleSizeCntr+5){
						log.log("Current sample size: "+sampleSizeCntr);
						List<FeatureModelConfiguration> trainingList = new ArrayList<FeatureModelConfiguration>();
						trainingList.addAll(adequateTrainingList);
						trainingList.addAll(additionalTrainingList.subList(0,Math.min(sampleSizeCntr, additionalTrainingList.size())));

						Map<AtomicSet, Double> cv =  new HashMap<AtomicSet, Double>();
						
						double rSquared = dm.findAtomicSetContributionValueOLS(fasm, snam, ExecutionTimeType.getInstance(), trainingList, serviceMashupCache,cv);
						
						double[] actualYValue= new double[testFmcs.size()];
						double[] estimatedYValue= new double[testFmcs.size()];
						
						for(int testCntr =0; testCntr<testFmcs.size();testCntr++ ){
							
							actualYValue[testCntr] = dm.findFeatureModelConfigurationNonfunctionalValue(testFmcs.get(testCntr),ExecutionTimeType.getInstance(),snam.getAnnotationMap(),serviceMashupCache);
							
							estimatedYValue[testCntr] = testFmcs.get(testCntr).estimateNonfunctionalValue(fasm,cv,ExecutionTimeType.getInstance());
							
							
							
						}
						
						double[] errorsSquared =  new double[testFmcs.size()];
						
						for(int cntr=0; cntr<testFmcs.size();cntr++)
							errorsSquared[cntr]= Math.pow( actualYValue[cntr]-estimatedYValue[cntr],2);
						
						double errSqrdAverage  = UtilityClass.findAverage(errorsSquared);
						
						double errSqRoot= Math.sqrt(errSqrdAverage);
						
						//System.out.println("Adaptation type: "+ar.getAdaptationType());
						curSheet.addCell(new Label(colOffset+1,row,curDir.toString()));
						curSheet.addCell(new Number(colOffset+2,row, iterationCntr));
						curSheet.addCell(new Number(colOffset+3,row, trainingList.size()));
						curSheet.addCell(new Number(colOffset+4,row, numberOfAtomicSet));
						curSheet.addCell(new Number(colOffset+5,row, fmSizeList[rcntr]));
						curSheet.addCell(new Number(colOffset+6,row, errSqRoot));
						curSheet.addCell(new Number(colOffset+7,row, rSquared));
						curSheet.addCell(new Number(colOffset+8,row, UtilityClass.getStdDev(actualYValue)));
						
						row++;
						
					}
					
					
					
					
					

					
					
					
					
					
					
					
					
//					evalCntr++;
					
					
					
				}
				
//				String beginRow = String.valueOf(lrow);
//				String endRow = String.valueOf(row-1);
				
//				curSheet.addCell(new Number(colOffsetTbl3+1,offsetTbl3,repositorySize[rcntr]));
//				curSheet.addCell(new Label(colOffsetTbl3+2,offsetTbl3,curDir.toString()));				
//				curSheet.addCell(new Formula(colOffsetTbl3+3, offsetTbl3,"AVERAGE(H"+beginRow+":H"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+4, offsetTbl3,"AVERAGE(H"+beginRow+":H"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+5, offsetTbl3,"AVERAGE(I"+beginRow+":I"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+6, offsetTbl3,"AVERAGE(I"+beginRow+":I"+endRow+")"));
				
//				curSheet.addCell(new Label(colOffsetTbl3+3, offsetTbl3,"AVERAGEIFS(H"+beginRow+":H"+endRow+",E"+beginRow+":E"+endRow+",\"=3\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+4, offsetTbl3,"AVERAGEIFS(H"+beginRow+":H"+endRow+",E"+beginRow+":E"+endRow+",\"=2\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+5, offsetTbl3,"AVERAGEIFS(I"+beginRow+":I"+endRow+",E"+beginRow+":E"+endRow+",\"=3\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+6, offsetTbl3,"AVERAGEIFS(I"+beginRow+":I"+endRow+",E"+beginRow+":E"+endRow+",\"=100\")"));
//				
				
//				offsetTbl3++;
				
				
			}
			
			
			
			
			
//			curSheet.addCell(new Label(colOffsetTbl1+1,offsetTbl1,String.valueOf(repositorySize[rcntr])));
//					
//			offsetTbl1++;
		}
		
//		int cntr=0;
		
		
		wb.write();
		wb.close();
		log.close();
	}
	
	
	
	
	public static void evaluateNonfunctionalHeuristicPrecisionInTermsofTrainingDatasizeDis() throws Exception{
		
		
		String jarAddress ="";
		
		   
		jarAddress= URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").getPath(), "UTF-8");
	   
		
		
		System.out.println(jarAddress);
		String dirAddress = jarAddress.substring(0 ,jarAddress.lastIndexOf('/')+1);
		
//		dirAddress = "/home/mbashari/EVAL_FOLDER/";
//		int curfmSizeList =30;
		
		Configuration.plannerAddress = dirAddress;
		Configuration.tempFolder = dirAddress+ "temp/";
		
		SimpleLogger log= new SimpleLogger(dirAddress+"log.txt", true);
		
		
		
		WritableWorkbook wb = Workbook.createWorkbook(new File(dirAddress+ "evaluationPrecTraining.xls"));
		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
		
		String evaluationDirectory =dirAddress+"fmFeatureSizes/";
		int curfmSizeList = Integer.valueOf( UtilityClass.readFile(dirAddress+ "rconf.txt").trim());;
		
		
		
		//int[] repositorySize = {400};
//		int repositoryIterationSize = 1000;
	
		
		int offset =0;
		int colOffset=0;
		
		
	
		
		
		curSheet.addCell(new Label(colOffset+1,0,"Mashup Family Name"));
		curSheet.addCell(new Label(colOffset+2,0,"Iteration Numer"));
		curSheet.addCell(new Label(colOffset+3,0,"Number of Training"));
		curSheet.addCell(new Label(colOffset+4,0,"Number of atomic set"));
		curSheet.addCell(new Label(colOffset+5,0,"Number of features"));		
		curSheet.addCell(new Label(colOffset+6,0,"Average Squared Error"));
		curSheet.addCell(new Label(colOffset+7,0,"R Squared"));
		curSheet.addCell(new Label(colOffset+8,0,"Test Set Std Dev"));
//		curSheet.addCell(new Label(colOffset+8,0,"Feature model reconfiguration"));
//		curSheet.addCell(new Label(colOffset+9,0,"Feature Distance"));
//		curSheet.addCell(new Label(colOffset+10,0,"Number of tries"));
		
		
		
//		curSheet.addCell(new Number(colOffsetTbl3+3,offsetTbl3,3));
//		curSheet.addCell(new Number(colOffsetTbl3+4,offsetTbl3,2));
//		curSheet.addCell(new Number(colOffsetTbl3+5,offsetTbl3,2));
//		curSheet.addCell(new Number(colOffsetTbl3+6,offsetTbl3,100));
//		offsetTbl3++;
//		
//		curSheet.addCell(new Label(colOffsetTbl3+1,offsetTbl3,"Number of Confs"));
//		curSheet.addCell(new Label(colOffsetTbl3+2,offsetTbl3,"Number of Possible Configuration"));
//		curSheet.addCell(new Label(colOffsetTbl3+3,offsetTbl3,"Average of Workflow Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl3+4,offsetTbl3,"Average of Workflow Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl3+5,offsetTbl3,"Average of Feature Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl3+6,offsetTbl3,"Average of Feature Adaptation (Failed)"));
//		offsetTbl3++;
//		
//		
//		curSheet.addCell(new Label(colOffsetTbl1+1,offsetTbl1,"Number of Possible Configuration"));
//		curSheet.addCell(new Label(colOffsetTbl1+2,offsetTbl1,"Number of Service Lookups"));
//		curSheet.addCell(new Label(colOffsetTbl1+3,offsetTbl1,"Average Service Lookup time"));
//		curSheet.addCell(new Label(colOffsetTbl1+4,offsetTbl1,"Number of Workflow Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+5,offsetTbl1,"Average of Workflow Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+6,offsetTbl1,"Number of Feature Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+7,offsetTbl1,"Average of Feature Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+8,offsetTbl1,"Number of Workflow Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+9,offsetTbl1,"Average of Workflow Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+10,offsetTbl1,"Number of Feature Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+11,offsetTbl1,"Average of Feature Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+12,offsetTbl1,"Service Lookup"));
//		curSheet.addCell(new Label(colOffsetTbl1+13,offsetTbl1,"Workflow Adaptation"));
//		curSheet.addCell(new Label(colOffsetTbl1+14,offsetTbl1,"Feature Adaptation "));
//		curSheet.addCell(new Label(colOffsetTbl1+15,offsetTbl1,"Adaptation Failure"));
//		
//		curSheet.addCell(new Label(colOffsetTbl2+1,offsetTbl2,"Number of Feature Change"));
//		curSheet.addCell(new Label(colOffsetTbl2+2,offsetTbl2,"Occurences"));
//		
//		Map<Integer,Integer> featureChangeOccurenceMap = new HashMap<Integer, Integer>();
//		
//		offsetTbl1++;
//		offsetTbl2++;
		
		offset++;
		int row = offset;
		
		
			
			log.log("Current fm size: "+curfmSizeList);
//			long cntServiceLookup=0;
//			long sumServiceLookup=0;
//			long avgServiceLookup=0;
//			
//			long serviceLookupCntr=0;
//			
//			long cntPlanningDurationSucc=0;
//			long sumPlanningDurationSucc=0;
//			long avgPlanningDurationSucc=0;
//			
//			long cntPlanningDurationFail=0;
//			long sumPlanningDurationFail=0;
//			long avgPlanningDurationFail=0;
//			
//			long cntFeatureAdaptationDurationSucc=0;
//			long sumFeatureAdaptationDurationSucc=0;
//			long avgFeatureAdaptationDurationSucc=0;
//			
//			long cntFeatureAdaptationDurationFail=0;
//			long sumFeatureAdaptationDurationFail=0;
//			long avgFeatureAdaptationDurationFail=0;
			
//			int evalCntr=0;
			
			File[] smFamilyDirectories = new File(evaluationDirectory+String.valueOf(curfmSizeList)).listFiles(new FileFilter() {
				
				public boolean accept(File pathname) {
					
					return pathname.isDirectory();
				}
			});
//			
//			File[] smFamilyDirectories ={new File("/home/mbashari/EVAL_FOLDER/evaluation/fmsize/smfamily/1200/1024")};
			
			//int maxSMIterationNo = repositoryIterationSize/smFamilyDirectories.length;
			
			
			for(File curDir: smFamilyDirectories){
				
				DomainModels dm = DomainModels.readFromDirectory(curDir);
				
				log.log("Current sm family: "+curDir);
				
				List<FeatureModelConfiguration> fmcs = dm.getFeatureModel().getAllValidConfiguration(-1);
				System.out.println("All possible configuration: "+fmcs.size());
				
				Map<FeatureModelConfiguration,FlowComponentNode> serviceMashupCache = new HashMap<FeatureModelConfiguration, FlowComponentNode>();
				
//				List<FeatureModelConfiguration> fmcs = dm.getFeatureModel().getAllValidConfiguration(-1,fmcSize,fmcVariation);
//				if(fmcs.size()==0)
//					continue;
				
//				int lrow = row;
				int maxSMIterationNo = 1;
				
				for(int iterationCntr=0; iterationCntr<maxSMIterationNo; iterationCntr++ ){
					
					
					// generate nonfunctional for services
					
					ServiceNonfunctionalAnnotationMap snam = new ServiceNonfunctionalAnnotationMap(dm.getServiceCollection().getServices());
					
					snam.generateNonfunctionRandomly(ExecutionTimeType.getInstance(),200, 50, 50, 30);
					
					// select 100 feature model configuration randomly for evaluation
					
					
					
					// for number of atomic set to number of atomic set *10 find the heuristic for estimation
					
					FeatureAtomicSetMap fasm = dm.getFeatureModel().findAtomicSets();
					
					
					
					int iterationNumber=200;
					
					List<FeatureModelConfiguration> adequateTrainingList = dm.getFeatureModel().generateRegressionConfigurations(1, fasm);
					
					List<AtomicSet> asl =  fasm.getAllAtomicSets(false);
					
					int numberOfAtomicSet = asl.size();
					
					List<FeatureModelConfiguration> testFmcs = dm.getFeatureModel().generateRandomFeatureModelConfiguration(100,adequateTrainingList);
					
					List<FeatureModelConfiguration> newExclusions = new ArrayList<FeatureModelConfiguration>();
					
					newExclusions.addAll(testFmcs);
					newExclusions.addAll(adequateTrainingList);
					
					List<FeatureModelConfiguration> additionalTrainingList = dm.getFeatureModel().generateRandomFeatureModelConfiguration(300,newExclusions);
					
					
					
					for(int sampleSizeCntr=0;sampleSizeCntr<iterationNumber;sampleSizeCntr=sampleSizeCntr+5){
						log.log("Current sample size: "+sampleSizeCntr);
						List<FeatureModelConfiguration> trainingList = new ArrayList<FeatureModelConfiguration>();
						trainingList.addAll(adequateTrainingList);
						trainingList.addAll(additionalTrainingList.subList(0,Math.min(sampleSizeCntr, additionalTrainingList.size())));

						Map<AtomicSet, Double> cv =  new HashMap<AtomicSet, Double>();
						
						double rSquared = dm.findAtomicSetContributionValueOLS(fasm, snam, ExecutionTimeType.getInstance(), trainingList, serviceMashupCache,cv);
						
						double[] actualYValue= new double[testFmcs.size()];
						double[] estimatedYValue= new double[testFmcs.size()];
						
						for(int testCntr =0; testCntr<testFmcs.size();testCntr++ ){
							
							actualYValue[testCntr] = dm.findFeatureModelConfigurationNonfunctionalValue(testFmcs.get(testCntr),ExecutionTimeType.getInstance(),snam.getAnnotationMap(),serviceMashupCache);
							
							estimatedYValue[testCntr] = testFmcs.get(testCntr).estimateNonfunctionalValue(fasm,cv,ExecutionTimeType.getInstance());
							
							
							
						}
						
						double[] errorsSquared =  new double[testFmcs.size()];
						
						for(int cntr=0; cntr<testFmcs.size();cntr++)
							errorsSquared[cntr]= Math.pow( actualYValue[cntr]-estimatedYValue[cntr],2);
						
						double errSqrdAverage  = UtilityClass.findAverage(errorsSquared);
						
						double errSqRoot= Math.sqrt(errSqrdAverage);
						
						//System.out.println("Adaptation type: "+ar.getAdaptationType());
						curSheet.addCell(new Label(colOffset+1,row,curDir.toString()));
						curSheet.addCell(new Number(colOffset+2,row, iterationCntr));
						curSheet.addCell(new Number(colOffset+3,row, trainingList.size()));
						curSheet.addCell(new Number(colOffset+4,row, numberOfAtomicSet));
						curSheet.addCell(new Number(colOffset+5,row, curfmSizeList));
						curSheet.addCell(new Number(colOffset+6,row, errSqRoot));
						curSheet.addCell(new Number(colOffset+7,row, rSquared));
						curSheet.addCell(new Number(colOffset+8,row, UtilityClass.getStdDev(actualYValue)));
						
						row++;
						
					}
					
					
					
					
					

					
					
					
					
					
					
					
					
//					evalCntr++;
					
					
					
				}
				
//				String beginRow = String.valueOf(lrow);
//				String endRow = String.valueOf(row-1);
				
//				curSheet.addCell(new Number(colOffsetTbl3+1,offsetTbl3,repositorySize[rcntr]));
//				curSheet.addCell(new Label(colOffsetTbl3+2,offsetTbl3,curDir.toString()));				
//				curSheet.addCell(new Formula(colOffsetTbl3+3, offsetTbl3,"AVERAGE(H"+beginRow+":H"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+4, offsetTbl3,"AVERAGE(H"+beginRow+":H"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+5, offsetTbl3,"AVERAGE(I"+beginRow+":I"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+6, offsetTbl3,"AVERAGE(I"+beginRow+":I"+endRow+")"));
				
//				curSheet.addCell(new Label(colOffsetTbl3+3, offsetTbl3,"AVERAGEIFS(H"+beginRow+":H"+endRow+",E"+beginRow+":E"+endRow+",\"=3\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+4, offsetTbl3,"AVERAGEIFS(H"+beginRow+":H"+endRow+",E"+beginRow+":E"+endRow+",\"=2\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+5, offsetTbl3,"AVERAGEIFS(I"+beginRow+":I"+endRow+",E"+beginRow+":E"+endRow+",\"=3\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+6, offsetTbl3,"AVERAGEIFS(I"+beginRow+":I"+endRow+",E"+beginRow+":E"+endRow+",\"=100\")"));
//				
				
//				offsetTbl3++;
				
				
			}
			
			
			
			
			
//			curSheet.addCell(new Label(colOffsetTbl1+1,offsetTbl1,String.valueOf(repositorySize[rcntr])));
//					
//			offsetTbl1++;
		
		
//		int cntr=0;
		
		
		wb.write();
		wb.close();
		log.close();
	}
	
	
	public static void evaluateNonfunctionalHeuristicPrecisionInTermsofTrainingDatasizeDis1() throws Exception{
		
		
		String jarAddress ="";
		
		   
		jarAddress= URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").getPath(), "UTF-8");
	   
		
		
		System.out.println(jarAddress);
		String dirAddress = jarAddress.substring(0 ,jarAddress.lastIndexOf('/')+1);
		
//		dirAddress = "/home/mbashari/EVAL_FOLDER/";
//		int curfmSizeList =30;
		
		Configuration.plannerAddress = dirAddress;
		Configuration.tempFolder = dirAddress+ "temp/";
		
		SimpleLogger log= new SimpleLogger(dirAddress+"log.txt", true);
		
		
		
		WritableWorkbook wb = Workbook.createWorkbook(new File(dirAddress+ "evaluationPrecASTraining.xls"));
		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
		
		String evaluationDirectory =dirAddress+"fmASSizes/";
		int curfmSizeList = Integer.valueOf( UtilityClass.readFile(dirAddress+ "rconf.txt").trim());;
		
		
		
		//int[] repositorySize = {400};
//		int repositoryIterationSize = 1000;
		
		int offset =0;
		int colOffset=0;
		
		
		
		curSheet.addCell(new Label(colOffset+1,0,"Mashup Family Name"));
		curSheet.addCell(new Label(colOffset+2,0,"Iteration Numer"));
		curSheet.addCell(new Label(colOffset+3,0,"Number of Training"));
		curSheet.addCell(new Label(colOffset+4,0,"Number of atomic set"));
		curSheet.addCell(new Label(colOffset+5,0,"Number of features"));		
		curSheet.addCell(new Label(colOffset+6,0,"Average Squared Error"));
		curSheet.addCell(new Label(colOffset+7,0,"R Squared"));
		curSheet.addCell(new Label(colOffset+8,0,"Test Set Std Dev"));
//		curSheet.addCell(new Label(colOffset+8,0,"Feature model reconfiguration"));
//		curSheet.addCell(new Label(colOffset+9,0,"Feature Distance"));
//		curSheet.addCell(new Label(colOffset+10,0,"Number of tries"));
		
		
		
//		curSheet.addCell(new Number(colOffsetTbl3+3,offsetTbl3,3));
//		curSheet.addCell(new Number(colOffsetTbl3+4,offsetTbl3,2));
//		curSheet.addCell(new Number(colOffsetTbl3+5,offsetTbl3,2));
//		curSheet.addCell(new Number(colOffsetTbl3+6,offsetTbl3,100));
//		offsetTbl3++;
//		
//		curSheet.addCell(new Label(colOffsetTbl3+1,offsetTbl3,"Number of Confs"));
//		curSheet.addCell(new Label(colOffsetTbl3+2,offsetTbl3,"Number of Possible Configuration"));
//		curSheet.addCell(new Label(colOffsetTbl3+3,offsetTbl3,"Average of Workflow Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl3+4,offsetTbl3,"Average of Workflow Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl3+5,offsetTbl3,"Average of Feature Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl3+6,offsetTbl3,"Average of Feature Adaptation (Failed)"));
//		offsetTbl3++;
//		
//		
//		curSheet.addCell(new Label(colOffsetTbl1+1,offsetTbl1,"Number of Possible Configuration"));
//		curSheet.addCell(new Label(colOffsetTbl1+2,offsetTbl1,"Number of Service Lookups"));
//		curSheet.addCell(new Label(colOffsetTbl1+3,offsetTbl1,"Average Service Lookup time"));
//		curSheet.addCell(new Label(colOffsetTbl1+4,offsetTbl1,"Number of Workflow Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+5,offsetTbl1,"Average of Workflow Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+6,offsetTbl1,"Number of Feature Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+7,offsetTbl1,"Average of Feature Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+8,offsetTbl1,"Number of Workflow Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+9,offsetTbl1,"Average of Workflow Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+10,offsetTbl1,"Number of Feature Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+11,offsetTbl1,"Average of Feature Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+12,offsetTbl1,"Service Lookup"));
//		curSheet.addCell(new Label(colOffsetTbl1+13,offsetTbl1,"Workflow Adaptation"));
//		curSheet.addCell(new Label(colOffsetTbl1+14,offsetTbl1,"Feature Adaptation "));
//		curSheet.addCell(new Label(colOffsetTbl1+15,offsetTbl1,"Adaptation Failure"));
//		
//		curSheet.addCell(new Label(colOffsetTbl2+1,offsetTbl2,"Number of Feature Change"));
//		curSheet.addCell(new Label(colOffsetTbl2+2,offsetTbl2,"Occurences"));
//		
//		Map<Integer,Integer> featureChangeOccurenceMap = new HashMap<Integer, Integer>();
//		
//		offsetTbl1++;
//		offsetTbl2++;
		
		offset++;
		int row = offset;
		
		
			
			log.log("Current fm size: "+curfmSizeList);
//			long cntServiceLookup=0;
//			long sumServiceLookup=0;
//			long avgServiceLookup=0;
//			
//			long serviceLookupCntr=0;
//			
//			long cntPlanningDurationSucc=0;
//			long sumPlanningDurationSucc=0;
//			long avgPlanningDurationSucc=0;
//			
//			long cntPlanningDurationFail=0;
//			long sumPlanningDurationFail=0;
//			long avgPlanningDurationFail=0;
//			
//			long cntFeatureAdaptationDurationSucc=0;
//			long sumFeatureAdaptationDurationSucc=0;
//			long avgFeatureAdaptationDurationSucc=0;
//			
//			long cntFeatureAdaptationDurationFail=0;
//			long sumFeatureAdaptationDurationFail=0;
//			long avgFeatureAdaptationDurationFail=0;
			
//			int evalCntr=0;
			
			File[] smFamilyDirectories = new File(evaluationDirectory+String.valueOf(curfmSizeList)).listFiles(new FileFilter() {
				
				public boolean accept(File pathname) {
					
					return pathname.isDirectory();
				}
			});
//			
//			File[] smFamilyDirectories ={new File("/home/mbashari/EVAL_FOLDER/evaluation/fmsize/smfamily/1200/1024")};
			
			//int maxSMIterationNo = repositoryIterationSize/smFamilyDirectories.length;
			
			
			for(File curDir: smFamilyDirectories){
				
				DomainModels dm = DomainModels.readFromDirectory(curDir);
				
				log.log("Current sm family: "+curDir);
				
				List<FeatureModelConfiguration> fmcs = dm.getFeatureModel().getAllValidConfiguration(-1);
				System.out.println("All possible configuration: "+fmcs.size());
				
				Map<FeatureModelConfiguration,FlowComponentNode> serviceMashupCache = new HashMap<FeatureModelConfiguration, FlowComponentNode>();
				
//				List<FeatureModelConfiguration> fmcs = dm.getFeatureModel().getAllValidConfiguration(-1,fmcSize,fmcVariation);
//				if(fmcs.size()==0)
//					continue;
				
//				int lrow = row;
				int maxSMIterationNo = 3;
				
				for(int iterationCntr=0; iterationCntr<maxSMIterationNo; iterationCntr++ ){
					
					
					// generate nonfunctional for services
					
					ServiceNonfunctionalAnnotationMap snam = new ServiceNonfunctionalAnnotationMap(dm.getServiceCollection().getServices());
					
					snam.generateNonfunctionRandomly(ExecutionTimeType.getInstance(),200, 50, 50, 30);
					
					// select 100 feature model configuration randomly for evaluation
					
					
					
					// for number of atomic set to number of atomic set *10 find the heuristic for estimation
					
					FeatureAtomicSetMap fasm = dm.getFeatureModel().findAtomicSets();
					
					
					
					int iterationNumber=200;
					
					List<FeatureModelConfiguration> adequateTrainingList = dm.getFeatureModel().generateRegressionConfigurations(1, fasm);
					
					List<AtomicSet> asl =  fasm.getAllAtomicSets(false);
					
					int numberOfAtomicSet = asl.size();
					
					List<FeatureModelConfiguration> testFmcs = dm.getFeatureModel().generateRandomFeatureModelConfiguration(100,adequateTrainingList);
					
					List<FeatureModelConfiguration> newExclusions = new ArrayList<FeatureModelConfiguration>();
					
					newExclusions.addAll(testFmcs);
					newExclusions.addAll(adequateTrainingList);
					
					List<FeatureModelConfiguration> additionalTrainingList = dm.getFeatureModel().generateRandomFeatureModelConfiguration(300,newExclusions);
					
					
					
					for(int sampleSizeCntr=0;sampleSizeCntr<iterationNumber;sampleSizeCntr=sampleSizeCntr+5){
						log.log("Current sample size: "+sampleSizeCntr);
						List<FeatureModelConfiguration> trainingList = new ArrayList<FeatureModelConfiguration>();
						trainingList.addAll(adequateTrainingList);
						trainingList.addAll(additionalTrainingList.subList(0,Math.min(sampleSizeCntr, additionalTrainingList.size())));

						Map<AtomicSet, Double> cv =  new HashMap<AtomicSet, Double>();
						
						double rSquared = dm.findAtomicSetContributionValueOLS(fasm, snam, ExecutionTimeType.getInstance(), trainingList, serviceMashupCache,cv);
						
						double[] actualYValue= new double[testFmcs.size()];
						double[] estimatedYValue= new double[testFmcs.size()];
						
						for(int testCntr =0; testCntr<testFmcs.size();testCntr++ ){
							
							actualYValue[testCntr] = dm.findFeatureModelConfigurationNonfunctionalValue(testFmcs.get(testCntr),ExecutionTimeType.getInstance(),snam.getAnnotationMap(),serviceMashupCache);
							
							estimatedYValue[testCntr] = testFmcs.get(testCntr).estimateNonfunctionalValue(fasm,cv,ExecutionTimeType.getInstance());
							
							
							
						}
						
						double[] errorsSquared =  new double[testFmcs.size()];
						double[] errors = new double[testFmcs.size()];
						
						for(int cntr=0; cntr<testFmcs.size();cntr++){
							errorsSquared[cntr]= Math.pow( actualYValue[cntr]-estimatedYValue[cntr],2);
							errors[cntr]= Math.abs( actualYValue[cntr]-estimatedYValue[cntr]);
						}
						
						double errSqrdAverage  = UtilityClass.findAverage(errorsSquared);
						double errAvg =  UtilityClass.findAverage(errors);
						
						double errSqRoot= Math.sqrt(errSqrdAverage);
						
						//System.out.println("Adaptation type: "+ar.getAdaptationType());
						curSheet.addCell(new Label(colOffset+1,row,curDir.toString()));
						curSheet.addCell(new Number(colOffset+2,row, iterationCntr));
						curSheet.addCell(new Number(colOffset+3,row, trainingList.size()));
						curSheet.addCell(new Number(colOffset+4,row, numberOfAtomicSet));
						curSheet.addCell(new Number(colOffset+5,row, curfmSizeList));
						curSheet.addCell(new Number(colOffset+6,row, errSqRoot));
						curSheet.addCell(new Number(colOffset+7,row, rSquared));
						curSheet.addCell(new Number(colOffset+8,row, UtilityClass.getStdDev(actualYValue)));
						curSheet.addCell(new Number(colOffset+9,row, errAvg));
						
						row++;
						
					}
					
					
					
					
					

					
					
					
					
					
					
					
					
//					evalCntr++;
					
					
					
				}
				
//				String beginRow = String.valueOf(lrow);
//				String endRow = String.valueOf(row-1);
				
//				curSheet.addCell(new Number(colOffsetTbl3+1,offsetTbl3,repositorySize[rcntr]));
//				curSheet.addCell(new Label(colOffsetTbl3+2,offsetTbl3,curDir.toString()));				
//				curSheet.addCell(new Formula(colOffsetTbl3+3, offsetTbl3,"AVERAGE(H"+beginRow+":H"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+4, offsetTbl3,"AVERAGE(H"+beginRow+":H"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+5, offsetTbl3,"AVERAGE(I"+beginRow+":I"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+6, offsetTbl3,"AVERAGE(I"+beginRow+":I"+endRow+")"));
				
//				curSheet.addCell(new Label(colOffsetTbl3+3, offsetTbl3,"AVERAGEIFS(H"+beginRow+":H"+endRow+",E"+beginRow+":E"+endRow+",\"=3\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+4, offsetTbl3,"AVERAGEIFS(H"+beginRow+":H"+endRow+",E"+beginRow+":E"+endRow+",\"=2\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+5, offsetTbl3,"AVERAGEIFS(I"+beginRow+":I"+endRow+",E"+beginRow+":E"+endRow+",\"=3\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+6, offsetTbl3,"AVERAGEIFS(I"+beginRow+":I"+endRow+",E"+beginRow+":E"+endRow+",\"=100\")"));
//				
				
//				offsetTbl3++;
				
				
			}
			
			
			
			
			
//			curSheet.addCell(new Label(colOffsetTbl1+1,offsetTbl1,String.valueOf(repositorySize[rcntr])));
//					
//			offsetTbl1++;
		
		
//		int cntr=0;
		
		
		wb.write();
		wb.close();
		log.close();
	}
	
	
	public static void runEvaluationFMSizeNF() throws Exception{
		WritableWorkbook wb = Workbook.createWorkbook(new File(homeAddress+ "evaluationFMSizeNF.xls"));
		WritableSheet curSheet = wb.createSheet("Evaluation", 1);
		
		String evaluationDirectory ="/home/mbashari/EVAL_FOLDER/evaluation/fmsize/smfamily/";
		int[] repositorySize = {400,800,1200,1600,2000};
		//int[] repositorySize = {400};
		int repositoryIterationSize = 1000;
		int fmcSize = 15;
		int fmcVariation =1;
		
		int offset =0;
		int colOffset=0;
		
		int offsetTbl2 =5;
		int colOffsetTbl2=40;
		
		int offsetTbl3 =5;
		int colOffsetTbl3=11;
		
		
		curSheet.addCell(new Label(colOffset+1,0,"Mashup Family Name"));
		curSheet.addCell(new Label(colOffset+2,0,"Configuration No"));
		curSheet.addCell(new Label(colOffset+3,0,"Response Time Constraint"));
		curSheet.addCell(new Label(colOffset+4,0,"Adaptation type"));
		curSheet.addCell(new Label(colOffset+5,0,"Number of Confs"));
		curSheet.addCell(new Label(colOffset+6,0,"Failure Response Time"));
		curSheet.addCell(new Label(colOffset+7,0,"Adaptation Response Time"));
		curSheet.addCell(new Label(colOffset+8,0,"Feature model reconfiguration"));
		curSheet.addCell(new Label(colOffset+9,0,"Feature Distance"));
		curSheet.addCell(new Label(colOffset+10,0,"Number of tries"));
		
		
		
//		curSheet.addCell(new Number(colOffsetTbl3+3,offsetTbl3,3));
//		curSheet.addCell(new Number(colOffsetTbl3+4,offsetTbl3,2));
//		curSheet.addCell(new Number(colOffsetTbl3+5,offsetTbl3,2));
//		curSheet.addCell(new Number(colOffsetTbl3+6,offsetTbl3,100));
//		offsetTbl3++;
//		
//		curSheet.addCell(new Label(colOffsetTbl3+1,offsetTbl3,"Number of Confs"));
//		curSheet.addCell(new Label(colOffsetTbl3+2,offsetTbl3,"Number of Possible Configuration"));
//		curSheet.addCell(new Label(colOffsetTbl3+3,offsetTbl3,"Average of Workflow Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl3+4,offsetTbl3,"Average of Workflow Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl3+5,offsetTbl3,"Average of Feature Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl3+6,offsetTbl3,"Average of Feature Adaptation (Failed)"));
//		offsetTbl3++;
//		
//		
//		curSheet.addCell(new Label(colOffsetTbl1+1,offsetTbl1,"Number of Possible Configuration"));
//		curSheet.addCell(new Label(colOffsetTbl1+2,offsetTbl1,"Number of Service Lookups"));
//		curSheet.addCell(new Label(colOffsetTbl1+3,offsetTbl1,"Average Service Lookup time"));
//		curSheet.addCell(new Label(colOffsetTbl1+4,offsetTbl1,"Number of Workflow Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+5,offsetTbl1,"Average of Workflow Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+6,offsetTbl1,"Number of Feature Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+7,offsetTbl1,"Average of Feature Adaptation (Successful)"));
//		curSheet.addCell(new Label(colOffsetTbl1+8,offsetTbl1,"Number of Workflow Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+9,offsetTbl1,"Average of Workflow Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+10,offsetTbl1,"Number of Feature Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+11,offsetTbl1,"Average of Feature Adaptation (Failed)"));
//		curSheet.addCell(new Label(colOffsetTbl1+12,offsetTbl1,"Service Lookup"));
//		curSheet.addCell(new Label(colOffsetTbl1+13,offsetTbl1,"Workflow Adaptation"));
//		curSheet.addCell(new Label(colOffsetTbl1+14,offsetTbl1,"Feature Adaptation "));
//		curSheet.addCell(new Label(colOffsetTbl1+15,offsetTbl1,"Adaptation Failure"));
//		
//		curSheet.addCell(new Label(colOffsetTbl2+1,offsetTbl2,"Number of Feature Change"));
//		curSheet.addCell(new Label(colOffsetTbl2+2,offsetTbl2,"Occurences"));
		
		Map<Integer,Integer> featureChangeOccurenceMap = new HashMap<Integer, Integer>();
		
		offsetTbl2++;
		
		offset++;
		int row = offset;
		
		for(int rcntr =0; rcntr<repositorySize.length; rcntr++){
			System.out.println(repositorySize[rcntr]);
			
//			long cntServiceLookup=0;
//			long sumServiceLookup=0;
//			long avgServiceLookup=0;
//			
//			long serviceLookupCntr=0;
//			
//			long cntPlanningDurationSucc=0;
//			long sumPlanningDurationSucc=0;
//			long avgPlanningDurationSucc=0;
//			
//			long cntPlanningDurationFail=0;
//			long sumPlanningDurationFail=0;
//			long avgPlanningDurationFail=0;
//			
//			long cntFeatureAdaptationDurationSucc=0;
//			long sumFeatureAdaptationDurationSucc=0;
//			long avgFeatureAdaptationDurationSucc=0;
//			
//			long cntFeatureAdaptationDurationFail=0;
//			long sumFeatureAdaptationDurationFail=0;
//			long avgFeatureAdaptationDurationFail=0;
			
			int evalCntr=0;
			
			File[] smFamilyDirectories = new File(evaluationDirectory+String.valueOf(repositorySize[rcntr])).listFiles(new FileFilter() {
				
				public boolean accept(File pathname) {
					
					return pathname.isDirectory();
				}
			});
			
			int maxSMIterationNo = repositoryIterationSize/smFamilyDirectories.length;
			
			
			for(File curDir: smFamilyDirectories){
				
				DomainModels dm = DomainModels.readFromDirectory(curDir);
				
				ServiceNonfunctionalAnnotationMap snam = new ServiceNonfunctionalAnnotationMap(dm.getServiceCollection().getServices());
				
				snam.generateNonfunctionRandomly(ExecutionTimeType.getInstance(),200, 50, 50, 30);
				ContextStateModel contextStateModelMain = new ContextStateModel(dm.getServiceCollection(), snam);
				
				
				
				
				
				
				FeatureAtomicSetMap fasm = dm.getFeatureModel().findAtomicSets();
				
				
				
				
				
				List<FeatureModelConfiguration> adequateTrainingList = dm.getFeatureModel().generateRegressionConfigurations(1, fasm);
				
				
				
				
				
				List<FeatureModelConfiguration> additionalTrainingList = dm.getFeatureModel().generateRandomFeatureModelConfiguration(100,adequateTrainingList);
				
				List<FeatureModelConfiguration> trainingList= new ArrayList<FeatureModelConfiguration>();
				
				
				trainingList.addAll(adequateTrainingList);
				trainingList.addAll(additionalTrainingList);
				
				Map<FeatureModelConfiguration,FlowComponentNode> trainingListMap = new HashMap<FeatureModelConfiguration, FlowComponentNode>();
				
				for(FeatureModelConfiguration fmc: trainingList){
					FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(dm, fmc, contextStateModelMain);
					try{
					FlowComponentNode fcn =	fmcmg.buildServiceMashup();
					trainingListMap.put(fmc, fcn);
					}
					catch(UnsuccessfulMashupGeneration ex){
						System.out.println("UNSUCCESSFUL MASHUP GENERATION!");
					}
					
					
				}
				
				
				
				
				
				
				
				List<FeatureModelConfiguration> fmcs = dm.getFeatureModel().getAllValidConfiguration(-1,fmcSize,fmcVariation);
				if(fmcs.size()==0)
					continue;
				
//				int lrow = row;
				
				for(int iterationCntr=0; iterationCntr<maxSMIterationNo; iterationCntr++ ){
					
					ContextStateModel contextStateModel = contextStateModelMain.clone();
					
					int featureModelConfigurationNo = UtilityClass.randInt(0, fmcs.size()-1);
					
					FeatureModelConfiguration fmc = fmcs.get(featureModelConfigurationNo);
					
					FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(dm, fmc, contextStateModel);
					
					
					FlowComponentNode fcn =	fmcmg.buildServiceMashup();
					
					Double currentNonfunctionalProperty = ExecutionTimeType.getInstance().getAggregatedValue(contextStateModel.getServiceNonfunctionalMap().getAnnotationMap(), fcn);
					
					NonfunctionalConstraint nfc  = new NonfunctionalConstraint(ExecutionTimeType.getInstance(), currentNonfunctionalProperty*1.1d, true);
					
					List<Service> mashupServices = fcn.findAllCalledServices();
					
					boolean serviceMashupFailed = false;
					Double currentResponseTime = 0d;
					Double updatedNonfunctionalProperty = 0d;
					
					while(!serviceMashupFailed){
						Service s = mashupServices.get(UtilityClass.randInt(0, mashupServices.size()-1));
						currentResponseTime = contextStateModel.getServiceNonfunctionalMap().getAnnotationMap().get(s).getAnnotation().get(ExecutionTimeType.getInstance()).getAverage();
						
						contextStateModel.getServiceNonfunctionalMap().getAnnotationMap().get(s).getAnnotation().put(ExecutionTimeType.getInstance(), new NonfunctionalMetric(ExecutionTimeType.getInstance(), currentResponseTime*1.25, 0d));
						
						updatedNonfunctionalProperty = ExecutionTimeType.getInstance().getAggregatedValue(contextStateModel.getServiceNonfunctionalMap().getAnnotationMap(), fcn);
						
						if(!nfc.satisfy(updatedNonfunctionalProperty))
							serviceMashupFailed = true;
						
					}
					
					
					
					
					
					
					
					//System.out.println(  iterationCntr+"/" + maxSMIterationNo + " Rep: "+String.valueOf(repositorySize[rcntr])+ " SM: "+ curDir.getName() +" No Of Conf:" + featureModelConfigurationNo +" Services: "+ mashupServices.size()+" Service Name: "+ s.getName());
					
					//System.out.println("Failed service: "+s.getName());
					
					
					
					List<NonfunctionalConstraint> nfcList = new ArrayList<NonfunctionalConstraint>();
					nfcList.add(nfc);
					
					long curTime = System.currentTimeMillis();
					Holder<Integer> holder = new Holder<Integer>(0);				
					FeatureModelConfiguration alternateFMC =  fmc.findAlternateConfigurationNF(dm,contextStateModel,nfcList,trainingListMap,fasm, holder);
					
					long duration = System.currentTimeMillis()-curTime;
					
					
					
					
					
					
					
					//System.out.println("Adaptation type: "+ar.getAdaptationType());
					curSheet.addCell(new Label(colOffset+1,row,curDir.toString()));
					curSheet.addCell(new Label(colOffset+2,row, String.valueOf(featureModelConfigurationNo)));
					curSheet.addCell(new Number(colOffset+3,row, nfc.getThreshold()));
					
					curSheet.addCell(new Number(colOffset+4,row,(alternateFMC==null)?0:1));
					curSheet.addCell(new Number(colOffset+5,row,repositorySize[rcntr]));
					curSheet.addCell(new Number(colOffset+6,row,updatedNonfunctionalProperty));
					curSheet.addCell(new Number(colOffset+8,row,duration));
					if(alternateFMC!=null){
						
						fmcmg = new FeatureModelConfigurationMashupGeneration(dm, alternateFMC, contextStateModel);
						
						
						fcn =	fmcmg.buildServiceMashup();
						
						Double adaptedNonfunctionalProperty = ExecutionTimeType.getInstance().getAggregatedValue(contextStateModel.getServiceNonfunctionalMap().getAnnotationMap(), fcn);
						
						
						curSheet.addCell(new Number(colOffset+7,row,adaptedNonfunctionalProperty));
						
						curSheet.addCell(new Number(colOffset+9,row,dm.getFeatureModel().getDistance(fmcs.get(featureModelConfigurationNo),alternateFMC)));
						curSheet.addCell(new Number(colOffset+10,row,holder.value));
						
					}
					
					
					
					
					row++;
					
					evalCntr++;
					
					
					
				}
				
//				String beginRow = String.valueOf(lrow);
//				String endRow = String.valueOf(row-1);
				
				curSheet.addCell(new Number(colOffsetTbl3+1,offsetTbl3,repositorySize[rcntr]));
				curSheet.addCell(new Label(colOffsetTbl3+2,offsetTbl3,curDir.toString()));				
//				curSheet.addCell(new Formula(colOffsetTbl3+3, offsetTbl3,"AVERAGE(H"+beginRow+":H"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+4, offsetTbl3,"AVERAGE(H"+beginRow+":H"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+5, offsetTbl3,"AVERAGE(I"+beginRow+":I"+endRow+")"));
//				curSheet.addCell(new Formula(colOffsetTbl3+6, offsetTbl3,"AVERAGE(I"+beginRow+":I"+endRow+")"));
				
//				curSheet.addCell(new Label(colOffsetTbl3+3, offsetTbl3,"AVERAGEIFS(H"+beginRow+":H"+endRow+",E"+beginRow+":E"+endRow+",\"=3\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+4, offsetTbl3,"AVERAGEIFS(H"+beginRow+":H"+endRow+",E"+beginRow+":E"+endRow+",\"=2\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+5, offsetTbl3,"AVERAGEIFS(I"+beginRow+":I"+endRow+",E"+beginRow+":E"+endRow+",\"=3\")"));
//				curSheet.addCell(new Label(colOffsetTbl3+6, offsetTbl3,"AVERAGEIFS(I"+beginRow+":I"+endRow+",E"+beginRow+":E"+endRow+",\"=100\")"));
//				
				
				offsetTbl3++;
				
				
			}
			
			
		
			
			
		
		}
		
//		int cntr=0;
		for(Integer fno: featureChangeOccurenceMap.keySet()){
			curSheet.addCell(new Label(colOffsetTbl2+1,offsetTbl2,String.valueOf(fno)));
			curSheet.addCell(new Label(colOffsetTbl2+2,offsetTbl2, String.valueOf(featureChangeOccurenceMap.get(fno))));
			offsetTbl2++;
		}
		
		wb.write();
		wb.close();
		
	}
	
	
	public static void compareConfigurations() throws Exception
	{
		DomainModels dm = DomainModels.readFromDirectory(new File("/home/mbashari/EVAL_FOLDER/evaluation/fmsize/smfamily/1200/1024"));
		
		List<FeatureModelConfiguration> fmcs = dm.getFeatureModel().getAllValidConfiguration(-1);
		System.out.println("All possible configuration: "+fmcs.size());
		
		
		FeatureAtomicSetMap fasm = dm.getFeatureModel().findAtomicSets();
		List<FeatureModelConfiguration> adequateTrainingList = dm.getFeatureModel().generateRegressionConfigurations(1, fasm);
		
		List<FeatureModelConfiguration> additionalTrainingList = dm.getFeatureModel().generateRandomFeatureModelConfiguration(1024,adequateTrainingList);
		
		System.out.println("Training Set Size: "+ adequateTrainingList.size()+ "Additional Set:"+ additionalTrainingList.size());
	}
	
	
	public static void testSMFamily() throws Exception
	{
		DomainModels dm = DomainModels.readFromDirectory(new File("/home/mbashari/evalpkg/240/sm1"));
		
		FeatureModelConfiguration fmc = dm.getFeatureModel().getARandomConfiguration();
		
		FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(dm, fmc);
		
		FlowComponentNode fcn =  fmcmg.buildServiceMashup();
		System.out.println(fcn.serializeToGV());
		
	}
	
	
}



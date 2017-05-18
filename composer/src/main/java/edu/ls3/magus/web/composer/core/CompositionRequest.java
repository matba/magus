package edu.ls3.magus.web.composer.core;





import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;

import edu.ls3.magus.cl.contextmanager.context.ContextModel;
import edu.ls3.magus.cl.exceptions.UnsuccessfulMashupGeneration;
import edu.ls3.magus.cl.fmconfigurator.ContextStateModel;
import edu.ls3.magus.cl.fmconfigurator.DomainModels;
import edu.ls3.magus.cl.fmconfigurator.FeatureModelConfigurationMashupGeneration;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModel;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModelConfiguration;

import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.FlowComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.OperationNode;

import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.cl.mashupconfigurator.service.ServiceCollection;


public class CompositionRequest {
	private String ontologyXml;
	private String featureModelXml ;
	private String[] serviceAnnotationXmls;
	private String[] selectedFeatures;
	private DomainModels requestDomainModel;
	private FeatureModelConfiguration featureModelConfiguration;
	private ContextStateModel contextStateModel;
	private StringBuilder requestLog ;
	private int progressedStep;
	private String workflowJSON;
	private String bpelXML;
	private String[] usedServiceURIs;
	//private final static String deploymentAddress="http://magus.online/";
	//private final static String deploymentAddress="http://localhost:8080/composer/";
	
	

	
	public CompositionRequest(String ontologyXml,String featureModelXml,String[] serviceAnnotationXmls,String[] selectedFeatures,String[] availableServiceURIs) throws Exception{
		setRequestLog(new StringBuilder());
		long curTime = System.currentTimeMillis();
		this.setOntologyXml(ontologyXml);
		
		this.featureModelXml = featureModelXml;
		this.serviceAnnotationXmls = serviceAnnotationXmls;
		this.selectedFeatures = selectedFeatures;
		this.requestDomainModel = new DomainModels();
		
		this.requestDomainModel.setContextModel(new ContextModel());
		this.requestDomainModel.setServiceCollection(new ServiceCollection());
		
		this.contextStateModel  = new ContextStateModel();
		
		getRequestLog().append("Reading Context Models..."+System.lineSeparator() );
		
		
		requestDomainModel.getContextModel().AddToContextModel(ontologyXml);
		getRequestLog().append("Context Model read. "+System.lineSeparator() );
		
		
		getRequestLog().append("Reading Service Annotations..."+System.lineSeparator() );
		
		for(String saXml:this.serviceAnnotationXmls){
			
			List<Service> newServices = Service.parseService(saXml, this.requestDomainModel.getContextModel());
			this.requestDomainModel.getServiceCollection().getServices().addAll(newServices);
			for(Service sv: newServices)
				this.contextStateModel.getServiceAvailabilty().put(sv.getURI(), false);
			getRequestLog().append("Service Annotations read: "+newServices.get(0).getName()+System.lineSeparator() );
			
		}
		
		for(String st: availableServiceURIs){
			this.contextStateModel.getServiceAvailabilty().put(st, true);
		}
		
		getRequestLog().append("Reading Feature Model and its annotations..."+System.lineSeparator() );
		
		requestDomainModel.setFeatureModel(FeatureModel.parse(this.featureModelXml, this.requestDomainModel.getFeatureModelAnnotation(),this.requestDomainModel.getContextModel()));
		
		getRequestLog().append("Feature Model Read"+System.lineSeparator() );
		
		featureModelConfiguration = new FeatureModelConfiguration(this.selectedFeatures, this.requestDomainModel.getFeatureModel());
		
		
		long duration = System.currentTimeMillis()-curTime;
		
		getRequestLog().append("Reading Models Duration: "+duration +"ms." +System.lineSeparator() );
		
		setProgressedStep(0);
		
	}
	
	
	public void CallPlanner() throws Exception{
		
		FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(this.requestDomainModel, this.featureModelConfiguration,contextStateModel);
		
		long curTime = System.currentTimeMillis();
		getRequestLog().append("Converting Domain Model to PDDL..."+System.lineSeparator() );
		
		DomainModels dm = requestDomainModel;
		
		
		
		fmcmg.convertToPDDL();
		
		long duration = System.currentTimeMillis()-curTime;

		getRequestLog().append("Converting Domain Model to PDDL: "+duration +"ms." +System.lineSeparator() );
		
		getRequestLog().append("Starting Planning..."+System.lineSeparator() );

		curTime = System.currentTimeMillis();
		List<String[]> rawplan = null;
		try{
			rawplan =fmcmg.Callplanner();
		}
		catch(UnsuccessfulMashupGeneration ex){
			getRequestLog().append("Planner was unsuccessfull to find any solution"+System.lineSeparator() );
			return ;
		}
		duration = System.currentTimeMillis()-curTime;

		getRequestLog().append("Planning Finished: "+duration +"ms." +System.lineSeparator() );
		
		System.out.println("Planning time: "+ duration);

		
		setProgressedStep(getProgressedStep() + 1);
		
		getRequestLog().append("Planning was successfull. Raw plan:"+System.lineSeparator() );
		
		for(String[] sl :rawplan)
		{
			for(String st: sl)
				getRequestLog().append(st+" ");
			
			getRequestLog().append(System.lineSeparator() );
		}
		
		getRequestLog().append("Analyzing returned plan..."+System.lineSeparator() );
		
		List<OperationNode> optimizedGraph = fmcmg.AnalyzePlan();

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
		
		getRequestLog().append("Analyzing Finished."+System.lineSeparator() );
		
		getRequestLog().append("Starting Optimization."+System.lineSeparator() );
		
		curTime = System.currentTimeMillis();
		
		optimizedGraph = fmcmg.OptimizeGraph();
		
		List<String> usedServices = new ArrayList<String>();
		for(OperationNode on : optimizedGraph){
			String curServiceURI = on.getCalledService().getCalledService().getURI();
			if(curServiceURI.length()==0)
				continue;
			if(!usedServices.contains(curServiceURI))
				usedServices.add(curServiceURI);
		}
		usedServiceURIs= usedServices.toArray(new String[0]);
		
		duration = System.currentTimeMillis()-curTime;

		getRequestLog().append("Optimization Finished: "+duration +"ms." +System.lineSeparator() );



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
		getRequestLog().append("The optimized workflow has " +String.valueOf(rawplan.size()+2)+ "operation node and "+cnt+" edges." +System.lineSeparator() );
		
		//String graphGV = OperationNode.serializedToGV(optimizedGraph);
		setWorkflowJSON(OperationNode.serializedToJSON(optimizedGraph));
		
		 
		setProgressedStep(getProgressedStep() + 1);
//		edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "test.gv"),graphGV);
//		edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "test.json"),graphJson);

//		String gs = OperationNode.serialize(optimizedGraph);
//		edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "wf1.txt"),gs);

//		System.out.println("Trying Optimization No 1");
//		ComponentNode n = OperationNode.BPELAlgorithmNo1(optimizedGraph);
//	
//		String nstr = n.serializeToGV(true);
//		edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "workflowreturn1.gv"),nstr);
//		
//		System.out.println("Flows "+n.GetNoOfFlows()+" Sequence "+ n.GetNoOfSequence() + " Link "+ n.GetNoOfLink());
//		
//		//List<OperationNode> workflow1 = n.convertToWorkflow();
//		//String gf = OperationNode.serializedToGV(workflow1);
//		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "workflowreturn1.gv"),gf);
//
//
//		System.out.println("Trying Optimization No 2");
//		FlowComponentNode fcn =FlowComponentNode.convertToFlowWithLink(optimizedGraph);
//		fcn.OptimizeNo1();
//		  
//		System.out.println("Flows "+fcn.GetNoOfFlows()+" Sequence "+ fcn.GetNoOfSequence() + " Link "+ fcn.GetNoOfLink());
//		
//		nstr = fcn.serializeToGV(true);
//		edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "workflowreturn2.gv"),nstr);
		
		//List<OperationNode> workflow2 = fcn.convertToWorkflow();
		//gf = OperationNode.serializedToGV(workflow2);
		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "workflowreturn2.gv"),gf);
		
		
		System.out.println("Trying BPEL Generation ");
		getRequestLog().append("Starting Conversion to BPEL."+System.lineSeparator() );
		FlowComponentNode fcn1 =fmcmg.GenerateBPEL();
		
		
		duration = System.currentTimeMillis()-curTime;
		
		this.setBpelXML(fcn1.serializeToXML(dm.getFeatureModelAnnotation().findEntities(this.featureModelConfiguration)));
		getRequestLog().append("Conversion to BPEL Finished: "+duration +"ms." +System.lineSeparator() );
		
		getRequestLog().append("Structure of Generated BPEL"+System.lineSeparator()+"Flows: "+fcn1.GetNoOfFlows()+" Sequence: "+ fcn1.GetNoOfSequence() + " Link: "+ fcn1.GetNoOfLink()+System.lineSeparator() );

		System.out.println("Flows "+fcn1.GetNoOfFlows()+" Sequence "+ fcn1.GetNoOfSequence() + " Link "+ fcn1.GetNoOfLink());

		setProgressedStep(getProgressedStep() + 1);
		//nstr = fcn1.serializeToGV(true);
		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "workflowreturn3.gv"),nstr);
		//List<OperationNode> workflow3 = fcn1.convertToWorkflow();
		// gf = OperationNode.serializedToGV(workflow3);
		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "workflowreturn3.gv"),gf);

		//String s = fcn1.serializeToGV(true);
		//edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File(homeAddress+ "testblock.gv"),s);
		//    	System.out.println("Execution Time(After Optimization): "+ ExecutionTime.findExecutionTime(annotation, optimizedGraph));
		//    	

		//UtilityClass.writeFile(new File("D:\\ao.txt"), GraphNode.serialize(optimizedGraph,dm));
		
	}
	
	


	public String getWorkflowJSON() {
		return workflowJSON;
	}


	public void setWorkflowJSON(String workflowJSON) {
		this.workflowJSON = workflowJSON;
	}


	public int getProgressedStep() {
		return progressedStep;
	}


	public void setProgressedStep(int progressedStep) {
		this.progressedStep = progressedStep;
	}


	public String getBpelXML() {
		return bpelXML;
	}


	public void setBpelXML(String bpelXML) {
		this.bpelXML = bpelXML;
	}


	public StringBuilder getRequestLog() {
		return requestLog;
	}


	private void setRequestLog(StringBuilder requestLog) {
		this.requestLog = requestLog;
	}


	public String[] getUsedServiceURIs() {
		return usedServiceURIs;
	}


	public void setUsedServiceURIs(String[] usedServiceURIs) {
		this.usedServiceURIs = usedServiceURIs;
	}


	public String getOntologyXml() {
		return ontologyXml;
	}


	public void setOntologyXml(String ontologyXml) {
		this.ontologyXml = ontologyXml;
	}
}

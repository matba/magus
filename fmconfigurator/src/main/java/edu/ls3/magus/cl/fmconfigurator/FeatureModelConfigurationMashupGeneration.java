package edu.ls3.magus.cl.fmconfigurator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.ls3.magus.cl.contextmanager.basic.Condition;
import edu.ls3.magus.cl.contextmanager.basic.Instance;
import edu.ls3.magus.cl.contextmanager.basic.InstanceType;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstanceS;
import edu.ls3.magus.cl.contextmanager.basic.StateFactType;
import edu.ls3.magus.cl.exceptions.EmptyEffects;
import edu.ls3.magus.cl.exceptions.UnsuccessfulMashupGeneration;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModelConfiguration;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.FlowComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.OperationNode;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.ServiceCall;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.cl.mashupconfigurator.service.ServiceCollection;
import edu.ls3.magus.cl.planning.Problem;
import edu.ls3.magus.cl.planning.ProblemDomain;
import edu.ls3.magus.configuration.Configuration;
import edu.ls3.magus.utility.UtilityClass;

public class FeatureModelConfigurationMashupGeneration {


	private FeatureModelConfiguration fmc;
	private DomainModels dm;
	private ContextStateModel contextStateModel;

	private String problemAddress;
	private String domainAddress;

	private List<Instance> planningProblemInstances;
	private Condition planningProblemPreconditions ;
	private Condition planningProblemEffects ;
	private List<String[]> rawPlan;
	private List<OperationNode> serviceMashupWorkflow;
	private FlowComponentNode serviceMashupBPELProcess;

	public FeatureModelConfigurationMashupGeneration( DomainModels dm, FeatureModelConfiguration fmc){
		this.dm =dm;
		this.fmc = fmc;
		this.contextStateModel = new ContextStateModel();

	}

	public FeatureModelConfigurationMashupGeneration( DomainModels dm, FeatureModelConfiguration fmc,ContextStateModel contextStateModel){
		this.dm =dm;
		this.fmc = fmc;
		this.contextStateModel = contextStateModel;
	}

	public FlowComponentNode buildServiceMashup() throws Exception {
		try{
			this.convertToPDDL();
		}
		catch(EmptyEffects ee){
			return new FlowComponentNode();
		}
		this.Callplanner();
		this.AnalyzePlan();
		this.OptimizeGraph();
		return this.GenerateBPEL();

	}

	public List<String> convertToPDDL() throws EmptyEffects, Exception{

		planningProblemPreconditions = dm.getFeatureModelAnnotation().findPrecondition(this.fmc);
		planningProblemEffects =  dm.getFeatureModelAnnotation().findEffect(this.fmc);

		if(planningProblemEffects.getConditions().isEmpty())
			throw new EmptyEffects();
		planningProblemInstances = dm.getFeatureModelAnnotation().findEntities(this.fmc, dm.getContextModel());
		List<InstanceType> insType = Instance.getAllTypes(planningProblemInstances);
		ServiceCollection svList = dm.getServiceCollection();
		if(contextStateModel.getServiceAvailabilty().size()>0){
			svList = svList.FilterByAvailability(contextStateModel.getServiceAvailabilty());
		}
		List<InstanceType> negpType =svList.GetAllNotPreconditionVars(insType);

		for(InstanceType it : negpType){
			Instance newIt = new Instance(it, "vvdummy"+it.getTypeName(), new URI( "http://bashari.ca/magus/#"+"vvdummy"+it.getTypeName()));
			dm.getContextModel().getInstances().add(newIt);
		}
		planningProblemInstances = dm.getFeatureModelAnnotation().findEntities(this.fmc, dm.getContextModel());
		insType = Instance.getAllTypes(planningProblemInstances);
		Problem pr = new Problem(planningProblemInstances, planningProblemPreconditions, planningProblemEffects);

		String problemPDDL = pr.PDDL3Serialize(null);
		 svList =  svList.FilterByUsedType(insType);

		ProblemDomain pd = new ProblemDomain(dm.getContextModel(),svList);

		List<StateFactType> sftl = svList.getAllUsedFactTypes();

		for(StateFactInstanceS sfis:planningProblemPreconditions.getConditions() )
			if(!sftl.contains(sfis.getStateFactInstance().getType()))
				sftl.add(sfis.getStateFactInstance().getType());
		for(StateFactInstanceS sfis:planningProblemEffects.getConditions() )
			if(!sftl.contains(sfis.getStateFactInstance().getType()))
				sftl.add(sfis.getStateFactInstance().getType());
		String problemDomainPDDL =  pd.PDDL3Serialize(null,sftl,insType);

		this.problemAddress = Configuration.tempFolder+ "pt.pddl";
		this.domainAddress = Configuration.tempFolder+ "pta.pddl";

		UtilityClass.writeFile(new File(problemAddress),problemPDDL);
		UtilityClass.writeFile(new File(domainAddress),problemDomainPDDL);

		List<String> result = new ArrayList<String>();

		result.add(problemAddress);
		result.add(domainAddress);

		return result;

	}


	public List<String[]> Callplanner() throws UnsuccessfulMashupGeneration, IOException, InterruptedException {

		rawPlan = new ArrayList<String[]>();

		Process p = Runtime.getRuntime().exec(Configuration.plannerAddress+ "FF-v2.32/ff -o "+this.domainAddress+" -f "+this.problemAddress);
		p.waitFor();

		BufferedReader reader =
				new BufferedReader(new InputStreamReader(p.getInputStream()));

		String line = "";
		boolean inPlan =false;
		boolean planRead = false;
		while ((line = reader.readLine())!= null) {
			//System.out.println(line);
			if(line.equals("**PLAN**")){
				inPlan=true;
				continue;
			}
			if(line.equals("**PLANEND**")){
				inPlan=false;
				planRead =true;
			}
			if(inPlan){
				rawPlan.add(line.split(" "));
			}

		}

		if(!planRead || (rawPlan.size()==0) )
		{
			//System.out.println("Plan not found!");
			throw new UnsuccessfulMashupGeneration();
			//throw new Exception("Plan not found!");

		}
		return rawPlan;
	}

	public List<OperationNode> AnalyzePlan() throws Exception{
		List<ServiceCall> plan  = new ArrayList<ServiceCall>();


		for(String[] sl :rawPlan)
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

				for(Instance n: planningProblemInstances)
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

				for(Instance n: planningProblemInstances)
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

				for(Instance n: planningProblemInstances)
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

		boolean deletableServiceFound=true;
		while(deletableServiceFound){
			deletableServiceFound = false;

			for(int pcnt=0; pcnt< plan.size();pcnt++){
				List<ServiceCall> newplan  = new ArrayList<ServiceCall>();
				newplan.addAll(plan);
				newplan.remove(pcnt);
				List<OperationNode> optimizedGraph = OperationNode.convertToGraph(newplan, planningProblemPreconditions, planningProblemEffects);
				if(OperationNode.safe2( optimizedGraph))
				{
					deletableServiceFound = true;
					plan.remove(pcnt);
					break;
				}
			}

		}





		//    	for(ServiceCall sc:plan)
		//    		System.out.println(sc.getCalledService().getName());

		setServiceMashupWorkflow(OperationNode.convertToGraph(plan, planningProblemPreconditions, planningProblemEffects));
		return getServiceMashupWorkflow();
	}


	public List<OperationNode> OptimizeGraph() throws Exception {

		OperationNode.optimizeNew(getServiceMashupWorkflow());
		return getServiceMashupWorkflow();
	}


	public FlowComponentNode GenerateBPEL() {

		setServiceMashupBPELProcess(FlowComponentNode.convertToFlowWithLink(getServiceMashupWorkflow()));



		getServiceMashupBPELProcess().OptimizeNo2();
		return getServiceMashupBPELProcess();
	}

	public List<OperationNode> getServiceMashupWorkflow() {
		return serviceMashupWorkflow;
	}

	private void setServiceMashupWorkflow(List<OperationNode> serviceMashupWorkflow) {
		this.serviceMashupWorkflow = serviceMashupWorkflow;
	}

	public FlowComponentNode getServiceMashupBPELProcess() {
		return serviceMashupBPELProcess;
	}

	private void setServiceMashupBPELProcess(FlowComponentNode serviceMashupBPELProcess) {
		this.serviceMashupBPELProcess = serviceMashupBPELProcess;
	}
}

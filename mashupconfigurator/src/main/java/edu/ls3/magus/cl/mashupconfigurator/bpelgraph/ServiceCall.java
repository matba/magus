package edu.ls3.magus.cl.mashupconfigurator.bpelgraph;

import java.net.URI;
import java.util.*;

import edu.ls3.magus.cl.contextmanager.basic.Condition;
import edu.ls3.magus.cl.contextmanager.basic.Instance;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstance;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstanceS;
import edu.ls3.magus.cl.mashupconfigurator.service.*;

public class ServiceCall {
	private Service calledService;
	private Map<URI,Instance> inputs  = new HashMap<URI,Instance>();
	private Map<URI,Instance> outputs = new HashMap<URI,Instance>();
	private Map<URI,Instance> vars = new HashMap<URI,Instance>();
	private Condition preconditions = null;
	private Condition postconditions = null;
	
	
	public ServiceCall(){
		
	}
	public ServiceCall(Service calledService, Map<URI,Instance> inputs, Map<URI,Instance> outputs,  Map<URI,Instance> vars){
		this.setCalledService(calledService);
		this.setInputs(inputs);
		this.setOutputs(outputs);
		this.setVars(vars);
	}
	
	public ServiceCall(String name, Condition preconditions, Condition postconditions)
	{
		Service s = new Service(name, null, null, null, null, null, null, null,null,null, null,null,"");
		this.setCalledService(s);
		this.preconditions = preconditions;
		this.postconditions = postconditions;
	}
	
	public Service getCalledService() {
		return calledService;
	}
	public void setCalledService(Service calledService) {
		this.calledService = calledService;
	}
	
	private Condition getPreconditionsH() throws Exception{
		return conditionAllocation(calledService.getPrecondition());
	}
	private Condition getPostConditionH() throws Exception{
		
		return conditionAllocation(calledService.getPostcondition());
	}
	
	private Condition conditionAllocation(Condition input) throws Exception{
		List<StateFactInstanceS> result = new ArrayList<StateFactInstanceS>();
		for(StateFactInstanceS c: input.getConditions())
		{
			List<Instance> params = new ArrayList<Instance>();
			
			
			for(Instance i : c.getStateFactInstance().getParams()){
				URI r = null;
				for(URI u : calledService.getInputs().keySet()){
					if(calledService.getInputs().get(u).equals(i))
					{
						r=u;
						break;
					}
				}
				if(r !=null){
					params.add(getInputs().get(r));
				}else{
					for(URI u : calledService.getOutputs().keySet()){
						if(calledService.getOutputs().get(u).equals(i))
						{
							r=u;
							break;
						}
					}
					if(r != null)
						params.add(getOutputs().get(r));
					else{
						for(URI u : calledService.getVars().keySet()){
							if(calledService.getVars().get(u).equals(i))
							{
								r=u;
								break;
							}
						}
						if(r != null)
							params.add(getVars().get(r));
						else{
							for(URI u : calledService.getContextVars().keySet()){
								if(calledService.getContextVars().get(u).equals(i))
								{
									r=u;
									break;
								}
							}
							if(r != null)
								params.add(calledService.getContextVars().get(r));
							else{
								throw new Exception("I couldnt find the parameter" + i.getName());
							}
						}
					}
				}
						
					
				
				
				
			}
			StateFactInstance sti = new StateFactInstance(c.getStateFactInstance().getType(), params.toArray( new Instance[0]));
			result.add(new StateFactInstanceS(sti, c.isNot()) );
		}
		return  new Condition(result);
	}
	
	public void initConditions() throws Exception{
		setPreconditions(getPreconditionsH());
		setPostconditions(getPostConditionH());
	}
	public Condition getPreconditions() {
		return preconditions;
	}
	private void setPreconditions(Condition preconditions) {
		this.preconditions = preconditions;
	}
	public Condition getPostconditions() {
		return postconditions;
	}
	private void setPostconditions(Condition postconditions) {
		this.postconditions = postconditions;
	}
	public Map<URI,Instance> getInputs() {
		return inputs;
	}
	private void setInputs(Map<URI,Instance> inputs) {
		this.inputs = inputs;
	}
	public Map<URI,Instance> getOutputs() {
		return outputs;
	}
	private void setOutputs(Map<URI,Instance> outputs) {
		this.outputs = outputs;
	}
	public Map<URI,Instance> getVars() {
		return vars;
	}
	private void setVars(Map<URI,Instance> vars) {
		this.vars = vars;
	}
	
}

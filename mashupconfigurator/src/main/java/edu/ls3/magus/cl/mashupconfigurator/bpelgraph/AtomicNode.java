package edu.ls3.magus.cl.mashupconfigurator.bpelgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.ls3.magus.cl.contextmanager.basic.Instance;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;

public class AtomicNode extends Node {

	private OperationNode operationNode;
	private boolean isFakeNode;
	private String id ;

	public OperationNode getOperationNode() {
		return operationNode;
	}

	public void setOperationNode(OperationNode operationNode) {
		this.operationNode = operationNode;
		
	}
	
	public AtomicNode(OperationNode operationNode)
	{
		this.operationNode = operationNode;
		setFakeNode(false);
	}
	
	public AtomicNode(boolean isFakeNode) {
		
		this.isFakeNode = isFakeNode;
		if(isFakeNode)
			id = "x"+ UUID.randomUUID().toString().substring(0, 5);
	}
	public String getID(){
		String dname = "emptynode";
		if(isFakeNode){
			dname = id;
		}
		else{
			
			if(getOperationNode().getCalledService()!=null)
			{
				dname = getOperationNode().getCalledService().getCalledService().getName().replaceAll("Service", "")+getOperationNode().getUuid().substring(0, 3);
			}
		}
		return dname;
	}
	
	@Override
	public AtomicNode getEndNode() {
		
		return this;
	}

	@Override
	public AtomicNode getStartNode() {
		
		return this;
	}

	public boolean isFakeNode() {
		return isFakeNode;
	}

	public void setFakeNode(boolean isFakeNode) {
		this.isFakeNode = isFakeNode;
	}

	@Override
	public String serializeToXMLComponent(String tab, List<Link> plinks) {
		StringBuilder result = new StringBuilder();
		
		
		
		if(!this.isFakeNode)
		{
			if(this.getOperationNode().getCalledService().getCalledService().getInvocationService()!=null){
				ServiceCall cs = this.getOperationNode().getCalledService();
				String sb = createAttribute("variable", cs.getOutputs().values());
				
				
				result.append(tab+"<bpel:receive operation=\""+cs.getCalledService().getInvocationService().getName()+ "\" partnerLink=\""+cs.getCalledService().getName()+ "\" "+sb+ ">"+System.lineSeparator());
				
				result.append(this.addSourceAndTargetLinks(tab,plinks));
				result.append(tab+"</bpel:receive>"+System.lineSeparator());
			}
			else{
				ServiceCall cs = this.getOperationNode().getCalledService();
				String sbi = createAttribute("inputVariable",cs.getInputs().values());
				String sbo = createAttribute("outputVariable",cs.getOutputs().values());
				
				
				
				result.append(tab+"<bpel:invoke operation=\""+cs.getCalledService().getName()+ "\" partnerLink=\""+cs.getCalledService().getName()+ "\" "+sbi+ "  "+sbo+ ">"+System.lineSeparator());
				
				result.append(this.addSourceAndTargetLinks(tab,plinks));
				result.append(tab+"</bpel:invoke>"+System.lineSeparator());
			}
		}
		return result.toString();
	}

	
	protected String createAttribute(String attributeName, Iterable<Instance> ins ) {
		
		StringBuilder sb = new StringBuilder();
		String stStr = " "+attributeName+"=\"";
		String endStr ="";
		String seperator ="";
		for(Instance i: ins){
			sb.append(stStr);
			sb.append(seperator);
			sb.append(i.getName());
			seperator=",";
			stStr="";
			endStr ="\" ";
		}
		sb.append(endStr);
		return sb.toString();
	}

	@Override
	public List<Service> findAllCalledServices() {
		
		List<Service> result = new ArrayList<Service>();
		if(!isFakeNode)
			if(getOperationNode().getCalledService().getCalledService().getInvocationService()==null)
				result.add(getOperationNode().getCalledService().getCalledService());
		return result;
	}
	

}

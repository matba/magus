package edu.ls3.magus.cl.mashupconfigurator.bpelgraph;

import java.util.ArrayList;
import java.util.List;

import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.cl.mashupconfigurator.service.ServiceCollection;
import edu.ls3.magus.utility.UtilityClass;

public class SequenceComponentNode extends ComponentNode {
	@Override
	public AtomicNode getEndNode() {
		
		return getNodes().get(getNodes().size()-1).getEndNode();
	}
	@Override
	public AtomicNode getStartNode() {
		
		return getNodes().get(0).getStartNode();
	}
	public static Node createARandomSequence(int no, List<Service> usedServices,ServiceCollection sc) throws Exception{
		if(no < UtilityClass.CuttedRandValueNormal(3, 2,2)){
			if(no==1){
				 
				Service s=sc.getServices().get(UtilityClass.randInt(0, sc.getServices().size()-1));
				while(usedServices.contains(s))
					s= sc.getServices().get(UtilityClass.randInt(0, sc.getServices().size()-1));
				usedServices.add(s);
				ServiceCall calledService = new ServiceCall(s, null, null,null);
				OperationNode on = new OperationNode(calledService, false, false);
				
				return new AtomicNode(on);
			}
			else{
				SequenceComponentNode fcn = new SequenceComponentNode();
				for(int cnt=0; cnt<no; cnt++){
					Service s=sc.getServices().get(UtilityClass.randInt(0, sc.getServices().size()-1));
					while(usedServices.contains(s))
						s= sc.getServices().get(UtilityClass.randInt(0, sc.getServices().size()-1));
					usedServices.add(s);
					ServiceCall calledService = new ServiceCall(s, null, null,null);
					OperationNode on = new OperationNode(calledService, false, false);
					fcn.getNodes().add( new AtomicNode(on));
				}
				return fcn;
			}
		}
		else{
			int noOfDevisions = (int) UtilityClass.CuttedRandValueNormal(2, 2,2);
			int remaining =no;
			int devisionCounter = 0;
			SequenceComponentNode fcn = new SequenceComponentNode();
			while(remaining>0){
				int nextbunch = (int) UtilityClass.CuttedRandValueNormal(no/noOfDevisions, Math.max(no/(noOfDevisions*5),1), 1);
				devisionCounter ++;
				if(remaining<nextbunch)
					nextbunch= remaining;
				if((devisionCounter==1)&&(nextbunch==remaining))
					nextbunch= remaining/2;
				fcn.getNodes().add(FlowComponentNode.createARandomFlow(nextbunch,usedServices,sc));
				remaining-= nextbunch;
			}
		
				
			
			
			
			return fcn;
			
			
		}
		
	}
	
	@Override
	public List<AtomicNode> getRealStartNodes(){
		List<AtomicNode> result = new ArrayList<AtomicNode>();
		Node n = getNodes().get(0);
			if(n.getClass().equals(AtomicNode.class)){	
				result.add(n.getStartNode());
			}
			if(n.getClass().equals(SequenceComponentNode.class)){	
				result.addAll(((SequenceComponentNode) n).getRealStartNodes());
			}
			if(n.getClass().equals(FlowComponentNode.class)){	
				result.addAll(((FlowComponentNode) n).getRealStartNodes());
			}
			
			
		
		
		return result;
		
	}
	
	@Override
	public List<AtomicNode> getRealEndNodes(){
		List<AtomicNode> result = new ArrayList<AtomicNode>();
		Node n = getNodes().get(getNodes().size()-1);
			if(n.getClass().equals(AtomicNode.class)){	
				result.add(n.getEndNode());
			}
			if(n.getClass().equals(SequenceComponentNode.class)){	
				result.addAll(((SequenceComponentNode) n).getRealEndNodes());
			}
			if(n.getClass().equals(FlowComponentNode.class)){	
				result.addAll(((FlowComponentNode) n).getRealEndNodes());
			}

		
		
		return result;
		
	}
	@Override
	public String serializeToBpelComponent(String inpTab, List<Link> plink) {
		
		StringBuilder result= new StringBuilder();
		String currentTab = inpTab;
		
		result.append(currentTab+"<bpel:sequence>"+System.lineSeparator());
		result.append(this.addSourceAndTargetLinks(currentTab,plink));
		for(Node n: this.getNodes()){
			result.append(n.serializeToBpelComponent(currentTab+"\t",plink));
		}
		
		
		
		
		result.append(currentTab+"</bpel:sequence>"+System.lineSeparator());
		
		return result.toString();
	}
	
}

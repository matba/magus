package edu.ls3.magus.cl.mashupconfigurator.bpelgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.ls3.magus.cl.contextmanager.basic.Condition;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstanceS;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.utility.Holder;


public abstract class ComponentNode extends Node {
	private List<Node> nodes;
	
	public List<Node> getNodes() {
		return nodes;
	}
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	public ComponentNode(){
		nodes = new ArrayList<Node>();
	}
	public  String getSubgraphSerializedGV(boolean simplifiedNames, HashMap<String, String> newNames, Holder<Character> cntr) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("subgraph cluster_"+ UUID.randomUUID().toString().substring(0, 5)+ " {"+ System.lineSeparator());
		if(this.getClass().equals(SequenceComponentNode.class)){
			sb.append("label = \"<sequence>\";"+ System.lineSeparator());
			sb.append("color=red;"+ System.lineSeparator());
			StringBuilder transtions = new StringBuilder();
			SequenceComponentNode scn = (SequenceComponentNode) this;
			for(int cnt=0; cnt<scn.getNodes().size(); cnt++){
				if(cnt<scn.getNodes().size()-1){
					AtomicNode edgeSt,edgeEn;
					edgeSt = scn.getNodes().get(cnt).getEndNode();
					edgeEn = scn.getNodes().get(cnt+1).getStartNode();
					
					String stName = edgeSt.getID();
					if(simplifiedNames && !edgeSt.isFakeNode()){
						if(newNames.containsKey(stName))
							stName =newNames.get(stName);
						else{
							stName = String.valueOf( cntr.value);
							newNames.put( edgeSt.getID(),stName);
							cntr.value++;
						}
					}
					String enName = edgeEn.getID();
					if(simplifiedNames && !edgeEn.isFakeNode()){
						if(newNames.containsKey(enName))
							enName =newNames.get(enName);
						else{
							enName = String.valueOf( cntr.value);
							newNames.put( edgeEn.getID(),enName);
							cntr.value++;
						}
					}
					
					transtions.append( stName+" -> "+enName+";"+System.lineSeparator());
				}
				if(scn.getNodes().get(cnt) instanceof ComponentNode){
					sb.append(((ComponentNode) scn.getNodes().get(cnt)).getSubgraphSerializedGV(simplifiedNames,newNames,cntr ));
				}
				
			}
			sb.append(transtions);
		}
		if(this.getClass().equals(FlowComponentNode.class)){
			sb.append("label = \"<flow>\";"+ System.lineSeparator());
			
			sb.append("color=blue;"+ System.lineSeparator());
			StringBuilder transtions = new StringBuilder();
			FlowComponentNode fcn = (FlowComponentNode) this;
			AtomicNode edgeSt,edgeEn;
			edgeSt = this.getStartNode();
			edgeEn = this.getEndNode();
			
			String stName = edgeSt.getID();
			if(simplifiedNames&& !edgeSt.isFakeNode()){
				if(newNames.containsKey(stName))
					stName =newNames.get(stName);
				else{
					stName = String.valueOf( cntr.value);
					newNames.put( edgeSt.getID(),stName);
					cntr.value++;
				}
			}
			String enName = edgeEn.getID();
			if(simplifiedNames&& !edgeEn.isFakeNode()){
				if(newNames.containsKey(enName))
					enName =newNames.get(enName);
				else{
					enName = String.valueOf( cntr.value);
					newNames.put( edgeEn.getID(),enName);
					cntr.value++;
				}
			}
			
			for(int cnt=0; cnt<fcn.getNodes().size(); cnt++){
				
				String curStName =  fcn.getNodes().get(cnt).getStartNode().getID();
				if(simplifiedNames && !fcn.getNodes().get(cnt).getStartNode().isFakeNode()){
					if(newNames.containsKey(curStName))
						curStName =newNames.get(curStName);
					else{
						curStName = String.valueOf( cntr.value);
						newNames.put( fcn.getNodes().get(cnt).getStartNode().getID(),curStName);
						cntr.value++;
					}
				}
				
				String curEnName =  fcn.getNodes().get(cnt).getEndNode().getID();
				if(simplifiedNames && !fcn.getNodes().get(cnt).getEndNode().isFakeNode()){
					if(newNames.containsKey(curEnName))
						curEnName =newNames.get(curEnName);
					else{
						curEnName = String.valueOf( cntr.value);
						newNames.put( fcn.getNodes().get(cnt).getEndNode().getID(),curEnName);
						cntr.value++;
					}
				}
				
				transtions.append(  stName+" -> "+ curStName+";"+System.lineSeparator());
				transtions.append(  curEnName+" -> "+ enName+";"+System.lineSeparator());
					
				
				
				if(fcn.getNodes().get(cnt) instanceof ComponentNode){
					sb.append(((ComponentNode) fcn.getNodes().get(cnt)).getSubgraphSerializedGV( simplifiedNames,newNames,cntr));
				}
				
			}
			sb.append(transtions);
			for(Link l : fcn.getLinks()){
				
				String curStName =  l.getStartNode().getEndNode().getID();
				if(simplifiedNames && !l.getStartNode().getEndNode().isFakeNode()){
					if(newNames.containsKey(curStName))
						curStName =newNames.get(curStName);
					else{
						curStName = String.valueOf( cntr.value);
						newNames.put(l.getStartNode().getEndNode().getID(),curStName);
						cntr.value++;
					}
				}
				
				String curEnName =  l.getEndNode().getStartNode().getID();
				if(simplifiedNames && !l.getEndNode().getStartNode().isFakeNode()){
					if(newNames.containsKey(curEnName))
						curEnName =newNames.get(curEnName);
					else{
						curEnName = String.valueOf( cntr.value);
						newNames.put( l.getEndNode().getStartNode().getID(),curEnName);
						cntr.value++;
					}
				}
				
				sb.append(  curStName+" -> "+curEnName+"[style=dashed, color=red, label = \"<link>\"] ;"+System.lineSeparator());
			}
			sb.append( edgeSt.getID()+ " [style=filled,color=black,shape=point];"+System.lineSeparator());
			sb.append( edgeEn.getID()+ " [style=filled,color=black,shape=point];"+System.lineSeparator());
		}
		
		sb.append("}"+ System.lineSeparator());
		return sb.toString();
	}
	public int complexityPoint(){
		int result =0;
		if(this.getClass().equals(FlowComponentNode.class)){
			result+= ((FlowComponentNode) this).getLinks().size();
			for(Node n : getNodes())
				if(n instanceof ComponentNode){
					result += ((ComponentNode) n).complexityPoint();
				}
		}
		
		
		return result;
		
	}
	
	public int GetNoOfFlows()
	{
		int result =0;

		for(Node n: getNodes()){
			if(n instanceof ComponentNode)
				result+=((ComponentNode) n).GetNoOfFlows();
		}
		
		if(this.getClass().equals(FlowComponentNode.class))
			result++;
		
		return result;
	}
	
	public int GetNoOfSequence()
	{
		int result =0;

		for(Node n: getNodes()){
			if(n instanceof ComponentNode)
				result+=((ComponentNode) n).GetNoOfSequence();
		}
		
		if(this.getClass().equals(SequenceComponentNode.class))
			 result++;
		
		return result;
	}
	public int GetNoOfLink()
	{
		int result =0;

		for(Node n: getNodes()){
			if(n instanceof ComponentNode)
				result+=((ComponentNode) n).GetNoOfLink();
		}
		
		if(this.getClass().equals(FlowComponentNode.class))
			 result+=((FlowComponentNode) this).getLinks().size();
		
		return result;
	}
	private List<OperationNode> convertToWorkflowRecursion(List<OperationNode> result,List<OperationNode> startNode,Map<OperationNode, OperationNode> oldnewMap) throws Exception{
		
		if(this.getClass().equals(SequenceComponentNode.class)){
			List<OperationNode> previousNode = startNode;
			//OperationNode endNode =null;
			for(Node n : getNodes()){

				if(n.getClass().equals(AtomicNode.class)){	
					for(OperationNode pn: previousNode)
						if(n.getStartNode().getOperationNode()!=null){
							OperationNode nodeToAdd=null;
							if(oldnewMap.containsKey(n.getStartNode().getOperationNode())){
								nodeToAdd = oldnewMap.get(n.getStartNode().getOperationNode());
							}
							else
							{
								nodeToAdd = new OperationNode(n.getStartNode().getOperationNode().getCalledService(),false,false,n.getStartNode().getOperationNode().getUuid());
								oldnewMap.put(n.getStartNode().getOperationNode(),nodeToAdd);
							}
								
							pn.getEdges().add(nodeToAdd);
						}
						else
							throw new Exception("Null node visited");
					if(!result.contains(oldnewMap.get( n.getStartNode().getOperationNode()))){
						OperationNode wo = null;
						if(oldnewMap.containsKey(n.getStartNode().getOperationNode())){
							wo = oldnewMap.get(n.getStartNode().getOperationNode());
						}
						else{
							wo = new OperationNode(n.getStartNode().getOperationNode().getCalledService(),false,false,n.getStartNode().getOperationNode().getUuid());
							oldnewMap.put(n.getStartNode().getOperationNode(),wo);
						}
						
						wo.getEdges().clear();
						
						result.add(wo);
					}
					previousNode = new ArrayList<OperationNode>();
					previousNode.add(oldnewMap.get( n.getStartNode().getOperationNode()));
				}
				if(n instanceof ComponentNode){	
					previousNode = ((ComponentNode) n).convertToWorkflowRecursion(result, previousNode,oldnewMap);
				}	
			}
			return previousNode;
		}
		if(this.getClass().equals(FlowComponentNode.class)){
			List<OperationNode> endNodes = new ArrayList<OperationNode>();

			for(Node n : getNodes()){
				
				
				if(n.getClass().equals(AtomicNode.class)){	
					
					for(OperationNode stn : startNode)
						if(n.getStartNode().getOperationNode()!=null){
							
							OperationNode nodeToAdd=null;
							if(oldnewMap.containsKey(n.getStartNode().getOperationNode())){
								nodeToAdd = oldnewMap.get(n.getStartNode().getOperationNode());
							}
							else
							{
								nodeToAdd = new OperationNode(n.getStartNode().getOperationNode().getCalledService(),false,false,n.getStartNode().getOperationNode().getUuid());
								
								oldnewMap.put(n.getStartNode().getOperationNode(),nodeToAdd);
							}
								
							stn.getEdges().add(nodeToAdd);
							
						}
						
						else
							throw new Exception("Null node visited");
					
					endNodes.add(oldnewMap.get(n.getStartNode().getOperationNode()));
					if(!result.contains(oldnewMap.get( n.getStartNode().getOperationNode()))){
						OperationNode wo = null;
						if(oldnewMap.containsKey(n.getStartNode().getOperationNode())){
							wo = oldnewMap.get(n.getStartNode().getOperationNode());
						}
						else{
							wo = new OperationNode(n.getStartNode().getOperationNode().getCalledService(),false,false,n.getStartNode().getOperationNode().getUuid());
							
							oldnewMap.put(n.getStartNode().getOperationNode(),wo);
						}
						
						
						
						wo.getEdges().clear();
						result.add(wo);
					}
					
							
					
				}
				if(n instanceof ComponentNode){	
					 List<OperationNode> nen = ((ComponentNode) n).convertToWorkflowRecursion(result, startNode,oldnewMap);
					 endNodes.addAll(nen );
					 
				}
				
			}
			
			for(Link l: ((FlowComponentNode) this).getLinks())
			{
				List<OperationNode> sNodes = new ArrayList<OperationNode>();
				List<OperationNode> eNodes = new ArrayList<OperationNode>();
				
				List<AtomicNode> saNodes = l.getStartNode().getRealEndNodes();
				List<AtomicNode> eaNodes = l.getEndNode().getRealStartNodes();
				
				for(AtomicNode a: saNodes)
					sNodes.add(oldnewMap.get( a.getOperationNode()));
				
				for(AtomicNode a: eaNodes)
					eNodes.add(oldnewMap.get(a.getOperationNode()));
				
				
				for(OperationNode s: sNodes)
					for(OperationNode e : eNodes)
						if(e!=null)
						s.getEdges().add(e);
						else
							throw new Exception("Null node visited");
						
			}
			
			
			return endNodes;
		}
		
		return result;
	}
	
	
	public List<OperationNode> convertToWorkflow() throws Exception{
		List<OperationNode> result= new ArrayList<OperationNode>();
		
		ServiceCall initsc = new ServiceCall("Start",new Condition(new ArrayList<StateFactInstanceS>()), null);
		OperationNode startNode = new OperationNode(initsc,true,false);
		
		result.add(startNode);
		
		List<OperationNode> startNodes = new ArrayList<OperationNode>();
		startNodes.add(startNode);
		
		Map<OperationNode,OperationNode> map = new HashMap<OperationNode, OperationNode>();
		
		
		List<OperationNode> endNodes = convertToWorkflowRecursion(result, startNodes,map);
		
		ServiceCall goalsc = new ServiceCall("End",null, new Condition(new ArrayList<StateFactInstanceS>()));

		OperationNode endNode = new OperationNode(goalsc,false,true);
		
		for(OperationNode n : endNodes)
			if(n!=null)
				n.getEdges().add(endNode);
			else
				System.out.println("Null node visited");
		result.add(endNode);
		
		OperationNode.removeDerivableEdges(result);
		return result;
		
	}
	
private List<OperationNode> convertToWorkflowRecursion(List<OperationNode> result,List<OperationNode> startNode){
		
		
		
		
		
		if(this.getClass().equals(SequenceComponentNode.class)){
			List<OperationNode> previousNode = startNode;
			//OperationNode endNode =null;
			for(Node n : getNodes()){
				if(n.getClass().equals(AtomicNode.class)){	
					for(OperationNode pn: previousNode)
						if(n.getStartNode().getOperationNode()!=null)
							pn.getEdges().add(n.getStartNode().getOperationNode());
						else
							System.out.println("Null node visited");
					if(!result.contains(n.getStartNode().getOperationNode())){
						n.getStartNode().getOperationNode().getEdges().clear();
						result.add(n.getStartNode().getOperationNode());
					}
					previousNode = new ArrayList<OperationNode>();
					previousNode.add(n.getStartNode().getOperationNode());
				}
				if(n instanceof ComponentNode){	
					previousNode = ((ComponentNode) n).convertToWorkflowRecursion(result, previousNode);
				}	
			}
			return previousNode;
		}
		if(this.getClass().equals(FlowComponentNode.class)){
			List<OperationNode> endNodes = new ArrayList<OperationNode>();

			for(Node n : getNodes()){
				
				
				if(n.getClass().equals(AtomicNode.class)){	
					
					for(OperationNode stn : startNode)
						if(n.getStartNode().getOperationNode()!=null)
						stn.getEdges().add(n.getStartNode().getOperationNode());
						else
							System.out.println("Null node visited");
					
					endNodes.add(n.getStartNode().getOperationNode());
					if(!result.contains(n.getStartNode().getOperationNode())){
						n.getStartNode().getOperationNode().getEdges().clear();
						result.add(n.getStartNode().getOperationNode());
					}
					
							
					
				}
				if(n instanceof ComponentNode){	
					List<OperationNode> nen = ((ComponentNode) n).convertToWorkflowRecursion(result, startNode);
					endNodes.addAll(nen );
					 
				}
				
			}
			
			for(Link l: ((FlowComponentNode) this).getLinks())
			{
				List<OperationNode> sNodes = new ArrayList<OperationNode>();
				List<OperationNode> eNodes = new ArrayList<OperationNode>();
				
				List<AtomicNode> saNodes = l.getStartNode().getRealEndNodes();
				List<AtomicNode> eaNodes = l.getEndNode().getRealStartNodes();
				
				for(AtomicNode a: saNodes)
					sNodes.add(a.getOperationNode());
				
				for(AtomicNode a: eaNodes)
					eNodes.add(a.getOperationNode());
				
				
				for(OperationNode s: sNodes)
					for(OperationNode e : eNodes)
						if(e!=null)
						s.getEdges().add(e);
						else
							System.out.println("Null node visited");	
						
			}
			
			
			return endNodes;
		}
		
		return result;
	}
	
	
	public List<OperationNode> convertToWorkflow2(){
		List<OperationNode> result= new ArrayList<OperationNode>();
		
		ServiceCall initsc = new ServiceCall("Start",new Condition(new ArrayList<StateFactInstanceS>()), null);
		OperationNode startNode = new OperationNode(initsc,true,false);
		
		result.add(startNode);
		
		List<OperationNode> startNodes = new ArrayList<OperationNode>();
		startNodes.add(startNode);
		
		
		List<OperationNode> endNodes = convertToWorkflowRecursion(result, startNodes);
		
		ServiceCall goalsc = new ServiceCall("End",null, new Condition(new ArrayList<StateFactInstanceS>()));

		OperationNode endNode = new OperationNode(goalsc,false,true);
		
		for(OperationNode n : endNodes)
			if(n!=null)
				n.getEdges().add(endNode);
			else
				System.out.println("Null node visited");
		
		result.add(endNode);
		OperationNode.removeDerivableEdges(result);
		return result;
		
	}
	@Override
	public List<Service> findAllCalledServices() {
		List<Service> result = new ArrayList<Service>();
		for(Node cn : this.getNodes())
			result.addAll(cn.findAllCalledServices());
		return result;
	}
}

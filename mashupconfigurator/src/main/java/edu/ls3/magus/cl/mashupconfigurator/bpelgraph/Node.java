package edu.ls3.magus.cl.mashupconfigurator.bpelgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.utility.Holder;

public abstract class Node {
	private List<Node> edges;
	public List<Node> getEdges(){
		return edges;
	}
	public void setEdges(List<Node> edges){
		this.edges = edges;
	}
	
	public String serializeToGV() {
		return serializeToGV(false);
	}
	
	
	public String serializeToGV(boolean simplifiedNames) {
		HashMap<String, String> newNames= new HashMap<String, String>();
		Holder<Character> cntr = new Holder<Character>('A');
		
		StringBuilder sb = new StringBuilder();
		sb.append(" digraph G {"+ System.lineSeparator());
		
		if(this instanceof ComponentNode){
			sb.append(((ComponentNode) this).getSubgraphSerializedGV(simplifiedNames,newNames,cntr));
		}
		AtomicNode edgeSt,edgeEn;
		edgeSt = this.getStartNode();
		edgeEn = this.getEndNode();
		
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
		
		
		sb.append(  "recieve"+" -> "+stName +";"+System.lineSeparator());
		sb.append(  enName+" -> "+"reply" +";"+System.lineSeparator());
		
		
		sb.append(  "recieve [shape=Mdiamond];"+System.lineSeparator());
		sb.append(  "reply [shape=Msquare];"+System.lineSeparator());
		sb.append("}");
		return sb.toString();
	}
	
	abstract public AtomicNode getEndNode() ;
	abstract public AtomicNode getStartNode() ;
	
	abstract public String serializeToBpelComponent(String tab,List<Link> plinks);
	
	abstract public List<Service> findAllCalledServices();
	
	public List<AtomicNode> getRealStartNodes(){
		List<AtomicNode> result = new ArrayList<AtomicNode>();
		result.add(getStartNode());
		return result;
		
	}
	public List<AtomicNode> getRealEndNodes(){
		List<AtomicNode> result = new ArrayList<AtomicNode>();
		result.add(getEndNode());
		return result;
		
	}
	
	public boolean contains(Node inp){
		if(this.equals(inp)){
			return true;
		}
		else{
			if(this instanceof ComponentNode){
				for(Node n : ((ComponentNode) this).getNodes())
					if(n.contains(inp))
						return true;
			}
		}
		return false;
	}
	protected String addSourceAndTargetLinks(String tab, List<Link> plinks) {
		
		StringBuilder result = new StringBuilder();
		String stStr =tab+ "<bpel:sources>"+System.lineSeparator();
		String endStr ="";
		for(Link l :plinks){
			if(l.getStartNode().equals(this))
			{
				result.append(stStr);
				result.append(tab+"\t<bpel:source linkName=\""+l.getName()+ "\" />"+System.lineSeparator());
				 
				stStr="";
				endStr =tab+"</bpel:sources>"+System.lineSeparator();
			}
				
		}
		result.append(endStr);
		
		stStr =tab+ "<bpel:targets>"+System.lineSeparator();
		endStr ="";
		for(Link l :plinks){
			if(l.getEndNode().equals(this))
			{
				result.append(stStr);
				result.append(tab+"\t<bpel:target linkName=\""+l.getName()+ "\" />"+System.lineSeparator());
				 
				stStr="";
				endStr =tab+"</bpel:targets>"+System.lineSeparator();
			}
				
		}
		result.append(endStr);
		
		
		return result.toString();
	}
	
}

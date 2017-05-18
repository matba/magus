package edu.ls3.magus.cl.mashupconfigurator.bpelgraph;

public class DependencyEdge {
	private OperationNode startNode;
	private OperationNode endNode;
	
	public DependencyEdge(OperationNode startNode, OperationNode endNode){
		this.setStartNode(startNode);
		this.setEndNode(endNode);
	}

	public OperationNode getStartNode() {
		return startNode;
	}

	public void setStartNode(OperationNode startNode) {
		this.startNode = startNode;
	}

	public OperationNode getEndNode() {
		return endNode;
	}

	public void setEndNode(OperationNode endNode) {
		this.endNode = endNode;
	}
	
	@Override
	public boolean equals(Object i){
		if(i.getClass() != DependencyEdge.class)
			return false;
		DependencyEdge s = (DependencyEdge) i;
		if(s.getStartNode().equals(getStartNode()) && s.getEndNode().equals(getEndNode()))
			return true;
		
		return false;
	}
}

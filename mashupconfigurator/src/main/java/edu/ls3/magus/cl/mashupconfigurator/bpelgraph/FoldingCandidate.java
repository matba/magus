package edu.ls3.magus.cl.mashupconfigurator.bpelgraph;

import java.util.List;

public class FoldingCandidate {
	private ComponentNode componentNode;
	private List<Node> startNode;
	private List<Node> endNode;
	
	public FoldingCandidate(ComponentNode componentNode, List<Node> startNode, List<Node> endNode){
		this.setComponentNode(componentNode);
		this.setStartNode(startNode);
		this.setEndNode(endNode);
	}

	public ComponentNode getComponentNode() {
		return componentNode;
	}

	public void setComponentNode(ComponentNode componentNode) {
		this.componentNode = componentNode;
	}

	public List<Node> getStartNode() {
		return startNode;
	}

	public void setStartNode(List<Node> startNode) {
		this.startNode = startNode;
	}

	public List<Node> getEndNode() {
		return endNode;
	}

	public void setEndNode(List<Node> endNode) {
		this.endNode = endNode;
	}
}

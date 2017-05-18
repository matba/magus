package edu.ls3.magus.cl.mashupconfigurator.bpelgraph;

import java.util.UUID;

public class Link {
	private Node startNode;
	private Node endNode;
	private String uuid;
	
	public Link(Node startNode, Node endNode){
		this.setStartNode(startNode);
		this.setEndNode(endNode);
		this.uuid = UUID.randomUUID().toString();
	}

	public Node getStartNode() {
		return startNode;
	}

	public void setStartNode(Node startNode) {
		this.startNode = startNode;
	}

	public Node getEndNode() {
		return endNode;
	}

	public void setEndNode(Node endNode) {
		this.endNode = endNode;
	}
	
	public String getName(){
		return uuid;
	}
	
}

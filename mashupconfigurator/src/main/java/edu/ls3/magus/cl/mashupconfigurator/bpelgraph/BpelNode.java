package edu.ls3.magus.cl.mashupconfigurator.bpelgraph;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableSet;

import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.cl.mashupconfigurator.service.ServiceCollection;

/**
 * This is auxilary class created to be able to read the generate BPEL files
 *
 */
public class BpelNode {
	public enum NodeType {
		INVOKE, RECIEVE, SEQUENCE, FLOW
	}

	private final List<BpelNode> children;
	private final Map<BpelNode, BpelNode> links;
	private final NodeType type;
	private final Service calledService;

	public BpelNode(List<BpelNode> children, Map<BpelNode, BpelNode> links, NodeType type, Service calledService) {
		this.children = children;
		this.links = links;
		this.type = type;
		this.calledService = calledService;
	}

	public List<BpelNode> getChildren() {
		return children;
	}

	public Map<BpelNode, BpelNode> getLinks() {
		return links;
	}

	public NodeType getType() {
		return type;
	}

	public Service getCalledService() {
		return calledService;
	}

	public static FlowComponentNode readFromBpelXml(String bpelXml, ServiceCollection serviceCollection)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(bpelXml));
		Document doc = dBuilder.parse(is);

		String rootExpression = "//*[@name='main']/*[name()='bpel:flow']";
		XPath xPath = XPathFactory.newInstance().newXPath();
		Node root = (Node) xPath.compile(rootExpression).evaluate(doc, XPathConstants.NODE);

		Map<String, edu.ls3.magus.cl.mashupconfigurator.bpelgraph.Node> nodeSources = new HashMap<>();
		Map<String, edu.ls3.magus.cl.mashupconfigurator.bpelgraph.Node> nodeTargets = new HashMap<>();
		return (FlowComponentNode) readNode(root, serviceCollection, nodeSources, nodeTargets);

	}

	public static edu.ls3.magus.cl.mashupconfigurator.bpelgraph.Node readNode(Node node,
			ServiceCollection serviceCollection,
			Map<String, edu.ls3.magus.cl.mashupconfigurator.bpelgraph.Node> nodeSources,
			Map<String, edu.ls3.magus.cl.mashupconfigurator.bpelgraph.Node> nodeTargets) {
		switch (node.getNodeName()) {
		case "bpel:flow":
			NodeList nl = node.getChildNodes();
			Set<String> nodeIds = new HashSet<>();

			for (int cntr = 0; cntr < nl.getLength(); cntr++) {
				if (nl.item(cntr).getNodeName().equals("bpel:links")) {
					NodeList linkChidren = nl.item(cntr).getChildNodes();
					for (int linkCntr = 0; linkCntr < linkChidren.getLength(); linkCntr++) {
						if (linkChidren.item(linkCntr).getNodeName().equals("bpel:link")) {
							nodeIds.add(linkChidren.item(linkCntr).getAttributes().getNamedItem("name").getNodeValue());
						}
					}
				}
			}

			List<edu.ls3.magus.cl.mashupconfigurator.bpelgraph.Node> flowChildNodes = processNode(nl, serviceCollection,
					nodeSources, nodeTargets);

			List<Link> links = new ArrayList<>();
			for (String linkStr : nodeIds) {
				if (!nodeSources.containsKey(linkStr) || !nodeTargets.containsKey(linkStr)) {
					throw new IllegalStateException("The link should have both source and target. Link ID" + linkStr +
							"Contains source: "+ nodeSources.containsKey(linkStr)+ "Contains target: "+  nodeTargets.containsKey(linkStr) );

				}
				links.add(new Link(nodeSources.get(linkStr), nodeTargets.get(linkStr)));
			}
			FlowComponentNode fcn = new FlowComponentNode();
			fcn.setNodes(flowChildNodes);
			fcn.setLinks(links);
			addLinks(fcn, node, nodeSources, nodeTargets);
			return fcn;
		case "bpel:sequence":
			nl = node.getChildNodes();
			List<edu.ls3.magus.cl.mashupconfigurator.bpelgraph.Node> sequenceChildNodes = processNode(nl,
					serviceCollection, nodeSources, nodeTargets);
			SequenceComponentNode scn = new SequenceComponentNode();
			scn.setNodes(sequenceChildNodes);
			addLinks(scn, node, nodeSources, nodeTargets);
			return scn;

		case "bpel:invoke":
		case "bpel:recieve":
			String serviceName = node.getAttributes().getNamedItem("partnerLink").getNodeValue();
			Service service = serviceCollection.getServiceByName(serviceName);
			ServiceCall sc = new ServiceCall(service, Collections.emptyMap(), Collections.emptyMap(),
					Collections.emptyMap());
			OperationNode on = new OperationNode(sc, false, false);
			AtomicNode an = new AtomicNode(on);
			addLinks(an, node, nodeSources, nodeTargets);
			
			return an;

		}
		return null;
	}
	
 	private static void addLinks(edu.ls3.magus.cl.mashupconfigurator.bpelgraph.Node bpelNode, Node node,
			Map<String, edu.ls3.magus.cl.mashupconfigurator.bpelgraph.Node> nodeSources,
			Map<String, edu.ls3.magus.cl.mashupconfigurator.bpelgraph.Node> nodeTargets) {
 		NodeList nodeList = node.getChildNodes();
		for (int cntr = 0; cntr < nodeList.getLength(); cntr++) {
			if (nodeList.item(cntr).getNodeName().equals("bpel:targets")) {
				NodeList targetChidren = nodeList.item(cntr).getChildNodes();
				for (int targetCntr = 0; targetCntr < targetChidren.getLength(); targetCntr++) {
					if (targetChidren.item(targetCntr).getNodeName().equals("bpel:target")) {
						nodeTargets.put(
								targetChidren.item(targetCntr).getAttributes().getNamedItem("linkName").getNodeValue(), bpelNode);
					}
				}

			} else if (nodeList.item(cntr).getNodeName().equals("bpel:sources")) {
				NodeList sourceChidren = nodeList.item(cntr).getChildNodes();
				for (int sourceCntr = 0; sourceCntr < sourceChidren.getLength(); sourceCntr++) {
					if (sourceChidren.item(sourceCntr).getNodeName().equals("bpel:source")) {
						nodeSources.put(
								sourceChidren.item(sourceCntr).getAttributes().getNamedItem("linkName").getNodeValue(), bpelNode);
					}
				}
			}				
		}
 	}

	private static List<edu.ls3.magus.cl.mashupconfigurator.bpelgraph.Node> processNode(NodeList nl,
			ServiceCollection serviceCollection,
			Map<String, edu.ls3.magus.cl.mashupconfigurator.bpelgraph.Node> nodeSources,
			Map<String, edu.ls3.magus.cl.mashupconfigurator.bpelgraph.Node> nodeTargets) {
		List<edu.ls3.magus.cl.mashupconfigurator.bpelgraph.Node> childNodes = new ArrayList<>();
		for (int cntr = 0; cntr < nl.getLength(); cntr++) {
			Set<String> sources = new HashSet<>();
			Set<String> targets = new HashSet<>();
			if (ImmutableSet.of("bpel:flow", "bpel:sequence", "bpel:invoke", "bpel:recieve")
					.contains(nl.item(cntr).getNodeName())) {
				edu.ls3.magus.cl.mashupconfigurator.bpelgraph.Node Node = readNode(nl.item(cntr), serviceCollection,
						nodeSources, nodeTargets);
				childNodes.add(Node);
				for (String str : sources) {
					nodeSources.put(str, Node);
				}
				for (String str : targets) {
					nodeTargets.put(str, Node);
				}
				sources.clear();
				targets.clear();

			}

		}
		return childNodes;

	}

}

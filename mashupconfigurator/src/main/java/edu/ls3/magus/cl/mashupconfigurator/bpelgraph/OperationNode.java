package edu.ls3.magus.cl.mashupconfigurator.bpelgraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.UUID;

import edu.ls3.magus.cl.contextmanager.basic.Condition;
import edu.ls3.magus.cl.contextmanager.basic.Instance;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstance;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstanceS;
import edu.ls3.magus.cl.contextmanager.basic.StateFactType;
import edu.ls3.magus.cl.contextmanager.context.ContextModel;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.cl.mashupconfigurator.service.ServiceCollection;

public class OperationNode {
	private String uuid;
	private List<OperationNode> edges;
	private ServiceCall calledService;
	private List<OperationNode> postNodes;
	private boolean isStartNode;
	private boolean isEndNode;
	
	static int filecnt = 0;
	
	public ServiceCall getCalledService() {
		return calledService;
	}
	private void setCalledService(ServiceCall calledService) {
		this.calledService = calledService;
	}
	private List<OperationNode> getPostNodes() {
		return postNodes;
	}
//	private void setPostNodes(List<OperationNode> postNodes) {
//		this.postNodes = postNodes;
//	}
	
	public List<OperationNode> getEdges() {
		return edges;
	}
	public void setEdges(List<OperationNode> edges) {
		this.edges = edges;
	}
	public OperationNode(){
		this.setUuid(UUID.randomUUID().toString());
		this.setEdges(new ArrayList<OperationNode>());
		this.postNodes = new ArrayList<OperationNode>();
		this.setCalledService(null);
		this.setEndNode(false);
		this.setStartNode(false);
	}
	public OperationNode(ServiceCall calledService, boolean isStartNode, boolean isEndNote){
		this.setUuid(UUID.randomUUID().toString());
		this.setEdges(new ArrayList<OperationNode>());
		this.postNodes = new ArrayList<OperationNode>();
		this.setCalledService(calledService);
		this.setStartNode(isStartNode);
		this.setEndNode(isEndNote);
	}
	public OperationNode(ServiceCall calledService, boolean isStartNode, boolean isEndNote,String uuid){
		this.setUuid(uuid);
		this.setEdges(new ArrayList<OperationNode>());
		this.postNodes = new ArrayList<OperationNode>();
		this.setCalledService(calledService);
		this.setStartNode(isStartNode);
		this.setEndNode(isEndNote);
	}
	
	@Override
	public boolean equals(Object i){
		if(i.getClass() != OperationNode.class)
			return false;
		OperationNode s = (OperationNode) i;
		if(s.getUuid().equals(getUuid()))
			return true;
		
		return false;
	}
	
	public static OperationNode createFromServiceCall(List<ServiceCall> serviceList){
		OperationNode result = new OperationNode();
		List<OperationNode> graphNodeList= new ArrayList<OperationNode>();
		for(ServiceCall s:serviceList){
			graphNodeList.add(new OperationNode(s,false,false));
			
		}
		//Find direct order relationships
		for(int fcnt=0; fcnt< graphNodeList.size(); fcnt++){
			for(int scnt=fcnt; scnt<graphNodeList.size(); scnt++){
				int cv =1 ; //graphNodeList.get(fcnt).getCalledService().compare(graphNodeList.get(scnt).getCalledService());
				if(cv>0){
					graphNodeList.get(fcnt).getPostNodes().add(graphNodeList.get(scnt));
				}
				if(cv<0){
					graphNodeList.get(scnt).getPostNodes().add(graphNodeList.get(fcnt));
				}
			}
		}
		//Find indirect order relationship
		boolean somethinghappened=true;
		while(somethinghappened){
			somethinghappened=false;
			for(OperationNode gf : graphNodeList)
				for(OperationNode gs : gf.getPostNodes()){
					for(OperationNode gt: gs.getPostNodes())
						if(!gf.getPostNodes().contains(gt)){
							gf.getPostNodes().add(gt);
							somethinghappened = true;
							
						}
					if(somethinghappened)
						break;
				}
		
				
		}
		
		//Creates composition plan based on orders
		List<OperationNode> fl = new ArrayList<OperationNode>();
		List<OperationNode> fl2 = new ArrayList<OperationNode>();
		List<OperationNode> fl3 = new ArrayList<OperationNode>();
		for(OperationNode gn : graphNodeList)
			if(gn.getPostNodes().size()==0)
				fl.add(gn);
		OperationNode finalNode = new OperationNode();
		for(OperationNode gn : fl){
			graphNodeList.remove(gn);
			gn.edges.add(finalNode);
			
		}
		
//		int count=1;
		
		while(graphNodeList.size()>0){
//			count=0;
			fl2= new ArrayList<OperationNode>();
			fl3= new ArrayList<OperationNode>();
			for(OperationNode gs : fl){
				for(OperationNode gf : graphNodeList)
				{
					if(gf.getPostNodes().contains(gs)){
						boolean found =false;
						for(OperationNode gt: gf.getPostNodes()){
							if(graphNodeList.contains(gt))
								found =true;
						}
						if(!found)
						{
							for(OperationNode gt: gf.getPostNodes())
								if(fl.contains(gt))
								{
									boolean fff=false;
									for(OperationNode ggg: gf.getEdges())
										if(ggg.getCalledService().equals(gt.getCalledService()))
											fff=true;
									if(!fff)
										gf.getEdges().add(gt);
									
									
								}
							if(!fl2.contains(gf))
								fl2.add(gf);
							if(!fl3.contains(gs))
								fl3.add(gs);
//							count++;
						}
					}
				}
			}
			for(OperationNode gn: fl3)
				fl.remove(gn);
			for(OperationNode gn: fl2)
				fl.add(gn);
			for(OperationNode gn : fl2)
				graphNodeList.remove(gn);
		}
		
		for(OperationNode gn : fl)
			result.getEdges().add(gn);
		

		return result;
	}
	public static List<OperationNode> optimize(List<ServiceCall> inpList) throws Exception{
		
		for(ServiceCall sc: inpList)
			sc.initConditions();
		List<OperationNode> result = new ArrayList<OperationNode>();
		Map<OperationNode, List<OperationNode>> dependecyList = new HashMap<OperationNode, List<OperationNode>>();
		List<OperationNode> firstLayer = new ArrayList<OperationNode>();
		
		List<OperationNode> graphNodeList= new ArrayList<OperationNode>();
		for(ServiceCall s:inpList){
			graphNodeList.add(new OperationNode(s,false,false));
		}
		
		for(OperationNode gn : graphNodeList){
			dependecyList.put(gn, new ArrayList<OperationNode>());
		}
		
		
		for(OperationNode gn : graphNodeList){
			for(OperationNode sgn: graphNodeList){
				if(gn.equals(sgn))
					continue;
				if(sgn.getCalledService().getPostconditions().haveIntersection(gn.getCalledService().getPreconditions()))
					dependecyList.get(gn).add(sgn);
			}
		}
		for(OperationNode gn : graphNodeList){
			if(dependecyList.get(gn).size()==0)
				firstLayer.add(gn);
		}
		for(OperationNode gn : firstLayer)
			result.add(gn);
		int cnt=0;
		
		while(firstLayer.size()>0)
		{
			OperationNode curg = firstLayer.get(0);
			firstLayer.remove(0);
			for(OperationNode g: dependecyList.keySet()){
				if(curg.equals(g))
					continue;
				if(dependecyList.get(g).contains(curg))
				{
					if(!curg.edges.contains(g)){
						curg.edges.add(g);
						cnt++;
					}
					dependecyList.get(g).remove(curg);
					if(dependecyList.get(g).size()==0)
						firstLayer.add(g);
				}
			}
		}
		System.out.println("total number of edges: "+cnt);
		
		return result;
	}
	
	public static List<OperationNode> convertToGraph(List<ServiceCall> inpList,Condition initialState, Condition goalState) throws Exception{
		for(ServiceCall sc: inpList)
			sc.initConditions();
		List<OperationNode> result = new ArrayList<OperationNode>();
		ServiceCall initsc = new ServiceCall("Start",new Condition(new ArrayList<StateFactInstanceS>()), initialState);
		
		
		OperationNode curGraphNode = new OperationNode(initsc,true,false);
		result.add(curGraphNode);
		
		
		for(ServiceCall sc : inpList){
			OperationNode gn = new OperationNode(sc,false,false);
			curGraphNode.getEdges().add(gn);
			curGraphNode = gn;
			result.add(curGraphNode);
		}
		ServiceCall goalsc = new ServiceCall("End",goalState, new Condition(new ArrayList<StateFactInstanceS>()));
		
		
		
		
		OperationNode endNode = new OperationNode(goalsc,false,true);
		curGraphNode.getEdges().add(endNode);
		result.add(endNode);
		return result;
	}
	
	public static void optimizeNew(List<OperationNode> inpGraph) throws Exception{
		
		
		
	Map<StateFactInstanceS, List<OperationNode>> SFINodes = new HashMap<StateFactInstanceS, List<OperationNode>>();
		
		FillSFINodes(inpGraph,SFINodes);
		
		if( !safe2(inpGraph))
			throw new Exception("The graph is not safe at the begining of optimization");
		
		List<DependencyEdge> unsafeEdges = new ArrayList<DependencyEdge>();
		List<DependencyEdge> candidateEdges = getRemovalCandidateEdges2(inpGraph,unsafeEdges);
		
		
//		System.out.print("Possible Edges:{");
//		for(Edge e : candidateEdges)
//			System.out.print("("+e.getStartNode().getCalledService().getCalledService().getName()+","+e.getEndNode().getCalledService().getCalledService().getName()+"), " );
//		System.out.println("}");
//		int cnt=0;
		while(candidateEdges.size()!=0){
		
			DependencyEdge selectedEdge = findBestNodeForRemoval(inpGraph, candidateEdges);
			//System.out.println("Selected Edge: ("+selectedEdge.getStartNode().getCalledService().getCalledService().getName()+","+selectedEdge.getEndNode().getCalledService().getCalledService().getName()+"), " );
			
			selectedEdge.getStartNode().getEdges().remove(selectedEdge.getEndNode());
			
			for(OperationNode gn: inpGraph)
			{
				if(gn.getEdges().contains(selectedEdge.getStartNode()))
					if(!gn.getEdges().contains(selectedEdge.getEndNode()))
						gn.getEdges().add(selectedEdge.getEndNode());
			}
			
			
			for(OperationNode gn: selectedEdge.getEndNode().getEdges())
				if(!selectedEdge.getStartNode().getEdges().contains(gn))
					selectedEdge.getStartNode().getEdges().add(gn);
			
			
//			DecimalFormat df= new DecimalFormat("000");
			//UtilityClass.writeFile( new File("D:\\"+df.format(cnt++)+".gv"), GraphNode.serializedToGV(inpGraph));
			
			
			
			candidateEdges = getRemovalCandidateEdges2(inpGraph,unsafeEdges);
			
			
//			System.out.print("Possible Edges:{");
//			for(Edge e : candidateEdges)
//				System.out.print("("+e.getStartNode().getCalledService().getCalledService().getName()+","+e.getEndNode().getCalledService().getCalledService().getName()+"), " );
//			System.out.println("}");
		
		}
		
//		DecimalFormat df= new DecimalFormat("000");
//		
//		UtilityClass.writeFile( new File("D:\\"+df.format(cnt)+".gv"), GraphNode.serializedToGV(inpGraph));
		removeDerivableEdges(inpGraph);
		
	}
	
private static void FillSFINodes(List<OperationNode> inpGraph, Map<StateFactInstanceS, List<OperationNode>> sfiNodes) {
		
		OperationNode stNode = null;
		for(OperationNode gn : inpGraph){
			if(gn.isStartNode())
				stNode =gn; 
				for(StateFactInstanceS sfis: gn.getCalledService().getPostconditions().getConditions()){
				if(!sfiNodes.containsKey(sfis)){
					sfiNodes.put(sfis, new ArrayList<OperationNode>());
					sfiNodes.put(sfis.getNegated(), new ArrayList<OperationNode>());
				}
				sfiNodes.get(sfis).add(gn);
			}
			for(StateFactInstanceS sfis: gn.getCalledService().getPreconditions().getConditions()){
				if(!sfiNodes.containsKey(sfis)){
					sfiNodes.put(sfis, new ArrayList<OperationNode>());
					sfiNodes.put(sfis.getNegated(), new ArrayList<OperationNode>());
				}
				
			}
		}
		
		for(StateFactInstanceS sfis: sfiNodes.keySet())
		{
			if(sfis.isNot()&&!stNode.getCalledService().getPostconditions().hasFact(sfis)&&!stNode.getCalledService().getPostconditions().hasFact(sfis.getNegated())){
				sfiNodes.get(sfis).add(stNode);
			}
		}
	}
public static void optimizeNew2(List<OperationNode> inpGraph) throws Exception{
			
		Map<StateFactInstanceS, List<OperationNode>> sfisNodes = new HashMap<StateFactInstanceS, List<OperationNode>>();
		Map<String, List<OperationNode>> beforeNodes = new HashMap<String, List<OperationNode>>();
		Map<String, List<OperationNode>> afterNodes = new HashMap<String, List<OperationNode>>();
		
		
		FillSFINodes(inpGraph,sfisNodes);
		fillBeforeAndAfterNodes(inpGraph, beforeNodes,afterNodes);
		
		
		if(!safe(inpGraph, sfisNodes, beforeNodes, afterNodes))
			throw new Exception();
		
		
		List<DependencyEdge> unsafeEdges = new ArrayList<DependencyEdge>();
		//List<Edge> candidateEdges = getRemovalCandidateEdges(inpGraph,unsafeEdges);
		DependencyEdge selectedEdge = getFirstCandidateEdges(inpGraph,unsafeEdges,sfisNodes,beforeNodes,afterNodes);
		
//		System.out.print("Possible Edges:{");
//		for(Edge e : candidateEdges)
//			System.out.print("("+e.getStartNode().getCalledService().getCalledService().getName()+","+e.getEndNode().getCalledService().getCalledService().getName()+"), " );
//		System.out.println("}");
//		int cnt=0;
		while(selectedEdge!=null){
		
			//Edge selectedEdge = findBestNodeForRemoval(inpGraph, candidateEdges);
			//System.out.println("Selected Edge: ("+selectedEdge.getStartNode().getCalledService().getCalledService().getName()+","+selectedEdge.getEndNode().getCalledService().getCalledService().getName()+"), " );
			
			selectedEdge.getStartNode().getEdges().remove(selectedEdge.getEndNode());
			
			for(OperationNode gn: inpGraph)
			{
				if(gn.getEdges().contains(selectedEdge.getStartNode()))
					if(!gn.getEdges().contains(selectedEdge.getEndNode())){
						boolean redundant = false;
						for(OperationNode gnc: gn.getEdges())
							if(!gnc.equals(selectedEdge.getStartNode()))
								if(gnc.equals(selectedEdge.getEndNode())||afterNodes.get(gnc.getUuid()).contains(selectedEdge.getEndNode())){
									redundant = true;
									break;
								}
						if(!redundant)
							gn.getEdges().add(selectedEdge.getEndNode());
					}
			}
			
			
			for(OperationNode gn: selectedEdge.getEndNode().getEdges())
				if(!selectedEdge.getStartNode().getEdges().contains(gn)){
					boolean redundant = false;
					for(OperationNode gnc: selectedEdge.getStartNode().getEdges())
						if(!gnc.equals(selectedEdge.getEndNode()))
							if(gnc.equals(gn)||afterNodes.get(gnc.getUuid()).contains(gn)){
								redundant = true;
								break;
							}
					if(!redundant)
						selectedEdge.getStartNode().getEdges().add(gn);
					
				}
			
			boolean remove = true;
			
			
			for(OperationNode g: selectedEdge.getStartNode().getEdges())
				if(afterNodes.get(g.getUuid()).contains(selectedEdge.getEndNode()))
					remove =false;
			
			if(remove){
				afterNodes.get(selectedEdge.getStartNode().getUuid()).remove(selectedEdge.getEndNode());
				beforeNodes.get(selectedEdge.getEndNode().getUuid()).remove(selectedEdge.getStartNode());
			}
			
			
			//removeDerivableEdges(inpGraph);
//			DecimalFormat df= new DecimalFormat("000");
			//UtilityClass.writeFile( new File("D:\\"+df.format(cnt++)+".gv"), GraphNode.serializedToGV(inpGraph));
			
			//System.out.println("Edge removed!");
			
			selectedEdge = getFirstCandidateEdges(inpGraph,unsafeEdges,sfisNodes,beforeNodes,afterNodes);
			
			
//			System.out.print("Possible Edges:{");
//			for(Edge e : candidateEdges)
//				System.out.print("("+e.getStartNode().getCalledService().getCalledService().getName()+","+e.getEndNode().getCalledService().getCalledService().getName()+"), " );
//			System.out.println("}");
		
		}
		
//		DecimalFormat df= new DecimalFormat("000");
//		
//		UtilityClass.writeFile( new File("D:\\"+df.format(cnt)+".gv"), GraphNode.serializedToGV(inpGraph));
		//removeDerivableEdges(inpGraph);
		
	}
	
	private static void fillBeforeAndAfterNodes(List<OperationNode> inpGraph, Map<String, List<OperationNode>> beforeNodes,
		Map<String, List<OperationNode>> afterNodes) {

		
		
		for(OperationNode curGn:inpGraph){
			afterNodes.put(curGn.getUuid() , new ArrayList<OperationNode>());
			
			Queue<OperationNode> q = new LinkedList<OperationNode>();
			List<OperationNode> processed = new ArrayList<OperationNode>();
			q.add(curGn);
			processed.add(curGn);
			while(!q.isEmpty()){
				OperationNode afterGn= q.remove();
				for(OperationNode g: afterGn.getEdges())
					if(!processed.contains(g)){
//						if(curGn.getCalledService().getCalledService().getName().equals("serv20f36446Service")&&g.getCalledService().getCalledService().getName().equals("servc31fa8d3Service"))
//							System.out.println("Well2");
						afterNodes.get(curGn.getUuid()).add(g);
						processed.add(g);
						q.add(g);
					}
			}
		
			beforeNodes.put(curGn.getUuid() , new ArrayList<OperationNode>());
			
			q = new LinkedList<OperationNode>();
			processed = new ArrayList<OperationNode>();
			q.add(curGn);
			processed.add(curGn);
			while(!q.isEmpty()){
				OperationNode beforeGn= q.remove();
				List<OperationNode> pe = GetPreNodes(inpGraph, beforeGn);
				for(OperationNode g: pe)
					if(!processed.contains(g)){
						beforeNodes.get(curGn.getUuid()).add(g);
						processed.add(g);
						q.add(g);
					}
			}
		
		}
		
}
	private static DependencyEdge findBestNodeForRemoval(List<OperationNode> graph,
			List<DependencyEdge> candidateEdges) {

		DependencyEdge minEdge = null;
		int minLen = -1;
		for(DependencyEdge curEdge : candidateEdges)
		{
			List<OperationNode> newgraph = getGraphAfterRemoval(graph, curEdge);
			int length = findLength(newgraph);
			if((minEdge == null) || length<minLen)
			{
				minEdge = curEdge;
				minLen = length;
			}
		}
		return minEdge;
	}
	
	public static void removeDerivableEdges(List<OperationNode> graph)
	{
		boolean newAdded = true;
		List<DependencyEdge> derivedEdges = new ArrayList<DependencyEdge>();
		List<DependencyEdge> curEdges = new ArrayList<DependencyEdge>();
		
		for(OperationNode gn: graph){
			for(OperationNode egn: gn.getEdges())
				curEdges.add(new DependencyEdge(gn, egn));
		}
		
		while(newAdded){
			newAdded = false;
			outerloop:
			for(DependencyEdge egs: curEdges)
				for(DependencyEdge ege : curEdges)
					if(egs.getEndNode().equals(ege.getStartNode()))
					{
						DependencyEdge newEdge = new DependencyEdge(egs.getStartNode(), ege.getEndNode());
						if(derivedEdges.contains(newEdge))
							continue;
						if(!curEdges.contains(newEdge))
							curEdges.add(newEdge);
						derivedEdges.add(newEdge);
						newAdded=true;
						break outerloop;
						
					}
					
					
			
		}
		
		for(DependencyEdge e: derivedEdges){
			e.getStartNode().getEdges().remove(e.getEndNode());
		}
	}
	
	
	private static int findLength(List<OperationNode> graph) {

		OperationNode startNode=null;
		int layerCounter=-1;
		for(OperationNode gn: graph)
			if(gn.isStartNode())
			{
				startNode= gn;
				break;
			}
		List<OperationNode> curLayer = new ArrayList<OperationNode>();
		curLayer.add(startNode);
		while(curLayer.size()>0)
		{
			List<OperationNode> newLayer =new ArrayList<OperationNode>();
			while(!curLayer.isEmpty()){
				OperationNode gn=  curLayer.remove(0);
				for(OperationNode nextGn: gn.getEdges())
					if(!newLayer.contains(nextGn))
						newLayer.add(nextGn);
			}
			
			curLayer = newLayer;
			layerCounter++;
		}
 		return layerCounter;
	}
	@SuppressWarnings("unused")
	private static List<DependencyEdge> getRemovalCandidateEdges(List<OperationNode> graph,List<DependencyEdge> unsafeEdges  ,Map<StateFactInstanceS, List<OperationNode>> sfiNodes, Map<String, List<OperationNode>> beforeNodes,  Map<String, List<OperationNode>> afterNodes) throws Exception {

		List<DependencyEdge> result = new ArrayList<DependencyEdge>();
		for(OperationNode gn: graph){
			for(OperationNode egn: gn.getEdges()){
				if(gn.isStartNode() || egn.isEndNode())
					continue;
				DependencyEdge curEdge = new DependencyEdge(gn, egn);
				
				if(unsafeEdges.contains(curEdge))
					continue;
				
				List<OperationNode> newgraph = getGraphAfterRemoval(graph, curEdge);
				//UtilityClass.writeFile(new File("D:\\out"+filecnt+".gv"), GraphNode.serializedToGV(newgraph));
				//filecnt++;
				if(safe(newgraph,sfiNodes,beforeNodes,afterNodes))
					result.add(curEdge);
				else
					unsafeEdges.add(curEdge);
					
			}
		}
		return result;
		
		
	}
	private static List<DependencyEdge> getRemovalCandidateEdges2(List<OperationNode> graph,List<DependencyEdge> unsafeEdges) throws Exception {
		
		List<DependencyEdge> result = new ArrayList<DependencyEdge>();
		for(OperationNode gn: graph){
			for(OperationNode egn: gn.getEdges()){
				if(gn.isStartNode() || egn.isEndNode())
					continue;
				DependencyEdge curEdge = new DependencyEdge(gn, egn);
				
				if(unsafeEdges.contains(curEdge))
					continue;
				
				List<OperationNode> newgraph = getGraphAfterRemoval(graph, curEdge);
				//UtilityClass.writeFile(new File("D:\\out"+filecnt+".gv"), GraphNode.serializedToGV(newgraph));
				//filecnt++;
				if(safe2(newgraph))
					result.add(curEdge);
				else
					unsafeEdges.add(curEdge);
					
			}
		}
		return result;
		
		
	}
	private static DependencyEdge getFirstCandidateEdges(List<OperationNode> graph,List<DependencyEdge> unsafeEdges,Map<StateFactInstanceS, List<OperationNode>> sfiNodes, Map<String, List<OperationNode>> beforeNodes,  Map<String, List<OperationNode>> afterNodes) throws Exception {
		
		//List<Edge> result = new ArrayList<Edge>();
		for(OperationNode gn: graph){
			for(OperationNode egn: gn.getEdges()){
				if(gn.isStartNode() || egn.isEndNode())
					continue;
				DependencyEdge curEdge = new DependencyEdge(gn, egn);
				
				if(unsafeEdges.contains(curEdge))
					continue;
				
				List<OperationNode> newgraph = getGraphAfterRemoval(graph, curEdge);
//				UtilityClass.writeFile(new File("/home/mbashari/graphs/out"+filecnt+".gv"), GraphNode.serializedToGV(newgraph));
//				filecnt++;
				
//				System.out.println("Select Edge: "+curEdge.getStartNode().getCalledService().getCalledService().getName() +"  "+curEdge.getEndNode().getCalledService().getCalledService().getName());
				
				Map<String,List<OperationNode>> newBeforeNodes = new HashMap<String, List<OperationNode>>();
				Map<String, List<OperationNode>> newAfterNodes = new HashMap<String, List<OperationNode>>();
				
				boolean remove = true;
				
				
				for(OperationNode g: curEdge.getStartNode().getEdges())
					if(afterNodes.get(g.getUuid()).contains(curEdge.getEndNode()))
						remove =false;
				
				
				
				
				newBeforeNodes.putAll(beforeNodes);
				newAfterNodes.putAll(afterNodes);
				
				newBeforeNodes.remove(curEdge.getEndNode().getUuid());
				newAfterNodes.remove(curEdge.getStartNode().getUuid());
				
				newBeforeNodes.put(curEdge.getEndNode().getUuid(), new ArrayList<OperationNode>());
				newAfterNodes.put(curEdge.getStartNode().getUuid(), new ArrayList<OperationNode>());
				
				newBeforeNodes.get(curEdge.getEndNode().getUuid()).addAll(beforeNodes.get(curEdge.getEndNode().getUuid()));
				newAfterNodes.get(curEdge.getStartNode().getUuid()).addAll(afterNodes.get(curEdge.getStartNode().getUuid()));
				
				if(remove){
//					if(curEdge.getStartNode().getCalledService().getCalledService().getName().equals("serv20f36446Service")&&curEdge.getEndNode().getCalledService().getCalledService().getName().equals("servc31fa8d3Service"))
//						System.out.println("Well2");
						
					newBeforeNodes.get(curEdge.getEndNode().getUuid()).remove(curEdge.getStartNode());
					newAfterNodes.get(curEdge.getStartNode().getUuid()).remove(curEdge.getEndNode());
				}
				
//				if(curEdge.getStartNode().getCalledService().getCalledService().getName().equals("serv20f36446Service")&&curEdge.getEndNode().getCalledService().getCalledService().getName().equals("serv4becfc6bService"))
//					System.out.println("Well");
				
				if(safe(newgraph,sfiNodes,newBeforeNodes,newAfterNodes)){
//					if(!safe2(newgraph))
//						throw new Exception("Problem!");
					return curEdge;
				}
				else{
//					if(safe2(newgraph))
//						throw new Exception("Problem New!");
					unsafeEdges.add(curEdge);
				}
					
			}
		}
		return null;
		
		
	}
	
	private static DependencyEdge getFirstCandidateEdges2(List<OperationNode> graph,List<DependencyEdge> unsafeEdges) throws Exception {
		
		//List<Edge> result = new ArrayList<Edge>();
		for(OperationNode gn: graph){
			for(OperationNode egn: gn.getEdges()){
				if(gn.isStartNode() || egn.isEndNode())
					continue;
				DependencyEdge curEdge = new DependencyEdge(gn, egn);
				
				if(unsafeEdges.contains(curEdge))
					continue;
				
				List<OperationNode> newgraph = getGraphAfterRemoval(graph, curEdge);
				//UtilityClass.writeFile(new File("D:\\out"+filecnt+".gv"), GraphNode.serializedToGV(newgraph));
				//filecnt++;
				if(safe2(newgraph))
					return curEdge;
				else
					unsafeEdges.add(curEdge);
					
			}
		}
		return null;
		
		
	}

	public static void optimizeNewX(List<OperationNode> inpGraph) throws Exception{
		
		
		
		List<DependencyEdge> unsafeEdges = new ArrayList<DependencyEdge>();
		List<DependencyEdge> candidateEdges = getRemovalCandidateEdges2(inpGraph,unsafeEdges);
		
		
//		System.out.print("Possible Edges:{");
//		for(Edge e : candidateEdges)
//			System.out.print("("+e.getStartNode().getCalledService().getCalledService().getName()+","+e.getEndNode().getCalledService().getCalledService().getName()+"), " );
//		System.out.println("}");
//		int cnt=0;
		while(candidateEdges.size()!=0){
		
			DependencyEdge selectedEdge = findBestNodeForRemoval(inpGraph, candidateEdges);
			//System.out.println("Selected Edge: ("+selectedEdge.getStartNode().getCalledService().getCalledService().getName()+","+selectedEdge.getEndNode().getCalledService().getCalledService().getName()+"), " );
			
			selectedEdge.getStartNode().getEdges().remove(selectedEdge.getEndNode());
			
			for(OperationNode gn: inpGraph)
			{
				if(gn.getEdges().contains(selectedEdge.getStartNode()))
					if(!gn.getEdges().contains(selectedEdge.getEndNode()))
						gn.getEdges().add(selectedEdge.getEndNode());
			}
			
			
			for(OperationNode gn: selectedEdge.getEndNode().getEdges())
				if(!selectedEdge.getStartNode().getEdges().contains(gn))
					selectedEdge.getStartNode().getEdges().add(gn);
			
//			DecimalFormat df= new DecimalFormat("000");
			//UtilityClass.writeFile( new File("D:\\"+df.format(cnt++)+".gv"), GraphNode.serializedToGV(inpGraph));
			
			candidateEdges = getRemovalCandidateEdges2(inpGraph,unsafeEdges);
			
			
//			System.out.print("Possible Edges:{");
//			for(Edge e : candidateEdges)
//				System.out.print("("+e.getStartNode().getCalledService().getCalledService().getName()+","+e.getEndNode().getCalledService().getCalledService().getName()+"), " );
//			System.out.println("}");
		
		}
		
//		DecimalFormat df= new DecimalFormat("000");
//		
//		UtilityClass.writeFile( new File("D:\\"+df.format(cnt)+".gv"), GraphNode.serializedToGV(inpGraph));
		removeDerivableEdges(inpGraph);
		
	}
	
public static void optimizeNew2X(List<OperationNode> inpGraph) throws Exception{
		
		
		
		List<DependencyEdge> unsafeEdges = new ArrayList<DependencyEdge>();
		//List<Edge> candidateEdges = getRemovalCandidateEdges(inpGraph,unsafeEdges);
		DependencyEdge selectedEdge = getFirstCandidateEdges2(inpGraph,unsafeEdges);
		
//		System.out.print("Possible Edges:{");
//		for(Edge e : candidateEdges)
//			System.out.print("("+e.getStartNode().getCalledService().getCalledService().getName()+","+e.getEndNode().getCalledService().getCalledService().getName()+"), " );
//		System.out.println("}");
		//int cnt=0;
		while(selectedEdge!=null){
		
			//Edge selectedEdge = findBestNodeForRemoval(inpGraph, candidateEdges);
			//System.out.println("Selected Edge: ("+selectedEdge.getStartNode().getCalledService().getCalledService().getName()+","+selectedEdge.getEndNode().getCalledService().getCalledService().getName()+"), " );
			
			selectedEdge.getStartNode().getEdges().remove(selectedEdge.getEndNode());
			
			for(OperationNode gn: inpGraph)
			{
				if(gn.getEdges().contains(selectedEdge.getStartNode()))
					if(!gn.getEdges().contains(selectedEdge.getEndNode()))
						gn.getEdges().add(selectedEdge.getEndNode());
			}
			
			
			for(OperationNode gn: selectedEdge.getEndNode().getEdges())
				if(!selectedEdge.getStartNode().getEdges().contains(gn))
					selectedEdge.getStartNode().getEdges().add(gn);
			
//			DecimalFormat df= new DecimalFormat("000");
			//UtilityClass.writeFile( new File("D:\\"+df.format(cnt++)+".gv"), GraphNode.serializedToGV(inpGraph));
			
			//System.out.println("Edge removed!");
			removeDerivableEdges(inpGraph);
			selectedEdge = getFirstCandidateEdges2(inpGraph,unsafeEdges);
			
			
//			System.out.print("Possible Edges:{");
//			for(Edge e : candidateEdges)
//				System.out.print("("+e.getStartNode().getCalledService().getCalledService().getName()+","+e.getEndNode().getCalledService().getCalledService().getName()+"), " );
//			System.out.println("}");
		
		}
		
//		DecimalFormat df= new DecimalFormat("000");
//		
//		UtilityClass.writeFile( new File("D:\\"+df.format(cnt)+".gv"), GraphNode.serializedToGV(inpGraph));
		removeDerivableEdges(inpGraph);
		
	}
	public static boolean safe(List<OperationNode> graph,Map<StateFactInstanceS, List<OperationNode>> sfiNodes, Map<String, List<OperationNode>> beforeNodes,  Map<String, List<OperationNode>> afterNodes) throws Exception {
		
		for(OperationNode gn : graph){
			List<OperationNode> preNodes = new ArrayList<OperationNode>();
			preNodes.addAll(beforeNodes.get(gn.getUuid()));
			for(StateFactInstanceS sfis : gn.getCalledService().getPreconditions().getConditions()){
//				if(sfis.isNot() &&  sfis.getStateFactInstance().getType().getTypeName().equals("p42a5e649"))
//					System.out.println("Reached there");
					
				
				List<OperationNode> filteredPreNodes = new ArrayList<OperationNode>();
				
				Map<StateFactInstanceS, List<OperationNode>> cns = sfiNodes;
				
				
				for(OperationNode curGn: cns.get(sfis))
					if(preNodes.contains(curGn))
						filteredPreNodes.add(curGn);
				
				List<OperationNode> negatingNodes =  sfiNodes.get(sfis.getNegated());
				boolean guranteedPreconditionSatisfierFound = false;
				for(OperationNode pNode : filteredPreNodes)
				{
						boolean nothingMakesConditionFalse =true;
						for(OperationNode nNode: negatingNodes){
							if(nNode.equals(gn)||nNode.equals(pNode))
								continue;
							if( !afterNodes.get(nNode.getUuid()).contains(pNode)  && !afterNodes.get( gn.getUuid()).contains(nNode))
								nothingMakesConditionFalse =false;
						}
						if(nothingMakesConditionFalse){
							guranteedPreconditionSatisfierFound = true;
							break;
						}
					}
					
				
				if(!guranteedPreconditionSatisfierFound){
//					System.out.println("Failed while trying to find precondition "+ (sfis.isNot()?"!":"") + sfis.getStateFactInstance().getType().getTypeName() + " for service call "+gn.getCalledService().getCalledService().getName());
//					System.out.println("***************");
					
					return false;
				}

			}
		}
//		System.out.println("***************");
		return true;
	}
	public static boolean safe2(List<OperationNode> graph) throws Exception {
		
		for(OperationNode gn : graph){
			if((gn.getCalledService().getCalledService()!=null)&&(gn.getCalledService().getCalledService().getInvocationService()!=null))
			{
				Service invService = gn.getCalledService().getCalledService().getInvocationService();
				OperationNode invNode = null;
				for(OperationNode n : graph){
					if(invService.equals(n.getCalledService().getCalledService())){
						invNode = n;
						break;
					}
				}
				if(invNode==null)
					return false;
				if(!isAfter(graph, invNode, gn))
					return false;
			}
			for(StateFactInstanceS sfis : gn.getCalledService().getPreconditions().getConditions()){
//				if(sfis.isNot() &&  sfis.getStateFactInstance().getType().getTypeName().equals("p42a5e649"))
//					System.out.println("Reached there");
			
				Queue<OperationNode> q = new LinkedList<OperationNode>();
				List<OperationNode> processed = new ArrayList<OperationNode>();
				boolean guranteedPreconditionSatisfierFound = false;
				for(OperationNode g: graph)
					if(g.getEdges().contains(gn))
						q.add(g);
				
				while(!q.isEmpty() && !guranteedPreconditionSatisfierFound)
				{
					OperationNode curGn = q.remove();
					processed.add(curGn);
					if(curGn.getCalledService().getPostconditions().getConditions().contains(sfis) || (sfis.isNot() && curGn.isStartNode() && !curGn.getCalledService().getPostconditions().getConditions().contains(sfis.getNegated()) ))
					{
						boolean nothingMakesConditionFalse =true;
						for(OperationNode egn: graph){
							if(egn.equals(gn)||egn.equals(curGn))
								continue;
							if(egn.getCalledService().getPostconditions().getConditions().contains(sfis.getNegated()) && !isAfter(graph,  egn,curGn)  && !isAfter(graph,  gn,egn))
								nothingMakesConditionFalse =false;
						}
						if(nothingMakesConditionFalse){
							guranteedPreconditionSatisfierFound = true;
//							System.out.println("Foud precondition "+ (sfis.isNot()?"!":"") +sfis.getStateFactInstance().getType().getTypeName() + " for service call "+gn.getCalledService().getCalledService().getName()+ "through service"+curGn.getCalledService().getCalledService().getName());
						}
					}
					for(OperationNode g: graph)
						if(g.getEdges().contains(curGn) && !processed.contains(g))
							q.add(g);
				}
				if(!guranteedPreconditionSatisfierFound){
//					System.out.println("Failed while trying to find precondition "+ (sfis.isNot()?"!":"") + sfis.getStateFactInstance().getType().getTypeName() + " for service call "+gn.getCalledService().getCalledService().getName());
					return false;
				}

			}
		}
//		System.out.println("***************");
		return true;
	}
	private static boolean isAfter(List<OperationNode> graph, OperationNode first,
			OperationNode second) throws Exception {
		if(first.equals(second))
		//	return false;
			throw new Exception("Not a meaningful comparison");
		Queue<OperationNode> q = new LinkedList<OperationNode>();
		List<OperationNode> processed = new ArrayList<OperationNode>();
		q.add(first);
		
		while(!q.isEmpty()){
			OperationNode curGn = q.remove();
			processed.add(curGn);
			for(OperationNode nextGn: curGn.getEdges()){
				if(nextGn.equals(second))
					return true;
				if(!processed.contains(nextGn)){
				q.add(nextGn);
				processed.add(nextGn);
				}
			}
		}
		return false;
	}
	private static List<OperationNode> copy(List<OperationNode> graph,Map<OperationNode, OperationNode> mapping){
		List<OperationNode> result = new ArrayList<OperationNode>();
		
		Queue<OperationNode> q = new LinkedList<OperationNode>();
		List<OperationNode> processed = new ArrayList<OperationNode>();
		
		
		OperationNode endNode = getEndNode(graph);
		
		q.add(endNode);
	///	System.out.println("**********");
		
		while(!q.isEmpty()){
			OperationNode curNode = q.remove();
			if(processed.contains(curNode))
				continue;
			OperationNode newNode = new OperationNode(curNode.getCalledService(), curNode.isStartNode(),curNode.isEndNode(),curNode.getUuid());
			
		//	System.out.println("New node created for service: " + curNode.getCalledService().getCalledService().getName());
			mapping.put(curNode, newNode);
			for(OperationNode e: curNode.getEdges() ){
				newNode.getEdges().add(mapping.get(e));
			}
			
			processed.add(0, curNode);
			result.add(0, newNode);
			
			for(OperationNode gn: graph)
			{
				if(processed.contains(gn))
					continue;
				
				int noExcludingProcessed = countExcluding(gn.getEdges(), processed);
				if(noExcludingProcessed==0)
					q.add(gn);
			}
		}
		
		return result;
	}
	
	private static int countExcluding(List<OperationNode> list,
			List<OperationNode> exc) {
		
		int result =0;
		for(OperationNode gn : list)
			if(!exc.contains(gn))
				result++;
		return result;
	}
	public static OperationNode getEndNode(List<OperationNode> graph){
		for(OperationNode gn: graph){
			if(gn.isEndNode())
				return gn;
		}
		return null;
	}
	
	
	private static List<OperationNode> getGraphAfterRemoval(List<OperationNode> graph, DependencyEdge inpcurEdge) {
		
		Map<OperationNode, OperationNode> mapping = new HashMap<OperationNode, OperationNode>();
		List<OperationNode> result = copy(graph,mapping);
		
		DependencyEdge curEdge = new DependencyEdge(mapping.get(inpcurEdge.getStartNode()), mapping.get(inpcurEdge.getEndNode()));
		
//		List<GraphNode> result = new ArrayList<GraphNode>();
//		GraphNode startNode = new GraphNode(inpcurEdge.getStartNode().getCalledService(),inpcurEdge.getStartNode().isStartNode(),inpcurEdge.getStartNode().isEndNode());
//		startNode.getEdges().addAll(inpcurEdge.getStartNode().getEdges());
//		startNode.getEdges().remove(inpcurEdge.getEndNode());
//		
//		GraphNode endNode = new GraphNode(inpcurEdge.getEndNode().getCalledService(),inpcurEdge.getEndNode().isStartNode(),inpcurEdge.getEndNode().isEndNode());
//		endNode.getEdges().addAll(inpcurEdge.getEndNode().getEdges());
//		
//		Edge curEdge = new Edge(startNode, endNode);
//		
//		
//		for(GraphNode gn: graph){
//			if(gn.equals(inpcurEdge.getStartNode()))
//				result.add(startNode);
//			else{
//				if(gn.equals(inpcurEdge.getEndNode()))
//					result.add(endNode);
//				else{
//					if(gn.getEdges().contains(inpcurEdge.getStartNode()))
//					{
//						GraphNode newNode = new GraphNode(gn.getCalledService(), gn.isStartNode(), gn.isEndNode());
//						newNode.getEdges().addAll(gn.getEdges());
//						newNode.getEdges().remove(inpcurEdge.getStartNode());
//						newNode.getEdges().add(startNode);
//						if(gn.getEdges().contains(inpcurEdge.getEndNode()))
//						{
//							newNode.getEdges().remove(inpcurEdge.getEndNode());
//							newNode.getEdges().add(endNode);
//						}
//						result.add(newNode);
//					}else{
//						if(gn.getEdges().contains(inpcurEdge.getEndNode()))
//						{
//							GraphNode newNode = new GraphNode(gn.getCalledService(),gn.isStartNode(),gn.isEndNode());
//							newNode.getEdges().addAll(gn.getEdges());
//							newNode.getEdges().remove(inpcurEdge.getEndNode());
//							newNode.getEdges().add(endNode);
//						}else{
//							result.add(gn);
//						}
//					}
//				}
//			}
//				
//		}
		
		curEdge.getStartNode().getEdges().remove(curEdge.getEndNode());
		
		for(OperationNode gn: result)
		{
			if(gn.getEdges().contains(curEdge.getStartNode()))
				if(!gn.getEdges().contains(curEdge.getEndNode()))
					gn.getEdges().add(curEdge.getEndNode());
		}
		
		
		for(OperationNode gn: curEdge.getEndNode().getEdges())
			if(!curEdge.getStartNode().getEdges().contains(gn))
				curEdge.getStartNode().getEdges().add(gn);
		
		
		
		return result;
	}
	public boolean isStartNode() {
		return isStartNode;
	}
	public void setStartNode(boolean isStartNode) {
		this.isStartNode = isStartNode;
	}
	public boolean isEndNode() {
		return isEndNode;
	}
	public void setEndNode(boolean isEndNode) {
		this.isEndNode = isEndNode;
	}
	public static String serializedToGV(List<OperationNode> graph) {
		
		StringBuilder sb = new StringBuilder();
		sb.append(" digraph G {"+ System.lineSeparator());
		
		LinkedList<OperationNode> gl= new LinkedList<OperationNode>();
//		HashSet<OperationNode> finalNodes = new HashSet<OperationNode>();
		List<OperationNode> processedNodes = new ArrayList<OperationNode>();
		
		for(OperationNode g: graph){
			if(g.isStartNode())
			gl.add(g);
//			processedNodes.add(g);
//			String gname = "emptynode";
//			if(g.getCalledService()!=null)
//			{
//				gname = g.getCalledService().getCalledService().getName();
//			}
//			sb.append(  "Start -> "+gname.replaceAll("Service", "")+";"+System.lineSeparator());
		}
		while(!gl.isEmpty()){
			OperationNode curg= gl.removeFirst();
			String gname = "emptynode";
			if(curg.getCalledService()!=null)
			{
				gname = curg.getCalledService().getCalledService().getName()+curg.getUuid().substring(0, 3);
				
			}
//			if( curg.getEdges().size() ==0)
//			{
//				if(!finalNodes.contains(curg))
//				   sb.append(  gname.replaceAll("Service", "")+" -> End;"+System.lineSeparator());
//				finalNodes.add(curg);
//				continue;
//			}
			for(OperationNode ng : curg.getEdges()){
				if(!processedNodes.contains(ng)){
					processedNodes.add(ng);
					gl.addLast(ng);
				}
				String dname = "emptynode";
				if(ng.getCalledService()!=null)
				{
					dname = ng.getCalledService().getCalledService().getName()+ng.getUuid().substring(0, 3);
				}
				sb.append(  gname.replaceAll("Service", "")+" -> "+dname.replaceAll("Service", "")+";"+System.lineSeparator());
			}
		}
		sb.append("}");
		return sb.toString();
	}
	
	
	public static String serializedToJSON(List<OperationNode> graph) {
		
		StringBuilder sbNodes = new StringBuilder();
		StringBuilder sbEdges = new StringBuilder();
		String seperator = "";
		String seperator2 = "";
		sbNodes.append("{");
		sbNodes.append(System.lineSeparator());
		sbNodes.append("\t\"nodes\":[");
		sbNodes.append(System.lineSeparator());
		
		sbEdges.append(System.lineSeparator());
		sbEdges.append("\t],");
		sbEdges.append(System.lineSeparator());
		sbEdges.append("\t\"links\":[");
		sbEdges.append(System.lineSeparator());
		
		LinkedList<OperationNode> gl= new LinkedList<OperationNode>();
		List<OperationNode> processedNodes = new ArrayList<OperationNode>();
		List<OperationNode> nodeArray = new ArrayList<OperationNode>();
		
		for(OperationNode g: graph){
			if(g.isStartNode())
			gl.add(g);
			String gname = "emptynode";
			String optype ="";
			if(g.getCalledService()!=null)
			{
				gname = g.getCalledService().getCalledService().getName();
				if(gname.endsWith("Callback"))
					optype="receive";
				else
					optype="invoke";
			}
			nodeArray.add(g);
			sbNodes.append(seperator);
			seperator = ","+System.lineSeparator();
			
			sbNodes.append("\t\t{\"type\":\""+optype+"\",\"name\":\""+gname.replaceAll("Service", "")+"\"}");
//			processedNodes.add(g);
//			String gname = "emptynode";
//			if(g.getCalledService()!=null)
//			{
//				gname = g.getCalledService().getCalledService().getName();
//			}
//			sb.append(  "Start -> "+gname.replaceAll("Service", "")+";"+System.lineSeparator());
		}
		while(!gl.isEmpty()){
			OperationNode curg= gl.removeFirst();
			String gname = "emptynode";
			if(curg.getCalledService()!=null)
			{
				gname = curg.getCalledService().getCalledService().getName();
				
			}
			
			int gindex = -1;
			for(int cnt=0; cnt<nodeArray.size(); cnt++)
				if(curg.equals(nodeArray.get(cnt))){
					gindex=cnt;
					
				}

			for(OperationNode ng : curg.getEdges()){
				if(!processedNodes.contains(ng)){
					processedNodes.add(ng);
					gl.addLast(ng);
					
				}
				if(!nodeArray.contains(ng)){
					nodeArray.add(ng);
					sbNodes.append(seperator);
					seperator = ","+System.lineSeparator();
					sbNodes.append("\t\t{\"type\":\"invoke\",\"name\":\""+gname.replaceAll("Service", "")+"\"}");
				}
				int dindex = -1;
				for(int cnt=0; cnt<nodeArray.size(); cnt++)
					if(ng.equals(nodeArray.get(cnt))){
						dindex=cnt;
						
					}
				sbEdges.append(seperator2);
				seperator2 = ","+System.lineSeparator();
				sbEdges.append("\t\t{\"source\":"+gindex +",\"target\":"+dindex+"}");
				
				
			}
		}
		sbEdges.append(System.lineSeparator());
		sbEdges.append("\t]");
		sbEdges.append(System.lineSeparator());
		sbEdges.append("}");
		return sbNodes.toString()+sbEdges.toString();
	}
	
	public static String serialize(List<OperationNode> graph){
		Condition initialState=null;
		Condition goalState =null;
		
		for(OperationNode gn: graph)
    	{
    		if(gn.isStartNode())
    			initialState = gn.getCalledService().getPostconditions();
    		if(gn.isEndNode())
    			goalState = gn.getCalledService().getPostconditions();
    	}
		
		StringBuilder sb = new StringBuilder();
		Map<OperationNode, String> map = new HashMap<OperationNode, String>();
		
		for(OperationNode gn: graph)
			map.put(gn, UUID.randomUUID().toString());
		
		
		sb.append("*INITIAL CONDITION*");
		sb.append(System.lineSeparator());
		
		for(StateFactInstanceS sfis: initialState.getConditions())
		{
			if(sfis.isNot())
				continue;
			StateFactInstance sfi = sfis.getStateFactInstance();
			
			sb.append(sfi.getType().getTypeName());
			
			for(Instance i: sfi.getParams()){
				sb.append(" "+i.getName());
			}
			
			
			sb.append(System.lineSeparator());
		}
		
		
		sb.append("*GOAL CONDITION*");
		sb.append(System.lineSeparator());
		
		

		for(StateFactInstanceS sfis: goalState.getConditions())
		{
			if(sfis.isNot())
				continue;
			StateFactInstance sfi = sfis.getStateFactInstance();
			
			sb.append(sfi.getType().getTypeName());
			
			for(Instance i: sfi.getParams()){
				sb.append(" "+i.getName());
			}
			
			
			sb.append(System.lineSeparator());
		}
		
		sb.append("*SERVICES CALLS*");
		sb.append(System.lineSeparator());
		
		for(OperationNode gn: graph)
		{
			sb.append(map.get(gn)+ " "+ gn.getCalledService().getCalledService().getName() );
			//for(gn.getCalledService().getCalledService().g)
			if(gn.isStartNode() || gn.isEndNode()){
				sb.append(System.lineSeparator());
				continue;
			}
			
			for(URI u: gn.getCalledService().getCalledService().getInputList())
	    	   {	
					sb.append(" "+gn.getCalledService().getInputs().get(u).getName());
	    	   }
	    	   for(URI u: gn.getCalledService().getCalledService().getOutputList())
	    	   {
	    		   sb.append(" "+gn.getCalledService().getOutputs().get(u).getName());
	    	   }
	    	   for(URI u: gn.getCalledService().getCalledService().getVarList())
	    	   {
	    		   sb.append(" "+gn.getCalledService().getVars().get(u).getName());
	    	   }
			
			
			sb.append(System.lineSeparator());
		}
		
		
		sb.append("*EDGES*");
		sb.append(System.lineSeparator());
		for(OperationNode gn: graph)
		{
			for(OperationNode egn: gn.getEdges()){
				sb.append(map.get(gn)+ " "+ map.get(egn));
				sb.append(System.lineSeparator());
				
			}
			
		}
		
		return sb.toString();
	}
	
	public static List<OperationNode>  readFromFile(File file,ContextModel cm,ServiceCollection scl) throws Exception{
		List<OperationNode> result= new ArrayList<OperationNode>();
		Map<String, OperationNode> map = new HashMap<String, OperationNode>();
		
		
		List<StateFactInstanceS> initialState = new ArrayList<StateFactInstanceS>();
		List<StateFactInstanceS> goalState = new ArrayList<StateFactInstanceS>();
		boolean serviceCallsRead =false;
		boolean initConditionsRead =false;
		boolean goalConditionRead =false;
	    String line;
	    int phase =0;
	    
	    BufferedReader br = new BufferedReader(new FileReader(file));
	    
	    while ((line = br.readLine()) != null) {
	       if(line.equals("*SERVICES CALLS*")){
	    	   if(!initConditionsRead || !goalConditionRead)
	    	   {
	    		   br.close();
	    		   throw new Exception("Init and goal state has not been read yet!");
	    	   }
	    	   phase =1;
	    	   serviceCallsRead=true;
	    	   continue;
	       }
	       if(line.equals("*EDGES*")){
	    	   if(!serviceCallsRead)
	    	   {
	    		   br.close();
	    		   throw new Exception("Service call has not been read yet!");
	    	   }
	    	   phase = 2;
	    	   continue;
	       }
	       if(line.equals("*INITIAL CONDITION*")){
	    	   
	    	   phase =3;
	    	   initConditionsRead =true;
	    	   continue;
	       }
	       if(line.equals("*GOAL CONDITION*")){
	    	   phase = 4;
	    	   goalConditionRead =true;
	    	   continue;
	       }
	       
	       if(phase==1)
	       {
	    	   String[] params = line.split(" ");
	    	   
	    	   String uuid = params[0];
	    	   
	    	   if(params[1].equals("Start") && (params.length==2)){
	    		   
	    		   ServiceCall initsc = new ServiceCall("Start",new Condition(new ArrayList<StateFactInstanceS>()), new Condition(initialState));
	    			
	    		   OperationNode gn = new OperationNode(initsc, true, false);
	    		   map.put(uuid, gn);
	    		   result.add(gn);
	    		   continue;
	    	   }
	    	   if(params[1].equals("End") && (params.length==2)){
	    		   
	    		   ServiceCall goalsc = new ServiceCall("End",new Condition(goalState), new Condition(new ArrayList<StateFactInstanceS>()));
	    			
	    		   OperationNode gn = new OperationNode(goalsc, false, true);
	    		   map.put(uuid, gn);
	    		   result.add(gn);
	    		   continue;
	    	   }
	    	   
	    	   Service s = scl.getServiceByName(params[1]);
	    	   Map<URI, Instance> inpMap = new HashMap<URI, Instance>();
	    	   Map<URI, Instance> outMap = new HashMap<URI, Instance>();
	    	   Map<URI, Instance> varMap = new HashMap<URI, Instance>();
	    	   int pcnt=2;
	    	   for(URI u: s.getInputList())
	    	   {
	    		   inpMap.put(u, cm.getInstanceByName(params[pcnt])[0]);
	    		   pcnt++;
	    	   }
	    	   for(URI u: s.getOutputList())
	    	   {
	    		   outMap.put(u, cm.getInstanceByName(params[pcnt])[0]);
	    		   pcnt++;
	    	   }
	    	   for(URI u: s.getVarList())
	    	   {
	    		   varMap.put(u, cm.getInstanceByName(params[pcnt])[0]);
	    		   pcnt++;
	    	   }
	    	   
	    	   ServiceCall sc = new ServiceCall(s, inpMap,outMap, varMap);
	    	   sc.initConditions();
	    	   OperationNode gn = new OperationNode(sc, false, false);
	    	   map.put(uuid, gn);
	    	   result.add(gn);
	       }
	       if(phase==2)
	       {
	    	   String[] params = line.split(" ");
	    	   if(params.length !=2)
	    	   {
	    		   br.close();
	    		   throw new Exception("Unexpected number of params");
	    	   }
	    		   
	    	   map.get(params[0]).getEdges().add(map.get(params[1]));
	       }
	       if((phase==3)||(phase==4))
	       {
	    	   String[] params = line.split(" ");
	    	   StateFactType sft = cm.getInstaceFactTypeByName(params[0])[0];
	    	   List<Instance> li = new ArrayList<Instance>();
	    	   
	    	   
	    	   for(int pcnt =1; pcnt<= sft.getParams().length; pcnt++){
	    	   
	    		   li.add(cm.getInstanceByName(params[pcnt])[0]);
	    		   
	    	   }
	    	   
	    	   
	    	   StateFactInstance sfi = new StateFactInstance(sft, li.toArray(new Instance[0]));
	    	   StateFactInstanceS sfis = new StateFactInstanceS(sfi,false);
	    	   if(phase==3)
	    		   initialState.add(sfis);
	    	   else
	    		   goalState.add(sfis);
	       }
	      
	    }
		
	    br.close();
	    
	    
		
		
		return result;
		
		
	}
	public static List<OperationNode> GetPreNodes(List<OperationNode> graph,
			OperationNode curNode) {
		
		List<OperationNode> result = new ArrayList<OperationNode>();
		for(OperationNode gn: graph)
			if(gn.getEdges().contains(curNode))
				result.add(gn);
		return result;
	}
	
	public static OperationNode getStartNode(List<OperationNode> graph){
		for(OperationNode gn : graph)
			if(gn.isStartNode())
				return gn;
		return null;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public static List<List<List<OperationNode>>> PIPG(List<List<OperationNode>> paths ) throws Exception{
		List<List<List<OperationNode>>> result = new ArrayList<List<List<OperationNode>>>();
		List<OperationNode> lastnode = new ArrayList<OperationNode>();
		for(List<OperationNode> curpath: paths){
			if(!lastnode.contains(curpath.get(curpath.size()-1)))
			{
				lastnode.add(curpath.get(curpath.size()-1));
				List<List<OperationNode>> newgroup = new ArrayList<List<OperationNode>>();
				newgroup.add(curpath);
				result.add(newgroup);
			}else{
				List<List<OperationNode>> groupToAdd = null;
				for(List<List<OperationNode>> g: result)
					for(List<OperationNode> p: g){
						if(p.get(p.size()-1).equals(curpath.get(curpath.size()-1))){
							groupToAdd =g;
						}
					}
				if(groupToAdd!=null){
					groupToAdd.add(curpath);
				}
				else{
					throw new Exception("Path group could not be found!");
				}
			}
		}
		if(result.size()==1)
			return result;
		boolean somethingChanged = true;
		
		while(somethingChanged){
			somethingChanged = false;
			iterationloop:
			for(int cnt1 = 0; cnt1<result.size(); cnt1++)
				for(int cnt2=cnt1+1; cnt2<result.size(); cnt2++){
					List<OperationNode> g1 = convertToArray(result.get(cnt1));
					List<OperationNode> g2 = convertToArray(result.get(cnt2));
					for(OperationNode o : g1)
						if(g2.contains(o)){
							somethingChanged = true;
							for(List<OperationNode> g: result.get(cnt2))
								result.get(cnt1).add(g);
							result.remove(cnt2);
							break iterationloop;
						}
				}
		}
		
		return result;
		
	} 
	
	public static void POSG(List<List<OperationNode>> paths, ComponentNode element, Stack<Node> stack) throws Exception{
		if(paths.size()==0){
			while(!stack.empty()){
				Node node = stack.pop();
				element.getNodes().add(node);
			}
			return;
		}
		if(paths.size()==1){
			for(OperationNode o: paths.get(0))
				element.getNodes().add(new AtomicNode(o));
			return;
		}
		List<List<List<OperationNode>>> g = PIPG(paths);
		
		if(g.size()==1){
			List<List<OperationNode>> g1 = g.get(0);
			boolean sameFirst = true;
			boolean sameLast = true;
			OperationNode firstN=null, lastN = null;
			for(List<OperationNode> pl : g1){
				if(firstN ==null){
					firstN = pl.get(0);
					lastN = pl.get(pl.size()-1);
				}
				else{
					if(!pl.get(0).equals(firstN))
						sameFirst = false;
					if(!pl.get(pl.size()-1).equals(lastN))
						sameLast = false;
					if(!sameFirst&&!sameLast)
						break;
				}
			}
			if(sameFirst&&sameLast){
				element.getNodes().add(new AtomicNode(firstN));
				stack.push(new AtomicNode(lastN));
				List<List<OperationNode>> rem = new ArrayList<List<OperationNode>>(); 
				for(List<OperationNode> pl: g1){
					pl.remove(pl.size()-1);
					if(pl.size()>0)
						pl.remove(0);
					if(pl.size()==0)
						rem.add(pl);
				}
				for(List<OperationNode> r:rem)
					g1.remove(r);
				
			}
			if(sameFirst&&!sameLast){
				element.getNodes().add(new AtomicNode(firstN));
				
				List<List<OperationNode>> rem = new ArrayList<List<OperationNode>>(); 
				for(List<OperationNode> pl: g1){
					pl.remove(0);
					if(pl.size()==0)
						rem.add(pl);
				}
				for(List<OperationNode> r:rem)
					g1.remove(r);
				
			}
			if(!sameFirst&&sameLast){
				stack.push(new AtomicNode(lastN));
				List<List<OperationNode>> rem = new ArrayList<List<OperationNode>>(); 
				for(List<OperationNode> pl: g1){
					pl.remove(pl.size()-1);
					if(pl.size()==0)
						rem.add(pl);
				}
				for(List<OperationNode> r:rem)
					g1.remove(r);
				
			}
			if(!sameFirst&&!sameLast){
				FlowComponentNode fcn = new FlowComponentNode();
				for(List<OperationNode> pl: g1){
					fcn.getNodes().add(new AtomicNode(pl.get(0)));
				}
				element.getNodes().add(fcn);
				List<List<OperationNode>> rem = new ArrayList<List<OperationNode>>(); 
				for(List<OperationNode> pl: g1){
					pl.remove(0);
					if(pl.size()==0)
						rem.add(pl);
				}
				for(List<OperationNode> r:rem)
					g1.remove(r);
				
				
				
				
			}
			POSG(g1, element, stack);
		}
		else{
			FlowComponentNode fcn = new FlowComponentNode();
			element.getNodes().add(fcn);
			for(List<List<OperationNode>> gl: g){
				SequenceComponentNode scn = new SequenceComponentNode();
				fcn.getNodes().add(scn);
				if(gl.size()==1){
					for(OperationNode o: gl.get(0)){
						scn.getNodes().add(new AtomicNode(o));
					}
				}
				else{
					POSG(gl,scn,stack);
				}
			}
		}
	}
	public static void POSGM(List<List<OperationNode>> paths, ComponentNode element, List<OperationNode> remainingNode) throws Exception{
		 Stack<Node> stack =  new Stack<Node>();
		
		if(paths.size()==1){
			ComponentNode e = element;
//			if(element.getClass().equals(FlowComponentNode.class)){
//				e = new SequenceComponentNode();
//				element.getNodes().add(e);
//			}
			for(OperationNode o: paths.get(0))
				e.getNodes().add(new AtomicNode(o));
			return;
		}
		List<List<List<OperationNode>>> g = PIPG(paths);
		
		if(g.size()==1){
			List<OperationNode> rmn= new ArrayList<OperationNode>();
			rmn.addAll(remainingNode);
			List<List<OperationNode>> g1 = g.get(0);
			boolean sameFirst = true;
			boolean sameLast = true;
			OperationNode firstN=null, lastN = null;
			for(List<OperationNode> pl : g1){
				if(firstN ==null){
					firstN = pl.get(0);
					lastN = pl.get(pl.size()-1);
				}
				else{
					if(!pl.get(0).equals(firstN))
						sameFirst = false;
					if(!pl.get(pl.size()-1).equals(lastN))
						sameLast = false;
					if(!sameFirst&&!sameLast)
						break;
				}
			}
			if(sameFirst&&sameLast){
				rmn.remove(firstN);
				rmn.remove(lastN);
				element.getNodes().add(new AtomicNode(firstN));
				stack.push(new AtomicNode(lastN));
				List<List<OperationNode>> rem = new ArrayList<List<OperationNode>>(); 
				for(List<OperationNode> pl: g1){
					pl.remove(pl.size()-1);
					if(pl.size()>0)
						pl.remove(0);
					if(pl.size()==0)
						rem.add(pl);
				}
				for(List<OperationNode> r:rem)
					g1.remove(r);
				
			}
			if(sameFirst&&!sameLast){
				rmn.remove(firstN);
				
				element.getNodes().add(new AtomicNode(firstN));
				
				List<List<OperationNode>> rem = new ArrayList<List<OperationNode>>(); 
				for(List<OperationNode> pl: g1){
					pl.remove(0);
					if(pl.size()==0)
						rem.add(pl);
				}
				for(List<OperationNode> r:rem)
					g1.remove(r);
				
			}
			if(!sameFirst&&sameLast){
				
				rmn.remove(lastN);
				stack.push(new AtomicNode(lastN));
				List<List<OperationNode>> rem = new ArrayList<List<OperationNode>>(); 
				for(List<OperationNode> pl: g1){
					pl.remove(pl.size()-1);
					if(pl.size()==0)
						rem.add(pl);
				}
				for(List<OperationNode> r:rem)
					g1.remove(r);
				
			}
			if(!sameFirst&&!sameLast){
				FlowComponentNode fcn = new FlowComponentNode();
				List<OperationNode> l = new ArrayList<OperationNode>();
				for(List<OperationNode> pl: g1){
					if(!l.contains(pl.get(0))){
						fcn.getNodes().add(new AtomicNode(pl.get(0)));
						l.add(pl.get(0));
					}
				}
				rmn.removeAll(l);
				element.getNodes().add(fcn);
				List<List<OperationNode>> rem = new ArrayList<List<OperationNode>>(); 
				for(List<OperationNode> pl: g1){
					pl.remove(0);
					if(pl.size()==0)
						rem.add(pl);
				}
				for(List<OperationNode> r:rem)
					g1.remove(r);
			}
			List<OperationNode> stNodes =  FindAllWithNoIncomingNodes(rmn);
			List<List<OperationNode>> g2= DFS(stNodes,rmn);
			if(g2.size()==0){
				while(!stack.empty()){
					Node node = stack.pop();
					element.getNodes().add(node);
				}
				return;
			}
			boolean allone = true;
			for(List<OperationNode> p: g2){
				if(p.size()!=1){
					allone =false;
					break;
				}
			}
			if(allone){
				List<OperationNode> nodeList = new ArrayList<OperationNode>();
				for(List<OperationNode> p: g2){
					if(!nodeList.contains(p.get(0)))
						nodeList.add(p.get(0));
				}
				if(nodeList.size()==1)
					element.getNodes().add(new AtomicNode( g2.get(0).get(0)));
				else{
					FlowComponentNode fcn = new FlowComponentNode();
					for(OperationNode ni : nodeList)
						fcn.getNodes().add(new AtomicNode( ni));
					element.getNodes().add(fcn);
				}
			}
			else{
				
				POSGM(g2, element,rmn);
			}
			
			
		}
		else{
			FlowComponentNode fcn = new FlowComponentNode();
			element.getNodes().add(fcn);
			for(List<List<OperationNode>> gl: g){
				List<OperationNode> rmn = convertToArray(gl);
				List<OperationNode> stNodes =  FindAllWithNoIncomingNodes(rmn);
				List<List<OperationNode>> g2= DFS(stNodes,rmn);
				if(g2.size()==1){
					if(g2.get(0).size()==1){
						fcn.getNodes().add(new AtomicNode(g2.get(0).get(0)));
					}
					else{
						SequenceComponentNode scn = new SequenceComponentNode();
						fcn.getNodes().add(scn);
						for(OperationNode o: g2.get(0)){
							scn.getNodes().add(new AtomicNode(o));
						}
					}
				}
				else{
					SequenceComponentNode scn = new SequenceComponentNode();
					fcn.getNodes().add(scn);
					
					
					POSGM(g2,scn,rmn);
				}
			}
		}
		while(!stack.empty()){
			Node node = stack.pop();
			element.getNodes().add(node);
		}
	}
	private static List<List<OperationNode>> DFS(OperationNode node){
		List<List<OperationNode>> result = new ArrayList<List<OperationNode>>();
		if(node.getEdges().size()==0){
			List<OperationNode> tmp = new ArrayList<OperationNode>();
			tmp.add(node);
			result.add(tmp);
			return result;
		}
		for(OperationNode o:node.getEdges()){
			List<List<OperationNode>> tmp = DFS(o);
			for(List<OperationNode> lo: tmp){
				lo.add(0,node);
				result.add(lo);
			}
		}
		return result;
	}
	
	private static List<List<OperationNode>> DFS(OperationNode node, List<OperationNode> rem){
		List<List<OperationNode>> result = new ArrayList<List<OperationNode>>();
		boolean isNoNextEdge = true;
		for(OperationNode n: node.getEdges())
			if(rem.contains(n)){
				isNoNextEdge = false;
				break;
			}
		if(isNoNextEdge){
			List<OperationNode> tmp = new ArrayList<OperationNode>();
			tmp.add(node);
			result.add(tmp);
			return result;
		}
		for(OperationNode o:node.getEdges()){
			if(!rem.contains(o))
				continue;
			List<List<OperationNode>> tmp = DFS(o , rem);
			for(List<OperationNode> lo: tmp){
				lo.add(0,node);
				result.add(lo);
			}
		}
		return result;
	}
	
	private static List<List<OperationNode>> DFS(List<OperationNode> nodelist, List<OperationNode> rmn){
		List<List<OperationNode>> result = new ArrayList<List<OperationNode>>();
		for(OperationNode n: nodelist)
			result.addAll(DFS(n,rmn));
		return result;
	}
	
	public static List<OperationNode> FindAllWithNoIncomingNodes(List<OperationNode> nodeList){
		List<OperationNode> result = new ArrayList<OperationNode>(); 
		List<OperationNode> withIncoming = new ArrayList<OperationNode>();
		for(OperationNode n: nodeList)
			for(OperationNode e : n.getEdges())
				if(!withIncoming.contains(e))
					withIncoming.add(e);
			
		for(OperationNode n : nodeList)
			if(!withIncoming.contains(n))
				result.add(n);
		
		return result;
	}
	
	
	public static ComponentNode BPELAlgorithmNo1(List<OperationNode> graph) throws Exception{
		List<List<OperationNode>> p = DFS( getStartNode(graph));
		for(List<OperationNode> i: p){
			i.remove(i.size()-1);
			i.remove(0);
		}
		
		SequenceComponentNode fcn = new SequenceComponentNode();
		List<OperationNode> rmn = convertToArray(p);
		
		
//		Stack<Node> stack= new Stack<Node>();
		POSGM(p, fcn,rmn);
		
		
		return fcn;
	}
	
	
	
	private static List<OperationNode> convertToArray(List<List<OperationNode>> list) {
		List<OperationNode> result = new ArrayList<OperationNode>();
		for(List<OperationNode> g : list)
			for(OperationNode o : g)
				if(!result.contains(o))
					result.add(o);
		return result;
	}
}

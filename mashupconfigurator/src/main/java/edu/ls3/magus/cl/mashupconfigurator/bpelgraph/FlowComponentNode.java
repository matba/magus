package edu.ls3.magus.cl.mashupconfigurator.bpelgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import edu.ls3.magus.cl.contextmanager.basic.Instance;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.cl.mashupconfigurator.service.ServiceCollection;
import edu.ls3.magus.utility.UtilityClass;

public class FlowComponentNode extends ComponentNode {
	
	private List<Link> links;
	private AtomicNode inputNode=null;
	private AtomicNode outputNode=null;
	
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	@Override
	public AtomicNode getStartNode() {
		
		if(inputNode==null){
			inputNode = new AtomicNode(true);
		}
		return inputNode;
	}
	@Override
	public AtomicNode getEndNode() {

		
		
				if(outputNode==null){
					outputNode = new AtomicNode(true);
				}
				return outputNode;
	}
	
	public FlowComponentNode(){
		super();
		links = new ArrayList<Link>();
	}
	public static FlowComponentNode convertToFlowWithLink(List<OperationNode> graph){
		
		FlowComponentNode result = new FlowComponentNode();
		OperationNode st = OperationNode.getStartNode(graph);
		Queue<AtomicNode> q = new LinkedList<AtomicNode>();
		Map<OperationNode, AtomicNode> mp = new HashMap<OperationNode, AtomicNode>();
		for(OperationNode n : st.getEdges()){
			if(n.isEndNode())
				continue;
			AtomicNode nn = new AtomicNode(n);
			q.add(nn);
			mp.put(n, nn);
			result.getNodes().add(nn);
		}
		while(!q.isEmpty()){
			AtomicNode n = q.remove();
			for(OperationNode o: n.getOperationNode().getEdges()){
				if(o.isEndNode())
					continue;
				AtomicNode endNode = null;
				if(mp.containsKey(o)){
					endNode = mp.get(o);
				}
				else{
					endNode= new AtomicNode(o);
					q.add(endNode);
					mp.put(o, endNode);
					result.getNodes().add(endNode);
				}
				result.getLinks().add(new Link(n, endNode));
			}
		}
		return result;
	}
	
	public void OptimizeNo1(){
		List<FoldingCandidate> candidates = getFoldingCandidates();
		while(candidates.size()>0){
			FoldingCandidate fc = FindBestCandidate(candidates);
			FoldComponent(fc);
			candidates = getFoldingCandidates();
		}
	}
	
	public void OptimizeNo2(){
		int maximumLinks = 0;
//		int cntr=0;
		while(maximumLinks< getLinks().size()){
			List<FoldingCandidate> candidates = getFoldingCandidates2(maximumLinks);
			if(candidates.isEmpty()){
				maximumLinks++;
			}
			else{
				maximumLinks = 0;
				FoldingCandidate fc = FindBestCandidate(candidates);
				FoldComponent(fc);
//				String s = serializeToGV();
//		    	try {
//					edu.ls3.magus.eval.generators.owls.UtilityClass.writeFile(new File( "/home/mbashari/intermediate/testblock"+cntr+".gv"),s);
//				} catch (IOException e) {
//				
//					e.printStackTrace();
//				}
//		    	cntr++;
				//candidates = getFoldingCandidates2(maximumLinks);
			}
		}
	}
	
	private void FoldComponent(FoldingCandidate fc) {
		ComponentNode cn = fc.getComponentNode();
		List<Node> startNode= fc.getStartNode();
		List<Node> endNode = fc.getEndNode();
		
		if(cn.getClass().equals(FlowComponentNode.class)){
			
			//List<FlowComponentNode> flowSubComs= new ArrayList<FlowComponentNode>();
			for(int cnt=0; cnt<cn.getNodes().size(); cnt++){
//				if(!getNodes().contains(cn.getNodes().get(cnt))){
//					boolean found = false;
//					for(FlowComponentNode f: flowSubComs)
//						if(f.getNodes().contains(cn.getNodes().get(cnt)))
//						{
//							found =true;
//							break;
//						}
//					if(!found ){
//						for(Node nf: getNodes())
//							if((nf.getClass().equals(FlowComponentNode.class))&&((FlowComponentNode) nf).getNodes().contains(cn.getNodes().get(cnt)))
//							{
//								flowSubComs.add((FlowComponentNode) nf);
//								break;
//							}
//					}
//					
//					continue;
//				}
				List<Link> toberemoved = new ArrayList<Link>();
				for(Link l: getLinks()){
					
					if(startNode.contains( l.getStartNode()) && l.getEndNode().equals(cn.getNodes().get(cnt))){
						toberemoved.add(l);
						
					}
					if(l.getStartNode().equals(cn.getNodes().get(cnt)) && endNode.contains( l.getEndNode())){
						toberemoved.add(l);
						
					}
				}
				for(Link l : toberemoved)
					getLinks().remove(l);
			}
			
//			for(int cnt1=0; cnt1<flowSubComs.size();cnt1++){
//				List<Link> toberemoved = new ArrayList<Link>();
//				for(Link l: getLinks()){
//					
//					if(l.getStartNode().equals(startNode) && l.getEndNode().equals(flowSubComs.get(cnt1))){
//						toberemoved.add(l);
//						
//					}
//					if(l.getStartNode().equals(flowSubComs.get(cnt1)) && l.getEndNode().equals(endNode)){
//						toberemoved.add(l);
//						
//					}
//				}
//				for(Link l : toberemoved)
//					getLinks().remove(l);
//			}
//			
			for(Node stNode: startNode)
				this.getLinks().add(new Link(stNode, cn));
			for(Node enNode: endNode)
				this.getLinks().add(new Link(cn, enNode));
			for(Node n: cn.getNodes())
				getNodes().remove(n);
//			for(Node n:flowSubComs)
//				getNodes().remove(n);
			List<Link> toberemoved = new ArrayList<Link>();
			for(Link l : getLinks())
				if(cn.contains(l.getStartNode()) && cn.contains(l.getEndNode()))
				{
					((FlowComponentNode) cn).getLinks().add(l);
					toberemoved.add(l);
				}
			for(Link l : toberemoved)
				getLinks().remove(l);
			List<Node> tbr  =new ArrayList<Node>();	
			List<Node> tba  =new ArrayList<Node>();	
			for(Node n: cn.getNodes())
				if(n.getClass().equals(FlowComponentNode.class)){
					tba.addAll(((FlowComponentNode)n).getNodes());
					getLinks().addAll(((FlowComponentNode)n).getLinks());
					tbr.add(n);
					for(Link l: getLinks()){
						
						if(l.getStartNode().equals(n)){
							l.setStartNode(cn);
							
						}
						if(l.getEndNode().equals(n)){
							l.setEndNode(cn);
							
						}
					}
				}
			cn.getNodes().addAll(tba);
			cn.getNodes().removeAll(tbr);
			getNodes().add(cn);
			
		}
		if(cn.getClass().equals(SequenceComponentNode.class)){
//			List<Node> actualStructure = new ArrayList<Node>();
//			
//			for(int cnt=0; cnt<cn.getNodes().size();cnt++)
//				if(getNodes().contains(cn.getNodes().get(cnt)))
//					actualStructure.add(cn.getNodes().get(cnt));
//				else
//				{
//					SequenceComponentNode nodeParent = null;
//					for(Node n: getNodes())
//						if((n.getClass().equals(SequenceComponentNode.class))&&((SequenceComponentNode) n).getNodes().contains(cn.getNodes().get(cnt)))
//						{
//							nodeParent=((SequenceComponentNode) n);
//							break;
//						}
//					actualStructure.add(nodeParent);
//					while((cnt<cn.getNodes().size())&&(nodeParent.getNodes().contains(cn.getNodes().get(cnt++))));
//					cnt--;
//				}
//			
			for(int cnt=0; cnt<cn.getNodes().size()-1; cnt++){
				for(Link l: getLinks())
					if(l.getStartNode().equals(cn.getNodes().get(cnt)) && l.getEndNode().equals(cn.getNodes().get(cnt+1))){
						getLinks().remove(l);
						break;
					}
			}
			List<Link> toberemoved = new ArrayList<Link>();
			for(Link l: getLinks())
				if( l.getEndNode().equals(cn.getNodes().get(0))){
					toberemoved.add(l);
				}
			for(Link l: toberemoved){
				getLinks().remove(l);
				getLinks().add(new Link(l.getStartNode(), cn));
			}
			toberemoved = new ArrayList<Link>();
			for(Link l: getLinks())
				if( l.getStartNode().equals(cn.getNodes().get(cn.getNodes().size()-1))){
					toberemoved.add(l);
					
				}
			for(Link l: toberemoved){
				getLinks().remove(l);
				getLinks().add(new Link(cn, l.getEndNode()));
			}
			
			for(Node n: cn.getNodes())
				getNodes().remove(n);
			
			
			boolean somethingchanged = true;
			
			while(somethingchanged){
				somethingchanged =false;
				for(int cnt=0; cnt< cn.getNodes().size();cnt++)
				{
					if(cn.getNodes().get(cnt).getClass().equals(SequenceComponentNode.class)){
						somethingchanged=true;
						SequenceComponentNode rm = (SequenceComponentNode)cn.getNodes().get(cnt);
						Node rmf = rm.getNodes().get(0);
						Node rml = rm.getNodes().get(rm.getNodes().size()-1);
						List<Link> toberemovedf = new ArrayList<Link>();
						for(Link l: getLinks())
							if( l.getEndNode().equals(rm)){
								toberemovedf.add(l);
							}
						for(Link l: toberemovedf){
							getLinks().remove(l);
							getLinks().add(new Link(l.getStartNode(), rmf));
						}
						
						List<Link> toberemovedl = new ArrayList<Link>();
						for(Link l: getLinks())
							if( l.getStartNode().equals(rm)){
								toberemovedl.add(l);
							}
						for(Link l: toberemovedl){
							getLinks().remove(l);
							getLinks().add(new Link( rml,l.getEndNode()));
						}
						
						
						
						cn.getNodes().remove(rm);
						
						cn.getNodes().addAll(cnt,rm.getNodes());
						
						
						break;
					}
				}
			}
			
			getNodes().add(cn);
		}
		
		
	}
	private FoldingCandidate FindBestCandidate(List<FoldingCandidate> candidates) {
		int lowestComplexityPoint = 1;
		FoldingCandidate result = null;
		if(candidates.size()==1)
			return candidates.get(0);
		for(FoldingCandidate fc:candidates){
			int cp = 0;
			if(fc.getComponentNode() instanceof FlowComponentNode)
			{
				cp-= (fc.getComponentNode().getNodes().size()-1)*2;
			}
			if(fc.getComponentNode() instanceof SequenceComponentNode)
			{
				cp-= (fc.getComponentNode().getNodes().size()-1);
			}
			if((lowestComplexityPoint==1)|| (cp<=lowestComplexityPoint))
			{
				lowestComplexityPoint=cp;
				result = fc;
			}
			
		}
		return result;
	}
	
	
	private List<FoldingCandidate> getFoldingCandidates() {
		
		List<FoldingCandidate> result = new ArrayList<FoldingCandidate>();
		
		result.addAll(getSequenceFoldingCandidate());
		result.addAll(getFlowFoldingCandidateFixed());
		return result;
	}
	
	private List<FoldingCandidate> getFoldingCandidates2(int maxinumLinks) {
		
		List<FoldingCandidate> result = new ArrayList<FoldingCandidate>();
		
		result.addAll(getSequenceFoldingCandidate2(maxinumLinks));
		//result.addAll(getFlowFoldingCandidate2(maxinumLinks));
		result.addAll(getFlowFoldingCandidateFixed());
		return result;
	}
	
	private List<FoldingCandidate> getSequenceFoldingCandidate() {
		List<FoldingCandidate> result = new ArrayList<FoldingCandidate>();
		Queue<Node> nodes = new LinkedList<Node>();
		nodes.addAll(getNodes());
		
		while(!nodes.isEmpty())
		{
			 
			 Node curNode = nodes.remove();
			 List<Node> precedingNodes = getAllPreNodes(curNode);
			 List<Node> followingNodes = getAllPostNodes(curNode);
			 
			 
			 
			 Node afterNode = null;
			 Node beforeNode = curNode;
			 while((precedingNodes.size()==1)&&(followingNodes.size()==1)){
				 afterNode = beforeNode;
				 beforeNode = precedingNodes.get(0);
				 
				 precedingNodes = getAllPreNodes(beforeNode);
				 followingNodes = getAllPostNodes(beforeNode);
			 }
			 
			 if(followingNodes.size()!=1){
				 beforeNode = afterNode;
			 }
			 Node startNode=beforeNode; 
			 
			 if(startNode ==null)
				 continue;
			 
			 curNode = startNode;
			 
			 SequenceComponentNode scn = new SequenceComponentNode();
			 
			 precedingNodes = getAllPreNodes(curNode);
			 followingNodes = getAllPostNodes(curNode);
			 boolean ft = true;
			 
			 while((ft|| (precedingNodes.size()<2))&&(followingNodes.size()==1)){
				 ft = false;
				 scn.getNodes().add(curNode);
				
				 curNode = followingNodes.get(0);
				 
				 precedingNodes = getAllPreNodes(curNode);
				 followingNodes = getAllPostNodes(curNode);
				 
			 }
			 if((precedingNodes.size()<2)&&(followingNodes.size()!=1))
				 scn.getNodes().add(curNode);
			 else
			 if((precedingNodes.size()<2)&&(followingNodes.size()==0))
				 scn.getNodes().add(curNode);
			 
			 if(scn.getNodes().size()<2)
				 continue;
			 
			 for(Node n: scn.getNodes())
				 if(nodes.contains(n))
					 nodes.remove(n);
			 
			 result.add(new FoldingCandidate(scn, null, null));
		}
		
		
		return result;
	}
	
	private List<FoldingCandidate> getSequenceFoldingCandidate2(int maxinumLinks) {
		List<FoldingCandidate> result = new ArrayList<FoldingCandidate>();
		Queue<Node> nodes = new LinkedList<Node>();
		nodes.addAll(getNodes());
		
		while(!nodes.isEmpty())
		{
			 
			 Node curNode = nodes.remove();
			 List<SequenceComponentNode> ls = FindSequencesStartingFrom(curNode, maxinumLinks);
					 
			 
			 for(SequenceComponentNode scn : ls)
				 if(scn.getNodes().size()>1)
					 result.add(new FoldingCandidate(scn, null, null));
		}
		
		
		return result;
	}
	
	private List<SequenceComponentNode> FindSequencesStartingFrom(Node curNode, int chancesLeft) {
		
		List<Node> followingNodes = getAllPostNodes(curNode);
		 
		 int chancesUsedGoingDown = followingNodes.size()-1;
		 List<SequenceComponentNode> ls =new ArrayList<SequenceComponentNode>();
		 ls.add(new SequenceComponentNode());
		 
		 if((followingNodes.size()>0)&&(chancesUsedGoingDown<=chancesLeft))
		 {
			 
			 for(Node followingN : followingNodes){
				 List<Node> precedingNodes = getAllPreNodes(followingN);
				 int chancesLeftAfterCreatingSequence = chancesLeft-chancesUsedGoingDown-(precedingNodes.size()-1);
				 if(chancesLeftAfterCreatingSequence>=0)
					 ls.addAll(FindSequencesStartingFrom(followingN, chancesLeftAfterCreatingSequence));
			 }
			 
		 }
		 
		 for(SequenceComponentNode scn: ls){
			 scn.getNodes().add(0, curNode);
		 }
		 
		 return ls;
	}
	private List<Node> getAllPreNodes(Node n){
		List<Node> result = new ArrayList<Node>();
		for(Link l: getLinks())
			if(l.getEndNode().equals(n))
				if(getNodes().contains(l.getStartNode()))
					result.add(l.getStartNode());
		return result;
	}
	
	private List<Node> getAllPostNodes(Node n){
		List<Node> result = new ArrayList<Node>();
		for(Link l: getLinks())
			if(l.getStartNode().equals(n))
				if(getNodes().contains(l.getEndNode()))
					result.add(l.getEndNode());
		return result;
	}
	
//	private List<FoldingCandidate> getFlowFoldingCandidate() {
//		List<FoldingCandidate> result = new ArrayList<FoldingCandidate>();
//		Queue<Node> nodes = new LinkedList<Node>();
//		nodes.addAll(getNodes());
//		
//		while(!nodes.isEmpty())
//		{
//			Node curNode = nodes.remove();
//			List<Node> precedingNodes = getAllPreNodes(curNode);
//			List<Node> followingNodes = getAllPostNodes(curNode);
//			if((precedingNodes.size()>1)||(followingNodes.size()>1))
//				continue;
//			
//			if((precedingNodes.size()==1)&& (followingNodes.size()==1)){
//			
//				Node flowStartNode  = precedingNodes.get(0);
//				Node flowEndNode  = followingNodes.get(0);
//				
//				List<Node> flowStartNodeFollowing = getAllPostNodes(flowStartNode);
//				List<Node> flowEndNodePreceding = getAllPreNodes(flowEndNode);
//				List<Node> possibleCandidates = new ArrayList<Node>();
//				for(Node n1: flowStartNodeFollowing)
//					if(flowEndNodePreceding.contains(n1)&&(!possibleCandidates.contains(n1)))
//						possibleCandidates.add(n1);
//				
//				FlowComponentNode fcn = new FlowComponentNode();
//				fcn.getNodes().add(curNode);
//				
//				for(Node n: possibleCandidates)
//				{
//					if(n.equals(curNode))
//						continue;
//					precedingNodes = getAllPreNodes(n);
//					followingNodes = getAllPostNodes(n);
//					if((precedingNodes.size()!=1)||(followingNodes.size()!=1))
//						continue;
//					nodes.remove(n);
////					if(n.getClass().equals(FlowComponentNode.class))
////					{
////						fcn.getNodes().addAll(((FlowComponentNode) n).getNodes());
////						fcn.getLinks().addAll(((FlowComponentNode) n).getLinks());
////					}
////					else
//						fcn.getNodes().add(n);
//				}
//				if(fcn.getNodes().size()>1)
//					result.add(new FoldingCandidate(fcn, flowStartNode, flowEndNode));
//			
//			}
//			else{
//				continue;
////				if((precedingNodes.size()==0)&& (precedingNodes.size()==0)){
////					FlowComponentNode fcn = new FlowComponentNode();
////					fcn.getNodes().add(curNode);
////					for(Node n: nodes)
////					{
////						precedingNodes = getAllPreNodes(n);
////						followingNodes = getAllPostNodes(n);
////						if((precedingNodes.size()!=0)||(followingNodes.size()!=0))
////							continue;
////						nodes.remove(n);
////						fcn.getNodes().add(n);
////					}
////					if(fcn.getNodes().size()>1)
////						result.add(new FoldingCandidate(fcn, null, null));
////				}
//			}
//		}
//		return result;
//	}
	
	
	
//	private List<FoldingCandidate> getFlowFoldingCandidateFixed() {
//		List<FoldingCandidate> result = new ArrayList<FoldingCandidate>();
//		Queue<Node> nodes = new LinkedList<Node>();
//		nodes.addAll(getNodes());
//		
//		while(!nodes.isEmpty())
//		{
//			Node curNode = nodes.remove();
//			List<Node> precedingNodes = getAllPreNodes(curNode);
//			List<Node> followingNodes = getAllPostNodes(curNode);
//			if((precedingNodes.size()<1)||(followingNodes.size()<1))
//				continue;
//			
//			
//			
//			Node flowStartNode  = precedingNodes.get(0);
//				
//			List<Node> flowStartNodeFollowing = getAllPostNodes(flowStartNode);
//			List<Node> survivedNodes= new ArrayList<Node>();
//			survivedNodes.add(curNode);
//			
//			for(Node n: flowStartNodeFollowing){
//				if(survivedNodes.contains(n))
//					continue;
//				List<Node> pNodePrecedingNode  = getAllPreNodes(n);
//				List<Node> pNodeFollowingNodes = getAllPostNodes(n);
//				if((precedingNodes.size()!=pNodePrecedingNode.size()) || !UtilityClass.includesAll(pNodePrecedingNode, precedingNodes))
//					continue;
//				
//				if((followingNodes.size()!=pNodeFollowingNodes.size()) || !UtilityClass.includesAll(pNodeFollowingNodes,followingNodes))
//					continue;
//				
//				
//				survivedNodes.add(n);
//			}
//			
//			
//			if(survivedNodes.size()==1)
//				continue;
//			
//			
//			
//			FlowComponentNode fcn = new FlowComponentNode();
//			
//			
//			for(Node n: survivedNodes)
//			{
//		
//				
//				nodes.remove(n);
////					if(n.getClass().equals(FlowComponentNode.class))
////					{
////						fcn.getNodes().addAll(((FlowComponentNode) n).getNodes());
////						fcn.getLinks().addAll(((FlowComponentNode) n).getLinks());
////					}
////					else
//				fcn.getNodes().add(n);
//			}
//			if(fcn.getNodes().size()>1)
//				result.add(new FoldingCandidate(fcn, precedingNodes,followingNodes));
//			
//			
//		
//		}
//		return result;
//	}
//	
	
	private List<FoldingCandidate> getFlowFoldingCandidateFixed() {
		List<FoldingCandidate> result = new ArrayList<FoldingCandidate>();
		Queue<Node> nodes = new LinkedList<Node>();
		nodes.addAll(getNodes());
		
		while(!nodes.isEmpty())
		{
			Node curNode = nodes.remove();
			List<Node> precedingNodes = getAllPreNodes(curNode);
			List<Node> followingNodes = getAllPostNodes(curNode);
			if((precedingNodes.size()<1)&&(followingNodes.size()<1))
				continue;
			
			boolean noPrecedingNode= false;
			
			
			
			List<Node> flowStartNodeFollowing= null;
			
			if(precedingNodes.size()<1){
				flowStartNodeFollowing = new ArrayList<Node>();
				for(Node n:nodes)
					if(getAllPreNodes(n).size()==0)
						flowStartNodeFollowing.add(n);
				noPrecedingNode =true;
			}
			else{
				Node flowStartNode  = precedingNodes.get(0);
				flowStartNodeFollowing = getAllPostNodes(flowStartNode);
			}
				
			// these are the node which can form a FLOW structure with current node
			List<Node> survivedNodes= new ArrayList<Node>();
			survivedNodes.add(curNode);
			
			for(Node n: flowStartNodeFollowing){
				if(survivedNodes.contains(n))
					continue;
				
				if(!noPrecedingNode){
				
					List<Node> pNodePrecedingNode  = getAllPreNodes(n);
					
					if((precedingNodes.size()!=pNodePrecedingNode.size()) || !UtilityClass.includesAll(pNodePrecedingNode, precedingNodes))
						continue;
				}
				
				List<Node> pNodeFollowingNodes = getAllPostNodes(n);
				
				
				
				if((followingNodes.size()!=pNodeFollowingNodes.size()) || ((followingNodes.size()!=0)  && !UtilityClass.includesAll(pNodeFollowingNodes,followingNodes)))
					continue;
				
				
				survivedNodes.add(n);
			}
			
			
			if(survivedNodes.size()==1)
				continue;
			
			
			
			FlowComponentNode fcn = new FlowComponentNode();
			
			
			for(Node n: survivedNodes)
			{
		
				
				nodes.remove(n);
//					if(n.getClass().equals(FlowComponentNode.class))
//					{
//						fcn.getNodes().addAll(((FlowComponentNode) n).getNodes());
//						fcn.getLinks().addAll(((FlowComponentNode) n).getLinks());
//					}
//					else
				fcn.getNodes().add(n);
			}
			if(fcn.getNodes().size()>1)
				result.add(new FoldingCandidate(fcn, precedingNodes,followingNodes));
			
			
		
		}
		return result;
	}
	
//	private List<FoldingCandidate> getFlowFoldingCandidate2(int maxinumLinks) {
//		List<FoldingCandidate> result = new ArrayList<FoldingCandidate>();
//		Queue<Node> nodes = new LinkedList<Node>();
//		nodes.addAll(getNodes());
//		
//		while(!nodes.isEmpty())
//		{
//			Node curNode = nodes.remove();
//			List<Node> precedingNodes = getAllPreNodes(curNode);
//			List<Node> followingNodes = getAllPostNodes(curNode);
//			List<Node> twoFolowingNode = new ArrayList<Node>();
//			for(Node n: followingNodes){
//				List<Node> ff = getAllPostNodes(n);
//				for(Node fn: ff)
//					if(!twoFolowingNode.contains(fn))
//						twoFolowingNode.add(fn);
//			}
//				
//			for(Node enNodes: twoFolowingNode){
//				List<Node> enNodePrecedingNodes = getAllPreNodes(enNodes);
//				List<Node> intersection = new ArrayList<Node>();
//				for(Node fn: followingNodes)
//					if(enNodePrecedingNodes.contains(fn))
//						intersection.add(fn);
//				if(intersection.size()<2)
//					continue;
//				List<FlowComponentNode> cfcn = GetAllFlows(curNode,enNodes,new ArrayList<Node>(), intersection,maxinumLinks);
//				for(FlowComponentNode c: cfcn)
//					result.add(new FoldingCandidate(c, curNode,enNodes));
//			}
//		}
//		return result;
//	}
	@SuppressWarnings("unused")
	private List<FlowComponentNode> GetAllFlows(Node stNode, Node enNode,List<Node> inflowNodes, List<Node> intersection, int chancesLeft) {
		
		List<FlowComponentNode> result=new ArrayList<FlowComponentNode>();
		if(intersection.size()==0){
			if(inflowNodes.size()>1)
			{
				FlowComponentNode fcn = new FlowComponentNode();
				fcn.getNodes().addAll(inflowNodes);
				result.add(fcn);
			}
			return result;
		}
		Node mNode = intersection.remove(0);
		List<Node> precedingNodes = getAllPreNodes(mNode);
		List<Node> followingNodes = getAllPostNodes(mNode);
		
		
		int chancesUsed = precedingNodes.size()+followingNodes.size()-2;
		
		if((chancesLeft-chancesUsed)>=0)
		{
			List<Node> inflowNodeNew = new ArrayList<Node>();
			inflowNodeNew.addAll(inflowNodes);
			inflowNodeNew.add(mNode);
			List<Node> intersectionNew = new ArrayList<Node>();
			intersectionNew.addAll(intersection);
			intersectionNew.remove(mNode);
			result.addAll(GetAllFlows(stNode, enNode, inflowNodeNew, intersectionNew, chancesLeft-chancesUsed));
		}
		else{
			List<Node> intersectionNew = new ArrayList<Node>();
			intersectionNew.addAll(intersection);
			intersectionNew.remove(mNode);
			result.addAll(GetAllFlows(stNode, enNode, inflowNodes, intersectionNew, chancesLeft));
		}
		
		return result;
	}
	
	@Override
	public List<AtomicNode> getRealStartNodes(){
		List<AtomicNode> result = new ArrayList<AtomicNode>();
		for(Node n : getNodes()){
			if(n.getClass().equals(AtomicNode.class)){	
				result.add(n.getStartNode());
			}
			if(n.getClass().equals(SequenceComponentNode.class)){	
				result.addAll(((SequenceComponentNode) n).getRealStartNodes());
			}
			if(n.getClass().equals(FlowComponentNode.class)){	
				result.addAll(((FlowComponentNode) n).getRealStartNodes());
			}
			
			
		}
		
		return result;
		
	}
	
	@Override
	public List<AtomicNode> getRealEndNodes(){
		List<AtomicNode> result = new ArrayList<AtomicNode>();
		for(Node n : getNodes()){
			if(n.getClass().equals(AtomicNode.class)){	
				result.add(n.getEndNode());
			}
			if(n.getClass().equals(SequenceComponentNode.class)){	
				result.addAll(((SequenceComponentNode) n).getRealEndNodes());
			}
			if(n.getClass().equals(FlowComponentNode.class)){	
				result.addAll(((FlowComponentNode) n).getRealEndNodes());
			}

		}
		
		return result;
		
	}
	
	public static Node createARandomFlow(int no, List<Service> usedServices, ServiceCollection sc) throws Exception{
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
				FlowComponentNode fcn = new FlowComponentNode();
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
			int devisionCounter=0;
			FlowComponentNode fcn = new FlowComponentNode();
			while(remaining>0){
				int nextbunch = (int) UtilityClass.CuttedRandValueNormal(no/noOfDevisions, Math.max(no/(noOfDevisions*5),1), 1);
				devisionCounter++;
				if(remaining<nextbunch)
					nextbunch= remaining;
				if((devisionCounter==1)&&(nextbunch==remaining))
					nextbunch= remaining/2;
				fcn.getNodes().add(SequenceComponentNode.createARandomSequence(nextbunch,usedServices,sc));
				remaining-= nextbunch;
			}
			int NoOfLinks = (int) UtilityClass.CuttedRandValueNormal(1, 1,0);
			List<Node> startNodes = new ArrayList<Node>();
			List<Node> endNodes = new ArrayList<Node>();
			if(fcn.getNodes().size()-2< NoOfLinks)
				NoOfLinks = Math.max(fcn.getNodes().size()-2, 0);
		
		
			for(int cnt =0; cnt< NoOfLinks; cnt++){
				int stNodeIndex = UtilityClass.randInt(0, fcn.getNodes().size()-1);
				Node stNode = fcn.getNodes().get(stNodeIndex );
				
				Node astNode = stNode;
				if(stNode instanceof ComponentNode){
					astNode  =((ComponentNode) stNode).getNodes().get(  UtilityClass.randInt(0, ((ComponentNode) stNode).getNodes().size()-2));
				}
				
				List<Integer> ex = new ArrayList<Integer>();
				ex.add(stNodeIndex);
				int enNodeIndex = UtilityClass.randInt(0, fcn.getNodes().size()-1, ex);
				Node enNode = fcn.getNodes().get(enNodeIndex );
				
				Node aenNode = enNode;
				if(enNode instanceof ComponentNode){
					aenNode  =((ComponentNode) enNode).getNodes().get(  UtilityClass.randInt(1, ((ComponentNode) enNode).getNodes().size()-1));
				}
				
				boolean alreadyUsed = false;
				
				for(int cnt1=0; cnt1<startNodes.size(); cnt1++){
					if(startNodes.get(cnt1).equals(astNode)&&endNodes.get(cnt1).equals(aenNode))
					{
						alreadyUsed=true;
						break;
					}
				}
				if(!alreadyUsed)
					fcn.getLinks().add(new Link(astNode, aenNode));
				
				
				
			}
			
			return fcn;
			
			
		}
		
		
	}
	public String serializeToBpel(List<Instance> vars){
		StringBuilder result= new StringBuilder();
		String currentTab ="";
		result.append(currentTab+"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+System.lineSeparator());
	
		result.append(currentTab+"<!-- process1 BPEL Process [Generated by the MAGUS Mashup tool]  -->"+System.lineSeparator());
		
		//result.append(currentTab+""+System.lineSeparator());
		
		result.append(currentTab+"<bpel:process name=\"process1\""+System.lineSeparator());
		currentTab ="\t\t";
		result.append(currentTab+"targetNamespace=\"http://magus.online/repositories/orderprocessing/wsdl/process1\""+System.lineSeparator());
		result.append(currentTab+"suppressJoinFailure=\"yes\""+System.lineSeparator());
		result.append(currentTab+"xmlns:tns=\"http://magus.online/repositories/orderprocessing/wsdl/process1\""+System.lineSeparator());
		result.append(currentTab+"xmlns:bpel=\"http://docs.oasis-open.org/wsbpel/2.0/process/executable\""+System.lineSeparator());
		result.append(currentTab+">"+System.lineSeparator());
		         
		currentTab ="\t";
		
		
		result.append(currentTab+"<!-- ================================================================= -->"+System.lineSeparator());
		result.append(currentTab+"<!-- PARTNERLINKS                                                      -->"+System.lineSeparator());
		result.append(currentTab+"<!-- List of services participating in this BPEL process               -->"+System.lineSeparator());
		result.append(currentTab+"<!-- ================================================================= -->"+System.lineSeparator());
		result.append(currentTab+"<bpel:partnerLinks>"+System.lineSeparator());
		
		
		//Create Service List
		List<Service> svs= findAllCalledServices();
		for(Service s:svs){
			String cbStr = "";
			if(s.getReceiveService()!=null)
				cbStr ="myRole=\""+s.getReceiveService()+"\"";
			//myRole="taxRequester"
			
			result.append(currentTab+"\t<bpel:partnerLink name=\""+s.getName()+"\"  partnerRole=\""+s.getName()+"Service\" "+cbStr+"  partnerLinkType=\""+s.getURI()+"\"/>"+System.lineSeparator());
		}
		
		
		result.append(currentTab+"</bpel:partnerLinks>"+System.lineSeparator());
		       
		result.append(currentTab+""+System.lineSeparator());
		
		result.append(currentTab+"<!-- ================================================================= -->"+System.lineSeparator());
		result.append(currentTab+"<!-- VARIABLES                                                         -->"+System.lineSeparator());
		
		result.append(currentTab+"<!-- List of messages and XML documents used within this BPEL process  -->"+System.lineSeparator());
		result.append(currentTab+"<!-- ================================================================= --> "+System.lineSeparator());
		result.append(currentTab+"<bpel:variables> "+System.lineSeparator());
	   
		for(Instance v:vars)
			result.append(currentTab+"\t<bpel:variable name=\""+v.getName()+"\"  messageType=\""+v.getURI()+"\" />"+System.lineSeparator());
		//Create Variables Lists
		
		result.append(currentTab+"</bpel:variables>"+System.lineSeparator());
		result.append(currentTab+"<bpel:faultHandlers>"+System.lineSeparator());
		result.append(currentTab+"</bpel:faultHandlers>"+System.lineSeparator());
		
		
		result.append(currentTab+"<!-- ================================================================= -->"+System.lineSeparator());
		result.append(currentTab+"<!-- ORCHESTRATION LOGIC                                               -->"+System.lineSeparator());
		result.append(currentTab+"<!-- Set of activities coordinating the flow of messages across the    -->"+System.lineSeparator());
		result.append(currentTab+"<!-- services integrated within this business process                  -->"+System.lineSeparator());
		result.append(currentTab+"<!-- ================================================================= -->"+System.lineSeparator());
		result.append(currentTab+"<bpel:sequence name=\"main\">"+System.lineSeparator());
		
		         
		//currentTab=currentTab;
		
		String sep="";
        String inputs="";
        
		
        for(Instance v:vars)
        	if(v.getIo().equals("input")){
        		inputs= inputs+sep+v.getName();
        		sep=",";
        	}
			
        
	    result.append(currentTab+"\t"+"<bpel:receive operation=\"--\"   variable=\""+inputs+"\" createInstance=\"yes\" />"+System.lineSeparator());
	        
	        
	        
	    result.append(this.serializeToBpelComponent(currentTab+"\t", new ArrayList<Link>()));
	    //Create Flow
	    
	    sep="";
        String outputs="";
        
		
        for(Instance v:vars)
        	if(v.getIo().equals("output")){
        		outputs= outputs+sep+v.getName();
        		sep=",";
        	}
			
		result.append(currentTab+"\t"+"<bpel:reply operation=\"--\"   variable=\""+outputs+"\" />"+System.lineSeparator());
		  
		result.append(currentTab+"</bpel:sequence>"+System.lineSeparator());
		result.append("</bpel:process>"+System.lineSeparator());
	    
	    
	    
	        
		         
		         
		
		
		
		
		return result.toString();
		
		
		
		
	}
	public String serializeToBpelComponent(String inpTab, List<Link> plink) {
		
		StringBuilder result= new StringBuilder();
		String currentTab = inpTab;
		List<Link> clink = new ArrayList<Link>();
		clink.addAll(plink);
		clink.addAll(this.getLinks());
		result.append(currentTab+"<bpel:flow>"+System.lineSeparator());
		currentTab = currentTab+"\t";
		result.append(currentTab+"<bpel:links>"+System.lineSeparator());
		for(Link l:this.getLinks()){
			result.append(currentTab+"\t<bpel:link name=\""+l.getName()+ "\" />"+System.lineSeparator());
		}
		result.append(currentTab+"</bpel:links>"+System.lineSeparator());
		result.append(this.addSourceAndTargetLinks(currentTab,plink));
		for(Node n: this.getNodes()){
			result.append(n.serializeToBpelComponent(inpTab+"\t",clink));
		}
		
		
		
		
		result.append(inpTab+"</bpel:flow>"+System.lineSeparator());
		
		return result.toString();
	}
}

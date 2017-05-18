package edu.ls3.magus.cl.planning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ls3.magus.cl.contextmanager.basic.Condition;
import edu.ls3.magus.cl.contextmanager.basic.Instance;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstance;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstanceS;
import edu.ls3.magus.cl.contextmanager.basic.StateFactType;
import edu.ls3.magus.cl.contextmanager.context.ContextModel;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;

public class Problem {
	private ContextModel contextModel;
	private Condition initialState,goalState;
	private int iterationNo;
	private List<Instance> entities;
	
	
	public Problem(ContextModel contextModel, Condition initialState, Condition goalState)
	{
		this.contextModel = contextModel;
		this.initialState = initialState;
		this.goalState = goalState;
		this.entities = null;
	}
	public Problem(List<Instance> entities, Condition initialState, Condition goalState)
	{
		this.contextModel = null;
		this.initialState = initialState;
		this.goalState = goalState;
		this.entities =  entities;
	}
	
	public ContextModel getContextModel() {
		return contextModel;
	}
	
	public Condition getInitialState() {
		return initialState;
	}
	
	public Condition getGoalState() {
		return goalState;
	}
	
	
	public String PDDL3Serialize(String post){
		if(post==null)
			post="";
		StringBuilder sb = new StringBuilder();
		sb.append("(define (problem pt"+post+")");
		sb.append(System.lineSeparator());
		sb.append("\t(:domain pta"+post+")");
		sb.append(System.lineSeparator());
		sb.append("\t(:requirements :strips :typing  :negative-preconditions )");
		sb.append(System.lineSeparator());
		sb.append("\t(:objects ");
		sb.append(System.lineSeparator());
		
		List<Instance> li ;
		if(contextModel==null)
			li = entities;
		else
			li =  contextModel.getVars();
		
		for(Instance ins :li){
			sb.append("\t\t");
			sb.append( ins.getName()+ " - "+ins.getType().getTypeName() );
			sb.append(System.lineSeparator());
		}
//		for(InstanceType ins :negTypeIns){
//			sb.append("\t\t");
//			sb.append( "vdummy"+ins.getTypeName()+ " - "+ins.getTypeName() );
//			sb.append(System.lineSeparator());
//		}
		sb.append("\t)");
		sb.append(System.lineSeparator());
		
		sb.append("\t(:init (dummypredicate)  ");
		List<StateFactInstanceS> finalList = new ArrayList<StateFactInstanceS>();
		for(StateFactInstanceS sfis : initialState.getConditions())
			if(!sfis.isNot())
				finalList.add(sfis);
		
		
		sb.append(toPDDLCondition(null,new Condition( finalList),"\t", false, false,null));
		
		sb.append("\t)");
		sb.append(System.lineSeparator());
		
		sb.append("\t(:goal ");
		
		
		List<StateFactInstanceS> finalList2 = new ArrayList<StateFactInstanceS>();
		for(StateFactInstanceS sfis : goalState.getConditions())
			if(!sfis.isNot())
				finalList2.add(sfis);
		//goalState.getConditions().removeAll(remList2);
		
		
		sb.append(toPDDLCondition(null,new Condition(finalList2),"\t", false, true,null));
		sb.append("\t)");
		sb.append(System.lineSeparator());
		sb.append(")");
		
		return sb.toString();
	}
	
	static String toPDDLCondition(Service s, Condition state,String identation, boolean useQuestionMark, boolean useAnd, StateFactInstanceS additionalCondition) {
		
		if((state.getConditions().size()+((additionalCondition==null)?0:1))==0)
			return "";
		
		StringBuilder sb = new StringBuilder();
		
		
		List<StateFactInstanceS> con = new ArrayList<StateFactInstanceS>();
		con.addAll(state.getConditions());
		if(additionalCondition!=null)
			con.add(additionalCondition);
		
		Boolean includeAnd = false;
		if(useAnd && ( con.size() >1))
			includeAnd = true;
		if(includeAnd){
			sb.append("(and ");
			sb.append(System.lineSeparator());
		}
		for(StateFactInstanceS sfis : con){
			
			sb.append(identation);
			sb.append("\t");
			if(sfis.isNot()){
				sb.append("(not ");
			}
			sb.append("(");
			sb.append(sfis.getStateFactInstance().getType().getTypeName());
			for(int cnt=0; cnt< sfis.getStateFactInstance().getParams().length; cnt++){
				sb.append(" ");
				if(useQuestionMark){
					if((s!=null)&&( s.getContextVarList().contains(sfis.getStateFactInstance().getParams()[cnt].getURI())))
						System.out.println(sfis.getStateFactInstance().getParams()[cnt].getURI());
					
					if((s==null)||( !s.getContextVarList().contains(sfis.getStateFactInstance().getParams()[cnt].getURI())))
						sb.append("?");
				}
				sb.append( sfis.getStateFactInstance().getParams()[cnt].getName());
			}
			
			sb.append(")");
			
			if(sfis.isNot()){
				sb.append(")");
			}
			sb.append(System.lineSeparator());
			
		}
		
		if(includeAnd){
			sb.append(identation);
			sb.append("\t");
			sb.append(")");
			sb.append(System.lineSeparator());
		}
		
		return sb.toString();
	}

	public String PDDLserialize()
	{
		
		StringBuilder sb = new StringBuilder();
	 	sb.append("objects:");
	 	sb.append(System.lineSeparator());
	 	List<Instance> li ;
		if(contextModel==null)
			li = entities;
		else
			li =  contextModel.getVars();
	 	for(Instance ins : li)
	 		sb.append(ins.getType().getTypeName() + " ("+ ins.getName()+");"+System.lineSeparator());
	 	
	 	sb.append(System.lineSeparator());
	 	
	 	sb.append("init:");
	 	if(initialState.getConditions().size()==0)
	 		sb.append("");
	 	String andSign ="";
	 	for(StateFactInstanceS sfi : initialState.getConditions()){
	 		sb.append(andSign+ sfi.getStateFactInstance().getType().getTypeName()+"("+sfi.getStateFactInstance().getParams()[0].getName()+","+sfi.getStateFactInstance().getParams()[1].getName()+") ");
	 		andSign = "&";
	 	}
	 	sb.append(System.lineSeparator());
	 	sb.append("goal:");
	 	if(goalState.getConditions().size()==0)
	 		sb.append("");
	 	andSign ="";
	 	for(StateFactInstanceS sfi : goalState.getConditions()){
	 		sb.append(andSign+ sfi.getStateFactInstance().getType().getTypeName()+"("+sfi.getStateFactInstance().getParams()[0].getName()+","+sfi.getStateFactInstance().getParams()[1].getName()+")");
	 		andSign = "&";
	 	}
	 		

	 	return sb.toString();
	}

	public String Serialize() {
		
		StringBuilder sb = new StringBuilder();
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
		return sb.toString();
		

	}
	
	public static Problem readFromFile(File file, ContextModel cm) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(file));
		List<StateFactInstanceS> initialState = new ArrayList<StateFactInstanceS>();
		List<StateFactInstanceS> goalState = new ArrayList<StateFactInstanceS>();
//		boolean serviceCallsRead =false;
//		boolean initConditionsRead =false;
//		boolean goalConditionRead =false;
	    String line;
	    int phase =0;
	    while ((line = br.readLine()) != null) {
	      
	       if(line.equals("*INITIAL CONDITION*")){
	    	   
	    	   phase =3;
//	    	   initConditionsRead =true;
	    	   continue;
	       }
	       if(line.equals("*GOAL CONDITION*")){
	    	   phase = 4;
//	    	   goalConditionRead =true;
	    	   continue;
	       }
	       
	       
	       if((phase==3)||(phase==4))
	       {
	    	   String[] params = line.split(" ");
	    	   StateFactType sft = cm.getInstaceFactTypeByName(params[0])[0];
	    	   List<Instance> li = new ArrayList<Instance>();
	    	   
	    	   
	    	   
	    	   for(int pcnt =1; pcnt<=sft.getParams().length;  pcnt++ ){
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
		return new Problem(cm, new Condition(initialState), new Condition(goalState));
	
	}

	public int getIterationNo() {
		return iterationNo;
	}

	public void setIterationNo(int iterationNo) {
		this.iterationNo = iterationNo;
	}
	
	
}

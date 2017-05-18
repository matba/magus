package edu.ls3.magus.cl.planning;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.ls3.magus.cl.contextmanager.basic.Instance;
import edu.ls3.magus.cl.contextmanager.basic.InstanceType;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstance;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstanceS;
import edu.ls3.magus.cl.contextmanager.basic.StateFactType;
import edu.ls3.magus.cl.contextmanager.context.ContextModel;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.cl.mashupconfigurator.service.ServiceCollection;

public class ProblemDomain {
	private ContextModel contextModel;
	private ServiceCollection serviceCollection;
	
	
	public ProblemDomain(ContextModel contextModel,ServiceCollection serviceCollection){
		this.contextModel = contextModel;
		this.serviceCollection = serviceCollection;
	}

	public String PDDL3Serialize(String post,List<StateFactType> sftl ,List<InstanceType> insTL){
		if(post ==null)
			post="";
		StringBuilder sb = new StringBuilder();
		Map<Service, StateFactInstanceS> recievePredicateMap = new HashMap<Service, StateFactInstanceS>();
		sb.append("(define (domain pta"+post+")");
		sb.append(System.lineSeparator());
		sb.append("(:requirements :strips :typing  :negative-preconditions )");
		sb.append(System.lineSeparator());
		
		sb.append("(:types ");
		sb.append(System.lineSeparator());
		for(InstanceType it : insTL){
			sb.append("	   ");
			sb.append(it.getTypeName());
			sb.append(System.lineSeparator());
		}
		sb.append(")");
		sb.append(System.lineSeparator());
		
		sb.append("(:predicates ");
		sb.append(System.lineSeparator());
		sb.append("	   (dummypredicate)");
		sb.append(System.lineSeparator());
		
		for(StateFactType sft :sftl){
			sb.append("	   ");
			sb.append("(");
			sb.append(sft.getTypeName());
			for(InstanceType it : sft.getParams()){
				sb.append(" ");
				sb.append( "?v"+ it.getTypeName() + " - "+  it.getTypeName());
			}
			sb.append(")");
			sb.append(System.lineSeparator());
		}
		for(Service ss: serviceCollection.getServices()){
			 
			
			
			if(ss.getReceiveService()!=null)
			{
				String predicateName = "x"+UUID.randomUUID().toString().substring(0, 5);
				InstanceType[] params = new InstanceType[ss.getInputList().size()];
				for(int cnt=0; cnt<params.length; cnt++){
					params[cnt]= ss.getInputs().get(ss.getInputList().get(cnt)).getType();
				}
				StateFactType sft1 = new StateFactType(predicateName, null, params);
				Instance[] sftParams = new Instance[ss.getInputList().size()];
				for(int cnt=0; cnt<params.length; cnt++){
					sftParams[cnt]= ss.getInputs().get(ss.getInputList().get(cnt));
				}
				StateFactInstance sfi = new StateFactInstance(sft1, sftParams);
				StateFactInstanceS sfis = new StateFactInstanceS(sfi, false);
				recievePredicateMap.put(ss,sfis);
				sb.append("	   ");
				sb.append("(");
				
				sb.append(predicateName);
				for(URI it : ss.getInputList()){
					sb.append(" ");
					sb.append( "?v"+ ss.getInputs().get(it).getName() + " - "+  ss.getInputs().get(it).getType().getTypeName());
				}
				sb.append(")");
				sb.append(System.lineSeparator());
			}
		}
				
		sb.append(")");
		sb.append(System.lineSeparator());
		
		
		
		
		
		
		
		for(Service ss: serviceCollection.getServices()){
		
			
			if(ss.getReceiveService()!=null)
			{
				sb.append( serializeToOperatorPDDL(ss,recievePredicateMap.get(ss))+System.lineSeparator());
				sb.append( serializeToOperatorPDDL(ss.getReceiveService(),recievePredicateMap.get(ss))+System.lineSeparator());
			}
			else{
				sb.append( serializeToOperatorPDDL(ss,null)+System.lineSeparator());
			}
		}
		sb.append(")");
		
		return sb.toString();
	}
	public String PDDL3Serialize(String post){
		if(post ==null)
			post="";
		StringBuilder sb = new StringBuilder();
		Map<Service, StateFactInstanceS> recievePredicateMap = new HashMap<Service, StateFactInstanceS>();
		sb.append("(define (domain pta"+post+")");
		sb.append(System.lineSeparator());
		sb.append("(:requirements :strips :typing  :negative-preconditions )");
		sb.append(System.lineSeparator());
		
		sb.append("(:types ");
		sb.append(System.lineSeparator());
		for(InstanceType it : contextModel.getInstanceTypes().getTypesAsList()){
			sb.append("	   ");
			sb.append(it.getTypeName());
			sb.append(System.lineSeparator());
		}
		sb.append(")");
		sb.append(System.lineSeparator());
		
		sb.append("(:predicates ");
		sb.append(System.lineSeparator());
		
		for(StateFactType sft :contextModel.getFactTypes().getStateFactTypeList()){
			sb.append("	   ");
			sb.append("(");
			sb.append(sft.getTypeName());
			for(InstanceType it : sft.getParams()){
				sb.append(" ");
				sb.append( "?v"+ it.getTypeName() + " - "+  it.getTypeName());
			}
			sb.append(")");
			sb.append(System.lineSeparator());
		}
		for(Service ss: serviceCollection.getServices()){
			 
			if(ss.getReceiveService()!=null)
			{
				String predicateName = "x"+UUID.randomUUID().toString().substring(0, 5);
				InstanceType[] params = new InstanceType[ss.getInputList().size()];
				for(int cnt=0; cnt<params.length; cnt++){
					params[cnt]= ss.getInputs().get(ss.getInputList().get(cnt)).getType();
				}
				StateFactType sft1 = new StateFactType(predicateName, null, params);
				Instance[] sftParams = new Instance[ss.getInputList().size()];
				for(int cnt=0; cnt<params.length; cnt++){
					sftParams[cnt]= ss.getInputs().get(ss.getInputList().get(cnt));
				}
				StateFactInstance sfi = new StateFactInstance(sft1, sftParams);
				StateFactInstanceS sfis = new StateFactInstanceS(sfi, false);
				recievePredicateMap.put(ss,sfis);
				sb.append("	   ");
				sb.append("(");
				
				sb.append(predicateName);
				for(URI it : ss.getInputList()){
					sb.append(" ");
					sb.append( "?v"+ ss.getInputs().get(it).getName() + " - "+  ss.getInputs().get(it).getType().getTypeName());
				}
				sb.append(")");
				sb.append(System.lineSeparator());
			}
		}
				
		sb.append(")");
		sb.append(System.lineSeparator());
		
		
		
		
		
		
		
		for(Service ss: serviceCollection.getServices()){
		
			
			if(ss.getReceiveService()!=null)
			{
				sb.append( serializeToOperatorPDDL(ss,recievePredicateMap.get(ss))+System.lineSeparator());
				sb.append( serializeToOperatorPDDL(ss.getReceiveService(),recievePredicateMap.get(ss))+System.lineSeparator());
			}
			else{
				sb.append( serializeToOperatorPDDL(ss,null)+System.lineSeparator());
			}
		}
		sb.append(")");
		
		return sb.toString();
	}
	
	private String serializeToOperatorPDDL(Service s, StateFactInstanceS sfis) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("(:action ");
		sb.append(s.getName());
		sb.append(System.lineSeparator());
		
		
		sb.append("	   ");
		sb.append(":parameters (");
		sb.append(System.lineSeparator());
		
		for(URI key :  s.getInputList())
		{
			Instance i = s.getInputs().get(key);
			sb.append("	   	   ");
			sb.append("?");
			sb.append(i.getName());
			sb.append(" - ");
			sb.append(i.getType().getTypeName());
			sb.append(System.lineSeparator());
			
		}
		for(URI key :  s.getOutputList())
		{
			Instance i = s.getOutputs().get(key);
			sb.append("	   	   ");
			sb.append("?");
			sb.append(i.getName());
			sb.append(" - ");
			sb.append(i.getType().getTypeName());
			sb.append(System.lineSeparator());
			
			
		}
		for(URI key :  s.getVarList())
		{
			Instance i = s.getVars().get(key);
			sb.append("	   	   ");
			sb.append("?");
			sb.append(i.getName());
			sb.append(" - ");
			sb.append(i.getType().getTypeName());
			sb.append(System.lineSeparator());
			
		}
		
		
		sb.append("	   ");
		sb.append(")");
		sb.append(System.lineSeparator());
		
		String pc = "";
		if((sfis!=null)&&(s.getReceiveService()==null)){
			
			
			pc = Problem.toPDDLCondition(s,s.getPrecondition(),"	   " , true, true, sfis);
		}
		else{
			pc = Problem.toPDDLCondition(s,s.getPrecondition(),"	   " , true, true,null);
		}
		if(pc.length()>0){
		sb.append("	   ");
		sb.append(":precondition");
		sb.append(System.lineSeparator());
		
		sb.append("	   	   ");
		sb.append(pc);
		
		sb.append(System.lineSeparator());
		
		}
		
		sb.append("	   ");
		sb.append(":effect");
		
		sb.append(System.lineSeparator());
		sb.append("	   	   ");
		if((sfis!=null)&&(s.getReceiveService()!=null)){
			sb.append(Problem.toPDDLCondition(s,s.getPostcondition(),"	   ", true, true,sfis));
		}else{
			sb.append(Problem.toPDDLCondition(s,s.getPostcondition(),"	   ", true, true,null));
		}
		
		
		sb.append(")");
		
		
		
		//sb.append("[");
		//sb.append("]");
		return sb.toString();
	}

	public String PDDLserialize(){
		StringBuilder sb = new StringBuilder();
		for(Service s :serviceCollection.getServices())
			sb.append(serializeToOperator(s));
		return sb.toString();
		
	}
	
	
	public String serializeToOperatorPDDL(Service s){
		return serializeToOperatorPDDL(s, null); 
	}
	
	public String serializeToOperator(Service s){
		StringBuilder sb = new StringBuilder();
		sb.append("operator: ");
		sb.append(s.getName());
		sb.append("(");
		String prefix = "";
		for(URI key :  s.getInputList())
		{
			sb.append(prefix);
			prefix = ", ";
			Instance i = s.getInputs().get(key);
			sb.append(i.getType().getTypeName());
			sb.append(" ?");
			sb.append(i.getName());
			
		}
		for(URI key :  s.getOutputList())
		{
			sb.append(prefix);
			prefix = ", ";
			Instance i = s.getOutputs().get(key);
			sb.append(i.getType().getTypeName());
			sb.append(" ?");
			sb.append(i.getName());
			
		}
		for(URI key :  s.getVarList())
		{
			sb.append(prefix);
			prefix = ", ";
			Instance i = s.getVars().get(key);
			sb.append(i.getType().getTypeName());
			sb.append(" ?");
			sb.append(i.getName());
			
		}
		sb.append(")");
		sb.append(System.lineSeparator());
		sb.append("");
		sb.append(System.lineSeparator());
		sb.append("[");
		prefix ="";
		for(StateFactInstanceS sfi : s.getPrecondition().getConditions()){
			sb.append(prefix);
			prefix = "& ";
			sb.append(sfi.getStateFactInstance().getType().getTypeName());
			sb.append("(");
			Instance[] p = sfi.getStateFactInstance().getParams();
			String prefix2 ="";
			for(Instance i: p)
			{
				sb.append(prefix2);
				prefix2=", ";
				sb.append("?");
				sb.append(i.getName());
			}
			sb.append(")");
		}
		sb.append("]");
		sb.append(System.lineSeparator());
		sb.append("[");
		prefix ="";
		for(StateFactInstanceS sfi : s.getPostcondition().getConditions()){
			sb.append(prefix);
			prefix = "& ";
			sb.append(sfi.getStateFactInstance().getType().getTypeName());
			sb.append(" (");
			Instance[] p = sfi.getStateFactInstance().getParams();
			String prefix2 ="";
			for(Instance i: p)
			{
				sb.append(prefix2);
				prefix2=", ";
				sb.append("?");
				sb.append(i.getName());
			}
			sb.append(")");
		}
		sb.append("]");
		sb.append(System.lineSeparator());
		sb.append("");
		//sb.append("[");
		//sb.append("]");
		return sb.toString();
	}
}

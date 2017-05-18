package edu.ls3.magus.cl.contextmanager.basic;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Condition  implements java.io.Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4861656785090892506L;
	private List<StateFactInstanceS> conditions;

	public List<StateFactInstanceS> getConditions() {
		return conditions;
	}

	public void setConditions(List<StateFactInstanceS> conditions) {
		this.conditions = conditions;
	}
	
	public Condition( List<StateFactInstanceS> cl)
	{
		
		this.conditions = new ArrayList<StateFactInstanceS>();
		this.conditions.addAll(cl);
		
			
	}

	public boolean haveIntersection(Condition c){
		for(StateFactInstanceS cond: getConditions())
		{
			for(StateFactInstanceS cond2: c.getConditions())
				if( cond.equals(cond2))
					return true;
		}
		
		
		return false;
	}

	public boolean subset(Condition c) {
		
		for(StateFactInstanceS cond: getConditions())
		{
			boolean found = false;
			for(StateFactInstanceS cond2: c.getConditions())
				if( cond.equals(cond2))
					found =true;
			if(!found)
				return false;
		}
		
		return true;
	}

	public Condition replace(Map<URI, Instance> original, Map<URI, Instance> replacement) {
		
		List<StateFactInstanceS> result= new ArrayList<StateFactInstanceS>();
		Map<Instance,Instance> replacementHM = new HashMap<Instance, Instance>();
		
		for(URI u : original.keySet())
			replacementHM.put(original.get(u), replacement.get(u));
			
		
		for(StateFactInstanceS sfis : getConditions())
		{
			
			Instance[] newparams = new Instance[sfis.getStateFactInstance().getParams().length];
			for(int cnt=0; cnt<newparams.length; cnt++)
				newparams[cnt] = replacementHM.get(sfis.getStateFactInstance().getParams()[cnt]);
			StateFactInstance sfi = new StateFactInstance(sfis.getStateFactInstance().getType(), newparams );
			StateFactInstanceS newsfis = new StateFactInstanceS(sfi, sfis.isNot());
			result.add(newsfis);
		}
		return new Condition(result);
	}

	public boolean hasFact(StateFactInstanceS sfi) {
		
		return getConditions().contains(sfi);
	}

	public boolean hasNotFact(StateFactInstanceS inp) {
		
		StateFactInstanceS  sfis = new StateFactInstanceS(inp.getStateFactInstance(), !inp.isNot());
		
		return getConditions().contains(sfis);
	}

	public void removeNot(StateFactInstanceS sfis) {
		
		StateFactInstanceS  t = new StateFactInstanceS(sfis.getStateFactInstance(), !sfis.isNot());
		getConditions().remove(t);
	}

	public static List<StateFactType> FilterByEntities(List<StateFactType> factTypes,
			List<Instance> instanceList) {
		List<StateFactType> result = new ArrayList<StateFactType>();
		
		List<InstanceType> itList = new ArrayList<InstanceType>();
		
		for(Instance ins:instanceList)
			if(!itList.contains(ins.getType()))
				itList.add(ins.getType());
		
		for(StateFactType csft: factTypes){
			boolean allTypesCovered = true;
			for(int cntr=0; cntr<csft.getParams().length; cntr++)
				if(!itList.contains( csft.getParams()[cntr])){
					allTypesCovered=false;
					break;
				}
			if(allTypesCovered)
				result.add(csft);
					
		}
		
		return result;
	}

	public int calculateDistance(Condition condition) {
		
		int distance =0;
		for(StateFactInstanceS sfis: getConditions()){
			if(!condition.getConditions().contains(sfis))
				distance++;
		}
		
		for(StateFactInstanceS sfis: condition.getConditions()){
			if(!getConditions().contains(sfis))
				distance++;
		}
		
		return distance;
	}

	public static List<Instance> getInvolvedInstances(Condition condition) {
		List<Instance> result = new ArrayList<Instance>();
		
		for(StateFactInstanceS sfis: condition.getConditions() ){
			for(Instance ins: sfis.getStateFactInstance().getParams())
				if(!result.contains(ins))
					result.add(ins);
		}
		
		return result;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String sep ="";
		
		for(StateFactInstanceS sfis: getConditions()){
			sb.append(sep+ sfis.toString());
			sep = ", ";
			
		}
		
		
		return sb.toString();
	}
	
}

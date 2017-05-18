package edu.ls3.magus.cl.contextmanager.basic;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class StateFactTypeMap {
private HashMap<URI,StateFactType> stateFactTypeMap ;
	
	public StateFactTypeMap(){
		setStateFactTypeMap(new HashMap<URI, StateFactType>());
	}
	
	public boolean exists(URI inp){
		return getStateFactTypeMap().containsKey(inp);
			
	}
	public StateFactType get(URI inp) throws Exception{
		if(getStateFactTypeMap().containsKey(inp))
			return getStateFactTypeMap().get(inp);
		else
			throw new Exception("Fact type not found!");
		
	}
	public StateFactType add(String typeName, URI typeURI, InstanceType[] params) throws Exception{
		if(getStateFactTypeMap().containsKey(typeURI))
		{
			throw new Exception("Already Exists!");
		}
		StateFactType newIT = new StateFactType(typeName, typeURI,params);
		getStateFactTypeMap().put(typeURI, newIT);
		return newIT;
	}
	public StateFactType add(StateFactType newIT) throws Exception{
		if(getStateFactTypeMap().containsKey(newIT.getURI()))
		{
			throw new Exception("Already Exists!");
		}
		
		getStateFactTypeMap().put(newIT.getURI(), newIT);
		return newIT;
	}

	public HashMap<URI,StateFactType> getStateFactTypeMap() {
		return stateFactTypeMap;
	}

	private void setStateFactTypeMap(HashMap<URI,StateFactType> stateFactTypeMap) {
		this.stateFactTypeMap = stateFactTypeMap;
	}
	
	public List<StateFactType> getStateFactTypeList(){
		List<StateFactType> result = new ArrayList<StateFactType>();
		result.addAll(stateFactTypeMap.values());
		return result;
	}
	
}

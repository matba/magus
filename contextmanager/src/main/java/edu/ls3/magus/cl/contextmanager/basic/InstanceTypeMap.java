package edu.ls3.magus.cl.contextmanager.basic;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.ls3.magus.utility.UtilityClass;


public class InstanceTypeMap {
	private HashMap<URI,InstanceType> instanceTypeMap ;
	
	public InstanceTypeMap(){
		setInstanceTypeMap(new HashMap<URI, InstanceType>());
	}
	
	public InstanceType addORGet(URI inp){
		if(getInstanceTypeMap().containsKey(inp))
			return getInstanceTypeMap().get(inp);
		InstanceType newIT = new InstanceType(UtilityClass.getLocalName(inp), inp);
		getInstanceTypeMap().put(inp, newIT);
		return newIT;
	}
	
	
	public InstanceType get(URI inp) throws Exception{
		if(getInstanceTypeMap().containsKey(inp))
			return getInstanceTypeMap().get(inp);
		else
			throw new Exception("The Instance type cannot be found!");
	}
	public HashMap<URI,InstanceType> getInstanceTypeMap() {
		return instanceTypeMap;
	}

	private void setInstanceTypeMap(HashMap<URI,InstanceType> instanceTypes) {
		this.instanceTypeMap = instanceTypes;
	}

	public List<InstanceType> getTypesAsList() {
		
		
		return new ArrayList<InstanceType>(instanceTypeMap.values());
	}
	public void add(InstanceType newInstanceType) throws Exception{
		if(getInstanceTypeMap().containsKey(newInstanceType.getURI()))
		{
			throw new Exception("InstanceType already exists!");
		}
		
		getInstanceTypeMap().put(newInstanceType.getURI(), newInstanceType);
		
	}
}

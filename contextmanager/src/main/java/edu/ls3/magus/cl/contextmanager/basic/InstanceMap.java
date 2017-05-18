package edu.ls3.magus.cl.contextmanager.basic;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstanceMap {
	private Map<URI,Instance> instanceMap;
	
	public InstanceMap(){
		setInstanceMap(new HashMap<URI,Instance>());
	}

	public Map<URI,Instance> getInstanceMap() {
		return instanceMap;
	}

	private void setInstanceMap(Map<URI,Instance> instanceSet) {
		this.instanceMap = instanceSet;
	}
	
	public boolean contains(URI uri){
		return instanceMap.containsKey(uri);
		
	}
	public Instance get(URI uri){
		return instanceMap.get(uri);
	}
	public void add(Instance newInstance) throws Exception{
		if(getInstanceMap().containsKey(newInstance.getURI()))
		{
			throw new Exception("Instance already exists!");
		}
		
		getInstanceMap().put(newInstance.getURI(), newInstance);
		
	}
	public List<Instance> getAsList(){
		List<Instance> result = new ArrayList<Instance>( getInstanceMap().values());
		return result;
	}
}


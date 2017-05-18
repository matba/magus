package edu.ls3.magus.cl.contextmanager.basic;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Instance implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7009718114555758746L;
	private final InstanceType type;
	private final String name;
	private final URI uri;
	private String io;
	//private final UUID uid;
	
	public Instance(InstanceType type, String name, URI uri){
		this.type = type;
		this.uri = uri;
		this.name = name;
		this.io = "none";
		//uid = UUID.randomUUID();
	}

	public URI getURI() {
		return uri;
	}

	public String getName() {
		return name;
	}

	public InstanceType getType() {
		return type;
	}

//	public UUID getUid() {
//		return uid;
//	}
//	
	
	@Override
	public boolean equals(Object i){
		if(i.getClass() != Instance.class)
			return false;
		Instance s = (Instance) i;
		if(s.getURI().equals(getURI()))
			return true;
		return false;
	}

	public static List<InstanceType> getAllTypes(List<Instance> insList) {
	
		List<InstanceType> result = new ArrayList<InstanceType>();
		
		for(Instance i:insList)
			if(!result.contains(i.getType()))
				result.add(i.getType());
		
		return result;
		
		
	}

	public String getIo() {
		return io;
	}

	public void setIo(String io) {
		this.io = io;
	}

	public static List<Instance> Filter(List<Instance> insList, InstanceType instanceType) {
		List<Instance> result = new ArrayList<Instance>();
		for(Instance ins:insList)
			if(instanceType.equals(ins.getType()))
				result.add(ins);
		
		return result;

	}
	
	@Override
	public String toString(){
		return getType().toString()+"("+getName()+")";
	}

}

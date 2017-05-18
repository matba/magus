package edu.ls3.magus.cl.contextmanager.basic;

import java.net.URI;

public class InstanceType implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -815798910236254496L;
	private final String typeName;
	private final URI typeURI;
	public InstanceType(String typeName, URI typeURI){
		this.typeName = typeName;
		this.typeURI = typeURI;
		
	}
	public String getTypeName() {
		return typeName;
	}
	public URI getURI() {
		return typeURI;
	}
	
	@Override
	public boolean equals(Object i){
		if(i.getClass() != InstanceType.class)
			return false;
		InstanceType s = (InstanceType) i;
		if(s.getURI().equals(getURI()))
			return true;
		return false;
	}
	
	@Override
	public String toString(){
		return getTypeName();
	}
}

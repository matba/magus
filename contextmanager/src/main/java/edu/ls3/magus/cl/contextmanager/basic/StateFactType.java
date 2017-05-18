package edu.ls3.magus.cl.contextmanager.basic;

import java.net.URI;

public class StateFactType implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7333263944555651389L;
	private final String typeName;
	private final URI typeURI;
	private final InstanceType[] params;
	
	public StateFactType(String typeName, URI typeURI, InstanceType[] params){
		this.typeName = typeName;
		this.typeURI = typeURI;
		this.params = params;
	}
	
	public String getTypeName() {
		return typeName;
	}
	public URI getURI() {
		return typeURI;
	}
	public InstanceType[] getParams() {
		return params;
	}
	@Override
	public String toString(){
		return getTypeName();
	}
	
}

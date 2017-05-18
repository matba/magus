package edu.ls3.magus.cl.contextmanager.basic;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

public class StateFactInstance  implements java.io.Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4820479894253194947L;
	private final StateFactType type;
	private final String name;
	private final URI uri;
	private final UUID uid;
	private final Instance[] params;
	
	
	
	public StateFactInstance(StateFactType type, String name, URI uri, Instance[] params){
		this.type = type;
		this.uri = uri;
		this.name = name;
		uid = UUID.randomUUID();
		this.params = params;
	}
	
	public StateFactInstance(StateFactType type,  Instance[] params){
		this.type = type;
		this.uri = null;
		this.name = "";
		uid = UUID.randomUUID();
		this.params = params;
	}

	public StateFactType getType() {
		return type;
	}


	public String getName() {
		return name;
	}


	public UUID getUid() {
		return uid;
	}


	public URI getUri() {
		return uri;
	}


	public Instance[] getParams() {
		return params;
	}
	
	@Override
	public boolean equals(Object i){
		if(i.getClass() != StateFactInstance.class)
			return false;
		StateFactInstance s = (StateFactInstance) i;
		
		if(!s.getType().equals(getType()))
			return false;
		
		for(int cnt=0; cnt<getParams().length; cnt++)
		{
			if(!params[cnt].equals(s.getParams()[cnt]))
				return false;
		}
		return true;
	}
	
	 @Override
	    public int hashCode() {
	        int hash = 1;
	        hash = hash * 17 + getType().hashCode();
	        if((params!=null)&&(params.length>0))
	        hash = hash * 31 + params[0].hashCode();
	        if((params!=null)&&(params.length>1))
	        hash = hash * 13 + params[1].hashCode();
	        return hash;
	    }

	public StateFactInstance replaceParams( Map<URI, Instance> instanceMap,
			Map<URI, URI> instanceMappingMap) {
		
		Instance[] newparams = new Instance[getParams().length];
		
		for(int cntr=0; cntr<params.length; cntr++){
			newparams[cntr]=instanceMap.get(instanceMappingMap.get(getParams()[cntr].getURI()));
		}
		
		
		return new StateFactInstance(getType(), newparams);
	}

	@Override
	public String toString(){

		StringBuilder sb = new StringBuilder();
		
		sb.append(getType().getTypeName());
		sb.append("(");
		String sep="";
		for(Instance ins:getParams())
		{
			sb.append(sep);
			sb.append(ins.getName());
			
			sep = ", ";
		}
		sb.append(")");
		
		return sb.toString();
	}
}

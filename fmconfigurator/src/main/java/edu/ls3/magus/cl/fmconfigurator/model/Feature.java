package edu.ls3.magus.cl.fmconfigurator.model;


import java.util.ArrayList;
import java.util.List;



public class Feature implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2714787218417178675L;
	public static final String varPrefix = "f";
	private String name;
	
	// Specifies if the feature is an optional feature
	private boolean isOptional;
	// Specifies if children of this feature form an alternative group
	private boolean isAlternative;
	//Specifies if children of this feature form an or group
	private boolean isOrGroup;;
	private List<Feature> children;
	private String uuid;
	
	public Feature(String name, boolean isOptional, boolean isAlternative, boolean isOrGroup, String uuid ){
		this.name=name;
		this.setOptional(isOptional);
		this.setAlternative(isAlternative);
		this.setOrGroup(isOrGroup);
		this.setChildren(new ArrayList<Feature>());
		this.setUuid(uuid);
	}
	
	public List<Feature> getFeatureAsList(){
		List<Feature> result = new ArrayList<Feature>();
		result.add(this);
		for(Feature c: this.getChildren())
			result.addAll(c.getFeatureAsList());
		
		return result;
		
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unused")
	private void setName(String name) {
		this.name = name;
	}

	

	public Feature findFeatureByName(String fn) {
		Feature result = null;
		if(this.getName().equals(fn))
			return this;
		
		for(Feature f : this.getChildren()){
			Feature curResult = f.findFeatureByName(fn);
			if(curResult!= null)
				return curResult;
		}
		return result;
		
	}

	public boolean isOptional() {
		return isOptional;
	}

	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	public boolean isAlternative() {
		return isAlternative;
	}

	public void setAlternative(boolean isAlternative) {
		this.isAlternative = isAlternative;
	}

	public boolean isOrGroup() {
		return isOrGroup;
	}

	public void setOrGroup(boolean isOrGroup) {
		this.isOrGroup = isOrGroup;
	}

	public List<Feature> getChildren() {
		return children;
	}

	private void setChildren(List<Feature> children) {
		this.children = children;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUuidVarName() {
		return  Feature.varPrefix+ uuid.replaceAll("-", "");
	}
	@Override
	public String toString(){
		return getName();
	}

	public boolean isGroupingFeature() {
		return (isAlternative()||isOrGroup());
	}
	

	
}

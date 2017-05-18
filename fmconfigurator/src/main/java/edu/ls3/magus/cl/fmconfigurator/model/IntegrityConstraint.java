package edu.ls3.magus.cl.fmconfigurator.model;

public class IntegrityConstraint {
	private Feature sourceFeature;
	private Feature targetFeature;
	private String type;
	static final String REQUIRES="requires";
	static final String EXCLUDES="excludes";
	
	public IntegrityConstraint(Feature sourceFeature, String type, Feature targetFeature){
		this.setSourceFeature(sourceFeature);
		this.setType(type);
		this.setTargetFeature(targetFeature);
	}

	public Feature getSourceFeature() {
		return sourceFeature;
	}

	private void setSourceFeature(Feature sourceFeature) {
		this.sourceFeature = sourceFeature;
	}

	public Feature getTargetFeature() {
		return targetFeature;
	}

	private void setTargetFeature(Feature targetFeature) {
		this.targetFeature = targetFeature;
	}

	public String getType() {
		return type;
	}

	private void setType(String type) {
		this.type = type;
	}
}

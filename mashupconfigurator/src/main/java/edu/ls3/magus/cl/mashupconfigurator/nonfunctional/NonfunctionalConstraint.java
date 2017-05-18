package edu.ls3.magus.cl.mashupconfigurator.nonfunctional;

public class NonfunctionalConstraint {
	private NonfunctionalMetricType type;
	private Double threshold;
	private boolean isLower;
	
	public NonfunctionalConstraint(NonfunctionalMetricType type, Double threshold, boolean isLower){
		this.type = type;
		this.threshold = threshold;
		this.isLower = isLower;
	}
	
	
	public NonfunctionalMetricType getType() {
		return type;
	}
	public void setType(NonfunctionalMetricType type) {
		this.type = type;
	}
	public Double getThreshold() {
		return threshold;
	}
	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}
	public boolean isLower() {
		return isLower;
	}
	public void setLower(boolean isLower) {
		this.isLower = isLower;
	}
	
	public boolean satisfy(Double value){
		if((isLower() && (value > getThreshold()) ) ||(!isLower() && (value < getThreshold()) ))
			return false;
		return true;
	}
	
	
}

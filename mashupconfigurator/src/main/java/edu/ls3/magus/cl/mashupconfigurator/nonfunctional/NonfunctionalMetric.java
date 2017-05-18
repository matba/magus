package edu.ls3.magus.cl.mashupconfigurator.nonfunctional;

import edu.ls3.magus.utility.UtilityClass;

public  class NonfunctionalMetric {
	
	private double average;
	private double standardDeviation;
	public NonfunctionalMetricType type ;
	
	
	
	
	
	public NonfunctionalMetric(NonfunctionalMetricType type, double average, double standardDeviation){
		setAverage(average);
		setStandardDeviation(standardDeviation);
		setType(type);
	}
	
	
	public double getExecutionValue() {
		
		double result = UtilityClass.randValueNormal(getAverage(), getStandardDeviation());
		if(result<0)
			return 0;
		return result;
	}
	
	
	public NonfunctionalMetricType getType(){
		return type;
	}
	
	public void setType(NonfunctionalMetricType type){
		this.type= type;
	}
	
	
	public double getAverage() {
		return average;
	}
	private void setAverage(double average) {
		this.average = average;
	}
	public double getStandardDeviation() {
		return standardDeviation;
	}
	private void setStandardDeviation(double standardDevation) {
		this.standardDeviation = standardDevation;
	}
	//public double getValue();
}

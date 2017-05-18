package edu.ls3.magus.cl.mashupconfigurator.nonfunctional;

import java.util.HashMap;
import java.util.Map;

public class NonfunctionalAnnotationSet {
	private Map<NonfunctionalMetricType, NonfunctionalMetric> annotation;
	
	public NonfunctionalAnnotationSet(){
		setAnnotation(new HashMap<NonfunctionalMetricType, NonfunctionalMetric>());
	}

	public Map<NonfunctionalMetricType, NonfunctionalMetric> getAnnotation() {
		return annotation;
	}

	private void setAnnotation(Map<NonfunctionalMetricType, NonfunctionalMetric> annotation) {
		this.annotation = annotation;
	}
}

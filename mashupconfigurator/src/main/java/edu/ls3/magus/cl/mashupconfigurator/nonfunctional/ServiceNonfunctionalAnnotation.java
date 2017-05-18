package edu.ls3.magus.cl.mashupconfigurator.nonfunctional;

import java.util.HashMap;
import java.util.Map;

import edu.ls3.magus.cl.mashupconfigurator.service.Service;

public class ServiceNonfunctionalAnnotation {
	private Service service;
	
	private Map<NonfunctionalMetricType, NonfunctionalMetric> annotation;
	
	
	public ServiceNonfunctionalAnnotation(Service service)
	{
		setService(service);
		
		setAnnotation(new HashMap<NonfunctionalMetricType, NonfunctionalMetric>());
	}
	
	public ServiceNonfunctionalAnnotation(Service service, Map<NonfunctionalMetricType, NonfunctionalMetric> annotation){
		setService(service);
		setAnnotation(annotation);
	}
	
	public Service getService() {
		return service;
	}
	private void setService(Service service) {
		this.service = service;
	}
	public Map<NonfunctionalMetricType, NonfunctionalMetric> getAnnotation() {
		return annotation;
	}
	private void setAnnotation(Map<NonfunctionalMetricType, NonfunctionalMetric> annotation) {
		this.annotation = annotation;
	}
	
	@Override
	public ServiceNonfunctionalAnnotation clone(){
		
		Map<NonfunctionalMetricType, NonfunctionalMetric> annotation = new HashMap<NonfunctionalMetricType, NonfunctionalMetric>();
		annotation.putAll(getAnnotation());
		
		return new ServiceNonfunctionalAnnotation(service, annotation);
		
	}



	
}

package edu.ls3.magus.cl.mashupconfigurator.nonfunctional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.utility.UtilityClass;

public class ServiceNonfunctionalAnnotationMap {
	private Map<Service, ServiceNonfunctionalAnnotation> annotationMap;	 
	
	public ServiceNonfunctionalAnnotationMap(){
		setAnnotationMap(new HashMap<Service, ServiceNonfunctionalAnnotation>());
	}
	
	public ServiceNonfunctionalAnnotationMap(List<Service> serviceList){
		setAnnotationMap(new HashMap<Service, ServiceNonfunctionalAnnotation>());
		for(Service s: serviceList)
			getAnnotationMap().put(s, new ServiceNonfunctionalAnnotation(s));
	}

	public ServiceNonfunctionalAnnotationMap(Map<Service, ServiceNonfunctionalAnnotation> annotationMap){
		this.setAnnotationMap(annotationMap);
		
	}
	public Map<Service, ServiceNonfunctionalAnnotation> getAnnotationMap() {
		return annotationMap;
	}

	private void setAnnotationMap(Map<Service, ServiceNonfunctionalAnnotation> annotationMap) {
		this.annotationMap = annotationMap;
	}

	public void generateAnnotationFromMap(Map<String, Double> serviceNFMap, NonfunctionalMetricType nfType) throws Exception {
		for(Service s: annotationMap.keySet()){
			if(serviceNFMap.containsKey(s.getName())){
				NonfunctionalMetric n = new NonfunctionalMetric(nfType, serviceNFMap.get(s.getName()), 0d);
				annotationMap.get(s).getAnnotation().put(nfType, n);
			}
			else{
				throw new Exception("Non functional property for "+s.getName()+ " has not been specified.");
			}
		}
		
	}
	
	
	public void generateNonfunctionRandomly(NonfunctionalMetricType type, double midMid, double midStdDev, double stdDevMid, double stdDevDev){
		
		
		
		for(Service s:getAnnotationMap().keySet()){
			
			double mid = UtilityClass.randValueNormal(midMid, midStdDev);
			double stdDev = UtilityClass.randValueNormal(stdDevMid, stdDevDev);
			getAnnotationMap().get(s).getAnnotation().put(type,new NonfunctionalMetric(type,mid, stdDev));
		}
	}
	
	@Override
	public ServiceNonfunctionalAnnotationMap clone(){
		Map<Service, ServiceNonfunctionalAnnotation> annotationMap = new HashMap<Service, ServiceNonfunctionalAnnotation>();
		
		for(Service s: this.getAnnotationMap().keySet())
		{
			annotationMap.put(s, this.getAnnotationMap().get(s).clone());
		}
		
		return new ServiceNonfunctionalAnnotationMap(annotationMap);
	}
	
	
}

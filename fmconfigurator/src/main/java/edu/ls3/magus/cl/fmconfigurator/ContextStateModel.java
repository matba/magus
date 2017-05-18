package edu.ls3.magus.cl.fmconfigurator;

import java.util.HashMap;
import java.util.Map;

import edu.ls3.magus.cl.mashupconfigurator.nonfunctional.ServiceNonfunctionalAnnotationMap;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.cl.mashupconfigurator.service.ServiceCollection;

public class ContextStateModel {
	private Map<String, Boolean> serviceAvailabilty;
	private ServiceNonfunctionalAnnotationMap serviceNonfunctionalMap;
	
	public ContextStateModel(){
		setServiceAvailabilty(new HashMap<String, Boolean>());
	}
	
	public ContextStateModel(ServiceCollection serviceCollection){
		setServiceAvailabilty(new HashMap<String, Boolean>());
		for(Service s: serviceCollection.getServices())
			serviceAvailabilty.put(s.getURI(), true);
	}

	public ContextStateModel(ServiceCollection serviceCollection,ServiceNonfunctionalAnnotationMap serviceNonfunctionalMap){
		this(serviceCollection);
		setServiceNonfunctionalMap(serviceNonfunctionalMap);
	}
	
	

	public ContextStateModel(Map<String, Boolean> serviceAvailability, ServiceNonfunctionalAnnotationMap serviceNonfunctionalAnnotationMap) {
		setServiceAvailabilty(serviceAvailability);
		setServiceNonfunctionalMap(serviceNonfunctionalAnnotationMap);
	}

	public Map<String, Boolean> getServiceAvailabilty() {
		return serviceAvailabilty;
	}


	public void setServiceAvailabilty(Map<String, Boolean> serviceAvailabilty) {
		this.serviceAvailabilty = serviceAvailabilty;
	}

	public ServiceNonfunctionalAnnotationMap getServiceNonfunctionalMap() {
		return serviceNonfunctionalMap;
	}

	private void setServiceNonfunctionalMap(ServiceNonfunctionalAnnotationMap serviceNonfunctionalMap) {
		this.serviceNonfunctionalMap = serviceNonfunctionalMap;
	}
	
	@Override
	public ContextStateModel clone(){
		Map<String,Boolean> serviceAvailability = new HashMap<String, Boolean>();
		serviceAvailability.putAll(getServiceAvailabilty());
	
		return new ContextStateModel(serviceAvailability,  serviceNonfunctionalMap.clone());
	}
}

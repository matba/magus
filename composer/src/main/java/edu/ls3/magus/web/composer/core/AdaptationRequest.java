package edu.ls3.magus.web.composer.core;

import java.util.ArrayList;
import java.util.List;

import edu.ls3.magus.cl.contextmanager.context.ContextModel;
import edu.ls3.magus.cl.exceptions.UnsuccessfulMashupGeneration;
import edu.ls3.magus.cl.fmconfigurator.ContextStateModel;
import edu.ls3.magus.cl.fmconfigurator.DomainModels;
import edu.ls3.magus.cl.fmconfigurator.FeatureModelConfigurationMashupGeneration;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModel;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModelConfiguration;
import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.OperationNode;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.cl.mashupconfigurator.service.ServiceCollection;
import edu.ls3.magus.utility.Holder;

public class AdaptationRequest {
	private String ontologyXml;
	private String featureModelXml ;
	private String[] serviceAnnotationXmls;
	private String[] selectedFeatures;
	private DomainModels requestDomainModel;
	private FeatureModelConfiguration featureModelConfiguration;
	private ContextStateModel contextStateModel;
	
	
	private StringBuilder requestLog ;
	
	private String workflowJSON;
	private String bpelXML;
	
	private String failedWorkflow;
	private String failedBPELXml;
	private String failedServiceURI;
	
	private int adaptationType;
	private FeatureModelConfiguration alternateFeatureModelConfiguration;
	private String replacementServiceName;
	private String replacementServiceURI;
	private String[] usedServiceURIs;
	
	
	
	public AdaptationRequest(String ontologyXml,String featureModelXml,String[] serviceAnnotationXmls,String[] selectedFeatures,String[] availableServiceURIs, String failedServiceURI, String failedWorkflow, String failedBPELXml) throws Exception{
		setRequestLog(new StringBuilder());
		
		
//		long curTime = System.currentTimeMillis();
		this.setOntologyXml(ontologyXml);
		
		this.featureModelXml = featureModelXml;
		this.serviceAnnotationXmls = serviceAnnotationXmls;
		this.selectedFeatures = selectedFeatures;
		
		this.failedWorkflow = failedWorkflow;
		this.failedBPELXml = failedBPELXml;		
		this.failedServiceURI = failedServiceURI;
		
		
		
		this.requestDomainModel = new DomainModels();
		
		this.requestDomainModel.setContextModel(new ContextModel());
		this.requestDomainModel.setServiceCollection(new ServiceCollection());
		
		
		
		
		
		
		requestDomainModel.getContextModel().AddToContextModel(ontologyXml);
		
		
		
		
		
		for(String saXml:this.serviceAnnotationXmls){
			
			List<Service> newServices = Service.parseService(saXml, this.requestDomainModel.getContextModel());
			this.requestDomainModel.getServiceCollection().getServices().addAll(newServices);
			for(Service sv: newServices)
				contextStateModel.getServiceAvailabilty().put(sv.getURI(), false);
			
			
		}
		
		for(String st: availableServiceURIs){
			contextStateModel.getServiceAvailabilty().put(st, true);
		}
		
		
		
		
		
		requestDomainModel.setFeatureModel(FeatureModel.parse(this.featureModelXml, this.requestDomainModel.getFeatureModelAnnotation(),this.requestDomainModel.getContextModel()));
		
		
		
		featureModelConfiguration = new FeatureModelConfiguration(this.selectedFeatures, this.requestDomainModel.getFeatureModel());
		
		
		
		//long duration = System.currentTimeMillis()-curTime;
		
		
		
		setAdaptationType(-1);
	}
	
	public void Adapt() throws Exception{
		// Try service adaptation
		if(!contextStateModel.getServiceAvailabilty().get(failedServiceURI)){
			List<Service> altServices = requestDomainModel.getServiceCollection().FindEquivalentServices(requestDomainModel.getServiceCollection().getServiceByURI(failedServiceURI), contextStateModel.getServiceAvailabilty());
			if(altServices.size()!=0){
				this.setWorkflowJSON(failedWorkflow.replaceAll(failedServiceURI, altServices.get(0).getURI()));
				this.setWorkflowJSON(this.getWorkflowJSON().replaceAll(requestDomainModel.getServiceCollection().getServiceByURI(failedServiceURI).getName(), altServices.get(0).getName()));
				this.setBpelXML(failedBPELXml.replaceAll(failedServiceURI, altServices.get(0).getURI()));
				this.setBpelXML(getBpelXML().replaceAll(requestDomainModel.getServiceCollection().getServiceByURI(failedServiceURI).getName(), altServices.get(0).getName()));
				this.setReplacementServiceName(altServices.get(0).getName());
				this.setReplacementServiceURI(altServices.get(0).getURI());
				usedServiceURIs=null;
				this.setAdaptationType(0);
				getRequestLog().append("Found a replacement service: "+altServices.get(0).getURI()+System.lineSeparator()  );
				return;
			}
			getRequestLog().append("Failed to find a replacement service."+System.lineSeparator() );		
		}
		// Try replanning
		FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(this.requestDomainModel, this.featureModelConfiguration, contextStateModel);
		boolean alternatePlanExists = true;
		try{
			fmcmg.buildServiceMashup();
		}
		catch (UnsuccessfulMashupGeneration ex) {
			alternatePlanExists = false;
			getRequestLog().append("Failed to find a replacement process with replanning."+System.lineSeparator() );	

		}
		if(alternatePlanExists){
			this.setAdaptationType(1);
			List<OperationNode> optimizedGraph= fmcmg.getServiceMashupWorkflow();
			this.setWorkflowJSON(OperationNode.serializedToJSON(optimizedGraph));
			this.setBpelXML(fmcmg.getServiceMashupBPELProcess().serializeToXML(requestDomainModel.getFeatureModelAnnotation().findEntities(this.featureModelConfiguration)));
			
			List<String> usedServices = new ArrayList<String>();
			
			for(OperationNode on : optimizedGraph){
				String curServiceURI = on.getCalledService().getCalledService().getURI();
				if(!usedServices.contains(curServiceURI))
					usedServices.add(curServiceURI);
			}
			setUsedServiceURIs(usedServices.toArray(new String[0]));
			
			
			getRequestLog().append("Found a replacement service mashup process. "+System.lineSeparator() );
			return;
		}
		
		// Try feature adaptation
		Holder<Integer> noOfTriesHolder = new Holder<Integer>(0);
		this.setAlternateFeatureModelConfiguration(this.featureModelConfiguration.findAlternateConfiguration(requestDomainModel,contextStateModel,noOfTriesHolder));
		
		if(this.getAlternateFeatureModelConfiguration()==null)
		{
			getRequestLog().append("Failed to find a replacement feature model configuration. "+System.lineSeparator() );
			this.setAdaptationType(-2);
			return;
		}
		else{
			fmcmg = new FeatureModelConfigurationMashupGeneration(this.requestDomainModel, this.getAlternateFeatureModelConfiguration(), contextStateModel);
			this.setAdaptationType(2);
			fmcmg.buildServiceMashup();
			List<OperationNode> optimizedGraph= fmcmg.getServiceMashupWorkflow();
			this.setWorkflowJSON(OperationNode.serializedToJSON(optimizedGraph));
			this.setBpelXML(fmcmg.getServiceMashupBPELProcess().serializeToXML(requestDomainModel.getFeatureModelAnnotation().findEntities(this.featureModelConfiguration)));
			List<String> usedServices = new ArrayList<String>();
			
			for(OperationNode on : optimizedGraph){
				String curServiceURI = on.getCalledService().getCalledService().getURI();
				if(!usedServices.contains(curServiceURI))
					usedServices.add(curServiceURI);
			}
			setUsedServiceURIs(usedServices.toArray(new String[0]));
			getRequestLog().append("Found a replacement feaure model configuration. "+System.lineSeparator() );
		}
	}

	public StringBuilder getRequestLog() {
		return requestLog;
	}

	private void setRequestLog(StringBuilder requestLog) {
		this.requestLog = requestLog;
	}

	public String getWorkflowJSON() {
		return workflowJSON;
	}

	private void setWorkflowJSON(String workflowJSON) {
		this.workflowJSON = workflowJSON;
	}

	public String getBpelXML() {
		return bpelXML;
	}

	private void setBpelXML(String bpelXML) {
		this.bpelXML = bpelXML;
	}

	public int getAdaptationType() {
		return adaptationType;
	}

	private void setAdaptationType(int adaptationType) {
		this.adaptationType = adaptationType;
	}

	public FeatureModelConfiguration getAlternateFeatureModelConfiguration() {
		return alternateFeatureModelConfiguration;
	}

	private void setAlternateFeatureModelConfiguration(FeatureModelConfiguration alternateFeatureModelConfiguration) {
		this.alternateFeatureModelConfiguration = alternateFeatureModelConfiguration;
	}

	public String getReplacementServiceName() {
		return replacementServiceName;
	}

	private void setReplacementServiceName(String replacementServiceName) {
		this.replacementServiceName = replacementServiceName;
	}

	public String[] getUsedServiceURIs() {
		return usedServiceURIs;
	}

	public void setUsedServiceURIs(String[] usedServiceURIs) {
		this.usedServiceURIs = usedServiceURIs;
	}

	public String getReplacementServiceURI() {
		return replacementServiceURI;
	}

	public void setReplacementServiceURI(String replacementServiceURI) {
		this.replacementServiceURI = replacementServiceURI;
	}

	public String getOntologyXml() {
		return ontologyXml;
	}

	public void setOntologyXml(String ontologyXml) {
		this.ontologyXml = ontologyXml;
	}

	public ContextStateModel getContextStateModel() {
		return contextStateModel;
	}

	
}

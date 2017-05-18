package edu.ls3.magus.web.composer.services;

public class AdaptMashupRequest {
	private String ontologyXml;
	private String featureModelXml ;
	private String[] serviceAnnotationXmls;
	private String[] selectedFeatures;
	private String[] availableServiceURIs;
	private String failedWorkflow;
	private String failedBPELXml;
	private String failedServiceURI;
	
	
	public String getOntologyXml() {
		return ontologyXml;
	}
	public void setOntologyXml(String ontologyXml) {
		this.ontologyXml = ontologyXml;
	}
	public String getFeatureModelXml() {
		return featureModelXml;
	}
	public void setFeatureModelXml(String featureModelXml) {
		this.featureModelXml = featureModelXml;
	}
	public String[] getServiceAnnotationXmls() {
		return serviceAnnotationXmls;
	}
	public void setServiceAnnotationXmls(String[] serviceAnnotationXmls) {
		this.serviceAnnotationXmls = serviceAnnotationXmls;
	}
	public String[] getSelectedFeatures() {
		return selectedFeatures;
	}
	public void setSelectedFeatures(String[] selectedFeatures) {
		this.selectedFeatures = selectedFeatures;
	}
	public String[] getAvailableServiceURIs() {
		return availableServiceURIs;
	}
	public void setAvailableServiceURIs(String[] availableServiceURIs) {
		this.availableServiceURIs = availableServiceURIs;
	}
	public String getFailedWorkflow() {
		return failedWorkflow;
	}
	public void setFailedWorkflow(String failedWorkflow) {
		this.failedWorkflow = failedWorkflow;
	}
	public String getFailedBPELXml() {
		return failedBPELXml;
	}
	public void setFailedBPELXml(String failedBPELXml) {
		this.failedBPELXml = failedBPELXml;
	}
	public String getFailedServiceURI() {
		return failedServiceURI;
	}
	public void setFailedServiceURI(String failedServiceURI) {
		this.failedServiceURI = failedServiceURI;
	}
	
	
	

}

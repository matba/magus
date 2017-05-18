package edu.ls3.magus.web.composer.services;

public class GenerateMashupRequest {
	private String ontologyXml;
	private String featureModelXml ;
	private String[] serviceAnnotationXmls;
	private String[] selectedFeatures;
	private String[] availableServiceURIs;
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
	
	
	

}

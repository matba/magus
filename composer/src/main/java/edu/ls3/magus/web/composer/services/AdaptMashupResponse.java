package edu.ls3.magus.web.composer.services;

public class AdaptMashupResponse {
	private String status;
	private String workflowJSON;
	private String bpelXML;
	private String replacementServiceName;
	private String replacementServiceURI;
	private String[] alternateFeatureModel;
	private int adaptationType;
	private String[] usedServiceURIs;
	private float alternateFMDistance;
	
	public AdaptMashupResponse(){
		status="";
		workflowJSON="";
		bpelXML="";
		alternateFeatureModel =null;
		replacementServiceName ="";
		setAdaptationType(-1);
		usedServiceURIs =null;
		setAlternateFMDistance(0);
	}
	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	public String getWorkflowJSON() {
		return workflowJSON;
	}


	public void setWorkflowJSON(String workflowJSON) {
		this.workflowJSON = workflowJSON;
	}


	public String getBpelXML() {
		return bpelXML;
	}


	public void setBpelXML(String bpelXML) {
		this.bpelXML = bpelXML;
	}


	public String[] getAlternateFeatureModel() {
		return alternateFeatureModel;
	}


	public void setAlternateFeatureModel(String[] alternateFeatureModel) {
		this.alternateFeatureModel = alternateFeatureModel;
	}


	public String getReplacementServiceName() {
		return replacementServiceName;
	}


	public void setReplacementServiceName(String replacementServiceName) {
		this.replacementServiceName = replacementServiceName;
	}


	public int getAdaptationType() {
		return adaptationType;
	}


	public void setAdaptationType(int adaptationType) {
		this.adaptationType = adaptationType;
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


	public float getAlternateFMDistance() {
		return alternateFMDistance;
	}


	public void setAlternateFMDistance(float alternateFMDistance) {
		this.alternateFMDistance = alternateFMDistance;
	}
	
}

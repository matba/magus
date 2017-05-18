package edu.ls3.magus.web.composer.services;

public class GenerateMashupResponse {
	private String status;
	private String workflowJSON;
	private String bpelXML;
	private String[] usedServiceURIs;
	
	public GenerateMashupResponse(){
		status="";
		workflowJSON="";
		bpelXML="";
		usedServiceURIs =null;
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


	public String[] getUsedServiceURIs() {
		return usedServiceURIs;
	}


	public void setUsedServiceURIs(String[] usedServiceURIs) {
		this.usedServiceURIs = usedServiceURIs;
	}
	
}

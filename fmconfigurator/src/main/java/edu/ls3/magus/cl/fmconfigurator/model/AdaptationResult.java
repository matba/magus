package edu.ls3.magus.cl.fmconfigurator.model;

import edu.ls3.magus.cl.mashupconfigurator.bpelgraph.FlowComponentNode;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;

public class AdaptationResult {
	public static final int SERVICE_FAILURE= 100;
	public static final int SERVICE_ADAPTATION = 1;
	public static final int WORKFLOW_ADAPTATION = 2;
	public static final int FEATURE_ADAPTATION = 3;
	
	private int adaptationType;
	private FeatureModelConfiguration alternateFeatureModelConfiguration;
	private FlowComponentNode alternateFlow;
	private Service replacementService;
	
	private long serviceAdaptationTime ;
	private long workflowAdaptationTime ;
	private long featureAdaptationTime ;
	
	private int noOfTries;
	
	public AdaptationResult()
	{
		setAdaptationType(-1);
		setAlternateFeatureModelConfiguration(null);
		setAlternateFlow(null);
		setReplacementService(null);
		
		setServiceAdaptationTime(0);
		setWorkflowAdaptationTime(0);
		setFeatureAdaptationTime(0);
		setNoOfTries(0);
	}
	
	
	public int getAdaptationType() {
		return adaptationType;
	}
	public void setAdaptationType(int adaptationType) {
		this.adaptationType = adaptationType;
	}
	
	public FlowComponentNode getAlternateFlow() {
		return alternateFlow;
	}
	public void setAlternateFlow(FlowComponentNode alternateFlow) {
		this.alternateFlow = alternateFlow;
	}
	public long getServiceAdaptationTime() {
		return serviceAdaptationTime;
	}
	public void setServiceAdaptationTime(long serviceAdaptationTime) {
		this.serviceAdaptationTime = serviceAdaptationTime;
	}
	public long getWorkflowAdaptationTime() {
		return workflowAdaptationTime;
	}
	public void setWorkflowAdaptationTime(long workflowAdaptationTime) {
		this.workflowAdaptationTime = workflowAdaptationTime;
	}
	public long getFeatureAdaptationTime() {
		return featureAdaptationTime;
	}
	public void setFeatureAdaptationTime(long featureAdaptationTime) {
		this.featureAdaptationTime = featureAdaptationTime;
	}
	public Service getReplacementService() {
		return replacementService;
	}
	public void setReplacementService(Service replacementService) {
		this.replacementService = replacementService;
	}
	public FeatureModelConfiguration getAlternateFeatureModelConfiguration() {
		return alternateFeatureModelConfiguration;
	}
	public void setAlternateFeatureModelConfiguration(FeatureModelConfiguration alternateFeatureModelConfiguration) {
		this.alternateFeatureModelConfiguration = alternateFeatureModelConfiguration;
	}


	public int getNoOfTries() {
		return noOfTries;
	}


	public void setNoOfTries(int noOfTries) {
		this.noOfTries = noOfTries;
	}


}

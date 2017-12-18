package edu.ls3.magus.web.composer.services;

public class UpdateContextStateModelResponse {
	public int statusCode;
	public String statusMessage;
	public boolean functionalPropertiesSatisfcation;
	public NonfunctionalPropertiesSatisfaction[] nonfunctionaPropetiesSatisfaction;
	public boolean adaptationIsRecommended;

}

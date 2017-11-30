package edu.ls3.magus.web.composer.services;

public class MashupAdaptationResponse {
	public int statusCode;
	public String statueMessage;
	public String bpelCodeXml;
	public String[] providedFeaturesUuidAfterAdaptation;
	public NonfunctionalProperty[] predictedNonfunctionalAfterAdaptation;
}

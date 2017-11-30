package edu.ls3.magus.web.composer.services;

public class MashupGenerationRequest {
	private String mashupFamilyURI;
	private String[] selectedFeaturesUuids;
	public String getMashupFamilyURI() {
		return mashupFamilyURI;
	}
	public void setMashupFamilyURI(String mashupFamilyURI) {
		this.mashupFamilyURI = mashupFamilyURI;
	}
	public String[] getSelectedFeaturesUuids() {
		return selectedFeaturesUuids;
	}
	public void setSelectedFeaturesURI(String[] selectedFeaturesURI) {
		this.selectedFeaturesUuids = selectedFeaturesURI;
	}
}

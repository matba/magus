package edu.ls3.magus.cl.fmconfigurator.model;

import java.util.*;

public class AtomicSet {
	private Set<Feature> featureList;
	private Set<Feature> mainFeatureList;
	private boolean isSingleSelectionStateFeature; // These atomic sets are always selected or unselected in feature model configurations
	
	
	public AtomicSet(){
		featureList = new HashSet<Feature>();
		mainFeatureList = new HashSet<Feature>();
		isSingleSelectionStateFeature=false;
	}
	
	// Getters and Setters
	public Set<Feature> getFeatureList() {
		return featureList;
	}

	@SuppressWarnings("unused")
	private void setFeatureList(Set<Feature> featureList) {
		this.featureList = featureList;
	}
	
	@Override
	public String toString() {
		
		return getFeatureList().toString();
	}

	public Set<Feature> getMainFeatureList() {
		return mainFeatureList;
	}



	public boolean isSingleSelectionStateFeature() {
		return isSingleSelectionStateFeature;
	}

	public void setSingleSelectionStateFeature(boolean isSingleSelectionStateFeature) {
		this.isSingleSelectionStateFeature = isSingleSelectionStateFeature;
	}
	
}

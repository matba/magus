package edu.ls3.magus.cl.fmconfigurator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.ls3.magus.cl.fmconfigurator.FeatureSelectionStatus;
import edu.ls3.magus.utility.UtilityClass;

public class DecisionPoint {
	private Feature feature;
	private List<List<Feature>> processedSelection;
	private List<List<Feature>> possibleSelection;
	private List<Feature> currentSelection;
	private List<DecisionPoint> children;
	
	public DecisionPoint(Feature feature, Boolean random){
		setFeature(feature);
		setPossibleSelection(new ArrayList<List<Feature>>());
		setProcessedSelection(new ArrayList<List<Feature>>());
		
		if(!feature.getChildren().isEmpty()){
		
			if(feature.isAlternative()){
				for(Feature childFeature: feature.getChildren()){
					List<Feature> cfl = new ArrayList<Feature>();
					cfl.add(childFeature);
					getPossibleSelection().add(cfl);
				}
			}
			if(feature.isOrGroup()){
				List<List<Feature>> ps = UtilityClass.getPowerSet(feature.getChildren());
				
				for(List<Feature> cc: ps)
					if(cc.size()==0)
					{
						ps.remove(cc);
						break;
					}
					
				
				for(List<Feature> cfl : ps){
					getPossibleSelection().add(cfl);
				}
			}
			if(!feature.isAlternative()&& !feature.isOrGroup()){
				List<Feature> allOptionalFeature = new ArrayList<Feature>();
				for(Feature childFeature: feature.getChildren())
					if(childFeature.isOptional()){
					
					
						allOptionalFeature.add(childFeature);
					
				}
				List<List<Feature>> ps = UtilityClass.getPowerSet(allOptionalFeature);
				for(List<Feature> cfl : ps){
					getPossibleSelection().add(cfl);
				}
			}
		
		}
		
		if(random)
		{
			Collections.shuffle(getPossibleSelection());
		}
		
		setChildren(new ArrayList<DecisionPoint>());
	}
	
	
	public DecisionPoint(Feature feature, Map<Feature,FeatureSelectionStatus> featureSelectionStatus) throws Exception{
		setFeature(feature);
		setPossibleSelection(new ArrayList<List<Feature>>());
		setProcessedSelection(new ArrayList<List<Feature>>());
		
		
		
		
		if(!feature.getChildren().isEmpty()){
		
			List<Feature> alreadySelectedChildren = new ArrayList<Feature>();
			List<Feature> alreadyUnselectedChildren = new ArrayList<Feature>();
			
			for(Feature childFeature: feature.getChildren()){
				if(featureSelectionStatus.get(childFeature)==FeatureSelectionStatus.Selected){
					alreadySelectedChildren.add(childFeature);
					
				}
				if(featureSelectionStatus.get(childFeature)==FeatureSelectionStatus.Unselected){
					alreadyUnselectedChildren.add(childFeature);
					
				}
			}
			
			
			if(feature.isAlternative()){
				
				
				if((alreadySelectedChildren.size()>1)||(alreadyUnselectedChildren.size()==feature.getChildren().size()))
					throw new Exception("Invalid combination of features are selected.");
				
				if(alreadySelectedChildren.size()==1){
					List<Feature> cfl = new ArrayList<Feature>();
					cfl.add(alreadySelectedChildren.get(0));
					getPossibleSelection().add(cfl);
					
				}
				else{
					for(Feature childFeature: feature.getChildren()){
						if(alreadyUnselectedChildren.contains(childFeature))
							continue;
						List<Feature> cfl = new ArrayList<Feature>();
						cfl.add(childFeature);
						getPossibleSelection().add(cfl);
					}
				}
				
				
				
			}
			if(feature.isOrGroup()){
				
				
				
				if((alreadyUnselectedChildren.size()==feature.getChildren().size()))
					throw new Exception("Invalid combination of features are selected.");
				
				List<Feature> selectableChildren = new ArrayList<Feature>();
				
				selectableChildren.addAll(feature.getChildren());
				
				selectableChildren.removeAll(alreadySelectedChildren);
				selectableChildren.removeAll(alreadyUnselectedChildren);
				
				List<List<Feature>> ps = UtilityClass.getPowerSet(selectableChildren);
				
				
				if(alreadySelectedChildren.size()>0){
					for(List<Feature> cc: ps)
						cc.addAll(alreadySelectedChildren);
				}
				else{
					for(List<Feature> cc: ps)
						if(cc.size()==0)
						{
							ps.remove(cc);
							break;
						}
					
				}
				
				for(List<Feature> cfl : ps){
					getPossibleSelection().add(cfl);
				}
			}
			if(!feature.isGroupingFeature()){
				
							
				List<Feature> allOptionalFeature = new ArrayList<Feature>();
				
				
				
				for(Feature childFeature: feature.getChildren())
					if(childFeature.isOptional()&& !alreadySelectedChildren.contains(childFeature)&& !alreadyUnselectedChildren.contains(childFeature)){
					
					
						allOptionalFeature.add(childFeature);
					
				}
				List<List<Feature>> ps = UtilityClass.getPowerSet(allOptionalFeature);
				for(List<Feature> cfl : ps){
					for(Feature f: alreadySelectedChildren)
						if(f.isOptional())
							cfl.add(f);
					getPossibleSelection().add(cfl);
				}
			}
		
		}
		
		Collections.shuffle(getPossibleSelection());
		
		setChildren(new ArrayList<DecisionPoint>());
	}
	
	public Feature getFeature() {
		return feature;
	}
	private void setFeature(Feature feature) {
		this.feature = feature;
	}
	public List<List<Feature>> getProcessedSelection() {
		return processedSelection;
	}
	public void setProcessedSelection(List<List<Feature>> processedSelection) {
		this.processedSelection = processedSelection;
	}
	public List<List<Feature>> getPossibleSelection() {
		return possibleSelection;
	}
	public void setPossibleSelection(List<List<Feature>> possibleSelection) {
		this.possibleSelection = possibleSelection;
	}
	public List<Feature> getCurrentSelection() {
		return currentSelection;
	}
	public void setCurrentSelection(List<Feature> currentSelection) {
		this.currentSelection = currentSelection;
	}


	public List<DecisionPoint> getChildren() {
		return children;
	}


	public void setChildren(List<DecisionPoint> children) {
		this.children = children;
	}


	public List<Feature> getCurConfiguration() {
		List<Feature> result = new ArrayList<Feature>();
		result.add(getFeature());
		for(DecisionPoint dp : getChildren())
			result.addAll(dp.getCurConfiguration());
		
		return result;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("'Feature Name':");
		sb.append(getFeature().getName());
		
		sb.append(",'Children':");
		sb.append(getFeature().getChildren().toString());

		
		sb.append(",'Current Selection':");
		if(currentSelection==null)
			sb.append("null");
		else
			sb.append(currentSelection.toString());
		
		sb.append(",'PossibleSelection':");
		sb.append(possibleSelection.toString());
		
		sb.append("}");
		
		return sb.toString();
	}
	
	
	

	public String toString2(String tab){
		StringBuilder sb = new StringBuilder();
		
		sb.append(tab+"{");
		sb.append("'Feature Name':");
		sb.append(getFeature().getName());
		
		sb.append(",'Children':[ ");
		sb.append(System.lineSeparator());
		
		
		String sep ="";
		
		for(DecisionPoint  dp : getChildren()){
			sb.append(sep);
			sb.append(dp.toString2("\t"+ tab));
			
			sb.append(System.lineSeparator());
		}
		
		
		
		sb.append(tab+"] }");
		
		return sb.toString();
	}
}

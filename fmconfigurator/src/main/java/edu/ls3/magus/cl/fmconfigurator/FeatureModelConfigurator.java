package edu.ls3.magus.cl.fmconfigurator;

import java.util.*;

import edu.ls3.magus.cl.exceptions.PreSetFeatureSelection;
import edu.ls3.magus.cl.fmconfigurator.model.AtomicSet;
import edu.ls3.magus.cl.fmconfigurator.model.DecisionPoint;
import edu.ls3.magus.cl.fmconfigurator.model.Feature;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModel;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModelConfiguration;

public class FeatureModelConfigurator {
	private FeatureModel featureModel;
	private Map<Feature,FeatureSelectionStatus> featureSelectionStatus;
	
	
	public FeatureModelConfigurator(FeatureModel featureModel) {
		this.featureModel = featureModel;
		featureSelectionStatus = new HashMap<Feature, FeatureSelectionStatus>();
		for(Feature f : featureModel.getFeatureList())
			featureSelectionStatus.put(f,FeatureSelectionStatus.NotSet);
		
		
	}
	
	
	public void setFeatureSelectionStatus(Feature f, FeatureSelectionStatus status) throws PreSetFeatureSelection{
		
		if(featureSelectionStatus.get(f).equals(status))
			return;
		
		if(!featureSelectionStatus.get(f).equals(FeatureSelectionStatus.NotSet))
			throw new PreSetFeatureSelection();
		
		featureSelectionStatus.put(f, status);
		
		if(!f.equals(getFeatureModel().getRootFeature()))
		{
			if(status.equals(FeatureSelectionStatus.Selected))
			{
				Feature pf = getFeatureModel().getFeatureParent(f);
				if(pf!=null)
					setFeatureSelectionStatus( pf, FeatureSelectionStatus.Selected);
			}
			
			
			
		}
		
	}
	
	
	public void setFeatureSetSelectionStatus(AtomicSet as, FeatureSelectionStatus status) throws PreSetFeatureSelection{
		for(Feature f: as.getFeatureList())
			if(status.equals(FeatureSelectionStatus.Selected)||(as.getMainFeatureList().contains(f)))
				setFeatureSelectionStatus(f, status);
	}
	
	
	public List<FeatureModelConfiguration> configureRestOfFeauresRandomly(int threshold) throws Exception {
		List<FeatureModelConfiguration> result = new ArrayList<FeatureModelConfiguration>();
		
		List<DecisionPoint> decisionPoints = new ArrayList<DecisionPoint>();
		int dpPointer = -1;
		Queue<DecisionPoint> processingQueue = new LinkedList<DecisionPoint>();
		Boolean allDone = false;
		
		List<Feature> curConfiguration = new ArrayList<Feature>();
		
		DecisionPoint rootDP = new DecisionPoint(getFeatureModel().getRootFeature(),featureSelectionStatus);
		processingQueue.add(rootDP);
		
		//fill decision points
		while(!processingQueue.isEmpty()){
			DecisionPoint newDP = processingQueue.remove();
			Feature curFeature = newDP.getFeature();
			
			
			if(!curFeature.isAlternative()&& !curFeature.isOrGroup()){
				for(Feature childFeature: curFeature.getChildren())
					if(!childFeature.isOptional()){
						DecisionPoint nnDP = new DecisionPoint(childFeature,featureSelectionStatus);
						processingQueue.add(nnDP);
						newDP.getChildren().add(nnDP);
						
					}
					
			}
			
			if(!newDP.getPossibleSelection().isEmpty()){
				List<Feature> curSelection = newDP.getPossibleSelection().get(0);
				newDP.setCurrentSelection(curSelection);
				newDP.getPossibleSelection().remove(0);
				for(Feature childFeature: curSelection){
					DecisionPoint nnDP = new DecisionPoint(childFeature,featureSelectionStatus);
					processingQueue.add(nnDP);
					newDP.getChildren().add(nnDP);
				}
				
			}
			decisionPoints.add(newDP);
			dpPointer++;
		}
		
		//fill first configuration
		curConfiguration = rootDP.getCurConfiguration();
		FeatureModelConfiguration nfmc  = new FeatureModelConfiguration(curConfiguration);
		if(nfmc.hasValidIntegrityConstraints(getFeatureModel())){
			result.add(nfmc);
			
		}
		
		//System.out.println(nfmc);
		
		
		
		
		//update decision points
		while(!allDone){
			curConfiguration = new ArrayList<Feature>();
			DecisionPoint cDP = decisionPoints.get(dpPointer);
			
			while((dpPointer>=0) && decisionPoints.get(dpPointer).getPossibleSelection().isEmpty()){
				
				
				dpPointer--;
			}
			
			
			if((dpPointer<0))
			{
				allDone = true;
				continue;
			}
			cDP = decisionPoints.get(dpPointer);
			
			for(Feature ft : cDP.getCurrentSelection()){
				DecisionPoint featureDP = null;
				for(DecisionPoint dp: decisionPoints )
					if(dp.getFeature().equals(ft)){
						featureDP= dp;
						break;
					}
								
				getFeatureModel().removeFeature(decisionPoints,featureDP);
			}
			
			if(!cDP.getFeature().isAlternative()&& !cDP.getFeature().isOrGroup()){
				for(Feature ft : cDP.getFeature().getChildren()){
					if(!ft.isOptional())
					{
						DecisionPoint featureDP = null;
						for(DecisionPoint dp: decisionPoints )
							if(dp.getFeature().equals(ft)){
								featureDP= dp;
								break;
							}
										
						getFeatureModel().removeFeature(decisionPoints,featureDP);
					}
				}
			}	
			
			List<Feature> curSelection = cDP.getPossibleSelection().get(0);
			cDP.getProcessedSelection().add(cDP.getCurrentSelection());
			cDP.setCurrentSelection(curSelection);
			cDP.getPossibleSelection().remove(0);
			
			cDP.setChildren(new ArrayList<DecisionPoint>());
			
			Feature cf = cDP.getFeature();
			if(!cf.isAlternative()&& !cf.isOrGroup()){
				for(Feature childFeature: cf.getChildren())
					if(!childFeature.isOptional()){
						DecisionPoint nnDP = new DecisionPoint(childFeature,featureSelectionStatus);
						processingQueue.add(nnDP);
						cDP.getChildren().add(nnDP);
					}
					
			}
			
			for(Feature childFeature: curSelection){
				DecisionPoint nnDP = new DecisionPoint(childFeature,featureSelectionStatus);
				processingQueue.add(nnDP);
				cDP.getChildren().add(nnDP);
			}
			
			while(dpPointer+1< decisionPoints.size()){
				DecisionPoint curDP =  decisionPoints.get(dpPointer+1);
				for(DecisionPoint featureDP: curDP.getChildren())
					getFeatureModel().removeFeature(decisionPoints, featureDP);
				DecisionPoint nnDP = new DecisionPoint(curDP.getFeature(),featureSelectionStatus);
				curDP.setProcessedSelection(nnDP.getProcessedSelection());
				curDP.setCurrentSelection(nnDP.getCurrentSelection());
				curDP.setPossibleSelection(nnDP.getPossibleSelection());
				curDP.setChildren(nnDP.getChildren());
			
				processingQueue.add(curDP);
				decisionPoints.remove(dpPointer+1);
			}
			
			
//			System.out.println();
			
//			System.out.println(rootDP.toString2(""));
			
			
			
			while(!processingQueue.isEmpty()){
				DecisionPoint newDP = processingQueue.remove();
				Feature curFeature = newDP.getFeature();
				
				if(!curFeature.isAlternative()&& !curFeature.isOrGroup()){
					for(Feature childFeature: curFeature.getChildren())
						if(!childFeature.isOptional()){
							DecisionPoint nnDP = new DecisionPoint(childFeature,featureSelectionStatus);
							processingQueue.add(nnDP);
							newDP.getChildren().add(nnDP);
						}
						
				}
				
				if(!newDP.getPossibleSelection().isEmpty()){
					curSelection = newDP.getPossibleSelection().get(0);
					newDP.setCurrentSelection(curSelection);
					newDP.getPossibleSelection().remove(0);
					for(Feature childFeature: curSelection){
						DecisionPoint nnDP = new DecisionPoint(childFeature,featureSelectionStatus);
						processingQueue.add(nnDP);
						newDP.getChildren().add(nnDP);
					}
				}
				decisionPoints.add(newDP);
				dpPointer++;
//				System.out.println();
//				
//				System.out.println(rootDP.toString2(""));
				
			}
			
			//fill first configuration
			curConfiguration = rootDP.getCurConfiguration();
			nfmc  = new FeatureModelConfiguration(curConfiguration);
//			System.out.println();
//			System.out.println();
//			System.out.println();
//			System.out.println(rootDP.toString2(""));
//			System.out.println();
//			System.out.println(decisionPoints.toString());
//			System.out.println();
//			System.out.println(nfmc);
//			System.out.println(result.size());
			
//			if(result.contains(nfmc))
//				throw new Exception("A configuration found that already that has been already found");
			
			
			//Filter those that do not respect integrity constraints
			if(nfmc.hasValidIntegrityConstraints(getFeatureModel())){
				result.add(nfmc);
				
			}
			
			//result.add(nfmc);
			//System.out.println(nfmc);
			if((threshold>-1)&&(result.size()>threshold)){
				//System.out.println("Threshhold activated!");
				break;
			}
			
			
			
		}
		
		//Filter those that do not respect integrity constraints
//		List<FeatureModelConfiguration> filteredResult = new ArrayList<FeatureModelConfiguration>();
//		for(FeatureModelConfiguration fmc : result)
//				if(fmc.hasValidIntegrityConstraints(this)){
//					filteredResult.add(fmc);
//					
//				}
		
		
		
		return result;
	}


	public FeatureModel getFeatureModel(){
		return featureModel;
	}


	public List<FeatureModelConfiguration> generateRandomConfigurations(int threshold,
			List<FeatureModelConfiguration> exclusions) throws Exception {
		
		List<FeatureModelConfiguration> fmcList = getFeatureModel().getAllValidConfiguration(exclusions.size()+threshold);
		
		fmcList.removeAll(exclusions);
		
		if(fmcList.size()>threshold)
			fmcList = fmcList.subList(0, threshold);
		
		return fmcList;
	}


	
}

package edu.ls3.magus.cl.fmconfigurator.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ls3.magus.cl.contextmanager.basic.Condition;
import edu.ls3.magus.cl.contextmanager.basic.Instance;
import edu.ls3.magus.cl.contextmanager.basic.InstanceType;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstance;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstanceS;
import edu.ls3.magus.cl.contextmanager.basic.StateFactType;
import edu.ls3.magus.cl.contextmanager.context.ContextModel;
import edu.ls3.magus.utility.UtilityClass;


public class FeatureAnnotationSet implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5223706858612417484L;
	private HashMap<Feature, FeatureAnnotation> annotationMap;
	private String baseURI ;
	
	public FeatureAnnotationSet(){
		annotationMap = new HashMap<Feature, FeatureAnnotation>();
		setBaseURI("");
	}
	
	public HashMap<Feature, FeatureAnnotation> getAnnotationMap() {
		return annotationMap;
	}

	@SuppressWarnings("unused")
	private void setAnnotationMap(HashMap<Feature, FeatureAnnotation> annotationMap) {
		this.annotationMap = annotationMap;
	}
	
	public static FeatureAnnotationSet readAnnotationSet(String address) throws IOException, ClassNotFoundException{
		 
         FileInputStream fileIn = new FileInputStream(address);
         ObjectInputStream in = new ObjectInputStream(fileIn);
         FeatureAnnotationSet e = (FeatureAnnotationSet) in.readObject();
         in.close();
         fileIn.close();
	     return e;
	}
	
	public List<Instance> findEntities(FeatureModelConfiguration fmc, ContextModel cm){
		
		List<Instance> ins = new ArrayList<Instance>();
		
		for(Feature f : fmc.getFeatureList()){
			List<Instance> enns = getAnnotationMap().get(f).getEntities().getAsList();
			ins.addAll(enns);
		}
		ins.addAll(cm.getInstances().getAsList());
		
		
		return ins;
	}
	
	public List<Instance> findEntities(FeatureModelConfiguration fmc){
		
		List<Instance> ins = new ArrayList<Instance>();
		
		for(Feature f : fmc.getFeatureList()){
			List<Instance> enns = getAnnotationMap().get(f).getEntities().getAsList();
			ins.addAll(enns);
		}
		
		
		
		return ins;
	}
	
	public Condition findPrecondition(FeatureModelConfiguration fmc){
		Condition result = null;
		List<StateFactInstanceS> sfis = new ArrayList<StateFactInstanceS>();
		
		for(Feature f : fmc.getFeatureList()){
			List<StateFactInstanceS> sfitemp = getAnnotationMap().get(f).getPreconditions().getFacts();
			for(StateFactInstanceS s: sfitemp){
				if(!sfis.contains(s))
					sfis.add(s );
			}
		}
		
		result = new Condition(sfis);
		return result;
	}
	
	public Map<StateFactInstanceS,List<Feature>> findAllAnnotatedPrecondition(FeatureModel fm){
		
		List<StateFactInstanceS> sfis = new ArrayList<StateFactInstanceS>();
		Map<StateFactInstanceS,List<Feature>> result = new HashMap<StateFactInstanceS, List<Feature>>();
		
		for(Feature f : fm.getFeatureList()){
			List<StateFactInstanceS> sfitemp = getAnnotationMap().get(f).getPreconditions().getFacts();
			for(StateFactInstanceS s: sfitemp){
				if(!sfis.contains(s)){
					sfis.add(s );
					List<Feature> newl = new ArrayList<Feature>();
					result.put(s, newl);
				}
				result.get(s).add(f);
				
				
			}
		}
		
		
		return result;
	}
	public Condition findEffect(FeatureModelConfiguration fmc){
		Condition result = null;
		List<StateFactInstanceS> sfis = new ArrayList<StateFactInstanceS>();
		
		
		for(Feature f : fmc.getFeatureList()){
			List<StateFactInstanceS> sfitemp = getAnnotationMap().get(f).getEffects().getFacts();
			for(StateFactInstanceS s: sfitemp){
				if(!sfis.contains(s))
					sfis.add(s);
			}
		}
		
		result = new Condition(sfis);
		return result;
	}
	
	public static FeatureAnnotationSet createRandomAnnotationSet(ContextModel cm, FeatureModel fm, String baseURI, float avgEnt, float stdevEnt, float avgPrc, float stdevPrc, float avgEff, float stdevEff) throws Exception{
		FeatureAnnotationSet fma = new FeatureAnnotationSet();
		fma.setBaseURI(baseURI);
		
		for(Feature f: fm.getFeatureList()){
			fma.getAnnotationMap().put(f, new FeatureAnnotation(f));
		}
		
		List<String> names = new ArrayList<String>();
		
		for(Feature f:fm.getFeatureList()){
			int noOfEntities = (int) Math.round(UtilityClass.randValueNormalP(avgEnt, stdevEnt));
			List<InstanceType> types=  cm.getInstanceTypes().getTypesAsList();
			for(int cntr=0; cntr<noOfEntities; cntr++){
				InstanceType curIT = types.get(UtilityClass.randInt(0, types.size()-1));
				int nameCntr = 1;
				while(names.contains("v"+curIT.getTypeName()+nameCntr))
					nameCntr++;
				
				String newInstanceName  = "v"+curIT.getTypeName()+nameCntr;
				names.add(newInstanceName);
				Instance newInstance = new Instance(curIT,newInstanceName , new URI(baseURI+"#"+newInstanceName));
				fma.getAnnotationMap().get(f).getEntities().add(newInstance);
				
				
			}
		}
		
		
		List<StateFactType> allFactTypes = new ArrayList<StateFactType>();
		allFactTypes.addAll(cm.getFactTypes().getStateFactTypeList());
		
		Collections.shuffle(allFactTypes);
		
		float pfRatio = avgPrc/avgEff;
		
		List<StateFactType> preconditonFactTypes = new ArrayList<StateFactType>();
		preconditonFactTypes.addAll(allFactTypes.subList(0, (int) (pfRatio*allFactTypes.size())));
		
		List<StateFactType> effectFactTypes = new ArrayList<StateFactType>();
		effectFactTypes.addAll(allFactTypes.subList((int) (pfRatio*allFactTypes.size()),allFactTypes.size()));
		
		
		
		
		for(Feature f:fm.getFeatureList()){
			List<Feature> ancestors = fm.getFeatureAnncestors(f);
			List<Instance> allAncestorsEntities = new ArrayList<Instance>(); 
			
			allAncestorsEntities.addAll(fma.getAnnotationMap().get(f).getEntities().getAsList());
			
			for(Feature af: ancestors){
				allAncestorsEntities.addAll(fma.getAnnotationMap().get(af).getEntities().getAsList());
			}
			
			List<StateFactType> possiblePreconditions = Condition.FilterByEntities(preconditonFactTypes, allAncestorsEntities);
			
			Collections.shuffle(possiblePreconditions); 
			
			List<StateFactType> selectedPrecondition = new ArrayList<StateFactType>();
			int noOfPreconditions =  (int) Math.round(UtilityClass.randValueNormalP(avgPrc, stdevPrc));
			selectedPrecondition.addAll(possiblePreconditions.subList(0, Math.min(possiblePreconditions.size(),noOfPreconditions)));
			
			for(StateFactType curSft : selectedPrecondition){
				Instance[] newPrecondtionParams = new Instance[curSft.getParams().length];
				for(int cntr=0; cntr<newPrecondtionParams.length; cntr++){
					List<Instance> compatibleInstances = Instance.Filter(allAncestorsEntities,curSft.getParams()[cntr]);
					
					if(compatibleInstances.size()==0){
						throw new Exception("A statefacttype has been selected that does not have any compatible entity for preconditon");
					}
					
					if(compatibleInstances.size()==1)
						newPrecondtionParams[cntr]=compatibleInstances.get(0);
					else
						newPrecondtionParams[cntr] = compatibleInstances.get(UtilityClass.randInt(0, compatibleInstances.size()-1));
				}
				
				
				StateFactInstance newPrecondition = new  StateFactInstance(curSft, newPrecondtionParams);
				
				fma.getAnnotationMap().get(f).getPreconditions().getFacts().add(new StateFactInstanceS(newPrecondition, false));
				
			}
			
			List<StateFactType> possibleEffects = Condition.FilterByEntities(effectFactTypes, allAncestorsEntities);
			
			Collections.shuffle(possibleEffects); 
			
			List<StateFactType> selectedEffects = new ArrayList<StateFactType>();
			
			int noOfEffects =   (int) Math.round(UtilityClass.randValueNormalP(avgEff, stdevEff));
			selectedEffects.addAll(possibleEffects.subList(0, Math.min(possibleEffects.size(),noOfEffects)));
			
			for(StateFactType curSft : selectedEffects){
				Instance[] newEffcondtionParams = new Instance[curSft.getParams().length];
				for(int cntr=0; cntr<newEffcondtionParams.length; cntr++){
					List<Instance> compatibleInstances = Instance.Filter(allAncestorsEntities,curSft.getParams()[cntr]);
					
					if(compatibleInstances.size()==0){
						throw new Exception("A statefacttype has been selected that does not have any compatible entity for effect");
					}
					
					if(compatibleInstances.size()==1)
						newEffcondtionParams[cntr]=compatibleInstances.get(0);
					else
						newEffcondtionParams[cntr] = compatibleInstances.get(UtilityClass.randInt(0, compatibleInstances.size()-1));
				}
				
				
				StateFactInstance newEffcondition = new  StateFactInstance(curSft, newEffcondtionParams);
				
				fma.getAnnotationMap().get(f).getEffects().getFacts().add(new StateFactInstanceS(newEffcondition, false));
				
			}
			
		}
		
		return fma;
	}

	public String getBaseURI() {
		return baseURI;
	}

	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}
}

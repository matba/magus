package edu.ls3.magus.generators;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ls3.magus.cl.contextmanager.basic.Condition;
import edu.ls3.magus.cl.contextmanager.basic.Instance;
import edu.ls3.magus.cl.contextmanager.basic.InstanceType;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstance;
import edu.ls3.magus.cl.contextmanager.basic.StateFactInstanceS;
import edu.ls3.magus.cl.contextmanager.basic.StateFactType;
import edu.ls3.magus.cl.contextmanager.context.ContextModel;
import edu.ls3.magus.cl.exceptions.UnsuccessfulMashupGeneration;
import edu.ls3.magus.cl.fmconfigurator.DomainModels;
import edu.ls3.magus.cl.fmconfigurator.FeatureModelConfigurationMashupGeneration;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureAnnotationSet;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModel;
import edu.ls3.magus.cl.fmconfigurator.model.FeatureModelConfiguration;
import edu.ls3.magus.cl.mashupconfigurator.service.Service;
import edu.ls3.magus.cl.mashupconfigurator.service.ServiceCollection;
import edu.ls3.magus.utility.SimpleLogger;
import edu.ls3.magus.utility.UtilityClass;

public class ServiceGenerator {
	public static ServiceCollection GenerateService(ContextModel cm, FeatureModel fm, FeatureAnnotationSet fma,String baseURI, float preconditionAvg, float preconditionStdDev,float effectAvg, float effectStdDev, float lengthynessFactorAvg,  float lengthynessFactorStdDev,float notRatio,String outputAddress,  SimpleLogger log ) throws Exception{
		List<Service> result = new ArrayList<Service>();
		
		int configurationCntr =1;
		
		//find all possible configurations
		List<FeatureModelConfiguration> fmcs = fm.getAllValidConfiguration(-1);
		
		int totalNoOfConfiguration = fmcs.size();
		//get a map for configuration to their pre-eff
		Map<FeatureModelConfiguration, Condition> preMaps = new HashMap<FeatureModelConfiguration, Condition>();
		Map<FeatureModelConfiguration, Condition> effMaps = new HashMap<FeatureModelConfiguration, Condition>();
		
		Map<Integer, List<FeatureModelConfiguration>> srtFMC = new HashMap<Integer, List<FeatureModelConfiguration>>();
		
		for(FeatureModelConfiguration fmc: fmcs){
			
			Condition planningProblemPreconditions = fma.findPrecondition(fmc);
			Condition planningProblemEffects = fma.findEffect(fmc);
			
			preMaps.put(fmc, planningProblemPreconditions);
			effMaps.put(fmc, planningProblemEffects);
			
			Integer curNo = planningProblemEffects.getConditions().size()-planningProblemPreconditions.getConditions().size();
			
			if(!srtFMC.containsKey(curNo))
				srtFMC.put(curNo, new ArrayList<FeatureModelConfiguration>());
			
			srtFMC.get(curNo).add(fmc);
			
		}
		//sort fmcs based on their number of pre-effs
		//create a queue of other unprocessed fmcs based on their sorting
		List<Integer> szList =  new ArrayList<Integer>();
		szList.addAll( srtFMC.keySet());
		Collections.sort(szList);
		
		
		List<FeatureModelConfiguration> satisfiableFmcs = new ArrayList<FeatureModelConfiguration>();
		
		Map<Condition,Condition> alreadySatisfiablePreEff = new HashMap<Condition, Condition>();
		
		//loop until queue is empty
		for(int szcntr=0; szcntr<szList.size(); szcntr++){
			
			while(!srtFMC.get(szList.get(szcntr)).isEmpty()){
				
				System.out.println("Creating services for configuration no: " + configurationCntr + " from " + totalNoOfConfiguration+ " . Progress: "+ ((configurationCntr*100)/totalNoOfConfiguration) + "%");
				if(log!=null)
					log.log("Creating services for configuration no: " + configurationCntr + " from " + totalNoOfConfiguration+ " . Progress: "+ ((configurationCntr*100)/totalNoOfConfiguration) + "%");
				FeatureModelConfiguration curFmc = srtFMC.get(szList.get(szcntr)).get(0);
				
				//System.out.println("Trying to find services for below configuration: ");
				//System.out.println(curFmc.toString());
				
				
				srtFMC.get(szList.get(szcntr)).remove(0);
				
				DomainModels dm = new DomainModels();
				dm.setContextModel(cm);
				dm.setFeatureModel(fm);
				dm.setFeatureModelAnnotation(fma);
				dm.setServiceCollection(new ServiceCollection(result));
				
				//check if the fmc is satisfiable
				FeatureModelConfigurationMashupGeneration fmcmg = new FeatureModelConfigurationMashupGeneration(dm, curFmc);
				
				if(result.size()>0){
					boolean alreadySatisfiable = false;
					
					Set<Condition> keyset = alreadySatisfiablePreEff.keySet();
					
					for(Condition curCond : keyset){
						if(curCond.subset(preMaps.get(curFmc))&& effMaps.get(curFmc).subset(alreadySatisfiablePreEff.get(curCond))){
							alreadySatisfiable = true;
							break;
						}
					}
					
					if(alreadySatisfiable)
					{
						configurationCntr++;
						System.out.println("Already Satisfiable New C!");
						if(log!=null)
							log.log("Already Satisfiable New C!");
						continue;
						
					}
					
					
					try {
						fmcmg.buildServiceMashup();
						
						System.out.println("Already Satisfiable!");
						
						if(log!=null)
							log.log("Already Satisfiable!");
						configurationCntr++;
						continue;
					} catch (UnsuccessfulMashupGeneration e) {
						
					}
				}
				
				if(satisfiableFmcs.isEmpty()){
					//find the services for satisfying the fmc with most pre and least eff
					////System.out.println("Preconditions: "+preMaps.get(curFmc).toString());
					//System.out.println("Effects: "+effMaps.get(curFmc).toString());
					List<Service> newServices = createTransitionServices(cm,fm,fma,curFmc,preMaps.get(curFmc), effMaps.get(curFmc),baseURI, configurationCntr,preconditionAvg,preconditionStdDev, effectAvg, effectStdDev,lengthynessFactorAvg,lengthynessFactorStdDev,notRatio);
					alreadySatisfiablePreEff.put(preMaps.get(curFmc),effMaps.get(curFmc));
					result.addAll(newServices);
					if(outputAddress!=null)
						(new ServiceCollection(newServices)).writeToDirectory(outputAddress);
					
					configurationCntr++;
//					for(Service s : newServices){
//						//System.out.println("Service added P:"+s.getPrecondition().toString()+" E:"+s.getPostcondition().toString());
//					}
				}
				else{
					//find the fmcs with least difference
					
					FeatureModelConfiguration mostSimFmc = null;
					int leastDistance  = 0;
					for(FeatureModelConfiguration fmc : satisfiableFmcs){
						int preDistance = preMaps.get(curFmc).calculateDistance(preMaps.get(fmc));
						int effDistance = effMaps.get(curFmc).calculateDistance(effMaps.get(fmc));
						
						if((mostSimFmc==null)|| (leastDistance>preDistance+effDistance)){
							mostSimFmc = fmc;
							leastDistance= preDistance+effDistance;
						}
						
					}
					//System.out.println("Most similar configuration is: ");
					//System.out.println(mostSimFmc.toString());
					
					//System.out.println("Preconditions1: "+preMaps.get(curFmc).toString());
					//System.out.println("Effects1: "+preMaps.get(mostSimFmc).toString());
					
					//System.out.println("Preconditions2: "+effMaps.get(mostSimFmc).toString());
					//System.out.println("Effects2: "+effMaps.get(curFmc).toString());
					
					//add services which make the fmc satable
					List<Service> newPreServices = createTransitionServices(cm,fm,fma,curFmc,preMaps.get(curFmc), preMaps.get(mostSimFmc),baseURI, configurationCntr,preconditionAvg,preconditionStdDev, effectAvg, effectStdDev,lengthynessFactorAvg, lengthynessFactorStdDev,notRatio);
					List<Service> newEffServices = createTransitionServices(cm,fm,fma,curFmc,effMaps.get(mostSimFmc), effMaps.get(curFmc),baseURI, configurationCntr,preconditionAvg,preconditionStdDev, effectAvg, effectStdDev,lengthynessFactorAvg, lengthynessFactorStdDev,notRatio);
					
					if(outputAddress!=null){
						(new ServiceCollection(newPreServices)).writeToDirectory(outputAddress);
						(new ServiceCollection(newEffServices)).writeToDirectory(outputAddress);
					}

					
					
					configurationCntr++;
					
					result.addAll(newPreServices);
					result.addAll(newEffServices);
					alreadySatisfiablePreEff.put(preMaps.get(curFmc),preMaps.get(mostSimFmc));
					alreadySatisfiablePreEff.put(effMaps.get(mostSimFmc),effMaps.get(curFmc));
					alreadySatisfiablePreEff.put(preMaps.get(curFmc),effMaps.get(curFmc));
//					for(Service s : newPreServices){
//						//System.out.println("Service added P:"+s.getPrecondition().toString()+" E:"+s.getPostcondition().toString());
//					}
//					for(Service s : newEffServices){
//						//System.out.println("Service added P:"+s.getPrecondition().toString()+" E:"+s.getPostcondition().toString());
//					}
					
					
					
				}
				
				//System.out.println("New number of services:"+result.size());
						
				dm.setServiceCollection(new ServiceCollection(result));
				
//				try {
//					fmcmg.buildServiceMashup();
//					
//				} catch (UnsuccessfulMashupGeneration e) {
//					throw new Exception("Feature model still unsatisfiable after adding required services.");
//				}
			}
			
		}
		

		return new ServiceCollection(result);
		
		
	}

	private static List<Service> createTransitionServices(ContextModel cm, FeatureModel fm, FeatureAnnotationSet fma,FeatureModelConfiguration fmc, Condition condition, Condition condition2, String baseURI, int configurationNo, float preconditionAvg, float preconditionStdDev,float effectAvg, float effectStdDev, float lengthynessFactorAvg, float lengthynessFactorStdDev, float notRatio) throws Exception {
		
		List<Service> result = new ArrayList<Service>();
		int serviceCntr =1;
		
		Set<StateFactType> exclusions = new HashSet<StateFactType>();
		
		for(StateFactInstanceS sfis: condition.getConditions()){
			if(!exclusions.contains(sfis.getStateFactInstance().getType()))
				exclusions.add(sfis.getStateFactInstance().getType());
		}
		for(StateFactInstanceS sfis: condition2.getConditions()){
			if(!exclusions.contains(sfis.getStateFactInstance().getType()))
				exclusions.add(sfis.getStateFactInstance().getType());
		}
		
		List<Instance> fmcInstances = fma.findEntities(fmc);
		
		Set<InstanceType> fmcInstanceTypes = new HashSet<InstanceType>();
		
		for(Instance ins: fmcInstances)
			if(!fmcInstanceTypes.contains(ins.getType()))
				fmcInstanceTypes.add(ins.getType());
		
		Set<StateFactType> possibleStateFactTypes = cm.filterStateFactTypeByInstanceType(fmcInstanceTypes);
		
		possibleStateFactTypes.removeAll(exclusions);
		
		List<StateFactInstanceS> toBeRemoved= new ArrayList<StateFactInstanceS>();
		
		for(StateFactInstanceS sfis: condition.getConditions()){
			if(condition2.getConditions().contains(sfis)){
				if(!toBeRemoved.contains(sfis))
					toBeRemoved.add(sfis);
				
			}
		}
		
		for(StateFactInstanceS sfis: toBeRemoved){
			condition.getConditions().remove(sfis);
			condition2.getConditions().remove(sfis);
		}
		
		if(condition2.getConditions().isEmpty())
			return result;
		
		List<StateFactInstanceS> remainingEffects = new ArrayList<StateFactInstanceS>();
		remainingEffects.addAll(condition2.getConditions());
		
		List<StateFactInstanceS> currentConditionSfis = new ArrayList<StateFactInstanceS>();
		currentConditionSfis.addAll(condition.getConditions());
		Condition currentContext = new  Condition(currentConditionSfis);
		
		//System.out.println("Current Context: "+currentContext);
		
		while(remainingEffects.size()>0){
			String serviceUri =baseURI+ "ServiceC"+configurationNo+"S"+serviceCntr;
			
			int noOfPreconditions = (int) Math.round( UtilityClass.CuttedRandValueNormal(preconditionAvg, preconditionStdDev, 0));
			List<StateFactInstanceS> servicePrecondition = new ArrayList<StateFactInstanceS>();
			List<StateFactInstanceS> selectedPrecondtions = new ArrayList<StateFactInstanceS>();
			
			Map<URI, Instance> inputs = new HashMap<URI, Instance>();
			Map<URI, Instance> outputs= new HashMap<URI, Instance>();
			List<URI> inputList = new ArrayList<URI>();
			List<URI> outputList= new ArrayList<URI>();
			
			Map<URI,URI> inputInstanceMap = new HashMap<URI, URI>(); 
			
			Map<URI,Instance> replacementMap =  new HashMap<URI, Instance>();
			
			// select subset of current context for precondition
			if(noOfPreconditions>=currentContext.getConditions().size())
				selectedPrecondtions.addAll(currentContext.getConditions());
			else{
				Collections.shuffle(currentContext.getConditions());
				selectedPrecondtions.addAll(currentContext.getConditions().subList(0, noOfPreconditions));
			}
			
			currentContext.getConditions().removeAll(selectedPrecondtions);
			
			List<Instance> preconditionInstances = Condition.getInvolvedInstances(new Condition(selectedPrecondtions)) ;
			
			
			for(Instance ins: preconditionInstances){
				int inpCntr =1;
				String name = "inp"+ins.getType().getTypeName()+inpCntr;
				
				while(inputs.containsKey(new URI(serviceUri+"#"+name))){
					inpCntr++;
					name = "inp"+ins.getType().getTypeName()+inpCntr;
				}
				
				URI uri = new URI(serviceUri+"#"+name);
				
				inputInstanceMap.put(ins.getURI(), uri);
				
				Instance newInstance = new Instance(ins.getType(), name, uri);
				
				inputList.add(uri);
				inputs.put(uri, newInstance);
				replacementMap.put(uri, newInstance);
			}
			
			for(StateFactInstanceS sfis: selectedPrecondtions){
				servicePrecondition.add(new StateFactInstanceS(sfis.getStateFactInstance().replaceParams(replacementMap,inputInstanceMap), sfis.isNot()));
			}
			
			//set number of effects
			int noOfEffects = (int) Math.round( UtilityClass.CuttedRandValueNormal(effectAvg, effectStdDev, 1));
			
			int noContributingToEffects = 0;
			int noTransitional =0;
			
			double ratio = UtilityClass.CuttedRandValueNormal(lengthynessFactorAvg, lengthynessFactorStdDev, 0);
			
			noContributingToEffects = (int) Math.round(ratio*noOfEffects);
			noTransitional =  (int) Math.round((1-ratio)*noOfEffects);
			
			
			
			if(noContributingToEffects>=remainingEffects.size()){
				noTransitional = 0;
				noContributingToEffects = remainingEffects.size();
			}
			
			if((noContributingToEffects==0)&&(noTransitional==0))
				noContributingToEffects = 1;
			
			Collections.shuffle(remainingEffects);
			
			List<StateFactInstanceS> serviceEffects= new ArrayList<StateFactInstanceS>();
			List<StateFactInstanceS> selectedEffects= new ArrayList<StateFactInstanceS>();
			List<Instance> effectInstances = new ArrayList<Instance>();
			
			if(noContributingToEffects>0){
				selectedEffects.addAll(remainingEffects.subList(0, noContributingToEffects));
				
				remainingEffects.removeAll(selectedEffects);
			
				effectInstances = Condition.getInvolvedInstances(new Condition(selectedEffects)) ;
				
				for(Instance ins: effectInstances){
					
					if(!inputInstanceMap.containsKey(ins.getURI())){
					
						int outCntr =1;
						String name = "out"+ins.getType().getTypeName()+outCntr;
						
						while(inputs.containsKey(new URI(serviceUri+"#"+name))){
							outCntr++;
							name = "out"+ins.getType().getTypeName()+outCntr;
						}
						
						URI uri = new URI(serviceUri+"#"+name);
						
						inputInstanceMap.put(ins.getURI(), uri);
						
						Instance newInstance = new Instance(ins.getType(), name, uri);
						
						outputList.add(uri);
						outputs.put(uri, newInstance);
						replacementMap.put(uri, newInstance);
					}
				}
				
				for(StateFactInstanceS sfis: selectedEffects){
					serviceEffects.add(new StateFactInstanceS(sfis.getStateFactInstance().replaceParams(replacementMap,inputInstanceMap), sfis.isNot()));
				}
				
				currentContext.getConditions().addAll(selectedEffects);
			
			}
			noTransitional = Math.min(possibleStateFactTypes.size(), noTransitional);
			
			if(noTransitional>0){
				List<StateFactType> pf = new ArrayList<StateFactType>();
				pf.addAll(possibleStateFactTypes);
				Collections.shuffle(pf);
				
				pf =pf.subList(0, noTransitional);
					
				List<StateFactInstanceS> selectedTransitional = new ArrayList<StateFactInstanceS>();

				for(StateFactType sft:pf){
					
					Instance[] newParams = new Instance[sft.getParams().length];
					
					for(int cntr =0; cntr<sft.getParams().length; cntr++){
						InstanceType it = sft.getParams()[cntr];
						Instance newInstance=null;
						for(Instance ins: preconditionInstances)
							if(ins.getType().equals(it))
							{
								newInstance = ins;
								break;
							}
						if(newInstance==null){
							for(Instance ins: effectInstances)
								if(ins.getType().equals(it))
								{
									newInstance = ins;
									break;
								}
						}
						if(newInstance==null){
							List<Instance> potentialSelection = new ArrayList<Instance>();
							for(Instance curIns: fmcInstances){
								if(curIns.getType().equals(it))
									potentialSelection.add(curIns);
							}
							if(potentialSelection.isEmpty())
								throw new Exception("There is no instance for a fact type.");
							else{
								if(potentialSelection.size()==1){
									newInstance = potentialSelection.get(0);
								}
								else{
									newInstance = potentialSelection.get(UtilityClass.randInt(0, potentialSelection.size()-1));
								}
							}
							
						}
						newParams[cntr] = newInstance;	
					}
					
					StateFactInstance newSfi = new StateFactInstance(sft, newParams);
					selectedTransitional.add(new StateFactInstanceS(newSfi, UtilityClass.randInt(0,100)<(notRatio*100)?true:false));
				}
				List<Instance> transInstances = Condition.getInvolvedInstances(new Condition(selectedTransitional)) ;
				
				for(Instance ins: transInstances){
					
					if(!inputInstanceMap.containsKey(ins.getURI())){
					
						int outCntr =1;
						String name = "out"+ins.getType().getTypeName()+outCntr;
						
						while(inputs.containsKey(new URI(serviceUri+"#"+name))){
							outCntr++;
							name = "out"+ins.getType().getTypeName()+outCntr;
						}
						
						URI uri = new URI(serviceUri+"#"+name);
						
						inputInstanceMap.put(ins.getURI(), uri);
						
						Instance newInstance = new Instance(ins.getType(), name, uri);
						
						outputList.add(uri);
						outputs.put(uri, newInstance);
						replacementMap.put(uri, newInstance);
					}
				}
				
				for(StateFactInstanceS sfis: selectedTransitional){
					serviceEffects.add(new StateFactInstanceS(sfis.getStateFactInstance().replaceParams(replacementMap,inputInstanceMap), sfis.isNot()));
				}
				
				currentContext.getConditions().addAll(selectedTransitional);
				
			}
			
			
			
			
			List<URI> varList = new ArrayList<URI>();
			Map<URI, Instance> vars = new HashMap<URI, Instance>();
			Map<URI, Instance> contextVars = new HashMap<URI, Instance>();
			List<URI> contextVarList = new ArrayList<URI>();
			
			//update current context
			
			
			Condition precondition = new Condition(servicePrecondition);
			Condition postcondition = new Condition(serviceEffects);
			Service newService = new Service("ServiceC"+configurationNo+"S"+serviceCntr, precondition, postcondition, inputs, outputs, vars, contextVars, inputList, outputList, varList, contextVarList, null, serviceUri);
			
			//System.out.println("Service added P:"+newService.getPrecondition().toString()+" E:"+newService.getPostcondition().toString());
			
			//System.out.println("Current Context: "+currentContext);
			
			serviceCntr++;
			result.add(newService);
			
			
			
			
		}
		
		return result;
	}
}
